function qryOrder(){
	
	$("#RADIO_MOBILE").val('');
	$("#RADIO_ORDERID").val('');
	$("#LOG_NUMBER").val('');
	$("#COMPANY").val('');
	$("#RADIO_RSRV_STR1").val('');
	
	$.beginPageLoading("正在查询数据...");
	ajaxSubmit('QryOrderPart','qryOrder','','QueryListPart',function(data){
		$.endPageLoading();
		if (data.get('ALERT_INFO') != '') {    //弹出返回的页面提示信息
			MessageBox.alert(data.get('ALERT_INFO'));
		}
		}, function(code, info, detail) {
		    $.endPageLoading();
		    MessageBox.error("错误提示", info, function(btn) {
		    }, null, detail);
		   }, function() {
		    $.endPageLoading();
		    MessageBox.alert("告警提示", "操作超时!", function(btn) {
		    });
		  });

}
//给单选隐藏域赋值
function setRowDateEdit(obj){
	
	$('#SIM_CARD_NO').val('');
	$('#RADIO_MOBILE').val(obj.getAttribute('SERIAL_NUMBER')==null?"":obj.getAttribute('SERIAL_NUMBER'));
	$('#RADIO_ORDERID').val(obj.getAttribute('ORDER_ID')==null?"":obj.getAttribute('ORDER_ID'));
	$('#RADIO_RSRV_STR1').val(obj.getAttribute('RSRV_STR1')==null?"":obj.getAttribute('RSRV_STR1'));
	$('#RADIO_STATE').val(obj.getAttribute('STATE')==null?"":obj.getAttribute('STATE'));
		
}
//确认按钮conFirm
function conFirm(){
	//判断是否选中
	var redio = $('#RADIO_MOBILE').val();
	if (redio == '') {
		MessageBox.alert("请选择要操作的订单");
		return;
	}
	var state = $('#RADIO_STATE').val();
	if (state != 'PC' && state != 'PP') {
		MessageBox.alert("不是待确认状态");
		return;
	}
	ajaxSubmit('hiddenPart','conFirm','','',function(data){
		MessageBox.alert("确认成功");
		qryOrder();
		}, function(code, info, detail) {
		    $.endPageLoading();
		    MessageBox.error("错误提示", info, function(btn) {
		    }, null, detail);
		   }, function() {
		    $.endPageLoading();
		    MessageBox.alert("告警提示", "操作超时!", function(btn) {
		    });
		  });

}
//备货按钮,备货查询判定方法readyGood
function readyGood(){
	//判断是否选中
	var redio = $('#RADIO_MOBILE').val();
	if (redio == '') {
		MessageBox.alert("请选择要操作的订单");
		return ;
	}
	ajaxSubmit('hiddenPart','qryReadyGood','','',function(data){
		
		if (data.get('STATE') != 'PK') {    //弹出返回的页面提示信息
			MessageBox.alert("状态不是待备货状态");
			return;
		}
		if (data.get('RSRV_TAG1') != '1' && data.get('RSRV_TAG1') != '2') {    //弹出返回的页面提示信息
			MessageBox.alert("状态正在通知平台，请稍等片刻再进行操作");
			return;
		}
		if (data.get('DEAL_STATE') != '0000' && data.get('DEAL_STATE') != '0') {    //弹出返回的页面提示信息
			MessageBox.alert("状态同步异常："+data.get('DEAL_RESULT'));
			return;
		}
		//备货前提条件满足，调用备货修改状态
		readyGoodUpdate();
		}, function(code, info, detail) {
		    $.endPageLoading();
		    MessageBox.error("错误提示", info, function(btn) {
		    }, null, detail);
		   }, function() {
		    $.endPageLoading();
		    MessageBox.alert("告警提示", "操作超时!", function(btn) {
		    });
		  });

}

//备货按钮,备货修改状态方法readyGood
function readyGoodUpdate(){
	ajaxSubmit('hiddenPart','readyGoodUpdate','','',function(data){
		$.endPageLoading();
		MessageBox.alert("备货成功");
		qryOrder();
		}, function(code, info, detail) {
		    $.endPageLoading();
		    MessageBox.error("错误提示", info, function(btn) {
		    }, null, detail);
		   }, function() {
		    $.endPageLoading();
		    MessageBox.alert("告警提示", "操作超时!", function(btn) {
		    });
		  });

}


