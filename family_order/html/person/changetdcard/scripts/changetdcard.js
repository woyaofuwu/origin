function refreshPartAtferAuth(data)
{
	$.ajax.submit('AuthPart', 'loadChildInfo', "&USER_INFO="+data.get("USER_INFO").toString()+"&CUST_INFO="+data.get("CUST_INFO").toString(), 'userInfoPart,NewCardInfo', function(data){
		$("#OLD_SIM_CARD_INFO").val(data);
		 $("#NEW_SIM_CARD_NO").attr("disabled",null);
		 $("#SIM_CARD_NO").val("");
		 $("#SIM_TYPE_CODE").val("");
		 $("#IMIS").val("");
		 specCheck();
		 
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
	});
}

//校验新sim卡
function checkSIM(tag,data) {
	 
	$("#NEW_SIM_CARD_INFO").val(data.get(0));
	if(tag==1){
		$.beginPageLoading();
		$.ajax.submit('AuthPart,userInfoPart','verifySimCard',data.get(0),'NewCardInfo',function(data)
		{ 
			$("#NEW_SIM_CARD_INFO").val(data);
			$("#SIM_CHECK_FLAG").val("1");//设置sim卡检查通过标识
			
			//检查是否是4G卡
			if ($("#LTE_4G").val()==4){
				$.ajax.submit('','verifySimcardUSIM',data,'',function(data)
				{
					if( data.get(0) && data.get(0).get('NOTICE_FLAG') == "1"){
						alert(data.get(0).get('NOTICE_CONTENT'));
					}
				},
				function(error_code,error_info){
					$.endPageLoading();
					alert(error_info);
				});
			}
			
		  	$.endPageLoading();
		  		
		},function(errorcode,errorinfo){
			alert(errorinfo);
			$.endPageLoading();
		});
	}else{
		
	}
}

//如果查询到的用户主服务状态为人工停机，则弹出“是否同时办理开机业务”对话框
function specCheck(obj)
{
  	if("true" == $("#USER_SVC_STATE_TAG").val()){
     //开机操作
    	 MessageBox.confirm("提示信息","是否同时办理开机业务？",function(btn){
			if(btn=="ok"){
				$("#OPEN_MOBILE_TAG").val("true");
			}else{
				$("#OPEN_MOBILE_TAG").val("false");
			}
		 });
  	}
}

function checkBeforeSubmit()
{
	if(!$.validate.verifyAll("NewCardInfo")) {
		return false;
	}
	
	if("" == $("#SIM_CHECK_FLAG").val()){
		alert("请先检查新SIM卡");
		return false;
	}
	return true;
}