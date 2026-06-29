
var SITE_ID = $("#siteId").val();
var FNCT_NO = $("#fnctNo").val();

function jf_goUpdt(artclSeq) {
    var url = kurl('/enterCldrApply/' + SITE_ID + '/' + FNCT_NO + '/' + artclSeq + '/artclView');
    $('#frm').attr('action', url).submit();
}

function jf_goSearch() {
    var url = kurl('/enterCldrApply/' + SITE_ID + '/' + FNCT_NO + '/artclSearch');
    $('#frm').attr('action', url).submit();
}
