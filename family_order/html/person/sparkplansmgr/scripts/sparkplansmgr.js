function querysparkPlans()
{
	if(!$.validate.verifyAll("QueryCondPart")) {//先校验已配置的校验属性
		return false;
	}
	
	$.beginPageLoading("查询礼品库存。。。");
	$.ajax.submit('QueryCondPart', 'querysparkPlans', '', 'QueryListPart', 
		function(ajaxData){
			$.endPageLoading();
			clearData();
		},
		function(code, info){
			$.endPageLoading();
			alert(info);
		});
}

function myTabSwitchAction(ptitle, title)
{
	return true;
}

function selectRow()
{
	clearData();
	
	var table = $.table.get("SparkPlansTable");
	var rowData = table.getRowData();
	
	//礼品调拨页面
	/*
	$('#range_CITY_CODE').val(rowData.get('CITY_NAME'));
	$('#range_DEPART_ID').val(rowData.get('DEPART_NAME'));
	$('#range_STAFF_ID').val(rowData.get('STAFF_ID'));
	$('#range_PRODUCT_NAME').val(rowData.get('PRODUCT_NAME'));
	$('#range_PACKAGE_NAME').val(rowData.get('PACKAGE_NAME'));
	$('#range_WARNNING_VALUE_D').val(rowData.get('WARNNING_VALUE_D'));
	$('#range_WARNNING_VALUE_U').val(rowData.get('WARNNING_VALUE_U'));
	$('#range_SURPLUS_VALUE').val(rowData.get('SURPLUS_VALUE'));*/
}

function assignSparkPlans()
{
	var table = $.table.get("SparkPlansTable");
	var rowData = table.getRowData();
	if(rowData.length == 0)
	{
		alert('请先选择一条记录');
		return false;
	}
	
	var param = '&EPARCHY_CODE=0898' + '&STAFF_ID=' + rowData.get('STAFF_ID') + '&NUMBER=' + $('#range_NUMBER').val();
	param += '&RES_KIND_CODE=' + rowData.get('RES_KIND_CODE') + '&STAFF_ID_F=' + $('#range_STAFF_ID_F').val();
	param += '&PRODUCT_NAME=' + rowData.get('PRODUCT_NAME') + '&PACKAGE_NAME=' + rowData.get('PACKAGE_NAME');
	param += '&CITY_CODE=' + rowData.get('CITY_CODE') + '&OPER_TYPE=0' + '&DEPART_ID=' + rowData.get('DEPART_ID');
	$.beginPageLoading("提交中。。。");
	$.ajax.submit(null, 'assignSparkPlans', param, '',afterAssignSparkPlans,
		function(code, info){
			$.endPageLoading();
			alert(info);
		});
}

function afterAssignSparkPlans()
{
	$.endPageLoading();
	alert('操作成功');
	querysparkPlans();
}

function importAssignInfo()
{
	var table = $.table.get("SparkPlansTable");
	var rowData = table.getRowData();
	if(rowData.length == 0)
	{
		alert('请先选择一条记录');
		return false;
	}
	
	if($('#ASSIGN_FILE_ID').val() == '')
	{
		alert('请先选择文件');
		return false;
	}
	
	var param = '&EPARCHY_CODE=0898' + '&STAFF_ID=' + rowData.get('STAFF_ID') + '&FILE_ID=' + $('#ASSIGN_FILE_ID').val();
	param += '&RES_KIND_CODE=' + rowData.get('RES_KIND_CODE');
	param += '&PRODUCT_NAME=' + rowData.get('PRODUCT_NAME') + '&PACKAGE_NAME=' + rowData.get('PACKAGE_NAME');
	param += '&CITY_CODE=' + rowData.get('CITY_CODE') + '&OPER_TYPE=0' + '&DEPART_ID=' + rowData.get('DEPART_ID');
	$.beginPageLoading("导入中。。。");
	$.ajax.submit(null, 'importAssignInfo', param, '',
		function(data){
			$.endPageLoading();
			var succSize = data.get('SUCC_SIZE','0');
			var failSize = data.get('FAIL_SIZE','0');
			var errorUrl = data.get('ERROR_URL','');
			var totalSize = data.get('TOTAL_SIZE', '0');
			var message = '';
			if(failSize > 0)
			{
				message += "批量调拨导入情况：共导入" + totalSize + "条<br/>成功" + succSize + "条<br/>失败" + failSize + "条<br/>请击<a href="+errorUrl+">[批量导入失败列表.xls]</a>下载导入失败文件<br/>";
			}
			else
			{
				message += "批量调拨导入成功";
			}
			$.showSucMessage("批量调拨导入结果",message);
		},
		function(code, info){
			$.endPageLoading();
			alert(info);
		});
}

