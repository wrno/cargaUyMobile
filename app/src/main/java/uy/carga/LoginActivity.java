package uy.carga;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        session = new SessionManager(getApplicationContext());

        Uri uri = getIntent().getData();
        String personaString = uri.getQueryParameter("persona");
        try {
            JSONObject json = new JSONObject(personaString);
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
    }
}