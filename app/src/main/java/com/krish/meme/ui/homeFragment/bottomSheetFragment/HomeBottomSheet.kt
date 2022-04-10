package com.krish.meme.ui.homeFragment.bottomSheetFragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.krish.meme.databinding.FragmentHomeBottomSheetBinding
import com.krish.meme.viewModel.MainViewModel

private const val TAG = "BottomSheetDialogFragment"

class HomeBottomSheet : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentHomeBottomSheetBinding
    private lateinit var mViewModel: MainViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBottomSheetBinding.inflate(inflater, container, false)

        mViewModel.checkChipId.observe(viewLifecycleOwner, Observer { value ->
            updateChip(value, binding.memeChipGroup)
        })

        binding.memeChipGroup.setOnCheckedChangeListener { group, checkedId ->
            val chip = group.findViewById<Chip>(checkedId)
            val selectedSubreddit = chip.text.toString().trim { it <= ' ' }

            mViewModel.checkChipId.value = checkedId
            binding.subredditEt.setText(selectedSubreddit)
            binding.subredditEt.setSelection(selectedSubreddit.length)
        }

        binding.applyBtn.setOnClickListener {
            val subName = binding.subredditEt.text.toString()
            if (subName.isNotBlank()) {
                val action = HomeBottomSheetDirections.actionHomeBottomSheetToHomeFragment(subName)
                findNavController().navigate(action)
            }else {
                Toast.makeText(requireContext(), "Please enter the subreddit before submitting", Toast.LENGTH_SHORT).show()
            }
            binding.subredditEt.text.clear()
        }
        return binding.root
    }

    private fun updateChip(selectedSubId: Int, subredditChipGroup: ChipGroup) {
        if (selectedSubId != 0) {
            try {
                binding.subredditEt.text.clear()
                val targetView = subredditChipGroup.findViewById<Chip>(selectedSubId)
                targetView.isChecked = true
                subredditChipGroup.requestChildFocus(targetView, targetView)
            } catch (e: Exception) {
                Log.d(TAG, "updateChip: ${e.message.toString()}")
            }
        }
    }
}