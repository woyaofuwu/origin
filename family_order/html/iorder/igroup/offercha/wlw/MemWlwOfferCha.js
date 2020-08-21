//提交
function checkSub(obj)
{
	var offerId = $("#prodDiv_OFFER_ID").val();
	
	if(offerId=="120001218301"){
		var apnType = $("#pam_APNTYPE").val();
		var apnName = $("#pam_APNNAME").val();
		if(apnType==null || apnType==""){
			$.validate.alerter.one($("#pam_APNTYPE")[0],"请先选择APN类型,再做修改,若不修改则点【取消】！");
			return false;
		}
		//专用APN名称不能与通用APN名称一致
		var comApnNames = "cmnbiot,cmnbiot1,cmnbiot2,cmnbiot3,cmnbiot4,cmnbiot5,cmnbiot6,";
		if("1" == apnType){
			if(comApnNames.indexOf(apnName+",")!=-1){
				$.validate.alerter.one($("#pam_APNNAME")[0],"专用APN名称不能与通用APN名称["+comApnNames+"]一致！");
				return false;
			}
		}
		var lowPowerMode = $("#pam_LOWPOWERMODE").val();
		var rauTauTimer = $("#pam_RAUTAUTIMER").val();
		if(rauTauTimer=="" && ("PSM"==lowPowerMode || "BOTH"==lowPowerMode)){
			$.validate.alerter.one($("#pam_RAUTAUTIMER")[0],"低电耗模式为[PSM,PSM+eDRX]需要填写[RAU/TAU定时器]参数值!");
			return false;
		}else if(rauTauTimer!="" && ("PSM"!=lowPowerMode && "BOTH"!=lowPowerMode)){
			$.validate.alerter.one($("#pam_RAUTAUTIMER")[0],"低电耗模式不为[PSM,PSM+eDRX]不需要填写[RAU/TAU定时器]参数值!");
			$("#pam_RAUTAUTIMER").val("");
			return false;
		}
		
		//平台校验要求PSM模式：RAU/TAU定时器应大于空闲态定时器
		if("PSM"==lowPowerMode || "BOTH"==lowPowerMode){
			var rauTauTimer = $("#pam_RAUTAUTIMER").val();
			var activeTimer = $("#pam_ACTIVETIMER").val();
			if(activeTimer >= rauTauTimer){
				$("#pam_ACTIVETIMER").val("");
				$.validate.alerter.one($("#pam_ACTIVETIMER")[0],"平台校验要求PSM或BOTH模式：RAU/TAU定时器应大于空闲态定时器。");
				return false;
			}
		}
		
		//这两个属性不需要传值到后台生成台帐
		$("#pam_COMMONAPN").val("");
		$("#pam_APNTYPE").val("");
	}

	if(offerId=="120099010001"||offerId=="120099011005"||offerId=="120099011011"||offerId=="120099011012"){
		var apnName = $("#pam_APNNAME").val();
		if(apnName == 'CMMTM' || apnName=='CMIOT'){
			
		}else if(/cmiot([a-zA-Z]{1,10})\.h(q|i)/g.test(apnName)){
			
		}else{
			$.validate.alerter.one($("#pam_APNNAME")[0],"您输入的APNNAME不规范，通用APN为CMMTM（适用于3G）或CMIOT（适用于4G），专用APN的规则为CMIOT********.HQ");
			return false;
		}
	}
	var checkflag = checkSubForOperType();
	if(!checkflag){
		return false;
	}
	if(!submitOfferCha())
		return false; 
	
	backPopup(obj);
}

//20180607看了online的代码 并没有上这段逻辑 所以暂时屏蔽掉 下同 
//尼玛 0613上线了 坑比 
function apnTypeChanged() {
	var apnType = $("#pam_APNTYPE").val();
	
	$("#pam_COMMONAPN").val("");	
	$("#pam_APNNAME").val("");			
	$("#pam_LOWPOWERMODE").val("");
	$("#pam_RAUTAUTIMER").val("");
	$("#pam_LOWPOWERMODE").attr("disabled", false);
	
	if ("0" == apnType) {
		$("#pam_COMMONAPN").attr("nullable", "yes");
		$("#pam_COMMONAPN").closest("li").attr("style", "");
		//$("#pam_APNNAME").closest("li").attr("style", "display:none");
	} else if("1" == apnType)
	{
		$("#pam_COMMONAPN").attr("nullable", "yes");
		$("#pam_COMMONAPN").closest("li").attr("style", "display:none");
		//$("#pam_APNNAME").closest("li").attr("style", "");
	}
}

function commonApnSelected() {

	var commonApnName = $("#pam_COMMONAPN").val();
	if ("" == commonApnName || null == commonApnName) {
		return;
	}
	var apnType = $("#pam_APNTYPE").val();
	if ("" == apnType || null == apnType) {
		$.validate.alerter.one($("#pam_APNTYPE")[0],"请先选择APN类型");
		$("#pam_COMMONAPN").val("");
		return;
	}
	// 将值写入APNNAME属性，
	$("#pam_APNNAME").val(commonApnName);
	$.beginPageLoading();
	
	$.httphandler.post("com.asiainfo.veris.crm.iorder.web.igroup.offercha.wlw.WlwParamHandler", "queryCommonApnTemplate",'&APNNAME='+commonApnName, function(data){
		$.endPageLoading();
		var lowPowerMode = data.get("LOWPOWERMODE");
		$("#pam_LOWPOWERMODE").val(lowPowerMode);
		$("#pam_LOWPOWERMODE").attr("disabled", true);	//通用APN对应的LOWPOWERMODE模式是固定的，不可选择
		var nullable = data.get("RAUTAUTIMER");
		$("#pam_RAUTAUTIMER").attr("nullable", nullable);
		
		if("no" == nullable){
			$("#pam_RAUTAUTIMER").closest("li").attr("style", "display:");
		}else{
			$("#pam_RAUTAUTIMER").closest("li").attr("style", "display:none");
		}
		lowPowerModeChanged();
		},    
		function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
		},
        {
		  async: false
    });
	
}



