
function refreshPartAtferAuth(){
	var sn = $("#SERIAL_NUMBER").val();
	var acceptStratDate = $("#ACCEPT_START_DATE").val();
	var acceptEndDate = $("#ACCEPT_END_DATE").val();
	if(acceptEndDate < acceptStratDate){
		MessageBox.alert("提示", "开始时间不能大于结束时间，请确认！");
		return false;
	}
	var param = "&SERIAL_NUMBER="+sn +"&ACCEPT_START_DATE="+acceptStratDate +"&ACCEPT_END_DATE="+acceptEndDate
	
	$.beginPageLoading("正在查询数据...");
	$.ajax.submit('', 'loadInfo', param, 'ResultPart,otherPart', function(rtnData){
		$.endPageLoading();
		if(rtnData == null || rtnData.length == 0){
			MessageBox.alert("提示", "未查询到预受理信息，请确认！");
		}else{
			var needTips = rtnData.get("NEED_TIPS");
			if(needTips == "true"){
				MessageBox.alert("提示", "该时间段内有超时未处理的预受理工单，请及时处理！");
			}
		}
	}, function(error_code, error_info,detail) {
		$.endPageLoading();
		MessageBox.error("错误提示", error_info, null, null, null, detail);
	});  
}



/**
 * 撤单按钮
 * 只允许状态为0的工单才能撤单
 * @param obj
 */
function cancelPreTrade(obj){
	
	var cancelReason = $("#CANCEL_REASON").val();
	var cancelRemark = $("#CANCEL_REMARK").val();
	
	var thisSerial = $(obj).attr("thisSerial");
	var thisTag = $(obj).attr("thisTag");
	var thisId = $(obj).attr("thisId");
	
	if(thisTag != "0"){
		MessageBox.alert("提示", "状态为[已同步]的才准许撤单！");
		return false;
	}
	if(cancelReason == ""){
		MessageBox.alert("提示", "撤单原因不能为空！");
		return false;
	}
	var param = "&SERIAL_NUMBER="+thisSerial + "&CANCEL_REASON=" + cancelReason + "&CANCEL_REMARK=" + cancelRemark +"&WIDENET_SYNC_ID=" +thisId;
	
	$.beginPageLoading("正在执行撤单...");
	$.ajax.submit('', 'cancelPreTrade', param, 'ResultPart,otherPart', function(rtnData){
		$.endPageLoading();
		if(rtnData == null || rtnData.length == 0){
			MessageBox.alert("提示", "撤单失败！");
		}
		MessageBox.alert("提示", "撤单成功！");
	}, function(error_code, error_info,detail) {
		$.endPageLoading();
		MessageBox.error("错误提示", error_info, null, null, null, detail);
	});  
	
}

/**
 * 外呼记录弹窗
 */
function showCallArea(obj){
	
	var thisCallOutNum = $(obj).attr("thisCallNum");	//外呼次数
	var thisCallResult = $(obj).attr("thisCallResult");	//外呼结果
	var serialNumber = $(obj).attr("thisSerial");
	var thisId = $(obj).attr("thisId");
	var thisTag = $(obj).attr("thisTag");
	
	if(thisTag != "0"){
		MessageBox.alert("提示", "状态为[已同步]的才允许添加外呼记录！");
		return false;
	}
	
	$("#CALL_OUT_TIME").val('');
	
	if(thisCallOutNum >= 3 || thisCallResult == 1){	//外呼次数大于3次，或者外呼结果为成功，则提示完结工单
		MessageBox.alert("提示", "外呼次数超过[3]次或者外呼结果为[成功]的工单可直接完成工单！", function(btn){
			if(btn == "ok"){
				$("#popup").css("display", "");
				$("#SELECT_SERIAL").val(serialNumber);
				$("#SELECT_ID").val(thisId);
				$("#CALL_OUT_NUM").val(thisCallOutNum);	//选择数据的外呼次数
				$("#CALL_OUT_NUM").html(thisCallOutNum);	//选择数据的外呼次数
			}
		});
	}else{
		$("#popup").css("display", "");
		$("#SELECT_SERIAL").val(serialNumber);
		$("#SELECT_ID").val(thisId);
		$("#CALL_OUT_NUM").val(thisCallOutNum);	//选择数据的外呼次数
		$("#CALL_OUT_NUM").html(thisCallOutNum);	//选择数据的外呼次数
	}
	
}


/**
 * 外呼结果选择事件
 */
function changeCallOutTag(){
	
	var callOutTag = $("#CALL_OUT_TAG").val();
	
	if(callOutTag == 2 || callOutTag == 3){
		$("#CALL_OUT_REASON_LI").css("display","");
	}
	if(callOutTag == 1){
		$("#CALL_OUT_REASON_LI").css("display","none");
		$("#CALL_OUT_REASON").val("0");
	}
}

/**
 * 外呼记录保存
 * @returns {Boolean}
 */
function checkOkCallOut(){
	
	var callOutTag = $("#CALL_OUT_TAG").val();
	var callOutTime = $("#CALL_OUT_TIME").val();
	
	if(callOutTime == null || callOutTime == '' || callOutTime == undefined){
		MessageBox.alert("提示", "请选择外呼时间！");
		return false;
	}
	if(callOutTag == 0){
		MessageBox.alert("提示", "请选择外呼结果！");
		return false;
	}
	if(callOutTag == 2 || callOutTag == 3){
		var callOutReason = $("#CALL_OUT_REASON").val();
		if(callOutReason == 0){
			MessageBox.alert("提示", "请选择外呼失败原因！");
			return false;
		}
	}
	
	updateCallOutNum();
}

function updateCallOutNum()
{
	var callOutNum = $("#CALL_OUT_NUM").val();
	var tabCallOutNum = Number(callOutNum) + 1;
	var tableEdit = new Array();
	tableEdit["TAB_CALL_OUT_NUM"] = tabCallOutNum;
    $.table.get("resultTab").updateRow(tableEdit);
    
    var thisSerialNumber = $("#SELECT_SERIAL").val();
    var callOutTag = $("#CALL_OUT_TAG").val();	//外呼结果标记1：成功，2：失败，3：待定
    var thisId = $("#SELECT_ID").val();
    
    var param = "&SERIAL_NUMBER="+thisSerialNumber + "&RSRV_NUM1=" + tabCallOutNum + "&WIDENET_SYNC_ID=" +thisId +"&RSRV_NUM2=" +callOutTag;
    if(callOutTag == 2 || callOutTag == 3){	//外呼失败或待定
    	var reasonStr1 = $("#CALL_OUT_REASON").val();
    	var reasonStr2 = $("#otherReason").val();
    	param += "&RSRV_STR1="+reasonStr1 ;
    	param += "&RSRV_STR2="+reasonStr2 ;
    	
    }
    
    $.beginPageLoading("正在新增外呼记录...");
    $.ajax.submit('', 'updatePreTrade', param, 'ResultPart,otherPart', function(rtnData){
		$.endPageLoading();
		if(rtnData == null || rtnData.length == 0){
			MessageBox.alert("提示", "修改外呼记录失败！");
		}
		MessageBox.alert("提示", "修改外呼记录成功！");
		$("#popup").css("display", "none");
		
		$("#CALL_OUT_TAG").val("0");
		$("#CALL_OUT_REASON").val("0");
		$("#otherReason").val("");
	}, function(error_code, error_info,detail) {
		$.endPageLoading();
		MessageBox.error("错误提示", error_info, null, null, null, detail);
	});  
}

