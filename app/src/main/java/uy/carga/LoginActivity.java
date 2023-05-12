package uy.carga;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

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

        try {
        /*
        En Desarrollo, usar ngrok para acceder a los servicios
        con certificado SSL confiable.
         */
            String url = this.getPackageManager().getApplicationInfo(
                    this.getPackageName(),
                    PackageManager.GET_META_DATA
            ).metaData.getString("api_url");
            url += "/cargauy-services/rest/mobile/login";
            url += "?code=" + code;
            url += "&state=" + state;

            StringRequest request = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject json = new JSONObject(response);
                                String ci = json.getString("ci");
                                String nombre = json.getString("nombre");
                                String email = json.getString("email");

                                Intent intent = new Intent(
                                        LoginActivity.this,
                                        ResultadoActivity.class
                                );

                                intent.putExtra("ci", ci);
                                intent.putExtra("nombre", nombre);
                                intent.putExtra("email", email);

                                startActivity(intent);
                            } catch (JSONException e) {
                                Intent intent = new Intent(LoginActivity.this,
                                        MainActivity.class);
                                if(e.getLocalizedMessage() != null){
                                    intent.putExtra("errorMessage", e.getLocalizedMessage());
                                }else{
                                    intent.putExtra("errorMessage",
                                            "Hubo un error intentando obtener los datos." +
                                                    "Intente nuevamente.");
                                }
                                startActivity(intent);
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Intent intent = new Intent(LoginActivity.this,
                                    MainActivity.class);

                            if(error.getLocalizedMessage() != null){
                                intent.putExtra("errorMessage", error.getLocalizedMessage());
                            }else{
                                intent.putExtra("errorMessage", "Error "
                                        + String.valueOf(error.networkResponse.statusCode));
                            }

                            startActivity(intent);
                        }
                    }) {
                /*
                Se debe agregar el header "ngrok-skip-browser-warning"
                con cualquier valor para evitar la página de warning de ngrok.
                 */
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> params = new HashMap<>();
                    params.put("ngrok-skip-browser-warning", "skip warning");
                    return params;
                }
            };

            requestQueue.add(request);
        }catch (PackageManager.NameNotFoundException e){
            Intent intent = new Intent(this, MainActivity.class);
            if(e.getLocalizedMessage() != null){
                intent.putExtra("errorMessage", e.getLocalizedMessage());
            }else{
                intent.putExtra("errorMessage",
                        "Hubo un error intentando establecer la conexión." +
                                "Intente nuevamente.");
            }
            startActivity(intent);
        }
    }
}