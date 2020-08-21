//证件号码查询 
function queryCustViolationNumber(){
	//查询条件校验
	if(!$.validate.verifyAll("psptPart")) {
		return false;
	}
	var psptId = $("#PSPT_ID").val();
	var psptTypeCode = $("#PSPT_TYPE_CODE").val();
	if(psptTypeCode==0 ||psptTypeCode==2){//身份证、户口本
		if(psptId.length!=15 && psptId.length!=18)
		{
			alert("证件号码长度不正确,请重新输入！");
			return false;
		}
	}	
	$.beginPageLoading("正在查询数据...");
	$.ajax.submit('psptPart', 'queryCustViolationNumber', null, 'QueryListPart', function(data){
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}