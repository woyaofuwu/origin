//查询订单信息
function queryTradeInfo()
{
   if(checkQueryTradeInfo()) {
	   if(!verifyAll('QueryCondPart'))
	   {
		   return false;
	   }
	  	$.beginPageLoading("数据查询中..");
	     $.ajax.submit('QueryCondPart', 'queryTradeInfo', null, 'TradeTablePart',function(data){
			$.endPageLoading();
		},
		function(error_code,error_info){
			$.endPageLoading();
			alert(error_info);
			window.location.href = window.location.href; 
	    });
	}
 }

 function checkPboss(){
 	
	var rowData = $.table.get("tradeTable").getRowData();
	
	var subType = rowData.get("SUBSCRIBE_TYPE");
	
	var param = "&SUBSCRIBE_TYPE="+subType;
	$.ajax.submit('','checkSubscribeType',param,'',function(data){
		resultCode = data.get("RESULT_CODE");
		if("1" == resultCode){
			$("#pbosstradPf").removeAttr("disabled");
			$("#tradPf").attr("disabled",true);
		}else{
			$("#pbosstradPf").attr("disabled",true);
			$("#tradPf").removeAttr("disabled");
		}
	},
	function(error_code,error_info){
		alert(error_info);
	});
	
 }

 function checkQueryTradeInfo()
 {
 	var result = true;
 	
 	if(($("#cond_ORDER_ID").val() == "" || $("#cond_ORDER_ID").val() == null) && ($("#cond_TRADE_ID").val() == "" || $("#cond_TRADE_ID").val() == null)&& ($("#cond_SERIAL_NUMBER").val() == "" || $("#cond_SERIAL_NUMBER").val() == null) ) {
		alert('服务号码/主订单标识/子订单标识不能同时为空!');
		$("#cond_SERIAL_NUMBER").focus();
		return false;
	}	
	return result;
 }
 

function queryError() {
	//获取选择行的数据
	var rowData = $.table.get("tradeTable").getRowData();
	
	if("{}" == rowData){
		alert("请先单击选中一条记录");
		return false;
	}
	
	var orderId = rowData.get("ORDER_ID");
	var tradeId = rowData.get("TRADE_ID");
	var eparchyCode = rowData.get("EPARCHY_CODE");
	var routeId = rowData.get("ROUTE_ID");
	var inputData =new Wade.DataMap();
	inputData.put("TRADE_ID",tradeId);
	inputData.put("ROUTE_ID",routeId);
	inputData.put("ORDER_ID",orderId);
	inputData.put("EPARCHY_CODE",eparchyCode);
	var param = "&TRADE_TABLE=" + inputData;
	$.beginPageLoading("数据查询中..");
 	$.ajax.submit('TradeTablePart', 'qryErrorMsg', param, 'errorMsgPart', function(data){
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}


function tradPf() {
	//获取选择行的数据
	var rowData = $.table.get("tradeTable").getRowData();
	
	if("{}" == rowData){
		alert("请先单击选中一条记录");
		return false;
	}
	
	var orderstate = rowData.get("ORDER_STATE");
	var tradestate = rowData.get("SUBSCRIBE_STATE");
	var orderId = rowData.get("ORDER_ID");
	var tradeId = rowData.get("TRADE_ID");
	var subType = rowData.get("SUBSCRIBE_TYPE");
	
	
	if((orderstate == "3") || (tradestate == "6") || (tradestate == "M") || ('300' == subType)) {
		var param = "&TRADE_TABLE=" + rowData;
		$.beginPageLoading("工单状态修改中..");
	 	$.ajax.submit('TradeTablePart', 'tradePfAgain', param, 'errorMsgPart', function(data){
			$.endPageLoading();
			alert('工单状态修改成功,等待AEE完工![ORDER_ID=' + orderId + ',TRADE_ID=' + tradeId + ']');
		},
		function(error_code,error_info){
			$.endPageLoading();
			alert(error_info);
	    });
	}else {
		alert('sorry,此状态工单暂不支持重跑!!');
	}
}
