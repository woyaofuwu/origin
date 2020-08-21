$(function(){
	if (!$.enterpriseLogin || !$.enterpriseLogin.isLogin()) 
	{
		MessageBox.alert("提示信息","请先在外框认证政企客户信息！", function(btn) {
			if(window.location.href.indexOf("/iorder?") > 0)
			{//界面必须在外框登录集团
				closeNav();
			}
		});
	}
});


function query(){
		$.beginPageLoading("数据查询中......");
		var cond_SERIAL_NUMBER=$("#cond_SERIAL_NUMBER").val();
		var cond_FINISHTAG=$("#cond_FINISHTAG").val();

		$.ajax.submit("", "queryInfos", "&cond_SERIAL_NUMBER="+cond_SERIAL_NUMBER+"&cond_FINISHTAG="+cond_FINISHTAG, "refreshtable", function(data){
				if(data.get("FLAG")=='0'){
					$.endPageLoading("查询数据失败~");
				}
				$.endPageLoading();
			},
			function(error_code,error_info, derror){
				$.endPageLoading();
				showDetailErrorInfo(error_code,error_info,derror);
	    	}
	    );

	
}

