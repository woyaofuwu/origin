//播记录查询
function queryRecord()
{
	//查询条件校验
	if(!$.validate.verifyAll("QueryPart"))
	{
		return false;
	}
	$.beginPageLoading("正在查询数据...");
	//播记录查询
	$.ajax.submit('QueryPart,recordNav', 'queryBaseRecord', null, 'HiddenPart,SerialNumPart,QueryListPart', function(data)
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
 * 查询类型切换
 */
function qryType(){
	$("#cond_START_TIME").val(""); //置空
	$("#cond_END_TIME").val("");
	var table;
	table = $.table.get("tablecontent");
	table.cleanRows();//清空表格的内容
	var selector =$("#cond_BLACK_TYPE");
	if(selector.val()!=""){
		$("#cond_START_TIME").attr("nullable","no");
		$("#cond_END_TIME").attr("nullable","no");
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



