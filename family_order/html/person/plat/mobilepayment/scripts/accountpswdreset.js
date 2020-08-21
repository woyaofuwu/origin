function refreshPartAtferAuth(data)
{
	$.ajax.submit('', 'getCustInfo', "&USER_INFO="+data.get("USER_INFO").toString()+"&CUST_INFO="+data.get("CUST_INFO").toString(), 'custInfoPart', 
	function(){
		$("#submitButton").attr("disabled",false);
	},
	function(error_code,error_info){
		$.endPageLoading();
		MessageBox.error("错误",error_info);
    });
}

function submitTrade(){
	if(checksubmit()){
		$.ajax.submit('condPart', 'submitTrade', '', '', 
		function(data){
			if(data.get('ALERT_INFO') != '')
				{
					MessageBox.alert("提示",data.get('ALERT_INFO'));
				}
		},
		function(error_code,error_info){
			$.endPageLoading();
			MessageBox.alert(error_info);
	    });
    }
}

function checksubmit(){
	var serial_number = $("#AUTH_SERIAL_NUMBER").val();

	//alert('serial_number:'+serial_number);
	
	if(!$.validate.verifyField($("#AUTH_SERIAL_NUMBER")[0])){
		serial_number.value="";
		return false;
	}

	return true;
}