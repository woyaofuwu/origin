function queryInfos()
{
	//查询条件校验
	if(!$.validate.verifyAll("QueryCondPart"))
	{
		return false;
	}
	$.beginPageLoading("数据查询中...");
	$.ajax.submit('QueryCondPart,staticNav', 'getAssigneeInfo', null, 'RefreshPart', function(data)
    {
		$.endPageLoading();
	},
	function(error_code,error_info)
	{
		$.endPageLoading();
		alert(error_info);
    });

}


function queryInfos1()
{
	//查询条件校验
	if(!$.validate.verifyAll("QueryCondPart"))
	{
		return false;
	}
	$.beginPageLoading("数据查询中...");
	$.ajax.submit('QueryCondPart,staticNav', 'getTransferorInfo', null, 'RefreshPart', function(data)
    {
		$.endPageLoading();
	},
	function(error_code,error_info)
	{
		$.endPageLoading();
		alert(error_info);
    });

}



function queryInfos2()
{
	//查询条件校验
	if(!$.validate.verifyAll("QueryCondPart"))
	{
		return false;
	}
	$.beginPageLoading("数据查询中...");
	$.ajax.submit('QueryCondPart,staticNav', 'getTransferorInfoHis', null, 'RefreshPart', function(data)
    {
		$.endPageLoading();
	},
	function(error_code,error_info)
	{
		$.endPageLoading();
		alert(error_info);
    });

}


