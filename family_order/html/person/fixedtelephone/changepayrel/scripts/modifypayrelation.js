
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
	$.ajax.submit('', 'loadChildInfo', "&USER_INFO="+data.get("USER_INFO").toString()+"&ACCT_INFO="+data.get("ACCT_INFO").toString(), 'ChgAcctInfoPart,NewAcctPart', function(){
		$.acctInfo.delWidget(3);//隐藏账户资料组件中第三列 [用户账期]
		$("#CHG_ACCT_TYPE").attr('disabled',false);
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
				$('#newSnInfo_PAY_MODE_CODE').val("0");
				return;
			}
		}
	}
	$.acctInfo.events.onChangePayModeCode;
}

/**
 * 变更帐户类型 
 * @return
 */
/*普通付费关系变更，界面选择“变更类型”，选择“已有账户”或“新增帐户”*/
function chgAcctType()
{
	var chgaccttype = $("#CHG_ACCT_TYPE");
	var newsn = $("#newSnInfo_SERIAL_NUMBER");
	var custname = $("#newSnInfo_CUST_NAME");


	/*合帐处理*/
	if(chgaccttype.val() == '0')
	{
		chgaccttype.val('0');
		newsn.attr('disabled',false);
	}
	/*分帐处理*/
	else if(chgaccttype.val() == '1')
	{
		chgaccttype.val('1');
		newsn.attr('disabled',true);
		//payModeChg();
	}
}

/*普通付费关系变更，“变更帐户类型”选择“合帐”时，输入目标号码后的校验*/
function checkNorChgNewSn()
{
	var chgaccttype = $("#CHG_ACCT_TYPE");
	var newsn = $("#newSnInfo_SERIAL_NUMBER");
	var oldsn = $('#AUTH_SERIAL_NUMBER');
	var oldUserErea = $('#OLDUSEREREA').val();
	var oldAcct = $('#OLDACCTID').val();
	
	if(chgaccttype.val() != '1'&&chgaccttype.val() != '0'){
		alert("请选择变更关系类型");
		return;
	}
	if(trim(newsn.val()) == '')
	{
		alert('请输入目标号码');
		return;
	}
	
	if(chgaccttype.val() == '0' && trim(newsn.val()) == trim(oldsn.val()))
	{
		alert('合帐时，目标号码不能与原号码相同');
		return;
	}
	ajaxSubmit(this,'getNorChgNewSnInfo', '&SERIAL_NUMBER=' + trim(newsn.val())+'&OLDUSEREREA='+oldUserErea+'&OLDACCTID='+oldAcct, '',function(data){
		var num3 = data.get(0,"RSRV_NUM3");
	  	var acct_only =data.get(0,"ACCT_ONLY");
	  	var acct_id= data.get(0,"ACCT_ID");
		if(num3< 0){
		    if(confirm('该服务号码有实时欠费，是否继续？')){
				 $("#newSnInfo_SERIAL_NUMBER").val(data.get(0,"SERIAL_NUMBER"));
                 $("#newSnInfo_CUST_NAME").val(data.get(0,"CUST_NAME"));
		   	}else{
		   		$("#newSnInfo_CUST_NAME").val('');
		   		$("#newSnInfo_CUST_NAME").val('');
				}
		}

		if(num3>=0){
			 $("#newSnInfo_SERIAL_NUMBER").val(data.get(0,"SERIAL_NUMBER"));
             $("#newSnInfo_CUST_NAME").val(data.get(0,"CUST_NAME"));
			/*如果是合帐，则刷新代表号*/
			if($("#newSnInfo_CHG_ACCT_TYPE").val()== '0')
			{
				$('#newSnInfo_DEBUTY_CODE').value = data.get(0,"DEBUTY_CODE");
			}
		
		 } 
		
		$('#newSnInfo_DEBUTY_CODE').attr('disabled',false);
		$('#newSnInfo_CUST_NAME').attr('disabled',false);
		$('#CHANGEACCTID').val(acct_id);
		alert("校验成功");
	},null,null);
}


/*普通付费关系变更，合帐，目标号码校验完后，刷新界面上的部分信息*/
function ajaxNorChgNewSn(data)
{ 
	  	var num3 = data.get(0,"RSRV_NUM3");
	  	var acct_only =data.get(0,"ACCT_ONLY");
	  	var acct_id= data.get(0,"ACCT_ID");
  
		if(num3< 0){
		    if(confirm('该服务号码有实时欠费，是否继续？')){
				 $("#newSnInfo_SERIAL_NUMBER").val(data.get(0,"SERIAL_NUMBER"));
                 $("#newSnInfo_CUST_NAME").val(data.get(0,"CUST_NAME"));
		   	}else{
		   		$("#newSnInfo_CUST_NAME").val('');
		   		$("#newSnInfo_CUST_NAME").val('');
				}
		}

		if(num3>=0){
			 $("#newSnInfo_SERIAL_NUMBER").val(data.get(0,"SERIAL_NUMBER"));
             $("#newSnInfo_CUST_NAME").val(data.get(0,"CUST_NAME"));
			/*如果是合帐，则刷新代表号*/
			if($("#newSnInfo_CHG_ACCT_TYPE").val()== '0')
			{
				$('#newSnInfo_DEBUTY_CODE').val(data.get(0,"DEBUTY_CODE"));
			}
		
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
	return true;
}