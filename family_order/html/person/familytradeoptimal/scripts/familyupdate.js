function refreshPartAtferAuth(data)
{
	var userId = data.get('USER_INFO').get('USER_ID');
	var serialNumber = data.get('USER_INFO').get('SERIAL_NUMBER');
	var param = '&USER_ID='+userId;
	param += '&SERIAL_NUMBER=' + serialNumber;
	param += '&EPARCHY_CODE=' + data.get('USER_INFO').get('EPARCHY_CODE');
	param += '&USER_PRODUCT_ID=' + data.get('USER_INFO').get('PRODUCT_ID');
	
	$.ajax.submit(null, 'loadInfo', param, 'FamilyInfoPart,viceInfopart',loadInfoSuccess,loadInfoError);
}
function loadInfoSuccess(data){
	
}

function loadInfoError(code,info){
	$.cssubmit.disabledSubmitBtn(true);
	MessageBox.error("错误",info,function(){
		$.redirect.toPage('familytradeoptimal.FamilyUpdate');
	});
}

function onTradeSubmit()
{
	var strupdateNow = 'false';
	var updateNow = $('#updateNowCheckBox').attr('checked');
	if(updateNow){
	   strupdateNow = 'true';
   	}
	var param = '&SERIAL_NUMBER='+$("#AUTH_SERIAL_NUMBER").val();
	param += '&updateNowCheckBox='+strupdateNow;
	param += '&REMARK='+$('#REMARK').val();
	
	$.cssubmit.addParam(param);
	
	return true;
}