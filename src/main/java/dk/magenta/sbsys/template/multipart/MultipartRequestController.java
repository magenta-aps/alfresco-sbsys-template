package dk.magenta.sbsys.template.multipart;

import java.io.IOException;
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
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

@RestController
public class MultipartRequestController {

    private final Logger logger = LoggerFactory.getLogger(MultipartRequestController.class);
    private final String SUCCESS = "success";
    private final String CRLF = "\r\n";
    @Value("${sbsys.endpoint}")
    private String sbsysEndpoint;
    private int contentLength;

    @RequestMapping(path = "/multipart", method = RequestMethod.POST)
    public Response request(@RequestBody Request requestBody) {

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

        String boundary = getBoundary();

        Path localFile = Paths.get(requestBody.getContentStorePath());

        logger.debug(requestBody.getJson());

        try {

            BodyPublisher body = buildMultipartBody(
                    requestBody.getJson(),
                    localFile,
                    boundary,
                    requestBody.getFilename(),
                    requestBody.getMimeType()
            );

            HttpRequest request = HttpRequest.newBuilder()
                    .header("Authorization", "Bearer " + requestBody.getToken())
                    .header("Content-Type", "multipart/form-data;boundary=" + boundary)
                    .POST(body)
                    .uri(URI.create(sbsysEndpoint))
                    .build();

            // logHeaders(request);
            logger.info("Make multipart/form-data request to SBSYS server");

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            int status = response.statusCode();

            logger.info("HTTP status: " + status);
            logger.debug("HTTP body: " + response.body());

            if (!(status >= 200 && status < 300)) {
                throw new SbsysException(response);
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return new Response(SUCCESS);
    }

    private BodyPublisher buildMultipartBody(
            String json,
            Path path,
            String boundary,
            String filename,
            String mimeType) throws IOException {

        String jsonPart = "--" + boundary + CRLF +
                "Content-Disposition: form-data; name=\"json\"" + CRLF +
                CRLF +
                json + CRLF;

        String filePart = "--" + boundary + CRLF +
                "Content-Disposition: form-data; name=\"files\"; filename=\"" + filename + "\"" + CRLF +
                "Content-Type: " + mimeType + CRLF +
                CRLF;

        String end = "--" + boundary + "--" + CRLF;

        List<byte[]> byteArrays = new ArrayList<byte[]>();
        byteArrays.add(jsonPart.getBytes(StandardCharsets.UTF_8));
        byteArrays.add(filePart.getBytes(StandardCharsets.UTF_8));
        byteArrays.add(Files.readAllBytes(path));
        byteArrays.add(CRLF.getBytes(StandardCharsets.UTF_8));
        byteArrays.add(end.getBytes(StandardCharsets.UTF_8));

        getContentLength(byteArrays);

        // log(byteArrays);

        return BodyPublishers.ofByteArrays(byteArrays);
    }

    private String getBoundary() {
        // No alphabet string constants in Java?
        String alphabetLowerCase = "abcdefghijklmnopqrtsuvwxyz";
        String alphabetLowerAndUpper = alphabetLowerCase + alphabetLowerCase.toUpperCase();
        StringBuilder boundary = new StringBuilder("----WebKitFormBoundary");

        Random random = new Random();
        for (int i = 0; i < 16; i++) {
            boundary.append(alphabetLowerAndUpper.charAt(random.nextInt(alphabetLowerAndUpper.length())));
        }

        return boundary.toString();
    }

    private void log(List<byte[]> byteArrays) {
        logger.debug("Outputting byteArrays...");
        int length = 0;
        for (byte[] byteArray : byteArrays) {
            String s = new String(byteArray, StandardCharsets.UTF_8);
            System.out.println(s);

            length += byteArray.length;
        }
        logger.debug(Integer.toString(length));
    }

    private void logHeaders(HttpRequest request) {
        logger.debug("Logging headers (START)...");
        var map = request.headers().map();
        for (var key : map.keySet()) {
            logger.debug(key, map.get(key));
        }
        logger.debug("Logging headers (END)...");
    }

    private int getContentLength(List<byte[]> byteArrays) {
        int contentLength = byteArrays.stream()
                .map(b -> b.length)
                .reduce(0, Integer::sum);
        this.contentLength = contentLength;
        return contentLength;
    }
}