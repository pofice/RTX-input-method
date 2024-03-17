package com.peakchao.webapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.*;
import android.widget.ProgressBar;

public class MainActivity extends AppCompatActivity {
    private WebView mWebView;
    private ProgressBar mWebProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mWebView = findViewById(R.id.mWebView);
        mWebProgress = findViewById(R.id.mWebProgress);

        initSetting();

        if (BuildConfig.isLocal){
            mWebView.loadUrl("file:///android_asset/index.html");
        }else {
            mWebView.loadUrl(BuildConfig.web_url);
        }
    }

    private void initSetting() {
        mWebProgress.setMax(100);
        WebSettings settings = mWebView.getSettings();
        mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        settings.setDefaultTextEncodingName("UTF-8");
        //设置自适应屏幕，两者合用
        settings.setAllowFileAccess(true);//设置可以访问文件
        settings.setLoadsImagesAutomatically(true); //支持自动加载图片
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setBuiltInZoomControls(false);
        settings.setSupportMultipleWindows(true);
        settings.setUseWideViewPort(true);//将图片调整到适合webview的大小
        settings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
        settings.setSupportZoom(false);
        settings.setPluginState(WebSettings.PluginState.ON);
        settings.setDomStorageEnabled(true);
        settings.setLoadsImagesAutomatically(true);
        settings.setJavaScriptEnabled(true);
        //自动播放音乐
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            settings.setMediaPlaybackRequiresUserGesture(false);
        }
        if (isNetAvailable(this)) {
            settings.setCacheMode(WebSettings.LOAD_DEFAULT); //根据cache-control决定是否从网络上取数据。
        } else {
            settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);  //没网，则从本地获取，即离线加载
        }
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
            }

            @Override
            public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
                super.doUpdateVisitedHistory(view, url, isReload);
            }
        });
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress == 100) {
                    mWebProgress.setVisibility(View.GONE);
                } else {
                    if (mWebProgress.getVisibility() == View.GONE) {
                        mWebProgress.setVisibility(View.VISIBLE);
                    }
                    mWebProgress.setProgress(newProgress);
                }
            }
        });
    }


    private boolean isNetAvailable(Context context) {
        if (context == null) {
            return false;
        }
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        return info != null && info.isAvailable();
    }


    @Override
    public void onResume() {
        if (mWebView != null) {
            mWebView.onResume();
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        if (mWebView != null) {
            mWebView.onPause();
            mWebView.stopLoading();
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        if (mWebView != null) {
            mWebView.destroy();
        }
        super.onDestroy();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        return false;
    }
}
