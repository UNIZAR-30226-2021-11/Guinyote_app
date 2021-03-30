package manyosoft.guinyote.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

import manyosoft.guinyote.R;

public class ListadoPartidasAdapter extends BaseAdapter {
    private Context context;
    private int layout;
    private ArrayList<Partida> partidas;


    public ListadoPartidasAdapter(Context context, int layout, ArrayList<Partida> partidas) {
        this.context = context;
        this.layout = layout;
        this.partidas = partidas;
    }

    @Override
    public int getCount() {
        return this.partidas.size();
    }

    @Override
    public Object getItem(int position) {
        return this.partidas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return this.partidas.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (convertView == null)    {
            LayoutInflater layoutInflater = LayoutInflater.from(this.context);

            v = layoutInflater.inflate(R.layout.list_item_partidas, parent, false);
        }

        String nombre = this.partidas.get(position).getNombre();
        Integer jugadores = this.partidas.get(position).getJugadores();

        TextView nombreTV = (TextView) v.findViewById(R.id.nombre);
        nombreTV.setText(nombre);

        TextView jugadoresTV = (TextView) v.findViewById(R.id.ocupacion);
        jugadoresTV.setText(String.format(Locale.getDefault(), "%d/4", jugadores));

        return v;
    }
}
