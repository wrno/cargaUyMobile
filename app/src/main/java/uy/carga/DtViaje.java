package uy.carga;

import android.annotation.SuppressLint;

import com.google.gson.annotations.Expose;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Objects;

public class DtViaje {
    @Expose private final int id;
    @Expose private final String fecha;
    @Expose private final String empresa;
    @Expose private final String rubroCliente;
    @Expose private final float volumenCarga;
    @Expose private final String origen;
    @Expose private final double origenX;
    @Expose private final double origenY;
    @Expose private final String destino;
    @Expose private final double destinoX;
    @Expose private final double destinoY;
    @Expose private final String vehiculo;
    @Expose private final String estado;

    @SuppressLint("SimpleDateFormat")
    public DtViaje(JSONObject viaje) throws JSONException {
        String fecha1;
        this.id = viaje.getInt("id");
        try {
            fecha1 = getFormat().format(Objects.requireNonNull((new SimpleDateFormat("yyyy-MM-dd")).parse(viaje.getString("fecha").substring(0, 10))));
        } catch (ParseException e) {
            fecha1 = viaje.getString("fecha");
        }
        this.fecha = fecha1;
        this.empresa = viaje.getJSONObject("empresa").getString("nombre");
        this.rubroCliente = viaje.getJSONObject("rubroCliente").getString("rubro");
        this.volumenCarga = (float) viaje.getDouble("volumenCarga");
        this.origen = viaje.getString("dirOrigen");
        this.origenX = viaje.getJSONObject("origen").getDouble("x");
        this.origenY = viaje.getJSONObject("origen").getDouble("y");
        this.destino = viaje.getString("dirDestino");
        this.destinoX = viaje.getJSONObject("destino").getDouble("x");
        this.destinoY = viaje.getJSONObject("destino").getDouble("y");
        this.vehiculo = viaje.getJSONObject("vehiculo").getString("matricula");
        this.estado = viaje.getString("estado");
    }

    public int getId() {
        return id;
    }

    public String getFecha() {
        return fecha;
    }

    public String getOrigen() {
        return origen;
    }

    public String getDestino() {
        return destino;
    }

    public String getEstado() {
        return estado;
    }

    public String getEmpresa() {
        return empresa;
    }

    public String getRubroCliente() {
        return rubroCliente;
    }

    public double getOrigenX() {
        return origenX;
    }

    public double getOrigenY() {
        return origenY;
    }

    public double getDestinoX() {
        return destinoX;
    }

    public double getDestinoY() {
        return destinoY;
    }

    public String getVehiculo() {
        return vehiculo;
    }

    public float getVolumenCarga() {
        return volumenCarga;
    }

    @SuppressLint("SimpleDateFormat")
    public DateFormat getFormat() {
        return new SimpleDateFormat("dd/MM/yyyy");
    }
}
