<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/jsp_header.jsp" %>

<c:if test='${layout==null || layout==""}'>
<%@ include file="/WEB-INF/jsp/include/user/header.jsp" %>
<%@ include file="/WEB-INF/jsp/fnct/enterCldrApply/enterCldrApply_basic_skin/main.head.jsp" %>
<%@ include file="/WEB-INF/jsp/include/user/body.jsp" %>
</c:if>

<div class="_fnctWrap">

<div class="cldr-step">
    <div class="cldr-step-title">신청내역 확인</div>

    <div class="cldr-search-box">
        <table>
            <tr>
                <th>이름</th>
                <td><input type="text" id="srchNm" style="width:200px;" placeholder="신청 시 입력한 이름"></td>
            </tr>
            <tr>
                <th>휴대전화</th>
                <td>
                    <input type="text" id="srchPhone1" style="width:50px;" value="010" maxlength="4" onkeyup="this.value=this.value.replace(/[^0-9]/g,'');">
                    <span>-</span>
                    <input type="text" id="srchPhone2" style="width:70px;" maxlength="4" onkeyup="this.value=this.value.replace(/[^0-9]/g,'');">
                    <span>-</span>
                    <input type="text" id="srchPhone3" style="width:70px;" maxlength="4" onkeyup="this.value=this.value.replace(/[^0-9]/g,'');">
                </td>
            </tr>
        </table>
        <div class="cldr-btn-area" style="margin-top:15px;">
            <button class="cldr-btn cldr-btn-primary" onclick="jf_search();">조회하기</button>
            <button class="cldr-btn cldr-btn-gray" onclick="javascript:jf_main('${vo.siteId}','${vo.fnctNo}');">이전으로</button>
        </div>
    </div>

    <div id="resultArea" style="display:none;">
        <table class="cldr-list-table">
            <thead>
                <tr>
                    <th>번호</th>
                    <th>신청 일자</th>
                    <th>시간</th>
                    <th>동행 인원</th>
                    <th>신청 일시</th>
                    <th>신청 상태</th>
                </tr>
            </thead>
            <tbody id="resultBody">
            </tbody>
        </table>
        <div id="noResult" style="display:none; text-align:center; padding:30px; color:#999;">
            조회된 신청 내역이 없습니다.
        </div>
    </div>

</div>

</div><%-- ._fnctWrap --%>

<form id="frm" name="frm" method="post" action="">
	<input type="hidden" name="layout" id="layout" value='<c:out value="${layout}"/>'>
</form>

<script>
var SITE_ID = "${vo.siteId}";
var FNCT_NO = "${vo.fnctNo}";

var STATUS_NM = { WAIT:'승인대기', APPROVED:'승인', REJECTED:'미승인', CANCELED:'취소' };
var STATUS_CLS = { WAIT:'artcl-status-wait', APPROVED:'artcl-status-approved',
                   REJECTED:'artcl-status-rejected', CANCELED:'artcl-status-canceled' };

function jf_main(siteId, fnctNo){
	var url = kurl('/enterCldrApply/' + siteId + '/' + fnctNo + '/main');
	$("#frm").attr('action', url);
	$("#frm").submit();
}

function jf_search() {
    var nm     = $('#srchNm').val().trim();
    var phone  = $('#srchPhone1').val().trim() + '-' + $('#srchPhone2').val().trim() + '-' + $('#srchPhone3').val().trim();

    if (!nm)   { alert('이름을 입력하세요.'); return; }
    if ($('#srchPhone2').val().trim() === '' || $('#srchPhone3').val().trim() === '') {
        alert('휴대전화를 입력하세요.'); return;
    }

    $.ajax({
        url : kurl('/enterCldrApply/' + SITE_ID + '/' + FNCT_NO + '/artclSearchProc'),
        data: { rqstNm: nm, rqstTel: phone },
        success: function(r) {
            var list = r.artclList;
            $('#resultArea').show();
            if (!list || list.length === 0) {
                $('#resultBody').html('');
                $('#noResult').show();
                return;
            }
            $('#noResult').hide();
            var html = '';
            list.forEach(function(a, i) {
                var artclDtStr = a.artclDt ? a.artclDt.substring(0, 10) : '';
                var rgsdtStr   = a.rgsde   ? a.rgsde.substring(0, 16).replace('T', ' ') : '';
                var statusNm   = STATUS_NM[a.artclStatus] || a.artclStatus;
                var statusCls  = STATUS_CLS[a.artclStatus] || '';
                html += '<tr style="cursor:pointer;" onclick="jf_artclView(' + a.artclSeq + ');">';
                html += '<td>' + (i+1) + '</td>';
                html += '<td>' + artclDtStr + '</td>';
                html += '<td>' + (a.applyTime || '') + '</td>';
                html += '<td>' + (a.companionCnt !== null ? a.companionCnt : '') + '</td>';
                html += '<td>' + rgsdtStr + '</td>';
                html += '<td class="' + statusCls + '">' + statusNm + '</td>';
                html += '</tr>';
            });
            $('#resultBody').html(html);
            /* 조회한 이름/전화번호를 세션에 임시 저장 (artclView에서 취소 확인용) */
            sessionStorage.setItem('cldrSrchRqstNm',  nm);
            sessionStorage.setItem('cldrSrchRqstTel', phone);
        }
    });
}

function jf_artclView(artclSeq) {
	var url = kurl('/enterCldrApply/' + SITE_ID + '/' + FNCT_NO + '/' + artclSeq + '/artclView');
	$("#frm").attr('action', url);
	$("#frm").submit();
}

$(function() {
    $('#srchNm, #srchPhone1, #srchPhone2, #srchPhone3').on('keypress', function(e) {
        if (e.keyCode === 13) jf_search();
    });
});
</script>

<c:if test='${layout==null || layout==""}'>
<%@ include file="/WEB-INF/jsp/fnct/enterCldrApply/enterCldrApply_basic_skin/artclSearch.js.jsp" %>
<%@ include file="/WEB-INF/jsp/include/user/footer.jsp" %>
</c:if>
