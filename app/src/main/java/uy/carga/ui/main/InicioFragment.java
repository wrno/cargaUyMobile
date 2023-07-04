package uy.carga.ui.main;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import uy.carga.DtViaje;
import uy.carga.R;
import uy.carga.SessionManager;
import uy.carga.ViajeAdapter;
import uy.carga.databinding.FragmentMainBinding;

public class InicioFragment extends Fragment {
    private static RecyclerView.Adapter<ViajeAdapter.MyViewHolder> adapterHoy;
    private static RecyclerView.Adapter<ViajeAdapter.MyViewHolder> adapterEnProgreso;
    private static RecyclerView.Adapter<ViajeAdapter.MyViewHolder> adapterFinalizados;
    private static RecyclerView.Adapter<ViajeAdapter.MyViewHolder> adapterCancelados;
    private static RecyclerView.Adapter<ViajeAdapter.MyViewHolder> adapterProximos;
    private static ArrayList<DtViaje> viajesHoy;
    private static ArrayList<DtViaje> viajesEnProgreso;
    private static ArrayList<DtViaje> viajesFinalizados;
    private static ArrayList<DtViaje> viajesCancelados;
    private static ArrayList<DtViaje> viajesProximos;
    private FragmentMainBinding binding;

    public static InicioFragment newInstance() {
        return new InicioFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        SessionManager sessionManager = new SessionManager(requireActivity().getApplicationContext());
        TextView welcome = view.findViewById(R.id.msgBienvenida);
        String msgWelcome = "¡Bienvenido, " + sessionManager.getUserDetails().get(SessionManager.KEY_NAME) + "!";
        welcome.setText(msgWelcome);

        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_main, container, false);

        RecyclerView recyclerViewHoy = view.findViewById(R.id.recyclerHoy);
        RecyclerView recyclerViewCancelados = view.findViewById(R.id.recyclerCancelados);
        RecyclerView recyclerViewEnProgreso = view.findViewById(R.id.recyclerEnProgreso);
        RecyclerView recyclerViewFinalizados = view.findViewById(R.id.recyclerFinalizados);
        RecyclerView recyclerViewProximos = view.findViewById(R.id.recyclerProximos);

        RecyclerView.LayoutManager layoutManagerProximos = new LinearLayoutManager(getContext());
        RecyclerView.LayoutManager layoutManagerCancelados = new LinearLayoutManager(getContext());
        RecyclerView.LayoutManager layoutManagerFinalizados = new LinearLayoutManager(getContext());
        RecyclerView.LayoutManager layoutManagerHoy = new LinearLayoutManager(getContext());
        RecyclerView.LayoutManager layoutManagerEnProgreso = new LinearLayoutManager(getContext());

        recyclerViewProximos.setLayoutManager(layoutManagerProximos);
        recyclerViewHoy.setLayoutManager(layoutManagerHoy);
        recyclerViewFinalizados.setLayoutManager(layoutManagerFinalizados);
        recyclerViewCancelados.setLayoutManager(layoutManagerCancelados);
        recyclerViewEnProgreso.setLayoutManager(layoutManagerEnProgreso);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        SessionManager sessionManager = new SessionManager(this.requireActivity().getApplicationContext());
        sessionManager.checkLogin();

        RecyclerView recyclerHoy = requireView().findViewById(R.id.recyclerHoy);
        recyclerHoy.setItemAnimator(new DefaultItemAnimator());

        RecyclerView recyclerEnProgreso = requireView().findViewById(R.id.recyclerEnProgreso);
        recyclerEnProgreso.setItemAnimator(new DefaultItemAnimator());

        RecyclerView recyclerProximos = requireView().findViewById(R.id.recyclerProximos);
        recyclerProximos.setItemAnimator(new DefaultItemAnimator());

        RecyclerView recyclerFinalizados = requireView().findViewById(R.id.recyclerFinalizados);
        recyclerFinalizados.setItemAnimator(new DefaultItemAnimator());

        RecyclerView recyclerCancelados = requireView().findViewById(R.id.recyclerCancelados);
        recyclerCancelados.setItemAnimator(new DefaultItemAnimator());

        RequestQueue requestQueue = Volley.newRequestQueue(this.requireContext());

