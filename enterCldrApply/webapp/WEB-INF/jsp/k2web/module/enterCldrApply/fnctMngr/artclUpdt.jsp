<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/jsp_header.jsp" %>

<c:set var="mode" value="_mngr"/>
<c:set var="pageTitle" value="${multiNm}"/>
<c:set var="pageLoca01" value="${fnctInfo.fnctInfoLang.fnctNm}"/>
<c:set var="pageLoca02">신청 관리</c:set>
<c:set var="pageLoca03">신청 수정</c:set>

<%@ include file="/WEB-INF/jsp/include/mngr/manager/manager_header.jsp"%>

<%@ include file="/WEB-INF/jsp/include/mngr/manager/manager_body.jsp"%>
<%@ include file="/WEB-INF/jsp/include/mngr/fnctInfo.jsp"%>

<c:choose>
<c:when test="${artcl != null}">

<div class="_write _labelW01">

    <div class="_form">
        <label class="_label">신청 일자</label>
        <div class="_insert">
            <input type="date" id="artclDt"
                value="<fmt:formatDate value='${artcl.artclDt}' pattern='yyyy-MM-dd'/>"
                style="width:160px;">
        </div>
    </div>

    <c:if test="${timeSlotList != null && fn:length(timeSlotList) > 0}">
    <div class="_form">
        <label class="_label">시간 선택</label>
        <div class="_insert">
            <c:forEach var="slot" items="${timeSlotList}">
            <label style="margin-right:14px;">
                <input type="radio" name="timeSlotSelect" value="${slot.slotSeq}"
                    ${artcl.timeSlotSeq == slot.slotSeq ? 'checked' : ''}>
                <c:out value="${slot.applyTime}"/>
            </label>
            </c:forEach>
        </div>
    </div>
    </c:if>

    <div class="_form">
        <label class="_label"><span style="color:#c00;">*</span> 신청자명</label>
        <div class="_insert">
            <input type="text" id="rqstNm" style="width:220px;"
                value="<c:out value='${artcl.rqstNm}'/>">
        </div>
    </div>

    <div class="_form">
        <label class="_label"><span style="color:#c00;">*</span> 휴대전화</label>
        <div class="_insert" id="phoneWrap"><%-- JS 렌더링 --%></div>
    </div>

    <div class="_form">
        <label class="_label">이메일</label>
        <div class="_insert" id="emailWrap"><%-- JS 렌더링 --%></div>
    </div>

    <div class="_form">
        <label class="_label">고교명</label>
        <div class="_insert">
            <input type="text" id="schNm" style="width:220px;" readonly
                value="<c:out value='${artcl.schNm}'/>">
            <input type="hidden" id="schCd" value="<c:out value='${artcl.schCd}'/>">
            <input type="hidden" id="schLc" value="<c:out value='${artcl.schLc}'/>">
            <input type="hidden" id="schTp" value="<c:out value='${artcl.schTp}'/>">
            <span class="_button _small _active">
                <a href="#none" onclick="jf_openSchoolSearch();">검색</a>
            </span>
            <span class="_button _small">
                <a href="#none" onclick="jf_clearSchool();">초기화</a>
            </span>
        </div>
    </div>

    <c:if test="${setup.companionUseYn == 'Y'}">
    <div class="_form">
        <label class="_label">동반 인원</label>
        <div class="_insert">
            <input type="number" id="companionCnt" style="width:80px;" min="0" value="${artcl.companionCnt}"> 명
        </div>
    </div>
    </c:if>

    <c:if test="${setup.targetCompUseYn == 'Y' && targetItemList != null && fn:length(targetItemList) > 0}">
    <div class="_form">
        <label class="_label">대상별 인원</label>
        <div class="_insert">
            <table class="_table _list _inner" style="width:60%;">
            	<colgroup>
                    <col style="width:auto;"><col style="width:350px;">
                </colgroup>
                <thead><tr><th>대상</th><th>인원</th></tr></thead>
                <tbody>
                <c:forEach var="ti" items="${targetItemList}">
                <c:set var="savedCompCnt" value="0"/>
                <c:forEach var="t" items="${artcl.targetList}">
                    <c:if test="${t.targetItemSeq == ti.targetItemSeq}">
                        <c:set var="savedCompCnt" value="${t.compCnt}"/>
                    </c:if>
                </c:forEach>
                <tr>
                    <td><c:out value="${ti.targetNm}"/></td>
                    <td>
                        <input type="number" class="target-comp-input" data-seq="${ti.targetItemSeq}" style="width:70px;" min="0" value="${savedCompCnt}">명
                    </td>
                </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
    </c:if>

    <c:if test="${artcl.dynamicFormItems != null && fn:length(artcl.dynamicFormItems) > 0}">
    <c:forEach var="item" items="${artcl.dynamicFormItems}" varStatus="s">
    <div class="_form">
        <label class="_label">
            <c:if test="${item.requiredYn == 'Y'}"><span style="color:#c00;">*</span> </c:if>
            <c:out value="${item.itemNm}"/>
        </label>
        <div class="_insert">
            <input type="hidden" class="answer-dynidx" value="${s.index + 1}">
            <c:choose>
                <c:when test="${item.itemType == 'Radio'}">
                    <c:forEach var="opt" items="${fn:split(item.itemOptions, ',')}">
                    <c:set var="optTrim" value="${fn:trim(opt)}"/>
                    <label style="margin-right:12px;">
                        <input type="radio" name="formRadio_${item.formItemSeq}"
                            class="answer-radio" data-dynidx="${s.index + 1}"
                            value="${optTrim}"
                            ${item.answerVal == optTrim ? 'checked' : ''}>
                        <c:out value="${optTrim}"/>
                    </label>
                    </c:forEach>
                </c:when>
                <c:when test="${item.itemType == 'Checkbox'}">
                    <c:forEach var="opt" items="${fn:split(item.itemOptions, ',')}">
                    <c:set var="optTrim" value="${fn:trim(opt)}"/>
                    <label style="margin-right:12px;">
                        <input type="checkbox" class="answer-chk" data-dynidx="${s.index + 1}"
                            value="${optTrim}"
                            ${fn:contains(item.answerVal, optTrim) ? 'checked' : ''}>
                        <c:out value="${optTrim}"/>
                    </label>
                    </c:forEach>
                </c:when>
                <c:otherwise>
                    <input type="text" class="answer-text" data-dynidx="${s.index + 1}"
                        style="width:300px;"
                        value="<c:out value='${item.answerVal}'/>">
                </c:otherwise>
            </c:choose>
        </div>
    </div>
    </c:forEach>
    </c:if>

