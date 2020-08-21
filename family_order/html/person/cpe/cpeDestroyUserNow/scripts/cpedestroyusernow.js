//用戶认证结束之后执行的js方法
function refreshPartAtferAuth(data)
{
	 $.beginPageLoading("正在查询数据...");
	 var serialNumber = data.get("USER_INFO").get("SERIAL_NUMBER");
	 var userId = data.get("USER_INFO").get("USER_ID");
	 var eparchyCode = data.get("USER_INFO").get("EPARCHY_CODE");
	 var param = "&SERIAL_NUMBER="+serialNumber+"&USER_ID="+userId+"&EPARCHY_CODE="+eparchyCode;
	 
	 $.ajax.submit('AuthPart', 'setPageCustInfo', param, 'OtherInfoPart,FallbackInfoPart', function(data){
			//MessageBox.confirm("提示",data.get("message"),afterTrade);
		    $.endPageLoading();
		},
		function(error_code,error_info){
			$.endPageLoading();
			alert(error_info);
	    });
}

/**
 * 改变是否退回CPE终端状态
 */
function  changeFallBackTag(){
	var fallbackTag=$("#FALLBACKTAG").val();
	if(fallbackTag == 1){
		//显示
		$('#REMOVE_REASON_TAG').css('display','block');
	}else{
		//隐藏
		$('#REMOVE_REASON_TAG').css('display','none');
		//清空内容
		$("#REMOVE_REASON").val("");
	}
	
}

/**
 * 初始化页面参数
 * */
function resetPage(jwcidMark){
	$.beginPageLoading("努力刷新中...");
	$.ajax.submit('','','',jwcidMark,function(data){
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
	});
}


function onTradeSubmit()
{
	if(!$.validate.verifyAll("FallbackInfoPart")) {
		return false;
	}
   //是否退还标志 
   var fallbackTag=$("#FALLBACKTAG").val();
   //退回原因
   var removeReason=$("#REMOVE_REASON").val();
   if(fallbackTag == 1){
	   //不退回
	   //原因必填
	   if(removeReason == ''){
		   alert("请填写不退回原因,谢谢！");
		   return false;
	   }
   }
	
	var param = '&SERIAL_NUMBER='+$("#AUTH_SERIAL_NUMBER").val();
	//param += '&REMOVE_REASON='+$('#REMOVE_REASON').val();  
	//param += '&FALLBACKTAG='+$('#FALLBACKTAG').val();  
	$.cssubmit.addParam(param); 
	return true;
}

