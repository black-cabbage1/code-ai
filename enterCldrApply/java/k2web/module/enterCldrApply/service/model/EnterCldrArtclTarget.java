package k2web.module.enterCldrApply.service.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import k2web.com.cmm.Statics;

@Entity
@Table(name = "ADD_ENTER_CLDR_ARTCL_TARGET")
public class EnterCldrArtclTarget extends Statics {

    /** SEQ (PK) */
    @Id
    @Column(name = "TARGET_SEQ", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "IdGenerator")
    @GenericGenerator(
        name      = "IdGenerator",
        strategy  = "k2web.com.cmm.hibernate.IdGenerator",
        parameters = {
            @org.hibernate.annotations.Parameter(name = "fnct", value = "ADD_ENTER_CLDR_ARTCL_TARGET")
        }
    )
    private Integer targetSeq;

    /** 신청 SEQ (FK) */
    @Column(name = "ARTCL_SEQ", nullable = false)
    private Integer artclSeq;

    /** 대상 항목 SEQ (FK) */
    @Column(name = "TARGET_ITEM_SEQ", nullable = false)
    private Integer targetItemSeq;

    /** 대상명 (등록 시점 스냅샷) */
    @Column(name = "TARGET_NM")
    private String targetNm;

    /** 대상별 동반 인원 수 */
    @Column(name = "COMP_CNT", nullable = false)
    private Integer compCnt;

    public Integer getTargetSeq() { return targetSeq; }
    public void setTargetSeq(Integer targetSeq) { this.targetSeq = targetSeq; }

    public Integer getArtclSeq() { return artclSeq; }
    public void setArtclSeq(Integer artclSeq) { this.artclSeq = artclSeq; }

    public Integer getTargetItemSeq() { return targetItemSeq; }
    public void setTargetItemSeq(Integer targetItemSeq) { this.targetItemSeq = targetItemSeq; }

    public String getTargetNm() { return targetNm; }
    public void setTargetNm(String targetNm) { this.targetNm = targetNm; }

    public Integer getCompCnt() { return compCnt; }
    public void setCompCnt(Integer compCnt) { this.compCnt = compCnt; }
}
