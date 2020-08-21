function checkSelection(selection) 
{
	var selectValue = $("#baseCommInfo_OPER_TYPE").val();
	
	if(selectValue == 1)
	{
		$("#numValue").html("<span class=\"e_required\">手机号码：</span>");
		$("#baseCommInfo_VALUE").val("");
		$("#baseCommInfo_VALUE").focus();
	} 
	else 
	{
		$("#numValue").html("<span class=\"e_required\">终端IMEI号：</span>");
		$("#baseCommInfo_VALUE").val("");
		$("#baseCommInfo_VALUE").focus();
	}
}

//查询
function serialNumberKeyDown()
{
	var operType = $("#baseCommInfo_OPER_TYPE").val();
	var numValue = $("#baseCommInfo_VALUE").val();
	
	if(numValue=="")
	{
		alert("手机号码或IMEI号不能为空。");
		return false;
	}
	
	if(operType == 1 && !$.verifylib.checkMbphone(numValue))
	{
		alert("请输入格式正确的手机号码！");
		return false;
	}
	
	var param = "&VALUE="+numValue+"&OPER_TYPE="+operType ;
	
	$.beginPageLoading("正在查询数据...");
	
	$.ajax.submit('DMCancelPart', 'loadChildInfo', param, 'QueryListPart', function()
	{	
		var selectValue = $("#baseCommInfo_OPER_TYPE").val(operType);
		
		if(operType == 1)
		{
			$("#numValue").html("<span class=\"e_required\">手机号码：</span>");
		} 
		else 
		{
			$("#numValue").html("<span class=\"e_required\">终端IMEI号：</span>");
		}
		$.endPageLoading();
	},
	function(error_code,error_info)
	{
		$.endPageLoading();
		alert(error_info);
    });
}

function checkB4Submit()
{
	var operType = $("#baseCommInfo_OPER_TYPE").val();
	var numValue = $("#baseCommInfo_VALUE").val();
	
	var changeOperType = $("#CHANGE_OPER_TYPE").val();
	var changeNumValue = $("#CHANGE_VALUE").val();
	
	//查询条件校验
	
	if(numValue=="")
	{
		alert("手机号码或IMEI号不能为空。");
		return false;
	}
	if(changeOperType == "" && changeNumValue == "")
	{
		alert("请点击查询后在提交！");
		return false;
	}
		
	if(operType != changeOperType && numValue != changeNumValue)
	{
		alert("查询条件已变更，请点击查询后再提交！");
		return false;
	}
	
	if(operType == 1 && !$.verifylib.checkMbphone(numValue))
	{
		alert("请输入格式正确的手机号码！");
		return false;
	}

	return true ;
}