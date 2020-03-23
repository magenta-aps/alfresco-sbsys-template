package dk.magenta.sbsys.template.multipart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.net.http.HttpResponse;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Problem contacting the SBSYS server")
public class SbsysException extends RuntimeException {

    private final Logger logger = LoggerFactory.getLogger(MultipartRequestController.class);

    public SbsysException(HttpResponse<String> response) {
        logger.info("SBSYS ERROR");
        logger.info("HTTP body: " + response.body());
    }
}
