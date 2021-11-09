package net.smart.rfid.jobs;

import java.io.InputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;
import net.smart.rfid.tunnel.db.services.DataStreamService;
import net.smart.rfid.utils.PropertiesUtil;
import net.smart.rfid.utils.Utils;

@Component
public class WMSAuto implements Runnable {

	static boolean running = false;

	static Socket echoSocket = null;
	static InputStream is = null;
	static PrintWriter pw = null;
	public static int msgid = 1;
	public static int msgid2 = 2000000;

	public static String PACKAGE_BARCODE = "";

	Timer timer = new Timer();

	public static String type = "";

	DataStreamService dataStreamService;

	public WMSAuto(DataStreamService dataStreamService) {
		this.dataStreamService = dataStreamService;
	}

	// @Override
	public void run() {

		running = true;
		try {
			String WMS_IP = PropertiesUtil.getWmsAutoIp();
			String WMS_PORT = PropertiesUtil.getWmsAutoPort();
			String SEPARATOR = PropertiesUtil.getWmsAutoSeparator();
			String typeSkuOrEpc = PropertiesUtil.getWmsAutoEpctype();
			System.out.println(WMS_IP + ":" + WMS_PORT);
			echoSocket = new Socket(WMS_IP, new Integer(WMS_PORT));
			is = echoSocket.getInputStream();

			byte[] buffer = new byte[2536];
			pw = new PrintWriter(echoSocket.getOutputStream(), true);

			// pw.flush();
			boolean login = true;
			int read;

			String mac = getMacAddress();
			mac = mac.replaceAll("-", "").toUpperCase();

			if (login) {
				pw.print("" + mac + "   V020000001**V7000005LIV01");
				pw.flush();
				login = false;
			}
			//
			timer.schedule(new KeepAlive(pw, mac), 0, 5000);

			String sender = "";
			String idmsg = "";
			String length = "";
			String msg = "";
			String cdapp = "";

			type = "";

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

							if (cdapp.equalsIgnoreCase("CC")) {

								PACKAGE_BARCODE = t.substring(37, 57);
								PACKAGE_BARCODE = PACKAGE_BARCODE.replaceAll("[^\\x20-\\x7e]", "");
								PACKAGE_BARCODE = PACKAGE_BARCODE + "                    ";
								PACKAGE_BARCODE = PACKAGE_BARCODE.substring(0, 20);
								System.out.println("**********\nCC " + PACKAGE_BARCODE + "\n**********");
								typeSkuOrEpc = t.substring(57, 58);

								// caricamento atteso

								// int idpack = CheckBox.pack.insert(CheckBox.conn);

								// CheckBox.scan.setIdpack(idpack);
								// CheckBox.scan.insert(CheckBox.conn);

								if (t.length() > 58) {
									String temp[] = t.substring(58, t.length()).split(Pattern.quote(SEPARATOR));
									for (int i = 0; i < temp.length; i++) {
										if (temp[i].trim().length() > 0) {
											// EPC
											if (typeSkuOrEpc == "S") {
												this.dataStreamService.createReaderStreamAtteso(PACKAGE_BARCODE, temp[i], "");
											} else {
												this.dataStreamService.createReaderStreamAtteso(PACKAGE_BARCODE, "", temp[i]);
											}
										}
									}
								}
							} else if (cdapp.equalsIgnoreCase("EC")) {
								PACKAGE_BARCODE = t.substring(37, 56);
								PACKAGE_BARCODE = PACKAGE_BARCODE.replaceAll("[^\\x20-\\x7e]", "");
								PACKAGE_BARCODE = PACKAGE_BARCODE + "                    ";
								PACKAGE_BARCODE = PACKAGE_BARCODE.substring(0, 20);
							}
							t = "";
						}

						if (cdapp.equalsIgnoreCase("OK")) {
							System.out.println(" - Note: Login OK");
						}

						if (cdapp.equalsIgnoreCase("CC")) {
							System.out.println(">> " + sender + "V01" + idmsg + "**V7000004CCOK");
							pw.print(sender + "V01" + idmsg + "**V7000004CCOK");
							pw.flush();
						}

						if (cdapp.equalsIgnoreCase("EC")) {
							String esito = "";
							if (typeSkuOrEpc == "S") {
								esito = this.dataStreamService.compareByPackage(PACKAGE_BARCODE, Utils.SKU);
							} else {
								esito = this.dataStreamService.compareByPackage(PACKAGE_BARCODE, Utils.EPC);
							}
							System.out.println(">> ESITO >>" + esito);
							// OK
							if (esito.equals(Utils.OK)) {
								System.out.println(">> " + sender + "V01" + idmsg + "**V7000024EC" + PACKAGE_BARCODE + "OK");
								pw.print(sender + "V01" + idmsg + "**V7000024EC" + PACKAGE_BARCODE + "OK");
								pw.flush();
								// KO
							} else {
								System.out.println(">> " + sender + "V01" + idmsg + "**V7000024EC" + PACKAGE_BARCODE + "KO");
								pw.print(sender + "V01" + idmsg + "**V7000024EC" + PACKAGE_BARCODE + "KO");
								pw.flush();
							}
						}

					}

				} catch (Exception e) {
					e.printStackTrace();
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
			running = false;
		} finally {
			running = false;
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

	class KeepAlive extends TimerTask {

		private PrintWriter pw;
		private String mac;

		public KeepAlive(PrintWriter pw, String mac) {
			this.pw = pw;
			this.mac = mac;
		}

		public void run() {

			if (msgid == 9999999)
				msgid = 0;
			msgid = msgid + 1;

			String sMsgid = String.format("%07d", msgid);

			// Keep Alive (Every 60 sec)
			System.out.println(">> " + mac + "   V02" + sMsgid + "**V7000002AK");

			String msgAck = "" + mac + "   V02" + sMsgid + "**V7000002AK";

			// new Thread(new CallNotify("PING","L")).start();

			pw.print(msgAck);
			pw.flush();

		}
	}

}
