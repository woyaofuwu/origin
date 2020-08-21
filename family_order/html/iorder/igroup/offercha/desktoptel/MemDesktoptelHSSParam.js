var paramInfos = new Wade.DataMap();
function initPageParam_120000008172(){
	$("#HSS_ROAM_ID").val(paramInfos.get("HSS_ROAM_ID"));
	$("#HSS_AUTH_TYPE").val(paramInfos.get("HSS_AUTH_TYPE"));
}

function checkSub(obj){
	
	var hssRoamId = $("#HSS_ROAM_ID").val();
	var hssAuthType = $("#HSS_AUTH_TYPE").val();
	paramInfos.put("HSS_ROAM_ID",hssRoamId);
	paramInfos.put("HSS_AUTH_TYPE",hssAuthType);
	
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