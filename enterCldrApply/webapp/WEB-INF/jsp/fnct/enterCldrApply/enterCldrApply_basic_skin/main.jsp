<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/jsp_header.jsp" %>

<c:if test='${layout==null || layout==""}'>
<%@ include file="/WEB-INF/jsp/include/user/header.jsp" %>
<%@ include file="/WEB-INF/jsp/fnct/enterCldrApply/enterCldrApply_basic_skin/main.head.jsp" %>
<%@ include file="/WEB-INF/jsp/include/user/body.jsp" %>
</c:if>

<div class="_fnctWrap">
<c:if test='${isManager && fn:length(editMode)>0}'>
    <a href="<k:url value='/enterCldrApply/fnctMngr/${vo.siteId}/${setup.setupSeq}/setupView'/>" class="_fnctAdmin _blockUI" target="_blank">관리</a>
</c:if>

<%-- ===== STEP 1: 안내 ===== --%>
<div id="step1" class="cldr-step">
    <div class="cldr-step-title">STEP1. <c:out value="${setup.setupNm}"/> 안내</div>

    <table class="cldr-info-table">
        <c:if test="${not empty setup.intro}">
        <tr>
            <th>소개</th>
            <td><c:out value="${setup.intro}"/></td>
        </tr>
        </c:if>
        <c:if test="${setup.recvStartDt != null || setup.recvEndDt != null}">
        <tr>
            <th>접수 기간</th>
            <td>
                <fmt:formatDate value="${setup.recvStartDt}" pattern="yyyy-MM-dd"/>
                ~
                <fmt:formatDate value="${setup.recvEndDt}" pattern="yyyy-MM-dd"/>
            </td>
        </tr>
        </c:if>
        <c:if test="${not empty setup.applyTarget}">
        <tr>
            <th>참여대상</th>
            <td><c:out value="${setup.applyTarget}"/></td>
        </tr>
        </c:if>
        <c:if test="${not empty setup.location}">
        <tr>
            <th>장소</th>
            <td><c:out value="${setup.location}"/></td>
        </tr>
        </c:if>
        <c:if test="${atchmnflList != null && fn:length(atchmnflList) > 0}">
        <tr>
            <th>첨부파일</th>
            <td>
                <c:forEach var="file" items="${atchmnflList}">
                    <a href="<k:url value='/enterCldrApply/${vo.siteId}/${file.atchmnflSeq}/fileDown'/>" style="display:block;"><c:out value="${file.orginlNm}"/></a>
                </c:forEach>
            </td>
        </tr>
        </c:if>
    </table>

    <c:if test="${not empty setup.content}">
    	<div class="cldr-content-area">${fn:replace(setup.content, cr, "<br>") }</div>
    </c:if>

    <div class="cldr-btn-area">
        <button class="cldr-btn cldr-btn-primary" onclick="showStep(2);">신청하기</button>
        <button class="cldr-btn cldr-btn-secondary" onclick="javascript:jf_artclSearch('${vo.siteId}','${vo.fnctNo}');">신청내역 확인</button>
    </div>
</div>

<%-- ===== STEP 2: 날짜 선택 ===== --%>
<div id="step2" class="cldr-step" style="display:none;">
    <div class="cldr-step-title">STEP2. 캘린더형 신청 일자 선택</div>
    <p style="color:#555; margin-bottom:15px;">
        원하는 일자를 클릭하여 신청해주세요.<br>
        일자에 신청불가 표시가 되어있는 경우는 학교 일정상 신청이 불가능한 시간입니다.
    </p>

    <div class="cldr-step2-wrap">
        <%-- 캘린더 --%>
        <div class="cldr-cal-wrap">
            <div class="cldr-cal-nav">
                <button onclick="prevMonth();">&#60;</button>
                <span class="cal-ym" id="calYearMonth"></span>
                <button onclick="nextMonth();">&#62;</button>
                <span class="cldr-legend">
                    <span class="dot dot-red"></span>신청마감
                    <span class="dot dot-green"></span>신청가능
                </span>
            </div>
            <table class="cldr-calendar">
                <thead>
                    <tr><th>일</th><th>월</th><th>화</th><th>수</th><th>목</th><th>금</th><th>토</th></tr>
                </thead>
                <tbody id="calendarBody"></tbody>
            </table>
        </div>

        <%-- 회차 정보 패널 --%>
        <div class="cldr-slot-panel">
            <div class="cldr-slot-title">회차정보</div>
            <div id="slotList" style="min-height:150px; color:#999; font-size:13px; padding:10px 0;">
                날짜를 선택하세요.
            </div>
            <div class="cldr-btn-area" style="margin-top:20px;">
                <button class="cldr-btn cldr-btn-primary" onclick="goToStep3();">신청하기</button>
            </div>
        </div>
    </div>
