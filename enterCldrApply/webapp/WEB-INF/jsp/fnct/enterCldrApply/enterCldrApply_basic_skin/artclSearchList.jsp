<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/jsp_header.jsp" %>

<c:if test='${layout==null || layout==""}'>
<%@ include file="/WEB-INF/jsp/include/user/header.jsp" %>
<%@ include file="/WEB-INF/jsp/fnct/enterCldrApply/enterCldrApply_basic_skin/artclSearchList.head.jsp" %>
<%@ include file="/WEB-INF/jsp/include/user/body.jsp" %>
</c:if>

<div class="_fnctWrap">
<c:if test='${isManager && fn:length(editMode)>0}'>
    <a href="<k:url value='/enterCldrApply/fnctMngr/${vo.siteId}/${setup.setupSeq}/setupView'/>" class="_fnctAdmin _blockUI" target="_blank">관리</a>
</c:if>

<div class="white-box">
    <div class="func-title"><strong>신청내역 조회 결과</strong></div>

    <c:choose>
        <c:when test="${artclList != null && fn:length(artclList) > 0}">
        <div class="list-table mt30" data-simplebar>
            <table>
                <thead>
                    <tr>
                        <th scope="col">번호</th>
                        <th scope="col">신청 일자</th>
                        <th scope="col">시간</th>
                        <th scope="col">동행 인원</th>
                        <th scope="col">신청 일시</th>
                        <th scope="col">신청 상태</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="artcl" items="${artclList}" varStatus="stat">
                    <tr onclick="jf_goUpdt('${artcl.artclSeq}');" onkeypress="if(event.keyCode===13||event.keyCode===32){jf_goUpdt('${artcl.artclSeq}');}" tabindex="0" style="cursor:pointer;">
                        <td>${fn:length(artclList) - stat.index}</td>
                        <td><fmt:formatDate value="${artcl.artclDt}" pattern="yyyy-MM-dd"/></td>
                        <td>
                            <c:choose>
                                <c:when test="${not empty artcl.applyTime}">${artcl.applyTime}</c:when>
                                <c:otherwise>-</c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                            <c:choose>
                                <c:when test="${artcl.companionCnt != null}">${artcl.companionCnt}명</c:when>
                                <c:otherwise>-</c:otherwise>
                            </c:choose>
                        </td>
                        <td><fmt:formatDate value="${artcl.rgsde}" pattern="yyyy-MM-dd HH:mm"/></td>
                        <td>
                            <c:choose>
                                <c:when test="${artcl.artclStatus == 'WAIT'}"><span class="artcl-status-wait">승인대기</span></c:when>
                                <c:when test="${artcl.artclStatus == 'APPROVED'}"><span class="artcl-status-approved">승인</span></c:when>
                                <c:when test="${artcl.artclStatus == 'REJECTED'}"><span class="artcl-status-rejected">미승인</span></c:when>
                                <c:when test="${artcl.artclStatus == 'CANCELED'}"><span class="artcl-status-canceled">취소</span></c:when>
                                <c:otherwise><c:out value="${artcl.artclStatus}"/></c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
        </c:when>
        <c:when test="${rqstNm != null}">
        <div class="no-result">조회된 신청 내역이 없습니다.</div>
        </c:when>
    </c:choose>

    <div class="flex-center mt40">
        <button class="btn-func2 color2" onclick="jf_goSearch();">이전으로</button>
    </div>
</div>

</div><%-- ._fnctWrap --%>

<form id="frm" name="frm" method="post" action="">
    <input type="hidden" name="layout" id="layout" value='<c:out value="${layout}"/>'>
    <input type="hidden" id="siteId" value='<c:out value="${vo.siteId}"/>'>
	<input type="hidden" id="fnctNo" value='<c:out value="${vo.fnctNo}"/>'>
</form>

<c:if test='${layout==null || layout==""}'>
<%@ include file="/WEB-INF/jsp/fnct/enterCldrApply/enterCldrApply_basic_skin/artclSearchList.js.jsp" %>
<%@ include file="/WEB-INF/jsp/include/user/footer.jsp" %>
</c:if>
