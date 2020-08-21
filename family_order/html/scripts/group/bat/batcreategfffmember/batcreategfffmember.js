function refreshProductInfoArea(data) {
	insertGroupCustInfo(data);
	var custId = $('#CUST_ID').val();
	
	$.beginPageLoading('正在查询集团已订购的产品列表...');
	$.ajax.submit('CondGroupPart','queryGroupOrderProduct','&CUST_ID='+custId,'productInfoArea',function(data){

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
		$.beginPageLoading('正在查询产品包元素中...');
		$.ajax.submit(this,'refreshProduct','&PRODUCT_ID='+productId+'&USER_ID='+userId+'&GROUP_ID='+groupId,'ProductPackagePart,GroupUserPart,RolePart',function(data){
			
			renderPayPlanSel(productId,userId);
			/*initClipTypeDisplay();*/
			
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
	
	
	//统付的生效截止时间
	var needShxdate = $('#IS_NEED_SHOW_SHXDATE').val();
	if(needShxdate == 'true'){
		var payEndDate = $('#NOTIN_PAY_END_DATE').val();
		if(payEndDate == null || payEndDate == ""){
			alert("统付的生效截止时间不能为空!");
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
		
		var payLimitFeeValue = $('#NOTIN_PAY_LIMIT_FEE').val();
		if(payLimitFeeValue == "" || payLimitFeeValue == null){
			alert("统付流量不能为空!");
			return false;
		}
		
		var flag = $.verifylib.checkPInteger(payLimitFeeValue);
		if(!flag){
			alert("统付流量大小必须是整数");
			return false;
		}
		
	}
	
	
	//校验必选包
	if (!selectedElements.checkForcePackage()) {
		return false;
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
	var pay_plan = "";	//付费计划
	var oper_type = "";	//变更方式
	var smsFlag = "";
	
	if($('#IS_NEED_SHOW_BGFS').val()=="true")
	{
		oper_type = $('#OPERCODE').val();
	}
	
	if($('#IS_NEED_SHOW_CYJS').val()=="true")
	{
		role_code_b = $('#ROLE_CODE_B').val();
	}
	
	if($('#IS_NEED_SHOW_FFFS').val()=="true")
	{
		pay_plan = getPayPlanValue();
	}
	
	if($('#IS_NEED_SHOW_DXTX').val()=="true")
	{
		if($('#NOTIN_SMS_FLAG').attr('checked'))
		{
			smsFlag = "1";
			if($("input[name='pam_NOTIN_sendForSms']:checked").val()=='1'){
				var smsInfo = $("#pam_NOTIN_SmsInfo").val();
				if(smsInfo == ""){
					alert("您选择的是个性模板短信下发,请输入短信内容！");
					return false;
				}
			}
		}
		else
		{
			smsFlag = "0";
		}
	} else {
		smsFlag = "0";
	}
	
	if ( elements == "" ) 
		return false;    
	var idata = $.DatasetList(elements);
	
	var batchOperTypeObj = $('#BATCH_OPER_TYPE').val()
	if(batchOperTypeObj == "ADDGFFFDINGEMEMBER"){
		var length = idata.length;
		for(var i=0;i<length;i++){
			var allSelectedElements = idata.get(i);
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
						if(oneAttrCode == "7343"){
							if(oneAttrValue == "" || oneAttrValue == null){
								alert("折扣率不能为空,请填写!");
								return false;
							} else {
								var flag = $.verifylib.checkPInteger(oneAttrValue);
								if(!flag){
									alert("折扣率必须是整数");
									return false;
								}
								if(oneAttrValue > 100 || oneAttrValue < 70){
									alert("折扣率必选是70到100之间!");
									return false;
								}
							}
						}
					}
				}
			}
		}
	}
	
	if(batchOperTypeObj == "ADDGFFFDINGEMEMBER"){
		var length = idata.length;
		for(var i=0;i<length;i++){
			var allSelectedElements = idata.get(i);
			var elementType = allSelectedElements.get("ELEMENT_TYPE_CODE");
			var packageId = allSelectedElements.get("PACKAGE_ID");
			var elementId = allSelectedElements.get("ELEMENT_ID");
			var state = allSelectedElements.get("MODIFY_TAG"); 
			if(state == "0" && elementType == "D" && packageId == "73430003") {
				alert("集团产品成员新增不可以添加集团自由充(定额统付)成员产品流量叠加包[73430003],流量叠加包只能在集团产品成员资料修改中新增！");
				return false;
			}
			
			if(state == "0" && elementType == "D" && packageId == "73430002") {
				var endDate = allSelectedElements.get("END_DATE"); 
				if(endDate != null && endDate != ""){
					if(endDate != null && endDate == "2050-12-31 23:59:59"){
						if(!confirm("优惠的结束时间是到2050-12-31,确认不需要修改？")){
							return false;
						}
					}
				}
			}
		}
	}
	
	if(batchOperTypeObj == "ADDGFFFLIMITAIONMEM"){
		var length = idata.length;
		for(var i=0;i<length;i++){
			var allSelectedElements = idata.get(i);
			var elementType = allSelectedElements.get("ELEMENT_TYPE_CODE");
			var packageId = allSelectedElements.get("PACKAGE_ID");
			var elementId = allSelectedElements.get("ELEMENT_ID");
			var state = allSelectedElements.get("MODIFY_TAG"); 
			if(state == "0" && elementType == "D" && packageId == "73440003") {
					alert("集团产品成员新增不可以添加集团自由充(限量统付)成员产品流量叠加包[73440003],流量叠加包只能在集团产品成员资料修改中新增！");
					return false;
			}
		}
	}
	
	var info = $.DataMap();
	
	info.put("ELEMENT_INFO", idata);
	info.put("GROUP_ID", group_id);
	info.put("PRODUCT_ID",grpProductId);
	info.put("USER_ID", grpUserID);
	info.put("CUST_ID", cust_id);
	info.put('USER_EPARCHY_CODE',user_eparchy_code);
	info.put('MEM_ROLE_B',role_code_b);
	info.put('PLAN_TYPE',pay_plan);
	info.put('GRP_SN', grp_sn);
	info.put('OPER_TYPE', oper_type);
	info.put('NOTIN_SMS_FLAG', smsFlag);
	
	
	if($('#IS_NEED_SHOW_DXTX').val()=="true")
	{
		if($('#NOTIN_SMS_FLAG').attr('checked'))
		{
			if($("input[name='pam_NOTIN_sendForSms']:checked").val()=='1'){
				var smsInfo = $("#pam_NOTIN_SmsInfo").val();
				if(smsInfo != ""){
					info.put('NOTIN_sendForSms','1');
					info.put('NOTIN_SmsInfo', smsInfo);
				}
			} else if($("input[name='pam_NOTIN_sendForSms']:checked").val()=='0'){
				info.put('NOTIN_sendForSms','0');
			}
		}
		else
		{
			smsFlag = "0";
		}
	}
	
	if($('#IS_NEED_SHOW_SHXDATE').val()=="true")
	{
		info.put('NOTIN_PAY_END_DATE', $('#NOTIN_PAY_END_DATE').val());
	}
	if($('#IS_NEED_SHOW_SHXDATE').val()=="true")
	{
		info.put('NOTIN_PAY_LIMIT_FEE', $('#NOTIN_PAY_LIMIT_FEE').val());
	}
	
	//add by wangyf6 at 20160809
	if($('#MEB_FILE_SHOW').val()=="true"){
		info.put('MEB_FILE_SHOW', "true");
		var mebFileList = $('#MEB_FILE_LIST').val();
		if(mebFileList != null && mebFileList != ""){
			info.put('MEB_FILE_LIST', mebFileList);
		}
	}
	
	//add by chenzg@20180704--begin--REQ201804280001集团合同管理界面优化需求---
	if($('#MEB_VOUCHER_FILE_LIST') && $('#MEB_VOUCHER_FILE_LIST').val() == ""){
		alert("请上传凭证附件！");
		return false;
	}
	info.put('MEB_VOUCHER_FILE_LIST', $('#MEB_VOUCHER_FILE_LIST').val());
	if($('#AUDIT_STAFF_ID') == null || $('#AUDIT_STAFF_ID').val() == ""){
		alert("请选择稽核人员！");
		return false;
	}
	info.put('AUDIT_STAFF_ID', $('#AUDIT_STAFF_ID').val());
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


function changeSmsFlag(){
	var smsFlag = $("#NOTIN_SMS_FLAG").attr("checked");
	
	if (smsFlag == true) {
		$('#NOTIN_SMS_FLAG').attr('value','1');
		$('#SMSFLAGPART').css('display','');
		$('#SMSFLAGPART2').css('display','');
	} else {
		$('#NOTIN_SMS_FLAG').attr('value','0');
		$('#SMSFLAGPART').css('display','none');
		$('#SMSFLAGPART2').css('display','none');
	}
}

function getOtherParam()
{
    var grpUserId=$("#GRP_USER_ID").val();
    var grpProductId=$("#PRODUCT_ID").val();
    
    var param="&GRP_USER_ID="+grpUserId+"&GRP_PRODUCT_ID="+grpProductId;
    return param;
} 
