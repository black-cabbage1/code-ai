package k2web.module.enterCldrApply.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
            db.setRecvStartDt(setup.getRecvStartDt());
            db.setRecvEndDt(setup.getRecvEndDt());
            db.setModStartDt(setup.getModStartDt());
            db.setModEndDt(setup.getModEndDt());
            db.setDplcAplyPsblYn(setup.getDplcAplyPsblYn());
            db.setPopupMsg(setup.getPopupMsg());
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
            db.setItemType(formItem.getItemType());
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

    @Override
    @Transactional
    public void setArtclUpdt(EnterCldrArtcl artcl, String updId) {
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
            db.setUpdde(TimeInfoUtil.getDate());
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
    public void setArtclStatusUpdt(Integer setupSeq, Integer artclSeq, String artclStatus, String updId) {
        EnterCldrArtcl db = enterCldrApplyDao.getArtcl(setupSeq, artclSeq);
        if (db != null) {
            db.setArtclStatus(artclStatus);
            db.setUpdde(TimeInfoUtil.getDate());
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

    /** 동적 폼 항목에 additm 값을 채워 반환 (조회/수정 화면용) */
    private List<EnterCldrFormItem> buildDynamicFormItems(EnterCldrArtcl artcl) {
        List<EnterCldrFormItem> formItems = enterCldrApplyDao.getFormItemList(artcl.getSetupSeq());
        List<EnterCldrFormItem> result = new ArrayList<>();
        if (formItems == null) return result;
        int dynIdx = 0;
        for (EnterCldrFormItem item : formItems) {
            String type = item.getItemType();
            if ("RQST_NM".equals(type) || "RQST_TEL".equals(type)
                    || "RQST_ML".equals(type) || "SCHOOL".equals(type)) continue;
            dynIdx++;
            item.setAnswerVal(getAdditm(artcl, dynIdx));
            result.add(item);
        }
        return result;
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
