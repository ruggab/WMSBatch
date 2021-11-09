package net.smart.rfid.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.smart.rfid.jobs.WMSManual;
import net.smart.rfid.tunnel.db.services.DataStreamService;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "http://localhost:4200")
public class WMSController {

	@Autowired
	DataStreamService dataStreamService;

	@PostMapping("/callWMSIn")
	public String callWMSIn(@RequestBody String packageId) throws Exception {
		try {
            WMSManual.callWMSIn(packageId, dataStreamService);
			return "ok";
		} catch (Exception e) {
			throw e;
		}
	}

	@PostMapping("/callWMSOut")
	public String callWMSOut(@RequestBody String packageId) throws Exception {
		try {
			 WMSManual.callWMSOut(packageId, dataStreamService);
			return "ok";
		} catch (Exception e) {
			throw e;
		}
	}
}
