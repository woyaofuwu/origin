// 集团资料查询成功后调用的方法
function selectGroupAfterAction(data){
	// 填充集团客户显示信息
	var cust_id = data.get("CUST_ID");
	$.ajax.submit('','queryTradeInfo', '&CUST_ID='+cust_id,'tradeInfo', function(data){
		$.endPageLoading(); 
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
}

// 集团资料查询失败后调用的方法
function selectGroupErrorAfterAction(){
	// 清空填充的集团客户信息内容
	alert("查询失败！");
    clearGroupCustInfo();
}


function successMessage(data){ 
	$.showSucMessage(data.get("RESULTINFO"));
}


function submitGroupNetInfo(){
	var tradeInfo = $("#pam_NOTIN_TRADE_CODE").val();
	var tradeInfos = tradeInfo.split("||");
	var tradeIds = tradeInfos[0].split(":");
	var tradeId = tradeIds[1];
	
	$.beginPageLoading();
	$.ajax.submit('','submitGroupNetInfo','&TRADE_ID='+tradeId,'groupNetInfo', function(data){
		$.endPageLoading(); 
		successMessage(data);
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
    
}

function queryGroupNetInfo(){
	var tradeInfo = $("#pam_NOTIN_TRADE_CODE").val();
	var tradeInfos = tradeInfo.split("||");
	var tradeIds = tradeInfos[0].split(":");
	var tradeId = tradeIds[1];
	var productName = tradeInfos[1];
	var grpProductCode = tradeInfos[2];
	var acceptDate =  tradeInfos[3];
	
	$("#pam_NOTIN_TRADE_ID").val(tradeId);
	 
	$.ajax.submit('','queryGroupNetInfo', '&TRADE_ID='+tradeId,'groupNetInfo', function(data){
		$.endPageLoading(); 
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });

}

