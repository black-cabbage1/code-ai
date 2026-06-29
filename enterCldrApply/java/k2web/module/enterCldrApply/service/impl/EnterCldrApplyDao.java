package k2web.module.enterCldrApply.service.impl;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.LockMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import k2web.com.cmm.hibernate.HibernateComAbstractDAO;
import k2web.com.util.CryptoUtil;
import k2web.module.enterCldrApply.service.EnterCldrApplyVo;
import k2web.module.enterCldrApply.service.model.EnterCldrArtcl;
import k2web.module.enterCldrApply.service.model.EnterCldrArtclTarget;
import k2web.module.enterCldrApply.service.model.EnterCldrAtchmnfl;
import k2web.module.enterCldrApply.service.model.EnterCldrFormItem;
import k2web.module.enterCldrApply.service.model.EnterCldrHoliday;
import k2web.module.enterCldrApply.service.model.EnterCldrSetup;
import k2web.module.enterCldrApply.service.model.EnterCldrTargetItem;
import k2web.module.enterCldrApply.service.model.EnterCldrTimeSlot;

@Repository("EnterCldrApplyDao")
public class EnterCldrApplyDao extends HibernateComAbstractDAO {

    private static final Logger LOG = LoggerFactory.getLogger(EnterCldrApplyDao.class);

    /* ===================== 설정 ===================== */

    public List<EnterCldrSetup> getSetupList(EnterCldrApplyVo vo, boolean isManage) {
        Criteria criteria = easyCreateCriteria(EnterCldrSetup.class);
        criteria.add( Restrictions.eq( "siteId", vo.getSiteId() ) );
        if (!isManage) {
            criteria.add(Restrictions.eq("useYn", "Y"));
        }
        criteria.addOrder(Order.asc("setupSeq"));
        List<EnterCldrSetup> list = criteria.list();
        return list != null && !list.isEmpty() ? list : null;
    }

    public EnterCldrSetup getSetup(Integer seq) {
        Criteria criteria = easyCreateCriteria(EnterCldrSetup.class);
        criteria.add(Restrictions.eq("setupSeq", seq));
        List<EnterCldrSetup> list = criteria.list();
        return list != null && !list.isEmpty() ? list.get(0) : null;
    }

    public void setSetupRegist(EnterCldrSetup setup) { save(setup); }
    public void setSetupUpdt(EnterCldrSetup setup) { update(setup); }
    public void setSetupDelete(EnterCldrSetup setup) { delete(setup); }

    /* ===================== 첨부파일 ===================== */

    public List<EnterCldrAtchmnfl> getAtchmnflList(Integer setupSeq) {
        Criteria criteria = easyCreateCriteria(EnterCldrAtchmnfl.class);
        criteria.add(Restrictions.eq("setupSeq", setupSeq));
        criteria.addOrder(Order.asc("atchmnflSeq"));
        List<EnterCldrAtchmnfl> list = criteria.list();
        return list != null && !list.isEmpty() ? list : null;
    }

    public EnterCldrAtchmnfl getAtchmnfl(Integer seq) {
        Criteria criteria = easyCreateCriteria(EnterCldrAtchmnfl.class);
        criteria.add(Restrictions.eq("atchmnflSeq", seq));
        List<EnterCldrAtchmnfl> list = criteria.list();
        return list != null && !list.isEmpty() ? list.get(0) : null;
    }

    public void setAtchmnflRegist(EnterCldrAtchmnfl atchmnfl) { save(atchmnfl); }
    public void setAtchmnflDelete(EnterCldrAtchmnfl atchmnfl) { delete(atchmnfl); }

    /* ===================== 시간 슬롯 ===================== */

    public List<EnterCldrTimeSlot> getTimeSlotList(Integer setupSeq) {
        Criteria criteria = easyCreateCriteria(EnterCldrTimeSlot.class);
        criteria.add(Restrictions.eq("setupSeq", setupSeq));
        criteria.addOrder(Order.asc("sortNo"));
        List<EnterCldrTimeSlot> list = criteria.list();
        return list != null && !list.isEmpty() ? list : null;
    }

