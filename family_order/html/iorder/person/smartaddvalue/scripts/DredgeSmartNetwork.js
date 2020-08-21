window.onload=function(){
    $("#myTabSet").afterSwitchAction(function(e, idx){
        if(idx=="0"){
            $("#SELECT_TYPE").val("TIME");
        }else if(idx=="1"){
            $("#SELECT_TYPE").val("MONTH");
        }else{
            MessageBox.alert("告警提示", "TableSet加载错误");
        }
    });
};


function onInitTrade() {

}

function refreshPartAtferAuth(data) {
    var user_info = data.get("USER_INFO").toString();
    var cust_info = data.get("CUST_INFO").toString();
    var acct_info = data.get("ACCT_INFO").toString();

    var param = "&USER_INFO=" + user_info + "&CUST_INFO=" + cust_info
        + "&ACCT_INFO=" + acct_info;
    $.beginPageLoading("数据处理中...");
    $.ajax.submit('AuthPart', 'onInitTrade', param, 'discntsTimePart,discntsMonthPart', function (
        data) {
        $.endPageLoading();
    }, function (error_code, error_info) {
        $.endPageLoading();
        alert(error_info);
    });
}

function checkAll() {
    var flag = true;
    var selectType = $("#SELECT_TYPE").val();
    if ($("#FIRST_TYPE_"+selectType).val() == '' && $("#THIRD_TYPE_"+selectType).val() == '') {
        MessageBox.alert("告警提示", "全家WIFI礼包（设备+调试）和全家WIFI调测服务包必选一项！");
        return false;
    }

    if ($("#FIRST_TYPE_"+selectType).val() != '' && $("#THIRD_TYPE_"+selectType).val() != '') {
        MessageBox.alert("告警提示", "全家WIFI礼包（设备+调试）和全家WIFI调测服务包只能选一项！");
        return false;
    }

    if ($("#FIRST_TYPE_"+selectType).val() == '' && $("#SECOND_TYPE_"+selectType).val() != '') {
        MessageBox.alert("告警提示", "选择全家WIFI礼包(新增接入点)前需选择全家WIFI礼包（设备+调试）!");
        return false;
    }

    if ($("#THIRD_TYPE_"+selectType).val() != '' && $("#SECOND_TYPE_"+selectType).val() != '') {
        MessageBox.alert("告警提示","全家WIFI礼包(新增接入点)与全家WIFI调测服务包不能同时选！");
        return false;
    }

    if ($("#FIRST_TYPE_"+selectType).val() == '' && $("#SECOND_TYPE_"+selectType).val() == ''
        && $("#THIRD_TYPE_"+selectType).val() == '') {
        MessageBox.alert("告警提示","未做任何操作，不允许提交订单！");
        return false;
    }
    flag = checkWideBeforeSubmit();
    if (!flag) {
        return false;
    }
    flag = checkFeeBeforeSubmit();
    if (!flag) {
        return false;
    }
    return flag;
}

//提交前宽带校验
function checkWideBeforeSubmit() {
    var selectType = $("#SELECT_TYPE").val();
    var FIRST_TYPE = $("#FIRST_TYPE_"+selectType).val();
    var SECOND_TYPE = $("#SECOND_TYPE_"+selectType).val();
    var THIRD_TYPE = $("#THIRD_TYPE_"+selectType).val();

    var result = true;
    var param = "&FIRST_TYPE=" + FIRST_TYPE + "&SECOND_TYPE=" + SECOND_TYPE
        + "&THIRD_TYPE=" + THIRD_TYPE + "&SERIAL_NUMBER="
        + $("#AUTH_SERIAL_NUMBER").val();

    $.beginPageLoading("提交前宽带校验中。。。");
    $.ajax.submit(null, 'checkWideBeforeSubmit', param, null, function (data) {
        $.endPageLoading();
        var resultCode = data.get("X_RESULTCODE");
        if (resultCode == '0000') {
            result = true;

        } else {
            result = false;
            $.MessageBox.error("61314", data.get("X_RESULTINFO"));
        }
    }, function (error_code, error_info) {
        $.endPageLoading();
        $.MessageBox.error(error_code, error_info);
        result = false;
    }, {
        async: false
    });
    return result;
}

