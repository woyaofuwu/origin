function changeTypeCode() {
	var bizTypeCode = $("#BIZ_TYPE_CODE").val();
	$.beginPageLoading("载入中..");
     $.ajax.submit('QueryCondPart', 'queryOperCode', null, 'OperCodePart',function(data){
		$.endPageLoading();
	},
	
	function(error_code,error_info,detail){
		$.endPageLoading();
		MessageBox.error("错误提示", error_info, null, null, null, detail);
    });
	
}

/**
*在隐藏域设置OPER_NAME值,并改变Table格式
*/
function afterSelectOper(obj) {
	obj = $(obj);
	var operName = $("#"+obj.attr("id")+" :selected").text();
	
	var bizTypeCode = $("#BIZ_TYPE_CODE").val();
	var operCode = $("#OPER_CODE").val();
	
	var bo = bizTypeCode+""+operCode;
	
	$("#cond_BEGIN_TIME").val("");
	$("#cond_END_TIME").val("");
	
	if(bo=='4001001' || bo=='4001011') {
		$("#cond_BEGIN_TIME").attr("disabled",true);
		$("#cond_END_TIME").attr("disabled",true);
	}
	else {
		$("#cond_BEGIN_TIME").attr("disabled",false);
		$("#cond_END_TIME").attr("disabled",false);
	}
	
	setTable(bizTypeCode + operCode);
	for(var i=1; i<=11; i++) {
		var table = $.table.get("infoTable" + i);//获取 JavaScript 包装的 Table 对象
		table.cleanRows();//清空表格的内容
	}
	
}

/**
*设置表格样式
*/
function setTable(operCode) {
	for(var i=1; i<=11; i++) {
		$("#infoTable" + i).attr("style","display:none");
	}
	if(operCode=='4001001') {
		$("#infoTable1").attr("style","display:");
	}else if(operCode=='4001002') {
		$("#infoTable2").attr("style","display:");
	}else if(operCode=='4001003') {
		$("#infoTable3").attr("style","display:");
	}else if(operCode=='4001004') {
		$("#infoTable4").attr("style","display:");
	}else if(operCode=='4001005') {
		$("#infoTable5").attr("style","display:");
	}else if(operCode=='4001006') {
		$("#infoTable6").attr("style","display:");
	}else if(operCode=='4001007') {
		$("#infoTable7").attr("style","display:");
	}else if(operCode=='4001008') {
		$("#infoTable8").attr("style","display:");
	}else if(operCode=='4001009') {
		$("#infoTable9").attr("style","display:");
	}else if(operCode=='4001010') {
		$("#infoTable10").attr("style","display:");
	}else if(operCode=='4001011') {
		$("#infoTable11").attr("style","display:");
	}
}

function getQueryCond() {
	var operCode = $("#OPER_CODE").val();
	var operName = $("#OPER_NAME").val();
	popupPage('sundryquery.platinfo.PlatQueryCond', 'init', '&OPER_CODE=' + operCode, operName, '800', '1024', 'CODING_STR');
}

function queryPlatInfo() {
	if(!$.validate.verifyAll("QueryCondPart")) {
		return false;
	}
	/**
	var coding = $("#CODING_STR").val();
	var data = $.DataMap(coding);
	
	var serialNumber = data.get("SERIAL_NUMBER");
	var startDate = data.get("START_DATE");
	var endDate = data.get("END_DATE");
	**/
	//注释by ouyk
	var serialNumber =$("#cond_SERIAL_NUMBER").val();
	var startDate = $("#cond_BEGIN_TIME").val();
	var endDate = $("#cond_END_TIME").val();
	var param = "&SERIAL_NUMBER=" + serialNumber + "&START_DATE=" + startDate + "&END_DATE=" + endDate;
	
	$.beginPageLoading("信息查询中..");
     $.ajax.submit('QueryCondPart', 'queryPlatInfo', param, 'InfoTablePart', function(data){
     
     	var bizTypeCode = $("#BIZ_TYPE_CODE").val();
		var operCode = $("#OPER_CODE").val();
		setTable(bizTypeCode + operCode);
		$.endPageLoading();
	},
	
	function(error_code,error_info,detail){
		$.endPageLoading();
		MessageBox.error("错误提示", error_info, null, null, null, detail);
		for(var i=1; i<=11; i++) {
			var table = $.table.get("infoTable" + i);//获取 JavaScript 包装的 Table 对象
			table.cleanRows();//清空表格的内容
		}
    });
    
    return true;
}
