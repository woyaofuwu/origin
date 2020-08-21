function initPageParam_110000806001() {
	debugger;
	var methodName = $("#cond_OPER_TYPE").val();
	if(methodName=="CrtMb")
	{
		$('#resetButton').css("display","none");
	}else{
		$("#OPERATE_ID").closest('li').css("display","none");
		$("#pam_SHORT_CODE").attr("disabled",false);
		if($("#RIGHT_CODE_chg").val()!='yes'){
			$('#resetButton').css("display","none");
		}
	}
	$('#OPERATE_ID').val("1");
	initMemParamInfo();
}

function Changeinput(obj){    
	if(obj==1){
		 $.httphandler.post("com.asiainfo.veris.crm.iorder.web.igroup.offercha.jwtvpn.JwtvpnHandler", "creatshortcode",'&MEB_USER_ID='+$('#cond_USER_ID').val()+'&GRP_USER_ID='+$('#cond_EC_USER_ID').val(), function(data){
				$.endPageLoading();
				creatShortCodeAfter(data);
				},    
				function(error_code,error_info,derror){
				$.endPageLoading();
				showDetailErrorInfo(error_code,error_info,derror);
		    });
	}
	else{
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
	   MessageBox.error("错误提示",message);
	}
}

function GetSHORT_CODE(shortCode){
	var SHORT_CODE = shortCode;
	$('#pam_SHORT_CODE').val(SHORT_CODE);
	if ($('#pam_SHORT_CODE').val()== "") {
		$('#OPERATE_ID').val("0");
		$('#validButton').css('display','');
		$.validate.alerter.one($("#pam_SHORT_CODE")[0],"自动生成短号码失败，请手动输入！");
	} else {
		$('#pam_SHORT_CODE').attr('disabled',true);
		$('#validButton').css('display','none');
		$('#SHORT_NUMBER_PARAM_INPUT').val(SHORT_CODE); 
	}
}
  
function displayShort(shortCode){ 
	$('#SHORT_NUMBER_PARAM_INPUT').val(shortCode);
}


function validateShortNum(obj) {  
	var shortCode = $('#pam_SHORT_CODE').val();
	//var shortNumber = $('#pam_NOTIN_OLD_SHORT_CODE').val();
	var shortNumber = $('#SHORT_NUMBER_PARAM_INPUT').val();
	if(shortCode != "" && shortNumber != shortCode) {
		if($.validate.verifyField(obj))
		{
			$.beginPageLoading(); 
			$.httphandler.post("com.asiainfo.veris.crm.iorder.web.igroup.offercha.jwtvpn.JwtvpnHandler", "validchk",'&SHORT_CODE='+obj.val()+'&GRP_USER_ID='+$('#cond_EC_USER_ID').val()+'&MEB_EPARCHY_CODE='+$('#cond_USER_EPARCHY_CODE').val(), function(data){
				$.endPageLoading();
				afterValidateShortNum(data);
				},    
				function(error_code,error_info,derror){
				$.endPageLoading();
				showDetailErrorInfo(error_code,error_info,derror);
		    });
		}
	}else{
		MessageBox.alert("验证提示","未做短号修改，不需要验证！");
	}
	
}

function afterValidateShortNum(data) { 
	$.endPageLoading();
	var shortCode = data.get("SHORT_CODE");
	var result = data.get("RESULT");  
 
	if(result=='true'){    
		MessageBox.success("提示","校验成功！");
		displayShort(shortCode);
	}else{   
		$('#pam_SHORT_CODE').val('');
		focus($('#pam_SHORT_CODE'));
	    var message=data.get("ERROR_MESSAGE");
	    MessageBox.error("错误提示",message);  
	}
}

function initMemParamInfo() {
	var METHOD_NAME = $('#cond_OPER_TYPE').val();
   	var RIGHT_CODE = $('#RIGHT_CODE').val();
   	if(METHOD_NAME == "CrtMb" && RIGHT_CODE == "yes") {
   		Changeinput(1);
   	}
   	
}


function validateParamPage(method){
	//var METHOD_NAME = $('#METHOD_NAME').val();
	var METHOD_NAME = $("#cond_OPER_TYPE").val();
	var RIGHT_CODE = $('#RIGHT_CODE').val();
	
	var shortNumber = $('#SHORT_NUMBER_PARAM_INPUT').val();
	var shortNumberInput = $('#pam_SHORT_CODE').val();
	if((METHOD_NAME == "CrtMb" || METHOD_NAME == 'ChgMb') && RIGHT_CODE == "yes") {
		if(shortNumber==''||shortNumber!=shortNumberInput){
			$.validate.alerter.one($("#validButton")[0], "短号码没有校验,请点击校验按钮!\n");
			return false;
		}
	}

	//防止多传数据
    $("#OPERATE_ID").val("");
    $("#OLD_PARAM").val("");
    $("#METHOD_NAME").val("");
    $("#RIGHT_CODE").val("");
    $("#SHORT_NUMBER_PARAM_INPUT").val("");
    $("#RIGHT_CODE_chg").val("");
    $("#USER_ID").val("");
    if(METHOD_NAME == "CrtMb"){
        $("#pam_NOTIN_OLD_SHORT_CODE").val("");
    }
	return true;
	
	
}

function resertShortCode(){
	var shortCode = $('#pam_NOTIN_OLD_SHORT_CODE').val();
	$('#pam_SHORT_CODE').val(shortCode);
	$('#SHORT_NUMBER_PARAM_INPUT').val(shortCode);
	
}

//提交
function checkSub(obj)
{
	var methodName = $("#cond_OPER_TYPE").val();
	if(!validateParamPage(methodName))
		return false; 
	if(!submitOfferCha())
		return false; 
	backPopup(obj);
}