    public EnterCldrTimeSlot getTimeSlot(Integer seq) {
        Criteria criteria = easyCreateCriteria(EnterCldrTimeSlot.class);
        criteria.add(Restrictions.eq("slotSeq", seq));
        List<EnterCldrTimeSlot> list = criteria.list();
        return list != null && !list.isEmpty() ? list.get(0) : null;
    }

    public void setTimeSlotRegist(EnterCldrTimeSlot timeSlot) { save(timeSlot); }
    public void setTimeSlotUpdt(EnterCldrTimeSlot timeSlot) { update(timeSlot); }
    public void setTimeSlotDelete(EnterCldrTimeSlot timeSlot) { delete(timeSlot); }

    /**
     * 시간 슬롯 행 비관적 잠금 조회 (SELECT FOR UPDATE).
     * 동시 신청 직렬화를 위해 tryArtclRegist() 내에서만 사용.
     * 호출 시 반드시 @Transactional 컨텍스트 안에 있어야 한다.
     */
    public EnterCldrTimeSlot getTimeSlotWithLock(Integer slotSeq) {
        Criteria criteria = easyCreateCriteria(EnterCldrTimeSlot.class);
        criteria.add(Restrictions.eq("slotSeq", slotSeq));
        criteria.setLockMode(LockMode.PESSIMISTIC_WRITE);
        List<EnterCldrTimeSlot> list = criteria.list();
        return list != null && !list.isEmpty() ? list.get(0) : null;
    }

    /* ===================== 대상별 항목 ===================== */

    public List<EnterCldrTargetItem> getTargetItemList(Integer setupSeq) {
        Criteria criteria = easyCreateCriteria(EnterCldrTargetItem.class);
        criteria.add(Restrictions.eq("setupSeq", setupSeq));
        criteria.addOrder(Order.asc("sortNo"));
        List<EnterCldrTargetItem> list = criteria.list();
        return list != null && !list.isEmpty() ? list : null;
    }

    public EnterCldrTargetItem getTargetItem(Integer seq) {
        Criteria criteria = easyCreateCriteria(EnterCldrTargetItem.class);
        criteria.add(Restrictions.eq("targetItemSeq", seq));
        List<EnterCldrTargetItem> list = criteria.list();
        return list != null && !list.isEmpty() ? list.get(0) : null;
    }

    public void setTargetItemRegist(EnterCldrTargetItem targetItem) { save(targetItem); }
    public void setTargetItemUpdt(EnterCldrTargetItem targetItem) { update(targetItem); }
    public void setTargetItemDelete(EnterCldrTargetItem targetItem) { delete(targetItem); }

    /* ===================== 폼 항목 ===================== */

    public List<EnterCldrFormItem> getFormItemList(Integer setupSeq) {
        Criteria criteria = easyCreateCriteria(EnterCldrFormItem.class);
        criteria.add(Restrictions.eq("setupSeq", setupSeq));
        criteria.addOrder(Order.asc("sortNo"));
        List<EnterCldrFormItem> list = criteria.list();
        return list != null && !list.isEmpty() ? list : null;
    }

    public EnterCldrFormItem getFormItem(Integer seq) {
        Criteria criteria = easyCreateCriteria(EnterCldrFormItem.class);
        criteria.add(Restrictions.eq("formItemSeq", seq));
        List<EnterCldrFormItem> list = criteria.list();
        return list != null && !list.isEmpty() ? list.get(0) : null;
    }

    public void setFormItemRegist(EnterCldrFormItem formItem) { save(formItem); }
    public void setFormItemUpdt(EnterCldrFormItem formItem) { update(formItem); }
    public void setFormItemDelete(EnterCldrFormItem formItem) { delete(formItem); }

    /* ===================== 휴일 ===================== */

    public List<EnterCldrHoliday> getHolidayList(Integer setupSeq) {
        Criteria criteria = easyCreateCriteria(EnterCldrHoliday.class);
        criteria.add(Restrictions.eq("setupSeq", setupSeq));
        criteria.addOrder(Order.asc("holidayDt"));
        List<EnterCldrHoliday> list = criteria.list();
        return list != null && !list.isEmpty() ? list : null;
    }

    public EnterCldrHoliday getHoliday(Integer seq) {
        Criteria criteria = easyCreateCriteria(EnterCldrHoliday.class);
        criteria.add(Restrictions.eq("holidaySeq", seq));
        List<EnterCldrHoliday> list = criteria.list();
        return list != null && !list.isEmpty() ? list.get(0) : null;
    }

