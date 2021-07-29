package com.github.solon_foot.ws_log;

import android.util.Log;
import com.github.solon_foot.TLog.PrintProxy;
import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Set;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

class WsServer extends WebSocketServer implements PrintProxy {

    public void println(int prolity,String msg){
        for (WebSocket webSocket : set) {
            webSocket.send(msg);
        }
    }

    public WsServer() {
        super(new InetSocketAddress( 0 ));
    }

    private static WsServer wsServer;
    public static WsServer it() {
        if (wsServer!=null)return wsServer;
        wsServer = new WsServer();
        wsServer.start();
        return wsServer;
    }
    Set<WebSocket> set = new HashSet<>();
    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        set.add(conn);
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        set.remove(conn);
    }

    @Override
    public void onMessage(WebSocket conn, String message) {

    }

    @Override
    public void onError(WebSocket conn, Exception ex) {

    }

    @Override
    public void onStart() {

        Log.e("test",getPort()+":port");
    }
}
