package net.smart.rfid.jobs;

import java.io.InputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.regex.Pattern;

import net.smart.rfid.tunnel.db.services.DataStreamService;
import net.smart.rfid.utils.PropertiesUtil;
import net.smart.rfid.utils.Utils;

public class WMSManual {

	static Socket echoSocket = null;
	static InputStream is = null;
	static PrintWriter pw = null;
	public static int msgid = 1;
	public static String PACKAGE_BARCODE = "";

	public static String callWMSIn(String barcodeIn, DataStreamService dataStreamService) {

		String txt = "";
		try {
			// fill barcodeIn
			barcodeIn = barcodeIn + "                    ";
			barcodeIn = barcodeIn.substring(0, 20);
			//
			String WMS_IP = PropertiesUtil.getWmsManualIp();
			String WMS_PORT = PropertiesUtil.getWmsManualPort();
			String SEPARATOR = PropertiesUtil.getWmsManualSeparator();
			String typeSkuOrEpc = PropertiesUtil.getWmsManualEpctype();
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

				System.out.println(">> " + mac + "   V02" + sMsgid + "**V7000005LIV02");
				pw.print("" + mac + "   V02" + sMsgid + "**V7000005LIV02");
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
							if (cdapp.equalsIgnoreCase("CC")) {
								PACKAGE_BARCODE = t.substring(37, 57);
								PACKAGE_BARCODE = PACKAGE_BARCODE.replaceAll("[^\\x20-\\x7e]", "");
								PACKAGE_BARCODE = PACKAGE_BARCODE + "                    ";
								PACKAGE_BARCODE = PACKAGE_BARCODE.substring(0, 20);
								System.out.println("**********\nCC " + PACKAGE_BARCODE + "\n**********");
								if (t.length() > 58) {
									String temp[] = t.substring(58, t.length()).split(Pattern.quote(SEPARATOR));
									for (int i = 0; i < temp.length; i++) {
										if (temp[i].trim().length() > 0) {
											// EPC
											if (typeSkuOrEpc == "S") {
												dataStreamService.createReaderStreamAtteso(PACKAGE_BARCODE, temp[i], "");
											} else {
												dataStreamService.createReaderStreamAtteso(PACKAGE_BARCODE, "", temp[i]);
											}
										}
									}
								}
							}
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
								System.out.println(">> " + mac + "   PLV" + sMsgid + "**V7000053RF               SCMAN002       " + barcodeIn + " ");
								pw.print("" + mac + "   PLV" + sMsgid + "**V7000053RF               SCMAN002       " + barcodeIn + " ");
								pw.flush();
								barcode = false;
							}
						}

						if (cdapp.equalsIgnoreCase("CC")) {
							System.out.println(">> " + sender + "V02" + idmsg + "**V7000004CCOK");
							pw.print(sender + "V02" + idmsg + "**V7000004CCOK");
							// String msgCC = ""+mac+" V02"+idmsg+"**V7000004CCOK";
							// System.out.println(">> "+msgCC);
							// pw.print(msgCC);
							pw.flush();
							txt = "OK";
							echoSocket.close();
						}

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

	public static String callWMSOut(String barcodeOut, DataStreamService dataStreamService) {

		String txt = "";
		try {
			// fill barcodeIn
			barcodeOut = barcodeOut + "                    ";
			barcodeOut = barcodeOut.substring(0, 20);

			String WMS_IP = PropertiesUtil.getWmsManualIp();
			String WMS_PORT = PropertiesUtil.getWmsManualPort();
			String SEPARATOR = PropertiesUtil.getWmsManualSeparator();
			String typeSkuOrEpc = PropertiesUtil.getWmsManualEpctype();
			
			System.out.println(WMS_IP + ":" + WMS_PORT);

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

				System.out.println(">> " + mac + "   V02" + sMsgid + "**V7000005LIV02");
				pw.print("" + mac + "   V02" + sMsgid + "**V7000005LIV02");
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
							if (cdapp.equalsIgnoreCase("EC")) {
								PACKAGE_BARCODE = t.substring(37, t.length());
								PACKAGE_BARCODE = PACKAGE_BARCODE.replaceAll("[^\\x20-\\x7e]", "");
								PACKAGE_BARCODE = PACKAGE_BARCODE + "                    ";
								PACKAGE_BARCODE = PACKAGE_BARCODE.substring(0, 20);
							}
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
								System.out.println(">> " + mac + "   PLV" + sMsgid + "**V7000084RD               SCMAN003       " + barcodeOut + "00000000000000000               ");
								pw.print("" + mac + "   PLV" + sMsgid + "**V7000084RD               SCMAN003       " + barcodeOut + "00000000000000000               ");
								pw.flush();
								barcode = false;
							}
						}

						if (cdapp.equalsIgnoreCase("EC")) {

							String esito = "";
							if (typeSkuOrEpc == "S") {
								esito = dataStreamService.compareByPackage(PACKAGE_BARCODE, Utils.SKU);
							} else {
								esito = dataStreamService.compareByPackage(PACKAGE_BARCODE, Utils.EPC);
							}
							System.out.println(">> ESITO >>" + esito);
							// OK
							if (esito.equals(Utils.OK)) {
								System.out.println(">> " + sender + "V02" + idmsg + "**V7000024EC" + PACKAGE_BARCODE + "OK");
								pw.print(sender + "V02" + idmsg + "**V7000024EC" + PACKAGE_BARCODE + "OK");
								pw.flush();
								// KO
							} else {
								System.out.println(">> " + sender + "V02" + idmsg + "**V7000024EC" + PACKAGE_BARCODE + "KO");
								pw.print(sender + "V02" + idmsg + "**V7000024EC" + PACKAGE_BARCODE + "KO");
								pw.flush();
							}
							echoSocket.close();
						}

						if (cdapp.equalsIgnoreCase("RD")) {
							txt = "ER";
							echoSocket.close();
						}

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
