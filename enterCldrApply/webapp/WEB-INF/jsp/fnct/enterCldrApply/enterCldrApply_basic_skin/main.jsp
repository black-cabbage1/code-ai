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

<%-- ===== 스텝 탭 ===== --%>
<div class="func-tab" id="stepTab">
	<ul>
		<li id="stepTab1" class="on"><a href="#link"><span>STEP 01</span><strong>안내</strong></a></li>
		<li id="stepTab2"><a href="#link"><span>STEP 02</span><strong>일자 선택</strong></a></li>
		<li id="stepTab3"><a href="#link"><span>STEP 03</span><strong>신청서 작성</strong></a></li>
	</ul>
</div>

<%-- ===== STEP 1: 안내 ===== --%>
<div id="step1" class="mt30">

	<div class="func-board view">
		<div class="title">
			<div class="subject">
				<strong><span><c:out value="${setup.setupNm}"/> 안내</span></strong>
			</div>
			<c:if test="${not empty setup.intro}">
			<div class="info">
				<c:out value="${setup.intro}"/>
			</div>
			</c:if>
		</div>

		<div class="con">
			<div class="detail">
				<ul>
					<c:if test="${setup.evtStartDt != null || setup.evtEndDt != null}">
					<li class="ico1">
						<strong>행사기간</strong>
						<p>
							<fmt:formatDate value="${setup.evtStartDt}" pattern="yyyy-MM-dd"/>
							~
							<fmt:formatDate value="${setup.evtEndDt}" pattern="yyyy-MM-dd"/>
						</p>
					</li>
					</c:if>
					<c:if test="${setup.recvStartDt != null || setup.recvEndDt != null}">
					<li class="ico1">
						<strong>접수기간</strong>
						<p>
							<fmt:formatDate value="${setup.recvStartDt}" pattern="yyyy-MM-dd HH:00"/>
							~
							<fmt:formatDate value="${setup.recvEndDt}" pattern="yyyy-MM-dd HH:00"/>
						</p>
					</li>
					</c:if>
					<c:if test="${not empty setup.applyTarget}">
					<li class="ico2">
						<strong>참여대상</strong>
						<p><c:out value="${setup.applyTarget}"/></p>
					</li>
					</c:if>
					<c:if test="${not empty setup.location}">
					<li class="ico3">
						<strong>장소</strong>
						<p><c:out value="${setup.location}"/></p>
					</li>
					</c:if>
					<c:if test="${not empty setup.mngInfo}">
					<li class="ico2">
						<strong>담당자</strong>
						<p><c:out value="${setup.mngInfo}"/></p>
					</li>
					</c:if>
				</ul>
			</div>

			<c:if test="${not empty setup.content}">
				<div class="cldr-content-area mt20">${setup.content}</div>
			</c:if>
		</div>

		<c:if test="${atchmnflList != null && fn:length(atchmnflList) > 0}">
		<div class="files">
			<ul>
				<c:forEach var="file" items="${atchmnflList}">
				<li>
				<form method="post" action="<k:url value='/enterCldrApply/${vo.siteId}/${file.atchmnflSeq}/fileDown'/>">
					<p><button type="submit" class="cldr-file-btn"><c:out value="${file.orginlNm}"/></button></p>
				</form>
				</li>
				</c:forEach>
			</ul>
		</div>
		</c:if>
	</div>

	<div class="flex-center mt40">
		<button class="btn-func2 color1" onclick="goToStep2();">신청하기</button>
		<button class="btn-func2 color2 ml10" onclick="jf_artclSearch('<c:out value="${vo.siteId}"/>','<c:out value="${vo.fnctNo}"/>');">신청내역 확인</button>
	</div>
</div>

