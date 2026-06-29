<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/jsp_header.jsp" %>

<c:set var="mode" value="_mngr"/>
<c:set var="pageTitle" value="${multiNm}"/>
<c:set var="pageLoca01" value="${fnctInfo.fnctInfoLang.fnctNm}"/>
<c:set var="pageLoca02">신청자 관리</c:set>

<%@ include file="/WEB-INF/jsp/include/mngr/manager/manager_header.jsp"%>

<script>
function jf_search() {
    document.frm.page.value = 1;
    document.frm.submit();
}

function jf_artclView(artclSeq) {
    var params = $('form[name="frm"]').serialize();
    location.href = kurl("/enterCldrApply/fnctMngr/${vo.siteId}/${setup.setupSeq}/" + artclSeq + "/artclView") + '?' + params;
}

function jf_statusUpdt(artclSeq, selectId) {
    var status   = $("#" + selectId).val();
    var statusNm = {WAIT: "승인대기", APPROVED: "승인", REJECTED: "미승인", CANCELED: "취소"}[status] || status;
    confirm("상태를 '" + statusNm + "'(으)로 변경하시겠습니까?",
        function() {
            $.ajax({
                url  : kurl("/enterCldrApply/fnctMngr/${vo.siteId}/${setup.setupSeq}/" + artclSeq + "/artclStatusUpdtProc"),
                type : "POST",
                data : { artclStatus: status },
                success: function(result) {
                    if (result.message) { alert(result.message); } else { location.reload(); }
                }
            });
        },
        function() {}
    );
}

function jf_artclDelete(artclSeq) {
    confirm("삭제하시겠습니까?",
        function() {
            $.ajax({
                url  : kurl("/enterCldrApply/fnctMngr/${vo.siteId}/${setup.setupSeq}/" + artclSeq + "/artclDeleteProc"),
                type : "POST",
                success: function(result) {
                    if (result.message) { alert(result.message); } else { location.reload(); }
                }
            });
        },
        function() {}
    );
}

function page_link(page) {
    document.frm.page.value = page;
    document.frm.submit();
}
</script>

<%@ include file="/WEB-INF/jsp/include/mngr/manager/manager_body.jsp"%>
<%@ include file="/WEB-INF/jsp/k2web/module/enterCldrApply/fnctMngr/fnctInfoTabs.jsp"%>

