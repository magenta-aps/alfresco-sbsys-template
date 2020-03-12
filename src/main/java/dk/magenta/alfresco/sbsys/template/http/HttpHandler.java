package dk.magenta.alfresco.sbsys.template.http;

import dk.magenta.alfresco.sbsys.template.json.MultipartRequest;
import org.alfresco.error.AlfrescoRuntimeException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
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

    private static final String AUTHORIZATION = "Authorization";
    private static final String URL_MULTIPART_FORM_DATA_REQUESTER = "http://localhost:8081/multipart";

    // TODO: condense the two methods below into one

    public static byte[] GET_CONTENT(String url, String token) {

        httpClient = getCloseableHttpClient();

        HttpGet httpGet = new HttpGet(url);
        httpGet.addHeader(AUTHORIZATION, "Bearer " + token);
        logger.debug(httpGet.toString());

        return executeAndClose(httpGet, getInputStreamResponseHandler(), "GET " + url);
    }

    /**
     * Make a HTTPS GET request
     * @param url The SBSYS URL to call
     * @param token The bearer auth token
     * @return The HTTP JSON body of the response
     */
    public static String GET_JSON(String url, String token) {

        httpClient = getCloseableHttpClient();

        HttpGet httpGet = new HttpGet(url);
        httpGet.addHeader(AUTHORIZATION, "Bearer " + token);
        logger.debug(httpGet.toString());

        return executeAndClose(httpGet, getStringResponseHandler(), "GET " + url);
    }

    /**
     * NOTE: this method calls the multipart-form-data-requester service and NOT SBSYS directly!
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
        return executeAndClose(
                httpPost,
                getStringResponseHandler(),
                "POST (multipart) " + URL_MULTIPART_FORM_DATA_REQUESTER
        );
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

    private static <T> T executeAndClose(HttpUriRequest request, ResponseHandler<T> responseHandler, String logMessage) {
        try {
            try {
                logger.debug(logMessage);
                return httpClient.execute(request, responseHandler);
            } finally {
                // httpClient.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new AlfrescoRuntimeException(e.getMessage());
        }
    }

    // TODO: condense the two methods below

    private static ResponseHandler<byte[]> getInputStreamResponseHandler() {
        ResponseHandler<byte[]> responseHandler = response -> {
            int status = response.getStatusLine().getStatusCode();
            logger.debug("HTTP status: " + status);

            if (status >= 200 && status < 300) {
                HttpEntity entity = response.getEntity();
                return entity != null ? EntityUtils.toByteArray(entity) : null;
            } else {
                logger.error("SBSYS Error");
                throw new AlfrescoRuntimeException("Got HTTP status " + status);
            }
        };
        return responseHandler;
    }

    private static ResponseHandler<String> getStringResponseHandler() {
        ResponseHandler<String> responseHandler = response -> {
            int status = response.getStatusLine().getStatusCode();
            logger.debug("HTTP status: " + status);

            if (status >= 200 && status < 300) {
                HttpEntity entity = response.getEntity();
                return entity != null ? EntityUtils.toString(entity) : null;
            } else {
                logger.error("SBSYS Error");
                throw new AlfrescoRuntimeException("Got HTTP status " + status);
            }
        };
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
