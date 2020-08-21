function refreshProductInfoArea(data) {
	insertGroupCustInfo(data);
	var custId = $('#CUST_ID').val();
	
	$.beginPageLoading('正在查询集团已订购的产品列表...');
	$.ajax.submit('CondGroupPart','queryGroupOrderProduct','&CUST_ID='+custId,'productInfoArea',function(data){

		//REQ201812200001关于优化集团产品二次确认功能的需求
		$("#TCSMS").attr("disabled",true);
		$("#TCSMS").attr("checked",true);
		$.ajax.submit("", "queryTwoCheckbatCond", "", "", function(data){
			if(data != null)
			{
				if(data.get("TWOCHECK") == '1'){
					$("#TCSMS").attr("disabled","");
				}
			}
		},
		function(error_code,error_info,derror){
			showDetailErrorInfo(error_code,error_info,derror);
	    });
		
		
		if(data.get("EFFECT_NOW")=='true')
		{
			selectedElements.isEffectNow = true;
		}
		$.endPageLoading();
	},function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});
}

function refreshProduct(){
	var productVal = $("#userProductInfo").val();
	if("" == productVal || null == productVal || undefined == productVal) {
		alert('请选择集团产品!');
		return false;
	}else {
		var productNameStr = $("#userProductInfo :selected").text();
		var productNameArry = productNameStr.split("|");
		setHiddenData(productNameArry);
		
		var productId = $("#PRODUCT_ID").val();
		var userId = $("#GRP_USER_ID").val();
		var groupId = $('#POP_cond_GROUP_ID').val();
		var batchOperType = $('#BATCH_OPER_TYPE').val();
		$.beginPageLoading('正在查询产品包元素中...');
		$.ajax.submit(this,'refreshProduct','&PRODUCT_ID='+productId+'&USER_ID='+userId+'&GROUP_ID='+groupId +'&BATCH_OPER_TYPE=' + batchOperType,'ProductPackagePart,GroupUserPart,RolePart,ApplyTypeA',function(data){
			
			renderPayPlanSel(productId,userId);
			initClipTypeDisplay();
			
			$.endPageLoading();
		},function(error_code,error_info,derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
		});
	}
}

function setHiddenData(Arry) {
	$("#PRODUCT_ID").val(Arry[0]);
	$("#GRP_PRODUCT_NAME").val(Arry[1]);
	$("#GRP_SN").val(Arry[2]);
	$("#GRP_USER_ID").val(Arry[3]);
}

function pkgListAfterSelectAction(package) {
	var selfParam =  "&GRP_USER_ID=" + $('#GRP_USER_ID').val() + "&PRODUCT_ID=" + $('#PRODUCT_ID').val();
	var eparchyCode;
	pkgElementList.renderComponent(package,eparchyCode,selfParam);
}

