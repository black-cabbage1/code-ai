package k2web.module.enterCldrApply.service.impl;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import jxl.Workbook;
import jxl.format.UnderlineStyle;
import jxl.write.Alignment;
import jxl.write.Border;
import jxl.write.BorderLineStyle;
import jxl.write.Colour;
import jxl.write.Label;
import jxl.write.VerticalAlignment;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

import javax.annotation.Resource;

import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import k2web.com.cop.fnct.service.FnctService;
import k2web.com.cop.fnct.service.model.FnctSkin;
import k2web.com.cop.fnct.service.model.FnctSkinUse;
import k2web.com.cop.fnct.service.model.FnctUse;
import k2web.com.cop.fnctmngr.service.FnctMngrService;
import k2web.com.cop.fnctmngr.service.FnctMngrVo;
import k2web.com.cop.fnctmngr.service.model.FnctMngr;
import k2web.com.util.FileUtil;
import k2web.com.util.K2Path;
import k2web.com.util.K2Properties;
import k2web.com.util.TimeInfoUtil;
import k2web.module.enterCldrApply.service.EnterCldrApplyService;
import k2web.module.enterCldrApply.service.EnterCldrApplyVo;
import k2web.module.enterCldrApply.service.model.EnterCldrArtcl;
import k2web.module.enterCldrApply.service.model.EnterCldrArtclTarget;
import k2web.module.enterCldrApply.service.model.EnterCldrAtchmnfl;
import k2web.module.enterCldrApply.service.model.EnterCldrFormItem;
import k2web.module.enterCldrApply.service.model.EnterCldrHoliday;
import k2web.module.enterCldrApply.service.model.EnterCldrSetup;
import k2web.module.enterCldrApply.service.model.EnterCldrTargetItem;
import k2web.module.enterCldrApply.service.model.EnterCldrTimeSlot;

@Service("EnterCldrApplyService")
public class EnterCldrApplyServiceImpl extends EgovAbstractServiceImpl implements EnterCldrApplyService {

    private static final Logger LOG = LoggerFactory.getLogger(EnterCldrApplyServiceImpl.class);

    @Resource(name = "EnterCldrApplyDao")
    private EnterCldrApplyDao enterCldrApplyDao;

    /** k2web.com.cop.fnct.service.FnctService */
	@Resource( name = "FnctService" )
	private FnctService fnctService;
	
	/** k2web.com.cop.fnctmngr.service.FnctMngrService */
	@Resource( name = "FnctMngrService" )
	private FnctMngrService fnctMngrService;
	
	
    /* ===================== 설정 ===================== */

    @Override
    public List<EnterCldrSetup> getSetupList(EnterCldrApplyVo vo, boolean isManage) {
    	List<EnterCldrSetup> setupList = enterCldrApplyDao.getSetupList(vo, isManage);
    	if( isManage && setupList!=null && setupList.size()>0 )	{
			String skinId = null;
			String skinNm = null;
			FnctSkinUse fnctSkinUse = null;
			FnctMngrVo fnctMngrVo = null;
			List<FnctMngr> fnctMngrList = null;
			for( EnterCldrSetup setup : setupList ) {
				
				//스킨아이디 및 스킨 명 셋팅
				fnctSkinUse = fnctService.getMultiFnctSkinUse( vo.getSiteId(), FNCT_ID, "" + setup.getSetupSeq() );
				if( fnctSkinUse != null ) {
					skinId = fnctSkinUse.getSkinId();
					FnctSkin fnctSkin = fnctService.getFnctSkinUpdtView( skinId );
					if( fnctSkin != null ) {
						skinNm = fnctSkin.getSkinNm();
						setup.setSkinId( skinId );
						setup.setSkinNm( skinNm );
					}
				}
				
				//관리자 셋팅
				fnctMngrVo = new FnctMngrVo();
				fnctMngrVo.setSiteId( vo.getSiteId() );
				fnctMngrVo.setFnctId( FNCT_ID );
				fnctMngrVo.setFnctNo( "" + setup.getSetupSeq() );
				fnctMngrList = fnctMngrService.getFnctMngrList( fnctMngrVo );
				setup.setFnctMngrList( fnctMngrList );
			}
		}
    	
        return setupList;
    }

    @Override
    public EnterCldrSetup getSetup(Integer seq) {
        return enterCldrApplyDao.getSetup(seq);
    }

