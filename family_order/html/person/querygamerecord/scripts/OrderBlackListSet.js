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
	var data=$("#cond_SUBMIT_MODE2").val();
	if(data=='1'||data=='3'){
		var date=$("#cond_SUBMIT_MODE3").val();
		if(date == ""){
			alert("黑名单有效期不能为空");
			return false;
		}

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
function checkDate(month){
	//查询条件校验
	if(!$.validate.verifyAll("QueryRecordPart"))
	{
		return false;
	}
	var startDate=$("#cond_START_DATE").val();
	var endDate=$("#cond_END_DATE").val();
	var start = new Date(startDate.replace("-", "/").replace("-", "/"));
	var end = new Date(endDate.replace("-", "/").replace("-", "/"));
	start.setMonth(start.getMonth()+month);
	if(start<end){
		alert("时间跨度不能超过"+month+"个月！");
		return false;
	}else{
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
}
function checkAddChg(){
	
	var data=$("#cond_SUBMIT_MODE2").val();
	
	if(data=='1'){
		$('#blackDate').css("display",'');

	}
	if(data=='3'){
		$('#blackDate').css("display",'');

	}
	if(data=='2'){
		$('#blackDate').css("display","none");
		$('#cond_SUBMIT_MODE3').val("");
	}

}