package com.example.chessforandroid;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.example.chessforandroid.util.RankingItem;

import java.util.ArrayList;

/**
 * Clase RankingAdapter, que funcionara como adaptador para utilizar cada RankingItem
 */
public class RankingAdapter extends BaseAdapter {
    /**
     * La activity en la que se este usando
     */
    private Context context;
    /**
     * Lista de elementos de la clasificacion
     */
    private ArrayList<RankingItem> lista;
    /**
     * Inflater encargado de pintar los elementos de la interfaz
     */
    private LayoutInflater inflater;

    public RankingAdapter(Activity context, ArrayList<RankingItem> lista) {
        this.context = context;
        this.lista = lista;
        this.inflater = LayoutInflater.from(context);//El inflater será el mismo que se esté encargando de la interfaz general de la activity
    }

    /**
     * Patron utilizado para ganar eficiencia en la carga y visualizacion de la clasificacion.
     * Esto ahorra hacer 4 findViewById cada vez que pinta un elemento de la lista, solo
     * lo pintara para el primero.
     */
    static class ViewHolder {
        TextView position;
        TextView user;
        TextView elo;
    }

    @Override
    public int getCount() {

        // el total de elementos en el listview es el total de la lista que representa
        return lista.size();
    }

    @Override
    public Object getItem(int position) {

        // cuando devuelve un elemento, devuelve el que coincide con la posicion seleccionada
        return lista.get(position);
    }

    @Override
    public long getItemId(int position) {

        // el id de cada item en la lista coincide con su posición
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // metodo que pinta un elemento de la lista
        ViewHolder holder = null;
        if (convertView == null) {

            // si es nulo, sea establece el holder en la convertView
            convertView = inflater.inflate(R.layout.fila_ranking, null);

            holder = new ViewHolder();
            holder.position = convertView.findViewById(R.id.txtPosition);
            holder.user = convertView.findViewById(R.id.txtUserRanking);
            holder.elo = convertView.findViewById(R.id.txtEloRanking);

            convertView.setTag(holder);

        } else {

            // si no es nulo, se toma el ya creado
            holder = (ViewHolder) convertView.getTag();
        }

        // elemento a pintar
        RankingItem ri = lista.get(position);

        // cada dato en su elemento de interfaz en el holder
        holder.position.setText(String.valueOf(ri.getPosition()));
        holder.user.setText(ri.getUser());
        holder.elo.setText(ri.getElo());

        return convertView;
    }

}

