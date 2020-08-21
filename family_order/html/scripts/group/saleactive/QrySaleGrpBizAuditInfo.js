/**
 * 查询集团业务稽核工单信息
 * @return
 */
function queryGrpAuditInfos(){
		//查询条件校验
	if(!$.validate.verifyAll("ConditionPart")) {
		return false;
	}
	
	/*if($("#GROUP_ID").val()=="" && $("#CUST_NAME").val()==""){
		alert("请输入集团客户编码或名称！");
		return false;
	}*/
		
	var startDate = $("#START_DATE").val();
	var endDate = $("#END_DATE").val();
	if($.trim(startDate).length > 7){
		startDate = startDate.substring(0,7);
	}
	if($.trim(endDate).length > 7){
		endDate = endDate.substring(0,7);
	}
	if(startDate != endDate){
		alert("开始时间和结束时间必须在同一个月内!");
		return false;
	}
	
	$.beginPageLoading("数据查询中..");
	$.ajax.submit('ConditionPart,hiddenPart', 'queryGrpAuditInfos', null, 'QueryListPart,hiddenPart', function(data){
		$.endPageLoading();
		var flag = data.get("ENABLE_FLAG");
		if(flag == "true" || flag == true) {
			$("#CITY_CODE").attr("disabled",false);
		}else {
			$("#CITY_CODE").attr("disabled",true);
		}
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}

function init()
{
	var flag = $("#ENABLE_FLAG").val();
	if(flag == "true" || flag == true){
		$("#CITY_CODE").attr("disabled",false);
	} else {
		$("#CITY_CODE").attr("disabled",true);
	}
}