</div>

<%-- ===== STEP 3: 신청서 작성 ===== --%>
<div id="step3" class="cldr-step" style="display:none;">
    <div class="cldr-step-title">STEP3. 캘린더형 신청 신청서 작성</div>

    <table class="cldr-form-table">
        <tr>
            <th>신청 일자</th>
            <td><span id="displayArtclDt" style="font-weight:bold;"></span></td>
        </tr>
        <tr>
            <th>시간 선택</th>
            <td id="slotCheckList">
                <%-- JS로 렌더링 --%>
            </td>
        </tr>
        <tr>
            <th><span style="color:#c00;">*</span> 이름</th>
            <td><input type="text" id="rqstNm" style="width:200px;" placeholder="이름을 입력하세요."></td>
        </tr>
        <tr>
            <th><span style="color:#c00;">*</span> 휴대전화</th>
            <td>
                <input type="text" id="phone1" style="width:50px;" value="010" maxlength="4" onkeyup="this.value=this.value.replace(/[^0-9]/g,'');">
                <span class="phone-sep">-</span>
                <input type="text" id="phone2" style="width:70px;" maxlength="4" onkeyup="this.value=this.value.replace(/[^0-9]/g,'');">
                <span class="phone-sep">-</span>
                <input type="text" id="phone3" style="width:70px;" maxlength="4" onkeyup="this.value=this.value.replace(/[^0-9]/g,'');">
            </td>
        </tr>
        <tr>
            <th>이메일</th>
            <td>
                <input type="text" id="rqstMl1" style="width:140px;">
                <span>@</span>
                <input type="text" id="rqstMl2" style="width:140px;">
                <select id="emailDomain" onchange="jf_emailDomain(this.value);" style="margin-left:5px;">
                    <option value="">직접입력</option>
                    <option value="naver.com">네이버</option>
                    <option value="google.com">Google</option>
                    <option value="daum.net">다음</option>
                    <option value="nate.com">네이트</option>
                    <option value="konkuk.ac.kr">konkuk.ac.kr</option>
                </select>
            </td>
        </tr>
        <tr>
            <th>고교검색</th>
            <td>
                <input type="text" id="schNm" style="width:220px;" readonly placeholder="고교를 검색하세요.">
                <input type="hidden" id="schCd">
                <input type="hidden" id="schLc">
                <input type="hidden" id="schTp">
                <button type="button" class="btn-func color3 btn-school">검색</button>
            </td>
        </tr>
        <c:if test="${formItemList != null && fn:length(formItemList) > 0}">
            <c:set var="dynCnt" value="0"/>
            <c:forEach var="item" items="${formItemList}">
            <c:if test="${item.itemType != 'RQST_NM' && item.itemType != 'RQST_TEL' && item.itemType != 'RQST_ML' && item.itemType != 'SCHOOL'}">
            <c:set var="dynCnt" value="${dynCnt + 1}"/>
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
                                <label style="margin-right:10px;">
                                    <input type="radio" name="formRadio_${item.formItemSeq}" class="answer-radio" data-dynidx="${dynCnt}" value="${fn:trim(opt)}">
                                    <c:out value="${fn:trim(opt)}"/>
                                </label>
                            </c:forEach>
                        </c:when>
                        <c:when test="${item.itemType == 'Checkbox'}">
                            <c:forEach var="opt" items="${fn:split(item.itemOptions, ',')}">
                                <label style="margin-right:10px;">
                                    <input type="checkbox" class="answer-chk" data-dynidx="${dynCnt}" value="${fn:trim(opt)}">
                                    <c:out value="${fn:trim(opt)}"/>
                                </label>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <input type="text" class="answer-text" data-dynidx="${dynCnt}" style="width:300px;" placeholder="<c:out value='${item.itemNm}'/>을 입력하세요.">
                        </c:otherwise>
                    </c:choose>
                </td>
            </tr>
            </c:if>
            </c:forEach>
        </c:if>
        <c:if test="${setup.companionUseYn == 'Y'}">
        <tr>
            <th>동행 인원</th>
            <td><input type="number" id="companionCnt" style="width:80px;" min="0" value="0"> 명</td>
        </tr>
        </c:if>
        <c:if test="${setup.targetCompUseYn == 'Y' && targetItemList != null && fn:length(targetItemList) > 0}">
        <tr>
            <th>대상별 인원</th>
            <td>
                <table class="cldr-target-table">
                    <c:forEach var="item" items="${targetItemList}" varStatus="s">
                    <tr>
                        <td><c:out value="${item.targetNm}"/></td>
                        <td>
                            <input type="number" class="target-comp-input"
                                data-seq="${item.targetItemSeq}" data-idx="${s.index}"
                                style="width:70px;" min="0" value="0">
                        </td>
                        <td>명</td>
                    </tr>
                    </c:forEach>
                </table>
            </td>
        </tr>
        </c:if>
    </table>

    <%-- 개인정보 동의 --%>
    <c:if test="${not empty setup.privacyPurpose || not empty setup.privacyItems}">
    <div class="cldr-agree-box">
        <c:if test="${not empty setup.privacyPurpose}">
        <strong>개인정보 수집·이용 목적</strong><br>
        <c:out value="${setup.privacyPurpose}"/><br><br>
        </c:if>
        <c:if test="${not empty setup.privacyItems}">
        <strong>수집하는 개인정보 항목</strong><br>
        <c:out value="${setup.privacyItems}"/><br><br>
        </c:if>
        <c:if test="${not empty setup.privacyPeriod}">
        <strong>개인정보 보유 및 이용기간</strong><br>
        <c:out value="${setup.privacyPeriod}"/>
        </c:if>
    </div>
    <div class="cldr-agree-check">
        <label><input type="checkbox" id="agreeChk"> 개인정보 수집 및 이용에 동의합니다.</label>
    </div>
    </c:if>

    <div class="cldr-btn-area" style="margin-top:20px;">
        <button class="cldr-btn cldr-btn-primary" onclick="submitArtcl();">신청하기</button>
        <button class="cldr-btn cldr-btn-gray" onclick="showStep(2);">취소</button>
    </div>
