
function init() {
  	var effectNow = $("#EFFECT_NOW").attr("checked");
	if (effectNow == false) {
		$('#EFFECT_NOW').attr('checked',true);
		selectedElements.isEffectNow = true;
	}
	/*
	var methodName = $('#PRODUCTPARAM_METHOD_NAME').val();
	var payEndDateObj = $('#pam_NOTIN_PAY_END_DATE');
	var payLimitFeeObj = $('#pam_NOTIN_PAY_LIMIT_FEE');
	alert(payEndDateObj);
	if(methodName=="CrtMb"){
		alert("111");
		payEndDateObj.attr('nullable','no');
		payLimitFeeObj.attr('nullable','no');
	}else{
		alert("222");
		payEndDateObj.attr('nullable','yes');
		payLimitFeeObj.attr('nullable','yes');
	}
	*/
}


function validateParamPage(methodName) {  
		
	if(methodName=='CrtUs' || methodName=='ChgUs'){
		var length = selectedElements.selectedEls.length;
		for(var i=0;i<length;i++){
			var allSelectedElements = selectedElements.selectedEls.get(i);
			var elementType = allSelectedElements.get("ELEMENT_TYPE_CODE");
			var packageId = allSelectedElements.get("PACKAGE_ID");
			var elementId = allSelectedElements.get("ELEMENT_ID");
			var state = allSelectedElements.get("MODIFY_TAG"); 
			if(elementType == "D" && (state == "0" || state == "2")) {
				var attrParams = allSelectedElements.get("ATTR_PARAM");
				if(attrParams != null && attrParams != ""){
					var size = attrParams.length;
					for(var j=0;j<size;j++){
						var oneAttr = attrParams.get(j);
						var oneAttrCode = oneAttr.get("ATTR_CODE");
						var oneAttrValue = oneAttr.get("ATTR_VALUE");
						if(oneAttrCode == "00051104" || oneAttrCode == "00051204"
						|| oneAttrCode == "00051304" || oneAttrCode == "00051404"
						|| oneAttrCode == "00051504" || oneAttrCode == "00051604"
						|| oneAttrCode == "00051704" || oneAttrCode == "00051804"
						|| oneAttrCode == "00051904" || oneAttrCode == "00052004"
						|| oneAttrCode == "00052804"){
							if(oneAttrValue == "" || oneAttrValue == null){
								alert("优惠折扣率不能为空,请填写!");
								return false;
							} else {
								var flag = $.verifylib.checkPInteger(oneAttrValue);
								if(!flag){
									alert("优惠折扣率必须是整数");
									return false;
								}
								if(oneAttrValue > 100 || oneAttrValue < 0){
									alert("优惠折扣率必须是0到100之间!");
									return false;
								}
							}
						}
					}
				}
			}
		}
	}
	
	if(methodName=='CrtMb' || methodName=='ChgMb'){
		var length = selectedElements.selectedEls.length;
		for(var i=0;i<length;i++){
			var allSelectedElements = selectedElements.selectedEls.get(i);
			var elementType = allSelectedElements.get("ELEMENT_TYPE_CODE");
			var packageId = allSelectedElements.get("PACKAGE_ID");
			var elementId = allSelectedElements.get("ELEMENT_ID");
			var state = allSelectedElements.get("MODIFY_TAG"); 
			if(methodName=='CrtMb'){
				if(state == "0" && elementType == "D" && packageId == "73440003") {
					alert("集团产品成员新增不可以添加集团自由充(限量统付)成员产品流量叠加包[73440003],流量叠加包只能在集团产品成员资料修改中新增！");
					return false;
				}
			}
		}
	}
	
	if(methodName=='CrtMb'){
		var smsFlag = $("#pam_NOTIN_SMS_FLAG").attr("checked");
		if (smsFlag == true) {
			if($("input[name='pam_NOTIN_sendForSms']:checked").val()=='1'){
				var smsInfo = $("#pam_NOTIN_SmsInfo").val();
				if(smsInfo == ""){
					alert("您选择的是个性模板短信下发,请输入短信内容！");
					return false;
				}
			}
		}
	}
	
	if(methodName=='CrtMb'){
		//var payStartDate = $("#pam_PAY_START_DATE").val();
		var payEndDate = $("#pam_NOTIN_PAY_END_DATE").val();
		if(payEndDate == "" || payEndDate == null){
			alert("生效截止时间不能为空!");
			return false;
		}
		
		var thisDate = new Date();
	    var year = thisDate.getYear().toString();  
	    var month = thisDate.getMonth()+1;
	    if (month <= 9) {
			month = "0" + month;
		}
		var day = thisDate.getDate();
		if (day <= 9) {
			day = "0" + day;
		}
		var today = year + "-" + month + "-" + day;
		
		if (payEndDate < today)
		{
			alert ("生效截止时间不能小于当前时间!");
			return false;
		}
	
		var payLimitFeeValue = $('#pam_NOTIN_PAY_LIMIT_FEE').val();
		if(payLimitFeeValue == "" || payLimitFeeValue == null){
			alert("统付流量不能为空!");
			return false;
		}
		
		var flag = $.verifylib.checkPInteger(payLimitFeeValue);
		if(!flag){
			alert("统付流量大小必须是整数!");
			return false;
		}
	}
	
	
	
	return true;
}

function changeSmsFlag(){
	var smsFlag = $("#pam_NOTIN_SMS_FLAG").attr("checked");
	if (smsFlag == true) {
		$('#pam_NOTIN_SMS_FLAG').attr('value','1');
		$('#SMSFLAGPART').css('display','');
	} else {
		$('#pam_NOTIN_SMS_FLAG').attr('value','0');
		$('#SMSFLAGPART').css('display','none');
	}
}

