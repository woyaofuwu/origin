function refreshGuaranteePartAtferAuth(data) {
	//清理页面数据
	clearGuaranteeInfo();
	$("#GUATANTEE_SERIAL_NUMBER").val("");
}

//清空担保人信息
function clearGuaranteeInfo() {
	$("#GUATANTEE_USER_ID").val("");
	$("#GUATANTEE_CUST_NAME").val("");
	$("#CREDIT_CLASS").val("");
	$("#OPEN_HOURS").val("");
	$("#REMARK").val("");
}

/**
 * 担保开机 校验担保号码方法
 * 
 * @param data
 * @return
 */
function checkGuaranteeSerailNumber() {
	var sn = $("#GUATANTEE_SERIAL_NUMBER").val();
	clearGuaranteeInfo();
	if (sn == null || sn == "") {
		alert("请输入担保客户号码！");
		return false;
	}
	if (!isTel(sn)) {
		alert("担保手机号码格式不正确，请重新输入！");
		$("#GUATANTEE_SERIAL_NUMBER").val("")
		return false;
	}
	var authSerialNumber = $("#AUTH_SERIAL_NUMBER").val();
	if (sn == authSerialNumber) {
		alert("担保手机号码不能与被担保手机号码相同，请重新输入！");
		$("#GUATANTEE_SERIAL_NUMBER").val("")
		return false;
	}
	$("#GUATANTEE_CUST_NAME").val("");
	$("#CREDIT_CLASS_NAME").val("");
	$("#OPEN_HOURS").val("");
	checkGuaranteeInfo();
}

//校验担保信息：身份校验之后回调
function checkGuaranteeInfo() {
	var sn = $("#GUATANTEE_SERIAL_NUMBER").val();
	$.beginPageLoading("正在校验数据...");
	$.ajax.submit('', 'queryGuaranteeSerialNumber', "&SERIAL_NUMBER=" + sn, 'busiInfoPart', function() {
		$("#CSSUBMIT_BUTTON").attr("disabled", false).removeClass("e_dis");
		$.endPageLoading();
	}, function(error_code, error_info, detail) {
		$("#CSSUBMIT_BUTTON").attr("disabled", true).addClass("e_dis");
		$.endPageLoading();
		MessageBox.error("错误提示", error_info, $.auth.reflushPage, null, null, detail);
	});
}

function isTel(str) {
	var reg = /^([0-9]|[\-])+$/g;
	if (str.length != 11 && str.length != 13) {//增加物联网手机号码长度 13位
		return false;
	} else {
		return reg.exec(str);
	}
}

/**
 * 担保开机提交校验
 * 
 * @return
 */
function guaranteeSubmitCheck() {
	var sn = $("#GUATANTEE_SERIAL_NUMBER").val();
	if (sn == "") {
		alert("请输入担保手机号码！");
		return false;
	}
	var openHours = $("#OPEN_HOURS").val();
	if (openHours == "") {
		alert("担保开机时间不能为空！");
		return false;
	}
	return true;
}