</div>

<form id="frm" name="frm" method="post" action="">
	<input type="hidden" name="layout" id="layout" value='<c:out value="${layout}"/>'>
</form>

<%-- ===== 학교 검색 레이어 ===== --%>
<div class="func-layer">
	<div class="box">
		<div class="row">
			<div class="title">
				<strong>고교 검색</strong>
				<button type="button" class="btn-layer-close">레이어 닫기</button>
			</div>
			<div class="search">
				<div class="input"><input type="text" id="srchSch" name="srchSch" placeholder="고등학교를 검색하세요" onkeypress="if(event.keyCode==13) jf_searchSchool();"></div>
				<div class="button"><button type="button" onclick="jf_school('<c:out value="${vo.siteId}"/>');">검색하기</button></div>
			</div>
			<div class="list addSch"></div>
		</div>
	</div>
</div>

</div><%-- ._fnctWrap --%>

<script>
/* ===================================================
   서버 데이터
=================================================== */
var SITE_ID  = "${vo.siteId}";
var FNCT_NO  = "${vo.fnctNo}";
var SETUP_SEQ = "${setup.setupSeq}";
var COMPANIONUSE = "${setup.companionUseYn}";
var TARGETUSE    = "${setup.targetCompUseYn}";
var RECV_START = "<fmt:formatDate value='${setup.recvStartDt}' pattern='yyyy-MM-dd'/>";
var RECV_END   = "<fmt:formatDate value='${setup.recvEndDt}'   pattern='yyyy-MM-dd'/>";

