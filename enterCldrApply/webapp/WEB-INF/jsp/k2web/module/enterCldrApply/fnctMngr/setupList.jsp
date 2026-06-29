<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/jsp_header.jsp" %>

<c:set var="mode" value="_mngr"/>
<c:set var="pageTitle" value="${multiNm}"/>
<c:set var="pageLoca01" value="${fnctInfo.fnctInfoLang.fnctNm}"/>

<%@ include file="/WEB-INF/jsp/include/mngr/manager/manager_header.jsp"%>

<script>
function jf_setupUpdtForm(siteId, setupSeq) {
    location.href = kurl("/enterCldrApply/fnctMngr/" + siteId + "/" +setupSeq+ "/setupView");
}

function jf_setupDelete(siteId, setupSeq) {
    confirm("삭제하시겠습니까?",
        function() {
            $.ajax({
                url  : kurl("/enterCldrApply/fnctMngr/" + siteId + "/setupDeleteProc"),
                type : "POST",
                data : { seq: setupSeq },
                success: function(result) {
                    if (!r.result) {
                        alert("실패하였습니다");
                    } else {
                    	alert(ktext("msg.delete.guide"), function(){
                    		location.reload();	
                    	});
                    }
                }
            });
        },
        function() {}
    );
}
</script>

<%@ include file="/WEB-INF/jsp/include/mngr/manager/manager_body.jsp"%>
<%@ include file="/WEB-INF/jsp/include/mngr/fnctInfo.jsp"%>

<c:choose>
    <c:when test="${setupList != null && fn:length(setupList) > 0}">
        <ul class="_widgetList">
            <c:forEach var="setup" items="${setupList}">
                <li>
					<div class="_divWrap">
						<div class="_head">
							<strong>
								${setup.setupNm}
							</strong>
						</div>
						<div class="_stat">
							<dl>
								<dt><k:text key="k2web.create.date" args="" /></dt>
								<dd><fmt:formatDate value="${setup.rgsde}" pattern="yyyy.MM.dd"/></dd>
							</dl>
							<dl>
								<dt><k:text key="k2web.use.status" args="" /></dt>
								<dd>
									<c:choose>
										<c:when test='${setup.useYn == "Y"}'>
										<k:text key="k2web.use3" args="" />
										</c:when>
										<c:otherwise>
										<k:text key="k2web.notUsed" args="" />
										</c:otherwise>
									</c:choose>
								</dd>
							</dl>
							<dl>
								<dt><k:text key="k2web.manager" args="" /></dt>
								<dd>
									<c:choose>
										<c:when test='${setup.fnctMngrList!=null && fn:length(setup.fnctMngrList)>0}'>
												<c:set var="isPrintedFnctMngrNm" value="${false}" />
												<c:forEach var="fnctMngr" items="${setup.fnctMngrList}" varStatus="stat">
													<c:if test='${fnctMngr.userInfo!=null && !isPrintedFnctMngrNm}'>
														<c:set var="isPrintedFnctMngrNm" value="${true}" />
														${fnctMngr.userInfo.userNm} <k:text key="k2web.rest" args="" /> ${fn:length(setup.fnctMngrList)-1}<k:text key="k2web.people2" args="" />
													</c:if>
													<%-- ${fnctMngr.userInfo.userNm}
													<c:if test='${!stat.last}'>, </c:if> --%>
												</c:forEach>
										</c:when>
										<c:otherwise>
											<k:text key="msg.none.setting" />
										</c:otherwise>
									</c:choose>
								</dd>
							</dl>
							<dl>
								<dt><k:text key="k2web.use.skin" args="" /></dt>
								<dd>${setup.skinNm}( ${setup.skinId} )</dd>
							</dl>
						</div>
						<div class="_goto">
							<div class="_inner">
								<input type="button" value="<k:text key="k2web.management" args="" />" class="_rscSet" onclick="jf_setupUpdtForm('${vo.siteId}', '${setup.setupSeq}');">
								<input type="button" value="<k:text key="k2web.delete" args="" />" class="_rscDelete" onclick="jf_setupDelete('${vo.siteId}', '${setup.setupSeq}');">
							</div>
						</div>
					</div>
				</li>
            </c:forEach>
        </ul>
    </c:when>
    <c:otherwise>
        <div class="_noData">등록된 설정이 없습니다.</div>
    </c:otherwise>
</c:choose>

<div class="_btnWidgetAdd">
    <a href="<k:url value='/enterCldrApply/fnctMngr/${vo.siteId}/setupRegist'/>">
        <input type="button" value="${fnctInfo.fnctInfoLang.fnctNm} 추가">
    </a>
</div>

<%@ include file="/WEB-INF/jsp/include/mngr/manager/manager_footer.jsp"%>
