var param;

//查询对账结果
function queryPayOrder(){	
	$("#BACKFEE_BTN").attr('disabled',true);
	$("#BACKFEE_BTN").attr('class','e_button-navy e_button-r');
	$.beginPageLoading("努力加载中...");
    ajaxSubmit('ParamsPart', 'queryPayOrder', null, 'QueryListPart', function(data) {
    	$.endPageLoading();
		if (data.get('ALERT_INFO') != '') {    //弹出返回的页面提示信息
			MessageBox.alert(data.get('ALERT_INFO'));
		}
    }, function(code, info, detail) {
        $.endPageLoading();
        MessageBox.error("错误提示", info);
    }, function() {
    	$.endPageLoading();
		MessageBox.alert("警告提示", "查询超时");
	});    
}
//选择一条记录后将它的值返回后台
function changeInfo(obj){	
	param = "&transactionId=" + obj.getAttribute('trans_action_id');  //取终端流水号
	var recResult = obj.getAttribute('rec_result');
	var isRefund = obj.getAttribute('is_refund');
	if(recResult == '1' && isRefund != '1'){
		$("#BACKFEE_BTN").attr('disabled',false);  //对账结果为1(BOSS多)且未退过款才可使用退款按钮
		$("#BACKFEE_BTN").attr('class','e_button-red e_button-r');
	}else{
		$("#BACKFEE_BTN").attr('disabled',true);
		$("#BACKFEE_BTN").attr('class','e_button-navy e_button-r');		
	}	
}
//调用退款接口
function backFee(){	
	if(confirm("确定提交吗?")){ 
		$.beginPageLoading("努力加载中...");
	    ajaxSubmit(null, 'backFee', param, null, function(data) {
	    	$.endPageLoading();
			if (data.get('ALERT_INFO') != '') {    //弹出返回的页面提示信息
				MessageBox.alert(data.get('ALERT_INFO'));
				queryPayOrder();
			}
	    }, function(code, info, detail) {
	        $.endPageLoading();
	        MessageBox.error("错误提示", info);
	        queryPayOrder();
	    }, function() {
	    	$.endPageLoading();
			MessageBox.alert("警告提示", "查询超时");
			queryPayOrder();
		});
		$("#BACKFEE_BTN").attr('disabled',true);
		$("#BACKFEE_BTN").attr('class','e_button-navy e_button-r');
	}    
}