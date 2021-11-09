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

	private static String wmsManualIp;
	private static String wmsManualPort;
	private static String wmsManualSeparator;
	private static String wmsManualEpctype;

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

	public static String getWmsManualIp() {
		return wmsManualIp;
	}

	public void setWmsManualIp(String wmsManualIp) {
		this.wmsManualIp = wmsManualIp;
	}

	public static String getWmsManualPort() {
		return wmsManualPort;
	}

	public  void setWmsManualPort(String wmsManualPort) {
		this.wmsManualPort = wmsManualPort;
	}

	public static String getWmsManualSeparator() {
		return wmsManualSeparator;
	}

	public void setWmsManualSeparator(String wmsManualSeparator) {
		this.wmsManualSeparator = wmsManualSeparator;
	}

	public static String getWmsManualEpctype() {
		return wmsManualEpctype;
	}

	public void setWmsManualEpctype(String wmsManualEpctype) {
		this.wmsManualEpctype = wmsManualEpctype;
	}
	

}
