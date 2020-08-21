/**
 * modify by weixb3 2013-4-25
 */
 function validateParamPage(methodName, isPre) {

	if(methodName=='CrtUs'||methodName=='ChgUs'||methodName=='DstUs' || methodName=='BbossManageCrtUs'){
		if($("#dataList3")){
			var dataset = $.table.get("dataList3").getTableData();
			var tag1 = 0;
			var tag2 = 0
			for(var i=0;i<dataset.length;i++){
				var data = dataset.get(i);

				if(data.get("CONTACTOR_TYPE_CODE")==2){
					tag1 = 1;
				}
				if(data.get("CONTACTOR_TYPE_CODE")==5){
					tag2 = 1;
				}
			}
			
			if(dataset.length==0){
				//联系人信息表格需要新增记录
				alert("\u8054\u7cfb\u4eba\u4fe1\u606f\u8868\u683c\u9700\u8981\u65b0\u589e\u8bb0\u5f55");
				return false;
			}else if(tag1 == 0){
				//联系人信息表格，客户经理信息需要新增记录
				alert("\u8054\u7cfb\u4eba\u4fe1\u606f\u8868\u683c\uff0c\u5ba2\u6237\u7ecf\u7406\u4fe1\u606f\u9700\u8981\u65b0\u589e\u8bb0\u5f55");
				return false;
			}else if(tag2 == 0){
				//联系人信息表格，订单提交人员信息需要新增记录
				alert("\u8054\u7cfb\u4eba\u4fe1\u606f\u8868\u683c\uff0c\u8ba2\u5355\u63d0\u4ea4\u4eba\u5458\u4fe1\u606f\u9700\u8981\u65b0\u589e\u8bb0\u5f55");
				return false;
			}
			else{
				$("#CONTACTOR_INFOS").val($.table.get("dataList3").getTableData("X_TAG,CONTACTOR_TYPE_CODE,CONTACTOR_NAME,CONTACTOR_PHONE,STAFF_NUMBER"));
			}
		}
	}

	var mandatory = $("#MANDATORY").val();
	if((methodName=='CrtUs' && isPre == 'false') || (methodName == 'ChgUs' &&  mandatory == 'true') || 
		(methodName=='BbossManageCrtUs' && mandatory == 'true'))
	{
		if($("#dataList"))
		{
			if($.table.get("dataList").getTableData("ATT_TYPE_CODE")==null||$.table.get("dataList").getTableData("ATT_TYPE_CODE")=="[]")
			{
				//合同信息表格需要新增记录
				alert("\u5408\u540c\u4fe1\u606f\u8868\u683c\u9700\u8981\u65b0\u589e\u8bb0\u5f55");
				return false;
			}
			else
			{
				$("#ATT_INFOS").val($.table.get("dataList").getTableData("X_TAG,ATT_TYPE_CODE,ATT_NAME_filename"));
			}
		}

		$("#AUDITOR_INFOS").val($.table.get("dataList2").getTableData("X_TAG,AUDITOR,AUDITOR_TIME,AUDITOR_DESC"));
	}
	if(methodName=='ChgUs' || methodName=='BbossManageCrtUs'||methodName=='DstUs' )
	{
		if($("#ATT_INFO_TAG").val()=="true"){
			$("#ATT_INFOS").val($.table.get("dataList").getTableData());
		}
		if($("#AUDITOR_INFO_TAG").val()=="true"){
			$("#AUDITOR_INFOS").val($.table.get("dataList2").getTableData());

		}
		if($("#CONTACTOR_INFO_TAG").val()=="true"){
			$("#CONTACTOR_INFOS").val($.table.get("dataList3").getTableData());
		}
	}

	//特殊参数非空校验
	var result = $.validate.verifyAll('paraminfotabset');
	if(result)
	{
		//保存  联系人信息  审批信息 合同附件 商品信息 到商产品信息中
		saveMerchInfo();
		
		//拼装产品特殊参数
		var flag = saveMerchpParamInfo(methodName);
		if(!flag)
		{
		  return false;
		}
		  return true;
	}
	else
	{
		return false;
   	}

}

/*
  add by chenyi
  拼装产品特殊参数
 */
function saveMerchpParamInfo(methodName) {

	if (methodName == 'ChgUs' || methodName == 'DstUs') {
		// BBOSS商产品对象
		var productGoodInfos = new Wade.DataMap($("#productGoodInfos").val());
		var productInfoList = productGoodInfos.get("PRODUCT_INFO_LIST");
		
		var BBossParamInfo = new Wade.DataMap();
		if(productInfoList == '' || productInfoList=='undefined')
		{
			return true;
		}
		
		for (var i = 0; i < productInfoList.length; i++) 
		{
			var productInfo = productInfoList.get(i);
			if (productInfo.get("PRODUCT_ID") == (productInfoList.get(0).get("PRODUCT_ID"))&& (i != 0)) 
			{
				productInfoList.remove(0);
				continue;
			}
			var userId = productInfo.get("USER_ID");
			var flag=setBBossParamInfo(BBossParamInfo, userId, productInfo.get("PRODUCT_ID"));
			if(!flag)
				return false;
		}
		$("#BBossParamInfo").val(BBossParamInfo);

	}
	return true;
}

