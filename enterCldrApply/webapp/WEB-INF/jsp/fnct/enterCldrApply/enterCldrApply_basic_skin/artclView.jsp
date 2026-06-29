<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/jsp_header.jsp" %>

<c:if test='${layout==null || layout==""}'>
<%@ include file="/WEB-INF/jsp/include/user/header.jsp" %>
<%@ include file="/WEB-INF/jsp/fnct/enterCldrApply/enterCldrApply_basic_skin/main.head.jsp" %>
<%@ include file="/WEB-INF/jsp/include/user/body.jsp" %>
</c:if>

<div class="_fnctWrap">

<c:choose>
    <c:when test="${artcl == null}">
    <div style="text-align:center; padding:50px; color:#999;">
        신청 정보를 찾을 수 없습니다.
    </div>
    </c:when>
    <c:otherwise>

    <%-- canModify는 컨트롤러에서 전달; artclView에는 전달 안 하므로 JS에서 링크 클릭 시 서버 검증에 맡김 --%>
    <c:set var="canCancel" value="${artcl.artclStatus == 'WAIT'}"/>

    <div class="cldr-view-header">
        <c:out value="${artcl.rqstNm}"/>님은 신청
        <c:choose>
            <c:when test="${artcl.artclStatus == 'WAIT'}">대기</c:when>
            <c:when test="${artcl.artclStatus == 'APPROVED'}">완료</c:when>
            <c:when test="${artcl.artclStatus == 'CANCELED'}">취소</c:when>
            <c:otherwise>처리</c:otherwise>
        </c:choose>
        되었습니다.
    </div>

    <table class="cldr-view-table">
        <tr>
            <th>신청 일자</th>
            <td>
                <fmt:formatDate value="${artcl.artclDt}" pattern="yyyy-MM-dd"/>
                <c:if test="${not empty artcl.applyTime}">
                    (<c:out value="${artcl.applyTime}"/>)
                </c:if>
            </td>
        </tr>
        <tr>
            <th>이름</th>
            <td><c:out value="${artcl.rqstNm}"/></td>
        </tr>
        <tr>
            <th>휴대전화</th>
            <td><c:out value="${artcl.rqstTel}"/></td>
        </tr>
        <c:if test="${not empty artcl.rqstMl}">
        <tr>
            <th>이메일</th>
            <td><c:out value="${artcl.rqstMl}"/></td>
        </tr>
        </c:if>
        <c:if test="${not empty artcl.schNm}">
        <tr>
            <th>고교명</th>
            <td>
                <c:out value="${artcl.schNm}"/>
                <c:if test="${not empty artcl.schLc}">
                    (<c:out value="${artcl.schLc}"/>)
                </c:if>
            </td>
        </tr>
        </c:if>
        <c:if test="${setup.companionUseYn == 'Y'}">
        <tr>
            <th>동행 인원</th>
            <td><c:out value="${artcl.companionCnt}"/> 명</td>
        </tr>
        </c:if>
        <c:if test="${setup.targetCompUseYn == 'Y' && artcl.targetList != null && fn:length(artcl.targetList) > 0}">
        <tr>
            <th>대상별 인원</th>
            <td>
                <c:forEach var="target" items="${artcl.targetList}">
                    <c:out value="${target.targetNm}"/>: <c:out value="${target.compCnt}"/>명<br>
                </c:forEach>
            </td>
        </tr>
        </c:if>
        <c:if test="${artcl.answerList != null && fn:length(artcl.answerList) > 0}">
            <c:forEach var="answer" items="${artcl.answerList}">
            <tr>
                <th><c:out value="${answer.itemNm}"/></th>
                <td><c:out value="${answer.answerVal}"/></td>
            </tr>
            </c:forEach>
        </c:if>
        <tr>
            <th>신청 상태</th>
            <td>
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
            </td>
        </tr>
    </table>

    <div class="cldr-btn-area">
        <c:if test="${artcl.artclStatus == 'WAIT'}">
        <button class="cldr-btn cldr-btn-primary" onclick="jf_goUpdt();">수정</button>
        </c:if>
        <c:if test="${canCancel}">
        <button class="cldr-btn cldr-btn-secondary" onclick="jf_cancel();">취소</button>
        </c:if>
        <button class="cldr-btn cldr-btn-gray"
            onclick="location.href=kurl('/enterCldrApply/${vo.siteId}/${vo.fnctNo}/artclSearch');">이전으로</button>
    </div>

    </c:otherwise>
</c:choose>

</div><%-- ._fnctWrap --%>

<script>
var SITE_ID   = "${vo.siteId}";
var FNCT_NO   = "${vo.fnctNo}";
var ARTCL_SEQ = "${artcl.artclSeq}";
function jf_goUpdt() {
    location.href = kurl('/enterCldrApply/' + SITE_ID + '/' + FNCT_NO + '/' + ARTCL_SEQ + '/artclUpdtView');
}

function jf_cancel() {
    var rqstNm  = sessionStorage.getItem('cldrSrchRqstNm') || '';
    var rqstTel = sessionStorage.getItem('cldrSrchRqstTel') || '';
    confirm('신청을 취소하시겠습니까? 취소 후에는 되돌릴 수 없습니다.', function() {
        $.ajax({
            url : kurl('/enterCldrApply/' + SITE_ID + '/' + FNCT_NO + '/' + ARTCL_SEQ + '/artclCancelProc'),
            type: 'POST',
            data: { rqstNm: rqstNm, rqstTel: rqstTel },
            success: function(r) {
                if (r.message) { alert(r.message); return; }
                alert('취소되었습니다.', function() { location.reload(); });
            }
        });
    }, function() {});
}
</script>

<c:if test='${layout==null || layout==""}'>
<%@ include file="/WEB-INF/jsp/fnct/enterCldrApply/enterCldrApply_basic_skin/artclView.js.jsp" %>
<%@ include file="/WEB-INF/jsp/include/user/footer.jsp" %>
</c:if>
