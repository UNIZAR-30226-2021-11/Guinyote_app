 package manyosoft.guinyote.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketExtension;
import com.neovisionaries.ws.client.WebSocketFactory;

import java.io.IOException;
import java.nio.channels.AsynchronousByteChannel;
import java.util.ArrayList;
import java.util.EmptyStackException;

import manyosoft.guinyote.R;
import manyosoft.guinyote.util.EstadoPartida;
import manyosoft.guinyote.util.Usuario;

public class JuegoActivity extends AppCompatActivity {
    /**
     * Servidor remoto del backend
     */
    private static final String SERVER      = "10.0.2.2:9000/simulation";
    private static final String SECURE_DIR  = "wss://" + SERVER;
    private static final String DIR         = "ws://" + SERVER;

    // Strings predefinidos para el paso de eventos
    private static final String game_id = "game_id";
    private static final String player_id = "player_id";
    private static final String pair_id = "pair_id";
    private static final String event_type = "event_type";

    // Eventos con los que se trabaja
    private static final Long EVENTO_CREATE_JOIN    = 0L;
    private static final Long EVENTO_JOIN           = 1L;
    private static final Long EVENTO_LEAVE          = 2L;
    private static final Long EVENTO_JUGAR_CARTA    = 3L;
    private static final Long EVENTO_CANTAR         = 5L;
    private static final Long EVENTO_NOCANTAR       = 5L;
    private static final Long EVENTO_CAMBIAR        = 4L;
    private static final Long EVENTO_NOCAMBIAR      = 4L;
    private static final Long EVENTO_PAUSAR         = 6L;

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
    private String carta1suit, carta2suit, carta3suit, carta4suit, carta5suit, carta6suit;
    private Integer carta1value, carta2value, carta3value, carta4value, carta5value, carta6value;
    // Cartas del tablero
    private ImageView triunfo, monton_robar, monton1, monton2, monton3;
    // Texto que muestra el nombre de la partida
    private TextView namePartida_textView;
    // Boton para pausar
    private Button pausar;
    // Botones para cantar o no
    private Button cantar, noCantar;
    private String singsuit;
    // Botones para cambiar la carta por el triunfo o no
    private Button cambiar, noCambiar;

    private String jsonReceived;

