
function validateParamPage(methodName) {
	
	if(methodName=='CrtMb'||methodName=='ChgMb') {
	    var ip_address = $("#pam_NOTIN_IP_ADDRESS").val();
	    if(!$.verifylib.checkIp(ip_address)){
	    	alert("IP地址格式不正确请重新输入！");
	        return false;
	    }
	}
	if(methodName=='CrtUs'||methodName=='ChgUs') {
	    var ip_address = $("#pam_NOTIN_MGR_PHONE").val();
	    if(!$.verifylib.checkMbphone(ip_address)){
	    	alert("电话号码格式不正确请重新输入！");
	        return false;
	    }
	}
	
	if(methodName=='CrtMb' || methodName=='ChgMb'){
		var length = selectedElements.selectedEls.length;
		for(var i=0;i<length;i++){
			var allSelectedElements = selectedElements.selectedEls.get(i);
			var elementType = allSelectedElements.get("ELEMENT_TYPE_CODE");
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
						if(oneAttrCode == "7050"){
							if(oneAttrValue == "" || oneAttrValue == null){
								if(mytab != null){
									mytab.switchTo("产品信息");
								}
								alert("折扣率不能为空,请填写!");
								return false;
							} else {
								var flag = $.verifylib.checkPInteger(oneAttrValue);
								if(!flag){
									if(mytab != null){
										mytab.switchTo("产品信息");
									}
									alert("折扣率必须是整数");
									return false;
								}
								if(oneAttrValue > 100 || oneAttrValue < 60){
									if(mytab != null){
										mytab.switchTo("产品信息");
									}
									alert("折扣率必选是60到100之间!");
									return false;
								}
							}
						}
					}
				}
				
			}
		}
	}
	
    return true;
}


