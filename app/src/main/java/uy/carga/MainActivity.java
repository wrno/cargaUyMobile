package uy.carga;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        session = new SessionManager(getApplicationContext());

        if(session.isLoggedIn()){ // Si el usuario ya estaba logueado:
            Intent intent = new Intent(getBaseContext(), InicioActivity.class);

            // No permitimos regresar.
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

            startActivity(intent);
            this.finish();
        }else{
            String error = getIntent().getStringExtra("errorMessage");
            if(error != null){
                Snackbar sb = Snackbar.make(
                        findViewById(R.id.button),
                        error,
                        BaseTransientBottomBar.LENGTH_INDEFINITE
                );
                sb.getView().getRootView().setBackgroundColor(ContextCompat.getColor(this, R.color.black));
                sb.show();
            }
        }
    }

    public void iniciarSesion(View v){
        try {
            String url = this.getPackageManager().getApplicationInfo(
                    this.getPackageName(),
                    PackageManager.GET_META_DATA
            ).metaData.getString("api_url");
            url += "/login";

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