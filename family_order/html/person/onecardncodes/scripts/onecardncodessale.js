function refreshPartAtferAuth(data)
{
	$.ajax.submit('AuthPart', 'loadChildInfo', "&USER_INFO="+data.get("USER_INFO").toString()+"&CUST_INFO="+data.get("CUST_INFO").toString(), 'userInfoPart,userInfoPart_newSim', function(data){
		$("#OLD_SIM_CARD_INFO").val(data);
	},
	function(error_code,error_info){
		$.endPageLoading();
		$("#CSSUBMIT_BUTTON").attr("disabled",true).removeClass("e_dis");
		alert(error_info);
	});
}


function queryInfo(){

	if(!$.validate.verifyAll("CustCondPart")){
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