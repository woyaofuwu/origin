function refreshPartAtferAuth(data)
{
	$.ajax.submit('AuthPart', 'loadChildInfo', "&USER_INFO="+data.get("USER_INFO").toString()+"&CUST_INFO="+data.get("CUST_INFO").toString(), 'userInfoPart,userInfoPart_newSim', function(data){
		$("#OLD_SIM_CARD_INFO").val(data);
		$("#CSSUBMIT_BUTTON").attr("disabled",true).removeClass("e_dis");

	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
	});
}
function checkNewMSim()
{
	$.ajax.submit('userInfoPart,AuthPart,userInfoPart_newSim', 'checkNewMSim', null, null, function(data){
		$("#commInfo_NEWRES_CODE").val(data.get("SIMCARDNO_A"));
		$("#commInfo_SIMNO").val(data.get("SIMCARDNO_B"));
		$("#commInfo_IMSI").val(data.get("IMSI_A"));
		$("#commInfo_IMSI1").val(data.get("IMSI_B"));
		$("#KI_A").val(data.get("KI_A"));
		$("#KI_B").val(data.get("KI_B"));
		$("#CSSUBMIT_BUTTON").attr("disabled",false).removeClass("e_dis");

				var obj = new Wade.DataMap();
				obj.put("TRADE_TYPE_CODE", "321");
				obj.put("MODE", "0"); 
				obj.put("CODE", "10"); 
				obj.put("FEE", data.get("FEE_DATA")); 
				$.feeMgr.removeFee("321","0","10");
				$.feeMgr.insertFee(obj);
	},
	function(error_code,error_info){
		$.endPageLoading();

		alert(error_info);
	});
}