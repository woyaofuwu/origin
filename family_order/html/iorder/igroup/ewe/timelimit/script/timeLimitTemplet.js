$(function(){
	$("#OverTimerTable").tap(function(e){

		if(e.target && $.nodeName(e.target, "input") && "checkbox" == $.attr(e.target, "type"))
			return;

		alert(OverTimerTable.getSelectedRowData());
	});
});

//根据timerId查询已有超时模板
function queryAllOverTimer(){
	debugger;
	var timerId = $("#cond_TIMER_ID").val();
	var operation = $("#OPERATION").val();
	if(operation=="update"){
		$.ajax.submit(this, "updateAllOverTimer", "&TIMER_ID="+timerId, "ResultPart", function(data){
			   $.endPageLoading();
			},null);
	}else{
		$.ajax.submit(this, "queryAllOverTimer", "&TIMER_ID="+timerId, "ResultPart", null,null);
	}
}

//切换提醒类型
function timerTypeChange()
{
	debugger;
	var timerType = $("#TIMER_TYPE").val();
	//到达提醒  和 完成提醒
	if(timerType == "1" || timerType == "3")
	{
		$("#LI_OFFSET_MODE").attr("style","display:none;");
		$("#LI_OFFSET_EXP").attr("style","display:none;");
		$("#LI_OFFSET_TYPE").attr("style","display:none;");
		$("#LI_OFFSET_VALUE").attr("style","display:none;");
		$("#LI_OFFSET_EXCLUDE").attr("style","display:none;");
		$("#LI_WARN_CONTENT").attr("style","display:none;");
		$("#LI_WARN_NUM").attr("style","display:none;");
	}
	else if(timerType == "2")
	{
//		$("#LI_OFFSET_LOCATION").attr("style","");
//		$("#OFFSET_LOCATION").attr("nullable", "no");
		$("#LI_OFFSET_MODE").attr("style","");
		$("#OFFSET_MODE").attr("nullable", "no");
		$('#LI_OFFSET_MODE').addClass('required');
		
		$("#LI_OFFSET_EXP").attr("style","");
		$("#OFFSET_EXP").attr("nullable", "no");
		$('#LI_OFFSET_EXP').addClass('required');
		
		$("#LI_OFFSET_TYPE").attr("style","");
		$("#OFFSET_TYPE").attr("nullable", "no");
		$('#LI_OFFSET_TYPE').addClass('required');
		
		$("#LI_OFFSET_VALUE").attr("style","");
		$("#OFFSET_VALUE").attr("nullable", "no");
		$('#LI_OFFSET_VALUE').addClass('required');
		
		$("#LI_OFFSET_EXCLUDE").attr("style","");
		$("#OFFSET_EXCLUDE").attr("nullable", "no");
		$('#LI_OFFSET_EXCLUDE').addClass('required');
		
		$("#LI_WARN_SVC").attr("style","");
		$("#WARN_SVC").attr("nullable", "no");
		$('#LI_WARN_SVC').addClass('required');
		
		$("#LI_WARN_CONTENT").attr("style","");
		$("#WARN_CONTENT").attr("nullable", "no");
		$('#LI_WARN_CONTENT').addClass('required');
		
		$("#LI_WARN_NUM").attr("style","");
		$("#WARN_NUM").attr("nullable", "no");
		$('#LI_WARN_NUM').addClass('required');
	}
}

//初始化偏移排除表达式列表
function initExcludeInfos()
{
	debugger;
	var excludeInfosStr = $("#OFFSET_EXCLUDE").val();
	if(excludeInfosStr != "")
	{
		$("#ExcludeTable tbody").html("");
		
		var excludeInfos = excludeInfosStr.split("|");
		
		for(var i = 0 ; i < excludeInfos.length ; i ++)
		{
			var excludes = excludeInfos[i].split(":");
			if(excludes.length == 3)
			{
				var excludeType = excludes[0];
				var excludeDay = excludes[1];
				var excludeTimerStr = excludes[2].split("-");
				if(excludeTimerStr.length == 2)
				{
					var excludeTimerStart = excludeTimerStr[0];
					var excludeTimerEnd = excludeTimerStr[1]; 
					var editData = new $.DataMap();
					editData["EXCLUDE_TYPE"] = excludeType;
					editData["EXCLUDE_DAY"] = excludeDay;
					editData["EXCLUDE_TIMER_START"] = excludeTimerStart;
					editData["EXCLUDE_TIMER_END"] = excludeTimerEnd;
					ExcludeTable.addRow(editData);
				}
				
			}
			
			$("#ExcludeTable").tap(function(e){
				excludeTableRowClick(ExcludeTable.getSelectedRowData());
			});
		}
	}
	else
	{
		$("#ExcludeTable tbody").html("");
	}
	
	showPopup('taskPopup','popupTaskExcludeItem',true);
}

