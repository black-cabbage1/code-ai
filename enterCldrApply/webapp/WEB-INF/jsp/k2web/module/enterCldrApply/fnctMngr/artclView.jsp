<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/jsp_header.jsp" %>

<c:set var="mode" value="_mngr"/>
<c:set var="pageTitle" value="${multiNm}"/>
<c:set var="pageLoca01" value="${fnctInfo.fnctInfoLang.fnctNm}"/>
<c:set var="pageLoca02">신청 관리</c:set>
<c:set var="pageLoca03">신청 상세</c:set>

<%@ include file="/WEB-INF/jsp/include/mngr/manager/manager_header.jsp"%>

<style>
.ecl-status          { font-weight: bold; font-size: 13px; }
.ecl-status-approved { color: #d84a38; }
.ecl-status-other    { color: #666; }
</style>

<script>
function jf_statusUpdt(status) {
    var statusNm = {WAIT:'승인대기', APPROVED:'승인', REJECTED:'미승인', CANCELED:'취소'}[status] || status;
    confirm("상태를 '" + statusNm + "'(으)로 변경하시겠습니까?",
        function() {
            $.ajax({
                url  : kurl("/enterCldrApply/fnctMngr/${vo.siteId}/${setupSeq}/${artcl.artclSeq}/artclStatusUpdtProc"),
                type : "POST",
                data : { artclStatus: status },
                success: function(result) {
                    if (result.message) { alert(result.message); } else {
                        alert("변경되었습니다.");
                        location.reload();
                    }
                }
            });
        },
        function() {}
    );
}

function jf_artclDelete() {
    confirm("삭제하시겠습니까?",
        function() {
            $.ajax({
                url  : kurl("/enterCldrApply/fnctMngr/${vo.siteId}/${setupSeq}/${artcl.artclSeq}/artclDeleteProc"),
                type : "POST",
                success: function(result) {
                    if (result.message) { alert(result.message); } else {
                        location.href = kurl("/enterCldrApply/fnctMngr/${vo.siteId}/artclList");
                    }
                }
            });
        },
        function() {}
    );
}
</script>

<%@ include file="/WEB-INF/jsp/include/mngr/manager/manager_body.jsp"%>
<%@ include file="/WEB-INF/jsp/k2web/module/enterCldrApply/fnctMngr/fnctInfoTabs.jsp"%>


<c:choose>
<c:when test="${artcl != null}">

<h2>신청 정보</h2>
<div class="_view _labelW01">
	
    <div class="_form">
        <label class="_label">신청 일자</label>
        <div class="_insert">
            <fmt:formatDate value="${artcl.artclDt}" pattern="yyyy-MM-dd"/>
        </div>
    </div>

    <c:if test="${not empty artcl.applyTime}">
    <div class="_form">
        <label class="_label">신청 시각</label>
        <div class="_insert">${artcl.applyTime}</div>
    </div>
    </c:if>

    <div class="_form">
        <label class="_label">신청 상태</label>
        <div class="_insert">
            <c:choose>
                <c:when test="${artcl.artclStatus == 'APPROVED'}">
                    <span class="ecl-status ecl-status-approved">승인</span>
                </c:when>
                <c:when test="${artcl.artclStatus == 'REJECTED'}">
                    <span class="ecl-status ecl-status-other">미승인</span>
                </c:when>
                <c:when test="${artcl.artclStatus == 'CANCELED'}">
                    <span class="ecl-status ecl-status-other">취소</span>
                </c:when>
                <c:otherwise>
                    <span class="ecl-status ecl-status-other">승인대기</span>
                </c:otherwise>
            </c:choose>
            <c:if test="${artcl.artclStatus != 'CANCELED'}">
            &nbsp;
            <span class="_button _small">
                <a href="#none" onclick="jf_statusUpdt('WAIT');">승인대기</a>
            </span>
            <span class="_button _small _active">
                <a href="#none" onclick="jf_statusUpdt('APPROVED');">승인</a>
            </span>
            <span class="_button _small">
                <a href="#none" onclick="jf_statusUpdt('REJECTED');">미승인</a>
            </span>
            </c:if>
            <c:if test="${artcl.statusUpdde != null}">
            	(상태 변경 일시 : <fmt:formatDate value="${artcl.statusUpdde}" pattern="yyyy-MM-dd HH:mm"/>)
            </c:if>
        </div>
    </div>

    <div class="_form">
        <label class="_label">신청자명</label>
        <div class="_insert"><c:out value="${artcl.rqstNm}"/></div>
    </div>

    <div class="_form">
        <label class="_label">휴대전화</label>
        <div class="_insert"><c:out value="${artcl.rqstTel}"/></div>
    </div>

    <c:if test="${not empty artcl.rqstMl}">
    <div class="_form">
        <label class="_label">이메일</label>
        <div class="_insert"><c:out value="${artcl.rqstMl}"/></div>
    </div>
    </c:if>

    <c:if test="${not empty artcl.schNm}">
    <div class="_form">
        <label class="_label">학교명</label>
        <div class="_insert">
            <c:out value="${artcl.schNm}"/>
            <c:if test="${not empty artcl.schLc}"> (<c:out value="${artcl.schLc}"/>)</c:if>
            <c:if test="${not empty artcl.schTp}"> / <c:out value="${artcl.schTp}"/></c:if>
        </div>
    </div>
    </c:if>

    <c:if test="${artcl.companionCnt != null}">
    <div class="_form">
        <label class="_label">동반 인원</label>
        <div class="_insert"><c:out value="${artcl.companionCnt}"/> 명</div>
    </div>
    </c:if>

    <c:if test="${artcl.targetList != null && fn:length(artcl.targetList) > 0}">
    <div class="_form">
        <label class="_label">대상별 인원</label>
        <div class="_insert">
            <table class="_table _list _inner">
                <thead><tr><th>대상</th><th>인원</th></tr></thead>
                <tbody>
                    <c:forEach var="target" items="${artcl.targetList}">
                        <tr>
                            <td><c:out value="${target.targetNm}"/></td>
                            <td><c:out value="${target.compCnt}"/> 명</td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
    </c:if>
	
    <c:if test="${artcl.dynamicFormItems != null && fn:length(artcl.dynamicFormItems) > 0}">
    <div class="_form">
        <label class="_label">추가 항목</label>
        <div class="_insert">
            <table class="_table _list _inner">
                <thead><tr><th>항목명</th><th>답변</th></tr></thead>
                <tbody>
                    <c:forEach var="item" items="${artcl.dynamicFormItems}">
                    <tr>
                        <td><c:out value="${item.itemNm}"/></td>
                        <td><c:out value="${item.answerVal}"/></td>
                    </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
    </c:if>

    <div class="_form">
        <label class="_label">등록일시</label>
        <div class="_insert">
            <fmt:formatDate value="${artcl.rgsde}" pattern="yyyy-MM-dd HH:mm"/>
        </div>
    </div>

    <c:if test="${artcl.updde != null && artcl.updde != artcl.rgsde}">
    <div class="_form">
        <label class="_label">수정일시</label>
        <div class="_insert">
            <fmt:formatDate value="${artcl.updde}" pattern="yyyy-MM-dd HH:mm"/>
        </div>
    </div>
    </c:if>

</div><!-- ._view -->

<c:url var="listUrl" value="/enterCldrApply/fnctMngr/${vo.siteId}/${setupSeq}/artclList.do">
    <c:param name="page"            value="${vo.page}"/>
    <c:param name="findArtclStatus" value="${vo.findArtclStatus}"/>
    <c:param name="findType"        value="${vo.findType}"/>
    <c:param name="findWord"        value="${vo.findWord}"/>
</c:url>
<c:url var="updtUrl" value="/enterCldrApply/fnctMngr/${vo.siteId}/${setupSeq}/${artcl.artclSeq}/artclUpdt.do">
    <c:param name="page"            value="${vo.page}"/>
    <c:param name="findArtclStatus" value="${vo.findArtclStatus}"/>
    <c:param name="findType"        value="${vo.findType}"/>
    <c:param name="findWord"        value="${vo.findWord}"/>
</c:url>
<div class="_areaButton">
    <span class="_button _large">
        <a href="${listUrl}">목록</a>
    </span>
    <span class="_button _large _active">
        <a href="${updtUrl}">수정</a>
    </span>
    <span class="_button _large">
        <a href="#none" onclick="jf_artclDelete();">삭제</a>
    </span>
</div>

</c:when>
<c:otherwise>
    <div class="_noData">신청 정보를 찾을 수 없습니다.</div>
    <div class="_areaButton">
        <span class="_button _large">
            <a href="<k:url value='/enterCldrApply/fnctMngr/${vo.siteId}/artclList'/>">목록</a>
        </span>
    </div>
</c:otherwise>
</c:choose>

<%@ include file="/WEB-INF/jsp/include/mngr/manager/manager_footer.jsp"%>