//发货按钮sendGood
//发货按钮sendGood查询判定方法sendGood
function sendGood(){
	//判断是否选中
	var redio = $('#RADIO_MOBILE').val();
	if (redio == '') {
		MessageBox.alert("请选择要操作的订单");
		return;
	}
	ajaxSubmit('hiddenPart','qrysendGood','','',function(data){
		$.endPageLoading();
		if (data.get('STATE') != 'PD') {    //弹出返回的页面提示信息
			MessageBox.alert("状态不是待发货状态");
			return;
		}
		if (data.get('RSRV_TAG1') != '1' && data.get('RSRV_TAG1') != '2') {    //弹出返回的页面提示信息
			MessageBox.alert("状态正在通知平台，请稍等片刻再进行操作");
			return;
		}
		if (data.get('DEAL_STATE') != '0000' && data.get('DEAL_STATE') != '0') {    //弹出返回的页面提示信息
			MessageBox.alert("状态同步异常："+data.get('DEAL_RESULT'));
			return;
		}
		//发货前提条件满足，调用发货修改状态
		sendGoodUpdate();
		}, function(code, info, detail) {
		    $.endPageLoading();
		    MessageBox.error("错误提示", info, function(btn) {
		    }, null, detail);
		   }, function() {
		    $.endPageLoading();
		    MessageBox.alert("告警提示", "操作超时!", function(btn) {
		    });
		  });

}

//发货按钮,发货修改状态方法sendGoodUpdate
function sendGoodUpdate(){
	//判断快递单号快递公司不能为空
	var logisticsNumber = $('#LOGISTICS_NUMBER').val();
	var logisticsCompany = $('#LOGISTICS_COMPANY').val();
	
	if (logisticsNumber == '') {    //弹出返回的页面提示信息
		MessageBox.alert("请填写快递单号");
		return;
	}
	if (logisticsCompany == '') {    //弹出返回的页面提示信息
		MessageBox.alert("请填写快递公司");
		return;
	}
	//给隐藏域赋值
	$('#LOG_NUMBER').val($('#LOGISTICS_NUMBER').val());
	$('#COMPANY').val($('#LOGISTICS_COMPANY').val());
	
	ajaxSubmit('hiddenPart','sendGoodUpdate','','',function(data){
		
		MessageBox.alert("发货成功");
		qryOrder();
		}, function(code, info, detail) {
		    $.endPageLoading();
		    MessageBox.error("错误提示", info, function(btn) {
		    }, null, detail);
		   }, function() {
		    $.endPageLoading();
		    MessageBox.alert("告警提示", "操作超时!", function(btn) {
		    });
		  });

}
//完成查询方法
function complete(){
	//判断是否选中
	var redio = $('#RADIO_MOBILE').val();
	if (redio == '') {
		MessageBox.alert("请选择要操作的订单");
		return;
	}
	ajaxSubmit('hiddenPart','qrysendGood','','',function(data){
		$.endPageLoading();
		if (data.get('STATE') != 'PS') {    //弹出返回的页面提示信息
			MessageBox.alert("未发货，不能完成");
			return;
		}
		if (data.get('RSRV_TAG1') != '1' && data.get('RSRV_TAG1') != '2') {    //弹出返回的页面提示信息
			MessageBox.alert("状态正在通知平台，请稍等片刻再进行操作");
			return;
		}
		if (data.get('DEAL_STATE') != '0000' && data.get('DEAL_STATE') != '0') {    //弹出返回的页面提示信息
			MessageBox.alert("状态同步异常："+data.get('DEAL_RESULT'));
			return;
		}
		//完成前提条件满足，调用完成修改状态
		completeUpdate();
		}, function(code, info, detail) {
		    $.endPageLoading();
		    MessageBox.error("错误提示", info, function(btn) {
		    }, null, detail);
		   }, function() {
		    $.endPageLoading();
		    MessageBox.alert("告警提示", "操作超时!", function(btn) {
		    });
		  });

}

//完成按钮修改状态complete
function completeUpdate(){
	 
	MessageBox.confirm("告警提示",'确定要完成吗?',function(ret){
		  if(ret=="ok"){
		   $.beginPageLoading("正在提交数据...");
		   ajaxSubmit('hiddenPart','complete','','',function(data){
				MessageBox.alert("完成成功");	
				qryOrder();
				}, function(code, info, detail) {
				    $.endPageLoading();
				    MessageBox.error("错误提示", info, function(btn) {
				    }, null, detail);
				   }, function() {
				    $.endPageLoading();
				    MessageBox.alert("告警提示", "操作超时!", function(btn) {
				    });
				  });
		  }
		 });

}

