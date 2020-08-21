function qryOrderInfos(obj){
	var param='';
	if(!isBlank($("#SUB_TYPE_CODE").val())){
		param += '&SUB_TYPE_CODE='+$("#SUB_TYPE_CODE").val();
	}
	if(!isBlank($("#cond_IBSYSID").val())){
		param += '&cond_IBSYSID='+$("#cond_IBSYSID").val();
	}
	if(!isBlank($("#cond_PRODUCTNO").val())){
		param += '&cond_PRODUCTNO='+$("#cond_PRODUCTNO").val();
	}
	if(!isBlank($("#cond_TITLE").val())){
		param += '&cond_TITLE='+$("#cond_TITLE").val();
	}
	if(!isBlank($("#cond_STAFF_ID").val())){
		param += '&cond_STAFF_ID='+$("#cond_STAFF_ID").val();
	}
	
	$.beginPageLoading('正在查询订单信息...');
	$.ajax.submit(null,'qryOrderInfos',param,'queryPart',function(data){
		hidePopup(obj);
		$.endPageLoading();
	},function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});
}

function isBlank(obj){
	if(obj==undefined || obj==null || obj==''){
		return true;
	}
	return false;
}

function singleSenderSms(obj){
	var tr = $(obj).parent().parent();
	var ibsysid = tr.attr("ibsysid");
	if(isBlank(ibsysid)){
		alert("未获取到订单号！");
		return false;
	}
	var title = tr.attr("title");
	if(isBlank(title)){
		alert("未获取到工单主题！");
		return false;
	}
	var staff = tr.attr("staff");
	if(isBlank(staff)){
		staff='';
	}
	var staffPhone = tr.attr("staffPhone");
	if(isBlank(staffPhone)){
		alert("未获取到员工电话号码，无法发送短信！");
		return false;
	}
	
	$.beginPageLoading('正在发送短信...');
	$.ajax.submit(null,'singleSenderSms2Staff','&IBSYSID='+ibsysid+'&TITLE='+title+'&STAFF_ID='+staff+"&STAFF_PHONE="+staffPhone,null,function(data){
		MessageBox.success("提交成功", "短信发送成功！", function(btn){});
		$.endPageLoading();
	},function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});
}

function batchSenderSms(){
	var rowDatas = myTable.getCheckedRowsData("TRADES");
	
	if(rowDatas==null||rowDatas==""){
		$.validate.alerter.one(rowDatas,"请您先选择需要发送短信的工单!");
		return false;
	}
	
	var requestList = new Wade.DatasetList();
	for(var i=0;i<rowDatas.length;i++){
		var rowData = rowDatas.get(i);
		var requestData = new Wade.DataMap();
		requestData.put("IBSYSID",rowData.get("IBSYSID"));
		requestData.put("TITLE",rowData.get("TITLE"));
		requestData.put("STAFF_ID",rowData.get("STAFF_ID"));
		requestData.put("STAFF_PHONE",rowData.get("STAFF_PHONE"));
		requestList.add(requestData);
	}
	$.beginPageLoading('正在发送短信...');
	$.ajax.post(null, 'batchSenderSms2Staff', '&SUBMIT_PARAM='+requestList.toString(), null, function(data){
		MessageBox.success("提交成功", "短信发送成功！", function(btn){});
		$.endPageLoading();
	}, function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});
	
}