<%-- ===== STEP 2: 날짜 선택 ===== --%>
<div id="step2" class="mt30" style="display:none;">
	<%-- <div class="func-title"><strong>STEP2. <c:out value="${setup.setupNm}"/> 일자 선택</strong></div> --%>

	<div class="func-cal mt50">
		<div class="cal-row">
			<div class="left">
				<div class="cal-search">
					<div class="control">
						<a href="#" class="prev" onclick="prevMonth(); return false;" aria-label="이전 달">이전</a>
						<strong id="calYearMonth" aria-live="polite"></strong>
						<a href="#" class="next" onclick="nextMonth(); return false;" aria-label="다음 달">다음</a>
					</div>
				</div>
				<div class="cal-box mt30" data-simplebar>
					<table>
						<thead>
							<tr>
								<th scope="col">일</th><th scope="col">월</th><th scope="col">화</th><th scope="col">수</th><th scope="col">목</th><th scope="col">금</th><th scope="col">토</th>
							</tr>
						</thead>
						<tbody id="calendarBody"></tbody>
					</table>
				</div>
			</div>
			<div class="right">
				<div class="cal-state mt35">
					<ul>
						<li class="ok">신청가능</li>
						<li class="no">마감</li>
					</ul>
				</div>
				<div class="cal-list mt20">
					<div class="title"><strong>회차정보</strong></div>
					<div class="list" id="slotList">
						<p style="text-align:center; padding:2rem; color:#999; font-size:1.5rem;">날짜를 선택하세요.</p>
					</div>
				</div>
				<div class="flex-center mt40">
					<button class="btn-func2 color1" onclick="goToStep3();">신청하기</button>
					<button class="btn-func2 color2 ml10" onclick="showStep(1, false);">취소하기</button>
				</div>
			</div>
		</div>
	</div>
</div>

