package manyosoft.guinyote.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import manyosoft.guinyote.R;
import manyosoft.guinyote.util.GuinyoteClienteJWT;
import manyosoft.guinyote.util.ListadoPartidasAdapter;
import manyosoft.guinyote.util.ListadoTorneosAdapter;
import manyosoft.guinyote.util.Partida;
import manyosoft.guinyote.util.Torneo;

public class ListadoTorneosActivity extends AppCompatActivity {

    private ListView listadoPartidas;
    private ArrayList<Torneo> torneos;
    private GuinyoteClienteJWT clienteJWT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_listado_torneos);

        // Botón de retroceso
        FloatingActionButton back = findViewById(R.id.back);
        back.setOnClickListener(view -> onBackPressed());

        clienteJWT = GuinyoteClienteJWT.getInstance();

        listadoPartidas = findViewById(R.id.listView_torneo);

        try {
            torneos = clienteJWT.getTournamentGames(this);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            torneos = new ArrayList<>();
        }

        // Adaptador para la lista de partidas
        ListadoTorneosAdapter adaptador = new ListadoTorneosAdapter(this, R.layout.list_item_partidas, torneos);
        listadoPartidas.setAdapter(adaptador);
        listadoPartidas.setClickable(true);

        listadoPartidas.setOnItemClickListener((adapterView, view, position, id) -> {
            Toast.makeText(ListadoTorneosActivity.this, "Seleccionaste la partida: " + torneos.get(position).getNombre() + ", id:"+id, Toast.LENGTH_SHORT).show();
            Intent teamSelection = new Intent(ListadoTorneosActivity.this, SeleccionEquipoActivity.class);
            teamSelection.putExtra("id",id);
            startActivity(teamSelection);
        });
    }
}