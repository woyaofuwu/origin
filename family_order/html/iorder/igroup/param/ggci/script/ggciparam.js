function init() {  
	debugger;
	window["GgciTable"] = new Wade.Table("GgciTable", {
		fixedMode:true,
		editMode:true,
	});
	$("#GgciTable").tap(function(e){
		if(e.target && $.nodeName(e.target, "input") && "checkbox" == $.attr(e.target, "type"))
			return;
		tableRowClick(GgciTable.getSelectedRowData());
	});
}

/**
 * 选择费用名称处理
 * @return
 */
function onchangeFeeType(){
	var val = $('#GGCI_FEE_TYPECODE').val();
	var text =$('#GGCI_FEE_TYPECODE').text();
	var feeTips = "";
	//信息系统使用功能费
	if(val == "0"){
		feeTips = "信息系统使用功能费：向客户提供系统服务，收取服务费、软件功能费、平台使用费或其他增值业务费等；通常按月计费，客户使用通信费资金，开具发票内容为：*电信服务*通信服务费。";
	}
	//信息系统集成费
	else if(val == "1"){
		feeTips = "信息系统集成费：将硬件、网络、数据库及相应的应用软件进行优化整合，提供全面系统解决方案而向客户收取的系统集成费，主要包括项目集成方案的设计咨询、集成开发费、安装联调、工程建设和其他费用。通常按项目进度计费，客户使用项目专项资金，开具发票内容为：*信息技术服务*信息系统服务。";
	}
	//信息系统服务费
	else if(val == "2"){
		feeTips = "信息系统服务费：向客户提供系统服务，收取服务费、软件功能费、平台使用费或其他增值业务费等，以及包含后续维护保障服务而向客户收取的硬件和软件维保费。通常按项按月计费费，通常客户使用的项目专项资金，开具发票内容为：*信息技术服务*信息系统服务";
	}
	else{
		feeTips = "";
	}
	$('#GGCI_FEE_TYPECODE_tip').attr("tip",feeTips);
	$('#GGCI_FEE_NAME').val(text);
	
}
/**
 * 取两个日期的相差月份
 * @param startDate
 * @param endDate
 * @return
 */
function getIntervalMonth(endDate, startDate){
	var startMonth = startDate.getMonth();
	var endMonth = endDate.getMonth();
	var intervalMonth = (endDate.getFullYear()*12+endMonth) - (startDate.getFullYear()*12+startMonth);
	return intervalMonth;
}
///**
// * 字符串日期转js日期对象
// * @param strDate
// * @return
// */
function stringToDate(strDate){
	var fullDate = strDate.split("-");
	return new Date(fullDate[0], fullDate[1]-1, fullDate[2],0,0,0);
}
/**
 * 下一步按钮校验处理
 * @param methodName
 * @return
 */
function validateChaParamPage() {
	var tableData = GgciTable.getData();
	if(tableData==null || tableData.length == 0){
		alert("请填写“项目名称,信息系统使用功能费,信息系统集成费,信息系统服务费”信息！");
		return false;
	}
	$("#pam_GGCI_TABLE_DATA").val(tableData);
    return true;
}
/**
 * 点击表格行数据处理
 * @return
 */
function tableRowClick(){
	//获取选择行的数据
	var j=GgciTable.selected;
	var  rowData=GgciTable.getRowData(j);
	
	 $("#GGCI_PROJ_NAME").val(rowData.get("GGCI_PROJ_NAME"));
	 $("#GGCI_FEE_TYPECODE").val(rowData.get("GGCI_FEE_TYPECODE"));
	 $("#GGCI_FEE_NAME").val(rowData.get("GGCI_FEE_NAME"));
	 $("#GGCI_FEE_NUM").val(rowData.get("GGCI_FEE_NUM"));
	 $("#GGCI_START_DATE").val(rowData.get("GGCI_START_DATE"));
	 $("#GGCI_END_DATE").val(rowData.get("GGCI_END_DATE"));
	 $("#GGCI_REMARK").val(rowData.get("GGCI_REMARK"));
	 //触发选择费用名称事件
	 $("#GGCI_FEE_TYPECODE").trigger("onchange");
}
/**
 * 新增表格行
 * @return
 */
