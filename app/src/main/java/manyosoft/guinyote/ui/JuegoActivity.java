 package manyosoft.guinyote.ui;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketExtension;
import com.neovisionaries.ws.client.WebSocketFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import manyosoft.guinyote.R;
import manyosoft.guinyote.util.EstadoPartida;
import manyosoft.guinyote.util.Usuario;

public class JuegoActivity extends AppCompatActivity {
    /**
     * Servidor remoto del backend
     */
    private static final String SERVER      = "10.0.2.2:9000/simulation";
    private static final String REMOTESERVER      = "15.188.14.213:11050/simulation";

    private static final String SECURE_DIR  = "wss://" + SERVER;
    private static final String DIR         = "ws://" + REMOTESERVER;

    // Strings predefinidos para el paso de eventos
    private static final String game_id = "game_id";
    private static final String player_id = "player_id";
    private static final String pair_id = "pair_id";
    private static final String event_type = "event_type";

    // Eventos con los que se trabaja
    private static final Integer EVENTO_CREATE_JOIN_SOLO    = 8;
    private static final Integer EVENTO_CREATE_JOIN         = 0;
    private static final Integer EVENTO_JOIN                = 1;
    private static final Integer EVENTO_LEAVE               = 2;
    private static final Integer EVENTO_JUGAR_CARTA         = 3;
    private static final Integer EVENTO_CANTAR              = 5;
    private static final Integer EVENTO_NOCANTAR            = 5;
    private static final Integer EVENTO_CAMBIAR             = 4;
    private static final Integer EVENTO_NOCAMBIAR           = 4;
    private static final Integer EVENTO_PAUSAR              = 6;
    private static final Integer EVENTO_VOTAR_PAUSAR        = 7;

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
    // Cartas del tableromonton
    private ImageView triunfo, monton_robar, monton1, monton2,monton3,monton4,tablero;
    // Texto que muestra el nombre de la partida
    private TextView namePartida_textView;
    // Boton para pausar
    private Button pausar;
    // Botones para cantar o no
    private Button cantar, noCantar;
    private String singsuit;
    // Botones para cambiar la carta por el triunfo o no
    private Button cambiar, noCambiar;
    // Mensaje de victoria
    private TextView mensajeFin, parejaGanadora, mensajePausa;
    // Boton de volver (final partida)
    private Button botonVolver;

    private Button votarPausa, votarNoPausa;

    private ImageButton infoPartida;

    private String jsonReceived;

