package net.smart.rfid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import net.smart.rfid.jobs.WMS;

@Component
public class StartComunication implements ApplicationRunner {

	private static final Logger LOG = LoggerFactory.getLogger(StartComunication.class);
	
	
	
	
	
	@Override
	public void run(ApplicationArguments args) throws Exception {
		
		LOG.info("Increment counter");
	
		WMS wms = new WMS();
		Thread threadWms = new Thread(wms);
		threadWms.start();
	}


}
