package k2web.module.enterCldrApply.web;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.JsonObject;

import k2web.com.cmm.PageNavigation;
import k2web.com.cmm.message.service.MessageVo;
import k2web.com.cmm.tag.UrlTag;
import k2web.com.cop.fnct.service.FnctService;
import k2web.com.cop.user.service.SessionUserInfoHelper;
import k2web.com.util.FileUtil;
import k2web.com.util.K2Util;
import k2web.module.enterCldrApply.service.EnterCldrApplyService;
import k2web.module.enterCldrApply.service.EnterCldrApplyVo;
import k2web.module.enterCldrApply.service.model.EnterCldrArtcl;
import k2web.module.enterCldrApply.service.model.EnterCldrAtchmnfl;
import k2web.module.enterCldrApply.service.model.EnterCldrFormItem;
import k2web.module.enterCldrApply.service.model.EnterCldrHoliday;
import k2web.module.enterCldrApply.service.model.EnterCldrSetup;
import k2web.module.enterCldrApply.service.model.EnterCldrTargetItem;
import k2web.module.enterCldrApply.service.model.EnterCldrTimeSlot;
import k2web.module.enterCldrApply.util.ResponseUtil;

@Controller
@RequestMapping("/enterCldrApply/fnctMngr")
public class EnterCldrApplyFnctMngrController {

    private static final Logger LOG = LoggerFactory.getLogger(EnterCldrApplyFnctMngrController.class);

    private static final String FNCT_ID = EnterCldrApplyService.FNCT_ID;
    private static final String JSP_PATH = "k2web/module/" + FNCT_ID + "/fnctMngr";

    private static final Set<String> ALLOWED_ARTCL_STATUSES = new HashSet<>(
        Arrays.asList("WAIT", "APPROVED", "REJECTED", "CANCELED")
    );

    @Resource(name = "EnterCldrApplyService")
    private EnterCldrApplyService enterCldrApplyService;

    @Resource(name = "FnctService")
    private FnctService fnctService;

    @Autowired
    private HttpServletRequest request;

    /* ===================== 설정 관리 ===================== */

    @RequestMapping("/{siteId}/setupList")
    public String setupList(
            @PathVariable String siteId,
            @ModelAttribute("vo") EnterCldrApplyVo vo,
            ModelMap model) {
        
    	K2Util.setFnctInfo(fnctService, siteId, FNCT_ID, model);
        
        model.addAttribute("setupList", enterCldrApplyService.getSetupList(vo, true));
        
        return JSP_PATH + "/setupList";
    }

    @RequestMapping("/{siteId}/setupRegist")
    public String setupRegist(
            @PathVariable String siteId,
            @ModelAttribute("vo") EnterCldrApplyVo vo,
            ModelMap model) {
        K2Util.setFnctInfo(fnctService, siteId, FNCT_ID, model);
        return JSP_PATH + "/setupForm";
    }

    @RequestMapping("/{siteId}/setupRegistProc")
    @ResponseBody
    public void setupRegistProc(
            @PathVariable String siteId,
            EnterCldrSetup setup,
            HttpServletResponse response) {
        
    	boolean result = false;
        try {
            String regId = SessionUserInfoHelper.getUserId();
            enterCldrApplyService.setSetupRegist(setup, regId);
            
            result = true;
        } catch (DataAccessException e) {
            LOG.error("설정 등록 오류", e);
        }
        
        JsonObject jsonObj = new JsonObject();
		jsonObj.addProperty("result", result);
		ResponseUtil.setJsonDataToResponse(response, jsonObj);
    }

    @RequestMapping("/{siteId}/{setupSeq}/setupView")
    public String setupView(
            @PathVariable String siteId,
            @PathVariable Integer setupSeq,
            @ModelAttribute("vo") EnterCldrApplyVo vo,
            ModelMap model) {
        K2Util.setFnctInfo(fnctService, siteId, FNCT_ID, model);
        model.addAttribute("setup", enterCldrApplyService.getSetup(setupSeq));
        model.addAttribute("timeSlotList", enterCldrApplyService.getTimeSlotList(setupSeq));
        model.addAttribute("targetItemList", enterCldrApplyService.getTargetItemList(setupSeq));
        model.addAttribute("formItemList", enterCldrApplyService.getFormItemList(setupSeq));
        model.addAttribute("holidayList", enterCldrApplyService.getHolidayList(setupSeq));
        model.addAttribute("atchmnflList", enterCldrApplyService.getAtchmnflList(setupSeq));
        return JSP_PATH + "/setupForm";
    }

