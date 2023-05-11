package uy.carga;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String error = getIntent().getStringExtra("errorMessage");
        if(error != null){

        }
    }

    public void iniciarSesion(View v){
        Intent intent = new Intent(this, WebViewActivity.class);
        startActivity(intent);
    }
}