    @Override
    @Transactional
    public void setSetupRegist(EnterCldrSetup setup, String rgsId) {
        setup.setRecvStartDt(applyHour(setup.getRecvStartDt(), setup.getRecvStartHour()));
        setup.setRecvEndDt(applyHour(setup.getRecvEndDt(), setup.getRecvEndHour()));
        setup.setModStartDt(applyHour(setup.getModStartDt(), setup.getModStartHour()));
        setup.setModEndDt(applyHour(setup.getModEndDt(), setup.getModEndHour()));
        Date now = TimeInfoUtil.getDate();
        setup.setRgsde(now);
        setup.setRgsId(rgsId);
        setup.setUpdde(now);
        setup.setUpdId(rgsId);
        enterCldrApplyDao.setSetupRegist(setup);
        
        //WZD_FNCT_USE INSERT!!
  		FnctUse fnctUse = new FnctUse();
  		fnctUse.setSiteId( setup.getSiteId() );
  		fnctUse.setFnctId( FNCT_ID );
  		fnctUse.setFnctNo( "" + setup.getSetupSeq() );
  		fnctUse.setFnctDetailNm( setup.getSetupNm() );
  		fnctService.setFnctUseRegist( fnctUse );
  		
  		//WZD_FNCT_SKIN_USE INSERT!!
  		FnctSkin basisSkin = fnctService.getBasisFnctSkin( FNCT_ID );
  		FnctSkinUse fnctSkinUse = new FnctSkinUse();
  		fnctSkinUse.setSiteId( setup.getSiteId() );
  		fnctSkinUse.setFnctId( FNCT_ID );
  		fnctSkinUse.setFnctNo( "" + setup.getSetupSeq() );
  		fnctSkinUse.setSkinId( basisSkin.getSkinId() );
  		fnctService.setFnctSkinUseRegist( fnctSkinUse );

  		// 고정 신청항목 4개 자동 삽입
  		String[][] fixedItems = {
  		    {"이름",   "RQST_NM",  "Y"},
  		    {"휴대전화", "RQST_TEL", "Y"},
  		    {"이메일",  "RQST_ML",  "N"},
  		    {"고교검색", "SCHOOL",   "N"}
  		};
  		for (int i = 0; i < fixedItems.length; i++) {
  		    EnterCldrFormItem fi = new EnterCldrFormItem();
  		    fi.setSetupSeq(setup.getSetupSeq());
  		    fi.setItemNm(fixedItems[i][0]);
  		    fi.setItemType(fixedItems[i][1]);
  		    fi.setRequiredYn(fixedItems[i][2]);
  		    fi.setFixedYn("Y");
  		    fi.setSortNo(i + 1);
  		    fi.setRgsde(now);
  		    fi.setRgsId(rgsId);
  		    fi.setUpdde(now);
  		    fi.setUpdId(rgsId);
  		    enterCldrApplyDao.setFormItemRegist(fi);
  		}
    }

	@Override
    @Transactional
    public void setSetupUpdt(EnterCldrSetup setup, String updId) {
        EnterCldrSetup db = enterCldrApplyDao.getSetup(setup.getSetupSeq());
        if (db != null) {
            db.setSetupNm(setup.getSetupNm());
            db.setUseYn(setup.getUseYn());
            db.setMonYn(setup.getMonYn()); db.setTueYn(setup.getTueYn());
            db.setWedYn(setup.getWedYn()); db.setThuYn(setup.getThuYn());
            db.setFriYn(setup.getFriYn()); db.setSatYn(setup.getSatYn());
            db.setSunYn(setup.getSunYn());
            db.setCompanionUseYn(setup.getCompanionUseYn());
            db.setTargetCompUseYn(setup.getTargetCompUseYn());
            db.setIntro(setup.getIntro());
            db.setApplyTarget(setup.getApplyTarget());
            db.setLocation(setup.getLocation());
            db.setContent(setup.getContent());
            db.setEvtStartDt(setup.getEvtStartDt());
            db.setEvtEndDt(setup.getEvtEndDt());
            db.setRecvStartDt(applyHour(setup.getRecvStartDt(), setup.getRecvStartHour()));
            db.setRecvEndDt(applyHour(setup.getRecvEndDt(), setup.getRecvEndHour()));
            db.setModStartDt(applyHour(setup.getModStartDt(), setup.getModStartHour()));
            db.setModEndDt(applyHour(setup.getModEndDt(), setup.getModEndHour()));
            db.setDplcAplyPsblYn(setup.getDplcAplyPsblYn());
            db.setPopupMsg(setup.getPopupMsg());
            db.setMngInfo(setup.getMngInfo());
            db.setPrivacyPurpose(setup.getPrivacyPurpose());
            db.setPrivacyItems(setup.getPrivacyItems());
            db.setPrivacyPeriod(setup.getPrivacyPeriod());
            db.setUpdde(TimeInfoUtil.getDate());
            db.setUpdId(updId);
            enterCldrApplyDao.setSetupUpdt(db);
            
            //WZD_FNCT_USE UPDATE!!
    		FnctUse fnctUse = fnctService.getFnctUse( setup.getSiteId(), FNCT_ID, "" + setup.getSetupSeq() );
    		fnctUse.setFnctDetailNm( setup.getSetupNm() );
    		fnctService.setFnctUseUpdt( fnctUse );
        }
    }