    public void setHolidayRegist(EnterCldrHoliday holiday) { save(holiday); }
    public void setHolidayUpdt(EnterCldrHoliday holiday) { update(holiday); }
    public void setHolidayDelete(EnterCldrHoliday holiday) { delete(holiday); }

    /* ===================== 신청 ===================== */

    public List<EnterCldrArtcl> getArtclList(EnterCldrApplyVo vo) {
        Criteria criteria = easyCreateCriteria(EnterCldrArtcl.class, vo.getPage(), vo.getRow());
        criteria.add(Restrictions.eq("delYn", "N"));
        applyArtclSearchConditions(criteria, vo);
        criteria.addOrder(Order.desc("rgsde"));
        List<EnterCldrArtcl> list = criteria.list();
        return list != null && !list.isEmpty() ? list : null;
    }

    public Long getArtclListCount(EnterCldrApplyVo vo) {
        Criteria criteria = easyCreateCriteriaCount(EnterCldrArtcl.class, "artclSeq");
        criteria.add(Restrictions.eq("delYn", "N"));
        applyArtclSearchConditions(criteria, vo);
        List<Long> list = criteria.list();
        return (list != null && !list.isEmpty()) ? list.get(0) : 0L;
    }

    private static final Set<String> ALLOWED_FIND_TYPES = new HashSet<>(
        Arrays.asList("rqstNm", "rqstMl", "schNm")
    );

    private void applyArtclSearchConditions(Criteria criteria, EnterCldrApplyVo vo) {

        criteria.add(Restrictions.eq("setupSeq", vo.getFindSetupSeq()));

        if (vo.getFindArtclStatus() != null && !vo.getFindArtclStatus().isEmpty()) {
            criteria.add(Restrictions.eq("artclStatus", vo.getFindArtclStatus()));
        }
        if (vo.getFindType() != null && !vo.getFindType().isEmpty()
                && vo.getFindWord() != null && !vo.getFindWord().isEmpty()
                && ALLOWED_FIND_TYPES.contains(vo.getFindType())) {
            criteria.add(Restrictions.like(vo.getFindType(), "%" + vo.getFindWord() + "%"));
        }
    }

    public EnterCldrArtcl getArtcl(Integer setupSeq, Integer artclSeq) {
        Criteria criteria = easyCreateCriteria(EnterCldrArtcl.class);
        criteria.add(Restrictions.eq("setupSeq", setupSeq));
        criteria.add(Restrictions.eq("artclSeq", artclSeq));
        List<EnterCldrArtcl> list = criteria.list();
        return list != null && !list.isEmpty() ? list.get(0) : null;
    }

    public void setArtclRegist(EnterCldrArtcl artcl) { save(artcl); }
    public void setArtclUpdt(EnterCldrArtcl artcl) { update(artcl); }
    public void setArtclDelete(EnterCldrArtcl artcl) { delete(artcl); }

    /**
     * 중복 신청 건수 조회 (설정당 이름+전화 기준).
     * CANCELED 제외, 관리자 삭제(DEL_YN=Y) 제외
     */
    public long getArtclDupCount(Integer setupSeq, String rqstNm, String rqstTel) {
        String encRqstTel = CryptoUtil.encodeARIACrypto(rqstTel);
        String hql = "SELECT COUNT(a.artclSeq) FROM EnterCldrArtcl a"
            + " WHERE a.setupSeq = ? AND a.rqstNm = ? AND a.rqstTel = ?"
            + " AND a.artclStatus != 'CANCELED' AND a.delYn = 'N'";
        List<Long> result = easyCreateQuery(hql, new Object[] {setupSeq, rqstNm, encRqstTel});
        return (result != null && !result.isEmpty()) ? result.get(0) : 0L;
    }

