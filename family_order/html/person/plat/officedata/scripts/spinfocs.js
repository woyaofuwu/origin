 
function queryList(){
	//查询条件校验
	if(!$.validate.verifyAll("QueryCondPart")) {
		return false;
	}
	$.beginPageLoading("正在查询数据...");
	$.ajax.submit('QueryCondPart,queryNav', 'queryList', null, 'QueryListPart', function(data){
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		MessageBox.error("错误",error_info);
	});
}

function tableRowClick(e) {
	var tab = $.table.get("listFormTable");
	var rowData = tab.getRowData();
	var jsonObj = String2JSON(rowData);
	
	$("#BIZ_CODE").val(jsonObj["BIZ_CODE"]);
	$("#BIZ_NAME").val(jsonObj["BIZ_NAME"]);
	$("#SP_CODE").val(jsonObj["SP_CODE"]);
	$("#SP_NAME").val(jsonObj["SP_NAME"]);
	$("#SP_TYPE").val(jsonObj["SP_TYPE"]);
	$("#SP_ATTR").val(jsonObj["SP_ATTR"]);
	$("#SP_DESC").val(jsonObj["SP_DESC"]);
	$("#SP_STATUS").val(jsonObj["SP_STATUS"]);
	$("#PROVINCE_NO").val(jsonObj["PROVINCE_NO"]);
	$("#RECORD_STATUS").val(jsonObj["RECORD_STATUS"]);
	$("#RECORDROWID").val(jsonObj["RECORDROWID"]);
	
	$("#TRADE_TYPE").val(jsonObj["TRADE_TYPE"]);
	$("#CLIENT_GRADE").val(jsonObj["CLIENT_GRADE"]);
	$("#CLIENT_ATTR").val(jsonObj["CLIENT_ATTR"]);
	$("#BIZ_STATUS").val(jsonObj["BIZ_STATUS"]);
	$("#CH_SIGN").val(jsonObj["CH_SIGN"]);
	$("#SEND_TYPE").val(jsonObj["SEND_TYPE"]);
	$("#BUSI_SCOPE").val(jsonObj["BUSI_SCOPE"]);
	$("#TYPE").val(jsonObj["TYPE"]);
	$("#REPORT_TIME").val(jsonObj["REPORT_TIME"]+"0000");
	$("#OPE_CODE").val(jsonObj["OPE_CODE"]);
}
function addSpInfoCS(){
	if (!$.validate.verifyAll("DetailListPart"))
		return false;

	$.beginPageLoading("正在进行新增操作...");
	$.ajax.submit('DetailListPart', 'addSpInfoCS', null, '', function(data){
		var tab = $.table.get("listFormTable");
		var jsonObj = String2JSON(data);
		tab.addRow(jsonObj);
		MessageBox.alert("提示","企业信息新增成功！");
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		MessageBox.error("错误",error_info);
	});
}

function updateSpInfoCS(){
	var RECORD_STATUS = $("#RECORD_STATUS").val();

	//删除记录
	if (RECORD_STATUS=="03")
	{
		MessageBox.alert("提示","已删除记录不允许修改，请选择其它记录！");
		return false;
	}

	if (!$.validate.verifyAll("DetailListPart"))
		return false;


	$.beginPageLoading("正在进行修改操作...");
	$.ajax.submit('DetailListPart', 'updateSpInfoCS', null, '', function(data){
		var tab = $.table.get("listFormTable");
		var jsonObj = String2JSON(data);
		tab.updateRow(jsonObj);
		MessageBox.alert("提示","企业信息修改成功！");
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		MessageBox.error("错误",error_info);
	});
}

function deleteSpInfoCS(){
	var RECORD_STATUS = $("#RECORD_STATUS").val();

	//删除记录
	if (RECORD_STATUS=="03")
	{
		MessageBox.alert("提示","已删除记录不允许重复删除，请选择其它记录！");
		return false;
	}

	//校验
	//if (!$.validate.verifyAll("DetailListPart"))
	//	return false;

	$.beginPageLoading("正在进行删除操作...");
	$.ajax.submit('DetailListPart', 'deleteSpInfoCS', null, '', function(data){
		var tab = $.table.get("listFormTable");
		var reData = tab.getRowData();
		var jsonObj = String2JSON(reData);
		jsonObj["RECORD_STATUS_DIS"]=" 删除";
		jsonObj["RECORD_STATUS"]="03";
		tab.updateRow(jsonObj);
		MessageBox.alert("提示","企业信息删除成功！");
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		MessageBox.error("错误",error_info);
	});
}

function importOcsData(){
	
	if($("#cond_STICK_LIST").val()==""){
		alert('上传文件不能为空！');
		return false;
	}
	
	var truthBeTold = confirm("确定要导入数据吗?");
	if (!truthBeTold) {
		return false;
	} 
	$.beginPageLoading("努力导入中...");
	$.ajax.submit('SubmitCondPart','importData','','',function(data){
		var title= "批量数据导入结果!";
		var dealType=data.get("DEAL_TYPE");
		var hint_message = data.get("HINT_MESSAGE");
		var batch_id = data.get("BATCH_ID");
		var batchTaskId = data.get("cond_BATCH_TASK_ID");
		var FAILED_TYPE = data.get("FAILED_TYPE");
		var SUCC_SIZE = data.get("SUCC_SIZE");
		if(dealType == '1'){ 
			hint_message += "批量导入成功，并且已启动，按[<a jwcid='@Any' href=\"javascript:$.closeMessage(this)\">返回</a>]返回到本页面！批次号：" +batch_id + "<br/>";
		}else if(dealType == '2'){
			//hint_message += "批量导入成功，按[<a jwcid='@Any' href=\"javascript:$.closeMessage(this)\">返回</a>]返回到本页面！按[<a href=\"#nogo\" onclick=\"javascript:openNav('批量任务启动','bat.battaskstart.BatTaskStart', 'queryBatTaskStart','&FROM_PAGE=1&cond_BATCH_TASK_ID="+batchTaskId+"');\">批量任务启动</a>]进入启动页面!批次号："+batch_id + "<br/>";
			hint_message += "批量导入情况：共成功导入" + SUCC_SIZE + "条<br/>";
		}
		if(FAILED_TYPE == '1'){
			var DATASET_SIZE = data.get("DATASET_SIZE");
			var FAILD_SIZE = data.get("FAILD_SIZE");
			var FILE_ID = data.get("FILE_ID");
			var ERROR_URL = data.get("ERROR_URL");
			hint_message += "批量导入情况：共导入" + DATASET_SIZE + "条<br/>成功" + SUCC_SIZE + "条<br/>失败" + FAILD_SIZE + "条<br/>请击<a href="+ERROR_URL+">[批量导入失败列表.xls]</a>下载导入失败文件<br/>";
		}
		
		$.showSucMessage(title, hint_message);
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
	});
}

