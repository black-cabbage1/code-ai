<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/jsp_header.jsp" %>

<c:set var="isEdit" value="${setup != null}"/>
<c:set var="mode" value="_mngr"/>
<c:set var="pageTitle" value="${multiNm}"/>
<c:set var="pageLoca01" value="${fnctInfo.fnctInfoLang.fnctNm}"/>
<c:set var="pageLoca02">설정 ${isEdit ? '수정' : '등록'}</c:set>

<%@ include file="/WEB-INF/jsp/include/mngr/manager/manager_header.jsp"%>

<script>
/* ===================================================
   공통 상수
=================================================== */
var SITE_ID   = "${vo.siteId}";
var SETUP_SEQ = "${setup.setupSeq}";
var DAY_FIELDS = ['monYn','tueYn','wedYn','thuYn','friYn','satYn','sunYn'];

/* ===================================================
   탭 전환 – sessionStorage로 현재 탭 유지
=================================================== */
$(function() {
    /* datepicker 공통 적용 */
    $(".jf-datepicker").datepicker({
        dateFormat    : "yy-mm-dd",
        changeMonth   : true,
        changeYear    : true,
        showButtonPanel: true,
        showMonthAfterYear: true,
        monthNames    : ['1','2','3','4','5','6','7','8','9','10','11','12'],
        monthNamesShort:['1','2','3','4','5','6','7','8','9','10','11','12']
    });

    /* 탭 복원 */
    var savedTab = sessionStorage.getItem("enterCldrApply_tab_" + SETUP_SEQ) || "tabSetup";
    jf_tabClick(savedTab);

    /* 대상별 인원 사용 토글 */
    $('input[name="targetCompUseYn"]').on('change', function() {
        $('#targetItemSection').toggle($(this).val() === 'Y');
    });
    if ($('input[name="targetCompUseYn"]:checked').val() === 'Y') {
        $('#targetItemSection').show();
    }
});

/* ---------------------------------------------------
   [reload 헬퍼] 등록 후 같은 탭으로 돌아오기
--------------------------------------------------- */
function jf_reloadTab(tabId) {
    sessionStorage.setItem("enterCldrApply_tab_" + SETUP_SEQ, tabId);
    location.reload();
}

/* ===================================================
   기본 설정 저장
=================================================== */
function jf_setupSave() {
    /* 필수값 검증 */
    var setupNm    = $("input[name='setupNm']").val().trim();
    var recvStart  = $("input[name='recvStartDt']").val().trim();
    var recvEnd    = $("input[name='recvEndDt']").val().trim();
    var modStart   = $("input[name='modStartDt']").val().trim();
    var modEnd     = $("input[name='modEndDt']").val().trim();

    if (!setupNm)   { alert("설정명을 입력하세요.", function(){$("input[name='setupNm']").focus();}); return;}
    if (!recvStart) { alert("접수 기간의 시작일을 선택하세요.", function(){$("input[name='recvStartDt']").focus();});  return; }
    if (!recvEnd)   { alert("접수 기간의 종료일을 선택하세요.", function(){$("input[name='recvEndDt']").focus();});  return; }
    if (recvStart > recvEnd) { alert("접수 기간의 시작일이 종료일보다 늦을 수 없습니다.", function(){$("input[name='recvStartDt']").focus();}); return; }
    if (!modStart)  { alert("수정 가능 기간의 시작일을 선택하세요.", function(){$("input[name='modStartDt']").focus();}); return; }
    if (!modEnd)    { alert("수정 가능 기간의 종료일을 선택하세요.", function(){$("input[name='modEndDt']").focus();}); return; }
    if (modStart > modEnd)   { alert("수정 가능 기간의 시작일이 종료일보다 늦을 수 없습니다.", function(){$("input[name='modStartDt']").focus();}); return; }

    var url = kurl("/enterCldrApply/fnctMngr/" + SITE_ID + "/<c:choose><c:when test='${isEdit}'>setupUpdtProc</c:when><c:otherwise>setupRegistProc</c:otherwise></c:choose>");

    var data = $("form[name='setupForm']").serializeArray();
    var names = $.map(data, function(f){ return f.name; });
    $.each(DAY_FIELDS, function(i, name) {
        if ($.inArray(name, names) === -1) data.push({ name: name, value: 'N' });
    });

    $.ajax({
        url: url, type: "POST", data: data,
        success: function(r) {
            if (!r.result) { alert("실패하였습니다"); return; }
            <c:choose>
            <c:when test="${isEdit}">alert("저장되었습니다.");</c:when>
            <c:otherwise>
            	alert("등록되었습니다.", function(){
            		location.href = kurl("/enterCldrApply/fnctMngr/" + SITE_ID + "/setupList");		
            	});
            </c:otherwise>
            </c:choose>
        }
    });
}

/* ===================================================
   시간 슬롯
=================================================== */
function jf_timeSlotRegist() {
    var applyTime = $("#newSlotTime").val().trim();
    var capacity  = $("#newSlotLimit").val().trim();
    var sortNo    = $("#newSlotSort").val().trim();
    if (!applyTime) { alert("신청 시간을 입력하세요."); return; }
    if (capacity === "") { alert("접수 건수를 입력하세요. (0 입력 시 무제한)"); return; }
    $.ajax({
        url: kurl("/enterCldrApply/fnctMngr/" + SITE_ID + "/timeSlotRegistProc"),
        type: "POST",
        data: { setupSeq: SETUP_SEQ, applyTime: applyTime, capacity: capacity, sortNo: sortNo },
        success: function(r) {
            if (!r.result) { alert("실패하였습니다"); return; }
            jf_reloadTab("tabSetup");
        }
    });
}

