package dk.magenta.alfresco.sbsys.template;

import org.alfresco.error.AlfrescoRuntimeException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;

public class HttpHandler {

    private static Log logger = LogFactory.getLog(HttpHandler.class);
    private static CloseableHttpClient httpClient;
    private static ResponseHandler<String> responseHandler;

    private static final String AUTHORIZATION = "Authorization";

    public static String GET(String url, String token) {

        httpClient = getCloseableHttpClient(getSSslConnectionSocketFactory(getSSLContext()));

        HttpGet httpGet = new HttpGet(url);
        httpGet.addHeader(AUTHORIZATION, "Bearer " + token);
        logger.debug(httpGet.toString());

        try {
            try {
                return httpClient.execute(httpGet, getResponseHandler());
            } finally {
                httpClient.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new AlfrescoRuntimeException(e.getMessage());
        }
    }

    private static CloseableHttpClient getCloseableHttpClient(SSLConnectionSocketFactory sslConnectionSocketFactory) {
        return HttpClients.custom()
                .setSSLSocketFactory(sslConnectionSocketFactory)
                .build();
    }

    private static ResponseHandler<String> getResponseHandler() {
        if (responseHandler == null) {
            responseHandler = new ResponseHandler<String>() {
                @Override
                public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
                    int status = response.getStatusLine().getStatusCode();
                    if (status >= 200 && status < 300) {
                        HttpEntity entity = response.getEntity();
                        return entity != null ? EntityUtils.toString(entity) : null;
                    } else {
                        logger.debug(response.getEntity().getContent().toString());
                        throw new AlfrescoRuntimeException("Got HTTP status " + Integer.toString(status) + " from SBSYS server");
                    }
                }
            };
        }
        return responseHandler;
    }

    private static SSLConnectionSocketFactory getSSslConnectionSocketFactory(SSLContext sslContext) {
        return new SSLConnectionSocketFactory(
                sslContext,
                new String[]{"TLSv1.2"},
                null,
                SSLConnectionSocketFactory.getDefaultHostnameVerifier()
        );
    }

    private static SSLContext getSSLContext() {
        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, getTrustManager(), new SecureRandom());
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        return sslContext;
    }

    private static TrustManager[] getTrustManager() {
        return new TrustManager[] {
                new X509TrustManagerImpl()
        };
    }
}
