package club.koumakan.rpc.exception;

public class CallbackTimeoutException extends Exception {

    public CallbackTimeoutException() {
        super();
    }

    public CallbackTimeoutException(String str) {
        super(str);
    }
}
