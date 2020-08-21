function qryCouponsQuotaInfo(){
	$.beginPageLoading("正在查询数据...");
	$.ajax.submit('QueryCondPart,recordNav','queryCouponsQuotaInfos','','QueryListPart',
			function(){
					$.endPageLoading();
			}, 
			function(error_code, error_info,detail){
				$.endPageLoading();
				alert(error_info);
			});
}

function addCouponsQuotaConfigInfo(){
	$('#popup').css('display','');
	$('#updateButton').attr("disabled",true);
	$("#updateButton").attr("className","e_dis");
}
function openCouponsQuotaConfigInfo(audit_order_id,row_id){
	row_id=row_id.replace(/\+/g,"%2B");
	$('#popup').css('display','');
	if(row_id ==""){
		
		$.beginPageLoading("正在查询数据...");
		$.ajax.submit('','onInitTrade',params,'editPart',function(data){
			$('#AUDIT_ORDER_ID').attr("readOnly",false);
			$('#AUDIT_ORDER_ID').attr("disabled",false);
			$('#AREA_CODE').attr("readOnly",false);
			$('#AREA_CODE').attr("disabled",false);
			$.endPageLoading();
		},
		function(error_code,error_info){
			$.endPageLoading();
			alert(error_info);
		});
		$("#AUDIT_ORDER_ID").val("");
		$("#TOTAL_AMOUNT").val("");
		
		$('#addButton').attr("disabled",false);
		$("#addButton").attr("className","e_button-page-ok");
		
		$('#updateButton').attr("disabled",true);
		$("#updateButton").attr("className","e_dis e_button-page-ok");
		

	}else{
		$('#addButton').attr("disabled",true);
		$("#addButton").attr("className","e_dis e_button-page-ok");
		
		$('#updateButton').attr("disabled",false);
		$("#updateButton").attr("className","e_button-page-ok");
		$('#AUDIT_ORDER_ID').attr("readOnly",true);
		$('#AUDIT_ORDER_ID').attr("disabled",true);
		$('#AREA_CODE').attr("readOnly",true);
		$('#AREA_CODE').attr("disabled",true);
		var params = "&OPER_TYPE=UPDATE&AUDIT_ORDER_ID=" + audit_order_id+"&ROW_ID="+row_id;
		$.beginPageLoading("正在查询数据...");
		$.ajax.submit('','queryCouponsQuotaInfo',params,'editPart',function(data){

			$.endPageLoading();
		},
		function(error_code,error_info){
			$.endPageLoading();
			alert(error_info);
		});
	}

}
function updateCouponsQuotaConfigInfo(str){
	
	if(!$.validate.verifyAll("editPart")) {
		return false;
	}

	var params = "&OPER_TYPE="+str
	
	$.beginPageLoading("正在更新数据...");
	$.ajax.submit('editPart','operCouponsQuotaConfig',params,'',function(data){
		if(data.get("SUCC_FLAG") && "1"==data.get("SUCC_FLAG"))
		{
			alert("数据更新成功！");
		}else{
			alert("数据更新失败！");
		}
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
	});
}
function deleteCouponsQuotaConfigInfo(audit_order_id,row_id){
	
	row_id=row_id.replace(/\+/g,"%2B");
	var params = "&OPER_TYPE=DELETE&AUDIT_ORDER_ID="+audit_order_id+"&ROW_ID="+row_id;
	
	$.beginPageLoading("正在删除数据...");
	$.ajax.submit('','operCouponsQuotaConfig',params,'',function(data){
		if(data.get("SUCC_FLAG") && "1"==data.get("SUCC_FLAG"))
		{
			alert("数据更新成功！");
		}else{
			alert("数据更新失败！");
		}
		$.endPageLoading();
		qryCouponsQuotaInfo();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
	});
}