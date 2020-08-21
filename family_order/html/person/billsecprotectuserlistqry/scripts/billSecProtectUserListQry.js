function queryGoodsList()
{
	if(!$.validate.verifyAll("CondPart")) {
		return false;
	}else{	 
		$.beginPageLoading("正在查询数据...");
		$.ajax.submit('CondPart', 'qryBillSecProtectUserList', "", 'ResultPart', function(){
			$.endPageLoading();
		},function(code, info, detail){
			$.endPageLoading();
			MessageBox.error("错误提示","查询错误！", null, null, info, detail);
		});
	}
}
 

function reset(){
	$("#cond_SERIAL_NUMBER").val('');
	$("#cond_PROTECT_TYPE_CODE").val('');
}

function exportBeforeAction(obj) {
//	if(!$.validate.verifyAll("CondPart")) {
//		return false;
//	}
	return true;
}