    private Long idPartida, idPlayer, idPair;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_juego);

        Intent intent = getIntent();
        idPartida = intent.getLongExtra("idPartida",-1);
        idPlayer = intent.getLongExtra("idPlayer",-1);
        idPair = intent.getLongExtra("idPair", -1);

        carta1 = findViewById(R.id.carta1);
        carta2 = findViewById(R.id.carta2);
        carta3 = findViewById(R.id.carta3);
        carta4 = findViewById(R.id.carta4);
        carta5 = findViewById(R.id.carta5);
        carta6 = findViewById(R.id.carta6);

        Usuario user = Usuario.getInstance();
        triunfo = findViewById(R.id.carta_triunfo);
        monton_robar = findViewById(R.id.montonRobar);
        monton_robar.setBackground(ContextCompat.getDrawable(JuegoActivity.this,user.getColorCarta()));
        monton1 = findViewById(R.id.monton1);
        monton2 = findViewById(R.id.monton2);
        monton3 = findViewById(R.id.monton3);

        namePartida_textView = findViewById(R.id.idPartida);

        pausar = findViewById(R.id.botonPausa);
        cantar = findViewById(R.id.botonCantar);
        noCantar = findViewById(R.id.botonNoCantar);
        singsuit = "";
        cambiar = findViewById(R.id.botonCambiar);
        noCambiar = findViewById(R.id.botonNoCambiar);

        Handler handler = new Handler()
        {
            @Override
            public void handleMessage(Message msg)
            {
                actualizaTablero();
            }
        };

        // Crea una conexión y la asocia al websocket de la clase
        try {
            ws =  new WebSocketFactory()
                    .setConnectionTimeout(TIMEOUT)
                    .createSocket(DIR)
                    .addListener(new WebSocketAdapter() {
                        // A text message arrived from the server.
                        public void onTextMessage(WebSocket websocket, String message) {
                            Log.d("Mensaje recibido ws", "a"+message);
                            jsonReceived = message;
                            handler.sendMessage(handler.obtainMessage());
                        }
                    })
                    .addExtension(WebSocketExtension.PERMESSAGE_DEFLATE)
                    .connectAsynchronously();

            ws.sendText("Hola!");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Funcionalidad boton pausar
        pausar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO Solicitar pausa de partida
                Snackbar.make(view, "Acción no disponible por el momento", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
/*
                // Enviar evento pausa
                if(ws.isOpen()) ws.sendText(generatePauseEvent());
                else    {
                    Snackbar.make(view, "Hubo un error en la conexión, reintente", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    reconectar();
                }
*/            }
        });

        // Funcionalidad boton cantar
        // Responde al Backend con el evento cantar
        cantar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Enviar evento cantar
                if(ws.isOpen()) ws.sendText(generateSingEvent(true, singsuit));
                else    {
                    Snackbar.make(view, "Hubo un error en la conexión, reintente la acción", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    reconectar();
                }
            }
        });

        // Funcionalidad boton noCantar
        noCantar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Enviar evento no cantar
                if(ws.isOpen()) ws.sendText(generateSingEvent(false, singsuit));
                else    {
                    Snackbar.make(view, "Hubo un error en la conexión, reintente la acción", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    reconectar();
                }
            }
        });

        // Funcionalidad boton cambiar
        cambiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Enviar evento cambiar
                if(ws.isOpen()) ws.sendText(generateChangeEvent(true));
                else    {
                    Snackbar.make(view, "Hubo un error en la conexión, reintente", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    reconectar();
                }
            }
        });

        // Funcionalidad boton noCambiar
        noCambiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Enviar evento no cambiar
                if(ws.isOpen()) ws.sendText(generateChangeEvent(false));
                else    {
                    Snackbar.make(view, "Hubo un error en la conexión, reintente", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    reconectar();
                }
            }
        });

        // Cartas
        carta1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Enviar evento no cambiar
                if(ws.isOpen()) ws.sendText(generatePlayCardEvent(carta1suit, carta1value));
                else    {
                    Snackbar.make(view, "Hubo un error en la conexión, reintente", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    reconectar();
                }
            }
        });


        carta2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Enviar evento no cambiar
                if(ws.isOpen()) ws.sendText(generatePlayCardEvent(carta2suit, carta2value));
                else    {
                    Snackbar.make(view, "Hubo un error en la conexión, reintente", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    reconectar();
                }
            }
        });


        carta3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Enviar evento no cambiar
                if(ws.isOpen()) ws.sendText(generatePlayCardEvent(carta3suit, carta3value));
                else    {
                    Snackbar.make(view, "Hubo un error en la conexión, reintente", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    reconectar();
                }
            }
        });


        carta4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Enviar evento no cambiar
                if(ws.isOpen()) ws.sendText(generatePlayCardEvent(carta4suit, carta4value));
                else    {
                    Snackbar.make(view, "Hubo un error en la conexión, reintente", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    reconectar();
                }
            }
        });


        carta5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Enviar evento no cambiar
                if(ws.isOpen()) ws.sendText(generatePlayCardEvent(carta5suit, carta5value));
                else    {
                    Snackbar.make(view, "Hubo un error en la conexión, reintente", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    reconectar();
                }
            }
        });

        carta6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Enviar evento no cambiar
                if(ws.isOpen()) ws.sendText(generatePlayCardEvent(carta6suit, carta6value));
                else    {
                    Snackbar.make(view, "Hubo un error en la conexión, reintente", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    reconectar();
                }
            }
        });

        carta1.setVisibility(View.INVISIBLE);
        carta2.setVisibility(View.INVISIBLE);
        carta3.setVisibility(View.INVISIBLE);
        carta4.setVisibility(View.INVISIBLE);
        carta5.setVisibility(View.INVISIBLE);
        carta6.setVisibility(View.INVISIBLE);

        namePartida_textView.setText(idPartida.toString());

        if(intent.getBooleanExtra("create", false))  {
            generateCreateJoinEvent();
        } else {
            generateJoinEvent();
        }
    }

    // Actualiza el tablero a partir del json recibido
    private void actualizaTablero() {
        EstadoPartida est = new EstadoPartida(jsonReceived, idPlayer, this);
        // Si es arrastre no hay monton de robar
        if (est.isArrastre()) monton_robar.setVisibility(View.INVISIBLE);
        else monton_robar.setVisibility(View.VISIBLE);

        // Actualiza la mano del jugador
        if (est.getCarta(1) != null) {
            carta1.setBackground(ContextCompat.getDrawable(JuegoActivity.this, est.getCarta(1)));
            carta1.setVisibility(View.VISIBLE);
            carta1.setClickable(est.isTirarCarta() && est.isPuedeCarta(1));
            carta1suit = est.getCartaSuit(1);
            carta1value = est.getCartaVal(1);
        } else carta1.setVisibility(View.INVISIBLE);

        if (est.getCarta(2) != null) {
            carta2.setBackground(ContextCompat.getDrawable(JuegoActivity.this, est.getCarta(2)));
            carta2.setVisibility(View.VISIBLE);
            carta2.setClickable(est.isTirarCarta() && est.isPuedeCarta(2));
            carta2suit = est.getCartaSuit(2);
            carta2value = est.getCartaVal(2);
        } else carta2.setVisibility(View.INVISIBLE);

        if (est.getCarta(3) != null) {
            carta3.setBackground(ContextCompat.getDrawable(JuegoActivity.this, est.getCarta(3)));
            carta3.setVisibility(View.VISIBLE);
            carta3.setClickable(est.isTirarCarta() && est.isPuedeCarta(3));
            carta3suit = est.getCartaSuit(3);
            carta3value = est.getCartaVal(3);
        } else carta3.setVisibility(View.INVISIBLE);

        if (est.getCarta(4) != null) {
            carta4.setBackground(ContextCompat.getDrawable(JuegoActivity.this, est.getCarta(4)));
            carta4.setVisibility(View.VISIBLE);
            carta4.setClickable(est.isTirarCarta() && est.isPuedeCarta(4));
            carta4suit = est.getCartaSuit(4);
            carta4value = est.getCartaVal(4);
        } else carta4.setVisibility(View.INVISIBLE);

        if (est.getCarta(5) != null) {
            carta5.setBackground(ContextCompat.getDrawable(JuegoActivity.this, est.getCarta(5)));
            carta5.setVisibility(View.VISIBLE);
            carta5.setClickable(est.isTirarCarta() && est.isPuedeCarta(5));
            carta5suit = est.getCartaSuit(5);
            carta5value = est.getCartaVal(5);
        } else carta5.setVisibility(View.INVISIBLE);

        if (est.getCarta(6) != null) {
            carta6.setBackground(ContextCompat.getDrawable(JuegoActivity.this, est.getCarta(6)));
            carta6.setVisibility(View.VISIBLE);
            carta6.setClickable(est.isTirarCarta() && est.isPuedeCarta(6));
            carta6suit = est.getCartaSuit(6);
            carta6value = est.getCartaVal(6);
        } else carta6.setVisibility(View.INVISIBLE);

        // Si es posible cantar, muestra y activa los botones de cantar y no cantar
        if (est.isCantar()) {
            singsuit = est.getSingsuit();
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

    // Genera un evento de creación de partida
    private String generateCreateJoinEvent() {
        JsonObject json = new JsonObject();
        json.addProperty(game_id, idPartida);
        json.addProperty(player_id, idPlayer);
        json.addProperty(pair_id, idPair);
        json.addProperty(event_type, EVENTO_CREATE_JOIN.toString());
        return json.toString();
    }

    // Genera un evento de unión a una partida ya existente
    private String generateJoinEvent() {
        JsonObject json = new JsonObject();
        json.addProperty(game_id, idPartida);
        json.addProperty(player_id, idPlayer);
        json.addProperty(pair_id, idPair);
        json.addProperty(event_type, EVENTO_JOIN.toString());
        return json.toString();
    }

    // Genera un evento de abandono de partida
    private String generateLeaveEvent() {
        JsonObject json = new JsonObject();
        json.addProperty(game_id, idPartida);
        json.addProperty(player_id, idPlayer);
        json.addProperty(event_type, EVENTO_LEAVE.toString());
        return json.toString();
    }

    // Genera un evento de lanzamiento de carta
    private String generatePlayCardEvent(String suit, Integer value) {
        // Carta
        JsonObject card = new JsonObject();
        card.addProperty("suit", suit);
        card.addProperty("val", value);

        JsonObject json = new JsonObject();
        json.addProperty(game_id, idPartida);
        json.addProperty(player_id, idPlayer);
        json.add("card", card);
        json.addProperty(event_type, EVENTO_LEAVE.toString());
        return json.toString();
    }

    // Genera un evento de cantar o no cantar
    private String generateSingEvent(boolean canta, String suit) {
        JsonObject json = new JsonObject();
        json.addProperty(game_id, idPartida);
        json.addProperty(player_id, idPlayer);
        json.addProperty("suit", suit);
        json.addProperty("has_singed", canta);
        json.addProperty(event_type, EVENTO_CANTAR.toString());
        return json.toString();
    }

    // Genera un evento de cambiar o no cambiar
    private String generateChangeEvent(boolean cambia) {
        JsonObject json = new JsonObject();
        json.addProperty(game_id, idPartida);
        json.addProperty(player_id, idPlayer);
        json.addProperty("changed", cambia);
        json.addProperty(event_type, EVENTO_CAMBIAR.toString());
        return json.toString();
    }

    // Genera un evento de pausa
    private String generatePauseEvent(boolean cambia) {
        JsonObject json = new JsonObject();
        json.addProperty(game_id, idPartida);
        json.addProperty(player_id, idPlayer);
        json.addProperty(event_type, EVENTO_PAUSAR.toString());
        return json.toString();
    }

    private void reconectar()    {
        try {
            ws = ws.recreate();
            ws.connect();
        } catch (IOException | WebSocketException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed()
    {
        if(ws.isOpen()) {
            ws.sendText(generateLeaveEvent());
            ws.sendClose();
        }
        finish();
        super.onBackPressed();  // optional
    }
}