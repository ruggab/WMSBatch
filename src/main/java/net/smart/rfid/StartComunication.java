package net.smart.rfid;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import net.smart.rfid.controller.WMSController;
import net.smart.rfid.jobs.WMSJob;
import net.smart.rfid.tunnel.db.services.DataStreamService;

@Component
public class StartComunication implements CommandLineRunner {

	private static final Logger logger = LoggerFactory.getLogger(StartComunication.class);
	
	@Autowired
	DataStreamService dataStreamService;
	
	
	@Override
	public void run(String... args) throws Exception {
		
		WMSController cc = new WMSController();
		cc.start();
		
//		logger.info("Start Thread WMSAuto");
//	
//		WMSJob wmsAuto = new WMSJob(dataStreamService);
//		Thread threadWms = new Thread(wmsAuto);
//		threadWms.start();
	}


}
