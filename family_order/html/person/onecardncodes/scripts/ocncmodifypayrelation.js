
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
		$("#PAY_MODE_CODE").bind("change",onChangePayMode);
	},
	function(error_code,error_info){
		$.endPageLoading();
		$("#CSSUBMIT_BUTTON").attr("disabled",true).removeClass("e_dis");
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
				$('#newSnInfo_PAY_MODE_CODE').val("0");
				return;
			}
		}
	}
	$.acctInfo.events.onChangePayModeCode;
}


/**
 * 提交时校验
 * @return
 */
function submitBeforeCheck()
{
	var chgaccttype = $("#CHG_ACCT_TYPE").val();
	if($("#ISACCT").val() == "1" && $("#CHG_ACCT_TYPE").val() =="0"){
		alert("该用户已经是绑定付费关系,不能再进行绑定付费");
		$("#CHG_ACCT_TYPE").val() ='0';
		return false;
	}
	if($("#ISACCT").val() == "0" && $("#CHG_ACCT_TYPE").val() =="1"){
		alert("该用户不是绑定付费关系,不能进行取消绑定");
		$("#CHG_ACCT_TYPE").val() ='1';
		return false;
	}
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


/*普通付费关系变更，界面选择“变更帐户类型”，选择“合帐”或“分帐”*/
function chgAcctType(obj)
{
	var chgaccttype = $("#CHG_ACCT_TYPE").val();
	if($("#ISACCT").val() == "1" && $("#CHG_ACCT_TYPE").val() =="0"){
		alert("该用户已经是绑定付费关系,不能再进行绑定付费");
		$("#CHG_ACCT_TYPE").val() ='0';
	}
	if($("#ISACCT").val() == "0" && $("#CHG_ACCT_TYPE").val() =="1"){
		alert("该用户不是绑定付费关系,不能进行取消绑定");
		$("#CHG_ACCT_TYPE").val() ='1';
	}
}