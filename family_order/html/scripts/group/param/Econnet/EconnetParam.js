
function init() {     
}

function validateParamPage(methodName) {

	/*
	if (methodName == "CrtMb") {
		var apnDiscnt = getElementValue("CAR_APN_DISCNT");
		if (apnDiscnt == null || apnDiscnt == "") {
			var selectElements = new Wade.DatasetList(getElement("SELECTED_ELEMENTS").value);
			for (var i=0;i<selectElements.getCount();i++) {
				var elementData=selectElements.get(i);
	   	 		var elementsDataset = elementData.get("ELEMENTS"); 
	   	 		for(var j=0;j<elementsDataset.getCount();j++){
	   	 			var svcElements = elementsDataset.get(j); 
	   	 			//EXIST DEL ADD
	   	 			var state = svcElements.get("STATE",""); 
	   	 			var elementType = svcElements.get("ELEMENT_TYPE_CODE", ""); 
	   	 			var elementId = svcElements.get("ELEMENT_ID",""); 
	   	 			//var imsdiscnt05list = apnDiscnt.split(',');
	 			    //for(var k=0;k<imsdiscnt05list.length;k++){
		         		if(elementType=="D" && elementId == '4014' && (state =='EXIST' || state=='ADD')) {
			        		alert('\u8be5\u96c6\u56e2\u672a\u8ba2\u8d2d\u8b66\u52a1\u901a\u4e13\u7f51\u5957\u9910(APN)[4013]\u5957\u9910,\u6210\u5458\u4e0d\u53ef\u8ba2\u8d2d\u8b66\u52a1\u901a\u6210\u5458\u4e13\u7f51APN\u5957\u9910[4014]!');
			         		return false;
		         		}	
	 			    //}	   	    		
	   	 		}
			}
		} else if(apnDiscnt == '4013'){
			var selectElements = new Wade.DatasetList(getElement("SELECTED_ELEMENTS").value);
			for (var i=0;i<selectElements.getCount();i++) {
				var elementData=selectElements.get(i);
	   	 		var elementsDataset = elementData.get("ELEMENTS"); 
	   	 		for(var j=0;j<elementsDataset.getCount();j++){
	   	 			var svcElements = elementsDataset.get(j); 
	   	 			//EXIST DEL ADD
	   	 			var state = svcElements.get("STATE",""); 
	   	 			var elementType = svcElements.get("ELEMENT_TYPE_CODE", ""); 
	   	 			var elementId = svcElements.get("ELEMENT_ID",""); 
	   	 			//var imsdiscnt05list = apnDiscnt.split(',');
	 			    //for(var k=0;k<imsdiscnt05list.length;k++){
		         		if(elementType=="D" && elementId != '4014' && (state =='EXIST' || state=='ADD')) {
			        		alert('\u8be5\u96c6\u56e2\u8ba2\u8d2d\u4e86\u8b66\u52a1\u901a\u4e13\u7f51\u5957\u9910(APN)[4013]\u5957\u9910,\u6210\u5458\u53ea\u80fd\u8ba2\u8d2d\u8b66\u52a1\u901a\u6210\u5458\u4e13\u7f51APN\u5957\u9910[4014]!');
			         		return false;
		         		}	
	 			    //}	   	    		
	   	 		}
			}
		}
	}
	*/
	
	if(methodName=='CrtMb'){
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

