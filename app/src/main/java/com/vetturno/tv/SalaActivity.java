package com.vetturno.tv;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.appcompat.app.AppCompatActivity;

public class SalaActivity extends AppCompatActivity {

    private static final String BASE_URL = "https://vetturno.vercel.app/sala/";

    // JS que parchea AudioContext para que se auto-resume sin gesto del usuario
    private static final String UNLOCK_AUDIO_JS =
        "(function(){" +
        "  var OrigAC = window.AudioContext || window.webkitAudioContext;" +
        "  if (!OrigAC) return;" +
        "  function resumeCtx(ctx){ if(ctx && ctx.state==='suspended') ctx.resume(); }" +
        "  var PatchedAC = function(){ var c = new OrigAC(); resumeCtx(c); return c; };" +
        "  PatchedAC.prototype = OrigAC.prototype;" +
        "  window.AudioContext = window.webkitAudioContext = PatchedAC;" +
        "  document.querySelectorAll('audio,video').forEach(function(el){" +
        "    el.muted = false;" +
        "    el.play && el.play().catch(function(){});" +
        "  });" +
        "})();";

    private WebView webView;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        setContentView(R.layout.activity_sala);

        webView = findViewById(R.id.webview);

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setMediaPlaybackRequiresUserGesture(false);
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // 1. Inyectar JS para desbloquear AudioContext
                view.evaluateJavascript(UNLOCK_AUDIO_JS, null);
                // 2. Simular tap para actuar como user gesture (desbloquea lo que JS no alcanza)
                new Handler().postDelayed(() -> simulateTap(view), 800);
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onPermissionRequest(final PermissionRequest request) {
                request.grant(request.getResources());
            }
        });

        String code = getIntent().getStringExtra("sala_code");
        if (code == null) {
            SharedPreferences prefs = getSharedPreferences("vetturno_prefs", MODE_PRIVATE);
            code = prefs.getString("sala_code", "");
        }

        webView.loadUrl(BASE_URL + code);
    }

    private void simulateTap(WebView view) {
        long now = SystemClock.uptimeMillis();
        MotionEvent down = MotionEvent.obtain(now, now, MotionEvent.ACTION_DOWN, 1f, 1f, 0);
        MotionEvent up   = MotionEvent.obtain(now, now + 50, MotionEvent.ACTION_UP, 1f, 1f, 0);
        view.dispatchTouchEvent(down);
        view.dispatchTouchEvent(up);
        down.recycle();
        up.recycle();
        // Reinjectar JS por si el AudioContext se creó después del tap
        view.evaluateJavascript(UNLOCK_AUDIO_JS, null);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        webView.onResume();
        webView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                View.SYSTEM_UI_FLAG_FULLSCREEN |
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        );
    }

    @Override
    protected void onPause() {
        super.onPause();
        webView.onPause();
    }

    @Override
    protected void onDestroy() {
        webView.destroy();
        super.onDestroy();
    }
}