        String url;
        try {
            url = this.requireContext().getPackageManager().getApplicationInfo(
                    this.requireContext().getPackageName(),
                    PackageManager.GET_META_DATA
            ).metaData.getString("api_url");
            url += "/viajes/";
            url += sessionManager.getUserDetails().get(SessionManager.KEY_CI);

            StringRequest stringRequest = new StringRequest(url,
                    response -> {
                        try {
                            JSONArray list = new JSONArray(response);
                            if (viajesProximos != null){
                                viajesProximos.clear();
                            }else {
                                viajesProximos = new ArrayList<>();
                            }
                            if (viajesCancelados != null){
                                viajesCancelados.clear();
                            }else {
                                viajesCancelados = new ArrayList<>();
                            }
                            if (viajesHoy != null){
                                viajesHoy.clear();
                            }else {
                                viajesHoy = new ArrayList<>();
                            }
                            if (viajesFinalizados != null){
                                viajesFinalizados.clear();
                            }else {
                                viajesFinalizados = new ArrayList<>();
                            }
                            if (viajesEnProgreso != null){
                                viajesEnProgreso.clear();
                            }else {
                                viajesEnProgreso = new ArrayList<>();
                            }
                            for(int i = 0; i < list.length(); i++){
                                JSONObject viaje = (JSONObject) list.get(i);
                                @SuppressLint("SimpleDateFormat") DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                                Date fecha = formatter.parse(viaje.getString("fecha").substring(0,10));
                                assert fecha != null;
                                DtViaje dtViaje =
                                        new DtViaje(viaje);
                                switch (dtViaje.getEstado()){
                                    case "SinIniciar":
                                        Date hoy = new Date();
                                        Calendar time = Calendar.getInstance();
                                        time.setTime(hoy);
                                        time.set(Calendar.HOUR, 0);
                                        time.set(Calendar.MINUTE, 0);
                                        time.set(Calendar.SECOND, 0);
                                        time.set(Calendar.MILLISECOND, 0);
                                        hoy = time.getTime();
                                        if (fecha.equals(hoy)){
                                            viajesHoy.add(dtViaje);
                                        }else if (fecha.compareTo(hoy) > 0){
                                            viajesProximos.add(dtViaje);
                                        }else {
                                            viajesCancelados.add(dtViaje);
                                        }
                                        break;

                                    case "Iniciado":
                                        viajesEnProgreso.add(dtViaje);
                                        break;

                                    case "Terminado":
                                        viajesFinalizados.add(dtViaje);
                                        break;

                                    case "Cancelado":
                                        viajesCancelados.add(dtViaje);
                                }
                            }

                            adapterHoy = new ViajeAdapter(viajesHoy, getActivity());
                            adapterCancelados = new ViajeAdapter(viajesCancelados, getActivity());
                            adapterFinalizados = new ViajeAdapter(viajesFinalizados, getActivity());
                            adapterEnProgreso = new ViajeAdapter(viajesEnProgreso, getActivity());
                            adapterProximos = new ViajeAdapter(viajesProximos, getActivity());

                            recyclerCancelados.setAdapter(adapterCancelados);
                            recyclerHoy.setAdapter(adapterHoy);
                            recyclerFinalizados.setAdapter(adapterFinalizados);
                            recyclerEnProgreso.setAdapter(adapterEnProgreso);
                            recyclerProximos.setAdapter(adapterProximos);

                            binding.setAdapterCancelados((ViajeAdapter) adapterCancelados);
                            binding.setAdapterEnProgreso((ViajeAdapter) adapterEnProgreso);
                            binding.setAdapterFinalizados((ViajeAdapter) adapterFinalizados);
                            binding.setAdapterHoy((ViajeAdapter) adapterHoy);
                            binding.setAdapterProximos((ViajeAdapter) adapterProximos);
                        } catch (JSONException | ParseException e) {
                            Log.e("Error JSON/Parse", e.getLocalizedMessage());
                            Snackbar sb = Snackbar.make(
                                    requireView().findViewById(R.id.msgBienvenida),
                                    Objects.requireNonNull(e.getLocalizedMessage()),
                                    BaseTransientBottomBar.LENGTH_LONG
                            );
                            sb.show();
                        }

                    },

                    error -> {
                        if (error.getCause() != null) {
                            Snackbar sb = Snackbar.make(
                                    requireView().findViewById(R.id.msgBienvenida),
                                    Objects.requireNonNull(error.getCause().getLocalizedMessage()),
                                    BaseTransientBottomBar.LENGTH_LONG
                            );
                            sb.show();
                        }else {
                            Snackbar sb = Snackbar.make(
                                    requireView().findViewById(R.id.msgBienvenida),
                                    String.valueOf(error.networkResponse.statusCode),
                                    BaseTransientBottomBar.LENGTH_LONG
                            );
                            sb.show();
                        }
                    });

            requestQueue.add(stringRequest);
        } catch (PackageManager.NameNotFoundException e) {
            Snackbar sb = Snackbar.make(
                    requireView().findViewById(R.id.msgBienvenida),
                    Objects.requireNonNull(e.getLocalizedMessage()),
                    BaseTransientBottomBar.LENGTH_LONG
            );
            sb.show();
        }
    }
}