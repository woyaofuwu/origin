function initCrtUs() {
	var effectNow = $("#EFFECT_NOW").attr("checked");
	if (effectNow == false) {
		$('#EFFECT_NOW').attr('checked',true);
		selectedElements.isEffectNow = true;
	}
}

function init() {
	
	/*
	var METHOD_NAME = $('#METHOD_NAME').val();
	if(METHOD_NAME=='CrtUs'){
	  	var effectNow = $("#EFFECT_NOW").attr("checked");
		if (effectNow == false) {
			$('#EFFECT_NOW').attr('checked',true);
		}
	}
	*/
}

function validateParamPage(methodName) {  
	
	if(methodName=='CrtUs' || methodName=='ChgUs'){
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
						if(oneAttrCode == "7340"){
							if(oneAttrValue == "" || oneAttrValue == null){
								alert("折扣率不能为空,请填写!");
								return false;
							} else {
								var flag = $.verifylib.checkPInteger(oneAttrValue);
								if(!flag){
									alert("折扣率必须是整数");
									return false;
								}
								if(oneAttrValue > 100 || oneAttrValue < 1){
									alert("折扣率必选是1到100之间!");
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