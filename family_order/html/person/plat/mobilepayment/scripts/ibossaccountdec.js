function queryAccountPay()
{
	if(CheckValideClick())
	{
		$.ajax.submit('condPart,IBossAccountDecNav', 'queryAccountPay', null, 'tableInfoPart', function(data){
		
			if(data.get('ALERT_INFO') != '')
			{
				MessageBox.alert("提示",data.get('ALERT_INFO'));
			}
			if(data.get('SUCCESS_FLAG') == 'true')
			{
				$.cssubmit.disabledSubmitBtn(false);
			}
		},
		function(error_code,error_info){
			$.endPageLoading();
			MessageBox.error("错误",error_info);
   	    });
	}
}

function CheckValideClick()
{	
	var serial_number = $("#cond_SERIAL_NUMBER").val();
	
	if(!$.validate.verifyField($("#cond_SERIAL_NUMBER")[0])){
		serial_number.value="";
		return false;
	}
	return true;
}

function checkBeforeSubmit(){

	if($("#TRANSACTIONS")==null||$("#TRANSACTIONS").checked==false) 
	{
		MessageBox.alert("提示",'请选中要操作的数据');
		return false;
	}else{
		this.blur();
		return confirmAll(this);
	}
	
}