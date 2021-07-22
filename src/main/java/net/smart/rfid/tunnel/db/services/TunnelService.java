package net.smart.rfid.tunnel.db.services;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import net.smart.rfid.tunnel.db.entity.ConfAntenna;
import net.smart.rfid.tunnel.db.entity.ConfReader;
import net.smart.rfid.tunnel.db.entity.Dispositivo;
import net.smart.rfid.tunnel.db.entity.ReaderStream;
import net.smart.rfid.tunnel.db.entity.ScannerStream;
import net.smart.rfid.tunnel.db.entity.Tipologica;
import net.smart.rfid.tunnel.db.entity.Tunnel;
import net.smart.rfid.tunnel.db.repository.ConfAntennaRepository;
import net.smart.rfid.tunnel.db.repository.ConfPortRepository;
import net.smart.rfid.tunnel.db.repository.ConfReaderRepository;
import net.smart.rfid.tunnel.db.repository.DispositivoRepository;
import net.smart.rfid.tunnel.db.repository.ReaderStreamAttesoRepository;
import net.smart.rfid.tunnel.db.repository.ReaderStreamRepository;
import net.smart.rfid.tunnel.db.repository.ScannerStreamRepository;
import net.smart.rfid.tunnel.db.repository.TipologicaRepository;
import net.smart.rfid.tunnel.db.repository.TunnelRepository;
import net.smart.rfid.tunnel.db.repository.ReaderStreamAttesoRepository.StreamBarcodeDifference;
import net.smart.rfid.tunnel.db.repository.ReaderStreamAttesoRepository.StreamEPCDifference;
import net.smart.rfid.tunnel.db.repository.ReaderStreamAttesoRepository.StreamTIDDifference;
import net.smart.rfid.tunnel.db.repository.ReaderStreamAttesoRepository.StreamUserDifference;

@Service
public class TunnelService {

	Logger logger = LoggerFactory.getLogger(TunnelService.class);
	
	@Autowired
	private DispositivoRepository dispositivoRepository;

	@Autowired
	private TipologicaRepository tipologicaRepository;

	@Autowired
	private TunnelRepository tunnelRepository;

	@Autowired
	private ConfAntennaRepository confAntennaRepository;

	@Autowired
	private ConfPortRepository confPortRepository;

	@Autowired
	private ConfReaderRepository confReaderRepository;

	@Autowired
	private ReaderStreamRepository readerStreamRepository;

	@Autowired
	private ScannerStreamRepository scannerStreamRepository;

	@Autowired
	private ReaderStreamAttesoRepository readerStreamAttesoRepository;

	

	private Hashtable<String, Runnable> mapW = new Hashtable<String, Runnable>();

	public Tunnel getTunnelById(Long id) throws Exception {
		Optional<Tunnel> tunnel = tunnelRepository.findById(id);
		Tunnel tunnelObj = tunnel.get();
		Set<Dispositivo> dispoSet = tunnelObj.getDispositivi();
		for (Iterator iterator = dispoSet.iterator(); iterator.hasNext();) {
			Dispositivo dispositivo = (Dispositivo) iterator.next();
			Tipologica tip = tipologicaRepository.getOne(dispositivo.getIdTipoDispositivo());
			dispositivo.setDescTipoDispositivo(tip.getDescrizione());
			if (dispositivo.getIdModelloReader() != null) {
				tip = tipologicaRepository.getOne(dispositivo.getIdModelloReader());
				dispositivo.setDescModelloReader(tip.getDescrizione());
			} else {
				dispositivo.setDescModelloReader("");
			}
		}

		setDescrizioniInTunnel(tunnelObj);
		return tunnelObj;
	}

	public void create(Tunnel tunnel) throws Exception {
		tunnelRepository.save(tunnel);
	}

	public List<Tunnel> getAllTunnel() throws Exception {
		//
		List<Tunnel> tunnelList = tunnelRepository.findAll();
		for (Iterator iterator1 = tunnelList.iterator(); iterator1.hasNext();) {
			Tunnel tunnel = (Tunnel) iterator1.next();
			setDescrizioniInTunnel(tunnel);
		}
		return tunnelList;
	}