var AVAIL_DAYS = {
    0: ${setup.sunYn == 'Y' ? 'true' : 'false'},
    1: ${setup.monYn == 'Y' ? 'true' : 'false'},
    2: ${setup.tueYn == 'Y' ? 'true' : 'false'},
    3: ${setup.wedYn == 'Y' ? 'true' : 'false'},
    4: ${setup.thuYn == 'Y' ? 'true' : 'false'},
    5: ${setup.friYn == 'Y' ? 'true' : 'false'},
    6: ${setup.satYn == 'Y' ? 'true' : 'false'}
};

var HOLIDAYS = [
    <c:forEach var="h" items="${holidayList}" varStatus="hs">
    "<fmt:formatDate value='${h.holidayDt}' pattern='yyyy-MM-dd'/>"<c:if test="${!hs.last}">,</c:if>
    </c:forEach>
];

var TIME_SLOTS = [
    <c:forEach var="slot" items="${timeSlotList}" varStatus="ss">
    {slotSeq:${slot.slotSeq}, applyTime:"<c:out value='${slot.applyTime}'/>", capacity:${slot.capacity}}<c:if test="${!ss.last}">,</c:if>
    </c:forEach>
];

var HAS_PRIVACY = ${not empty setup.privacyPurpose || not empty setup.privacyItems ? 'true' : 'false'};

/* ===================================================
   Step 이동
=================================================== */
function showStep(n) {
    $('#step1, #step2, #step3').hide();
    $('#step' + n).show();
    window.scrollTo(0, 0);
}

/* ===================================================
   신청내역 확인 화면 이동
=================================================== */
function jf_artclSearch(siteId, fnctNo){
	var url = kurl('/enterCldrApply/' + siteId + '/' + fnctNo + '/artclSearch');
	$("#frm").attr('action', url);
	$("#frm").submit();
}

/* ===================================================
   캘린더
=================================================== */
var calYear, calMonth, selectedDate = null, selectedSlotSeq = null;

function padZero(n) { return n < 10 ? '0' + n : '' + n; }

function isDayAvailable(dateStr) {
    if (RECV_START && dateStr < RECV_START) return false;
    if (RECV_END   && dateStr > RECV_END)   return false;
    var d = new Date(dateStr + 'T12:00:00');
    if (!AVAIL_DAYS[d.getDay()]) return false;
    if (HOLIDAYS.indexOf(dateStr) >= 0) return false;
    return true;
}

function renderCalendar(year, month) {
    var firstDay    = new Date(year, month - 1, 1).getDay();
    var daysInMonth = new Date(year, month, 0).getDate();
    var today = new Date();
    var todayStr = today.getFullYear() + '-' + padZero(today.getMonth()+1) + '-' + padZero(today.getDate());

    var html = '';
    var dayCount = 1;
    for (var row = 0; row < 6; row++) {
        html += '<tr>';
        for (var col = 0; col < 7; col++) {
            var cellNum = row * 7 + col;
            if (cellNum < firstDay || dayCount > daysInMonth) {
                html += '<td></td>';
            } else {
                var dateStr = year + '-' + padZero(month) + '-' + padZero(dayCount);
                var avail   = isDayAvailable(dateStr);
                var isHol   = HOLIDAYS.indexOf(dateStr) >= 0;
                var isSel   = (dateStr === selectedDate);
                var isTod   = (dateStr === todayStr);

                var cls = '';
                var onclick = '';
                var dot = '';
                if (isHol) {
                    cls = 'holiday';
                } else if (!avail) {
                    cls = 'unavail';
                } else {
                    cls = isSel ? 'selected' : 'avail';
                    onclick = ' onclick="clickDay(\'' + dateStr + '\')"';
                    dot = '<span class="dot ' + (isSel ? 'dot-green' : 'dot-green') + '"></span>';
                }
                var todayCls = isTod ? ' style="border:2px solid #1a5336;"' : '';
                html += '<td class="' + cls + '"' + onclick + todayCls + '>' + dayCount + dot + '</td>';
                dayCount++;
            }
        }
        html += '</tr>';
        if (dayCount > daysInMonth) break;
    }
    $('#calendarBody').html(html);
    $('#calYearMonth').text(year + '.' + padZero(month));
}

