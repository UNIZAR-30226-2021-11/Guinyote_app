package manyosoft.guinyote.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Clase que traduce las cartas recibidas en JSON al id que poseen en la aplicación y viceversa
 */
public class CardTranslator {
    private final String AppBastos = "basto_";
    private final String AppCopas = "copa_";
    private final String AppEspadas = "espada_";
    private final String AppOros = "oro_";
    private final String BackBastos = "bastos";
    private final String BackOros = "oros";
    private final String BackEspadas = "espadas";
    private final String BackCopas = "copas";



    private Map<String, String> palo;

    public CardTranslator() {
        this.palo = new HashMap<>();
        this.palo.put(BackBastos, AppBastos);
        this.palo.put(BackCopas, AppCopas);
        this.palo.put(BackOros, AppOros);
        this.palo.put(BackEspadas, AppEspadas);
    }

    public String getAppCardName(String suit, Integer val) {
        return this.palo.get(suit)+val.toString();
    }

    // TODO IMPLEMENTAR
    public String getBackCardName(String cardName)  {
        return "";
    }
}
