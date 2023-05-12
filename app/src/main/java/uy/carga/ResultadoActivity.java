package uy.carga;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ResultadoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultado);
        TextView txtCi = findViewById(R.id.textViewCi);
        TextView txtNombre = findViewById(R.id.textViewFullName);
        TextView txtEmail = findViewById(R.id.textViewEmail);

        Intent intent = getIntent();
        String ci = txtCi.getText() + " " + intent.getStringExtra("ci").trim();
        String nombre = txtNombre.getText() + " " + intent.getStringExtra("nombre").trim();
        String email = txtEmail.getText() + " " + intent.getStringExtra("email").trim();

        txtCi.setText(ci);
        txtNombre.setText(nombre);
        txtEmail.setText(email);
    }
}