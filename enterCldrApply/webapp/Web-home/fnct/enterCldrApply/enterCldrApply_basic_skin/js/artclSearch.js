var SITE_ID = $("#siteId").val();
var FNCT_NO = $("#fnctNo").val();

function jf_main(siteId, fnctNo) {
    var url = kurl('/enterCldrApply/' + siteId + '/' + fnctNo + '/main');
    $("#frm").attr('action', url);
    $("#frm").submit();
}

function jf_search() {
    var nm    = $('#srchNm').val().trim();
    var phone = $('#srchPhone1').val().trim() + '-' + $('#srchPhone2').val().trim() + '-' + $('#srchPhone3').val().trim();

    if (!nm) { alert('이름을 입력하세요.'); return; }
    if ($('#srchPhone2').val().trim() === '' || $('#srchPhone3').val().trim() === '') {
        alert('휴대전화를 입력하세요.'); return;
    }

    sessionStorage.setItem('cldrSrchRqstNm',  nm);
    sessionStorage.setItem('cldrSrchRqstTel', phone);

    var url = kurl('/enterCldrApply/' + SITE_ID + '/' + FNCT_NO + '/artclSearchList');
    $("#rqstNm").val(nm);
    $("#rqstTel").val(phone);
    $("#frm").attr('action', url);
    $("#frm").submit();
}

$(function() {
    $('#srchNm, #srchPhone1, #srchPhone2, #srchPhone3').on('keypress', function(e) {
        if (e.keyCode === 13) jf_search();
    });
});
