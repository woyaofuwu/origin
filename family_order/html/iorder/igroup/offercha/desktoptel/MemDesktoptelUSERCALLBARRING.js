var paramInfos = new Wade.DataMap();
function initPageParam_120010122813(){
	$("#MEMB_USERCALLBARRING").val(paramInfos.get("MEMB_USERCALLBARRING"));
}

function checkSub(obj){
	var userAll = $("#MEMB_USERCALLBARRING").val();
	paramInfos.put("MEMB_USERCALLBARRING",userAll);
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