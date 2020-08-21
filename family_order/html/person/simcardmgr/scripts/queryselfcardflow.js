function queryFlow(){
	var serial_number = $("#cond_SERIAL_NUMBER").val();
	var serial_number_temp = $("#cond_SERIAL_NUMBER_TEMP").val();
	if(serial_number==""&&serial_number_temp==""){
		alert("手机号码或者临时号码不可同时为空！");
		return false;
	}
	$.beginPageLoading();
	$.ajax.submit('QueryCondPart', 'querySelfCard', "", 'QueryListPart', function(data){
		$("#NOW_DATE").val(data.get("NOW_DATE"));
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
	});
}

function delFlow(){
	var allData = $.table.get("choseTable").getTableData(null,true);
	if(allData.length<1){
		alert("无数据可以操作！")
		return false;
	}

	var rowData = $.table.get("choseTable").getRowData();
	if (rowData.length == 0) {
		alert("请您选择记录后再进行删除操作！");
		return false;
	}
	var transId = rowData.get("TRANS_ID");
	var startDate = rowData.get("START_DATE");
	var endDate = rowData.get("END_DATE");
	var nowDate = $("#NOW_DATE").val();
	var dealReason = $("#DEAL_REASON").val();

	//0表示历史，1表示非历史
	if(rowData.get("HIS") == "0"){
		alert("该记录是历史记录，不可删除！");
		return false;
	}
	var rowState = rowData.get("STATE");
	if(rowState == "C"){
		if(nowDate<endDate){
			alert("正在换卡中，不可删除！");
			return false;
		}
	}
	if(dealReason==null||dealReason==""){
		alert("删除原因不可为空！");
		return false;
	}
	var param = "&TRANS_ID="+transId+"&START_DATE="+startDate+"&DEAL_REASON="+dealReason;
	$.beginPageLoading();
	$.ajax.submit('', 'delFlowInfo', param, '', function(data){
		$.table.get("choseTable").deleteRow();
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
	});
}

function  selectInfo(){
	var allData = $.table.get("choseTable").getTableData(null,true);
	if(allData.length>1){
		var rowData = $.table.get("choseTable").getRowData();
		if (rowData.length > 0) {
			if(rowData.get("HIS") == "0"){
				alert("该记录是历史记录，不可删除！");
			}
		}
	}
} 

