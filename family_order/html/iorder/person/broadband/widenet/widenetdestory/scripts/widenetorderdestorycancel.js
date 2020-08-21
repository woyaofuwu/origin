//用戶认证结束之后执行的js方法
function refreshPartAtferAuth(data)
{ 
	var param = "&ROUTE_EPARCHY_CODE="+data.get("USER_INFO").get("EPARCHY_CODE")+"&USER_ID="+data.get("USER_INFO").get("USER_ID");
	param += "&SERIAL_NUMBER="+data.get("USER_INFO").get("SERIAL_NUMBER");
	
    $.beginPageLoading("宽带资料查询。。。");
	 $.ajax.submit('AuthPart,hiddenPart', 'loadChildInfo', param, 'wideInfoPart,BusiInfoPart,destroyInfoPart', function(data){
//		 	var warmType=data.get("WARM_TYPE");
//		 	if(warmType&&warmType=="1"){
//		 		MessageBox.alert("告警提示", "客户已办理魔百和业务，咨询客户是否取消。");
//		 	}
		 	
//		 	var pactive = data.get("ACTIVE_FLAG");
//		 	if(pactive&&pactive=="1"){
//		 		MessageBox.alert("告警提示", "您有宽带类营销活动，请使用宽带特殊拆机功能。");
//		 	}else
//		 	if(pactive&&pactive=="2"){
//		 		MessageBox.alert("告警提示", "您有宽带类包年套餐，请使用宽带特殊拆机功能。");
//		 	}
		 	$.endPageLoading();
		},
		function(error_code,error_info){
			$.endPageLoading();
			MessageBox.alert("提示",error_info);
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
		  button.empty();
		  button.html('<span class="e_ico-unfold"></span>展示客户信息'); 
    }else {
       div.css('display', '');
       button.empty();
	   button.html('<span class="e_ico-fold"></span>隐藏客户信息'); 
    }
}



//提交校验----预约拆机
function onSubmit()
{

	var hiddenState = $("#HIDDEN_STATE").val();
	if(hiddenState=="")//已预约的
	{
		MessageBox.alert("告警提示","没有办理预约拆机！不能办理预约拆机取消业务！");
		return false;
	}

	return true;
}
