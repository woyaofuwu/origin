function queryRemindOrder(){
	var serialNumber = $("#cond_SERIAL_NUMBER").val();
	var  remindTag= $("#DESTROY_REMIND_TAG").val();
	if(remindTag==null||""==remindTag){
		MessageBox.alert("重要提示","请选择催单状态！");
		return;
	}
	var param = "&REMOTE_SERIAL_NUMBER=" + serialNumber+"&DESTROY_REMIND_TAG="+remindTag;
	$.beginPageLoading("数据处理中...");
	$.ajax.submit('', 'queryRemindOrder',param , 'TableInfoPart', function(data){
		$.endPageLoading();
		if(data && data.length) {
			//MessageBox.success("提示信息","查询成功！");
        } else {
        	MessageBox.success("提示信息","无数据！");
		}
		$.endPageLoading();
		// $.cssubmit.disabledSubmitBtn(false);
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}

function urgeOrder(self){
	if(!$.validate.verifyAll('QueryCondPart')) {
		return false;
	}
	var remindReason=$('#REMIND_REASON').val();
	var rowIndex = self.parentNode.parentNode.parentNode.rowIndex;
	var num =rowIndex-1;
	var rowData = DestroyTable.getRowData(num);
	var orderId =rowData.get('REMOTE_ORDER_ID');//订单号
	var serial_number =rowData.get('REMOTE_SERIAL_NUMBER');//手机号
	var param = "&REMOTE_SERIAL_NUMBER=" + serial_number + "&REMOTE_ORDER_ID=" + orderId+"&CD_REASON="+remindReason;
	$.beginPageLoading("催单中...");
	$.ajax.submit('', 'onTradeSubmit',param , '', function(data){
		$.endPageLoading();
		MessageBox.success("提示信息","催单成功！");
		$(self).attr('disabled',true);
		$(self).html('已催单');
		//window.location.reload();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert("错误提示：" + error_info);
    });
}
/*去掉空格*/
function trim(str) {
	return str.replace(/^\s+|\s+$/,'');
}
