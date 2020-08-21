
$(function(){
	//初始化
	var custName = $("#cond_CUST_NAME").val();
	var contactPhone = $("#cond_GROUP_CONTACT_PHONE").val();
	var address = $("#cond_GROUP_ADDR").val();
	$("#pam_ESP_GROUP_CONTACTS").val(custName);
	$("#pam_ESP_GROUP_CONTACTS_PHONE").val(contactPhone);
	
	var psptId = $("#cond_PSPT_ID").val();
	var psptTypeCode = $("#cond_PSPT_TYPE_CODE").val();
	if (psptTypeCode && (psptTypeCode == "0" || psptTypeCode == "1")) {
		//身份证
		$("#pam_ESP_MANAGER_LICENSE").val("1");
		$("#pam_ESP_MANAGER_LICENSE_NUM").val(psptId);
	} else if (psptTypeCode && psptTypeCode == "2"){
		//户口本
		$("#pam_ESP_MANAGER_LICENSE").val("3");
		$("#pam_ESP_MANAGER_LICENSE_NUM").val(psptId);
	} else if (psptTypeCode && psptTypeCode == "C"){
		//军官证 
		$("#pam_ESP_MANAGER_LICENSE").val("4");
		$("#pam_ESP_MANAGER_LICENSE_NUM").val(psptId);
	}
	
	if (address) {
		$("#pam_ESP_GROUP_ADDRESS").val(address);
	}
	//默认海口经纬度 百度地图
	$("#pam_ESP_GROUP_ADDRESS_LNG").val("110.2048317258");
	$("#pam_ESP_GROUP_ADDRESS_LAT").val("20.0497739066");
	
})

function initPageParam_110000380300(){
	//初始化
	var custName = $("#cond_CUST_NAME").val();
	var contactPhone = $("#cond_GROUP_CONTACT_PHONE").val();
	var address = $("#cond_GROUP_ADDR").val();
	$("#pam_ESP_GROUP_CONTACTS").val(custName);
	$("#pam_ESP_GROUP_CONTACTS_PHONE").val(contactPhone);
	if (address) {
		$("#pam_ESP_GROUP_ADDRESS").val(address);
	}
	var psptId = $("#cond_PSPT_ID").val();
	var psptTypeCode = $("#cond_PSPT_TYPE_CODE").val();
	if (psptTypeCode && (psptTypeCode == "0" || psptTypeCode == "1")) {
		//身份证
		$("#pam_ESP_MANAGER_LICENSE").val("1");
		$("#pam_ESP_MANAGER_LICENSE_NUM").val(psptId);
	} else if (psptTypeCode && psptTypeCode == "2"){
		//户口本
		$("#pam_ESP_MANAGER_LICENSE").val("3");
		$("#pam_ESP_MANAGER_LICENSE_NUM").val(psptId);
	} else if (psptTypeCode && psptTypeCode == "C"){
		//军官证 
		$("#pam_ESP_MANAGER_LICENSE").val("4");
		$("#pam_ESP_MANAGER_LICENSE_NUM").val(psptId);
	}
	//默认海口经纬度 百度地图
	$("#pam_ESP_GROUP_ADDRESS_LNG").val("110.2048317258");
	$("#pam_ESP_GROUP_ADDRESS_LAT").val("20.0497739066");
}

// 提交
function checkSub(obj) {

	//校验
	if (!checkAddressLng() || !checkAddressLat()) {
		return false;
	}
	
    if (!submitOfferCha())
        return false;

    backPopup(obj);
}

//经度
function checkAddressLng() {
    var addressLng = $("#pam_ESP_GROUP_ADDRESS_LNG").val();
    var reg = /^\d+(\.\d+)?$/; 
	if (!(reg.test(addressLng))) {
		$.validate.alerter.one($("#pam_ESP_GROUP_ADDRESS_LNG")[0], "请填写数字!");
		return false;
	}
    reg = /^\d+(\.\d{1,10})?$/;
    if (!(reg.test(addressLng))) {
    	$.validate.alerter.one($("#pam_ESP_GROUP_ADDRESS_LNG")[0], "请填写正确的经度,小数点后最多保留10位");
        return false;
    }
    return true;
}
//维度
function checkAddressLat() {
	var addressLat = $("#pam_ESP_GROUP_ADDRESS_LAT").val();
	var reg = /^\d+(\.\d+)?$/; 
	if (!(reg.test(addressLat))) {
		$.validate.alerter.one($("#pam_ESP_GROUP_ADDRESS_LAT")[0], "请填写数字!");
		return false;
	}
	reg = /^\d+(\.\d{1,11})?$/;
	if (!(reg.test(addressLat))) {
		$.validate.alerter.one($("#pam_ESP_GROUP_ADDRESS_LAT")[0], "请填写正确的纬度,小数点后最多保留11位!");
		return false;
	}
	return true;
}


//电话号码
function checkPhone() {
    var phone = $("#pam_ESP_GROUP_CONTACTS_PHONE").val();
    var reg = /^0?(13|14|15|18|17|16|19)[0-9]{9}$/;
    if (!(reg.test(phone))) {
        $("#pam_ESP_GROUP_CONTACTS_PHONE").val("");
        $.validate.alerter.one($("#pam_ESP_GROUP_CONTACTS_PHONE")[0], "您输入的格式不正确!");
        return false;
    }
};

//证件号码
function checkLicense() {
    var license = $("#pam_ESP_MANAGER_LICENSE_NUM").val();
    var reg = /^(\d{17}[\d|X])$|^(\d{15})$/;
    if (!(reg.test(license))) {
        $("#pam_ESP_MANAGER_LICENSE_NUM").val("");
        $.validate.alerter.one($("#pam_ESP_MANAGER_LICENSE_NUM")[0], "您输入的格式不正确!");
        return false;
    }
}
