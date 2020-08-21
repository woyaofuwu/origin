//用戶认证结束之后执行的js方法
function refreshPartAtferAuth(data)
{
	 var serialNumber = data.get("USER_INFO").get("SERIAL_NUMBER");
	 var userId = data.get("USER_INFO").get("USER_ID");
	 var eparchyCode = data.get("USER_INFO").get("EPARCHY_CODE");
	 var param = "&SERIAL_NUMBER="+serialNumber+"&USER_ID="+userId+"&EPARCHY_CODE="+eparchyCode;
	 
	 $.ajax.submit('AuthPart', 'setPageCustInfo', param, 'BusiInfoPart,HiddenPart,DestroyInfoPart', function(data){
			//MessageBox.confirm("提示",data.get("message"),afterTrade);
			$("#REMOVE_REASON_CODE").attr("disabled", false);
			$("#REMARK").attr("disabled", false);
			$("#QUERY_BTN").attr("disabled", false);
			$("#QUERY_REST").attr("disabled", false);
			$("#QUERY_BTN").attr("className", "e_button-page-ok");
			$("#QUERY_REST").attr("className", "e_button-page");
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
	 var userId =$.auth.getAuthData().get("USER_INFO").get("USER_ID");
	 var eparchyCode= $.auth.getAuthData().get("USER_INFO").get("EPARCHY_CODE");
	
	
	//判断是否为海洋通 船东用户  船东不允许销户  start add by xuzh5 2018-7-2 15:09:10
	$.beginPageLoading("数据查询中..");
	var flag=0;
	$.ajax.submit('DestroyInfoPart', 'checkUserInfo', "&USER_ID="+userId, '', function(data){
		$.endPageLoading();
		flag=data.get('FLAG');
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    },{
		"async" : false
	});
	
	if(flag=='1'){
		MessageBox.alert("提示信息", "该用户为海洋通船东用户，不允许立即销户！");
		return false;
	}
	  //判断是否为海洋通 船东用户  船东不允许销户  end add by xuzh5 2018-7-2 15:09:10
	
	var serialNumber=$("#AUTH_SERIAL_NUMBER").val();
	var rs1 = $.ajax.check('DestroyInfoPart', 'checkScore','USER_ID='+userId
			   +'&SERIAL_NUMBER='+serialNumber+'&EPARCHY_CODE='+eparchyCode, 'SCORE', null);
	   
	 var score = rs1.rscode();
	 $("#SCORE").val(score);//赋值页面
	 if(score!=null && parseInt(score)>0)
	    {
			var message = '销户业务将清除用户积分,该用户当前积分为[' + score + ']分,';
			message += '如果要继续办理本业务,请点击【是】(系统自动将用户积分清零);';
			message += '否则请点击【否】终止办理.';
			MessageBox.confirm("提示",message,dealMsgBox);
		}
		else 	//默认继续处理
		{
			return true;
		}
}

function dealMsgBox(btn)
{
	if(btn=='ok')
	{
	    $.cssubmit.submitTrade();
	    return true;
	}
	else if(btn=='cancel')
	{
		$.endPageLoading();
		return false;
	}
}