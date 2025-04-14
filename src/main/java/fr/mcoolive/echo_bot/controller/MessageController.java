package fr.mcoolive.echo_bot.controller;

import fr.mcoolive.echo_bot.service.RulesEngine;
import fr.mcoolive.echo_bot.service.Rule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static java.util.concurrent.CompletableFuture.supplyAsync;

@Controller
@RequestMapping(MessageController.REQUEST_MAPPING)
public class MessageController {
    public static final String REQUEST_MAPPING = "/api/message";
    private final Logger LOGGER = LoggerFactory.getLogger(MessageController.class);

    @Autowired
    private RulesEngine<Map<String, Object>, Map<String, Object>> rulesEngine;

    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public CompletableFuture<ResponseEntity<Map<String, Object>>> post(@RequestBody Map<String, Object> json, HttpServletResponse servletResponse) throws IOException {
        final Optional<Rule.Result<Map<String, Object>>> optResult = rulesEngine.execute(json);
        if (!optResult.isPresent())
            return completedFuture(ResponseEntity.internalServerError().build());

        final Rule.Result<Map<String, Object>> result = optResult.get();
        LOGGER.debug(result.getExplanation());

        final long delayInMs = result.getDelayInMs();
        if (delayInMs < 0) {
            // FIXME: this closes the underlying TCP connection. Maybe we want to hang forever...
            servletResponse.setStatus(HttpServletResponse.SC_REQUEST_TIMEOUT);
            servletResponse.getOutputStream().close();
            return null;
        }

        final ResponseEntity<Map<String, Object>> response = ResponseEntity
                .status(result.getHttpStatus())
                .body(result.getOutput());

        if (delayInMs == 0) {
            return completedFuture(response);
        } else { // delayInMs > 0
            // FIXME: the current implementation consumes too many threads, which can result in
            //  a "503 - Service Unavailable" error instead of returning the expected result after a delay.
            //  We should configure a dedicated Executor that uses a scheduler, to avoid blocking threads and improve scalability.
            //  //private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
            //  //return scheduler.schedule(asyncResponse, delayInMs, MILLISECONDS);
            return supplyAsync(() -> delayedReturn(response, delayInMs));
        }
    }

    private static <T> T delayedReturn(T t, long delayInMs) {
        try {
            Thread.sleep(delayInMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return t;
    }
}
