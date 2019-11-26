package dk.magenta.alfresco.sbsys.template;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;

public class HttpHandler {

    private static Log logger = LogFactory.getLog(HttpHandler.class);
    private static CloseableHttpClient httpClient;
    private static ResponseHandler<String> responseHandler;

    private static final String AUTHORIZATION = "Authorization";

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
                return httpClient.execute(httpGet, getResponseHandler());
            } finally {
                httpClient.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new AlfrescoRuntimeException(e.getMessage());
        }
    }

    public static String POST_MULTIPART(String url, String token, String json, InputStream file) {

        httpClient = getCloseableHttpClient();

        HttpPost httpPost = new HttpPost(url);
        httpPost.addHeader(AUTHORIZATION, "Bearer " + token);
        logger.debug(httpPost.toString());

        StringBody jsonValue = new StringBody(json, ContentType.APPLICATION_JSON); // Is this right???
        InputStreamBody inputStreamBody = new InputStreamBody(file, "random_filename.docx");

        HttpEntity httpEntity = MultipartEntityBuilder.create()
                .addPart("json", jsonValue)
                .addPart("files", inputStreamBody)
                .build();

        httpPost.setEntity(httpEntity);

        // TODO: put this into private method
        try {
            try {
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