function prevMonth() {
    calMonth--;
    if (calMonth < 1) { calMonth = 12; calYear--; }
    renderCalendar(calYear, calMonth);
}
function nextMonth() {
    calMonth++;
    if (calMonth > 12) { calMonth = 1; calYear++; }
    renderCalendar(calYear, calMonth);
}

function clickDay(dateStr) {
    selectedDate = dateStr;
    selectedSlotSeq = null;
    renderCalendar(calYear, calMonth);
    loadSlotAvailability(dateStr);
}

function loadSlotAvailability(dateStr) {
    $('#slotList').html('<div style="padding:10px; color:#666;">로딩 중...</div>');
    $.ajax({
        url : kurl('/enterCldrApply/' + SITE_ID + '/' + FNCT_NO + '/slotAvailability'),
        data: { artclDt: dateStr },
        success: function(r) { renderSlotPanel(r.slotList); }
    });
}

function renderSlotPanel(slots) {
    if (!slots || slots.length === 0) {
        $('#slotList').html('<div style="padding:10px; color:#999;">등록된 회차가 없습니다.</div>');
        return;
    }
    var html = '<ul class="cldr-slot-list">';
    slots.forEach(function(s) {
        var isFull    = (s.capacity > 0 && s.remain <= 0);
        var isUnlimit = (s.capacity === 0);
        var statusNm  = isUnlimit ? '신청가능' : (isFull ? '마감' : '신청가능');
        var statusCls = isFull ? 'full' : (isUnlimit ? 'unlimited' : 'avail');
        var disabled  = isFull ? 'disabled' : '';
        html += '<li><label>';
        html += '<input type="radio" name="slotSelect" value="' + s.slotSeq + '" ' + disabled + '>';
        html += '<span class="slot-time">' + s.applyTime + '</span>';
        html += '<span class="slot-status ' + statusCls + '">' + statusNm + '</span>';
        html += '</label></li>';
    });
    html += '</ul>';
    $('#slotList').html(html);

    var firstAvail = slots.find(function(s) { return s.capacity === 0 || s.remain > 0; });
    if (firstAvail) {
        $('input[name="slotSelect"][value="' + firstAvail.slotSeq + '"]').prop('checked', true);
        selectedSlotSeq = firstAvail.slotSeq;
    }

    $('input[name="slotSelect"]').on('change', function() {
        selectedSlotSeq = $(this).val();
    });
}

function goToStep3() {
    if (!selectedDate) { alert('날짜를 선택하세요.'); return; }
    selectedSlotSeq = $('input[name="slotSelect"]:checked').val();
    if (!selectedSlotSeq) { alert('시간을 선택하세요.'); return; }

    var slot = TIME_SLOTS.find(function(s) { return s.slotSeq == selectedSlotSeq; });
    var slotTime = slot ? slot.applyTime : '';
    $('#displayArtclDt').text(selectedDate + ' (' + slotTime + ')');

    /* 시간 선택 라디오 렌더링 */
    var slotHtml = '<ul style="list-style:none; padding:0; margin:0;">';
    TIME_SLOTS.forEach(function(s) {
        var checked = (s.slotSeq == selectedSlotSeq) ? 'checked' : '';
        slotHtml += '<li><label>';
        slotHtml += '<input type="radio" name="step3SlotSelect" value="' + s.slotSeq + '" ' + checked + '>';
        slotHtml += ' ' + s.applyTime;
        slotHtml += '</label></li>';
    });
    slotHtml += '</ul>';
    $('#slotCheckList').html(slotHtml);

    showStep(3);
}

/* ===================================================
   이메일 도메인 자동완성
=================================================== */
function jf_emailDomain(val) {
    if (val) { $('#rqstMl2').val(val).prop('readonly', true); }
    else     { $('#rqstMl2').val('').prop('readonly', false).focus(); }
}

/* ===================================================
   학교 검색
=================================================== */

$(function () {
	// 고등학교 검색
	const searchSchool = function () {
		const btnSchool = $('.btn-school');
		const btnSchoolClose = $('.btn-layer-close')
		const layrerSchool = $('.func-layer');
		
		btnSchool.on('click', function () {
			if (!layrerSchool.hasClass('on')) {
				layrerSchool.addClass('on');
			} else {
				layrerSchool.removeClass('on');
			}
		});
		
		btnSchoolClose.on('click', function () {
			layrerSchool.removeClass('on');
		})
	};
	
	searchSchool();
});

