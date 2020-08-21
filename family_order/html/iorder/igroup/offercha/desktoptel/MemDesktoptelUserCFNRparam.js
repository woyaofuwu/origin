var paramInfos = new Wade.DataMap();
function initPageParam_120010122808(){
	$("#UserCFNRInfo").val(paramInfos.get("UserCFNRInfo"));
}

function checkSub(obj){
	var userCfnr = $("#UserCFNRInfo").val();
	paramInfos.put("UserCFNRInfo",userCfnr);
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