    @RequestMapping("/{siteId}/setupUpdtProc")
    @ResponseBody
    public void setupUpdtProc(
            @PathVariable String siteId,
            EnterCldrSetup setup,
            HttpServletResponse response) {

    	boolean result = false;
        try {
            String updId = SessionUserInfoHelper.getUserId();
            enterCldrApplyService.setSetupUpdt(setup, updId);
            
            result = true;
        } catch (DataAccessException e) {
            LOG.error("설정 수정 오류", e);
        }
        
        JsonObject jsonObj = new JsonObject();
		jsonObj.addProperty("result", result);
		ResponseUtil.setJsonDataToResponse(response, jsonObj);
    }

    @RequestMapping("/{siteId}/setupDeleteProc")
    @ResponseBody
    public void setupDeleteProc(
            @PathVariable String siteId,
            @RequestParam Integer seq,
            HttpServletResponse response) {
    	
    	boolean result = false;
        try {
            enterCldrApplyService.setSetupDelete(seq);
            result = true;
        } catch (DataAccessException e) {
            LOG.error("설정 삭제 오류", e);
        }
        
        JsonObject jsonObj = new JsonObject();
		jsonObj.addProperty("result", result);
		ResponseUtil.setJsonDataToResponse(response, jsonObj);
    }

    /* ===================== 첨부파일 관리 ===================== */

    @RequestMapping("/{siteId}/atchmnflRegistProc")
    @ResponseBody
    public void atchmnflRegistProc(
            @PathVariable String siteId,
            EnterCldrAtchmnfl atchmnfl,
            HttpServletResponse response) {

        boolean result = false;
        try {
            enterCldrApplyService.setAtchmnflRegist(siteId, atchmnfl, SessionUserInfoHelper.getUserId());
            result = true;
        } catch (DataAccessException e) {
            LOG.error("첨부파일 등록 오류", e);
        }

        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty("result", result);
        ResponseUtil.setJsonDataToResponse(response, jsonObj);
    }

    @RequestMapping("/{siteId}/atchmnflDeleteProc")
    @ResponseBody
    public void atchmnflDeleteProc(
            @PathVariable String siteId,
            @RequestParam Integer seq,
            HttpServletResponse response) {

        boolean result = false;
        try {
            enterCldrApplyService.setAtchmnflDelete(siteId, seq);
            result = true;
        } catch (DataAccessException e) {
            LOG.error("첨부파일 삭제 오류", e);
        }

        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty("result", result);
        ResponseUtil.setJsonDataToResponse(response, jsonObj);
    }

    /* ===================== 시간 슬롯 관리 ===================== */

    @RequestMapping("/{siteId}/timeSlotRegistProc")
    @ResponseBody
    public void timeSlotRegistProc(
            @PathVariable String siteId,
            EnterCldrTimeSlot timeSlot,
            HttpServletResponse response) {
        
    	boolean result = false;
        try {
            enterCldrApplyService.setTimeSlotRegist(timeSlot, SessionUserInfoHelper.getUserId());
            result = true;
        } catch (DataAccessException e) {
            LOG.error("시간슬롯 등록 오류", e);
        }
        
        JsonObject jsonObj = new JsonObject();
		jsonObj.addProperty("result", result);
		ResponseUtil.setJsonDataToResponse(response, jsonObj);
    }

    @RequestMapping("/{siteId}/timeSlotUpdtProc")
    @ResponseBody
    public void timeSlotUpdtProc(
            @PathVariable String siteId,
            EnterCldrTimeSlot timeSlot,
            HttpServletResponse response) {
    	
    	boolean result = false;
        try {
            enterCldrApplyService.setTimeSlotUpdt(timeSlot, SessionUserInfoHelper.getUserId());
            result = true;
        } catch (DataAccessException e) {
            LOG.error("시간슬롯 수정 오류", e);
        }

        JsonObject jsonObj = new JsonObject();
		jsonObj.addProperty("result", result);
		ResponseUtil.setJsonDataToResponse(response, jsonObj);
    }

