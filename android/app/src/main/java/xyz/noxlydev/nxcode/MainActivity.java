package xyz.noxlydev.nxcode;

import android.graphics.Color;
import android.os.Build;
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

        Bridge bridge = getBridge();
        if (bridge == null) return;

        WebView webView = bridge.getWebView();
        if (webView == null) return;

        WebSettings settings = webView.getSettings();

        // JavaScript wajib aktif untuk code-server
        settings.setJavaScriptEnabled(true);

        // DOM storage (VS Code pakai ini untuk settings/state)
        settings.setDomStorageEnabled(true);

        // Database storage
        settings.setDatabaseEnabled(true);

        // Izinkan akses file lokal
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);

        // Izinkan mixed content (HTTP di dalam HTTPS context)
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

        // Cache mode default supaya Service Worker code-server bisa berjalan
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);

        // FIX: Matikan force dark mode — penyebab utama layar hitam di Android 10+
        // Force dark dari sistem bisa membuat canvas/renderer VS Code jadi hitam
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // Android 13+ pakai API baru
                settings.setAlgorithmicDarkeningAllowed(false);
            } else {
                // Android 10-12
                settings.setForceDark(WebSettings.FORCE_DARK_OFF);
            }
        }

        // FIX: Background color eksplisit — cegah flicker hitam saat load
        webView.setBackgroundColor(Color.parseColor("#1e1e1e"));

        // Prevent long press default (copy/paste bubble yang ganggu)
        webView.setOnLongClickListener(v -> true);

        // Haptic feedback dimatikan karena long press di-override
        webView.setHapticFeedbackEnabled(false);
    }
}