    @Override
    @Transactional
    public void setSetupDelete(Integer seq) {
        EnterCldrSetup db = enterCldrApplyDao.getSetup(seq);
        if (db != null) {
            enterCldrApplyDao.setSetupDelete(db);
        }
    }

    /* ===================== 첨부파일 ===================== */

    @Override
    public List<EnterCldrAtchmnfl> getAtchmnflList(Integer setupSeq) {
        return enterCldrApplyDao.getAtchmnflList(setupSeq);
    }

    @Override
    public EnterCldrAtchmnfl getAtchmnfl(Integer seq) {
        return enterCldrApplyDao.getAtchmnfl(seq);
    }

    @Override
    @Transactional
    public void setAtchmnflRegist(String siteId, EnterCldrAtchmnfl atchmnfl, String rgsId) {
    	if(atchmnfl != null) {
    		String tempPath = K2Properties.getServerContextRoot() + "/sites/" + siteId + "/tempFile/" + atchmnfl.getFilePath() + "/";
			String atchPath = K2Path.getAtchmnflPath(siteId, FNCT_ID, false);
			FileUtil.move(tempPath + atchmnfl.getChangeNm(), atchPath +  atchmnfl.getChangeNm());
    		
			atchmnfl.setFilePath(atchPath);
    		atchmnfl.setRgsde(TimeInfoUtil.getDate());
            atchmnfl.setRgsId(rgsId);
            enterCldrApplyDao.setAtchmnflRegist(atchmnfl);
    	}
    }

    @Override
    @Transactional
    public void setAtchmnflDelete(String siteId, Integer seq) {
        EnterCldrAtchmnfl db = enterCldrApplyDao.getAtchmnfl(seq);
        if (db != null) {
        	String atchPath = db.getFilePath();
            String atchFile = db.getChangeNm();
            
            enterCldrApplyDao.setAtchmnflDelete(db);
            
            FileUtil.delete(atchPath + atchFile);
        }
    }

    /* ===================== 시간 슬롯 ===================== */

    @Override
    public List<EnterCldrTimeSlot> getTimeSlotList(Integer setupSeq) {
        return enterCldrApplyDao.getTimeSlotList(setupSeq);
    }

    @Override
    public EnterCldrTimeSlot getTimeSlot(Integer seq) {
        return enterCldrApplyDao.getTimeSlot(seq);
    }

    @Override
    @Transactional
    public void setTimeSlotRegist(EnterCldrTimeSlot timeSlot, String rgsId) {
        Date now = TimeInfoUtil.getDate();
        timeSlot.setRgsde(now);
        timeSlot.setRgsId(rgsId);
        timeSlot.setUpdde(now);
        timeSlot.setUpdId(rgsId);
        enterCldrApplyDao.setTimeSlotRegist(timeSlot);
    }

    @Override
    @Transactional
    public void setTimeSlotUpdt(EnterCldrTimeSlot timeSlot, String updId) {
        EnterCldrTimeSlot db = enterCldrApplyDao.getTimeSlot(timeSlot.getSlotSeq());
        if (db != null) {
            db.setApplyTime(timeSlot.getApplyTime());
            db.setCapacity(timeSlot.getCapacity());
            db.setSortNo(timeSlot.getSortNo());
            db.setUpdde(TimeInfoUtil.getDate());
            db.setUpdId(updId);
            enterCldrApplyDao.setTimeSlotUpdt(db);
        }
    }

    @Override
    @Transactional
    public void setTimeSlotDelete(Integer seq) {
        EnterCldrTimeSlot db = enterCldrApplyDao.getTimeSlot(seq);
        if (db != null) {
            enterCldrApplyDao.setTimeSlotDelete(db);
        }
    }

    /* ===================== 대상별 항목 ===================== */

    @Override
    public List<EnterCldrTargetItem> getTargetItemList(Integer setupSeq) {
        return enterCldrApplyDao.getTargetItemList(setupSeq);
    }

    @Override
    @Transactional
    public void setTargetItemRegist(EnterCldrTargetItem targetItem, String rgsId) {
        targetItem.setRgsde(TimeInfoUtil.getDate());
        targetItem.setRgsId(rgsId);
        enterCldrApplyDao.setTargetItemRegist(targetItem);
    }

