$(function(){
	if(typeof(eval(window.top.getCustorInfo))=="function"){
		var sn = window.top.getCustorInfo();
		var param = "&SERIAL_NUMBER="+sn;
		ajaxSubmit(null, 'getTradeTypeCode', param, '',function(data){
		state_code = data.get(0).get('STATE_CODE');
		main_tag = data.get(0).get('MAIN_TAG');
		service_id = data.get(0).get('SERVICE_ID');
		if(main_tag && main_tag == '1'){
			if(state_code == '0'){
				$("#TRADE_TYPE_CODE").val('131');
				$("#Stop").removeClass("e_dis");
				$("#Open").addClass("e_button-page-ok e_dis");
			}else{
				$("#TRADE_TYPE_CODE").val('133');
				$("#Open").removeClass("e_dis");
				$("#Stop").addClass("e_button-page-ok e_dis");
			}
		}
	},function(errorcode, errorinfo){
		$.endPageLoading();
	});
	}
})

function stopMobile(){
	$("#TRADE_TYPE_CODE").val("131");
	$("#Stop").removeClass().addClass("e_button-page-ok");
	$("#Open").removeClass().addClass("e_button-page-ok e_dis");
	$.auth.autoAuth();
}
function openMobile(){
	$("#TRADE_TYPE_CODE").val("133");
	$("#Open").removeClass().addClass("e_button-page-ok");
	$("#Stop").removeClass().addClass("e_button-page-ok e_dis");
	$.auth.autoAuth();
}


