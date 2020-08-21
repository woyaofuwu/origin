
function init() {     
}

function queryApplyTypebs(){
	var vpnAttr = $('#pam_APPLY_TYPE_A').val(); 
	
	$.beginPageLoading();
	//var param = '&APPLY_TYPE_A='+vpnAttr+'&METHOD_NAME=queryApplyTypebs&CLASS_NAME=com.asiainfo.veris.crm.order.web.group.param.gfff.CreateWlwGroupMemberP';
    //Wade.httphandler.submit('','com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.dynamichtml.ProxyParam','productParamInvoker',param, afterQueryApplyTypeb,errFun);
    $.httphandler.post("com.asiainfo.veris.crm.iorder.web.igroup.offercha.wlw.WlwParamHandler", "queryApplyTypebs",'&APPLY_TYPE_A='+vpnAttr, function(data){
		$.endPageLoading();
		afterQueryApplyTypeb(data);
		},    
		function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
}

function afterQueryApplyTypeb(data) 
{
	$.endPageLoading();
	var APPLY_TYPE_B_LIST = data.get('APPLY_TYPE_B_LIST');
	//$('#pam_APPLY_TYPE_B').empty();
	pam_APPLY_TYPE_B.empty();
	for(var i=0; i < APPLY_TYPE_B_LIST.length; i++){
		var APPLY_TYPE_B = APPLY_TYPE_B_LIST.get(i).get('PARA_CODE1');
		var TYPE_B = APPLY_TYPE_B_LIST.get(i).get('PARA_CODE1');
		pam_APPLY_TYPE_B.append(APPLY_TYPE_B,TYPE_B,APPLY_TYPE_B);
	}
	$("#pam_APPLY_TYPE_B").val(APPLY_TYPE_B_LIST.get(APPLY_TYPE_B_LIST.length-1).get('PARA_CODE1'));
	
}

/*function errFun(error_code,error_info)
{
	$.endPageLoading();
	alert(error_info);
}*/


function validateParamPage(methodName) {  
	if(methodName=='CrtMb' || methodName=='ChgMb'){
		var commanAppName = "";
		var specialAppName = "";
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
						}
					}
					
					if(serviceCodeValue != "" || operTypeValue != "" || 
						serviceBillingTypeValue!="" || serviceUsageStateValue !="")
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
		
		for(var i=0;i<length;i++){
			var allSelectedElements = selectedElements.selectedEls.get(i);
			var elementType = allSelectedElements.get("ELEMENT_TYPE_CODE");
			var elementId = allSelectedElements.get("ELEMENT_ID");
			var state = allSelectedElements.get("MODIFY_TAG");
			if(elementType == "S" && (state == "0" || state == "2" || state == "exist"))
			{
				if(elementId == "99011022" || elementId ==  "99011028")
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
								commanAppName = oneAttrValue;
							}
						}
					}
					
				} else if(elementId == "99011021" || elementId == "99011029") {
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
								specialAppName = oneAttrValue;
							}
						}
					}
				} 
				//-------add by chenzg@20171210--REQ201712080006NB-IoT业务支撑系统改造方案（全网需求）--begin-------
				else if(elementId == "1218301") {
					var attrParams = allSelectedElements.get("ATTR_PARAM");
					if(attrParams != null && attrParams != ""){
						var modeVal = getAttrVale(attrParams, "LOWPOWERMODE");
						if(modeVal != null && $.trim(modeVal) != "" && modeVal=="PSM"){
							var timerVal = getAttrVale(attrParams, "RAUTAUTIMER");
							if(timerVal==null || timerVal==""){
								if(mytab != null){
									mytab.switchTo("产品信息");
								}
								alert("RAU/TAU定时器不能为空,请填写!");
								return false;
							}
							setAttrVale(attrParams, "RAUTAUTIMER", timerVal);
						}else{
							setAttrVale(attrParams, "RAUTAUTIMER", "");
						}
					}
				}
				//-------add by chenzg@20171210--REQ201712080006NB-IoT业务支撑系统改造方案（全网需求）--end-------
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
							if(oneAttrCode == "APNNAME"){//appname的校验
								if(oneAttrValue == null || oneAttrValue == ""){
									if(mytab != null){
										mytab.switchTo("产品信息");
									}
									alert("APNNAME不能为空,请填写!");
									return false;
								}
								if(packageId == "70000005" || packageId == "70000009" || packageId == "70000012"){//通用优惠包
									if(commanAppName != oneAttrValue){
										if(mytab != null){
											mytab.switchTo("产品信息");
										}
										alert("通用流量包下的优惠的APPNAME与对应的服务的APPNAME不一样!请核实重新填写!");
										return false;
									}
								} else if(packageId == "70000008" || packageId == "70000011" || packageId == "70000013"){//定向优惠包
									if(specialAppName != oneAttrValue){
										if(mytab != null){
											mytab.switchTo("产品信息");
										}
										alert("定向流量包下的优惠的APPNAME与对应的服务的APPNAME不一样!请核实重新填写!");
										return false;
									}
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

//提交
function checkSub(obj)
{
	if(!submitOfferCha())
		return false; 
	backPopup(obj);
}
