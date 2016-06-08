package io.aino.agents.core;

import com.sun.jersey.client.urlconnection.HttpURLConnectionFactory;
import com.sun.jersey.client.urlconnection.URLConnectionClientHandler;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by mystes-am on 8.6.2016.
 */
public class HttpProxyFactory implements HttpURLConnectionFactory {

    @Override
    public HttpURLConnection getHttpURLConnection(URL url) throws IOException {
        return (HttpURLConnection) url.openConnection();
    }

    public static URLConnectionClientHandler getConnectionHandler(){
        return new URLConnectionClientHandler(new HttpProxyFactory());
    }
}
