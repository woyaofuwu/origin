$(document).ready(function(){
	initValue();
});

function initValue()
{
	$("#VALID_MEMBER_NUMBER_AREA").css("display","none");
	$('#VALID_MEMBER_NUMBER').val("");
}

function refreshPartAtferAuth(data)
{
	var userId = data.get('USER_INFO').get('USER_ID');
	var serialNumber = data.get('USER_INFO').get('SERIAL_NUMBER');
	var param = '&SERIAL_NUMBER=' + serialNumber;
	$.ajax.submit(null, 'loadInfo', param, 'UUInfoPart',loadInfoSuccess,loadInfoError);
}
function loadInfoSuccess(data){
	var valideMemberNumber = data.get("VALIDE_MEBMER_NUMBER");
	if(valideMemberNumber&&valideMemberNumber!="-1"){
		$("#VALID_MEMBER_NUMBER_AREA").css("display","");
		$("#VALID_MEMBER_NUMBER").val(valideMemberNumber);
	}else{
		$("#VALID_MEMBER_NUMBER_AREA").css("display","none");
		$("#VALID_MEMBER_NUMBER").val("0")
	}
}

function loadInfoError(code,info){
	$.cssubmit.disabledSubmitBtn(true);
	$.showErrorMessage("错误",info);
}

function onTradeSubmit()
{
	var param = '&SERIAL_NUMBER='+$("#AUTH_SERIAL_NUMBER").val();
	param += '&REMARK='+$('#REMARK').val();
	var data = $.table.get("UUInfoTable").getTableData();
	if(data.length == 0) {
		alert("您没有进行任何操作，不能提交！");
		return false;
	}
	param += '&MEB_LIST='+data;
	$.cssubmit.addParam(param);
	
	return true;
}

function clickRow()
{	
	var rowData = $.table.get("UUInfoTable").getRowData();
	$('#MEB_SERIAL_NUMBER').val(rowData.get('SERIAL_NUMBER_B'));
	$('#OLD_SHORT_CODE').val(rowData.get('SHORT_CODE_B'));
	$('#NEW_SHORT_CODE').val('');
}

function changeShortCode()
{
	if(!$.validate.verifyAll("MEB_SHORT_CHANGE")){
		return false;
	}
	
	var table = $.table.get("UUInfoTable");
	var rowData = table.getRowData();
	var newShortCode = $('#NEW_SHORT_CODE').val();
	var list = table.getTableData(null,true);
	for(var i = 0, size = list.length; i < size; i++){
		var tmp = list.get(i);
		var shortCode = tmp.get("SHORT_CODE_B");
		if(shortCode == newShortCode){
			alert("短号["+newShortCode+"]已经存在，请重新选择！");
			return false;
		}
	}
	
	rowData['SHORT_CODE_B'] = newShortCode;
	table.updateRow(rowData);
	
	$('#OLD_SHORT_CODE').val($('#NEW_SHORT_CODE').val());
	$('#NEW_SHORT_CODE').val('');
}