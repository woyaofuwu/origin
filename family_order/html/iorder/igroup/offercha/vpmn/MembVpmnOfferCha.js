var moList = new Wade.DatasetList();
var short = 0 ;
var notInShortCode ;
function initPageParam_110000800001()
{
	debugger;
	
	//政企一单清特殊处理
	if($("#SET_OFFERCHA_SOURCE").val() == "EcIntegrateOrder")
	{//删除短号输入框，在号码导入时录入短号
		$("#pam_SHORT_CODE1").attr("nullable", "yes");
		$("#pam_SHORT_CODE1").closest("li").css('display','none');
		$("#pam_SHORT_CODE2").attr("nullable", "yes");
		$("#pam_SHORT_CODE2").attr("readonly", "true");
		$("#pam_SHORT_CODE2").closest("li").removeClass("link required");

        $("#pam_SHORT_CODE").attr("nullable", "yes");
        $("#pam_SHORT_CODE").closest("li").css("display", "none");
        $("#pam_SHORT_CODE").val("#SHORT_CODE#"); //先录入一个特殊标识，数据转换时进行替换

		$("#VPMN_CRM").closest("li").css('display','none');
		$("#OPERATE_ID").closest("li").css('display','none');
		
	}else {
		
		var  offerType = $("#cond_OPER_TYPE").val();
		if("CrtMb"==offerType){
			$("#reloadNa").css("display", "none");
			if(short == 0 ){
				Changeinput(); 
			}
			if(short == 1 ){
				$("#pam_SHORT_CODE2").closest("li").css("display", "none");
				$("#pam_SHORT_CODE2").closest("li").removeClass("link required");
				$("#pam_SHORT_CODE2").attr("nullable", "yes");
				$("#pam_SHORT_CODE1").closest("li").css("display", "");
				$("#pam_SHORT_CODE1").closest("li").addClass("link required");
				$("#pam_SHORT_CODE1").attr("nullable", "no");
			}
			if(short == 2 ){
				$("#pam_SHORT_CODE1").closest("li").css("display", "none");
				$("#pam_SHORT_CODE1").closest("li").removeClass("link required");
				$("#pam_SHORT_CODE1").attr("nullable", "yes");
				$("#pam_SHORT_CODE2").closest("li").css("display", "");
				$("#pam_SHORT_CODE2").closest("li").addClass("link required");
				$("#pam_SHORT_CODE2").attr("nullable", "no");
			}
			
		}
		if("ChgMb"==offerType){  
			debugger;
			$("#pam_SHORT_CODE1").closest("li").css("display", "none");
			$("#pam_SHORT_CODE1").closest("li").removeClass("link required");
			$("#pam_SHORT_CODE1").attr("nullable", "yes");
			$("#pam_SHORT_CODE2").closest("li").css("display", "");
			$("#pam_SHORT_CODE2").closest("li").addClass("link required");
			$("#pam_SHORT_CODE2").attr("nullable", "no");
			$("#VPMN_CRM").closest("li").css("display", "none");
			$("#VPMN_CRM").closest("li").removeClass("link required");
			$("#VPMN_CRM").attr("nullable", "yes");
			$("input[name='pam_TWOCHECK_SMS_FLAG']").closest("li").css("display", "none");
			$("input[name='pam_TWOCHECK_SMS_FLAG']").closest("li").removeClass("link required");
			$("input[name='pam_TWOCHECK_SMS_FLAG']").attr("nullable", "yes");
			$("#OPERATE_ID").closest("li").css("display", "none");
			$("#OPERATE_ID").closest("li").removeClass("link required");
			$("#OPERATE_ID").attr("nullable", "yes");
			notInShortCode = $("#pam_SHORT_CODE").val();
			
			var html="<input type='text' id='pam_OLD_SHORT_CODE' name='pam_OLD_SHORT_CODE' jwcid='@TextField' value='"+notInShortCode+"' readOnly = 'true'/>";
			$(html).insertBefore($("#pam_SHORT_CODE"));
			$("#pam_OLD_SHORT_CODE").css("display", "none");
		}
		
		
		//REQ201812200001关于优化集团产品二次确认功能的需求
		$.ajax.submit("", "queryTwoCheck", "", "", function(data){
			$.endPageLoading();
			if(data != null)
			{
				if(data.get("TWOCHECK") == '1'){
					$("input[name='pam_TWOCHECK_SMS_FLAG']").attr("disabled","");
				}
			}
		},
		function(error_code,error_info,derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
		});
	}
}

