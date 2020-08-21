/*
*改变查询方式时,初始化界面,清空界面数据
*/
function initPage() {
	resetArea('QueryCondPart', true);
	/*resetArea('NormalQueryPart', true);
	resetArea('DayQueryPart', true);
	resetArea('TimeQueryPart', true);
	resetArea('SummaryQueryPart', true);*/

	$.table.get("MonitorInfoTable").cleanRows();
	$.table.get("SummaryInfoTable").cleanRows();
	$.table.get("SheetInfoTable").cleanRows();
	
	$("#NormalQueryPart").css("display", "none");
	$("#DayQueryPart").css("display", "none");
	$("#TimeQueryPart").css("display", "none");
	$("#SummaryQueryPart").css("display", "none");
	
	$("#MonitorInfoPart").css("display", "none");
	$("#SummaryInfoPart").css("display", "none");
	$("#SheetInfoPart").css("display", "none");
}

function showPage(formJwcid, tableJwcid) {
	initPage();
	formJwcid = "#" + formJwcid;
	tableJwcid = "#" + tableJwcid;
	$(formJwcid).css("display", "");
	$(tableJwcid).css("display", "");
}

function removeContentBind() {
	$("#START_SERIAL_NUMBER").attr("nullable", "yes");
	$("#END_SERIAL_NUMBER").attr("nullable", "yes");
	$("#DATE_REPORT").attr("nullable", "yes");
	$("#DATE_START").attr("nullable", "yes");
	$("#DATE_END").attr("nullable", "yes");
	$("#DATE_START_SUMMARY").attr("nullable", "yes");
	$("#DATE_END_SUMMARY").attr("nullable", "yes");
}

function addContentBind(type) {
	removeContentBind();
	if(type == '1') {//普通查询
		$("#START_SERIAL_NUMBER").attr("nullable", "no");
		$("#END_SERIAL_NUMBER").attr("nullable", "no");
	}else if(type == '2') {//日报表查询
		$("#DATE_REPORT").attr("nullable", "no");
	}else if(type == '3') {//时段表查询
		$("#DATE_START").attr("nullable", "no");
		$("#DATE_END").attr("nullable", "no");
	}else if(type == '4') {//汇总报表查询
		$("#DATE_START_SUMMARY").attr("nullable", "no");
		$("#DATE_END_SUMMARY").attr("nullable", "no");
	}else if(type == '5') {//清单报表查询
		$("#DATE_START_SUMMARY").attr("nullable", "no");
		$("#DATE_END_SUMMARY").attr("nullable", "no");
	}
}

function changeQueryType() {
	var data = $.DataMap();
	var type = $("#QUERY_TYPE").val();
	if(type == '1') {//普通查询
		showPage('NormalQueryPart', 'MonitorInfoPart');
	}else if(type == '2') {//日报表查询
		showPage('DayQueryPart', 'MonitorInfoPart');
	}else if(type == '3') {//时段表查询
		showPage('TimeQueryPart', 'MonitorInfoPart');
	}else if(type == '4') {//汇总报表查询
		showPage('SummaryQueryPart', 'SummaryInfoPart');
	}else if(type == '5') {//清单报表查询
		showPage('SummaryQueryPart', 'SheetInfoPart');
	}
	addContentBind(type);
	$("#QUERY_TYPE").val(type);
}

function queryMonitorInfo() {
	var type = $("#QUERY_TYPE").val();
	var refushArea = getRefushArea(type);
	if(!$.validate.verifyAll('QueryCondPart')) {
		return false;
	}
	
	if(!verifyData(type)) {
		return false;
	}
	
	$.beginPageLoading("载入中..");
    $.ajax.submit('QueryCondPart', 'queryMonitorInfo', null, refushArea, function(data){
		$.endPageLoading();
	},
	
	function(error_code,error_info,detail){
		$.endPageLoading();
		MessageBox.error("错误提示", error_info, null, null, null, detail);
    });
}

function getRefushArea(type) {
	if(type == '1') {//普通查询
		return 'MonitorInfoPart';
	}else if(type == '2') {//日报表查询
		return 'MonitorInfoPart';
	}else if(type == '3') {//时段表查询
		return 'MonitorInfoPart';
	}else if(type == '4') {//汇总报表查询
		return 'SummaryInfoPart';
	}else if(type == '5') {//清单报表查询
		return 'SheetInfoPart';
	}
}

function verifyData(type) {
	if(type == "1") {
		var stratSerialNumber = $("#START_SERIAL_NUMBER").val();
		var endSerialNumber = $("#END_SERIAL_NUMBER").val();
		if(parseInt(stratSerialNumber) > parseInt(endSerialNumber)) {
			alert("起始号码不能比结束号码大！");	
			return false;
		}
		if((parseInt(endSerialNumber) - parseInt(stratSerialNumber)) > 1000) {
			alert("服务号码【起始、终止】范围不能不能超过1000!");
			return false;
		}
		
	}else if(type == "3") {
		var startArr = $("#DATE_START").val().split("-");
		var endArr = $("#DATE_END").val().split("-");
		var startDate = new Date(startArr[0], startArr[1], startArr[2]).getTime();
		var endDate = new Date(endArr[0], endArr[1], endArr[2]).getTime();
		if(startDate > endDate) {
			alert("开始日期不能比结束日期大！");	
			return false;
		}
	}
	return true;
}

function exportBeforeAction(obj) {
	//$("#MonitorInfoPart").css("display", "none");
	//$("#SummaryInfoPart").css("display", "none");
	//$("#SheetInfoPart").css("display", "none");
	var data;
	var part1 = $("#MonitorInfoPart").css("display");
	var part2 = $("#SheetInfoPart").css("display");
	var part3 = $("#SummaryInfoPart").css("display");
	if('block' == part1) {
		data = $.table.get("MonitorInfoTable").getTableData("INTF_ID", true);
	}else if('block' == part2) {
		data = $.table.get("SheetInfoTable").getTableData("TITLE", true);
	}else if('block' == part3) {
		data = $.table.get("SummaryInfoTable").getTableData("TITLE", true);
	}
	//alert(data);
	if(data.length == 0) {
			alert("请先查询出数据再导出!");
			return false;
	}
	return true;
}