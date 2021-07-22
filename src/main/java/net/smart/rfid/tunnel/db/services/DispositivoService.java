package net.smart.rfid.tunnel.db.services;

import java.io.Reader;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import net.smart.rfid.tunnel.db.entity.Dispositivo;
import net.smart.rfid.tunnel.db.entity.Tipologica;
import net.smart.rfid.tunnel.db.entity.Tunnel;
import net.smart.rfid.tunnel.db.repository.DispositivoRepository;
import net.smart.rfid.tunnel.db.repository.TipologicaRepository;
import net.smart.rfid.tunnel.db.repository.TunnelRepository;

@Service
public class DispositivoService  {

	@Autowired
	private DispositivoRepository dispositivoRepository;
	
	
	@Autowired
	private TunnelRepository tunnelRepository;
	
	

	@Autowired
	private TipologicaRepository tipologicaRepository;

	public Dispositivo getDispositivoById(Long dispositivoId) throws Exception {
		Optional<Dispositivo> dispositivo = dispositivoRepository.findById(dispositivoId);
		Dispositivo dispositivoObj = dispositivo.get();
		return dispositivoObj;
	}

	public void createDispositivo(Dispositivo dispositivo) throws Exception {
		Reader rr = null;
		// Controllo se esiste un dispositivo gia presente con lo stesso ipadress
		List<Dispositivo> list = dispositivoRepository.findByIpAdress(dispositivo.getIpAdress());
		if (list.size() > 0) {
			throw new Exception("Attenzione IP già in uso per altro Dispositivo");
		}
		//
		// Salva dispositivo
		dispositivoRepository.save(dispositivo);
	}

	public List<Dispositivo> getAllDispositivi() throws Exception {
		//
		List<Dispositivo> dispositivoList = dispositivoRepository.findAll(Sort.by(Sort.Direction.ASC, "ipAdress"));
		for (Iterator iterator = dispositivoList.iterator(); iterator.hasNext();) {
			Dispositivo dispositivo = (Dispositivo) iterator.next();
			List<Tunnel> tunnel = tunnelRepository.getTunnelFromDisp(dispositivo.getId());
			Tipologica tip = tipologicaRepository.getOne(dispositivo.getIdTipoDispositivo());
			dispositivo.setDescTipoDispositivo(tip.getDescrizione());
			if (dispositivo.getIdModelloReader() != null) {
				tip = tipologicaRepository.getOne(dispositivo.getIdModelloReader());
				dispositivo.setDescModelloReader(tip.getDescrizione());
			} else {
				dispositivo.setDescModelloReader("");
			}
		}
		return dispositivoList;
	}
	
	public List<Dispositivo> findAllDispositivi() throws Exception {
		List<Dispositivo> dispositivoList = dispositivoRepository.findAllDispositivi();
		return dispositivoList;
	}

	public void delete(Long dispoId) throws Exception {
		dispositivoRepository.deleteById(dispoId);
	}

	@Transactional
	public void save(Dispositivo dispositivo) throws Exception {
		dispositivoRepository.save(dispositivo);
	}

	

	public List<Dispositivo> getReaderRfidList() throws Exception {
		//
		List<Dispositivo> dispositivoList = dispositivoRepository.findByIdTipoDispositivo(new Long(1));
		return dispositivoList;
	}

	public List<Dispositivo> getReaderBarcodeList() throws Exception {
		//
		List<Dispositivo> dispositivoList = dispositivoRepository.findByIdTipoDispositivo(new Long(2));
		return dispositivoList;
	}
	
	public boolean isDeviceStart(Long idDevice) throws Exception {
		Dispositivo dispositivo = dispositivoRepository.getOne(idDevice);

		return dispositivo.isStato();
	}
	
	
}