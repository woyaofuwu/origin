function sendAuditMessage(){	
	//校验
	if(!$.validate.verifyAll("GrpDealPart")) {
		return false;
	}
	//审核后，提交
	$.ajax.submit('GrpDealPart', 'sendAuditMessage', null, null, 
	function(data){			
		var content = '点【确定】继续业务受理';
		if(data) {
			content = '处理成功,点【确定】继续业务受理';
		}
						
		MessageBox.success("业务受理",content, function(btn){
			if (btn == "ok") {
				window.parent.location.reload(true);
			} 
		}, {});	
  		$.endPageLoading();
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
}