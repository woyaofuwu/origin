function initPageParam_110010000140()
{
	debugger;
	initCrtUs();
	bindStates();
}

function bindStates(){
	$("#pam_TEXT_ECGN_ZH").bind("keyup",function(){
		var zh=$("#pam_TEXT_ECGN_ZH");
		zh.val(zh.val().replace(/[^\u4E00-\u9FA5]/g,''));
	});
	$("#pam_TEXT_ECGN_ZH").bind("beforepaste",function(){
		clipboardData.setData('text',clipboardData.getData('text').replace(/[^\u4E00-\u9FA5]/g,''));
	});
	
	$("#pam_TEXT_ECGN_EN").bind("keyup",function(){
		var en=$("#pam_TEXT_ECGN_EN");
		en.val(en.val().replace(/[\W]/g,''));
	});
	$("#pam_TEXT_ECGN_EN").bind("beforepaste",function(){
		clipboardData.setData('text',clipboardData.getData('text').replace(/[^\d]/g,''));
	});
}


function showPwdInfo(){
	debugger;
	if($('#PasswdPart').css('display')=="none"){
		$("#PasswdPart").css("display","");
	}else{
		$("#PasswdPart").css("display","none");
		$("#NEW_PASSWD").val('');
		$("#RE_NEW_PASSWD").val('');
	}
	
}

/** 
 * 当输入新密码时的一次校验
 */
function checkPassword1()
{	
	var pass1 = $("#NEW_PASSWD").val();
   	var pass1Lenth = pass1.length;
   	var patrn = /^([+-]?)(\d+)$/;
   	
   	if(!$.validate.verifyField($("#NEW_PASSWD"))) 
   	{
   		$("#NEW_PASSWD").val('');
   		$("#NEW_PASSWD").focus();
   		return false;
   	}
   	
   	if(pass1Lenth != 6)
   	{
   		$.validate.alerter.one($("#NEW_PASSWD")[0], "输入密码长度不等于6,请重新输入！");
    	$("#NEW_PASSWD").val('');
   		$("#NEW_PASSWD").focus();
	  	return false;	  
   	}
  	var isNumber =  patrn.test(pass1);
  	if(isNumber == false)
  	{
  		$.validate.alerter.one($("#NEW_PASSWD")[0], "输入密码必须全为数字！");
    	$("#NEW_PASSWD").val('');
   		$("#NEW_PASSWD").focus();
      	return false;	
  	}
    
    var count=0;
	var count1=0;
	var count2=0;
	
	for(var i=0; i<pass1.length-1; i++)
	{
		if(pass1.charAt(i)*1 == pass1.charAt(i+1))
		{
			count++;
		}
		else if(parseInt(pass1.charAt(i))+1==parseInt(pass1.charAt(i+1)))
		{
			count1++;	
		}
		else if(parseInt(pass1.charAt(i))-1==parseInt(pass1.charAt(i+1)))
		{
			count2++;
		}
	}

	if(count == pass1.length-1 || count1 == pass1.length-1 || count2 == pass1.length-1)
	{
		$.validate.alerter.one($("#NEW_PASSWD")[0], "新服务密码过于简单，请重新输入！");
		$("#NEW_PASSWD").val('');
   		$("#NEW_PASSWD").focus();					
		return false;
	}	
}

/** 
 * 设置完密码点击确定后，进行密码校验且将值返回父页面
 */
function checkPassword()
{
	if(!$.validate.verifyAll('PasswdPart')) return false;
	
	var pass1 = $("#NEW_PASSWD").val();
	var pass2 = $("#RE_NEW_PASSWD").val();
	var passwd;	   
	if (pass1 != pass2)
	{
		$.validate.alerter.one($("#RE_NEW_PASSWD")[0], "前后两次输入密码不一致！");
		return false;
    }
    else
    {
        //$.setReturnValue(['pam_TEMP_PWD',pass2],true);
    	$("#pam_TEMP_PWD").val(pass2);
    	$("#PasswdPart").css("display","none");
    	$("#NEW_PASSWD").val('');
		$("#RE_NEW_PASSWD").val('');
    }
}

function initCrtUs() 
{
	var methodname=$("#cond_OPER_TYPE").val();
	$("#METHOD_NAME").val(methodname);
	var operpart = $("#OperCodePart");
	var prodtype = $("#ProductTypePart");
	var loginname = $("#LoginNamePart");
	var tmppwd = $("#TmpPwdPart");
	if(methodname == "CrtUs")
	{
		operpart.css("display","none");
		$("#pam_BIZ_NAME").attr("disabled","true");
		$("#pam_BILLING_TYPE").attr("disabled","true");
		$("#pam_ACCESS_MODE").attr("disabled","true");
		
		$.httphandler.post("com.asiainfo.veris.crm.iorder.web.igroup.offercha.tisms.SmsHandler", "getBizCodeTail",'', function(data){
			suc(data);
			},    
			function(error_code,error_info,derror){
			err(error_code,error_info,derror);
	    });
	}else if(methodname == "ChgUs")
	{
		$("#validButton").css("display","none");
		prodtype.css("display","none");
		$("#pam_PRODUCT_TYPE").attr("nullable","yes");
		loginname.css("display","none");
		tmppwd.css("display","none");
		$("#pam_SERV_CODE").attr("disabled","true");
		
	    statetodisable();
	}
	
}
function suc(data)
{ 
	$.endPageLoading();
		
	var strSvcCodeTail= data.get("strSvcCodeTail");		
	$("#pam_SERV_CODE").val($("#pam_SERV_CODE").val()+strSvcCodeTail);
}

