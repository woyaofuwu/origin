function showAuthPart(){
	var type = $("#AUTHTYPE").val();
//	alert(type);
	if(type == "1"){
		$("#authPart3").css("display", "block");
		$("#authPart4").css("display", "none");
		$("#authPart5").css("display", "none");
		$("#authPart6").css("display", "none");
		$("#authPart7").css("display", "none");
	} else if(type == "2"){
		$("#authPart3").css("display", "none");
		$("#authPart4").css("display", "block");
		$("#authPart5").css("display", "none");
		$("#authPart6").css("display", "none");
		$("#authPart7").css("display", "none");
	} else if(type == "3"){
		$("#authPart3").css("display", "none");
		$("#authPart4").css("display", "none");
		$("#authPart5").css("display", "block");
		$("#authPart6").css("display", "none");
		$("#authPart7").css("display", "none");
	} else if(type == "4"){
		$("#authPart3").css("display", "none");
		$("#authPart4").css("display", "none");
		$("#authPart5").css("display", "none");
		$("#authPart6").css("display", "block");
		$("#authPart7").css("display", "none");
	} else if(type == "5"){
		$("#authPart3").css("display", "none");
		$("#authPart4").css("display", "none");
		$("#authPart5").css("display", "none");
		$("#authPart6").css("display", "none");
		$("#authPart7").css("display", "block");
	}
}


//IVR部分还没有提供，先放着到时候再改


function queryCustInfo(){
	var SERIAL_NUMBER = $("#cond_SERIAL_NUMBER").val();
	var param = '&SERIAL_NUMBER=' + SERIAL_NUMBER;
	$.beginPageLoading("信息查询中......");
	$.ajax.submit(null, 'queryCustInfo', param, 'custInfoPart,authPart1,authPart4,authPart5,authPart6,authPart7', function(data) {
		$.endPageLoading();
		$("#CUST_NAME").val(data.get("CUST_NAME"));
		$("#PSPT_ID").val(data.get("PSPT_ID"));
	},function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
	});
}

function queryLastTrade(){
	var CUST_NAME = $("#CUST_NAME").val();
	if(CUST_NAME == "" || CUST_NAME == null){
		alert("请先校验手机号码！");
		return;
	}
	var SERIAL_NUMBER = $("#cond_SERIAL_NUMBER").val();
	var param = '&SERIAL_NUMBER=' + SERIAL_NUMBER;
	$.beginPageLoading("业务信息查询中......");
	$.ajax.submit(null, 'queryLastTrade', param, 'authPart5', function(data) {
		$.endPageLoading();
		$("#FLAG3").attr("disabled",false);
	},function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
	});
}

function queryUUInfos(){
	var CUST_NAME = $("#CUST_NAME").val();
	if(CUST_NAME == "" || CUST_NAME == null){
		alert("请先校验手机号码！");
		return;
	}
	var SERIAL_NUMBER = $("#cond_SERIAL_NUMBER").val();
	var param = '&SERIAL_NUMBER=' + SERIAL_NUMBER;
	$.beginPageLoading("亲亲网号码查询中......");
	$.ajax.submit(null, 'queryUUInfos', param, 'authPart7,NavBarPart', function(data) {
		$.endPageLoading();
		$("#FLAG5").attr("disabled",false);
	},function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
	});
}

function queryPayLog(){
	var CUST_NAME = $("#CUST_NAME").val();
	if(CUST_NAME == "" || CUST_NAME == null){
		alert("请先校验手机号码！");
		return;
	}
	var SERIAL_NUMBER = $("#cond_SERIAL_NUMBER").val();
	var param = '&SERIAL_NUMBER=' + SERIAL_NUMBER;
	$.beginPageLoading("最后一次缴费信息查询中......");
	$.ajax.submit(null, 'queryPayLog', param, 'authPart6', function(data) {
		$.endPageLoading();
		$("#FLAG4").attr("disabled",false);
	},function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
	});
}

