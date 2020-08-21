function refreshPartAtferAuth(data) {
	$.beginPageLoading("正在查询数据...");
	var userInfo = data.get("USER_INFO"); 
	$.ajax.submit('', 'cpeDataInfoQry', "&USER_ID="+userInfo.get("USER_ID")+"&SERIAL_NUMBER="+userInfo.get("SERIAL_NUMBER"),
			'QueryListPart', function(data) { 
					$.endPageLoading();
					if(data !=null && data.get("CODE")=="2"){
						var msg=data.get("MSG");
						alert(msg);
						return false;
					}
					
			}, function(error_code, error_info,detail) {
				$.endPageLoading();
				MessageBox.error("错误提示", error_info, $.auth.reflushPage, null, null, detail);
			}); 
}