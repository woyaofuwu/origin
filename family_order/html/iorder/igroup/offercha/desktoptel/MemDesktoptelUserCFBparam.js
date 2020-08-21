var paramInfos = new Wade.DataMap();
function initPageParam_120010122807(){
	$("#UserCFBInfo").val(paramInfos.get("UserCFBInfo"));

}

function checkSub(obj){

	var userCfb = $("#UserCFBInfo").val();
	paramInfos.put("UserCFBInfo",userCfb);
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