/**
 * 
 */
function getUnfinishTrade(obj){
	//查询条件校验
	if(!$.validate.verifyAll("QueryCondPart")) {//先校验已配置的校验属性
		return false;
	}
	$.ajax.submit('QueryCondPart', 'queryUnfinishTrade', null, 'QueryListPart', function(data){
		if(data.get('ALERT_INFO') != '')
		{
			alert(data.get('ALERT_INFO'));
		}
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}

function inputCtrlForStaff(obj){
	staffId = $(obj).val().toUpperCase();
	if(staffId.length > 8){
		obj.value = staffId.substring(0,8);
	} else {
		obj.value = staffId;
	}
}



/*REQ201707210024 未完工单界面功能优化*/
//显示Layer
function displayLayer(obj) {
	var params = "&ORDER_ID="+obj.orderid+"&TRADE_ID="+obj.tradeid+"&SERIAL_NUMBER="+obj.serialnumber+"&TRADE_TYPE_CODE="+obj.tradetypecode;
	
	$.beginPageLoading();
	$.ajax.submit(this, "queryUnfinishTradeTrace", params, "table4TradeTrace,table4PFTrace", function(data){
		/*if(data.get('ALERT_INFO') !=''){
			alert(data.get('ALERT_INFO'));
		}*/
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
	
	$("#popup").css("display", "");
}

// 隐藏Layer
function hiddenLayer() {
	
	$("#popup").css("display", "none");
}