//新增偏移排除表达式列表
function addExcludeTable()
{
	debugger;
	var excludeType = $("#EXCLUDE_TYPE").val();
	var excludeDay = $("#EXCLUDE_DAY").val();
	var excludeTimerStart = $("#EXCLUDE_TIMER_START").val();
	var excludeTimerEnd = $("#EXCLUDE_TIMER_END").val();
	
	if(excludeType == "0") //按日期
	{
		var tempDateReg = /^(([1-2]([0-9]){0,1}|[1-9]|3([0-1]))\,){0,1}([1-2]([0-9])|[1-9]|3([0-1]))$/;
		if(!tempDateReg.test(excludeDay))
		{
			MessageBox.alert("提示","按日期填写排除日期出错！");
			return false;
		}
		
	}
	if(excludeType == "1") //按星期
	{
		var tempWeekReg = /^([1-7]\,){0,1}|[1-7]$/;
		if(!tempWeekReg.test(excludeDay))
		{
			MessageBox.alert("提示","按星期填写排除日期出错！");
			return false;
		}
	}
	
	var tempTimerReg = /^([0-1][0-9][0-5][0-9])|(2[0-4][0-5][0-9])|([0-1][0-9]60)|(2[0-4]60)$/;
	if(!tempTimerReg.test(excludeTimerStart))
	{
		MessageBox.alert("提示","时间段-开始时间格式不对！");
		return false;
	}
	
	if(!tempTimerReg.test(excludeTimerEnd))
	{
		MessageBox.alert("提示","时间段-结束时间格式不对！");
		return false;
	}
	var editData = new $.DataMap();
	editData["EXCLUDE_TYPE"] = $("#EXCLUDE_TYPE").val();
	editData["EXCLUDE_DAY"] = $("#EXCLUDE_DAY").val();
	editData["EXCLUDE_TIMER_START"] = $("#EXCLUDE_TIMER_START").val();
	editData["EXCLUDE_TIMER_END"] = $("#EXCLUDE_TIMER_END").val();
	ExcludeTable.addRow(editData);
	
	$("#ExcludeTable").tap(function(e){
		excludeTableRowClick(ExcludeTable.getSelectedRowData());
	});
}

//修改偏移排除表达式列表
function modExcludeTable()
{
	debugger;
	if(ExcludeTable.selected != undefined)
	{
		var excludeType = $("#EXCLUDE_TYPE").val();
		var excludeDay = $("#EXCLUDE_DAY").val();
		var excludeTimerStart = $("#EXCLUDE_TIMER_START").val();
		var excludeTimerEnd = $("#EXCLUDE_TIMER_END").val();
		
		if(excludeType == "0") //按日期
		{
			var tempDateReg = /^(([1-2]([0-9]){0,1}|[1-9]|3([0-1]))\,){0,1}([1-2]([0-9])|[1-9]|3([0-1]))$/;
			if(!tempDateReg.test(excludeDay))
			{
				MessageBox.alert("提示","按日期填写排除日期出错！");
				return false;
			}
			
		}
		if(excludeType == "1") //按星期
		{
			var tempWeekReg = /^([1-7]\,){0,1}|[1-7]$/;
			if(!tempWeekReg.test(excludeDay))
			{
				MessageBox.alert("提示","按星期填写排除日期出错！");
				return false;
			}
		}
		
		var tempTimerReg = /^([0-1][0-9][0-5][0-9])|(2[0-4][0-5][0-9])|([0-1][0-9]60)|(2[0-4]60)$/;
		if(!tempTimerReg.test(excludeTimerStart))
		{
			MessageBox.alert("提示","时间段-开始时间格式不对！");
			return false;
		}
		
		if(!tempTimerReg.test(excludeTimerEnd))
		{
			MessageBox.alert("提示","时间段-结束时间格式不对！");
			return false;
		}
		var editData = new $.DataMap();
		editData["EXCLUDE_TYPE"] = $("#EXCLUDE_TYPE").val();
		editData["EXCLUDE_DAY"] = $("#EXCLUDE_DAY").val();
		editData["EXCLUDE_TIMER_START"] = $("#EXCLUDE_TIMER_START").val();
		editData["EXCLUDE_TIMER_END"] = $("#EXCLUDE_TIMER_END").val();
		ExcludeTable.updateRow(editData,ExcludeTable.selected);
	}
	else
	{
		MessageBox.alert("提示","请选择需要修改的数据！");
	}
}

