function callNpVerify(data){
    console.debug("data=" + data);
    $.beginPageLoading("正在查询数据...");
    var serialNumber = data.get("USER_INFO").get("SERIAL_NUMBER");
    var custName = data.get("CUST_INFO").get("CUST_NAME");
    var param = "&SERIAL_NUMBER=" + serialNumber
        + "&CRED_NUMBER=" + data.get("CUST_INFO").get("PSPT_ID")
        + "&CUST_NAME=" + data.get("CUST_INFO").get("CUST_NAME");
    console.debug("param=" + param);
    $.ajax.submit('','callNpVerify', param, '', function(npCheckMsg)
        {
            console.log("===np=data=====" + npCheckMsg);
            var ruleParam = "&TRADE_TYPE_CODE=2888&CUST_NAME=123" + "&ACCESS_NUMBER=" + serialNumber + "&SERIAL_NUMBER=" + serialNumber + '&ERROR_SET=' + encodeURI(encodeURI(npCheckMsg));
            popupDialog('甩单规则列表', 'tpOrder.TpRuleList', 'init', ruleParam , '/order/iorder', '80em', '480px', true, null, null);
            $.endPageLoading();
        },
        function(error_code,error_info)
        {
            $.endPageLoading();
            alert(error_info);
        });
}
