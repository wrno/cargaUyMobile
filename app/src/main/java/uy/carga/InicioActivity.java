package uy.carga;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import uy.carga.ui.main.InicioFragment;

public class InicioActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SessionManager sessionManager = new SessionManager(getApplicationContext());
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        setContentView(R.layout.activity_inicio);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainerView, InicioFragment.newInstance())
                    .commitNow();
        }
        sessionManager.checkLogin();
        String error = getIntent().getStringExtra("errorMessage");
        if(error != null){
            Snackbar sb = Snackbar.make(
                    myToolbar,
                    error,
                    BaseTransientBottomBar.LENGTH_LONG
            );
            sb.getView().getRootView().setBackgroundColor(ContextCompat.getColor(this, R.color.black));
            sb.show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            SessionManager sessionManager = new SessionManager(getApplicationContext());
            sessionManager.logoutUser(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}