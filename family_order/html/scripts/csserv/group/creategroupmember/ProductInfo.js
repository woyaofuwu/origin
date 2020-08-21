
function initProductInfo() {
	var effectNow = $("#EFFECT_NOW").attr("checked");
	if (effectNow == true) {
		selectedElements.isEffectNow = effectNow;
	}
}

/**提示*/
function productInfoNextCheck() {

	if (typeof (validateParamPage) == "function") {
		mytab.switchTo("产品参数信息");
		var result = $.validate.verifyAll("prama")
		if(!result){
			return false;
		}
		
		if (!validateParamPage("CrtMb")) {
			return false;
		}
	}
	
	
	mytab.switchTo("产品信息");
		//校验必选包
	if (!selectedElements.checkForcePackage()) {
		return false;
	}
	
	var tempElements = selectedElements.selectedEls;
	if (!tempElements) {
		return false;
	}
	
	var flag=1;
    if(flag==1){
        flag=0;
    var num=0;
    var length= selectedElements.getSubmitData().length;
    for(var i=0;i<length;i++){
        temp=selectedElements.getSubmitData().get(i);
        if(temp.get("isNeedChange")==1){
        	var endDate = temp.get("END_DATE").substring(0,10);
        	if(endDate == '2050-12-31'){
        		num++;
        	}
        }
        if(temp.get("ELEMENT_ID")==84017249){
        	alert("如选择办理达量限速功能，使用超过套餐内的流量，则将进行限速。");
        }
        var now = new Date();
		var startDate = temp.get("START_DATE");
        if(startDate.substring(0,10) < now.format('yyyy-MM-dd')){
        	alert("开始时间不能小于当前时间");
        	return false;
        }
        
    }
    if(num!=0) {alert("结束日期为2050年12月31日，请确认是否修改");}
    }
    
	for (var i = 0; i < tempElements.length; i++) {
		if (tempElements.get(i, "MODIFY_TAG") == "0" && tempElements.get(i, "ATTR_PARAM_TYPE") == "9" && tempElements.get(i, "ATTR_PARAM").get(0, "PARAM_VERIFY_SUCC") == "false") {
			alert(tempElements.get(i, "ELEMENT_NAME") + ",\u7f3a\u5c11\u670d\u52a1\u53c2\u6570\uff0c\u8bf7\u8865\u5168\u76f8\u5173\u670d\u52a1\u53c2\u6570\u4fe1\u606f\uff01");
			$('#'+tempElements.get(i,'ITEM_INDEX')+'_ATTRPARAM').click(); 
			return false;
		}
	}
	
	if(!$.validate.verifyAll("productInfoPart")){
		return false;
	}
	if($("#MEB_VOUCHER_FILE_LIST") && $("#MEB_VOUCHER_FILE_LIST").val() == ""){
		alert("请上传凭证信息");
		mytab.switchTo("凭证信息");
		return false;
	}
	if($("#AUDIT_STAFF_ID") && $("#AUDIT_STAFF_ID").val() == ""){
		alert("请选择稽核人员！");
		mytab.switchTo("凭证信息");
		return false;
	}
	
	$.beginPageLoading('业务验证中....');
	var checkParam = "&SERIAL_NUMBER=" + $("#MEB_SERIAL_NUMBER").val() + "&PRODUCT_ID=" + $("#PRODUCT_ID").val() + "&USER_ID_B=" + $("#MEB_USER_ID").val()+ "&USER_ID=" + $("#GRP_USER_ID").val() + "&BRAND_CODE_B=" + $("#MEB_BRAND_CODE").val() + "&EPARCHY_CODE_B=" + $("#MEB_EPARCHY_CODE").val() + "&ALL_SELECTED_ELEMENTS=" + tempElements+"&IF_ADD_MEB="+$("#IF_ADD_MEB").val()+"&SHORT_CODE="+$("#pam_SHORT_CODE").val();
	pageFlowRuleCheck("com.asiainfo.veris.crm.order.web.frame.csview.group.rule.CreateGroupMemberRule", "checkProductInfoRule", checkParam);
	
	return false;
}

function pageFlowCheckAfterAction(){
	var submitData = selectedElements.getSubmitData();
	if (!submitData) {
		return false;
	}
	$("#SELECTED_ELEMENTS").val(submitData);
	
	var selectparam = "&SELECTED_ELEMENTS=" + submitData + "&ID=" + $("#MEB_USER_ID").val() + "&PRODUCT_ID=" + $("#PRODUCT_ID").val() + "&TRADE_TYPE_CODE=" + $("#TRADE_TYPE_CODE").val();
	Wade.httphandler.submit("", "com.asiainfo.veris.crm.order.web.frame.csview.group.common.util.GroupCacheUtilHttpHandler", "saveSelectElementsCache", selectparam, "", "", {async:false});
	return true;
}
/**
 * 点击左侧包之后,执行的自定义方法
 */
function pkgListAfterSelectAction(pkg) {
	var selfParam = "&GRP_USER_ID=" + $("#GRP_USER_ID").val() + "&PRODUCT_ID=" + $("#PRODUCT_ID").val();
	var eparchyCode = $("#MEB_EPARCHY_CODE").val();
	pkgElementList.renderComponent(pkg, eparchyCode, selfParam);
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

function productTabSwitchAction(ptitle,title){
	
	if ($('#elementPanel').length != 0  && $('#elementPanel').css('display') =='block'){
		$('#elementPanel').css('display','none');
	}
	
	return true;
}  

function getOtherParam()
{
    var grpUserId=$("#GRP_USER_ID").val();
    var grpProductId=$("#PRODUCT_ID").val();
    var mebProductId=$("#MEB_PRODUCT_ID").val();
    
    var param="&GRP_USER_ID="+grpUserId+"&GRP_PRODUCT_ID="+grpProductId+"&PRODUCT_ID="+mebProductId;
    return param;
} 

