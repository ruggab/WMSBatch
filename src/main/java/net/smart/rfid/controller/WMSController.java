package net.smart.rfid.controller;

import java.io.InputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import net.smart.rfid.tunnel.db.services.DataStreamService;
import net.smart.rfid.utils.PropertiesUtil;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "http://localhost:4200")
public class WMSController {

	@Autowired
	DataStreamService dataStreamService;
	static Socket echoSocket = null;
	static InputStream is = null;
	static PrintWriter pw = null;
	public static int msgid = 1;
	public static String PACKAGE_BARCODE = "";
	
	@GetMapping("/callWMSIn")
	public String callWMSIn(@RequestParam String packageId) throws Exception {
		
		String txt = "";
		try {

			

			// fill barcodeIn
			packageId = packageId + "                    ";
			packageId = packageId.substring(0, 20);
			
			String WMS_IP = PropertiesUtil.getWmsAutoIp();
			String WMS_PORT = PropertiesUtil.getWmsAutoPort();
			String SEPARATOR = PropertiesUtil.getWmsAutoSeparator();
			String typeSkuOrEpc = PropertiesUtil.getWmsAutoEpctype();
			//
			System.out.println(WMS_IP + ":" + WMS_PORT);
			//
			echoSocket = new Socket(WMS_IP, new Integer(WMS_PORT));

			is = echoSocket.getInputStream();

			byte[] buffer = new byte[2536];
			pw = new PrintWriter(echoSocket.getOutputStream(), true);

			boolean login = true;
			boolean barcode = true;
			int read;

			String mac = getMacAddress();
			mac = mac.replaceAll("-", "").toUpperCase();

			if (login) {
				msgid = msgid + 1;
				String sMsgid = String.format("%07d", msgid);
				System.out.println(">> " + mac + "   V01" + sMsgid + "**V7000005LIV01");
				pw.print("" + mac + "   V01" + sMsgid + "**V7000005LIV01");
				pw.flush();
				login = false;
			}

			String sender = "";
			String idmsg = "";
			String length = "";
			String msg = "";
			String cdapp = "";

			while ((read = is.read(buffer)) != -1) {

				try {

					String output = new String(buffer, 0, read);
					System.out.println("<< " + output + " (" + output.length() + ")");

					String in[] = output.split("\\n");
					for (String t : in) {

						if (t.length() >= 37) {

							cdapp = t.substring(35, 37);
							sender = t.substring(0, 15);
							idmsg = t.substring(18, 25);
							length = t.substring(30, 35);
							msg = t.substring(35, (Integer.parseInt(length) + 35));
							
							t = "";

						}

						if (cdapp.equalsIgnoreCase("OK")) {
							System.out.println(" - Note: Login OK");
							// Thread.sleep(500);
							if (barcode) {
								msgid = msgid + 1;
								String sMsgid = String.format("%07d", msgid);
								Thread.sleep(500);
								// Test Barcode
								System.out.println(">> " + mac + "   PLV" + sMsgid + "**V7000053RF               SCMAN002       " + packageId + " ");
								pw.print("" + mac + "   PLV" + sMsgid + "**V7000053RF               SCMAN002       " + packageId + " ");
								pw.flush();
								barcode = false;
							}
						}

//						if (cdapp.equalsIgnoreCase("CC")) {
//							System.out.println(">> " + sender + "V01" + idmsg + "**V7000004CCOK");
//							pw.print(sender + "V01" + idmsg + "**V7000004CCOK");
//							// String msgCC = ""+mac+" V01"+idmsg+"**V7000004CCOK";
//							// System.out.println(">> "+msgCC);
//							// pw.print(msgCC);
//							pw.flush();
//							txt = "OK";
//							echoSocket.close();
//						}

						if (cdapp.equalsIgnoreCase("ER")) {
							txt = "ER";
							echoSocket.close();
						}
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		} catch (Exception e) {
			// e.printStackTrace();
			System.err.println("WMSAuto Socket Closed...");
		} finally {
			try {
				echoSocket.close();
			} catch (Exception e) {
			}
		}
		return txt;
	}

	@PostMapping("/callWMSOut")
	public String callWMSOut(@RequestBody String packageId) throws Exception {
		try {
			//WMSTest.extPackage = packageId;
			return "ok";
		} catch (Exception e) {
			throw e;
		}
	}

	public static String getMacAddress() {

		InetAddress ip;
		String macAddress = "";
		try {

			ip = InetAddress.getLocalHost();
			System.out.println("Current IP address  : " + ip.getHostAddress());

			NetworkInterface network = NetworkInterface.getByInetAddress(ip);

			byte[] mac = network.getHardwareAddress();

			System.out.print("Current MAC address : ");

			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < mac.length; i++) {
				sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
			}
			System.out.println(sb.toString());

			macAddress = sb.toString();

		} catch (UnknownHostException e) {

			e.printStackTrace();

		} catch (SocketException e) {

			e.printStackTrace();

		}
		return macAddress;
	}

}
