/**
 * REQ201510270009 FTTH光猫申领押金金额显示优化【2015业务挑刺】
 * 新增显示押金金额
 * */
function refreshPartAtferAuth(data)
{  
	$.beginPageLoading("正在查询数据...");
	var userInfo = data.get("USER_INFO");
	$.ajax.submit('', 'checkFTTHdeposit', "&USER_ID="+userInfo.get("USER_ID")+"&SERIAL_NUMBER="+userInfo.get("SERIAL_NUMBER"), '', function(rtnData) { 
			if(rtnData!=null&&rtnData.length > 0){
					$.endPageLoading();
					var rtnCode=rtnData.get("DEPOSIT");
					$("#DEPOSIT").val(rtnCode);
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
	param += '&DEPOSIT='+$('#DEPOSIT').val();
	param += '&APPLY_TYPE='+$('#APPLY_TYPE').val();
	
	$.cssubmit.addParam(param);
	
	return true;
}