/*
 * 分产品录入特殊属性
 * add by chenkh
 */
function setBBossParamInfo(BBossParamInfo, userId, product_Id) {
	
	var dataList4 = $("#dataList4");
	if(dataList4.length==0)
	{
		return true;
	}
	
	var dataset = $.table.get("dataList4").getTableData(null, true);
	var flag = true;
	for (var i = 0; i < dataset.length; i++) {

		var data = dataset.get(i);
		var productId = data.get("PRODUCT_ID");
		if (product_Id != productId) 
		{
			continue;
		}

		if (!BBossParamInfo.containsKey(productId)) {
			var attrCode = data.get("PARAM_CODE");
			var attrName = data.get("PARAM_NAME");
			var attrValue = data.get("PARAM_VALUE");
			//判断user资料表中是否存在
			var recover = "";
			var oldAttrValue = "";
			//判断附件是否重填
			var oldAttachValue = $("#oldAttachValue_"+attrCode).val();
			if(null != oldAttachValue && oldAttachValue.length != 0 && oldAttachValue == attrValue){
				$.showErrorMessage("请重新上传特殊属性附件！");
				return false;
			}

			Wade.httphandler.submit("",
					"com.asiainfo.veris.crm.order.web.frame.csview.group.verifyClass.frontDataVerify",
					"qryTradeSpecAttr", "&USER_ID=" + userId + "&ATTR_CODE="
							+ attrCode + "&ATTR_VALUE=" + attrValue, function(d) {
						flag = d.map.result;
						if (flag == "false") {
							oldAttrValue = d.map.ATTR_VALUE;
							recover = d.map.recover;
						}
					}, function(e, i) {
						$.showErrorMessage("操作失败");
						result = false;
					}, {
						async : false
					});
			if (attrValue == "") {
				continue;
			}
			var params = new Wade.DatasetList();
			var param = new Wade.DataMap();
			if (flag == "false") {
				var paramOld = new Wade.DataMap();
				paramOld.put("ATTR_CODE", attrCode);
				paramOld.put("ATTR_NAME", attrName);
				paramOld.put("ATTR_VALUE", oldAttrValue);
				paramOld.put("STATE", "DEL");
				params.add(paramOld);
				BBossParamInfo.put(productId, params);
			}
			param.put("ATTR_CODE", attrCode);
			param.put("ATTR_NAME", attrName);
			param.put("ATTR_VALUE", attrValue);
			param.put("STATE", "ADD");
			params.add(param);
			BBossParamInfo.put(productId, params);
		} else {
			var attrCode = data.get("PARAM_CODE");
			var attrName = data.get("PARAM_NAME");
			var attrValue = data.get("PARAM_VALUE");
			//判断user资料表中是否存在
			var recover = "";
			var oldAttrValue = "";
			//判断附件是否重填
			var oldAttachValue = $("#oldAttachValue_"+attrCode).val();
			if(null != oldAttachValue&& oldAttachValue.length != 0 && oldAttachValue == attrValue){
				$.showErrorMessage("请重新上传特殊属性附件！");
				return false;
			}
			if (attrValue == "") {
				continue;
			}

			Wade.httphandler.submit("",
					"com.asiainfo.veris.crm.order.web.frame.csview.group.verifyClass.frontDataVerify",
					"qryTradeSpecAttr", "&USER_ID=" + userId + "&ATTR_CODE="
							+ attrCode + "&ATTR_VALUE=" + attrValue, function(d) {
						flag = d.map.result;
						if (flag == "false") {
							oldAttrValue = d.map.ATTR_VALUE;
							recover = d.map.recover;
						}
					}, function(e, i) {
						$.showErrorMessage("操作失败");
						result = false;
					}, {
						async : false
					});
			var param = new Wade.DataMap();
			if (flag == "false") {
				var paramOld = new Wade.DataMap();
				paramOld.put("ATTR_CODE", attrCode);
				paramOld.put("ATTR_NAME", attrName);
				paramOld.put("ATTR_VALUE", oldAttrValue);
				paramOld.put("STATE", "DEL");
				var paramsOld = BBossParamInfo.get(productId);
				paramsOld.add(paramOld);
				BBossParamInfo.put(productId, paramsOld);
			}
			param.put("ATTR_CODE", attrCode);
			param.put("ATTR_NAME", attrName);
			param.put("ATTR_VALUE", attrValue);
			param.put("STATE", "ADD");
			var params = BBossParamInfo.get(productId);
			params.add(param);
			BBossParamInfo.put(productId, params);
		}
	}
	return true;
}

