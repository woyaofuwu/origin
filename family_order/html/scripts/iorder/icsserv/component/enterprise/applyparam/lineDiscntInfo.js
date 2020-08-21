function initPageParam(){
	
	var productId = $("#cond_OFFER_CODE").val(); 
	var hiddenId = $("#OFFER_CHA_HIDDEN_ID").val();
	var index = hiddenId.split("_").pop();
	var productNo = index+"ProductNO"; //专线实例号
	var width = index+"width";//带宽
	var price = index+"price";//月租费
	var once = index+"once";//一次性费用（安装调试费）（元）
	var ip = index+"ip";//ip地址使用费
	var soft = index + "soft";//软件应用服务费
	var tech = index + "tech";//技术支持服务费
	var voice = index + "voice";//语音通信费（元/分钟）
	var sla = index +"sla";//SLA服务费（元/月）
	var groupprecent = index +"groupprecent";
	var aprecent = index +"aprecent";
	var zprecent = index +"zprecent";
	$("#NOTIN_LINE_NO").val($("#"+productNo).text());	
	$("#NOTIN_RSRV_STR9").val($("#"+productNo).text());
	$("#NOTIN_RSRV_STR1").val($("#"+width).text());
	$("#NOTIN_RSRV_STR2").val($("#"+price).text());
	$("#NOTIN_RSRV_STR3").val($("#"+once).text());
	if("7011" == productId||"70111" == productId||"70112" == productId){
		$("#NOTIN_RSRV_STR10").val($("#"+ip).text());
		$("#NOTIN_RSRV_STR11").val($("#"+soft).text());
		$("#NOTIN_RSRV_STR12").val($("#"+tech).text());
	}else if ("7010" == productId){
		$("#NOTIN_RSRV_STR15").val($("#"+voice).text());
	}else if("7012" == productId||"70121" == productId||"70122" == productId){
		$("#NOTIN_RSRV_STR16").val($("#"+sla).text());
		$("#NOTIN_RSRV_STR11").val($("#"+soft).text());
		$("#NOTIN_RSRV_STR12").val($("#"+tech).text());
		$("#NOTIN_RSRV_STR6").val($("#"+groupprecent).text());
		$("#NOTIN_RSRV_STR7").val($("#"+aprecent).text());
		$("#NOTIN_RSRV_STR8").val($("#"+zprecent).text());
	}else if("7016" == productId){
		$("#NOTIN_RSRV_STR10").val($("#"+ip).text());
		$("#NOTIN_RSRV_STR11").val($("#"+soft).text());
		$("#NOTIN_RSRV_STR12").val($("#"+tech).text());
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
	var productNo = index+"ProductNO"; //专线实例号
	var width = index+"width";//带宽
	var price = index+"price";//月租费
	var once = index+"once";//一次性费用（安装调试费）（元）
	var ip = index+"ip";//ip地址使用费
	var soft = index + "soft";//软件应用服务费
	var tech = index + "tech";//技术支持服务费
	var voice = index + "voice";//语音通信费（元/分钟）
	var sla = index +"sla";//SLA服务费（元/月）
	var groupprecent = index +"groupprecent";
	var aprecent = index +"aprecent";
	var zprecent = index +"zprecent";
	//alert($("#0ProductNO").html());
	//alert($("#0ProductNO").val());
	//alert($("#"+productNo).val());
	$("#"+width).text($("#NOTIN_RSRV_STR1").val());
	$("#"+price).text($("#NOTIN_RSRV_STR2").val());
	$("#"+once).text($("#NOTIN_RSRV_STR3").val());
	if("7011" == productId||"70111" == productId||"70112" == productId){
		$("#"+ip).text($("#NOTIN_RSRV_STR10").val());
		$("#"+soft).text($("#NOTIN_RSRV_STR11").val());
		$("#"+tech).text($("#NOTIN_RSRV_STR12").val());
	}else if ("7010" == productId){
		$("#"+voice).text($("#NOTIN_RSRV_STR15").val());
	}else if("7012" == productId||"70121" == productId||"70122" == productId){
		$("#"+sla).text($("#NOTIN_RSRV_STR16").val());
		$("#"+soft).text($("#NOTIN_RSRV_STR11").val());
		$("#"+tech).text($("#NOTIN_RSRV_STR12").val());
		$("#"+groupprecent).text($("#NOTIN_RSRV_STR6").val());
		$("#"+aprecent).text($("#NOTIN_RSRV_STR7").val());
		$("#"+zprecent).text($("#NOTIN_RSRV_STR8").val());
	}else if("7016" == productId){
		$("#"+ip).text($("#NOTIN_RSRV_STR10").val());
		$("#"+soft).text($("#NOTIN_RSRV_STR11").val());
		$("#"+tech).text($("#NOTIN_RSRV_STR12").val());
	}
	backPopup(obj);
}


