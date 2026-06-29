<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/jsp_header.jsp" %>

<c:if test='${layout==null || layout==""}'>
<%@ include file="/WEB-INF/jsp/include/user/header.jsp" %>
<%@ include file="/WEB-INF/jsp/fnct/enterCldrApply/enterCldrApply_basic_skin/artclSearch.head.jsp" %>
<%@ include file="/WEB-INF/jsp/include/user/body.jsp" %>
</c:if>

<div class="_fnctWrap">
<c:if test='${isManager && fn:length(editMode)>0}'>
    <a href="<k:url value='/enterCldrApply/fnctMngr/${vo.siteId}/${setup.setupSeq}/setupView'/>" class="_fnctAdmin _blockUI" target="_blank">관리</a>
</c:if>

<div class="white-box">
    <div class="func-title"><strong>신청내역 확인</strong></div>

    <div class="func-form mt20">
        <div class="row">
            <div class="title">이름</div>
            <div class="insert">
                <div class="form-input max240">
                    <input type="text" id="srchNm" placeholder="신청 시 입력한 이름" aria-label="이름">
                </div>
            </div>
        </div>
        <div class="row">
            <div class="title">휴대전화</div>
            <div class="insert">
                <div class="form-input max90">
                    <input type="text" id="srchPhone1" value="010" maxlength="4" aria-label="휴대전화 앞자리" onkeyup="this.value=this.value.replace(/[^0-9]/g,'');">
                </div>
                <span class="dash" aria-hidden="true">-</span>
                <div class="form-input max100">
                    <input type="text" id="srchPhone2" maxlength="4" aria-label="휴대전화 가운데자리" onkeyup="this.value=this.value.replace(/[^0-9]/g,'');">
                </div>
                <span class="dash" aria-hidden="true">-</span>
                <div class="form-input max100">
                    <input type="text" id="srchPhone3" maxlength="4" aria-label="휴대전화 끝자리" onkeyup="this.value=this.value.replace(/[^0-9]/g,'');">
                </div>
            </div>
        </div>
    </div>

    <div class="flex-center mt30">
        <button class="btn-func2 color1" onclick="jf_search();">조회하기</button>
        <button class="btn-func2 color2 ml10" onclick="jf_main('<c:out value="${vo.siteId}"/>','<c:out value="${vo.fnctNo}"/>');">이전으로</button>
    </div>
</div>

</div><%-- ._fnctWrap --%>

<form id="frm" name="frm" method="post" action="">
	<input type="hidden" name="layout" id="layout" value='<c:out value="${layout}"/>'>
	<input type="hidden" name="rqstNm" id="rqstNm" value=''>
	<input type="hidden" name="rqstTel" id="rqstTel" value=''>
	<input type="hidden" id="siteId" value='<c:out value="${vo.siteId}"/>'>
	<input type="hidden" id="fnctNo" value='<c:out value="${vo.fnctNo}"/>'>
</form>

<c:if test='${layout==null || layout==""}'>
<%@ include file="/WEB-INF/jsp/fnct/enterCldrApply/enterCldrApply_basic_skin/artclSearch.js.jsp" %>
<%@ include file="/WEB-INF/jsp/include/user/footer.jsp" %>
</c:if>
