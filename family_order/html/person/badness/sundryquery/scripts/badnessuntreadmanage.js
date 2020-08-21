function queryBadnessInfo() {
	if(!verifyAll('badInfoPart'))
   	{
	   return false;
   	}
	$.beginPageLoading("信息查询中..");
     $.ajax.submit('badInfoPart,badInfoNav', 'queryBadInfos', null, 'badInfoTablePart,TipInfoPart', function(data){
		$("#CSSUBMIT_BUTTON").attr("disabled",false).removeClass("e_dis");
		$.endPageLoading();
		if(data.get("ALERT_INFO") != null) {
			$("#TipInfoPart").css("display","block");
		}else {
			$("#TipInfoPart").css("display","none");
		}
	},
	
	function(error_code,error_info,detail){
		$("#CSSUBMIT_BUTTON").attr("disabled",true).addClass("e_dis");
		$.endPageLoading();
		MessageBox.error("错误提示", error_info, null, null, null, detail);
    });
}

function untreadBadnessInfos() {
	var data = $.table.get("badTable").getCheckedRowDatas();//获取选择中的数据
	if(data == null || data.length == 0) {
		alert('请选择待处理信息后，再继续办理业务!');
		return false;
	}
	
	var param = "&BADNESS_TABLE=" + data
	$.beginPageLoading("业务处理中..");
     $.ajax.submit('phoneInfoPart,dealInfoPart', 'untreadBadnessInfos', param, 'badInfoTablePart,phoneInfoPart,dealInfoPart', function(data){
		$.endPageLoading();
		$.showSucMessage("信息处理成功!");
	},
	
	function(error_code,error_info,detail){
		$.endPageLoading();
		MessageBox.error("错误提示", error_info, null, null, null, detail);
    });
}

function exportBeforeAction(obj) {
	var data = $.table.get("badTable").getTableData("INFO_RECV_ID", true);
	//alert(data);
	if(data.length == 0) {
		alert("请先查询出数据再导出!");
		return false;
	}
	return true;
}