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

public class RankingAdapter extends BaseAdapter {
    private Context context; //El contexto será la activity en la que se esté usando
    private ArrayList<RankingItem> lista; //La lista de elementos
    private LayoutInflater inflater;//El encargado de distribuir/pintar los elementos en la interfaz

    public RankingAdapter(Activity context, ArrayList<RankingItem> lista) {
        this.context = context;
        this.lista = lista;
        this.inflater = LayoutInflater.from(context);//El inflater será el mismo que se esté encargando de la interfaz general de la activity
    }

    static class ViewHolder {
        //Patrón utilizado para ganar eficiencia en la carga y visualización de listas
        //Básicamente nos ahorra tener que hacer 4 findViewById cada vez que pinta un elemento de la lista (solo los hará para el primero)
        TextView position;
        TextView user;
        TextView elo;
    }

    @Override
    public int getCount() {
        //El total de elementos en el listview coincidirá con el total de la lista que representa
        return lista.size();
    }

    @Override
    public Object getItem(int position) {
        //Cuando nos tenga que devolver un elemento, querremos que nos devuelva el que coincide con la posición seleccionada
        return lista.get(position);
    }

    @Override
    public long getItemId(int position) {
        //El id de cada item en la lista coincidirá con su posición
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //Este es el método que pinta un elemento de la lista, por lo tanto se ejecutará una vez para cada elemento que se pinte

        ViewHolder holder = null;
        if (convertView == null) {//Si convertView es nulo, será la primera vez que
            convertView = inflater.inflate(R.layout.fila_ranking, null);
            holder = new ViewHolder();
            holder.position = convertView.findViewById(R.id.txtPosition);
            holder.user = convertView.findViewById(R.id.txtUserRanking);
            holder.elo = convertView.findViewById(R.id.txtEloRanking);
            convertView.setTag(holder);//añado el holder a la convertView, para tenerlos siempre disponible
        } else {
            holder = (ViewHolder) convertView.getTag();//Si no es la primera vez, cogeré el holder que ya había creado
        }
        RankingItem ri = lista.get(position);//cojo el elemento que toca pintar

        //Pongo cada dato en su elemento de interfaz en el holder

        holder.position.setText(String.valueOf(ri.getPosition()));
        holder.user.setText(ri.getUser());
        holder.elo.setText(ri.getElo());
        return convertView;//devuelvo la vista del elemento
    }

}

