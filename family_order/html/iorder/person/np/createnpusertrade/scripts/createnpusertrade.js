$(document).ready(function () {
	
    $("#SERIAL_NUMBER").unbind().bind("keypress", function (event) {
        // 回车事件
        if (event.keyCode == 13 || event.keyCode == 108) {
            checkMphone(1);
        }
    });

    $("#CHECK_SN_BTN").tap(checkMphone);

    $("#CHECK_SIM_BTN").tap(checkSimCardNo);

    $("#HOME_OPERATOR").bind("change", setNetWorkType);

    $.custInfo.setRealName(true); // 设置启用实名制
    $("#IS_REAL_NAME").attr("checked", $.custInfo.isRealName).attr("disabled", $.custInfo.isRealName);

//    $("#NP_BACK").tap(checkSerialNumber);

    $("#SHOW_HOT_RECOMM_AREA,#HIDE_HOT_RECOMM_AREA").tap(function () {
        var $hotRecommArea = $("#HOT_RECOMM_AREA");
        if ($hotRecommArea.hasClass("e_hide-x")) {
            $hotRecommArea.removeClass("e_hide-x");
        } else {
            $hotRecommArea.addClass("e_hide-x");
        }
        $(this).css("display", "none").siblings().css("display", "");
    });
    
    $("#PSPT_ID").bind("change", function(){
    	changeAuthCodeExpired();
	});
	

    $.acctInfo.delWidget(7);  // 银行协议号去掉
    $.acctInfo.hideWidget(3); // 隐藏用户结账日
});

/*$Id:$*/
var PAGE_FEE_LIST = $.DataMap();

function setNetWorkType() {
    var homeOperator = $("#HOME_OPERATOR").val();
    // 归属运营商：001-中国电信 002-中国移动 003-中国联通
    // CDMA=1 CDMA2000=2 GSM=3 TD-SCDMA=4
    if (homeOperator === "001") {
        $("#NETWORK_TYPE").val(1);
    }
    if (homeOperator === "003") {
        $("#NETWORK_TYPE").val(3);
    }
}

// 读卡前的动作
function beforeReadCard() {
    $.simcard.setSerialNumber($("#SERIAL_NUMBER").val());
    return true;
}

// 读卡后的动作
function afterReadCard(data) {
    $("#SIM_CARD_NO").val(data.get("SIM_CARD_NO"));
    if (data.get("IS_WRITED") === "1") { // 用来判断卡是否被写过
        resetSIMCheck();
        checkSimCardNo(data);
    }
}

function beforeWriteCard() {
    $.simcard.setSerialNumber($("#SERIAL_NUMBER").val());
    return true;
}

// 写卡之后的动作
function afterWriteCard(data) {
    if (data.get("RESULT_CODE") === "0") {
        $.simcard.readSimCard();
    }
}

// 页面关闭时调用释放资源流程
function onClose() {

} 
var tableCacheData = new $.DataMap();

// 校验网络类型
function checkNetWorkType() {
    var homeOperator = $("#HOME_OPERATOR").val(); // 归属运营商：001-中国电信 002-中国移动 003-中国联通
    var networkType = $("#NETWORK_TYPE").val(); // CDMA=1 CDMA2000=2 GSM=3 TD-SCDMA=4

    if (homeOperator === "001" && (networkType !== "1" && networkType !== "2")) {
        MessageBox.alert("提示", "中国电信运商网络只能为 CDMA 或 CDMA2000！",
            function () {
                $("#NETWORK_TYPE").val("");
            });
    }
    if (homeOperator === "003" && networkType !== "3") {
        MessageBox.alert("提示", "中国联通运商网络只能为 GSM 或 WCDMA！",
            function () {
                $("#NETWORK_TYPE").val("");
            });
    }
}

