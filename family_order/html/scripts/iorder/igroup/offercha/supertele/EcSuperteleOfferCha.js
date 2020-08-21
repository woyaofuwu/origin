function sucShortNumRes(data,exchangeShortSn1)
{
	if("true"== data.get("FLAG"))
	{
		$("#pam_EXCHANGE_SHORT_SN").val(exchangeShortSn1);
		MessageBox.success("验证结果", "恭喜您,短号码"+exchangeShortSn1+"验证成功!", function(){
		});
	}
	else
	{
		MessageBox.alert("验证提示",data.get("ERROR_MESSAGE")+",请重新验证!");
	}
}

function sucValidateExchangeTeleSn(data)
{  
	$.endPageLoading();
	$("#pam_E_CUST_NAME").val(data.get("CUST_NAME"));
	$("#pam_E_CUST_ID").val(data.get("CUST_ID")); 
	$("#pam_E_USER_ID").val(data.get("USER_ID"));
	$("#pam_E_EPARCHY_CODE").val(data.get("EPARCHY_CODE")); 
	$("#pam_E_CITY_CODE").val(data.get("CITY_CODE")); 
	$("#pam_E_PRODUCT_ID").val(data.get("PRODUCT_ID")); 
	$("#pam_E_BRAND_CODE").val(data.get("BRAND_CODE")); 
	$("#userServNumber").val(data.get("SERIAL_NUMBER")); 
	
	$("#pam_USER_TYPE").val(data.get("USER_TYPE")); 
	$("#pam_PSPT_TYPE").val(data.get("PSPT_TYPE_NAME"));  
	$("#pam_PSPT_ID").val(data.get("PSPT_ID"));
	$("#pam_OPEN_DATE").val(data.get("OPEN_DATE")); 
	$("#pam_DEVELOP_NO").val(data.get("DEVELOP_NO")); 
    $("#pam_PRODUCT_NAME").val(data.get("PRODUCT_NAME")); 
    $("#pam_BRAND_NAME").val(data.get("BRAND_NAME")); 
    
    if(data.get("FLAG") == "1"){
    	$("#pam_EXCHANGE_SHORT_SN").val(data.get("EXCHANGE_SHORT_SN"));
    	$("#pam_EXCHANGE_SHORT_SN").attr("disabled",true);
    }
}
//总机号码验证
function  validateExchangeTeleSn(){
	
	var exchangeteleSn1 = $("#pam_EXCHANGETELE_SN_1").val();
	if(null==exchangeteleSn1|| "" == exchangeteleSn1){
		$.validate.alerter.one($("#pam_EXCHANGETELE_SN_1")[0], "您输入的总机号码为空，请输入后再验证!\n");			
		return false; 
    }
	$.beginPageLoading("数据加载中......");
	$.httphandler.post("com.asiainfo.veris.crm.iorder.web.igroup.supertele.SuperteleHandler", "queryExchangeUserInfo",'&SERIAL_NUMBER='+ exchangeteleSn1, function(data){
		$.endPageLoading();
		MessageBox.success("验证结果", "恭喜您,总机号码"+exchangeteleSn1+"验证成功!", function(){
		
		});
		$("#pam_EXCHANGETELE_SN").val(exchangeteleSn1);
		sucValidateExchangeTeleSn(data);
		},    
		function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
}

//短号码验证
function  shortNumValidateSuperTeleAdmin(){
	var exchangeShortSn1 = $("#pam_EXCHANGE_SHORT_SN_1").val();
	if( exchangeShortSn1.length > 6 ) {  
	    $.validate.alerter.one($("#pam_EXCHANGE_SHORT_SN_1")[0], "短号码，最多只能输入6个数字!\n");
		return false;
	}
	if(null==exchangeShortSn1|| "" == exchangeShortSn1){
		$.validate.alerter.one($("#pam_EXCHANGE_SHORT_SN_1")[0], "您输入的短号码为空，请输入后再验证!\n");			
		return false; 
    }
	$.beginPageLoading("数据加载中......");
	$.httphandler.post("com.asiainfo.veris.crm.iorder.web.igroup.supertele.SuperteleHandler", "superTeleShortNumAdmin",'&SHORT_NUM='+ exchangeShortSn1, function(data){
		$.endPageLoading();
		
		sucShortNumRes(data,exchangeShortSn1);
		},    
		function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
}


//产品参数提交
function submitSupertele(obj){
	
	if(!submitOfferCha())
		return false; 
	var exchangeteleSn = $("#pam_EXCHANGETELE_SN").val();
	var exchangeteleSn1 = $("#pam_EXCHANGETELE_SN_1").val();
	var exchangeShortSn = $("#pam_EXCHANGE_SHORT_SN").val();
	var exchangeShortSn1 = $("#pam_EXCHANGE_SHORT_SN_1").val();
	var operType = $("#cond_OPER_TYPE").val();
	
	if(null==exchangeteleSn|| "" == exchangeteleSn){
		$.validate.alerter.one($("#pam_EXCHANGETELE_SN_1")[0], "您没有验证总机号码，请验证!\n");			
		return false; 
    }
	
	if(null==exchangeShortSn|| "" == exchangeShortSn){
		$.validate.alerter.one($("#pam_EXCHANGE_SHORT_SN_1")[0], "您没有验证总机号码，请验证!\n");			
		return false; 
    }
	
	if(exchangeteleSn!=exchangeteleSn1){
		$.validate.alerter.one($("#pam_EXCHANGETELE_SN_1")[0], "您输入的总机号码与验证号码不一致，请重新验证!\n");			
		return false; 
		}

	if(exchangeShortSn!=exchangeShortSn1){
		$.validate.alerter.one($("#pam_EXCHANGE_SHORT_SN_1")[0], "您输入的短号码与验证号码不一致，请重新验证!\n");			
		return false; 
		}
	
	backPopup(obj);
	}