/*
 * add by chenyi 保存 联系人信息 审批信息 合同附件 商品信息 到商产品信息中
 */

function saveMerchInfo(){
	//BBOSS商产品对象
	var productGoodInfos = new Wade.DataMap($("#productGoodInfos").val());
	//附件信息
	var attEdit=new Wade.DatasetList($("#ATT_INFOS").val());
	for(var i=0;i<attEdit.length;i++){
	      attEdit.get(i).put("ATT_NAME",attEdit.get(i).get("ATT_NAME_filename"));
	}

	//审批信息
	var auditorEdit=new Wade.DatasetList($("#AUDITOR_INFOS").val());
	//联系人信息
	var contactorEdit=new Wade.DatasetList($("#CONTACTOR_INFOS").val());
	var goodInfo = productGoodInfos.get("GOOD_INFO");

	goodInfo.put("ATT_INFOS",attEdit);
	goodInfo.put("AUDITOR_INFOS",auditorEdit);
	goodInfo.put("CONTACTOR_INFOS",contactorEdit);

	productGoodInfos.put("GOOD_INFO",goodInfo);

	$("#productGoodInfos").val(productGoodInfos);
	
	// 3- 商产品信息通过MC模式传递值，页面参数传递有大小限制(暂时不用)
//	var transParams = '&productGoodInfos=' + encodeURIComponent($('#productGoodInfos').val()) +	'&GROUP_ID='+ $('#GROUP_ID').val();
//	$.ajax.submit('', 'transProductGoodInfos', transParams , '', function(data) {
//	}, function(error_code, error_info) {
//	}, {
//		async : false
//	});
}

/**
 * modify by weixb3 2013-4-25
 */

function createAttInfo(obj) {
	var dataset = $.table.get("dataList").getTableData("ATT_TYPE_CODE")
	for(var i=0;i<dataset.length;i++){
		var data = dataset.get(i);
		if(data.get("ATT_TYPE_CODE")==1 && $("#ATT_TYPE_CODE").val()==1){
			//只能上传一个合同附件
			alert("\u53ea\u80fd\u4e0a\u4f20\u4e00\u4e2a\u5408\u540c\u9644\u4ef6");
			return false;
		}
		if(data.get("ATT_TYPE_CODE")==2 && $("#ATT_TYPE_CODE").val()==2){
			//只能上传一个普通附件
			alert("\u53ea\u80fd\u4e0a\u4f20\u4e00\u4e2a\u666e\u901a\u9644\u4ef6");
			return false;
		}
	}
	var attEdit = $.ajax.buildJsonData("ATT");

	if(attEdit.ATT_TYPE_CODE==null || attEdit.ATT_TYPE_CODE==""){
		//附件类型不能为空
		alert("\u9644\u4ef6\u7c7b\u578b\u4e0d\u80fd\u4e3a\u7a7a");
		return false;
	}
	if(attEdit.ATT_NAME_filename==null || attEdit.ATT_NAME_filename==""){
		//商品级相关附件名称
		alert("\u5546\u54c1\u7ea7\u76f8\u5173\u9644\u4ef6\u540d\u79f0\u4e0d\u80fd\u4e3a\u7a7a");
		return false;
	}else{
		attEdit.ATT_NAME_filename=$('#ATT_NAME').val();
	}

	//附件类型是合同附件
	if(attEdit.ATT_TYPE_CODE=="1"){

		if(attEdit.ATT_TYPE==""){
			attEdit.ATT_TYPE = "合同附件";
		}		
	}
	//请先上传附件到BBOSS
	/* 新增表格行 */

	$.table.get("dataList").addRow(attEdit);
	$("#ATT_INFO_TAG").val(true);
	//resetArea("ATT");
	resetArea("ATT",true);

}
function deleteAttInfo(obj) {
	/* 删除表格行 */
	$.table.get("dataList").deleteRow();
}
function createAuditorInfo(obj) {
	var auditorEdit = $.ajax.buildJsonData("AUDITOR");
	if(auditorEdit.AUDITOR==null || auditorEdit.AUDITOR==""){
		//审批人姓名不能为空
		alert("\u5ba1\u6279\u4eba\u59d3\u540d\u4e0d\u80fd\u4e3a\u7a7a");
		return false;
	}
	if(auditorEdit.AUDITOR_TIME==null || auditorEdit.AUDITOR_TIME==""){
		//审批时间不能为空
		alert("\u5ba1\u6279\u65f6\u95f4\u4e0d\u80fd\u4e3a\u7a7a");
		return false;
	}
	if(auditorEdit.AUDITOR_DESC==null ||auditorEdit.AUDITOR_DESC==""){
		//审批意见不能为空
		alert("\u5ba1\u6279\u610f\u89c1\u4e0d\u80fd\u4e3a\u7a7a");
		return false;
	}
	/* 新增表格行 */

	$.table.get("dataList2").addRow(auditorEdit);
	$("#AUDITOR_INFO_TAG").val("true");
	resetArea("AUDITOR",true); //重置 areaId 里的表单数据，clean:true|false


}
function deleteAuditorInfo(obj) {
	/* 删除表格行 */
	$.table.get("dataList2").deleteRow();
}
function createContactorInfo(obj) {
	/* 新增表格行 */
	var contactorEdit = $.ajax.buildJsonData("CONTACTOR");
	if(contactorEdit.CONTACTOR_TYPE==null || contactorEdit.CONTACTOR_TYPE==""){
		//联系人类型不能为空
		alert("\u8054\u7cfb\u4eba\u7c7b\u578b\u4e0d\u80fd\u4e3a\u7a7a");
		return false;
	}
	if(contactorEdit.CONTACTOR_NAME==null || contactorEdit.CONTACTOR_NAME==""){
		//联系人姓名不能为空
		alert("\u8054\u7cfb\u4eba\u59d3\u540d\u4e0d\u80fd\u4e3a\u7a7a");
		return false;
	}
	if(contactorEdit.CONTACTOR_PHONE==null || contactorEdit.CONTACTOR_PHONE==""){
		//联系人电话不能为空
		alert("\u8054\u7cfb\u4eba\u7535\u8bdd\u4e0d\u80fd\u4e3a\u7a7a");
		return false;
	}

	if (contactorEdit.CONTACTOR_TYPE_CODE == "1"){
		contactorEdit["CONTACTOR_NAME"]=contactorEdit["CONTACTOR_NAME_INPUT"];
	}else{
		contactorEdit["CONTACTOR_NAME"]=contactorEdit["POP_CONTACTOR_NAME"];
	}

	$.table.get("dataList3").addRow(contactorEdit);
	$("#CONTACTOR_INFO_TAG").val("true");
	resetArea("CONTACTOR",true); //重置 areaId 里的表单数据，clean:true|false

}
function deleteContactorInfo(obj) {
	/* 删除表格行 */
	$.table.get("dataList3").deleteRow();
}

