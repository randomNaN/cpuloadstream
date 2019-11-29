package com.cpuserv.demo;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;

/**
 * CpuLoadComponent
 * 
 */
@Component
public class CpuLoadComponent {
    // private static final Logger log = LoggerFactory.getLogger(CpuLoadComponent.class);
    private static final SystemInfo sysInfo = new SystemInfo();
    
    @Scheduled(fixedRate = 200000)
    public double getLoad() {
        CentralProcessor processor = sysInfo.getHardware().getProcessor();
        // log.info("Started scheduling component with processor:" + 
        //         processor.getModel());

        return processor.getSystemCpuLoad() * 100;
    }
}