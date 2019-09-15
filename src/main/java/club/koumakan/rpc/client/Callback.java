package club.koumakan.rpc.client;

import club.koumakan.rpc.Future;

public abstract class Callback<C> extends Future {

    public abstract void response(C responseMessage);
}
