
var SITE_ID      = $("#siteId").val();
var FNCT_NO      = $("#fnctNo").val();
var SETUP_SEQ    = $("#setupSeq").val();
var COMPANIONUSE = $("#companionUse").val();
var TARGETUSE    = $("#targetUse").val();
var EVT_START    = $("#evtStart").val();
var EVT_END      = $("#evtEnd").val();
var RECV_START   = $("#recvStart").val();
var RECV_END     = $("#recvEnd").val();
var POPUP_MSG    = $("#popupMsg").val();

var AVAIL_DAYS = {
    0: $("#sunYn").val() === "Y",
    1: $("#monYn").val() === "Y",
    2: $("#tueYn").val() === "Y",
    3: $("#wedYn").val() === "Y",
    4: $("#thuYn").val() === "Y",
    5: $("#friYn").val() === "Y",
    6: $("#satYn").val() === "Y"
};

var HOLIDAYS = [];
$(".holiday-date").each(function () {
    HOLIDAYS.push($(this).val());
});

var TIME_SLOTS = [];
$(".time-slot").each(function () {
    TIME_SLOTS.push({
        slotSeq: Number($(this).data("slot-seq")),
        applyTime: String($(this).data("apply-time")),
        capacity: Number($(this).data("capacity"))
    });
});

var HAS_PRIVACY  = $.trim($("#privacyPurpose").val()) !== "" || $.trim($("#privacyItems").val()) !== "";
var ML_REQUIRED  = $("#emailRequired").val() === "Y";
var SCH_REQUIRED = $("#schoolRequired").val() === "Y";



/* ===================================================
   Step 이동
=================================================== */
function showStep(n, reloadChk) {
    $('#step1, #step2, #step3').hide();
    $('#step' + n).show();
    $('#stepTab li').removeClass('on');
    $('#stepTab' + n).addClass('on');
    window.scrollTo(0, 0);
    
    if(reloadChk != null && reloadChk) {
    	location.reload();
    }


}

/* ===================================================
   신청내역 확인 화면 이동
=================================================== */
function jf_artclSearch(siteId, fnctNo) {
    var url = kurl('/enterCldrApply/' + siteId + '/' + fnctNo + '/artclSearch');
    $("#frm").attr('action', url);
    $("#frm").submit();
}

/* ===================================================
   캘린더
=================================================== */
var calYear, calMonth, selectedDate = null, selectedSlotSeq = null, currentSlots = [];
var fullDates = {};

function padZero(n) { return n < 10 ? '0' + n : '' + n; }

var _today   = new Date();
var todayStr = _today.getFullYear() + '-' + padZero(_today.getMonth() + 1) + '-' + padZero(_today.getDate());

function isDayAvailable(dateStr) {
    if (dateStr < todayStr) return false;
    if (EVT_START && dateStr < EVT_START) return false;
    if (EVT_END   && dateStr > EVT_END)   return false;
    var d = new Date(dateStr + 'T12:00:00');
    if (!AVAIL_DAYS[d.getDay()]) return false;
    if (HOLIDAYS.indexOf(dateStr) >= 0) return false;
    return true;
}

function isRecvPeriod() {
    var now = new Date();
    if (RECV_START) {
        var rs = new Date(RECV_START.replace(' ', 'T'));
        if (now < rs) return false;
    }
    if (RECV_END) {
        var re = new Date(RECV_END.replace(' ', 'T'));
        if (now > re) return false;
    }
    return true;
}

function goToStep2() {
    if (!isRecvPeriod()) {
        alert('접수기간이 아닙니다.');
        return;
    }
    showStep(2, false);
}

function renderCalendar(year, month) {
    var firstDay    = new Date(year, month - 1, 1).getDay();
    var daysInMonth = new Date(year, month, 0).getDate();

    var html = '';
    var dayCount = 1;
    for (var row = 0; row < 6; row++) {
        html += '<tr>';
        for (var col = 0; col < 7; col++) {
            var cellNum = row * 7 + col;
            if (cellNum < firstDay || dayCount > daysInMonth) {
                html += '<td></td>';
            } else {
                var dateStr  = year + '-' + padZero(month) + '-' + padZero(dayCount);
                var avail    = isDayAvailable(dateStr);
                var isHol    = HOLIDAYS.indexOf(dateStr) >= 0;
                var isSel    = (dateStr === selectedDate);
                var isTod    = (dateStr === todayStr);
                var dowCls   = col === 0 ? ' sun' : (col === 6 ? ' sat' : '');
                var todayCls = isTod ? ' today-hl' : '';

                if (isSel) {
                    html += '<td class="selected' + dowCls + '">';
                    html += '<span class="date">' + dayCount + '</span>';
                    html += '</td>';
                } else if (isHol) {
                    html += '<td class="holiday' + dowCls + '">';
                    html += '<span class="date">' + dayCount + '</span>';
                    html += '<span class="state empty"></span>';
                    html += '</td>';
                } else if (!avail) {
                    html += '<td class="unavail">';
                    html += '<span class="date">' + dayCount + '</span>';
                    html += '</td>';
                } else {
                    var dotCls = fullDates[dateStr] ? 'no' : 'ok';
                    //html += '<td class="avail' + dowCls + todayCls + '">';
                    html += '<td class="avail' + dowCls + '">';
                    html += '<a onclick="clickDay(\'' + dateStr + '\')">';
                    html += '<span class="date">' + dayCount + '</span>';
                    html += '<span class="state ' + dotCls + '"></span>';
                    html += '</a>';
                    html += '</td>';
                }
                dayCount++;
            }
        }
        html += '</tr>';
        if (dayCount > daysInMonth) break;
    }
    $('#calendarBody').html(html);
    $('#calYearMonth').text(year + '.' + padZero(month));
}

