function refreshPartAtferAuth(data)
{
	$.ajax.submit('', 'loadChildInfo', "&USER_INFO="+data.get("USER_INFO").toString(), 'ScoreAcctPart', function(){
			
	},
	function(error_code,error_info){
		$.endPageLoading();
		MessageBox.error("错误提示", error_info, $.auth.reflushPage, null, null, null);
    });
}

/** check serialNumber */

//业务提交
function onTradeSubmit()
{
	var param = '&SERIAL_NUMBER='+$("#AUTH_SERIAL_NUMBER").val();
	if(confirm('您确定修改用户积分账户资料吗？')){
		$.cssubmit.addParam(param);
		return true;
	}else{
		return false;
	}
}

