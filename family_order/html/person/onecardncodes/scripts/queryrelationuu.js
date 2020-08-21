


function reset(){
	$('#relatinfoPart :input').val('');
}


/**查询时调用*/
function queryRelationUUInfos() {
	if(!verifyAll()) {
		return false;
	}
	$.beginPageLoading("查询中..");
     $.ajax.submit('relatinfoPart', 'queryRelationInfos', null, 'relatinoTablePart',function(data){
		$.endPageLoading();
		data.get(0).get("IS_EMPTY_ID");
		var str = data.get(0).get("ISSHOW");
		
		if(str=='0') {
			$("#tipss").text("该客户的两个服务号码是绑定付费关系！");
			$("#TipInfoPart").css("display","block");
		}else {
			$("#tipss").text("该客户的两个服务号码不是绑定付费关系！");
			$("#TipInfoPart").css("display","block");
		}
	},
	
	function(error_code,error_info,detail){
		$.endPageLoading();
		$('#relatinfoPart input').val('');
		$("#TipInfoPart").css("display","none");
		$("#relationuuTableInfos").html('');
		MessageBox.error("错误提示", error_info, null, null, null, detail);
    });
}

