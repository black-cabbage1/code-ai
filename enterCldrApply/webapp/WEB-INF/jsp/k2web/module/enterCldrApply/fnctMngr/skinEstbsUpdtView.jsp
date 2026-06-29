<%@ page import="k2web.com.util.K2Util" %>
<%@ page import="k2web.com.cmm.tag.TextTag" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/jsp/jsp_header.jsp" %>

<c:set var="mode" value="_mngr"/>
<c:set var="pageTitle" value="${multiNm}"/>
<c:set var="pageLoca01" value="${fnctInfo.fnctInfoLang.fnctNm}"/>
<c:set var="pageLoca02">
	<k:text key="k2web.skin.setting" args="" />
</c:set>

<%@ include file="/WEB-INF/jsp/include/mngr/manager/manager_header.jsp"%>
<%@ include file="/WEB-INF/jsp/include/mngr/manager/manager_body.jsp"%>
<%@ include file="/WEB-INF/jsp/k2web/module/enterCldrApply/fnctMngr/fnctInfoTabs.jsp"%>

<iframe src="<k:url value='/skin/fnctMngr/${vo.siteId}/${fnctId}/skinEstbsUpdtView'/>" title="${fnctInfo.fnctInfoLang.fnctNm} <k:text key="k2web.skin.list" args="" />" class="_skinListIframe"></iframe>

<%@ include file="/WEB-INF/jsp/include/mngr/manager/manager_footer.jsp"%>