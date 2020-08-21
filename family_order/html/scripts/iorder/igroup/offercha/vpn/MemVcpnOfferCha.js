function initPageParam_110000800101(){
	var offerType = $("#cond_OPER_TYPE").val();
	if("ChgMb"==offerType){
		$("#autoShort").css("display", "none");
	}
}

function  autoShortCode()
{
	var serialNumber = $("#cond_SERIAL_NUMBER_INPUT").val();
	var userId = $("#cond_EC_USER_ID").val();

	$.httphandler.post("com.asiainfo.veris.crm.iorder.web.igroup.vcpn.VcpnHandler", "AutomaticNumber",'&SERIAL_NUMBER='+ serialNumber+'&USER_ID='+userId, function(data){
		$.endPageLoading();
		autoShortCodeNum(data);
		},    
		function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
}

function validateShortNum() 
{       
		//短号码验证
		var shortCode = $("#pam_SHORT_CODE_1").val();
		var userId = $("#cond_EC_USER_ID").val();
		var re = /[^\d]/g;
		if(re.test(shortCode)){
			$.validate.alerter.one($("#pam_SHORT_CODE_1")[0],"短号码只能为数字，请重新输入！");
			return false;
		}
		if( shortCode.length > 6 ) {  
		    $.validate.alerter.one($("#pam_SHORT_CODE_1")[0], "短号码，最多只能输入6个数字!\n");
			return false;
		}
		if(null==shortCode|| "" == shortCode){
			$.validate.alerter.one($("#pam_SHORT_CODE_1")[0], "您输入的短号码为空，请输入后再验证!\n");			
			return false; 
	    }
		$.beginPageLoading("数据加载中......");
		$.httphandler.post("com.asiainfo.veris.crm.iorder.web.igroup.vcpn.VcpnHandler", "superTeleShortNumAdmin",'&SHORT_CODE='+ shortCode+'&USER_ID='+userId, function(data){
			$.endPageLoading();
			sucShortNumRes(data,shortCode);
			},    
			function(error_code,error_info,derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
	    });
}

function sucShortNumRes(data,shortCode)
{
	if("true"== data.get("FLAG"))
	{
		$("#pam_SHORT_CODE").val(shortCode);
		MessageBox.success("验证结果", "恭喜您,短号码"+shortCode+"验证成功!", function(){
		});
	}
	else
	{
		MessageBox.alert("验证提示",data.get("ERROR_MESSAGE"));
	}
}

function autoShortCodeNum(data)
{
	if("true"== data.get("FLAG"))
	{
		$("#pam_SHORT_CODE_1").val(data.get("SHORT_CODE"));
		MessageBox.success("验证结果",data.get("OK_MESSAGE"));
	}
	else
	{
		MessageBox.alert("验证提示",data.get("ERROR_MESSAGE"));
	}
}


function submitVcpnMember(obj)
{
	if(!submitOfferCha())
		return false; 
	var shortCode = $("#pam_SHORT_CODE").val();
	var shortCode1 = $("#pam_SHORT_CODE_1").val();
	
	if(null==shortCode|| "" == shortCode){
		$.validate.alerter.one($("#pam_SHORT_CODE_1")[0], "您没有验证短号码，请验证!\n");			
		return false; 
    }
	
	if(shortCode!=shortCode1){
		$.validate.alerter.one($("#pam_SHORT_CODE_1")[0], "您输入的短号码与验证号码不一致，请重新验证!\n");			
		return false; 
		}

	backPopup(obj);
}