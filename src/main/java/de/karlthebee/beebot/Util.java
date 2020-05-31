package de.karlthebee.beebot;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import de.karlthebee.beebot.module.Worker;
import de.karlthebee.beebot.ts3.BeeBot;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.Random;
import java.util.Set;

@Slf4j
public class Util {

    public static Set<ConstraintViolation<Object>> validate(Object o) {
        var validator = Validation.buildDefaultValidatorFactory().getValidator();
        return validator.validate(o);
    }

    public static String randomId() {
        return Integer.toHexString(new Random().nextInt());
    }

    /**
     * Generates an token
     * @return an random 256b secure hex string
     */
    public static String generateToken(){
        var bytes = new byte[512];  //512B is enough for everythuibng
        new SecureRandom().nextBytes(bytes); // secure random
        return DigestUtils.sha256Hex(bytes); // return hex
    }

    public static ObjectMapper mapper() {
        var mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        return mapper;
    }

    /**
     *
     * @param array the array
     * @param element the element
     * @return true if the element is in the array
     */
    public static boolean contains(int[] array, int element) {
        for (int n = 0; n < array.length; ++n) {
            if (array[n] == element) return true;
        }
        return false;
    }
}
