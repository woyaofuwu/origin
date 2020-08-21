

//用戶认证结束之后执行的js方法
function refreshPartAtferAuth(data)
{
	var param = "&ROUTE_EPARCHY_CODE="+data.get("USER_INFO").get("EPARCHY_CODE")+"&USER_ID="+data.get("USER_INFO").get("USER_ID");

    $.beginPageLoading("业务资料查询。。。");
	 $.ajax.submit('AuthPart', 'loadChildInfo', param, 'wideInfoPart,BusiInfoPart', function(data){
//		 	var warmType=data.get("WARM_TYPE");
//		 	if(warmType&&warmType=="1"){
//		 		MessageBox.alert("告警提示", "客户已办理魔百和业务，咨询客户是否取消。");
//		 	}
		 	
		 	$.endPageLoading();
		},
		function(error_code,error_info){
			$.endPageLoading();
			alert(error_info);
	    });
}



//业务提交
/*如果用户有积分，则提示先进行兑换*/
function submitBeforeCheck()
{
	if(!$.validate.verifyAll("DestroyInfoPart")) {
		return false;
	}
	//退光猫选项必填校验
	if($("#MODEM_RETUAN").val()=="")
	{
		MessageBox.alert("提示", "请选择是否退光猫","", null);
		return false;
	}
//	if(!$.validate.verifyAll("wideInfoPart")) {
//		return false;
//	}
	
    var param = "&ROUTE_EPARCHY_CODE="+$.auth.getAuthData().get("USER_INFO").get("EPARCHY_CODE");
	$.cssubmit.addParam(param);
    return true;

}


