package com.example.webviewaudiotest;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowInsets;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

public class MainActivity extends Activity {
    private static final String YOUTUBE = "https://music.youtube.com/";
    private static final String CONTROL = "file:///android_asset/control.html";
    private static final String PREFS = "results";
    private static final int DARK = Color.rgb(16, 24, 39);
    private static final int DARKER = Color.rgb(10, 16, 28);
    private static final int ACCENT = Color.rgb(42, 151, 131);
    private static final int MIST = Color.rgb(207, 222, 235);
    private WebView webView;
    private TextView status;
    private ProgressBar progress;
    private String source = "YouTube Music";
    private boolean returnedFromBackgroundButton = false;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().setStatusBarColor(DARK);
        getWindow().setNavigationBarColor(DARKER);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(DARK);
        root.setFitsSystemWindows(true);
        setContentView(root);

        TextView title = new TextView(this);
        title.setText("WEBVIEW AUDIO TEST");
        title.setTextSize(18);
        title.setTypeface(null, Typeface.BOLD);
        title.setTextColor(Color.WHITE);
        title.setPadding(dp(14), dp(12), dp(14), dp(3));
        root.addView(title);

        TextView provider = new TextView(this);
        PackageInfo pkg = WebView.getCurrentWebViewPackage();
        String providerName = pkg == null ? "Unknown provider" : pkg.packageName + " (" + pkg.versionName + ")";
        provider.setText("WebView provider: " + providerName);
        provider.setTextColor(MIST);
        provider.setTextSize(11);
        provider.setPadding(dp(14), 0, dp(14), dp(9));
        root.addView(provider);

        LinearLayout tabs = new LinearLayout(this);
        tabs.setPadding(dp(6), 0, dp(6), dp(5));
        root.addView(tabs);
        addButton(tabs, "YOUTUBE MUSIC", () -> loadSource(true), 1.4f);
        addButton(tabs, "CONTROL AUDIO", () -> loadSource(false), 1.3f);
        addButton(tabs, "RELOAD", () -> webView.reload(), .8f);

        progress = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        progress.setMax(100);
        progress.setProgressTintList(android.content.res.ColorStateList.valueOf(ACCENT));
        root.addView(progress, new LinearLayout.LayoutParams(-1, dp(3)));

        status = new TextView(this);
        status.setText("Starting WebView...");
        status.setTextSize(12);
        status.setTextColor(MIST);
        status.setPadding(dp(12), dp(7), dp(12), dp(7));
        root.addView(status);

        webView = new WebView(this);
        webView.setBackgroundColor(Color.WHITE);
        root.addView(webView, new LinearLayout.LayoutParams(-1, 0, 1));

        LinearLayout footer = new LinearLayout(this);
        footer.setPadding(dp(6), dp(6), dp(6), dp(4));
        footer.setBackgroundColor(DARK);
        root.addView(footer);
        addButton(footer, "SEND TO BACKGROUND", () -> {
            returnedFromBackgroundButton = true;
            moveTaskToBack(true);
        }, 1.6f);
        addButton(footer, "LOG RESULT", this::showLogDialog, 1.0f);
        addButton(footer, "RESULTS", this::showResults, .8f);

