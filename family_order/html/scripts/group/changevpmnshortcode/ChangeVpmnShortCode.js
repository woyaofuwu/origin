function init() {
	var custName = $('#CUST_NAME').val();
	if(custName ==''){
		$('#bsubmit').attr('disabled','true');	
	}
	else {
		$('#bsubmit').attr('disabled','false');
	}
	
}
function getVpnInfo(){  
    $('#bsubmit').attr('disabled','true');	
    $('#CUST_NAME').val('');
	$('#POST_CODE').val('');
	$('#PHONE').val('');
	$('#HOME_ADDRESS').val('');
	$('#BRAND').val('');
	$('#CITY_NAME').val('');
	$('#PRODUCT_NAME').val('');
	$('#OPEN_DATE').val('');

	 
	if(!$.validate.verifyField($("#cond_SERIAL_NUMBER"))) return false;
	 
	alert ('验证通过！');
	authAfterFunction(); 
	 
}
function authAfterFunction() {

	if(!$.validate.verifyField($("#cond_SERIAL_NUMBER"))) return false;
	
	$.beginPageLoading("");
	$.ajax.submit("queryForm", "queryVpnInfo", null, "parentVpmnPart", 
		function(data){
			$.endPageLoading();
		},
		function(error_code,error_info, derror){
			$.endPageLoading(); 
			showDetailErrorInfo(error_code,error_info,derror);
    	}
    );
}
//USERCHECK查询失败后调用的方法
function userCheckErrAction(state,data) {
	selectMemberInfoErrAfterAction();
	
	if(state == 'USER_CUSTOM'){//网外号码
		$("#GROUP_AUTH_FLAG").val("true");
		queryMemberInfo();
		return;
	}
} 
/////////////////////////
 
function onSubmitBaseTradeCheck(){ 
	var authTag = $("#GROUP_AUTH_FLAG").val();
	if(authTag!= 'true'){
		alert('号码未验证，请验证！');
		groupAuthStart();
		return false;
	}
	if(!$.validate.verifyField($("#NEW_SHORT_CODE"))) return false;  
	$.beginPageLoading('业务验证中....');
	var checkParam = "&SERIAL_NUMBER=" + $("#MEB_SERIAL_NUMBER").val() + "&USER_ID=" + $("#GRP_USER_ID").val() + "&SHORT_CODE="+
					$('#NEW_SHORT_CODE').val()+"&MEB_EPARCHY_CODE="+ $('#MEB_EPARCHY_CODE').val();
	var result = ruleCheck("com.asiainfo.veris.crm.order.web.group.rules.VpmnShortCodeRule", "checkShortCodeRule", checkParam);
	$.endPageLoading();
	return result; 
}


function afterValidateShortNum(data) { 
	alert("luoyong afterValidateShortNum 有用吗？");
	$.endPageLoading(); 
	var result = data.get("RESULT");  
 
	if(result==true||result=='true'){    
//		$.showSucMessage("提示","效验成功"); 
	}else{    
	    var message=data.get("ERROR_MESSAGE");
	    $.showWarnMessage("错误提示",message);  
	    return false;
	}
}
function errafterCheck(e,i)
{   
	alert("luoyong errafterCheck 有用吗？");
	$.endPageLoading();
    $.showErrorMessage(e+":"+i);
    return false;
}
//查询 VPMN信息
function qryVpnInfo(){  
	$.beginPageLoading();
	$.ajax.submit("queryForm", "queryVpnInfo", null, "vpnInfoPart", 
		function(data){
			$.endPageLoading();
		},
		function(error_code,error_info, derror){
			$.endPageLoading(); 
			showDetailErrorInfo(error_code,error_info,derror);
    	}
    );
}
//成员号码资料查询成功后调用的方法
function selectMemberInfoAfterAction(data){
  
  if(data == null)
	return;
	
	//异地号码判断
  if(!afterCheckInfo(data)) 
  	return false;
  var memcustInfo = data.get("MEB_CUST_INFO");
  var memuserInfo = data.get("MEB_USER_INFO"); 
  
  insertMemberCustInfo(memcustInfo);
  insertMemberUserInfo(memuserInfo); 
  
  qryVpnInfo();
}

//成员资料查询失败后调用的方法
function selectMemberInfoAfterErrorAction() {

	clearMemberCustInfo();
	clearMemberUserInfo(); 
}
//判断 异地号码的后续处理
function afterCheckInfo(data){
  var result = data.get("OUT_PHONE","false");;
  if(result == "true"){
  	if(!(confirm("请注意：该号码是异地号码，是否继续成员新增?"))){
  	    //选择取消则退出办理
	    $('#cond_SERIAL_NUMBER').val();
	    selectMemberInfoAfterErrorAction();
	    return false;
	}
  }
  return true;
}