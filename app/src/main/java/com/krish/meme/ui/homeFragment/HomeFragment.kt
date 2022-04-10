package com.krish.meme.ui.homeFragment

import android.Manifest
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.google.firebase.messaging.FirebaseMessaging


import com.krish.meme.R
import com.krish.meme.adapter.ButtonClick
import com.krish.meme.adapter.HeaderFooterAdapter
import com.krish.meme.adapter.MainListAdapter
import com.krish.meme.databinding.FragmentHomeBinding
import com.krish.meme.model.Meme
import com.krish.meme.viewModel.MainViewModel
import com.krish.meme.worker.NotificationWorker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File
import java.util.concurrent.TimeUnit


private const val TAG = "HomeFragment"
const val TOPIC = "/topic/myTopic"
private const val WORKER_TAG = "MEMES"

@AndroidEntryPoint
class HomeFragment : Fragment(), ButtonClick {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var mViewModel: MainViewModel
    private val mAdapter: MainListAdapter by lazy { MainListAdapter(this@HomeFragment) }
    private var writePermissionGranted = false
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private val args by navArgs<HomeFragmentArgs>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel = ViewModelProvider(this)[MainViewModel::class.java]
        permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { permissions ->
                writePermissionGranted = permissions ?: writePermissionGranted
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        setupRecyclerView()
        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)

        lifecycleScope.launch {
            val subreddit = if (args.subreddit == "meme" || args.subreddit.isNullOrBlank()) "meme" else args.subreddit
            mViewModel.postData(subreddit!!).collectLatest {
                mAdapter.submitData(it)
            }
        }

        mAdapter.addLoadStateListener { loadState ->

            if (loadState.refresh is LoadState.Loading) {
                showShimmerEffect()
            } else {
                hideShimmerEffect()
                val error = when {
                    loadState.prepend is LoadState.Error -> loadState.prepend as LoadState.Error
                    loadState.append is LoadState.Error -> loadState.append as LoadState.Error
                    loadState.refresh is LoadState.Error -> loadState.refresh as LoadState.Error
                    else -> null
                }
                error?.let {
                    if (it.error.message == "HTTP 404 Not Found") {
                        binding.notFound.visibility = View.VISIBLE
                        binding.errorMessage.visibility = View.VISIBLE
                        binding.errorMessage.setText(R.string.error_message)

                    }
                    if (it.error.message == "HTTP 503 Service Unavailable"){
                        binding.notFound.visibility = View.VISIBLE
                        binding.errorMessage.visibility = View.VISIBLE
                        binding.errorMessage.setText(R.string.error_message)
                    }
                }
            }
        }

        binding.changeSubreddit.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_homeBottomSheet)
        }

        setupPeriodicWorker()

        return binding.root
    }

    private fun setupPeriodicWorker(force: Boolean = false) {
        Log.d(TAG, "setupPeriodicWorker: ")
        WorkManager.getInstance(requireContext()).enqueueUniquePeriodicWork(
            WORKER_TAG,
            if (force) ExistingPeriodicWorkPolicy.REPLACE else ExistingPeriodicWorkPolicy.KEEP,
            PeriodicWorkRequest.Builder(
                NotificationWorker::class.java,
                16,
                TimeUnit.MINUTES
            ).build()
        )
    }


    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            adapter = mAdapter.withLoadStateFooter(footer = HeaderFooterAdapter(mAdapter::retry))
            layoutManager = LinearLayoutManager(requireContext())
        }
        showShimmerEffect()
    }

    private fun showShimmerEffect() {
        binding.shimmerFrameLayout.startShimmer()
        binding.recyclerView.visibility = View.GONE
        binding.shimmerFrameLayout.visibility = View.VISIBLE
    }

    private fun hideShimmerEffect() {
        binding.shimmerFrameLayout.stopShimmer()
        binding.recyclerView.visibility = View.VISIBLE
        binding.shimmerFrameLayout.visibility = View.GONE

    }


    override fun shareClick(meme: Meme) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(
            Intent.EXTRA_TEXT,
            "Check out this cool meme I found on reddit \n${meme.url}"
        )
        startActivity(intent)
    }

    override fun downloadClick(meme: Meme) {
        val hasWritePermission = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

        val minSdk29 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
        writePermissionGranted = hasWritePermission || minSdk29

        if (!writePermissionGranted) {
            permissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        } else {
            downloadImage(meme)
        }
    }

    private fun downloadImage(meme: Meme) {
        try {
            val url = Uri.parse(meme.url)
            val request = DownloadManager.Request(url)
            val title = meme.title + ": reddit"
            val fileTypeList = meme.url.split(".")
            val fileType = fileTypeList[fileTypeList.size - 1]
            request.setTitle(title)
            request.setDescription("Downloading File Please Wait...")
            val cookie = CookieManager.getInstance().getCookie(meme.url)
            request.addRequestHeader("cookie", cookie)
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            request.setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                File.separator + meme.author + ".${fileType}"
            )

            val dm = requireActivity().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            dm.enqueue(request)
            Toast.makeText(requireContext(), "Download Started", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.d(TAG, "Error : ${e.message}")
        }
    }

    override fun onResume() {
        super.onResume()
        binding.shimmerFrameLayout.startShimmer()
    }

    override fun onPause() {
        binding.shimmerFrameLayout.stopShimmer()
        super.onPause()
    }

}