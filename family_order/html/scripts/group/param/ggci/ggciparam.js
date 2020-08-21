function init() {   
}
function initCrtUs() {
}

/**
 * 选择费用名称处理
 * @return
 */
function onchangeFeeType(){
	var val = $('#GGCI_FEE_TYPECODE :selected').val();
	var text = $('#GGCI_FEE_TYPECODE :selected').text();
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
	$('#feeTipSpan').html(feeTips);
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
/**
 * 字符串日期转js日期对象
 * @param strDate
 * @return
 */
function stringToDate(strDate){
	var fullDate = strDate.split("-");
	return new Date(fullDate[0], fullDate[1]-1, fullDate[2],0,0,0);
}
/**
 * 下一步按钮校验处理
 * @param methodName
 * @return
 */
function validateParamPage(methodName) {
	var tableData = $.table.get("GgciTable").getTableData(null, true);
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
	 var rowData = $.table.get("GgciTable").getRowData();
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
function addTableRow() {
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
	$.table.get("GgciTable").addRow(editData);
}
/**
 * 修改表格行数据
 * @return
 */
function updateTableRow() {
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
	$.table.get("GgciTable").updateRow(editData);
}
/**
 * 删除表格行数据
 * @return
 */
function deleteTableRow() {
	//获取编辑区的数据
	var editData = $.ajax.buildJsonData("GgciTableEditPart");
	//往表格里添加一行并将编辑区数据绑定上
	$.table.get("GgciTable").deleteRow();
}
 
