function refreshPartAtferAuth(data) {
	
	$.beginPageLoading("正在查询数据...");
	var userInfo = data.get("USER_INFO");
	$.ajax.submit('', 'unlockAreaQryInfo', "&USER_ID="+userInfo.get("USER_ID")+"&SERIAL_NUMBER="+userInfo.get("SERIAL_NUMBER"), 'remarkPart', function(rtnData) { 
				if(rtnData!=null&&rtnData.length > 0){
					var rtnCode=rtnData.get("PCCRELIEVE");
					if("0" == rtnCode)
					{
						$("#STATUE").val("限速");
					}else{
						$("#STATUE").val("解速");
					}
					$("#RELIEVEFLAG").val(rtnCode);
					$.endPageLoading();
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
	if(!verifyAll('remarkPart'))
   	{
	   return false;
   	}
	var param = '&SERIAL_NUMBER='+$("#AUTH_SERIAL_NUMBER").val();
	param += '&REMARK='+$('#REMARK').val();  
	param += '&RELIEVEFLAG='+$('#RELIEVEFLAG').val(); 
	$.cssubmit.addParam(param); 
	return true;
}