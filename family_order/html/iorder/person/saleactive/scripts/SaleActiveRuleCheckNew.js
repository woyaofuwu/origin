function refreshPartAtferAuth(data) {
	$.beginPageLoading("正在查询数据...");
	var userInfo = data.get("USER_INFO");
	var param = "&USER_ID=" + userInfo.get("USER_ID") + "&SERIAL_NUMBER=" + userInfo.get("SERIAL_NUMBER");
	ajaxSubmit('', 'initCampnTypes', param, 'QueryRecordPart,QueryListPart', function(rtnData) {
		$.endPageLoading();
		$('#SERIAL_NUMBER').val(userInfo.get("SERIAL_NUMBER"));
		$('#USER_ID').val(userInfo.get("USER_ID"));
	}, function(code, info, detail) {
		$.endPageLoading();
		MessageBox.error("错误提示", info, function(btn) {
		}, null, detail);
	}, function() {
		$.endPageLoading();
		MessageBox.alert("告警提示", "查询超时!", function(btn) {
		});
	});
}

function queryProductByType() {
	var campnType = $('#SALE_CAMPN_TYPE').val();
	var param = "&CAMPN_TYPE=" + campnType;
	$.beginPageLoading("查询营销方案...");
	ajaxSubmit(null, 'queryProdByLabel', param, 'QueryRecordPart1,QueryRecordPart2', function(dataset) {
		$.endPageLoading();
	}, function(code, info, detail) {
		$.endPageLoading();
		MessageBox.error("错误提示", "营销方案列表查询失败！</br>" + info);
	}, function() {
		$.endPageLoading();
		MessageBox.alert("告警提示", "营销方案列表查询超时!", function(btn) {
		});
	});
}

function checkByProduct() {
	var prodId = $('#SALE_PRODUCT_ID').val();
	var param = "&PRODUCT_ID=" + prodId;
	$.beginPageLoading("查询营销包...");
	ajaxSubmit(null, 'queryPackageList', param, 'QueryRecordPart2', function(dataset) {
		$.endPageLoading();
	}, function(code, info, detail) {
		$.endPageLoading();
		MessageBox.error("错误提示", "营销包查询失败！</br>" + info);
	}, function() {
		$.endPageLoading();
		MessageBox.alert("告警提示", "营销包查询超时!", function(btn) {
		});
	});
}

function checkRule() {
	if (!$.validate.verifyAll("QueryRecordPart")) {
		return false;
	}
	if (!$.validate.verifyAll("QueryRecordPart1")) {
		return false;
	}
	if (!$.validate.verifyAll("QueryRecordPart2")) {
		return false;
	}
	var campnType = $('#SALE_CAMPN_TYPE').val();
	var prodId = $('#SALE_PRODUCT_ID').val();
	var packId = $("#SALE_PACKAGE_ID").val();
	var serialNum = $('#SERIAL_NUMBER').val();
	var userId = $('#USER_ID').val();
	var param = "&CAMPN_TYPE=" + campnType + "&PRODUCT_ID=" + prodId
			+ "&PACKAGE_ID=" + packId + "&SERIAL_NUMBER=" + serialNum;
	$.beginPageLoading("校验中...");
	ajaxSubmit(null, 'checkSaleActiveRule', param, 'QueryListPart', function() {
		$.endPageLoading();
	}, function(code, info, detail) {
		$.endPageLoading();
		MessageBox.error("错误提示", "校验失败！</br>" + info);
	}, function() {
		$.endPageLoading();
		MessageBox.alert("告警提示", "校验超时!", function(btn) {
		});
	});
}

function displaySwitch(btn, o) {
	var button = $(btn);
	var div = $('#' + o);
	if (div.css('display') != "none") {
		div.css('display', 'none');
		button.children("i").attr('className', 'e_ico-unfold');
		button.children("span:first").text("展示客户信息");
	} else {
		div.css('display', '');
		button.children("i").attr('className', 'e_ico-fold');
		button.children("span:first").text("不展示客户信息");
	}
}