package uy.carga;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class ViajeActivity extends AppCompatActivity {
    private DtViaje viaje;
    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private MapView map = null;

    @SuppressLint({"SetTextI18n", "SimpleDateFormat"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration.getInstance().load(getApplicationContext(), PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));
        setContentView(R.layout.activity_viaje);

        Toolbar toolbar = findViewById(R.id.toolbarViaje);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        SessionManager sessionManager = new SessionManager(getApplicationContext());
        sessionManager.checkLogin();

        String viajeJson = getIntent().getStringExtra("viaje");
        if (viajeJson != null) {
            viaje = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().fromJson(viajeJson, DtViaje.class);
            if (viaje != null) {
                TextView fecha = findViewById(R.id.varFecha);
                TextView estado = findViewById(R.id.varEstado);
                TextView empresa = findViewById(R.id.varEmpresa);
                TextView rubro = findViewById(R.id.varRubro);
                TextView volumen = findViewById(R.id.varVolumen);
                TextView vehiculo = findViewById(R.id.varVehiculo);
                TextView origen = findViewById(R.id.varOrigen);
                TextView destino = findViewById(R.id.varDestino);

                fecha.setText(viaje.getFecha());
                empresa.setText(viaje.getEmpresa());
                rubro.setText(viaje.getRubroCliente());
                volumen.setText(viaje.getVolumenCarga() + " t");
                vehiculo.setText(viaje.getVehiculo());
                origen.setText(viaje.getOrigen());
                destino.setText(viaje.getDestino());

                Button btnViaje = findViewById(R.id.buttonViaje);
                btnViaje.setText("No disponible");
                btnViaje.setEnabled(false);
                btnViaje.setVisibility(View.GONE);

                switch (viaje.getEstado()) {
                    case "SinIniciar":
                        Date hoy = new Date();
                        if (viaje.getFecha().compareTo(viaje.getFormat().format(hoy)) >= 0) {
                            estado.setText("Sin iniciar");
                            if (viaje.getFecha().compareTo(viaje.getFormat().format(hoy)) == 0) {
                                btnViaje.setText("Iniciar viaje");
                                btnViaje.setEnabled(true);
                                btnViaje.setVisibility(View.VISIBLE);
                            }
                        } else {
                            estado.setText("Expirado");
                        }
                        break;

                    case "Iniciado":
                        estado.setText("Iniciado");
                        btnViaje.setText("Finalizar viaje");
                        btnViaje.setEnabled(true);
                        btnViaje.setVisibility(View.VISIBLE);
                        break;

                    case "Terminado":
                        estado.setText("Terminado");
                        break;

                    case "Cancelado":
                        estado.setText("Cancelado");
                        break;
                }
            } else {
                finish();
            }
        } else {
            finish();
        }

        map = findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);

        requestPermissionsIfNecessary(new String[]{
                // if you need to show the current location, uncomment the line below
                // Manifest.permission.ACCESS_FINE_LOCATION,
                // WRITE_EXTERNAL_STORAGE is required in order to show the map
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        });

        map.setMultiTouchControls(true);
        IMapController mapController = map.getController();
        mapController.setZoom(16.0);
        GeoPoint origenPoint = new GeoPoint(viaje.getOrigenX(), viaje.getOrigenY());
        GeoPoint destinoPoint = new GeoPoint(viaje.getDestinoX(), viaje.getDestinoY());
        Marker origenMarker = new Marker(map);
        origenMarker.setPosition(origenPoint);
        map.getOverlays().add(origenMarker);
        Marker destinoMarker = new Marker(map);
        destinoMarker.setPosition(destinoPoint);
        map.getOverlays().add(destinoMarker);

        List<GeoPoint> points = new ArrayList<>();
        points.add(origenPoint);
        points.add(destinoPoint);

        map.addOnFirstLayoutListener((v, left, top, right, bottom) -> {
            map.zoomToBoundingBox(BoundingBox.fromGeoPoints(points), false, 100);
            map.invalidate();
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        map.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        map.onPause();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        ArrayList<String> permissionsToRequest = new ArrayList<>(Arrays.asList(permissions).subList(0, grantResults.length));
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                    this,
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    private void requestPermissionsIfNecessary(String[] permissions) {
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted
                permissionsToRequest.add(permission);
            }
        }
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                    this,
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    public void onOrigenClick(View v) {
        IMapController mapController = map.getController();
        GeoPoint origen = new GeoPoint(viaje.getOrigenX(), viaje.getOrigenY());
        mapController.setCenter(origen);
        map.invalidate();
    }

    public void onDestinoClick(View v) {
        IMapController mapController = map.getController();
        GeoPoint destino = new GeoPoint(viaje.getDestinoX(), viaje.getDestinoY());
        mapController.setCenter(destino);
        map.invalidate();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        if (item.getItemId() == android.R.id.home) {
            Intent i = new Intent(this, InicioActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onViajeClick(View v) {
        v.setEnabled(false);
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        String url;
        try {
            url = this.getPackageManager().getApplicationInfo(
                    this.getPackageName(),
                    PackageManager.GET_META_DATA
            ).metaData.getString("api_url");
            if (viaje.getEstado().equals("SinIniciar")) {
                url += "/comenzarViaje";
            } else if (viaje.getEstado().equals("Iniciado")) {
                url += "/terminarViaje";
            } else {
                Intent i = new Intent(this, InicioActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("errorMessage", "Se inició un viaje.");
                startActivity(i);
                finish();
            }

            StringRequest request = new StringRequest(Request.Method.POST, url,
                    response -> {
                        Intent i = new Intent(this, InicioActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        if(viaje.getEstado().equals("SinIniciar")) {
                            i.putExtra("errorMessage", "Se inició el viaje.");
                        }else if(viaje.getEstado().equals("Iniciado")){
                            i.putExtra("errorMessage", "Finalizó el viaje.");
                        }
                        startActivity(i);
                        finish();
                    },

                    error -> {
                        if(error.getLocalizedMessage() != null){
                            Snackbar sb = Snackbar.make(
                                    findViewById(R.id.toolbarViaje),
                                    Objects.requireNonNull(error.getLocalizedMessage()),
                                    BaseTransientBottomBar.LENGTH_LONG
                            );
                            sb.show();
                        }else if (error.getCause() != null) {
                            Snackbar sb = Snackbar.make(
                                    findViewById(R.id.toolbarViaje),
                                    Objects.requireNonNull(error.getCause().getLocalizedMessage()),
                                    BaseTransientBottomBar.LENGTH_LONG
                            );
                            sb.show();
                        } else {
                            Snackbar sb = Snackbar.make(
                                    findViewById(R.id.toolbarViaje),
                                    String.valueOf(error.networkResponse.statusCode),
                                    BaseTransientBottomBar.LENGTH_LONG
                            );
                            sb.show();
                        }
                        v.setEnabled(true);
                    }) {
                @Override
                public byte[] getBody() {
                    HashMap<String, Object> params = new HashMap<>();
                    params.put("id", viaje.getId());
                    return new JSONObject(params).toString().getBytes();
                }

                @Override
                public String getBodyContentType() {
                    return "application/json";
                }
            };

            requestQueue.add(request);
        } catch (PackageManager.NameNotFoundException e) {
            Snackbar sb = Snackbar.make(
                    findViewById(R.id.toolbarViaje),
                    Objects.requireNonNull(e.getLocalizedMessage()),
                    BaseTransientBottomBar.LENGTH_LONG
            );
            sb.show();
            v.setEnabled(true);
        }
    }
}