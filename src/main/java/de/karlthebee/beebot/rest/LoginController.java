package de.karlthebee.beebot.rest;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("login")
@CrossOrigin(origins = "*")
@Slf4j
public class LoginController extends RestUtil {

    @GetMapping("test")
    public Okay testLogin(){
        requireToken();
        return new Okay();
    }

    @Data
    class Okay{
        private String ok = "okay";
    }
}
