
function exportResult()
{
	var export1 = $("#export1");
	if($('#badTable tbody tr').length<=0){
		export1.disabled="false";
		MessageBox.alert("导出结果","没有数据存在,不能导出!",null,null,null);
		return;
	}
    $.beginPageLoading("正在导出数据...");
	ajaxSubmit('badInfoTablePart', 'exportDatas', null, null, function(data){
		window.location = data.get("URL");
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
	}
	 );
}


/**报表时调用*/
function queryBadnessInfosForm() {
	if(!verifyData()) {
		return false;
	}
	
	$.beginPageLoading("查询中..");
     $.ajax.submit('badInfoPart', 'queryBadnessInfosForm', null, 'badInfoTablePart',function(data){
		$.endPageLoading();
		if(data.get("ALERT_CODE") != null) {
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

/**查询时调用 For S系列*/
function queryBadnessInfosNew() {
	if(!verifyData()) {
		return false;
	}
	
	$.beginPageLoading("查询中..");
     $.ajax.submit('badInfoPart', 'queryBadnessInfosNew', null, 'badInfoTablePart',function(data){
		$.endPageLoading();
		if(data.get("ALERT_CODE") != null) {
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

/**查询时调用*/
function queryBadnessInfos() {
	if(!verifyData()) {
		return false;
	}
	
	$.beginPageLoading("查询中..");
     $.ajax.submit('badInfoPart', 'queryBadnessInfos', null, 'badInfoTablePart',function(data){
		$.endPageLoading();
		if(data.get("ALERT_CODE") != null) {
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

/**统计时调用*/
function staticsBadnessInfos() {
	if(!verifyData()) {
		return false;
	}
	
	$.beginPageLoading("查询中..");
     $.ajax.submit('badInfoPart', 'staticsBadnessInfos', null, 'badInfoTablePart',function(data){
		$.endPageLoading();
		if(data.get("ALERT_CODE") != null) {
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

function verifyData() {
	var startArr = $("#REPORT_START_TIME").val().split("-");
	var endArr = $("#REPORT_END_TIME").val().split("-");
	var startDate = new Date(startArr[0], startArr[1], startArr[2]).getTime();
	var endDate = new Date(endArr[0], endArr[1], endArr[2]).getTime();
	if(startDate > endDate) {
		alert("开始日期不能比结束日期大,请检查输入!");	
		return false;
	}else if(startArr[0] != endArr[0]) {
		alert("起止日期不能跨年,请检查输入!");	
		return false;
	}else if(endArr[1] - startArr[1] > 2) {
		alert("起止日期不能跨三个月份,请检查输入!");	
		return false;
	}
	return true;
}

function changeReportType() {
	var reportTypeCode =$("#REPORT_TYPE_CODE").val();
	var param = '&REPORT_TYPE_CODE=' + reportTypeCode;
	
	$.beginPageLoading("载入中..");
     $.ajax.submit('', 'getServRequestType', param, 'ServRequestTypePart',function(data){
		$.endPageLoading();
	},
	
	function(error_code,error_info,detail){
		$.endPageLoading();
		MessageBox.error("错误提示", error_info, null, null, null, detail);
    });
}

function exportResult4S()
{
	var export1 = $("#export1");
	if($('#badTable tbody tr').length<=0){
		export1.disabled="false";
		MessageBox.alert("导出结果","没有数据存在,不能导出!",null,null,null);
		return;
	}
    $.beginPageLoading("正在导出数据...");
	ajaxSubmit('badInfoPart', 'exportDatas', null, null, function(data){
		window.location = data.get("URL");
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
	}
	 );
}