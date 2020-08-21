
function init() {     
}

function queryApplyTypebs(){
	//alert('123');
	var vpnAttr = $('#pam_APPLY_TYPE_A').val(); 
	//alert(vpnAttr);
	
	$.beginPageLoading();
	var param = '&APPLY_TYPE_A='+vpnAttr+'&METHOD_NAME=queryApplyTypebs&CLASS_NAME=com.asiainfo.veris.crm.order.web.group.param.gfff.CreateWlwGroupMemberP';
    Wade.httphandler.submit('','com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.dynamichtml.ProxyParam','productParamInvoker',param, afterQueryApplyTypeb,errFun);
    
	/*var param = '&APPLY_TYPE_A='+vpnAttr+'&PARAM_INFO='+info; 
    $.ajax.submit('', 'queryApplyTypebs', param , 'productParamPart', function(data){ 
    	$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });*/
}

function afterQueryApplyTypeb(data) 
{
	$.endPageLoading();
	//var info = data.get('PARAM_INFO');
	//$('#info').val(info);
	var APPLY_TYPE_B_LIST = data.get('APPLY_TYPE_B_LIST');
	//alert(APPLY_TYPE_B_LIST);
	//$('#APPLY_TYPE_B_LIST').val(APPLY_TYPE_B_LIST);
	
	$('#pam_APPLY_TYPE_B').empty();
	for(var i=0; i < APPLY_TYPE_B_LIST.length; i++){
		var APPLY_TYPE_B = APPLY_TYPE_B_LIST.get(i).get('PARA_CODE1');
		var TYPE_B = APPLY_TYPE_B_LIST.get(i).get('PARA_CODE1');
		jsAddItemToSelect($('#pam_APPLY_TYPE_B'), TYPE_B, APPLY_TYPE_B);
	}
	
}

function errFun(error_code,error_info)
{
	$.endPageLoading();
	alert(error_info);
}

/**
 * 作用：下拉列表给值
 */
function jsAddItemToSelect(objSelect, objItemText, objItemValue) { 
    //判断是否存在        
    if (jsSelectIsExitItem(objSelect, objItemValue)) {        
        alert("该Item的Value值已经存在");        
    } else {   
       objSelect.append("<option selected title=\"" +objItemText+"\" value=\"" +objItemValue+"\">" +objItemText+ "</option>");
    }        
}   		

 function jsSelectIsExitItem(objSelect, objItemValue) {  
    var isExit=false; 
    objSelect.children("option").each(function(){
         if(this.value==objItemValue)
            return !isExit;
    });
   return isExit;
}

function validateParamPage(methodName) {  
	
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
