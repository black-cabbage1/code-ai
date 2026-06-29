var SITE_ID = $("#siteId").val();
var FNCT_NO = $("#fnctNo").val();
var ARTCL_SEQ  = $("#artclSeq").val();
var CAN_MODIFY = $("#canModify").val();

function jf_goUpdt(status) {
    if (status !== 'WAIT') { alert('승인/미승인 처리되어 수정이 불가능 합니다.'); return; }
    if (CAN_MODIFY !== 'true') { alert('수정기간이 아닙니다.'); return; }
    var url = kurl('/enterCldrApply/' + SITE_ID + '/' + FNCT_NO + '/' + ARTCL_SEQ + '/artclUpdtView');
    $('#frm').attr('action', url).submit();
}

function jf_cancel() {
    var rqstNm  = sessionStorage.getItem('cldrSrchRqstNm') || '';
    var rqstTel = sessionStorage.getItem('cldrSrchRqstTel') || '';
    confirm('신청을 취소하시겠습니까? 취소 후에는 복구가 불가능 합니다.', function() {
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

function jf_list() {
    var url = kurl('/enterCldrApply/' + SITE_ID + '/' + FNCT_NO + '/artclSearchList');
    $('#frm').attr('action', url).submit();
}
