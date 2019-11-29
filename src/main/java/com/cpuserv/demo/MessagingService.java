package com.cpuserv.demo;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class MessagingService {

    private static final Logger log = LoggerFactory.getLogger(MessagingService.class);

    @Autowired SimpMessagingTemplate msgTemplate;

    public void sendMessage() {
        
        log.info("Sending message....");
        msgTemplate.convertAndSend("/exchange/amq.fanout", "alert");
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (Exception e) {
            log.error("Can't pause the thread");
        }
    }
    
}