function jf_school(siteId){
	var srchSch = $("#srchSch").val();
	if( srchSch == null || srchSch == "" ) {
		alert("고교명을 입력하세요.");
		$("#srchSch").focus();
	} else {
		$.ajax({ 
			type:"post",
			url:kurl( '/enterHgs/' + siteId + '/getSchList' ),
			async:false,
			cache:false,
			data : { 
				'srchSch' : srchSch 
			},
			success:function(r) {
				var html = "";
				
				if( $( r ).size() > 0 ) {
					html += "<ul>";
					$( r ).each(function() {
						html += "<li>";
						html += "<a href=\"#none\" onclick=\"javascript:jf_insert('"+this.schCd+"','"+this.schNm+"','"+this.rgn+"','"+this.ctgr+"');\">";
						html += "<span>"+this.schNm+"</span> <span>"+this.rgn+"</span> <span>"+this.ctgr+"</span>";
						html += "</a>";
						html += "</li>";
					});
					html += "</ul>";
				} else {
					html = "<p class=\"no-data\">검색어를 입력하세요</p>";
				}
				
				$(".addSch").empty().append( html );
			}
		});
	}
}

function jf_insert(code, schNm, rgn, ctgr){
	$("#schNm").val(schNm);
	$('#schCd').val(code);
	$("#schLc").val(rgn);
	$("#schTp").val(ctgr);
	$('.func-layer').removeClass('on');
}

function escHtml(str) {
    return str ? str.replace(/[&<>"']/g, function(m) {
        return {'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[m];
    }) : '';
}

/* ===================================================
   신청 제출
=================================================== */
function submitArtcl() {
    var artclDt     = selectedDate;
    var timeSlotSeq = $('input[name="step3SlotSelect"]:checked').val() || selectedSlotSeq;
    var rqstNm      = $('#rqstNm').val().trim();
    var rqstTel     = $('#phone1').val().trim() + '-' + $('#phone2').val().trim() + '-' + $('#phone3').val().trim();

    if (!rqstNm)    { alert('이름을 입력하세요.', function(){ $('#rqstNm').focus(); }); return; }
    if ($('#phone2').val().trim() === '' || $('#phone3').val().trim() === '') {
        alert('휴대전화를 입력하세요.'); return;
    }
    if (!timeSlotSeq){ alert('시간을 선택하세요.'); return; }

    if (HAS_PRIVACY && !$('#agreeChk').prop('checked')) {
        alert('개인정보 수집 및 이용에 동의해 주세요.'); return;
    }

    /* 데이터 구성 */
    var rqstMl2 = $('#rqstMl2').val().trim();
    var data = {
        'setupSeq'    : SETUP_SEQ,
        'artclDt'     : artclDt,
        'timeSlotSeq' : timeSlotSeq,
        'rqstNm'      : rqstNm,
        'rqstTel'     : rqstTel,
        'rqstMl'      : $('#rqstMl1').val().trim() + (rqstMl2 ? '@' + rqstMl2 : ''),
        'schCd'       : $('#schCd').val(),
        'schNm'       : $('#schNm').val().trim(),
        'schLc'       : $('#schLc').val(),
        'schTp'       : $('#schTp').val()
    };

    if (COMPANIONUSE === 'Y') {
        data['companionCnt'] = $('#companionCnt').val() || 0;
    }

    /* 대상별 인원 */
    var ti = 0;
    $('.target-comp-input').each(function() {
        data['targetList[' + ti + '].targetItemSeq'] = $(this).data('seq');
        data['targetList[' + ti + '].compCnt']       = $(this).val() || 0;
        ti++;
    });

    /* 동적 폼 항목 답변 → additm1~N */
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
        url : kurl('/enterCldrApply/' + SITE_ID + '/artclRegistProc'),
        type: 'POST',
        data: data,
        success: function(r) {
            if (r.message) { alert(r.message); return; }
            alert('신청이 완료되었습니다.', function(){
                showStep(1);
            });
        }
    });
}

