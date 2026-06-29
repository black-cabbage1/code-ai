package k2web.module.enterCldrApply.service.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import k2web.com.cmm.Statics;

@Entity
@Table(name = "ADD_ENTER_CLDR_ATCHMNFL")
public class EnterCldrAtchmnfl extends Statics {

    /** 첨부파일 SEQ (PK) */
    @Id
    @Column(name = "ATCHMNFL_SEQ", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "IdGenerator")
    @GenericGenerator(
        name      = "IdGenerator",
        strategy  = "k2web.com.cmm.hibernate.IdGenerator",
        parameters = {
            @org.hibernate.annotations.Parameter(name = "fnct", value = "ADD_ENTER_CLDR_ATCHMNFL")
        }
    )
    private Integer atchmnflSeq;

    /** 설정 SEQ (FK) */
    @Column(name = "SETUP_SEQ", nullable = false)
    private Integer setupSeq;

    /** 원본 파일명 */
    @Column(name = "ORGINL_NM", nullable = false)
    private String orginlNm;

    /** 저장 파일명 */
    @Column(name = "CHANGE_NM", nullable = false)
    private String changeNm;

    /** 파일 저장 경로 */
    @Column(name = "FILE_PATH", nullable = false)
    private String filePath;

    /** 파일 확장자 */
    @Column(name = "FILE_EXT")
    private String fileExt;

    /** 등록일시 */
    @Column(name = "RGSDE", nullable = false)
    private Date rgsde;

    /** 등록자 ID */
    @Column(name = "RGS_ID")
    private String rgsId;

    public Integer getAtchmnflSeq() { return atchmnflSeq; }
    public void setAtchmnflSeq(Integer atchmnflSeq) { this.atchmnflSeq = atchmnflSeq; }

    public Integer getSetupSeq() { return setupSeq; }
    public void setSetupSeq(Integer setupSeq) { this.setupSeq = setupSeq; }

    public String getOrginlNm() { return orginlNm; }
    public void setOrginlNm(String originNm) { this.orginlNm = originNm; }

    public String getChangeNm() { return changeNm; }
    public void setChangeNm(String changeNm) { this.changeNm = changeNm; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public String getFileExt() { return fileExt; }
    public void setFileExt(String fileExt) { this.fileExt = fileExt; }

    public Date getRgsde() { return rgsde; }
    public void setRgsde(Date rgsde) { this.rgsde = rgsde; }

    public String getRgsId() { return rgsId; }
    public void setRgsId(String rgsId) { this.rgsId = rgsId; }
}