//删除偏移排除表达式列表
function delExcludeTable()
{
	debugger;
	if(ExcludeTable.selected != undefined)
	{
		ExcludeTable.deleteRow(ExcludeTable.selected);
	}
	else
	{
		MessageBox.alert("提示","请选择需要删除的数据！");
	}
}

//保存偏移排除表达式列表
function saveExcludeInfos(obj)
{
	debugger;
	var excludeStr = "";
	var tableList = ExcludeTable.getData(true);
	
	if(tableList == "" || tableList.length == 0)
	{
		MessageBox.confirm("提示信息", "没有选择规则！是否返回填写。", function(btn){
			if("ok" == btn){
				return;
			}
			else
			{
				hidePopup(obj);
				showPopup("taskPopup","popupTaskTimerItem", true);
			}
		});
	}
	else
	{
		if("" != tableList)
		{
			for(var i=0; i<tableList.length ;i++)
			{
				var table = tableList.get(i);
				var excludeType = table.get("EXCLUDE_TYPE");
				var excludeDay = table.get("EXCLUDE_DAY"); 
				var excludeTimerStart = table.get("EXCLUDE_TIMER_START");
				var excludeTimerEnd = table.get("EXCLUDE_TIMER_END");
				var tmpexcludeStr = excludeType + ":" + excludeDay + ":" + excludeTimerStart + "-" + excludeTimerEnd;
				
				if(excludeStr == "")
				{
					excludeStr = tmpexcludeStr;
				}
				else
				{
					excludeStr = excludeStr + "|" + tmpexcludeStr;
				}
			}
		}
	
		$("#OFFSET_EXCLUDE").val(excludeStr);
		hidePopup(obj);
		showPopup("taskPopup","popupTaskTimerItem", true);
	}
}

//点击偏移排除表达式列表
function excludeTableRowClick(rowData){
	debugger;
	$("#EXCLUDE_TYPE").val(rowData.get("EXCLUDE_TYPE"));
	$("#EXCLUDE_DAY").val(rowData.get("EXCLUDE_DAY"));
	$("#EXCLUDE_TIMER_START").val(rowData.get("EXCLUDE_TIMER_START"));
	$("#EXCLUDE_TIMER_END").val(rowData.get("EXCLUDE_TIMER_END"));
}

//保存超时规则配置
function saveOverTimerInfos(obj)
{
	debugger;
	if(!$.validate.verifyAll("writeForm")){
		return false;
	}
	
	var timerType = $("#TIMER_TYPE").val();
	var timerObject = $("#TIMER_OBJECT").val();
	var offsetLocation = $("#OFFSET_LOCATION").val();
	var offsetMode = $("#OFFSET_MODE").val();
	var offsetExp =  $("#OFFSET_EXP").val();
	var offsetType = $("#OFFSET_TYPE").val();
	var offsetValue = $("#OFFSET_VALUE").val();
	var offsetExclude = $("#OFFSET_EXCLUDE").val();
	var warnSvc = $("#WARN_SVC").val();
	var warnContent = $("#WARN_CONTENT").val();
	var warnNum = $("#WARN_NUM").val();
	var param = "&TIMER_TYPE="+timerType+"&TIMER_OBJECT="+timerObject+"&OFFSET_LOCATION="+offsetLocation+"&OFFSET_MODE="+offsetMode+"&OFFSET_EXP="+offsetExp+"&OFFSET_TYPE="+offsetType+"&OFFSET_VALUE="+offsetValue+"&OFFSET_EXCLUDE="+offsetExclude+"&WARN_SVC="+warnSvc+"&WARN_CONTENT="+warnContent+"&WARN_NUM="+warnNum
	
	var operation = $("#OPERATION").val();
	if (operation == 'update') {
		var timerId = $("#TIMER_ID").val();
		$.ajax.submit(this, "updateAllOverTimer", "&TIMER_ID=" + timerId + param,"writeForm", function(data) {
					$.endPageLoading();
					$("#OPERATION").val("");
					MessageBox.alert("提示", "已成功修改！");
					hidePopup(obj);
				}, null);
	} else if (operation == 'insert') {
		$.ajax.submit(this, "insertAllOverTimer", param, "writeForm", function(data) {
			$.endPageLoading();
			MessageBox.alert("提示", "已成功新增！");
			hidePopup(obj);
		}, null);
	}
}