function importBack()
{
	var table = $.table.get("SparkPlansTable");
	var rowData = table.getRowData();
	if(rowData.length == 0)
	{
		alert('请先选择一条记录');
		return false;
	}
	
	if($('#BACK_FILE_ID').val() == '')
	{
		alert('请先选择文件');
		return false;
	}
	
	var param = '&EPARCHY_CODE=0898' + '&STAFF_ID=' + rowData.get('STAFF_ID') + '&FILE_ID=' + $('#BACK_FILE_ID').val();
	param += '&RES_KIND_CODE=' + rowData.get('RES_KIND_CODE');
	param += '&PRODUCT_NAME=' + rowData.get('PRODUCT_NAME') + '&PACKAGE_NAME=' + rowData.get('PACKAGE_NAME');
	param += '&CITY_CODE=' + rowData.get('CITY_CODE') + '&OPER_TYPE=1' + '&DEPART_ID=' + rowData.get('DEPART_ID');
	$.beginPageLoading("导入中。。。");
	$.ajax.submit(null, 'importBackInfo', param, '',
		function(data){
			$.endPageLoading();
			var succSize = data.get('SUCC_SIZE','0');
			var failSize = data.get('FAIL_SIZE','0');
			var errorUrl = data.get('ERROR_URL','');
			var totalSize = data.get('TOTAL_SIZE', '0');
			var message = '';
			if(failSize > 0)
			{
				message += "批量回收导入情况：共导入" + totalSize + "条<br/>成功" + succSize + "条<br/>失败" + failSize + "条<br/>请击<a href="+errorUrl+">[批量导入失败列表.xls]</a>下载导入失败文件<br/>";
			}
			else
			{
				message += "批量回收导入成功";
			}
			$.showSucMessage("批量回收导入结果",message);
		},
		function(code, info){
			$.endPageLoading();
			alert(info);
		});
}

function clearData()
{
	resetArea('EditPart');
	window.SimpleUpload_ASSIGN_FILE_ID.clearData();
	window.SimpleUpload_BACK_FILE_ID.clearData();
}

function queryAssignLog()
{
	if(!$.validate.verifyAll("QueryCondPart")) {//先校验已配置的校验属性
		return false;
	}
	
	$.beginPageLoading("查询中。。。");
	$.ajax.submit('QueryCondPart', 'queryAssignLog', '', 'QueryListPart,ExportPart', 
		function(ajaxData){
			$.endPageLoading();
		},
		function(code, info){
			$.endPageLoading();
			alert(info);
		});
}

function popupPrint()
{
	var table = $.table.get("SparkPlansTable");
	var rowData = table.getRowData();
	if(rowData.length == 0)
	{
		alert('请先选择一条记录');
		return false;
	}
	
	/*
	 * var param = '&EPARCHY_CODE=0898' + '&STAFF_ID=' + rowData.get('STAFF_ID') + '&NUMBER=' + $('#range_NUMBER').val();
	param += '&RES_KIND_CODE=' + rowData.get('RES_KIND_CODE') + '&STAFF_ID_F=' + $('#range_STAFF_ID_F').val();
	param += '&PRODUCT_NAME=' + rowData.get('PRODUCT_NAME') + '&PACKAGE_NAME=' + rowData.get('PACKAGE_NAME');
	param += '&CITY_CODE=' + rowData.get('CITY_CODE') + '&OPER_TYPE=0' + '&DEPART_ID=' + rowData.get('DEPART_ID');
	 * */
	
	var param = '&STAFF_ID='+rowData.get('STAFF_ID');
	param += '&refresh=true';
	param += '&STAFF_ID_F=' + $('#range_STAFF_ID_F').val();
	param += '&PACKAGE_NAME=' + encodeURIComponent(rowData.get('PACKAGE_NAME'));
	param += '&PRODUCT_NAME=' + encodeURIComponent(rowData.get('PRODUCT_NAME'));
	param += '&NUMBER=' + $('#range_NUMBER').val();
	param += '&OUT_DEPART_NAME=' + encodeURIComponent(rowData.get('DEPART_NAME'));
	param += '&OUT_CITY_NAME=' + encodeURIComponent(rowData.get('CITY_NAME'));
	
	popupPage ('sparkplansmgr.SparkPlansDepartDistributePrint','financeConfirminit',param, '星火计划礼包操作确认单', '700', '500');
}

function autoCopyNum(obj_start_name,obj_end_name){	
		
		var obj_start=$('#'+obj_start_name);
		var obj_end=$('#'+obj_end_name);
		
		obj_end.val(obj_start.val());
	}