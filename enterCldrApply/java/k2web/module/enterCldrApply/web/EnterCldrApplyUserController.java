package k2web.module.enterCldrApply.web;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import k2web.com.cmm.aop.Layout;
import k2web.com.cmm.message.service.MessageVo;
import k2web.com.cmm.tag.TextTag;
import k2web.com.cmm.tag.UrlTag;
import k2web.com.cop.fnct.service.FnctService;
import k2web.com.util.K2Util;
import k2web.module.enterCldrApply.service.EnterCldrApplyService;
import k2web.module.enterCldrApply.service.EnterCldrApplyVo;
import k2web.module.enterCldrApply.service.model.EnterCldrArtcl;
import k2web.module.enterCldrApply.service.model.EnterCldrAtchmnfl;
import k2web.module.enterCldrApply.service.model.EnterCldrHoliday;
import k2web.module.enterCldrApply.service.model.EnterCldrSetup;
import k2web.module.enterCldrApply.service.model.EnterCldrTimeSlot;
import k2web.module.enterCldrApply.util.ResponseUtil;

@Controller
@RequestMapping("/enterCldrApply")
public class EnterCldrApplyUserController {

    private static final Logger LOG = LoggerFactory.getLogger(EnterCldrApplyUserController.class);

    private static final String FNCT_ID = EnterCldrApplyService.FNCT_ID;

    @Resource(name = "EnterCldrApplyService")
    private EnterCldrApplyService enterCldrApplyService;

    @Resource(name = "FnctService")
    private FnctService fnctService;

