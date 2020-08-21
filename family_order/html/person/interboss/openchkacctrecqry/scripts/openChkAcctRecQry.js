function queryOpenChkAcctRecLList()
{
	if(!$.validate.verifyAll("CondPart")) {
		return false;
	}else{	 
		$.beginPageLoading("正在查询数据...");
		$.ajax.submit('CondPart', 'qryOpenChkAcctRecList', "", 'ResultPart', function(){
			$.endPageLoading();
		},function(code, info, detail){
			$.endPageLoading();
			MessageBox.error("错误提示","查询错误！", null, null, info, detail);
		});
		$.ajax.submit('CondPart', 'qryOpenChkAcctRecDayAll', "", 'ResultPart0', function(){
			$.endPageLoading();
		},function(code, commInfo, detail){
			$.endPageLoading();
			MessageBox.error("错误提示","查询错误！", null, null, commInfo, detail);
		});
	}
}
 

function reset(){
	$("#cond_RECON_DATE").val('');
	$("#cond_RESULT_TYPE").val('');
}

function exportBeforeAction(obj) {
//	if(!$.validate.verifyAll("CondPart")) {
//		return false;
//	}
	return true;
}