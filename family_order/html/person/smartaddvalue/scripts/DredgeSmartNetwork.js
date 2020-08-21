function onInitTrade() {

}

function refreshPartAtferAuth(data) {
    var user_info = data.get("USER_INFO").toString();
    var cust_info = data.get("CUST_INFO").toString();
    var acct_info = data.get("ACCT_INFO").toString();

    var param = "&USER_INFO=" + user_info + "&CUST_INFO=" + cust_info
        + "&ACCT_INFO=" + acct_info;
    $.beginPageLoading("数据处理中...");
    $.ajax.submit('AuthPart', 'onInitTrade', param, 'discntsPart', function(
        data) {
        $.endPageLoading();
    }, function(error_code, error_info) {
        $.endPageLoading();
        alert(error_info);
    });
}

function checkAll() {
    var flag = true;

    if ($("#FIRST_TYPE").val() == '' && $("#THIRD_TYPE").val() == '') {
        alert("第一类或第三类必选一项！");
        flag = false;
        return;
    }

    if ($("#FIRST_TYPE").val() != '' && $("#THIRD_TYPE").val() != '') {
        alert("第一类和第三类只能选一项！");
        flag = false;
        return;
    }

    if ($("#FIRST_TYPE").val() == '' && $("#SECOND_TYPE").val() != '') {
        alert("选择第二类前需选第一类！");
        flag = false;
        return;
    }

    if ($("#THIRD_TYPE").val() != '' && $("#SECOND_TYPE").val() != '') {
        alert("第二类与选第三类不能同时选！");
        flag = false;
        return;
    }

    if ($("#FIRST_TYPE").val() == '' && $("#SECOND_TYPE").val() == ''
        && $("#THIRD_TYPE").val() == '') {
        alert("未做任何操作，不允许提交订单！");
        flag = false;
        return;
    }

//		if(checkWideBeforeSubmit()&&checkFeeBeforeSubmit()){
//			flag = true;
//		}else{
//			flag = false;
//		}
    flag = checkFeeBeforeSubmit();
    if(!flag){
        return false;
    }
    flag = checkWideBeforeSubmit();
    if(!flag){
        return false;
    }

    return flag;
}

//提交前宽带校验
function checkWideBeforeSubmit() {
    var FIRST_TYPE = $("#FIRST_TYPE").val();
    var SECOND_TYPE = $("#SECOND_TYPE").val();
    var THIRD_TYPE = $("#THIRD_TYPE").val();

    var result = true;
    var param = "&FIRST_TYPE=" + FIRST_TYPE + "&SECOND_TYPE=" + SECOND_TYPE
        + "&THIRD_TYPE=" +THIRD_TYPE+ "&SERIAL_NUMBER="
        + $("#AUTH_SERIAL_NUMBER").val();

    $.beginPageLoading("提交前费用校验中。。。");
    $.ajax.submit(null, 'checkWideBeforeSubmit', param, null, function(data) {
        $.endPageLoading();
        var resultCode = data.get("X_RESULTCODE");
        if (resultCode == '0000')
        {
            result = true;

        }else{
            result = false;
            $.MessageBox.error("61314", data.get("X_RESULTINFO"));
        }

    }, function(error_code, error_info) {
        $.endPageLoading();
        $.MessageBox.error(error_code, error_info);
        result = false;
    }, {
        async : false
    });

    return result;
}

//提交前费用校验
function checkFeeBeforeSubmit() {
    var FIRST_TYPE = $("#FIRST_TYPE").val();
    var SECOND_TYPE = $("#SECOND_TYPE").val();
    var THIRD_TYPE = $("#THIRD_TYPE").val();

    var result = true;
    var param = "&FIRST_TYPE=" + FIRST_TYPE + "&SECOND_TYPE=" + SECOND_TYPE
        + "&THIRD_TYPE=" +THIRD_TYPE+ "&SERIAL_NUMBER="
        + $("#AUTH_SERIAL_NUMBER").val();

    $.beginPageLoading("提交前费用校验中。。。");
    $.ajax.submit(null, 'checkFeeBeforeSubmit', param, null, function(data) {
        $.endPageLoading();
        var resultCode = data.get("X_RESULTCODE");
        if (resultCode == '0000')
        {
            result = true;
        }else{
            result = false;
            $.MessageBox.error("61314", data.get("X_RESULTINFO"));
        }

    }, function(error_code, error_info) {
        $.endPageLoading();
        $.MessageBox.error(error_code, error_info);
        result = false;
    }, {
        async : false
    });

    return result;
}

//根据所选优惠获取设备
function getDevice(value,num){

    var param = "&TYPE_CODE="+value;
    $.ajax.submit(null, 'getDevice', param, null, function(data) {
        $.endPageLoading();
        var resultCode = data.get("X_RESULTCODE");
        if (resultCode == '0000')
        {
            if("1"==num){
                $("#FIRST_TYPE_DEVICE").val("");
                $("#FIRST_TYPE_DEVICE").val(data.get("X_RESULTINFO"));
            }
            if("2"==num){
                $("#SECOND_TYPE_DEVICE").val("");
                $("#SECOND_TYPE_DEVICE").val(data.get("X_RESULTINFO"));
            }
            if("3"==num){
                $("#THIRD_TYPE_DEVICE").val("")
                $("#THIRD_TYPE_DEVICE").val(data.get("X_RESULTINFO"));
            }

        }

    }, function(error_code, error_info) {
        $.endPageLoading();
        $.MessageBox.error(error_code, error_info);
    }, {
        async : false
    });

}