        TextView hint = new TextView(this);
        hint.setText("Play audio, background the app or lock your screen, then return and log what happened.");
        hint.setTextColor(MIST);
        hint.setTextSize(11);
        hint.setGravity(Gravity.CENTER);
        hint.setPadding(dp(10), 0, dp(10), dp(8));
        root.addView(hint);

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setMediaPlaybackRequiresUserGesture(true);
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);
        settings.setAllowFileAccessFromFileURLs(false);
        settings.setAllowUniversalAccessFromFileURLs(false);
        settings.setJavaScriptCanOpenWindowsAutomatically(false);
        settings.setSupportMultipleWindows(false);
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        CookieManager.getInstance().setAcceptCookie(true);
        CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                progress.setProgress(newProgress);
                progress.setVisibility(newProgress < 100 ? View.VISIBLE : View.GONE);
            }
        });
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, android.graphics.Bitmap favicon) {
                status.setText("Loading " + source + "...");
            }
            @Override
            public void onPageFinished(WebView view, String url) {
                status.setText(source + " loaded. Start playing audio manually.");
            }
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                if (request.isForMainFrame()) {
                    status.setText("Page error: " + error.getDescription());
                }
            }
        });

        if (state != null) {
            source = state.getString("source", "YouTube Music");
            webView.restoreState(state);
        } else {
            loadSource(true);
        }
    }

    private void loadSource(boolean youtube) {
        source = youtube ? "YouTube Music" : "Control audio";
        // Local HTML needs file access to reach its bundled WAV; remote music site does not.
        webView.getSettings().setAllowFileAccess(!youtube);
        status.setText("Loading " + source + "...");
        webView.loadUrl(youtube ? YOUTUBE : CONTROL);
    }

    private void showLogDialog() {
        final String[] choices = {
            "App switch / Home — audio kept playing",
            "App switch / Home — audio stopped",
            "Screen locked — audio kept playing",
            "Screen locked — audio stopped",
            "Page or Google sign-in failed"
        };
        new AlertDialog.Builder(this)
            .setTitle("Record result: " + source)
            .setItems(choices, (dialog, selected) -> {
                String key = source.equals("YouTube Music") ? "youtube" : "control";
                String type = selected < 2 ? "home" : selected < 4 ? "lock" : "load";
                String outcome = selected == 0 || selected == 2 ? "PASS — continued" :
                                 selected == 4 ? "BLOCKED / FAILED" : "FAIL — stopped";
                getSharedPreferences(PREFS, MODE_PRIVATE).edit().putString(key + "_" + type, outcome).apply();
                status.setText("Recorded " + source + " / " + type + ": " + outcome);
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    private void showResults() {
        SharedPreferences p = getSharedPreferences(PREFS, MODE_PRIVATE);
        String[] keys = {"youtube_home", "youtube_lock", "youtube_load", "control_home", "control_lock", "control_load"};
        String[] names = {"YouTube / switch apps", "YouTube / lock screen", "YouTube / page or sign-in", "Control / switch apps", "Control / lock screen", "Control / page"};
        StringBuilder result = new StringBuilder("WebView provider:\n");
        PackageInfo pkg = WebView.getCurrentWebViewPackage();
        result.append(pkg == null ? "Unknown" : pkg.packageName + " " + pkg.versionName).append("\n\n");
        for (int i = 0; i < keys.length; i++) {
            result.append(names[i]).append(":\n  ").append(p.getString(keys[i], "NOT TESTED")).append("\n\n");
        }
        new AlertDialog.Builder(this)
            .setTitle("Playback test results")
            .setMessage(result.toString())
            .setPositiveButton("Done", null)
            .setNeutralButton("Reset", (dialog, which) -> {
                p.edit().clear().apply();
                status.setText("Saved results cleared.");
            })
            .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (returnedFromBackgroundButton && status != null) {
            returnedFromBackgroundButton = false;
            status.setText("Returned to app. Did music continue? Tap LOG RESULT.");
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("source", source);
        if (webView != null) webView.saveState(outState);
        super.onSaveInstanceState(outState);
    }

    // Deliberately do NOT call webView.onPause() or pauseTimers() when backgrounded;
    // this test measures what the platform and the page actually do.
    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.destroy();
            webView = null;
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) webView.goBack();
        else super.onBackPressed();
    }

    private void addButton(LinearLayout row, String label, Runnable onClick, float weight) {
        Button button = new Button(this);
        button.setText(label);
        button.setTextSize(10);
        button.setAllCaps(false);
        button.setTextColor(Color.WHITE);
        button.setBackgroundTintList(android.content.res.ColorStateList.valueOf(ACCENT));
        button.setOnClickListener(v -> onClick.run());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, dp(43), weight);
        params.setMargins(dp(3), 0, dp(3), 0);
        row.addView(button, params);
    }

    private int dp(float value) {
        return (int) (getResources().getDisplayMetrics().density * value + 0.5f);
    }
}
