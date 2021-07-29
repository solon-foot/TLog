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
        String uri = session.getUri();
        return newFixedLengthResponse(Response.Status.OK, MIME_HTML, html.replace("WEB_SOCKET_PORT",WsServer.it().getPort() + ""));
    }
static String html ="<html>\n"
    + "    <head>\n"
    + "        <title>在线log</title>\n"
    + "        <meta http-equiv=\"content-type\" content=\"text/html;charset=utf-8\">\n"
    + "    </head>\n"
    + "    <body>\n"
    + "        <ul id='ul'>\n"
    + "        </ul>\n"
    + "    </body>\n"
    + "    <script>\n"
    + "        let host = window.location.hostname;\n"
    + "        let port = WEB_SOCKET_PORT;\n"
    + "        let ws = new WebSocket('ws://'+host+\":\"+port)\n"
    + "        let divObj = document.getElementById(\"ul\");\n"
    + "        ws.onmessage = (e)=>{\n"
    + "            let li = document.createElement('li');\n"
    + "            li.innerHTML = e.data;\n"
    + "            divObj.appendChild(li);\n"
    + "        }\n"
    + "    </script>\n"
    + "</html>";
    public Response baseResponse(String url, IHTTPSession session) {
        InputStream is = null;
        try {
            is = context.getAssets().open("web" + url);
            byte[] b;
            b = new byte[is.available()];
            is.read(b);
            return newFixedLengthResponse(Response.Status.OK, getMimeTypeForFile(url), new ByteArrayInputStream(b), b.length);
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
