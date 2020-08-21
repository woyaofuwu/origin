var paramInfos = new Wade.DataMap();
function initPageParam_120010122819(){
	$("#MEMB_WAKE_TIMES").val(paramInfos.get("MEMB_WAKE_TIMES"));
	$("#MEMB_WAKE_NUMBER").val(paramInfos.get("MEMB_WAKE_NUMBER"));
	$("#MEMB_WAKE_TIME").val(paramInfos.get("MEMB_WAKE_TIME"));
}

function checkSub(obj){
	var membTime = $("#MEMB_WAKE_TIMES").val();
	var membNumber = $("#MEMB_WAKE_NUMBER").val();
	var membTimeNew=new Date(membTime).format('yyyyMMddHHmmss');
	paramInfos.put("MEMB_WAKE_TIMES",membTime);
	paramInfos.put("MEMB_WAKE_NUMBER",membNumber);
	var result = submitOfferCha();
	if(result==true){
		paramInfos.put("MEMB_WAKE_TIME",membTimeNew);
		backPopup(obj);
	}
	try {
		//backPopup(obj);
	} catch (msg) {
			$.error(msg.message);
	}

	
}