    @Override
    @Transactional
    public void setTargetItemUpdt(EnterCldrTargetItem targetItem, String updId) {
        EnterCldrTargetItem db = enterCldrApplyDao.getTargetItem(targetItem.getTargetItemSeq());
        if (db != null) {
            db.setTargetNm(targetItem.getTargetNm());
            db.setSortNo(targetItem.getSortNo());
            enterCldrApplyDao.setTargetItemUpdt(db);
        }
    }

    @Override
    @Transactional
    public void setTargetItemDelete(Integer seq) {
        EnterCldrTargetItem db = enterCldrApplyDao.getTargetItem(seq);
        if (db != null) {
            enterCldrApplyDao.setTargetItemDelete(db);
        }
    }

    /* ===================== 폼 항목 ===================== */

    @Override
    public List<EnterCldrFormItem> getFormItemList(Integer setupSeq) {
        return enterCldrApplyDao.getFormItemList(setupSeq);
    }

    @Override
    public EnterCldrFormItem getFormItem(Integer seq) {
        return enterCldrApplyDao.getFormItem(seq);
    }

    @Override
    @Transactional
    public void setFormItemRegist(EnterCldrFormItem formItem, String rgsId) {
        if (formItem.getFixedYn() == null) formItem.setFixedYn("N");
        Date now = TimeInfoUtil.getDate();
        formItem.setRgsde(now);
        formItem.setRgsId(rgsId);
        formItem.setUpdde(now);
        formItem.setUpdId(rgsId);
        enterCldrApplyDao.setFormItemRegist(formItem);
    }

    @Override
    @Transactional
    public void setFormItemUpdt(EnterCldrFormItem formItem, String updId) {
        EnterCldrFormItem db = enterCldrApplyDao.getFormItem(formItem.getFormItemSeq());
        if (db != null) {
            db.setItemNm(formItem.getItemNm());
            if (!"Y".equals(db.getFixedYn())) {
                db.setItemType(formItem.getItemType());
            }
            db.setRequiredYn(formItem.getRequiredYn());
            db.setItemOptions(formItem.getItemOptions());
            db.setSortNo(formItem.getSortNo());
            db.setUpdde(TimeInfoUtil.getDate());
            db.setUpdId(updId);
            enterCldrApplyDao.setFormItemUpdt(db);
        }
    }

    @Override
    @Transactional
    public void setFormItemDelete(Integer seq) {
        EnterCldrFormItem db = enterCldrApplyDao.getFormItem(seq);
        if (db != null) {
            enterCldrApplyDao.setFormItemDelete(db);
        }
    }

    /* ===================== 휴일 ===================== */

    @Override
    public List<EnterCldrHoliday> getHolidayList(Integer setupSeq) {
        return enterCldrApplyDao.getHolidayList(setupSeq);
    }

    @Override
    @Transactional
    public void setHolidayRegist(EnterCldrHoliday holiday, String rgsId) {
        Date now = TimeInfoUtil.getDate();
        holiday.setRgsde(now);
        holiday.setRgsId(rgsId);
        enterCldrApplyDao.setHolidayRegist(holiday);
    }

    @Override
    @Transactional
    public void setHolidayUpdt(EnterCldrHoliday holiday, String updId) {
        EnterCldrHoliday db = enterCldrApplyDao.getHoliday(holiday.getHolidaySeq());
        if (db != null) {
            db.setHolidayDt(holiday.getHolidayDt());
            db.setHolidayNm(holiday.getHolidayNm());
            enterCldrApplyDao.setHolidayUpdt(db);
        }
    }

    @Override
    @Transactional
    public void setHolidayDelete(Integer seq) {
        EnterCldrHoliday db = enterCldrApplyDao.getHoliday(seq);
        if (db != null) {
            enterCldrApplyDao.setHolidayDelete(db);
        }
    }

    /* ===================== 신청 ===================== */

    @Override
    public List<EnterCldrArtcl> getArtclList(EnterCldrApplyVo vo) {
        List<EnterCldrArtcl> list = enterCldrApplyDao.getArtclList(vo);
        if (list != null) {
            for (EnterCldrArtcl artcl : list) {
                EnterCldrTimeSlot slot = enterCldrApplyDao.getTimeSlot(artcl.getTimeSlotSeq());
                if (slot != null) {
                    artcl.setApplyTime(slot.getApplyTime());
                }
                artcl.setTargetList(enterCldrApplyDao.getArtclTargetList(artcl.getArtclSeq()));
            }
        }
        return list;
    }

    @Override
    public Long getArtclListCount(EnterCldrApplyVo vo) {
        return enterCldrApplyDao.getArtclListCount(vo);
    }