// 重置手机号码校验
function resetSNCheck() {
    $("#CHECK_SN_BTN").css("display", "");
    $("#SN_SUCCESS_LABEL").css("display", "none");
    $("#SN_ERROR_LABEL").css("display", "none");
    $("#CHECK_SIM_BTN").css("display", "");
    $("#SIM_SUCCESS_LABEL").css("display", "none");
    $("#SIM_ERROR_LABEL").css("display", "none");
}

// 校验手机号码失败
function checkSerialNumberFailed() {
    $("#CHECK_SN_BTN").css("display", "none");
    $("#SN_ERROR_LABEL").css("display", "");
}

// 校验手机号码成功
function checkSerialNumberSucceed() {
    $("#CHECK_SN_BTN").css("display", "none");
    $("#SN_SUCCESS_LABEL").css("display", "");
    $.custInfo.setDefaultValueAfterSerialNumberCheck();
}

// 检查号码，查询产品类型
function checkMphone() {
    var serialNumber = $("#SERIAL_NUMBER").val();
    var len = serialNumber.length;
    if (len !== 11 || !/^[0-9]+$/.test(serialNumber)) {
        MessageBox.alert("输入的手机号码不对，请重新输入！");
        return false;
    }

    var $npBack = $("#NP_BACK");
    var reg = /(^14[5|7]\d{8})|(^13[4|5|6|7|8|9]\d{8})|(^15[0|1|2|7|8|9]\d{8})|(^18[2|3|7|8]\d{8})$/;
    if (reg.test(serialNumber)) {
        $npBack.attr("disabled", false);
    } else {
        $npBack.attr("checked", false).attr("disabled", true);
    }

    var npBack = "0";
    if ($npBack.attr("checked")) {
        npBack = "1";
    }
    var key = serialNumber + "_isChecked_" + npBack;
    if (tableCacheData.get(key) !== "1") {
        checkSerialNumber();
    } else {
        checkSerialNumberSucceed();
    }
}