</div><!-- ._edit -->

<%-- 학교 검색 레이어 --%>
<div id="schoolSearchLayer" style="display:none;position:fixed;top:0;left:0;width:100%;height:100%;background:rgba(0,0,0,.4);z-index:9999;">
    <div style="position:absolute;top:50%;left:50%;transform:translate(-50%,-50%);background:#fff;border-radius:6px;width:480px;max-height:70vh;overflow:hidden;display:flex;flex-direction:column;">
        <div style="display:flex;justify-content:space-between;align-items:center;padding:14px 18px;border-bottom:1px solid #ddd;">
            <strong>고교 검색</strong>
            <a href="#none" onclick="jf_closeSchoolSearch();" style="font-size:18px;color:#555;text-decoration:none;">&#10005;</a>
        </div>
        <div style="padding:12px 18px;border-bottom:1px solid #eee;display:flex;gap:8px;">
            <input type="text" id="srchSchNm" style="flex:1;padding:6px;" placeholder="고등학교명을 입력하세요."
                onkeypress="if(event.keyCode==13) jf_searchSchool();">
            <span class="_button _small _active">
                <a href="#none" onclick="jf_searchSchool();">검색</a>
            </span>
        </div>
        <div style="overflow-y:auto;max-height:340px;padding:8px 0;">
            <ul id="schoolResultList" style="list-style:none;margin:0;padding:0;"></ul>
        </div>
    </div>
</div>

