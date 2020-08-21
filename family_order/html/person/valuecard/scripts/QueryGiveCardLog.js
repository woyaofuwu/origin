function qryGiveValueCardInfo(){
	if(!$.validate.verifyAll("QueryCondPart")) {
		return false;
	}
	var bStaffId =$("#cond_BEGIN_STAFF_ID").val();
	var eStaffId =$("#cond_END_STAFF_ID").val();
	if(bStaffId !="" && eStaffId ==""){
		alert("请输入结束工号");
		return false;
	}
	if(bStaffId =="" && eStaffId !=""){
		alert("请输入开始工号");
		return false;
	}
	
	
	$.beginPageLoading("正在查询数据...");
	$.ajax.submit('QueryCondPart,recordNav','getValueCardGiveInfo','','QueryListPart',
			function(){
					$.endPageLoading();
			}, 
			function(error_code, error_info,detail){
				$.endPageLoading();
				alert(error_info);
			});
}