package com.cpuserv.demo;

//import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;
import java.util.UUID;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

@RestController
public class StreamingController {
    private static final Logger log = LoggerFactory.getLogger(StreamingController.class);
    
    @Autowired StreamingService streamService;
    
    @Autowired AmqpAdmin admin;

    @Autowired RabbitTemplate template;

    @Autowired FanoutExchange exchange;

    public void sendMessage() {
        
        template.convertAndSend(exchange.getName(), "", "alert");
        log.info("Send message to exchange: " + exchange.getName());
    }

    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public void getStream(HttpServletResponse response) {
        response.setContentType("multipart/x-mixed-replace; boundary=--BoundaryString\r\n");
        
        Queue queue = new Queue(UUID.randomUUID().toString(), true, false, false);
        Binding binding = BindingBuilder.bind(queue).to(exchange);
        admin.declareQueue(queue);
        admin.declareBinding(binding);
        log.info("Created queue " + queue.getName() + 
                " with binding " + binding.getDestination());
        
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