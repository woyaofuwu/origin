//初始化方法
$(function(){
	var custName = $("#cond_CUST_NAME").val();
	var contactPhone = $("#cond_GROUP_CONTACT_PHONE").val();
	var address = $("#cond_GROUP_ADDR").val();
	var cityCode = $("#cond_CITY_CODE").val();
	$("#pam_ESP_CUST_PROVINCE").val("898");
	$("#pam_ESP_BUILD_MODE").val("咪咕基地");
	$("#pam_ESP_LICENSE").val("百视通");
	$("#pam_ESP_CONTACTS").val(custName);
	$("#pam_ESP_CONTACTS_PHONE").val(contactPhone);
	$("#pam_ESP_CUST_ADDR").val(address);
	$("#pam_ESP_CUST_CITY").val(cityCode);
	 
});

//初始化方法
function initPageParam_110000380700(){
	var custName = $("#cond_CUST_NAME").val();
	var contactPhone = $("#cond_GROUP_CONTACT_PHONE").val();
	var address = $("#cond_GROUP_ADDR").val();
	var cityCode = $("#cond_CITY_CODE").val();
 	$("#pam_ESP_CUST_PROVINCE").val("898");
	$("#pam_ESP_BUILD_MODE").val("咪咕基地");
	$("#pam_ESP_LICENSE").val("百视通");
	$("#pam_ESP_CONTACTS").val(custName);
	$("#pam_ESP_CONTACTS_PHONE").val(contactPhone);
	$("#pam_ESP_CUST_ADDR").val(address); 
	$("#pam_ESP_CUST_CITY").val(cityCode);
}

//商品属性提交校验
function checkSub(obj)
{
	if(!submitOfferCha()){
		return false; 
	}
	backPopup(obj);
}

//根据所选省份获取地市信息
function changeCityByProvince()
{
	var province = $("#pam_ESP_CUST_PROVINCE").val();
	var param = "&PROVINCE="+province;
	$.ajax.submit("","qryEspCustCity",param,null,function(data){
		pam_ESP_CUST_CITY.empty();
		for(var i = 0; i < data.length;i++){
			pam_ESP_CUST_CITY.append(data.get(i).get("DATA_NAME"),data.get(i).get("DATA_ID"));
		}
	},function(error_code,error_info,derror){
	})
}

//选择牌照方时校验建设模式是否已选
function checkBuildMode() {
	var buildMode = $("#pam_ESP_BUILD_MODE").val();
	if (buildMode == null || buildMode == "" || undefined == buildMode) {
		$("#pam_ESP_LICENSE").val("");
		$.validate.alerter.one($("#pam_ESP_BUILD_MODE")[0], "您未选择建设模式，请先选择建设模式!");
		return false;
	}
}

//选择建设模式-》分省自建 时弹出提示信息
function changeBuildMode() {
	var buildMode = $("#pam_ESP_BUILD_MODE").val();
	if ("分省自建" ==  buildMode ) {
		MessageBox.alert("提示信息","请确认已订购省内基础电视视频内容，否则将影响业务功能使用!");
	}
}

//输入手机号码格式校验
function checkPhone(){ 
    var phone = $("#pam_ESP_CONTACTS_PHONE").val();
    var reg = /^1[0-9]{10}$/; 
//    var reg = /^1[0-9]*$/;
    if(!(reg.test(phone))){
    	$("#pam_ESP_CONTACTS_PHONE").val("");
    	$.validate.alerter.one($("#pam_ESP_CONTACTS_PHONE")[0], "您输入的格式不正确，请重新输入11位手机号!");
		return false;
    } 
}

//输入设备数量格式校验
function checkDeviceNum(){ 
	var deviceNum = $("#pam_ESP_DEVICE_NUM").val();
    var reg = /^[0-9]*$/;
    if(!(reg.test(deviceNum))){
    	$("#pam_ESP_DEVICE_NUM").val("");
    	$.validate.alerter.one($("#pam_ESP_DEVICE_NUM")[0], "您输入的格式不正确，请重新输入!");
		return false;
    } 
}

//输入订购月份格式校验
function checkOrderMonthNum(){ 
	var deviceNum = $("#pam_ESP_SUBSCRIBE_TIME").val();
    var reg = /^[0-9]*$/;
    if(!(reg.test(deviceNum))){
    	$("#pam_ESP_SUBSCRIBE_TIME").val("");
    	$.validate.alerter.one($("#pam_ESP_SUBSCRIBE_TIME")[0], "您输入的格式不正确，请重新输入!");
		return false;
    } 
}
