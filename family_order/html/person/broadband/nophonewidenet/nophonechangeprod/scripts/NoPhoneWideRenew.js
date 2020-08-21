//用戶认证结束之后执行的js方法
function refreshPartAtferAuth(data)
{
	$("#NOPHONE_SERIAL_NUMBER").val(data.get("USER_INFO").get("SERIAL_NUMBER"));
	var param = "&ROUTE_EPARCHY_CODE="+data.get("USER_INFO").get("EPARCHY_CODE")+"&USER_ID="+data.get("USER_INFO").get("USER_ID");
	param += "&SERIAL_NUMBER="+data.get("USER_INFO").get("SERIAL_NUMBER");
	
    $.beginPageLoading("宽带资料查询。。。");
	 $.ajax.submit('AuthPart,hiddenPart', 'loadChildInfo', param, 'wideInfoPart,BusiInfoPart', function(data){
		 $.feeMgr.insertFee(data);
		 $.endPageLoading(); 		 	 
		},
		function(error_code,error_info){
			$.endPageLoading();
			$("#CSSUBMIT_BUTTON").attr("disabled", true).addClass("e_dis");
			alert(error_info);
	    });
}
//点击展示，隐藏客户信息按钮
function displayUserInfo(btn,o)
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
	var renewTag=$("#RENEW_TAG").val(); 
	var renewDec=$("#RENEW_DESC").val(); 
	var fee=$("#FEE").val()/100;
	var feeyear=$("#FEE_YEAR").val()/100;
	var feeday=$("#FEE_DAY").val()/100;
	var stopOpenTag=$("#STOP_OPEN_TAG").val(); //停机后续约标记，值为1则标明属于这种情况。空则不是。
	var stopopendesc="";
	if(stopOpenTag=="1"){
		stopopendesc="当前状态是停机后续约，首月按天收取剩余天数费用，按天算费用【"+feeday+"】，";
	} 
	if(renewTag=="0"){
		MessageBox.alert("告警提示","不允许办理续约业务，原因："+renewDec);
		return false;
	} 
	 
	var hiddenState = $("#HIDDEN_STATE").val(); 
	if(hiddenState!="")//已预约的
	{
		MessageBox.alert("告警提示","已办理预约拆机！请先办理【预约拆机取消】业务，再来办理宽带续约。");
		return false;
	} 
	//edit by zhangxing3 for 候鸟月、季、半年套餐（海南）
	var tipMsg = "";
	var discntCode = $("#DISCNT_CODE").val();
	if (discntCode == "84014240")
	{
		tipMsg = "提交成功后，您的宽带将续约一个月：其中度假宽带月套餐费用【"+feeyear+"】，"+stopopendesc+"总费用【"+fee+"】。确认继续吗?";
	}
	else if (discntCode == "84014241")
	{
		tipMsg = "提交成功后，您的宽带将续约三个月：其中度假宽带季套餐费用【"+feeyear+"】，"+stopopendesc+"总费用【"+fee+"】。确认继续吗?";
	}
	else if (discntCode == "84014242")
	{
		tipMsg = "提交成功后，您的宽带将续约半年：其中度假宽带半年套餐费用【"+feeyear+"】，"+stopopendesc+"总费用【"+fee+"】。确认继续吗?";
	}
	else{
		tipMsg = "提交成功后，您的宽带将续约一年：其中包年费用【"+feeyear+"】，"+stopopendesc+"总费用【"+fee+"】。确认继续吗?";
	}
	//edit by zhangxing3 for 候鸟月、季、半年套餐（海南）
	if(!window.confirm(tipMsg))
	{
		return false ;
	}
	
    var param = "&ROUTE_EPARCHY_CODE="+$.auth.getAuthData().get("USER_INFO").get("EPARCHY_CODE");
    $.cssubmit.addParam(param);
    return true;

}
 
