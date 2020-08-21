function checkExport(){
	var startime = $("#START_TIME").val();//开始时间
	if( startime == '' ){
		alert("请选择查询开始时间");
		return false;
	}
	
	var endtime = $("#END_TIME").val();//结束时间
	if( endtime == '' ){
		alert("请选择结束时间");
		return false;
	}
	
	if(startime.substring(0,7) != endtime.substring(0,7)){
		alert("时间范围不能跨月！");
		return false;
	}
	
	return true;
}


//表格点击事件
function UiopReqQueryTableClick(){
	
	//获取选择行的数据
	var rowData = $.table.get("DeptTable").getRowData();
	$("#DETAIL_SERIAL_NUMBER").val(rowData.get("col_SERIAL_NUMBER"))
	$("#DETAIL_START_TIME").val(rowData.get("col_START_TIME"))
	$("#DETAIL_END_TIME").val(rowData.get("col_END_TIME"))
	$("#DETAIL_SVC_CODE").val(rowData.get("col_SVC_CODE"))
	$("#DETAIL_RESULTCODE").val(rowData.get("col_RESULTCODE"))
	$("#DETAIL_RESULTINFO").val(rowData.get("col_RESULTINFO"))
	$("#DETAIL_ISVC_CONTENT").val(rowData.get("col_ISVC_CONTENT"))
	$("#DETAIL_OSVC_CONTENT").val(rowData.get("col_OSVC_CONTENT"))
	$("#DETAIL_IN_MODE_CODE").val(rowData.get("col_IN_MODE_CODE"))
	$("#DETAIL_TRADE_STAFF_ID").val(rowData.get("col_TRADE_STAFF_ID"))
	$("#DETAIL_MONTH").val(rowData.get("col_MONTH"))
	$("#DETAIL_TRADE_ID").val(rowData.get("col_TRADE_ID"))
	$("#DETAIL_PROTOCOL").val(rowData.get("col_PROTOCOL"))
	$("#DETAIL_TRADE_DEPART_ID").val(rowData.get("col_TRADE_DEPART_ID"))
	$("#DETAIL_TRADE_EPARCHY_CODE").val(rowData.get("col_TRADE_EPARCHY_CODE"))
	$("#DETAIL_TRADE_DEPART_PASSWD").val(rowData.get("col_TRADE_DEPART_PASSWD"))
	$("#DETAIL_TRADE_TERMINAL_ID").val(rowData.get("col_TRADE_TERMINAL_ID"))
	$("#DETAIL_BIZ_CODE").val(rowData.get("col_BIZ_CODE"))
	$("#DETAIL_TRANS_CODE").val(rowData.get("col_TRANS_CODE"))
	$("#DETAIL_NEED_TIME").val(rowData.get("col_NEED_TIME"))
	$("#DETAIL_CALLSTART_TIME").val(rowData.get("col_CALLSTART_TIME"))
	$("#DETAIL_CALLEND_TIME").val(rowData.get("col_CALLEND_TIME"))
	$("#DETAIL_CALL_NEED_TIME").val(rowData.get("col_CALL_NEED_TIME"))
	$("#DETAIL_REMOTE_IP").val(rowData.get("col_REMOTE_IP"))
	$("#DETAIL_LOCAL_IP").val(rowData.get("col_LOCAL_IP"))
	$("#DETAIL_PORT").val(rowData.get("col_PORT"))
	
}

//查询列表
function queryList(){

	var serial_number = $("#SERIAL_NUMBER").val();//用户号码
	
	var startime = $("#START_TIME").val();//开始时间
	if( startime == '' ){
		alert("请选择查询开始时间");
		return false;
	}
	
	var endtime = $("#END_TIME").val();//结束时间
	if( endtime == '' ){
		alert("请选择结束时间");
		return false;
	}
	
	if(startime.substring(0,7) != endtime.substring(0,7)){
		alert("时间范围不能跨月！");
		return false;
	}
	
	
	$.beginPageLoading("数据查询中..");

	//查询
	$.ajax.submit('UiopReqQueryCondPart', 'queryList', null, 'UiopReqQueryListPart', function(){
		$.endPageLoading();
		
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });

}