function Changeinput() {  

	var shortNumber = $('#cond_SERIAL_NUMBER_INPUT').val();
	var grpUserId = $("#cond_EC_USER_ID").val();
	$.beginPageLoading("数据加载中......");
	$.httphandler.post("com.asiainfo.veris.crm.iorder.web.igroup.offercha.vpmn.SuperteleHandler", "creatshortcode",'&SERIAL_NUMBER='+ shortNumber+'&EC_USER_ID='+ grpUserId, function(data){
		$.endPageLoading();
		creatShortCodeAfter(data);
		},    
		function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
	
	
}

function creatShortCodeAfter(data){
	$.endPageLoading();
	var shortCode = data.get("SHORT_CODE"); 
	$("#pam_SHORT_CODE1").val(shortCode);
	$("#OPERATE_ID").val("1");
	$("#pam_SHORT_CODE2").closest("li").css("display", "none");
	$("#pam_SHORT_CODE2").closest("li").removeClass("link required");
	$("#pam_SHORT_CODE2").attr("nullable", "yes");
	$("#pam_SHORT_CODE").val(shortCode);
	$("#pam_SHORT_CODE1").closest("li").css("display", "");
	$("#pam_SHORT_CODE1").closest("li").addClass("link required");
	$("#pam_SHORT_CODE1").attr("nullable", "no");
	short =1;

}

//短号验证
function validateShortNum() {  
	
	//政企一单清特殊处理
	if($("#SET_OFFERCHA_SOURCE").val() == "EcIntegrateOrder")
	{//此处不校验短号
		//$.validate.alerter.one($("#pam_SHORT_CODE2")[0],"一单清特殊处理，不需要验证！");
		return true;
	}
	
	var shortCode = $('#pam_SHORT_CODE2').val();
	var oldshortcode=$('#pam_OLD_SHORT_CODE').val();
	if(shortCode==""||shortCode==undefined||shortCode ==null){
		$.validate.alerter.one($("#pam_SHORT_CODE2")[0],"短号码为空，请输入后再验证！");
		return false;
	}
	if(shortCode==oldshortcode){
		$.validate.alerter.one($("#pam_SHORT_CODE2")[0],"未做短号修改，不需要验证！");
		return true;
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

function queryOperateId(){
	var operateId = $('#OPERATE_ID').val(); 
	if("1"==operateId){
		Changeinput();
	}
	if("0"==operateId){
		$("#pam_SHORT_CODE1").closest("li").css("display", "none");
		$("#pam_SHORT_CODE1").closest("li").removeClass("link required");
		$("#pam_SHORT_CODE1").attr("nullable", "yes");
		$("#pam_SHORT_CODE").val("");
		$("#pam_SHORT_CODE2").closest("li").css("display", "");
		$("#pam_SHORT_CODE2").closest("li").addClass("link required");
		$("#pam_SHORT_CODE2").attr("nullable", "no");
	}
	
}
//重置
function resertShortCode(){
	
	$("#pam_SHORT_CODE2").val(notInShortCode);
	
}

//提交
function checkSub(obj)
{
	var result = submitOfferCha();
	
	//政企一单清特殊处理
	if($("#SET_OFFERCHA_SOURCE").val() != "EcIntegrateOrder")
	{
		var shortC = $("#pam_SHORT_CODE").val();
		if(shortC ==""||shortC==undefined ||shortC ==null){
			$.validate.alerter.one($("#pam_SHORT_CODE2")[0],"短号码未验证，请验证！");	
			return false;
		}
		var payPlanList = $("input[name='pam_TWOCHECK_SMS_FLAG']:checked") ;
		if(payPlanList.length==0){ 
			if(!confirm("当前业务办理涉及不知情订购，若需用户短信二次确认请在界面中选择，重新选择请点【取消】按钮！")){
				return false;
			}
		} 
	}
	
	  if(result==true){
		  backPopup(obj);
	  }
	  try {
		  //backPopup(obj);
	  } catch (msg) {
		  $.error(msg.message);
	  }
	
}
