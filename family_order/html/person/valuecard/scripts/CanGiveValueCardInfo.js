function qryGiveValueCardInfo(){
	$.beginPageLoading("正在查询数据...");
	$.ajax.submit('QueryCondPart,recordNav','queryCanGiveValueCardInfos','','QueryListPart',
			function(){
					$.endPageLoading();
			}, 
			function(error_code, error_info,detail){
				$.endPageLoading();
				alert(error_info);
			});
}

function addCanGiveConfigInfo(){
	$('#popup').css('display','');
	$('#updateButton').attr("disabled",true);
	$("#updateButton").attr("className","e_dis");
}
function openCanGiveConfigInfo(staff_id,row_id){
	row_id=row_id.replace(/\+/g,"%2B");
	$('#popup').css('display','');
	if(row_id ==""){
		//日期控件需要重新初始化
		$.beginPageLoading("正在查询数据...");
		$.ajax.submit('','onInitTrade',params,'editPart',function(data){
//			$('#AUDIT_ORDER_ID').attr("readOnly",false);
//		    $('#AUDIT_ORDER_ID').attr("disabled",false);
//			$('#AREA_CODE').attr("readOnly",false);
//			$('#AREA_CODE').attr("disabled",false);
			$.endPageLoading();
		},
		function(error_code,error_info){
			$.endPageLoading();
			alert(error_info);
		});
		
		$('#addButton').attr("disabled",false);
		$("#addButton").attr("className","e_button-page-ok");
		
		$('#updateButton').attr("disabled",true);
		$("#updateButton").attr("className","e_dis e_button-page-ok");
	}else{

		$('#addButton').attr("disabled",true);
		$("#addButton").attr("className","e_dis e_button-page-ok");
		
		$('#updateButton').attr("disabled",false);
		$("#updateButton").attr("className","e_button-page-ok");
		
		var params = "&OPER_TYPE=UPDATE&STAFF_ID=" + staff_id+"&ROW_ID="+row_id;
		$.beginPageLoading("正在查询数据...");
		$.ajax.submit('','queryCanGiveValueCardInfo',params,'editPart',function(data){

			$.endPageLoading();
		},
		function(error_code,error_info){
			$.endPageLoading();
			alert(error_info);
		});
	}

}
function updateCanGiveConfigInfo(str){
	
	if(!$.validate.verifyAll("editPart")) {
		return false;
	}

	var params = "&OPER_TYPE="+str
	
	$.beginPageLoading("正在更新数据...");
	$.ajax.submit('editPart','operCanGiveValueCardConfig',params,'',function(data){
		if(data.get("SUCC_FLAG") && "1"==data.get("SUCC_FLAG"))
		{
			alert("数据更新成功！");
			hideLayer('popup');
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
function deleteCanGiveConfigInfo(staff_id,row_id){
	
	row_id=row_id.replace(/\+/g,"%2B");
	var params = "&OPER_TYPE=DELETE&STAFF_ID="+staff_id+"&ROW_ID="+row_id;
	
	$.beginPageLoading("正在删除数据...");
	$.ajax.submit('','operCanGiveValueCardConfig',params,'',function(data){
		if(data.get("SUCC_FLAG") && "1"==data.get("SUCC_FLAG"))
		{
			alert("数据更新成功！");
		}else{
			alert("数据更新失败！");
		}

		$.endPageLoading();
		qryGiveValueCardInfo();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
	});
}
function hideLayer(optionID) {
	document.getElementById(optionID).style.display = "none";
	$.endPageLoading();
	qryGiveValueCardInfo();
}