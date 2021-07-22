package net.smart.rfid.WMSBatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import net.smart.rfid.jobs.WMS;
import net.smart.rfid.tunnel.db.services.TipologicaService;

@Component
public class StartComunication implements CommandLineRunner {

	private static final Logger LOG = LoggerFactory.getLogger(StartComunication.class);
	
	@Autowired(required=true)
    private TipologicaService tipologicaService;
	
	
	
	@Override
	public void run(String... args) throws Exception {
		
		LOG.info("Increment counter");
		tipologicaService.getDescrizioneById(1L);
		WMS wms = new WMS();
		Thread threadWms = new Thread(wms);
		threadWms.start();
	}


}
