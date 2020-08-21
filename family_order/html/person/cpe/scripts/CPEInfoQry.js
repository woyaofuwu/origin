function refreshPartAtferAuth(data) {
	
	$.beginPageLoading("正在查询数据...");
	var userInfo = data.get("USER_INFO");
	$.ajax.submit('', 'cpeInfoQry', "&USER_ID="+userInfo.get("USER_ID")+"&SERIAL_NUMBER="+userInfo.get("SERIAL_NUMBER"), 'remarkPart', function(rtnData) { 
				if(rtnData!=null&&rtnData.length > 0){
					var rtnCode=rtnData.get("RTN_CODE");
					var rtnMsg=rtnData.get("RTN_MSG");
					if(rtnCode == "0"){
						$("#RSRV_STR1").val(rtnData.get("RSRV_STR1"));
						$("#RSRV_STR4").val(rtnData.get("RSRV_STR4"));
						$("#REMARK").val(rtnData.get("REMARK"));
						$.endPageLoading();
					}else{
						$.endPageLoading();
						alert(rtnMsg);
						return false;
					}
				}else{
					$.endPageLoading();
					alert("程序出错，未找到数据！");
					return false; 
				}
			}, function(error_code, error_info,detail) {
				$.endPageLoading();
				MessageBox.error("错误提示", error_info, $.auth.reflushPage, null, null, detail);
			}); 
}