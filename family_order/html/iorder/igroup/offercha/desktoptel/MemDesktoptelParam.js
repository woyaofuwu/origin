function initPageParam_110000222201(){
    var offerTpye = $("#cond_OPER_TYPE").val();
	if($("#IF_SHORT_CODE").val()=='yes'||offerTpye=='CrtMb'){
        $("#pam_SHORT_CODE").attr("disabled",false);
	}else{
        $("#pam_SHORT_CODE").attr("disabled",true);
	}
	
	//政企一单清特殊处理
	if($("#SET_OFFERCHA_SOURCE").val() == "EcIntegrateOrder")
	{//删除短号输入框，在号码导入时录入短号
		$("#pam_SHORT_CODE").closest("li").find("button").css("display", "none");
		$("#pam_SHORT_CODE").val("#SHORT_CODE#"); //先录入一个特殊标识，数据转换时进行替换
	}
}


//短号验证
function validateShortNum() {  	
	var shortCode = $("#pam_SHORT_CODE").val();
    var shortCodeTmp = $.trim(shortCode); //去掉空格  
    //var meb_eparchy_code = $("#MEB_EPARCHY_CODE").val();

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
	debugger;
	var shortCode = data.get("SHORT_CODE");
	var result = data.get("RESULT");  
 
	if(result==true||result=="true"){ 
		$.validate.alerter.one($("#pam_SHORT_CODE")[0],"短号码可以使用！");
		$("#pam_OLD_SHORT_CODE").val(shortCode);
		short =2;
	}else{   
		$('#pam_SHORT_CODE').val("");
		$.validate.alerter.one($("#pam_SHORT_CODE")[0],data.get("ERROR_MESSAGE"));
	}
}


function checkSub(obj){
	var offerTpye = $("#cond_OPER_TYPE").val();
	if(!validateParamPage(offerTpye))
		return false;
	if(!submitOfferCha())
		return false; 
	backPopup(obj);
}

function validateParamPage(methodName) 
{
	if (methodName=='CrtUs')
	{
    	var vpn_infos = $("#VPN_INFOS").val();
    	var vpn_no = $("#pam_VPN_NO").val();
    	if(vpn_infos != "" && vpn_infos != null)	
    	{
    		if(vpn_no == "") 
    		{
    			alert ("\u8BF7\u9009\u62E9\u0056\u0050\u004E\u7F16\u7801\uFF01");
				return false;
    		}
    	}
	}
	if(methodName == "CrtUs" || methodName == "ChgUs") 
	{
		var userNum = $("#pam_BGUserNum").val();
		var fixUserNum = $("#pam_BGFixUserNum").val();
		var mobUserNum = $("#pam_BGMobileUserNum").val();
		var num = parseInt(fixUserNum)+parseInt(mobUserNum);
		if(userNum<num) 
		{
			alert ("\u56FA\u5B9A\u96C6\u56E2\u6210\u5458\u6570\u4E0E\u79FB\u52A8\u96C6\u56E2\u6210\u5458\u6570\u4E4B\u548C\u4E0D\u80FD\u5927\u4E8E\u96C6\u56E2\u6210\u5458\u6570\uFF0C\u8BF7\u91CD\u65B0\u586B\u5199\uFF01");
			return false;
		}
	}
	else if(methodName == "CrtMb") 
	{
    	var old_short_code = $("#pam_OLD_SHORT_CODE").val(); 
    	var short_code = $("#pam_SHORT_CODE").val(); 

		if(old_short_code)
		{
			if(old_short_code != short_code)
			{
				//alert("\u8BF7\u5148\u9A8C\u8BC1\u6210\u5458\u77ED\u53F7\uFF01");
				$.validate.alerter.one($("#pam_SHORT_CODE")[0],"请先验证成员短号！");	
				$("#pam_SHORT_CODE").focus();
				return false;
			}
		}
		else
		{
			//alert("\u8BF7\u5148\u9A8C\u8BC1\u6210\u5458\u77ED\u53F7\uFF01");
			$.validate.alerter.one($("#pam_SHORT_CODE")[0],"请先验证成员短号！");
			$("#pam_SHORT_CODE").focus();
			return false;
		}
	}
    return true;
}