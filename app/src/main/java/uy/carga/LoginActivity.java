package uy.carga;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        session = new SessionManager(getApplicationContext());

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
            url += "/cargauy-services/rest/mobile/callback";
            url += "?code=" + code;
            url += "&state=" + state;

            StringRequest request = new StringRequest(Request.Method.GET, url,
                    response -> {
                        session.logoutUser(this);
                        try {
                            JSONObject json = new JSONObject(response);
                            String ci = json.getString("ci");
                            String nombre = json.getString("nombre");
                            String email = json.getString("email");
                            String refreshToken = json.getString("refreshToken");

                            Intent intent = new Intent(
                                    getBaseContext(),
                                    InicioActivity.class
                            );

                            // No permitimos regresar.
                            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

                            // Creamos la sesión.
                            session.createLoginSession(nombre, ci, email, refreshToken);

                            startActivity(intent);
                            LoginActivity.this.finish();
                        } catch (JSONException e) {
                            Intent intent = new Intent(getBaseContext(),
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
                    },
                    error -> {
                        Intent intent = new Intent(getBaseContext(),
                                MainActivity.class);

                        if(error.getCause() != null){
                            if(error.getCause().getLocalizedMessage() != null){
                                intent.putExtra("errorMessage", error.getCause().getLocalizedMessage());
                            }else if (error.getCause().getMessage() != null) {
                                    intent.putExtra("errorMessage", error.getCause().getMessage());
                            }
                        }else {
                                intent.putExtra("errorMessage", "Error "
                                        + error.networkResponse.statusCode);
                        }

                        startActivity(intent);
                        LoginActivity.this.finish();
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
            Intent intent = new Intent(getBaseContext(), MainActivity.class);
            if(e.getLocalizedMessage() != null){
                intent.putExtra("errorMessage", e.getLocalizedMessage());
            }else{
                intent.putExtra("errorMessage",
                        "Hubo un error intentando establecer la conexión." +
                                "Intente nuevamente.");
            }
            startActivity(intent);
            LoginActivity.this.finish();
        }
    }
}