//cancel()
function cancel(){
	/*var data = $.DataMap();
	data.put("RESULT_CODE", '0');
	data.put("SIM_CARD_NO", '898600E8211350398220');
	data.put("IMSI", '460027897328220');
	data.put("EMPTY_CARD_ID", '2107000740019979');
	afterWriteCard(data);
	return;*/
	//判断是否选中
	var redio = $('#RADIO_MOBILE').val();
	if (redio == '') {
		MessageBox.alert("请选择要操作的订单");
		return;
	}
	MessageBox.confirm("告警提示",'确定要取消吗?',function(ret){
		  if(ret=="ok"){
		   $.beginPageLoading("正在提交数据...");
		   ajaxSubmit('hiddenPart','cancel','','',function(data){
				MessageBox.alert("取消成功");	
				qryOrder();
				}, function(code, info, detail) {
				    $.endPageLoading();
				    MessageBox.error("错误提示", info, function(btn) {
				    }, null, detail);
				   }, function() {
				    $.endPageLoading();
				    MessageBox.alert("告警提示", "操作超时!", function(btn) {
				    });
				  });
		  }
		 });

}

function beforeReadCard() {//读卡前，将手机号码传过去查询资源
    var sn = $("#RADIO_MOBILE").val();//设置隐藏域传手机号码,只能单选，如果没选隐藏域传空
    if(sn==''){
    	 MessageBox.alert("告警提示", "请选择订单!");
    	 return false;
    }
    $.simcard.setSerialNumber(sn);
    return true;
}

function beforeCheckSimCardNo(data) {//读卡返回数据，可用来判断卡是否被写过
    var isWrited = data.get("IS_WRITED"); // 用来判断卡是否被写过
    if (isWrited === "1") {//已经写过了，展示号码，没写过不做任何事，继续往下走
        var simno = data.get("SIM_CARD_NO");
        $("#SIM_CARD_NO").val(simno);
    }
}

function beforeWriteCard(){//写卡之前的判断，只返回ture和false，满足写卡条件返回true，不满足返回false
    //判断是否选择要操作的订单，先判断有没有选过订单、再判断RSRV_STR1有没有写过卡，提示客户是否再次写卡，再次写卡的话返回true,否则返回false
	 var sn = $("#RADIO_MOBILE").val();
	 var rsrvStr1= $("#RADIO_RSRV_STR1").val();
	 if(sn==''){
    	 MessageBox.alert("告警提示", "请选择订单!");
    	 return false;
     }
	 
	 if(rsrvStr1!='1'){
		 MessageBox.confirm("告警提示",'确定要再次写卡吗?',function(ret){
			  if(ret=="ok"){
			    return true;
			  }else{
				  return false;
			  }
		 });
     }else{
    	  return true;
     }
   
}
	 
function afterWriteCard(data) {
    if (data.get("RESULT_CODE") === "0") {
    	//提示写卡成功
    	
    	var simno = data.get("SIM_CARD_NO");
    	var imsi=data.get("IMSI");
    	var emptyCardId=data.get("EMPTY_CARD_ID");
    	$("#SIM_CARD_NO").val(simno);
    	var orderId = $("#RADIO_ORDERID").val();
    	if(orderId == ''){
    		MessageBox.alert("对不起，写卡成功，订单号为空");
    	}
    	var params = "&SIM_CARD_NO="+simno+"&IMSI="+imsi+"&EMPTY_CARD_ID="+emptyCardId+"&ORDER_ID="+orderId+"&SERIAL_NUMBER="+$("#RADIO_MOBILE").val();
    	//ajax更新下面状态
    	 //通过订单号更新订单的SIM_CARD_NO、IMSI、EMPTY_CARD_ID RSRV_STR1=2：写卡成功 //qryOrder()重新刷新页面
    	//更新完将号码展示
    	$.beginPageLoading("写卡成功,正在生成写卡工单......");
    	ajaxSubmit('','writeCardUpdate',params,'',function(data){
	    		MessageBox.alert("写卡工单生成成功");
	    		qryOrder();
    		}, function(code, info, detail) {
    		    $.endPageLoading();
    		    MessageBox.error("错误提示", info, function(btn) {
    		    }, null, detail);
    		   }, function() {
    		    $.endPageLoading();
    		    MessageBox.alert("告警提示", "操作超时!", function(btn) {
    		    });
    		  });
        
       
        
    }
}
