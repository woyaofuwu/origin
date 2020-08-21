function refreshPartAtferAuth(data)
{
	$.ajax.submit('', 'loadChildInfo', "&USER_INFO="+data.get("USER_INFO").toString()+"&CUST_INFO="+data.get("CUST_INFO").toString()+"&ACCTDAY_INFO="+data.get("ACCTDAY_INFO").toString(), 'userGPRSInfos', 
	function()
	{
		if("TRUE" == $("#ISGRP").val())
		{
			$("#REMARK").val($("#GRP_REMARK").val());
		}
	},
	function(error_code,error_info){
		$.endPageLoading();
		$.cssubmit.disabledSubmitBtn(true);
		alert(error_info);
    });
}

function addRuleParams()
{
	if("TRUE" == $("#ISGRP").val())
	{
		var param = "&ISGRP=TRUE";
		$.tradeCheck.addParam(param);
	}
}

function changeGPRS(){

	var table = $.table.get("SubTable");
	var rowEdit = $.ajax.buildJsonData("discntPart");
	rowEdit["col_END_DATE"]=getEndDate();
	
	for(var i=0;i<table.getTableData(null, true).length;i++){
		table.updateRow(rowEdit,i+1);
	}	
}

function getEndDate(){
	var sysdate = new Date();
	var s="";
	var d="";
	
	s += sysdate.getFullYear() + "-";
	if(sysdate.getMonth()<9){
	  s += '0'+(sysdate.getMonth() + 1) + "-";            // 获取月份。
	}else{
		s += (sysdate.getMonth() + 1) + "-";
	}
	if(sysdate.getDate()<9){
		s += '0'+ (sysdate.getDate()-1+" "+"23:59:59");
	}else{
		s += (sysdate.getDate()-1+" "+"23:59:59");                   // 获取日。
	}	
	return s;
}

/*提交*/
function onTradeSubmit()
{
	if(!$.validate.confirmAll()) return false;

	return true;
}