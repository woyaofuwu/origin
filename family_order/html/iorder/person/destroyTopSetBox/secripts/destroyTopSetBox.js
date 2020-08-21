//用戶认证结束之后执行的js方法
function refreshPartAtferAuth(data)
 {
	$.beginPageLoading("信息加载中......");
	$.ajax.submit('AuthPart', 'loadPageInfo', "&USER_INFO="+ data.get("USER_INFO").toString() + "&CUST_INFO=" + data.get("CUST_INFO").toString(), 'topSetBoxInfoPart',
			function(data) {
			//根据需求编号REQ201709080021 这里去掉只有交了押金的才显示退还机顶盒的检验
				//var isHasFee=data.get("IS_HAS_FEE");
				//if(isHasFee=="1"){		//显示是否退还机顶盒
					$("#isBackSet").css("display","");
				//}
				$.endPageLoading();
			}, function(error_code, error_info, derror) {
				$.endPageLoading();
				showDetailErrorInfo(error_code, error_info, derror);
			});
}

//BUS201907310012关于开发家庭终端调测费的需求
function isReturnTopsetbox()
{
	if($.feeMgr)
	{
		$.feeMgr.clearFee();
	}
	var commFeeTag = $("#COMMISSIONING_FEE_TAG").val();
	var commFee = $("#COMMISSIONING_FEE").val();
	var leftMonths = $("#LEFT_MONTHS").val();
	var activeFeeTag = $("#ACTIVE_FEE_TAG").val();

	var feeData = $.DataMap();

	if ($("#IS_RETURN_TOPSETBOX").val() == "0" && commFeeTag == "1" && Number(leftMonths) > 0) //不归还
	{
		feeData.put("MODE", "0");
		feeData.put("CODE", "515");
		feeData.put("FEE", commFee);
		feeData.put("PAY", commFee);
		feeData.put("TRADE_TYPE_CODE", "3806");
		$.feeMgr.insertFee(feeData);
		
		feeData.put("MODE", "0");
		feeData.put("CODE", "517");
		feeData.put("FEE", "10000");
		feeData.put("PAY", "10000");
		feeData.put("TRADE_TYPE_CODE", "3806");
		$.feeMgr.insertFee(feeData);
	} 
	else if ($("#IS_RETURN_TOPSETBOX").val() == "1" && commFeeTag == "1" && Number(leftMonths) > 0) //归还
	{
		feeData.put("MODE", "0");
		feeData.put("CODE", "515");
		feeData.put("FEE", commFee);
		feeData.put("PAY", commFee);
		feeData.put("TRADE_TYPE_CODE", "3806");
		$.feeMgr.insertFee(feeData);
	}else if($("#IS_RETURN_TOPSETBOX").val() == "0" && activeFeeTag == "1") //不归还
	{
		feeData.put("MODE", "0");
		feeData.put("CODE", "517");
		feeData.put("FEE", "10000");
		feeData.put("PAY", "10000");
		feeData.put("TRADE_TYPE_CODE", "3806");
		$.feeMgr.insertFee(feeData);
	}
}

//BUS201907310012关于开发家庭终端调测费的需求


/**
 * 业务提交校验
 */
function submitBeforeCheck()
{
//	if($("#RES_ID").val()!=$("#RES_NO").val()){
//		alert("终端串不一致，请重新校验！");
//		return false;
//	}
//	if(!$.validate.verifyAll("widenetInfoPart")||!$.validate.verifyAll("topSetBoxInfoPart")) {
//		return false;
//	}
//	var isReturnTopsetbox=$("IS_RETURN_TOPSETBOX");
//	if(!isReturnTopsetbox||isReturnTopsetbox==""){
//		alert("请选择是否退还机顶盒！");
//		return false;
//	}
//	var isHasFee=$("#IS_HAS_FEE").val();
//	if(isHasFee=="1"){
		if(!$.validate.verifyAll("submitPart")||!$.validate.verifyAll("submitPart")) {
			return false;
		}
//	}
		
		//BUS201907310012关于开发家庭终端调测费的需求
		var commFeeTag = $("#COMMISSIONING_FEE_TAG").val();
		var commFee = $("#COMMISSIONING_FEE").val();
		var leftMonths = $("#LEFT_MONTHS").val();
		if(commFeeTag == "1")
		{
			var txt = "拆机后魔百和将不能正常使用，请确认要继续拆机吗?";
			if ($("#IS_RETURN_TOPSETBOX").val() == "0" )
				txt = "不归还机顶盒，要收取业务违约金（按照10元/月*未使用月数）和终端赔偿款（100元/部!请确认要继续拆机吗?";
			if ($("#IS_RETURN_TOPSETBOX").val() == "1" && Number(leftMonths) > 0 && Number(leftMonths) <=24 )
				txt = "归还机顶盒，未满2年要收取业务违约金（按照10元/月*未使用月数）!请确认要继续拆机吗?";
			MessageBox.confirm("告警提示",txt,function(re){
				if(re=="ok"){
					$.cssubmit.submitTrade();
				}
			});
		}
		//BUS201907310012关于开发家庭终端调测费的需求
		else{
			return true;
		}
}