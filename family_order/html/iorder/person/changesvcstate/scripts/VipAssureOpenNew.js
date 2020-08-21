function refreshVipAssurePartAtferAuth(data) {
	//清理页面数据
	clearVipInfo();
	$("#GUATANTEE_SERIAL_NUMBER").val("");
}

//清空大客户信息
function clearVipInfo() {
	$("#VIP_CUST_NAME").val("");
	$("#GUATANTEE_USER_ID").val("");
	$("#VIP_CLASS_NAME").val("");
	$("#VIP_CLASS_ID").val("");
	$("#OPEN_HOURS").val("");
	$("#CITY_CODE").val("");
	$("#MANAGER_NAME").val("");
	$("#MANAGER_PHONE").val("");
	$("#CUST_MANAGER_ID").val("");
	$("#ASSURE_COUNT").val("");
	$("#REMARK").val("");
}

function checkVipAssureInfo() {
	var vipsn = $("#GUATANTEE_SERIAL_NUMBER").val();
	var authsn = $("#AUTH_SERIAL_NUMBER").val();
	clearVipInfo();
	if (vipsn == '') {
		MessageBox.alert('请输入大客户服务号码');
		return;
	}
	if (vipsn == authsn) {
		MessageBox.alert('大客户服务号码不能与报开号码相同');
		return;
	}
	$.beginPageLoading("正在查询数据...");
	$.ajax.submit('', 'queryVipAssureSnInfo', "&SERIAL_NUMBER=" + vipsn, 'busiInfoPart', function() {
		$("#CSSUBMIT_BUTTON").attr("disabled", false).removeClass("e_dis");
		$.endPageLoading();
	}, function(error_code, error_info, detail) {
		$("#CSSUBMIT_BUTTON").attr("disabled", true).addClass("e_dis");
		$.endPageLoading();
		MessageBox.error("错误提示", error_info, $.auth.reflushPage, null, null, detail);
	});
}

/**
 * 大客户担保开机提交校验
 * 
 * @return
 */
function vipAssureSubmitCheck() {
	var sn = $("#GUATANTEE_SERIAL_NUMBER").val();
	if (sn == "") {
		MessageBox.alert("请输入大客户号码！");
		return false;
	}
	if (!isTel(sn)) {
		MessageBox.alert("大客户号码格式不正确，请重新输入！");
		$("#GUATANTEE_SERIAL_NUMBER").val("")
		return false;
	}
	var authSerialNumber = $("#AUTH_SERIAL_NUMBER").val();
	if (sn == authSerialNumber) {
		MessageBox.alert("大客户号码不能与被担保手机号码相同，请重新输入！");
		$("#GUATANTEE_SERIAL_NUMBER").val("")
		return false;
	}
	var openHours = $("#OPEN_HOURS").val();
	if (openHours == "") {
		MessageBox.alert("担保时间不能为空！");
		return false;
	}
	return true;
}

function isTel(str) {
	var reg = /^([0-9]|[\-])+$/g;
	if (str.length != 11 && str.length != 13) {//增加物联网手机号码长度 13位
		return false;
	} else {
		return reg.exec(str);
	}
}
