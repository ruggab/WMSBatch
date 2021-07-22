package net.smart.rfid.tunnel.db.entity;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * @author Gabriele
 *
 */
@Entity
@Table(name = "tunnel")
public class Tunnel {

	public Tunnel() {
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	private String nome;
	private Long idSceltaGestColli;
	private Long idSceltaTipoColli;

	private Long idReaderRfidSelected;
	private Long idReaderBarcodeSelected;
	private Long idTipoFormatoEPC;
	private Long idBarcodeType;
	private String formatoEPC;
	private Long idTipoReaderSelected;
	private String msgEnd;
	private String msgNoRead;
	private String descSceltaGestColli;
	private String descSceltaTipoColli;
	private String descTipoFormatoEPC;
	private String descTipoReaderSelected;
	private String descReaderRfidSelected;
	private String descReaderBarcodeSelected;
	private boolean stato;
	
	private Long idSceltaGestAtteso;;
	private boolean attesoEpc;
	private boolean attesoUser;
	private boolean attesoTid;
	private boolean attesoBarcode;
	private boolean attesoQuantita;
	
	@ManyToMany
	private Set<Dispositivo> dispositivi;
	

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getIdSceltaGestColli() {
		return idSceltaGestColli;
	}

	public void setIdSceltaGestColli(Long idSceltaGestColli) {
		this.idSceltaGestColli = idSceltaGestColli;
	}

	public Long getIdSceltaTipoColli() {
		return idSceltaTipoColli;
	}

	public void setIdSceltaTipoColli(Long idSceltaTipoColli) {
		this.idSceltaTipoColli = idSceltaTipoColli;
	}

	public Long getIdReaderRfidSelected() {
		return idReaderRfidSelected;
	}

	public void setIdReaderRfidSelected(Long idReaderRfidSelected) {
		this.idReaderRfidSelected = idReaderRfidSelected;
	}

	public Long getIdReaderBarcodeSelected() {
		return idReaderBarcodeSelected;
	}

	public void setIdReaderBarcodeSelected(Long idReaderBarcodeSelected) {
		this.idReaderBarcodeSelected = idReaderBarcodeSelected;
	}

	public Long getIdTipoFormatoEPC() {
		return idTipoFormatoEPC;
	}

	public void setIdTipoFormatoEPC(Long idTipoFormatoEPC) {
		this.idTipoFormatoEPC = idTipoFormatoEPC;
	}

	public String getFormatoEPC() {
		return formatoEPC;
	}

	public void setFormatoEPC(String formatoEPC) {
		this.formatoEPC = formatoEPC;
	}

	public String getDescSceltaGestColli() {
		return descSceltaGestColli;
	}

	public void setDescSceltaGestColli(String descSceltaGestColli) {
		this.descSceltaGestColli = descSceltaGestColli;
	}

	public String getDescSceltaTipoColli() {
		return descSceltaTipoColli;
	}

	public void setDescSceltaTipoColli(String descSceltaTipoColli) {
		this.descSceltaTipoColli = descSceltaTipoColli;
	}

	public String getDescTipoFormatoEPC() {
		return descTipoFormatoEPC;
	}

	public void setDescTipoFormatoEPC(String descTipoFormatoEPC) {
		this.descTipoFormatoEPC = descTipoFormatoEPC;
	}

	public String getDescTipoReaderSelected() {
		return descTipoReaderSelected;
	}

	public void setDescTipoReaderSelected(String descTipoReaderSelected) {
		this.descTipoReaderSelected = descTipoReaderSelected;
	}

	public String getMsgNoRead() {
		return msgNoRead;
	}

	public void setMsgNoRead(String msgNoRead) {
		this.msgNoRead = msgNoRead;
	}

	public Long getIdTipoReaderSelected() {
		return idTipoReaderSelected;
	}

	public void setIdTipoReaderSelected(Long idTipoReaderSelected) {
		this.idTipoReaderSelected = idTipoReaderSelected;
	}

	public String getDescReaderRfidSelected() {
		return descReaderRfidSelected;
	}

	public void setDescReaderRfidSelected(String descReaderRfidSelected) {
		this.descReaderRfidSelected = descReaderRfidSelected;
	}

	public String getDescReaderBarcodeSelected() {
		return descReaderBarcodeSelected;
	}

	public void setDescReaderBarcodeSelected(String descReaderBarcodeSelected) {
		this.descReaderBarcodeSelected = descReaderBarcodeSelected;
	}

	public Set<Dispositivo> getDispositivi() {
		return dispositivi;
	}

	public void setDispositivi(Set<Dispositivo> dispositivi) {
		this.dispositivi = dispositivi;
	}

	public boolean isStato() {
		return stato;
	}

	public void setStato(boolean stato) {
		this.stato = stato;
	}

	public Long getIdBarcodeType() {
		return idBarcodeType;
	}

	public void setIdBarcodeType(Long idBarcodeType) {
		this.idBarcodeType = idBarcodeType;
	}

	public String getMsgEnd() {
		return msgEnd;
	}

	public void setMsgEnd(String msgEnd) {
		this.msgEnd = msgEnd;
	}

	public Long getIdSceltaGestAtteso() {
		return idSceltaGestAtteso;
	}

	public void setIdSceltaGestAtteso(Long idSceltaGestAtteso) {
		this.idSceltaGestAtteso = idSceltaGestAtteso;
	}

	public boolean isAttesoEpc() {
		return attesoEpc;
	}

	public void setAttesoEpc(boolean attesoEpc) {
		this.attesoEpc = attesoEpc;
	}

	public boolean isAttesoUser() {
		return attesoUser;
	}

	public void setAttesoUser(boolean attesoUser) {
		this.attesoUser = attesoUser;
	}

	public boolean isAttesoTid() {
		return attesoTid;
	}

	public void setAttesoTid(boolean attesoTid) {
		this.attesoTid = attesoTid;
	}

	public boolean isAttesoBarcode() {
		return attesoBarcode;
	}

	public void setAttesoBarcode(boolean attesoBarcode) {
		this.attesoBarcode = attesoBarcode;
	}

	public boolean isAttesoQuantita() {
		return attesoQuantita;
	}

	public void setAttesoQuantita(boolean attesoQuantita) {
		this.attesoQuantita = attesoQuantita;
	}

	

	
	
	
}
