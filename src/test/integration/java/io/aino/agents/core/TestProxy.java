package io.aino.agents.core;

import java.io.*;
import java.net.*;

public class TestProxy {
    private boolean running = false;

    private final String host;
    private final int sourcePort;
    private final int destinationPort;

    public TestProxy(String host, int sourcePort, int destinationPort){
        this.host = host;
        this.sourcePort = sourcePort;
        this.destinationPort = destinationPort;
    }

    private void runProxy() throws IOException {
        ServerSocket serverSocket = new ServerSocket(sourcePort);

        final byte[] request = new byte[1024];
        byte[] reply = new byte[4096];

        while(running) {
            Socket client = serverSocket.accept();
            Socket server = new Socket(host, destinationPort);

            try {
                proxyThread(client, server, request).start();

                final OutputStream clientOut= client.getOutputStream();
                final InputStream serverIn = server.getInputStream();
                try {
                    readBytes(reply, serverIn, clientOut);
                } finally {
                    clientOut.close();
                }
            }
            finally {
                try {
                    if (server != null) server.close();
                    if (client != null) client.close();
                }
                catch(IOException e) {
                    //don't care, just a test thing
                }
            }
        }
    }

    private Thread proxyThread(Socket client, Socket server, final byte[] request) throws IOException{
        final InputStream clientIn = client.getInputStream();
        final OutputStream serverOut = server.getOutputStream();

        Thread thread = new Thread() {
            public void run() {
                try {
                    readBytes(request, clientIn, serverOut);
                }
                catch (IOException e) {
                    //don't care, just a test thing
                }
                try {
                    serverOut.close();
                } catch (IOException e) {
                    //don't care, just a test thing
                }
            }
        };
        return thread;
    }

    private int readBytes(final byte[] request, InputStream inputStream, OutputStream outputStream) throws IOException {
        int amountOfBytes;
        while((amountOfBytes = inputStream.read(request)) != -1) {
            outputStream.write(request, 0, amountOfBytes);
            outputStream.flush();
        }
        return amountOfBytes;
    }

    public boolean isRunning(){
        return this.running;
    }

    public void start() throws Exception {
        this.running = true;
        new Thread(){
            public void run(){
                try {
                    runProxy();
                } catch (IOException e){

                }
            }
        }.run();
    }

    public void stop() {
        this.running = false;
    }
}
