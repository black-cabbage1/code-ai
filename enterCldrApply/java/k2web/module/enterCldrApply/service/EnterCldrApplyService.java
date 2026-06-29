package k2web.module.enterCldrApply.service;

import java.util.List;

import k2web.module.enterCldrApply.service.model.EnterCldrArtcl;
import k2web.module.enterCldrApply.service.model.EnterCldrAtchmnfl;
import k2web.module.enterCldrApply.service.model.EnterCldrFormItem;
import k2web.module.enterCldrApply.service.model.EnterCldrHoliday;
import k2web.module.enterCldrApply.service.model.EnterCldrSetup;
import k2web.module.enterCldrApply.service.model.EnterCldrTargetItem;
import k2web.module.enterCldrApply.service.model.EnterCldrTimeSlot;

public interface EnterCldrApplyService {

    public static final String FNCT_ID = "enterCldrApply";

    /* ===================== 설정 ===================== */
    List<EnterCldrSetup> getSetupList(EnterCldrApplyVo vo, boolean isManage);
    EnterCldrSetup getSetup(Integer seq);
    void setSetupRegist(EnterCldrSetup setup, String regId);
    void setSetupUpdt(EnterCldrSetup setup, String mdfcnId);
    void setSetupDelete(Integer seq);

    /* ===================== 첨부파일 ===================== */
    List<EnterCldrAtchmnfl> getAtchmnflList(Integer setupSeq);
    void setAtchmnflRegist(String siteId, EnterCldrAtchmnfl atchmnfl, String regId);
    void setAtchmnflDelete(String siteId, Integer seq);

    /* ===================== 시간 슬롯 ===================== */
    List<EnterCldrTimeSlot> getTimeSlotList(Integer setupSeq);
    EnterCldrTimeSlot getTimeSlot(Integer seq);
    void setTimeSlotRegist(EnterCldrTimeSlot timeSlot, String regId);
    void setTimeSlotUpdt(EnterCldrTimeSlot timeSlot, String mdfcnId);
    void setTimeSlotDelete(Integer seq);

    /* ===================== 대상별 항목 ===================== */
    List<EnterCldrTargetItem> getTargetItemList(Integer setupSeq);
    void setTargetItemRegist(EnterCldrTargetItem targetItem, String regId);
    void setTargetItemUpdt(EnterCldrTargetItem targetItem, String updId);
    void setTargetItemDelete(Integer seq);

    /* ===================== 폼 항목 ===================== */
    List<EnterCldrFormItem> getFormItemList(Integer setupSeq);
    EnterCldrFormItem getFormItem(Integer seq);
    void setFormItemRegist(EnterCldrFormItem formItem, String regId);
    void setFormItemUpdt(EnterCldrFormItem formItem, String mdfcnId);
    void setFormItemDelete(Integer seq);

    /* ===================== 휴일 ===================== */
    List<EnterCldrHoliday> getHolidayList(Integer setupSeq);
    void setHolidayRegist(EnterCldrHoliday holiday, String regId);
    void setHolidayUpdt(EnterCldrHoliday holiday, String updId);
    void setHolidayDelete(Integer seq);

    /* ===================== 신청 ===================== */
    List<EnterCldrArtcl> getArtclList(EnterCldrApplyVo vo);
    Long getArtclListCount(EnterCldrApplyVo vo);
    EnterCldrArtcl getArtcl(Integer setupSeq, Integer artclSeq);
    void setArtclRegist(EnterCldrArtcl artcl);
    void setArtclUpdt(EnterCldrArtcl artcl, String mdfcnId);
    void setArtclStatusUpdt(Integer setupSeq, Integer artclSeq, String artclStatus, String mdfcnId);
    void setArtclDelete(Integer setupSeq, Integer artclSeq);

    /** 신청자 목록 조회 (사용자: 이름+전화번호 기준) */
    List<EnterCldrArtcl> getArtclListByUser(Integer setupSeq, String rqstNm, String rqstTel);

    /** 신청자 중복 여부 확인 (설정당 이름+전화 기준, CANCELED 제외) */
    boolean isArtclDuplicated(Integer setupSeq, String rqstNm, String rqstTel);

    /** 시간 슬롯 잔여 인원 조회 */
    int getTimeSlotRemainCnt(Integer timeSlotSeq, String artclDt);
}
