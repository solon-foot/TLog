package com.github.solon_foot.ws_log;

import android.content.Context;
import com.github.solon_foot.TLog;
import fi.iki.elonen.NanoHTTPD;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

class HttpServer extends NanoHTTPD {

    private Context context;
    public HttpServer(Context context,int port) {
        super(port);
        this.context = context;
    }

    @Override
    public Response serve(IHTTPSession session) {
        if ("/port".equals(session.getUri()))
            return newFixedLengthResponse(Response.Status.OK, MIME_PLAINTEXT, WsServer.it().getPort()+"");
        try (InputStream is = context.getAssets().open("web/index.html")){
            return newFixedLengthResponse(Response.Status.OK, MIME_HTML, is,is.available());
        } catch (IOException e) {
            return newFixedLengthResponse(Response.Status.NOT_FOUND, MIME_HTML, e.getMessage());
        } catch (Exception e) {
            return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, MIME_HTML, e.getMessage());
        } finally {

        }
    }
}
