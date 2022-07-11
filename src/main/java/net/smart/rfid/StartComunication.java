package net.smart.rfid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import net.smart.rfid.controller.WMSController;
@Component
public class StartComunication  {

	private static final Logger logger = LoggerFactory.getLogger(StartComunication.class);

	@Autowired
	WMSController wmsController;
	
	@EventListener(ApplicationReadyEvent.class)
	public void init() {

		
		try {
			wmsController.start();
		} catch (Exception e) {
			logger.error(e.toString());
			

		}

	}

}
