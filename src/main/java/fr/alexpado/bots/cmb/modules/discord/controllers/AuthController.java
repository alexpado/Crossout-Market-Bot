package fr.alexpado.bots.cmb.modules.discord.controllers;


import fr.alexpado.bots.cmb.modules.discord.DiscordSettings;
import fr.alexpado.bots.cmb.modules.discord.HTTPRequest;
import fr.alexpado.bots.cmb.modules.discord.events.DiscordLoginEvent;
import fr.alexpado.bots.cmb.modules.discord.models.Session;
import fr.alexpado.bots.cmb.modules.discord.repositories.DiscordUserRepository;
import fr.alexpado.bots.cmb.modules.discord.repositories.SessionRepository;
import fr.alexpado.bots.cmb.modules.discord.utils.SessionQuery;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(maxAge = 3600, origins = "*")
public class AuthController {

    private final ApplicationEventPublisher publisher;
    private final DiscordSettings settings;
    private final SessionRepository sessionRepository;
    private final DiscordUserRepository discordUserRepository;

    public AuthController(ApplicationEventPublisher publisher, DiscordSettings settings, SessionRepository sessionRepository, DiscordUserRepository discordUserRepository) {
        this.publisher = publisher;
        this.settings = settings;
        this.sessionRepository = sessionRepository;
        this.discordUserRepository = discordUserRepository;
    }

    /**
     * Retrieve a user with the provided session code. If the code isn't associated with an user, the code will be
     * tested against the Discord API.
     *
     * @param request
     *         The request object generated by the http server library
     * @param bearer
     *         The Discord session code used to authenticate the user
     *
     * @return The user associated to the provided code, or the newly authenticated user
     */
    @RequestMapping(value = "/login", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public Session login(HttpServletRequest request, @RequestHeader(value = "Authorization", defaultValue = "") String bearer) {
        String code = bearer.replace("Bearer", "").trim();

        Optional<Session> optionalSession = this.sessionRepository.findByCode(code);

        if (optionalSession.isPresent()) { // We are already logged in.
            Session session = optionalSession.get();
            session.setLastUse(System.currentTimeMillis());
            return this.sessionRepository.save(session);
        }

        AtomicReference<Session> session = new AtomicReference<>(null);

        new HTTPRequest(this.settings).doFullAuth(request, this.discordUserRepository, this.sessionRepository, session::set, jsonObject -> {
            System.err.println(jsonObject.toString(2));
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        });

        this.publisher.publishEvent(new DiscordLoginEvent(this, session.get()));
        return session.get();
    }

    /**
     * Remove a user's session associated with the provided code.
     *
     * @param request
     *         The request object generated by the http server library
     * @param content
     *         The request body containing the session's code to logout
     */
    @RequestMapping(value = "/logout", method = RequestMethod.DELETE, produces = {MediaType.APPLICATION_JSON_VALUE})
    public void logoutUser(HttpServletRequest request, @RequestBody(required = false) Map<String, String> content) {
        Session session = new SessionQuery(this.sessionRepository).getSession(request, HttpStatus.UNAUTHORIZED);
        String otherSessionCode = content != null ? content.getOrDefault("session", session.getCode()) : session.getCode();
        Session otherSession = new SessionQuery(this.sessionRepository).getSession(otherSessionCode, session.getDiscordUser());
        this.sessionRepository.delete(otherSession);
        throw new ResponseStatusException(HttpStatus.NO_CONTENT);
    }

    /**
     * @param request
     *         The request object generated by the http server library
     *
     * @return The list of session for the currently logged in user
     */
    @RequestMapping(value = "/sessions", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public List<Session> getUserSessions(HttpServletRequest request) {
        Session session = new SessionQuery(this.sessionRepository).getSession(request, HttpStatus.UNAUTHORIZED);
        return new SessionQuery(this.sessionRepository).getSessions(session.getDiscordUser());
    }

}