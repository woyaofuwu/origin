var paramInfos = new Wade.DataMap();
function initPageParam_120010122809(){
	$("#userCFNLInfo").val(paramInfos.get("userCFNLInfo"));
}

function checkSub(obj){
	var userCfnl = $("#userCFNLInfo").val();
	paramInfos.put("userCFNLInfo",userCfnl);
	var result = submitOfferCha();
	if(result==true){
		backPopup(obj);
	}
	try {
		//backPopup(obj);
	} catch (msg) {
			$.error(msg.message);
	}

	
}