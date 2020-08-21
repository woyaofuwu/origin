function queryBadnessReleaseInfo() {
	if(!verifyAll('QueryCondPart'))
   	{
	   return false;
   	}
	$.beginPageLoading("信息查询中..");
     $.ajax.submit('QueryCondPart', 'queryBadnessReleaseInfo', null, 'QueryListPart', function(data){
     	$("#CSSUBMIT_BUTTON").attr("disabled",false).removeClass("e_dis");
		$.endPageLoading();
	},
	
	function(error_code,error_info,detail){
		$("#CSSUBMIT_BUTTON").attr("disabled",true).addClass("e_dis");
		$.endPageLoading();
		MessageBox.error("错误提示", error_info, null, null, null, detail);
    });
}

function dealBadnessReleaseInfo() {
	if(!verifyAll('SubPart')) {
	   return false;
   	}
   	
   	var data = $.table.get("QueryListTable").getCheckedRowDatas();//获取选择中的数据
   	
	if(data == null || data.length == 0) {
		alert('请选择待处理信息后，再继续办理业务!');
		return false;
	}
	var inforecvid = "";
	var serialNumber = "";
	var state = "";
	var source = "";
	for(var i=0; i<data.length; i++) {
		if(i == 0) {
			inforecvid = data.get(i).get("CHECK_ID");
			serialNumber = data.get(i).get("SERIAL_NUMBER");
			state = data.get(i).get("HANDLING_STATE");
			source = data.get(i).get("SOURCE_DATA");
		}
		if(i > 0) {
			inforecvid = inforecvid + "," + data.get(i).get("CHECK_ID");
			serialNumber = serialNumber + "," + data.get(i).get("SERIAL_NUMBER");
			state = state + "," + data.get(i).get("HANDLING_STATE");
			source = source + "," + data.get(i).get("SOURCE_DATA");
		}
	}
	
	var param = "&INFO_RECV_ID=" + inforecvid + "&SERIAL_NUMBER=" + serialNumber + "&HANDLING_STATE=" + state + "&SOURCE_DATA=" + source;
	//alert(param);
	$.beginPageLoading("业务处理中..");
     $.ajax.submit('SubPart', 'dealBadnessReleaseInfo', param, 'QueryListPart,SubPart', function(data){
		$.endPageLoading();
		$.showSucMessage("信息处理成功!");
	},
	
	function(error_code,error_info,detail){
		$.endPageLoading();
		MessageBox.error("错误提示", error_info, null, null, null, detail);
    });
    
}