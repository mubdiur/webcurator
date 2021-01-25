package io.github.webcurate.clients

//import android.webkit.WebView
//import android.webkit.WebViewClient
//
//object HiddenWeb : WebViewClient() {
//
////    override fun onPageFinished(view: WebView?, url: String?) {
////        super.onPageFinished(view, url)
////
////    }
//
//    override fun onPageCommitVisible(view: WebView?, url: String?) {
//        super.onPageCommitVisible(view, url)
//        view?.loadUrl(
//            "javascript:window.WebJsClient.saveHtml" +
//                    "('<html>'+document.getElementsByTagName('body')[0].innerHTML+'</html>');"
//        )
//    }
//}