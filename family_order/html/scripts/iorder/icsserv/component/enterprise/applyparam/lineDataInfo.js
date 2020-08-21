function initPageParam(){
	
	var productId = $("#cond_OFFER_CODE").val(); 
	var hiddenId = $("#OFFER_CHA_HIDDEN_ID").val();
	var index = hiddenId.split("_").pop();
	var portContactA = index+"PORTACONTACT"; //A端联系人
	var portContactPhoneA = index+"PORTACONTACTPHONE";//A端联系人电话
	$("#NOTIN_PORT_CONTACT_A").val($("#"+portContactA).text());	
	$("#NOTIN_PORT_CONTACT_PHONE_A").val($("#"+portContactPhoneA).text());
	if("7012" == productId||"70121" == productId||"70122" == productId){
		var portContactZ = index+"PORTZCONTACT"; //A端联系人
		var portContactPhoneZ = index+"PORTZCONTACTPHONE";//A端联系人电话
		$("#NOTIN_PORT_CONTACT_Z").val($("#"+portContactZ).text());	
		$("#NOTIN_PORT_CONTACT_PHONE_Z").val($("#"+portContactPhoneZ).text());
	}
	

}

//提交
function checkSub(obj)
{
	if(!$.validate.verifyAll("offerChaPopupItem")){
		return false;
	}

	var productId = $("#cond_OFFER_CODE").val();
	var hiddenId = $("#OFFER_CHA_HIDDEN_ID").val();
	var index = hiddenId.split("_").pop();
	
	var portContactA = index+"PORTACONTACT"; //A端联系人
	var portContactPhoneA = index+"PORTACONTACTPHONE";//A端联系人电话
	$("#"+portContactA).text($("#NOTIN_PORT_CONTACT_A").val());
	$("#"+portContactPhoneA).text($("#NOTIN_PORT_CONTACT_PHONE_A").val());
	
	if("7012" == productId||"70121" == productId||"70122" == productId){
		var portContactZ = index+"PORTZCONTACT"; //A端联系人
		var portContactPhoneZ = index+"PORTZCONTACTPHONE";//A端联系人电话
		$("#"+portContactZ).text($("#NOTIN_PORT_CONTACT_Z").val());
		$("#"+portContactPhoneZ).text($("#NOTIN_PORT_CONTACT_PHONE_Z").val());
	}
	backPopup(obj);
}


