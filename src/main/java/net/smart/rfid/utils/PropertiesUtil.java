package net.smart.rfid.utils;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "batch.tunnel")
@Configuration
public class PropertiesUtil {
	private static String wmsip;
	private static String wmsport;
	private static String separator;
	private static String epctype;
	
	public static String getWmsip() {
		return wmsip;
	}
	public  void setWmsip(String wmsip) {
		this.wmsip = wmsip;
	}
	
	public static String getWmsport() {
		return wmsport;
	}
	public  void setWmsport(String wmsport) {
		this.wmsport = wmsport;
	}
	public static String getSeparator() {
		return separator;
	}
	public  void setSeparator(String separator) {
		this.separator = separator;
	}
	public static String getEpctype() {
		return epctype;
	}
	public  void setEpctype(String epctype) {
		this.epctype = epctype;
	}
	
	
	

}
