
function init() {
	
  	var effectNow = $("#EFFECT_NOW").attr("checked");
	if (effectNow == false) {
		$('#EFFECT_NOW').attr('checked',true);
		selectedElements.isEffectNow = true;
	}
	
}


function validateParamPage(methodName) {  
		
	if(/*methodName=='CrtMb' || methodName=='ChgMb' ||*/ methodName=='CrtUs' || methodName=='ChgUs'){
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
						if(/*oneAttrCode == "7343" ||*/ oneAttrCode == "00099002" || oneAttrCode == "00052804"){
							if(oneAttrValue == "" || oneAttrValue == null){
								alert("优惠下的折扣率不能为空,请填写!");
								return false;
							} else {
								var flag = $.verifylib.checkPInteger(oneAttrValue);
								if(!flag){
									alert("折扣率必须是整数");
									return false;
								}
								if(oneAttrValue > 100 || oneAttrValue < 0){
									alert("折扣率必选是0到100之间!");
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
				if(state == "0" && elementType == "D" && packageId == "73430003") {
					alert("集团产品成员新增不可以添加集团自由充(定额统付)成员产品流量叠加包[73430003],流量叠加包只能在集团产品成员资料修改中新增！");
					return false;
				}
			}
			if(state == "0" && elementType == "D" && packageId == "73430002") {
				var endDate = allSelectedElements.get("END_DATE"); 
				
				if(endDate != null && endDate != ""){
				
					var dateLength = endDate.length;
					if(endDate != null && endDate == "2050-12-31 23:59:59"){
						if(!confirm("优惠的结束时间是到2050-12-31,确认不需要修改？")){
							return false;
						}
					}
					
					if(dateLength < 19){
						var returnValues = endDate.split(' ');
						var result = returnValues[0];
						
						if(result != null && result != ""){
							var returnDate = result.split('-');
							
							var mon = returnDate[1];
							var day = returnDate[2];
							if(mon <= 9 && mon.length != 2){
								alert("时间格式错误,请输入正确的格式!例如：2015-06-01");
								return false;
							}
							if(day <= 9 && day.length != 2){
								alert("时间格式错误,请输入正确的格式!例如：2015-06-01");
								return false;
							}
						}
					}
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
	
	/**
	if(methodName=='CrtUs'){
		var payLimitRebate = $("#pam_NOTIN_PAY_LIMIT_REBATE").val();
		if (payLimitRebate == "" || payLimitRebate == null) {
			alert("折扣率不能为空,请填写!");
			return false;
		}  else {
			var flag = $.verifylib.checkPInteger(payLimitRebate);
			if(!flag){
				alert("折扣率必须是整数");
				return false;
			}
			if(payLimitRebate > 100 || payLimitRebate < 70){
				alert("折扣率必选是70到100之间!");
				return false;
			}
		}					
	}*/
	
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

