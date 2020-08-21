/**
 * Auth组件输入号码认证后回调函数
 *
 * @param data
 * @returns
 */
function refreshPartAtferAuth(data) {
    var param = "&ROUTE_EPARCHY_CODE=" + data.get("USER_INFO").get("EPARCHY_CODE") + "&TRADE_TYPE_CODE="
        + $("#TRADE_TYPE_CODE").val() + "&SERIAL_NUMBER=" + $("#AUTH_SERIAL_NUMBER").val();
    if ('133' == $("#TRADE_TYPE_CODE").val()) {
        $.beginPageLoading("业务资料查询。。。");
        $.ajax.submit(this, 'loadChildInfo', param, '', function (data) {
            var isWideUser = data.get('IS_WIDE_USER'); // 是否是宽带用户
            $("#IS_OPEN_WIDE").val("");
            if ('Y' == isWideUser) { //是否是宽带用户
                $("#openWidePart").css('display', '');
            } else {
                $("#openWidePart").css('display', 'none');
            }
            var isIMSUser = data.get('IS_IMS_USER');
            $("#IS_OPEN_IMS").val("");
            if ('Y' == isIMSUser) {
                $("#openIMSPart").css('display', ''); // 显示
            } else {
                $("#openIMSPart").css('display', 'none'); // 隐藏
            }
            $.endPageLoading();
            
            //REQ201812130016 家庭IMS固话补充需求
            if('Y'==data.get('IMS_STOP')){
            	MessageBox.alert("提示", "是否对家庭IMS固话同时开机？如是，请单独前往家庭IMS固话报开界面操作。");
            }
        }, function (error_code, error_info) {
            $.endPageLoading();
            $.MessageBox.error(error_code, error_info);
        });
    }
}

//开机选择关联开宽带时
function selectOpenWide() {
    var param = "&ROUTE_EPARCHY_CODE=" + $.auth.getAuthData().get("USER_INFO").get("EPARCHY_CODE") + "&SERIAL_NUMBER="
        + $("#AUTH_SERIAL_NUMBER").val() + "&USER_ID=" + $.auth.getAuthData().get("USER_INFO").get("USER_ID");
    if ('Y' == $("#IS_OPEN_WIDE").val()) { // 选择关联开机的时候才需要校验
        $.beginPageLoading("业务校验中。。。");
        $.ajax.submit(this, 'checkOpenWide', param, '', function (data) {
            if ('-1' == data.get('RESULT_CODE')) {
                MessageBox.alert("告警提示", data.get('RESULT_INFO'));
                $("#IS_OPEN_WIDE").val('N');
            }
            $.endPageLoading();
        }, function (error_code, error_info) {
            $.endPageLoading();
            $.MessageBox.error(error_code, error_info);
        });
    }
}

//开机时选择关联开IMS时
function selectOpenIMS() {
    var param = "&ROUTE_EPARCHY_CODE=" + $.auth.getAuthData().get("USER_INFO").get("EPARCHY_CODE") + "&SERIAL_NUMBER="
        + $("#AUTH_SERIAL_NUMBER").val() + "&USER_ID=" + $.auth.getAuthData().get("USER_INFO").get("USER_ID");
    if ('Y' == $("#IS_OPEN_IMS").val()) {
        $.beginPageLoading("业务校验中。。。");
        $.ajax.submit(this, 'checkOpenIMS', param, '', function (data) {
            if ('-1' == data.get('RESULT_CODE')) {
                MessageBox.alert("告警提示", data.get('RESULT_INFO'));
                $("#IS_OPEN_IMS").val('N');
            }
            $.endPageLoading();
        }, function (error_code, error_info) {
            $.endPageLoading();
            $.MessageBox.error(error_code, error_info);
        });
    }
}

// 提交组件提交前校验
function onTradeSubmit() {
    var param = "&SERIAL_NUMBER=" + $.auth.getAuthData().get("USER_INFO").get("SERIAL_NUMBER");
    $.cssubmit.addParam(param);
    return true;
}
