package club.koumakan.rpc.core.client;

import club.koumakan.rpc.core.client.functional.Callback;

public class CallbackInfo {

    private Callback callback;
    // false未被调用, true已被调用
    private boolean call = false;

    public CallbackInfo(Callback callback) {
        this.callback = callback;
    }

    public Callback getCallback() {
        call = true;
        return callback;
    }

    public boolean isCall() {
        return call;
    }
}
