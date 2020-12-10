package de.karlthebee.beebot.service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class IPService {
    private static final Cache<String, Object> ipCache = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build();

    /**
     * See https://attacomsian.com/blog/how-to-convert-country-code-to-emoji-in-java
     * @param code the country code ("DE")
     * @return an unicode flag
     */
    public String countryCodeToEmoji(String code){
        // offset between uppercase ascii and regional indicator symbols
        int OFFSET = 127397;
        if (code==null || code.length()!=2)
            return "";
        code = code.toUpperCase();
        if (code.equals("UK"))
            code = "GB";

        var builder = new StringBuilder();
        for (int i = 0; i < code.length(); i++) {
            builder.appendCodePoint(code.charAt(i) + OFFSET);
        }

        return builder.toString();

    }
}
