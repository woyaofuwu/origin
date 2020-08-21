var popupAuth=function(){
	//调用组件方法弹出密码认证框
	$.auth.popAuthCheck();
	//重载$.auth.afterAuthCheck方法，,改变原有执行动作
	$.extend($.auth, {"afterAuthCheck":function(data){

		if(data.containsKey("CHECK_MODE")){
			$.auth.cacheData["AUTH_DATA"].put("CHECK_MODE", data.get("CHECK_MODE"));
			$.auth.cacheData["AUTH_DATA"].put("CHECK_DESC", data.get("CHECK_MODE_DESC"));
		}
		var param = "&RESULT_CODE="+data.get("RESULT_CODE");
		param += "&RESULT_INFO="+data.get("RESULT_INFO");

		var authData = $.auth.getAuthData();
		param += "&USER_ID="+authData.get("USER_INFO").get("USER_ID");
		param += "&SERIAL_NUMBER="+authData.get("USER_INFO").get("SERIAL_NUMBER");
		param += "&EPARCHY_CODE="+authData.get("USER_INFO").get("EPARCHY_CODE");
		param += "&CITY_CODE="+authData.get("USER_INFO").get("CITY_CODE");
		param += "&PRODUCT_ID="+authData.get("USER_INFO").get("PRODUCT_ID");
		param += "&BRAND_CODE="+authData.get("USER_INFO").get("BRAND_CODE");
		param += "&CUST_ID="+authData.get("CUST_INFO").get("CUST_ID");
		param += "&CUST_NAME="+authData.get("CUST_INFO").get("CUST_NAME");
		
		var authParams = $.params.load($("#"+$.auth.popupHandler).val());
		param += "&USER_PASSWD="+authParams.get("USER_PASSWD");
		param += "&PSPT_ID="+authParams.get("PSPT_ID");

		$.cssubmit.addParam(param);
		
		$.cssubmit.bindCallBackEvent(function(rsd){
			var flag = "success";
			if(rsd.get("RESULT_CODE")=="1"){
				flag = "error";
			}
			$.cssubmit.showMessage(flag, "用户身份校验提示", rsd.get("RESULT_INFO"), false);
		});
		
		//继续提交
		$.cssubmit.submitTrade();
	}});
	//设置不自动提交业务
	return false;
};
