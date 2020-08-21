function queryListInfos()
{
	if(!$.validate.verifyAll("CondPart")) {
		return false;
	}else{	 
		$.beginPageLoading("正在查询数据...");
		$.ajax.submit('CondPart', 'qryConsumInfos', "", 'ResultPart,editPart,showPart', function(data){
			$.endPageLoading();
			$("#USER_ID").val(data.get("USER_ID"));
			var qryFlag=data.get("EXISE");
			if(qryFlag=="FALSE"){
				$("#SendSMSBtn").attr("disabled",true);
				alert("根据号码查询不到用户数据，请重新输入号码查询");
				$("#cond_SERIAL_NUMBER").focus();
			}else if(qryFlag=="TRUE"){
				var consumInfo=$("#PRODUCT_NEW").val();
				if(consumInfo != ""){
					$("#SendSMSBtn").attr("disabled",false);
				}
			}else{
				reset();
				$("#SendSMSBtn").attr("disabled",true);
			}
		},function(code, info, detail){
			$.endPageLoading();
			MessageBox.error("错误提示","查询错误！", null, null, info, detail);
		});
	}
}
  

function reset(){
	$("#cond_SERIAL_NUMBER").val('');
	$("#ResultPart").empty();
	$("#TELE_TIME1").val('');
	$("#TELE_TIME3").val('');
	$("#TELE_TIME2").val('');
	$("#SMS_CNT").val('');
	$("#GPRS_CNT").val('');
	$("#WLAN").val('');
	$("#ALL_FEE").val('');
}

function consum(){
	if(!$.validate.verifyAll("editPart")) {
		return false;
	}else{	 
		$.beginPageLoading("计算推荐套餐...");
		var serialNum=$("#cond_SERIAL_NUMBER").val();
		var userID=$("#USER_ID").val(); 
		var params="&SERIAL_NUMBER="+serialNum+"&USER_ID="+userID+"&TELE_TIME1="+$("#TELE_TIME1").val()+"&TELE_TIME2="+$("#TELE_TIME2").val()+
		"&TELE_TIME3="+$("#TELE_TIME3").val()+"&SMS_CNT="+$("#SMS_CNT").val()+"&GPRS_CNT="+$("#GPRS_CNT").val()+"&WLAN="+$("#WLAN").val()
		+"&ALL_FEE="+$("#ALL_FEE").val();
		$.ajax.submit('', 'consum', params, '', function(data){
			$.endPageLoading(); 
			var proNew=data.get("PRODUCT_NEW");
			var desc=data.get("DESC");
			alert("重新计算成功，新的推荐套餐为：【"+proNew+"】");
			$("#PRODUCT_NEW").val(proNew);
			$("#DESC").val(desc);
		},function(code, info, detail){
			$.endPageLoading();
			MessageBox.error("错误提示","计算推荐套餐错误！", null, null, info, detail);
		});
	} 
}

function sendConsumSMS(){
	
	$.beginPageLoading("发送套餐推荐短信...");
	var prodNew=$("#PRODUCT_NEW").val();
	var prodDesc=$("#DESC").val();
	var serialNum=$("#cond_SERIAL_NUMBER").val();
	var userID=$("#USER_ID").val(); 
	var params="&SERIAL_NUMBER="+serialNum+"&USER_ID="+userID+"&PRODUCT_NEW="+prodNew+"&PRODUCT_DESC="+prodDesc;
	$.ajax.submit('', 'sendConsumSMS', params, '', function(data){
		$.endPageLoading(); 
		var smsFlag=data.get("SMS_FLAG");
		if(smsFlag=="TRUE"){
			alert("发送套餐推荐短信成功"); 
		}
	},function(code, info, detail){
		$.endPageLoading();
		MessageBox.error("错误提示","计算推荐套餐错误！", null, null, info, detail);
	});
}