// 向后台发起请求较验号码
function checkSerialNumber() {
    var serialNumber = $("#SERIAL_NUMBER").val();
    if (serialNumber === "") {
        return false;
    }
    var npBack = "0";
    if ($("#NP_BACK").attr("checked")) {
        npBack = "1";
    }

    $("#NETWORK_TYPE").val("");
    $("#HOME_OPERATOR").val("");
    $.beginPageLoading("号码校验中......");
    var param = "&SERIAL_NUMBER=" + serialNumber + "&NP_BACK=" + npBack;
    $.ajax.submit(null, "checkSerialNumber", param, "CheckSerialNumberHidePart,ProductTypePart,TradeInfoHidePart,OtherInfoHidePart,SalePackage",
        function (ajaxData) {
            // 绑定"更多产品"和"变更"按钮点击事件
            $("#SELECT_PRODUCT_BTN,#CHANGE_PRODUCT_BTN").tap(checkBeforeSelectProduct);
            $("#HIDE_HOT_RECOMM_AREA").css("display", "");

            var rObj = ajaxData.get(0).get("checkSerialNumber");
            var message = rObj.get("MESSAGE");
            if (message !== "" && typeof message !== "undefined") {
                // (7)系统读取当前未完工的携入申请工单数，如果超过系统设置值N，返回一个提示给用户
                MessageBox.alert("提示", message);
            }
            $.custInfo.setSerialNumber($("#SERIAL_NUMBER").val());
            var key = serialNumber + "_isChecked_" + npBack;
            tableCacheData.put(key, "1"); // 1-较验通过

            // 校验完后界面部分数据处理--预存费用
            setAjaxAtferCheckMphone(rObj);
            $.endPageLoading();


            // 携号转网背景下吉祥号码业务规则优化需求（上） by mengqx
            var salePackageObj = $("#SalePackage");
            var xCoding = rObj.get("A_X_CODING_STR");
            if (xCoding) {
                salePackageObj.css("display", "");
                var saleProductId = xCoding.split("|")[0];
                var salePackageId = xCoding.split("|")[1];
                var salePackageName = xCoding.split("|")[2];

                var miniCharge = "0";
                if(salePackageName && salePackageName.indexOf("月约定消费") >= 0){
                    miniCharge = salePackageName.substring(salePackageName.indexOf("月约定消费")+5,salePackageName.length-2);
                }
                MessageBox.alert("提示", "尊敬的客户，您的号码为吉祥号码，携入新开户按现有吉祥号码营销方案执行，" +
                    "承诺在网使用海南移动通信服务5年，从活动生效当月起，三年内约定每月最低消费" + miniCharge + "元（最低" +
                    "消费含套餐月使用费、通话费及其他费用，不含信息费、宽带费等费用）， 若约定期内月实际消费低于约定的月最低" +
                    "消费，将按约定的月最低消费收取费用；三年内不能办理主动销号和过户业务。");


                $("#SALE_PRODUCT_ID").val(saleProductId);
                $("#SALE_PACKAGE_ID").val(salePackageId);
                $("#BIND_SALE_TAG").val("1"); // 绑定营销活动

                var checkboxes = $("#packageTable input[name=packageCheckbox]"); // 获取表格全部复选框
                checkboxes.each(function () {
                    if (this.value === salePackageId) {
                        this.click();
                        $(this).parent().parent().addClass("on");
                    }
                });

                // 无更改权限时隐藏其他可选包并设置营销包必选 wenhj
                if ($("#SYSCHANGPACKAGE").val() !== "1") {
                    checkboxes.each(function () {
                        if (this.value !== salePackageId)
                            $(this).parent().parent().css("display", "none");
                        else if (rObj.get("SYSCHANGPACKAGE4") !== "1")
                            $(this).parent().parent().attr("disabled", true);
                    });
                }
            } else {
                $("#SALE_PRODUCT_ID").val("");
                $("#SALE_PACKAGE_ID").val("");
                $("#BIND_SALE_TAG").val("0");
                salePackageObj.css("display", "none");
            }
            //end 携号转网背景下吉祥号码业务规则优化需求（上） by mengqx

            /**
             * REQ201602290007 关于入网业务人证一致性核验提醒的需求
             * chenxy3 2016-03-08
             * */
            $.beginPageLoading("正在查询数据......");
            $.ajax.submit(null, "checkNeedBeforeCheck", null, null,
                function (ajaxData) {
                    var flag = ajaxData.get("PARA_CODE1");
                    if (flag === "1") {
                        var param = "&TRADE_ID=10" + "&EPARCHY_CODE=0898"
                            + "&TRADE_TYPE_CODE=40" + "&RAN=" + Math.random(); // RAN参数保证每次弹窗都刷新页面内容;
                        popupPage("业务检查", "beforecheck.BeforeCheckNew", "init", param, null, "full");
                    }
                    $.endPageLoading();
                },
                function (error_code, error_info) {
                    $.endPageLoading();
                    MessageBox.error(error_info);
                });
        },
        function (error_code, error_info) {
            $.endPageLoading();
            checkSerialNumberFailed();
            MessageBox.error(error_info);
        });
}

// 重置SIM卡校验
function resetSIMCheck() {
    $("#CHECK_SIM_BTN").css("display", "");
    $("#SIM_SUCCESS_LABEL").css("display", "none");
    $("#SIM_ERROR_LABEL").css("display", "none");
}

// 校验SIM卡失败
function checkSIMFailed() {
    $("#CHECK_SIM_BTN").css("display", "none");
    $("#SIM_ERROR_LABEL").css("display", "");
}

// 校验SIM卡成功
function checkSIMSucceed() {
    $("#CHECK_SIM_BTN").css("display", "none");
    $("#SIM_SUCCESS_LABEL").css("display", "");
}

