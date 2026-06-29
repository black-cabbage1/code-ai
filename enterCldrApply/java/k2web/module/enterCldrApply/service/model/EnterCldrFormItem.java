package k2web.module.enterCldrApply.service.model;

import java.util.Date;

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

@Entity
@Table(name = "ADD_ENTER_CLDR_FORM_ITEM")
public class EnterCldrFormItem extends Statics {

    /** 항목 SEQ (PK) */
    @Id
    @Column(name = "FORM_ITEM_SEQ", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "IdGenerator")
    @GenericGenerator(
        name      = "IdGenerator",
        strategy  = "k2web.com.cmm.hibernate.IdGenerator",
        parameters = {
            @org.hibernate.annotations.Parameter(name = "fnct", value = "ADD_ENTER_CLDR_FORM_ITEM")
        }
    )
    private Integer formItemSeq;

    /** 설정 SEQ (FK) */
    @Column(name = "SETUP_SEQ", nullable = false)
    private Integer setupSeq;

    /** 항목명 */
    @Column(name = "ITEM_NM", nullable = false)
    private String itemNm;

    /** 항목 유형 (TEXT/Phone/Email/Radio/Checkbox/School) */
    @Column(name = "ITEM_TYPE", nullable = false)
    private String itemType;

    /** 선택 옵션 목록(줄바꿈 구분, SELECT·RADIO·CHECKBOX 사용) */
    @Lob
    @Column(name = "ITEM_OPTIONS")
    private String itemOptions;
    
    /** 필수여부 */
    @Column(name = "REQUIRED_YN", nullable = false)
    private String requiredYn;

    /** 고정 항목 여부 (설정 생성 시 자동 삽입, Y=고정) */
    @Column(name = "FIXED_YN")
    private String fixedYn;

    /** 정렬 순서 */
    @Column(name = "SORT_NO")
    private Integer sortNo;

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

    /** 신청 조회 시 해당 항목의 답변값 (비영속) */
    @Transient private String answerVal;

    public Integer getFormItemSeq() { return formItemSeq; }
    public void setFormItemSeq(Integer formItemSeq) { this.formItemSeq = formItemSeq; }

    public Integer getSetupSeq() { return setupSeq; }
    public void setSetupSeq(Integer setupSeq) { this.setupSeq = setupSeq; }

    public String getItemNm() { return itemNm; }
    public void setItemNm(String itemNm) { this.itemNm = itemNm; }

    public String getItemType() { return itemType; }
    public void setItemType(String itemType) { this.itemType = itemType; }

    public String getRequiredYn() { return requiredYn; }
    public void setRequiredYn(String requiredYn) { this.requiredYn = requiredYn; }

    public String getItemOptions() { return itemOptions; }
    public void setItemOptions(String itemOptions) { this.itemOptions = itemOptions; }

    public String getFixedYn() { return fixedYn; }
    public void setFixedYn(String fixedYn) { this.fixedYn = fixedYn; }

    public Integer getSortNo() { return sortNo; }
    public void setSortNo(Integer sortNo) { this.sortNo = sortNo; }

	public Date getRgsde() { return rgsde; }
	public void setRgsde(Date rgsde) { this.rgsde = rgsde; }
	
	public Date getUpdde() { return updde; }
	public void setUpdde(Date updde) { this.updde = updde; }
	
	public String getRgsId() { return rgsId; }
	public void setRgsId(String rgsId) { this.rgsId = rgsId; }
	
    public String getUpdId() { return updId; }
    public void setUpdId(String updId) { this.updId = updId; }

    public String getAnswerVal() { return answerVal; }
    public void setAnswerVal(String answerVal) { this.answerVal = answerVal; }
}
