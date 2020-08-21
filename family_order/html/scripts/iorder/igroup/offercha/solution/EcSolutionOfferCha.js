//function initPageParam_120000000461(){
//	
//	$("#pam_APN_IP").closest("li").css("display", "none");
//	$("#pam_APN_IP").closest("li").removeClass("link required");
//	$("#pam_APN_IP").attr("nullable", "yes");
//}
//function initPageParam_120000000462(){
//	
//	$("#pam_APN_IP").closest("li").css("display", "none");
//	$("#pam_APN_IP").closest("li").removeClass("link required");
//	$("#pam_APN_IP").attr("nullable", "yes");
//}
//function initPageParam_120000000451(){
//	
//	$("#pam_APN_IP").closest("li").css("display", "none");
//	$("#pam_APN_IP").closest("li").removeClass("link required");
//	$("#pam_APN_IP").attr("nullable", "yes");
//}

function submitSolution(obj)
{
	debugger;
	if(!submitOfferCha())
		return false; 
	

	var apnIp1 = $("#pam_G78BOSS1").val();
	var apnName1 = $("#pam_G78BOSS2").val();
	var apnIp2 = $("#pam_G463BOSS1").val();
	var	apnName2 = $("#pam_G463BOSS2").val();
	var apnName3 = $("#pam_G461BOSS2").val();
	var apnName4 = $("#pam_G462BOSS2").val();
	var apnName5 = $("#pam_G451BOSS2").val();
	var apnIp;
	var apnName;
	if(""!=apnIp1 && undefined !=apnIp1){
		apnIp = apnIp1;
	}
	if(""!=apnIp2 && undefined !=apnIp2){
		apnIp = apnIp2;
	}
	if(""!=apnName1 && undefined!=apnName1){
		apnName = apnName1;
	}
	if(""!=apnName2 && undefined!=apnName2){
		apnName = apnName2;
	}
	if(""!=apnName3 && undefined!=apnName3){
		apnName = apnName3;
	}
	if(""!=apnName4 && undefined!=apnName4){
		apnName = apnName4;
	}
	if(""!=apnName5 && undefined!=apnName5){
		apnName = apnName5;
	}
	

	$.beginPageLoading("数据加载中......");
	$.httphandler.post("com.asiainfo.veris.crm.iorder.web.igroup.solution.SolutionHandler", "queryApnInfo",'&APN_IP='+ apnIp+'&APN_NAME='+ apnName, function(data){
		$.endPageLoading();
			if(""!=apnIp1&&undefined !=apnIp1){
				if("false"== data.get("FLAG1"))
				{
					$.validate.alerter.one($("#pam_G78BOSS1")[0],data.get("ERROR_MESSAGE1"));
					return false;
				}
				if("false"== data.get("FLAG"))
				{
					$.validate.alerter.one($("#pam_G78BOSS2")[0],data.get("ERROR_MESSAGE"));
					return false;
				}else{
					backPopup(obj);
				}
			}else if(""!=apnIp2&&undefined !=apnIp2){
				if("false"== data.get("FLAG1"))
				{
					$.validate.alerter.one($("#pam_G463BOSS1")[0],data.get("ERROR_MESSAGE1"));
					return false;
				}
				if("false"== data.get("FLAG"))
				{
					$.validate.alerter.one($("#pam_G463BOSS2")[0],data.get("ERROR_MESSAGE"));
					return false;
				}else{
					backPopup(obj);
				}
			}else if(""!=apnName3&&undefined !=apnName3){
				if("false"== data.get("FLAG"))
				{
					$.validate.alerter.one($("#pam_G461BOSS2")[0],data.get("ERROR_MESSAGE"));
					return false;
				}else{
					backPopup(obj);
				}
			}else if(""!=apnName4&&undefined !=apnName4){
				if("false"== data.get("FLAG"))
				{
					$.validate.alerter.one($("#pam_G462BOSS2")[0],data.get("ERROR_MESSAGE"));
					return false;
				}else{
					backPopup(obj);
				}
			}else{
				if("false"== data.get("FLAG"))
				{
					$.validate.alerter.one($("#pam_G451BOSS2")[0],data.get("ERROR_MESSAGE"));
					return false;
				}else{
					backPopup(obj);
				}
			}
		},    
		function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
}