// 校验SIM卡
function checkSimCardNo(data) {
    var simCardNo = $("#SIM_CARD_NO").val();
    if (simCardNo === "") {
        MessageBox.alert("SIM卡号不能为空！");
        return false;
    }
    if (simCardNo.length < 20) {
        MessageBox.alert("输入的SIM卡长度不正确，请重新输入！");
        return false;
    }

    var eparchyCode = $("#EPARCHY_CODE").val();
    var serialNumber = $("#SERIAL_NUMBER").val();
    var npBack = "0";
    if ($("#NP_BACK").attr("checked")) {
        npBack = "1";
    }

    var key = serialNumber + "_isChecked_" + npBack;
    if (tableCacheData.get(key) !== "1") {
        MessageBox.alert("号码校验未通过,请选校验号码！");
        return false;
    }
    var simKey = simCardNo + "_isChecked";
    if (tableCacheData.get(simKey) === "1") {
        MessageBox.alert("sim卡[" + simCardNo + "]校验已通过！");
        checkSIMSucceed();
        return false;
    }

    var params = "&SIM_CARD_NO=" + simCardNo
        + "&ROUTE_EPARCHY_CODE=" + eparchyCode
        + "&SERIAL_NUMBER=" + serialNumber + "&EMPTY_CARD_ID=";
    $.beginPageLoading("SIM卡校验中......");
    $.ajax.submit(null, "checkSimCardNo", params, null,
        function (ajaxData) {
            if (ajaxData) {
                var rtnData = ajaxData.get(0);
                var key = simCardNo + "_isChecked";
                tableCacheData.put(key, "1"); // 1-较验通过
                tableCacheData.put(simCardNo, rtnData.get("RES_INFO_DATA"));
                checkSIMSucceed();
                // MessageBox.success("SIM卡校验通过！");
                var feeMode = rtnData.get("FEE_MODE");
                var feeTypeCode = rtnData.get("FEE_TYPE_CODE");
                var fee = rtnData.get("FEE");
                if (fee != null && fee !== "") {
                    var feeData = new $.DataMap();
                    feeData.put("TRADE_TYPE_CODE", "40");
                    feeData.put("MODE", feeMode);
                    feeData.put("CODE", feeTypeCode);
                    feeData.put("FEE", fee);
                    $.feeMgr.removeFee("40", feeMode, feeTypeCode);
                    $.feeMgr.insertFee(feeData);
                    tableCacheData.put("SIMCARD_FEE", feeData);
                }
            }
            $.endPageLoading();
        },
        function (error_code, error_info, derror) {
            $.endPageLoading();
            checkSIMFailed();
            showDetailErrorInfo(error_code, error_info, derror);
        });
}

function checkBeforeSelectProduct(el) {
    var $el = $(el);
    if ($el.attr("productId")) { // 热门或推荐产品
        var productId = $el.attr("productId"),
            productName = $el.attr("productName"),
            productDesc = $el.attr("title"),
            brandCode = $el.attr("brandCode");
        afterChangeProduct(productId, productName, productDesc, brandCode);
    } else {
        ProductSelect.popupProductSelect($("#PRODUCT_TYPE_CODE").val(), $("#EPARCHY_CODE").val(), "");
    }
}

function checkAuthCode() {
	var authCode = $("#AUTH_CODE").val();
	var reg =/^([0-9]{6})$/;
    if (!reg.test(authCode)) {
    	MessageBox.alert("提示","授权码为0-9的6位数字");
		$("#AUTH_CODE").val("");
		return;
    }
}

