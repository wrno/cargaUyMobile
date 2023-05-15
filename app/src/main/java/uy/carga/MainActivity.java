package uy.carga;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String error = getIntent().getStringExtra("errorMessage");
        if(error != null){
            Snackbar.make(
                    findViewById(R.id.button),
                    error,
                    BaseTransientBottomBar.LENGTH_INDEFINITE).show();
        }
    }

    public void iniciarSesion(View v){
        try {
            /*
            En Desarrollo, usar ngrok para acceder a los servicios
            con certificado SSL confiable. La URL debe setearse
            en AndroidManifest.xml.
             */
            String url = this.getPackageManager().getApplicationInfo(
                    this.getPackageName(),
                    PackageManager.GET_META_DATA
            ).metaData.getString("api_url");
            url += "/cargauy-services/rest/mobile/login-gubuy";

            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        }catch (PackageManager.NameNotFoundException e){
            Snackbar.make(
                    findViewById(R.id.button),
                    Objects.requireNonNull(e.getLocalizedMessage()),
                    BaseTransientBottomBar.LENGTH_INDEFINITE).show();
        }
    }
}