var tablecacheData = new Wade.DataMap();

$(document).ready(function(){
	$.auth.setUserListSvc("SS.RestoreUserNpSVC.queryUserList");
})



function queryDestroyedUserInfo(data) {
	var serial_number = data.get("USER_INFO").get("SERIAL_NUMBER");
	var custStr = data.get("CUST_INFO").toString();
	var acctStr = data.get("ACCT_INFO").toString();
	var userStr = data.get("USER_INFO").toString();
	var param = "&SERIAL_NUMBER=" + serial_number + "&USER_INFO=" + userStr
			+ "&CUST_INFO=" + custStr + "&ACCT_INFO=" + acctStr;
	$.ajax.submit('AuthPart', 'queryDestroyedUserInfo', param,
			'oldUserInfoPart,oldNpUserInfoPart,newProductInfo,userResInfosPart,EditPart',
			function(data) {
				if (data && data.length > 0) {
					tablecacheData.clear();
					tablecacheData.put("info_"+serial_number, data.get("info"));
					var resparamObj = data.get("resparam");
					tablecacheData.put("resparam_"+serial_number, resparamObj);
					//必须得修改sim卡
					var need_change_sim = resparamObj.get("NEED_CHANGE_SIM");
					if(need_change_sim == "true"){
						$("#_sim").attr("class","e_required");
						$("#_sim_info").attr("class","e_required");
					}else{
						$("#_sim").attr("class","");
						$("#_sim_info").attr("class","");
					}
					
					
				}
			}, function(error_code, error_info) {
				$.endPageLoading();
				alert(error_info);
			});

}

function myTabSwitchAction(v, v2) {

	var objTabset = $.tabset("mytab");
	var title = objTabset.getCurrentTitle();// 获取当前标签页标题
	if (v != title) {
		objTabset.switchTo(v);
	}
	if (v2 != title) {
		objTabset.switchTo(v2);
	}

}

/**
 * 读卡前的动作
 * @returns {Boolean}
 */
function beforeReadCard(){
	
		var sn = $("#AUTH_SERIAL_NUMBER").val();
		$.simcard.setSerialNumber(sn);
		return true;
}

/**
 * 读卡后的动作
 */
function afterReadCard(data){
	var isWrited = data.get("IS_WRITED");//用来判断卡是否被写过
	if(isWrited == "1"){
		var simno =data.get("SIM_CARD_NO");
		 $("#SIM_CARD_NO").val(simno);
		 checkResource();
	}
}

/**
 * 写卡之后
 * @param data
 */
function afterWriteCard(data){
	if(data.get("RESULT_CODE")=="0"){
		$.simcard.checkReadCard();
	}
}

/*
function resTableRowClick(){
	var tableObj = $.table.get("userResTable");
	var rowIndex = tableObj.getTable().attr("selected");
	var json = tableObj.getRowData(null, rowIndex);
	var resTypeCode = json.get("RES_TYPE_CODE");
	var tag = json.get("tag");
	var serialNumber = $("#AUTH_SERIAL_NUMBER").val();
	var resparamObj = tablecacheData.get("resparam_"+serialNumber);
	var need_change_phone = resparamObj.get("NEED_CHANGE_NUMBER");
	
	
}
*/


/**
 * sim卡校验
 */