function jf_timeSlotEdit(seq) {
    $("tr[id^='editSlot_']").hide();
    $("tr[id^='viewSlot_']").show();
    $("#viewSlot_" + seq).hide();
    $("#editSlot_" + seq).show();
}
function jf_timeSlotEditCancel(seq) {
    $("#editSlot_" + seq).hide();
    $("#viewSlot_" + seq).show();
}
function jf_timeSlotUpdt(seq) {
    var applyTime = $("#editSlotTime_" + seq).val().trim();
    var capacity  = $("#editSlotCapacity_" + seq).val().trim();
    var sortNo    = $("#editSlotSort_" + seq).val().trim();
    if (!applyTime) { alert("신청 시간을 입력하세요."); return; }
    if (capacity === "") { alert("접수 건수를 입력하세요. (0 입력 시 무제한)"); return; }
    $.ajax({
        url: kurl("/enterCldrApply/fnctMngr/" + SITE_ID + "/timeSlotUpdtProc"),
        type: "POST",
        data: { slotSeq: seq, applyTime: applyTime, capacity: capacity, sortNo: sortNo },
        success: function(r) {
            if (!r.result) { alert("실패하였습니다"); return; }
            jf_reloadTab("tabSetup");
        }
    });
}
function jf_timeSlotDelete(seq) {
    confirm("삭제하시겠습니까?",
        function() {
            $.ajax({
                url: kurl("/enterCldrApply/fnctMngr/" + SITE_ID + "/timeSlotDeleteProc"),
                type: "POST", data: { seq: seq },
                success: function(r) {
                    if (!r.result) { alert("실패하였습니다"); return; }
                    $("#viewSlot_" + seq).remove();
                    $("#editSlot_" + seq).remove();
                    if ($("#tblTimeSlot tbody tr.data-row").length === 0) {
                        $("#tblTimeSlot tbody").prepend('<tr class="no-data"><td colspan="4" class="_noData">등록된 시간 슬롯이 없습니다.</td></tr>');
                    }
                }
            });
        }, function() {}
    );
}

/* ===================================================
   신청 대상
=================================================== */
function jf_targetItemRegist() {
    var targetNm = $("#newTargetNm").val().trim();
    var sortNo   = $("#newTargetSort").val().trim();
    if (!targetNm) { alert("대상명을 입력하세요."); return; }
    $.ajax({
        url: kurl("/enterCldrApply/fnctMngr/" + SITE_ID + "/targetItemRegistProc"),
        type: "POST",
        data: { setupSeq: SETUP_SEQ, targetNm: targetNm, sortNo: sortNo },
        success: function(r) {
            if (!r.result) { alert("실패하였습니다"); return; }
            jf_reloadTab("tabSetup");
        }
    });
}

function jf_targetItemEdit(seq) {
    $("tr[id^='editTarget_']").hide();
    $("tr[id^='viewTarget_']").show();
    $("#viewTarget_" + seq).hide();
    $("#editTarget_" + seq).show();
}
function jf_targetItemEditCancel(seq) {
    $("#editTarget_" + seq).hide();
    $("#viewTarget_" + seq).show();
}
function jf_targetItemUpdt(seq) {
    var targetNm = $("#editTargetNm_" + seq).val().trim();
    var sortNo   = $("#editTargetSort_" + seq).val().trim();
    if (!targetNm) { alert("대상명을 입력하세요."); return; }
    $.ajax({
        url: kurl("/enterCldrApply/fnctMngr/" + SITE_ID + "/targetItemUpdtProc"),
        type: "POST",
        data: { targetItemSeq: seq, targetNm: targetNm, sortNo: sortNo },
        success: function(r) {
            if (!r.result) { alert("실패하였습니다"); return; }
            jf_reloadTab("tabSetup");
        }
    });
}
function jf_targetItemDelete(seq) {
    confirm("삭제하시겠습니까?",
        function() {
            $.ajax({
                url: kurl("/enterCldrApply/fnctMngr/" + SITE_ID + "/targetItemDeleteProc"),
                type: "POST", data: { seq: seq },
                success: function(r) {
                    if (!r.result) { alert("실패하였습니다"); return; }
                    $("#viewTarget_" + seq).remove();
                    $("#editTarget_" + seq).remove();
                    if ($("#tblTarget tbody tr.data-row").length === 0) {
                        $("#tblTarget tbody").prepend('<tr class="no-data"><td colspan="3" class="_noData">등록된 신청 대상이 없습니다.</td></tr>');
                    }
                }
            });
        }, function() {}
    );
}

