var SITE_ID = $("#siteId").val();
var FNCT_NO = $("#fnctNo").val();
var COMPANIONUSE = $("#companionUseYn").val();
var ML_REQUIRED  = $("#emailRequired").val() == 'Y';
var SCH_REQUIRED = $("#schoolRequired").val() == 'Y';

function jf_emailDomain(val) {
    if (val) { $('#rqstMl2').val(val).prop('readonly', true); }
    else     { $('#rqstMl2').val('').prop('readonly', false).focus(); }
}

$(function() {
    var btnSchool      = $('.btn-school');
    var btnSchoolClose = $('.btn-layer-close');
    var layerSchool    = $('.func-layer');

    btnSchool.on('click', function() { layerSchool.toggleClass('on'); });
    btnSchoolClose.on('click', function() { layerSchool.removeClass('on'); });

    $(document).on('keydown', function(e) {
        if (e.keyCode === 27) $('.func-layer').removeClass('on');
    });

    $(document).on('click', '.sch-result-item', function(e) {
        e.preventDefault();
        jf_insert($(this).data('cd'), $(this).data('nm'), $(this).data('rgn'), $(this).data('ctgr'));
    });
});

function jf_school(siteId) {
    var srchSch = $("#srchSch").val();
    if (!srchSch) {
        alert("고교명을 입력하세요.");
        $("#srchSch").focus();
    } else {
        $.ajax({
            type  : "post",
            url   : kurl('/enterHgs/' + siteId + '/getSchList'),
            async : false,
            cache : false,
            data  : { 'srchSch': srchSch },
            success: function(r) {
                var html = "";
                if ($(r).size() > 0) {
                    html += "<ul>";
                    $(r).each(function() {
                        html += '<li><a href="#none" class="sch-result-item"' +
                                ' data-cd="'   + escHtml(this.schCd)  + '"' +
                                ' data-nm="'   + escHtml(this.schNm)  + '"' +
                                ' data-rgn="'  + escHtml(this.rgn)    + '"' +
                                ' data-ctgr="' + escHtml(this.ctgr)   + '">';
                        html += '<span>' + escHtml(this.schNm) + '</span> <span>' + escHtml(this.rgn) + '</span> <span>' + escHtml(this.ctgr) + '</span>';
                        html += '</a></li>';
                    });
                    html += "</ul>";
                } else {
                    html = '<p class="no-data">검색어를 입력하세요</p>';
                }
                $(".addSch").empty().append(html);
            }
        });
    }
}

function jf_insert(code, schNm, rgn, ctgr) {
    $("#schNm").val(schNm);
    $('#schCd').val(code);
    $("#schLc").val(rgn);
    $("#schTp").val(ctgr);
    $('.func-layer').removeClass('on');
}

function escHtml(str) {
    return str ? str.replace(/[&<>"']/g, function(m) {
        return {'&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;'}[m];
    }) : '';
}

function submitUpdt() {
    var rqstNm  = $('#rqstNm').val().trim();
    var slotSeq = $('input[name="timeSlotSelect"]:checked').val();
    if (!rqstNm)  { alert('이름을 입력하세요.'); return; }
    if ($('#phone2').val().trim() === '' || $('#phone3').val().trim() === '') {
        alert('휴대전화를 입력하세요.'); return;
    }
    if (!slotSeq) { alert('시간을 선택하세요.'); return; }

    if (ML_REQUIRED && (!$('#rqstMl1').val().trim() || !$('#rqstMl2').val().trim())) {
        alert('이메일을 입력하세요.'); $('#rqstMl1').focus(); return;
    }
    if (SCH_REQUIRED && !$('#schNm').val().trim()) {
        alert('고교명을 검색하여 선택하세요.'); return;
    }

    var rqstMl2 = $('#rqstMl2').val().trim();
    var data = {
        'artclSeq'    : $('#artclSeqHidden').val(),
        'setupSeq'    : $('#setupSeqHidden').val(),
        'artclDt'     : $('#artclDtHidden').val(),
        'timeSlotSeq' : slotSeq,
        'rqstNm'      : rqstNm,
        'rqstTel'     : $('#phone1').val() + '-' + $('#phone2').val() + '-' + $('#phone3').val(),
        'rqstMl'      : $('#rqstMl1').val().trim() + (rqstMl2 ? '@' + rqstMl2 : ''),
        'schCd'       : $('#schCd').val(),
        'schNm'       : $('#schNm').val().trim(),
        'schLc'       : $('#schLc').val(),
        'schTp'       : $('#schTp').val()
    };

    if (COMPANIONUSE === 'Y') {
        data['companionCnt'] = $('#companionCnt').val() || 0;
    }

    var ti = 0;
    $('.target-comp-input').each(function() {
        data['targetList[' + ti + '].targetItemSeq'] = $(this).data('seq');
        data['targetList[' + ti + '].compCnt']       = $(this).val() || 0;
        ti++;
    });

    $('.answer-dynidx').each(function() {
        var slot    = parseInt($(this).val());
        var row     = $(this).closest('.row');
        var val     = '';
        var textEl  = row.find('.answer-text');
        var radioEl = row.find('input.answer-radio:checked');
        var chkEls  = row.find('input.answer-chk:checked');
        if (textEl.length)       val = textEl.val().trim();
        else if (radioEl.length) val = radioEl.val();
        else if (chkEls.length)  val = $.map(chkEls.toArray(), function(el) { return $(el).val(); }).join(',');
        data['additm' + slot] = val;
    });

    $.ajax({
        url : kurl('/enterCldrApply/' + SITE_ID + '/artclUpdtProc'),
        type: 'POST',
        data: data,
        success: function(r) {
            if (r.message) { alert(r.message); return; }
            alert('수정되었습니다.', function() {
                var url = kurl('/enterCldrApply/' + SITE_ID + '/' + FNCT_NO + '/' + $('#artclSeqHidden').val() + '/artclView');
                $('#frm').attr('action', url).submit();
            });
        }
    });
}
