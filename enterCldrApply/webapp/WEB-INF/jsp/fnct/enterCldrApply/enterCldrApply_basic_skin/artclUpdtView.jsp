<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/jsp_header.jsp" %>

<c:if test='${layout==null || layout==""}'>
<%@ include file="/WEB-INF/jsp/include/user/header.jsp" %>
<%@ include file="/WEB-INF/jsp/fnct/enterCldrApply/enterCldrApply_basic_skin/artclUpdtView.head.jsp" %>
<%@ include file="/WEB-INF/jsp/include/user/body.jsp" %>
</c:if>

<div class="_fnctWrap">

<c:choose>
    <c:when test="${!canModify}">
    <div class="white-box">
        <p class="p-color1" style="text-align:center; padding:4rem; font-size:1.6rem;">수정기간이 아닙니다. 관리자에게 문의바랍니다.</p>
        <div class="flex-center mt20">
            <button class="btn-func2 color2" onclick="history.back();">이전으로</button>
        </div>
    </div>
    </c:when>
    <c:otherwise>

    <div class="white-box">
        <div class="func-title"><strong>신청자 정보 수정</strong></div>

        <div class="func-form mt20">
            <div class="row">
                <div class="title">신청 일자</div>
                <div class="insert">
                    <fmt:formatDate value="${artcl.artclDt}" pattern="yyyy-MM-dd"/>
                    <c:if test="${not empty artcl.applyTime}">
                        &nbsp;(<c:out value="${artcl.applyTime}"/>)
                    </c:if>
                    <input type="hidden" id="artclDtHidden" value="<fmt:formatDate value='${artcl.artclDt}' pattern='yyyy-MM-dd'/>">
                    <input type="hidden" id="artclSeqHidden" value="${artcl.artclSeq}">
                    <input type="hidden" id="setupSeqHidden" value="${setup.setupSeq}">
                </div>
            </div>
            <div class="row">
                <div class="title top">시간 선택</div>
                <div class="insert">
                    <c:forEach var="slot" items="${timeSlotList}">
                    <c:set var="isFull" value="${slot.capacity > 0 && slot.bookedCount >= slot.capacity}"/>
                    <c:set var="isCurrent" value="${artcl.timeSlotSeq == slot.slotSeq}"/>
                    <div class="form-radio mr15${isFull && !isCurrent ? ' slot-label-full' : ''}">
                        <input type="radio" name="timeSlotSelect"
                            id="ts_${slot.slotSeq}" value="${slot.slotSeq}"
                            ${isCurrent ? 'checked' : ''}
                            ${isFull && !isCurrent ? 'disabled' : ''}>
                        <span class="custom-radio"></span>
                        <label for="ts_${slot.slotSeq}">
                            <c:out value="${slot.applyTime}"/>
                            <c:if test="${isFull && !isCurrent}"><span class="slot-full-badge">(마감)</span></c:if>
                        </label>
                    </div>
                    </c:forEach>
                </div>
            </div>
            <div class="row">
                <div class="title"><span class="must" aria-hidden="true">필수</span>이름</div>
                <div class="insert">
                    <c:out value='${artcl.rqstNm}'/>
                    <input type="hidden" id="rqstNm" value="<c:out value='${artcl.rqstNm}'/>">
                </div>
            </div>
            <c:set var="telParts" value="${fn:split(artcl.rqstTel, '-')}"/>
            <div class="row">
                <div class="title"><span class="must" aria-hidden="true">필수</span>휴대전화</div>
                <div class="insert">
                    <div class="form-input max90">
                        <input type="text" id="phone1" readonly aria-label="휴대전화 앞자리" value="${fn:length(telParts) > 0 ? telParts[0] : '010'}">
                    </div>
                    <span class="dash" aria-hidden="true">-</span>
                    <div class="form-input max100">
                        <input type="text" id="phone2" readonly aria-label="휴대전화 가운데자리" value="${fn:length(telParts) > 1 ? telParts[1] : ''}">
                    </div>
                    <span class="dash" aria-hidden="true">-</span>
                    <div class="form-input max100">
                        <input type="text" id="phone3" readonly aria-label="휴대전화 끝자리" value="${fn:length(telParts) > 2 ? telParts[2] : ''}">
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
            <c:set var="emailLocal" value="${fn:substringBefore(artcl.rqstMl, '@')}"/>
            <c:set var="emailDomainVal" value="${fn:substringAfter(artcl.rqstMl, '@')}"/>
            <div class="row">
                <div class="title">
                    <c:if test="${emailRequired == 'Y'}"><span class="must" aria-hidden="true">필수</span></c:if>
                    이메일
                </div>
                <div class="insert">
                    <div class="form-input max240">
                        <input type="text" id="rqstMl1" aria-label="이메일 아이디" ${emailRequired == 'Y' ? 'aria-required="true"' : ''} value="<c:out value='${emailLocal}'/>">
                    </div>
                    <span class="dash" aria-hidden="true">@</span>
                    <div class="form-input max240">
                        <input type="text" id="rqstMl2" aria-label="이메일 도메인" ${emailRequired == 'Y' ? 'aria-required="true"' : ''} value="<c:out value='${emailDomainVal}'/>">
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
                    고교명
                </div>
                <div class="insert">
                    <div class="form-input max400" style="display:inline-flex; max-width:22rem; margin-right:1rem;">
                        <input type="text" id="schNm" readonly aria-label="고교명" ${schoolRequired == 'Y' ? 'aria-required="true"' : ''} value="<c:out value='${artcl.schNm}'/>">
                    </div>
                    <input type="hidden" id="schCd" value="<c:out value='${artcl.schCd}'/>">
                    <input type="hidden" id="schLc" value="<c:out value='${artcl.schLc}'/>">
                    <input type="hidden" id="schTp" value="<c:out value='${artcl.schTp}'/>">
                    <button type="button" class="btn-func color3 btn-school" aria-haspopup="dialog">검색</button>
                </div>
            </div>
            <c:if test="${setup.companionUseYn == 'Y'}">
            <div class="row">
                <div class="title">동행 인원</div>
                <div class="insert">
                    <div class="form-input max70">
                        <input type="number" id="companionCnt" min="0" value="${artcl.companionCnt}">
                    </div>
                    &nbsp;명
                </div>
            </div>
            </c:if>
            <c:if test="${setup.targetCompUseYn == 'Y' && targetItemList != null && fn:length(targetItemList) > 0}">
            <div class="row">
                <div class="title">대상별 인원</div>
                <div class="insert">
                    <div class="target-list">
                        <c:forEach var="item" items="${targetItemList}" varStatus="s">
                        <c:set var="targetCompCnt" value="0"/>
                        <c:forEach var="t" items="${artcl.targetList}">
                            <c:if test="${t.targetItemSeq == item.targetItemSeq}">
                                <c:set var="targetCompCnt" value="${t.compCnt}"/>
                            </c:if>
                        </c:forEach>
                        <div class="target-row">
                            <span class="target-nm"><c:out value="${item.targetNm}"/></span>
                            <div class="form-input max70">
                                <input type="number" class="target-comp-input"
                                    data-seq="${item.targetItemSeq}" data-idx="${s.index}"
                                    min="0" value="${targetCompCnt}">
                            </div>
                            <span>명</span>
                        </div>
                        </c:forEach>
                    </div>
                </div>
            </div>
            </c:if>
            <%-- 동적 폼 항목 --%>
            <c:if test="${artcl.dynamicFormItems != null && fn:length(artcl.dynamicFormItems) > 0}">
                <c:forEach var="addItm" items="${artcl.dynamicFormItems}" varStatus="dynStat">
                <div class="row">
                    <div class="title">
                        <c:if test="${addItm.requiredYn == 'Y'}"><span class="must" aria-hidden="true">필수</span></c:if>
                        <c:out value="${addItm.itemNm}"/>
                    </div>
                    <div class="insert">
                        <input type="hidden" class="answer-dynidx" value="${dynStat.count}">
                        <c:choose>
                            <c:when test="${addItm.itemType == 'Radio'}">
                                <c:forEach var="opt" items="${fn:split(addItm.itemOptions, ',')}">
                                    <c:set var="optTrim" value="${fn:trim(opt)}"/>
                                    <div class="form-radio mr15">
                                        <input type="radio" name="formRadio_${addItm.formItemSeq}"
                                            id="r_${addItm.formItemSeq}_${optTrim}"
                                            class="answer-radio" data-dynidx="${dynStat.count}"
                                            value="${optTrim}"
                                            ${addItm.answerVal == optTrim ? 'checked' : ''}>
                                        <span class="custom-radio"></span>
                                        <label for="r_${addItm.formItemSeq}_${optTrim}"><c:out value="${optTrim}"/></label>
                                    </div>
                                </c:forEach>
                            </c:when>
                            <c:when test="${addItm.itemType == 'Checkbox'}">
                                <c:forEach var="opt" items="${fn:split(addItm.itemOptions, ',')}">
                                    <c:set var="optTrim" value="${fn:trim(opt)}"/>
                                    <div class="form-check mr15">
                                        <input type="checkbox" id="c_${addItm.formItemSeq}_${optTrim}"
                                            class="answer-chk" data-dynidx="${dynStat.count}"
                                            value="${optTrim}"
                                            ${fn:contains(addItm.answerVal, optTrim) ? 'checked' : ''}>
                                        <span class="custom-check"></span>
                                        <label for="c_${addItm.formItemSeq}_${optTrim}"><c:out value="${optTrim}"/></label>
                                    </div>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <div class="form-input max400">
                                    <input type="text" class="answer-text" data-dynidx="${dynStat.count}"
                                        value="<c:out value='${addItm.answerVal}'/>">
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
                </c:forEach>
            </c:if>
        </div>

        <div class="flex-center mt40">
            <button class="btn-func2 color1" onclick="submitUpdt();">수정</button>
            <button class="btn-func2 color2 ml10" onclick="history.back();">이전으로</button>
        </div>
    </div>

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

    </c:otherwise>
</c:choose>

</div><%-- ._fnctWrap --%>

<form id="frm" name="frm" method="post" action="">
    <input type="hidden" name="layout" id="layout" value='<c:out value="${layout}"/>'>
    <input type="hidden" id="siteId" value='<c:out value="${vo.siteId}"/>'>
	<input type="hidden" id="fnctNo" value='<c:out value="${vo.fnctNo}"/>'>
</form>

<input type="hidden" id="companionUseYn" value='<c:out value="${setup.companionUseYn}"/>'>
<input type="hidden" id="emailRequired" value='<c:out value="${emailRequired}"/>'>
<input type="hidden" id="schoolRequired" value='<c:out value="${schoolRequired}"/>'>

<c:if test='${layout==null || layout==""}'>
<%@ include file="/WEB-INF/jsp/fnct/enterCldrApply/enterCldrApply_basic_skin/artclUpdtView.js.jsp" %>
<%@ include file="/WEB-INF/jsp/include/user/footer.jsp" %>
</c:if>
