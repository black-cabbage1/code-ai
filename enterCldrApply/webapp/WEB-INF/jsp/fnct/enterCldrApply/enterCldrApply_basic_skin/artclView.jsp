<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/jsp_header.jsp" %>

<c:if test='${layout==null || layout==""}'>
<%@ include file="/WEB-INF/jsp/include/user/header.jsp" %>
<%@ include file="/WEB-INF/jsp/fnct/enterCldrApply/enterCldrApply_basic_skin/artclView.head.jsp" %>
<%@ include file="/WEB-INF/jsp/include/user/body.jsp" %>
</c:if>

<div class="_fnctWrap">

<c:choose>
    <c:when test="${artcl == null}">
    <div class="white-box">
        <div class="no-result" style="text-align:center; padding:5rem; color:#999; font-size:1.5rem;">
            신청 정보를 찾을 수 없습니다.
        </div>
    </div>
    </c:when>
    <c:otherwise>

    <c:set var="canCancel" value="${artcl.artclStatus == 'WAIT'}"/>

    <div class="white-box">
        <%-- 상태 배너 --%>
        <div class="func-banner">
            <div class="box">
                <c:out value="${artcl.rqstNm}"/>님의 신청이
                <c:choose>
                    <c:when test="${artcl.artclStatus == 'WAIT'}">접수되었습니다.</c:when>
                    <c:when test="${artcl.artclStatus == 'APPROVED'}">승인되었습니다.</c:when>
                    <c:when test="${artcl.artclStatus == 'CANCELED'}">취소되었습니다.</c:when>
                    <c:otherwise>처리되었습니다.</c:otherwise>
                </c:choose>
            </div>
        </div>

        <%-- 상세 정보 --%>
        <div class="func-result mt30">
            <div class="row">
                <div class="title">신청 일자</div>
                <div class="insert">
                    <fmt:formatDate value="${artcl.artclDt}" pattern="yyyy-MM-dd"/>
                    <c:if test="${not empty artcl.applyTime}">
                        &nbsp;(<c:out value="${artcl.applyTime}"/>)
                    </c:if>
                </div>
            </div>
            <div class="row">
                <div class="title">이름</div>
                <div class="insert"><c:out value="${artcl.rqstNm}"/></div>
            </div>
            <div class="row">
                <div class="title">휴대전화</div>
                <div class="insert"><c:out value="${artcl.rqstTel}"/></div>
            </div>
            <c:if test="${not empty artcl.rqstMl}">
            <div class="row">
                <div class="title">이메일</div>
                <div class="insert"><c:out value="${artcl.rqstMl}"/></div>
            </div>
            </c:if>
            <c:if test="${not empty artcl.schNm}">
            <div class="row">
                <div class="title">고교명</div>
                <div class="insert">
                    <c:out value="${artcl.schNm}"/>
                    <c:if test="${not empty artcl.schLc}">
                        &nbsp;(<c:out value="${artcl.schLc}"/>)
                    </c:if>
                </div>
            </div>
            </c:if>
            <c:if test="${setup.companionUseYn == 'Y'}">
            <div class="row">
                <div class="title">동행 인원</div>
                <div class="insert"><c:out value="${artcl.companionCnt}"/> 명</div>
            </div>
            </c:if>
            <c:if test="${setup.targetCompUseYn == 'Y' && artcl.targetList != null && fn:length(artcl.targetList) > 0}">
            <div class="row">
                <div class="title">대상별 인원</div>
                <div class="insert">
                    <c:forEach var="target" items="${artcl.targetList}">
                        <c:out value="${target.targetNm}"/>: <c:out value="${target.compCnt}"/>명&nbsp;
                    </c:forEach>
                </div>
            </div>
            </c:if>
            <c:if test="${artcl.dynamicFormItems != null && fn:length(artcl.dynamicFormItems) > 0}">
                <c:forEach var="addItm" items="${artcl.dynamicFormItems}">
                <div class="row">
                    <div class="title"><c:out value="${addItm.itemNm}"/></div>
                    <div class="insert"><c:out value="${addItm.answerVal}"/></div>
                </div>
                </c:forEach>
            </c:if>
            <div class="row">
                <div class="title">신청 상태</div>
                <div class="insert">
                    <c:choose>
                        <c:when test="${artcl.artclStatus == 'WAIT'}">
                            <span class="artcl-status-wait">승인대기</span>
                        </c:when>
                        <c:when test="${artcl.artclStatus == 'APPROVED'}">
                            <span class="artcl-status-approved">승인</span>
                        </c:when>
                        <c:when test="${artcl.artclStatus == 'REJECTED'}">
                            <span class="artcl-status-rejected">미승인</span>
                        </c:when>
                        <c:when test="${artcl.artclStatus == 'CANCELED'}">
                            <span class="artcl-status-canceled">취소</span>
                        </c:when>
                        <c:otherwise><c:out value="${artcl.artclStatus}"/></c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>

        <%-- 버튼 영역 --%>
        <div class="flex-center mt40">
            <button class="btn-func2 color1" onclick="jf_goUpdt('${artcl.artclStatus}');">수정</button>
            <c:if test="${canCancel}">
            <button class="btn-func2 color2 ml10" onclick="jf_cancel();">취소</button>
            </c:if>
            <button class="btn-func2 color2 ml10" onclick="jf_list();">이전으로</button>
        </div>
    </div>

    </c:otherwise>
</c:choose>

</div><%-- ._fnctWrap --%>

<form id="frm" name="frm" method="post" action="">
	<input type="hidden" name="layout" id="layout" value='<c:out value="${layout}"/>'>
	<input type="hidden" name="rqstNm" value='<c:out value="${artcl.rqstNm}"/>'>
	<input type="hidden" name="rqstTel" value='<c:out value="${artcl.rqstTel}"/>'>
	<input type="hidden" id="siteId" value='<c:out value="${vo.siteId}"/>'>
	<input type="hidden" id="fnctNo" value='<c:out value="${vo.fnctNo}"/>'>
</form>

<input type="hidden" id="artclSeq" value='<c:out value="${artcl.artclSeq}"/>'>
<input type="hidden" id="canModify" value='<c:out value="${canModify}"/>'>

<c:if test='${layout==null || layout==""}'>
<%@ include file="/WEB-INF/jsp/fnct/enterCldrApply/enterCldrApply_basic_skin/artclView.js.jsp" %>
<%@ include file="/WEB-INF/jsp/include/user/footer.jsp" %>
</c:if>
