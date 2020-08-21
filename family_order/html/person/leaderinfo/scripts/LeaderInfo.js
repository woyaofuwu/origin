function queryInfos()
{
	//查询条件校验
	if(!$.validate.verifyAll("QueryCondPart"))
	{
		return false;
	}
	$.beginPageLoading("数据查询中...");
	$.ajax.submit('QueryCondPart,RefreshPart', 'getLeaderInfo', null, 'RefreshPart', function(data)
    {
		if(data.get('ALERT_INFO') != '')
		{
			alert(data.get('ALERT_INFO'));
		}
		$.endPageLoading();
	},
	function(error_code,error_info)
	{
		$.endPageLoading();
		alert(error_info);
    });

}

function openAddWindow()
{
	var sn=$("#SERIAL_NUMBER").val();
	$.popupPage('leaderinfo.AddLeaderInfo', 'initAddPage', '&refresh=true&SERIAL_NUMBER='+sn, '新增领导信息', '800', '400');
}

function openEditWindow(rowIndex)
{
	var selTable = $.table.get("ResultTable");
	var rowValue = selTable.getRowData(null,rowIndex);
	var num=rowValue.length;
//	$("input[name=infos]:checked").each(function()
//		    {
//		    	var checkedVal = $(this).val();
//		    	if(checkedVal != "")
//		    	{
//		    		num++;
//		    	}
//			});
//	if(num==0){
//		alert("请选择要修改行");
//		return false;
//	}else if(num>1){
//		alert("一次只能选择一个修改,请重新选择");
//		return false;
//	}
	if(num==0){
		alert("请选择要修改行");
		return false;
	}
	var SERIAL_NUMBER = rowValue.get("SERIAL_NUMBER");
	var LEADER_NAME = rowValue.get("LEADER_NAME");
	var POSITION =rowValue.get("POSITION");
	
	var param = '&LEADER_NAME='+encodeURI(encodeURI(LEADER_NAME))+'&SERIAL_NUMBER='+SERIAL_NUMBER+'&POSITION='+encodeURI(encodeURI(POSITION));
	
	$.popupPage('leaderinfo.EditLeaderInfo', 'initEditPage', '&refresh=true'+param, '编辑领导信息', '800', '600');
}


function openDeleteWindow(rowIndex)
{
	var selTable = $.table.get("ResultTable");
	var rowValue = selTable.getRowData(null,rowIndex);
	var num=rowValue.length;
//	$("input[name=infos]:checked").each(function()
//		    {
//		    	var checkedVal = $(this).val();
//		    	if(checkedVal != "")
//		    	{
//		    		num++;
//		    	}
//			});
//	if(num==0){
//		alert("请选择要删除行");
//		return false;
//	}else if(num>1){
//		alert("一次只能选择一个删除,请重新选择");
//		return false;
//	}
	if(num==0){
		alert("请选择要删除行");
		return false;
	}
	var SERIAL_NUMBER = rowValue.get("SERIAL_NUMBER");
	var LEADER_NAME = rowValue.get("LEADER_NAME");
	var POSITION =rowValue.get("POSITION");
		
	var param = '&LEADER_NAME='+encodeURI(encodeURI(LEADER_NAME))+'&SERIAL_NUMBER='+SERIAL_NUMBER+'&POSITION='+encodeURI(encodeURI(POSITION));
	$.popupPage('leaderinfo.DeleteLeaderInfo', 'initDeletePage', '&refresh=true'+param, '确认要删除的领导信息', '800', '600');
	
}


function refreshTask()
{
	$.beginPageLoading("数据刷新中...");
	
	$.ajax.submit('QueryCondPart,staticNav', 'getLeaderInfo', null, 'RefreshPart', function(data)
    {
		$.endPageLoading();
	},
	function(error_code,error_info)
	{
		$.endPageLoading();
		alert(error_info);
    });
}

function addTask()
{
	//查询条件校验
	if(!$.validate.verifyAll("addLeaderInfoPart"))
	{
		return false;
	}
	
	$.beginPageLoading("数据新增中...");
	
	$.ajax.submit('addLeaderInfoPart', 'insertLeaderInfo', null, 'addLeaderInfoPart', function(data)
    { 
		var resultcode = data.get(0).get("X_RESULTCODE");
		if(resultcode == '0'){
			alert("新增领导信息成功。如需查看请重新查询！");
			setPopupReturnValue('','',true);
		}else{
			alert("新增领导信息失败");
		}
		$.endPageLoading();
	},
	function(error_code,error_info)
	{
		$.endPageLoading();
		alert(error_info);
    });
}

function editTask()
{
	//查询条件校验
	if(!$.validate.verifyAll("editLeaderInfoPart"))
	{
		return false;
	}
	
	$.beginPageLoading("数据保存中...");
	
	$.ajax.submit('editLeaderInfoPart', 'updateLeaderInfo', null, 'editLeaderInfoPart', function(data)
    {
		var resultcode = data.get(0).get("X_RESULTCODE");
		if(resultcode == '0')
		{
			alert("该领导信息更新成功.如需查看请重新查询！");
			setPopupReturnValue('','',true);
		
		}else{
			alert("修改领导信息失败");
		}
    	
		$.endPageLoading();
	},
	function(error_code,error_info)
	{
		$.endPageLoading();
		alert(error_info);
    });
}

function deleteTask()
{
	//查询条件校验
	if(!$.validate.verifyAll("deleteLeaderInfoPart"))
	{
		return false;
	}
	
	$.beginPageLoading("数据保存中...");
	
	$.ajax.submit('deleteLeaderInfoPart', 'deleteLeaderInfo', null, 'deleteLeaderInfoPart', function(data)
    {
		var resultcode = data.get(0).get("X_RESULTCODE");
		if(resultcode == '0')
		{
			alert("删除成功，请重新查询！");
			setPopupReturnValue('','',true);
		
		}else{
			alert("删除领导信息失败");
		}
    	
		$.endPageLoading();
	},
	function(error_code,error_info)
	{
		$.endPageLoading();
		alert(error_info);
    });
}