	private void setDescrizioniInTunnel(Tunnel tunnel) {
		Tipologica tip = null;
		if (tunnel.getIdSceltaGestColli() != null) {
			tip = tipologicaRepository.getOne(tunnel.getIdSceltaGestColli());
			tunnel.setDescSceltaGestColli(tip.getDescrizione());
		}
		if (tunnel.getIdSceltaTipoColli() != null) {
			tip = tipologicaRepository.getOne(tunnel.getIdSceltaTipoColli());
			tunnel.setDescSceltaTipoColli(tip.getDescrizione());
		}
		if (tunnel.getIdTipoFormatoEPC() != null) {
			tip = tipologicaRepository.getOne(tunnel.getIdTipoFormatoEPC());
			tunnel.setDescTipoFormatoEPC(tip.getDescrizione());
		}
		if (tunnel.getIdTipoReaderSelected() != null) {
			tip = tipologicaRepository.getOne(tunnel.getIdTipoReaderSelected());
			tunnel.setDescTipoReaderSelected(tip.getDescrizione());
		}
		if (tunnel.getIdReaderBarcodeSelected() != null) {
			Dispositivo dispo = dispositivoRepository.getOne(tunnel.getIdReaderBarcodeSelected());
			tunnel.setDescReaderBarcodeSelected(dispo.getIpAdress());
		}
		if (tunnel.getIdReaderRfidSelected() != null) {
			Dispositivo dispo = dispositivoRepository.getOne(tunnel.getIdReaderRfidSelected());
			tunnel.setDescReaderRfidSelected(dispo.getIpAdress());
		}
	}

	public void delete(Long tunnelId) throws Exception {
		tunnelRepository.deleteById(tunnelId);
	}

	@Transactional
	public void save(Tunnel tunnel) throws Exception {

		tunnelRepository.save(tunnel);
		for (Dispositivo dispositivo : tunnel.getDispositivi()) {
			dispositivo.setIdTunnel(tunnel.getId());
			dispositivoRepository.save(dispositivo);
		}
	}

	@Transactional
	public void aggiornaDispositivo(Dispositivo dispo) throws Exception {
		dispositivoRepository.save(dispo);
	}

	@Transactional
	public void aggiornaConfReader(ConfReader confReader) throws Exception {
		confReaderRepository.save(confReader);
	}

	public List<ConfAntenna> getAllAntenna(Long readerId) throws Exception {
		List<ConfAntenna> listAntenna = confAntennaRepository.findByIdReader(readerId);
		return listAntenna;
	}



	public Integer getSeqNextVal() throws Exception {
		Integer nextVal = tunnelRepository.getSeqNextVal();
		return nextVal;
	}

	public int compareByPackage(ScannerStream scannerStream, Boolean epc, Boolean tid, Boolean user, Boolean barcode, Boolean quantita) throws Exception {
		int ret = 2;
		String comp = compareQuantitaByPackage(scannerStream.getId(), scannerStream.getPackageData(), epc, tid, user, barcode, quantita);
		String quantitaRet = comp.replace("KO", "");
		if (comp.contains("OK")) {
			ret = 1;
			quantitaRet = comp.replace("OK", "");
		}
		comp = comp.replace(quantitaRet, "");
		if (!quantita && ret == 1) {
			// Se la quantita è OK allora controllo anche il contenuto in caso di selezione
			// diversa da quantita

			if (epc) {
				comp = compareEPCByPackage(scannerStream.getId(), scannerStream.getPackageData());
			}
			if (tid) {
				comp = compareTIDByPackage(scannerStream.getId(), scannerStream.getPackageData());
			}
			if (user) {
				comp = compareUserByPackage(scannerStream.getId(), scannerStream.getPackageData());
			}
			if (barcode) {
				comp = compareBarcodeByPackage(scannerStream.getId(), scannerStream.getPackageData());
			}

		}
		if (comp.contains("OK")) {
			ret = 1;
		} else {
			ret = 2;
		}
		scannerStream.setEsito(comp);
		scannerStream.setQuantita(quantitaRet);
		scannerStreamRepository.save(scannerStream);
		return ret;
	}

	private String compareEPCByPackage(Long packId, String packageData) throws Exception {
		String ret = "OK";
		List<StreamEPCDifference> listDiffFromAttesoAndRead = readerStreamAttesoRepository.getDiffEPCExpectedRead(packId, packageData);
		if (listDiffFromAttesoAndRead.size() > 0) {
			ret = "KO - Expected > Read";
		}
		List<StreamEPCDifference> listDiffFromReadAndAtteso = readerStreamAttesoRepository.getDiffEPCReadExpected(packId, packageData);
		if (listDiffFromReadAndAtteso.size() > 0) {
			if (!StringUtils.isEmpty(ret))
				ret = ret + " AND ";
			ret = ret + " KO - Read > Expected";
		}
		return ret;
	}

