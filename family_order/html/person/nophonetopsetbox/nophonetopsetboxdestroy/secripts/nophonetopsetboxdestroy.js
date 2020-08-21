//用戶认证结束之后执行的js方法
function refreshPartAtferAuth(data)
 {
	$.beginPageLoading("信息加载中......");
	$.ajax.submit('AuthPart', 'loadPageInfo', "&USER_INFO="+ data.get("USER_INFO").toString() + "&CUST_INFO=" + data.get("CUST_INFO").toString(), 'userInfoPart,topSetBoxInfoPart',
			function(data) {
				var isHasFee=data.get("IS_HAS_FEE");  // 1:收取了押金，0：没收押金
				if(isHasFee=="1"){		//显示是否退还机顶盒
					$("#isBackSet").css("display","block");
				}
				
				$.feeMgr.removeFee($("#TRADE_TYPE_CODE").val(),"0","515");
				if(data.get("COMMISSIONING_FEE_TOPSETBOX")>0){
					var feeData = $.DataMap();
				     feeData.clear();
				     feeData.put("MODE", "0");
				     feeData.put("CODE", "515");
				     feeData.put("FEE", data.get("COMMISSIONING_FEE_TOPSETBOX"));
				     feeData.put("PAY", data.get("COMMISSIONING_FEE_TOPSETBOX"));
				     feeData.put("TRADE_TYPE_CODE", $("#TRADE_TYPE_CODE").val());
				     $.feeMgr.insertFee(feeData);
				}
			
				
				$.cssubmit.disabledSubmitBtn(false,"submitButton");
				$.endPageLoading();
			}, function(error_code, error_info, derror) {
				$.cssubmit.disabledSubmitBtn(true,"submitButton");
				$.endPageLoading();
				showDetailErrorInfo(error_code, error_info, derror);
			});
}

function setModemNumeric()
{
	$.feeMgr.removeFee($("#TRADE_TYPE_CODE").val(),"0","517");
	if($("#IS_RETURN_TOPSETBOX").val()=="0"){
		$("#modemFeePart").css('display', '');
		var feeData = $.DataMap();
	     feeData.clear();
	     feeData.put("MODE", "0");
	     feeData.put("CODE", "517");
	     feeData.put("FEE", "10000");
	     feeData.put("PAY", "10000");
	     feeData.put("TRADE_TYPE_CODE", $("#TRADE_TYPE_CODE").val());
	     $.feeMgr.insertFee(feeData);
		
	}else{
		$("#modemFeePart").css('display', 'none');
	}
	
}



/**
 * 业务提交校验
 */
function submitBeforeCheck()
{
	var isHasFee=$("#IS_HAS_FEE").val();
	if(isHasFee=="1"){
		if(!$.validate.verifyAll("submitPart")||!$.validate.verifyAll("submitPart")) {
			return false;
		}
	}

	return true;
}