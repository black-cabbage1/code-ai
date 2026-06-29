package k2web.module.enterCldrApply.service.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import k2web.com.cmm.Statics;

@Entity
@Table(name = "ADD_ENTER_CLDR_TIME_SLOT")
public class EnterCldrTimeSlot extends Statics {

    /** 시간 슬롯 SEQ (PK) */
    @Id
    @Column(name = "SLOT_SEQ", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "IdGenerator")
    @GenericGenerator(
        name      = "IdGenerator",
        strategy  = "k2web.com.cmm.hibernate.IdGenerator",
        parameters = {
            @org.hibernate.annotations.Parameter(name = "fnct", value = "ADD_ENTER_CLDR_TIME_SLOT")
        }
    )
    private Integer slotSeq;

    /** 설정 SEQ (FK) */
    @Column(name = "SETUP_SEQ", nullable = false)
    private Integer setupSeq;

    /** 신청 시간 (HH24:MI) */
    @Column(name = "APPLY_TIME", nullable = false)
    private String applyTime;

    /** 회차별 접수 건수 제한 (0: 무제한) */
    @Column(name = "CAPACITY", nullable = false)
    private Integer capacity;

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

    /** 해당 날짜의 실 접수 건수 (수정 화면 용, 비영속) */
    @Transient private int bookedCount;

    public int getBookedCount() { return bookedCount; }
    public void setBookedCount(int bookedCount) { this.bookedCount = bookedCount; }

    public Integer getSlotSeq() { return slotSeq; }
    public void setSlotSeq(Integer slotSeq) { this.slotSeq = slotSeq; }

    public Integer getSetupSeq() { return setupSeq; }
    public void setSetupSeq(Integer setupSeq) { this.setupSeq = setupSeq; }

    public String getApplyTime() { return applyTime; }
    public void setApplyTime(String applyTime) { this.applyTime = applyTime; }

    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }

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
}
