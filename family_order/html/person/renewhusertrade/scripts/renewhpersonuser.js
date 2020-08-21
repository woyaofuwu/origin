//用戶认证结束之后执行的js方法
function refreshPartAtferAuth(data)
{
	$("#H_SERIAL_NUMBER").val(data.get("USER_INFO").get("SERIAL_NUMBER"));
	var param = "&ROUTE_EPARCHY_CODE=" + data.get("USER_INFO").get("EPARCHY_CODE")
				+ "&USER_ID=" + data.get("USER_INFO").get("USER_ID")
				+ "&SERIAL_NUMBER=" + data.get("USER_INFO").get("SERIAL_NUMBER");
	
    $.beginPageLoading("资料查询。。。");
	 $.ajax.submit('AuthPart,hiddenPart', 'loadChildInfo', param, 'ProductInfoPart,BusiInfoPart', function(data){
		 $.endPageLoading(); 		 	 
		},
		function(error_code,error_info){
			$.endPageLoading();
			$("#CSSUBMIT_BUTTON").attr("disabled", true).addClass("e_dis");
			alert(error_info);
	    });
}
//点击展示，隐藏客户信息按钮
function displayUserInfo(btn, o)
{
	var button = $(btn);
    var div = $('#'+o);
    if (div.css('display') != "none")
    {
    	div.css('display', 'none');
    	button.children("i").attr('className', 'e_ico-unfold'); 
    	button.children("span:first").text("展示客户信息");
    }else {
    	div.css('display', '');
    	button.children("i").attr('className', 'e_ico-fold'); 
    	button.children("span:first").text("不展示客户信息");
    }
}

//业务提交----续约宽带
function submitBeforeCheck()
{
	var fee = $.feeMgr.findFeeInfo("7511", "2", "802");
	
	if (fee == null || parseInt(fee.get("PAY")) < 0) {
		MessageBox.alert("告警提示","和校园异网号码续费实缴金额不能小于等于0元!");
		return false;
	}
	
	var tipMsg = "提交成功后，您将续费【" + parseInt(fee.get("PAY")) / 100 + "元】。确认继续吗?";
	if (!window.confirm(tipMsg))
	{
		return false;
	}

	var param = "&ROUTE_EPARCHY_CODE=" + $.auth.getAuthData().get("USER_INFO").get("EPARCHY_CODE");
    $.cssubmit.addParam(param);
    return true;
}
 
