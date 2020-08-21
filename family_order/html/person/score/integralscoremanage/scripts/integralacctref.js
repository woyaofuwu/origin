function refreshPartAtferAuth(data)
{
	debugger;
	$.ajax.submit('', 'getCommInfo', "&USER_ID="+data.get("USER_INFO").get("USER_ID"), 'hide',function(){
		$("#COND_QUERY_LIMIT").attr('disabled',false);
		$("#COND_QUERY_VALUE").attr('disabled',false);
		$("#COND_CUSTOMER_ID").attr('disabled',false);
		$("#COND_VIP_NO").attr('disabled',false);
		$("#qryBtn").attr('disabled',false);
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}

function queryIntegralAccts(){
	var limit = $("#COND_QUERY_LIMIT").val();
	var sss = $("#COND_QUERY_VALUE").val();

	if(limit == ''){
		 alert('请选择查询条件!');
		 $("#COND_QUERY_LIMIT").focus();
		 return false;
	}
	
	if(sss == ''){
		alert('请输入查询值!');
		$("#COND_QUERY_VALUE").focus();
		return false;
	}
	
	if(limit == '1'){
		if(sss != null && sss != ''){
		    if(!$.verifylib.checkEmail(sss))
		    {
		       alert('请输入有效的eMail!');
		       $("#COND_QUERY_VALUE").focus();
		       return false;
		    }
    	}
	}
	
	if(limit == '2'){
		if(sss != null && sss != ''){
	        if(!$.verifylib.checkMbphone(sss))
	        {
	            alert('请输入有效的手机号码!');
	            $("#COND_QUERY_VALUE").focus();
	            return false;
	        }
    	}
	}
	$.ajax.submit('QueryPart', 'queryScoreAcctList', '', 'AcctTablePart');
}

//业务提交
function onTradeSubmit(form)
{
	var refAcctId = $("#REF_INTEGRAL_ACCT_ID").val();
	var userAcctId = $("#USER_INTEGRAL_ACCT_ID").val();
	if(refAcctId == '')
	{
		alert('请选择关联积分账户!');
		return false;
	}
	if(refAcctId == userAcctId)
	{
		alert('所选关联积分账户与用户积分账户一致，请重新选择!');
		return false;
	}
	var sn =$("#AUTH_SERIAL_NUMBER").val();
	var param = '&REF_INTEGRAL_ACCT_ID='+refAcctId+'&SERIAL_NUMBER='+sn;
	$.cssubmit.addParam(param);
	$.cssubmit.submitTrade(param);
}

function saveAcctId(obj)
{
	acctId=$(obj).attr("acctId");
	$("#REF_INTEGRAL_ACCT_ID").val(acctId);
}