function addGgciTableRow() {
	debugger;
	//获取编辑区的数据
	var editData = $.ajax.buildJsonData("GgciTableEditPart");
	if($.trim(editData.GGCI_PROJ_NAME) == ''){
		alert('请填写项目名称！');
		return false;
	}
	if($.trim(editData.GGCI_FEE_TYPECODE) == ''){
		alert('请选择收费名称！');
		return false;
	}
	if($.trim(editData.GGCI_FEE_NUM) == '' || !$.verifylib.checkPInteger(editData.GGCI_FEE_NUM)){
		alert('请填写收费金额，要求填写正整数！');
		return false;
	}
	if($.trim(editData.GGCI_START_DATE) == ''){
		editData.GGCI_START_DATE = $('#hidden_SYS_DATE_NOW').val();
		//alert('请填写生效时间！');
		//return false;
	}
	if($.trim(editData.GGCI_END_DATE) == ''){
		alert('请填写收费截止时间！');
		return false;
	}
	//计算有效期(月份)
	//var intervalMonth = getIntervalMonth(stringToDate(editData.GGCI_END_DATE), stringToDate(editData.GGCI_START_DATE));
	//往表格里添加一行并将编辑区数据绑定上	
	GgciTable.addRow(editData);
}
/**
 * 修改表格行数据
 * @return
 */
function updateGgciTableRow() {
	//获取编辑区的数据
	var editData = $.ajax.buildJsonData("GgciTableEditPart");
	if($.trim(editData.GGCI_PROJ_NAME) == ''){
		alert('请填写项目名称！');
		return false;
	}
	if($.trim(editData.GGCI_FEE_TYPECODE) == ''){
		alert('请选择收费名称！');
		return false;
	}
	if($.trim(editData.GGCI_FEE_NUM) == '' || !$.verifylib.checkPInteger(editData.GGCI_FEE_NUM)){
		alert('请填写收费金额，要求填写正整数！');
		return false;
	}
	if($.trim(editData.GGCI_START_DATE) == ''){
		alert('请填写生效时间！');
		return false;
	}
	if($.trim(editData.GGCI_END_DATE) == ''){
		alert('请填写收费截止时间！');
		return false;
	}
	//计算有效期(月份)
	//editData.GGCI_INTERVAL_MONTH = getIntervalMonth(stringToDate(editData.GGCI_END_DATE), stringToDate(editData.GGCI_START_DATE));
	//往表格里添加一行并将编辑区数据绑定上
	GgciTable.updateRow(editData);
}
/**
 * 删除表格行数据
 * @return
 */
function deleteGgciTableRow() {
//	//获取编辑区的数据
//	var editData = $.ajax.buildJsonData("GgciTableEditPart");
//	//往表格里添加一行并将编辑区数据绑定上
	var j=GgciTable.selected;
	var  delData=GgciTable.getRowData(j);
	GgciTable.deleteRow(j);
}

/**
 * 提交前数据校验
 * @param obj
 * @returns {Boolean}
 */
