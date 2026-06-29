<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/jsp_header.jsp" %>

<c:if test='${layout==null || layout==""}'>
<%@ include file="/WEB-INF/jsp/include/user/header.jsp" %>
<%@ include file="/WEB-INF/jsp/fnct/enterCldrApply/enterCldrApply_basic_skin/main.head.jsp" %>
<%@ include file="/WEB-INF/jsp/include/user/body.jsp" %>
</c:if>

<div class="_fnctWrap">

<c:choose>
    <c:when test="${!canModify}">
    <div class="cldr-step">
        <div style="text-align:center; padding:40px; color:#c62828; font-size:16px;">
            수정기간이 아닙니다. 관리자에게 문의바랍니다.
        </div>
        <div class="cldr-btn-area" style="justify-content:center;">
            <button class="cldr-btn cldr-btn-gray" onclick="history.back();">이전으로</button>
        </div>
    </div>
    </c:when>
    <c:otherwise>

    <div class="cldr-step">
        <div class="cldr-step-title">신청자 정보 수정</div>

        <table class="cldr-form-table">
            <tr>
                <th>신청 일자</th>
                <td>
                    <fmt:formatDate value="${artcl.artclDt}" pattern="yyyy-MM-dd"/>
                    <c:if test="${not empty artcl.applyTime}">
                        (<c:out value="${artcl.applyTime}"/>)
                    </c:if>
                    <input type="hidden" id="artclDtHidden" value="<fmt:formatDate value='${artcl.artclDt}' pattern='yyyy-MM-dd'/>">
                    <input type="hidden" id="artclSeqHidden" value="${artcl.artclSeq}">
                    <input type="hidden" id="setupSeqHidden" value="${setup.setupSeq}">
                </td>
            </tr>
            <tr>
                <th>시간 선택</th>
                <td>
                    <c:forEach var="slot" items="${timeSlotList}">
                    <label style="margin-right:12px;">
                        <input type="radio" name="timeSlotSelect" value="${slot.slotSeq}"
                            ${artcl.timeSlotSeq == slot.slotSeq ? 'checked' : ''}>
                        <c:out value="${slot.applyTime}"/>
                    </label>
                    </c:forEach>
                </td>
            </tr>
            <tr>
                <th><span style="color:#c00;">*</span> 이름</th>
                <td><input type="text" id="rqstNm" style="width:200px;" value="<c:out value='${artcl.rqstNm}'/>"></td>
            </tr>
            <tr>
                <th><span style="color:#c00;">*</span> 휴대전화</th>
                <td id="phoneWrap">
                    <%-- JS로 분리 --%>
                </td>
            </tr>
            <tr>
                <th>이메일</th>
                <td id="emailWrap">
                    <%-- JS로 분리 --%>
                </td>
            </tr>
            <tr>
                <th>고교명</th>
                <td>
                    <input type="text" id="schNm" style="width:220px;" readonly
                        value="<c:out value='${artcl.schNm}'/>">
                    <input type="hidden" id="schCd"  value="<c:out value='${artcl.schCd}'/>">
                    <input type="hidden" id="schLc"  value="<c:out value='${artcl.schLc}'/>">
                    <input type="hidden" id="schTp"  value="<c:out value='${artcl.schTp}'/>">
                    <button type="button" class="cldr-btn cldr-btn-primary"
                        style="padding:6px 14px; font-size:13px;" onclick="jf_openSchoolSearch();">검색</button>
                </td>
            </tr>
            <c:if test="${setup.companionUseYn == 'Y'}">
            <tr>
                <th>동행 인원</th>
                <td><input type="number" id="companionCnt" style="width:80px;" min="0"
                        value="${artcl.companionCnt}"> 명</td>
            </tr>
            </c:if>
            <c:if test="${setup.targetCompUseYn == 'Y' && targetItemList != null && fn:length(targetItemList) > 0}">
            <tr>
                <th>대상별 인원</th>
                <td>
                    <table class="cldr-target-table">
                        <c:forEach var="item" items="${targetItemList}" varStatus="s">
                        <c:set var="targetCompCnt" value="0"/>
                        <c:forEach var="t" items="${artcl.targetList}">
                            <c:if test="${t.targetItemSeq == item.targetItemSeq}">
                                <c:set var="targetCompCnt" value="${t.compCnt}"/>
                            </c:if>
                        </c:forEach>
                        <tr>
                            <td><c:out value="${item.targetNm}"/></td>
                            <td>
                                <input type="number" class="target-comp-input"
                                    data-seq="${item.targetItemSeq}" data-idx="${s.index}"
                                    style="width:70px;" min="0" value="${targetCompCnt}">
                            </td>
                            <td>명</td>
                        </tr>
                        </c:forEach>
                    </table>
                </td>
            </tr>
            </c:if>
            <%-- 동적 폼 항목 (RQST_NM/TEL/ML/SCHOOL 제외) --%>
            <c:if test="${formItemList != null && fn:length(formItemList) > 0}">
                <c:set var="dynCnt" value="0"/>
                <c:forEach var="item" items="${formItemList}">
                <c:if test="${item.itemType != 'RQST_NM' && item.itemType != 'RQST_TEL' && item.itemType != 'RQST_ML' && item.itemType != 'SCHOOL'}">
                <c:set var="dynCnt" value="${dynCnt + 1}"/>
                <c:set var="savedAnswer" value=""/>
                <c:forEach var="ans" items="${artcl.answerList}">
                    <c:if test="${ans.formItemSeq == item.formItemSeq}">
                        <c:set var="savedAnswer" value="${ans.answerVal}"/>
                    </c:if>
                </c:forEach>
                <tr>
                    <th>
                        <c:if test="${item.requiredYn == 'Y'}"><span style="color:#c00;">*</span> </c:if>
                        <c:out value="${item.itemNm}"/>
                    </th>
                    <td>
                        <input type="hidden" class="answer-dynidx" value="${dynCnt}">
                        <c:choose>
                            <c:when test="${item.itemType == 'Radio'}">
                                <c:forEach var="opt" items="${fn:split(item.itemOptions, ',')}">
                                    <c:set var="optTrim" value="${fn:trim(opt)}"/>
                                    <label style="margin-right:10px;">
                                        <input type="radio" name="formRadio_${item.formItemSeq}"
                                            class="answer-radio" data-dynidx="${dynCnt}"
                                            value="${optTrim}"
                                            ${savedAnswer == optTrim ? 'checked' : ''}>
                                        <c:out value="${optTrim}"/>
                                    </label>
                                </c:forEach>
                            </c:when>
                            <c:when test="${item.itemType == 'Checkbox'}">
                                <c:forEach var="opt" items="${fn:split(item.itemOptions, ',')}">
                                    <c:set var="optTrim" value="${fn:trim(opt)}"/>
                                    <label style="margin-right:10px;">
                                        <input type="checkbox" class="answer-chk" data-dynidx="${dynCnt}"
                                            value="${optTrim}"
                                            ${fn:contains(savedAnswer, optTrim) ? 'checked' : ''}>
                                        <c:out value="${optTrim}"/>
                                    </label>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <input type="text" class="answer-text" data-dynidx="${dynCnt}" style="width:300px;"
                                    value="<c:out value='${savedAnswer}'/>">
                            </c:otherwise>
                        </c:choose>
                    </td>
                </tr>
                </c:if>
                </c:forEach>
            </c:if>
        </table>

        <div class="cldr-btn-area">
            <button class="cldr-btn cldr-btn-primary" onclick="submitUpdt();">수정</button>
            <button class="cldr-btn cldr-btn-gray" onclick="history.back();">이전으로</button>
        </div>
    </div>

    <%-- 학교 검색 레이어 --%>
    <div id="schoolSearchLayer" class="cldr-school-layer">
        <div class="cldr-school-inner">
            <div class="cldr-school-header">
                <strong>고교 검색</strong>
                <button onclick="jf_closeSchoolSearch();">&#10005;</button>
            </div>
            <div class="cldr-school-search">
                <input type="text" id="srchSchNm" placeholder="고등학교명을 입력하세요."
                    onkeypress="if(event.keyCode==13) jf_searchSchool();">
                <button onclick="jf_searchSchool();">검색하기</button>
            </div>
            <div class="cldr-school-results">
                <ul id="schoolResultList"></ul>
            </div>
        </div>
    </div>

    </c:otherwise>
