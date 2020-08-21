
$(document).ready(function () {
    $("#SCAN_PSPT").bind("click", function () {
        getMsgByEForm("NETCHOOSE_PSPT_ID", "CUST_NAME", "SEX", "FOLK_CODE", "BIRTHDAY", "PSPT_ADDR", null, "PSPT_END_DATE");
        $.custInfo.verifyIdCard("NETCHOOSE_PSPT_ID");
        $.custInfo.verifyIdCardName("NETCHOOSE_PSPT_ID");
        querySerialMobile();
    });
});

function querySerialMobile() {
    // 查询条件校验
    if (!$.validate.verifyAll("QueryPhonePart")) {
        return;
    }
    var $psptId = $("#NETCHOOSE_PSPT_ID");
    var psptId = $psptId.val();
    if (psptId === "" || (psptId.length !== 15 && psptId.length !== 18)) {
        $.validate.alerter.one($psptId[0], "证件号码为空或者不符合长度！");
        return;
    }
    $("#chooseNumListMsg").css("display", "none");
    $.ajax.submit("QueryPhonePart", "queryIdlePhone", null, "PhonePart",
        function () {
            $.endPageLoading();
            $("#PhonePart").css("display", "");
        }, function (error_code, error_info) {
            $.endPageLoading();
            MessageBox.error(error_info);
            $("#chooseNumListMsg").css("display", "");
        });
}

// 将选择的手机号码传到父页面上
function setNewPhone(el) {
    var custName = $("#CUST_NAME").val(),
        sex = getMatchingValueFromSelectorByText("SEX", $("#SEX").val()),
        folkCode = getMatchingValueFromSelectorByText("FOLK_CODE", $("#FOLK_CODE").val());
    setPopupReturnValue(el, {
        "INFO_TAG": "1",
        "OCCUPY_TYPE_CODE": $(el).attr("occupyTypeCode"),
        "SERIAL_NUMBER": $(el).attr("resNo"),
        "PSPT_ID": $(el).attr("randomNo"),
        "CUST_NAME": custName,
        "PAY_NAME": custName,
        "SEX": sex,
        "FOLK_CODE": folkCode,
        "BIRTHDAY": $("#BIRTHDAY").val(),
        "PSPT_ADDR": $("#PSPT_ADDR").val(),
        "PSPT_END_DATE": $("#PSPT_END_DATE").val()
    }, true);
    parent.checkMphone(1);
}