
/*
*改变查询方式时,初始化界面,清空界面数据
*/
function initPage() {
	resetArea("QueryCondPart", true);
	$.table.get("MonitorInfoTable").cleanRows();
	$("#NormalQueryPart").css("display", "none");
	$("#DayQueryPart").css("display", "none");
	$("#TimeQueryPart").css("display", "none");
}
function showPage(formJwcid) {
	initPage();
	formJwcid = "#" + formJwcid;
	$(formJwcid).css("display", "");
}
function removeContentBind() {
	$("#START_SERIAL_NUMBER").attr("nullable", "yes");
	$("#END_SERIAL_NUMBER").attr("nullable", "yes");
	$("#DATE_REPORT").attr("nullable", "yes");
	$("#DATE_START").attr("nullable", "yes");
	$("#DATE_END").attr("nullable", "yes");
}
function addContentBind(type) {
	removeContentBind();
	if (type == "1") {//普通查询
		$("#START_SERIAL_NUMBER").attr("nullable", "no");
		$("#END_SERIAL_NUMBER").attr("nullable", "no");
	} else {
		if (type == "2") {//日报表查询
			$("#DATE_REPORT").attr("nullable", "no");
		} else {
			if (type == "3") {//时段表查询
				$("#DATE_START").attr("nullable", "no");
				$("#DATE_END").attr("nullable", "no");
			}
		}
	}
}
function changeQueryType() {
	var type = $("#QUERY_TYPE").val();
	if (type == "1") {//普通查询
		showPage("NormalQueryPart");
	} else {
		if (type == "2") {//日报表查询
			showPage("DayQueryPart");
		} else {
			if (type == "3") {//时段表查询
				showPage("TimeQueryPart");
			}
		}
	}
	addContentBind(type);
	$("#QUERY_TYPE").val(type);
}
function queryVerifySuspectSms() {
	var type = $("#QUERY_TYPE").val();
	if (!$.validate.verifyAll("QueryCondPart")) {
		return false;
	}
	if (!verifyData(type)) {
		return false;
	}
	$.beginPageLoading("载入中..");
	$.ajax.submit("QueryCondPart", "queryVerifySuspectSms", null, "MonitorInfoPart,TipInfoPart", function (data) {
		$.endPageLoading();
	}, function (error_code, error_info, detail) {
		$.endPageLoading();
		MessageBox.error("错误提示", error_info, null, null, null, detail);
	});
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

function handleSuspectSms() {
	var dataset = $.DatasetList();
	var checkTags = document.getElementsByName('INTF_ID');
	if(checkTags.length == 0) {
		alert('提交数据不能为空!');
		return false;
	}
	for(var i=0;i<checkTags.length;i++){
		var intfID = document.getElementsByName('INTF_ID')[i].value;
		var selectValue = document.getElementsByName('CHECK_TAG')[i].value;
		var dataMap = $.DataMap();
		dataMap.put("INTF_ID", intfID);
		dataMap.put("PROCESS_TAG", selectValue);
		dataset.add(dataMap);
	}
	
	var param = "&MONITORE_INFO=" + dataset.toString();
	$.beginPageLoading("业务处理中..");
	$.ajax.submit("QueryCondPart", "handleSuspectSms", param, "MonitorInfoPart", function (data) {
		$.endPageLoading();
		$.showSucMessage("审核成功!");
	}, function (error_code, error_info, detail) {
		$.endPageLoading();
		MessageBox.error("错误提示", error_info, null, null, null, detail);
	});
	return true;
}