/* ===================================================
   추가 폼 항목
=================================================== */
function jf_formItemRegist() {
    var itemNm     = $("#newFormItemNm").val().trim();
    var itemType   = $("#newFormItemType").val();
    var requiredYn = $("input[name='newFormRequiredYn']:checked").val() || "N";
    var itemOptions = $("#newFormItemValues").val().trim();
    var sortNo     = $("#newFormItemSort").val().trim();
    if (!itemNm) { alert("항목명을 입력하세요."); return; }
    $.ajax({
        url: kurl("/enterCldrApply/fnctMngr/" + SITE_ID + "/formItemRegistProc"),
        type: "POST",
        data: { setupSeq: SETUP_SEQ, itemNm: itemNm, itemType: itemType,
                requiredYn: requiredYn, itemOptions: itemOptions, sortNo: sortNo },
        success: function(r) {
            if (!r.result) { alert("실패하였습니다"); return; }
            jf_reloadTab("tabSetup");
        }
    });
}

function jf_formItemEdit(seq) {
    $("tr[id^='editForm_']").hide();
    $("tr[id^='viewForm_']").show();
    $("#viewForm_" + seq).hide();
    $("#editForm_" + seq).show();
}
function jf_formItemEditCancel(seq) {
    $("#editForm_" + seq).hide();
    $("#viewForm_" + seq).show();
}
function jf_formItemUpdt(seq) {
    var itemNm      = $("#editFormNm_" + seq).val().trim();
    var itemType    = $("#editFormType_" + seq).val();
    var requiredYn  = $("input[name='editFormReq_" + seq + "']:checked").val() || "N";
    var itemOptions = $("#editFormOptions_" + seq).val().trim();
    var sortNo      = $("#editFormSort_" + seq).val().trim();
    if (!itemNm) { alert("항목명을 입력하세요."); return; }
    $.ajax({
        url: kurl("/enterCldrApply/fnctMngr/" + SITE_ID + "/formItemUpdtProc"),
        type: "POST",
        data: { formItemSeq: seq, itemNm: itemNm, itemType: itemType,
                requiredYn: requiredYn, itemOptions: itemOptions, sortNo: sortNo },
        success: function(r) {
            if (!r.result) { alert("실패하였습니다"); return; }
            jf_reloadTab("tabSetup");
        }
    });
}
function jf_formItemDelete(seq) {
    confirm("삭제하시겠습니까?",
        function() {
            $.ajax({
                url: kurl("/enterCldrApply/fnctMngr/" + SITE_ID + "/formItemDeleteProc"),
                type: "POST", data: { seq: seq },
                success: function(r) {
                    if (!r.result) { alert(r.errorMsg || "실패하였습니다"); return; }
                    $("#viewForm_" + seq).remove();
                    $("#editForm_" + seq).remove();
                    if ($("#tblFormItem tbody tr.data-row").length === 0) {
                        $("#tblFormItem tbody").prepend('<tr class="no-data"><td colspan="6" class="_noData">등록된 폼 항목이 없습니다.</td></tr>');
                    }
                }
            });
        }, function() {}
    );
}

/* ===================================================
   휴일
=================================================== */
function jf_holidayRegist() {
    var holidayDt = $("#newHolidayDt").val().trim();
    var holidayNm = $("#newHolidayReason").val().trim();
    if (!holidayDt) { alert("휴일 일자를 선택하세요."); return; }
    $.ajax({
        url: kurl("/enterCldrApply/fnctMngr/" + SITE_ID + "/holidayRegistProc"),
        type: "POST",
        data: { setupSeq: SETUP_SEQ, holidayDt: holidayDt, holidayNm: holidayNm },
        success: function(r) {
            if (!r.result) { alert("실패하였습니다"); return; }
            jf_reloadTab("tabHoliday");
        }
    });
}

function jf_holidayEdit(seq) {
    $("tr[id^='editHoliday_']").hide();
    $("tr[id^='viewHoliday_']").show();
    $("#viewHoliday_" + seq).hide();
    $("#editHoliday_" + seq).show();
}
function jf_holidayEditCancel(seq) {
    $("#editHoliday_" + seq).hide();
    $("#viewHoliday_" + seq).show();
}
function jf_holidayUpdt(seq) {
    var holidayDt = $("#editHolidayDt_" + seq).val().trim();
    var holidayNm = $("#editHolidayNm_" + seq).val().trim();
    if (!holidayDt) { alert("휴일 일자를 선택하세요."); return; }
    $.ajax({
        url: kurl("/enterCldrApply/fnctMngr/" + SITE_ID + "/holidayUpdtProc"),
        type: "POST",
        data: { holidaySeq: seq, holidayDt: holidayDt, holidayNm: holidayNm },
        success: function(r) {
            if (!r.result) { alert("실패하였습니다"); return; }
            jf_reloadTab("tabHoliday");
        }
    });
}
function jf_holidayDelete(seq) {
    confirm("삭제하시겠습니까?",
        function() {
            $.ajax({
                url: kurl("/enterCldrApply/fnctMngr/" + SITE_ID + "/holidayDeleteProc"),
                type: "POST", data: { seq: seq },
                success: function(r) {
                    if (!r.result) { alert("실패하였습니다"); return; }
                    $("#viewHoliday_" + seq).remove();
                    $("#editHoliday_" + seq).remove();
                    if ($("#tblHoliday tbody tr.data-row").length === 0) {
                        $("#tblHoliday tbody").prepend('<tr class="no-data"><td colspan="3" class="_noData">등록된 휴일이 없습니다.</td></tr>');
                    }
                }
            });
        }, function() {}
    );
}