// 选完产品后的动作
function afterChangeProduct(productId, productName, productDesc, brandCode) {
    $("#PRODUCT_ID").val(productId);
    $("#PRODUCT_NAME").val(productName);
    $("#PRODUCT_DESC").html(productDesc).attr("title", productDesc);
    $("#SELECT_PRODUCT_BTN").attr("disabled", true).css("display", "none");  // 隐藏"产品目录"按钮
    $("#CHANGE_PRODUCT_BTN").attr("disabled", false).css("display", "");     // 展示"变更"按钮
    $("#PRODUCT_DISPLAY").css("display", "");                                // 展示已选产品
    $("#PRODUCT_COMPONENT_DISPLAY").css("display", "");                      // 展示"待选区"和"已选区"组件

    var eparchyCode = $("#EPARCHY_CODE").val();
    var param = "&NEW_PRODUCT_ID=" + productId;
    offerList.renderComponent(productId, eparchyCode);
    selectedElements.renderComponent(param, eparchyCode);

    $.cssubmit.disabledSubmitBtn(false);
    // $.feeMgr.clearFeeList("40");
    $.feeMgr.insertFee(tableCacheData.get("SIMCARD_FEE"));

    var inParam = "&PRODUCT_ID=" + productId + "&BRAND_CODE=" + brandCode
            + "&EPARCHY_CODE=" + eparchyCode;
    $.ajax.submit(null, "getProductFeeInfo", inParam, null,
        function () {
            $.cssubmit.disabledSubmitBtn(false);
            /* 应乃捷要求，携入申请开户不需要费用
            for (var i = 0; i < ajaxData.getCount(); i++) {
                var data = ajaxData.get(i);
                if (data) {
                    feeData.clear();
                    feeData.put("MODE", data0.get("FEE_MODE"));
                    feeData.put("CODE", data0.get("FEE_TYPE_CODE"));
                    feeData.put("FEE",  data0.get("FEE"));
                    feeData.put("PAY",  data0.get("FEE"));
                    feeData.put("TRADE_TYPE_CODE","40");
                    $.feeMgr.insertFee(feeData);
                }
            }*/
        },
        function (error_code, error_info, derror) {
            $.endPageLoading();
            showDetailErrorInfo(error_code, error_info, derror);
        });
    //海南添加，选择产品弹出提示
    if(typeof(createPersonUserExtend) != "undefined" && createPersonUserExtend.afterChangeProduct)
    {
        createPersonUserExtend.afterChangeProduct(inParam);
    }

}

// 初始化产品产
function initProduct() {
	var eparchyCode = $("#EPARCHY_CODE").val();
	offerList.renderComponent("", eparchyCode);
	selectedElements.renderComponent("&NEW_PRODUCT_ID=", eparchyCode);
    $("#SELECT_PRODUCT_BTN").attr("disabled", false).css("display", "");
    $("#PRODUCT_ID").val("");
    $("#PRODUCT_NAME").html("");
    $("#PRODUCT_DESC").html("");
    $("#PRODUCT_DISPLAY").css("display", "none");
    $("#PRODUCT_COMPONENT_DISPLAY").css("display", "none");
}

function disableElements(data) {
    if ($("#B_REOPEN_TAG").val() === '1') {
        selectedElements.disableAll();
    } else {
        if (data) {
            var newProductStartDate = data.get(0).get("NEW_PRODUCT_START_DATE");
            if (newProductStartDate) {
                $("#NEW_PRODUCT_START_DATE").val(newProductStartDate);
            }
        }
    }
}

// 检查同一证件号已开实名制用户的数量是否已超出预定值
function checkRealNameLimitByPspt() {
    var custName = $("#CUST_NAME").val();
    var psptId = $("#PSPT_ID").val();
    if (custName === "" || psptId === "") {
        return false;
    }
    var params = "&custName=" + custName + "&psptId=" + psptId
            + "&EPARCHY_CODE=" + $("#EPARCHY_CODE").val();
    $.beginPageLoading("新开户实名制校验中......");
    $.ajax.submit(null, "checkRealNameLimitByPspt", params, null,
        function (ajaxData) {
            $.endPageLoading();
            if (ajaxData.get(0).get("CODE") === "0") {
                return true;
            } else {
                MessageBox.alert(ajaxData.get(0).get("MSG"));
                return false;
            }
        },
        function (error_code, error_info, derror) {
            $.endPageLoading();
            showDetailErrorInfo(error_code, error_info, derror);
        });
    return true;
}

