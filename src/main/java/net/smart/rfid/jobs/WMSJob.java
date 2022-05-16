package net.smart.rfid.jobs;

import java.io.IOException;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import net.smart.rfid.tunnel.db.services.DataStreamService;
import net.smart.rfid.utils.PropertiesUtil;
import net.smart.rfid.utils.Utils;
import net.smart.rfid.utils.WebSocketToClient;

@Component
public class WMSJob extends GenericJob {

	private static final Logger logger = LoggerFactory.getLogger(WMSJob.class);

	static boolean running = false;

	static Socket echoSocket = null;
	static InputStream is = null;
	static PrintWriter pw = null;
	public static int msgid = 1;
	public static int cont1 = 1;
	public static int cont2 = 1;

	static Timer timer = new Timer();
	static TimerTask keepAlive1 = null;
	static TimerTask keepAlive2 = null;

	public static String type = "";
	DataStreamService dataStreamService;

	public WMSJob(DataStreamService dataStreamService) {
		this.dataStreamService = dataStreamService;
	}

	// @Override
	public void run() {
		logger.info("Start RUN");
		String PACKAGE_BARCODE = "";
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

			int read;

			String mac = getMacAddress();
			mac = mac.replaceAll("-", "").toUpperCase();

			// LOGIN TO AUTO
			logger.info("login V01>>" + mac + "   V010000001**V7000005LIV01");
			pw.print("" + mac + "   V010000001**V7000005LIV01");
			pw.flush();
			//
			// LOGIN TO MANUAL
			logger.info("login V02>>" + mac + "   V020000001**V7000005LIV02");
			pw.print("" + mac + "   V020000001**V7000005LIV02");
			pw.flush();
			//

			//
			keepAlive1 = new KeepAlive1(pw, mac);
			keepAlive2 = new KeepAlive2(pw, mac);
			timer.schedule(keepAlive1, 0, 5000);
			timer.schedule(keepAlive2, 0, 5000);
			running = true;
			String sender = "";
			String idmsg = "";
			String gate = "";
			String length = "";
			String msg = "";
			String cdapp = "";

			type = "";

			while ((read = is.read(buffer)) != -1 && running) {
				try {

					String output = new String(buffer, 0, read);
					logger.info("WMS RESP BUFF:<< " + output + " (" + output.length() + ")");
					String in[] = output.split("\\n");
					for (String t : in) {

						if (t.length() >= 37) {

							cdapp = t.substring(35, 37);
							logger.debug("cdapp:<< " + cdapp);
							sender = t.substring(0, 15);
							logger.debug("sender:<< " + sender);
							gate = t.substring(15, 18);
							logger.debug("gate:<< " + gate);

							idmsg = t.substring(18, 25);
							logger.debug("idmsg:<< " + idmsg);
							length = t.substring(30, 35);
							logger.debug("length<< " + length);
							msg = t.substring(35, (Integer.parseInt(length) + 35));
							logger.debug("msg:<< " + msg);

							if (cdapp.equalsIgnoreCase("CC")) {
								logger.info(">> cdapp >>" + cdapp);
								PACKAGE_BARCODE = t.substring(37, 57);
								PACKAGE_BARCODE = PACKAGE_BARCODE.replaceAll("[^\\x20-\\x7e]", "");
								PACKAGE_BARCODE = PACKAGE_BARCODE + "                    ";
								PACKAGE_BARCODE = PACKAGE_BARCODE.substring(0, 20);
								logger.info("**********CC " + PACKAGE_BARCODE + "**********");
								typeSkuOrEpc = t.substring(57, 58);
								dataStreamService.deleteExpectedByPackage(PACKAGE_BARCODE);
								if (t.length() > 58) {
									String temp[] = t.substring(58, t.length()).split(Pattern.quote(SEPARATOR));
									for (int i = 0; i < temp.length; i++) {
										if (temp[i].trim().length() > 0) {
											if (typeSkuOrEpc.equals("S")) {
												// SKU
												logger.info("**********SKU: " + temp[i] + "**********");
												dataStreamService.createSinglePackExpected(PACKAGE_BARCODE.trim(), "", "", temp[i]);
											} else {
												logger.info("**********EPC: " + temp[i] + "**********");
												dataStreamService.createSinglePackExpected(PACKAGE_BARCODE.trim(), temp[i], "", "");
											}
										}
									}
								}
							} else if (cdapp.equalsIgnoreCase("EC")) {
								logger.info(">> cdapp >>" + cdapp);
								logger.info(">> t >>" + t);
								PACKAGE_BARCODE = t.substring(37, t.length());
								PACKAGE_BARCODE = PACKAGE_BARCODE.replaceAll("[^\\x20-\\x7e]", "");
								PACKAGE_BARCODE = PACKAGE_BARCODE + "                    ";
								PACKAGE_BARCODE = PACKAGE_BARCODE.substring(0, 20);
							}
							t = "";
						}

						if (cdapp.equalsIgnoreCase("OK")) {
							logger.debug("PING RESP: OK");
							logger.info("cont1,cont2: " +  cont1 +" - "+ cont2);
							if (gate.equals("V01")) {
								cont1 = cont1 - 1;
							}
							if (gate.equals("V02")) {
								cont2 = cont2 - 1;
							}
						}

						if (cdapp.equalsIgnoreCase("CC")) {
							logger.info(">> " + sender + gate + idmsg + "**V7000004CCOK");
							pw.print(sender + gate + idmsg + "**V7000004CCOK");
							pw.flush();
						}

						if (cdapp.equalsIgnoreCase("EC")) {
							logger.info(">> cdapp >>" + cdapp);
							String esito = "";
							if (typeSkuOrEpc.equalsIgnoreCase("S")) {
								esito = this.dataStreamService.compareByPackage(PACKAGE_BARCODE, Utils.SKU);
							} else {
								esito = this.dataStreamService.compareByPackage(PACKAGE_BARCODE, Utils.EPC);
							}
							logger.info(">> ESITO >>" + esito);
							// OK
							if (esito.equals(Utils.OK)) {
								logger.info(">> " + sender + gate + idmsg + "**V7000024EC" + PACKAGE_BARCODE + "OK");
								pw.print(sender + gate + idmsg + "**V7000024EC" + PACKAGE_BARCODE + "OK");
								pw.flush();
								// KO
							} else {
								logger.info(">> " + sender + gate + idmsg + "**V7000024EC" + PACKAGE_BARCODE + "KO");
								pw.print(sender + gate + idmsg + "**V7000024EC" + PACKAGE_BARCODE + "KO");
								pw.flush();
							}
						}
					}

				} catch (Exception e) {
					logger.error("**** Error on cicle: " + e.toString() + " " + e.getMessage());
				}
			}

		} catch (Exception e) {
			logger.error("***** ERROR: " + e.toString() + " " + e.getMessage());
		} finally {
			running = false;
			try {
				stop();
				logger.info("Close socket WMS");
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("***** ERROR on Close socket: " + e.toString() + " " + e.getMessage());
			}
		}
	}

	public static String getMacAddress() {

		InetAddress ip;
		String macAddress = "";
		try {

			ip = InetAddress.getLocalHost();
			logger.debug("Current IP address  : " + ip.getHostAddress());
			NetworkInterface network = NetworkInterface.getByInetAddress(ip);
			byte[] mac = network.getHardwareAddress();
			logger.debug("Current MAC address : ");
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < mac.length; i++) {
				sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
			}
			logger.debug(sb.toString());
			macAddress = sb.toString();

		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (SocketException e) {
			e.printStackTrace();
		}
		return macAddress;
	}

	
	
	class KeepAlive1 extends TimerTask {

		private PrintWriter pw;
		private String mac;
		public KeepAlive1(PrintWriter pw, String mac) {
			this.pw = pw;
			this.mac = mac;
		}
		
		public void run()  {
			if (msgid == 9999999) {
				msgid = 0;
			}
			msgid = msgid + 1;
			String sMsgid = String.format("%07d", msgid);
			cont1 = cont1 +1;
			logger.info("cont1:" + cont1);
			String msgAck = "" + mac + "   V01" + sMsgid + "**V7000002AK";
			pw.print(msgAck);
			pw.flush();
			if (cont1 < 3) {
				//message to websocket
				String msgAck1 = "GATE1-" + msgid;
				WebSocketToClient.sendMessageOnPackageReadEvent(msgAck1);
			} else {
				try {
					stop();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			//
			

		}
	}


	class KeepAlive2 extends TimerTask {
		private PrintWriter pw;
		private String mac;
		public KeepAlive2(PrintWriter pw, String mac) {
			this.pw = pw;
			this.mac = mac;
		}
		public void run() {
			if (msgid == 9999999) {
				msgid = 0;
			}
			msgid = msgid + 1;
			String sMsgid = String.format("%07d", msgid);
			String msgAck = "" + mac + "   V02" + sMsgid + "**V7000002AK";
			cont2 = cont2 +1;
			logger.info("cont2:" + cont2);
			pw.print(msgAck);
			pw.flush();
			//
			if (cont1 < 3) {
				//message to websocket
				String msgAck2 = "GATE2-" + msgid;
				WebSocketToClient.sendMessageOnPackageReadEvent(msgAck2);
			} else {
				try {
					stop();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
	}
	
	@Override
	public void stop() throws Exception {
		logger.info("STOP WMS");
		running = false;
		try {
			if (timer != null) {
				//timer.cancel();
				timer.purge();
				keepAlive1.cancel();
				keepAlive2.cancel();
			}
			if (echoSocket != null) {
				echoSocket.close();
			}
			
		} catch (Exception e) {
			logger.error("stop: " + e.toString() + " - " + e.getMessage());
		} finally {
			WebSocketToClient.sendMessageOnPackageReadEvent("STOP");
		}
		logger.info("stop wms from service");
	}

}
