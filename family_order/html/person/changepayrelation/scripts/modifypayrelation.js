
$(function(){
	$.acctInfo.pullWidget(3);//隐藏账户资料组件中第三列 [用户账期]
});


/**
 * 认证之后回调方法
 * @param data
 * @return
 */
function refreshPartAtferAuth(data)
{
	var param = "&USER_ID="+data.get("USER_INFO").get("USER_ID")
				+"&SERIAL_NUMBER="+data.get("USER_INFO").get("SERIAL_NUMBER")
				+"&ACCT_ID="+data.get("ACCT_INFO").get("ACCT_ID")
				+"&EPARCHY_CODE="+data.get("ACCT_INFO").get("EPARCHY_CODE");
	$.ajax.submit('', 'loadChildInfo',param , 'ChgAcctInfoPart,NewAcctPart', function(){
		$.acctInfo.delWidget(3);//隐藏账户资料组件中第三列 [用户账期]
		$("#CHG_ACCT_TYPE").attr('disabled',false);
		$("#PAY_MODE_CODE").unbind("change");//解除组件中绑定的事件
		$("#PAY_MODE_CODE").bind("change",onChangePayMode);
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}

function onChangePayMode(){
	var payModeCode = $("#PAY_MODE_CODE").val();
	if(payModeCode!='' && payModeCode!='0' && payModeCode!='5'){
		//分散帐期添加校验，如果帐期不为自然月，提示信息
		var acctDay = $.auth.getAuthData().get('ACCTDAY_INFO').get('ACCT_DAY');
		if(acctDay != 1){
			var flag = window.confirm('该用户的帐期日为'+acctDay+'号；托收后将变更为自然月[1号]，是否继续？');
			if(!flag){
				$('#PAY_MODE_CODE').val("0");
				return;
			}
		}
	}
	//继续执行组件中的绑定事件
	$.acctInfo.events.onChangePayModeCode();
}

/**
 * 变更帐户类型 
 * @return
 */
function chgAcctType()
{
	var chgAcctType = $("#CHG_ACCT_TYPE");
	
	if(chgAcctType.val() == "0")
	{
		alert("只能选择【新增帐户】！");
		chgAcctType.val("");
		return ;
	}
	
}

/**
 * 提交时校验
 * @return
 */
function submitBeforeCheck()
{
	var iUserPrepayTag = $.auth.getAuthData().get('USER_INFO').get('PREPAY_TAG');
	var payModeCode = $("#PAY_MODE_CODE").val();
	 if("1" == iUserPrepayTag && "0"!= payModeCode){
		 alert("预付费用户不能过到非现金帐户！");
		 return false;
	  }
	if(!$.validate.verifyAll()) return false;
    $.cssubmit.addParam("&SERIAL_NUMBER="+$.auth.getAuthData().get("USER_INFO").get("SERIAL_NUMBER"));
	return true;
}