/* ===================================================
   첨부파일
=================================================== */
function jf_fileUploadCallback(siteId, today, fileOrg, fileRename, fileSize, num) {
    if (num === 'atchmnfl') {
        var ext = fileOrg.lastIndexOf(".") > -1 ? fileOrg.substring(fileOrg.lastIndexOf(".") + 1) : "";
        $("#atchmnflOrginlNm").val(fileOrg);
        $("#atchmnflChangeNm").val(fileRename);
        $("#atchmnflFilePath").val(today);
        $("#atchmnflFileExt").val(ext);
        $("#atchmnflDisplayNm").val(fileOrg);
    }
}

function jf_atchmnflRegist() {
    if (!$("#atchmnflChangeNm").val()) { alert("파일을 선택하세요."); return; }
    $.ajax({
        url: kurl("/enterCldrApply/fnctMngr/" + SITE_ID + "/atchmnflRegistProc"),
        type: "POST",
        data: {
            setupSeq  : SETUP_SEQ,
            orginlNm  : $("#atchmnflOrginlNm").val(),
            changeNm  : $("#atchmnflChangeNm").val(),
            filePath  : $("#atchmnflFilePath").val(),
            fileExt   : $("#atchmnflFileExt").val()
        },
        success: function(r) {
            if (!r.result) { alert("실패하였습니다"); return; }
            jf_reloadTab("tabSetup");
        }
    });
}

function jf_atchmnflDelete(seq, trElem) {
    confirm("첨부파일을 삭제하시겠습니까?",
        function() {
            $.ajax({
                url: kurl("/enterCldrApply/fnctMngr/" + SITE_ID + "/atchmnflDeleteProc"),
                type: "POST", data: { seq: seq },
                success: function(r) {
                    if (!r.result) { alert("실패하였습니다"); return; }
                    $(trElem).closest("li").remove();
                }
            });
        }, function() {}
    );
}
</script>

<%@ include file="/WEB-INF/jsp/include/mngr/manager/manager_body.jsp"%>
<c:if test="${isEdit}">
<%@ include file="/WEB-INF/jsp/k2web/module/enterCldrApply/fnctMngr/fnctInfoTabs.jsp"%>
</c:if>

<%-- ============================================================
     탭1: 기본 설정
