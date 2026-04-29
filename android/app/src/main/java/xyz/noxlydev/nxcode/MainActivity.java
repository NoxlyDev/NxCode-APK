package xyz.noxlydev.nxcode;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import com.getcapacitor.Bridge;
import com.getcapacitor.BridgeActivity;

public class MainActivity extends BridgeActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();

        // Null check dulu sebelum akses bridge/webview
        // Tanpa ini → crash saat activity recreate (rotate, popup, dsb)
        Bridge bridge = getBridge();
        if (bridge == null) return;

        WebView webView = bridge.getWebView();
        if (webView == null) return;

        WebSettings settings = webView.getSettings();

        // JavaScript wajib aktif untuk code-server
        settings.setJavaScriptEnabled(true);

        // Izinkan DOM storage (dipakai VS Code untuk settings/state)
        settings.setDomStorageEnabled(true);

        // Izinkan mixed content (http di dalam https context)
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

        // Prevent long press default (copy/paste bubble yang ganggu)
        webView.setOnLongClickListener(v -> true);

        // Haptic feedback tetap jalan meski long press di-override
        webView.setHapticFeedbackEnabled(false);
    }
}