function onSubmit() {
    var $phone = $("#PHONE");
    if ($phone.val().length >= 15) {
        MessageBox.alert("联系电话不能大于等于15位！");
        $phone.focus();
        return false;
    }
    var serialNumber = $("#SERIAL_NUMBER").val();
    if (!$.validate.verifyAll()) {
        return false;
    }
    var isRealName = "1";
    if (!$.custInfo.isRealName) {
        isRealName = "0";
    }
    var npBack = "0";
    if ($("#NP_BACK").attr("checked")) {
        npBack = "1";
    }
    var key = serialNumber + "_isChecked_" + npBack;
    if (tableCacheData.get(key) !== "1") {
        MessageBox.alert("号码校验未通过,请选校验号码！");
        return false;
    }
    var simCardNo = $("#SIM_CARD_NO").val();
    var simKey = simCardNo + "_isChecked";
    if (tableCacheData.get(simKey) !== "1") {
        MessageBox.alert("SIM卡校验未通过,请选校验SIM卡！");
        return false;
    }
    if ($("#PRODUCT_NAME").val() === "") {
        MessageBox.alert("请选择产品！");
        return false;
    }
    if ($("#HOME_OPERATOR").val() === "") {
        MessageBox.alert("归属运营商不能为空！");
        return false;
    }
    if ($("#NETWORK_TYPE").val() === "") {
        MessageBox.alert("网络类型不能为空！");
        return false;
    }
    if ($("#AUTH_CODE").val() === "") {
        MessageBox.alert("授权码不能为空！");
        return false;
    }
    if ($("#AUTH_CODE_EXPIRED").val() === "") {
        MessageBox.alert("授权码日期不能为空！");
        return false;
    }

    var canSubmit = selectedElements.checkForcePackage();
    if (!canSubmit) {
        return false;
    }
    var resStr = tableCacheData.get(simCardNo).toString();
    var submitData = selectedElements.getSubmitData();
    var param = "&SELECTED_ELEMENTS=" + submitData.toString()
            + "&PRODUCT_ID=" + $("#PRODUCT_ID").val()
            + "&INVOICE_NO=" + $("#_INVOICE_CODE").val()
            + "&IS_REAL_NAME=" + isRealName + "&NP_BACK=" + npBack
            + "&RES_INFO_DATA=" + resStr;
    $.cssubmit.addParam(param);
    return true;
}

// 备注信息特殊字符校验
function checkRemark() {
    var remark = $("#REMARK").val();
    if (remark === "") {
        return true;
    }

    var pattern = new RegExp("[`~!@#$^&*()=|{}':;',\\[\\].<>/?~!@#￥……&*()——|{}【】‘;:”“'。,、?]");
    if (pattern.test(remark)) {
        MessageBox.alert("备注信息不充许包含特殊字符！！！");
        return false;
    }
    return true;
}

// 密码组件前校验
function passwdBeforeAction() {
    if ($("#PSPT_TYPE_CODE").val() === "") {
        MessageBox.alert("证件类型不能为空！");
        return false;
    }
    var psptId = $("#PSPT_ID").val();
    if (psptId === "") {
        MessageBox.alert("证件号码不能为空！");
        return false;
    }
    // 将值赋给组件处理
    $.password.setPasswordAttr(psptId, $("#SERIAL_NUMBER").val());
    return true;
}

// 密码组件后赋值
function passwdAfterAction(data) {
    $("#USER_PASSWD").val(data.get("NEW_PASSWORD"));
}

// 变更营销包权限判断 by mengqx
function chooseSaleActivePackage(o) {
    if ($("#SYSCHANGPACKAGE").val() === "1") {
        if (o.checked) {
            $("#SALE_PACKAGE_ID").val(o.value);
            $("#BIND_SALE_TAG").val("1");
        }
    }

    if ($("input[name=packageCheckbox]:checked").length === 0) {
        $("#SALE_PACKAGE_ID").val("");
        $("#BIND_SALE_TAG").val("0");
    }
}


/**
 * @panyu5
 * @授权码改为必填
 * @2018/11/29
 */
