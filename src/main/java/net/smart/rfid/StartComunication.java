package net.smart.rfid;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import net.smart.rfid.controller.WMSController;
@Component
public class StartComunication  {

	private static final Logger logger = LoggerFactory.getLogger(StartComunication.class);

	@Autowired
	WMSController wmsController;
	
	@PostConstruct
	public void init() {

		
		try {
			wmsController.start();
		} catch (Exception e) {
			logger.error(e.toString());
			

		}

	}

}
