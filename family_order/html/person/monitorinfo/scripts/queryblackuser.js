/*
*改变查询方式时,初始化界面,清空界面数据
*/
function initPage() {
	resetArea('QueryCondPart', true);

	$.table.get("monitorInfoTable").cleanRows();
	
	$("#NormalQueryPart").css("display", "none");
	$("#TimeQueryPart").css("display", "none");
}

function showPage(formJwcid) {
	initPage();
	formJwcid = "#" + formJwcid;
	$(formJwcid).css("display", "");
}

function removeContentBind() {
	$("#SERIAL_NUMBER").attr("nullable", "yes");
	$("#START_DATE").attr("nullable", "yes");
	$("#END_DATE").attr("nullable", "yes");
}

function addContentBind(type) {
	removeContentBind();
	if(type == '1') {//按服务号码查询
		$("#SERIAL_NUMBER").attr("nullable", "no");
	}else if(type == '2') {//按时间段查询
		$("#START_DATE").attr("nullable", "no");
		$("#END_DATE").attr("nullable", "no");
	}
}

function changeQueryType() {
	var type = $("#QUERY_TYPE").val();
	if(type == '1') {//按服务号码查询
		showPage('NormalQueryPart');
	}else if(type == '2') {//按时间段查询
		showPage('TimeQueryPart');
	}
	removeContentBind();
	addContentBind(type);
	$("#QUERY_TYPE").val(type);
}

function queryBlackUsers() {
	var type = $("#QUERY_TYPE").val();
	if(!$.validate.verifyAll('QueryCondPart')) {
		return false;
	}
	
	if(!verifyData(type)) {
		return false;
	}
	
	$.beginPageLoading("载入中..");
    $.ajax.submit('QueryCondPart', 'queryBlackUsers', null, 'MonitorInfoPart', function(data){
		$.endPageLoading();
		if(data!=null && data.get('ALERT_INFO')!=null ) {
			$("#TipInfoPart").css("display","block");
		}else {
			$("#TipInfoPart").css("display","none");
		}
	},
	
	function(error_code,error_info,detail){
		$.endPageLoading();
		MessageBox.error("错误提示", error_info, null, null, null, detail);
    });
}

function exportBeforeAction(obj) {
	var data = $.table.get("monitorInfoTable").getTableData("SERIAL_NUMBER", true);
	//alert(data);
	if(data.length == 0) {
		alert("请先查询出数据再导出!");
		return false;
	}
	return true;
}

function verifyData(type) {
	if(type == "2") {
		var startArr = $("#START_DATE").val().split("-");
		var endArr = $("#END_DATE").val().split("-");
		var startDate = new Date(startArr[0], startArr[1], startArr[2]).getTime();
		var endDate = new Date(endArr[0], endArr[1], endArr[2]).getTime();
		if(startDate > endDate) {
			alert("开始日期不能比结束日期大！");	
			return false;
		}
	}
	return true;
}