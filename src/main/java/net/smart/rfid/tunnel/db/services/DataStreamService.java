package net.smart.rfid.tunnel.db.services;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.smart.rfid.tunnel.db.entity.ReaderStreamAtteso;
import net.smart.rfid.tunnel.db.entity.ScannerStream;
import net.smart.rfid.tunnel.db.repository.ReaderStreamAttesoRepository;
import net.smart.rfid.tunnel.db.repository.ReaderStreamAttesoRepository.ExpectedDifference;
import net.smart.rfid.tunnel.db.repository.ScannerStreamRepository;
import net.smart.rfid.utils.Utils;

@Service
public class DataStreamService  {

	Logger logger = LoggerFactory.getLogger(DataStreamService.class);
	

	@Autowired
	private ReaderStreamAttesoRepository readerStreamAttesoRepository;

	@Autowired
	private ScannerStreamRepository scannerStreamRepository;

	public ReaderStreamAtteso createSinglePackExpected(String packageData, String epc, String tid, String sku) throws Exception {
		ReaderStreamAtteso rsa = new ReaderStreamAtteso();
		rsa.setPackageData(Utils.removeSpaces(packageData));
		rsa.setEpc(Utils.removeSpaces(epc));
		rsa.setTid(Utils.removeSpaces(tid));
		rsa.setBarcode(Utils.removeSpaces(sku));
		rsa.setDataInsert(new Date());
		ReaderStreamAtteso readerStreamAtteso = readerStreamAttesoRepository.save(rsa);
		return readerStreamAtteso;
	}


	
	public String compareByPackage( String packageData, String typeExp) throws Exception {
		packageData = packageData.trim();
		logger.info("compareByPackage:" +  packageData);
		ScannerStream sc = scannerStreamRepository.findByPackageData(packageData);
		logger.info("idpack:" +  sc.getId());
		Integer qtaAtteso = readerStreamAttesoRepository.getCountExpected(packageData);
		logger.info("quantita atteso:" + qtaAtteso);
		String esito = Utils.MISSING_EXPECTED;
		if (qtaAtteso != 0 && !packageData.contains("E-")) {
			List<ExpectedDifference> listDiff = readerStreamAttesoRepository.getEsitoDiffNew(sc.getId().intValue(), packageData, typeExp);
			esito = Utils.OK;
			if (listDiff.size() > 0) {
				esito = Utils.KO;
			}
		}
		return esito;
	}
	
	
	@Transactional
	public void deleteExpectedByPackage(String packageData) throws Exception {
		//
		readerStreamAttesoRepository.deleteByPackageData(packageData.trim());
	}
	
	
	
	
}