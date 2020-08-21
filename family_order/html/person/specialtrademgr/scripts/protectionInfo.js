
function refreshPartAtferAuth(data)
{
	var user_info = data.get("USER_INFO").toString();
	var cust_info = data.get("CUST_INFO").toString();
	var acct_info = data.get("ACCT_INFO").toString();
	
	
	var param = "&USER_INFO="+user_info+"&CUST_INFO="+cust_info+"&ACCT_INFO="+acct_info;
	$.beginPageLoading("数据处理中...");
	$.ajax.submit('AuthPart', 'loadChildInfo', param, 'QueryPartTwo,custInfoPart,CustInfoTwoPart', function(data){
		doAfter(data);
		$.endPageLoading();
	},
	function(error_code,error_info)
	{
		$.endPageLoading();
		alert(error_info);
    });
}

function doAfter(ajaxDataset)
{
	var rs = ajaxDataset.get(0, "FLAG");
	if(rs == "0")
	{
		$("#OptCheckBox").attr("checked", false);
		$("#cond_SVC_PASSWD").attr("disabled", false);
	}
	else
	{
		$("#OptCheckBox").attr("checked", true);
		$("#cond_SVC_PASSWD").attr("disabled", true);
	}
	return true;
}

function showOne()
{
	var flag = $("#OptCheckBox").attr("checked");
	if(flag)
	{
		var flagOper  = $("#FLAG").val();
		if(flagOper=="1")
		{
			$("#cond_SVC_PASSWD").attr("disabled", true);
		}
		else
		{
			$("#cond_SVC_PASSWD").attr("disabled", false);
		}
		
	}
	else
	{
		
		$("#cond_SVC_PASSWD").attr("disabled", true);
	}
	
}

function checkAll()
{

	//查询条件校验
	if(!$.validate.verifyAll("CustInfoTwoPart")) {
		return false;
	}
	
	var flag  = $("#FLAG").val();
	if(flag=="1")
	{
		if($("#OptCheckBox").attr("checked"))
		{
			alert("业务没有做任何修改，不允许提交!");
			return false;
		}
	}
	else
	{
		if(!$("#OptCheckBox").attr("checked"))
		{
			alert("业务没有做任何修改，不允许提交!");
			return false;
		}
		else{
			if($("#cond_SVC_PASSWD").val() == ''){
				alert('详单密码不能为空!');
				return false;
			}
		}
	}
	 
	return true;
}
  
 
 












