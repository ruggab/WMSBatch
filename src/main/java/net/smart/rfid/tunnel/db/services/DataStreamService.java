package net.smart.rfid.tunnel.db.services;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import net.smart.rfid.tunnel.db.entity.ReaderStream;
import net.smart.rfid.tunnel.db.entity.ReaderStreamAtteso;
import net.smart.rfid.tunnel.db.entity.ScannerStream;
import net.smart.rfid.tunnel.db.repository.ReaderStreamAttesoRepository;
import net.smart.rfid.tunnel.db.repository.ReaderStreamRepository;
import net.smart.rfid.tunnel.db.repository.ScannerStreamRepository;
import net.smart.rfid.tunnel.db.repository.ReaderStreamAttesoRepository.StreamBarcodeDifference;
import net.smart.rfid.tunnel.db.repository.ReaderStreamAttesoRepository.StreamEPCDifference;
import net.smart.rfid.tunnel.db.repository.ReaderStreamAttesoRepository.StreamTIDDifference;
import net.smart.rfid.tunnel.db.repository.ReaderStreamAttesoRepository.StreamUserDifference;
import net.smart.rfid.tunnel.db.repository.ReaderStreamRepository.ReaderStreamOnly;

@Service
public class DataStreamService  {

	Logger logger = LoggerFactory.getLogger(DataStreamService.class);
	

	@Autowired
	private ReaderStreamRepository readerStreamRepository;

	@Autowired
	private ScannerStreamRepository scannerStreamRepository;

	@Autowired
	private ReaderStreamAttesoRepository readerStreamAttesoRepository;

	
	public ScannerStream getLastScanner() throws Exception {
		ScannerStream scannerStream = scannerStreamRepository.getLastScanner();
		return scannerStream;
	}

	


	public void createScannerStream(Long tunnelId, String packageData, String dettaglio) throws Exception {
		ScannerStream ss = new ScannerStream();
		ss.setIdTunnel(tunnelId);
		ss.setPackageData(packageData);
		ss.setTimeStamp(new Date());
		ss.setDettaglio(dettaglio);
		scannerStreamRepository.save(ss);
	}

	public void saveScannerStream(ScannerStream ss) throws Exception {

		scannerStreamRepository.save(ss);
	}

	public ReaderStreamAtteso createReaderStreamAtteso(String collo, String epc, String tid) throws Exception {
		ReaderStreamAtteso rsa = new ReaderStreamAtteso();
		rsa.setPackageData(collo);
		rsa.setEpc(epc);
		rsa.setTid(tid);
		ReaderStreamAtteso readerStreamAtteso = readerStreamAttesoRepository.save(rsa);
		return readerStreamAtteso;
	}

	public List<ReaderStreamAtteso> getReaderStreamAtteso(String collo) throws Exception {

		List<ReaderStreamAtteso> listStreamAtteso = readerStreamAttesoRepository.getReaderStreamExpectedByCollo(collo);
		return listStreamAtteso;
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
		if (tid||quantita) {
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

	public List<ReaderStream> getAllReaderStream() throws Exception {
		//
		List<ReaderStream> listReaderStream = readerStreamRepository.findAll(Sort.by(Sort.Direction.DESC, "timeStamp"));
		return listReaderStream;
	}
	
	
	public List<ReaderStreamOnly> getAllDistinctReaderStream() throws Exception {
		//
		List<ReaderStreamOnly> listReaderStream = readerStreamRepository.getReaderStreamDistinctList();
		return listReaderStream;
	}
	
	
	public List<ReaderStreamOnly> getDistinctReaderStreamByPackage(String packId) throws Exception {
		//
		List<ReaderStreamOnly> listReaderStream = readerStreamRepository.getReaderStreamDistinctByPackId(packId);
		return listReaderStream;
	}

	public List<ScannerStream> getAllScannerStream() throws Exception {
		//
		List<ScannerStream> listScannerStream = scannerStreamRepository.findAll(Sort.by(Sort.Direction.DESC, "timeStamp"));
		return listScannerStream;
	}
	
	@Transactional
	public void deleteAllData() throws Exception {
		//
		readerStreamRepository.deleteAll();
		scannerStreamRepository.deleteAll();
		
	}
	

	public void enableTrigger() throws Exception {
		scannerStreamRepository.enableTrigger();
		logger.info("Trigger ENABLED");
	}
	
	
	public void disableTrigger() throws Exception {
		scannerStreamRepository.disableTrigger();
		logger.info("Trigger DISABLED");
	}
	
	public Integer getTotalPackageReadDay() throws Exception {
		return scannerStreamRepository.getTotalPackageReadDay();
	}
	
	public Integer getTotalPackageKoDay() throws Exception {
		return scannerStreamRepository.getTotalPackageKoDay();
	}
  
	
	public Integer getTotalPackageReadLastWeek() throws Exception {
		return scannerStreamRepository.getTotalPackageReadLastWeek();
	}
  
	public Integer getTotalPackageKoLastWeek() throws Exception {
		return scannerStreamRepository.getTotalPackageKoLastWeek();
	}
		
	
	public Integer getTotalPackageReadLastMonth() throws Exception {
		return scannerStreamRepository.getTotalPackageReadLastMonth();
	}
  
	public Integer getTotalPackageKoLastMonth() throws Exception {
		return scannerStreamRepository.getTotalPackageKoLastMonth();
	}
	

	
}