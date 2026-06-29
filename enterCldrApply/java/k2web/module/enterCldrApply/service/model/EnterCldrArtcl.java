package k2web.module.enterCldrApply.service.model;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import k2web.com.cmm.Statics;
import k2web.com.util.CryptoUtil;

@Entity
@Table(name = "ADD_ENTER_CLDR_ARTCL")
public class EnterCldrArtcl extends Statics {

    @Id
    @Column(name = "ARTCL_SEQ", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "IdGenerator")
    @GenericGenerator(
        name      = "IdGenerator",
        strategy  = "k2web.com.cmm.hibernate.IdGenerator",
        parameters = {
            @org.hibernate.annotations.Parameter(name = "fnct", value = "ADD_ENTER_CLDR_ARTCL")
        }
    )
    private Integer artclSeq;

    @Column(name = "SETUP_SEQ", nullable = false)
    private Integer setupSeq;

    @Column(name = "TIME_SLOT_SEQ", nullable = false)
    private Integer timeSlotSeq;

    @Column(name = "ARTCL_DT", nullable = false)
    private Date artclDt;

    /** 이름 */
    @Column(name = "RQST_NM", nullable = false)
    private String rqstNm;

    /** 휴대전화 (암호화) */
    @Column(name = "RQST_TEL", nullable = false)
    private String rqstTel;

    /** 이메일 */
    @Column(name = "RQST_ML")
    private String rqstMl;

    /** 고교코드 */
    @Column(name = "SCH_CD")
    private String schCd;

    /** 고교이름 */
    @Column(name = "SCH_NM")
    private String schNm;

    /** 고교지역 */
    @Column(name = "SCH_LC")
    private String schLc;

    /** 고교유형 */
    @Column(name = "SCH_TP")
    private String schTp;

    /** 동반 인원 합계 */
    @Column(name = "COMPANION_CNT")
    private Integer companionCnt;

    /** 신청 상태 (WAIT/APPROVED/REJECTED/CANCELED) */
    @Column(name = "ARTCL_STATUS", nullable = false)
    private String artclStatus;

    @Column(name = "DEL_YN", nullable = false)
    private String delYn;

    @Column(name = "RGSDE", nullable = false)
    private Date rgsde;

    @Column(name = "UPDDE", nullable = false)
    private Date updde;

    /* ─── 추가 항목 (동적 폼 항목 답변 저장용) ─── */
    @Column(name = "ADDITM1")  private String additm1;
    @Column(name = "ADDITM2")  private String additm2;
    @Column(name = "ADDITM3")  private String additm3;
    @Column(name = "ADDITM4")  private String additm4;
    @Column(name = "ADDITM5")  private String additm5;
    @Column(name = "ADDITM6")  private String additm6;
    @Column(name = "ADDITM7")  private String additm7;
    @Column(name = "ADDITM8")  private String additm8;
    @Column(name = "ADDITM9")  private String additm9;
    @Column(name = "ADDITM10") private String additm10;
    @Column(name = "ADDITM11") private String additm11;
    @Column(name = "ADDITM12") private String additm12;
    @Column(name = "ADDITM13") private String additm13;
    @Column(name = "ADDITM14") private String additm14;
    @Column(name = "ADDITM15") private String additm15;

    /* ─── 비영속 ─── */
    @Transient private List<EnterCldrArtclTarget> targetList;
    @Transient private List<EnterCldrFormItem> dynamicFormItems;
    @Transient private String applyTime;

    // ── getters / setters ──

    public Integer getArtclSeq() { return artclSeq; }
    public void setArtclSeq(Integer v) { this.artclSeq = v; }

    public Integer getSetupSeq() { return setupSeq; }
    public void setSetupSeq(Integer v) { this.setupSeq = v; }

    public Integer getTimeSlotSeq() { return timeSlotSeq; }
    public void setTimeSlotSeq(Integer v) { this.timeSlotSeq = v; }

    public Date getArtclDt() { return artclDt; }
    public void setArtclDt(Date v) { this.artclDt = v; }

    public String getRqstNm() { return rqstNm; }
    public void setRqstNm(String v) { this.rqstNm = v; }

    public String getRqstTel() { return CryptoUtil.decodeARIACrypto(rqstTel); }
    public void setRqstTel(String v) { this.rqstTel = CryptoUtil.encodeARIACrypto(v); }

    public String getRqstMl() { return rqstMl; }
    public void setRqstMl(String v) { this.rqstMl = v; }

    public String getSchCd() { return schCd; }
    public void setSchCd(String v) { this.schCd = v; }

    public String getSchNm() { return schNm; }
    public void setSchNm(String v) { this.schNm = v; }

    public String getSchLc() { return schLc; }
    public void setSchLc(String v) { this.schLc = v; }

    public String getSchTp() { return schTp; }
    public void setSchTp(String v) { this.schTp = v; }

    public Integer getCompanionCnt() { return companionCnt; }
    public void setCompanionCnt(Integer v) { this.companionCnt = v; }

    public String getArtclStatus() { return artclStatus; }
    public void setArtclStatus(String v) { this.artclStatus = v; }

    public String getDelYn() { return delYn; }
    public void setDelYn(String v) { this.delYn = v; }

    public Date getRgsde() { return rgsde; }
    public void setRgsde(Date v) { this.rgsde = v; }

    public Date getUpdde() { return updde; }
    public void setUpdde(Date v) { this.updde = v; }

    public String getAdditm1()  { return additm1;  } public void setAdditm1(String v)  { this.additm1  = v; }
    public String getAdditm2()  { return additm2;  } public void setAdditm2(String v)  { this.additm2  = v; }
    public String getAdditm3()  { return additm3;  } public void setAdditm3(String v)  { this.additm3  = v; }
    public String getAdditm4()  { return additm4;  } public void setAdditm4(String v)  { this.additm4  = v; }
    public String getAdditm5()  { return additm5;  } public void setAdditm5(String v)  { this.additm5  = v; }
    public String getAdditm6()  { return additm6;  } public void setAdditm6(String v)  { this.additm6  = v; }
    public String getAdditm7()  { return additm7;  } public void setAdditm7(String v)  { this.additm7  = v; }
    public String getAdditm8()  { return additm8;  } public void setAdditm8(String v)  { this.additm8  = v; }
    public String getAdditm9()  { return additm9;  } public void setAdditm9(String v)  { this.additm9  = v; }
    public String getAdditm10() { return additm10; } public void setAdditm10(String v) { this.additm10 = v; }
    public String getAdditm11() { return additm11; } public void setAdditm11(String v) { this.additm11 = v; }
    public String getAdditm12() { return additm12; } public void setAdditm12(String v) { this.additm12 = v; }
    public String getAdditm13() { return additm13; } public void setAdditm13(String v) { this.additm13 = v; }
    public String getAdditm14() { return additm14; } public void setAdditm14(String v) { this.additm14 = v; }
    public String getAdditm15() { return additm15; } public void setAdditm15(String v) { this.additm15 = v; }

    public List<EnterCldrArtclTarget> getTargetList() { return targetList; }
    public void setTargetList(List<EnterCldrArtclTarget> v) { this.targetList = v; }

    public List<EnterCldrFormItem> getDynamicFormItems() { return dynamicFormItems; }
    public void setDynamicFormItems(List<EnterCldrFormItem> v) { this.dynamicFormItems = v; }

    public String getApplyTime() { return applyTime; }
    public void setApplyTime(String v) { this.applyTime = v; }
}
