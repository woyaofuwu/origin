function afterSubmitSerialNumber(data)
{	
	var packageId = $("#SALE_PACKAGE_ID").val();
	var productId = $("#SALE_PRODUCT_ID").val();
	var campnType = $("#SALE_CAMPN_TYPE").val();
	var newImei = $("#NEW_IMEI").val();
	var deviceModelCode = $("#DEVICE_MODE_CODE").val();
	var relOfferCode = $("#REL_OFFER_ID").val();
	marketActivityModule.readerComponent(packageId, productId, campnType, newImei, deviceModelCode, relOfferCode);
}


function onTradeSubmit()
{
	if (!$.validate.verifyAll('elementPanel'))
		return false;
	if (!marketActivityModule.saleactiveSubmitJSCheck())
		return false;
	var saleactiveData = marketActivityModule.getSaleActiveSubmitData();
	var param = '&SERIAL_NUMBER=' + $("#SALE_SERIAL_NUMBER").val();
	param += '&SALEACITVEDATA=' + saleactiveData.toString();
	param += "&SMS_VERI_SUCCESS=" + $("#SMS_VERI_SUCCESS").val()
	var netOrderId = $("#NET_ORDER_ID").val();
	if (typeof(netOrderId) != "undefined" && "" != netOrderId) {
		param += '&NET_ORDER_ID=' + netOrderId;
	}
    // 无线固话和铁通营销活动传ORDER_TYPE_CODE
    var tradeTypeCode = $("#TRADE_TYPE_CODE").val();
    if (typeof tradeTypeCode !== "undefined" && tradeTypeCode !== null
            && (tradeTypeCode === "3814" || tradeTypeCode === "3815")) {
        params += "&ORDER_TYPE_CODE=" + tradeTypeCode;
    }
	$.cssubmit.addParam(param);
		
	var checkParam = "";
	checkParam += '&PACKAGE_ID_A=' + saleactiveData.get('PACKAGE_ID');
	checkParam += '&PRODUCT_ID_A=' + saleactiveData.get('PRODUCT_ID');
	if (saleactiveData.get('OTHER_NUMBER', '') != '') {
		checkParam += '&OTHER_NUMBER=' + saleactiveData.get('OTHER_NUMBER', '');
	}
	$.tradeCheck.addParam(checkParam);
	return true;
}

function  checkSMSButton(){
    var saleActiveData = marketActivityModule.getSaleActiveSubmitData();
    var serialNumber = $("#AUTH_SERIAL_NUMBER").val();
    var userId = $("#SALEACTIVE_USER_ID").val();
    var eparchyCode = $("#" + $("#SALEACTIVEMODULE_EPARCHY_CODE_COMPID").val()).val();
    var packageId = saleActiveData.get("PACKAGE_ID");
    var productId = saleActiveData.get("PRODUCT_ID");
    var limitCount = $("#LIMIT_COUNT").val();
    var noticeContent = $("#NOTICE_CONTENT").val();
    var smsCodeFlag = $("#CHECK_SMS_VERICODE").val();
    
    var checkData = $.DataMap();
    checkData.put("SERIAL_NUMBER", serialNumber);
    checkData.put("USER_ID", userId);
    checkData.put("EPARCHY_CODE", eparchyCode);
    checkData.put("LIMIT_COUNT", limitCount);
    checkData.put("SMS_VERI_CODE_TYPE", smsCodeFlag);
    checkData.put("NOTICE_CONTENT", noticeContent);
    
    if (smsCodeFlag === "2") {
        MessageBox.confirm("确定要校验验证码吗?", null,
            function (btn) {
                if (btn === "ok") {
                	marketActivityModule.doSMSVeriCode(packageId, productId, checkData);
                } else {
                   // $("#SubmitPart").css("display", "");
                	$("#MerchSubmitPart").css("display", "");
                    $("#checkSMSBtn").css("display", "none");
                }
            });
    } else if (smsCodeFlag === "1") {
    	marketActivityModule.doSMSVeriCode(packageId, productId, checkData);
    }
}

function  redPackPlaceOrderCall(){
    var redPackVal = $("#RED_PACK_VALUE").val();
    var deviceCost = $("#DEVICE_COST").val();
    if (redPackVal !== null && redPackVal > 0) {
        var saleActiveData = marketActivityModule.getSaleActiveSubmitData();
        var outParams = "&SERIAL_NUMBER=" + $("#AUTH_SERIAL_NUMBER").val()
                + "&PACKAGE_ID=" + saleActiveData.get("PACKAGE_ID")
                + "&PRODUCT_ID=" + saleActiveData.get("PRODUCT_ID")
                + "&CAMPN_TYPE=" + saleActiveData.get("CAMPN_TYPE")
                + "&RES_NO=" + saleActiveData.get("SALEGOODS_IMEI")
                + "&AMT_VAL=" + redPackVal + "&EPARCHY_CODE=" + "0898";
        // 调和包平台接口发送验证码（下订单）
        var finalFee = deviceCost - redPackVal;
        MessageBox.confirm("购机款为（" + deviceCost/100 + "）元，"
                + "使用电子券金额为（" + redPackVal/100 + "）元，"
                + "现金支付金额为（" + finalFee/100 + "）元。", null,
            function (btn) {
               if (btn === "ok")
                   redPackPlaceOrder(outParams);
            });
    } else {
        MessageBox.alert("无红包金额！");
        afterRedPackCall();
    }
}

function  afterRedPackCall(){
    $("#CSSUBMIT_BUTTON").click();
}

function  redPackPlaceOrder(){
    var userId = $("#SALEACTIVE_USER_ID").val();
    inParams += "&USER_ID=" + userId;
    $.beginPageLoading("和包平台下发验证码。。。");
    ajaxSubmit(null, "redPackPlaceOrder", inParams, null,
        function (ajaxData) {
            $.endPageLoading();
            if (ajaxData.get("X_RESULTCODE") === "1") {
                var redOrderId = ajaxData.get("RED_ORDERID");
                var redMerId = ajaxData.get("RED_MERID");
                var serialNumber = ajaxData.get("SERIAL_NUMBER");
                var productId = ajaxData.get("PRODUCT_ID");
                var packageId = ajaxData.get("PACKAGE_ID");
                var amtVal = ajaxData.get("AMT_VAL");
                var deviceModelCode = ajaxData.get("DEVICE_MODEL_CODE");

                var popupParam = "&RED_ORDERID=" + redOrderId
                        + "&RED_MERID=" + redMerId
                        + "&USER_ID=" + userId
                        + "&SERIAL_NUMBER=" + serialNumber
                        + "&PRODUCT_ID=" + productId
                        + "&PACKAGE_ID=" + packageId
                        + "&AMT_VAL=" + amtVal
                        + "&DEVICE_MODEL_CODE=" + deviceModelCode;
                popupPage("请输入红包验证码", "saleactive.sub.RedPackAuthCodeNew", "initParams", popupParam, null, null, saleActiveTrade.afterRedPackCall);
            }
        },
        function (error_code, error_info) {
            $.endPageLoading();
            MessageBox.alert(error_info);
        });	
}

