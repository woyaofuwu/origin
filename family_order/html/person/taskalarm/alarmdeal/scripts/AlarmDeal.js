/*
 * 弹出风险处理修改信息对话框
 */ 
function popupEditAlarmDialog(obj)
{
	debugger;
	var alarm_id = $(obj).attr('alarm_id');
	var handleState = $(obj).attr('handle_state');
	if (handleState == "2"){
		alert('所选记录为[已处理]状态,不能再处理！');
		return false;
	}
	var alarmcreating_time = $(obj).attr('alarmcreating_time');
	var taskwarning_message = encodeURI(encodeURI($(obj).attr('taskwarning_message')));
	popupPage('taskalarm.alarmdeal.ManageAlarm','initPage','&refresh=true&cond_ALARM_ID='+alarm_id+'&cond_ALARMCREATING_TIME='+alarmcreating_time+'&cond_TASKWARNING_MESSAGE='+taskwarning_message,'业务风险信息处理', '700', '500');
}

function queryAlarmByCond ()
{

	
	$.ajax.submit('QueryCondPart', 'queryAlarmByCond', null, 'RefreshPart', function(data){
		if(data.get('ALERT_INFO') != '')
		{
			alert(data.get('ALERT_INFO'));
		}
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
	
}




//关闭风险数据
function closeAlarm(){
	// 得到所有复选框
	var alarmInfo = $.table.get("ResultTable").getCheckedRowDatas();// 获取选择中的数据		
	// 保存要启用的风险告警
	if (alarmInfo == null || alarmInfo.length == 0) {
		alert('请选择待要关闭的数据!');
		return false;
	}
	var alarmIDs = "";
	//获得选中的checkBox对象组
	for(var i=0 ; i<alarmInfo.length ; i++){
		var handle_state = alarmInfo.get([i],"HANDLE_STATE");
		var closed_state = alarmInfo.get([i],"RSRV_STR2");
		if("未处理"!=handle_state && closed_state != "已关闭" ){
			alarmIDs += (alarmIDs == "" ? "" : ",")+  alarmInfo.get([i],"ALARM_ID");
		}
	}
	if (alarmIDs ==""){
		alert('所选记录均为[未处理]状态或记录[已关闭],不能关闭！');
		return false;
	}

	var param = "&multi_ALARM_ID=" + alarmIDs;
	$.ajax.submit('QueryCondPart', 'closeAlarm', param, "RefreshPart", function(data){
		if (data.get('UPDATE_SUCCESS_FLAG') == 1) {
			alert ("规则关闭成功");
		}else{
			alert ("规则关闭失败");
		}
		$.endPageLoading();
	},
	
	function(error_code,error_info,detail){
		$.endPageLoading();
		MessageBox.error("错误提示", error_info, null, null, null, detail);
    });
}

/*
 * 在删除监控业务发送请求,将剩下的业务检索出来.
 */
function queryLeftAlarm ()
{
	alert ('业务风险数据处理成功');
	$.ajax.submit('QueryCondPart', 'queryAlarmByCond', null, 'RefreshPart', function(data){
		if(data.get('ALERT_INFO') != '')
		{
			alert(data.get('ALERT_INFO'));
		}
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}

//风险统计图表 中测
function alarmChart(){

	//redirectToNav('person.taskalarm.TaskChart', '', '', 'contentframe');
    openNav('风险统计图表','taskalarm.alarmdeal.TaskChart', '', '');
}

/*
 * 逐条处理业务风险数据
 */
function dealAlarmByOne ()
{
	
	// 业务风险信息编号
	var alarmID = $("#cond_ALARM_ID").val();
	$.ajax.submit('RefreshPart', 'dealAlarm', '&cond_ALARM_ID=' + alarmID, null, function(data){
 		$.endPageLoading();
 		if(data.get('ALERT_INFO') != null && data.get('ALERT_INFO') != '') {
 			$.showWarnMessage(data.get('ALERT_INFO'));
 		}else{
 			debugger;
 			if (data.get('DEAL_SUCCESS_FLAG') == 1) {
 				alert ("风险告警处理成功");
 			}
 			parent.$("#QUERY_BTN").click();
 			$.closePopupPage(true);
 		    return true;
 		}
 	},
 	
 	function(error_code,error_info,detail){
 		$.endPageLoading();
 		MessageBox.error("错误提示", error_info, null, null, null, detail);
     });
}


/*
 * 处理业务风险告警.
 */
function dealAlarm()
{
 // 得到所有复选框
	var alarmInfo = $.table.get("ResultTable").getCheckedRowDatas();// 获取选择中的数据			
	// 保存要启用的风险告警
	if (alarmInfo == null || alarmInfo.length == 0) {
		alert('请选择待要处理的数据!');
		return false;
	}
	var alarmIDs = "";
	//获得选中的checkBox对象组
	for(var i=0 ; i<alarmInfo.length ; i++){
		var handle_state = alarmInfo.get([i],"HANDLE_STATE");
		if("已处理"!= handle_state){
			alarmIDs += (alarmIDs == "" ? "" : ",")+  alarmInfo.get([i],"ALARM_ID");
		}
	}
	if (alarmIDs ==""){
		alert('所选记录均为[已处理]状态,不能再处理！');
		return false;
	}
	var param = "&multi_ALARM_ID=" + alarmInfo;
	$.ajax.submit('QueryCondPart', 'dealAlarmBatch', param, "RefreshPart", function(data){
		if(data.get('ALERT_INFO') != null && data.get('ALERT_INFO') != '') 
		{
			alert(data.get('ALERT_INFO'));
		}
		if (data.get('DEAL_SUCCESS_FLAG') == 1) {
			alert ("风险告警处理成功");
		}
		debugger;
		$("#QUERY_BTN").click();
		$.closePopupPage(true);
	    return true;
	},
	
	function(error_code,error_info,detail){
		$.endPageLoading();
		MessageBox.error("错误提示", error_info, null, null, null, detail);
    });
	
}