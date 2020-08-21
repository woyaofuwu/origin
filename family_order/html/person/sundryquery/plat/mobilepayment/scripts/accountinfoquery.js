function refreshPartAtferAuth(data)
{
	$.ajax.submit('AuthPart', 'loadChildInfo', null, 'paymentInfoPart,trueNamePart', 
	function(){
		$("#realNameHref").attr("disabled", false);
	},
	function(error_code,error_info){
		$.endPageLoading();
		MessageBox.error("错误",error_info);
    });
}

function changeRealName()
{
	var trueName = $("#cond_TRUE_NAME").val();
	if(trueName == null || trueName.length<=0){
		MessageBox.alert("提示","实名标志不能为空！");
		return;
	}
	
	$.ajax.submit('AuthPart,trueNamePart', 'submitTrade', null, '', function(data){
		
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