//提交前费用校验
function checkFeeBeforeSubmit() {
    var selectType = $("#SELECT_TYPE").val();
    var FIRST_TYPE = $("#FIRST_TYPE_"+selectType).val();
    var SECOND_TYPE = $("#SECOND_TYPE_"+selectType).val();
    var THIRD_TYPE = $("#THIRD_TYPE_"+selectType).val();

    var result = true;
    var param = "&FIRST_TYPE=" + FIRST_TYPE + "&SECOND_TYPE=" + SECOND_TYPE
        + "&THIRD_TYPE=" + THIRD_TYPE + "&SERIAL_NUMBER="
        + $("#AUTH_SERIAL_NUMBER").val();

    $.beginPageLoading("提交前费用校验中。。。");
    $.ajax.submit(null, 'checkFeeBeforeSubmit', param, null, function (data) {
        $.endPageLoading();
        var resultCode = data.get("X_RESULTCODE");
        if (resultCode == '0000') {
            if(data.get("IS_MONTH")!='1'){
                result = false;
                var msg="本次办理需从话费中扣除"+data.get("ALL_FEE")+"元！请确认!";
                MessageBox.confirm("告警提示", msg, function (re) {
                    if (re == "ok") {
                        $.cssubmit.submitTrade();
                    }
                });
            }else{
                result = true;
            }
        } else {
            result = false;
            $.MessageBox.error("61314", data.get("X_RESULTINFO"));
        }

    }, function (error_code, error_info) {
        $.endPageLoading();
        $.MessageBox.error(error_code, error_info);
        result = false;
    }, {
        async: false
    });
    return result;
}

//根据所选优惠获取设备
function getDevice(value, num) {
    var selectType = $("#SELECT_TYPE").val();
    var param = "&TYPE_CODE=" + value;
    if(value==""){
        if ("1" == num) {
            $("#FIRST_TYPE_DEVICE_"+selectType).val("");
        }
        if ("2" == num) {
            $("#SECOND_TYPE_DEVICE_"+selectType).val("");
        }
        if ("3" == num) {
            $("#THIRD_TYPE_DEVICE_"+selectType).val("");
        }
        return;
    }
    if("3" == num){
        if(!checkUserClass(value)){
            return;
        }
    }
    $.ajax.submit(null, 'getDevice', param, null, function (data) {
        $.endPageLoading();
        var resultCode = data.get("X_RESULTCODE");
        if (resultCode == '0000') {
            if ("1" == num) {
                $("#FIRST_TYPE_DEVICE_"+selectType).val("");
                $("#FIRST_TYPE_DEVICE_"+selectType).val(data.get("X_RESULTINFO"));
            }
            if ("2" == num) {
                $("#SECOND_TYPE_DEVICE_"+selectType).val("");
                $("#SECOND_TYPE_DEVICE_"+selectType).val(data.get("X_RESULTINFO"));
            }
            if ("3" == num) {
                $("#THIRD_TYPE_DEVICE_"+selectType).val("")
                $("#THIRD_TYPE_DEVICE_"+selectType).val(data.get("X_RESULTINFO"));
            }
        }
    }, function (error_code, error_info) {
        $.endPageLoading();
        //$.MessageBox.error(error_code, error_info);
		  MessageBox.confirm(error_info, null,
             function (btn) {
                 $("#AUTH_SUBMIT_BTN").trigger("click");
             })
    }, {
        async: false
    });
}

function checkUserClass(value) {
    var selectType = $("#SELECT_TYPE").val();
    var result = true;
    $.ajax.submit(null, 'checkUserClass', '&TYPE_CODE='+value+'&SERIAL_NUMBER='+$('#AUTH_SERIAL_NUMBER').val(), null, function (data) {
        $.endPageLoading();
        result = true;
    }, function (error_code, error_info) {
        $.endPageLoading();
        $.MessageBox.error(error_code, error_info);
        $("#THIRD_TYPE_"+selectType).val("");
        $("#THIRD_TYPE_DEVICE_" + selectType).val("")
        result = false;
    }, {
        async: false
    });
    return result;
}


	