function loadMonthAvailability(year, month) {
    $.ajax({
        url    : kurl('/enterCldrApply/' + SITE_ID + '/' + FNCT_NO + '/slotMonthAvailability'),
        data   : { year: year, month: month },
        success: function(r) {
            if (r.dateMap) {
                for (var d in r.dateMap) { fullDates[d] = (r.dateMap[d] === 'full'); }
            }
            renderCalendar(year, month);
        },
        error: function() { renderCalendar(year, month); }
    });
}

function prevMonth() {
    calMonth--;
    if (calMonth < 1) { calMonth = 12; calYear--; }
    loadMonthAvailability(calYear, calMonth);
}

function nextMonth() {
    calMonth++;
    if (calMonth > 12) { calMonth = 1; calYear++; }
    loadMonthAvailability(calYear, calMonth);
}

function clickDay(dateStr) {
    selectedDate    = dateStr;
    selectedSlotSeq = null;
    renderCalendar(calYear, calMonth);
    loadSlotAvailability(dateStr);
}

function loadSlotAvailability(dateStr) {
    $('#slotList').html('<p style="text-align:center; padding:2rem; color:#999; font-size:1.5rem;">로딩 중...</p>');
    $.ajax({
        url : kurl('/enterCldrApply/' + SITE_ID + '/' + FNCT_NO + '/slotAvailability'),
        data: { artclDt: dateStr },
        success: function(r) { renderSlotPanel(r.slotList); }
    });
}

function renderSlotPanel(slots) {
    currentSlots = slots || [];
    if (!slots || slots.length === 0) {
        if (selectedDate) { fullDates[selectedDate] = true; renderCalendar(calYear, calMonth); }
        $('#slotList').html('<p style="text-align:center; padding:2rem; color:#999; font-size:1.5rem;">등록된 회차가 없습니다.</p>');
        return;
    }
    var html = '<ul>';
    slots.forEach(function(s) {
        var isFull    = (s.capacity > 0 && s.remain <= 0);
        var statusNm  = isFull ? '마감' : '신청가능';
        var statusCls = isFull ? 'no' : 'ok';
        var disabled  = isFull ? ' disabled' : '';
        var idStr     = 'slot_' + s.slotSeq;
        html += '<li>';
        html += '<div class="form-radio">';
        html += '<input type="radio" name="slotSelect" id="' + idStr + '" value="' + s.slotSeq + '"' + disabled + '>';
        html += '<span class="custom-radio"></span>';
        html += '<label for="' + idStr + '">' + s.applyTime + '</label>';
        html += '</div>';
        html += '<span class="state ' + statusCls + '">' + statusNm + '</span>';
        html += '</li>';
    });
    html += '</ul>';
    $('#slotList').html(html);

    var allFull = slots.every(function(s) { return s.capacity > 0 && s.remain <= 0; });
    if (selectedDate) {
        fullDates[selectedDate] = allFull;
        renderCalendar(calYear, calMonth);
    }

    var firstAvail = slots.find(function(s) { return s.capacity === 0 || s.remain > 0; });
    if (firstAvail) {
        $('input[name="slotSelect"][value="' + firstAvail.slotSeq + '"]').prop('checked', true);
        selectedSlotSeq = firstAvail.slotSeq;
    }

    $('input[name="slotSelect"]').on('change', function() {
        selectedSlotSeq = $(this).val();
    });
}

