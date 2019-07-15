package acme.guess.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Hello controller.
 */
@Controller
@RequestMapping("/api/hello")
public class HelloController {
    @GetMapping("/")
    @ResponseBody
    public String hello() {
        return "Hello, World!";
    }
}
