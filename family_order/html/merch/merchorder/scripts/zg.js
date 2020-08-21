function getOkmOtherParam() {
	var authData = $.auth.getAuthData();
	if (authData) {
		var userInfo = authData.get("USER_INFO");
		return "&SERIAL_NUMBER=" + userInfo.get("SERIAL_NUMBER") + "&USER_ID=" + userInfo.get("USER_ID");
	}
	return "";
}

function openOneKeySalePage(offer) {
	var offerName = offer.get("OFFER_NAME");
	var offerCode = offer.get("OFFER_CODE");
	var serialNumber = $.auth.getAuthData().get("USER_INFO").get("SERIAL_NUMBER");
	var param = "&SERIAL_NUMBER=" + serialNumber + "&ONEKEY_PRODUCT_ID=" + offerCode + "&AUTO_AUTH=true";
	openNav(offerName, "merch.OneKeyMarketingActivity", "init", param);
}

function oneKeySaleAfterAuth(data) {
	var acctTag = data.get("USER_INFO").get("ACCT_TAG");
	var userProductId = data.get("USER_INFO").get("PRODUCT_ID");
	var eparchyCode = data.get('USER_INFO').get('EPARCHY_CODE');

	if ((acctTag != "0") && (userTypeCode != "B")) {
		MessageBox.alert("激活状态校验", "此号码未激活不能办理该业务！");
		return false;
	}
	oneKeySale.setEnv(eparchyCode);
	oneKeySale.initData();

	merchOffer["EPARCHY_CODE"] = eparchyCode;
	suggest.setParam("EPARCHY_CODE", eparchyCode);
}

function orderOneKeySaleAction(offer) {
	setOneKeySaleSubmitParam(offer);
	$("#CSSUBMIT_BUTTON").click();
}

function addOneKeySaleShoppingCart(offer) {
	setOneKeySaleSubmitParam(offer);
	$("#ADD_SHOPPING_CART").click();
}

function setOneKeySaleSubmitParam(offer) {
	var userInfo = $.auth.getAuthData().get("USER_INFO");
	$.cssubmit.setParam("SERIAL_NUMBER", userInfo.get("SERIAL_NUMBER"));
	$.cssubmit.setParam("USER_ID", userInfo.get("USER_ID"));
	$.cssubmit.setParam("EPARCHY_CODE", userInfo.get("EPARCHY_CODE"));
	$.cssubmit.setParam("COMP_PRODUCT_ID", offer.get("OFFER_CODE"));
	$.cssubmit.setParam("REG_SVC", "SS.OneKeyMarketingActivitySVC.tradeReg");
}

function refreshMarketPartAtferAuth(data) {
	var eparchyCode = data.get('USER_INFO').get('EPARCHY_CODE');
	var userId = data.get('USER_INFO').get('USER_ID');
	$('#custinfo_EPARCHY_CODE').val(eparchyCode);
	var labelId = $('#LABEL_ID').val();
	saleActiveFilter.readerComponent(userId, "", "", "", "", labelId);
}

function setRuleParam() {
	var authData = $.auth.getAuthData();
	var serialNum = authData.get('USER_INFO').get('SERIAL_NUMBER');
	var userId = authData.get('USER_INFO').get('USER_ID');
	$('#SALE_SERIAL_NUMBER').val(serialNum);
	$.tradeCheck.addParam('&SERIAL_NUMBER=' + serialNum);
	$.tradeCheck.addParam('&USER_ID=' + userId);
	var activeType = $("#ACTIVE_TYPE").val();
	var activeFlag = $("#ACTIVE_FLAG").val(); // 异地
	if (typeof (activeType) != "undefined")
		$.tradeCheck.addParam('&ACTIVE_TYPE=' + activeType);
	if (typeof (activeFlag) != "undefined")
		$.tradeCheck.addParam('&ACTIVE_FLAG=' + activeFlag);
}