function err(error_code,error_info,derror)
{
	$.endPageLoading();
	showDetailErrorInfo(error_code,error_info,derror);
}

function checkservcode()
{   
	var servCode = $("#pam_SERV_CODE");	
	
	if(!$.validate.verifyField(servCode)) return false;
	
	var param = "&SERV_CODE=" + servCode.val();
	$.httphandler.post("com.asiainfo.veris.crm.iorder.web.igroup.offercha.tisms.SmsHandler", "checkValidServCode",param, function(data){
		afterValidchk(data);
		},    
		function(error_code,error_info,derror){
		err(error_code,error_info,derror);
    });

}


function afterValidchk(data) 
{
	var servCode = data.get("SERV_CODE"); 
	var result = data.get("RESULT"); 
	if(result=='false') 
	{		
		$.validate.alerter.one($("#pam_SERV_CODE")[0], data.get("ERROR_MESSAGE"));
		$("#SHORT_NUMBER_CODE_INPUT").val("");	
      	$("#pam_SERV_CODE").focus();
      	return false;
	}
	else 
	{
		$("#SHORT_NUMBER_CODE_INPUT").val(servCode);	
		$.validate.alerter.one($("#pam_SERV_CODE")[0], "验证成功！");
	}
}

function  actionOnChgOpertype()
{ 
	var opervalue = $("#pam_OPER_CODE").val();		
    if(opervalue == "71" || opervalue == "72")
    {    
        statetodisable();            
    }
    else if(opervalue == "05")
    { 
         statetoundisable();
    }    
}

function statetodisable()
{      
	$("#pam_BILLING_TYPE").attr("disabled","true");
	$("#pam_BIZ_CODE").attr("disabled","true");
	$("#pam_SERV_CODE").attr("disabled","true");
	$("#pam_ACCESS_MODE").attr("disabled","true");
	$("#pam_BIZ_ATTR").attr("disabled","true");
	$("#pam_BIZ_STATUS").attr("disabled","true");
	$("#pam_PRICE").attr("disabled","true");
	$("#pam_BILLING_MODE").attr("disabled","true");
	$("#pam_MAX_ITEM_PRE_DAY").attr("disabled","true");
	$("#pam_BILLING_MODE").attr("disabled","true");
	$("#pam_IS_TEXT_ECGN").attr("disabled","true");
	$("#pam_MAX_ITEM_PRE_MON").attr("disabled","true");
	$("#pam_DEFAULT_ECGN_LANG").attr("disabled","true");
	$("#pam_TEXT_ECGN_ZH").attr("disabled","true");
	$("#pam_TEXT_ECGN_EN").attr("disabled","true");
	$("#pam_ADMIN_NUM").attr("disabled","true");
	$("#pam_BIZ_NAME").attr("disabled","true");
	$("#pam_BIZ_PRI").attr("disabled","true");
	$("#pam_CS_URL").attr("disabled","true");
	$("#pam_USAGE_DESC").attr("disabled","true");
	$("#pam_PRE_CHARGE").attr("disabled","true");
	$("#pam_BIZ_TYPE_CODE").attr("disabled","true");  
	$("#pam_INTRO_URL").attr("disabled","true");   
}

function statetoundisable()
{   
	$("#pam_BILLING_TYPE").attr("disabled","");
	$("#pam_OPER_CODE").attr("disabled","");
	$("#pam_ACCESS_MODE").attr("disabled","");
	$("#pam_BIZ_STATUS").attr("disabled","");
	$("#pam_PRICE").attr("disabled","");
	$("#pam_BILLING_MODE").attr("disabled","");
	$("#pam_USAGE_DESC").attr("disabled","");
	$("#pam_INTRO_URL").attr("disabled","");
	$("#pam_MAX_ITEM_PRE_DAY").attr("disabled","");
	$("#pam_IS_TEXT_ECGN").attr("disabled","");
	$("#pam_MAX_ITEM_PRE_MON").attr("disabled","");
	$("#pam_DEFAULT_ECGN_LANG").attr("disabled","");
	$("#pam_TEXT_ECGN_ZH").attr("disabled","");
	$("#pam_TEXT_ECGN_EN").attr("disabled","");
	$("#pam_ADMIN_NUM").attr("disabled","");
	$("#pam_BIZ_TYPE_CODE").attr("disabled","");
	$("#pam_BIZ_PRI").attr("disabled","");
	$("#pam_CS_URL").attr("disabled","");
	$("#pam_PRE_CHARGE").attr("disabled","");    
}


//提交
function checkSub(obj)
{
	var methodName=$("#cond_OPER_TYPE").val();
	if(!submitOfferCha())
		return false; 
	if(!validateParamPage(methodName))
		return false;
	backPopup(obj);
}

function validateParamPage(methodName) 
{
	if(methodName=='CrtUs')
	{
		var shortNumber = $("#SHORT_NUMBER_CODE_INPUT").val();
		var shortNumberInput = $("#pam_SERV_CODE").val();
		if(shortNumber)	
		{
			if(shortNumber != shortNumberInput) 
			{
				$.validate.alerter.one($("#pam_SERV_CODE")[0], "请先验证服务代码！");
				return false;
			}
		}
		else 
		{
			$.validate.alerter.one($("#pam_SERV_CODE")[0], "请先验证服务代码！");
			return false;
		}
	}
	else if(methodName=='ChgUs')
	{
		var operCode = $("#pam_OPER_CODE").val();
	   	if(operCode == "")
	   	{
	   		$.validate.alerter.one($("#pam_OPER_CODE")[0], "请选择操作类型！");
	       	return false;
	   	} 
	}
	return true;  
}