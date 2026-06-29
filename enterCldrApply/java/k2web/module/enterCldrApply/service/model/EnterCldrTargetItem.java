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
@Table(name = "ADD_ENTER_CLDR_TARGET_ITEM")
public class EnterCldrTargetItem extends Statics {

    /** 항목 SEQ (PK) */
    @Id
    @Column(name = "TARGET_ITEM_SEQ", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "IdGenerator")
    @GenericGenerator(
        name      = "IdGenerator",
        strategy  = "k2web.com.cmm.hibernate.IdGenerator",
        parameters = {
            @org.hibernate.annotations.Parameter(name = "fnct", value = "ADD_ENTER_CLDR_TARGET_ITEM")
        }
    )
    private Integer targetItemSeq;

    /** 설정 SEQ (FK) */
    @Column(name = "SETUP_SEQ", nullable = false)
    private Integer setupSeq;

    /** 대상명 (예: 학부모, 1학년) */
    @Column(name = "TARGET_NM", nullable = false)
    private String targetNm;

    /** 정렬 순서 */
    @Column(name = "SORT_NO")
    private Integer sortNo;

    /** 등록일시 */
    @Column(name = "RGSDE", nullable = false)
    private Date rgsde;
    
    /** 등록자 ID */
    @Column(name = "RGS_ID")
    private String rgsId;

    public Integer getTargetItemSeq() { return targetItemSeq; }
    public void setTargetItemSeq(Integer targetItemSeq) { this.targetItemSeq = targetItemSeq; }

    public Integer getSetupSeq() { return setupSeq; }
    public void setSetupSeq(Integer setupSeq) { this.setupSeq = setupSeq; }

    public String getTargetNm() { return targetNm; }
    public void setTargetNm(String targetNm) { this.targetNm = targetNm; }

    public Integer getSortNo() { return sortNo; }
    public void setSortNo(Integer sortNo) { this.sortNo = sortNo; }

    public Date getRgsde() { return rgsde; }
    public void setRgsde(Date rgsde) { this.rgsde = rgsde; }

    public String getRgsId() { return rgsId; }
    public void setRgsId(String rgsId) { this.rgsId = rgsId; }
}
