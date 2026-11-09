package com.pharmacy.swagger;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Opening the aggregator's base URL lands on the Swagger UI instead of a 404.
 */
@Controller
public class RootRedirectController {
    
    @GetMapping("/")
    public String root() {
        return "redirect:/swagger-ui.html";
    }
}
