package net.smart.rfid.tunnel.db.repository;

import java.math.BigInteger;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import net.smart.rfid.tunnel.db.entity.ReaderStreamAtteso;

@Repository
public interface ReaderStreamAttesoRepository extends JpaRepository<ReaderStreamAtteso, Long> {

	@Query(value = "select s.id, s.packId, epc,tid from reader_stream_atteso s where s.packId = ?1", nativeQuery = true)
	List<ReaderStreamAtteso> getReaderStreamExpectedByPackage(String packageData);
	
	public void deleteByPackageData(String packageData);
	
	
	//QUANTITA
	@Query(value = "select count (distinct (s.epc)) from reader_stream s where s.pack_id = :packId and s.package_data = :packageData ", nativeQuery = true)
	Integer getCountDistinctEpcLetto(@Param ("packId") Long packId, @Param ("packageData") String packageData);

	//QUANTITA
	@Query(value = "select count (distinct (s.tid)) from reader_stream s where s.pack_id = :packId and s.package_data = :packageData ", nativeQuery = true)
	Integer getCountDistinctTidLetto(@Param ("packId") Long packId, @Param ("packageData") String packageData);
	
	@Query(value = "select count (distinct (s.epc)) from reader_stream s where s.pack_id = :packId and s.package_data = :packageData ", nativeQuery = true)
	Integer getCountDistinctBarcodeLetto(@Param ("packId") Long packId, @Param ("packageData") String packageData);

	@Query(value = "select count (distinct (s.user_data)) from reader_stream s where s.pack_id = :packId and s.package_data = :packageData ", nativeQuery = true)
	Integer getCountDistinctUserLetto(@Param ("packId") Long packId, @Param ("packageData") String packageData);
	
	@Query(value = "select count (*) from reader_stream_atteso a where a.package_data = :packageData ", nativeQuery = true)
	Integer getCountExpected(@Param ("packageData") String packageData);
	
	
	
	
	
	public static interface StreamDifference {
		public String getArticle();
		public Long getExpected();
		public Long getRead();
		public Long getDiff();
	}
	
	public static interface ExpectedDifference {
		public BigInteger getId_package();
		public String getPackage_data();
		public String getTag();
		public String getType();
		public Integer getExpected();
		public Integer getRead();
		public Integer getDifference();
	}
	
	
	@Query(value = "select barcode as article, sum(expected) as expected , sum(read) as read , sum(expected) - sum(read) as diff from ("
			+ "select  a.barcode, count(0) as expected,0 as read  from reader_stream_atteso a where a.package_data = :packageData  group by  a.barcode "
			+ "union all "
			+ "select  s.sku as barcode, 0 expected, count(distinct epc) as read from reader_stream s where s.pack_id = :packId and s.package_data = :packageData  group by s.epc,sku "
			+ ") b group by barcode having  sum(expected) - sum(read) <> 0", nativeQuery = true)
	List<StreamDifference> getDiffBarcode(@Param ("packId") Long packId, @Param ("packageData") String packageData);
	
	
	@Query(value = "select tid, sum(expected) as expected , sum(read) as read , sum(expected) - sum(read) as diff from ("
			+ "select  a.tid, count(0) as expected, 0 as read  from reader_stream_atteso a where a.package_data = :packageData  group by  a.tid "
			+ "union all "
			+ "select  s.tid, 0 expected, count(distinct tid) as read from reader_stream s where s.pack_id = :packId and s.package_data = :packageData  group by s.tid "
			+ ") b group by tid having  sum(expected) - sum(read) <> 0", nativeQuery = true)
	List<StreamDifference> getDiffTid(@Param ("packId") Long packId, @Param ("packageData") String packageData);
	
	@Query(value = "select epc, sum(expected) as expected , sum(read) as read , sum(expected) - sum(read) as diff from ("
			+ "select  a.epc, count(0) as expected, 0 as read  from reader_stream_atteso a where a.package_data = :packageData  group by  a.epc "
			+ "union all "
			+ "select  s.epc, 0 expected, count(distinct epc) as read from reader_stream s where s.pack_id = :packId and s.package_data = :packageData  group by s.epc "
			+ ") b group by epc having  sum(expected) - sum(read) <> 0", nativeQuery = true)
	List<StreamDifference> getDiffEpc(@Param ("packId") Long packId, @Param ("packageData") String packageData);
	

	//
	@Query(value = "select id_package, package_data, tag, type, expected, read, difference from esito_diff(:packId)" , nativeQuery = true)
	List<ExpectedDifference> getEsitoDiffOld(@Param ("packId") Integer packId);
	
	@Query(value = "select id_package, package_data, tag, type, expected, read, difference from esito_diff_new(:packId,:packData,:typeExpected)" , nativeQuery = true)
	List<ExpectedDifference> getEsitoDiffNew(@Param ("packId") Integer packId, @Param ("packData") String packData,  @Param ("typeExpected") String typeExpected);

}
