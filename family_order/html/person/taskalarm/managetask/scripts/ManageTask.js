/*
 * 弹出业务配置修改信息对话框
 */
function popupEditTaskDialog(obj) {
	var trade_type_code = $(obj).attr('trade_type_code');
	var trade_type = encodeURI(encodeURI($(obj).attr('trade_type'))); // "Special Open"; //
	var trade_type_value = $(obj).attr('trade_type_value');
	popupPage('taskalarm.managetask.EditTask', 'initPage',
			'&refresh=true&cond_TRADE_TYPE_CODE=' + trade_type_code
					+ '&cond_TRADE_TYPE=' + trade_type
					+ '&cond_TRADE_TYPE_VALUE=' + trade_type_value, '监控业务管理',
			'700', '300');
}

function popupAddTaskDialog(obj) {

	//popupDialog('taskalarm.managetask.AddTask', null, '&refresh=true','新增监控业务', '1000', '400');
	//popupPage('taskalarm.managetask.AddTask', null, '&refresh=true','新增监控业务', '', '');
	popupPage('taskalarm.managetask.AddTask', null, '&refresh=true',
			'新增监控业务', '700', '300');
}

function queryConfiguredTask(obj) {
	refreshTask();
}

function closeMyWindow(){
	setPopupReturnValue("", "");
	return true;
}

/*
 * 刷新业务信息
 */
function refreshTask() {
	// 刷用配置列表
	$.ajax.submit('queryPart', 'queryConfiguredTask', null, 'RefreshPart',
			function(data) {
				$.endPageLoading();
				if (data.get('ALERT_INFO') != null
						&& data.get('ALERT_INFO') != '') {
					$.showWarnMessage(data.get('ALERT_INFO'));
				}
			}, function(error_code, error_info) {
				$.endPageLoading();
				alert(error_info);
			});
}

/*
 * 在"新增”功能中查询
 */
function queryTaskByCon() {
	$.beginPageLoading("数据查询中...");

	$.ajax.submit('QueryPopuCondPart,taskNav', 'queryTaskByCon', null,
			'RefreshPopuPart', function(data) {
				$.endPageLoading();
			}, function(error_code, error_info) {
				$.endPageLoading();
				alert(error_info);
			});
}

/*
 * 得到选中的复选框的值.
 */
function getSelectedRow() {
	var tradeTypeCodes = $.table.get("ResultTable").getCheckedRowDatas();// 获取选择中的数据
	if (tradeTypeCodes == null || tradeTypeCodes.length == 0) {
		alert('请选择待要删除的数据!');
		return false;
	}
	var param = "&multi_TRADE_TYPE_CODE=" + tradeTypeCodes;
	var tradeTypeCode = $("#cond_TRADE_TYPE_CODE").val();
	var tradeType = $("#cond_TRADE_TYPE").val();
	param += "&MAINCOND_TRADE_TYPE_CODE="+tradeTypeCode;
	param += "&MAINCOND_TRADE_TYPE="+tradeType;
	
	$.beginPageLoading("信息处理中.");
	$.ajax.submit('queryPart', 'delTask', param, 'RefreshPart', function(data) {
		if (data.get('DELETE_SUCCESS_FLAG') >0) {
			alert("监控业务删除成功");
		}
		//$("#QUERY_BTN").click();
		$.endPageLoading();
	},

	function(error_code, error_info, detail) {
		$.endPageLoading();
		MessageBox.error("错误提示", error_info, null, null, null, detail);
	});

}

function selectTask() {
	var task_id = "";
	var task_name = "";

	var task = $("input[name=task]:checked").val();

	if (task == null) {
		alert("请选中一行记录！");
		return false;
	}
	task_id = task.split(";")[0];
	task_name = task.split(";")[1];

	var task_info = task_id + ";" + task_name;

	setPopupReturnValue(task_info, task_name, true);
}

