package manyosoft.guinyote.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketExtension;
import com.neovisionaries.ws.client.WebSocketFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.EmptyStackException;

import manyosoft.guinyote.R;
import manyosoft.guinyote.util.EstadoPartida;
import manyosoft.guinyote.util.Usuario;

public class JuegoActivity extends AppCompatActivity {
    /**
     * Servidor remoto del backend
     */
    private static final String SERVER      = "echo.websocket.org";
    private static final String SECURE_DIR  = "wss://" + SERVER;
    private static final String DIR         = "ws://" + SERVER;

    // TODO RELLENAR LOS EVENTOS
    private static final String EVENTO_CANTAR    = "";
    private static final String EVENTO_NOCANTAR  = "";
    private static final String EVENTO_CAMBIAR   = "";
    private static final String EVENTO_NOCAMBIAR = "";

    /**
     * The timeout value in milliseconds for socket connection.
     */
    private static final int TIMEOUT = 5000;

    /**
     * Websocket para la conexión al backend
     */
    private static WebSocket ws;

    // Cartas de la mano del jugador
    private ImageButton carta1, carta2, carta3, carta4, carta5, carta6;
    // Cartas del tablero
    private ImageView triunfo, monton_robar, monton1, monton2, monton3;
    // Texto que muestra el nombre de la partida
    private TextView namePartida_textView;
    // Boton para pausar
    private Button pausar;
    // Botones para cantar o no
    private Button cantar, noCantar;
    // Botones para cambiar la carta por el triunfo o no
    private Button cambiar, noCambiar;


    private Long idPartida, idPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_juego);

        Intent intent = getIntent();
        idPartida = intent.getLongExtra("idPartida",-1);
        idPlayer = intent.getLongExtra("idPlayer",-1);

        carta1 = findViewById(R.id.carta1);
        carta2 = findViewById(R.id.carta2);
        carta3 = findViewById(R.id.carta3);
        carta4 = findViewById(R.id.carta4);
        carta5 = findViewById(R.id.carta5);
        carta6 = findViewById(R.id.carta6);

        triunfo = findViewById(R.id.carta_triunfo);
        monton_robar = findViewById(R.id.montonRobar);
        monton1 = findViewById(R.id.monton1);
        monton2 = findViewById(R.id.monton2);
        monton3 = findViewById(R.id.monton3);

        namePartida_textView = findViewById(R.id.idPartida);

        pausar = findViewById(R.id.botonPausa);
        cantar = findViewById(R.id.botonCantar);
        noCantar = findViewById(R.id.botonNoCantar);
        cambiar = findViewById(R.id.botonCambiar);
        noCambiar = findViewById(R.id.botonNoCambiar);

        // Crea una conexión y la asocia al websocket de la clase
        try {
            ws =  new WebSocketFactory()
                    .setConnectionTimeout(TIMEOUT)
                    .createSocket(SECURE_DIR)
                    .addListener(new WebSocketAdapter() {
                        // A text message arrived from the server.
                        public void onTextMessage(WebSocket websocket, String message) {
                            actualizaTablero(message);
                        }
                    })
                    .addExtension(WebSocketExtension.PERMESSAGE_DEFLATE)
                    .connect();
        } catch (WebSocketException | IOException e) {
            e.printStackTrace();
        }

        // Funcionalidad boton pausar
        pausar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO Solicitar pausa de partida
                Snackbar.make(view, "Acción no disponible por el momento", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // Funcionalidad boton cantar
        // Responde al Backend con el evento cantar
        cantar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO Enviar evento cantar
                // Enviar evento cantar
                //ws.sendText();
            }
        });

        // Funcionalidad boton noCantar
        noCantar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO Enviar evento no cantar
                // Enviar evento no cantar
                //ws.sendText();
            }
        });

        // Funcionalidad boton cambiar
        cambiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO Enviar evento cambiar
                // Enviar evento cantar
                //ws.sendText();
            }
        });

        // Funcionalidad boton noCambiar
        noCambiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO Enviar evento no cambiar
                // Enviar evento no cantar
                //ws.sendText();
            }
        });

        namePartida_textView.setText(idPartida.toString());
    }

    // Actualiza el tablero a partir del json recibido
    private void actualizaTablero(String json) {
        EstadoPartida est = new EstadoPartida(json, idPlayer,this);
        // Si es arrastre no hay monton de robar
        if (est.isArrastre()) monton_robar.setVisibility(View.INVISIBLE);
        else monton_robar.setVisibility(View.VISIBLE);

        // Actualiza la mano del jugador
        if (est.getCarta(1) != null) {
            carta1.setBackground(ContextCompat.getDrawable(JuegoActivity.this, est.getCarta(1)));
            carta1.setVisibility(View.VISIBLE);
            carta1.setClickable(est.isTirarCarta() && est.isPuedeCarta(1));
        } else carta1.setVisibility(View.INVISIBLE);

        if (est.getCarta(2) != null) {
            carta2.setBackground(ContextCompat.getDrawable(JuegoActivity.this, est.getCarta(2)));
            carta2.setVisibility(View.VISIBLE);
            carta2.setClickable(est.isTirarCarta() && est.isPuedeCarta(2));
        } else carta2.setVisibility(View.INVISIBLE);

        if (est.getCarta(3) != null) {
            carta3.setBackground(ContextCompat.getDrawable(JuegoActivity.this, est.getCarta(3)));
            carta3.setVisibility(View.VISIBLE);
            carta3.setClickable(est.isTirarCarta() && est.isPuedeCarta(3));
        } else carta3.setVisibility(View.INVISIBLE);

        if (est.getCarta(4) != null) {
            carta4.setBackground(ContextCompat.getDrawable(JuegoActivity.this, est.getCarta(4)));
            carta4.setVisibility(View.VISIBLE);
            carta4.setClickable(est.isTirarCarta() && est.isPuedeCarta(4));
        } else carta4.setVisibility(View.INVISIBLE);

        if (est.getCarta(5) != null) {
            carta5.setBackground(ContextCompat.getDrawable(JuegoActivity.this, est.getCarta(5)));
            carta5.setVisibility(View.VISIBLE);
            carta5.setClickable(est.isTirarCarta() && est.isPuedeCarta(5));
        } else carta5.setVisibility(View.INVISIBLE);

        if (est.getCarta(6) != null) {
            carta6.setBackground(ContextCompat.getDrawable(JuegoActivity.this, est.getCarta(6)));
            carta6.setVisibility(View.VISIBLE);
            carta6.setClickable(est.isTirarCarta() && est.isPuedeCarta(6));
        } else carta6.setVisibility(View.INVISIBLE);

        // Si es posible cantar, muestra y activa los botones de cantar y no cantar
        if (est.isCantar()) {
            cantar.setVisibility(View.VISIBLE);
            cantar.setClickable(true);
            noCantar.setVisibility(View.VISIBLE);
            noCantar.setClickable(true);
        } else {
            cantar.setVisibility(View.INVISIBLE);
            cantar.setClickable(false);
            noCantar.setVisibility(View.INVISIBLE);
            noCantar.setClickable(false);
        }

        // Si es posible cambiar, muestra los botones de cambiar y no cambiar
        if (est.isCambiar()) {
            cambiar.setVisibility(View.VISIBLE);
            cambiar.setClickable(true);
            noCambiar.setVisibility(View.VISIBLE);
            noCambiar.setClickable(true);
        } else {
            cambiar.setVisibility(View.INVISIBLE);
            cambiar.setClickable(false);
            noCambiar.setVisibility(View.INVISIBLE);
            noCambiar.setClickable(false);
        }
    }
}