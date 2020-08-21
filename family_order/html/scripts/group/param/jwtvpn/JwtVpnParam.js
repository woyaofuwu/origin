function createserialnumber() {
	var work_type_code = $('#pam_WORK_TYPE_CODE').val();
	
	if(null == work_type_code || "" == work_type_code || work_type_code =="-----选择-----"){
		alert("\u0056\u0050\u004D\u004E\u96C6\u56E2\u7C7B\u578B\u4E0D\u80FD\u4E3A\u7A7A\uFF01");
		return false;
	} 
	var serialNumber = $('#SERIAL_NUMBER').val();
	if(serialNumber != "" && serialNumber != null)	{
		var del = $.DataMap();
		del.put("RES_TYPE_CODE", "L");
		del.put("RES_CODE",serialNumber);
		deleteRes(del);
	}
	
	var userEpachyCode = $('#GRP_USER_EPARCHYCODE').val();
	Wade.httphandler.submit('','com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.dynamichtml.ProxyParam','productParamInvoker',
    		'&WORK_TYPE_CODE='+work_type_code+'&METHOD_NAME=createSerialNumber&CLASS_NAME=com.asiainfo.veris.crm.order.web.group.param.jwtvpn.UserParamInfo&GRP_USER_EPARCHY_CODE='+userEpachyCode,afterValidchk,errafterCheck);
    		
}

function afterValidchk(data) {
	$.endPageLoading();
	
	var vpnNo = data.get("VPN_NO");
	
	var addData = $.DataMap();
	addData.put("RES_TYPE_CODE", "L");
	addData.put("RES_CODE", vpnNo);
	addData.put("CHECKED", "true");
	addData.put("DISABLED", "true");
	insertRes(addData);
	
	$("#SERIAL_NUMBER").val(vpnNo);
	$("#HIDDEN_SERIAL_NUMBER").val(vpnNo);
}

function errafterCheck(e,i)
{   
	$.endPageLoading();
    $.showErrorMessage(e+":"+i);
}


function Changeinput(obj){    
	if(obj==1){
//		ajaxDirect4CS('group.param.vpn.MemParamInfo', 'creatshortcode', '&USER_ID='+$('#USER_ID").val(), null, false, GetSHORT_CODE);
		 Wade.httphandler.submit('','com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.dynamichtml.ProxyParam','productParamInvoker',
		    		'&MEB_USER_ID='+$('#MEB_USER_ID').val()+'&GRP_USER_ID='+$('#GRP_USER_ID').val()+'&METHOD_NAME=creatshortcode&CLASS_NAME=com.asiainfo.veris.crm.order.web.group.param.vpn.MemParamInfo',creatShortCodeAfter,errafterCheck);
		    		
		
	}
	else{
		var shortNumber = $('#SHORT_NUMBER_PARAM_INPUT').val();
		if( shortNumber !=""){
			del = $.DataMap();
			del.put("RES_TYPE_CODE","S");
			del.put("RES_CODE",shortNumber);
			deleteRes(del);
		}
		
		$('#pam_SHORT_CODE').attr('disabled',false);
		$('#pam_SHORT_CODE').val('');
		$('#validButton').css('display','');
	}
}
function creatShortCodeAfter(data){
	$.endPageLoading();
	var result = data.get("RESULT"); 
	if(result=='true'){
		GetSHORT_CODE(data.get("SHORT_CODE"));
	}else{   
	   var message=data.get("ERROR_MESSAGE");
	   $.showWarnMessage(message);  
	}
}

function GetSHORT_CODE(shortCode){
	var SHORT_CODE = shortCode;
	$('#pam_SHORT_CODE').val(SHORT_CODE);
	if ($('#pam_SHORT_CODE').val()== "") {
		$('#q1a1').checked = false;
		$('#q1a1').attr('disabled',true);
		$('#q1a2').checked = true;
		$('#validButton').css('display','');
		alert("\u81EA\u52A8\u751F\u6210\u77ED\u53F7\u7801\u5931\u8D25\uFF0C\u8BF7\u624B\u52A8\u8F93\u5165\uFF01");
	} else {
		$('#pam_SHORT_CODE').attr('disabled',true);
		$('#validButton').css('display','none');
		var shortNumber = $('#SHORT_NUMBER_PARAM_INPUT').val();
		if(shortNumber != "" && shortNumber != null){ 
			var del = $.DataMap();
			del.put("RES_TYPE_CODE","S");
			del.put("RES_CODE",shortNumber);
			deleteRes(del);
		} 
		var obj =$.DataMap();
		obj.put("RES_TYPE_CODE","S");
		obj.put("RES_CODE",SHORT_CODE);
		obj.put("CHECKED","true");
		obj.put("DISABLED","true");
		insertRes(obj);
		$('#SHORT_NUMBER_PARAM_INPUT').val(SHORT_CODE); 
	}
}
  