/**产品拼串*/
function setData(obj){
	if(!$.validate.verifyAll("scrollPart")) {
		return false;
	}
	
	//成员角色校验
	var cyjs = $('#IS_NEED_SHOW_CYJS').val();
	if(cyjs =='true'){
		var roleCodeB = $('#ROLE_CODE_B').val();;
		if(roleCodeB == ''){
			alert('成员角色不能为空');
			$('#ROLE_CODE_B').focus();
	        return false;
		}
	}
	
	//付费方式校验
	var fffs = $('#IS_NEED_SHOW_FFFS').val();
	if(fffs == 'true'){
		if(!checkPayPlan()){
			return false;
		}
	}
	
	//校验必选包
	if (!selectedElements.checkForcePackage()) {
		return false;
	}
	//是否必选选择VPMN跨省资费
	var isNeedOutProDiscnt = $('#isNeedOutProDiscnt').val(); 
	if(isNeedOutProDiscnt == 'true'){
		var isNotHas = true;
		var length = selectedElements.selectedEls.length; 
		for(var i=0;i<length;i++){
			var temp = selectedElements.selectedEls.get(i);
			var elementType = temp.get("ELEMENT_TYPE_CODE");
 			var packageId = temp.get("PACKAGE_ID");
 			var state = temp.get("MODIFY_TAG");  
 			if(state=='0' && elementType=='D' && packageId=='80000103'){ 
 				isNotHas = false;
 				break;
			}
		} 
		if(isNotHas){
			alert('VPMN集团是跨省集团，成员新增需要跨省资费，请选择跨省资费!');
		    return false;
		}
	}
	
	var tempElements = selectedElements.getSubmitData();
	$("#SELECTED_ELEMENTS").val(tempElements);
	/*var flag = true;
	var grpProductId = $("#PRODUCT_ID").val();
	$.ajax.submit('GroupUserPart', 'checkMustDiscnt','&GRPPRODUCTID='+grpProductId, null,function(data){
			$.endPageLoading();
			verifyBySelectDiscnt(data);
		},function(error_code,error_info,derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
			flag = false;
		},{async:false});
	return flag;	*/
	
	//是否需要规则校验
	var is_need_rule_check = $('#IS_NEED_RULE_CHECK').val();
	if(is_need_rule_check == 'false')
	{
		commSubmit();
		return true;
	}
	
	
	var checkParam = "&PRODUCT_ID=" + $("#PRODUCT_ID").val() + "&USER_ID=" + $("#GRP_USER_ID").val() + "&ALL_SELECTED_ELEMENTS=" + tempElements + "&SPEC_CDTION=grpBat&TIPS_TYPE=0|1|2";

	if(!grpBatruleCheck("com.asiainfo.veris.crm.order.web.frame.csview.group.rule.CreateGroupMemberRule", "checkGrpBatTipsRule", checkParam))
	{
		return false;
	}
	
	commSubmit();
	
	return true;
}

