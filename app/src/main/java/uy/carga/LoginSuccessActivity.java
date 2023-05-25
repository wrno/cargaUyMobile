package uy.carga;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Map;

public class LoginSuccessActivity extends AppCompatActivity {
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_success);
        session = new SessionManager(getApplicationContext());
        TextView txtCi = findViewById(R.id.textViewCi);
        TextView txtNombre = findViewById(R.id.textViewFullName);
        TextView txtEmail = findViewById(R.id.textViewEmail);

        Map<String, String> userDetails = session.getUserDetails();

        String ci = txtCi.getText() + " " + userDetails.get(SessionManager.KEY_CI);
        String nombre = txtNombre.getText() + " " + userDetails.get(SessionManager.KEY_NAME);
        String email = txtEmail.getText() + " " + userDetails.get(SessionManager.KEY_EMAIL);

        txtCi.setText(ci);
        txtNombre.setText(nombre);
        txtEmail.setText(email);
    }
}