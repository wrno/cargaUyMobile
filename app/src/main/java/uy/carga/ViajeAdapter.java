package uy.carga;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.ArrayList;

import uy.carga.databinding.ViajesLayoutBinding;

public class ViajeAdapter extends RecyclerView.Adapter<ViajeAdapter.MyViewHolder> implements ViajeClickListener {
    private final ArrayList<DtViaje> dataSet;
    private final Context context;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        final TextView textViewFecha;
        final TextView textViewOrigen;
        final TextView textViewDestino;
        public final ViajesLayoutBinding viajesLayoutBinding;

        public MyViewHolder(ViajesLayoutBinding viajesLayoutBinding) {
            super(viajesLayoutBinding.getRoot());
            this.viajesLayoutBinding = viajesLayoutBinding;
            this.textViewFecha = itemView.findViewById(R.id.txtFecha);
            this.textViewOrigen = itemView.findViewById(R.id.txtOrigen);
            this.textViewDestino = itemView.findViewById(R.id.txtDestino);
        }
    }

    public ViajeAdapter(ArrayList<DtViaje> data, Context ctx) {
        this.dataSet = data;
        context = ctx;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                           int viewType) {
        ViajesLayoutBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.viajes_layout, parent, false);

        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {
        holder.viajesLayoutBinding.setModel(dataSet.get(listPosition));

        TextView textViewFecha = holder.textViewFecha;
        TextView textViewOrigen = holder.textViewOrigen;
        TextView textViewDestino = holder.textViewDestino;

        String fecha = "Fecha: " + dataSet.get(listPosition).getFecha();
        String origen = "Origen: " + dataSet.get(listPosition).getOrigen();
        String destino = "Destino: " + dataSet.get(listPosition).getDestino();

        textViewFecha.setText(fecha);
        textViewOrigen.setText(origen);
        textViewDestino.setText(destino);

        holder.viajesLayoutBinding.setItemClickListener(this);
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    @SuppressWarnings("unused")
    public void cardClicked(DtViaje f) {
        Intent intent = new Intent(context, ViajeActivity.class);
        intent.putExtra("viaje", new Gson().toJson(f));
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        context.startActivity(intent);
    }
}
