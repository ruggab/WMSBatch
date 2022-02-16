package net.smart.rfid;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import net.smart.rfid.jobs.WMSAuto;
import net.smart.rfid.tunnel.db.services.DataStreamService;

@Component
public class StartComunication implements CommandLineRunner {

	private static final Logger logger = LoggerFactory.getLogger(StartComunication.class);
	
	@Autowired
	DataStreamService dataStreamService;
	
	
	@Override
	public void run(String... args) throws Exception {
		
		logger.info("Start Thread WMSAuto");
	
		WMSAuto wmsAuto = new WMSAuto(dataStreamService);
		Thread threadWms = new Thread(wmsAuto);
		threadWms.start();
	}


}
