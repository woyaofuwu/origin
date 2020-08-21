function refreshPartAtferAuth(data)
{
	$.ajax.submit('AuthPart', 'loadChildInfo', "&USER_INFO="+data.get("USER_INFO").toString()+"&CUST_INFO="+data.get("CUST_INFO").toString(), 'userInfoPart', function(data){
		$("#OLD_SIM_CARD_INFO").val(data);
		 $("#NEW_SIM_CARD_NO").attr("disabled",null);
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
		$.ajax.submit('AuthPart,userInfoPart','verifySimCard','','NewCardInfo',function(data)
		{ 
			$("#NEW_SIM_CARD_INFO").val(data);
			if(data.get("TIPS_TYPE")!=""||data.get("TIPS_TYPE")!=null){
				$("#ALERT_INFO").text(data.get("TIPS_INFO"));
				$("#ALERT_INFO_DIV").css("display","block");				
			}
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
  	if("ture" == $("#USER_SVC_STATE_TAG").val()){
     //开机操作
    	 MessageBox.confirm("提示信息","该用户状态为申请停机，选择[确定]将继续换卡，请在换卡后引导用户办理【报开】否则将不能正常使用，[取消]用户先办理【报开】后再来办理换卡业务！",function(btn){
			if(btn=="ok"){
				$("#OPEN_MOBILE_TAG").val("true");
			}else{
				$("#OPEN_MOBILE_TAG").val("false");
			}
		 });
		 
	//	 MessageBox.alert("提示","该用户状态为申请停机，选择[是]将继续换卡，请在换卡后引导用户办理【报开】否则将不能正常使用，[否]用户先办理【报开】后再来办理换卡业务！");
  	}
}


function test(data){
	//alert("new:"+$("#NEW_SIM_CARD_INFO").val());
	//alert("old:"+$("#OLD_SIM_CARD_INFO").val());
	 
}
//返回SIM卡类型和IMSI
function afterCheckSimCard(data){
	//返回SIM卡类型，和IMSI
	alert("1123122321312");
//	$("#SIM_CARD_TYPE_NAME").innerHTML="1111";
//	$("#IMSI").innerHTML="2222";
}
