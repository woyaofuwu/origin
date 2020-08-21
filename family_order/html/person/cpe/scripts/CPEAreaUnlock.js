function refreshPartAtferAuth(data) {
	
	$.beginPageLoading("正在查询数据...");
	var userInfo = data.get("USER_INFO");
	$.ajax.submit('', 'unlockAreaQryInfo', "&USER_ID="+userInfo.get("USER_ID")+"&SERIAL_NUMBER="+userInfo.get("SERIAL_NUMBER"), 'remarkPart', function(rtnData) { 
				if(rtnData!=null&&rtnData.length > 0){
					var rtnCode=rtnData.get("RTN_CODE");
					var rtnMsg=rtnData.get("RTN_MSG");
					if(rtnCode == "0"){
						//REQ201602160009 CPE无线宽带锁定小区的数量改成6个 修改了保存锁定地址
						$("#RSRV_STR1").val(rtnData.get("RSRV_STR21"));
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

function onTradeSubmit()
{
	var param = '&SERIAL_NUMBER='+$("#AUTH_SERIAL_NUMBER").val();
	param += '&REMARK='+$('#REMARK').val();  
	$.cssubmit.addParam(param); 
	return true;
}