    @Override
    public EnterCldrArtcl getArtcl(Integer setupSeq, Integer artclSeq) {
        EnterCldrArtcl artcl = enterCldrApplyDao.getArtcl(setupSeq, artclSeq);
        if (artcl != null) {
            EnterCldrTimeSlot slot = enterCldrApplyDao.getTimeSlot(artcl.getTimeSlotSeq());
            if (slot != null) {
                artcl.setApplyTime(slot.getApplyTime());
            }
            List<EnterCldrArtclTarget> targetList = enterCldrApplyDao.getArtclTargetList(artcl.getArtclSeq());
            if (targetList != null) {
                for (EnterCldrArtclTarget target : targetList) {
                    if (target.getTargetNm() == null || target.getTargetNm().isEmpty()) {
                        EnterCldrTargetItem item = enterCldrApplyDao.getTargetItem(target.getTargetItemSeq());
                        if (item != null) target.setTargetNm(item.getTargetNm());
                    }
                }
            }
            artcl.setTargetList(targetList);
            artcl.setDynamicFormItems(buildDynamicFormItems(artcl));
        }
        return artcl;
    }

    @Override
    @Transactional
    public void setArtclRegist(EnterCldrArtcl artcl) {
        artcl.setArtclStatus("WAIT");
        artcl.setDelYn("N");
        artcl.setRgsde(TimeInfoUtil.getDate());
        artcl.setUpdde(artcl.getRgsde());
        enterCldrApplyDao.setArtclRegist(artcl);

        List<EnterCldrArtclTarget> targetList = artcl.getTargetList();
        if (targetList != null) {
            for (EnterCldrArtclTarget target : targetList) {
                target.setArtclSeq(artcl.getArtclSeq());
                enterCldrApplyDao.setArtclTargetRegist(target);
            }
        }
    }

    /**
     * 신청 등록 — 슬롯 행 비관적 잠금(SELECT FOR UPDATE) 후 정원 재확인 + 저장을 원자적으로 처리.
     * 동시에 여러 사용자가 마지막 자리를 신청해도 단 한 명만 성공한다.
     *
     * 처리 순서:
     *   1) 슬롯 행 잠금 → 같은 슬롯의 다른 신청 트랜잭션은 여기서 대기
     *   2) 잠금 상태에서 현재 접수 건수 재확인
     *   3) 정원 이내면 등록, 초과면 실패 메시지 반환
     *   4) 트랜잭션 커밋 시 잠금 해제 → 대기 중인 다음 트랜잭션 실행
     *
     * @return null: 등록 성공, 문자열: 실패 사유 메시지
     */
    @Override
    @Transactional
    public String tryArtclRegist(EnterCldrArtcl artcl) {
        String artclDtStr = artcl.getArtclDt() != null
            ? new SimpleDateFormat("yyyy-MM-dd").format(artcl.getArtclDt()) : "";

        // 1. 슬롯 행 비관적 잠금 — 이 시점부터 같은 슬롯의 다른 트랜잭션은 대기
        EnterCldrTimeSlot slot = enterCldrApplyDao.getTimeSlotWithLock(artcl.getTimeSlotSeq());
        if (slot == null) {
            return "유효하지 않은 시간대입니다.";
        }

        // 2. 잠금 상태에서 현재 접수 건수 재확인 (정원 0 = 무제한)
        if (slot.getCapacity() != null && slot.getCapacity() > 0) {
            long applied = enterCldrApplyDao.getArtclSlotCount(artcl.getTimeSlotSeq(), artclDtStr);
            if (applied >= slot.getCapacity()) {
                return "선택하신 시간대의 정원이 마감되었습니다.";
            }
        }

        // 3. 정원 이내 — 등록
        artcl.setArtclStatus("WAIT");
        artcl.setDelYn("N");
        Date now = TimeInfoUtil.getDate();
        artcl.setRgsde(now);
        artcl.setUpdde(now);
        enterCldrApplyDao.setArtclRegist(artcl);

        List<EnterCldrArtclTarget> targetList = artcl.getTargetList();
        if (targetList != null) {
            for (EnterCldrArtclTarget target : targetList) {
                target.setArtclSeq(artcl.getArtclSeq());
                enterCldrApplyDao.setArtclTargetRegist(target);
            }
        }
        return null; // 성공
    }