    @RequestMapping("/{siteId}/timeSlotDeleteProc")
    @ResponseBody
    public void timeSlotDeleteProc(
            @PathVariable String siteId,
            @RequestParam Integer seq,
            HttpServletResponse response) {
        
    	boolean result = false;
        try {
            enterCldrApplyService.setTimeSlotDelete(seq);
            result = true;
        } catch (DataAccessException e) {
            LOG.error("시간슬롯 삭제 오류", e);
        }
        
        JsonObject jsonObj = new JsonObject();
		jsonObj.addProperty("result", result);
		ResponseUtil.setJsonDataToResponse(response, jsonObj);
    }

    /* ===================== 대상별 항목 관리 ===================== */

    @RequestMapping("/{siteId}/targetItemRegistProc")
    @ResponseBody
    public void targetItemRegistProc(
            @PathVariable String siteId,
            EnterCldrTargetItem targetItem,
            HttpServletResponse response) {
        
    	boolean result = false;
        try {
            enterCldrApplyService.setTargetItemRegist(targetItem, SessionUserInfoHelper.getUserId());
            result = true;
        } catch (DataAccessException e) {
            LOG.error("대상항목 등록 오류", e);
        }
        
        JsonObject jsonObj = new JsonObject();
		jsonObj.addProperty("result", result);
		ResponseUtil.setJsonDataToResponse(response, jsonObj);
    }

    @RequestMapping("/{siteId}/targetItemUpdtProc")
    @ResponseBody
    public void targetItemUpdtProc(
            @PathVariable String siteId,
            EnterCldrTargetItem targetItem,
            HttpServletResponse response) {

    	boolean result = false;
        try {
            enterCldrApplyService.setTargetItemUpdt(targetItem, SessionUserInfoHelper.getUserId());
            result = true;
        } catch (DataAccessException e) {
            LOG.error("대상항목 수정 오류", e);
        }

        JsonObject jsonObj = new JsonObject();
		jsonObj.addProperty("result", result);
		ResponseUtil.setJsonDataToResponse(response, jsonObj);
    }

    @RequestMapping("/{siteId}/targetItemDeleteProc")
    @ResponseBody
    public void targetItemDeleteProc(
            @PathVariable String siteId,
            @RequestParam Integer seq,
            HttpServletResponse response) {
        
    	boolean result = false;
        try {
            enterCldrApplyService.setTargetItemDelete(seq);
            result = true;
        } catch (DataAccessException e) {
            LOG.error("대상항목 삭제 오류", e);
        }
        
        JsonObject jsonObj = new JsonObject();
		jsonObj.addProperty("result", result);
		ResponseUtil.setJsonDataToResponse(response, jsonObj);
    }

    /* ===================== 폼 항목 관리 ===================== */

    @RequestMapping("/{siteId}/formItemRegistProc")
    @ResponseBody
    public void formItemRegistProc(
            @PathVariable String siteId,
            EnterCldrFormItem formItem,
            HttpServletResponse response) {
        
    	boolean result = false;
        try {
            enterCldrApplyService.setFormItemRegist(formItem, SessionUserInfoHelper.getUserId());
            result = true;
        } catch (DataAccessException e) {
            LOG.error("폼항목 등록 오류", e);
        }
        
        JsonObject jsonObj = new JsonObject();
		jsonObj.addProperty("result", result);
		ResponseUtil.setJsonDataToResponse(response, jsonObj);
    }

    @RequestMapping("/{siteId}/formItemUpdtProc")
    @ResponseBody
    public void formItemUpdtProc(
            @PathVariable String siteId,
            EnterCldrFormItem formItem,
            HttpServletResponse response) {
        
    	boolean result = false;
        try {
            enterCldrApplyService.setFormItemUpdt(formItem, SessionUserInfoHelper.getUserId());
            result = true;
        } catch (DataAccessException e) {
            LOG.error("폼항목 수정 오류", e);
        }
        
        JsonObject jsonObj = new JsonObject();
		jsonObj.addProperty("result", result);
		ResponseUtil.setJsonDataToResponse(response, jsonObj);
    }

