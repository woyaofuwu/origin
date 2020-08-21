function closeMyPage()
{
    closeNav("simcardmgr.RemoteCardESim");
}

function submitCashbox(){
//金库认证临时注销
//	var rightCode = 'CSM_REMOTECARD';
//	var serialNumber = $("#AUTH_SERIAL_NUMBER").val();
//	if(beforeSubmit()){
//		$.cssubmit.exeCashbox(rightCode,serialNumber,$.cssubmit.submitTrade);
//	}
return true;
}


function refreshPartAtferAuth(data)
{

	
	$.beginPageLoading();
	$.ajax.submit('AuthPart,hiddenUserInfopart', 'loadChildInfo', "&USER_INFO="+data.get("USER_INFO").toString()+"&CUST_INFO="+data.get("CUST_INFO").toString(), 'userInfoPart,NewCardInfo,hiddenInfoPart', function(data){
		$.endPageLoading();
	
		$("#IMEI").attr("disabled",null);
		$("#NEW_EID").attr("disabled",null);
		$("#CSSUBMIT_BUTTON").attr("disabled",true).addClass("e_dis");
		$.cssubmit.disabledSubmitBtn(false);
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
	});
}






function beforeSubmit(){
     
	if(!$.validate.verifyAll("NewCardInfo")) {
		return false;
	}
	var isChecked = $("#IS_CHECKED").val();
	if(isChecked != "0"){
		MessageBox.alert("错误提示", "请先校验卡片信息！");
		return false;
	}
	$.tradeCheck.addParam('&RES_TYPE_CODE='+$("#RES_TYPE_CODE").val()+'&NEW_SIM_CARD_NO='+$("#SIM_CARD_NO").val());
	var orderTypeCode = $("#TRADE_TYPE_CODE").val();
	var param = "&ORDER_TYPE_CODE=" + orderTypeCode;
	$.cssubmit.addParam(param);
	return true;
}

function checkWriteCard(newIMEI){
	var sn = $("#AUTH_SERIAL_NUMBER").val();
	if(undefined == sn || null == sn || '' == sn){
        MessageBox.alert('手机号码不能为空');
		return ;
	}
	var newEid =$("#NEW_EID").val();
	var oldEid = $("#OLE_EID").val();
	var imei = $("#IMEI").val();

	if(oldEid == newEid){
		MessageBox.alert("错误提示", "新eSIM卡设备Eid不得与老eSIM卡一致！");
		return;
	}
	var expressionIMEI = /^[0-9]+.?[0-9]*$/;
	if(!expressionIMEI.test(imei)){ 
		MessageBox.alert("提示信息",'IMEI格式错误！IMEI应为纯数字!'); 
		return false;
	}  
	if((imei.toString().length) != 15){
	MessageBox.alert("提示信息",'IMEI格式错误！IMEI应为15位数字!'); 
	return false;
	};
//	$.simcard.checkSimCard(simCardNo, "verifySimCard(data)","SIM_CARD_NO");
//    var param = "&SERIAL_NUMBER="+sn+"&ACTION=CHECK_SIMCARD";
//    verifySimCard_New(param,simCardNo);
	$.feeMgr.clearFeeList();
	$.beginPageLoading("校验SIM卡......");
	
	$.ajax.submit('AuthPart,userInfoPart,hiddenInfoPart,NewCardInfo','verifySimCard','','NewCardInfo',function(input){
		$.endPageLoading();
		alert("=======");
		$("#IS_CHECKED").val("0");
		$("#NEW_SIM_CARD_NO").val($("#SIM_CARD_NO").val());
		$("#NEW_SIM_CARD_INFO").val(input);
		var resultCode = input.get('X_RESULTCODE');
		var tradeTypeCode = $("#TRADE_TYPE_CODE").val();
		if('0'==resultCode){
		    var fee = input.get("FEE");
			$.feeMgr.removeFee(tradeTypeCode,"0","10");
			if( fee > 0){
	
				var obj = new Wade.DataMap();
				obj.put("MODE", "0"); 
				obj.put("CODE", "10"); 
				obj.put("FEE", fee); 
				obj.put("PAY",  fee);		
				obj.put("TRADE_TYPE_CODE","141");
				$.feeMgr.insertFee(obj);
			}
		
		$("#CSSubmitID").val("1");

	
	}else{
		MessageBox.error("业务受理异常:"+input.get('X_RESULT_INFO'));			
	}
},
function(error_code,error_info){
	$.endPageLoading();
	alert(error_info);
	return false;
});

}