function queryCdrBil(){
	var CUST_NAME = $("#CUST_NAME").val();
	if(CUST_NAME == "" || CUST_NAME == null){
		alert("请先校验手机号码！");
		return;
	}
	var SERIAL_NUMBER = $("#cond_SERIAL_NUMBER").val();
	var SERIAL_NUMBER1 = $("#cond_CALLED_NUMBER1").val();
	var SERIAL_NUMBER2 = $("#cond_CALLED_NUMBER2").val();
	var SERIAL_NUMBER3 = $("#cond_CALLED_NUMBER3").val();
	if(SERIAL_NUMBER1 == SERIAL_NUMBER2 || SERIAL_NUMBER2 == SERIAL_NUMBER3 || SERIAL_NUMBER1 == SERIAL_NUMBER3){
		alert("请输入三个不同的号码！")
		return;
	}
	if("" == SERIAL_NUMBER1 || "" == SERIAL_NUMBER2 || "" == SERIAL_NUMBER3)
	{
		alert("请输入三个不同的号码！")
		return;
	}
	var param = '&SERIAL_NUMBER=' + SERIAL_NUMBER + '&SERIAL_NUMBER1=' + SERIAL_NUMBER1 + '&SERIAL_NUMBER2=' + SERIAL_NUMBER2 + '&SERIAL_NUMBER3=' + SERIAL_NUMBER3;
	$.beginPageLoading("通话记录验证中......");
	$.ajax.submit(null, 'queryCdrBil', param, '', function(data) {
		$.endPageLoading();
		var result = data.get("RESULT_CODE");
		if(result == "0000"){
			$("#CALLEDFLAGT").attr("checked",true);
			$("#CALLEDFLAGF").attr("checked",false);
			checkCalledFlag();
		} else if (result == "1111"){
			$("#CALLEDFLAGF").attr("checked",true);
			$("#CALLEDFLAGT").attr("checked",false);
			checkCalledFlag();
		}
	},function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
		checkCalledFlag();
	});
}

function queryMonFeeInfos(){
	var CUST_NAME = $("#CUST_NAME").val();
	if(CUST_NAME == "" || CUST_NAME == null){
		alert("请先校验手机号码！");
		return;
	}
	var SERIAL_NUMBER = $("#cond_SERIAL_NUMBER").val();
	var param = '&SERIAL_NUMBER=' + SERIAL_NUMBER;
	$.beginPageLoading("账单查询中......");
	$.ajax.submit(null, 'queryMonFeeInfos', param, 'authPart4,consumeNavBarPart', function(data) {
		$.endPageLoading();
		$("#FLAG2").attr("disabled",false);
	},function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
	});
}

function checkOpenDate(){
	var SERIAL_NUMBER = $("#cond_SERIAL_NUMBER").val();
	var OPEN_DATE = $("#OPEN_DATE").val();
	var CUST_NAME = $("#CUST_NAME").val();
	if(CUST_NAME == "" || CUST_NAME == null){
		alert("请先校验手机号码！");
		return;
	}
	if(OPEN_DATE == ""){
		alert("请选择入网时间！");
		return;
	}
	var param = '&OPEN_DATE=' + OPEN_DATE + '&SERIAL_NUMBER=' + SERIAL_NUMBER;
	$.beginPageLoading("入网时间校验中......");
	$.ajax.submit(null, 'checkOpenDate', param, '', function(data) {
		$.endPageLoading();
		var result = data.get("RESULT_CODE");
		if(result == "0000"){
			$("#FLAG1").attr("checked",true);
		} else if (result == "1111"){
			$("#FLAG1").attr("checked",false);
		}
	},function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
	});
}