<form name="frm" method="get" action="<k:url value='/enterCldrApply/fnctMngr/${vo.siteId}/${setup.setupSeq}/artclList'/>">
    <input type="hidden" name="page" value="${vo.page}">

    <div class="_listHead">
        <div class="_count"><k:text key="k2web.total" args="" /> <strong>${pageNavi.count}</strong> <k:text key="k2web.count4" args="" /></div>
        <div class="_search">
            <fieldset>
                <legend>검색</legend>
                <select name="findArtclStatus" onchange="jf_search();">
                    <option value="">전체 상태</option>
                    <option value="WAIT"     ${vo.findArtclStatus == 'WAIT'     ? 'selected' : ''}>승인대기</option>
                    <option value="APPROVED" ${vo.findArtclStatus == 'APPROVED' ? 'selected' : ''}>승인</option>
                    <option value="REJECTED" ${vo.findArtclStatus == 'REJECTED' ? 'selected' : ''}>미승인</option>
                    <option value="CANCELED" ${vo.findArtclStatus == 'CANCELED' ? 'selected' : ''}>취소</option>
                </select>
                <select name="findType">
                    <option value="rqstNm" ${vo.findType == 'rqstNm' ? 'selected' : ''}>신청자명</option>
                    <option value="schNm"  ${vo.findType == 'schNm'  ? 'selected' : ''}>학교명</option>
                </select>
                <input type="text" name="findWord" value="<c:out value='${vo.findWord}'/>"
                    placeholder="검색어를 입력하세요."
                    onkeypress="if(event.keyCode=='13') jf_search();">
                <span class="_button _small _active">
                    <a href="#none" onclick="jf_search();">검색</a>
                </span>
            </fieldset>
        </div>
    </div>

    <table class="_table _list">
        <colgroup>
            <col class="_num">
            <col class="_w120">
            <col class="_auto">
            <col class="_w250">
            <col class="_w80">
            <col class="_w80">
            <col style="width:210px;">
            <col class="_w130">
            <col class="_w130">
            <col class="_w80">
        </colgroup>
        <thead>
            <tr>
                <th>번호</th>
                <th>신청 일자</th>
                <th>신청자</th>
                <th>학교명</th>
                <th>동반인원</th>
                <th>상태</th>
                <th>상태 변경</th>
                <th>등록일시</th>
                <th>수정일시</th>
                <th>삭제</th>
            </tr>
        </thead>
        <tbody>
            <c:choose>
                <c:when test="${artclList != null && fn:length(artclList) > 0}">
                    <c:forEach var="artcl" items="${artclList}" varStatus="stat">
                        <tr>
                            <td>${pageNavi.count-((vo.page-1)*vo.row)-stat.index}</td>
                            <td><fmt:formatDate value="${artcl.artclDt}" pattern="yyyy-MM-dd"/></td>
                            <td>
                                <a href="#none" onclick="jf_artclView('${artcl.artclSeq}');">
                                    <c:out value="${artcl.rqstNm}"/>
                                </a>
                            </td>
                            <td><c:out value="${artcl.schNm}"/></td>
                            <td>
                                <c:choose>
                                    <c:when test="${artcl.companionCnt != null}">${artcl.companionCnt}명</c:when>
                                    <c:otherwise>-</c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${artcl.artclStatus == 'APPROVED'}"><span class="artcl-status _active">승인</span></c:when>
                                    <c:when test="${artcl.artclStatus == 'REJECTED'}"><span class="artcl-status _inactive">미승인</span></c:when>
                                    <c:when test="${artcl.artclStatus == 'CANCELED'}"><span class="artcl-status _canceled">취소</span></c:when>
                                    <c:otherwise><span class="artcl-status">승인대기</span></c:otherwise>
                                </c:choose>
                            </td>
                            <td style="white-space:nowrap;">
                                <select id="status_${artcl.artclSeq}" style="width:82px;">
                                    <option value="WAIT"     ${artcl.artclStatus == 'WAIT'     ? 'selected' : ''}>승인대기</option>
                                    <option value="APPROVED" ${artcl.artclStatus == 'APPROVED' ? 'selected' : ''}>승인</option>
                                    <option value="REJECTED" ${artcl.artclStatus == 'REJECTED' ? 'selected' : ''}>미승인</option>
                                    <option value="CANCELED" ${artcl.artclStatus == 'CANCELED' ? 'selected' : ''}>취소</option>
                                </select>
                                <span class="_button _small _active">
                                    <a href="#none" onclick="jf_statusUpdt('${artcl.artclSeq}', 'status_${artcl.artclSeq}');">변경</a>
                                </span>
                            </td>
                            <td><fmt:formatDate value="${artcl.rgsde}" pattern="yyyy-MM-dd HH:mm"/></td>
                            <td>
                            	<c:choose>
                            	<c:when test="${artcl.updde != null && artcl.updde != artcl.rgsde}">
                            		<fmt:formatDate value="${artcl.updde}" pattern="yyyy-MM-dd HH:mm"/>
                            	</c:when>
                            	<c:otherwise>
                            		-
                            	</c:otherwise>
                            	</c:choose>
                            </td>
                            <td>
                                <span class="_button _small">
                                    <a href="#none" onclick="jf_artclDelete('${artcl.artclSeq}');">삭제</a>
                                </span>
                            </td>
                        </tr>
                    </c:forEach>
                </c:when>
                <c:otherwise>
                    <tr><td colspan="10" class="_noData">신청 내역이 없습니다.</td></tr>
                </c:otherwise>
            </c:choose>
        </tbody>
    </table>

    <k:page value="pageNavi" script="page_link"/>

</form>

<%@ include file="/WEB-INF/jsp/include/mngr/manager/manager_footer.jsp"%>
