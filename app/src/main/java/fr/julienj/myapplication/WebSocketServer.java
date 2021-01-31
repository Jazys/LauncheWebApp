package fr.julienj.myapplication;

/*
 * Copyright (c) 2010-2020 Nathan Rajlich
 *
 *  Permission is hereby granted, free of charge, to any person
 *  obtaining a copy of this software and associated documentation
 *  files (the "Software"), to deal in the Software without
 *  restriction, including without limitation the rights to use,
 *  copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following
 *  conditions:
 *
 *  The above copyright notice and this permission notice shall be
 *  included in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 *  EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 *  OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 *  NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 *  HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 *  WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 *  FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 *  OTHER DEALINGS IN THE SOFTWARE.
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;

import org.java_websocket.WebSocket;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ClientHandshake;

/**
 * A simple WebSocketServer implementation. Keeps track of a "chatroom".
 */
public class WebSocketServer extends org.java_websocket.server.WebSocketServer {

    HashMap<String,WebSocket> client= new HashMap<String, WebSocket>();

    public WebSocketServer(int port) throws UnknownHostException {
        super(new InetSocketAddress(port));
    }

    public WebSocketServer(InetSocketAddress address) {
        super(address);
    }

    public WebSocketServer(int port, Draft_6455 draft) {
        super(new InetSocketAddress(port), Collections.<Draft>singletonList(draft));
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {

        if(!client.containsKey(conn.getRemoteSocketAddress().getAddress().getHostAddress()+":"+conn.getRemoteSocketAddress().getPort()))
            client.put(conn.getRemoteSocketAddress().getAddress().getHostAddress()+":"+conn.getRemoteSocketAddress().getPort(), conn);

        conn.send("Welcome to the server!"); //This method sends a message to the new client
        broadcast("new connection: " + handshake
                .getResourceDescriptor()); //This method sends a message to all clients connected
        System.out.println("jj "+
                conn.getRemoteSocketAddress().getAddress().getHostAddress() + " entered the room!" + conn.getRemoteSocketAddress().getPort());
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {

        if(client.containsKey(conn.getRemoteSocketAddress().getAddress().getHostAddress()+":"+conn.getRemoteSocketAddress().getPort()))
            client.remove(conn.getRemoteSocketAddress().getAddress().getHostAddress()+":"+conn.getRemoteSocketAddress().getPort());

        broadcast(conn + " has left the room!");
        System.out.println("jj "+conn + " has left the room!");
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        broadcast(message);
        System.out.println("jj "+conn + ": " + message);
    }

    @Override
    public void onMessage(WebSocket conn, ByteBuffer message) {
        broadcast(message.array());
        System.out.println("jj "+conn + ": " + message);
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
        if (conn != null) {
            // some errors like port binding failed may not be assignable to a specific websocket
        }
    }

    @Override
    public void onStart() {
        System.out.println("jj WSS  Server started!");
        setConnectionLostTimeout(0);
        setConnectionLostTimeout(100);
    }

}