/* ===================================================
   초기화
=================================================== */
$(function() {
    var now = new Date();
    calYear  = now.getFullYear();
    calMonth = now.getMonth() + 1;

    /* recv 기간이 미래면 해당 월로 초기 이동 */
    if (RECV_START) {
        var rs = new Date(RECV_START + 'T12:00:00');
        if (rs > now) { calYear = rs.getFullYear(); calMonth = rs.getMonth() + 1; }
    }
    renderCalendar(calYear, calMonth);

    /* ESC로 레이어 닫기 */
    $(document).on('keydown', function(e) {
        if (e.keyCode === 27) jf_closeSchoolSearch();
    });
});
</script>

<style>
/* 레이어 */
.func-layer {position: fixed; left: 0; top: 0; z-index: 100; width: 100%; height: 100%; display: none; justify-content: center; align-items: center; background: rgba(0,0,0,.5);}
.func-layer.on {display: flex;}
.func-layer .box {display: flex; background: #fff; width: 90%; max-width: 54rem;}
.func-layer .box .row {width: 100%; padding: 0 3rem 3rem 3rem;}
.func-layer .box .title {display: flex; justify-content: space-between; align-items: center; height: 10rem;}
.func-layer .box .title strong {font-weight: 600; font-size: 2.4rem; color: #333333;}
.func-layer .box .title .btn-layer-close {display: flex; width: 4.1rem; height: 4.1rem; background: url('../images/ic-layer-close.png') no-repeat center / 4.1rem auto; text-indent: -9999rem;}

.func-layer .box .search {display: flex; align-items: center;}
.func-layer .box .search .input {width: calc(100% - 7rem); padding-right: 1rem;}
.func-layer .box .search .input input {width: 100%; height: 7rem; border: 0.1rem solid #cccccc; padding: 0 1.5rem; font-size: 1.8rem; color: #333;}
.func-layer .box .search .input input::placeholder {color: #666;}
.func-layer .box .search .button {width: 7rem;}
.func-layer .box .search .button button {display: flex; width: 100%; height: 7rem; background: #00713b url('../images/ic-layer-search.png') no-repeat center / 2.9rem auto; text-indent: -9999rem; transition: background-color .35s;}
.func-layer .box .search .button button:hover {background-color: #005f31;}

.func-layer .box .list {overflow: auto; height: 22rem; background: #f7f7f7; margin-top: 2rem; padding: 2rem;}
.func-layer .box .list li {position: relative; display: flex; margin-bottom: 1.5rem; padding-left: 2rem;}
.func-layer .box .list li:last-child {margin-bottom: 0;}
.func-layer .box .list li::after {content: ''; position: absolute; left: 0; top: 1rem; width: .5rem; height: .5rem; background: #609015; border-radius: 100%;}
.func-layer .box .list li span {position: relative; display: block; padding-right: 1.5rem; margin-right: 1.5rem;}
.func-layer .box .list li span::after {content: ''; position: absolute; right: 0; top: .6rem; width: .1rem; height: 1.2rem; background: #cdcdcd;}
.func-layer .box .list li span:last-child::after {display: none;}
.func-layer .box .list .no-data {display: flex; justify-content: center; align-items: center; height: 100%; color: #aaaaaa;}

@media screen and (max-width: 700px) {
	.func-layer .box .title {height: 8rem;}
	.func-layer .box .title strong {font-size: 2rem;}
	.func-layer .box .search .input {width: calc(100% - 5rem);}
	.func-layer .box .search .input input {height: 5rem; font-size: 1.6rem;}
	.func-layer .box .search .button {width: 5rem;}
	.func-layer .box .search .button button {height: 5rem;}
	.func-layer .box .list li {margin-bottom: 1.2rem; font-size: 1.3rem;}
	.func-layer .box .list li::after {top: .8rem;}
	.func-layer .box .list li span {margin-right: 1rem; padding-right: 1rem;}
}
</style>

<c:if test='${layout==null || layout==""}'>
<%@ include file="/WEB-INF/jsp/fnct/enterCldrApply/enterCldrApply_basic_skin/main.js.jsp" %>
<%@ include file="/WEB-INF/jsp/include/user/footer.jsp" %>
</c:if>