function verifyBySelectDiscnt(data) {
	var checkresult = data.get('checkresult');
	if(""==checkresult || null==checkresult) {
		commSubmit();
	}else {
		$.showWarnMessage('未选择必选包元素!',checkresult);
		return false;
	}
}
function commSubmit() {
	var group_id = $("#GROUP_ID").val();
	var productName = $("#GRP_PRODUCT_NAME").val();
	var grpProductId = $("#PRODUCT_ID").val();
	var grpUserID = $("#GRP_USER_ID").val();
	var elements = $("#SELECTED_ELEMENTS").val();
	var cust_id = $("#CUST_ID").val();
	var user_eparchy_code = $('#USER_EPARCHY_CODE').val();
	var grp_sn = $('#GRP_SN').val();
	var role_code_b = "";	//成员角色
	var ldxs = "";		//不可取消来电彩铃业务
	var jrxyjt = "";	//是否加入集团
	var pay_plan = "";	//付费计划
	var oper_type = "";	//变更方式
	var APPLY_TYPE_A = "";	//一级选项
	var APPLY_TYPE_B = "";	//二级选项
	var tcsms = "";		//下发二次确认短信
	
	//add by wangyf6 at 20150306
	var grpClipType = $('#GRP_CLIP_TYPE').val();
	var grpUserClipType = $('#GRP_USER_CLIP_TYPE').val();
	var grpUserMod = $('#GRP_USER_MOD').val();
	var clipType = $('#CLIP_TYPE').val();
	
	if($('#IS_NEED_SHOW_BGFS').val()=="true")
	{
		oper_type = $('#OPERCODE').val();
	}
	
	if($('#IS_NEED_SHOW_CYJS').val()=="true")
	{
		role_code_b = $('#ROLE_CODE_B').val();
	}
	
	if($('#IS_NEED_SHOW_LDXS').val()=="true")
	{
		if($('#LDXS').attr('checked'))
		{
			ldxs = "1";
		}
		else
		{
			ldxs = "0";
		}
	}
	if($('#IS_NEED_SHOW_TCSMS').val()=="true")
	{
		if($('#TCSMS').attr('checked'))
		{
			tcsms = "true";
		}
		else
		{
			tcsms = "false";
			alert("当前业务办理涉及不知情订购，若需用户短信二次确认请在界面中选择！");
		}
	}
	
	if($('#IS_NEED_SHOW_JRXYJT').val()=="true")
	{
		if($('#JRXYJT').attr('checked'))
		{
			jrxyjt = "1";
		}
		else
		{
			jrxyjt = "0";
		}
	}else
	{
		jrxyjt = "0";
	}
	
	
	if($('#IS_NEED_SHOW_FFFS').val()=="true")
	{
		pay_plan = getPayPlanValue();
	}
	
	if($('#IS_SHOW_APPLY_TYPE').val()=="true")
	{
		APPLY_TYPE_A = $('#APPLY_TYPE_A').val();
		APPLY_TYPE_B = $('#APPLY_TYPE_B').val();
	}
	
	if ( elements == "" ) 
		return false;    
	var idata = $.DatasetList(elements);
	
	//start add by wangyf6 at 20151119
	var checkResult = checkDiscntZhekouLv(idata);
	if(!checkResult){
		return false;
	}
	//end add by wangyf6 at 20151120
	
	//start add by wangyf6 at 20170811
	var checkWlw = checkDiscntAttrForWlw(idata);
	if(!checkWlw){
		return false;
	}
	//end add by wangyf6 at 20170811
	
	var info = $.DataMap();
	
	info.put("ELEMENT_INFO", idata);
	info.put("GROUP_ID", group_id);
	info.put("PRODUCT_ID",grpProductId);
	info.put("USER_ID", grpUserID);
	info.put("CUST_ID", cust_id);
	info.put('USER_EPARCHY_CODE',user_eparchy_code);
	info.put('MEM_ROLE_B',role_code_b);
	info.put('CANCEL_LING',ldxs);	//不可来电取消彩铃业务
	info.put('PAGE_SELECTED_TC',tcsms);	//页面上勾选下发二次确认短信
	info.put('JOIN_IN',jrxyjt);	//加入相应集团
	info.put('PLAN_TYPE',pay_plan);
	info.put('GRP_SN', grp_sn);
	info.put('OPER_TYPE', oper_type);
	info.put('APPLY_TYPE_A', APPLY_TYPE_A);
	info.put('APPLY_TYPE_B', APPLY_TYPE_B);
	
	//add by wangyf6 at 20150306
	if(grpClipType == "1" && grpUserMod == "1"){
		if(clipType != null && clipType != ""){
			info.put('GRP_CLIP_TYPE', grpClipType);
			info.put('GRP_USER_CLIP_TYPE', grpUserClipType);
			info.put('GRP_USER_MOD', grpUserMod);
			info.put('CLIP_TYPE', clipType);
		}
	}
	
	//add by wangyf6 at 20160809
	if($('#MEB_FILE_SHOW').val()=="true"){
		info.put('MEB_FILE_SHOW', "true");
		var mebFileList = $('#MEB_FILE_LIST').val();
		if(mebFileList != null && mebFileList != ""){
			info.put('MEB_FILE_LIST', mebFileList);
		}
	}
	
	//add by wangyf6 at 20181009
	if($('#IS_SHOW_IMSVPMN_PARAM').val()=="true")
	{
		var callDispMode = $('#pam_CALL_DISP_MODE').val();
		var outerCall = $('#pam_OuterCall').val();
		if(callDispMode != null && callDispMode != "")
		{
			info.put('CALL_DISP_MODE', callDispMode);
		}
		if(outerCall != null && outerCall != "")
		{
			info.put('OuterCall', outerCall);
		}		
	}
	
	//add by chenzg@20180704--begin--REQ201804280001集团合同管理界面优化需求---
	if($('#MEB_VOUCHER_FILE_LIST')){
		var mebVoucherFileList = $('#MEB_VOUCHER_FILE_LIST').val();
		if( mebVoucherFileList== ""){
			alert("请上传凭证附件！");
			return false;
		}else{
			info.put('MEB_VOUCHER_FILE_LIST', mebVoucherFileList);
		}		
	}
	if($('#AUDIT_STAFF_ID')){
		var auditStaffId = $('#AUDIT_STAFF_ID').val();
		if( auditStaffId== ""){
			alert("请选择稽核员！");
			return false;
		}else{
			info.put('AUDIT_STAFF_ID', auditStaffId);
		}		
	}
	//add by chenzg@20180704--end----REQ201804280001集团合同管理界面优化需求---
	
	
	//$.setReturnValue({'POP_CODING_STR':productName},false);
 	//$.setReturnValue({'CODING_STR':info},true);
 	parent.$('#POP_CODING_STR').val(productName);
 	parent.$('#CODING_STR').val(info);
 	parent.hiddenPopupPageGrp();
 	
}