</c:choose>

</div><%-- ._fnctWrap --%>

<script>
var SITE_ID      = "${vo.siteId}";
var FNCT_NO      = "${vo.fnctNo}";
var COMPANIONUSE = "${setup.companionUseYn}";

/* 전화번호 / 이메일 분리 렌더링 */
(function() {
    var rawPhone = "<c:out value='${artcl.rqstTel}'/>";
    var parts = rawPhone.split('-');
    var p1 = parts[0] || '010', p2 = parts[1] || '', p3 = parts[2] || '';
    $('#phoneWrap').html(
        '<input type="text" id="phone1" style="width:50px;" maxlength="4" value="' + p1 + '" onkeyup="this.value=this.value.replace(/[^0-9]/g,\'\')">' +
        '<span style="margin:0 5px;">-</span>' +
        '<input type="text" id="phone2" style="width:70px;" maxlength="4" value="' + p2 + '" onkeyup="this.value=this.value.replace(/[^0-9]/g,\'\')">' +
        '<span style="margin:0 5px;">-</span>' +
        '<input type="text" id="phone3" style="width:70px;" maxlength="4" value="' + p3 + '" onkeyup="this.value=this.value.replace(/[^0-9]/g,\'\')">'
    );

    var rawEmail = "<c:out value='${artcl.rqstMl}'/>";
    var eParts = rawEmail.split('@');
    var e1 = eParts[0] || '', e2 = eParts[1] || '';
    $('#emailWrap').html(
        '<input type="text" id="rqstMl1" style="width:140px;" value="' + e1 + '">' +
        '<span> @ </span>' +
        '<input type="text" id="rqstMl2" style="width:140px;" value="' + e2 + '">' +
        '<select id="emailDomain" onchange="jf_emailDomain(this.value);" style="margin-left:5px;">' +
        '<option value="">직접입력</option>' +
        '<option value="naver.com">네이버</option>' +
        '<option value="google.com">Google</option>' +
        '<option value="daum.net">다음</option>' +
        '<option value="nate.com">네이트</option>' +
        '<option value="konkuk.ac.kr">konkuk.ac.kr</option>' +
        '</select>'
    );
})();

