function getOtherParam()
{
    var grpUserId=$("#GRP_USER_ID").val();
    var grpProductId=$("#PRODUCT_ID").val();
    
    var param="&GRP_USER_ID="+grpUserId+"&GRP_PRODUCT_ID="+grpProductId;
    return param;
} 

function refreshProductInfoArea(data) {
	insertGroupCustInfo(data);
	var custId = $('#CUST_ID').val();
	
	$.beginPageLoading('正在查询集团已订购的产品列表...');
	$.ajax.submit('CondGroupPart','queryGroupOrderProduct','&CUST_ID='+custId,'productInfoArea',function(data){

		//REQ201812200001关于优化集团产品二次确认功能的需求
		$("#TCSMS").attr("disabled",true);
		$("#TCSMS").attr("checked",true);
		$.ajax.submit("", "queryTwoCheckbatByWP", "", "", function(data){
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
		$.beginPageLoading('正在查询产品包元素中...');
		$.ajax.submit(this,'refreshProduct','&PRODUCT_ID='+productId+'&USER_ID='+userId+'&GROUP_ID='+groupId,'ProductPackagePart,GroupUserPart,RolePart',function(data){
			
			renderPayPlanSel(productId,userId);
			
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
	  
	
	
	if(obj=='0'){
		$('#remindPopup').attr('style','display:none');
		return false;
		}
	
	
	if(!$.validate.verifyAll("scrollPart")) {
		
		$('#remindPopup').attr('style','display:none');
		return false;
	}
	
	
		

	//成员角色校验
	var cyjs = $('#IS_NEED_SHOW_CYJS').val();
	if(cyjs =='true'){
		var roleCodeB = $('#ROLE_CODE_B').val();;
		if(roleCodeB == ''){
			alert('成员角色不能为空');
			$('#ROLE_CODE_B').focus();
			$('#remindPopup').attr('style','display:none');
	        return false;
		}
	}
	
	//付费方式校验
	var fffs = $('#IS_NEED_SHOW_FFFS').val();
	if(fffs == 'true'){
		if(!checkPayPlan()){
			$('#remindPopup').attr('style','display:none');
			return false;
		}
	}
	
	//校验必选包
	if (!selectedElements.checkForcePackage()) {
		$('#remindPopup').attr('style','display:none');
		return false;
	}
	
	
	
	/*if(flag==1){
		flag=0;
	var num=0;
	var length= selectedElements.selectedEls.length;
	for(var i=0;i<length;i++){
		temp=selectedElements.selectedEls.get(i);
		if(temp.get("isNeedChange")==1){
        var endDate = temp.get("END_DATE").substring(0,10);
        if(endDate == '2050-12-31')
         num++;
		}
		var now = new Date();
		var startDate = temp.get("START_DATE");
        if(startDate.substring(0,10) < now.format('yyyy-MM-dd')){
        	alert("开始时间不能小于当前时间");
        	return false;
        }
	}
	if(num!=0) {alert("当前有"+num+"条没设置结束时间，请确认是否修改");return false;}
	}*/
	
	
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
			$('#remindPopup').attr('style','display:none');
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
		$('#remindPopup').attr('style','display:none');
		return true;
	}
	
	
	var checkParam = "&PRODUCT_ID=" + $("#PRODUCT_ID").val() + "&USER_ID=" + $("#GRP_USER_ID").val() + "&ALL_SELECTED_ELEMENTS=" + tempElements + "&SPEC_CDTION=grpBat&TIPS_TYPE=0|1|2";

	if(!grpBatruleCheck("com.asiainfo.veris.crm.order.web.frame.csview.group.rule.CreateGroupMemberRule", "checkGrpBatTipsRule", checkParam))
	{
		$('#remindPopup').attr('style','display:none');
		return false;
	}
	
	commSubmit();
	
	$('#remindPopup').attr('style','display:none');
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
	var tcsms = "";		//下发二次确认短信
	
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
	
	if ( elements == "" ) 
		return false;    
	//var idata = $.DatasetList(elements);
	var idata = selectedElements.getSubmitData();
	
	var batchOperTypeObj = $('#BATCH_OPER_TYPE').val()
	if(batchOperTypeObj == "BATADDWPGRPMEMBER"){
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
						if(oneAttrCode == "114485"){
							if(oneAttrValue == "" || oneAttrValue == null){
								alert("请选择订购套餐的折扣，折扣范围为0-100");
								return false;
							} else {
								var flag = $.verifylib.checkPInteger(oneAttrValue);
								if(!flag){
									alert("订购套餐的折扣必须是整数");
									return false;
								}
								if(oneAttrValue > 100 || oneAttrValue < 0){
									alert("订购套餐的折扣必须是0到100之间!");
									return false;
								}
							}
						}
						//分钟数校验 || 流量校验(单位以M/G结尾)
						else if(oneAttrCode == "144485" || oneAttrCode == "124485"){
							if(allSelectedElements.get("CHECK_OK")==false){
								alert(allSelectedElements.get("CHECK_INFO"));
								return false;
							}
						}
					}
				}
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
	info.put('CANCEL_LING',ldxs);	//不可来电取消彩铃业务
	info.put('PAGE_SELECTED_TC',tcsms);	//页面上勾选下发二次确认短信
	info.put('JOIN_IN',jrxyjt);	//加入相应集团
	info.put('PLAN_TYPE',pay_plan);
	info.put('GRP_SN', grp_sn);
	info.put('OPER_TYPE', oper_type);
	
	//add by wangyf6 at 20160809
	if($('#MEB_FILE_SHOW').val()=="true"){
		info.put('MEB_FILE_SHOW', "true");
		var mebFileList = $('#MEB_FILE_LIST').val();
		if(mebFileList != null && mebFileList != ""){
			info.put('MEB_FILE_LIST', mebFileList);
		}
	}
	//add by chenzg@20180704--begin--REQ201804280001集团合同管理界面优化需求---
	if($("#MEB_VOUCHER_FILE_LIST")){
		 var voucherFileList = $("#MEB_VOUCHER_FILE_LIST").val();
		 if(voucherFileList == ""){
			 alert("请上传凭证信息!");
			 return false;
		 }else{
			 info.put("MEB_VOUCHER_FILE_LIST", voucherFileList);
		 }
	 }
	 if($("#AUDIT_STAFF_ID")){
		 var auditStaffId = $("#AUDIT_STAFF_ID").val();
		 if(auditStaffId == ""){
			 alert("请选择稽核员!");
			 return false;
		 }else{
			 info.put("AUDIT_STAFF_ID", auditStaffId);
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


function unChangeTimeCount(){
	var tempElements = selectedElements.getSubmitData();
	
	var num=0;
	for(var i=0;i<tempElements.length;i++){
		temp = tempElements.get(i);
		var now = new Date();
		var startDate = temp.get("START_DATE");
	    if(startDate.substring(0,10) < now.format('yyyy-MM-dd')){
	    	alert("开始时间不能小于当前时间");
	    	$('#remindPopup').attr('style','display:none');
	    	return false;
	    }
	    if(temp.get(0)==84017249){
        	alert("如选择办理达量限速功能，使用超过套餐内的流量，则将进行限速。");
        }
		if(temp.get("isNeedChange")==1){
	        var endDate = temp.get("END_DATE").substring(0,10);
	        if(endDate == '2050-12-31'){
	        	num++;
	        }
		}
	}
	$("#unChangeNum").html(num);
	
	$('#remindPopup').attr('style','display:block')
}