function queryInfos3()
{
	//查询条件校验
	if(!$.validate.verifyAll("QueryCondPart"))
	{
		return false;
	}
	$.beginPageLoading("数据查询中...");
	$.ajax.submit('QueryCondPart,staticNav', 'getTransferPoint', null, 'RefreshPart', function(data)
    {
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
	var sn=$("#L_MOBILE").val();
	$.popupPage('scoremgr.AddScoreAssignee', 'initAddPage', '&refresh=true&L_MOBILE='+sn, '新增受让人信息', '800', '400');
}

function openEditWindow(rowIndex)
{
	var selTable = $.table.get("ResultTable");
	var rowValue = selTable.getRowData(null,rowIndex);
	var num=0;
	$("input[name=infos]:checked").each(function()
		    {
		    	var checkedVal = $(this).val();
		    	if(checkedVal != "")
		    	{
		    		num++;
		    	}
			});
	if(num==0){
		alert("请选择要修改行");
		return false;
	}else if(num>1){
		alert("一次只能选择一个修改,请重新选择");
		return false;
	}
	var B_MOBILE = rowValue.get("B_MOBILE");
	var ASSIGNEE_NAME = rowValue.get("ASSIGNEE_NAME");
	var VALIDNUM_TYPE =rowValue.get("VALIDNUM_TYPE");
	var ASSIGNEE_ID = rowValue.get("ASSIGNEE_ID");
	var L_MOBILE =$("#L_MOBILE").val();
	
	var param = '&ASSIGNEE_NAME='+encodeURI(encodeURI(ASSIGNEE_NAME))+'&L_MOBILE='+L_MOBILE+'&B_MOBILE='+B_MOBILE+'&VALIDNUM_TYPE='+VALIDNUM_TYPE+'&ASSIGNEE_ID='+ASSIGNEE_ID;
	
	$.popupPage('scoremgr.EditScoreAssignee', 'initEditPage', '&refresh=true'+param, '编辑受让人信息', '800', '600');
}

function openRepalceWindow(rowIndex)
{
	var selTable = $.table.get("ResultTable");
	var rowValue = selTable.getRowData(null,rowIndex);
	var num=0;
	$("input[name=infos]:checked").each(function()
		    {
		    	var checkedVal = $(this).val();
		    	if(checkedVal != "")
		    	{
		    		num++;
		    	}
			});
	if(num==0){
		alert("请选择要修改行");
		return false;
	}else if(num>1){
		alert("一次只能选择一个修改,请重新选择");
		return false;
	}
	var L_MOBILE =$("#L_MOBILE").val();
	var B_MOBILE = rowValue.get("B_MOBILE");
	
	var param ='&L_MOBILE='+L_MOBILE+'&B_MOBILE='+B_MOBILE;
	
	$.popupPage('scoremgr.RepalceScoreAssignee', 'initReplacePage', '&refresh=true'+param, '替换受让人信息', '800', '600');
}


function getSelectedRow(obj)
{
	if(confirm("确定要停用该行受让人吗？")){
    var L_MOBILE =$("#L_MOBILE").val();
    var sn =$(obj).parent().parent().find("td[name=mobile]").html();
   
	$.beginPageLoading("停用中...");
    $.ajax.submit(null, 'StopAssignee', '&L_MOBILE=' + L_MOBILE+'&B_MOBILE=' + sn, null, function(data)
    {
    	if(data.get("X_RESULTCODE")=="00")
		{
			alert("停用受让人成功");
		}else{
			alert(data.get("X_RESULTINFO"));
		}
		$.endPageLoading();
		
		refreshTask();
	
		
	},
	function(error_code,error_info)
	{
		$.endPageLoading();
		alert(error_info);
    });
}
}

function getSelectedRow1(obj)
{
	if(confirm("确定要启用该行受让人吗？")){
    var L_MOBILE =$("#L_MOBILE").val();
    var sn =$(obj).parent().parent().find("td[name=mobile]").html();

	$.beginPageLoading("启用中...");
    $.ajax.submit(null, 'StartAssignee', '&L_MOBILE=' + L_MOBILE+'&B_MOBILE=' + sn, null, function(data)
    {
    	if(data.get("X_RESULTCODE")=="00")
		{
			alert("启用受让人成功。");
		}else{
			alert(data.get("X_RESULTINFO"));
		}
		$.endPageLoading();
		
		refreshTask();
		
		
	},
	function(error_code,error_info)
	{
		$.endPageLoading();
		alert(error_info);
    });
}
}


function refreshTask()
{
	$.beginPageLoading("数据刷新中...");
	
	$.ajax.submit('QueryCondPart,staticNav', 'getAssigneeInfo', null, 'RefreshPart', function(data)
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
	if(!$.validate.verifyAll("addAssigneePart"))
	{
		return false;
	}
	
	$.beginPageLoading("数据新增中...");
	
	$.ajax.submit('addAssigneePart', 'AddAssignee', null, 'addAssigneePart', function(data)
    { 
		if(data.get("X_RESULTCODE")=="00")
		{
			alert("新增受让人成功。如需查看请重新查询！");
			setPopupReturnValue('','',true);
		}else{
			alert(data.get("X_RESULTINFO"));
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
	if(!$.validate.verifyAll("editAssigneePart"))
	{
		return false;
	}
	
	$.beginPageLoading("数据保存中...");
	
	$.ajax.submit('editAssigneePart', 'EditAssignee', null, 'editAssigneePart', function(data)
    {
    	
    	if(data.get("X_RESULTCODE")=="00")
		{
			alert("该受让人更新成功.如需查看请重新查询！");
			setPopupReturnValue('','',true);
		
		}else{
			alert(data.get("X_RESULTINFO"));
		}
    	
		$.endPageLoading();
	},
	function(error_code,error_info)
	{
		$.endPageLoading();
		alert(error_info);
    });
}

function repalceTask()
{
	//查询条件校验
	if(!$.validate.verifyAll("ReplaceAssignee"))
	{
		return false;
	}
	
	$.beginPageLoading("数据替换中...");
	$.ajax.submit('ReplaceAssignee', 'ReplaceAssignee', null, 'ReplaceAssignee', function(data)
    {
		if(data.get("X_RESULTCODE")=="00")
		{
    		alert("该受让人更新成功.如需查看请重新查询！");
			setPopupReturnValue('','',true);
			
		}else{
			alert(data.get("X_RESULTINFO"));
		}
		$.endPageLoading();
	},
	function(error_code,error_info)
	{
		$.endPageLoading();
		alert(error_info);
    });
}
	
	
	//办理校验
	function checkAndSubmit()
	{
		//查询条件校验
		if(!$.validate.verifyAll("Transferpointpart"))
		{
			return false;
		}
		
		$.beginPageLoading("业务受理中...");
		
		$.ajax.submit('Transferpointpart', 'submitProcess', null, 'Transferpointpart', function(data)
		{
			if(data.get("X_RESULTCODE")=="00")
			{
				alert("积分赠送成功");
			}else{
				alert(data.get("X_RESULTINFO"));
			}
			$.endPageLoading();
		},
		function(error_code,error_info)
		{
			$.endPageLoading();
			alert(error_info);
	    });
	}