    @RequestMapping("/{siteId}/formItemDeleteProc")
    @ResponseBody
    public void formItemDeleteProc(
            @PathVariable String siteId,
            @RequestParam Integer seq,
            HttpServletResponse response) {
        
    	boolean result = false;
    	String errorMsg = null;
        try {
            k2web.module.enterCldrApply.service.model.EnterCldrFormItem item =
                enterCldrApplyService.getFormItem(seq);
            if (item != null && ("RQST_NM".equals(item.getItemType()) || "RQST_TEL".equals(item.getItemType()))) {
                errorMsg = "이름과 휴대전화 항목은 삭제할 수 없습니다.";
            } else {
                enterCldrApplyService.setFormItemDelete(seq);
                result = true;
            }
        } catch (DataAccessException e) {
            LOG.error("폼항목 삭제 오류", e);
        }

        JsonObject jsonObj = new JsonObject();
		jsonObj.addProperty("result", result);
		if (errorMsg != null) jsonObj.addProperty("errorMsg", errorMsg);
		ResponseUtil.setJsonDataToResponse(response, jsonObj);
    }

    /* ===================== 휴일 관리 ===================== */

    @RequestMapping("/{siteId}/holidayRegistProc")
    @ResponseBody
    public void holidayRegistProc(
            @PathVariable String siteId,
            EnterCldrHoliday holiday,
            HttpServletResponse response) {
        
    	boolean result = false;
        try {
            enterCldrApplyService.setHolidayRegist(holiday, SessionUserInfoHelper.getUserId());
            result = true;
        } catch (DataAccessException e) {
            LOG.error("휴일 등록 오류", e);
        }
        
        JsonObject jsonObj = new JsonObject();
		jsonObj.addProperty("result", result);
		ResponseUtil.setJsonDataToResponse(response, jsonObj);
    }

    @RequestMapping("/{siteId}/holidayUpdtProc")
    @ResponseBody
    public void holidayUpdtProc(
            @PathVariable String siteId,
            @RequestParam Integer holidaySeq,
            @RequestParam(required = false) String holidayDt,
            @RequestParam(required = false) String holidayNm,
            HttpServletResponse response) {

    	boolean result = false;
        try {
            EnterCldrHoliday holiday = new EnterCldrHoliday();
            holiday.setHolidaySeq(holidaySeq);
            if (holidayDt != null && !holidayDt.isEmpty()) {
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
                sdf.setLenient(false);
                holiday.setHolidayDt(sdf.parse(holidayDt));
            }
            holiday.setHolidayNm(holidayNm);
            enterCldrApplyService.setHolidayUpdt(holiday, SessionUserInfoHelper.getUserId());
            result = true;
        } catch (Exception e) {
            LOG.error("휴일 수정 오류", e);
        }

        JsonObject jsonObj = new JsonObject();
		jsonObj.addProperty("result", result);
		ResponseUtil.setJsonDataToResponse(response, jsonObj);
    }

    @RequestMapping("/{siteId}/holidayDeleteProc")
    @ResponseBody
    public void holidayDeleteProc(
            @PathVariable String siteId,
            @RequestParam Integer seq,
            HttpServletResponse response) {
    	
    	boolean result = false;
        try {
            enterCldrApplyService.setHolidayDelete(seq);
            result = true;
        } catch (DataAccessException e) {
            LOG.error("휴일 삭제 오류", e);
        }
        
        JsonObject jsonObj = new JsonObject();
		jsonObj.addProperty("result", result);
		ResponseUtil.setJsonDataToResponse(response, jsonObj);
    }

    /* ===================== 신청 관리 ===================== */

