function queryMobilePayment()
{
	if(checksubmit())
	{
		$.ajax.submit('QueryPart', 'submitTrade', null, 'tablePart,listPart', function(data){
		
			if(data.get('ALERT_INFO') != '')
			{
				MessageBox.alert("提示",data.get('ALERT_INFO'));
			}
		},
		function(error_code,error_info){
			$.endPageLoading();
			MessageBox.error("错误",error_info);
   	    });
	}
}

/* check submit */
function checksubmit(){
	var serial_number = $("#cond_SERIAL_NUMBER").val();
	var startDate = $('#cond_START_DATE').val();
	var endDate =   $('#cond_END_DATE').val();	
	//alert('serial_number:'+serial_number + ' startDate:'+startDate + ' endDate:'+endDate);
	
	if(serial_number.length<=0 || serial_number==""){
		MessageBox.alert("提示","手机号码不能为空！");
		return false;
	}
	
	if(!$.validate.verifyField($("#cond_SERIAL_NUMBER")[0])){
		serial_number.value="";
		return false;
	}
	
	if(startDate.length <= 0 || startDate == ""){
		MessageBox.alert("提示","开始时间不能为空！");
		return false;
	}
	
	if(endDate.length <= 0 || endDate == ""){
		MessageBox.alert("提示","结束时间不能为空！");
		return false;
	}

	return true;
}