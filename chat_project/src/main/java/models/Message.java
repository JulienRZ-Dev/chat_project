package models;

import java.util.Date;

public class Message {

    // Attributes
    private final int recipient; // destinataire
    private final int transmitter; // emetteur
    private final String content;
    private final Date date;


    // Constructor
    public Message(int recipient, int transmitter, String content) {
        this.recipient = recipient;
        this.transmitter = transmitter;
        this.content = content;
        this.date = new Date(); // La date est décidée au moment de la construction de l'objet message
    }


    // Getters and setters

    // Getters

    public int getRecipient() {
        return recipient;
    }

    public int getTransmitter() {
        return transmitter;
    }

    public String getContent() {
        return content;
    }

    public Date getDate() {
        return date;
    }

    // Pas de setters pour message, aucun message ne doit être modifié après construction
}
