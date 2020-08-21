function queryContactLogs(){
		//查询条件校验
	if(!$.validate.verifyAll("QueryCondPart")) {
		return false;
	}
	$.beginPageLoading("数据查询中..");
	$.ajax.submit('QueryCondPart', 'queryContactLogs', null, 'QueryListPart', function(data){
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}

function changeType(){
	var type = $("#QUERY_TYPE").val()
	if(type==0){
		//标识
		$("#CUST_CONTACT_ID_LI").css("display","");
		$("#STAFF_ID_LI").css("display","none");
		$("#START_DATE_LI").css("display","none");
		$("#END_DATE_LI").css("display","none");
		$("#CUST_CONTACT_ID").attr("nullable","no");
		$("#STAFF_ID").attr("nullable","yes");
		$("#START_DATE").attr("nullable","yes");
		$("#END_DATE").attr("nullable","yes");
	}else if(type==1){
		$("#CUST_CONTACT_ID_LI").css("display","none");
		$("#STAFF_ID_LI").css("display","");
		$("#START_DATE_LI").css("display","");
		$("#END_DATE_LI").css("display","");
		$("#CUST_CONTACT_ID").attr("nullable","yes");
		$("#STAFF_ID").attr("nullable","no");
		$("#START_DATE").attr("nullable","no");
		$("#END_DATE").attr("nullable","no");
	}
}

//同步导出
function exportTradeData() {
	$.beginPageLoading("导出中...");
	
	$.ajax.submit('QueryCondPart','exportExcel',null,'',function(data){
		if (data && data.get("url")) {
			window.open(data.get("url"),"_self");
		}
		$.endPageLoading();
	},function(e,i){
		$.showErrorMessage("导出失败");
		$.endPageLoading();
	});
}
function inputCtrlForStaff(obj){
	var staffId = $(obj).val().toUpperCase(); //obj.value.toUpperCase();
	if(staffId.length > 8){
		$(obj).val(staffId.substring(0,8));
	} else {
		$(obj).val(staffId);
	}
}