    @Override
    @Transactional
    public void setArtclUpdt(EnterCldrArtcl artcl, boolean isManage) {
        EnterCldrArtcl db = enterCldrApplyDao.getArtcl(artcl.getSetupSeq(), artcl.getArtclSeq());
        if (db != null) {
            db.setTimeSlotSeq(artcl.getTimeSlotSeq());
            db.setArtclDt(artcl.getArtclDt());
            db.setRqstNm(artcl.getRqstNm());
            db.setRqstTel(artcl.getRqstTel());
            db.setRqstMl(artcl.getRqstMl());
            db.setSchCd(artcl.getSchCd());
            db.setSchNm(artcl.getSchNm());
            db.setSchLc(artcl.getSchLc());
            db.setSchTp(artcl.getSchTp());
            db.setCompanionCnt(artcl.getCompanionCnt());
            db.setAdditm1(artcl.getAdditm1());   db.setAdditm2(artcl.getAdditm2());
            db.setAdditm3(artcl.getAdditm3());   db.setAdditm4(artcl.getAdditm4());
            db.setAdditm5(artcl.getAdditm5());   db.setAdditm6(artcl.getAdditm6());
            db.setAdditm7(artcl.getAdditm7());   db.setAdditm8(artcl.getAdditm8());
            db.setAdditm9(artcl.getAdditm9());   db.setAdditm10(artcl.getAdditm10());
            db.setAdditm11(artcl.getAdditm11()); db.setAdditm12(artcl.getAdditm12());
            db.setAdditm13(artcl.getAdditm13()); db.setAdditm14(artcl.getAdditm14());
            db.setAdditm15(artcl.getAdditm15());
            
            if(!isManage) {
            	//사용자가 수정했을 경우에만 수정일시 갱신
            	db.setUpdde(TimeInfoUtil.getDate());
            }
            
            enterCldrApplyDao.setArtclUpdt(db);

            enterCldrApplyDao.deleteArtclTargetByArtcl(db.getArtclSeq());
            db.setTargetList(artcl.getTargetList());
            List<EnterCldrArtclTarget> targetList = db.getTargetList();
            if (targetList != null) {
                for (EnterCldrArtclTarget target : targetList) {
                    target.setArtclSeq(db.getArtclSeq());
                    enterCldrApplyDao.setArtclTargetRegist(target);
                }
            }
        }
    }

    @Override
    @Transactional
    public void setArtclStatusUpdt(Integer setupSeq, Integer artclSeq, String artclStatus, boolean isManage) {
        EnterCldrArtcl db = enterCldrApplyDao.getArtcl(setupSeq, artclSeq);
        if (db != null) {
            db.setArtclStatus(artclStatus);
            db.setStatusUpdde(TimeInfoUtil.getDate());
            enterCldrApplyDao.setArtclUpdt(db);
        }
    }

    @Override
    @Transactional
    public void setArtclDelete(Integer setupSeq, Integer artclSeq) {
        EnterCldrArtcl db = enterCldrApplyDao.getArtcl(setupSeq, artclSeq);
        if (db != null) {
            db.setDelYn("Y");
            db.setUpdde(TimeInfoUtil.getDate());
            enterCldrApplyDao.setArtclUpdt(db);
        }
    }

    @Override
    public List<EnterCldrArtcl> getArtclListByUser(Integer setupSeq, String rqstNm, String rqstTel) {
        List<EnterCldrArtcl> list = enterCldrApplyDao.getArtclListByUser(setupSeq, rqstNm, rqstTel);
        if (list != null) {
            for (EnterCldrArtcl artcl : list) {
                EnterCldrTimeSlot slot = enterCldrApplyDao.getTimeSlot(artcl.getTimeSlotSeq());
                if (slot != null) artcl.setApplyTime(slot.getApplyTime());
            }
        }
        return list;
    }

    @Override
    public boolean isArtclDuplicated(Integer setupSeq, String rqstNm, String rqstTel) {
        return enterCldrApplyDao.getArtclDupCount(setupSeq, rqstNm, rqstTel) > 0;
    }

    @Override
    public int getTimeSlotRemainCnt(Integer timeSlotSeq, String artclDt) {
        EnterCldrTimeSlot slot = enterCldrApplyDao.getTimeSlot(timeSlotSeq);
        if (slot == null) return 0;
        int capacity = slot.getCapacity() != null ? slot.getCapacity() : 0;
        if (capacity == 0) return Integer.MAX_VALUE; // 0: 무제한
        long applied = enterCldrApplyDao.getArtclSlotCount(timeSlotSeq, artclDt);
        return (int) Math.max(0, capacity - applied);
    }