    private Long idPartida, idPlayer, idPair;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_juego);

        Intent intent = getIntent();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        idPartida = intent.getLongExtra("idPartida",-1);
        idPlayer = intent.getLongExtra("idPlayer",-1);

        idPair = intent.getLongExtra("idPair", -1);
        // TODO DEBUG ONLY
        //idPair = 1L;
        //idPlayer = 3L;


        carta1 = (ImageButton) findViewById(R.id.carta1);
        carta2 = (ImageButton) findViewById(R.id.carta2);
        carta3 = (ImageButton) findViewById(R.id.carta3);
        carta4 = (ImageButton) findViewById(R.id.carta4);
        carta5 = (ImageButton) findViewById(R.id.carta5);
        carta6 = (ImageButton) findViewById(R.id.carta6);

        Usuario user = Usuario.getInstance();
        tablero = findViewById(R.id.tablero);
        tablero.setBackground(ContextCompat.getDrawable(JuegoActivity.this,user.getColorTapete()));
        triunfo = findViewById(R.id.carta_triunfo);
        monton_robar = findViewById(R.id.montonRobar);
        monton_robar.setBackground(ContextCompat.getDrawable(JuegoActivity.this,user.getColorCarta()));
        monton1 = findViewById(R.id.monton1);
        monton2 = findViewById(R.id.monton2);
        monton3 = findViewById(R.id.monton3);
        monton4 = findViewById(R.id.monton4);

        namePartida_textView = findViewById(R.id.idPartida);

        pausar = findViewById(R.id.botonPausa);
        cantar = findViewById(R.id.botonCantar);
        noCantar = findViewById(R.id.botonNoCantar);
        singsuit = "";
        cambiar = findViewById(R.id.botonCambiar);
        noCambiar = findViewById(R.id.botonNoCambiar);

        mensajeFin = findViewById(R.id.textoFin);
        mensajePausa = findViewById(R.id.textoPausado);
        parejaGanadora = findViewById(R.id.textoGanador);
        botonVolver = findViewById(R.id.botonVolver);

        votarPausa = findViewById(R.id.botonVotarPausar);
        votarNoPausa = findViewById(R.id.botonVotarNoPausar);

        infoPartida = findViewById(R.id.infoPartida);
        infoPartida.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               AlertDialog .Builder builder = new AlertDialog.Builder(JuegoActivity.this);
               builder.setTitle("AYUDA");
               builder.setView(R.layout.fragment_dialogo);
               builder.setNegativeButton("CERRAR", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       dialog.dismiss();
                   }
               });
               builder.show();
           }
       });

        Handler handler = new Handler()
        {
            @Override
            public void handleMessage(Message msg)
            {
                actualizaTablero();
            }
        };

        if(intent.getBooleanExtra("create", false))  {
            generateCreateJoinEvent();
        } else {
            generateJoinEvent();
        }

        // Crea una conexión y la asocia al websocket de la clase
        try {
            ws =  new WebSocketFactory()
                    .setConnectionTimeout(TIMEOUT)
                    .createSocket(DIR)
                    .addListener(new WebSocketAdapter() {
                        // A text message arrived from the server.
                        @Override
                        public void onTextMessage(WebSocket websocket, String message) {
                            Log.d("Mensaje recibido ws", message);
                            jsonReceived = message;
                            handler.sendMessage(handler.obtainMessage());
                        }

                        // Cuando se conecta con el servidor
                        @Override
                        public void onConnected(WebSocket websocket, Map<String, List<String>> headers)   {
                            // Manda mi player id y pair id
                            Log.d("Primer envio", sendMyself());
                            websocket.sendText(sendMyself());
                            // Manda evento de creacion o union
                            if(intent.getBooleanExtra("solo", false)){
                                Log.d("segundo envio", "solo-game");
                                websocket.sendText(generateCreateJoinSoloEvent());
                            } else if(intent.getBooleanExtra("create", false))  {
                                Log.d("segundo envio", "create-game");
                                websocket.sendText(generateCreateJoinEvent());
                            } else {
                                Log.d("segundo envio", "join-game");
                                websocket.sendText(generateJoinEvent());
                            }
                        }

                    })
                    .addExtension(WebSocketExtension.PERMESSAGE_DEFLATE)
                    .connectAsynchronously();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Funcionalidad boton pausar
        pausar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
/*                // TODO Solicitar pausa de partida
                Snackbar.make(view, "Acción no disponible por el momento", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
*/
                // Enviar evento pausa
                if(ws.isOpen()) ws.sendText(generatePauseEvent());
                else    {
                    Snackbar.make(view, "Hubo un error en la conexión, reintente", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    reconectar();
                }
            }
        });

        if(intent.getBooleanExtra("solo", false)) pausar.setVisibility(View.INVISIBLE);

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
                Log.d("Envio carta 1", "Mandara: "+generatePlayCardEvent(carta1suit, carta1value));
                if(ws.isOpen()) ws.sendText(generatePlayCardEvent(carta1suit, carta1value));
                else    {
                    Log.d("Envio carta 1", "Se va a reconectar");
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



        // Elementos de fin de pantalla
        mensajeFin.setVisibility(View.INVISIBLE);
        parejaGanadora.setVisibility(View.INVISIBLE);
        mensajePausa.setVisibility(View.INVISIBLE);

        botonVolver.setVisibility(View.INVISIBLE);
        botonVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ws.isOpen()) {
                    Log.d("Fin partida","El usuario ha pulsado boton volver a menu principal");
                    ws.sendText(generateLeaveEvent());
                    ws.disconnect();
                }
                finish();
            }
        });

        votarPausa.setOnClickListener(view -> ws.sendText(generateVotePauseEvent(true)));

        votarNoPausa.setOnClickListener(view -> ws.sendText(generateVotePauseEvent(false)));
    }

    // Actualiza el tablero a partir del json recibido
    private void actualizaTablero() {
        EstadoPartida est = new EstadoPartida(jsonReceived, idPlayer,this);
        // Si es arrastre no hay monton de robar
        if (est.isArrastre()) monton_robar.setVisibility(View.INVISIBLE);
        else monton_robar.setVisibility(View.VISIBLE);

        // Log de la mano del jugador
        for(int i = 1; i <= 6; i++) {
            if(est.getCarta(i) != null) Log.d("carta "+i, est.getCarta(i)+ ", "+est.getCartaVal(i) + " de "+ est.getCartaSuit(i));
            else Log.d("carta "+i, "null");
        }

        // Actualiza la mano del jugador
        if (est.getCarta(1) != null) {
            carta1.setBackground(ContextCompat.getDrawable(JuegoActivity.this, est.getCarta(1)));
            carta1.setVisibility(View.VISIBLE);
            if(est.isTirarCarta() && est.isPuedeCarta(1)) {
                Log.d("carta1", "Puede Lanzar");
                carta1.setClickable(true);
                carta1.setAlpha(1.0f);
            } else {
                carta1.setClickable(false);
                carta1.setAlpha(0.25f);
            }
            carta1suit = est.getCartaSuit(1);
            carta1value = est.getCartaVal(1);
        } else {
            carta1.setVisibility(View.INVISIBLE);
            carta1.setClickable(false);
        }

        if (est.getCarta(2) != null) {
            carta2.setBackground(ContextCompat.getDrawable(JuegoActivity.this, est.getCarta(2)));
            carta2.setVisibility(View.VISIBLE);
            if(est.isTirarCarta() && est.isPuedeCarta(2)) {
                Log.d("carta2", "Puede Lanzar");
                carta2.setClickable(true);
                carta2.setAlpha(1.0f);
            } else {
                carta2.setClickable(false);
                carta2.setAlpha(0.25f);
            }
            carta2suit = est.getCartaSuit(2);
            carta2value = est.getCartaVal(2);
        } else {
            carta2.setVisibility(View.INVISIBLE);
            carta2.setClickable(false);
        }

        if (est.getCarta(3) != null) {
            carta3.setBackground(ContextCompat.getDrawable(JuegoActivity.this, est.getCarta(3)));
            carta3.setVisibility(View.VISIBLE);
            if(est.isTirarCarta() && est.isPuedeCarta(3)) {
                Log.d("carta3", "Puede Lanzar");
                carta3.setClickable(true);
                carta3.setAlpha(1.0f);
            } else {
                carta3.setClickable(false);
                carta3.setAlpha(0.25f);
            }
            carta3suit = est.getCartaSuit(3);
            carta3value = est.getCartaVal(3);
        } else {
            carta3.setVisibility(View.INVISIBLE);
            carta3.setClickable(false);
        }

        if (est.getCarta(4) != null) {
            carta4.setBackground(ContextCompat.getDrawable(JuegoActivity.this, est.getCarta(4)));
            carta4.setVisibility(View.VISIBLE);
            if(est.isTirarCarta() && est.isPuedeCarta(4)) {
                Log.d("carta4", "Puede Lanzar");
                carta4.setClickable(true);
                carta4.setAlpha(1.0f);
            } else {
                carta4.setClickable(false);
                carta4.setAlpha(0.25f);
            }
            carta4suit = est.getCartaSuit(4);
            carta4value = est.getCartaVal(4);
        } else {
            carta4.setVisibility(View.INVISIBLE);
            carta4.setClickable(false);
        }

        if (est.getCarta(5) != null) {
            carta5.setBackground(ContextCompat.getDrawable(JuegoActivity.this, est.getCarta(5)));
            carta5.setVisibility(View.VISIBLE);
            if(est.isTirarCarta() && est.isPuedeCarta(5)) {
                Log.d("carta5", "Puede Lanzar");
                carta5.setClickable(true);
                carta5.setAlpha(1.0f);
            } else {
                carta5.setClickable(false);
                carta5.setAlpha(0.25f);
            }
            carta5suit = est.getCartaSuit(5);
            carta5value = est.getCartaVal(5);
        } else {
            carta5.setVisibility(View.INVISIBLE);
            carta5.setClickable(false);
        }

        if (est.getCarta(6) != null) {
            carta6.setBackground(ContextCompat.getDrawable(JuegoActivity.this, est.getCarta(6)));
            carta6.setVisibility(View.VISIBLE);
            if(est.isTirarCarta() && est.isPuedeCarta(6)) {
                Log.d("carta6", "Puede Lanzar");
                carta6.setClickable(true);
                carta6.setAlpha(1.0f);
            } else {
                carta6.setClickable(false);
                carta6.setAlpha(0.25f);
            }
            carta6suit = est.getCartaSuit(6);
            carta6value = est.getCartaVal(6);
        } else {
            carta6.setVisibility(View.INVISIBLE);
            carta6.setClickable(false);
        }

        try {
            if(est.getCards_played_round() != null && est.getCards_played_round().get(0) != null) {
                monton1.setBackgroundResource(est.getCards_played_round().get(0));
                monton1.setVisibility(View.VISIBLE);
            }
            else monton1.setVisibility(View.INVISIBLE);
        } catch (Exception e) {
            monton1.setVisibility(View.INVISIBLE);
            e.printStackTrace();
        }

        try {
            if(est.getCards_played_round() != null && est.getCards_played_round().get(1) != null) {
                monton2.setBackgroundResource(est.getCards_played_round().get(1));
                monton2.setVisibility(View.VISIBLE);
            }
            else monton2.setVisibility(View.INVISIBLE);
        } catch (Exception e) {
            monton2.setVisibility(View.INVISIBLE);
            e.printStackTrace();
        }

        try {
            if(est.getCards_played_round() != null && est.getCards_played_round().get(2) != null) {
                monton3.setBackgroundResource(est.getCards_played_round().get(2));
                monton3.setVisibility(View.VISIBLE);
            }
            else monton3.setVisibility(View.INVISIBLE);
        } catch (Exception e) {
            monton3.setVisibility(View.INVISIBLE);
            e.printStackTrace();
        }
        try {
            if(est.getCards_played_round() != null && est.getCards_played_round().get(3) != null) {
                monton4.setBackgroundResource(est.getCards_played_round().get(3));
                monton4.setVisibility(View.VISIBLE);
            }
            else monton4.setVisibility(View.INVISIBLE);
        } catch (Exception e) {
            monton4.setVisibility(View.INVISIBLE);
            e.printStackTrace();
        }

        // Si es posible cantar, muestra y activa los botones de cantar y no cantar
        if (est.isCantar()) {
            Log.d("Evento","Se esta cantando!");
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
            Log.d("Evento","Se esta cambiando!");
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

        // Actualiza el triunfo
        if(est.getTriumph_suit() != null && est.getTriumph_val() != null)    {
            triunfo.setBackgroundResource(
                    getResources().getIdentifier(
                            est.getTriumph_suit()+"_"+est.getTriumph_val(),
                            "drawable", getPackageName()
                    )
            );
        }

        if(est.getStatus().equals("votePause"))  {
            votarPausa.setVisibility(View.VISIBLE);
            votarNoPausa.setVisibility(View.VISIBLE);
        } else {
            votarPausa.setVisibility(View.INVISIBLE);
            votarNoPausa.setVisibility(View.INVISIBLE);
        }

        if(est.getStatus().equals("paused"))    {
            mensajePausa.setVisibility(View.VISIBLE);
            botonVolver.setVisibility(View.VISIBLE);
        }

        // Si la partida ha finalizado lo muestra al jugador
        if(est.isEnded())   {
            mensajeFin.setVisibility(View.VISIBLE);
            boolean heGanado = est.getWinner_pair().equals(idPair);


            Long points_team_a;
            if (est != null && est.getPoints_team_a() != null && est.getPoints_sing_a() != null)
                points_team_a = est.getPoints_team_a() + est.getPoints_sing_a();
            else points_team_a = -1L;
            Long points_team_b;
            if(est != null && est.getPoints_team_b() != null && est.getPoints_sing_b() != null)
                points_team_b = est.getPoints_team_b() + est.getPoints_sing_b();
            else
                points_team_b = -1L;


            if(heGanado)    parejaGanadora.setText("¡Has ganado! " + points_team_a.toString() + " - " + points_team_b.toString());
            else            parejaGanadora.setText("La próxima será " + points_team_a.toString() + " - " + points_team_b.toString());
            parejaGanadora.setVisibility(View.VISIBLE);

            botonVolver.setVisibility(View.VISIBLE);
        }
    }

    private String sendMyself() {
        JsonObject json = new JsonObject();
        json.addProperty("player_id", idPlayer);
        json.addProperty(pair_id, idPair);
        return json.toString();
    }

    // Genera un evento de creación de partida
    private String generateCreateJoinEvent() {
        JsonObject json = new JsonObject();
        json.addProperty(game_id, idPartida);
        json.addProperty(player_id, idPlayer);
        json.addProperty(pair_id, idPair);
        json.addProperty(event_type, EVENTO_CREATE_JOIN);
        return json.toString();
    }

    // Genera un evento de creación de partida
    private String generateCreateJoinSoloEvent() {
        JsonObject json = new JsonObject();
        json.addProperty(game_id, idPartida);
        json.addProperty(player_id, idPlayer);
        json.addProperty(pair_id, idPair);
        json.addProperty(event_type, EVENTO_CREATE_JOIN_SOLO);
        return json.toString();
    }

    // Genera un evento de unión a una partida ya existente
    private String generateJoinEvent() {
        JsonObject json = new JsonObject();
        json.addProperty(game_id, idPartida);
        json.addProperty(player_id, idPlayer);
        json.addProperty(pair_id, idPair);
        json.addProperty(event_type, EVENTO_JOIN);
        return json.toString();
    }

    // Genera un evento de abandono de partida
    private String generateLeaveEvent() {
        JsonObject json = new JsonObject();
        json.addProperty(game_id, idPartida);
        json.addProperty(player_id, idPlayer);
        json.addProperty(event_type, EVENTO_LEAVE);
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
        json.addProperty(event_type, EVENTO_JUGAR_CARTA);
        return json.toString();
    }

    // Genera un evento de cantar o no cantar
    private String generateSingEvent(boolean canta, String suit) {
        JsonObject json = new JsonObject();
        json.addProperty(game_id, idPartida);
        json.addProperty(player_id, idPlayer);
        json.addProperty("suit", suit);
        json.addProperty("has_singed", canta);
        json.addProperty(event_type, EVENTO_CANTAR);
        return json.toString();
    }

    // Genera un evento de cambiar o no cambiar
    private String generateChangeEvent(boolean cambia) {
        JsonObject json = new JsonObject();
        json.addProperty(game_id, idPartida);
        json.addProperty(player_id, idPlayer);
        json.addProperty("changed", cambia);
        json.addProperty(event_type, EVENTO_CAMBIAR);
        return json.toString();
    }

    // Genera un evento de pausa
    private String generatePauseEvent() {
        JsonObject json = new JsonObject();
        json.addProperty(game_id, idPartida);
        json.addProperty(player_id, idPlayer);
        json.addProperty(event_type, EVENTO_PAUSAR);
        return json.toString();
    }


    // Genera un evento de votacion de pausa
    private String generateVotePauseEvent(boolean quiere) {
        JsonObject json = new JsonObject();
        json.addProperty(game_id, idPartida);
        json.addProperty(player_id, idPlayer);
        json.addProperty("vote", quiere);
        json.addProperty(event_type, EVENTO_VOTAR_PAUSAR);
        return json.toString();
    }

    private void reconectar()    {
        try {
            ws = ws.recreate().connectAsynchronously();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed()
    {
        if(ws.isOpen()) {
            ws.sendText(generateLeaveEvent());
            ws.disconnect();
        }
        finish();
        super.onBackPressed();  // optional
    }
}