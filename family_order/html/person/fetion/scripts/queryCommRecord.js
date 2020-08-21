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
		qryAfter();
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
//	var start=new Date(startDate);
//	var end=new Date(endDate);
	start.setMonth(start.getMonth()+month);
	if(start<end){
		alert("时间跨度不能超过"+month+"个月！");
		return false;
	}else{
		$.beginPageLoading("正在查询数据...");
		//播记录查询
		$.ajax.submit('QueryRecordPart,recordNav', 'queryBaseRecord', null, 'HiddenPart,SerialNumPart,QueryListPart', function(data)
		{
			var flag = data.get("IS_SUCCESS");
			if(flag == '1'){
			   MessageBox.alert("提示","没有符合查询条件的数据!");
			}
			$.endPageLoading();
		},
		function(error_code,error_info)
		{
			$.endPageLoading();
			alert(error_info);
	    });
	}
}

function qryAfter(){
	
	var selector =$("#cond_USER_MSGLOG_TYPE");
	var upBlock  =$("#up");
	var downBlock  =$("#down");
	if(selector.val()==1){
		upBlock.css('display', 'none');
		downBlock.css('display','');
	}else{
		upBlock.css('display', '');
		downBlock.css('display','none');
	}
	
}


/**
 * 上下行日志查询切换
 */

function qryTypeChange(){
	qryAfter();
	var selector =$("#cond_USER_MSGLOG_TYPE");
	var table;
	if(selector.val()==1){//1下行
		table = $.table.get("downtable");
	}else{
		table = $.table.get("uptable");
	}
	table.cleanRows();//清空表格的内容
}

/**
 * 查询类型切换
 */
function qryType(){
	$("#cond_START_DATE").val(""); //置空
	$("#cond_END_DATE").val("");
	var table;
	table = $.table.get("tablecontent");
	table.cleanRows();//清空表格的内容
	var selector =$("#cond_BLACK_TYPE");
	if(selector.val()!=""){
		$("#cond_START_DATE").attr("nullable","no");
		$("#cond_END_DATE").attr("nullable","no");
	}
}




/**
 * 服务动作代码
 */

function qryActionMark(){
	var selector = $("#cond_SUBMIT_MODE2").val();
	if("2" == selector){
		 $("#blacktime").css('display','none');
		$("#cond_SUBMIT_MODE3").val("");
		$("#cond_SUBMIT_MODE3").attr("nullable","yes");
	}else{
		 $("#blacktime").css('display','');
		$("#cond_SUBMIT_MODE3").attr("nullable","no");
	}
}

