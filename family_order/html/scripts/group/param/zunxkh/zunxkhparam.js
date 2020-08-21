function init() {   

}

function validateParamPage(methodName) {
	if(methodName=='CrtMb' || methodName=='ChgMb'){
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
						if(oneAttrCode == "114485"){
							if(oneAttrValue == "" || oneAttrValue == null){
								alert("请选择订购套餐的折扣，折扣范围为0-100");
								mytab.switchTo("产品信息");
								return false;
							} else {
								var flag = $.verifylib.checkPInteger(oneAttrValue);
								if(!flag){
									alert("订购套餐的折扣必须是整数");
									mytab.switchTo("产品信息");
									return false;
								}
								if(oneAttrValue > 100 || oneAttrValue < 0){
									alert("订购套餐的折扣必须是0到100之间!");
									mytab.switchTo("产品信息");
									return false;
								}
							}
						}
						//分钟数校验 || 流量校验(单位以M/G结尾)
						else if(oneAttrCode == "144485" || oneAttrCode == "124485"){
							if(allSelectedElements.get("CHECK_OK")==false){
								alert(allSelectedElements.get("CHECK_INFO"));
								mytab.switchTo("产品信息");
								return false;
							}
						}
					}
				}
			}
		}
	}
	return true;
}
/**
 * 属性弹出框确定按钮事件
 * 校验尊享语音优惠包优惠的折扣、功能费属性填值
 * @param index
 * @return
 */
function checkZxyybDiscntAttr(itemIndex){
	var tempElement = selectedElements.selectedEls.get(itemIndex);
	var monthFee = $("#18529").val();				//月功能费
	var percent = $("#18530").val();				//折扣率
	var discntCode = tempElement.get("ELEMENT_ID");	//优惠编码
	//alert(discntCode+"|"+monthFee+"|"+percent);
	//功能费校验
	if(!$.verifylib.checkPInteger(monthFee)){
		alert("功能费请填写整数！");
		return false;
	}else{
		if(!(monthFee>=0)){
			alert("功能费要求大于等于0！");
			return false;
		}
	}
	//折扣率校验
	if(!$.verifylib.checkPInteger(percent)){
		alert("折扣率请填写整数！");
		return false;
	}else{
		//2折底线优惠,84011045 尊享语音优惠包A1（2折底线,按分钟计费）,84011046 尊享语音优惠包A2（2折底线,按6秒计费）
		if("84011045"==discntCode || "84011046"==discntCode){
			if(!(percent>=20 && percent<=100)){
				alert("该优惠要求折扣必须在[20-100]之间！");
				return false;
			}
		}
		//6折底线优惠,84011047 尊享语音优惠包B1（6折底线,按分钟计费）,84011048 尊享语音优惠包B2（6折底线,按6秒计费）
		else if("84011047"==discntCode || "84011048"==discntCode){
			if(!(percent>=60 && percent<=100)){
				alert("该优惠要求折扣必须在[60-100]之间！");
				return false;
			}
		}
	}
	
	selectedElements.confirmAttr(itemIndex);
	return true;
}



 
