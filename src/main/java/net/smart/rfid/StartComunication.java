package net.smart.rfid;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

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

	
		Client client = ClientBuilder.newClient();
	
		WebTarget target = client.target("http://localhost:8080/api/v1/startWms");
		try {
			target.request().get();
		} catch (Exception e) {
			logger.error("Path error");
			target = client.target("http://localhost:8080/WMSBatch/api/v1/startWms");
			try {
				target.request().get();
			} catch (Exception e2) {
				logger.error("Path error 2");
			}
			
		}
		
		// logger.info("Start Thread WMSAuto");
		//
		// WMSJob wmsAuto = new WMSJob(dataStreamService);
		// Thread threadWms = new Thread(wmsAuto);
		// threadWms.start();
	}

}
