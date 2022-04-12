# Deddit
<p align="center">  
🗡️ Deddit demonstrates modern Android development with Hilt, Coroutines, Flow, Jetpack (ViewModel,Paging3), and Material Design based on MVVM architecture.
</p>


## Functionality  

The following **functionality** is completed:

* [x] Explore Memes from reddit
* [x] Apply filter to enjoy best subreddits 
* [x] Notification using **Firebase Cloud Messaging** 
* [x] Paginating data 
* [x] **Download** and **Share** your meme 

## API
* [x] Meme API : https://github.com/D3vd/Meme_Api
* [x] Firebase Cloud Messaging API : https://fcm.googleapis.com/fcm/send

## Tech stack & Open-source libraries
- Minimum SDK level 26
- [Kotlin](https://kotlinlang.org/) based, [Coroutines](https://github.com/Kotlin/kotlinx.coroutines) + [Flow](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/) for asynchronous.
- [Hilt](https://dagger.dev/hilt/) for dependency injection.
- Jetpack
  - Lifecycle - Observe Android lifecycles and handle UI states upon the lifecycle changes.
  - ViewModel - Manages UI-related data holder and lifecycle aware. Allows data to survive configuration changes such as screen rotations.
  - DataBinding - Binds UI components in your layouts to data sources in your app using a declarative format rather than programmatically.
  - Paging3 -  It has support for requesting the next page to load more data automatically.
  - Work Manager - WorkManager is intended for tasks that require a guarantee that the system will run them even if the app exits.
  - DataStore - Jetpack DataStore is a data storage solution. It allows us to store key-value pairs **(like SharedPreferences)**
- Architecture
  - MVVM Architecture (View - DataBinding - ViewModel - Model)
  - Repository Pattern
- [Retrofit2 & OkHttp3](https://github.com/square/retrofit) - Construct the REST APIs.
- [Moshi](https://github.com/square/moshi/) - A modern JSON library for Kotlin and Java.
- [Glide](https://github.com/bumptech/glide) - Loading images from network.
- [Material-Components](https://github.com/material-components/material-components-android) - Material design components for building ripple animation, and Cardview 
- [Shimmer-Layout](https://facebook.github.io/shimmer-android/) - The motive of using the Shimmer layout is to inform the user that the structure is currently loading.
- [Lottie](https://lottiefiles.com/) - Lottie file is a JSON-based animation filet that enables developers to implement animations on any platform as easily as implementing static assets.

## Video Walkthrough

Deddit                      |
:-------------------------:|



https://user-images.githubusercontent.com/73629899/162626022-632a0fe5-81cd-46e8-8ae9-d2e5a0bceaae.mp4 


## Architecture
Deddit is based on the MVVM architecture and the Repository pattern.

![architecture](https://user-images.githubusercontent.com/24237865/77502018-f7d36000-6e9c-11ea-92b0-1097240c8689.png)