/**附件类型下拉菜单修改时更新页面*/
function attTypeChange(obj) {
	$('#ATT_TYPE').val(obj.options[obj.selectedIndex].text);
	var attType = obj.value;
	if (attType==1) {
		$("#contArea").css("display","block");

	} else {
		$("#contArea").css("display","none");

	}
}

/**联系人类型下拉菜单修改时更新页面*/
function contactorTypeChange(obj) {
	var contactorType = obj.value;
	if (contactorType == 1) {
		$("#staffFieldInputArea")[0].style.display = "block";
		$("#staffFieldArea")[0].style.display = "none";
	} else {
		$("#staffFieldInputArea")[0].style.display = "none";
		$("#staffFieldArea")[0].style.display = "block";
	}

	var selectedIndex = $(obj)[0].selectedIndex;
	var contactorTypeName = $(obj)[0].options(selectedIndex).innerText;
	$('#CONTACTOR_TYPE').val(contactorTypeName);
}

/**员工组件选择后查询员工联系电话和总部用户名*/
function queryStaffNumber(obj) {
	if ($("#CONTACTOR_NAME").val() == "") {
		alert("\u8BF7\u6307\u5B9A\u5177\u4F53\u4EBA\u5458\uFF01");
		return;
	}
	var staffId = $("#CONTACTOR_NAME").val();
	$("#CONTACTOR_NAME").val($("#CONTACTOR_NAME"));
	ajaxSubmit("staffFieldArea","queryStaffNumber", "STAFF_ID=" + staffId, null,function(data) {
				if ($("#CONTACTOR_TYPE_CODE").val() == 5 && $("#STAFF_NUMBER")) {
					var staffNumber = data.get("STAFF_NUMBER");
					if (staffNumber == "") {
						alert("\u8BE5\u5458\u5DE5\u672A\u540C\u6B65\u603B\u90E8\u7528\u6237\u540D\uFF0C\u8BF7\u91CD\u65B0\u9009\u62E9\uFF01");
						return false;
					}
					$("#STAFF_NUMBER").val(staffNumber);
				}
				var serialNumber = data.get("STAFF_PHONE");
				$("#CONTACTOR_PHONE").val(serialNumber);
			});
}