<c:url var="viewUrl" value="/enterCldrApply/fnctMngr/${vo.siteId}/${setupSeq}/${artcl.artclSeq}/artclView">
    <c:param name="page"            value="${vo.page}"/>
    <c:param name="findArtclStatus" value="${vo.findArtclStatus}"/>
    <c:param name="findType"        value="${vo.findType}"/>
    <c:param name="findWord"        value="${vo.findWord}"/>
</c:url>
<div class="_areaButton">
    <span class="_button _large _active">
        <a href="#none" onclick="submitUpdtMngr();">저장</a>
    </span>
    <span class="_button _large">
        <a href="${viewUrl}">취소</a>
    </span>
</div>

</c:when>
<c:otherwise>
    <div class="_noData">신청 정보를 찾을 수 없습니다.</div>
    <div class="_areaButton">
        <span class="_button _large">
            <a href="<k:url value='/enterCldrApply/fnctMngr/${vo.siteId}/${setupSeq }/artclList'/>">목록</a>
        </span>
    </div>
</c:otherwise>
</c:choose>

<script>
var SITE_ID   = "${vo.siteId}";
var SETUP_SEQ = "${setupSeq}";
var ARTCL_SEQ = "${artcl.artclSeq}";
var VIEW_URL  = '<c:out value="${viewUrl}"/>';
var COMPANION_USE = "${setup.companionUseYn}";

/* 전화번호 분리 */
(function() {
    var raw = "<c:out value='${artcl.rqstTel}'/>";
    var p = raw.split('-');
    $('#phoneWrap').html(
        '<input type="text" id="phone1" style="width:55px;" maxlength="4" value="' + (p[0]||'010') + '" onkeyup="this.value=this.value.replace(/[^0-9]/g,\'\')">' +
        ' - ' +
        '<input type="text" id="phone2" style="width:70px;" maxlength="4" value="' + (p[1]||'') + '" onkeyup="this.value=this.value.replace(/[^0-9]/g,\'\')">' +
        ' - ' +
        '<input type="text" id="phone3" style="width:70px;" maxlength="4" value="' + (p[2]||'') + '" onkeyup="this.value=this.value.replace(/[^0-9]/g,\'\')">'
    );
})();

