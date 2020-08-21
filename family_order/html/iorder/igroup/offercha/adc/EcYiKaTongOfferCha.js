function initPageParam_110010005743() {   

	var methodName = $('#PRODUCTPARAM_METHOD_NAME').val();
	 
	if( methodName=="CrtUs" ){
		$("#SYSTEM_INTEGRATION_FEE").val("0");
	}else{
		$("#SYSTEM_INTEGRATION_FEE").closest("li").css("display","none");
		$("#SYSTEM_INTEGRATION_FEE").css('display','none');
		$("#SYSTEM_INTEGRATION_FEE").attr('nullable','yes');
	} 
}

function checkSub(obj)
{	

	//多余字段不需要入attr表,需要测试看费用那个值需不需要入attr表
	var methodName = $('#PRODUCTPARAM_METHOD_NAME').val();
    if( methodName=="CrtUs" ){
    	 var pay_money = $('#SYSTEM_INTEGRATION_FEE').val();
    	 creatMoney(pay_money);
	}
	$("#PRODUCTPARAM_METHOD_NAME").val("");
	$('#SYSTEM_INTEGRATION_FEE').val("");
	$("#SYSTEM_INTEGRATION_FEE").attr('nullable','yes');
	
	if(!submitOfferCha())
		return false; 
	
	backPopup(obj);
}

//j2ee $.feeMgr 方法调用有问题，后期再测
function creatMoney(pay_money) {

	if ($.feeMgr) {
		$.feeMgr.clearFee();
	}
	// 更新付费列表 开始
	var obj = $.DataMap();
	obj.put("TRADE_TYPE_CODE", "3660");
	obj.put("MODE", "0");
	obj.put("CODE", "999");
	obj.put("FEE", pay_money * 100);

	$.feeMgr.insertFee(obj);
}