<%-- ===== STEP 3: 신청서 작성 ===== --%>
<div id="step3" class="mt30" style="display:none;">
	<%--<div class="func-title"><strong>STEP3. 신청서 작성</strong></div>--%>

	<div class="func-form mt20">
		<div class="row">
			<div class="title">신청 일자</div>
			<div class="insert"><span id="displayArtclDt"></span></div>
		</div>
		<div class="row">
			<div class="title top">시간 선택</div>
			<div class="insert" id="slotCheckList"></div>
		</div>
		<div class="row">
			<div class="title"><span class="must" aria-hidden="true">필수</span>이름</div>
			<div class="insert">
				<div class="form-input max320">
					<input type="text" id="rqstNm" placeholder="이름을 입력하세요." aria-label="이름" aria-required="true">
				</div>
			</div>
		</div>
		<div class="row">
			<div class="title"><span class="must" aria-hidden="true">필수</span>휴대전화</div>
			<div class="insert">
				<div class="form-input max90">
					<input type="text" id="phone1" value="010" maxlength="4" aria-label="휴대전화 앞자리" aria-required="true" onkeyup="this.value=this.value.replace(/[^0-9]/g,'');">
				</div>
				<span class="dash" aria-hidden="true">-</span>
				<div class="form-input max100">
					<input type="text" id="phone2" maxlength="4" aria-label="휴대전화 가운데자리" aria-required="true" onkeyup="this.value=this.value.replace(/[^0-9]/g,'');">
				</div>
				<span class="dash" aria-hidden="true">-</span>
				<div class="form-input max100">
					<input type="text" id="phone3" maxlength="4" aria-label="휴대전화 끝자리" aria-required="true" onkeyup="this.value=this.value.replace(/[^0-9]/g,'');">
				</div>
			</div>
		</div>
		<%-- 이메일/고교 필수 여부 --%>
		<c:set var="emailRequired" value="N"/>
		<c:set var="schoolRequired" value="N"/>
		<c:forEach var="fxItem" items="${formItemList}">
			<c:if test="${fxItem.itemType == 'RQST_ML' || fxItem.itemType == 'Email'}"><c:set var="emailRequired" value="${fxItem.requiredYn}"/></c:if>
			<c:if test="${fxItem.itemType == 'SCHOOL' || fxItem.itemType == 'School'}"><c:set var="schoolRequired" value="${fxItem.requiredYn}"/></c:if>
		</c:forEach>
		<div class="row">
			<div class="title">
				<c:if test="${emailRequired == 'Y'}"><span class="must" aria-hidden="true">필수</span></c:if>
				이메일
			</div>
			<div class="insert">
				<div class="form-input max240">
					<input type="text" id="rqstMl1" aria-label="이메일 아이디" ${emailRequired == 'Y' ? 'aria-required="true"' : ''}>
				</div>
				<span class="dash" aria-hidden="true">@</span>
				<div class="form-input max240">
					<input type="text" id="rqstMl2" aria-label="이메일 도메인" ${emailRequired == 'Y' ? 'aria-required="true"' : ''}>
				</div>
				<div class="form-select select-domain">
					<select id="emailDomain" aria-label="이메일 도메인 선택" onchange="jf_emailDomain(this.value);">
						<option value="">직접입력</option>
						<option value="naver.com">네이버</option>
						<option value="google.com">Google</option>
						<option value="daum.net">다음</option>
						<option value="nate.com">네이트</option>
						<option value="konkuk.ac.kr">konkuk.ac.kr</option>
					</select>
				</div>
			</div>
		</div>
		<div class="row">
			<div class="title">
				<c:if test="${schoolRequired == 'Y'}"><span class="must" aria-hidden="true">필수</span></c:if>
				고교검색
			</div>
			<div class="insert">
				<div class="form-input max320 mr10">
					<input type="text" id="schNm" readonly placeholder="고교를 검색하세요." aria-label="고교명" ${schoolRequired == 'Y' ? 'aria-required="true"' : ''}>
				</div>
				<input type="hidden" id="schCd">
				<input type="hidden" id="schLc">
				<input type="hidden" id="schTp">
				<button type="button" class="btn-func color3 btn-school" aria-haspopup="dialog">검색</button>
			</div>
		</div>
		<%-- 동적 폼 항목 --%>
		<c:if test="${formItemList != null && fn:length(formItemList) > 0}">
			<c:set var="dynCnt" value="0"/>
			<c:forEach var="item" items="${formItemList}">
			<c:if test="${item.fixedYn != 'Y'}">
			<c:set var="dynCnt" value="${dynCnt + 1}"/>
			<div class="row">
				<div class="title">
					<c:if test="${item.requiredYn == 'Y'}"><span class="must">필수</span></c:if>
					<c:out value="${item.itemNm}"/>
				</div>
				<div class="insert">
					<input type="hidden" class="answer-dynidx" value="${dynCnt}">
					<c:choose>
						<c:when test="${item.itemType == 'Radio'}">
							<c:forEach var="opt" items="${fn:split(item.itemOptions, ',')}">
							<div class="form-radio mr15">
								<input type="radio" name="formRadio_${item.formItemSeq}" id="r_${item.formItemSeq}_${fn:trim(opt)}" class="answer-radio" data-dynidx="${dynCnt}" value="${fn:trim(opt)}">
								<span class="custom-radio"></span>
								<label for="r_${item.formItemSeq}_${fn:trim(opt)}"><c:out value="${fn:trim(opt)}"/></label>
							</div>
							</c:forEach>
						</c:when>
						<c:when test="${item.itemType == 'Checkbox'}">
							<c:forEach var="opt" items="${fn:split(item.itemOptions, ',')}">
							<div class="form-check mr15">
								<input type="checkbox" id="c_${item.formItemSeq}_${fn:trim(opt)}" class="answer-chk" data-dynidx="${dynCnt}" value="${fn:trim(opt)}">
								<span class="custom-check"></span>
								<label for="c_${item.formItemSeq}_${fn:trim(opt)}"><c:out value="${fn:trim(opt)}"/></label>
							</div>
							</c:forEach>
						</c:when>
						<c:otherwise>
							<div class="form-input max400">
								<input type="text" class="answer-text" data-dynidx="${dynCnt}" placeholder="<c:out value='${item.itemNm}'/>을 입력하세요.">
							</div>
						</c:otherwise>
					</c:choose>
				</div>
			</div>
			</c:if>
			</c:forEach>
		</c:if>
		<%-- 동행 인원 --%>
		<c:if test="${setup.companionUseYn == 'Y'}">
		<div class="row">
			<div class="title">동행 인원</div>
			<div class="insert">
				<div class="form-input max70">
					<input type="number" id="companionCnt" min="0" value="0" maxlength="300">
				</div>
				&nbsp;명
			</div>
		</div>
		</c:if>
		<%-- 대상별 인원 --%>
		<c:if test="${setup.targetCompUseYn == 'Y' && targetItemList != null && fn:length(targetItemList) > 0}">
		<div class="row">
			<div class="title">대상별 인원</div>
			<div class="insert">
				<div class="target-list">
					<c:forEach var="item" items="${targetItemList}" varStatus="s">
					<div class="target-row">
						<span class="target-nm"><c:out value="${item.targetNm}"/></span>
						<div class="form-input max70">
							<input type="number" class="target-comp-input" data-seq="${item.targetItemSeq}" data-idx="${s.index}" min="0" value="0"  maxlength="300">
						</div>
						<span>명</span>
					</div>
					</c:forEach>
				</div>
			</div>
		</div>
		</c:if>
	</div>

	<%-- 개인정보 동의 --%>
	<c:if test="${not empty setup.privacyPurpose || not empty setup.privacyItems}">
	<div class="func-agree mt20">
		<div class="mt50">
			건국대학교'(이하 대학)은 개인정보보호법 등 관련 법령상의 개인정보 보호 규정을 준수하며, 고객님의 개인정보 보호에 최선을 다하고 있습니다.<br>
			대학은 개인정보보호법 제15조 및 동법 제22조에 근거하여, 다음과 같이 개인정보를 수집·이용하는데 동의를 받고자 합니다.
		</div>
		<div class="agree-circle mt20">
			<c:set var="privacyNum" value="1"/>
			<ul>
				<li>
					<c:if test="${not empty setup.privacyPurpose}">
						<span class="num">${privacyNum}</span>
						<strong>개인정보 수집·이용 목적</strong><br>
						<c:out value="${setup.privacyPurpose}"/>
						
						<c:set var="privacyNum" value="${privacyNum + 1}"/>
					</c:if>
				</li>
				<li>
					<c:if test="${not empty setup.privacyItems}">
						<span class="num">${privacyNum}</span>
						<strong>수집하는 개인정보의 항목</strong><br>
						<c:out value="${setup.privacyItems}"/>
						
						<c:set var="privacyNum" value="${privacyNum + 1}"/>
					</c:if>
				</li>
				<li>
					<c:if test="${not empty setup.privacyItems}">
						<span class="num">${privacyNum}</span>
						<strong>개인정보 보유 및 이용기간</strong><br>
						<c:out value="${setup.privacyPeriod}"/>
						
						<c:set var="privacyNum" value="${privacyNum + 1}"/>
					</c:if>
				</li>
			</ul>
		</div>
		<div class="agree-box mt40">
			<span class="text">개인정보 수집 및 이용에 동의합니다.</span>
			<div class="radio">
				<div class="form-check">
					<input type="checkbox" id="agreeChk">
					<span class="custom-check"></span>
					<label for="agreeChk">동의함</label>
				</div>
			</div>
		</div>
	</div>
	</c:if>

	<div class="flex-center mt40">
		<button class="btn-func2 color1" onclick="submitArtcl();">신청하기</button>
		<button class="btn-func2 color2 ml10" onclick="showStep(2, false);">이전</button>
	</div>
