function renderGroupSerialNumber(productId,eparchyCode){
	var resTypeCode = $('#GROUP_SERIALNUMBER_RESTYPE').val();
	var param = '&PRODUCT_ID='+productId +'&EPARCHY_CODE='+eparchyCode+'&RES_TYPE_CODE='+resTypeCode;
	Wade.httphandler.submit('','com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.groupserialnumber.GroupSerialNumberHttpHandler','createGrpSn',param,
    	function(data){
    		if(data == null){
    			cleanGroupSerialNumberPart();
    			var aftererrorAction = $("#GROUP_SERIALNUMBER_ERROR_ACTION").val();
			    if (aftererrorAction != '') {
			        eval(""+aftererrorAction);
			    }
    			
    		}
    		insertGroupSerialNumberPart(data);
    		var afterAction = $("#GROUP_SERIALNUMBER_ACTION").val();
		    if (afterAction != '') {
		        eval(""+afterAction);
		    }
    		
    	},
		function(error_code,error_info,derror){
			cleanGroupSerialNumberPart();
			var aftererrorAction = $("#GROUP_SERIALNUMBER_ERROR_ACTION").val();
		    if (aftererrorAction != '') {
		        eval(""+aftererrorAction);
		    }
			showDetailErrorInfo(error_code,error_info,derror);
		});
}

function insertGroupSerialNumberPart(data){
	var ifResCode = data.get("IF_RES_CODE");
	$('#IF_RES_CODE').val(ifResCode);
	
	var sn = data.get("SERIAL_NUMBER");
	var hiddenSn = data.get("HIDDEN_SERIAL_NUMBER");
	var resTypeCode = data.get("RES_TYPE_CODE");
	if(ifResCode && ifResCode == "0" ){
		$('#SERIAL_NUMBER').val(sn);
		$('#SERIAL_NUMBER').attr('disabled',true);
		$('#RES_TYPE_CODE').val(resTypeCode);
		$('#HIDDEN_SERIAL_NUMBER').val(hiddenSn);
	}else{
		$('#SERIAL_NUMBER').attr('disabled',false);
	}
}

function cleanGroupSerialNumberPart(){
	
	$('#IF_RES_CODE').val('');
	$('#SERIAL_NUMBER').val('');
	$('#RES_TYPE_CODE').val('');
	$('#HIDDEN_SERIAL_NUMBER').val('');

}


function initGroupSerialNumber() {
	var ifResCode = $("#IF_RES_CODE").val();
	
	if(ifResCode && ifResCode == "0") {
		var serialNumber = $("#SERIAL_NUMBER").val();
		var resTypeCode = $("#RES_TYPE_CODE").val();
		
		if(serialNumber == null || serialNumber == "") return;
		
		var resData = $.DataMap();
	    resData.put("RES_TYPE_CODE", resTypeCode);
	    resData.put("RES_CODE", serialNumber);
	    resData.put("CHECKED","true");
	    resData.put("DISABLED","true");
		insertRes(resData);
		$("#HIDDEN_SERIAL_NUMBER").val(serialNumber);
	}
}

function checkSerialNumber(serialNumberObj) {

    var serialNumber = $(serialNumberObj).val();
    $(serialNumberObj).attr('disabled',true);//解决连点回车事件问题
    var hiddenSerialNumber = $('#HIDDEN_SERIAL_NUMBER').val();
    var resTypeCode = $("#RES_TYPE_CODE").val();
    var grpUserEparchyCode =$('#GROUP_SERIALNUMBER_EPARCHY_CODE').val();
    
	$.beginPageLoading();
	var param = "&IS_CHECK=" + true + "&SERIAL_NUMBER=" + serialNumber +'&GRP_USER_EPARCHYCODE='+grpUserEparchyCode+ "&PRODUCT_ID=" + $("#GRP_PRODUCT_ID").val() + "&RES_TYPE_CODE=" + resTypeCode;
	$.ajax.submit(null, null, param, $("#GROUP_SERIALNUMBER_ID").val() + ".GroupSNPart", 
		function(data){
			$.endPageLoading();
			afterCheckSerialNumber(data, resTypeCode, serialNumber);
		},
		function(error_code,error_info,derr){
			$.endPageLoading();
			$(serialNumberObj).attr('disabled',false);
			showDetailErrorInfo(error_code,error_info,derr);
		}
	);
	
    return false;
}

function afterCheckSerialNumber(data, resTypeCode, serialNumber) {
	var resultCode = data.get("X_RESULTCODE", "-1")
	var resultInfo = data.get("X_RESULTINFO", "");
	
	if(resultCode == "0") {
		var hiddenSerialNumber = $('#HIDDEN_SERIAL_NUMBER').val();
		
		if(hiddenSerialNumber && hiddenSerialNumber != "") {
			if(hiddenSerialNumber == serialNumber) {
				return;
			}
			var deleObj = $.DataMap();
			deleObj.put("RES_TYPE_CODE", resTypeCode);
		    deleObj.put("RES_CODE", hiddenSerialNumber);
		    deleObj.put("CHECKED","true");
		    deleObj.put("DISABLED","true");
		    deleteRes(deleObj);
     	}
		
		var addObj = $.DataMap();
	 	addObj.put("RES_TYPE_CODE", resTypeCode);
		addObj.put("RES_CODE", serialNumber);
		addObj.put("CHECKED", "true");
		addObj.put("DISABLED", "true");
		insertRes(addObj);
		
		$("#HIDDEN_SERIAL_NUMBER").val(serialNumber);
		
		alert(resultInfo);
	} else {
  		alert(resultInfo);
  	}
}

function isCheckedSerialNumber() {
	var serialNumber = $('#SERIAL_NUMBER').val();
	var hiddenSerialNumber = $('#HIDDEN_SERIAL_NUMBER').val();
	
	if(hiddenSerialNumber == null || hiddenSerialNumber == "" || (serialNumber != hiddenSerialNumber)){
		alert("服务号码未校验,请先校验服务号码!");
		$('#SERIAL_NUMBER').focus();
		return false;
	}
	return true;
}