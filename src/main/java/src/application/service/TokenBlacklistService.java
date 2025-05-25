package src.application.service;

import org.springframework.stereotype.Service;
import java.util.HashSet;
import java.util.Set;
import java.util.*;

@Service
public class TokenBlacklistService {
    private final Set<String> blacklistedTokens = Collections.synchronizedSet(new HashSet<>());
    private final Map<String, List<String>> userTokens = new HashMap<>();

    //add token to the blacklist
    public void blacklistToken(String token) {
        blacklistedTokens.add(token);
    }

    //blacklist all previous tokens for a user
    public void blacklistPreviousTokens(String username) {
        List<String> tokens = userTokens.get(username);
        if (tokens != null) {
            blacklistedTokens.addAll(tokens);
            tokens.clear();
        }
    }

    //check if a token is blacklisted
    public boolean isBlacklisted(String token) {
        return blacklistedTokens.contains(token);
    }

    //store newly issued token
    public void storeToken(String username, String token) {
        userTokens.computeIfAbsent(username, k -> new ArrayList<>()).add(token);
    }
}
