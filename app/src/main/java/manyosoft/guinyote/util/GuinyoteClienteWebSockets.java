package manyosoft.guinyote.util;

import java.io.*;
import com.neovisionaries.ws.client.*;

import java.util.LinkedList;
import java.util.Queue;

public class GuinyoteClienteWebSockets {
    /**
     * Servidor remoto del backend
     */
    private static final String SERVER = /*"ws://15.188.14.213:27000/echo"*/"ws://echo.websocket.org";

    /**
     * The timeout value in milliseconds for socket connection.
     */
    private static final int TIMEOUT = 5000;

    /**
     * Websocket para la conexión al backend
     */
    private static WebSocket ws;

    /**
     * Buzon que almacena los mensajes recibidos del backend mediante el websocket
     */
    private static Queue<String> buzon;

    /**
     * Connect to the server.
     */
    public void connect() throws IOException, WebSocketException
    {
        // Crea un buzón vacío
        buzon = new LinkedList<>();

        // Crea una conexión y la asocia al websocket de la clase
        ws =  new WebSocketFactory()
                .setConnectionTimeout(TIMEOUT)
                .createSocket(SERVER)
                .addListener(new WebSocketAdapter() {
                    // A text message arrived from the server.
                    public void onTextMessage(WebSocket websocket, String message) {
                        buzon.add(message);
                    }
                })
                .addExtension(WebSocketExtension.PERMESSAGE_DEFLATE)
                .connect();

    }

    public void close() {
        if(ws != null && ws.isOpen())
            ws.disconnect();
        if(buzon != null)
            buzon.clear();
    }

    /**
     * Envía una cadena de texto al backend
     * @param text Cadena de texto enviada
     */
    public void sendText(String text)    {
        if(text != null)
            ws.sendText(text);
    }

    /**
     * Devuelve una cadena de texto recibida del backend y la descarta de los mensajes recibidos.
     * Si no hay ningún mensaje devuelve null.
     * @return Cadena de texto enviada por el backend (null si no se ha recibido nada)
     */
    public String readText() {
        return buzon.poll();
    }
}
