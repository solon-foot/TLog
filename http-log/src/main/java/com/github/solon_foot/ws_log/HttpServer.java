package com.github.solon_foot.ws_log;

import android.content.Context;
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
        InputStream is = null;
        try {
            String headScript = "<script>var port = " + WsServer.it().getPort() + ";</script>";
            byte[] bytes = headScript.getBytes();
            is = context.getAssets().open("web/index.html");
            byte[] b;
            b = new byte[is.available()+ bytes.length];
            System.arraycopy(bytes,0,b,0,bytes.length);
            is.read(b,bytes.length,is.available());
            return newFixedLengthResponse(Response.Status.OK, MIME_HTML, new ByteArrayInputStream(b), b.length);
        } catch (IOException e) {
            return newFixedLengthResponse(Response.Status.NOT_FOUND, MIME_HTML, e.getMessage());
        } catch (Exception e) {
            return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, MIME_HTML, e.getMessage());
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
