package dk.magenta.sbsys.template.multipart;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

@RestController
public class MultipartRequestController {

    private final Logger logger = LoggerFactory.getLogger(MultipartRequestController.class);

    private final String SUCCESS = "success";
    private final String SBSYS_ENDPOINT = "https://sbsip-m-01.bk-sbsys.dk:28443/convergens-sbsip-sbsys-webapi-proxy/proxy/api/kladde";

    @RequestMapping(path = "/multipart", method = RequestMethod.POST)
    public Response request(@RequestBody Request body) {

        // SSL
        TrustManager[] trustAllCerts = {
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

                    }

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }
                }
        };

        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("SSL");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            sslContext.init(null, trustAllCerts, new SecureRandom());
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }

        HttpClient httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .sslContext(sslContext)
                .build();
        String boundary = new BigInteger(256, new Random()).toString();

        Path localFile = Paths.get(body.getContentStorePath());
        Map<Object, Object> data = new LinkedHashMap<>();
        data.put("json", buildJsonPart(body));
        data.put("files", localFile);

        logger.debug(buildJsonPart(body));

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .header("Authorization", "Bearer " + body.getToken())
                    .header("Content-Type", "multipart/form-data;boundary=" + boundary)
                    .POST(ofMimeMultipartData(data, boundary, body.getMimeType()))
                    .uri(URI.create(SBSYS_ENDPOINT))
                    .build();

            logger.info("Make multipart/form-data request to SBSYS server");
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            logger.info("HTTP status: " + response.statusCode());
            logger.debug("HTTP body: " + response.body());

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new Response(SUCCESS);
    }

    private String buildJsonPart(Request body) {
        // TODO: this could be handled better with Jackson
        return new StringBuilder()
                .append("{\"SagID\":")
                .append(body.getCaseId())
                .append(",\"Navn\":\"")
                .append(body.getName())
                .append("\"}")
                .toString();
    }

    private BodyPublisher ofMimeMultipartData(Map<Object, Object> data, String boundary, String mimeType) throws IOException {
        var byteArrays = new ArrayList<byte[]>();
        byte[] separator = ("--" + boundary + "\r\nContent-Disposition: form-data; name=").getBytes(StandardCharsets.UTF_8);
        for (Map.Entry<Object, Object> entry : data.entrySet()) {
            byteArrays.add(separator);

            if (entry.getValue() instanceof Path) {
                var path = (Path) entry.getValue();
                byteArrays.add(("\"" + entry.getKey() + "\"; filename=\"" + path.getFileName()
                        + "\"\r\nContent-Type: " + mimeType + "\r\n\r\n").getBytes(StandardCharsets.UTF_8));
                byteArrays.add(Files.readAllBytes(path));
                byteArrays.add("\r\n".getBytes(StandardCharsets.UTF_8));
            }
            else {
                byteArrays.add(("\"" + entry.getKey() + "\"\r\n\r\n" + entry.getValue() + "\r\n")
                        .getBytes(StandardCharsets.UTF_8));
            }
        }
        byteArrays.add(("--" + boundary + "--").getBytes(StandardCharsets.UTF_8));
        return BodyPublishers.ofByteArrays(byteArrays);
    }
}