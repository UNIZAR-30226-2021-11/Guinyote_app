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

public class ListadoTorneosAdapter extends BaseAdapter {
    private Context context;
    private int layout;
    private ArrayList<Torneo> torneos;


    public ListadoTorneosAdapter(Context context, int layout, ArrayList<Torneo> torneos) {
        this.context = context;
        this.layout = layout;
        this.torneos = torneos;
    }

    @Override
    public int getCount() {
        return this.torneos.size();
    }

    @Override
    public Object getItem(int position) {
        return this.torneos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return this.torneos.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (convertView == null)    {
            LayoutInflater layoutInflater = LayoutInflater.from(this.context);

            v = layoutInflater.inflate(R.layout.list_item_partidas, parent, false);
        }

        String nombre = this.torneos.get(position).getNombre();

        TextView nombreTV = (TextView) v.findViewById(R.id.nombre);
        nombreTV.setText(nombre);
        return v;
    }
}