	private String compareTIDByPackage(Long packId, String packageData) throws Exception {
		String ret = "OK";
		List<StreamTIDDifference> listDiffFromAttesoAndRead = readerStreamAttesoRepository.getDiffTIDExpectedRead(packId, packageData);
		if (listDiffFromAttesoAndRead.size() > 0) {
			ret = "KO - Expected > Read";
		}
		List<StreamTIDDifference> listDiffFromReadAndAtteso = readerStreamAttesoRepository.getDiffTIDReadExpected(packId, packageData);
		if (listDiffFromReadAndAtteso.size() > 0) {
			if (!StringUtils.isEmpty(ret))
				ret = ret + " AND ";
			ret = ret + " KO - Read > Expected";
		}
		return ret;
	}

	private String compareBarcodeByPackage(Long packId, String packageData) throws Exception {
		String ret = "OK";
		List<StreamBarcodeDifference> listDiffFromAttesoAndRead = readerStreamAttesoRepository.getDiffBCExpectedRead(packId, packageData);
		if (listDiffFromAttesoAndRead.size() > 0) {
			ret = "KO - Expected > Read";
		}
		List<StreamBarcodeDifference> listDiffFromReadAndAtteso = readerStreamAttesoRepository.getDiffBCReadExpected(packId, packageData);
		if (listDiffFromReadAndAtteso.size() > 0) {
			if (!StringUtils.isEmpty(ret))
				ret = ret + " AND ";
			ret = ret + " KO - Read > Expected";
		}
		return ret;
	}

	private String compareUserByPackage(Long packId, String packageData) throws Exception {
		String ret = "OK";
		List<StreamUserDifference> listDiffFromAttesoAndRead = readerStreamAttesoRepository.getDiffUSERExpectedRead(packId, packageData);
		if (listDiffFromAttesoAndRead.size() > 0) {
			ret = "KO - Expected > Read";
		}
		List<StreamUserDifference> listDiffFromReadAndAtteso = readerStreamAttesoRepository.getDiffUSERReadExpected(packId, packageData);
		if (listDiffFromReadAndAtteso.size() > 0) {
			if (!StringUtils.isEmpty(ret))
				ret = ret + " AND ";
			ret = ret + " KO - Read > Expected";
		}
		return ret;
	}

	private String compareQuantitaByPackage(Long packId, String packageData, boolean epc, boolean tid, boolean user, boolean barcode, boolean quantita) throws Exception {
		String ret = "OK";
		Integer letto = null;
		if (epc) {
			letto = readerStreamAttesoRepository.getCountDistinctEpcLetto(packId, packageData);
		}
		if (tid || quantita) {
			letto = readerStreamAttesoRepository.getCountDistinctTidLetto(packId, packageData);
		}
		if (user) {
			letto = readerStreamAttesoRepository.getCountDistinctUserLetto(packId, packageData);
		}
		if (barcode) {
			letto = readerStreamAttesoRepository.getCountDistinctBarcodeLetto(packId, packageData);
		}

		Integer atteso = readerStreamAttesoRepository.getCountExpected(packageData);
		if (letto.intValue() != atteso.intValue()) {
			ret = "KO";
		}
		return ret + letto;
	}

	public void createScannerStream(Long tunnelId, String packageData, String dettaglio) throws Exception {

		List<ScannerStream> scannerStreamList = scannerStreamRepository.getScannerNoDetail();
		if (scannerStreamList.size() > 0) {
			// Setto ERROR ai precedenti colli
			for (int i = 0; i < scannerStreamList.size(); i++) {
				ScannerStream scannerStream = scannerStreamList.get(i);
				scannerStream.setDettaglio("ERROR");
				scannerStreamRepository.save(scannerStream);
			}
		}
		ScannerStream ss = new ScannerStream();
		ss.setIdTunnel(tunnelId);
		ss.setPackageData(packageData);
		ss.setTimeStamp(new Date());
		ss.setDettaglio(dettaglio);
		scannerStreamRepository.save(ss);
	}

	public List<ScannerStream> getScannerNoDetail() throws Exception {

		return scannerStreamRepository.getScannerNoDetail();
	}

	

	public boolean isTunnelStart(Long idTunnel) throws Exception {
		Tunnel tunnel = tunnelRepository.getOne(idTunnel);

		return tunnel.isStato();
	}

	public ScannerStream saveScannerStream(ScannerStream ss) throws Exception {

		return scannerStreamRepository.save(ss);
	}

	

}