package net.smart.rfid.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.smart.rfid.jobs.WMS;
@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "http://localhost:4200")
public class WMSController {

	
	@PostMapping("/callWMSIn")
	public String  callWMSIn(@RequestBody String packageId) throws Exception {
		try {
			WMS.barcodeIn = packageId;
			
			return "ok";
		} catch (Exception e) {
			throw e;
		}
	}
	
	
	@PostMapping("/callWMSOut")
	public String  callWMSOut(@RequestBody String packageId) throws Exception {
		try {
			WMS.barcodeOut = packageId;
			
			return "ok";
		} catch (Exception e) {
			throw e;
		}
	}
}
