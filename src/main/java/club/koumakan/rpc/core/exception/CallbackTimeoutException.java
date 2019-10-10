package club.koumakan.rpc.core.exception;

public class CallbackTimeoutException extends Exception {

    public CallbackTimeoutException() {
        super();
    }

    public CallbackTimeoutException(String str) {
        super(str);
    }
}