function checkOtherFlag(){
	var FLAG1 = $("#FLAG1");
	var FLAG2 = $("#FLAG2");
	var FLAG3 = $("#FLAG3");
	var FLAG4 = $("#FLAG4");
	var FLAG5 = $("#FLAG5");
	if(FLAG1.attr("checked")||FLAG2.attr("checked")||FLAG3.attr("checked")||FLAG4.attr("checked")||FLAG5.attr("checked")){
		$("#OTHERFLAGT").attr("checked",true);
		$("#OTHERFLAGF").attr("checked",false);
		$("#AUTHTYPE").attr("disabled","none")
		checkFlagBeforeSubmit();
	} else {
		$("#OTHERFLAGT").attr("checked",false);
		$("#OTHERFLAGF").attr("checked",true);
		$('#AUTHTYPE').removeAttr("disabled");
		checkFlagBeforeSubmit();
	}
}

function checkIdenFlag(){
	$("#IDENFLAGT").attr("checked",true);
	checkFlagBeforeSubmit();
}

/*function chkIvrPasswd(){
	var v_serviceno = $("#cond_SERIAL_NUMBER").val();
		var v_cardid = null;
		var v_typeId = "2";
		var v_typeName = "个人密码";
		var v_return_pwd = null;
		var v_thisdoc = window;
		window.top.AiCtFBH.transToPwdVerifyCallByCRM(v_serviceno, v_cardid, v_typeId,v_typeName, v_return_pwd, v_thisdoc);
		$("#IvrBtn").attr("disabled", true).addClass("e_dis");
}*/


function chkIvrPasswd(){
	var frame=top.document.getElementById("callcenter"); 
	var rhkfframe=window.top.document.getElementById("public_iframe");
	if (frame && frame.contentWindow && frame.contentWindow.document) {
		frame.contentWindow.transIvrTransValidate(self, $("#cond_SERIAL_NUMBER").val());
		$("#IvrBtn").attr("disabled", true).addClass("e_dis");
	}else if(rhkfframe && rhkfframe.contentWindow && rhkfframe.contentWindow.document){
		rhkfframe.contentWindow.transIvrTransValidatePID($("#cond_SERIAL_NUMBER").val(),self);
		$("#IvrBtn").attr("disabled", true).addClass("e_dis");
	}
}

function ValidateUserPasswdCallBack(flag){
	if(flag=="0"){
		alert("IVR验证正确");
		$("#IDENFLAGT").attr("checked",true);
		$("#IDENFLAGF").attr("checked",false);
		checkCalledFlag();
		//var handler = $("#HANDLER").val();
		//var returnObj = $.parseJSON("{\""+handler+"\":1}");
		//$.setReturnValue(returnObj, true);
	}else{
		alert("IVR验证错误");
		$("#IDENFLAGT").attr("checked",false);
		$("#IDENFLAGF").attr("checked",true);
		checkCalledFlag();
	}
	$("#IvrBtn").attr("disabled", false).removeClass("e_dis");
}

function checkCalledFlag(){
	checkFlagBeforeSubmit();
}

function checkFlagBeforeSubmit(){
	var idenflag = $("#IDENFLAGT");
	var calledflag = $("#CALLEDFLAGT");
	var otherflag = $("#OTHERFLAGT");
	if((idenflag.attr("checked")&&calledflag.attr("checked"))||
			(idenflag.attr("checked")&&otherflag.attr("checked"))||
			(calledflag.attr("checked")&&otherflag.attr("checked"))||
			(idenflag.attr("checked")&&calledflag.attr("checked")&&otherflag.attr("checked"))){
		$.cssubmit.disabledSubmitBtn(false);
	}else {
		$.cssubmit.disabledSubmitBtn(true);
	}
}

function checkBeforeSubmit(){
	var CUST_NAME = $("#CUST_NAME").val();
	if(CUST_NAME == "" || CUST_NAME == null){
		alert("请先校验预停机手机号码！");
		return false;
	}
	var param = "&SERIAL_NUMBER="+$("#cond_SERIAL_NUMBER").val();
	$.cssubmit.addParam(param);
	return true;
}
