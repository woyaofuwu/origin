function queryInfo2(){

	if(!$.validate.verifyAll("mainPart")){
		return false;
	}
	
	$.beginPageLoading("数据处理中...");
	$.ajax.submit('mainPart', 'getOtherSNInfoM', null, 'userInfoPart', function(data){ 
		$.endPageLoading();
	},
	function(error_code,error_info)
	{
		$.endPageLoading();
		alert(error_info);
    });
}

function queryInfo(){

	if(!$.validate.verifyAll("CustCondPart")){
		return false;
	}
	var sn = $("#SERIAL_NUMBER").val();
	var sno = $("#SERIAL_NUMBER_O").val()

	if(sn ==sno){
		alert("主副卡号码不能相同！");
		return false;
	}
	
	$.beginPageLoading("数据处理中...");
	$.ajax.submit('CustCondPart', 'getOtherSNInfo', null, 'userInfoPart_newSim', function(data){ 
		$.endPageLoading();
	},
	function(error_code,error_info)
	{
		$.endPageLoading();
		alert(error_info);
    });
}
function refreshPartAtferAuth(data)
{
	$.ajax.submit('AuthPart', 'loadChildInfo', "&USER_INFO="+data.get("USER_INFO").toString()+"&CUST_INFO="+data.get("CUST_INFO").toString(), 'userInfoPart', function(data){
		$("#OLD_SIM_CARD_INFO").val(data);
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
	});
}

function read(data){
	//alert("空卡序列号："+data.toString());
}

function before(){
	$.simcard.setSerialNumber($("#SERIAL_NUMBER").val());
	$.simcard.setSerialNumberB($("#SERIAL_NUMBER_O").val());
	alert("主号码  = "+$("#SERIAL_NUMBER").val()+"副号码  = "+$("#SERIAL_NUMBER_O").val());

	return true;
}