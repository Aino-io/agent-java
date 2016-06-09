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

    private void run() throws IOException {
        ServerSocket ss = new ServerSocket(sourcePort);

        final byte[] request = new byte[1024];
        byte[] reply = new byte[4096];

        while(isRunning()) {
            Socket client = null, server = null;
            try {
                client = ss.accept();

                final InputStream from_client = client.getInputStream();
                final OutputStream to_client= client.getOutputStream();

                try { server = new Socket(host, destinationPort); }
                catch (IOException e) {
                    PrintWriter out = new PrintWriter(new OutputStreamWriter(to_client));
                    out.println("Proxy server cannot connect to " + host + ":" +
                            destinationPort + ":\n" + e);
                    out.flush();
                    client.close();
                    continue;
                }

                final InputStream from_server = server.getInputStream();
                final OutputStream to_server = server.getOutputStream();

                Thread t = new Thread() {
                    public void run() {
                        int bytes_read;
                        try {
                            while((bytes_read = from_client.read(request)) != -1) {
                                to_server.write(request, 0, bytes_read);
                                to_server.flush();
                            }
                        }
                        catch (IOException e) {}
                        try {to_server.close();} catch (IOException e) {}
                    }
                };

                t.start();
                int bytes_read;
                try {
                    while((bytes_read = from_server.read(reply)) != -1) {
                        to_client.write(reply, 0, bytes_read);
                        to_client.flush();
                    }
                }
                catch(IOException e) {}
                to_client.close();
            }
            catch (IOException e) { System.err.println(e); }
            finally {
                try {
                    if (server != null) server.close();
                    if (client != null) client.close();
                }
                catch(IOException e) {}
            }
        }
    }

    public boolean isRunning(){
        return this.running;
    }

    public void start() throws Exception {
        this.running = true;
        run();
    }

    public void stop() {
        this.running = false;
    }
}
