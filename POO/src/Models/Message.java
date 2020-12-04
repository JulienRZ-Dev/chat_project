package Models;

import java.util.Date;

public class Message {

    // Attributes
    private final String recipient; // destinataire
    private final String transmitter; // emetteur
    private final String content;
    private final Date date;


    // Constructor
    public Message(String recipient, String transmitter, String content) {
        this.recipient = recipient;
        this.transmitter = transmitter;
        this.content = content;
        this.date = new Date(); // La date est décidée au moment de la construction de l'objet message
    }


    // Getters and setters

    // Getters

    public String getRecipient() {
        return recipient;
    }

    public String getTransmitter() {
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