============================================================ --%>
<div id="tabSetup" class="tab-content">
<form name="setupForm" method="post">
	<input type="hidden" name="siteId" value="${vo.siteId}">
    <c:if test="${isEdit}">
        <input type="hidden" name="setupSeq" value="${setup.setupSeq}">
    </c:if>
    
    <h2>기본 정보</h2>
    <div class="_write _labelW01">

        <div class="_form">
            <label class="_label"><mark class="must">*</mark>설정명</label>
            <div class="_insert">
                <input type="text" name="setupNm" class="_full" value="<c:out value='${setup.setupNm}'/>">
            </div>
        </div>

        <div class="_form">
            <label class="_label"><mark class="must">*</mark>사용여부</label>
            <div class="_insert">
                <label><input type="radio" name="useYn" value="Y" ${setup.useYn == 'Y' || !isEdit ? 'checked' : ''}> 사용</label>
                <label><input type="radio" name="useYn" value="N" ${setup.useYn == 'N' ? 'checked' : ''}> 미사용</label>
            </div>
        </div>
		
		<div class="_form">
            <label class="_label"><mark class="must">*</mark>접수 기간</label>
            <div class="_insert">
                <input type="text" name="recvStartDt" class="jf-datepicker" style="width:120px;"
                    value="<fmt:formatDate value='${setup.recvStartDt}' pattern='yyyy-MM-dd'/>">
                ~
                <input type="text" name="recvEndDt" class="jf-datepicker" style="width:120px;"
                    value="<fmt:formatDate value='${setup.recvEndDt}' pattern='yyyy-MM-dd'/>">
            </div>
        </div>

        <div class="_form">
            <label class="_label"><mark class="must">*</mark>수정 가능 기간</label>
            <div class="_insert">
                <input type="text" name="modStartDt" class="jf-datepicker" style="width:120px;"
                    value="<fmt:formatDate value='${setup.modStartDt}' pattern='yyyy-MM-dd'/>">
                ~
                <input type="text" name="modEndDt" class="jf-datepicker" style="width:120px;"
                    value="<fmt:formatDate value='${setup.modEndDt}' pattern='yyyy-MM-dd'/>">
            </div>
        </div>
        
        <div class="_form">
            <label class="_label"><mark class="must">*</mark>신청 가능 요일</label>
            <div class="_insert">
                <label><input type="checkbox" name="monYn" value="Y" ${setup.monYn == 'Y' ? 'checked' : ''}> 월</label>
                <label><input type="checkbox" name="tueYn" value="Y" ${setup.tueYn == 'Y' ? 'checked' : ''}> 화</label>
                <label><input type="checkbox" name="wedYn" value="Y" ${setup.wedYn == 'Y' ? 'checked' : ''}> 수</label>
                <label><input type="checkbox" name="thuYn" value="Y" ${setup.thuYn == 'Y' ? 'checked' : ''}> 목</label>
                <label><input type="checkbox" name="friYn" value="Y" ${setup.friYn == 'Y' ? 'checked' : ''}> 금</label>
                <label><input type="checkbox" name="satYn" value="Y" ${setup.satYn == 'Y' ? 'checked' : ''}> 토</label>
                <label><input type="checkbox" name="sunYn" value="Y" ${setup.sunYn == 'Y' ? 'checked' : ''}> 일</label>
            </div>
        </div>

        <div class="_form">
            <label class="_label">동반인원 사용</label>
            <div class="_insert">
                <label><input type="radio" name="companionUseYn" value="Y" ${setup.companionUseYn == 'Y' ? 'checked' : ''}> 사용</label>
                <label><input type="radio" name="companionUseYn" value="N" ${setup.companionUseYn != 'Y' ? 'checked' : ''}> 미사용</label>
            </div>
        </div>

        <div class="_form">
            <label class="_label">대상별 인원 사용</label>
            <div class="_insert">
                <label><input type="radio" name="targetCompUseYn" value="Y" ${setup.targetCompUseYn == 'Y' ? 'checked' : ''}> 사용</label>
                <label><input type="radio" name="targetCompUseYn" value="N" ${setup.targetCompUseYn != 'Y' ? 'checked' : ''}> 미사용</label>

                <c:if test="${isEdit}">
                <div id="targetItemSection" style="display:none; margin-top:12px;">
                    <table id="tblTarget" class="_table _list">
                        <colgroup>
                            <col style="width:100px;"><col style="width:auto;"><col style="width:155px;">
                        </colgroup>
                        <thead>
                            <tr><th>순서</th><th>항목명</th><th>관리</th></tr>
                        </thead>
                        <tbody>
                            <c:choose>
                                <c:when test="${targetItemList != null && fn:length(targetItemList) > 0}">
                                    <c:forEach var="item" items="${targetItemList}">
                                        <tr id="viewTarget_${item.targetItemSeq}" class="data-row">
                                            <td><c:out value="${item.sortNo}"/></td>
                                            <td><c:out value="${item.targetNm}"/></td>
                                            <td>
                                                <span class="_button _small _active"><a href="#none" onclick="jf_targetItemEdit('${item.targetItemSeq}');">수정</a></span>
                                                <span class="_button _small"><a href="#none" onclick="jf_targetItemDelete('${item.targetItemSeq}');">삭제</a></span>
                                            </td>
                                        </tr>
                                        <tr id="editTarget_${item.targetItemSeq}" class="edit-row" style="display:none;">
                                            <td><input type="text" id="editTargetSort_${item.targetItemSeq}" value="${item.sortNo}" style="width:45px;"></td>
                                            <td><input type="text" id="editTargetNm_${item.targetItemSeq}" value="<c:out value='${item.targetNm}'/>" style="width:100%;"></td>
                                            <td>
                                                <span class="_button _small _active"><a href="#none" onclick="jf_targetItemUpdt('${item.targetItemSeq}');">저장</a></span>
                                                <span class="_button _small"><a href="#none" onclick="jf_targetItemEditCancel('${item.targetItemSeq}');">취소</a></span>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>
                                    <tr class="no-data"><td colspan="3" class="_noData">등록된 신청 대상이 없습니다.</td></tr>
                                </c:otherwise>
                            </c:choose>
                            <tr class="_addRow">
                                <td><input type="text" id="newTargetSort" style="width:45px;" placeholder="순서"></td>
                                <td><input type="text" id="newTargetNm"   class="_full" placeholder="항목명 (예: 학부모, 1학년, 2학년)"></td>
                                <td><span class="_button _small _active"><a href="#none" onclick="jf_targetItemRegist();">추가</a></span></td>
                            </tr>
                        </tbody>
                    </table>
                </div>
                </c:if>
            </div>
        </div>

        <div class="_form">
            <label class="_label">중복 신청 허용</label>
            <div class="_insert">
                <label><input type="radio" name="dplcAplyPsblYn" value="N"
                    ${empty setup.dplcAplyPsblYn || setup.dplcAplyPsblYn == 'N' ? 'checked' : ''}> 불가</label>
                &nbsp;
                <label><input type="radio" name="dplcAplyPsblYn" value="Y"
                    ${setup.dplcAplyPsblYn == 'Y' ? 'checked' : ''}> 허용</label>
            </div>
            <div class="_insert _comment">이름+휴대전화 기준으로 동일 신청 모듈에 중복 신청 허용 여부</div>
        </div>

        <div class="_form">
            <label class="_label">회차 정보</label>
            <div class="_insert">
                <c:choose>
                    <c:when test="${isEdit}">
                    <table id="tblTimeSlot" class="_table _list">
                        <colgroup>
                            <col style="width:100px;"><col style="width:180px;"><col style="width:auto;"><col style="width:155px;">
                        </colgroup>
                        <thead>
                            <tr><th>순서</th><th>신청 시간</th><th>접수 건수</th><th>관리</th></tr>
                        </thead>
                        <tbody>
                            <c:choose>
                                <c:when test="${timeSlotList != null && fn:length(timeSlotList) > 0}">
                                    <c:forEach var="slot" items="${timeSlotList}">
                                        <tr id="viewSlot_${slot.slotSeq}" class="data-row">
                                            <td><c:out value="${slot.sortNo}"/></td>
                                            <td><c:out value="${slot.applyTime}"/></td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${slot.capacity == 0}">무제한</c:when>
                                                    <c:otherwise><c:out value="${slot.capacity}"/> 건</c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <span class="_button _small _active"><a href="#none" onclick="jf_timeSlotEdit('${slot.slotSeq}');">수정</a></span>
                                                <span class="_button _small"><a href="#none" onclick="jf_timeSlotDelete('${slot.slotSeq}');">삭제</a></span>
                                            </td>
                                        </tr>
                                        <tr id="editSlot_${slot.slotSeq}" class="edit-row" style="display:none;">
                                            <td><input type="text" id="editSlotSort_${slot.slotSeq}" value="${slot.sortNo}" style="width:55px;"></td>
                                            <td><input type="text" id="editSlotTime_${slot.slotSeq}" value="${slot.applyTime}" style="width:100px;"></td>
                                            <td><input type="number" id="editSlotCapacity_${slot.slotSeq}" value="${slot.capacity}" min="0" style="width:80px;"></td>
                                            <td>
                                                <span class="_button _small _active"><a href="#none" onclick="jf_timeSlotUpdt('${slot.slotSeq}');">저장</a></span>
                                                <span class="_button _small"><a href="#none" onclick="jf_timeSlotEditCancel('${slot.slotSeq}');">취소</a></span>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>
                                    <tr class="no-data"><td colspan="4" class="_noData">등록된 회차 정보가 없습니다.</td></tr>
                                </c:otherwise>
                            </c:choose>
                            <tr class="_addRow">
                                <td><input type="text" id="newSlotSort" style="width:55px;" placeholder="순서"></td>
                                <td><input type="text" id="newSlotTime" style="width:100px;" placeholder="HH:MM"></td>
                                <td><input type="number" id="newSlotLimit" style="width:80px;" min="0" placeholder="0=무제한"></td>
                                <td><span class="_button _small _active"><a href="#none" onclick="jf_timeSlotRegist();">추가</a></span></td>
                            </tr>
                        </tbody>
                    </table>
                    </c:when>
                    <c:otherwise>
                        <span style="color:#888;">설정 등록 후 추가할 수 있습니다.</span>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>

        <div class="_form">
            <label class="_label">신청항목 관리</label>
            <div class="_insert">
                <c:choose>
                    <c:when test="${isEdit}">
                    <table id="tblFormItem" class="_table _list">
                        <colgroup>
                            <col style="width:100px;">
                            <col style="width:auto;">
                            <col style="width:110px;">
                            <col style="width:100px;">
                            <col style="width:auto;">
                            <col style="width:155px;">
                        </colgroup>
                        <thead>
                            <tr><th>순서</th><th>항목명</th><th>유형</th><th>필수여부</th><th>내용</th><th>관리</th></tr>
                        </thead>
                        <tbody>
                            <c:choose>
                                <c:when test="${formItemList != null && fn:length(formItemList) > 0}">
                                    <c:forEach var="item" items="${formItemList}">
                                        <c:set var="isLocked" value="${item.itemType == 'RQST_NM' || item.itemType == 'RQST_TEL'}"/>
                                        <tr id="viewForm_${item.formItemSeq}" class="data-row">
                                            <td><c:out value="${item.sortNo}"/></td>
                                            <td>
                                                <c:out value="${item.itemNm}"/>
                                                <c:if test="${item.fixedYn == 'Y'}"><span style="color:#888;font-size:11px;margin-left:4px;">[고정]</span></c:if>
                                            </td>
                                            <td><c:out value="${item.itemType}"/></td>
                                            <td>${item.requiredYn == 'Y' ? '필수' : '선택'}</td>
                                            <td><c:out value="${item.itemOptions}"/></td>
                                            <td>
                                                <c:if test="${!isLocked}">
                                                    <span class="_button _small _active"><a href="#none" onclick="jf_formItemEdit('${item.formItemSeq}');">수정</a></span>
                                                    <span class="_button _small"><a href="#none" onclick="jf_formItemDelete('${item.formItemSeq}');">삭제</a></span>
                                                </c:if>
                                                <c:if test="${isLocked}">
                                                    <span style="color:#aaa;font-size:12px;">삭제불가</span>
                                                </c:if>
                                            </td>
                                        </tr>
                                        <c:if test="${!isLocked}">
                                        <tr id="editForm_${item.formItemSeq}" class="edit-row" style="display:none;">
                                            <td><input type="text" id="editFormSort_${item.formItemSeq}" value="${item.sortNo}" style="width:50px;"></td>
                                            <td><input type="text" id="editFormNm_${item.formItemSeq}" value="<c:out value='${item.itemNm}'/>" style="width:100%;"></td>
                                            <td>
                                                <select id="editFormType_${item.formItemSeq}" style="width:95px;">
                                                    <option value="TEXT"     ${item.itemType == 'TEXT'     ? 'selected' : ''}>텍스트</option>
                                                    <option value="Phone"    ${item.itemType == 'Phone'    ? 'selected' : ''}>전화번호</option>
                                                    <option value="Email"    ${item.itemType == 'Email'    ? 'selected' : ''}>이메일</option>
                                                    <option value="Radio"    ${item.itemType == 'Radio'    ? 'selected' : ''}>라디오</option>
                                                    <option value="Checkbox" ${item.itemType == 'Checkbox' ? 'selected' : ''}>체크박스</option>
                                                    <option value="School"   ${item.itemType == 'School'   ? 'selected' : ''}>학교검색</option>
                                                </select>
                                            </td>
                                            <td style="white-space:nowrap;">
                                                <label><input type="radio" name="editFormReq_${item.formItemSeq}" value="Y" ${item.requiredYn == 'Y' ? 'checked' : ''}> 필수</label>
                                                <label><input type="radio" name="editFormReq_${item.formItemSeq}" value="N" ${item.requiredYn != 'Y' ? 'checked' : ''}> 선택</label>
                                            </td>
                                            <td><input type="text" id="editFormOptions_${item.formItemSeq}" value="<c:out value='${item.itemOptions}'/>" style="width:100%;"></td>
                                            <td>
                                                <span class="_button _small _active"><a href="#none" onclick="jf_formItemUpdt('${item.formItemSeq}');">저장</a></span>
                                                <span class="_button _small"><a href="#none" onclick="jf_formItemEditCancel('${item.formItemSeq}');">취소</a></span>
                                            </td>
                                        </tr>
                                        </c:if>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>
                                    <tr class="no-data"><td colspan="6" class="_noData">등록된 폼 항목이 없습니다.</td></tr>
                                </c:otherwise>
                            </c:choose>
                            <tr class="_addRow">
                                <td><input type="text" id="newFormItemSort" style="width:50px;" placeholder="순서"></td>
                                <td><input type="text" id="newFormItemNm" style="width:150px;" placeholder="항목명"></td>
                                <td>
                                    <select id="newFormItemType" style="width:95px;">
                                        <option value="TEXT">텍스트</option>
                                        <option value="Phone">전화번호</option>
                                        <option value="Email">이메일</option>
                                        <option value="Radio">라디오</option>
                                        <option value="Checkbox">체크박스</option>
                                        <option value="School">학교검색</option>
                                    </select>
                                </td>
                                <td style="white-space:nowrap;">
                                    <label><input type="radio" name="newFormRequiredYn" value="Y"> 필수</label>
                                    <label><input type="radio" name="newFormRequiredYn" value="N" checked> 선택</label>
                                </td>
                                <td><input type="text" id="newFormItemValues" class="_full" placeholder="선택항목 값 (Radio/Checkbox일 때, 콤마로 구분)"></td>
                                <td><span class="_button _small _active"><a href="#none" onclick="jf_formItemRegist();">추가</a></span></td>
                            </tr>
                        </tbody>
                    </table>
                    </c:when>
                    <c:otherwise>
                        <span style="color:#888;">설정 등록 후 추가할 수 있습니다.</span>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>

        <div class="_form">
            <label class="_label">신청대상</label>
            <div class="_insert">
                <input type="text" name="applyTarget" class="_full" value="<c:out value='${setup.applyTarget}'/>">
            </div>
        </div>

        <div class="_form">
            <label class="_label">장소</label>
            <div class="_insert">
                <input type="text" name="location" class="_full" value="<c:out value='${setup.location}'/>">
            </div>
        </div>

        <div class="_form">
            <label class="_label">소개</label>
            <div class="_insert">
            	<input type="text" name="intro" class="_full" value="<c:out value='${setup.intro}'/>">
            </div>
        </div>

        <div class="_form">
            <label class="_label">내용</label>
            <div class="_insert">
                <textarea name="content" class="_editor"><c:out value="${setup.content}"/></textarea>
            </div>
        </div>

        <%-- 첨부파일 --%>
        <div class="_form">
            <label class="_label">첨부파일</label>
            <div class="_insert">
                <c:if test="${isEdit && atchmnflList != null && fn:length(atchmnflList) > 0}">
                    <ul class="file-list" style="margin-bottom:8px; padding:0; list-style:none;">
                        <c:forEach var="file" items="${atchmnflList}">
                            <li style="margin-bottom:4px;">
                                <span><c:out value="${file.orginlNm}"/></span>                                
                                <span class="_button _small">
                                    <a href="#none" onclick="jf_atchmnflDelete('${file.atchmnflSeq}', this);">삭제</a>
                                </span>
                            </li>
                        </c:forEach>
                    </ul>
                </c:if>
                <c:if test="${isEdit}">
                    <input type="hidden" id="atchmnflOrginlNm">
                    <input type="hidden" id="atchmnflChangeNm">
                    <input type="hidden" id="atchmnflFilePath">
                    <input type="hidden" id="atchmnflFileExt">
                    <input type="text" id="atchmnflDisplayNm" class="_w200" readonly placeholder="파일을 선택하세요">
                    <span class="_button _small _white">
                        <input type="button" onclick="jf_fileUploadFormCommon('atchmnfl','','${vo.siteId}');" value="파일 선택">
                    </span>
                    <span class="_button _small _active">
                        <input type="button" onclick="jf_atchmnflRegist();" value="등록">
                    </span>
                </c:if>
                <c:if test="${!isEdit}">
                    <span style="color:#888;">설정 등록 후 파일을 추가할 수 있습니다.</span>
                </c:if>
            </div>
            <c:if test="${isEdit}">
            	<div class="_insert _comment">
            		'등록' 버튼을 눌러 저장하여야 합니다.
            	</div>
            </c:if>
        </div>

        <div class="_form">
            <label class="_label">팝업 안내 메시지</label>
            <div class="_insert">
                <textarea name="popupMsg" rows="4" class="_full"><c:out value="${setup.popupMsg}"/></textarea>
            </div>
        </div>

    </div><%-- ._write --%>

    <%-- ============================================================
         개인정보 관리
    ============================================================ --%>
    <h2 style="padding-top:20px; margin-bottom:10px;">개인정보 관리</h2>
    <div class="_write _labelW01">
        <div class="_form">
            <label class="_label">개인정보 수집·이용 목적</label>
            <div class="_insert">
                <textarea name="privacyPurpose" rows="4" class="_full"><c:out value="${setup.privacyPurpose}"/></textarea>
            </div>
        </div>

        <div class="_form">
            <label class="_label">수집하는 개인정보 항목</label>
            <div class="_insert">
                <textarea name="privacyItems" rows="4" class="_full"><c:out value="${setup.privacyItems}"/></textarea>
            </div>
        </div>

        <div class="_form">
            <label class="_label">개인정보 보유 및 이용기간</label>
            <div class="_insert">
                <textarea name="privacyPeriod" rows="2" class="_full" style="height:50px;"><c:out value="${setup.privacyPeriod}"/></textarea>
            </div>
        </div>
    </div>

    <div class="_areaButton">
        <span class="_button _large _active">
            <input type="button" onclick="jf_setupSave();" value="${isEdit ? '저장' : '등록'}">
        </span>
        <span class="_button _large">
            <a href="<k:url value='/enterCldrApply/fnctMngr/${vo.siteId}/setupList'/>">목록</a>
        </span>
    </div>
