package de.karlthebee.beebot.rest;

import de.karlthebee.beebot.Registry;
import de.karlthebee.beebot.Util;
import de.karlthebee.beebot.module.Worker;
import de.karlthebee.beebot.rest.data.Violation;
import de.karlthebee.beebot.ts3.BeeBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

public class RestUtil {
    @Autowired
    protected HttpServletRequest request;


    /**
     * Requires the user to be logged in
     */
    public void requireToken() {
        String token = request.getParameter("token");
        if (!Registry.getInstance().getAdminToken().equalsIgnoreCase(token)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Your token is invalid");
        }
    }

    /**
     * @param config the configuration
     * @return a list of client-parsable violations
     */
    public List<Violation> getViolations(Object config) {
        var errors = Util.validate(config);
        return errors.stream()
                .map(e -> new Violation(e.getPropertyPath().toString(), e.getMessage()))
                .collect(Collectors.toList());
    }

    /**
     * @param bid the bot ID
     * @return the BeeBot
     * @throws ResponseStatusException if the bot couldn't be found
     */
    public BeeBot botById(String bid) throws ResponseStatusException {
        return Registry.getInstance().getBeeBotByUid(bid).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Could not find BeeBot '" + bid + "'"));
    }
}

