package com.redshift.order.Controller;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.ws.Response;

@RestController
public class LogController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @RequestMapping("/api/logging/{level}")
    public ResponseEntity changeLoggingLevel(@PathVariable(value="level")String level, @RequestParam("package") String package_name){
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        level=level.toUpperCase();
        if(level.equalsIgnoreCase("Debug")){
            loggerContext.getLogger(package_name).setLevel(Level.DEBUG);
        }
        if(level.equalsIgnoreCase("INFO")){
            loggerContext.getLogger(package_name).setLevel(Level.INFO);
        }
        if(level.equalsIgnoreCase("WARN")){
            loggerContext.getLogger(package_name).setLevel(Level.WARN);
        }
        if(level.equalsIgnoreCase("ERROR")){
            loggerContext.getLogger(package_name).setLevel(Level.ERROR);
        }




        return ResponseEntity.ok().body("logger level set to :"+loggerContext.getLogger(package_name).getLevel());
    }

    @RequestMapping("/api/logging")
    public ResponseEntity printLog(@RequestParam("package")String package_name){
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        logger.debug("debug");
        logger.info("info");
        logger.warn("warn");
        logger.error("error");
        return ResponseEntity.ok().body("logger level is :"+loggerContext.getLogger(package_name).getLevel());

    }

}
