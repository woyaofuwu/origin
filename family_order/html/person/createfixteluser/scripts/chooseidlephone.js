/*$Id: ChooseIdlePhone.js,v 1.2 2013/04/26 07:10:32 chenzm3 Exp $*/

function querySerialMobile(){
		//查询条件校验
	if(!$.validate.verifyAll("QueryPhonePart")) {
		return false;
	}
	var areaCode = $("#AREA_CODE").val();
	var tradeEparchyCode = $("#TRADE_EPARCHY_CODE").val();
	if(areaCode == tradeEparchyCode){
		alert('请选择异地地州进行空闲号码查询！');
		return false;
	}
	
	$.ajax.submit('QueryPhonePart', 'queryIDlePhone', null, 'PhonePart', function(data){
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}

function setReturnValue2(idleValue){
	cancel(true);
	parent.document.getElementById('userinfo_SERIAL_NUMBER').value=idleValue;
	parent.document.getElementById('userinfo_SERIAL_NUMBER').focus();
}