/*function changeAuthTag(){
	var authTag = $("#AUTH_TAG").val();
	if(authTag == "1"){
		$("#AUTH_AREA").css("display","");
		$('#AUTH_CODE').attr("nullable","no");
		$("#AUTH_EXPIRED_AREA").css("display","");
		$('#AUTH_CODE_EXPIRED').attr("nullable","no");
	}else{
		$("#AUTH_AREA").css("display","none");
		$('#AUTH_CODE').attr("nullable","yes");
		$("#AUTH_EXPIRED_AREA").css("display","none");
		$('#AUTH_CODE_EXPIRED').attr("nullable","yes");
	}	
}*/
function changeAuthCodeExpired(){
	var authCode = $("#AUTH_CODE").val();
	var authCodeExpired = $("#AUTH_CODE_EXPIRED").val();
	if(authCode == ''){
		MessageBox.alert("提示","请先输入授权码");
		$("#AUTH_CODE_EXPIRED").val("");
		return;
	}

	var currentDate = new Date().format("yyyy-MM-dd HH:mm");
    if(authCodeExpired<currentDate) {
        MessageBox.alert("您选择的授权码有效期不能小于当前日期！");
        $("#AUTH_CODE_EXPIRED").val("");
        return;
   }
    
    var psptId = $("#PSPT_ID").val();
	var psptType = $("#PSPT_TYPE_CODE").val();
	if(psptId == '' || psptType == ''){
		//空值时不做处理
		return;
	}

	var params = "&AUTH_CODE=" + authCode
    + "&SERIAL_NUMBER=" + $("#SERIAL_NUMBER").val()
    + "&AUTH_CODE_EXPIRED=" + authCodeExpired+"&PSPT_ID=" + psptId+"&PSPT_TYPE_CODE=" + psptType;
    
	MessageBox.confirm("确认提示", "授权码/授权码有效期确认无误", function (btn) {
		if(btn=="ok")
		{
			$.beginPageLoading("携转申请查验处理中......");
			$.ajax.submit(null, "checkBeforeNpUser", params, null,
					function (ajaxData) {
				$.endPageLoading();
				if (ajaxData.get("RESULT_CODE") == "0") {
					MessageBox.success("携转申请查验成功！");
					return true;
				} else {
					MessageBox.alert(ajaxData.get("RESULT_INFO"));
					$("#PSPT_ID").val("");
					return false;
				}
			},
			function (error_code, error_info, derror) {
				$.endPageLoading();
				showDetailErrorInfo(error_code, error_info, derror);
			});
		}
		else
		{
			$("#AUTH_CODE_EXPIRED").val("");
			return false;
		}
	});
	
    
}

// 新开户号码校验完后返回值的处理
function setAjaxAtferCheckMphone(data) {
    var feeData;
    var simCardNoObj = $("#SIM_CARD_NO");
    simCardNoObj.val("898600");
    simCardNoObj.attr("disabled", false);
    var checkResultCode = data.get("CHECK_RESULT_CODE");
    $("#CHECK_RESULT_CODE").val(checkResultCode); // 设置号码校验通过
    checkSerialNumberSucceed();

    $.feeMgr.clearFeeList("40");
    if (data.get("FEE_CODE_FEE")) {
        feeData = $.DataMap();
        feeData.put("MODE",  "2");
        feeData.put("CODE", "62"); // 选号预存收入
        feeData.put("FEE", data.get("FEE_CODE_FEE"));
        feeData.put("PAY", data.get("FEE_CODE_FEE"));
        feeData.put("TRADE_TYPE_CODE", "40");
        feeData.put("SYSCHANGPACKAGE4", data.get("SYSCHANGPACKAGE4")); // REQ201608310006 关于海口分公司四级吉祥号码规则优化（二）
        $.feeMgr.insertFee(feeData);
        PAGE_FEE_LIST.put("NUMBER_FEE", $.feeMgr.cloneData(feeData));
    }

}