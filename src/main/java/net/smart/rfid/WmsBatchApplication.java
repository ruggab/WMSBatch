package net.smart.rfid;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class WmsBatchApplication extends SpringBootServletInitializer {
	
	/*Questo batch è sempre in ascolto dei messaggi che arrivano dal barcode del WMS Morato per il tunnel automatico
	 *dopo aver effettuato la login il barcode manda viene inviato con un messaggio di tipo CC in tal caso il batch memorizza l'atteso
	 *se il messaggio è di tipo EC sarà inviato l'esito */
   
	public static void main(String[] args) {
		SpringApplication.run(WmsBatchApplication.class, args);
		
	}
	
	

} 
