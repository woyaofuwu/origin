window.onload = function () {
    $("#cond_SERIAL_NUMBER").focus();
}

function queryUserOrderInfoLog() {
    //查询条件校验
    if (!$.validate.verifyAll("UserOrderInfoLogCond")) {
        return false;
    }

    $.beginPageLoading("正在查询数据...");

    ajaxSubmit('UserOrderInfoLogCond', 'qryUserOrderInfoLog', null, 'OrderInfoLogList',
        function (data) {
            $.endPageLoading();
        }, function (code, info, detail) {
            $.endPageLoading();
            MessageBox.error("错误提示", info);
        }, function () {
            $.endPageLoading();
            MessageBox.alert("告警提示", "查询超时!", function (btn) {
            });
        });
}


function reset(){
    $("#cond_SERIAL_NUMBER").val('');
    $("#cond_STRAT_BOOK_TIME").val('');
    $("#cond_END_BOOK_TIME").val('');
    $("#cond_SERV_TYPE").val('');

}

function hideConfirmLog(){
	$('#confirmLog').css('display','none');
}

function showConfirmLogPop(confirm_log) {
	$('#confirm_log').html(confirm_log);
    showPopup('UI-popup3','UI-popup-query3');
}