    @RequestMapping("/{siteId}/{setupSeq}/artclList")
    public String artclList(
            @PathVariable String siteId,
            @PathVariable Integer setupSeq,
            @ModelAttribute("vo") EnterCldrApplyVo vo,
            ModelMap model) {
        K2Util.setFnctInfo(fnctService, siteId, FNCT_ID, model);
        
        EnterCldrSetup setup = enterCldrApplyService.getSetup(setupSeq);
        if(setup != null) {
        	model.addAttribute("setup", setup);
        	vo.setFindSetupSeq( setup.getSetupSeq() );
        	
        	Long cnt = enterCldrApplyService.getArtclListCount(vo);
            PageNavigation pageNavi = new PageNavigation( vo.getPage(), vo.getRow() );
    		pageNavi.setCount( cnt );
    		
    		model.addAttribute( "artclList", enterCldrApplyService.getArtclList(vo) );
    		model.addAttribute( "pageNavi", pageNavi );
    		
        } else {
        	
        	MessageVo messageVo = new MessageVo();
    		messageVo.setMessage( "정상적인 접근이 아닙니다." );
    		messageVo.setLocation( UrlTag.getValue( request, "/enterCldrApply/fnctMngr/" + siteId + "/setupList" ) );
    		return "message:";
        }
        
        return JSP_PATH + "/artclList";
    }

    @RequestMapping("/{siteId}/{setupSeq}/{artclSeq}/artclView")
    public String artclView(
            @PathVariable String siteId,
            @PathVariable Integer setupSeq,
            @PathVariable Integer artclSeq,
            @ModelAttribute("vo") EnterCldrApplyVo vo,
            ModelMap model) {
		K2Util.setFnctInfo(fnctService, siteId, FNCT_ID, model);

		MessageVo messageVo = new MessageVo();

		boolean processChk = true;
		EnterCldrSetup setup = enterCldrApplyService.getSetup(setupSeq);
		if (setup != null) {
			if ("N".equals(setup.getUseYn())) {
				processChk = false;
			}
		} else {
			processChk = false;
		}

		if (processChk) {
			model.addAttribute("setupSeq", setupSeq);
			model.addAttribute("artcl", enterCldrApplyService.getArtcl(setupSeq, artclSeq));
			return JSP_PATH + "/artclView";
		} else {
			messageVo.setMessage("정상적인 접근이 아닙니다.");
			messageVo.setLocation(UrlTag.getValue(request, "/enterCldrApply/fnctMngr/" + siteId + "/setupList"));
			return "message:";
		}
        
    }

    @RequestMapping("/{siteId}/{setupSeq}/{artclSeq}/artclStatusUpdtProc")
    @ResponseBody
    public void artclStatusUpdtProc(
            @PathVariable String siteId,
            @PathVariable Integer setupSeq,
            @PathVariable Integer artclSeq,
            @RequestParam String artclStatus,
            HttpServletResponse response) {
        
    	boolean processChk = true;
		EnterCldrSetup setup = enterCldrApplyService.getSetup(setupSeq);
		if (setup != null) {
			if ("N".equals(setup.getUseYn())) {
				processChk = false;
			}
		} else {
			processChk = false;
		}
		
		boolean result = false;
		if (processChk && ALLOWED_ARTCL_STATUSES.contains(artclStatus)) {
			try {
	            enterCldrApplyService.setArtclStatusUpdt(setupSeq, artclSeq, artclStatus, true);
	            result = true;
	        } catch (DataAccessException e) {
	            LOG.error("신청 상태 변경 오류", e);
	        }
		}

        JsonObject jsonObj = new JsonObject();
		jsonObj.addProperty("result", result);
		ResponseUtil.setJsonDataToResponse(response, jsonObj);
    }

    @RequestMapping("/{siteId}/{setupSeq}/{artclSeq}/artclDeleteProc")
    @ResponseBody
    public void artclDeleteProc(
            @PathVariable String siteId,
            @PathVariable Integer setupSeq,
            @PathVariable Integer artclSeq,
            HttpServletResponse response) {
    	

		boolean processChk = true;
		EnterCldrSetup setup = enterCldrApplyService.getSetup(setupSeq);
		if (setup != null) {
			if ("N".equals(setup.getUseYn())) {
				processChk = false;
			}
		} else {
			processChk = false;
		}

		boolean result = false;
		if(processChk) {
			try {
				enterCldrApplyService.setArtclDelete(setupSeq, artclSeq);
				result = true;
			} catch (DataAccessException e) {
				LOG.error("신청 삭제 오류", e);
			}
		}

		JsonObject jsonObj = new JsonObject();
		jsonObj.addProperty("result", result);
		ResponseUtil.setJsonDataToResponse(response, jsonObj);
    }
    
