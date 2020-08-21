

/**查询时调用*/
function querySimCardInfos() {
	if(!verifyAll()) {
		return false;
	}
	$.beginPageLoading("查询中..");
     $.ajax.submit('siminfoPart', 'querySimcardInfos', null, 'simcardTablePart',function(data){
		$.endPageLoading();
		//data.get(0).get("fadfasd");
		//$("#id").val("aaaaaa");
		//CHOOSE_TYPE
		data.get(0).get("SIM_TYPE_CODE");
		data.get(0).get("ISSHOW");

		if(data.get("ALERT_CODE") != null) {
			$("#TipInfoPart").css("display","block");
		}else {
			$("#TipInfoPart").css("display","none");
		}
	},
	
	function(error_code,error_info,detail){
		$.endPageLoading();
		$("#simcardTableInfos").html('');
		MessageBox.error("错误提示", error_info, null, null, null, detail);
    });
}

function reset(){
	$('#siminfoPart input').val('');
	$("#simcardTableInfos").html('');

}