</div>

<form id="frm" name="frm" method="post" action="">
	<input type="hidden" name="layout" id="layout" value='<c:out value="${layout}"/>'>
</form>

<%-- ===== 학교 검색 레이어 ===== --%>
<div class="func-layer" role="dialog" aria-modal="true" aria-labelledby="schLayerTitle">
	<div class="box">
		<div class="row">
			<div class="title">
				<strong id="schLayerTitle">고교 검색</strong>
				<button type="button" class="btn-layer-close" aria-label="레이어 닫기">레이어 닫기</button>
			</div>
			<div class="search">
				<div class="input"><input type="text" id="srchSch" name="srchSch" placeholder="고등학교를 검색하세요" aria-label="고등학교 검색어" onkeypress="if(event.keyCode==13) jf_school('<c:out value="${vo.siteId}"/>');"></div>
				<div class="button"><button type="button" onclick="jf_school('<c:out value="${vo.siteId}"/>');">검색하기</button></div>
			</div>
			<div class="list addSch" aria-live="polite"></div>
		</div>
	</div>
</div>

</div><%-- ._fnctWrap --%>


<%-- JS 사용 변수 처리 --%>
<input type="hidden" id="siteId" value="<c:out value='${vo.siteId}'/>">
<input type="hidden" id="fnctNo" value="<c:out value='${vo.fnctNo}'/>">
<input type="hidden" id="setupSeq" value="<c:out value='${setup.setupSeq}'/>">
<input type="hidden" id="companionUse" value="<c:out value='${setup.companionUseYn}'/>">
<input type="hidden" id="targetUse" value="<c:out value='${setup.targetCompUseYn}'/>">

