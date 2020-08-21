

//业务提交
function submitBeforeCheck()
{	
	var  openStopType=$("#OPEN_STOP_TYPE").val();
	//alert("openStopType:"+openStopType+",TRADE_TYPE_CODE:"+$("#TRADE_TYPE_CODE").val());
	if(openStopType == ''){
		alert("报停\报开类型：不能为空");
		return false;
	}
	
	var param = "&SERIAL_NUMBER="+$.auth.getAuthData().get("USER_INFO").get("SERIAL_NUMBER");
	$.cssubmit.addParam(param);
    return true;
}

function  changOpenStopType(){
	var  openStopType=$("#OPEN_STOP_TYPE").val();
	if(openStopType == '0'){
		//报停
		$("#TRADE_TYPE_CODE").val('9830');
	}else if(openStopType == '1'){
		//报开
		$("#TRADE_TYPE_CODE").val('9831');
	}
}