function authGroupclickActionBase(sn){
	$('#AUTH_SERIAL_NUMBER').val(sn);
    if($.userCheck.checkSnValid('AUTH_SERIAL_NUMBER')){
		$.userCheck.queryUser('AUTH_SERIAL_NUMBER');
	}
}