    @Override
    public List<EnterCldrTimeSlot> getTimeSlotListForEdit(Integer setupSeq, Integer artclSeq, String artclDt) {
        List<EnterCldrTimeSlot> slots = enterCldrApplyDao.getTimeSlotList(setupSeq);
        if (slots == null) return new ArrayList<>();
        for (EnterCldrTimeSlot slot : slots) {
            if (slot.getCapacity() != null && slot.getCapacity() > 0
                    && artclDt != null && !artclDt.isEmpty()) {
                long cnt = enterCldrApplyDao.getArtclSlotCountExcluding(slot.getSlotSeq(), artclDt, artclSeq);
                slot.setBookedCount((int) cnt);
            }
        }
        return slots;
    }

    @Override
    public String excelDown(EnterCldrApplyVo vo) {
        EnterCldrSetup setup = enterCldrApplyDao.getSetup(vo.getFindSetupSeq());

        EnterCldrApplyVo allVo = new EnterCldrApplyVo();
        allVo.setFindSetupSeq(vo.getFindSetupSeq());
        allVo.setPage(1);
        allVo.setRow(99999);
        List<EnterCldrArtcl> artclList = enterCldrApplyDao.getArtclList(allVo);

        SimpleDateFormat sdf  = new SimpleDateFormat("yyyyMMdd",           Locale.KOREA);
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd",         Locale.KOREA);
        SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.KOREA);

        String today = sdf.format(TimeInfoUtil.getDate());
        StringBuilder filePath = new StringBuilder(K2Path.getFileTempPath() + today + "/");

        String excelFilePath = null;
        WritableWorkbook workbook = null;
        try {
            FileUtil.makeDirectorys(filePath.toString());
            String setupNm = (setup != null && setup.getSetupNm() != null) ? setup.getSetupNm() : "신청자";
            filePath.append(setupNm).append("_신청자 리스트_").append(today).append(".xls");
            workbook = Workbook.createWorkbook(new File(filePath.toString()));
            WritableSheet sheet = workbook.createSheet("신청자 리스트_" + today, 0);

            WritableFont   titleFont   = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE);
            WritableCellFormat titleFmt = new WritableCellFormat(titleFont);
            titleFmt.setBackground(Colour.GREY_25_PERCENT);
            titleFmt.setBorder(Border.ALL, BorderLineStyle.THIN);
            titleFmt.setAlignment(Alignment.CENTRE);
            titleFmt.setVerticalAlignment(VerticalAlignment.CENTRE);

            WritableFont   cellFont    = new WritableFont(WritableFont.ARIAL, 10);
            WritableCellFormat centerFmt = new WritableCellFormat(cellFont);
            centerFmt.setBackground(Colour.WHITE);
            centerFmt.setBorder(Border.ALL, BorderLineStyle.THIN);
            centerFmt.setAlignment(Alignment.CENTRE);
            centerFmt.setVerticalAlignment(VerticalAlignment.CENTRE);

            WritableCellFormat leftFmt = new WritableCellFormat(cellFont);
            leftFmt.setBackground(Colour.WHITE);
            leftFmt.setBorder(Border.ALL, BorderLineStyle.THIN);
            leftFmt.setAlignment(Alignment.LEFT);
            leftFmt.setVerticalAlignment(VerticalAlignment.CENTRE);

            String[] headers = {
                "번호", "신청일자", "신청시각", "신청자명", "휴대전화", "이메일",
                "고교코드", "고교명", "고교지역", "고교유형", "동반인원", "신청상태",
                "등록일시", "수정일시",
                "추가항목1", "추가항목2", "추가항목3", "추가항목4", "추가항목5",
                "추가항목6", "추가항목7", "추가항목8", "추가항목9", "추가항목10",
                "추가항목11", "추가항목12", "추가항목13", "추가항목14", "추가항목15"
            };
            for (int c = 0; c < headers.length; c++) {
                sheet.addCell(new Label(c, 0, headers[c], titleFmt));
            }