function displayShort(shortCode){ 
		var shortNumber = $('#SHORT_NUMBER_PARAM_INPUT').val();
		var shortNumberInput = $('#pam_SHORT_CODE').val()
		
		if(shortNumber != "" && shortNumber != null)	{
			if(shortNumber != shortNumberInput) {
				var del = $.DataMap();
				del.put("RES_TYPE_CODE","S");
				del.put("RES_CODE",shortNumber);
				deleteRes(del);
			}
		}
		var obj =$.DataMap();
		obj.put("RES_TYPE_CODE","S");
		obj.put("RES_CODE",shortCode);
		obj.put("CHECKED","true");
		obj.put("DISABLED","true");
		insertRes(obj);
		$('#SHORT_NUMBER_PARAM_INPUT').val(shortCode);
}


function validateShortNum(obj) {  
	var shortCode = $('#pam_SHORT_CODE').val();
	var shortNumber = $('#pam_NOTIN_OLD_SHORT_CODE').val();
	if(shortCode != "" && shortNumber != shortCode) {
		var shortNumber = $('#SHORT_NUMBER_PARAM_INPUT').val();
		if( shortNumber !=""){
			del = $.DataMap();
			del.put("RES_TYPE_CODE","S");
			del.put("RES_CODE",shortNumber);
			deleteRes(del);
		}
		var shortCode = $('#pam_NOTIN_OLD_SHORT_CODE').val();
		if( shortNumber !=""){
			del = $.DataMap();
			del.put("RES_TYPE_CODE","S");
			del.put("RES_CODE",shortCode);
			deleteRes(del);
		}
		if(verifyField(obj))
		{
			$.beginPageLoading(); 
//			ajaxDirect4CS('group.param.vpn.MemParamInfo', 'validchk', '&SHORT_CODE='+obj.val(), null,false,afterValidateShortNum);
			Wade.httphandler.submit('','com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.dynamichtml.ProxyParam','productParamInvoker',
		    		'&SHORT_CODE='+obj.val()+'&GRP_USER_ID='+$('#GRP_USER_ID').val()+'&MEB_EPARCHY_CODE='+$('#MEB_EPARCHY_CODE').val()+'&METHOD_NAME=validchk&CLASS_NAME=com.asiainfo.veris.crm.order.web.group.param.vpn.MemParamInfo',afterValidateShortNum,errafterCheck);
		}
	}else{
		$.showWarnMessage("提示","未做短号修改，不需要验证！");  
	}
	
}

function afterValidateShortNum(data) { 
	$.endPageLoading();
	var shortCode = data.get("SHORT_CODE");
	var result = data.get("RESULT");  
 
	if(result=='true'){    
		$.showSucMessage("提示","效验成功");
		displayShort(shortCode);
	}else{   
		$('#pam_SHORT_CODE').val('');
		focus($('#pam_SHORT_CODE'));
	    var message=data.get("ERROR_MESSAGE");
	    $.showWarnMessage("错误提示",message);  
	}
}

function initMemParamInfo() {
	var METHOD_NAME = $('#METHOD_NAME').val();
   	var RIGHT_CODE = $('#RIGHT_CODE').val();
   	if(METHOD_NAME == "CrtMb" && RIGHT_CODE == "yes") {
   		Changeinput(1);
   	}
   	
}


function validateParamPage(method){
	var isValidateShortCode = false;
	var METHOD_NAME = $('#METHOD_NAME').val();
	var RIGHT_CODE = $('#RIGHT_CODE').val();
	if((METHOD_NAME == "CrtMb" || METHOD_NAME == 'ChgMb') && RIGHT_CODE == "yes") {
		if($('#DYNATABLE_RES_RECORD').val()=='[]'){
			alert('短号码没有校验,请点击校验按钮!')
		}else{
			isValidateShortCode = true;
		}
	}else{
		isValidateShortCode = true;
	}
	return isValidateShortCode;
	
}

function resertShortCode(){
	var shortNumber = $('#SHORT_NUMBER_PARAM_INPUT').val();
	if(shortNumber != "" && shortNumber != null)	
	{
		var del = $.DataMap();
		del.put("RES_TYPE_CODE","S");
		del.put("RES_CODE",shortNumber);
		deleteRes(del);
	}
	var shortCode = $('#pam_NOTIN_OLD_SHORT_CODE').val();
	$('#pam_SHORT_CODE').val(shortCode);
	$('#SHORT_NUMBER_PARAM_INPUT').val(shortCode);
	if(shortCode != "" && shortCode != null)	
	{
		var obj =$.DataMap();
		obj.put("RES_TYPE_CODE","S");
		obj.put("RES_CODE",shortCode);
		obj.put("CHECKED","true");
		obj.put("DISABLED","true");
		insertRes(obj);
	}
	//$("#DYNATABLE_RES_RECORD").val($("#DYNATABLE_RES_LIST").val());
	var oldList = $.DatasetList($("#DYNATABLE_RES_LIST").val());//过滤掉MODIFY_TAG为EXIST的资源信息
	oldList.each(function(item,index,totalcount){
		if (item.get("MODIFY_TAG") == "EXIST"){
			oldList.removeAt(index);
		}
	});
	$("#DYNATABLE_RES_RECORD").val(oldList.toString());
	
}
