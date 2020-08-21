function initPageParam_110022000020()
{
	debugger;
	/*  var  offerType = $("#cond_OPER_TYPE").val();
	  if("CrtMb"==offerType){
		  $("#reloadNa").closest("div").css("display", "none");
		  $("#VPMN_CRM").closest("li").css("display", "none");
		  $("#VPMN_CRM").closest("li").removeClass("link required");
		  $("#VPMN_CRM").attr("nullable", "yes");
	  }*/
    var netThypeCode =  $("#NET_TYPE_CODE").val();
    if(netThypeCode == "05")
    {
        $('#pam_OuterCall').closest("li").css('display','');
        $('#pam_OuterCall').attr("nullable", "no");
    }
    else if(netThypeCode == "00")
    {
        $('#pam_OuterCall').closest("li").css('display','none');
        $('#pam_OuterCall').attr("nullable", "yes")
    }

    //政企一单清特殊处理
	if($("#SET_OFFERCHA_SOURCE").val() == "EcIntegrateOrder")
	{//删除短号输入框，在号码导入时录入短号
		$("#pam_SHORT_CODE").attr("nullable", "yes");
		$("#pam_SHORT_CODE").closest("li").css('display','none');
		$("#pam_SHORT_CODE").val("#SHORT_CODE#"); //先录入一个特殊标识，数据转换时进行替换
	}
}


//短号验证
function validateImsShortNum(obj) {
    var shortCode = $("#pam_SHORT_CODE").val();
    var shortCodeTmp = $.trim(shortCode); //去掉空格
    var user_id = $("#GRP_USER_ID").val();

    if(shortCode==""||shortCode==undefined||shortCode ==null){
        $.validate.alerter.one($("#pam_SHORT_CODE")[0],"短号码为空，请输入后再验证！");
        $("#pam_SHORT_CODE").focus();
        return false;
    }
    if(shortCode != shortCodeTmp)
    {
        $.validate.alerter.one($("#pam_SHORT_CODE")[0],'短号['+shortCode+']含有空格，请去掉!');
        return false;
    }

    var shortNumber = $("#pam_OLD_SHORT_CODE").val();
    if(shortNumber == shortCode )
    {
        $.validate.alerter.one($("#pam_SHORT_CODE")[0],"短号未修改,无需校验!");
        $("#pam_SHORT_CODE").focus();
        return false;
    }

    if (shortCode.length < 3 || shortCode.length > 6){
        $.validate.alerter.one($("#pam_SHORT_CODE")[0],"短号长度只能为3~6，请检查!");
        $("#pam_SHORT_CODE").focus();
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
	var shortCode = data.get("SHORT_CODE");
	var result = data.get("RESULT");  
 
	if(result==true||result=="true"){ 
		$.validate.alerter.one($("#pam_SHORT_CODE")[0],"验证通过，录入的短号码可以使用！");
		$("#pam_OLD_SHORT_CODE").val(shortCode);
	}else{
		$('#pam_SHORT_CODE').val("");
		$.validate.alerter.one($("#pam_SHORT_CODE")[0],data.get("ERROR_MESSAGE"));
	}
}


//提交
function checkSub(obj)
{
	/*var shortC = $("#pam_SHORT_CODE").val();
	if(shortC ==""||shortC==undefined ||shortC ==null){
		$.validate.alerter.one($("#pam_SHORT_CODE")[0],"短号码未验证，请验证！");
		return false;
	}*/
    var  offerType = $("#cond_OPER_TYPE").val();
    if(!validateParamPage(offerType))
        return false;
	if(!submitOfferCha())
		return false; 
	
	backPopup(obj);
}
function validateParamPage(methodName)
{
	//政企一单清特殊处理
	if($("#SET_OFFERCHA_SOURCE").val() == "EcIntegrateOrder")
	{//此处不校验短号
		return true;
	}
	
    var shortNumber = $("#pam_OLD_SHORT_CODE").val();
    var shortNumberInput = $("#pam_SHORT_CODE").val();
    if (shortNumber)
    {
        if (shortNumber != shortNumberInput)
        {
            $.validate.alerter.one($("#pam_SHORT_CODE")[0],"请先验证短号码！");
            return false;
        }
    }else{
        $.validate.alerter.one($("#pam_SHORT_CODE")[0],"请先验证短号码！");
        return false;
	}
    return true;
}