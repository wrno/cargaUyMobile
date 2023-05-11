package uy.carga;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Uri uri = getIntent().getData();
        String code = uri.getQueryParameter("code");
        String state = uri.getQueryParameter("state");

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        /*
        En Desarrollo, usar ngrok para acceder a los servicios
        con certificado SSL confiable.
         */
        String url = "https://125b-2800-a4-29d9-4900-49e-b697-5b5b-a5a3.ngrok-free.app";
        url += "/cargauy-services/rest/mobile/login-token";

        /*
        Se debe agregar el header "ngrok-skip-browser-warning"
        con cualquier valor para evitar la página de warning de ngrok.
         */
        Map<String, String> headers = new HashMap<>();
        headers.put("ngrok-skip-browser-warning", "skip warning");

        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Intent intent = new Intent(LoginActivity.this,
                                MainActivity.class);
                        intent.putExtra("errorMessage", error.getLocalizedMessage());
                        startActivity(intent);
                    }
                });
    }
}