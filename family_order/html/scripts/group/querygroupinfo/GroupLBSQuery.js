 function qryClick(){	
 	var serialNumb = $("#cond_SERIAL_NUMBER_B").val();
 	var serialNum = $("#cond_SERIAL_NUMBER").val();
 	
 	if("" == serialNumb && "" == serialNum){
 		alert("产品编码和手机号码不能同时为空！");
 		return;
 	}
 	$.beginPageLoading("查询中......");
    $.ajax.submit('QueryCondPart','queryInfos', '&SERIAL_NUMBER_B='+serialNumb+'&SERIAL_NUMBER='+serialNum,'groupENetInfo,refreshHintBar', function(data){
		$.endPageLoading(); 
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
    
 }