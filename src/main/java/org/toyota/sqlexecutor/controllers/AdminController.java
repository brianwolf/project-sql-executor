package org.toyota.sqlexecutor.controllers;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequestMapping("/")
public class AdminController {

    private static Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    private Environment env;

    @GetMapping("/")
    public ResponseEntity<Object> getMessages() {

        String version = env.getProperty("project.version");

        HashMap<String, Object> result = new HashMap<>();
        result.put("version", version);

        logger.info(String.format("version = %s", version));

        return ResponseEntity.status(200).body(result);
    }

    @GetMapping("/docs")
    public RedirectView docs(RedirectAttributes attributes) {
        return new RedirectView("swagger-ui/index.html");
    }
}
