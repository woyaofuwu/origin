function queryBadnessInfo() {
	if(!verifyAll('badInfoPart'))
   	{
	   return false;
   	}
	$.beginPageLoading("信息查询中..");
     $.ajax.submit('badInfoPart,badInfoNav', 'queryBadnessInfo', null, 'badInfoTablePart', function(data){
		$.endPageLoading();
		if(data.get('ALERT_INFO') != null && data.get('ALERT_INFO') != '') {
			$.showWarnMessage(data.get('ALERT_INFO'));
		}
	},
	
	function(error_code,error_info,detail){
		$.endPageLoading();
		MessageBox.error("错误提示", error_info, null, null, null, detail);
    });
}

function queryOtherBadnessInfo() {
	var data = $.table.get("badTable").getCheckedRowDatas();//获取选择中的数据
	var param = "&BADNESS_TABLE=" + data;
	//alert(param);
	$.beginPageLoading("信息查询中..");
     $.ajax.submit('', 'queryOtherBadnessInfo', param, 'otherInfoTablePart', function(data){
		$.endPageLoading();
		if(data.get('ALERT_INFO') != null && data.get('ALERT_INFO') != '') {
			$.showWarnMessage(data.get('ALERT_INFO'));
		}
	},
	
	function(error_code,error_info,detail){
		$.endPageLoading();
		MessageBox.error("错误提示", error_info, null, null, null, detail);
    });
}

/**
*获取选择中的数据
*/
function getSelectedRowData() {
	//var data = $.table.get("badTable").getTableData(null, true);//获取全表数据
	var dataset = $.DatasetList();
	$("#BadnessTableInfos input[name=revcIdCheckBox]").each(function() {
	   if(this.checked){
	    	//this.click();
	    	var table = $.table.get("badTable");
	    	var data = table.getRowData();
	    	dataset.add(data);
	   }
	});
	return dataset;
}

function exportBeforeAction(obj) {
	var data = $.table.get("badTable").getTableData("INFO_RECV_ID", true);
	//alert(data);
	if(data.length == 0) {
		alert("请先查询出数据再导出!");
		return false;
	}
	return true;
}