    /**
     * 슬롯 유효 접수 건수 조회 (잔여 접수 건수 계산용).
     * WAIT/APPROVED 만 집계 (REJECTED/CANCELED/삭제 제외)
     * DB 방언 독립: TO_CHAR() 대신 날짜 범위(>= startOfDay AND < nextDay) 사용
     */
    public long getArtclSlotCount(Integer timeSlotSeq, String artclDt) {
        Date[] range = toDayRange(artclDt);
        if (range == null) return 0L;
        String hql = "SELECT COUNT(a.artclSeq) FROM EnterCldrArtcl a"
            + " WHERE a.timeSlotSeq = ?"
            + " AND a.artclDt >= ? AND a.artclDt < ?"
            + " AND a.artclStatus IN ('WAIT', 'APPROVED') AND a.delYn = 'N'";
        List<Long> result = easyCreateQuery(hql, new Object[] {timeSlotSeq, range[0], range[1]});
        return (result != null && !result.isEmpty()) ? result.get(0) : 0L;
    }

    /**
     * 슬롯 유효 접수 건수 조회 (특정 artcl 제외) — 관리자 수정 화면에서 본인 신청 제외 후 잔여 계산용.
     * DB 방언 독립: TO_CHAR() 대신 날짜 범위(>= startOfDay AND < nextDay) 사용
     */
    public long getArtclSlotCountExcluding(Integer timeSlotSeq, String artclDt, Integer excludeArtclSeq) {
        Date[] range = toDayRange(artclDt);
        if (range == null) return 0L;
        String hql = "SELECT COUNT(a.artclSeq) FROM EnterCldrArtcl a"
            + " WHERE a.timeSlotSeq = ?"
            + " AND a.artclDt >= ? AND a.artclDt < ?"
            + " AND a.artclStatus IN ('WAIT', 'APPROVED') AND a.delYn = 'N'"
            + " AND a.artclSeq != ?";
        List<Long> result = easyCreateQuery(hql, new Object[] {timeSlotSeq, range[0], range[1], excludeArtclSeq});
        return (result != null && !result.isEmpty()) ? result.get(0) : 0L;
    }

    /**
     * "yyyy-MM-dd" 문자열을 [해당일 00:00:00, 다음날 00:00:00) 범위로 변환.
     * DB 날짜 비교를 DB 함수 없이 처리하기 위한 헬퍼.
     */
    private Date[] toDayRange(String artclDt) {
        if (artclDt == null || artclDt.isEmpty()) return null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setLenient(false);
            Calendar cal = Calendar.getInstance();
            cal.setTime(sdf.parse(artclDt));
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            Date startOfDay = cal.getTime();
            cal.add(Calendar.DATE, 1);
            Date nextDay = cal.getTime();
            return new Date[] {startOfDay, nextDay};
        } catch (Exception e) {
            LOG.warn("날짜 범위 변환 실패: {}", artclDt);
            return null;
        }
    }

    /** 사용자 본인 신청 목록 조회 (이름+전화 기준, 관리자 삭제 제외) */
    public List<EnterCldrArtcl> getArtclListByUser(Integer setupSeq, String rqstNm, String rqstTel) {
        String encRqstTel = CryptoUtil.encodeARIACrypto(rqstTel);
        Criteria criteria = easyCreateCriteria(EnterCldrArtcl.class);
        criteria.add(Restrictions.eq("setupSeq", setupSeq));
        criteria.add(Restrictions.eq("rqstNm", rqstNm));
        criteria.add(Restrictions.eq("rqstTel", encRqstTel));
        criteria.add(Restrictions.eq("delYn", "N"));
        criteria.addOrder(Order.desc("rgsde"));
        List<EnterCldrArtcl> list = criteria.list();
        return list != null && !list.isEmpty() ? list : null;
    }

    /* ===================== 신청별 대상 동반인원 ===================== */

    public List<EnterCldrArtclTarget> getArtclTargetList(Integer artclSeq) {
        Criteria criteria = easyCreateCriteria(EnterCldrArtclTarget.class);
        criteria.add(Restrictions.eq("artclSeq", artclSeq));
        List<EnterCldrArtclTarget> list = criteria.list();
        return list != null && !list.isEmpty() ? list : null;
    }

    public void setArtclTargetRegist(EnterCldrArtclTarget target) { save(target); }
    public void setArtclTargetDelete(EnterCldrArtclTarget target) { delete(target); }

    public void deleteArtclTargetByArtcl(Integer artclSeq) {
        bulkUpdate("DELETE FROM EnterCldrArtclTarget WHERE artclSeq = ?", artclSeq);
    }

}
