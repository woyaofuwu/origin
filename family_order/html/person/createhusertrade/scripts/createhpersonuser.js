function initPage() 
{
	//给账户信息设置默认值，并隐藏不需要的
	$("#PAY_MODE_CODE").val('0');
	$("#ACCT_DAY").val('1');
	
	$("#PAY_MODE_CODE").attr("disabled",true);
	$("#ACCT_DAY").attr("disabled",true);
	
	$("#SuperBankCodeLi").css('display', 'none');
	$("#BankCodeLi").css('display', 'none');
	$("#BankAcctNoLi").css('display', 'none');
	$("#bankAgreementNoLi").css('display', 'none');
}

function resetSnCheck() {
	var serialNumber = $("#SERIAL_NUMBER").val();
	var oldSerialNumber = $("#OLD_SERIAL_NUMBER").val();
	$("#SERIAL_NUMBER_INPUT").removeClass("e_elements-success");
	$("#SERIAL_NUMBER_INPUT").removeClass("e_elements-error");
	if (serialNumber == oldSerialNumber) {
		$("#CHECK_RESULT_CODE").val("1"); // 设置号码校验通过
		$("#SERIAL_NUMBER_INPUT").addClass("e_elements-success");
	} else {
		$("#CHECK_RESULT_CODE").val("0"); // 设置号码未校验
	}
}

function checkHSerialNumber() {
	
	if (!verifyAll('BaseInfoPart')) {
//		resetSnCheck();
		return false;
	}
	
	var serialNumber = $("#SERIAL_NUMBER").val();
	var oldSerialNumber = $("#OLD_SERIAL_NUMBER").val();
	
	if (null != serialNumber && '' != serialNumber && serialNumber.length < 11)
	{
		alert("异网服务号码必须为11位手机号码，请检查输入再进行校验！");
//		resetSnCheck();
		$("#SERIAL_NUMBER").focus();
		return false;
	}

	if (serialNumber == oldSerialNumber) {
		return;
	}
	
	$("#CHECK_RESULT_CODE").val("0");		//设置号码未校验
	$.custInfo.setSerialNumber(serialNumber);	//针对实名制，需要传递手机给组件

	$("#SERIAL_NUMBER_INPUT").removeClass("e_elements-success");
	$("#SERIAL_NUMBER_INPUT").removeClass("e_elements-error");
	
	$.beginPageLoading("异网号码校验中...");
	
	$.ajax.submit(this, 'checkHSerialNumber', '&SERIAL_NUMBER=' + serialNumber+'&OLD_SERIAL_NUMBER=' + oldSerialNumber, 'ProductInfoPart',
		function(data) {
			$("#CHECK_RESULT_CODE").val("1");		//设置号码校验通过
			$("#OLD_SERIAL_NUMBER").val(serialNumber);
			$("#SERIAL_NUMBER_INPUT").addClass("e_elements-success");
			$.cssubmit.disabledSubmitBtn(false);

		 	var feeData = $.DataMap();
		 	$.feeMgr.clearFeeList("7510");
			feeData.clear();
			if (data.get("FEE_MODE") && data.get("FEE_TYPE_CODE") && data.get("FEE")) {
				feeData.put("MODE", data.get("FEE_MODE"));
				feeData.put("CODE", data.get("FEE_TYPE_CODE"));
				feeData.put("FEE",  data.get("FEE"));
				feeData.put("PAY",  data.get("FEE"));		
				feeData.put("TRADE_TYPE_CODE","7510");		
				$.feeMgr.insertFee(feeData);
			}
			$.endPageLoading();
		}, 
		function(error_code, error_info, derror) {
			$.endPageLoading();
			$("#CHECK_RESULT_CODE").val("0");		//设置号码未校验
			$("#OLD_SERIAL_NUMBER").val(serialNumber);
			$("#SERIAL_NUMBER_INPUT").addClass("e_elements-error");
			showDetailErrorInfo(error_code,error_info,derror);
		}
	);
}

function changeProductType() {
	
	setBrandCode();
	
	var productType = $("#PRODUCT_TYPE_CODE").val();

	$.beginPageLoading("产品信息查询...");

	$.ajax.submit(this, 'getProductInfoByProductType', '&PRODUCT_TYPE='
			+ productType, 'productPart', function(data) {
		initProduct();
		$.endPageLoading();
	}, function(error_code, error_info) {
		$.endPageLoading();
		$.MessageBox.error(error_code, error_info);
	});
}

