
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
		
		var discount = false;//true:折扣率低于6折
		var discount24 = false;//true:折扣率低于2.4折
		var length = selectedElements.selectedEls.length;
		
		for(var i=0;i<length;i++){//服务策略的拦截校验
			var allSelectedElements = selectedElements.selectedEls.get(i);
			var elementType = allSelectedElements.get("ELEMENT_TYPE_CODE");
			var elementId = allSelectedElements.get("ELEMENT_ID");
			var state = allSelectedElements.get("MODIFY_TAG");
			if(elementType == "S" && (state == "0" || state == "2"))
			{
				var serviceCodeValue = "";
				var operTypeValue = "";
				var serviceBillingTypeValue = "";
				var serviceUsageStateValue = "";
				var scStartDateTime = "";
				var scEndDateTime = "";
				if(elementId == "99011005" || elementId == "99011012" || elementId == "99011021" ||
					elementId == "99011022" || elementId == "99011028" || elementId == "99011029" ||
					elementId == "99011024" || elementId == "99011025")
				{
					var attrParams = allSelectedElements.get("ATTR_PARAM");
					if(attrParams != null && attrParams != ""){
						var size = attrParams.length;
						for(var j=0;j<size;j++){
							var oneAttr = attrParams.get(j);
							var oneAttrCode = oneAttr.get("ATTR_CODE");
							var oneAttrValue = oneAttr.get("ATTR_VALUE");
							if(oneAttrCode == "ServiceCode"){
								if(oneAttrValue != null && oneAttrValue != "")
								{
									serviceCodeValue = oneAttrValue;
								}
							}
							else if(oneAttrCode == "OperType")
							{
								if(oneAttrValue != null && oneAttrValue != "")
								{
									operTypeValue = oneAttrValue;
								}
							}
							else if(oneAttrCode == "ServiceBillingType")
							{
								if(oneAttrValue != null && oneAttrValue != "")
								{
									serviceBillingTypeValue = oneAttrValue;
								}
							}
							else if(oneAttrCode == "ServiceUsageState")
							{
								if(oneAttrValue != null && oneAttrValue != "")
								{
									serviceUsageStateValue = oneAttrValue;
								}
							}
							else if(oneAttrCode == "ServiceStartDateTime")
							{
								if(oneAttrValue != null && oneAttrValue != "")
								{
									scStartDateTime = oneAttrValue;
								}
							}
							else if(oneAttrCode == "ServiceEndDateTime")
							{
								if(oneAttrValue != null && oneAttrValue != "")
								{
									scEndDateTime = oneAttrValue;
								}
							}
						}
					}
					
					if(serviceCodeValue != "" || operTypeValue != "" || 
						serviceBillingTypeValue!="" || serviceUsageStateValue !="" || scStartDateTime != "" ||
						scEndDateTime != "")
					{
						if(!(serviceCodeValue != "" && operTypeValue != "" 
							&& serviceBillingTypeValue != "" && serviceUsageStateValue !=""))
						{
							if(mytab != null){
								mytab.switchTo("产品信息");
							}
							alert("请把该服务" + elementId + "策略的属性值填写完,订购业务唯一代码、操作类型、计费方式、配额状态!");
							return false;
						}
					}
				}
			}
		}
		
		debugger;
		//先校验对应服务的appname
		for(var i=0;i<length;i++){
			var allSelectedElements = selectedElements.selectedEls.get(i);
			var elementType = allSelectedElements.get("ELEMENT_TYPE_CODE");
			var elementId = allSelectedElements.get("ELEMENT_ID");
			var state = allSelectedElements.get("MODIFY_TAG");
			if(elementType == "S" && (state == "0" || state == "2" || state == "exist"))
			{
				if(elementId == "99011022" || elementId ==  "99011028"
					|| elementId == "99011021" || elementId == "99011029")
				{
					var attrParams = allSelectedElements.get("ATTR_PARAM");
					if(attrParams != null && attrParams != ""){
						var size = attrParams.length;
						for(var j=0;j<size;j++){
							var oneAttr = attrParams.get(j);
							var oneAttrCode = oneAttr.get("ATTR_CODE");
							var oneAttrValue = oneAttr.get("ATTR_VALUE");
							if(oneAttrCode == "APNNAME"){
								if(oneAttrValue == null || oneAttrValue == ""){
									if(mytab != null){
										mytab.switchTo("产品信息");
									}
									alert("APNNAME不能为空,请填写!");
									return false;
								}
							}
						}
					}
					
				}
			}
		}
		
		debugger;
		//定向专线的apnname校验-重写
		for(var i=0;i<length;i++){
			var allSelectedElements = selectedElements.selectedEls.get(i);
			var elementType = allSelectedElements.get("ELEMENT_TYPE_CODE");
			var elementId = allSelectedElements.get("ELEMENT_ID");
			var state = allSelectedElements.get("MODIFY_TAG"); 
			var packageId = allSelectedElements.get("PACKAGE_ID");
		
			if(elementType == "D" && (state == "0" || state == "2" || state == "exist")) {
				if(packageId == "70000005" || packageId == "70000009" || packageId == "70000012"
					|| packageId == "70000008" || packageId == "70000011" || packageId == "70000013"){
					var attrParams = allSelectedElements.get("ATTR_PARAM");
					if(attrParams != null && attrParams != ""){
						var size = attrParams.length;
						for(var j=0;j<size;j++){
							var oneAttr = attrParams.get(j);
							var oneAttrCode = oneAttr.get("ATTR_CODE");
							var oneAttrValue = oneAttr.get("ATTR_VALUE");//优惠的apnname
							if(oneAttrCode == "APNNAME")
							{//--1--
								if(oneAttrValue == null || oneAttrValue == ""){
									if(mytab != null){
										mytab.switchTo("产品信息");
									}
									alert("APNNAME不能为空,请填写!");
									return false;
								}
								
								if(packageId == "70000008" || packageId == "70000011" || packageId == "70000013"){//定向优惠包
									var breakFlag = false;//标识
									
									//优惠中的appname,在对应的服务中一定要存在
									for(var k=0;k<length;k++){
										var allSelectedElementSVC = selectedElements.selectedEls.get(k);
										var elementTypeSVC = allSelectedElementSVC.get("ELEMENT_TYPE_CODE");
										var elementIdSVC = allSelectedElementSVC.get("ELEMENT_ID");
										var stateSVC = allSelectedElementSVC.get("MODIFY_TAG"); 
										if(elementTypeSVC == "S" && (stateSVC == "0" || stateSVC == "2" || stateSVC == "exist"))
										{
											if(elementIdSVC == "99011021" || elementIdSVC == "99011029")
											{
												var attrSvcParams = allSelectedElementSVC.get("ATTR_PARAM");
												if(attrSvcParams != null && attrSvcParams != ""){
													var sizeSVC = attrSvcParams.length;
													for(var h=0;h<sizeSVC;h++){
														var oneAttrSvc = attrSvcParams.get(h);
														var oneAttrCodeSvc = oneAttrSvc.get("ATTR_CODE");
														var oneAttrValueSvc = oneAttrSvc.get("ATTR_VALUE");
														if(oneAttrCodeSvc == "APNNAME"){
															if(oneAttrValueSvc == oneAttrValue){
																breakFlag = true;//优惠的appname在服务中存在
																break;
															}
														}
														
													}
												}
											}
										}
									}
									debugger;
									if(breakFlag == true){
										break;//退出外循环
									}
									else 
									{
										if(mytab != null){
											mytab.switchTo("产品信息");
										}
										alert("定向流量包下的优惠的APNNAME与对应的服务的APNNAME不一样!请核实重新填写!");
										return false;
									}
								
								}
								else if(packageId == "70000005" || packageId == "70000009" || packageId == "70000012"){//通用优惠包
									var breakFlag = false;//标识
									
									//优惠中的appname,在对应的服务中一定要存在
									for(var k=0;k<length;k++){
										var allSelectedElementSVC = selectedElements.selectedEls.get(k);
										var elementTypeSVC = allSelectedElementSVC.get("ELEMENT_TYPE_CODE");
										var elementIdSVC = allSelectedElementSVC.get("ELEMENT_ID");
										var stateSVC = allSelectedElementSVC.get("MODIFY_TAG"); 
										if(elementTypeSVC == "S" && (stateSVC == "0" || stateSVC == "2" || stateSVC == "exist"))
										{
											if(elementIdSVC == "99011022" || elementIdSVC == "99011028")
											{
												var attrSvcParams = allSelectedElementSVC.get("ATTR_PARAM");
												if(attrSvcParams != null && attrSvcParams != ""){
													var sizeSVC = attrSvcParams.length;
													for(var h=0;h<sizeSVC;h++){
														var oneAttrSvc = attrSvcParams.get(h);
														var oneAttrCodeSvc = oneAttrSvc.get("ATTR_CODE");
														var oneAttrValueSvc = oneAttrSvc.get("ATTR_VALUE");
														if(oneAttrCodeSvc == "APNNAME"){
															if(oneAttrValueSvc == oneAttrValue){
																breakFlag = true;//优惠的appname在服务中存在
																break;
															}
														}
														
													}
												}
											}
										}
									}
									debugger;
									if(breakFlag == true){
										break;//退出外循环
									}
									else 
									{
										if(mytab != null){
											mytab.switchTo("产品信息");
										}
										alert("通用流量包下的优惠的APNNAME与对应的服务的APNNAME不一样!请核实重新填写!");
										return false;
									}
								}
								
								
							}//--1--
						}
					}
				}
			}
			
		}
		
		for(var i=0;i<length;i++){
			var allSelectedElements = selectedElements.selectedEls.get(i);
			var elementType = allSelectedElements.get("ELEMENT_TYPE_CODE");
			var elementId = allSelectedElements.get("ELEMENT_ID");
			var state = allSelectedElements.get("MODIFY_TAG"); 
			var packageId = allSelectedElements.get("PACKAGE_ID");
		
			if(elementType == "D" && (state == "0" || state == "2" || state == "exist")) {
				if(packageId == "70000005" || packageId == "70000009" || packageId == "70000012"
					|| packageId == "70000008" || packageId == "70000011" || packageId == "70000013"){
					var attrParams = allSelectedElements.get("ATTR_PARAM");
					if(attrParams != null && attrParams != ""){
						var size = attrParams.length;
						for(var j=0;j<size;j++){ //先找折扣率
							var oneAttr = attrParams.get(j);
							var oneAttrCode = oneAttr.get("ATTR_CODE");
							var oneAttrValue = oneAttr.get("ATTR_VALUE");
							if(oneAttrCode == "Discount"){
								discount = false;
								if(oneAttrValue == null || oneAttrValue == ""){
									if(mytab != null){
										mytab.switchTo("产品信息");
									}
									alert("优惠的折扣率不能为空,请填写!");
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
									if(oneAttrValue > 100 || oneAttrValue < 1){
										if(mytab != null){
											mytab.switchTo("产品信息");
										}
										alert("折扣率必须是1到100之间的整数!");
										return false;
									}
									if(oneAttrValue < 60){
										discount = true;
									}
								}
							}
							//-------add by chenzg@20171211---REQ201711150003关于新增物联卡本省折扣需求---begin---------
							/*本省折扣率校验*/
							else if(oneAttrCode == "20171211"){
								//本省折扣率有值才做校验，没值则不做校验
								if($.trim(oneAttrValue).length != 0){
									var flag = $.verifylib.checkPInteger(oneAttrValue);
									if(!flag){
										if(mytab != null){
											mytab.switchTo("产品信息");
										}
										alert("本省折扣率必须是整数");
										return false;
									}
									if(oneAttrValue > 100 || oneAttrValue < 0){
										if(mytab != null){
											mytab.switchTo("产品信息");
										}
										alert("本省折扣率必须是0到100之间的整数!");
										return false;
									}
									//当本省折扣内填写折扣后，原有折扣窗口默认100
									setAttrVale(attrParams, "Discount", "100");
									//当本省折扣低于6折时，判断审批工单号是否填写
									if(oneAttrValue<60){
										var audiInfo = getAttrVale(attrParams, "AudiInfo0898");
										if(audiInfo==null || $.trim(audiInfo) == ""){
											if(mytab != null){
												mytab.switchTo("产品信息");
											}
											alert("本省折扣低于6折，请填写审批工单号及名称信息！");
											return false;
										}
									}
								}
								
							}
							//-------add by chenzg@20171211---REQ201711150003关于新增物联卡本省折扣需求---end-----------
						}
						for(var j=0;j<size;j++){
							var oneAttr = attrParams.get(j);
							var oneAttrCode = oneAttr.get("ATTR_CODE");
							var oneAttrValue = oneAttr.get("ATTR_VALUE");
							if(oneAttrCode == "Discount"){ //折扣率校验
								if(oneAttrValue == null || oneAttrValue == ""){
									if(mytab != null){
										mytab.switchTo("产品信息");
									}
									alert("优惠的折扣率不能为空,请填写!");
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
									if(oneAttrValue > 100 || oneAttrValue < 1){
										if(mytab != null){
											mytab.switchTo("产品信息");
										}
										alert("折扣率必须是1到100之间的整数!");
										return false;
									}
									if(oneAttrValue < 60){
										discount = true;
									}
								}
							} else if(oneAttrCode == "PromiseUseMonths"){//在网时间校验
								if(oneAttrValue != null && oneAttrValue != ""){
									var flag = $.verifylib.checkPInteger(oneAttrValue);
									if(!flag){
										if(mytab != null){
											mytab.switchTo("产品信息");
										}
										alert("在网时间必须是整数");
										return false;
									}
									//var values = parseInt(oneAttrValue)%24;
									var values = parseInt(oneAttrValue);
									if(values < 24){
										if(mytab != null){
											mytab.switchTo("产品信息");
										}
										alert("在网时间必须是大于等于24的整数!请重新填写");
										return false;
									}
								} else {
									if(discount == true){
										if(mytab != null){
											mytab.switchTo("产品信息");
										}
										alert("折扣率低于6折!在网时间必须填写");
										return false;
									}
								}
							} else if(oneAttrCode == "CanShare"){//是否共享校验
								if(oneAttrValue == null || oneAttrValue == ""){
									if(mytab != null){
										mytab.switchTo("产品信息");
									}
									alert("是否可共享不能为空,请选择!");
									return false;
								}
							} else if(oneAttrCode == "MinimumOfYear"){//年承诺收入（元）
								if(oneAttrValue != null && oneAttrValue != ""){
									var flag = $.verifylib.checkPInteger(oneAttrValue);
									if(!flag){
										if(mytab != null){
											mytab.switchTo("产品信息");
										}
										alert("年承诺收入(元)必须是整数!");
										return false;
									}
									var values = parseInt(oneAttrValue);
									if(values < 50000){
										if(mytab != null){
											mytab.switchTo("产品信息");
										}
										alert("年承诺收入(元)必须是大于等于50000!请重新填写");
										return false;
									}
								} else {
									if(discount == true){
										if(mytab != null){
											mytab.switchTo("产品信息");
										}
										alert("折扣率低于6折!年承诺收入(元)必须填写");
										return false;
									}
								}
							} else if(oneAttrCode == "BatchAccounts"){//入网用户数(张)
								if(oneAttrValue != null && oneAttrValue != ""){
									var flag = $.verifylib.checkPInteger(oneAttrValue);
									if(!flag){
										if(mytab != null){
											mytab.switchTo("产品信息");
										}
										alert("入网用户数(张)必须是整数!");
										return false;
									}
									var values = parseInt(oneAttrValue);
									if(values < 50000){
										if(mytab != null){
											mytab.switchTo("产品信息");
										}
										alert("入网用户数(张)必须是大于等于50000!请重新填写");
										return false;
									}
								} else {
									if(discount == true){
										if(mytab != null){
											mytab.switchTo("产品信息");
										}
										alert("折扣率低于6折!入网用户数(张)必须填写");
										return false;
									}
								}
							} else if(oneAttrCode == "ApprovalNum"){//审批文号
								if(oneAttrValue == null || oneAttrValue == ""){
									if(discount == true){
										if(mytab != null){
											mytab.switchTo("产品信息");
										}
										alert("折扣率低于6折!审批文号必须填写");
										return false;
									}
								}
							}
							
						}
					}
				}
				//-----------add by chenzg@20171220--NB-IOT流量产品包属性校验,41003605----begin-------
				if(packageId == "41003605"){
					var attrParams = allSelectedElements.get("ATTR_PARAM");
					debugger;
					if(attrParams != null && attrParams != ""){
						var size = attrParams.length;
						for(var j=0;j<size;j++){ //先找折扣率
							var oneAttr = attrParams.get(j);
							var oneAttrCode = oneAttr.get("ATTR_CODE");
							var oneAttrValue = oneAttr.get("ATTR_VALUE");
							if(oneAttrCode == "Discount"){
								discount = false;
								discount24 = false;
								if(oneAttrValue == null || oneAttrValue == ""){
									if(mytab != null){
										mytab.switchTo("产品信息");
									}
									alert("优惠的折扣率不能为空,请填写!");
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
									if(oneAttrValue > 100 || oneAttrValue < 1){
										if(mytab != null){
											mytab.switchTo("产品信息");
										}
										alert("折扣率必须是1到100之间的整数!");
										return false;
									}
									if(oneAttrValue < 60){
										discount = true;
									}
									if(oneAttrValue < 24){
										discount24 = true;
									}
								}
							}
						}
						debugger;
						for(var j=0;j<size;j++){
							var oneAttr = attrParams.get(j);
							var oneAttrCode = oneAttr.get("ATTR_CODE");
							var oneAttrValue = oneAttr.get("ATTR_VALUE");
							if(oneAttrCode == "APNNAME"){//appname的校验
								if(oneAttrValue == null || oneAttrValue == ""){
									if(mytab != null){
										mytab.switchTo("产品信息");
									}
									alert("APNNAME不能为空,请填写!");
									return false;
								}
							} else if(oneAttrCode == "Discount"){ //折扣率校验
								if(oneAttrValue == null || oneAttrValue == ""){
									if(mytab != null){
										mytab.switchTo("产品信息");
									}
									alert("优惠的折扣率不能为空,请填写!");
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
									if(oneAttrValue > 100 || oneAttrValue < 1){
										if(mytab != null){
											mytab.switchTo("产品信息");
										}
										alert("折扣率必须是1到100之间的整数!");
										return false;
									}
									if(oneAttrValue < 60){
										discount = true;
									}
									if(oneAttrValue < 24){
										discount24 = true;
									}
								}
							} else if(oneAttrCode == "PromiseUseMonths"){//在网时间校验
								if(oneAttrValue != null && oneAttrValue != ""){
									var flag = $.verifylib.checkPInteger(oneAttrValue);
									if(!flag){
										if(mytab != null){
											mytab.switchTo("产品信息");
										}
										alert("在网时间必须是整数");
										return false;
									}
									//var values = parseInt(oneAttrValue)%24;
									var values = parseInt(oneAttrValue);
									if(values < 24){
										if(mytab != null){
											mytab.switchTo("产品信息");
										}
										alert("在网时间必须是大于等于24的整数!请重新填写");
										return false;
									}
								} else {
									if(discount == true){
										if(mytab != null){
											mytab.switchTo("产品信息");
										}
										alert("折扣率低于6折!在网时间必须填写");
										return false;
									}
								}
							} else if(oneAttrCode == "CanShare"){//是否共享校验
								oneAttr.put("ATTR_VALUE", "0"); //填0,暂不支持流量共享
							} else if(oneAttrCode == "BatchAccounts"){//入网用户数(张)
								if(oneAttrValue != null && oneAttrValue != ""){
									var flag = $.verifylib.checkPInteger(oneAttrValue);
									if(!flag){
										if(mytab != null){
											mytab.switchTo("产品信息");
										}
										alert("入网用户数(张)必须是整数!");
										return false;
									}
									var values = parseInt(oneAttrValue);
									if(values < 0){
										if(mytab != null){
											mytab.switchTo("产品信息");
										}
										alert("入网用户数(张)必须是大于0!请重新填写");
										return false;
									}
								} else {
									if(discount == true){
										if(mytab != null){
											mytab.switchTo("产品信息");
										}
										alert("折扣率低于6折!入网用户数(张)必须填写");
										return false;
									}
								}
							} else if(oneAttrCode == "ApprovalNum"){//审批文号
								if(oneAttrValue == null || oneAttrValue == ""){
									//if(discount == true){
									if(discount24 == true){
										if(mytab != null){
											mytab.switchTo("产品信息");
										}
										//alert("折扣率低于6折!审批文号必须填写");
										alert("折扣率低于2.4折!审批文号必须填写!");
										return false;
									}
								}
							}
							
						}
					}
				}
				//-----------add by chenzg@20171220--NB-IOT流量产品包属性校验,41003605----end-------
				
			}
			
			
		}
		
		//---------------add by chenzg@20180712-BUG20180706172625关于物联网NB-IOT短信基础通信服务依赖拦截的优化-begin---------------
		/*校验218302,NB-IOT短信基础通信服务依赖的优惠是否办理，
		 * 短信基础通信服务依赖于NB-IOT短信年包或测试期套餐*/
		if(methodName=='CrtMb'){
			for(var i=0;i<length;i++){
				var allSelectedElements = selectedElements.selectedEls.get(i);
				var elementType = allSelectedElements.get("ELEMENT_TYPE_CODE");
				var elementId = allSelectedElements.get("ELEMENT_ID");
				var state = allSelectedElements.get("MODIFY_TAG"); 
				if(elementType == "S" && elementId == "1218302" && state == "0"){
					var selDiscnts = false;
					for(var j=0;j<selectedElements.selectedEls.length;j++){
						var each = selectedElements.selectedEls.get(j);
						var eType = each.get("ELEMENT_TYPE_CODE");
						var eId = each.get("ELEMENT_ID");
						var eState = each.get("MODIFY_TAG"); 
						var ePkgId = each.get("PACKAGE_ID");
						if(eType == "D" && (ePkgId=="41003608" || ePkgId=="41003606") && (eState == "0" || eState == "2" || eState == "exist")) {
							selDiscnts = true;	//选了NB-IOT短信年包或测试期套餐
							break;
						}
					}
					if(selDiscnts == false){
						alert("短信基础通信服务依赖于NB-IOT短信年包或测试期套餐，请选择！");
						if(mytab != null){
							mytab.switchTo("产品信息");
						}
						return false;
					}
				} 
				//NB-IOT数据通信服务依赖于NB-IOT流量年包（A档或B档）或测试期套餐
				else if(elementType == "S" && elementId == "1218301" && state == "0"){
					var selDiscnts = false;
					for(var j=0;j<selectedElements.selectedEls.length;j++){
						var each = selectedElements.selectedEls.get(j);
						var eType = each.get("ELEMENT_TYPE_CODE");
						var eId = each.get("ELEMENT_ID");
						var eState = each.get("MODIFY_TAG"); 
						var ePkgId = each.get("PACKAGE_ID");
						if(eType == "D" && (ePkgId=="41003605" || ePkgId=="41003608") && (eState == "0" || eState == "2" || eState == "exist")) {
							selDiscnts = true;	//选了NB-IOT流量年包（A档或B档）或测试期套餐
							break;
						}
					}
					if(selDiscnts == false){
						alert("数据通信服务依赖于流量年包（A档或B档）或测试期套餐，请选择！");
						if(mytab != null){
							mytab.switchTo("产品信息");
						}
						return false;
					}
				}
			}
		}		
		//---------------add by chenzg@20180712-BUG20180706172625关于物联网NB-IOT短信基础通信服务依赖拦截的优化-end-----------------
		//2019-byyuanza -SREQ201904080043  办理物联网集团成员加套餐时，审批文号不要填写符号、中文、特殊字符等，只填写数字或英文 --START
		for (var i=0;i<length;i++){
			var allSelectedElements = selectedElements.selectedEls.get(i);
			var elementType = allSelectedElements.get("ELEMENT_TYPE_CODE");
			var elementId = allSelectedElements.get("ELEMENT_ID");
			var state = allSelectedElements.get("MODIFY_TAG"); 
			var productid = allSelectedElements.get("PRODUCT_ID");
			if(elementType == "D" && (state == "0" || state == "2" || state == "exist")){
				if(productid == "20171215"|| productid == "20005014"
					|| productid == "20161125"|| productid == "20161123" ){
					var attrParams = allSelectedElements.get("ATTR_PARAM");
					if(attrParams != null && attrParams != ""){
						var size = attrParams.length;
						for(var j=0;j<size;j++){
							var oneAttr = attrParams.get(j);
							var oneAttrCode = oneAttr.get("ATTR_CODE");
							var oneAttrValue = oneAttr.get("ATTR_VALUE");
							if(oneAttrCode == "ApprovalNum" && oneAttrValue != ""){
								var grep = /^[A-Za-z0-9]+$/;
								if (!grep.test(oneAttrValue)){
									$.showWarnMessage("错误提醒", "审批文号只能填写数字或英文字母,请重新输入！");
									return false;
								   	}
								break;//一个套餐只有一个审批文号，不再做循坏了
							}
						}
					}
				}
			}
		}
		// -END
		
		
		
	}
	
	if(methodName=='CrtUs' || methodName=='ChgUs'){
		var length = selectedElements.selectedEls.length;
		var groupType = "0";
		
		for(var i=0;i<length;i++){//先查找集团智能网语音通信服务的企业类型
			var allSelectedElements = selectedElements.selectedEls.get(i);
			var elementType = allSelectedElements.get("ELEMENT_TYPE_CODE");
			var elementId = allSelectedElements.get("ELEMENT_ID");
			var state = allSelectedElements.get("MODIFY_TAG");
			if(elementType == "S" && (state == "0" || state == "2" || state == "exist") && elementId == "99011015")
			{
				var serviceCodeValue = "";
				var operTypeValue = "";
				var serviceBillingTypeValue = "";
				var serviceUsageStateValue = "";
				var attrParams = allSelectedElements.get("ATTR_PARAM");
				if(attrParams != null && attrParams != ""){
					var size = attrParams.length;
					for(var j=0;j<size;j++){
						var oneAttr = attrParams.get(j);
						var oneAttrCode = oneAttr.get("ATTR_CODE");
						var oneAttrValue = oneAttr.get("ATTR_VALUE");
						if(oneAttrCode == "GroupType"){
							if(oneAttrValue == "2"){
								groupType = "2";//集群类企业
							}else if(oneAttrValue == "1"){
								groupType = "1";//普通企业
							}
						}
					}
				}
			}
		}
		
		for(var i=0;i<length;i++){//集团智能网语音通信服务校验
			var allSelectedElements = selectedElements.selectedEls.get(i);
			var elementType = allSelectedElements.get("ELEMENT_TYPE_CODE");
			var elementId = allSelectedElements.get("ELEMENT_ID");
			var state = allSelectedElements.get("MODIFY_TAG");
			if(elementType == "S" && (state == "0" || state == "2" || state == "exist") && elementId == "99011015")
			{
				var serviceCodeValue = "";
				var operTypeValue = "";
				var serviceBillingTypeValue = "";
				var serviceUsageStateValue = "";
				var attrParams = allSelectedElements.get("ATTR_PARAM");
				if(attrParams != null && attrParams != ""){
					var size = attrParams.length;
					for(var j=0;j<size;j++){
						var oneAttr = attrParams.get(j);
						var oneAttrCode = oneAttr.get("ATTR_CODE");
						var oneAttrValue = oneAttr.get("ATTR_VALUE");
						if(oneAttrCode == "ForwardFlag"){
							if(groupType == "2" && oneAttrValue !="0"){
								if(mytab != null){
									mytab.switchTo("产品信息");
								}
								alert("企业类型是集群类企业时,自动前转只能选择不支持!");
								return false;
							}
						}else if(oneAttrCode == "GroupAccount"){
							if(groupType == "2" && oneAttrValue !="" && oneAttrValue !="0"){
								if(mytab != null){
									mytab.switchTo("产品信息");
								}
								alert("企业类型是集群类企业时,通话阀值只能填0!");
								return false;
							}
						}else if(oneAttrCode == "LockFlag"){
							if(groupType == "2" && oneAttrValue !=""){
								if(mytab != null){
									mytab.switchTo("产品信息");
								}
								alert("企业类型是集群类企业时,被叫闭锁不能选择!");
								return false;
							}
						}
					}
				}
			}
		}
		
	}
	
	if(methodName=='CrtMb' || methodName=='ChgMb'){
		var groupType = $('#GROUP_TYPE').val(); //集团企业类型
		var userFlag = "2";//用户类别
		var userAreaFlag = "3";
		
		var length = selectedElements.selectedEls.length;
		
		for(var i=0;i<length;i++){//先查找个人智能网语音通信服务,取相应的属性值
			var allSelectedElements = selectedElements.selectedEls.get(i);
			var elementType = allSelectedElements.get("ELEMENT_TYPE_CODE");
			var elementId = allSelectedElements.get("ELEMENT_ID");
			var state = allSelectedElements.get("MODIFY_TAG");
			if(elementType == "S" && (state == "0" || state == "2" || state == "exist")
				&& elementId == "99011019")
			{
				var serviceCodeValue = "";
				var operTypeValue = "";
				var serviceBillingTypeValue = "";
				var serviceUsageStateValue = "";
				var attrParams = allSelectedElements.get("ATTR_PARAM");
				if(attrParams != null && attrParams != ""){
					var size = attrParams.length;
					for(var j=0;j<size;j++){
						var oneAttr = attrParams.get(j);
						var oneAttrCode = oneAttr.get("ATTR_CODE");
						var oneAttrValue = oneAttr.get("ATTR_VALUE");
						if(oneAttrCode == "UserFlag" && oneAttrValue != null && oneAttrValue != ""){
							userFlag = oneAttrValue;
						} else if(oneAttrCode == "userAreaFlag" && oneAttrValue != null && oneAttrValue != ""){
							userAreaFlag = oneAttrValue;
						}
					}
				}
			}
		}
		
		for(var i=0;i<length;i++){//对个人智能网语音通信服务的一些属性限制
			var allSelectedElements = selectedElements.selectedEls.get(i);
			var elementType = allSelectedElements.get("ELEMENT_TYPE_CODE");
			var elementId = allSelectedElements.get("ELEMENT_ID");
			var state = allSelectedElements.get("MODIFY_TAG");
			if(elementType == "S" && (state == "0" || state == "2" || state == "exist")
				&& elementId == "99011019")
			{
				var serviceCodeValue = "";
				var operTypeValue = "";
				var serviceBillingTypeValue = "";
				var serviceUsageStateValue = "";
				var attrParams = allSelectedElements.get("ATTR_PARAM");
				if(attrParams != null && attrParams != ""){
					var size = attrParams.length;
					for(var j=0;j<size;j++){
						var oneAttr = attrParams.get(j);
						var oneAttrCode = oneAttr.get("ATTR_CODE");
						var oneAttrValue = oneAttr.get("ATTR_VALUE");
						if(oneAttrCode == "IDDFlag" && userFlag == "1" && oneAttrValue == ""){
							if(mytab != null){
								mytab.switchTo("产品信息");
							}
							alert("用户类别为企业客户成员时,企业客户成员国际长途权限必选,不能为空!");
							return false;
						}else if(oneAttrCode == "AutoForwardFlag"){
							if(userFlag == "1" && oneAttrValue == ""){
								if(mytab != null){
									mytab.switchTo("产品信息");
								}
								alert("用户类别为企业客户成员时,是否支持自动前转必选,不能为空!");
								return false;
							}
							if(groupType == "2" && oneAttrValue == "1" && oneAttrValue != ""){
								if(mytab != null){
									mytab.switchTo("产品信息");
								}
								alert("该集团产品的企业类型是集群类企业,是否支持自动前转只能选择不支持选项!");
								return false;
							}
						}else if(oneAttrCode == "RoamingFlag"  && userFlag == "1" && oneAttrValue == ""){ 
							if(mytab != null){
								mytab.switchTo("产品信息");
							}
							alert("用户类别为企业客户成员时,国际漫游权限必选,不能为空!");
							return false;
						} else if(oneAttrCode == "UserClass"){
							if(userFlag == "1" && (oneAttrValue == null || oneAttrValue == "")){
								if(mytab != null){
									mytab.switchTo("产品信息");
								}
								alert("用户类别为企业客户成员时,通话阀值的级别必填,不能为空!");
								return false;
							}
							if(oneAttrValue != null && oneAttrValue != ""){
								var flag = $.verifylib.checkPInteger(oneAttrValue);
								if(!flag){
									if(mytab != null){
										mytab.switchTo("产品信息");
									}
									alert("通话阀值的级别必须是整数!");
									return false;
								}
								var values = parseInt(oneAttrValue);
								if(values <= 0){
									if(mytab != null){
										mytab.switchTo("产品信息");
									}
									alert("通话阀值的级别必须是大于0!请重新填写");
									return false;
								}
							}
						} else if(oneAttrCode == "LockFlag"){
							if(groupType == "2" && oneAttrValue != null && oneAttrValue != ""){
								if(mytab != null){
									mytab.switchTo("产品信息");
								}
								alert("该集团产品的企业类型是集群类企业,被叫闭锁不能选择!");
								return false;
							}
						} else if(oneAttrCode == "userShutFlag"){
							if(groupType == "1" && oneAttrValue != null && oneAttrValue != ""){
								if(mytab != null){
									mytab.switchTo("产品信息");
								}
								alert("该集团产品的企业类型是普通企业,客户成员闭锁功能标识不能选择!");
								return false;
							}
						} else if(oneAttrCode == "userWhiteNumFlag"){
							if(groupType == "2" && oneAttrValue != null && oneAttrValue != ""){
								if(mytab != null){
									mytab.switchTo("产品信息");
								}
								alert("该集团产品的企业类型是集群类企业,白名单功能状态不能选择!");
								return false;
							}
						} else if(oneAttrCode == "userAreaOperType"){
							if(userAreaFlag != "1" && oneAttrValue != null && oneAttrValue != ""){
								if(mytab != null){
									mytab.switchTo("产品信息");
								}
								alert("成员区域限制标识选项不是多省漫游,限制区域操作类型不能选择!");
								return false;
							} else if(userAreaFlag == "1" && (oneAttrValue == null || oneAttrValue == "")){
								if(mytab != null){
									mytab.switchTo("产品信息");
								}
								alert("成员区域限制标识选项是多省漫游,限制区域操作类型不能为空!");
								return false;
							}
						} else if(oneAttrCode == "userArea"){
							if(userAreaFlag != "1" && oneAttrValue != null && oneAttrValue != ""){
								if(mytab != null){
									mytab.switchTo("产品信息");
								}
								alert("成员区域限制标识选项不是多省漫游,限制区域列表不能填写!");
								return false;
							}else if(userAreaFlag == "1" && (oneAttrValue == null || oneAttrValue == "")){
								if(mytab != null){
									mytab.switchTo("产品信息");
								}
								alert("成员区域限制标识选项是多省漫游,限制区域列表不能为空");
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
 * 修改属性值
 * @param attrParams
 * @param attrCode
 * @param attrVal
 * @return
 */
function setAttrVale(attrParams, attrCode, attrVal){
	//alert("setAttrVale attrParams="+attrParams);
	for(var i=0;i<attrParams.length;i++){
		var param = attrParams.get(i);
		var oneAttrCode = param.get("ATTR_CODE");
		var oneAttrValue = param.get("ATTR_VALUE");
		if(oneAttrCode == attrCode){
			param.put("ATTR_VALUE",attrVal);
		}
	}
}
/**
 * 取属性值
 * @param attrParams
 * @param attrCode
 * @return
 */
function getAttrVale(attrParams, attrCode){
	var attrVal = "";
	//alert("getAttrVale attrParams="+attrParams);
	for(var i=0;i<attrParams.length;i++){
		var param = attrParams.get(i);
		var oneAttrCode = param.get("ATTR_CODE");
		var oneAttrValue = param.get("ATTR_VALUE");
		if(oneAttrCode == attrCode){
			attrVal = oneAttrValue;
			break;
		}
	}
	
	return attrVal;
}

/**
 * NBIOT物联网产品
 * 1218301 NB-IOT数据通信服务
 * 选择APN类型处理
 * @return
 */
function apnTypeChanged() {
	var apnType = $("#APNTYPE").val();
	$("#COMMONAPN").val("");	
	$("#APNNAME").val("");			
	$("#LOWPOWERMODE").val("");
	$("#RAUTAUTIMER").val("");
	$("#APNNAME").attr("readonly", false);
	$("#LOWPOWERMODE").attr("disabled", false);
	//通用APN
	if ("0" == apnType) {
		$("#COMMONAPN").attr("nullable", "yes");
		$.each($("#COMMONAPN").parents("li[class=li]"), function(index, obj){
			$(obj).attr("style", "");
		});
		//通用APN类型，APNNAME只能选，不能填
		$("#APNNAME").attr("readonly", true);	
	} 
	//专用APN
	else if("1" == apnType)
	{
		$("#COMMONAPN").attr("nullable", "yes");
		$.each($("#COMMONAPN").parents("li[class=li]"), function(index, obj){
			$(obj).attr("style", "display:none");
		});
		//专用APN类型，APNNAME只能填，不能选
		$("#APNNAME").attr("readonly", false);	
	}
}
/**
 * NBIOT物联网产品
 * 1218301 NB-IOT数据通信服务
 * 选择通用APN名称时处理
 * @return
 */
function commonApnSelected() {
	var commonApnName = $("#COMMONAPN").val();
	if ("" == commonApnName || null == commonApnName) {
		return;
	}
	var apnType = $("#APNTYPE").val();
	if ("" == apnType || null == apnType) {
		alert("请先选择APN类型");
		$("#COMMONAPN").val("");
		return;
	}
	// 将值写入APNNAME属性，
	$("#APNNAME").val(commonApnName);
	$.beginPageLoading();
	//查询通用apn模板
	var param = '&APNNAME='
			+ commonApnName
			+ '&METHOD_NAME=queryCommonApnTemplate&CLASS_NAME=com.asiainfo.veris.crm.order.web.group.param.gfff.CreateWlwGroupMemberP';
	Wade.httphandler
			.submit(
					'',
					'com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.dynamichtml.ProxyParam',
					'productParamInvoker', param, afterQueryCommonApnTemplate, errFun);

}
/**
 * NBIOT物联网产品
 * 1218301 NB-IOT数据通信服务
 * 根据模板更改LOWPOWERMODE和RAUTAUTIMER的值
 */
function afterQueryCommonApnTemplate(data){
	$.endPageLoading();
	var lowPowerMode = data.get("LOWPOWERMODE");
	$("#LOWPOWERMODE").val(lowPowerMode);
	$("#LOWPOWERMODE").attr("disabled", true);	//通用APN对应的LOWPOWERMODE模式是固定的，不可选择
	var nullable = data.get("RAUTAUTIMER");
	$("#RAUTAUTIMER").attr("nullable", nullable);
	//必填则显示
	if("no" == nullable){
		$.each($("#RAUTAUTIMER").parents("li[class=li]"), function(index, obj){
			$(obj).attr("style", "");
		});
	}
	//非必填则不显示
	else{
		$("#RAUTAUTIMER").val("");
		$.each($("#RAUTAUTIMER").parents("li[class=li]"), function(index, obj){
			$(obj).attr("style", "display:none");
		});
	}
}
/**
 * NBIOT物联网产品
 * 1218301 NB-IOT数据通信服务
 * 专用APN省电模式选择处理
 * @return
 */
function lowPowerModeChanged(){
	var lowPowerMode = $("#LOWPOWERMODE").val();
	if("PSM"==lowPowerMode || "BOTH"==lowPowerMode){
		$("#RAUTAUTIMER").attr("nullable", "no");
		$.each($("#RAUTAUTIMER").parents("li[class=li]"), function(index, obj){
			$(obj).attr("style", "");
		});
	}else{
		$("#RAUTAUTIMER").attr("nullable", "yes");
		$("#RAUTAUTIMER").val("");
		$.each($("#RAUTAUTIMER").parents("li[class=li]"), function(index, obj){
			$(obj).attr("style", "display:none");
		});
	}
}
/**
 * NBIOT物联网产品
 * 1218301 NB-IOT数据通信服务
 * 属性框填值后【确认】按钮触发事件
 * @return
 */
function confirmNbiot1218301Svc(itemIndex){
	var apnType = $("#APNTYPE").val();
	var apnName = $("#APNNAME").val();
	if(apnType==null || apnType==""){
		alert("请先选择APN类型,再做修改,若不修改则点【取消】！");
		return false;
	}
	//专用APN名称不能与通用APN名称一致
	var comApnNames = "cmnbiot,cmnbiot1,cmnbiot2,cmnbiot3,cmnbiot4,cmnbiot5,cmnbiot6,";
	if("1" == apnType){
		if(comApnNames.indexOf(apnName+",")!=-1){
			alert("专用APN名称不能与通用APN名称["+comApnNames+"]一致！");
			return false;
		}
	}
	var lowPowerMode = $("#LOWPOWERMODE").val();
	var rauTauTimer = $("#RAUTAUTIMER").val();
	if(rauTauTimer=="" && ("PSM"==lowPowerMode || "BOTH"==lowPowerMode)){
		alert("低电耗模式为[PSM,PSM+eDRX]需要填写[RAU/TAU定时器]参数值!");
		return false;
	}else if(rauTauTimer!="" && ("PSM"!=lowPowerMode && "BOTH"!=lowPowerMode)){
		alert("低电耗模式不为[PSM,PSM+eDRX]不需要填写[RAU/TAU定时器]参数值!");
		$("#RAUTAUTIMER").val("");
		return false;
	}
	
	
	//这两个属性不需要传值到后台生成台帐
	$("#COMMONAPN").val("");
	$("#APNTYPE").val("");
	selectedElements.confirmAttr(itemIndex);
}
