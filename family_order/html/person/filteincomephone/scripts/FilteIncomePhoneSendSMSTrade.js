function refreshPartAtferAuth(data) {
	$.ajax.submit('', 'loadChildInfo', "&USER_INFO="
			+ (data.get("USER_INFO")).toString() + "&CUST_INFO="
			+ (data.get("CUST_INFO")).toString(),
			'funcPart', function() {
			}, function(error_code, error_info,detail) {
				$.endPageLoading();
				MessageBox.error("错误提示", error_info, $.auth.reflushPage, null, null, detail);
			});
}

function submitopenfunc(){

	var open_tag = $("#comminfo_SENDSMS_TAG").val();
	var cond_OPENFUNC_RADIO = $("input[name='cond_OPENFUNC_RADIO']:checked").val();
	if(open_tag == "0"){
		if(cond_OPENFUNC_RADIO =="2"){
			alert("号码未开通短信提醒功能，无法关闭！");
			return false;
		}
		if(confirm("确定开通短信提醒功能吗？"))
		{
			return true;
		}
	}else if(open_tag == "1"){
		if(cond_OPENFUNC_RADIO =="1"){
			alert("号码已经开通短信提醒功能，无需重新开通！");
			return false;
		}
		if(confirm("确定关闭短信提醒功能吗？"))
		{
			return true;	
			
		}
	}
	
	return false;
}