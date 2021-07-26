package net.smart.rfid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import net.smart.rfid.jobs.WMS;
import net.smart.rfid.tunnel.db.services.DataStreamService;

@Component
public class StartComunication implements CommandLineRunner {

	private static final Logger LOG = LoggerFactory.getLogger(StartComunication.class);
	
	@Autowired
	DataStreamService dataStreamService;
	
	
	@Override
	public void run(String... args) throws Exception {
		
		LOG.info("Start Thread WMS");
	
		WMS wms = new WMS(dataStreamService);
		Thread threadWms = new Thread(wms);
		threadWms.start();
	}


}