function overTimerTableRowClick(rowData)
{
	debugger;
	$("#TIMER_TYPE").val(rowData.get("TIMER_TYPE"));
	$("#TIMER_OBJECT").val(rowData.get("TIMER_OBJECT"));
	$("#OFFSET_LOCATION").val(rowData.get("OFFSET_LOCATION"));
	$("#OFFSET_MODE").val(rowData.get("OFFSET_MODE"));
	$("#OFFSET_EXP").val(rowData.get("OFFSET_EXP"));
	$("#OFFSET_TYPE").val(rowData.get("OFFSET_TYPE"));
	$("#OFFSET_VALUE").val(rowData.get("OFFSET_VALUE"));
	$("#OFFSET_EXCLUDE").val(rowData.get("OFFSET_EXCLUDE"));
	$("WARN_SVC").val(rowData.get("WARN_SVC"));
	$("#WARN_CONTENT").val(rowData.get("WARN_CONTENT"));
	$("#WARN_NUM").val(rowData.get("WARN_NUM"));
	$("#TIMER_TYPE").trigger("change");
}

function updateOverTimerInfos(obj){
	debugger;
	var timerId = $(obj).attr("timerId");
	$.ajax.submit(this, "queryAllOverTimer", "&TIMER_ID="+timerId, "writeForm", function(data){
		   $.endPageLoading();
		    $("#TIMER_ID").val(data.get(0).get("TIMER_ID"));
		    $("#TIMER_TYPE").attr("disabled", "false");
			$("#TIMER_TYPE").val(data.get(0).get("TIMER_TYPE"));
			$("#TIMER_OBJECT").val(data.get(0).get("TIMER_OBJECT"));
			$("#OFFSET_LOCATION").val(data.get(0).get("OFFSET_LOCATION"));
			$("#OFFSET_MODE").val(data.get(0).get("OFFSET_MODE"));
			$("#OFFSET_EXP").val(data.get(0).get("OFFSET_EXP"));
			$("#OFFSET_TYPE").val(data.get(0).get("OFFSET_TYPE"));
			$("#OFFSET_VALUE").val(data.get(0).get("OFFSET_VALUE"));
			$("#OFFSET_EXCLUDE").val(data.get(0).get("OFFSET_EXCLUDE"));
			$("WARN_SVC").val(data.get(0).get("WARN_SVC"));
			$("#WARN_CONTENT").val(data.get(0).get("WARN_CONTENT"));
			$("#WARN_NUM").val(data.get(0).get("WARN_NUM"));
			$("#OPERATION").val("update");
		   showPopup('taskPopup','popupTaskTimerItem',true);
		},null);
}

function insertOverTimerInfos(){
	debugger;
	$("#TIMER_TYPE").attr("disabled", "");
	$("#TIMER_ID").val("");
	$("#TIMER_TYPE").val("");
	$("#TIMER_OBJECT").val("");
	$("#OFFSET_LOCATION").val("");
	$("#OFFSET_MODE").val("");
	$("#OFFSET_EXP").val("");
	$("#OFFSET_TYPE").val("");
	$("#OFFSET_VALUE").val("");
	$("#OFFSET_EXCLUDE").val("");
	$("WARN_SVC").val("");
	$("#WARN_CONTENT").val("");
	$("#WARN_NUM").val("");
	$("#OPERATION").val("insert");
	showPopup('taskPopup', 'popupTaskTimerItem', true);
}

function deleteOverTimerInfos(obj){
	debugger;
	var timerId = $(obj).attr("timerId");
	$.ajax.submit(this, "delAllOverTimer", "&TIMER_ID="+timerId, "writeForm", function(data){
		   $.endPageLoading();
		   MessageBox.alert("提示", "已成功删除！");
		},null);
}