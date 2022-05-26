package net.smart.rfid.utils;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class Utils {
	
	public static final String MISSING_EXPECTED = "E-MISSING-EXPECTED";
	public static final String MISSING_PACKAGE = "E-MISSING-PACKAGE";
	public static final String MORE_PACKAGE = "E-MORE-PACKAGE";
	public static final String TAG_NO_READ = "E-TAG-NO-READ";
	public static final String OK = "OK";
	public static final String KO = "KO";
	public static final String EPC = "EPC";
	public static final String TID = "TID";
	public static final String SKU = "SKU";
	public static final String QTY = "QTY";
	public static final String TAG_ERR = "E-";

	
	public static String removeSpaces(String st) {
		String ret = st.replaceAll("\\s+","");;
		return ret;
	}
	

	public static String getMacAddress() throws SocketException {
	    String firstInterface = null;        
	    Map<String, String> addressByNetwork = new HashMap<String, String>();
	    Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();

	    while(networkInterfaces.hasMoreElements()){
	        NetworkInterface network = networkInterfaces.nextElement();

	        byte[] bmac = network.getHardwareAddress();
	        if(bmac != null){
	            StringBuilder sb = new StringBuilder();
	            for (int i = 0; i < bmac.length; i++){
	                sb.append(String.format("%02X%s", bmac[i], (i < bmac.length - 1) ? "-" : ""));        
	            }

	            if(sb.toString().isEmpty()==false){
	                addressByNetwork.put(network.getName(), sb.toString());
	                //System.out.println("Address = "+sb.toString()+" @ ["+network.getName()+"] "+network.getDisplayName());
	            }

	            if(sb.toString().isEmpty()==false && firstInterface == null){
	                firstInterface = network.getName();
	            }
	        }
	    }

	    if(firstInterface != null){
	        return addressByNetwork.get(firstInterface);
	    }

	    return null;
	}

	

}
