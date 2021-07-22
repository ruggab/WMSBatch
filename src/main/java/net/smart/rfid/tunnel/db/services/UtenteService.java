package net.smart.rfid.tunnel.db.services;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import net.smart.rfid.tunnel.db.entity.Funzione;
import net.smart.rfid.tunnel.db.entity.FunzioneProfilo;
import net.smart.rfid.tunnel.db.entity.ProfiloUtente;
import net.smart.rfid.tunnel.db.entity.Utente;
import net.smart.rfid.tunnel.db.repository.FunzioneProfiloRepository;
import net.smart.rfid.tunnel.db.repository.FunzioneRepository;
import net.smart.rfid.tunnel.db.repository.ProfiloUtenteRepository;
import net.smart.rfid.tunnel.db.repository.UtenteRepository;

@Service
public class UtenteService  {
	Logger logger = LoggerFactory.getLogger(UtenteService.class);

	@Autowired
	private UtenteRepository utenteRepository;
	
	@Autowired
	private FunzioneRepository funzioneRepository;
	
	@Autowired
	private ProfiloUtenteRepository profiloUtenteRepository;
	
	@Autowired
	private FunzioneProfiloRepository funzioneProfiloRepository;


	public List<Utente> getAllUsers() {
		return utenteRepository.findAll();
	}

	public Utente getUsersByUsrAndPsw(String usr, String psw) throws Exception {
		
		Utente utente =  null;
		List<Utente> userList = utenteRepository.findByUsrAndPsw(usr, psw);
		if (userList.size() > 0) {
			utente = userList.get(0);
			utente.setListFunzioni(new ArrayList<Funzione>());
			List<ProfiloUtente> listProfiloUtente = profiloUtenteRepository.findByIdUtente(utente.getId());
			for (Iterator iterator = listProfiloUtente.iterator(); iterator.hasNext();) {
				ProfiloUtente profiloUtente = (ProfiloUtente) iterator.next();
				List<FunzioneProfilo> listFunzioneProfilo = funzioneProfiloRepository.findByIdProfiloOrderByOrdine(profiloUtente.getIdProfilo());
				for (Iterator iterator2 = listFunzioneProfilo.iterator(); iterator2.hasNext();) {
					FunzioneProfilo funzioneProfilo = (FunzioneProfilo) iterator2.next();
					Funzione funzione  = funzioneRepository.findById(funzioneProfilo.getIdFunzione()).get();
					utente.getListFunzioni().add(funzione);
				}
			}
		
		} else {
			logger.error("User o password errati");
			throw new Exception("User o password errati");
		}
		logger.info("User collegato correttamente");
		return utente;
	}

}