    @RequestMapping("/{siteId}/{setupSeq}/{artclSeq}/artclUpdt")
    public String artclUpdt(
            @PathVariable String siteId,
            @PathVariable Integer setupSeq,
            @PathVariable Integer artclSeq,
            @ModelAttribute("vo") EnterCldrApplyVo vo,
            ModelMap model) {
        K2Util.setFnctInfo(fnctService, siteId, FNCT_ID, model);

        EnterCldrSetup setup = enterCldrApplyService.getSetup(setupSeq);
        if (setup == null) {
            MessageVo messageVo = new MessageVo();
            messageVo.setMessage("정상적인 접근이 아닙니다.");
            messageVo.setLocation(UrlTag.getValue(request, "/enterCldrApply/fnctMngr/" + siteId + "/setupList"));
            return "message:";
        }

        model.addAttribute("setup", setup);
        model.addAttribute("setupSeq", setupSeq);
        EnterCldrArtcl artcl = enterCldrApplyService.getArtcl(setupSeq, artclSeq);
        String artclDtStr = "";
        if (artcl != null && artcl.getArtclDt() != null) {
            artclDtStr = new java.text.SimpleDateFormat("yyyy-MM-dd").format(artcl.getArtclDt());
        }
        model.addAttribute("artcl", artcl);
        model.addAttribute("timeSlotList", enterCldrApplyService.getTimeSlotListForEdit(setupSeq, artclSeq, artclDtStr));
        model.addAttribute("targetItemList", enterCldrApplyService.getTargetItemList(setupSeq));
        return JSP_PATH + "/artclUpdt";
    }

    @RequestMapping("/{siteId}/{setupSeq}/{artclSeq}/artclUpdtProc")
    @ResponseBody
    public void artclUpdtProc(
            @PathVariable String siteId,
            @PathVariable Integer setupSeq,
            @PathVariable Integer artclSeq,
            EnterCldrArtcl artcl,
            HttpServletResponse response) {

    	boolean processChk = true;
		EnterCldrSetup setup = enterCldrApplyService.getSetup(setupSeq);
		if (setup != null) {
			if ("N".equals(setup.getUseYn())) {
				processChk = false;
			}
		} else {
			processChk = false;
		}

		boolean result = false;
		if(processChk) {
			
			artcl.setSetupSeq(setupSeq);
	        artcl.setArtclSeq(artclSeq);

	        try {
	            enterCldrApplyService.setArtclUpdt(artcl, true);
	            result = true;
	        } catch (DataAccessException e) {
	            LOG.error("신청 수정(관리자) 오류", e);
	        }
		}
		
        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty("result", result);
        ResponseUtil.setJsonDataToResponse(response, jsonObj);
    }

    /* ===================== 엑셀 다운로드 ===================== */

    @RequestMapping("/{siteId}/{setupSeq}/excelDown")
    public void excelDown(
            @PathVariable String siteId,
            @PathVariable Integer setupSeq,
            @ModelAttribute("vo") EnterCldrApplyVo vo,
            HttpServletResponse response) {
        vo.setFindSetupSeq(setupSeq);
        String excelFilePath = enterCldrApplyService.excelDown(vo);
        FileUtil.download(excelFilePath, request, response);
    }

    @RequestMapping( "/{siteId}/{setupSeq}/skinEstbsUpdtView" )
	public String skinEstbsUpdtView(
			@PathVariable( "siteId" ) String siteId,
			@PathVariable( "setupSeq" ) Integer setupSeq,
			@ModelAttribute( "vo" ) EnterCldrApplyVo vo,
			ModelMap model ) {
    	K2Util.setFnctInfo(fnctService, siteId, FNCT_ID, model);
        
		model.addAttribute( "setup", enterCldrApplyService.getSetup(setupSeq) );
		
		return JSP_PATH + "/skinEstbsUpdtView";
	}
}
