function queryReporterBlack() {
	if(!verifyAll('editInfoPart'))
   	{
	   return false;
   	}
	$.beginPageLoading("信息查询中..");
     $.ajax.submit('editInfoPart', 'queryReporterBlack', null, 'infoTablePart,TipInfoPart', function(data){
		$.endPageLoading();
		if(data.get("ALERT_INFO") != null) {
			$("#TipInfoPart").css("display","block");
		}else {
			$("#TipInfoPart").css("display","none");
		}
	},
	
	function(error_code,error_info,detail){
		$.endPageLoading();
		MessageBox.error("错误提示", error_info, null, null, null, detail);
    });
}

function addBlack() {
	if(!verifyAll('dealBlackPart'))
   	{
	   return false;
   	}
	$.beginPageLoading("信息处理中..");
     $.ajax.submit('dealBlackPart', 'addBlack', null, 'dealBlackPart', function(data){
		$.endPageLoading();
		$.showSucMessage("新增成功");
	},
	
	function(error_code,error_info,detail){
		$.endPageLoading();
		MessageBox.error("错误提示", error_info, null, null, null, detail);
    });
}

function addBlack() {
	if(!verifyAll('dealBlackPart'))
   	{
	   return false;
   	}
	$.beginPageLoading("信息处理中..");
     $.ajax.submit('dealBlackPart', 'addBlack', null, 'dealBlackPart', function(data){
		$.endPageLoading();
		$.showSucMessage("新增成功");
	},
	
	function(error_code,error_info,detail){
		$.endPageLoading();
		MessageBox.error("错误提示", error_info, null, null, null, detail);
    });
}

function doAction(listener) {
	var message;
	if('addBlack' == listener) {
		message = "新增成功";
	}else if('updBlack' == listener) {
		message = "更新成功";
	}else if('delBlack' == listener) {
		message = "删除成功";
	}
	
	//if('delBlack' != listener) {
		if(!verifyAll('dealBlackPart'))
	   	{
		   return false;
	   	}
	   	
	   	var serialNumber = $("#cond_NEW_SERIAL_NUMBER").val();
	    if(!(serialNumber.length == 8 || serialNumber.length == 11)) {
	    	alert('新增手机号码格式不正确!');
	    	return false;
	    }
	//}
	
	$.beginPageLoading("信息处理中..");
     $.ajax.submit('dealBlackPart', listener, null, 'dealBlackPart', function(data){
		$.endPageLoading();
		$.showSucMessage(message);
	},
	
	function(error_code,error_info,detail){
		$.endPageLoading();
		MessageBox.error("错误提示", error_info, null, null, null, detail);
    });
}