function refreshPartAtferAuth(data) {
    window.userId = data.get("USER_INFO").get("USER_ID");
    var param = "&CUST_ID=" + data.get("CUST_INFO").get("CUST_ID")
        + "&CUST_EPARCHY_CODE=" + data.get("CUST_INFO").get("EPARCHY_CODE")
        + "&USER_ID=" + data.get("USER_INFO").get("USER_ID")
        + "&SERIAL_NUMBER=" + data.get("USER_INFO").get("SERIAL_NUMBER")
        + "&CHECK_MODE=" + data.get("CHECK_MODE");  //认证方式
    $.beginPageLoading("加载中...");
    $.ajax.submit('', 'loadDiscntInfo', param, 'resultsPart', function () {
        $.endPageLoading();
    }, function (error_code, error_info) {
        $.endPageLoading();
        alert(error_info);
        $.cssubmit.resetTrade();//重新加载页面
    });

}

