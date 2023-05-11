package uy.carga;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import java.util.HashMap;
import java.util.Map;

public class WebViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        WebView webView = findViewById(R.id.webView);
        webView.setWebChromeClient(new WebChromeClient());

        /*
        En Desarrollo, usar ngrok para acceder a los servicios
        con certificado SSL confiable.
         */
        String url = "https://125b-2800-a4-29d9-4900-49e-b697-5b5b-a5a3.ngrok-free.app";
        url += "/cargauy-services/rest/mobile/login-gubuy";

        /*
        Se debe agregar el header "ngrok-skip-browser-warning"
        con cualquier valor para evitar la página de warning de ngrok.
         */
        Map<String, String> headers = new HashMap<>();
        headers.put("ngrok-skip-browser-warning", "skip warning");
        webView.loadUrl(url, headers);
    }
}