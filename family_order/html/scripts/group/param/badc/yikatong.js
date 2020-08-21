function init() {   
	var obj = $('#SYSTEM_INTEGRATION');
	var methodName = $('#PRODUCTPARAM_METHOD_NAME').val();
	 $("#SYSTEM_INTEGRATION_FEE").val("0");
	if( methodName=="CrtUs" ){
		obj.css('display','');
		$('#SYSTEM_INTEGRATION_FEE').attr('nullable','no');
	}else{
		obj.css('display','none');
		obj.attr('nullable','yes');
		$('#SYSTEM_INTEGRATION_FEE').attr('nullable','yes');
	} 
}

function validateParamPage(methodName) { 
	if (methodName=='CrtUs')
	{ 
	    var pay_money = $('#SYSTEM_INTEGRATION_FEE').val();
		return creatMoney(pay_money);   
	}  
    return true;
}

// j2ee $.feeMgr 方法调用有问题，后期再测
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
	// 更新付费列表 结束
	return true;
}

 
