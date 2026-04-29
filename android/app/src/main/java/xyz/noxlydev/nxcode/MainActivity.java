package xyz.noxlydev.nxcode;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import com.getcapacitor.Bridge;
import com.getcapacitor.BridgeActivity;
import com.getcapacitor.BridgeWebViewClient;

public class MainActivity extends BridgeActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();

        Bridge bridge = getBridge();
        if (bridge == null) return;

        WebView webView = bridge.getWebView();
        if (webView == null) return;

        WebSettings settings = webView.getSettings();

        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);

        // Matikan force dark — cegah layar hitam di Android 10+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                settings.setAlgorithmicDarkeningAllowed(false);
            } else {
                settings.setForceDark(WebSettings.FORCE_DARK_OFF);
            }
        }

        webView.setBackgroundColor(Color.parseColor("#1e1e1e"));
        webView.setOnLongClickListener(v -> true);
        webView.setHapticFeedbackEnabled(false);

        // FIX UTAMA: Override WebViewClient untuk mencegah dialog "Open with"
        // Kapasitor default-nya melempar Android intent untuk semua URL http://
        // yang tidak dikenal — ini yang menyebabkan dialog muncul.
        // Solusi: extend BridgeWebViewClient, tangkap URL localhost,
        // dan paksa load langsung di WebView tanpa intent.
        webView.setWebViewClient(new BridgeWebViewClient(bridge) {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                // Semua URL localhost/127.0.0.1 → load dalam WebView, BUKAN intent
                if (url.contains("localhost") || url.contains("127.0.0.1")) {
                    view.loadUrl(url);
                    return true;
                }
                // URL eksternal → biarkan Capacitor handle (buka di luar app)
                return super.shouldOverrideUrlLoading(view, request);
            }
        });
    }
}
