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
@Table(name = "ADD_ENTER_CLDR_HOLIDAY")
public class EnterCldrHoliday extends Statics {

    /** 휴일 SEQ (PK) */
    @Id
    @Column(name = "HOLIDAY_SEQ", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "IdGenerator")
    @GenericGenerator(
        name      = "IdGenerator",
        strategy  = "k2web.com.cmm.hibernate.IdGenerator",
        parameters = {
            @org.hibernate.annotations.Parameter(name = "fnct", value = "ADD_ENTER_CLDR_HOLIDAY")
        }
    )
    private Integer holidaySeq;

    /** 설정 SEQ (FK) */
    @Column(name = "SETUP_SEQ", nullable = false)
    private Integer setupSeq;

    /** 휴일 일자 */
    @Column(name = "HOLIDAY_DT", nullable = false)
    private Date holidayDt;

    /** 휴일명 */
    @Column(name = "HOLIDAY_NM")
    private String holidayNm;

    /** 등록일시 */
    @Column(name = "RGSDE", nullable = false)
    private Date rgsde;

    /** 등록자 ID */
    @Column(name = "RGS_ID")
    private String rgsId;

    public Integer getHolidaySeq() { return holidaySeq; }
    public void setHolidaySeq(Integer holidaySeq) { this.holidaySeq = holidaySeq; }

    public Integer getSetupSeq() { return setupSeq; }
    public void setSetupSeq(Integer setupSeq) { this.setupSeq = setupSeq; }

    public Date getHolidayDt() { return holidayDt; }
    public void setHolidayDt(Date holidayDt) { this.holidayDt = holidayDt; }

    public String getHolidayNm() { return holidayNm; }
    public void setHolidayNm(String holidayNm) { this.holidayNm = holidayNm; }

    public Date getRgsde() { return rgsde; }
    public void setRgsde(Date rgsde) { this.rgsde = rgsde; }

    public String getRgsId() { return rgsId; }
    public void setRgsId(String rgsId) { this.rgsId = rgsId; }
}
