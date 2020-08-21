function initPageParam_110000613001()
{
	var roleCodeB = $("#IMSROLE_CODE_B").val();

	if("3" == roleCodeB){
		$("#pam_SUPERTELNUMBER").attr("disabled", false);
		$("#pam_OPERATORPRIONTY").attr("disabled", false);
	}
	  
}


//短号验证
function validateShortNum() {  	
	var shortCode = $('#pam_SHORT_CODE2').val();
	if(shortCode==""||shortCode==undefined||shortCode ==null){
		$.validate.alerter.one($("#pam_SHORT_CODE2")[0],"短号码为空，请输入后再验证！");
		return false;
	}
	
	var grpUserId = $("#cond_EC_USER_ID").val();
	$.beginPageLoading("数据加载中......");
	$.httphandler.post("com.asiainfo.veris.crm.iorder.web.igroup.offercha.vpmn.SuperteleHandler", "validchk",'&SHORT_CODE='+ shortCode+'&EC_USER_ID='+ grpUserId, function(data){
		$.endPageLoading();
		afterValidateShortNum(data);
		},    
		function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
	
	
}


function afterValidateShortNum(data) { 
	$.endPageLoading();
	debugger;
	var shortCode = data.get("SHORT_CODE");
	var result = data.get("RESULT");  
 
	if(result==true||result=="true"){ 
		$.validate.alerter.one($("#pam_SHORT_CODE2")[0],"短号码可以使用！");
		$("#pam_SHORT_CODE").val(shortCode);
		short =2;
	}else{   
		$('#pam_SHORT_CODE2').val("");
		$.validate.alerter.one($("#pam_SHORT_CODE2")[0],data.get("ERROR_MESSAGE"));
	}
}
//话务员勾选验证
function isSetSuperTelOper(){
	
	var serialNumber = $('#cond_SERIAL_NUMBER_INPUT').val();
	$.beginPageLoading();
	$.httphandler.post("com.asiainfo.veris.crm.iorder.web.igroup.offercha.centrexsupertel.CentrexsupertelParamHandler", "checkSuperTelOper", '&SERIAL_NUMBER='+ serialNumber,function(data){
		$.endPageLoading();
		sucIsSetSuperTelOper(data);
		},    
		function(error_code,error_info,derror){
		$.endPageLoading();
		$.endPageLoading();
		$("#EXCHANGETELE_SN").val("");
	    $("#E_CUST_NAME").val("");
	    $("#E_CUST_ID").val("");
	    $("#E_USER_ID").val("");
	    $("#E_EPARCHY_CODE").val("");
		showDetailErrorInfo(error_code,error_info,derror);
    });
}

function sucIsSetSuperTelOper(data){

	var roleCodeB = $("#IMSROLE_CODE_B").val();
	var obj = $("#pam_IS_SUPERTELOPER");
	var payPlanList = $("input[name='pam_IS_SUPERTELOPER']:checked");
	
	if("true" != data.get("RESULT")){
		
		MessageBox.alert("提示信息",data.get("ERROR_MESSAGE"));
		//var payPlanList = $("#pam_IS_SUPERTELOPER");
		payPlanList.attr("checked",false);
		$("#pam_SUPERTELNUMBER").attr("disabled", true);
		$("#pam_OPERATORPRIONTY").attr("disabled", true);
		
		$("#span_SUPERTELNUMBER").attr("class", "");
		$("#span_OPERATORPRIONTY").attr("class", "");
		
		$("#pam_SUPERTELNUMBER").attr("nullable", "yes");
		$("#pam_OPERATORPRIONTY").attr("nullable", "yes");
		
		$("#ROLE_CODE_B").val("1");
	}else{
			payPlanList.attr("checked",true);
			
			$("#pam_SUPERTELNUMBER").attr("disabled", false);
			$("#pam_OPERATORPRIONTY").attr("disabled", false);
			
			$("#span_SUPERTELNUMBER").attr("class", "e_required");
			$("#span_OPERATORPRIONTY").attr("class", "e_required");
			
			$("#pam_SUPERTELNUMBER").attr("nullable", "no");
			$("#pam_OPERATORPRIONTY").attr("nullable", "no");
			
			$("#ROLE_CODE_B").val("3");
		
	}
}

//提交
function checkSub(obj)
{
	var shortC = $("#pam_SHORT_CODE").val();
	if(shortC ==""||shortC==undefined ||shortC ==null){
		$.validate.alerter.one($("#pam_SHORT_CODE2")[0],"短号码未验证，请验证！");	
		return false;
	}
	if(!submitOfferCha())
		return false; 
	
	backPopup(obj);
}