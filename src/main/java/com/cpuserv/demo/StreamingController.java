package com.cpuserv.demo;

//import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StreamingController {
    private static final Logger log = LoggerFactory.getLogger(StreamingController.class);
    
    @Autowired CpuLoadComponent cpuLoadComponent;
    @Autowired StreamingService streamService;
    @Autowired MessagingService msgService;

    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public void getStream(HttpServletResponse response) {
        response.setContentType("multipart/x-mixed-replace; boundary=--BoundaryString\r\n");
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
        response.setHeader("Pragma", "no-cache"); // HTTP 1.0.
        response.setDateHeader("Expires", 0);
        
        try (OutputStream outStream = response.getOutputStream()) {
            
            while(true) {
                Double load = cpuLoadComponent.getLoad();
                byte[] imageData = streamService.getBytedImage(load);
                
                if (load.compareTo(50.0) > 0) {
                    msgService.sendMessage();
                }

                outStream.write((
                "--BoundaryString\r\n" + 
                "Content-type: image/jpeg\r\n" + 
                "Content-Length: " + imageData.length + "\r\n\r\n").getBytes());
                outStream.write(imageData);
                outStream.write("\r\n\r\n".getBytes());
                outStream.flush();

                TimeUnit.MILLISECONDS.sleep(20);
            }

        } catch (Exception e) {
            //log.error("Error on streaming controller");
            return;
        }
    }
}