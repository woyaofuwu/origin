
function refreshPartAtferAuth(data)
{
	$.ajax.submit('AuthPart', 'loadChildInfo', "&USER_ID="+data.get("USER_INFO").get("USER_ID") +
		"&SERIAL_NUMBER="+data.get("USER_INFO").get("SERIAL_NUMBER"), 'refreshParts1', function()
	{
		
	});
}

function chkApnSelect(obj) 
{
	if(obj)
	{
		var apnInstId = $(obj).val();
		var apnUserInfos = $(obj).parent().siblings(); 
		if(apnUserInfos)
		{
			var apnName = $(apnUserInfos[0]).text();
			var apnDesc = $(apnUserInfos[1]).text();
			var apnCntxid = $(apnUserInfos[2]).text();
			var apnTplid = $(apnUserInfos[3]).text();	
			var apnType = $(apnUserInfos[5]).text();
			var apnIpv4Add = $(apnUserInfos[6]).text();
		
			$("#APN_NAME").val(apnName);
			$("#APN_DESC").val(apnDesc);
			$("#APN_CNTXID").val(apnCntxid);
			$("#APN_TPLID").val(apnTplid);
			$("#APN_TYPE").val(apnType);
			$("#APN_IPV4ADD").val(apnIpv4Add);
			$("#APN_INST_ID").val(apnInstId);
		}
	}
}


//业务提交
function submitTrade(form)
{
	if (!$.validate.verifyAll())
	{
		return false;
	}
	
	var apnInstId = $("#APN_INST_ID").val();

	if(apnInstId == null || apnInstId == "")
	{
		alert("请选择要删除的专用APN绑定信息!");
		return false;
	}
		
	//var userId = $("#USER_ID").val();
	var param = 'SERIAL_NUMBER=' + $("#AUTH_SERIAL_NUMBER").val();
	//param += '&COMM_ID=' + $("#COMM_ID").val();
	//param += '&EFFECTIVE_DATE=' + $("#EFFECTIVE_DATE").val();
	//param += '&EXPIRE_DATE=' + $("#EXPIRE_DATE").val();
	
	$.cssubmit.addParam(param);
	return true;
	//$.cssubmit.submitTrade(param);
}


