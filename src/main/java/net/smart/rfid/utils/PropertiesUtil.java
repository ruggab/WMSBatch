package net.smart.rfid.utils;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "batch.tunnel")
@Configuration
public class PropertiesUtil {
	private static String wmsip;
	private static Integer wmsport;
	private static String separator;
	private static String epctype;
	
	public static String getWmsip() {
		return wmsip;
	}
	public static void setWmsip(String wmsip) {
		PropertiesUtil.wmsip = wmsip;
	}
	
	public static Integer getWmsport() {
		return wmsport;
	}
	public static void setWmsport(Integer wmsport) {
		PropertiesUtil.wmsport = wmsport;
	}
	public static String getSeparator() {
		return separator;
	}
	public static void setSeparator(String separator) {
		PropertiesUtil.separator = separator;
	}
	public static String getEpctype() {
		return epctype;
	}
	public static void setEpctype(String epctype) {
		PropertiesUtil.epctype = epctype;
	}
	
	
	

}