function checkResource(){
	var serialNumber = $("#AUTH_SERIAL_NUMBER").val();
	var simCardNo = $("#SIM_CARD_NO").val();
	if(simCardNo == ""){
		alert("sim卡不能为空！");
		return false;
	}
	
	var tabObj = getAllTableDataStatus("userResTable",null);
	var len = tabObj.length;
	var msg = "";
	for(var i = 0;i<len;i++){
		
		var data =  tabObj.get(i);
		var resTypeCode = data.get("RES_TYPE_CODE");
		var _tag = data.get("tag");
		if(resTypeCode == "1"){
			if(simCardNo == data.get("RES_CODE") &&_tag == 0 ){
				msg = "此【"+simCardNo+"】已经存在,并且校验成功！";
				break;
			}
			
//			if(_tag == 0){
//				msg = "已经存在一条校验通过的sim【"+data.get("RES_CODE")+"】卡号！";
//				break;
//			}
		}
	}
	
	if(msg!=""){
		alert(msg);
		return false;
	}
	
	
	var resCheckKey = simCardNo+"_isChecked";
	var resCheckObj = tablecacheData.get(resCheckKey);
	if(resCheckObj && resCheckObj == "1"){
		alert("该【"+simCardNo+"】卡已经校验通过");
		return;
	}

	$.beginPageLoading("sim卡校验中......");
	var param = "&SERIAL_NUMBER=" + serialNumber + "&SIM_CARD_NO=" + simCardNo+ "&RES_TYPE_CODE=1";
	$.ajax.submit(null, 'checkResource', param,null,
		function(data) {
		//alert(data);
		 var key = simCardNo+"_isChecked";
	   	 tablecacheData.put(key,"1");//1较验通过
	   	 //alert(data.get(0).get("RES_INFO_DATA"));
	   	// tablecacheData.put(key,data.get(0).get("RES_INFO_DATA"));
	 	
	   	addRow(data.get(0).get("RES_INFO_DATA"));
	   	$("#RES_INFO").val(data.get(0).get("RES_INFO_DATA").get("IMSI"));
	   	
		 var feeMode = data.get(0).get("FEE_MODE");
		 var feeTypeCode = data.get(0).get("FEE_TYPE_CODE");
		 var fee = data.get(0).get("FEE");
		 if(fee !=null && fee!=""){
			 var obj = new Wade.DataMap();
			 obj.put("TRADE_TYPE_CODE", "39");
			 obj.put("MODE", feeMode); 
			 obj.put("CODE", feeTypeCode); 
			 obj.put("FEE", fee);
			 $.feeMgr.removeFee("39",feeMode,feeTypeCode);
			 $.feeMgr.insertFee(obj);
			 tablecacheData.put("SIMCARD_FEE",obj);
		 }
		 $.endPageLoading();
			
		}, function(error_code, error_info) {
			$.endPageLoading();
			alert(error_info);
	});
}

/**
 * 
 * @param resObj 校验返回数据
 * @param simCardNo 当前校验sim卡号
 */
function addRow(data){
	
	var rowid = getRowIdcheckSimCard();
	if(rowid !=-1){
		var simCardNo = getSimCardcheckSimCard();
		var obj = $("tr[status=0]").click();
		$.table.get("userResTable").deleteRow();
		var simkey = simCardNo+"_isChecked";
	  	var simflag = tablecacheData.put(simkey,"");
	}
	
	
	var startDate = data.get("START_DATE");
	var endDate = data.get("END_DATE");
	var imsi = data.get("IMSI");
	var ki = data.get("KI");
	var opc = data.get("OPC");
	var simCardNo = data.get("SIM_CARD_NO");
	var resTypeCode = data.get("RES_TYPE_CODE");//资源接口返回的sim卡类
	
	var resEdit = new Array();
	resEdit["RES_TYPE_NAME"]="新sim卡";
	resEdit["RES_CODE"] = simCardNo;
	resEdit["START_DATE"] = startDate;
	resEdit["END_DATE"] = endDate;
	resEdit["RES_TYPE_CODE"] = "1";
	resEdit["IMSI"] = imsi;
	resEdit["KI"] = ki;
	resEdit["OPC"] = opc;
	resEdit["NEW_RES_TYPE_CODE"] = resTypeCode;
    $.table.get("userResTable").addRow(resEdit, 0, null, null, null);
}



function checkSimCardIsChange(){
		var tabObj = getAllTableDataStatus("userResTable",null);
		var len = tabObj.length;
		for(var i = 0;i<len;i++){
			
			var data =  tabObj.get(i);
			var resTypeCode = data.get("RES_TYPE_CODE");
			var _tag = data.get("tag");
			if(resTypeCode == 1 && _tag == 0){
				return true;
			}
		}
	
	return false;
}


//得到新增sim卡行号
function getRowIdcheckSimCard(){
	var tabObj = getAllTableDataStatus("userResTable",null);
	var len = tabObj.length;
	for(var i = 0;i<len;i++){
		
		var data =  tabObj.get(i);
		var resTypeCode = data.get("RES_TYPE_CODE");
		var _tag = data.get("tag");
		if(resTypeCode == 1 && _tag == 0){
			return i+1;
		}
	}

return -1;
}


