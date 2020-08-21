function queryRecord()
{
	if(!$.validate.verifyAll("QueryPart"))
	{
		return false;
	}
	$.beginPageLoading("正在查询数据...");
	$.ajax.submit('QueryPart', 'qryDimensionalCodeStateList', null, 'QueryListPart', function(data)
	{
		$.endPageLoading();
	},
	function(error_code,error_info)
	{
		$.endPageLoading();
		alert(error_info);
    });
}
function tradeActionFun(data)
{
	$.ajax.submit('AuthPart', 'qryOtherInfos', '', 'formPart', function(returnData){
		$("#CSSUBMIT_BUTTON").attr("disabled", true);
		$("#SubmitPart").attr("class", "e_dis");
	},	
	function(error_code,error_info){
		$("#CSSUBMIT_BUTTON").attr("disabled", true);
		$("#SubmitPart").attr("class", "e_dis");
		$.endPageLoading();
		alert(error_info);
    });
}
function changeOpr_code(){
	var status = $("#STATUS").val();
	var opr_code = $("#OPR_CODE").val();
	$.ajax.submit('AuthPart', 'checkStatus', '&CUR_STATUS='+status+'&CUR_OPR_CODE='+opr_code, '', checkStatusBackFun);
}
function checkStatusBackFun(data){
	var x_result_code=data.get("X_RESULT_CODE");
	var x_result_info=data.get("X_RESULT_INFO");
	if('-1'==x_result_code){
		$("#OPR_CODE").val("");
		$("#CSSUBMIT_BUTTON").attr("disabled", true);
		$("#SubmitPart").attr("class", "e_dis");
		alert(x_result_info);
	}else{
		$("#CSSUBMIT_BUTTON").attr("disabled", "");
		$("#SubmitPart").attr("class", "");
	}
}
function beforeActionFun() {
		if(confirm('确认提交变更操作')){
		return true;
	}
	return false;
}