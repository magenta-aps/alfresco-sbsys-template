package dk.magenta.alfresco.sbsys.template;

import com.google.gson.Gson;
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
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class MergeData extends AbstractWebScript {
    private static Log logger = LogFactory.getLog(MergeData.class);

    private static final String AUTHORIZATION = "Authorization";
    private static final String TOKEN = "token";

    @Override
    public void execute(WebScriptRequest webScriptRequest, WebScriptResponse webScriptResponse) throws IOException {

        Gson gson = new Gson();

        // Get POSTed JSON as string from request and deserialize into POJO
        TemplateReceiveModel req = gson.fromJson(
                webScriptRequest.getContent().getContent(),
                TemplateReceiveModel.class
        );
        logger.debug(req.token.get(TOKEN));

        // SSL
//        SSLContext sslContext = SSLContexts.custom()
//                .loadTrustMaterial(new File())

//        SSLContext sslContext = SSLContexts.createDefault();
//        SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(
//                sslContext,
//                new String[] {"TLSv1"},
//                null,
//                SSLConnectionSocketFactory.getDefaultHostnameVerifier()
//        );

        TrustManager[] trustAllCerts = new TrustManager[] {
            new X509TrustManagerImpl()
        };

        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("TLS");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            sslContext.init(null, trustAllCerts, new SecureRandom());
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }

        SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(
                sslContext,
                new String[]{"TLSv1.2"},
                null,
                SSLConnectionSocketFactory.getDefaultHostnameVerifier()
        );

        // Get case details from the SBSYS server
        CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLSocketFactory(sslConnectionSocketFactory)
                .build();

        try {
            // TODO: fix hardcoded URL
            HttpGet httpGet = new HttpGet("https://sbsip-m-01.bk-sbsys.dk:28443/convergens-sbsip-sbsys-webapi-proxy/proxy/api/sag/" + req.kladde.get("SagID"));
            httpGet.addHeader(AUTHORIZATION, "Bearer " + req.token.get(TOKEN));
            logger.debug(httpGet.toString());

            // Create response handler
            ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
                @Override
                public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
                    int status = response.getStatusLine().getStatusCode();
                    if (status >= 200 && status < 300) {
                        HttpEntity entity = response.getEntity();
                        return entity != null ? EntityUtils.toString(entity) : null;
                    } else {
                        logger.debug("##### SBSYS server message #####");
                        logger.debug(response.getStatusLine().getReasonPhrase());
                        logger.debug(response.getEntity().getContent().toString());
                        logger.debug("################################");
                        throw new AlfrescoRuntimeException("Got HTTP status " + Integer.toString(status) + " from SBSYS server");
                    }
                }
            };

            String response = httpClient.execute(httpGet, responseHandler);
            logger.debug(response);

            Case sbsysCase = gson.fromJson(response, Case.class);

            System.out.println("hurra");
        } finally {
            httpClient.close();
        }


        logger.debug("Working!!!");
    }
}
