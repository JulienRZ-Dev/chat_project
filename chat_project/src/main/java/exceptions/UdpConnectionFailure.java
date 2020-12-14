package exceptions;

public class UdpConnectionFailure extends Exception {
    public UdpConnectionFailure(String infos) {
        super("Erreur au niveau de " + infos);
    }
}