function jf_emailDomain(val) {
    if (val) { $('#rqstMl2').val(val).prop('readonly', true); }
    else     { $('#rqstMl2').val('').prop('readonly', false).focus(); }
}

/* 학교 검색 */
function jf_openSchoolSearch() {
    $('#srchSchNm').val(''); $('#schoolResultList').html('');
    $('#schoolSearchLayer').show();
    setTimeout(function(){ $('#srchSchNm').focus(); }, 100);
}
function jf_closeSchoolSearch() { $('#schoolSearchLayer').hide(); }
function jf_searchSchool() {
    var nm = $('#srchSchNm').val().trim();
    if (!nm || nm.length < 2) { alert('학교명을 2자 이상 입력하세요.'); return; }
    $.ajax({
        url : kurl('/enterCldrApply/' + SITE_ID + '/schoolSearch'),
        data: { schNm: nm },
        success: function(list) {
            if (!list || list.length === 0) {
                $('#schoolResultList').html('<li style="padding:10px; color:#999;">검색 결과가 없습니다.</li>');
                return;
            }
            var html = '';
            list.forEach(function(s) {
                html += '<li onclick="jf_selectSchool(\'' + escHtml(s.schNm) + '\',\'' + escHtml(s.schCd||'') + '\',\'' + escHtml(s.schLc||'') + '\',\'' + escHtml(s.schTp||'') + '\')">';
                html += '<div class="sch-nm">' + escHtml(s.schNm) + '</div>';
                html += '<div class="sch-info">' + escHtml(s.schLc||'') + ' / ' + escHtml(s.schTp||'') + '</div>';
                html += '</li>';
            });
            $('#schoolResultList').html(html);
        }
    });
}
function jf_selectSchool(nm, code, region, type) {
    $('#schNm').val(nm); $('#schCd').val(code);
    $('#schLc').val(region); $('#schTp').val(type);
    jf_closeSchoolSearch();
}
function escHtml(str) {
    return str ? str.replace(/[&<>"']/g, function(m) {
        return {'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[m];
    }) : '';
}

/* 수정 제출 */
function submitUpdt() {
    var rqstNm  = $('#rqstNm').val().trim();
    var slotSeq = $('input[name="timeSlotSelect"]:checked').val();
    if (!rqstNm)  { alert('이름을 입력하세요.'); return; }
    if ($('#phone2').val().trim() === '' || $('#phone3').val().trim() === '') {
        alert('휴대전화를 입력하세요.'); return;
    }
    if (!slotSeq) { alert('시간을 선택하세요.'); return; }

    var rqstMl2 = $('#rqstMl2').val().trim();
    var data = {
        'artclSeq'    : $('#artclSeqHidden').val(),
        'setupSeq'    : $('#setupSeqHidden').val(),
        'artclDt'     : $('#artclDtHidden').val(),
        'timeSlotSeq' : slotSeq,
        'rqstNm'      : rqstNm,
        'rqstTel'     : $('#phone1').val() + '-' + $('#phone2').val() + '-' + $('#phone3').val(),
        'rqstMl'      : $('#rqstMl1').val().trim() + (rqstMl2 ? '@' + rqstMl2 : ''),
        'schCd'       : $('#schCd').val(),
        'schNm'       : $('#schNm').val().trim(),
        'schLc'       : $('#schLc').val(),
        'schTp'       : $('#schTp').val()
    };
    if (COMPANIONUSE === 'Y') {
        data['companionCnt'] = $('#companionCnt').val() || 0;
    }

    var ti = 0;
    $('.target-comp-input').each(function() {
        data['targetList[' + ti + '].targetItemSeq'] = $(this).data('seq');
        data['targetList[' + ti + '].compCnt']       = $(this).val() || 0;
        ti++;
    });

    /* 동적 폼 항목 → additm1~N */
    $('.answer-dynidx').each(function() {
        var slot = parseInt($(this).val());
        var row  = $(this).closest('tr');
        var val  = '';
        var textEl  = row.find('.answer-text');
        var radioEl = row.find('input.answer-radio:checked');
        var chkEls  = row.find('input.answer-chk:checked');
        if (textEl.length)       val = textEl.val().trim();
        else if (radioEl.length) val = radioEl.val();
        else if (chkEls.length)  val = $.map(chkEls.toArray(), function(el){ return $(el).val(); }).join(',');
        data['additm' + slot] = val;
    });

    $.ajax({
        url : kurl('/enterCldrApply/' + SITE_ID + '/artclUpdtProc'),
        type: 'POST',
        data: data,
        success: function(r) {
            if (r.message) { alert(r.message); return; }
            alert('수정되었습니다.', function() { history.back(); });
        }
    });
}

$(function() {
    $(document).on('keydown', function(e) {
        if (e.keyCode === 27) jf_closeSchoolSearch();
    });
});
</script>

<c:if test='${layout==null || layout==""}'>
<%@ include file="/WEB-INF/jsp/fnct/enterCldrApply/enterCldrApply_basic_skin/artclUpdt.js.jsp" %>
<%@ include file="/WEB-INF/jsp/include/user/footer.jsp" %>
</c:if>