/** 设置用户产品品牌 */
function setBrandCode() {
	if ($("#PRODUCT_TYPE_CODE").val() != "") {
		$("#BRAND").val($("#PRODUCT_TYPE_CODE :selected").text());
		initProduct();
	} else {
		$("#BRAND").val('');
	}
}

function changeProduct() {
//	$.feeMgr.clearFeeList($("#TRADE_TYPE_CODE").val());
	var productId = $("#PRODUCT_ID").val();
	var eparchyCode = $("#EPARCHY_CODE").val();
	var param = "&NEW_PRODUCT_ID=" + productId;

	offerList.renderComponent(productId, eparchyCode);
	selectedElements.renderComponent(param, eparchyCode);
}

/** 产品初始化 */
function initProduct() {
	offerList.renderComponent("", $("#EPARCHY_CODE").val());
	selectedElements.renderComponent("&NEW_PRODUCT_ID=", $("#EPARCHY_CODE").val());
}

function checkBeforeProduct(){
	ProductSelect.popupProductSelect($('#PRODUCT_TYPE_CODE').val(),$('#EPARCHY_CODE').val(),'');
}

/* 密码组件前校验 */
function PasswdbeforeAction() {
	if($("#PSPT_TYPE_CODE").val() == "") {
		alert("证件类型不能为空！");
		return false;
	}
	if($("#PSPT_ID").val() == "") {
		alert("证件号码不能为空！");
		return false;
    }
     
    // 将值赋给组件处理
    var psptId =$("#PSPT_ID").val();
    var serialNumber = $("#SERIAL_NUMBER").val();
    $.password.setPasswordAttr(psptId, serialNumber);
    return true ;
}

/* 密码组件后赋值 */
function PasswdafterAction(data) {
	$("#USER_PASSWD").val(data.get("NEW_PASSWORD"));
}

function disableElements(data) {
	if (data) {
		var temp = data.get(0);
		if (data.get(0).get("NEW_PRODUCT_START_DATE")) {
			$("#NEW_PRODUCT_START_DATE").val(temp.get("NEW_PRODUCT_START_DATE"));
		}
	}
}

function onSubmit() {
	var checkResultCode = $("#CHECK_RESULT_CODE").val();
	
	if (checkResultCode != "1") {
		alert("异网号码开户号码校验未通过！");
		$("#SERIAL_NUMBER").focus();
		return false;
	}
	
	var checkResultCode = $("#CHECK_RESULT_CODE").val();
	if (checkResultCode != "1") {
		alert("异网号码开户号码校验未通过！");
		$("#SERIAL_NUMBER").focus();
		return false;
	}
	if(!verifyAll('BaseInfoPart'))
	{
		return false;
	}
	if(!verifyAll('CustInfoPart'))
	{
		return false;
	}
	if(!verifyAll('AcctInfoPart'))
	{
		return false;
	}
	if(!verifyAll('PasswdPart'))
	{
		return false;
	}
	if(!verifyAll('ProductInfoPart'))
	{
		return false;
	}
	// 2015-04-13 密码加密
	if($("#USER_PASSWD").val() != null && $("#USER_PASSWD").val() != "" && $("#USER_PASSWD").val().length == 6) {
		var newPwd = $("#USER_PASSWD").val(); 
		$.getScript("scripts/csserv/common/des/des.js", function(){
			var data = newPwd;
			var firstKey = "c";
			var secondKey = "x"
			var thirdKey = "y"
			$("#USER_PASSWD").val(strEnc(data, firstKey, secondKey, thirdKey) + "xxyy");
		});
	}
	
	var data = selectedElements.getSubmitData();
	if (data && data.length > 0) 
	{
		var param = "&SELECTED_ELEMENTS=" + data.toString();
		$.cssubmit.addParam(param);
	}
	else
	{
		alert('您未选择开户产品，不能提交！');
		return false;
	}
	
	var fee = $.feeMgr.findFeeInfo("7510", "2", "802");
	
	if (fee == null || fee.get("PAY") < fee.get("FEE")) {
		MessageBox.alert("告警提示","和校园异网号码开户预存款实缴金额不能低于应缴金额!");
		return false;
	}
	
	return true;
}