    @Autowired
    private HttpServletRequest request;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(sdf, true));
    }
    
    /**
     * 날짜별 슬롯 잔여 현황 일괄 조회 (Ajax)
     */
    @RequestMapping("/{siteId}/{fnctNo}/slotAvailability")
    @ResponseBody
    public void slotAvailability(
            @PathVariable String siteId,
            @PathVariable Integer fnctNo,
            @RequestParam String artclDt,
            HttpServletResponse response) {

        List<EnterCldrTimeSlot> slots = enterCldrApplyService.getTimeSlotList(fnctNo);
        JsonArray arr = new JsonArray();
        if (slots != null) {
            for (EnterCldrTimeSlot slot : slots) {
                int remain = enterCldrApplyService.getTimeSlotRemainCnt(slot.getSlotSeq(), artclDt);
                JsonObject obj = new JsonObject();
                obj.addProperty("slotSeq",   slot.getSlotSeq());
                obj.addProperty("applyTime", slot.getApplyTime());
                obj.addProperty("capacity",  slot.getCapacity());
                obj.addProperty("remain",    remain);
                arr.add(obj);
            }
        }
        JsonObject jsonObj = new JsonObject();
        jsonObj.add("slotList", arr);
        ResponseUtil.setJsonDataToResponse(response, jsonObj);
    }

    /**
     * 월 단위 날짜별 슬롯 마감 여부 일괄 조회 (Ajax)
     * 반환: { dateMap: { "yyyy-MM-dd": "full"|"ok" } }
     */
    @RequestMapping("/{siteId}/{fnctNo}/slotMonthAvailability")
    @ResponseBody
    public void slotMonthAvailability(
            @PathVariable String siteId,
            @PathVariable Integer fnctNo,
            @RequestParam int year,
            @RequestParam int month,
            HttpServletResponse response) {

        EnterCldrSetup setup   = enterCldrApplyService.getSetup(fnctNo);
        List<EnterCldrTimeSlot> slots    = enterCldrApplyService.getTimeSlotList(fnctNo);
        List<EnterCldrHoliday>  holidays = enterCldrApplyService.getHolidayList(fnctNo);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Set<String> holidaySet = new HashSet<>();
        if (holidays != null) {
            for (EnterCldrHoliday h : holidays) {
                if (h.getHolidayDt() != null) holidaySet.add(sdf.format(h.getHolidayDt()));
            }
        }

        String[] dayYn = new String[8];
        if (setup != null) {
            dayYn[Calendar.SUNDAY]    = setup.getSunYn();
            dayYn[Calendar.MONDAY]    = setup.getMonYn();
            dayYn[Calendar.TUESDAY]   = setup.getTueYn();
            dayYn[Calendar.WEDNESDAY] = setup.getWedYn();
            dayYn[Calendar.THURSDAY]  = setup.getThuYn();
            dayYn[Calendar.FRIDAY]    = setup.getFriYn();
            dayYn[Calendar.SATURDAY]  = setup.getSatYn();
        }

        String recvStart = (setup != null && setup.getRecvStartDt() != null) ? sdf.format(setup.getRecvStartDt()) : null;
        String recvEnd   = (setup != null && setup.getRecvEndDt()   != null) ? sdf.format(setup.getRecvEndDt())   : null;

        JsonObject dateMap = new JsonObject();
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, 1);
        int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        for (int day = 1; day <= daysInMonth; day++) {
            cal.set(year, month - 1, day);
            String dateStr = sdf.format(cal.getTime());
            int dow = cal.get(Calendar.DAY_OF_WEEK);

            if (recvStart != null && dateStr.compareTo(recvStart) < 0) continue;
            if (recvEnd   != null && dateStr.compareTo(recvEnd)   > 0) continue;
            if (dayYn[dow] == null || !"Y".equals(dayYn[dow])) continue;
            if (holidaySet.contains(dateStr)) continue;

            if (slots == null || slots.isEmpty()) {
                dateMap.addProperty(dateStr, "full");
                continue;
            }
            boolean allFull = true;
            for (EnterCldrTimeSlot slot : slots) {
                if (slot.getCapacity() == 0) { allFull = false; break; }
                int remain = enterCldrApplyService.getTimeSlotRemainCnt(slot.getSlotSeq(), dateStr);
                if (remain > 0) { allFull = false; break; }
            }
            dateMap.addProperty(dateStr, allFull ? "full" : "ok");
        }

        JsonObject result = new JsonObject();
        result.add("dateMap", dateMap);
        ResponseUtil.setJsonDataToResponse(response, result);
    }

    /**
     * 캘린더 신청 메인 화면 (캘린더 뷰)
     * 설정 정보, 시간 슬롯, 휴일, 폼 항목을 함께 전달
     */
    @RequestMapping("/{siteId}/{fnctNo}/main")
    @Layout
    public String main(
            @PathVariable String siteId,
            @PathVariable Integer fnctNo,
            @ModelAttribute("vo") EnterCldrApplyVo vo,
            ModelMap model) {
        K2Util.setFnctInfo(fnctService, siteId, FNCT_ID, model);

        EnterCldrSetup setup = enterCldrApplyService.getSetup(fnctNo);
        if (setup == null || !"Y".equals(setup.getUseYn())) {
            return "redirect:/error";
        }

        model.addAttribute("setup", setup);
        model.addAttribute("timeSlotList", enterCldrApplyService.getTimeSlotList(fnctNo));
        model.addAttribute("holidayList", enterCldrApplyService.getHolidayList(fnctNo));
        model.addAttribute("formItemList", enterCldrApplyService.getFormItemList(fnctNo));
        model.addAttribute("targetItemList", enterCldrApplyService.getTargetItemList(fnctNo));
        model.addAttribute("atchmnflList", enterCldrApplyService.getAtchmnflList(fnctNo));
        return "skin:main";
    }

    /**
     * 신청 가능 잔여 인원 조회 (Ajax)
     */
    @RequestMapping("/{siteId}/remainCnt")
    @ResponseBody
    public void remainCnt(
            @PathVariable String siteId,
            @RequestParam Integer timeSlotSeq,
            @RequestParam String artclDt,
            HttpServletResponse response) {
        int remain = enterCldrApplyService.getTimeSlotRemainCnt(timeSlotSeq, artclDt);
        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty("remainCnt", remain);
        ResponseUtil.setJsonDataToResponse(response, jsonObj);
    }

    /**
     * 신청 등록 처리
     */
    @RequestMapping("/{siteId}/artclRegistProc")
    @ResponseBody
    public void artclRegistProc(
            @PathVariable String siteId,
            EnterCldrArtcl artcl,
            HttpServletResponse response) {
        String message = null;
        try {
            EnterCldrSetup setup = enterCldrApplyService.getSetup(artcl.getSetupSeq());
            if (setup == null || !"Y".equals(setup.getUseYn())) {
                message = "유효하지 않은 신청입니다.";
            } else {
                Date now = new Date();
                if (setup.getRecvStartDt() != null && now.before(setup.getRecvStartDt())) {
                    message = "아직 접수기간이 아닙니다.";
                } else if (setup.getRecvEndDt() != null && now.after(setup.getRecvEndDt())) {
                    message = "접수기간이 마감되었습니다.";
                } else if (!"Y".equals(setup.getDplcAplyPsblYn())
                        && enterCldrApplyService.isArtclDuplicated(
                                artcl.getSetupSeq(), artcl.getRqstNm(), artcl.getRqstTel())) {
                    message = "이미 동일한 신청 내역이 존재합니다.";
                } else {
                    // 슬롯 잠금 + 정원 재확인 + 저장을 원자적으로 처리 (동시 신청 초과 방지)
                    message = enterCldrApplyService.tryArtclRegist(artcl);
                }
            }
        } catch (DataAccessException e) {
            LOG.error("신청 등록 오류", e);
            message = "처리 중 오류가 발생하였습니다.";
        }
        JsonObject jsonObj = new JsonObject();
        if (message != null) jsonObj.addProperty("message", message);
        ResponseUtil.setJsonDataToResponse(response, jsonObj);
    }

    /**
     * 신청 조회 화면 (이름+전화번호 조회)
     */
    @RequestMapping("/{siteId}/{fnctNo}/artclSearch")
    @Layout
    public String artclSearch(
            @PathVariable String siteId,
            @PathVariable Integer fnctNo,
            @ModelAttribute("vo") EnterCldrApplyVo vo,
            ModelMap model) {
        K2Util.setFnctInfo(fnctService, siteId, FNCT_ID, model);
        model.addAttribute("setup", enterCldrApplyService.getSetup(fnctNo));
        return "skin:artclSearch";
    }

    /**
     * 신청 조회 결과 목록
     */
    @RequestMapping("/{siteId}/{fnctNo}/artclSearchList")
    @Layout
    public String artclSearchList(
            @PathVariable String siteId,
            @PathVariable Integer fnctNo,
            @RequestParam(required = false) String rqstNm,
            @RequestParam(required = false) String rqstTel,
            @ModelAttribute("vo") EnterCldrApplyVo vo,
            ModelMap model) {
        K2Util.setFnctInfo(fnctService, siteId, FNCT_ID, model);
        model.addAttribute("setup", enterCldrApplyService.getSetup(fnctNo));
        model.addAttribute("rqstNm", rqstNm);
        if (rqstNm != null && !rqstNm.trim().isEmpty()
                && rqstTel != null && !rqstTel.trim().isEmpty()) {
            model.addAttribute("artclList", enterCldrApplyService.getArtclListByUser(fnctNo, rqstNm, rqstTel));
        }
        return "skin:artclSearchList";
    }

    /**
     * 신청 상세 조회 (사용자)
     */
    @RequestMapping("/{siteId}/{fnctNo}/{artclSeq}/artclView")
    @Layout
    public String artclView(
            @PathVariable String siteId,
            @PathVariable Integer fnctNo,
            @PathVariable Integer artclSeq,
            @ModelAttribute("vo") EnterCldrApplyVo vo,
            ModelMap model) {
        
    	EnterCldrSetup setup = enterCldrApplyService.getSetup(fnctNo);
    	if(setup == null) {
    		MessageVo messageVo = new MessageVo();
    		messageVo.setMessage( TextTag.getText( request, "정상적인 접근이 아닙니다.", null, "" ) ); 
    		messageVo.setLocation( UrlTag.getValue( request, "/enterCldrApply/" + siteId + "/" + fnctNo + "/main" ) );
    		
    		return "message:";
    	}
		
    	EnterCldrArtcl artcl = enterCldrApplyService.getArtcl(fnctNo, artclSeq);
    	if(artcl == null) {
    		MessageVo messageVo = new MessageVo();
    		messageVo.setMessage( TextTag.getText( request, "신청 정보를 찾을 수 없습니다.", null, "" ) ); 
    		messageVo.setLocation( UrlTag.getValue( request, "/enterCldrApply/" + siteId + "/" + fnctNo + "/artclSearch" ) );
    		
    		return "message:";
    	}
    	
        Date now = new Date();
        boolean canModify = true;
        if (setup.getModStartDt() != null && now.before(setup.getModStartDt())) canModify = false;
        if (setup.getModEndDt() != null   && now.after(setup.getModEndDt()))   canModify = false;
        
        model.addAttribute("setup", setup);
        model.addAttribute("artcl", artcl);
        model.addAttribute("canModify", canModify);

        return "skin:artclView";
    }

    /**
     * 신청 수정 폼 (사용자)
     */
    @RequestMapping("/{siteId}/{fnctNo}/{artclSeq}/artclUpdtView")
    @Layout
    public String artclUpdtView(
            @PathVariable String siteId,
            @PathVariable Integer fnctNo,
            @PathVariable Integer artclSeq,
            @ModelAttribute("vo") EnterCldrApplyVo vo,
            ModelMap model) {
        K2Util.setFnctInfo(fnctService, siteId, FNCT_ID, model);
        EnterCldrSetup setup = enterCldrApplyService.getSetup(fnctNo);
        
        if (setup == null) {
        	MessageVo messageVo = new MessageVo();
    		messageVo.setMessage( TextTag.getText( request, "정상적인 접근이 아닙니다.", null, "" ) ); 
    		messageVo.setLocation( UrlTag.getValue( request, "/enterCldrApply/" + siteId + "/" + fnctNo + "/main" ) );
    		
    		return "message:";
        }

        Date now = new Date();
        boolean canModify = true;
        if (setup.getModStartDt() != null && now.before(setup.getModStartDt())) canModify = false;
        if (setup.getModEndDt() != null   && now.after(setup.getModEndDt()))   canModify = false;

        EnterCldrArtcl artcl = enterCldrApplyService.getArtcl(fnctNo, artclSeq);
        if(artcl == null) {
    		MessageVo messageVo = new MessageVo();
    		messageVo.setMessage( TextTag.getText( request, "신청 정보를 찾을 수 없습니다.", null, "" ) ); 
    		messageVo.setLocation( UrlTag.getValue( request, "/enterCldrApply/" + siteId + "/" + fnctNo + "/artclSearch" ) );
    		
    		return "message:";
    	}
        
        String artclDtStr = "";
        if (artcl != null && artcl.getArtclDt() != null) {
            artclDtStr = new java.text.SimpleDateFormat("yyyy-MM-dd").format(artcl.getArtclDt());
        }
        model.addAttribute("setup",          setup);
        model.addAttribute("artcl",          artcl);
        model.addAttribute("canModify",      canModify);
        model.addAttribute("timeSlotList",   enterCldrApplyService.getTimeSlotListForEdit(fnctNo, artclSeq, artclDtStr));
        model.addAttribute("targetItemList", enterCldrApplyService.getTargetItemList(fnctNo));
        model.addAttribute("formItemList",   enterCldrApplyService.getFormItemList(fnctNo));
        
        return "skin:artclUpdtView";
    }

    /**
     * 신청 수정 처리 (사용자)
     */
    @RequestMapping("/{siteId}/artclUpdtProc")
    @ResponseBody
    public void artclUpdtProc(
            @PathVariable String siteId,
            EnterCldrArtcl artcl,
            HttpServletResponse response) {
        String message = null;
        try {
            EnterCldrSetup setup = enterCldrApplyService.getSetup(artcl.getSetupSeq());
            if (setup == null) {
                message = "유효하지 않은 신청입니다.";
            } else {
                Date now = new Date();
                if (setup.getModStartDt() != null && now.before(setup.getModStartDt())) {
                    message = "수정 가능 기간이 아닙니다. 관리자에게 문의바랍니다.";
                } else if (setup.getModEndDt() != null && now.after(setup.getModEndDt())) {
                    message = "수정 가능 기간이 마감되었습니다. 관리자에게 문의바랍니다.";
                } else {
                    enterCldrApplyService.setArtclUpdt(artcl, false);
                }
            }
        } catch (DataAccessException e) {
            LOG.error("신청 수정 오류", e);
            message = "처리 중 오류가 발생하였습니다.";
        }
        JsonObject jsonObj = new JsonObject();
        if (message != null) jsonObj.addProperty("message", message);
        ResponseUtil.setJsonDataToResponse(response, jsonObj);
    }
    
    /**
     * 신청 취소 처리 (상태를 CANCELED로 변경, 물리삭제 아님)
     */
    @RequestMapping("/{siteId}/{fnctNo}/{seq}/artclCancelProc")
    @ResponseBody
    public void artclCancelProc(
            @PathVariable String siteId,
            @PathVariable Integer fnctNo,
            @PathVariable Integer seq,
            @RequestParam String rqstNm,
            @RequestParam String rqstTel,
            HttpServletResponse response) {
        String message = null;
        try {
            EnterCldrArtcl artcl = enterCldrApplyService.getArtcl(fnctNo, seq);
            if (artcl == null) {
                message = "신청 정보를 찾을 수 없습니다.";
            } else if (!artcl.getRqstNm().equals(rqstNm) || !artcl.getRqstTel().equals(rqstTel)) {
                message = "신청자 정보가 일치하지 않습니다.";
            } else if (!"WAIT".equals(artcl.getArtclStatus())) {
                message = "취소 가능한 상태가 아닙니다. 관리자에게 문의바랍니다.";
            } else {
                EnterCldrSetup setup = enterCldrApplyService.getSetup(artcl.getSetupSeq());
                if (setup != null) {
                    Date now = new Date();
                    if (setup.getModStartDt() != null && now.before(setup.getModStartDt())) {
                        message = "취소 가능 기간이 아닙니다. 관리자에게 문의바랍니다.";
                    } else if (setup.getModEndDt() != null && now.after(setup.getModEndDt())) {
                        message = "취소 가능 기간이 마감되었습니다. 관리자에게 문의바랍니다.";
                    }
                }
                if (message == null) {
                    enterCldrApplyService.setArtclStatusUpdt(fnctNo, seq, "CANCELED", false);
                }
            }
        } catch (DataAccessException e) {
            LOG.error("신청 취소 오류", e);
            message = "처리 중 오류가 발생하였습니다.";
        }
        JsonObject jsonObj = new JsonObject();
        if (message != null) jsonObj.addProperty("message", message);
        ResponseUtil.setJsonDataToResponse(response, jsonObj);
    }

    
    /**
     * 첨부파일 다운로드 — 웹 취약점 방지를 위해 POST 전용
     */
    @RequestMapping(value = "/{siteId}/{atchmnflSeq}/fileDown", method = RequestMethod.POST)
    public void fileDown(
            @PathVariable String siteId,
            @PathVariable Integer atchmnflSeq,
            HttpServletResponse response) {

        EnterCldrAtchmnfl file = enterCldrApplyService.getAtchmnfl(atchmnflSeq);
        if (file == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        File baseDir = new File(file.getFilePath());
        File f = new File(baseDir, file.getChangeNm());
        try {
            if (!f.getCanonicalPath().startsWith(baseDir.getCanonicalPath() + File.separator)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
        } catch (java.io.IOException e) {
            LOG.error("파일 경로 검증 오류", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }
        if (!f.exists() || !f.isFile()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        java.io.FileInputStream in = null;
        try {
            String encodedNm = java.net.URLEncoder.encode(file.getOrginlNm(), "UTF-8").replace("+", "%20");
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition",
                "attachment; filename=\"" + encodedNm + "\"; filename*=UTF-8''" + encodedNm);
            response.setContentLengthLong(f.length());

            in = new java.io.FileInputStream(f);
            java.io.OutputStream out = response.getOutputStream();
            byte[] buf = new byte[8192];
            int len;
            while ((len = in.read(buf)) != -1) {
                out.write(buf, 0, len);
            }
            out.flush();
        } catch (java.io.IOException e) {
            LOG.error("파일 다운로드 오류", e);
        } finally {
            if (in != null) { try { in.close(); } catch (java.io.IOException ignored) {} }
        }
    }

    private static String dateToString(Date date, String pattern) {
        if (date == null || pattern == null || pattern.isEmpty()) return "";
        return new SimpleDateFormat(pattern).format(date);
    }
}
