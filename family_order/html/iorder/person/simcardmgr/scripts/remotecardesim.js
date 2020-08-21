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

function beforeReadCard(){
	var sn = $("#AUTH_SERIAL_NUMBER").val();
	$.simcard.setSerialNumber(sn);
	return true;
}

function afterWriteCard(data){
	var simCardNo = data.get("SIM_CARD_NO");
	if(data.get("RESULT_CODE")=="0"){
		$.simcard.checkSimCard(simCardNo, "verifySimCard","SIM_CARD_NO");
	}else {
		MessageBox.alert("错误提示", "写卡失败！");
		return false;
	}
}

function setSerialNumber(){
	var sn = $("#AUTH_SERIAL_NUMBER").val();
	$.simcardcheck.setSerialNumber(sn);
	return true;
}

function beforeSubmit(){

	if(!$.validate.verifyAll("NewCardInfo")) {
		return false;
	}
	var isChecked = $("#IS_CHECKED").val();
	if(isChecked != "1"){
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
		$("#SIM_CARD").attr("disabled",true);
	    $("#SIM_CARD_NO").attr("disabled",true);

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

function queryCheckResult(){
	var eid = $.trim($("#NEW_EID").val());
	var checkFlag = $("#IS_CHECKED").val();
	if("1" != checkFlag){
		alert("请先输入新设备的EID、IMEI号并校验");
		return false;
	}

	$.beginPageLoading();
	setTimeout(function() {
		$.ajax.submit('NewCardInfo,AuthPart,hiddenInfoPart', 'queryCheckResult', null, '', function(data){
				var tradeTypeCode = $("#TRADE_TYPE_CODE").val();
				var resultCode = data.get('X_RESULTCODE');
				if('0'==resultCode){
					$("#SIM_CARD_NO").val(data.get('ICC_ID'));
					$("#IMSI").val(data.get('IMSI'));
					$("#NEW_SIM_CARD_INFO").val(data);
					$.feeMgr.removeFee(tradeTypeCode,"0","10");
					var total = data.get("FEE") ;
					var feeData = $.DataMap();
					feeData.put("MODE", "0");
					feeData.put("CODE", "10");
					feeData.put("FEE",  total);
					feeData.put("PAY",  total);
					feeData.put("TRADE_TYPE_CODE",tradeTypeCode);
					$.feeMgr.insertFee(feeData);
					$("#CSSUBMIT_BUTTON").attr("disabled",false).removeClass("e_dis");
				}else if('-1'==resultCode){
					MessageBox.error("平台校验校验失败:"+data.get('X_RESULT_INFO'));
				}
				else if('1'==resultCode){
					MessageBox.alert("处理异常:"+data.get('X_RESULT_INFO'));
				}
				$.endPageLoading();
			},
			function(error_code,error_info){
				$.endPageLoading();
				$("#CSSUBMIT_BUTTON").attr("disabled",true).addClass("e_dis");
				MessageBox.alert(error_info);
			});
	}, 30);
}


function checkIMEI(newIMEI){

	var oldEid = $("#OLE_EID").val();
	var eid = $.trim($("#NEW_EID").val());
	var imei = $.trim(newIMEI.val());
	var checkFlag = $("#IS_CHECKED").val();
	if("1" == checkFlag){
		alert("校验请求已发送,请点击检索按钮查询结果");
		return false;
	}
	if(eid == ""){
		MessageBox.alert("",'新EID号不能为空！');
		$("#eid").focus();
		return false;
	}
	// if(typeof(oldEid) == "undefined" || oldEid == ""){
	// 	MessageBox.alert("提示信息",'旧EID号不能为空！');
	// 	return false;
	// }
	var expressionIMEI = /^[0-9]+.?[0-9]*$/;
	if(!expressionIMEI.test(imei)){
		MessageBox.alert("提示信息",'IMEI格式错误！IMEI应为纯数字!');
		return false;
	}
	if((imei.toString().length) != 15){
		MessageBox.alert("提示信息",'IMEI格式错误！IMEI应为15位数字!');
		return false;
	};

	$.beginPageLoading();
	setTimeout(function() {
		$.ajax.submit('NewCardInfo,AuthPart,hiddenInfoPart', 'verifyIMEI', null, '', function(data){
				var tradeTypeCode = $("#TRADE_TYPE_CODE").val();
				var resultCode = data.get('X_RESULTCODE');
				if('0'==resultCode){
					$("#IS_CHECKED").val("1"); //用来标记是否发送校验请求成功
					$("#NEW_EID").attr("disabled","true");
					$("#NEW_IMEI").attr("disabled","true");
					MessageBox.alert('提示信息','发送校验请求成功,请点击检索按钮查询校验结果');
				}else{
					MessageBox.error("平台校验异常:"+data.get('X_RESULT_INFO'));
				}
				$.endPageLoading();
			},
			function(error_code,error_info){
				$.endPageLoading();
				$("#CSSUBMIT_BUTTON").attr("disabled",true).addClass("e_dis");
				MessageBox.alert(error_info);
			});
	}, 30);
}

