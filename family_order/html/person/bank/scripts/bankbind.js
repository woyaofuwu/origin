function refreshPartAtferAuth(data)
{
	var user_info = data.get("USER_INFO").toString();
	var param = "&USER_INFO="+user_info;
	
	$.beginPageLoading("数据查询中..");
	$.ajax.submit('AuthPart', 'loadChildInfo', param, 'AuthPart,BankInfoPart', function(data){
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}

function checkBankCardNo()
{
	var bank_name = $('#bank_BANK_NAME').val();
	if(bank_name==""){
		alert("银行名称不能为空！");
		$('#bank_BANK_NAME').focus();
		return false;
	}
	
	var bank_card = $('#bank_BANK_CARD_NO').val();
	if(bank_card==""){
		alert("银联卡不能为空！");
		$('#bank_BANK_CARD_NO').focus();
		return false;
	}
	
	/*var password = $('#bank_PASS_WORD').val();
	if(password.length < 6 || password.length > 20){
		alert("请输入最小6位，最大20位的密码！");
		$('#bank_PASS_WORD').focus();
		return false;
	}
	var passwordCheck = /^[0-9]*$/;
	if(passwordCheck.test(password)){
		alert("密码不能全是数字！");
		$('#bank_PASS_WORD').focus();
		return false;
	}
	passwordCheck = /^[A-Za-z]+$/;
	if(passwordCheck.test(password)){
		alert("密码不能全是字母！");
		$('#bank_PASS_WORD').focus();
		return false;
	}
	passwordCheck = /^[A-Za-z0-9]+$/;
	if(!passwordCheck.test(password)){
		alert("密码由字母和数字组成，不能全为字母和数字！");
		$('#bank_PASS_WORD').focus();
		return false;
	}*/
	return true;
}
 