function validateParamPage(obj) {
	debugger;
	if(!$.validate.verifyAll("dynamicOfferParam")){
		return false;
	}
	//选择异网号码
	if(! validateChaParamPage())
	{
		
		return false;
	}
	if(submitDynamicOfferChaSpec($(obj).attr('OFFER_ID'))){
		backPopup(obj);
		return true;
	}else{
		return false;
	}
	return true;
}
//提交产品特征规格
function submitDynamicOfferChaSpec(offerId)
{
	var offerId = $("#prodDiv_OFFER_ID").val();
	
	var brand= PageData.getData($(".e_SelectOfferPart")).get("BRAND");
	
	var offerChaSpecDataset = new Wade.DatasetList(); //产品规格特征
	
	var chaSpecObjs = $("#dynamicOfferParam input");
	
	

	var chaSpecId = 0;
	for(var i = 0, size = chaSpecObjs.length; i < size; i++)
	{
		var chaSpecObjId = chaSpecObjs[i].id;
		var chaSpecObjName = chaSpecObjs[i].name;
		var chaValue = "";
		if(chaSpecObjName.indexOf("pam_") == -1)
		{
			continue;
		}
		if("BOSG"==brand&&isBbossNull(chaSpecObjId)){
			if(chaSpecObjs[i].type != "checkbox" &&  chaSpecObjs[i].type != "radio") // 复选框 只有name没有id
			{
				continue;
			}
		}
		else if (isNull(chaSpecObjId)) {
			if(chaSpecObjs[i].type != "checkbox" &&  chaSpecObjs[i].type != "radio") // 复选框 只有name没有id
			{
				continue;
			}
		}
		if(chaSpecObjs[i].type == "checkbox" ||  chaSpecObjs[i].type == "radio")
		{
			chaValue = chaSpecObjs[i].checked ? "on" : "";
		}
		else
		{
			chaValue = $("#"+chaSpecObjId).val();
		}
		if(chaValue == null || "" == chaValue){
			continue;
		}
		var offerChaSpecData = new Wade.DataMap();
		var objSpecId = chaSpecObjs[i].specId;
		if(objSpecId != null && "" != objSpecId){
			offerChaSpecData.put("CHA_SPEC_ID", objSpecId);
		}else{
			offerChaSpecData.put("CHA_SPEC_ID", chaSpecId);
			chaSpecId = chaSpecId + 1;
		}
		offerChaSpecData.put("ATTR_VALUE", chaValue);
		
		if(chaSpecObjName.indexOf("pam_") == 0)
		{
			var prodChaValCode = chaSpecObjName.substring(4);
			offerChaSpecData.put("ATTR_CODE", prodChaValCode);

		}
		if(chaSpecObjs[i].type == "radio" || chaSpecObjs[i].type == "checkbox"){//针对些单选框的name没有pam的情况
			if(chaSpecObjName.indexOf("pam_") != 0){
				offerChaSpecData.put("ATTR_CODE", chaSpecObjName);
			}			
		}
		
		offerChaSpecDataset.add(offerChaSpecData);
	}

	var chaSpecObjs2 = $("#dynamicOfferParam textarea");
	for(var i = 0, size = chaSpecObjs2.length; i < size; i++)
	{
		var chaSpecObjId = chaSpecObjs2[i].id;
		var chaSpecObjName = chaSpecObjs2[i].name;
		var chaValue = $("#"+chaSpecObjId).val();
		var offerChaSpecData = new Wade.DataMap();
		offerChaSpecData.put("ATTR_VALUE", chaValue);
		if(chaSpecObjName.indexOf("pam_") == 0)
		{
			var prodChaValCode = chaSpecObjName.substring(4);
			offerChaSpecData.put("ATTR_CODE", prodChaValCode);
		}
		offerChaSpecData.put("CHA_SPEC_ID", chaSpecId);
		
		offerChaSpecData.put("ATTR_NAME", chaSpecObjs[i].getAttribute("desc"));
		chaSpecId = chaSpecId + 1;
		offerChaSpecDataset.add(offerChaSpecData);
	}
	
	var grpItemEleObjs =$("#dynamicOfferParam textarea[isgrpitem='true']");
	if (grpItemEleObjs && grpItemEleObjs.length>0){
		for (var i = 0;i<grpItemEleObjs.length;i++){
			var grpMapObj = new Wade.DataMap($(grpItemEleObjs[i]).text());
			var paramData = grpMapObj.get("ELEMENT_DATAS");
			for (var j =0;j<paramData.items.length;j++){
				var offerChaSpecData = new Wade.DataMap();
				var grpItem = $(grpItemEleObjs[i]).attr("id").substring(8);
				offerChaSpecData.put("CHA_SPEC_ID", paramData.keys[j]);
				offerChaSpecData.put("CHA_VALUE", paramData.items[j]);
				offerChaSpecData.put("GROUP_ATTR", grpItem);
				offerChaSpecDataset.add(offerChaSpecData);
			}
		}
	}
	
	$("#cha_"+offerId).val(offerChaSpecDataset);
	
	//将产品特征规格放到数据对象中 
	if(offerChaSpecDataset.length != 0)
	{
		var offerData = PageData.getData($("#prodDiv_OFFER_DATA"));
		offerData.put("OFFER_CHA_SPECS", offerChaSpecDataset);
		
		var esopOfferCha = offerData.get("ESOP_OFFER_CHA");
		if(!esopOfferCha)
		{
			offerData.removeKey("ESOP_OFFER_CHA");
		}
		//保存数据
		PageData.setData($("#prodDiv_OFFER_DATA"), offerData);
	}
	
	$("ul[id=prodSpecUL] li:nth-child(1)").children().eq("1").css("display", "none");
	
	return true;
}