//得到校验 过的sim卡号
function getSimCardcheckSimCard(){
	var tabObj = getAllTableDataStatus("userResTable",null);
	var len = tabObj.length;
	for(var i = 0;i<len;i++){
		
		var data =  tabObj.get(i);
		var resTypeCode = data.get("RES_TYPE_CODE");
		var _tag = data.get("tag");
		if(resTypeCode == 1 && _tag == 0){
			return data.get("RES_CODE");
		}
	}

return -1;
}



/**
 * 提交前校验
 * @returns {Boolean}
 */
function submitBeforeAction() {
	
	
	var serialNumber = $("#AUTH_SERIAL_NUMBER").val();
	var key = "resparam_"+serialNumber
	var resparamObj = tablecacheData.get(key);
	
	var simCardNo = getSimCardcheckSimCard();
	var simkey = simCardNo+"_isChecked";
  	var simflag = tablecacheData.get(simkey);
	var need_change_sim = resparamObj.get("NEED_CHANGE_SIM");
	if(need_change_sim == "true" && simflag !="1"){
		alert("sim卡校验没有通过,请重新校验！");
		return false;
	}
	
	
	
	var tabObj = getAllTableDataStatus("userResTable",null);
	var len = tabObj.length;
	if(len<3){
		alert("资源信息有误 ！");//这里应该 有3条数据，一条手机号码，一条老sim，一条新sim
		return false;
	}
	
	var data = selectedElements.getSubmitData();
	if(data.length<1){
		alert("请选择产品！");
		return false;
	}
	
	
	tablecacheData.put(simkey,"");//提交后清空sim校验 通过标记

	var param = "&SELECTED_ELEMENTS=" + data.toString() + "&PRODUCT_ID="
			+ $("#PRODUCT_ID").val();

	
	var userNpInfo = tablecacheData.get("info_"+serialNumber);
	var asp = userNpInfo.get("ASP");
	var port_out_date = userNpInfo.get("PORT_OUT_DATE");
	var home_netid = userNpInfo.get("HOME_NETID");
	var a_np_card_type = userNpInfo.get("A_NP_CARD_TYPE");

	var b_np_card_type = userNpInfo.get("B_NP_CARD_TYPE");
	var port_out_netid = userNpInfo.get("PORT_OUT_NETID");
	var port_in_netid = userNpInfo.get("PORT_IN_NETID");
	var np_service_type = userNpInfo.get("NP_SERVICE_TYPE");
	var invoiceNo = $("#_INVOICE_CODE").val()
    var userId = $.auth.getAuthData().get("USER_INFO").get("USER_ID");
	param = param + "&SERIAL_NUMBER="+serialNumber+"&resInfos="+tabObj+"&INVOICE_NO="+invoiceNo+"&USER_ID="+userId;
	$.cssubmit.addParam(param);
	//tablecacheData.clear();
	return true;
}

$(document).ready(function() {

	// $("#AUTH_SUBMIT_BTN").unbind();
	// $("#AUTH_SUBMIT_BTN").bind("click",
	// function() {
	// queryDestroyedUserInfo();
	// });
})

// ===================产品组件脚本开始================================================
/**
 * 选中select
 * 
 * @param select名称
 * @param val
 *            值
 */
function selectedOption(name, val) {

	$("#" + name + " option[value=" + val + "]").attr("selected", true);
}

function checkBeforeProduct() {

	var eparchy_code = $.auth.getAuthData().get("USER_INFO")
			.get("EPARCHY_CODE");
	ProductSelect.popupProductSelect($('#PRODUCT_TYPE_CODE').val(),
			eparchy_code, '');
}

/**
 * 选完产品后的动作
 * 
 * @param productId
 * @param productName
 * @param brandCode
 * @param brandName
 */
