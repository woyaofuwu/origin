function batprintfast(){ 
	var conSerialNumber = $("#cond_SERIAL_NUMBER").val();
	var queryedSerialNumber = $("#queryed_SERIAL_NUMBER").val();
	
	if(queryedSerialNumber =='' || queryedSerialNumber == null )
	{
		queryedSerialNumber = conSerialNumber;
	}
	
	
	if( queryedSerialNumber != conSerialNumber)
	{
		alert("输入服务号码与查询到的服务号码信息不一致，请刷新资料！");
		return false;
	} else {

		var tradeId = $("#cond_bindInfos").val();

		$.beginPageLoading("补录中......");
		$.ajax.submit('QueryCondPart','redirectToMsgBox', '&TRADE_ID='+tradeId,'groupNetInfo,refreshHintBar', function(data){
			successMessage(data);
			$.endPageLoading(); 
		},
		function(error_code,error_info,derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
	    });

	}
}

function successMessage(data){ 
	var result = data.get(0);
	MessageBox.alert("集团业务免填单补录成功","业务流水号:"+result.get("TRADE_ID"),function(btn){
	});
}

function qryClick(obj){
	var serialNum = $("#cond_SERIAL_NUMBER").val();
	var staffId = $("#cond_STAFF_ID").val();
	if(serialNum == null || serialNum == "")
	{
		alert("请输入服务号码！");
		return false;
	}
 	var startDate = $("#cond_START_DATE").val(); 
 	var endDate = $("#cond_END_DATE").val(); 
 	
 	if(startDate == null || startDate == "")
 	{
 		alert("查询开始时间不能为空！");
 		return false;
 	}
 	if(endDate == null || endDate == "")
 	{
 		alert("查询结束时间不能为空！");
 		return false;
 	}
 
	//校验起始日期范围
	var range = "31";
	var startArray = startDate.split("-");
	var endArray = endDate.split("-");
	var dateStart = new Date(startArray[0],startArray[1],startArray[2]);
	var endStart = new Date(endArray[0],endArray[1],endArray[2]);
	
	var day = (endStart.getTime() - dateStart.getTime()) / (1000*60*60*24) ;
	if(day > range){
		MessageBox.alert("状态", "【起始、终止】日期时间段不能超过"+range+"天~");
		return false;
	}
	
	var resultFlag = $.validate.queryAll(obj);
	if(!resultFlag){
		return false;
	}
	
	$.beginPageLoading("查询中......");
	$.ajax.submit('QueryCondPart','queryPrints', '&SERIAL_NUMBER='+serialNum+'&STAFF_ID='+staffId+'&START_DATE='
    +startDate+'&END_DATE='+endDate,'groupNetInfo,refreshHintBar', function(data){
		$.endPageLoading(); 
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
    
}



function reset(){
	$("#cond_SERIAL_NUMBER").val("");
	$("#cond_STAFF_ID").val("");
	$("#cond_START_DATE").val(""); 
 	$("#cond_END_DATE").val(""); 
}
