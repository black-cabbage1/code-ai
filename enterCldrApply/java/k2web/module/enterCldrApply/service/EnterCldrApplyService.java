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
    void setSetupUpdt(EnterCldrSetup setup, String updId);
    void setSetupDelete(Integer seq);

    /* ===================== 첨부파일 ===================== */
    List<EnterCldrAtchmnfl> getAtchmnflList(Integer setupSeq);
    EnterCldrAtchmnfl getAtchmnfl(Integer seq);
    void setAtchmnflRegist(String siteId, EnterCldrAtchmnfl atchmnfl, String regId);
    void setAtchmnflDelete(String siteId, Integer seq);

    /* ===================== 시간 슬롯 ===================== */
    List<EnterCldrTimeSlot> getTimeSlotList(Integer setupSeq);
    EnterCldrTimeSlot getTimeSlot(Integer seq);
    void setTimeSlotRegist(EnterCldrTimeSlot timeSlot, String regId);
    void setTimeSlotUpdt(EnterCldrTimeSlot timeSlot, String updId);
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
    void setFormItemUpdt(EnterCldrFormItem formItem, String updId);
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
    void setArtclUpdt(EnterCldrArtcl artcl, boolean isManage);
    void setArtclStatusUpdt(Integer setupSeq, Integer artclSeq, String artclStatus, boolean isManage);
    void setArtclDelete(Integer setupSeq, Integer artclSeq);

    /** 신청자 목록 조회 (사용자: 이름+전화번호 기준) */
    List<EnterCldrArtcl> getArtclListByUser(Integer setupSeq, String rqstNm, String rqstTel);

    /** 신청자 중복 여부 확인 (설정당 이름+전화 기준, CANCELED 제외) */
    boolean isArtclDuplicated(Integer setupSeq, String rqstNm, String rqstTel);

    /** 시간 슬롯 잔여 인원 조회 */
    int getTimeSlotRemainCnt(Integer timeSlotSeq, String artclDt);

    /**
     * 신청 등록 — 슬롯 행 비관적 잠금 후 정원 재확인 + 저장을 원자적으로 처리.
     * 동시 신청 시 초과 등록을 방지한다.
     * @return null: 성공, 문자열: 실패 사유 메시지
     */
    String tryArtclRegist(EnterCldrArtcl artcl);

    /** 신청자 목록 엑셀 다운로드 — 임시 파일 경로 반환 */
    String excelDown(EnterCldrApplyVo vo);

    /** 관리자 수정 화면용 슬롯 목록 (현재 artcl 제외한 접수 건수 포함) */
    List<EnterCldrTimeSlot> getTimeSlotListForEdit(Integer setupSeq, Integer artclSeq, String artclDt);
}