<input type="hidden" id="evtStart"  value="<fmt:formatDate value='${setup.evtStartDt}'  pattern='yyyy-MM-dd'/>">
<input type="hidden" id="evtEnd"    value="<fmt:formatDate value='${setup.evtEndDt}'    pattern='yyyy-MM-dd'/>">
<input type="hidden" id="recvStart" value="<fmt:formatDate value='${setup.recvStartDt}' pattern='yyyy-MM-dd HH:00:00'/>">
<input type="hidden" id="recvEnd"   value="<fmt:formatDate value='${setup.recvEndDt}'   pattern='yyyy-MM-dd HH:00:00'/>">
<input type="hidden" id="popupMsg"  value="<c:out value='${setup.popupMsg}'/>">

<input type="hidden" id="sunYn" value="<c:out value='${setup.sunYn}'/>">
<input type="hidden" id="monYn" value="<c:out value='${setup.monYn}'/>">
<input type="hidden" id="tueYn" value="<c:out value='${setup.tueYn}'/>">
<input type="hidden" id="wedYn" value="<c:out value='${setup.wedYn}'/>">
<input type="hidden" id="thuYn" value="<c:out value='${setup.thuYn}'/>">
<input type="hidden" id="friYn" value="<c:out value='${setup.friYn}'/>">
<input type="hidden" id="satYn" value="<c:out value='${setup.satYn}'/>">

<input type="hidden" id="emailRequired" value="<c:out value='${emailRequired}'/>">
<input type="hidden" id="schoolRequired" value="<c:out value='${schoolRequired}'/>">

<input type="hidden" id="privacyPurpose" value="<c:out value='${setup.privacyPurpose}'/>">
<input type="hidden" id="privacyItems" value="<c:out value='${setup.privacyItems}'/>">

<div id="timeSlotData" style="display:none;">
	<c:forEach var="slot" items="${timeSlotList}">
		<input type="hidden"
			   class="time-slot"
			   data-slot-seq="<c:out value='${slot.slotSeq}'/>"
			   data-apply-time="<c:out value='${slot.applyTime}'/>"
			   data-capacity="<c:out value='${slot.capacity}'/>">
	</c:forEach>
</div>
<%-- JS 사용 변수 처리 --%>

<c:if test='${layout==null || layout==""}'>
<%@ include file="/WEB-INF/jsp/fnct/enterCldrApply/enterCldrApply_basic_skin/main.js.jsp" %>
<%@ include file="/WEB-INF/jsp/include/user/footer.jsp" %>
</c:if>