function errorAction() {
	clearGroupCustInfo();
}

function addChoiceArea (str) {
	$('#'+str).click();
}

/**
 * 设置ADCMAS弹出的服务参数页面URL值
 * 
 */
(function(){$.extend(SelectedElements.prototype,{
	buildPopupAttrParam: function(data){
	        var eparchyCode=$("#MEB_EPARCHY_CODE").val();
	        var param="&MEB_EPARCHY_CODE="+eparchyCode;
	        return param;
	       }
	});
})();

//yanwu begin
function queryApplyTypebs(){
	//alert('123');
	var vpnAttr = $('#APPLY_TYPE_A').val(); 
	//alert(vpnAttr);

	$.beginPageLoading();
	//var param = '&APPLY_TYPE_A='+vpnAttr+'&METHOD_NAME=queryApplyTypebs&CLASS_NAME=com.asiainfo.veris.crm.order.web.group.param.gfff.CreateWlwGroupMemberP';
    //Wade.httphandler.submit('','com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.dynamichtml.ProxyParam','productParamInvoker',param, afterQueryApplyTypeb,errFun);
    
	var param = '&APPLY_TYPE_A='+vpnAttr; 
    $.ajax.submit(this, 'queryApplyTypebs', param , 'ApplyTypeB', function(data){ 
    	
    	$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}

function afterQueryApplyTypeb(data) 
{
	var APPLY_TYPE_B_LIST = data.get('APPLY_TYPE_B_LIST');
	//alert(APPLY_TYPE_B_LIST);
	//$('#APPLY_TYPE_B_LIST').val(APPLY_TYPE_B_LIST);
	
	$('#APPLY_TYPE_B').empty();
	for(var i=0; i < APPLY_TYPE_B_LIST.length; i++){
		var APPLY_TYPE_B = APPLY_TYPE_B_LIST.get(i).get('APPLY_TYPE_B');
		var TYPE_B = APPLY_TYPE_B_LIST.get(i).get('TYPE_B');
		jsAddItemToSelect($('#APPLY_TYPE_B'), TYPE_B, APPLY_TYPE_B);
	}
	$.endPageLoading();
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
//yanwu end 


function checkDiscntZhekouLv(data){
	
	var batchOperTypeObj = $('#BATCH_OPER_TYPE').val()
	if(batchOperTypeObj == "BATADDECONNECTMEM" || batchOperTypeObj == "BATADDPOCUMEM" ){
		if(data != null){
		
			var length = data.length;
			for(var i=0;i<length;i++){
				var allSelectedElements = data.get(i);
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
							if(oneAttrCode == "7050"){
								if(oneAttrValue == "" || oneAttrValue == null){
									alert("折扣率不能为空,请填写!");
									return false;
								} else {
									var flag = $.verifylib.checkPInteger(oneAttrValue);
									if(!flag){
										alert("折扣率必须是整数");
										return false;
									}
									//add by chenzg@20171214 集团物联网（M2M业务）成员批量新增,要求放开10-100
									if(batchOperTypeObj == "BATADDPOCUMEM"){
										if(oneAttrValue > 100 || oneAttrValue < 10){
											alert("折扣率必须是10到100之间!");
											return false;
										}
									}else{
										if(oneAttrValue > 100 || oneAttrValue < 60){
											alert("折扣率必须是60到100之间!");
											return false;
										}
									}
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

function checkDiscntAttrForWlw(data){
	
	var batchOperTypeObj = $('#BATCH_OPER_TYPE').val()
	if(batchOperTypeObj == "BATADDWLWMEBWLT" || batchOperTypeObj == "BATADDWLWMEBJIQIKA" || batchOperTypeObj == "BATADDWLWMEBNBIOT" ){
		if(data != null){
			
			var discount = false;//true:折扣率低于6折
			var discount24 = false;//true:折扣率低于2.4折
			var length = data.length;
			
			//服务策略的拦截校验
			for(var i=0;i<length;i++){
				var allSelectedElements = data.get(i);
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
								if(oneAttrCode == "ServiceCode")
								{
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
					}
					
					if(serviceCodeValue != "" || operTypeValue != "" || 
						serviceBillingTypeValue!="" || serviceUsageStateValue !="" || scStartDateTime != "" ||
						scEndDateTime != "")
					{
						if(!(serviceCodeValue != "" && operTypeValue != "" 
							&& serviceBillingTypeValue != "" && serviceUsageStateValue !=""))
						{
							alert("请把该服务" + elementId + "策略的属性值填写完,订购业务唯一代码、操作类型、计费方式、配额状态!");
							return false;
						}
					}
				}
			}
			
			for(var i=0;i<length;i++){
				var allSelectedElements = data.get(i);
				var elementType = allSelectedElements.get("ELEMENT_TYPE_CODE");
				var elementId = allSelectedElements.get("ELEMENT_ID");
				var state = allSelectedElements.get("MODIFY_TAG");
				if(elementType == "S" && (state == "0" || state == "2"))
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
										alert("APNNAME不能为空,请填写!");
										return false;
									}
								}
							}
						}
					}
				}
			}
			
			
			///定向专线的apnname校验-重写
			for(var i=0;i<length;i++){
				var allSelectedElements = data.get(i);
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
										alert("APNNAME不能为空,请填写!");
										return false;
									}
									
									if(packageId == "70000008" || packageId == "70000011" || packageId == "70000013"){//定向优惠包
										var breakFlag = false;//标识
										
										//优惠中的appname,在对应的服务中一定要存在
										for(var k=0;k<length;k++){
											var allSelectedElementSVC = data.get(k);
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
											alert("定向流量包下的优惠的APNNAME与对应的服务的APNNAME不一样!请核实重新填写!");
											return false;
										}
									
									}
									else if(packageId == "70000005" || packageId == "70000009" || packageId == "70000012"){//通用优惠包
										var breakFlag = false;//标识
										
										//优惠中的appname,在对应的服务中一定要存在
										for(var k=0;k<length;k++){
											var allSelectedElementSVC = data.get(k);
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
				var allSelectedElements = data.get(i);
				var elementType = allSelectedElements.get("ELEMENT_TYPE_CODE");
				var packageId = allSelectedElements.get("PACKAGE_ID");
				var elementId = allSelectedElements.get("ELEMENT_ID");
				var state = allSelectedElements.get("MODIFY_TAG"); 
				if(elementType == "D" && (state == "0" || state == "2")) {
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
										alert("优惠的折扣率不能为空,请填写!");
										return false;
									} else {
										var flag = $.verifylib.checkPInteger(oneAttrValue);
										if(!flag){
											alert("折扣率必须是整数");
											return false;
										}
										if(oneAttrValue > 100 || oneAttrValue < 1){
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
											alert("本省折扣率必须是整数");
											return false;
										}
										if(oneAttrValue > 100 || oneAttrValue < 0){
											alert("本省折扣率必须是0到100之间的整数!");
											return false;
										}
										//当本省折扣内填写折扣后，原有折扣窗口默认100
										setAttrVale(attrParams, "Discount", "100");
										//当本省折扣低于6折时，判断审批工单号是否填写
										if(oneAttrValue<60){
											var audiInfo = getAttrVale(attrParams, "AudiInfo0898");
											if(audiInfo==null || $.trim(audiInfo) == ""){
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
										alert("优惠的折扣率不能为空,请填写!");
										return false;
									} else {
										var flag = $.verifylib.checkPInteger(oneAttrValue);
										if(!flag){
											alert("折扣率必须是整数");
											return false;
										}
										if(oneAttrValue > 100 || oneAttrValue < 1){
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
											alert("在网时间必须是整数");
											return false;
										}
										//var values = parseInt(oneAttrValue)%24;
										var values = parseInt(oneAttrValue);
										if(values < 24){
											alert("在网时间必须是大于等于24的整数!请重新填写");
											return false;
										}
									} else {
										if(discount == true){
											alert("折扣率低于6折!在网时间必须填写");
											return false;
										}
									}
								} else if(oneAttrCode == "CanShare"){//是否共享校验
									if(oneAttrValue == null || oneAttrValue == ""){
										alert("是否可共享不能为空,请选择!");
										return false;
									}
								} else if(oneAttrCode == "MinimumOfYear"){//年承诺收入（元）
									if(oneAttrValue != null && oneAttrValue != ""){
										var flag = $.verifylib.checkPInteger(oneAttrValue);
										if(!flag){
											alert("年承诺收入(元)必须是整数!");
											return false;
										}
										var values = parseInt(oneAttrValue);
										if(values < 50000){
											alert("年承诺收入(元)必须是大于等于50000!请重新填写");
											return false;
										}
									} else {
										if(discount == true){
											alert("折扣率低于6折!年承诺收入(元)必须填写");
											return false;
										}
									}
								} else if(oneAttrCode == "BatchAccounts"){//入网用户数(张)
									if(oneAttrValue != null && oneAttrValue != ""){
										var flag = $.verifylib.checkPInteger(oneAttrValue);
										if(!flag){
											alert("入网用户数(张)必须是整数!");
											return false;
										}
										var values = parseInt(oneAttrValue);
										if(values < 50000){
											alert("入网用户数(张)必须是大于等于50000!请重新填写");
											return false;
										}
									} else {
										if(discount == true){
											alert("折扣率低于6折!入网用户数(张)必须填写");
											return false;
										}
									}
								} else if(oneAttrCode == "ApprovalNum"){//审批文号
									if(oneAttrValue == null || oneAttrValue == ""){
										if(discount == true){
											alert("折扣率低于6折!审批文号必须填写");
											return false;
										}
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
									alert("优惠的折扣率不能为空,请填写!");
									return false;
								} else {
									var flag = $.verifylib.checkPInteger(oneAttrValue);
									if(!flag){
										alert("折扣率必须是整数");
										return false;
									}
									if(oneAttrValue > 100 || oneAttrValue < 1){
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
									alert("APNNAME不能为空,请填写!");
									return false;
								}
							} else if(oneAttrCode == "Discount"){ //折扣率校验
								if(oneAttrValue == null || oneAttrValue == ""){
									alert("优惠的折扣率不能为空,请填写!");
									return false;
								} else {
									var flag = $.verifylib.checkPInteger(oneAttrValue);
									if(!flag){
										alert("折扣率必须是整数");
										return false;
									}
									if(oneAttrValue > 100 || oneAttrValue < 1){
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
										alert("在网时间必须是整数");
										return false;
									}
									//var values = parseInt(oneAttrValue)%24;
									var values = parseInt(oneAttrValue);
									if(values < 24){
										alert("在网时间必须是大于等于24的整数!请重新填写");
										return false;
									}
								} else {
									if(discount == true){
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
										alert("入网用户数(张)必须是整数!");
										return false;
									}
									var values = parseInt(oneAttrValue);
									if(values < 0){
										alert("入网用户数(张)必须是大于0!请重新填写");
										return false;
									}
								} else {
									if(discount == true){
										alert("折扣率低于6折!入网用户数(张)必须填写");
										return false;
									}
								}
							} else if(oneAttrCode == "ApprovalNum"){//审批文号
								if(oneAttrValue == null || oneAttrValue == ""){
									//if(discount == true){
									if(discount24 == true){
										//alert("折扣率低于6折!审批文号必须填写");
										alert("折扣率低于2.4折!审批文号必须填写");
										return false;
									}
								}
							}
							
						}
					}
				}
				//-----------add by chenzg@20171220--NB-IOT流量产品包属性校验,41003605----end-------
			}
			
			//---------------add by chenzg@20180712-BUG20180706172625关于物联网NB-IOT短信基础通信服务依赖拦截的优化-begin---------------
			/*校验218302,NB-IOT短信基础通信服务依赖的优惠是否办理，
			 * 短信基础通信服务依赖于NB-IOT短信年包或测试期套餐*/
			if(batchOperTypeObj == "BATADDWLWMEBNBIOT"){
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
		
		
		//start
		if(data != null){
			var length = data.length;
			var groupType = $('#GROUP_TYPE').val(); //集团企业类型
			var userFlag = "2";//用户类别
			var userAreaFlag = "3";
			
			for(var i=0;i<length;i++){//先查找个人智能网语音通信服务,取相应的属性值
				var allSelectedElements = data.get(i);
				var elementType = allSelectedElements.get("ELEMENT_TYPE_CODE");
				var elementId = allSelectedElements.get("ELEMENT_ID");
				var state = allSelectedElements.get("MODIFY_TAG");
				if(elementType == "S" && state == "0" && elementId == "99011019")
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
				var allSelectedElements = data.get(i);
				var elementType = allSelectedElements.get("ELEMENT_TYPE_CODE");
				var elementId = allSelectedElements.get("ELEMENT_ID");
				var state = allSelectedElements.get("MODIFY_TAG");
				if(elementType == "S" && state == "0" && elementId == "99011019")
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
								alert("用户类别为企业客户成员时,企业客户成员国际长途权限必选,不能为空!");
								return false;
							}else if(oneAttrCode == "AutoForwardFlag"){
								if(userFlag == "1" && oneAttrValue == ""){
									alert("用户类别为企业客户成员时,是否支持自动前转必选,不能为空!");
									return false;
								}
								if(groupType == "2" && oneAttrValue == "1" && oneAttrValue != ""){
									alert("该集团产品的企业类型是集群类企业,是否支持自动前转只能选择不支持选项!");
									return false;
								}
							}else if(oneAttrCode == "RoamingFlag"  && userFlag == "1" && oneAttrValue == ""){ 
								alert("用户类别为企业客户成员时,国际漫游权限必选,不能为空!");
								return false;
							} else if(oneAttrCode == "UserClass"){
								if(userFlag == "1" && (oneAttrValue == null || oneAttrValue == "")){
									alert("用户类别为企业客户成员时,通话阀值的级别必填,不能为空!");
									return false;
								}
								if(oneAttrValue != null && oneAttrValue != ""){
									var flag = $.verifylib.checkPInteger(oneAttrValue);
									if(!flag){
										alert("通话阀值的级别必须是整数!");
										return false;
									}
									var values = parseInt(oneAttrValue);
									if(values <= 0){
										alert("通话阀值的级别必须是大于0!请重新填写");
										return false;
									}
								}
							} else if(oneAttrCode == "LockFlag"){
								if(groupType == "2" && oneAttrValue != null && oneAttrValue != ""){
									alert("该集团产品的企业类型是集群类企业,被叫闭锁不能选择!");
									return false;
								}
							} else if(oneAttrCode == "userShutFlag"){
								if(groupType == "1" && oneAttrValue != null && oneAttrValue != ""){
									alert("该集团产品的企业类型是普通企业,客户成员闭锁功能标识不能选择!");
									return false;
								}
							} else if(oneAttrCode == "userWhiteNumFlag"){
								if(groupType == "2" && oneAttrValue != null && oneAttrValue != ""){
									alert("该集团产品的企业类型是集群类企业,白名单功能状态不能选择!");
									return false;
								}
							} else if(oneAttrCode == "userAreaOperType"){
								if(userAreaFlag != "1" && oneAttrValue != null && oneAttrValue != ""){
									alert("成员区域限制标识选项不是多省漫游,限制区域操作类型不能选择!");
									return false;
								} else if(userAreaFlag == "1" && (oneAttrValue == null || oneAttrValue == "")){
									alert("成员区域限制标识选项是多省漫游,限制区域操作类型不能为空!");
									return false;
								}
							} else if(oneAttrCode == "userArea"){
								if(userAreaFlag != "1" && oneAttrValue != null && oneAttrValue != ""){
									alert("成员区域限制标识选项不是多省漫游,限制区域列表不能填写!");
									return false;
								}else if(userAreaFlag == "1" && (oneAttrValue == null || oneAttrValue == "")){
									alert("成员区域限制标识选项是多省漫游,限制区域列表不能为空");
									return false;
								}
							}
							
						}
					}
				}
			}
		
		}
		//end
		
	}
	
	return true;
}