//校验不同操作状态下的参数值
function checkSubForOperType(){
	debugger;
	var offerId = $("#prodDiv_OFFER_ID").val();
	
	var offercode = $("#prodDiv_OFFER_OPER_CODE").val();
	var userWhiteNumFlag = $("#pam_userWhiteNumFlag").val();
	var userClass = $("#pam_UserClass").val();
	var userClassFlag = $("#pam_userClassFlag").val();
	
if(offerId=="120099011019"){	

	if(offercode == "0"){
		if(userWhiteNumFlag == null||userWhiteNumFlag == "" ){
			$.validate.alerter.one($("#pam_userWhiteNumFlag")[0],"订购时，白名单功能状态必填！");
			return false;
		}else if(userClass == null||userClass == "" ){
			$.validate.alerter.one($("#pam_UserClass")[0],"订购时，通话阀值的级别必填！");
			return false;
		}else if(userClassFlag == null||userClassFlag == ""){
			$.validate.alerter.one($("#pam_userClassFlag")[0],"订购时，企业客户成员个人通话阀值是否立即生效必填！");
			return false;
		}else if(userWhiteNumFlag == "0"&&userClassFlag!="1"){
			$.validate.alerter.one($("#pam_userClassFlag")[0],"白名单功能状态为“去激活”时，“企业客户成员个人通话阀值是否立即生效” 必须设置为立即生效！");
			return false;
		}else{
			return true;
		}
	}else if(offercode == "3"){
       //changeA、changeB来自EnterpriseOffer.js
		if(changeA == 1&&changeB != 1){
			$.validate.alerter.one($("#pam_userClassFlag")[0],"白名单功能状态修改时，“企业客户成员个人通话阀值是否立即生效” 必须修改！");
			return false;
		}else if(changeA != 1&&changeB == 1){
			$.validate.alerter.one($("#pam_userWhiteNumFlag")[0],"企业客户成员个人通话阀值是否立即生效修改时，“白名单功能状态” 必须修改！");
			return false;
		}else if(userWhiteNumFlag == "0"&&userClassFlag!="1"){
			$.validate.alerter.one($("#pam_userClassFlag")[0],"白名单功能状态为“去激活”时，“企业客户成员个人通话阀值是否立即生效” 必须设置为立即生效！");
			return false;
		}else{
			return true;
		}
		
	}else {
		return true;
	}

  }else {
		return true;
  }
}

function lowPowerModeChanged(){
	var lowPowerMode = $("#pam_LOWPOWERMODE").val();
	if("PSM"==lowPowerMode || "BOTH"==lowPowerMode){
		$("#pam_RAUTAUTIMER").attr("nullable", "no");
		$("#pam_RAUTAUTIMER").closest("li").attr("style", "display:");
	}else{
		$("#pam_RAUTAUTIMER").attr("nullable", "yes");
		$("#pam_RAUTAUTIMER").val("");
		$("#pam_RAUTAUTIMER").closest("li").attr("style", "display:none");
	}
	
	if("PSM"==lowPowerMode){
		$("#pam_ACTIVETIMER").attr("nullable", "no");
		$("#pam_ACTIVETIMER").closest("li").attr("style", "display:");
		$("#pam_NBIOTEDRXCYCLE").attr("nullable", "yes");
		$("#pam_NBIOTEDRXCYCLE").val("");
		$("#pam_NBIOTEDRXCYCLE").closest("li").attr("style", "display:none");
	}else if ("eDRX"==lowPowerMode){
		$("#pam_NBIOTEDRXCYCLE").attr("nullable", "no");
		$("#pam_NBIOTEDRXCYCLE").closest("li").attr("style", "display:");
		$("#pam_ACTIVETIMER").attr("nullable", "yes");
		$("#pam_ACTIVETIMER").val("");
		$("#pam_ACTIVETIMER").closest("li").attr("style", "display:none");
	}else if ("BOTH"==lowPowerMode){
		$("#pam_NBIOTEDRXCYCLE").attr("nullable", "no");
		$("#pam_NBIOTEDRXCYCLE").closest("li").attr("style", "display:");
		$("#pam_ACTIVETIMER").attr("nullable", "no");
		$("#pam_ACTIVETIMER").closest("li").attr("style", "display：");
	}else{
		$("#pam_NBIOTEDRXCYCLE").attr("nullable", "yes");
		$("#pam_NBIOTEDRXCYCLE").val("");
		$("#pam_NBIOTEDRXCYCLE").closest("li").attr("style", "display:none");
		
		$("#pam_ACTIVETIMER").attr("nullable", "yes");
		$("#pam_ACTIVETIMER").val("");
		$("#pam_ACTIVETIMER").closest("li").attr("style", "display:none");
	}
}
