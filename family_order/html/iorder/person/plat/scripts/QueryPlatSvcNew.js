window.onload = function () {
    $("#cond_SERIAL_NUMBER").focus();
}

function queryUserPlatsvcs() {
    //查询条件校验
    if (!$.validate.verifyAll("UserPlatsvcCond")) {
        return false;
    }

    $.beginPageLoading("正在查询数据...");

    ajaxSubmit('UserPlatsvcCond', 'qryUserPlatSvc', null, 'PlatsvcList',
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