/* 이메일 분리 */
(function() {
    var raw = "<c:out value='${artcl.rqstMl}'/>";
    var ep  = raw.split('@');
    $('#emailWrap').html(
        '<input type="text" id="rqstMl1" style="width:150px;" value="' + (ep[0]||'') + '">' +
        ' @ ' +
        '<input type="text" id="rqstMl2" style="width:150px;" value="' + (ep[1]||'') + '">' +
        '<select id="emailDomain" onchange="jf_emailDomain(this.value);" style="margin-left:6px;">' +
        '<option value="">직접입력</option>' +
        '<option value="naver.com">네이버</option>' +
        '<option value="google.com">Google</option>' +
        '<option value="daum.net">다음</option>' +
        '<option value="nate.com">네이트</option>' +
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
    setTimeout(function(){ $('#srchSchNm').focus(); }, 80);
}
function jf_closeSchoolSearch() { $('#schoolSearchLayer').hide(); }
function jf_clearSchool() {
    $('#schNm').val(''); $('#schCd').val(''); $('#schLc').val(''); $('#schTp').val('');
}
function jf_searchSchool() {
    var nm = $('#srchSchNm').val().trim();
    if (!nm || nm.length < 2) { alert('학교명을 2자 이상 입력하세요.'); return; }
    $.ajax({
    	url: kurl( '/enterHgs/' + SITE_ID + '/getSchList' ),
        data: { 'srchSch' : nm },
        success:function(list) {
			var html = "";
			
			if( $( list ).size() > 0 ) {
				html += "<ul>";
				$( list ).each(function() {
					console.log(this);
					html += '<li onclick="jf_selectSchool(\'' + esc(this.schNm) + '\',\'' + esc(this.schCd||'') + '\',\'' + esc(this.rgn||'') + '\',\'' + esc(this.ctgr||'') + '\')"' +
                    ' style="padding:10px 16px;cursor:pointer;border-bottom:1px solid #f0f0f0;" onmouseover="this.style.background=\'#f5f5f5\'" onmouseout="this.style.background=\'\'">' +
                    '<div style="font-weight:bold;">' + esc(this.schNm) + '</div>' +
                    '<div style="font-size:12px;color:#888;">' + esc(this.schLc||'') + ' / ' + esc(this.rgn||'') + ' / ' + esc(this.ctgr||'') + '</div>' +
                    '</li>';
				});
				html += "</ul>";
			} else {
				$('#schoolResultList').html('<li style="padding:10px;color:#999;">검색 결과가 없습니다.</li>');
                return;
			}
			
			$('#schoolResultList').html(html);
		}
    });
}
function jf_selectSchool(nm, cd, lc, tp) {
    $('#schNm').val(nm); $('#schCd').val(cd); $('#schLc').val(lc); $('#schTp').val(tp);
    jf_closeSchoolSearch();
}
function esc(str) {
    return str ? str.replace(/[&<>"']/g, function(m) {
        return {'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[m];
    }) : '';
}

/* 수정 제출 */
function submitUpdtMngr() {
    var rqstNm = $('#rqstNm').val().trim();
    if (!rqstNm) { alert('신청자명을 입력하세요.'); return; }
    if (!$('#phone2').val().trim() || !$('#phone3').val().trim()) {
        alert('휴대전화를 입력하세요.'); return;
    }
    var slotSeq = $('input[name="timeSlotSelect"]:checked').val();

    var rqstMl2 = $('#rqstMl2').length ? $('#rqstMl2').val().trim() : '';
    var data = {
        'artclDt'     : $('#artclDt').val(),
        'rqstNm'      : rqstNm,
        'rqstTel'     : $('#phone1').val() + '-' + $('#phone2').val() + '-' + $('#phone3').val(),
        'rqstMl'      : $('#rqstMl1').val().trim() + (rqstMl2 ? '@' + rqstMl2 : ''),
        'schCd'       : $('#schCd').val(),
        'schNm'       : $('#schNm').val().trim(),
        'schLc'       : $('#schLc').val(),
        'schTp'       : $('#schTp').val()
    };
    if (slotSeq) data['timeSlotSeq'] = slotSeq;
    if (COMPANION_USE === 'Y') {
        data['companionCnt'] = $('#companionCnt').val() || 0;
    }

    var ti = 0;
    $('.target-comp-input').each(function() {
        data['targetList[' + ti + '].targetItemSeq'] = $(this).data('seq');
        data['targetList[' + ti + '].compCnt']       = $(this).val() || 0;
        ti++;
    });

    $('.answer-dynidx').each(function() {
        var slot = parseInt($(this).val());
        var wrap = $(this).closest('div._insert');
        var val  = '';
        var textEl  = wrap.find('input.answer-text');
        var radioEl = wrap.find('input.answer-radio:checked');
        var chkEls  = wrap.find('input.answer-chk:checked');
        if      (textEl.length)  val = textEl.val().trim();
        else if (radioEl.length) val = radioEl.val();
        else if (chkEls.length)  val = $.map(chkEls.toArray(), function(el){ return $(el).val(); }).join(',');
        data['additm' + slot] = val;
    });

    $.ajax({
        url : kurl('/enterCldrApply/fnctMngr/' + SITE_ID + '/' + SETUP_SEQ + '/' + ARTCL_SEQ + '/artclUpdtProc'),
        type: 'POST',
        data: data,
        success: function(r) {
            if (r.message) { alert(r.message); return; }
            if (r.result) {
                alert('수정되었습니다.', function() {
                    location.href = VIEW_URL;
                });
            } else {
                alert('수정에 실패하였습니다.');
            }
        }
    });
}

$(function() {
    $(document).on('keydown', function(e) {
        if (e.keyCode === 27) jf_closeSchoolSearch();
    });
    $('#schoolSearchLayer').on('click', function(e) {
        if ($(e.target).is('#schoolSearchLayer')) jf_closeSchoolSearch();
    });
});
</script>

<%@ include file="/WEB-INF/jsp/include/mngr/manager/manager_footer.jsp"%>