function goToStep3() {
    if (!selectedDate) { alert('날짜를 선택하세요.'); return; }
    selectedSlotSeq = $('input[name="slotSelect"]:checked').val();
    if (!selectedSlotSeq) { alert('시간을 선택하세요.'); return; }

    var slot     = TIME_SLOTS.find(function(s) { return s.slotSeq == selectedSlotSeq; });
    var slotTime = slot ? slot.applyTime : '';
    $('#displayArtclDt').text(selectedDate + (slotTime ? ' (' + slotTime + ')' : ''));

    var slotHtml = '<ul>';
    currentSlots.forEach(function(s) {
        var isFull    = (s.capacity > 0 && s.remain <= 0);
        var isChecked = (s.slotSeq == selectedSlotSeq);
        var disabled  = (isFull && !isChecked) ? ' disabled' : '';
        var statusNm  = isFull ? '마감' : '신청가능';
        var statusCls = isFull ? 'no' : 'ok';
        var idStr     = 'step3slot_' + s.slotSeq;
        slotHtml += '<li>';
        slotHtml += '<div class="form-radio">';
        slotHtml += '<input type="radio" name="step3SlotSelect" id="' + idStr + '" value="' + s.slotSeq + '"' + (isChecked ? ' checked' : '') + disabled + '>';
        slotHtml += '<span class="custom-radio"></span>';
        slotHtml += '<label for="' + idStr + '">' + s.applyTime + '</label>';
        slotHtml += '</div>';
        slotHtml += '<span class="state ' + statusCls + '">' + statusNm + '</span>';
        slotHtml += '</li>';
    });
    slotHtml += '</ul>';
    $('#slotCheckList').html(slotHtml);

    $('input[name="step3SlotSelect"]').on('change', function() {
        selectedSlotSeq = $(this).val();
    });

    if (POPUP_MSG) { alert(POPUP_MSG); }
    showStep(3, false);

    // 고등학교 검색
    const searchSchool = function () {
        const btnSchool = $('.btn-school');
        const btnSchoolClose = $('.btn-layer-close')
        const layrerSchool = $('.func-layer');

        btnSchool.on('click', function () {
            if (!layrerSchool.hasClass('on')) {
                layrerSchool.addClass('on');
            } else {
                layrerSchool.removeClass('on');
            }
        });

        btnSchoolClose.on('click', function () {
            layrerSchool.removeClass('on');
        })
    };
}

/* ===================================================
   이메일 도메인 자동완성
=================================================== */
function jf_emailDomain(val) {
    if (val) { $('#rqstMl2').val(val).prop('readonly', true); }
    else     { $('#rqstMl2').val('').prop('readonly', false).focus(); }
}

/* ===================================================
   학교 검색
=================================================== */
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

/* ===================================================
   신청 제출
=================================================== */
function submitArtcl() {
    var artclDt     = selectedDate;
    var timeSlotSeq = $('input[name="step3SlotSelect"]:checked').val() || selectedSlotSeq;
    var rqstNm      = $('#rqstNm').val().trim();
    var rqstTel     = $('#phone1').val().trim() + '-' + $('#phone2').val().trim() + '-' + $('#phone3').val().trim();

    if (!rqstNm) { alert('이름을 입력하세요.', function() { $('#rqstNm').focus(); }); return; }
    if ($('#phone2').val().trim() === '' || $('#phone3').val().trim() === '') {
        alert('휴대전화를 입력하세요.'); return;
    }
    if (!timeSlotSeq) { alert('시간을 선택하세요.'); return; }

    if (ML_REQUIRED && (!$('#rqstMl1').val().trim() || !$('#rqstMl2').val().trim())) {
        alert('이메일을 입력하세요.', function() { $('#rqstMl1').focus(); }); return;
    }
    if (SCH_REQUIRED && !$('#schNm').val().trim()) {
        alert('고교명을 검색하여 선택하세요.'); return;
    }

    if (HAS_PRIVACY && !$('#agreeChk').prop('checked')) {
        alert('개인정보 수집 및 이용에 동의해 주세요.'); return;
    }

    var rqstMl2 = $('#rqstMl2').val().trim();
    var data = {
        'setupSeq'    : SETUP_SEQ,
        'artclDt'     : artclDt,
        'timeSlotSeq' : timeSlotSeq,
        'rqstNm'      : rqstNm,
        'rqstTel'     : rqstTel,
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
        url : kurl('/enterCldrApply/' + SITE_ID + '/artclRegistProc'),
        type: 'POST',
        data: data,
        success: function(r) {
            if (r.message) { alert(r.message); return; }
            alert('신청이 완료되었습니다.', function() { showStep(1, true); });
        }
    });
}

/* ===================================================
   초기화
=================================================== */
$(function() {
    var now = new Date();
    calYear  = now.getFullYear();
    calMonth = now.getMonth() + 1;

    var startRef = EVT_START || RECV_START;
    if (startRef) {
        var rs = new Date(startRef + 'T12:00:00');
        if (rs > now) { calYear = rs.getFullYear(); calMonth = rs.getMonth() + 1; }
    }
    loadMonthAvailability(calYear, calMonth);

    $(document).on('keydown', function(e) {
        if (e.keyCode === 27) $('.func-layer').removeClass('on');
    });
});
