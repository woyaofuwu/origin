
$(function(){
	//初始化
	var custName = $("#cond_CUST_NAME").val();
	var contactPhone = $("#cond_GROUP_CONTACT_PHONE").val();
	var address = $("#cond_GROUP_ADDR").val();

	$("#pam_ESP_GROUP_CONTACTS").val(custName);
	$("#pam_ESP_GROUP_CONTACTS_PHONE").val(contactPhone);
	if (address) {
		$("#pam_ESP_GROUP_ADDRESS").val(address);
	}
})

function initPageParam_110000921015(){
	//初始化
	var custName = $("#cond_CUST_NAME").val();
	var contactPhone = $("#cond_GROUP_CONTACT_PHONE").val();
	var address = $("#cond_GROUP_ADDR").val();
	$("#pam_ESP_GROUP_CONTACTS").val(custName);
	$("#pam_ESP_GROUP_CONTACTS_PHONE").val(contactPhone);
	if (address) {
		$("#pam_ESP_GROUP_ADDRESS").val(address);
	}
}

// 提交
function checkSub(obj) {
	
     //校验
    if (!checkOrderTime()){
    	$.validate.alerter.one($("#pam_ESP_GROUP_ORDER_TIME")[0], "您输入的订购时长格式不正确!");
    	return false;
    }
    
    if (!checkPhone()){
        $.validate.alerter.one($("#pam_ESP_GROUP_CONTACTS_PHONE")[0], "您输入的号码格式不正确!");
    	return false;
    }
    
    if (!checkMail()){
    	 $.validate.alerter.one($("#pam_ESP_GROUP_MAIL")[0], "您输入的邮箱格式不正确!");
    	return false;
    }
    	
    if (!submitOfferCha())
        return false;

    backPopup(obj);
}

//电话号码
function checkPhone() {
    var phone = $("#pam_ESP_GROUP_CONTACTS_PHONE").val();
    var reg = /^0?(13|14|15|18|17|16|19)[0-9]{9}$/;
    if (!(reg.test(phone))) {
        $.validate.alerter.one($("#pam_ESP_GROUP_CONTACTS_PHONE")[0], "您输入的格式不正确!");
        return false;
    }
    return true;
};

//邮箱地址 
function checkMail() {
    var license = $("#pam_ESP_GROUP_MAIL").val();
    var reg = /^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/;
    if (!(reg.test(license))) {
        return false;
    }
    return true;
}

//订购时长校验
function checkOrderTime() {
    var orderTime = $("#pam_ESP_GROUP_ORDER_TIME").val();
    var reg = /^[1-9]\d{0,2}$/;
    if (!(reg.test(orderTime))) {
        return false;
    }
    return true;
}