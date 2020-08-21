/* $Id: rps.js,v 1.1 2013/02/27 11:09:42 huyong Exp $ */

/* 金普斯控件接口 */

function checkDriver() {
	try {
		RpsClient.OcxVersion;
	} catch(e) {
		alert('系统检测到您没有安装写卡客户端，请到系统首页进行下载安装！');
		return false;
	}
}

function getOcxVersion() {
	return RpsClient.OcxVersion;
}

function getICCID() {
	var result = RpsClient.GetICCID();
	if(result != 0) {
		var code = RpsClient.LastErrorCode;
		var desc = RpsClient.LastErrorDesc;
		alert('获取SIM卡序列号出错：\n' + '[' + code + ']' + desc);
		return false;
	} else {
		return RpsClient.ICCID;
	}
}

function getIMSI() {
	var result = RpsClient.GetIMSI();
	if(result != 0) {
		var code = RpsClient.LastErrorCode;
		var desc = RpsClient.LastErrorDesc;
		alert('获取IMSI出错：\n' + '[' + code + ']' + desc);
		return false;
	} else {
		return RpsClient.IMSI;
	}
}

function getEmptyCardId() {
	var result = RpsClient.GetSerialFile();
	if(result != 0) {
		var code = RpsClient.LastErrorCode;
		var desc = RpsClient.LastErrorDesc;
		alert('获取白卡序列号出错：\n' + '[' + code + ']' + desc);
		return false;
	} else {
		return RpsClient.SerialFile;
	}
}

function initWebService(url1) {
	var result = RpsClient.InitWebService(url1);
	if(result != 0) {
		var code = RpsClient.LastErrorCode;
		var desc = RpsClient.LastErrorDesc;
		alert('初始化RPS系统连接失败：\n' + '[' + code + ']' + desc);
	}
	return result;
}

function login(userName, password) {
	var result = RpsClient.Login(userName, password);
	if(result != 0) {
		var code = RpsClient.LastErrorCode;
		var desc = RpsClient.LastErrorDesc;
		alert('RPS用户认证失败：\n' + '[' + code + ']' + desc);
	}
	return result;
}

function personalize(str) {
	var result = RpsClient.Personalize(str);
	if(result != 0) {
		var code = RpsClient.LastErrorCode;
		var desc = RpsClient.LastErrorDesc;
		alert('写卡失败：\n' + '[' + code + ']' + desc);
	}
	return result;
}

function personalizeTD(str) {
	var result = RpsClient.TDPersonalize(str);
	if(result != 0) {
		var code = RpsClient.LastErrorCode;
		var desc = RpsClient.LastErrorDesc;
		alert('TD写卡失败：\n' + '[' + code + ']' + desc);
	}
	return result;
}

function rePersonalize(str) {
	var result = RpsClient.RePersonalize(str);
	if(result != 0) {
		var code = RpsClient.LastErrorCode;
		var desc = RpsClient.LastErrorDesc;
		alert('重个人化失败：\n' + '[' + code + ']' + desc);
	}
	return result;
}

function clearSPBADNSMS(clearSPB, clearADN, clearSMS) {
	var result = RpsClient.ClearSPBADNSMS(clearSPB, clearADN, clearSMS);
	if(result != 0) {
		var code = RpsClient.LastErrorCode;
		var desc = RpsClient.LastErrorDesc;
		alert('清除卡内个人信息失败：\n' + '[' + code + ']' + desc);
	}
	return result;
}

function logout() {
	var result = RpsClient.LogOut();
	if(result != 0) {
		var code = RpsClient.LastErrorCode;
		var desc = RpsClient.LastErrorDesc;
		alert('RPS登出失败：\n' + '[' + code + ']' + desc);
	}
	return result;
}
