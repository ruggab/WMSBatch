package net.smart.rfid.controller;

import java.io.InputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import net.smart.rfid.jobs.WMSTest;
import net.smart.rfid.tunnel.db.services.DataStreamService;
import net.smart.rfid.utils.PropertiesUtil;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "http://localhost:4200")
public class WMSController {
	private static final Logger logger = LoggerFactory.getLogger(WMSController.class);
	@Autowired
	DataStreamService dataStreamService;
	static Socket echoSocket = null;
	static InputStream is = null;
	static PrintWriter pw = null;
	public static int msgid = 1;
	public static String PACKAGE_BARCODE = "";

	
	

	
	
	
	
	@GetMapping("/callWMSInV2")
	public String callWMSInV2(@RequestParam String packageId) throws Exception {

		String txt = "";
		try {

			// fill barcodeIn
			packageId = packageId + "                    ";
			packageId = packageId.substring(0, 20);

			String WMS_IP = PropertiesUtil.getWmsAutoIp();
			String WMS_PORT = PropertiesUtil.getWmsAutoPort();
			//
			logger.info("WMS:" + WMS_IP + ":" + WMS_PORT);
			String mac = getMacAddress();
			mac = mac.replaceAll("-", "").toUpperCase();

			msgid = msgid + 1;
			String sMsgid = String.format("%07d", msgid);
			//
			echoSocket = new Socket(WMS_IP, new Integer(WMS_PORT));
			is = echoSocket.getInputStream();
			pw = new PrintWriter(echoSocket.getOutputStream(), true);
			
			
			
			// Test Barcode
			logger.info(">> " + mac + "   PLV" + sMsgid + "**V7000053RF               SCMAN002       " + packageId + " ");
			pw.print("" + mac + "   PLV" + sMsgid + "**V7000053RF               SCMAN002       " + packageId + " ");
			pw.flush();
			Thread.sleep(500);
			echoSocket.close();

		} catch (Exception e) {
			// e.printStackTrace();
			echoSocket.close();
			logger.error("WMSAuto Socket Closed...");
		} finally {
			try {
				echoSocket.close();
			} catch (Exception e) {
			}
		}	
		return txt;
	}
	
	
	
	
	@GetMapping("/callWMSOutV2")
	public String callWMSOutV2(@RequestParam String packageId) throws Exception {

		String txt = "";
		try {

			// fill barcodeIn
			packageId = packageId + "                    ";
			packageId = packageId.substring(0, 20);

			String WMS_IP = PropertiesUtil.getWmsAutoIp();
			String WMS_PORT = PropertiesUtil.getWmsAutoPort();
			String mac = getMacAddress();
			mac = mac.replaceAll("-", "").toUpperCase();

			msgid = msgid + 1;
			String sMsgid = String.format("%07d", msgid);
			//
			logger.info("WMS:" + WMS_IP + ":" + WMS_PORT);
			//
			echoSocket = new Socket(WMS_IP, new Integer(WMS_PORT));

			is = echoSocket.getInputStream();
			pw = new PrintWriter(echoSocket.getOutputStream(), true);
			
			// Test Barcode
			logger.info(">> " + mac + "   PLV" + sMsgid + "**V7000084RD               SCMAN003       " + packageId + "00000000000000000               ");
			pw.print("" + mac + "   PLV" + sMsgid + "**V7000084RD               SCMAN003       " + packageId + "00000000000000000               ");
			pw.flush();
			Thread.sleep(500);
			echoSocket.close();

		} catch (Exception e) {
			// e.printStackTrace();
			echoSocket.close();
			logger.error("WMSAuto Socket Closed...");
		} finally {
			try {
				echoSocket.close();
			} catch (Exception e) {
			}
		}
		return txt;
	}



	public static String getMacAddress() {

		InetAddress ip;
		String macAddress = "";
		try {

			ip = InetAddress.getLocalHost();
			logger.info("Current IP address  : " + ip.getHostAddress());

			NetworkInterface network = NetworkInterface.getByInetAddress(ip);

			byte[] mac = network.getHardwareAddress();

			System.out.print("Current MAC address : ");

			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < mac.length; i++) {
				sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
			}
			logger.info(sb.toString());

			macAddress = sb.toString();

		} catch (UnknownHostException e) {

			e.printStackTrace();

		} catch (SocketException e) {

			e.printStackTrace();

		}
		return macAddress;
	}

}
