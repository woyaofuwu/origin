function changePayMode() {
    var PRODUCT_PACKAGE_ID = $("#PRODUCT_PACKAGE_ID").val();
    var CAMPN_TYPE = PRODUCT_PACKAGE_ID.split('-')[0];
    var PRODUCT_ID = PRODUCT_PACKAGE_ID.split('-')[1];
    var PACKAGE_ID = PRODUCT_PACKAGE_ID.split('-')[2];
    $.beginPageLoading("配置费用校验。。。");
    var param = "&PRODUCT_ID="+PRODUCT_ID+"&PACKAGE_ID="+PACKAGE_ID;
    $.ajax.submit(null,'checkIsNeedPay',param,'',function(data){
            if (data.get("IS_NEED_PAY") == 1)
            {
                $("#PAY_MODE").attr("disabled",false);
            } else
            {
                $("#PAY_MODE").attr("disabled",true);
                $("#PAY_MODE").val("");
            }
            $.endPageLoading();
        },
        function(error_code,error_info,derror){
            $.endPageLoading();
            showDetailErrorInfo(error_code,error_info,derror);
        });
}