//查询授权申请记录
function queryAuthApply(){
	var start_Date = $("#cond_START_DATE").val();
	var end_Date = $("#cond_END_DATE").val();
	var menu_Id = $("#cond_MENU_ID").val();
	var staff_Id = $("#AWS_STAFF_ID").val();
	var state = $("#cond_ASK_STATE").val();
	
	if(start_Date > end_Date){
		alert("开始时间不能大于结束时间！");
		return false;
	}
	
	var params = "&COND_MENU_ID="+menu_Id;
	params += "&COND_ASK_STAFF_ID="+staff_Id;
	params += "&COND_ASK_START_DATE="+start_Date;
	params += "&COND_ASK_END_DATE="+end_Date;
	params += "&COND_AWS_STATE="+state;
	$.ajax.submit('queryPart', 'queryApplyTradeList', params, 'detailPart',
			function(data) {
				$.endPageLoading();
				if (data.get('ALERT_INFO') != null
						&& data.get('ALERT_INFO') != '') {
					$.showWarnMessage(data.get('ALERT_INFO'));
				}
			}, function(error_code, error_info) {
				$.endPageLoading();
				alert(error_info);
			});
}


//审核操作
function approveFuc(op){
	
	var checkAuth = $.table.get("ResultTable").getCheckedRowDatas();// 获取选择中的数据		
	// 保存要启用的风险告警
	if (checkAuth == null || checkAuth.length == 0) {
		alert('请至少选择一条记录');
		return false;
	}
	var accNames = " ";
	var flag=0;
	//获得选中的checkBox对象组
	for(var i=0 ; i<checkAuth.length ; i++){
		var td_AWS_STATE = checkAuth.get([i],"AWS_STATE");
		var td_MENU_ID =checkAuth.get([i],"MENU_ID");
		if(op=='reject' && td_AWS_STATE == '1'){
			accNames += checkAuth.get([i],"MENU_ID");
			accNames += " ";
			flag =1;
		}else if(op=='accept' && td_AWS_STATE == '2'){
			accNames += checkAuth.get([i],"MENU_ID");
			accNames += " ";
			flag =2;
		}
		
	}
	if(op=='reject'){
		if(flag ==1){
			MessageBox.confirm('操作提醒',"以下" + accNames  + "授权申请已被通过，是否继续操作?<br/>选择'是'继续，选择'否'进行自动剔除并提交。",         
		           	function(btn){
					    if(btn=='ok' || btn=='cancel'){
		           		  appFuc(op,checkAuth,btn,flag);
	           			}
	           		},
	           		{ok:"是",cancel:"否",ext1:"取消"}
	        );
		}else{
			MessageBox.confirm("操作提醒","是否进行'拒绝'操作",
					function(btn){
						if(btn=='ok'){
							appFuc(op,checkAuth,btn,flag);
		           		}
				    }
		    );
		}
	}else if(op=='accept'){
		if(flag ==2){
			MessageBox.confirm('操作提醒',"以下" + accNames  + "授权申请已被拒绝，是否继续操作?<br/>选择'是'继续，选择'否'进行自动剔除并提交。",         
	           	function(btn){
				    if(btn=='ok' || btn=='cancel'){
	           		  appFuc(op,checkAuth,btn,flag);
           			}
           		},
           		{ok:"是",cancel:"否",ext1:"取消"}
           	);
		}else{
			MessageBox.confirm("操作提醒","是否进行'通过'操作",
				function(btn){
					if(btn=='ok'){
						appFuc(op,checkAuth,btn,flag);
	           		}
			    }
			);
			
		}
	}
}

//从checkLis中，获得name为checkName的DOM对象
function getCheckListNode(checkLis,checkName){
	if(checkLis.length <= 0)
		return;
	for(var i=0;i<checkLis.length;i++){
		if(checkLis[i].name == checkName){
			return checkLis[i];
		}
	}
}

function appFuc(op,checkAuth,btn,flag){
	if (checkAuth == null || checkAuth.length == 0) {
		alert('请至少选择一条记录');
		return false;
	}
	var removeFlag = 0;
	if (btn == "cancel" && flag == 1){
		removeFlag = 1;
	}
	var params = "&OPE_ASK_IDS="+checkAuth;
	params += "&OPE_OP_FLAG="+op;
	params += "&OPE_BTN="+btn;
	params += "&OPE_REMOVE_FLAG="+removeFlag;
	//加上条件信息
	var start_Date = $("#cond_START_DATE").val();
	var end_Date = $("#cond_END_DATE").val();
	var menu_Id = $("#cond_MENU_ID").val();
	var staff_Id = $("#AWS_STAFF_ID").val();
	var state = $("#cond_ASK_STATE").val();
	params += "&COND_MENU_ID="+menu_Id;
	params += "&COND_ASK_STAFF_ID="+staff_Id;
	params += "&COND_ASK_START_DATE="+start_Date;
	params += "&COND_ASK_END_DATE="+end_Date;
	params += "&COND_AWS_STATE="+state;
	$.ajax.submit('queryPart', 'approveAuthFuc', params, 'detailPart',
			function(data) {
				$.endPageLoading();
				if (data.get('ALERT_INFO') != null
						&& data.get('ALERT_INFO') != '') {
					$.showWarnMessage(data.get('ALERT_INFO'));
				}
				if (data.get('UPDATE_SUCCESS_FLAG') == 999) {
					alert("剔除数据后已无记录可提交");
					//setPopupReturnValue(tradeTypeCode, tradeTypeValue, true);
					//refreshTask();
					
				}else if (data.get('UPDATE_SUCCESS_FLAG') == 1) {
					alert ("快速授权审核成功");
				}
				//queryAuthApply();
			}, function(error_code, error_info) {
				$.endPageLoading();
				alert(error_info);
			});
	
}