package net.smart.rfid.utils;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "batch.tunnel")
@Configuration
public class PropertiesUtil {
	private static String wmsAutoIp;
	private static String wmsAutoPort;
	private static String wmsAutoSeparator;
	private static String wmsAutoEpctype;

	
	public static String getWmsAutoIp() {
		return wmsAutoIp;
	}

	public  void setWmsAutoIp(String wmsAutoIp) {
		this.wmsAutoIp = wmsAutoIp;
	}

	public static String getWmsAutoPort() {
		return wmsAutoPort;
	}

	public  void setWmsAutoPort(String wmsAutoPort) {
		this.wmsAutoPort = wmsAutoPort;
	}

	public static String getWmsAutoSeparator() {
		return wmsAutoSeparator;
	}

	public  void setWmsAutoSeparator(String wmsAutoSeparator) {
		this.wmsAutoSeparator = wmsAutoSeparator;
	}

	public static String getWmsAutoEpctype() {
		return wmsAutoEpctype;
	}

	public  void setWmsAutoEpctype(String wmsAutoEpctype) {
		this.wmsAutoEpctype = wmsAutoEpctype;
	}

	


	

}