</form>

</div><%-- #tabSetup --%>


<c:if test="${isEdit}">

<%-- ============================================================
     탭2: 휴일 관리
============================================================ --%>
<div id="tabHoliday" class="tab-content" style="display:none;">
    <table id="tblHoliday" class="_table _list">
        <colgroup>
            <col style="width:160px;"><col style="width:auto;"><col style="width:155px;">
        </colgroup>
        <thead>
            <tr><th>휴일 일자</th><th>휴일명</th><th>관리</th></tr>
        </thead>
        <tbody>
            <c:choose>
                <c:when test="${holidayList != null && fn:length(holidayList) > 0}">
                    <c:forEach var="holiday" items="${holidayList}">
                        <%-- 보기 행 --%>
                        <tr id="viewHoliday_${holiday.holidaySeq}" class="data-row">
                            <td><fmt:formatDate value="${holiday.holidayDt}" pattern="yyyy-MM-dd"/></td>
                            <td><c:out value="${holiday.holidayNm}"/></td>
                            <td>
                                <span class="_button _small _active">
                                    <a href="#none" onclick="jf_holidayEdit('${holiday.holidaySeq}');">수정</a>
                                </span>
                                <span class="_button _small">
                                    <a href="#none" onclick="jf_holidayDelete('${holiday.holidaySeq}');">삭제</a>
                                </span>
                            </td>
                        </tr>
                        <%-- 수정 행 (숨김) --%>
                        <tr id="editHoliday_${holiday.holidaySeq}" class="edit-row" style="display:none;">
                            <td>
                                <input type="text" id="editHolidayDt_${holiday.holidaySeq}"
                                    class="jf-datepicker" style="width:125px;"
                                    value="<fmt:formatDate value='${holiday.holidayDt}' pattern='yyyy-MM-dd'/>">
                            </td>
                            <td>
                                <input type="text" id="editHolidayNm_${holiday.holidaySeq}"
                                    value="<c:out value='${holiday.holidayNm}'/>" style="width:100%;">
                            </td>
                            <td>
                                <span class="_button _small _active">
                                    <a href="#none" onclick="jf_holidayUpdt('${holiday.holidaySeq}');">저장</a>
                                </span>
                                <span class="_button _small">
                                    <a href="#none" onclick="jf_holidayEditCancel('${holiday.holidaySeq}');">취소</a>
                                </span>
                            </td>
                        </tr>
                    </c:forEach>
                </c:when>
                <c:otherwise>
                    <tr class="no-data"><td colspan="3" class="_noData">등록된 휴일이 없습니다.</td></tr>
                </c:otherwise>
            </c:choose>
            <tr class="_addRow">
                <td>
                    <input type="text" id="newHolidayDt" class="jf-datepicker" style="width:130px;" placeholder="yyyy-MM-dd">
                </td>
                <td><input type="text" id="newHolidayReason" class="_full" placeholder="휴일명(예: 설날, 임시공휴일)"></td>
                <td>
                    <span class="_button _small _active">
                        <a href="#none" onclick="jf_holidayRegist();">추가</a>
                    </span>
                </td>
            </tr>
        </tbody>
    </table>
</div><%-- #tabHoliday --%>

</c:if><%-- isEdit --%>

<%@ include file="/WEB-INF/jsp/include/mngr/manager/manager_footer.jsp"%>
