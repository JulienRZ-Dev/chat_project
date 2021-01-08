package models;

import java.sql.Timestamp;

public class Message {

    // Attributes
    private final int recipient; // destinataire
    private final int transmitter; // emetteur
    private final String content;
    private final Timestamp date;


    // Constructor
    public Message(int recipient, int transmitter, String content, Timestamp date) {
        this.recipient = recipient;
        this.transmitter = transmitter;
        this.content = content;
        this.date = date;
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

    public Timestamp getDate() {
        return date;
    }

    // Pas de setters pour message, aucun message ne doit être modifié après construction
}
