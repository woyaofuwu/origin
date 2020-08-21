 
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
	
	$("#USER_MOBILE").val(jsonObj["USER_MOBILE"]);
	$("#SPECIAL_TYPE").val(jsonObj["SPECIAL_TYPE"]);
	$("#TYPE_NAME").val(jsonObj["TYPE_NAME"]);
	$("#PROMPT_MESSAGE").val(jsonObj["PROMPT_MESSAGE"]);
	$("#ISBOLD").val(jsonObj["ISBOLD"]);
	$("#COLOR").val(jsonObj["COLOR"]);
	$("#REMOVE_TAG").val(jsonObj["REMOVE_TAG"]);
	$("#INSERT_TIME").val(jsonObj["INSERT_TIME"]);
	$("#OPER_TIME").val(jsonObj["OPER_TIME"]);
	$("#OPER_STAFF_ID").val(jsonObj["OPER_STAFF_ID"]);
}
function addSpeclist(){
	if (!$.validate.verifyAll("DetailListPart"))
		return false;

	$.beginPageLoading("正在进行新增操作...");
	$.ajax.submit('DetailListPart', 'addSpeclist', null, '', function(data){
		var tab = $.table.get("listFormTable");
		var jsonObj = String2JSON(data);
		tab.addRow(jsonObj);
		MessageBox.alert("提示","特殊名单新增成功！");
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		MessageBox.error("错误",error_info);
	});
}

function updateSpeclist(){
	var REMOVE_TAG = $("#REMOVE_TAG").val();

	//删除记录
	if (REMOVE_TAG=="1")
	{
		MessageBox.alert("提示","已失效记录不允许修改，请选择其它记录！");
		return false;
	}

	if (!$.validate.verifyAll("DetailListPart"))
		return false;


	$.beginPageLoading("正在进行修改操作...");
	$.ajax.submit('DetailListPart', 'updateSpeclist', null, '', function(data){
		var tab = $.table.get("listFormTable");
		var jsonObj = String2JSON(data);
		tab.updateRow(jsonObj);
		MessageBox.alert("提示","特殊名单修改成功！");
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		MessageBox.error("错误",error_info);
	});
}

function deleteSpeclist(){
	var REMOVE_TAG = $("#REMOVE_TAG").val();

	//删除记录
	if (REMOVE_TAG=="1")
	{
		MessageBox.alert("提示","已失效记录不允许重复删除，请选择其它记录！");
		return false;
	}

	//校验
	//if (!$.validate.verifyAll("DetailListPart"))
	//	return false;

	$.beginPageLoading("正在进行删除操作...");
	$.ajax.submit('DetailListPart', 'deleteSpeclist', null, '', function(data){
		var tab = $.table.get("listFormTable");
		var reData = tab.getRowData();
		var jsonObj = String2JSON(reData);
		jsonObj["REMOVE_TAG"]="1";
		tab.updateRow(jsonObj);
		MessageBox.alert("提示","特殊名单删除成功！");
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		MessageBox.error("错误",error_info);
	});
}