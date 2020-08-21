//播记录查询
function queryRecord()
{
	//查询条件校验
	if(!$.validate.verifyAll("QueryRecordPart"))
	{
		return false;
	}
	$.beginPageLoading("正在查询数据...");
	//播记录查询
	$.ajax.submit('QueryRecordPart,recordNav', 'queryBaseRecord', null, 'HiddenPart,SerialNumPart,QueryListPart', function(data)
	{
		$.endPageLoading();
	},
	function(error_code,error_info)
	{
		$.endPageLoading();
		alert(error_info);
    });
}

function queryAbnormal()
{
	//查询条件校验
	if(!$.validate.verifyAll("QueryRecordPart"))
	{
		return false;
	}
	$.beginPageLoading("正在查询数据...");
	//播记录查询
	$.ajax.submit('QueryRecordPart,recordNav', 'queryAbnormal', null, 'HiddenPart,SerialNumPart,QueryListPart', function(data)
	{
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
	if(!$.validate.verifyAll("blackListProcessPart"))
	{
		return false;
	}
	
	$.beginPageLoading("业务受理中...");
	
	$.ajax.submit('blackListProcessPart', 'submitProcess', null, 'blackListProcessPart', function(data)
	{
		MessageBox.alert("提示","操作成功！",function(btn)
	{
		if(btn=="ok")
		{
			window.location.href = window.location.href;
		}
	});
		
		$.endPageLoading();
	},
	function(error_code,error_info)
	{
		$.endPageLoading();
		alert(error_info);
    });
}

//屏蔽业务办理校验
function checkAndSubmit4Shield()
{
	//查询条件校验
	if(!$.validate.verifyAll("shieldProcessPart"))
	{
		return false;
	}
	
	$.beginPageLoading("业务受理中...");
	
	$.ajax.submit('shieldProcessPart', 'submitProcess', null, 'shieldProcessPart', function(data)
	{
		MessageBox.alert("提示","操作成功！",function(btn)
	{
		if(btn=="ok")
		{
			window.location.href = window.location.href;
		}
	});
		
		$.endPageLoading();
	},
	function(error_code,error_info)
	{
		$.endPageLoading();
		alert(error_info);
    });
}

//受理类型动态刷新
function changeOperaType()
{
		//查询条件校验
	var serialNum = $('#cond_PHONE_NUM').val();
	
	if(serialNum == "")
	{
		alert("手机号码不能为空！");
		window.location.href = window.location.href;
		return false;
	}
	$.beginPageLoading("正在加载数据...");
	$.ajax.submit('QueryRecordPart', 'operaType', null, 'QueryRecordPart',function()
	{
		$.endPageLoading();
	},
	function(error_code,error_info)
	{
		$.endPageLoading();
		alert(error_info);
    });
}

//轨迹查询
function queryAction() 
{
	//查询条件校验
	if(!$.validate.verifyAll("QueryRecordPart"))
	{
		return false;
	}
	
	$('#resultTable_Body').html('');
	
	$.beginPageLoading("正在查询数据...");
	//轨迹查询
	$.ajax.submit('QueryRecordPart,recordNav,TradeIdPart', 'queryBaseBehavior', null, 'QueryListPart,recordNav,HiddenPart', function(data)
	{
		$.endPageLoading();
	},
	function(error_code,error_info)
	{
		$.endPageLoading();
		alert(error_info);
    });
}

function showMsgTablePart(){
	var type = $("#cond_MSGTYPE").val();
	if(type == "0"){
		$("#QueryListPartUp").css("display", "block");
		$("#QueryListPartDown").css("display", "none");
	} else if(type == "1"){
		$("#QueryListPartUp").css("display", "none");
		$("#QueryListPartDown").css("display", "block");
	}
}

function queryMsgRecord()
{
	//查询条件校验
	if(!$.validate.verifyAll("QueryRecordPart"))
	{
		return false;
	}
	var type = $("#cond_MSGTYPE").val();
	if(type == "0"){
		$.beginPageLoading("正在查询数据...");
		//播记录查询
		$.ajax.submit('QueryRecordPart,recordNavUp', 'queryBaseRecord', null, 'HiddenPart,SerialNumPart,QueryListPartUp', function(data)
				{
			$.endPageLoading();
				},
				function(error_code,error_info)
				{
					$.endPageLoading();
					alert(error_info);
				});		
	}else{
		$.beginPageLoading("正在查询数据...");
		//播记录查询
		$.ajax.submit('QueryRecordPart,recordNavDown', 'queryBaseRecord', null, 'HiddenPart,SerialNumPart,QueryListPartDown', function(data)
				{
			$.endPageLoading();
				},
				function(error_code,error_info)
				{
					$.endPageLoading();
					alert(error_info);
				});		
	}
}

/**
 * 查询类型切换
 */
function qryType(){
	$("#cond_PARA_CODE3").val(""); //置空
	$("#cond_PARA_CODE4").val("");
	var table;
	table = $.table.get("tablecontent");
	table.cleanRows();//清空表格的内容
	var selector =$("#cond_BLACK_TYPE");
	if(selector.val()!=""){
		$("#cond_PARA_CODE3").attr("nullable","no");
		$("#cond_PARA_CODE4").attr("nullable","no");
	}
}


function showBlackTime(){
	var type = $("#cond_SUBMIT_MODE2").val();
	if("1" == type){
		$("#blacktime").css("display", "");
	} else {
		$("#blacktime").css("display", "none");
		$("#cond_SUBMIT_MODE3").val("");
	}
}