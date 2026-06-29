package k2web.module.enterCldrApply.web;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import k2web.com.cmm.aop.Layout;
import k2web.com.cop.fnct.service.FnctService;
import k2web.com.util.K2Util;
import k2web.module.enterCldrApply.service.EnterCldrApplyService;
import k2web.module.enterCldrApply.service.EnterCldrApplyVo;
import k2web.module.enterCldrApply.service.model.EnterCldrArtcl;
import k2web.module.enterCldrApply.service.model.EnterCldrSetup;
import k2web.module.enterCldrApply.service.model.EnterCldrTimeSlot;
import k2web.module.enterCldrApply.util.ResponseUtil;

@Controller
@RequestMapping("/enterCldrApply")
public class EnterCldrApplyUserController {

    private static final Logger LOG = LoggerFactory.getLogger(EnterCldrApplyUserController.class);

    private static final String FNCT_ID = EnterCldrApplyService.FNCT_ID;
    private static final String JSP_PATH = "fnct/enterCldrApply/enterCldrApply_basic_skin";

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
        return JSP_PATH + "/main";
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
                    int remain = enterCldrApplyService.getTimeSlotRemainCnt(
                        artcl.getTimeSlotSeq(), dateToString(artcl.getArtclDt(), "yyyy-MM-dd"));
                    if (remain <= 0) {
                        message = "선택하신 시간대의 정원이 마감되었습니다.";
                    } else {
                        enterCldrApplyService.setArtclRegist(artcl);
                    }
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
        return JSP_PATH + "/artclSearch";
    }

    /**
     * 신청 조회 결과 (Ajax) - 이름+전화번호 기준, 취소 포함 전체 이력
     */
    @RequestMapping("/{siteId}/{fnctNo}/artclSearchProc")
    @ResponseBody
    public void artclSearchProc(
            @PathVariable String siteId,
            @PathVariable Integer fnctNo,
            @RequestParam String rqstNm,
            @RequestParam String rqstTel,
            HttpServletResponse response) {
        List<EnterCldrArtcl> list = enterCldrApplyService.getArtclListByUser(fnctNo, rqstNm, rqstTel);
        JsonArray arr = new JsonArray();
        if (list != null) {
            for (EnterCldrArtcl a : list) {
                JsonObject obj = new JsonObject();
                obj.addProperty("artclSeq",     a.getArtclSeq());
                obj.addProperty("artclDt",      dateToString(a.getArtclDt(), "yyyy-MM-dd"));
                obj.addProperty("applyTime",    a.getApplyTime() != null ? a.getApplyTime() : "");
                obj.addProperty("companionCnt", a.getCompanionCnt() != null ? a.getCompanionCnt() : 0);
                obj.addProperty("rgsde",        dateToString(a.getRgsde(), "yyyy-MM-dd'T'HH:mm"));
                obj.addProperty("artclStatus",  a.getArtclStatus() != null ? a.getArtclStatus() : "");
                arr.add(obj);
            }
        }
        JsonObject jsonObj = new JsonObject();
        jsonObj.add("artclList", arr);
        ResponseUtil.setJsonDataToResponse(response, jsonObj);
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
        K2Util.setFnctInfo(fnctService, siteId, FNCT_ID, model);
        model.addAttribute("setup", enterCldrApplyService.getSetup(fnctNo));
        model.addAttribute("artcl", enterCldrApplyService.getArtcl(fnctNo, artclSeq));
        return JSP_PATH + "/artclView";
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
        if (setup == null) return "redirect:/error";

        Date now = new Date();
        boolean canModify = true;
        if (setup.getModStartDt() != null && now.before(setup.getModStartDt())) canModify = false;
        if (setup.getModEndDt() != null   && now.after(setup.getModEndDt()))   canModify = false;

        model.addAttribute("setup",          setup);
        model.addAttribute("artcl",          enterCldrApplyService.getArtcl(fnctNo, artclSeq));
        model.addAttribute("canModify",      canModify);
        model.addAttribute("timeSlotList",   enterCldrApplyService.getTimeSlotList(fnctNo));
        model.addAttribute("targetItemList", enterCldrApplyService.getTargetItemList(fnctNo));
        model.addAttribute("formItemList",   enterCldrApplyService.getFormItemList(fnctNo));
        return JSP_PATH + "/artclUpdt";
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
                    enterCldrApplyService.setArtclUpdt(artcl, null);
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
                    enterCldrApplyService.setArtclStatusUpdt(fnctNo, seq, "CANCELED", null);
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

    
    private static String dateToString(Date date, String pattern) {
        if (date == null || pattern == null || pattern.isEmpty()) return "";
        return new SimpleDateFormat(pattern).format(date);
    }
}
