package k2web.module.enterCldrApply.service.model;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import k2web.com.cmm.Statics;
import k2web.com.cop.fnctmngr.service.model.FnctMngr;

@Entity
@Table(name = "ADD_ENTER_CLDR_SETUP")
public class EnterCldrSetup extends Statics {

    /** 설정 SEQ (PK) */
    @Id
    @Column(name = "SETUP_SEQ", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "IdGenerator")
    @GenericGenerator(
        name      = "IdGenerator",
        strategy  = "k2web.com.cmm.hibernate.IdGenerator",
        parameters = {
            @org.hibernate.annotations.Parameter(name = "fnct", value = "ADD_ENTER_CLDR_SETUP")
        }
    )
    private Integer setupSeq;

    /** 설정명 */
    @Column(name = "SETUP_NM", nullable = false)
    private String setupNm;
    
    /** 사이트 ID */
    @Column(name = "SITE_ID", nullable = false)
    private String siteId;

    /** 사용여부 */
    @Column(name = "USE_YN", nullable = false)
    private String useYn;

    /** 요일별 신청가능여부 */
    @Column(name = "MON_YN", nullable = false) private String monYn;
    @Column(name = "TUE_YN", nullable = false) private String tueYn;
    @Column(name = "WED_YN", nullable = false) private String wedYn;
    @Column(name = "THU_YN", nullable = false) private String thuYn;
    @Column(name = "FRI_YN", nullable = false) private String friYn;
    @Column(name = "SAT_YN", nullable = false) private String satYn;
    @Column(name = "SUN_YN", nullable = false) private String sunYn;

    /** 동반인원 사용여부 */
    @Column(name = "COMPANION_USE_YN", nullable = false)
    private String companionUseYn;

    /** 대상별 인원 사용여부 */
    @Column(name = "TARGET_COMP_USE_YN", nullable = false)
    private String targetCompUseYn;

    /** 소개 */
    @Column(name = "INTRO")
    private String intro;

    /** 신청대상 */
    @Column(name = "APPLY_TARGET")
    private String applyTarget;

    /** 장소 */
    @Column(name = "LOCATION")
    private String location;

    /** 내용 (에디터) */
    @Lob
    @Column(name = "CONTENT")
    private String content;

    /** 행사 시작일 */
    @Column(name = "EVT_START_DT")
    private Date evtStartDt;

    /** 행사 종료일 */
    @Column(name = "EVT_END_DT")
    private Date evtEndDt;

    /** 접수 시작일 */
    @Column(name = "RECV_START_DT")
    private Date recvStartDt;

    /** 접수 종료일 */
    @Column(name = "RECV_END_DT")
    private Date recvEndDt;

    /** 수정 시작일 */
    @Column(name = "MOD_START_DT")
    private Date modStartDt;

    /** 수정 종료일 */
    @Column(name = "MOD_END_DT")
    private Date modEndDt;

    /** 팝업 안내 메시지 */
    @Column(name = "POPUP_MSG")
    private String popupMsg;

    /** 담당자 정보 */
    @Column(name = "MNG_INFO")
    private String mngInfo;

    /** 개인정보 수집·이용 목적 */
    @Column(name = "PRIVACY_PURPOSE")
    private String privacyPurpose;

    /** 수집하는 개인정보 항목 */
    @Column(name = "PRIVACY_ITEMS")
    private String privacyItems;

    /** 개인정보 보유 및 이용기간 */
    @Column(name = "PRIVACY_PERIOD")
    private String privacyPeriod;

    /** 중복 신청 허용 여부 (Y: 허용, N: 불가, 기본 N) */
    @Column(name = "DPLC_APLY_PSBL_YN", nullable = false)
    private String dplcAplyPsblYn = "N";

    /** 등록일시 */
    @Column(name = "RGSDE", nullable = false)
    private Date rgsde;

    /** 등록자 ID */
    @Column(name = "RGS_ID")
    private String rgsId;

    /** 수정일시 */
    @Column(name = "UPDDE", nullable = false)
    private Date updde;

    /** 수정자 ID */
    @Column(name = "UPD_ID")
    private String updId;
    
    /** 접수 시작 시각 (0~23) — 폼 바인딩용, DB 저장 안 함 */
    @Transient private Integer recvStartHour;
    /** 접수 종료 시각 (0~23) — 폼 바인딩용, DB 저장 안 함 */
    @Transient private Integer recvEndHour;
    /** 수정 시작 시각 (0~23) — 폼 바인딩용, DB 저장 안 함 */
    @Transient private Integer modStartHour;
    /** 수정 종료 시각 (0~23) — 폼 바인딩용, DB 저장 안 함 */
    @Transient private Integer modEndHour;

    /** 사용중인 스킨 아이디 */
	@Transient
	private String skinId;
	
	/** 사용중인 스킨 명 */
	@Transient
	private String skinNm;
	
	/** 기능별 관리자 리스트 */
	@Transient
	private List<FnctMngr> fnctMngrList;

    public Integer getSetupSeq() { return setupSeq; }
    public void setSetupSeq(Integer setupSeq) { this.setupSeq = setupSeq; }

    public String getSetupNm() { return setupNm; }
    public void setSetupNm(String setupNm) { this.setupNm = setupNm; }

