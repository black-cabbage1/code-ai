<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/include/mngr/fnctInfo.jsp"%>

<c:set var="requestURIVal" value="${requestScope['javax.servlet.forward.servlet_path']}"/>

<div class="_tab _level1">
    <ul>
        <li><a href="<k:url value='/enterCldrApply/fnctMngr/${vo.siteId}/${setupSeq }/setupView'/>" class="<c:if test="${fn:indexOf(requestURIVal, 'setupView') != -1}">_on</c:if>">설정 관리</a></li>
        <li><a href="<k:url value='/enterCldrApply/fnctMngr/${vo.siteId}/${setupSeq }/artclList'/>" class="<c:if test="${fn:indexOf(requestURIVal, 'artcl') != -1}">_on</c:if>">신청자 관리</a></li>
        <c:if test='${isTopManager || isMidManager || isSiteManager}'>
			<li><input type="button" onclick="jf_fnctMngr('${vo.siteId}', 'enterCldrApply', '${setupSeq }')" value="<k:text key="k2web.manager.setting" args="" />"></li>
		</c:if>
		<li><a href="<k:url value='/enterCldrApply/fnctMngr/${vo.siteId}/${setupSeq }/skinEstbsUpdtView'/>" class="<c:if test="${fn:indexOf(requestURIVal, 'skinEstbsUpdtView') != -1}">_on</c:if>"><k:text key="k2web.skin.setting" args="" /></a></li>
    </ul>
</div>

<c:if test="${fn:indexOf(requestURIVal, 'setupView') != -1}">
	<div class="_tab _level2">
	    <ul>
	        <li><a href="#none" class="tab-btn" data-tab="tabSetup"   onclick="jf_tabClick('tabSetup')">기본 정보</a></li>
	        <li><a href="#none" class="tab-btn" data-tab="tabHoliday" onclick="jf_tabClick('tabHoliday')">휴일 관리</a></li>
	    </ul>
	</div>
</c:if>

<script>
function jf_tabClick(tabId) {
    if ($("#" + tabId).length === 0) tabId = "tabSetup";
    $(".tab-content").hide();
    $(".tab-btn").removeClass("_on");
    $("#" + tabId).show();
    $("a[data-tab='" + tabId + "']").addClass("_on");
    sessionStorage.setItem("enterCldrApply_tab_" + SETUP_SEQ, tabId);
}
</script>