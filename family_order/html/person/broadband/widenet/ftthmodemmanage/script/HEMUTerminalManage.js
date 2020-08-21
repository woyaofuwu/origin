/***************查询和目终端信息**********************/
function checkSerialNumber(){
	var param = "";	
	var phoneNum = $("#SERIAL_NUMBER").val();
	if(phoneNum.length != 11 ||!$.verifylib.checkMbphone(phoneNum)){
		   alert("手机号码格式不对");
		   return false;
	 }
	param += "&SERIAL_NUMBER="+phoneNum;
	$(".hidden_input").val("");
	$.beginPageLoading("查询用户终端信息...");
	$.ajax.post(null, "getTerminalBySN", param, null, 
			function(data){
		    var terminalTradeType = $("#TERMINAL_TRADE_TYPE");
			$.endPageLoading();
			if(data.get("RESULT_CODE")=='0000'){
				$("#INST_ID").val(data.get("INST_ID"));
				$("#RES_CODE").val(data.get("RES_CODE"));
				$("#RES_NAME").val(data.get("GOODS_NAME"));
				$("#DEVICE_MODEL_CODE").val(data.get("DEVICE_MODEL_CODE"));
				$("#DEVICE_BRAND_CODE").val(data.get("DEVICE_BRAND_CODE"));
				$("#AUTH_SERIAL_NUMBER").val(data.get("SERIAL_NUMBER"));
			}else if(data.get("RESULT_CODE")=='0001'){
				$("#INST_ID").val(data.get("INST_ID"));
				$("#PRODUCT_ID").val(data.get("PRODUCT_ID"));
				$("#RES_CODE").val(data.get("RES_CODE"));
				$("#RES_NAME").val(data.get("RES_NAME"));
				$("#PACKAGE_ID").val(data.get("PACKAGE_ID"));
				$("#DEPOSIT_TRADE_ID").val(data.get("DEPOSIT_TRADE_ID"));
				$("#AUTH_SERIAL_NUMBER").val(data.get("SERIAL_NUMBER"));
			}
			else{
		
				MessageBox.alert("告警提示",data.get("RESULT_INFO"));
				$("#SERIAL_NUMBER").val("");
			}
			},	
			function(code, info, detail){
				$.endPageLoading();
				MessageBox.alert("告警提示",info);
			},
			function(){
				$.endPageLoading();
				MessageBox.alert("告警提示","查询用户信息超时！");
			}
		);
}



function checkNumber(){
	var serial_number = $("#SERIAL_NUMBER").val();
	var serial_number1 = $("#AUTH_SERIAL_NUMBER").val();
	if(serial_number == "" || serial_number1 == null){
		MessageBox.alert("提示","请校验服务号码！");
		return false;
	}
	if(serial_number != serial_number1){
		MessageBox.alert("提示","服务号码与校验号码不相同！");
		return false;
	}
	return true;
}

function changeBusiTerminaltrade(){
	var terminalTradeType = $("#TERMINAL_TRADE_TYPE").val();
	var instId = $("#INST_ID").val();
	var productId = $("#PRODUCT_ID").val();
	if(terminalTradeType==3&&instId==""&&productId==""){
		MessageBox.alert("提示","该号码不支持终端申领");
	}
	if(terminalTradeType==3&&instId!=""){
		MessageBox.alert("提示","该号码已经申领过终端了");
	}
	
	if(terminalTradeType==2){
		 $("#terminalapplyinfo").attr("style","display:none");
	}else{
		 $("#terminalapplyinfo").attr("style","display:''");
	}
	
}

function checkTerminalId(){
	if(!checkNumber()){
		return;
	}
	
	var terminalId = $("#TERMINAL_ID").val();
	if(terminalId == ""){
		MessageBox.alert("提示","请输入终端串码");
		return false;
	}
	
	$.beginPageLoading("正在校验终端信息...");
	var serial_number = $("#SERIAL_NUMBER").val();
	
	$.ajax.post(null, "checkTerminalId", "&SERIAL_NUMBER="+serial_number+"&RES_ID="+terminalId,"TerminalCheckPart", function(data){
	
		$.endPageLoading();	
		$.closePopupDiv('checkTerminalPopup');
		if(data.get("X_RESULTCODE")!='0'){
			MessageBox.alert("提示",data.get("X_RESULTINFO"));
			$("#TERMINAL_ID").val("");
			$("#APPLYTERMINALID").val("");
			return;
		}
		
	},function(e,i){
		$.endPageLoading();	
		MessageBox.alert("提示",i,function(){});
		$.closePopupDiv('checkTerminalPopup');
		$("#TERMINAL_ID").val("");
	});
}
function submit(){
	if(!checkNumber()){
		return;
	}
	var instId = $("#INST_ID").val();
	var resCode = $("#RES_CODE").val();
	var resName = $("#RES_KIND_NAME").val();
	var applyTerminalId = $("#APPLYTERMINALID").val();
	var resKindCode = $("#RES_KIND_CODE").val();
	var serialNumber = $("#SERIAL_NUMBER").val();
	var terminalTradeType = $("#TERMINAL_TRADE_TYPE").val();
	var productId = $("#PRODUCT_ID").val();
	var packageId = $("#PACKAGE_ID").val();

	var depositTradeId = $("#DEPOSIT_TRADE_ID").val();

	if(terminalTradeType==3&&productId==""&&instId!=""){
		MessageBox.alert("提示","该号码不支持终端申领");
		return;
	}
	if(terminalTradeType==3&&instId!=""){
		MessageBox.alert("提示","该号码已经申领过终端了");
		return;
	}
	
	var param = "&SERIAL_NUMBER="+serialNumber+"&RES_ID="+applyTerminalId+"&INST_ID="+instId+"&RES_KIND_NAME="+resName+
	"&OLD_RES_ID="+resCode+"&TERMINAL_TRADE_TYPE="+terminalTradeType+"&PRODUCT_ID="+productId+"&PACKAGE_ID="+packageId+"&DEPOSIT_TRADE_ID="+depositTradeId;
	$.beginPageLoading("修改用户和目终端信息...");
	$.ajax.post(null, "onTradeSubmit", param,"TerminalCheckPart", function(data){
		$.endPageLoading();	
		if(data.get("ORDER_ID")!='0'){
			var title = "业务受理成功";
			var content = "点【确定】继续业务受理。";
			content = "客户订单标识：" + data.get("ORDER_ID") + "<br/>点【确定】继续业务受理。";
			$.cssubmit.showMessage("success", title, content, false);
			
		}else{
			MessageBox.alert("提示","更改用户终端信息成功！");
			$("#SERIAL_NUMBER").val("");
			$("#TERMINAL_ID").val("");
		
		}
		
	},function(e,i){
		$.endPageLoading();	
		MessageBox.alert("提示",i,function(){});
		$.closePopupDiv('checkTerminalPopup');
		$("#TERMINAL_ID").val("");
	});
	
	
}