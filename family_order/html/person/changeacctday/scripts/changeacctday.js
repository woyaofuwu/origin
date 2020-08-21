function refreshPartAtferAuth(data)
{
	var oldAcctDay=$.auth.getAuthData().get('ACCTDAY_INFO').get('ACCT_DAY');
	$("#custInfo_OLD_ACCT_DAY").val(oldAcctDay);//设置老的账期日
	$("#custInfo_NEW_ACCT_DAY").attr('disabled',false);
	$("#REMARK").attr('disabled',false);
	if("TRUE" == $("#ISGRP").val())
	{
		$("#REMARK").val($("#GRP_REMARK").val());
	}
	
	cleanData();
}

function changeAcctDay(){
	var oldAcctDay = $("#custInfo_OLD_ACCT_DAY").val();
	var newAcctDay = $("#custInfo_NEW_ACCT_DAY").val();
	
	if(newAcctDay != null && newAcctDay != ""){
		if(oldAcctDay == "1" && newAcctDay == "1"){
			alert("用户当前账期日为1日,新账期日不能为1日!");
			cleanData();
		}else if(oldAcctDay != "1" && newAcctDay != "1"){
			alert("用户当前账期日不为1日,新账期日只能为1日!");
			cleanData();
		}else{
			$.beginPageLoading("获取数据中..");
			var userId=$.auth.getAuthData().get('USER_INFO').get('USER_ID');
			var params = "&ACCT_DAY="+newAcctDay+"&USER_ID="+userId+"&SERIAL_NUMBER="+$("#AUTH_SERIAL_NUMBER").val();
			$.ajax.submit('', 'getNewAcctDayByModify',params , '', 
			function(data)
			{
				$("#custInfo_NEXT_CHECK_DATE").val(data.get("NEXT_CHECK_DATE"));
				$("#custInfo_CHG_CHECK_FIRST_DATE").val(data.get("CHG_CHECK_FIRST_DATE"));
				$.endPageLoading();
			},
			function(error_code,error_info){
				$.endPageLoading();
				alert(error_info);
		    });
		}
	}else{
		alert("请选择用户新的账期日!");
		cleanData();
	}
};

function cleanData(){
	$("#custInfo_NEW_ACCT_DAY").val("");
	$("#custInfo_NEXT_CHECK_DATE").val("");
	$("#custInfo_CHG_CHECK_FIRST_DATE").val("");
};

/*提交*/
function onTradeSubmit()
{
	if(!$.validate.confirmAll()) return false;
	var oldAcctDay = $("#custInfo_OLD_ACCT_DAY").val();
	var newAcctDay = $("#custInfo_NEW_ACCT_DAY").val();
	if(oldAcctDay == newAcctDay){
		alert("用户当前账期日与新账期日一致,无需提交!");
		return false;
	}

	return true;
}


function refreshPartBeforeAuth()
{
	if("TRUE" == $("#ISGRP").val())
	{
		var param = "&ISGRP=TRUE";
		$.tradeCheck.addParam(param);
	}
}
