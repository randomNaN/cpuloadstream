package com.cpuserv.demo;

//import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StreamingController {

    @Autowired StreamingService streamService;

    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public void getStream(HttpServletResponse response) {
        response.setContentType("multipart/x-mixed-replace; boundary=--BoundaryString\r\n");

        try (OutputStream outStream = response.getOutputStream()) {
                
            while(true) {
                byte[] imageData = streamService.getBytedImage();

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
            //e.printStackTrace();
            return;
        }
    }
}