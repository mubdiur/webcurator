package io.github.webcurator.viewmodels

//class FeedsViewModel(application: Application) : AndroidViewModel(
//    application
//) {
//    private var feedList = flowOf<List<Feed>>()
//
//    init {
//        viewModelScope.launch {
//            DatabaseClient.getInstance(application).feedDao().getAllFeeds().collect {
//                feedList = it
//            }
//        }
//    }
//
//    fun feedList() = feedList
//}