var isOK = 0;

function initPageParam_120000009013()
{
	debugger;
	if("0"!=$("#pam_MODIFY_TAG").val()||isOK==1){
		$("#pam_SI_BASE_IN_CODE").closest("li").css("display","none");
		$("#pam_SI_BASE_IN_CODE").css("display","none");
		$("#pam_SECEND").css("display","none");
		$("#pam_CHOOSE").css("display","none");
		$("#pam_BIZ_ATTR_TYPE").attr('disabled',true);
		$("#pam_BIZ_SERV_MODE").attr('disabled',true);

		$("#pam_BIZ_IN_CODE").closest("li").css("display","");
		$("#pam_BIZ_IN_CODE").css("display","");
	}
}

//提交
function checkSub(obj)
{
	var bizInCode = $("#pam_BIZ_IN_CODE").val();
	if(bizInCode==""||bizInCode=="undefined"){
		$.validate.alerter.one($("#SVR_CODE_END_SECEND")[0],"请补全基本接入号！");
		return false;
	}
	
	var passl = checkSignExist();
	if(!passl)
		return false;
	
	//将多余的参数置0,多余参数不如表,保持和原数据一致
	$("#pam_SERVICE_ID").val("");
	$("#pam_MODIFY_TAG").val("");
	$("#pam_SI_BASE_IN_CODE").val("");
	$("#pam_SVRCODETAIL").val("");
	
	if(!submitOfferCha())
		return false; 

	isOK = 1;
	
	backPopup(obj);
}

function createBizCodeExtend(){
	$.httphandler.post("com.asiainfo.veris.crm.iorder.web.igroup.offercha.wlw.WlwParamHandler", "createBizCodeExtend","", function(data){
		$.endPageLoading();
		servCodeFlag = afterCreateBizCodeExtend(data);
		},    
		function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
}

function afterCreateBizCodeExtend(data){
	  var extendCode = data.get(0,"EXTEND_CODE");
	  if (extendCode == "" || extendCode == null){
		  MessageBox.alert("","自动生成错误，请手动输入!");
	  }else{
	     $("#pam_SVRCODETAIL").val(extendCode);
	     checkAccessNumber();
	  }
}

function checkAccessNumber(){
	if ($("#pam_SVRCODETAIL").val()=="") {
		return;
	}
    var bizInCode = $("#pam_SI_BASE_IN_CODE").val();
    var svcCode = $("#pam_SVRCODETAIL").val();
    var length = svcCode.length;

   var accessNumber = bizInCode+svcCode;
   
   var groupInfo = $.enterpriseLogin.getInfo().get("GROUP_INFO");
   var groupId = groupInfo.get("GROUP_ID","");
   
   $.httphandler.post("com.asiainfo.veris.crm.iorder.web.igroup.offercha.wlw.WlwParamHandler", "getDumpIdByajax",'&GROUP_ID='+groupId+'&ACCESSNUMBER='+accessNumber, function(data){
		$.endPageLoading();
		servCodeFlag = afterCheckAccessNumber(data,accessNumber);
		},    
		function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
   });
}

/**校验服务代码AJAX刷新后的处理*/
function afterCheckAccessNumber(data,accessNumber){
	var flag = data.get(0,"ISCHECKAACCESSNUMBER");
	if(flag == "true"){
		$.validate.alerter.one($("#SVR_CODE_END_SECEND")[0],"服务代码可以使用！");
		$('#pam_BIZ_IN_CODE').val(accessNumber.replace(/(^\s*)|(\s*$)/g, ""));
		$("#SVR_CODE_END_SECEND").val($("#pam_SVRCODETAIL").val());
	}else{
		$.validate.alerter.one($("#SVR_CODE_END_SECEND")[0],"服务代码["+accessNumber+"]不能使用，请重新生成！");
		$('#pam_BIZ_IN_CODE').val("");
		$("#SVR_CODE_END_SECEND").val($("#pam_SVRCODETAIL").val());
	}
}

function changeButton(){
	var bizServMode = $("#pam_BIZ_SERV_MODE").val();
	if (bizServMode=='02'){
		$("#pam_BIZ_SERV_CODE").attr('nullable',"no");
		$("#pam_BIZ_SERV_CODE").closest("li").attr("class", "link required");

	}else{
		$("#pam_BIZ_SERV_CODE").attr('nullable',"yes");
		$("#pam_BIZ_SERV_CODE").closest("li").attr("class", "");
	}
}

function afterCheckSignExist(data) {
	var inProduct = data.get('IN_PRODUCT');
	if (inProduct == "true") {
		var hasZh = paramMap.get("HAS_ZH");
		if (hasZh == "true") {
			MessageBox.alert("","对不起，中文签名含有敏感字符[" + paramMap.get("PARAM_NAME") + "]，请重新输入");
			getElement("pam_TEXT_ECGN_ZH").select();
			return false;
		}

		var hasEn = paramMap.get("HAS_EN");
		if (hasEn == "true") {
			MessageBox.alert("","对不起，英文签名含有敏感字符[" + paramMap.get("PARAM_NAME") + "]，请重新输入");
			getElement("pam_TEXT_ECGN_EN").select();
			return false;
		}
	}
	
	return true;
}

/*校验中英文签名是否有敏感字符*/
function checkSignExist() {
	var productId = $("#cond_OFFER_CODE").val();
	var textZH = $("#pam_TEXT_SIGN_ZH").val();
	var textEN = $("#pam_TEXT_SIGN_EN").val();
	
	var pass = false;
	
	$.httphandler.post("com.asiainfo.veris.crm.iorder.web.igroup.offercha.wlw.WlwParamHandler", "getSensitiveTextByajax",'&TEXT_ECGN_ZH='+textZH+'&TEXT_ECGN_EN='+textEN+'&PRODUCT_ID='+productId, function(data){
		$.endPageLoading();
		pass = afterCheckSignExist(data);
		},    
		function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
		},
        {
		  async: false
    });
	
	return pass;
}
