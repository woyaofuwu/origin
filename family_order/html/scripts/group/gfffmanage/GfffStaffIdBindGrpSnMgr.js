
function queryBindInfos(){
	
	$.beginPageLoading("数据查询中......");//数据查询中......
	$.ajax.submit("QueryCondPart", "queryBindInfos", null, "QueryCondPart,ratioPart,editForm", 
		function(data){
			$.endPageLoading();
		},
		function(error_code,error_info, derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
    	}
    );
}

function tableRowClick()
{
	var rowData = $.table.get("ratioTable").getRowData();
	var tradeStaffId = rowData.get("TRADE_STAFF_ID");
	var serialNumber = rowData.get("SERIAL_NUMBER");
	var updateTime = rowData.get("UPDATE_TIME");
	var instId = rowData.get("INST_ID");
	
	$("#E_TRADE_STAFF_ID").val(tradeStaffId);
	$("#E_SERIAL_NUMBER").val(serialNumber);
	$("#E_INST_ID").val(instId);
	
	$("#O_TRADE_STAFF_ID").val(tradeStaffId);
	$("#O_SERIAL_NUMBER").val(serialNumber);
}

function clearSetting()
{
	$("#E_USER_ID").val("");
	$("#E_GROUP_ID").val("");
	$("#E_CUST_NAME").val("");
	$("#E_SERIAL_NUMBER").val("");
	$("#E_GPRS_MAX").val("");
}

/**
 * 增加一行
 * @return
 */
function addTableRow() {
	var tradeStaffId = $("#E_TRADE_STAFF_ID").val();
	var otradeStaffId = $("#O_TRADE_STAFF_ID").val();
	var serialNumber = $("#E_SERIAL_NUMBER").val();
	var oserialNumber = $("#O_SERIAL_NUMBER").val();
	var instId = $("#E_INST_ID").val();
	
	if(tradeStaffId==null || $.trim(tradeStaffId).length==0){
		alert("工号不能为空");
		return false;
	}
	if(serialNumber==null || $.trim(serialNumber).length==0){
		alert("产品编码不能为空");
		return false;
	}
	
	if(tradeStaffId == otradeStaffId && serialNumber ==oserialNumber){
		alert("该条数据已存在，无法新增！");
		return false;
	}

	var param = "";
	param += '&TRADE_STAFF_ID='+tradeStaffId;
	param += '&SERIAL_NUMBER='+serialNumber;
	$.beginPageLoading("业务处理中...");	
	$.ajax.submit('editForm', 'submitAdd', param, 'QueryCondPart,editForm', function(data) {
		alert("增加工号绑定产品编码关系成功！");
		$.ajax.submit("QueryCondPart", "queryBindInfos", null, "QueryCondPart,ratioPart,editForm", 
				function(data){
					$.endPageLoading();
				},
				function(error_code,error_info, derror){
					$.endPageLoading();
					showDetailErrorInfo(error_code,error_info,derror);
		    	}
		    );
	},
	function(error_code,error_info,derror)
	{
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
}
/**
 * 修改一行
 * @return
 */
function updateTableRow() {
	var tradeStaffId = $("#E_TRADE_STAFF_ID").val();
	var otradeStaffId = $("#O_TRADE_STAFF_ID").val();
	var serialNumber = $("#E_SERIAL_NUMBER").val();
	var oserialNumber = $("#O_SERIAL_NUMBER").val();
	var instId = $("#E_INST_ID").val();
	if(tradeStaffId==null || $.trim(tradeStaffId).length==0){
		alert("工号不能为空");
		return false;
	}
	if(serialNumber==null || $.trim(serialNumber).length==0){
		alert("产品编码不能为空");
		return false;
	}
	if(instId==null || $.trim(instId).length==0){
		alert("唯一标识为空，只能新增！");
		return false;
	}
	if(tradeStaffId == otradeStaffId && serialNumber ==oserialNumber){
		alert("数据未变动，无法修改！");
		return false;
	}

	var param = "";
	param += '&TRADE_STAFF_ID='+tradeStaffId;
	param += '&SERIAL_NUMBER='+serialNumber;
	param += '&INST_ID='+instId;
	$.beginPageLoading("业务处理中...");	
	$.ajax.submit('editForm', 'submitMod', param, 'QueryCondPart,editForm', function(data) {
		alert("修改工号绑定产品编码关系成功！");
		$.ajax.submit("QueryCondPart", "queryBindInfos", null, "QueryCondPart,ratioPart,editForm", 
				function(data){
					$.endPageLoading();
				},
				function(error_code,error_info, derror){
					$.endPageLoading();
					showDetailErrorInfo(error_code,error_info,derror);
		    	}
		    );
	},
	function(error_code,error_info,derror)
	{
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
}
/**
 * 删除一行
 * @return
 */
function deleteTableRow() {
	var tradeStaffId = $("#E_TRADE_STAFF_ID").val();
	var serialNumber = $("#E_SERIAL_NUMBER").val();
	var instId = $("#E_INST_ID").val();
	
	if(instId==null || $.trim(instId).length==0){
		alert("唯一标识为空！");
		return false;
	}
	
	var param = "";
	param += '&TRADE_STAFF_ID='+tradeStaffId;
	param += '&SERIAL_NUMBER='+serialNumber;
	param += '&INST_ID='+instId;
	$.beginPageLoading("业务处理中...");	
	$.ajax.submit('editForm', 'submitDel', param, 'QueryCondPart,editForm', function(data) {
		alert("删除工号绑定产品编码关系成功！");
		$.ajax.submit("QueryCondPart", "queryBindInfos", null, "QueryCondPart,ratioPart,editForm", 
				function(data){
					$.endPageLoading();
				},
				function(error_code,error_info, derror){
					$.endPageLoading();
					showDetailErrorInfo(error_code,error_info,derror);
		    	}
		    );
	},
	function(error_code,error_info,derror)
	{
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
}

