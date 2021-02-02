package io.github.ponnamkarthik.flutterwebview;

import org.adblockplus.libadblockplus.android.webview.AdblockWebView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.PluginRegistry.Registrar;
import io.flutter.plugin.platform.PlatformView;
import android.view.LayoutInflater;
import android.util.Log;

import static io.flutter.plugin.common.MethodChannel.MethodCallHandler;


public class FlutterWeb implements PlatformView, MethodCallHandler {

    Context context;
    Registrar registrar;
    AdblockWebView webView;
    String url = "";
    MethodChannel channel;
    EventChannel.EventSink onPageFinishEvent;
    EventChannel.EventSink onPageStartEvent;


    @SuppressLint("SetJavaScriptEnabled")
    FlutterWeb(Context context, Registrar registrar, int id) {
        this.context = context;
        this.registrar = registrar;
        this.url = url;
        webView = getWebView(registrar, context);

        channel = new MethodChannel(registrar.messenger(), "ponnamkarthik/flutterwebview_" + id);
        final EventChannel onPageFinishEvenetChannel = new EventChannel(registrar.messenger(), "ponnamkarthik/flutterwebview_stream_pagefinish_" + id);
        final EventChannel onPageStartEvenetChannel = new EventChannel(registrar.messenger(), "ponnamkarthik/flutterwebview_stream_pagestart_" + id);

        onPageFinishEvenetChannel.setStreamHandler(new EventChannel.StreamHandler() {
            @Override
            public void onListen(Object o, EventChannel.EventSink eventSink) {
                onPageFinishEvent = eventSink;
            }

            @Override
            public void onCancel(Object o) {

            }
        });
        onPageStartEvenetChannel.setStreamHandler(new EventChannel.StreamHandler() {
            @Override
            public void onListen(Object o, EventChannel.EventSink eventSink) {
                onPageStartEvent = eventSink;
            }

            @Override
            public void onCancel(Object o) {

            }
        });
        channel.setMethodCallHandler(this);
    }

    @Override
    public View getView() {
        return webView;
    }

    @Override
    public void dispose() {

    }

    private AdblockWebView getWebView(Registrar registrar, Context context) {
//        setContentView(R.layout.layout);
//        AdblockWebView webView = new AdblockWebView(registrar.context());
//        View view = LayoutInflater.from(context).inflate(R.layout.layout, null);
//        webView.setContentView(view.findViewById(R.id.main_webview));

//        AdblockWebView webview = (AdblockWebView) view.findViewById(R.id.main_webview);
//        Log.d("WEBVIEW LOG: ", view.getClass().toString());
        AdblockWebView webView = new AdblockWebView(registrar.context());
//        view.setTag(webView);
//        WebView webview = (WebView) view.findViewById(R.id.main_webview);
//        webView.setProvider(AdblockHelper.get()
//                .init(this, basePath, true, AdblockHelper.PREFERENCE_NAME)
//                .preloadSubscriptions(AdblockHelper.PRELOAD_PREFERENCE_NAME, map)
//                .addEngineCreatedListener(engineCreatedListener)
//                .addEngineDisposedListener(engineDisposedListener)
//                .getProvider());
//        WebView webView = (WebView)findViewById(R.id.main_webview);
//        registrar.activity().findViewById(R.id.main_webview).addView(webView);
        webView.setWebViewClient(new CustomWebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        return webView;
    }



    private class CustomWebViewClient extends WebViewClient {
        @SuppressWarnings("deprecated")
        @Override
        public boolean shouldOverrideUrlLoading(WebView wv, String url) {
            if (url.startsWith("http") || url.startsWith("https") || url.startsWith("ftp")) {
                return false;
            } else {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                registrar.activity().startActivity(intent);
                return true;
            }
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            if(onPageStartEvent != null) {
                onPageStartEvent.success(url);
            }
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            if(onPageFinishEvent != null) {
                onPageFinishEvent.success(url);
            }
            super.onPageFinished(view, url);
        }
    }

    @Override
    public void onMethodCall(MethodCall call, MethodChannel.Result result) {
        switch (call.method) {
            case "loadUrl":
                String url = call.arguments.toString();
                webView.loadUrl(url);
                break;
            case "loadData":
                String html = call.arguments.toString();
                webView.loadDataWithBaseURL(null, html, "text/html", "utf-8",null);
                break;
            default:
                result.notImplemented();
        }

    }

}