    public String getUseYn() { return useYn; }
    public void setUseYn(String useYn) { this.useYn = useYn; }

    public String getMonYn() { return monYn; }
    public void setMonYn(String monYn) { this.monYn = monYn; }

    public String getTueYn() { return tueYn; }
    public void setTueYn(String tueYn) { this.tueYn = tueYn; }

    public String getWedYn() { return wedYn; }
    public void setWedYn(String wedYn) { this.wedYn = wedYn; }

    public String getThuYn() { return thuYn; }
    public void setThuYn(String thuYn) { this.thuYn = thuYn; }

    public String getFriYn() { return friYn; }
    public void setFriYn(String friYn) { this.friYn = friYn; }

    public String getSatYn() { return satYn; }
    public void setSatYn(String satYn) { this.satYn = satYn; }

    public String getSunYn() { return sunYn; }
    public void setSunYn(String sunYn) { this.sunYn = sunYn; }

    public String getCompanionUseYn() { return companionUseYn; }
    public void setCompanionUseYn(String companionUseYn) { this.companionUseYn = companionUseYn; }

    public String getTargetCompUseYn() { return targetCompUseYn; }
    public void setTargetCompUseYn(String targetCompUseYn) { this.targetCompUseYn = targetCompUseYn; }

    public String getIntro() { return intro; }
    public void setIntro(String intro) { this.intro = intro; }

    public String getApplyTarget() { return applyTarget; }
    public void setApplyTarget(String applyTarget) { this.applyTarget = applyTarget; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Date getEvtStartDt() { return evtStartDt; }
    public void setEvtStartDt(Date evtStartDt) { this.evtStartDt = evtStartDt; }

    public Date getEvtEndDt() { return evtEndDt; }
    public void setEvtEndDt(Date evtEndDt) { this.evtEndDt = evtEndDt; }

    public Date getRecvStartDt() { return recvStartDt; }
    public void setRecvStartDt(Date recvStartDt) { this.recvStartDt = recvStartDt; }

    public Date getRecvEndDt() { return recvEndDt; }
    public void setRecvEndDt(Date recvEndDt) { this.recvEndDt = recvEndDt; }

    public Date getModStartDt() { return modStartDt; }
    public void setModStartDt(Date modStartDt) { this.modStartDt = modStartDt; }

    public Date getModEndDt() { return modEndDt; }
    public void setModEndDt(Date modEndDt) { this.modEndDt = modEndDt; }

    public String getPopupMsg() { return popupMsg; }
    public void setPopupMsg(String popupMsg) { this.popupMsg = popupMsg; }

    public String getMngInfo() { return mngInfo; }
    public void setMngInfo(String mngInfo) { this.mngInfo = mngInfo; }

    public Integer getRecvStartHour() { return recvStartHour; }
    public void setRecvStartHour(Integer recvStartHour) { this.recvStartHour = recvStartHour; }

    public Integer getRecvEndHour() { return recvEndHour; }
    public void setRecvEndHour(Integer recvEndHour) { this.recvEndHour = recvEndHour; }

    public Integer getModStartHour() { return modStartHour; }
    public void setModStartHour(Integer modStartHour) { this.modStartHour = modStartHour; }

    public Integer getModEndHour() { return modEndHour; }
    public void setModEndHour(Integer modEndHour) { this.modEndHour = modEndHour; }

    public String getPrivacyPurpose() { return privacyPurpose; }
    public void setPrivacyPurpose(String privacyPurpose) { this.privacyPurpose = privacyPurpose; }

    public String getPrivacyItems() { return privacyItems; }
    public void setPrivacyItems(String privacyItems) { this.privacyItems = privacyItems; }

    public String getPrivacyPeriod() { return privacyPeriod; }
    public void setPrivacyPeriod(String privacyPeriod) { this.privacyPeriod = privacyPeriod; }

    public String getDplcAplyPsblYn() { return dplcAplyPsblYn; }
    public void setDplcAplyPsblYn(String dplcAplyPsblYn) { this.dplcAplyPsblYn = dplcAplyPsblYn; }

    public Date getRgsde() { return rgsde; }
	public void setRgsde(Date rgsde) { this.rgsde = rgsde; }
	
	public Date getUpdde() { return updde; }
	public void setUpdde(Date updde) { this.updde = updde; }
	
	public String getRgsId() { return rgsId; }
	public void setRgsId(String rgsId) { this.rgsId = rgsId; }
	
    public String getUpdId() { return updId; }
    public void setUpdId(String updId) { this.updId = updId; }
	public String getSkinId() {
		return skinId;
	}
	public void setSkinId(String skinId) {
		this.skinId = skinId;
	}
	public String getSkinNm() {
		return skinNm;
	}
	public void setSkinNm(String skinNm) {
		this.skinNm = skinNm;
	}
	public List<FnctMngr> getFnctMngrList() {
		return fnctMngrList;
	}
	public void setFnctMngrList(List<FnctMngr> fnctMngrList) {
		this.fnctMngrList = fnctMngrList;
	}
	public String getSiteId() {
		return siteId;
	}
	public void setSiteId(String siteId) {
		this.siteId = siteId;
	}
}