            if (artclList != null) {
                for (int i = 0; i < artclList.size(); i++) {
                    EnterCldrArtcl artcl = artclList.get(i);
                    // applyTime 조회
                    if (artcl.getTimeSlotSeq() != null) {
                        EnterCldrTimeSlot slot = enterCldrApplyDao.getTimeSlot(artcl.getTimeSlotSeq());
                        if (slot != null) artcl.setApplyTime(slot.getApplyTime());
                    }
                    String statusNm = "승인대기";
                    if      ("APPROVED".equals(artcl.getArtclStatus())) statusNm = "승인";
                    else if ("REJECTED".equals(artcl.getArtclStatus())) statusNm = "미승인";
                    else if ("CANCELED".equals(artcl.getArtclStatus())) statusNm = "취소";

                    int row = i + 1;
                    int col = 0;
                    sheet.addCell(new Label(col++, row, String.valueOf(row),                                       centerFmt));
                    sheet.addCell(new Label(col++, row, artcl.getArtclDt()   != null ? sdf2.format(artcl.getArtclDt())   : "", centerFmt));
                    sheet.addCell(new Label(col++, row, artcl.getApplyTime() != null ? artcl.getApplyTime()               : "", centerFmt));
                    sheet.addCell(new Label(col++, row, artcl.getRqstNm()    != null ? artcl.getRqstNm()                  : "", leftFmt));
                    sheet.addCell(new Label(col++, row, artcl.getRqstTel()   != null ? artcl.getRqstTel()                 : "", centerFmt));
                    sheet.addCell(new Label(col++, row, artcl.getRqstMl()    != null ? artcl.getRqstMl()                  : "", leftFmt));
                    sheet.addCell(new Label(col++, row, artcl.getSchCd()     != null ? artcl.getSchCd()                   : "", centerFmt));
                    sheet.addCell(new Label(col++, row, artcl.getSchNm()     != null ? artcl.getSchNm()                   : "", leftFmt));
                    sheet.addCell(new Label(col++, row, artcl.getSchLc()     != null ? artcl.getSchLc()                   : "", centerFmt));
                    sheet.addCell(new Label(col++, row, artcl.getSchTp()     != null ? artcl.getSchTp()                   : "", centerFmt));
                    sheet.addCell(new Label(col++, row, artcl.getCompanionCnt() != null ? String.valueOf(artcl.getCompanionCnt()) : "0", centerFmt));
                    sheet.addCell(new Label(col++, row, statusNm,                                                  centerFmt));
                    sheet.addCell(new Label(col++, row, artcl.getRgsde()  != null ? sdf3.format(artcl.getRgsde())  : "", centerFmt));
                    sheet.addCell(new Label(col++, row, artcl.getUpdde()  != null ? sdf3.format(artcl.getUpdde())  : "", centerFmt));

                    String[] additms = {
                        artcl.getAdditm1(),  artcl.getAdditm2(),  artcl.getAdditm3(),
                        artcl.getAdditm4(),  artcl.getAdditm5(),  artcl.getAdditm6(),
                        artcl.getAdditm7(),  artcl.getAdditm8(),  artcl.getAdditm9(),
                        artcl.getAdditm10(), artcl.getAdditm11(), artcl.getAdditm12(),
                        artcl.getAdditm13(), artcl.getAdditm14(), artcl.getAdditm15()
                    };
                    for (String val : additms) {
                        sheet.addCell(new Label(col++, row, val != null ? val : "", leftFmt));
                    }
                }
            }
            workbook.write();
            excelFilePath = filePath.toString();
        } catch (IOException e) {
            LOG.error("엑셀 생성 오류", e);
        } catch (WriteException e) {
            LOG.error("엑셀 쓰기 오류", e);
        } finally {
            if (workbook != null) {
                try { workbook.close(); } catch (Exception e) { LOG.error("엑셀 닫기 오류", e); }
            }
        }
        return excelFilePath;
    }

    /** 동적 폼 항목에 additm 값을 채워 반환 (조회/수정 화면용) */
    private List<EnterCldrFormItem> buildDynamicFormItems(EnterCldrArtcl artcl) {
        List<EnterCldrFormItem> formItems = enterCldrApplyDao.getFormItemList(artcl.getSetupSeq());
        List<EnterCldrFormItem> result = new ArrayList<>();
        if (formItems == null) return result;
        int dynIdx = 0;
        for (EnterCldrFormItem item : formItems) {
            if ("Y".equals(item.getFixedYn())) continue;
            dynIdx++;
            item.setAnswerVal(getAdditm(artcl, dynIdx));
            result.add(item);
        }
        return result;
    }

    /** Date에 시각(0~23)을 적용하여 HH:00:00으로 반환. date 또는 hour가 null이면 date 그대로 반환. */
    private Date applyHour(Date date, Integer hour) {
        if (date == null || hour == null) return date;
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    private String getAdditm(EnterCldrArtcl a, int idx) {
        switch (idx) {
            case  1: return a.getAdditm1();  case  2: return a.getAdditm2();
            case  3: return a.getAdditm3();  case  4: return a.getAdditm4();
            case  5: return a.getAdditm5();  case  6: return a.getAdditm6();
            case  7: return a.getAdditm7();  case  8: return a.getAdditm8();
            case  9: return a.getAdditm9();  case 10: return a.getAdditm10();
            case 11: return a.getAdditm11(); case 12: return a.getAdditm12();
            case 13: return a.getAdditm13(); case 14: return a.getAdditm14();
            case 15: return a.getAdditm15();
            default: return null;
        }
    }
}
