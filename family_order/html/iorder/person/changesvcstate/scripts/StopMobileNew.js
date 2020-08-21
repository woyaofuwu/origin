/**
 * Auth组件输入号码认证后回调函数
 *
 * @param data
 * @returns
 */
function refreshPartAtferAuth(data) {
    var param = "&ROUTE_EPARCHY_CODE=" + data.get("USER_INFO").get("EPARCHY_CODE") + "&TRADE_TYPE_CODE="
        + $("#TRADE_TYPE_CODE").val() + "&SERIAL_NUMBER=" + $("#AUTH_SERIAL_NUMBER").val();
    if ('131' == $("#TRADE_TYPE_CODE").val()) {
        $.beginPageLoading("业务资料查询。。。");
        $.ajax.submit(this, 'loadChildInfo', param, '', function (data) {
            var isWideUser = data.get('IS_WIDE_USER'); // 是否是宽带用户
            $("#IS_STOP_WIDE").val("");
            if ('Y' == isWideUser) {
                $("#stopWidePart").css('display', '');
            } else {
                $("#stopWidePart").css('display', 'none');
            }
            var isMinCharge = data.get('IS_MIN_CHARGE');
            if ('1' == isMinCharge) {
                MessageBox.alert("提示消息", "若在约定最低消费类的营销活动生效期间办理报停，即使报停满一个计费月，仍按约定最低话费额收取。", function (btn) {});
            }
            $.endPageLoading();
        }, function (error_code, error_info) {
            $.endPageLoading();
            $.MessageBox.error(error_code, error_info);
        });
    }
}

// 停机选择关联停宽带时
function selectStopWide() {
    var param = "&ROUTE_EPARCHY_CODE=" + $.auth.getAuthData().get("USER_INFO").get("EPARCHY_CODE") + "&SERIAL_NUMBER="
        + $("#AUTH_SERIAL_NUMBER").val() + "&USER_ID=" + $.auth.getAuthData().get("USER_INFO").get("USER_ID");
    if ('Y' == $("#IS_STOP_WIDE").val()) { // 选择关联停机的时候才需要校验
        $.beginPageLoading("业务校验中。。。");
        $.ajax.submit(this, 'checkStopWide', param, '', function (data) {
            if ('-1' == data.get('RESULT_CODE')) {
                MessageBox.alert("告警提示", "宽带用户有包年套餐不能关联停机！");
                $("#IS_STOP_WIDE").val('N');
            }
            if ('-2' == data.get('RESULT_CODE'))
            {
                MessageBox.alert("告警提示","宽带用户有候鸟短期套餐不能关联停机！");
                $("#IS_STOP_WIDE").val('N');
            }
            //add by zhangxing3 for REQ201807310009有手机号码候鸟宽带资费
			if ('-3' == data.get('RESULT_CODE'))
			{
				MessageBox.alert("告警提示","宽带用户有度假宽带活动不能关联停机！");
				$("#IS_STOP_WIDE").val('N');
			}
			//add by zhangxing3 for REQ201807310009有手机号码候鸟宽带资费
            $.endPageLoading();
        }, function (error_code, error_info) {
            $.endPageLoading();
            $.MessageBox.error(error_code, error_info);
        });
    }
}

// 提交组件提交前校验
function onTradeSubmit() {
    var tradeTypeCode = $('#TRADE_TYPE_CODE').val();
    if ('131' == $("#TRADE_TYPE_CODE").val()) {
        var param = "&SERIAL_NUMBER=" + $.auth.getAuthData().get("USER_INFO").get("SERIAL_NUMBER");
        $.cssubmit.addParam(param);
    }
    return true;
}
