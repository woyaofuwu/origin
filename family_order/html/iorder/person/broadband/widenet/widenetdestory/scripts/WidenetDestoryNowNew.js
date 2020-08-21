//用戶认证结束之后执行的js方法
function refreshPartAtferAuth(data) {
    var param = "&ROUTE_EPARCHY_CODE=" + data.get("USER_INFO").get("EPARCHY_CODE") + "&USER_ID=" + data.get("USER_INFO").get("USER_ID");
    $.beginPageLoading("业务资料查询。。。");
    $.ajax.submit('AuthPart', 'loadChildInfo', param, 'wideInfoPart,BusiInfoPart', function (data) {
            $.endPageLoading();
        },
        function (error_code, error_info) {
            $.endPageLoading();
            MessageBox.alert("提示",error_info);
        });
}

//业务提交
/*如果用户有积分，则提示先进行兑换*/
function submitBeforeCheck() {
    //退光猫选项必填校验
    if ($("#MODEM_RETUAN").val() == "") {
        MessageBox.alert("提示", "请选择是否退光猫", "", null);
        return false;
    }

    var param = "&ROUTE_EPARCHY_CODE=" + $.auth.getAuthData().get("USER_INFO").get("EPARCHY_CODE");
    $.cssubmit.addParam(param);
    return true;
}