function getOtherParam()
{
    var grpUserId=$("#GRP_USER_ID").val();
    var grpProductId=$("#PRODUCT_ID").val();
    
    var param="&GRP_USER_ID="+grpUserId+"&GRP_PRODUCT_ID="+grpProductId;
    return param;
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
	lowPowerModeChanged();
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
	
	if("PSM"==lowPowerMode){
		$("#ACTIVETIMER").attr("nullable", "no");
		$.each($("#ACTIVETIMER").parents("li[class=li]"), function(index, obj){
			$(obj).attr("style", "");
		});
		$("#NBIOTEDRXCYCLE").attr("nullable", "yes");
		$("#NBIOTEDRXCYCLE").val("");
		$.each($("#NBIOTEDRXCYCLE").parents("li[class=li]"), function(index, obj){
			$(obj).attr("style", "display:none");
		});
	}else if ("eDRX"==lowPowerMode){
		$("#NBIOTEDRXCYCLE").attr("nullable", "no");
		$.each($("#NBIOTEDRXCYCLE").parents("li[class=li]"), function(index, obj){
			$(obj).attr("style", "");
		});
		$("#ACTIVETIMER").attr("nullable", "yes");
		$("#ACTIVETIMER").val("");
		$.each($("#ACTIVETIMER").parents("li[class=li]"), function(index, obj){
			$(obj).attr("style", "display:none");
		});
	}else if ("BOTH"==lowPowerMode){
		$("#NBIOTEDRXCYCLE").attr("nullable", "no");
		$.each($("#NBIOTEDRXCYCLE").parents("li[class=li]"), function(index, obj){
			$(obj).attr("style", "");
		});
		$("#ACTIVETIMER").attr("nullable", "no");
		$.each($("#ACTIVETIMER").parents("li[class=li]"), function(index, obj){
			$(obj).attr("style", "");
		});
	}else{
		$("#NBIOTEDRXCYCLE").attr("nullable", "yes");
		$("#NBIOTEDRXCYCLE").val("");
		$.each($("#NBIOTEDRXCYCLE").parents("li[class=li]"), function(index, obj){
			$(obj).attr("style", "display:none");
		});
		$("#ACTIVETIMER").attr("nullable", "yes");
		$("#ACTIVETIMER").val("");
		$.each($("#ACTIVETIMER").parents("li[class=li]"), function(index, obj){
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
	
	//平台校验要求PSM模式：RAU/TAU定时器应大于空闲态定时器
	if("PSM"==lowPowerMode || "BOTH"==lowPowerMode){
		var rauTauTimer = $("#RAUTAUTIMER").val();
		var activeTimer = $("#ACTIVETIMER").val();
		if(activeTimer >= rauTauTimer){
			$("#ACTIVETIMER").val("");
			alert("平台校验要求PSM或BOTH模式：RAU/TAU定时器应大于空闲态定时器。");
			return false;
		}
	}
	
	//这两个属性不需要传值到后台生成台帐
	$("#COMMONAPN").val("");
	$("#APNTYPE").val("");
	selectedElements.confirmAttr(itemIndex);
}

/**
 * REQ201805280003关于修改融合通信业务语音优惠活动内容的需求
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

/**
 * REQ201805280003关于修改融合通信业务语音优惠活动内容的需求
 * 属性弹出框确定按钮事件
 * 校验集团固话畅聊包产品优惠的折扣属性填值
 * @param index
 * @return
 */
function checkClbDiscntAttr(itemIndex){
	var tempElement = selectedElements.selectedEls.get(itemIndex);
	var percent = $("#18605").val();				//折扣率
	var discntCode = tempElement.get("ELEMENT_ID");	//优惠编码
	
	//折扣率校验
	if(!$.verifylib.checkPInteger(percent)){
		alert("折扣率请填写>=60的整数！");
		return false;
	}else{
		if(!(percent>=60 && percent<=100)){
			alert("该优惠要求折扣必须在[60-100]之间！");
			return false;
		}
	}
	
	selectedElements.confirmAttr(itemIndex);
	return true;
}

function initClipTypeDisplay(){
	var grpClipTypeValue = $('#GRP_CLIP_TYPE').val();
	var grpUserModValue = $('#GRP_USER_MOD').val();
	if(grpClipTypeValue == "1" && grpUserModValue == "1"){
		$('#CLIPTYPEPART').css('display','block');
	} else {
		$('#CLIPTYPEPART').css('display','none');
	}
}
