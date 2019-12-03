package dk.magenta.alfresco.sbsys.template;

import dk.magenta.alfresco.sbsys.template.json.MultipartRequest;
import org.alfresco.error.AlfrescoRuntimeException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
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
    private static final String URL_MULTIPART_FORM_DATA_REQUESTER = "http://localhost:8081/multipart";

    /**
     * Make a HTTPS GET request
     * @param url The SBSYS URL to call
     * @param token The bearer auth token
     * @return The HTTP body of the response
     */
    public static String GET(String url, String token) {

        httpClient = getCloseableHttpClient();

        HttpGet httpGet = new HttpGet(url);
        httpGet.addHeader(AUTHORIZATION, "Bearer " + token);
        logger.debug(httpGet.toString());

        try {
            try {
                logger.debug("GET " + url);
                return httpClient.execute(httpGet, getResponseHandler());
            } finally {
                httpClient.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new AlfrescoRuntimeException(e.getMessage());
        }
    }

    /**
     * NOTE: this method call the multipart-form-data-requester service and NOT SBSYS directly!
     */
    public static String POST_MULTIPART(MultipartRequest multipartRequest) {

        httpClient = getCloseableHttpClient(null);

        HttpPost httpPost = new HttpPost(URL_MULTIPART_FORM_DATA_REQUESTER);
        logger.debug(httpPost.toString());

        HttpEntity httpEntity = new StringEntity(
                RequestResponseHandler.serialize(multipartRequest),
                ContentType.APPLICATION_JSON
        );

        httpPost.setEntity(httpEntity);

        // TODO: put this into private method
        try {
            try {
                logger.debug("POST (multipart) " + URL_MULTIPART_FORM_DATA_REQUESTER);
                return httpClient.execute(httpPost, getResponseHandler());
            } finally {
                httpClient.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new AlfrescoRuntimeException(e.getMessage());
        }
    }

    private static CloseableHttpClient getCloseableHttpClient() {
        return getCloseableHttpClient(getSslConnectionSocketFactory(getSSLContext()));
    }

    private static CloseableHttpClient getCloseableHttpClient(SSLConnectionSocketFactory sslConnectionSocketFactory) {
        if (sslConnectionSocketFactory == null) {
            return HttpClients.createDefault();
        } else {
            return HttpClients.custom()
                    .setSSLSocketFactory(sslConnectionSocketFactory)
                    .build();
        }
    }

    private static ResponseHandler<String> getResponseHandler() {
        if (responseHandler == null) {
            responseHandler = new ResponseHandler<String>() {
                @Override
                public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
                    int status = response.getStatusLine().getStatusCode();

                    logger.debug("HTTP status: " + status);

                    if (status >= 200 && status < 300) {
                        HttpEntity entity = response.getEntity();
                        return entity != null ? EntityUtils.toString(entity) : null;
                    } else {
                        logger.error("SBSYS Error");
                        // TODO: close things
                        throw new AlfrescoRuntimeException("Got HTTP status " + status);
                    }
                }
            };
        }
        return responseHandler;
    }

    private static SSLConnectionSocketFactory getSslConnectionSocketFactory(SSLContext sslContext) {
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