function afterChangeProduct(productId, productName, brandCode, brandName) {
	$("#PRODUCT_ID").val(productId);
	$("#PRODUCT_NAME").val(productName);
	var eparchy_code = $.auth.getAuthData().get("USER_INFO")
			.get("EPARCHY_CODE");
	var param = "&NEW_PRODUCT_ID=" + productId;
	packageList.renderComponent(productId, eparchy_code);
	pkgElementList.initElementList(null);

	selectedElements.renderComponent(param, eparchy_code);
	$.cssubmit.disabledSubmitBtn(false);
	$("#BRAND").val($("#PRODUCT_TYPE_CODE :selected").text());
	
	$.feeMgr.clearFeeList("39");
    $.feeMgr.insertFee(tablecacheData.get("SIMCARD_FEE"));
	var feeData = $.DataMap();
	var inparam = "&PRODUCT_ID="+productId + "&BRAND_CODE="+brandCode + "&EPARCHY_CODE="+eparchy_code;
	$.ajax.submit(null, 'getProductFeeInfo', inparam, null, function(data) {
	    $.cssubmit.disabledSubmitBtn(false);
	  	for(var i = 0; i < data.getCount(); i++) {
	  	     var data0 = data.get(i);
		     if(data0){
						feeData.clear();
						feeData.put("MODE", data0.get("FEE_MODE"));
						feeData.put("CODE", data0.get("FEE_TYPE_CODE"));
						feeData.put("FEE",  data0.get("FEE"));
						feeData.put("PAY",  data0.get("FEE"));		
						feeData.put("TRADE_TYPE_CODE","39");							
						$.feeMgr.insertFee(feeData);			
				}
	  	}

       },
       function(error_code,error_info,derror){
		$.endPageLoading();
		alert(error_info);
		});

}

/**
 * 产品搜索
 * 
 * @returns {Boolean}
 */
function searchOptionEnter() {
	var searchType = $("#productSearchType").val();
	var searchLi = $("#Ul_Search_productSearch li[class=focus]");
	if (searchType == "1") {
		// 产品搜索
		var productId = searchLi.attr("PRODUCT_ID");
		var productName = searchLi.attr("PRODUCT_NAME");
		var brandCode = searchLi.attr("BRAND_CODE");
		var brandName = searchLi.attr("BRAND");
		afterChangeProduct(productId, productName, brandCode, brandName);
	} else if (searchType == "2") {
		// 元素搜索
		var reOrder = searchLi.attr("REORDER");
		var elementId = searchLi.attr("ELEMENT_ID");
		var elementName = searchLi.attr("ELEMENT_NAME");
		var productId = searchLi.attr("PRODUCT_ID");
		var packageId = searchLi.attr("PACKAGE_ID");
		var elementTypeCode = searchLi.attr("ELEMENT_TYPE_CODE");
		var forceTag = searchLi.attr("FORCE_TAG");

		if (reOrder != "R"
				&& selectedElements.checkIsExist(elementId, elementTypeCode)) {
			alert("您所选择的元素" + elementName + "已经存在于已选区，不能重复添加");
			return false;
		}
		var elementIds = $.DatasetList();
		var selected = $.DataMap();
		selected.put("PRODUCT_ID", productId);
		selected.put("PACKAGE_ID", packageId);
		selected.put("ELEMENT_ID", elementId);
		selected.put("ELEMENT_TYPE_CODE", elementTypeCode);
		selected.put("MODIFY_TAG", "0");
		selected.put("ELEMENT_NAME", elementName);
		selected.put("FORCE_TAG", forceTag);
		selected.put("REORDER", reOrder);
		elementIds.add(selected);
		if (selectedElements.addElements) {
			selectedElements.addElements(elementIds);
		}
	}
	$("#Div_Search_productSearch").css("visibility", "hidden");
}

/**
 * 设置品牌
 */
function setBrandCode() {
	if ($("#PRODUCT_TYPE_CODE").val() != "") {
		$("#BRAND").val($("#PRODUCT_TYPE_CODE :selected").text());
		initProduct();
	} else {
		$("#BRAND").val('');
	}
}

/**
 * 初始化产品产
 */
function initProduct() {
	var eparchy_code = $.auth.getAuthData().get("USER_INFO")
			.get("EPARCHY_CODE");
	// packageList.renderComponent("",$("#EPARCHY_CODE").val());
	packageList.renderComponent("", eparchy_code);
	pkgElementList.initElementList(null);
	// selectedElements.renderComponent("&NEW_PRODUCT_ID=",$("#EPARCHY_CODE").val());
	selectedElements.renderComponent("&NEW_PRODUCT_ID=", eparchy_code);
	$("#PRODUCT_NAME").val('');
}

function disableElements(data) {
	if ($("#B_REOPEN_TAG").val() == '1') {
		selectedElements.disableAll();
	} else {
		if (data) {
			var temp = data.get(0);
			if (data.get(0).get("NEW_PRODUCT_START_DATE")) {
				$("#NEW_PRODUCT_START_DATE").val(
						temp.get("NEW_PRODUCT_START_DATE"));
			}
		}
	}
}
// ===================产品组件脚本结束================================================
