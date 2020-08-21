var paramInfos = new Wade.DataMap();
function initPageParam_120010122806(){
	$("#UserCFUInfo").val(paramInfos.get("UserCFUInfo"));

}

function checkSub(obj){

	var userCfu = $("#UserCFUInfo").val();
	paramInfos.put("UserCFUInfo",userCfu);
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