function addTask(obj) {
	var tradeTypeInfo = $("#cond_TRADE_TYPE").val();
	var tradeTypeValue = $('#cond_TRADE_TYPE_VALUE').val();
	var tradeTypeCode = tradeTypeInfo.split(";")[0];
	var tradeType = tradeTypeInfo.split(";")[1];
	var PtradeType  = parent.$("#cond_TRADE_TYPE").val();
	var PtradeTypeCode  = parent.$("#cond_TRADE_TYPE_CODE").val();
	//alert("Parent's tradeType:" + parent.$("#cond_TRADE_TYPE").val() + ",Parent's tradeTypeCode:" + parent.$("#cond_TRADE_TYPE_CODE").val());
	if (tradeTypeCode == '') {
		alert("请选择要配置的业务");
		return false;
	}
	if (tradeTypeValue == '') {
		alert("请为选择要监控的业务设置阀值");
		return false;
	}

	var param = '&add_TRADE_TYPE=' + tradeType + '&add_TRADE_TYPE_VALUE='
			+ tradeTypeValue + '&add_TRADE_TYPE_CODE=' + tradeTypeCode;
	param += "&MAINCOND_TRADE_TYPE_CODE="+PtradeTypeCode;
	param += "&MAINCOND_TRADE_TYPE="+PtradeType;
	$.beginPageLoading("数据新增中...");
	$.ajax.submit('addPart', 'addTask', param, null, function(data) {
		
		$.endPageLoading();
		
		
		
		debugger;
		parent.$("#QUERY_BTN").click();
		if (data.get('ADD_SUCCESS_FLAG') == '1') {
			alert("监控业务新增成功");
			
		}
		$.closePopupPage(true);
	    return true;
		//refreshTask();
		//window.close();
		//$.endPageLoading();
		//$("#QUERY_BTN").click();
		//$.endPageLoading();
		//$.closePopupPage(true);
	    //return true;
		
		 
	}, function(error_code, error_info) {
		$.endPageLoading();
		alert(error_info);
	});
}

function editTask(obj) {
	var tradeType = $('#cond_TRADE_TYPE').val();
	var tradeTypeCode = $('#cond_TRADE_TYPE_CODE').val();
	var tradeTypeValue = $('#cond_TRADE_TYPE_VALUE').val();
	var PtradeType  = parent.$("#cond_TRADE_TYPE").val();
	var PtradeTypeCode  = parent.$("#cond_TRADE_TYPE_CODE").val();
	//alert("Parent's tradeType:" + parent.$("#cond_TRADE_TYPE").val() + ",Parent's tradeTypeCode:" + parent.$("#cond_TRADE_TYPE_CODE").val());
	if (tradeTypeValue == '') {
		alert("要监控的阀值不能为空！");
		return false;
	}

	var param = '&edit_TRADE_TYPE_CODE=' + tradeTypeCode + '&edit_TRADE_TYPE='
			+ tradeType + '&edit_TRADE_TYPE_VALUE=' + tradeTypeValue;
	param += "&MAINCOND_TRADE_TYPE_CODE="+PtradeTypeCode;
	param += "&MAINCOND_TRADE_TYPE="+PtradeType;
	$.beginPageLoading("数据保存中...");

	$.ajax.submit('editPart', 'editTask', param, null, function(data) {
		parent.$("#QUERY_BTN").click();
		$.closePopupPage(true);
		$.endPageLoading();
		if (data.get('UPDATE_SUCCESS_FLAG') == '1') {
			setPopupReturnValue(tradeTypeCode, tradeTypeValue, true);
			//refreshTaskFromEditTask();
			alert("该业务阀值更新成功！");
		}
		//window.close(); 
		
		//$.endPageLoading();
		//alert("parent:" + parent);
		//parent.$("#QUERY_BTN").click();
		
		
	}, function(error_code, error_info) {
		$.endPageLoading();
		alert(error_info);
	});
}

/*
 * 刷新业务信息
 */
function refreshTaskFromEditTask() {
	// 刷用配置列表
	$.ajax.submit('queryPart', 'queryConfiguredTask', null, 'RefreshPart',
			function(data) {
				$.endPageLoading();
				if (data.get('ALERT_INFO') != null
						&& data.get('ALERT_INFO') != '') {
					$.showWarnMessage(data.get('ALERT_INFO'));
				}
			}, function(error_code, error_info) {
				$.endPageLoading();
				alert(error_info);
			});
}
