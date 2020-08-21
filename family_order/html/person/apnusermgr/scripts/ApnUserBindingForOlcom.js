
function refreshPartAtferAuth(data)
{
	$.ajax.submit('BalancePart', 'loadChildInfo', "&USER_ID="+data.get("USER_INFO").get("USER_ID") +
		"&SERIAL_NUMBER="+data.get("USER_INFO").get("SERIAL_NUMBER"), 'refreshParts1', function()
	{
		/*$("#CHNAGE_PHONE").attr('disabled',false);
		$("#VALUE_CARD").css('display','none');
		$("#OBJECT_PHONE").css('display','none');
		$("#otherinfo_OBJECT_SERIAL_NUMBER").val('');
		$("#otherinfo_OBJECT_SERIAL_NUMBER").bind("keydown",serialNumberKeydown);*/
	});
}

function showApnIpvAddress()
{
	var apnType = $("#APN_TYPE").val();
	if(apnType != null && apnType != "")
	{
		/*
		1	2G-APN动态地址
		2	2G-APN静态地址
		3	4G-APN动态地址
		4	4G-APN静态地址
		*/
		if(apnType == "1" || apnType == "3")
		{
			$("#PdnIp4AddPart").css("display","none");
			$("#PdnIp4AddRequiredLbl").removeClass("e_required")
			$("#APN_IPV4ADD").val("");
		}
		else if(apnType == "2" || apnType == "4")
		{
			$("#PdnIp4AddPart").css("display","");
			$("#PdnIp4AddRequiredLbl").addClass("e_required");
			$("#APN_IPV4ADD").val("");
		}
		else 
		{
			$("#PdnIp4AddPart").css("display","");
			$("#PdnIp4AddRequiredLbl").removeClass("e_required");
			$("#APN_IPV4ADD").val("");
		}
	}
	else 
	{
		$("#PdnIp4AddPart").css("display","");
		$("#PdnIp4AddRequiredLbl").removeClass("e_required");
		$("#APN_IPV4ADD").val("");
	}
}

//业务提交
function submitTrade(form)
{
	if (!$.validate.verifyAll())
	{
		return false;
	}
	
	var apnName = $("#APN_NAME").val();
	var apnDesc = $("#APN_DESC").val();
	var apnCntxid = $("#APN_CNTXID").val();
	var apnTplid= $("#APN_TPLID").val();
	var apnType = $("#APN_TYPE").val();
	var apnIp4Add = $("#APN_IPV4ADD").val();
	
	if(apnName == null || apnName == "")
	{
		alert("APN名称不能为空!");
		return false;
	}
	if(apnDesc == null || apnDesc == "")
	{
		alert("APN描述不能为空!");
		return false;
	}
	
	if(apnTplid == null || apnTplid == "")
	{
		alert("APNQOS的模板ID不能为空!");
		return false;
	}
	
	if(apnCntxid == null || apnCntxid == "")
	{
		alert("APN的CNTXID不能为空!");
		return false;
	}
	
	if(apnCntxid == 0)
	{
		alert(1);
		alert("APN的CNTXID的范围值只能是1-50,请重新填写!");
		return false;
	}
	if(apnCntxid > 50)
	{
		alert(2);
		alert("APN的CNTXID的范围值只能是1-50,请重新填写!");
		return false;
	}
	
	if(apnType == null || apnType == "")
	{
		alert("请选择PDN IPV4ADD类型!");
		return false;
	}
	
	if(apnType == "2" || apnType == "4")
	{
		if(apnIp4Add == null || apnIp4Add == "")
		{
			alert("APN静态地址时,请填写PDN IPV4ADD类型地址!");
			return false;
		}
	}
	
	var title = $("#APN_TPLID option:selected").attr("title");
	if(apnType == "1")
	{
		if(title != "APN_LX2G_TPLID" && title != "APN_ZX2G_TPLID")
		{
			alert("[PDN IPV4ADD类型]为2G-APN动态地址,[APNQOS的模板ID]只能选择2G离线或2G在线!");
			return false;
		}
		
	}
	else if(apnType == "2")
	{
		if(title != "APN_LX2G_TPLID" && title != "APN_ZX2G_TPLID")
		{
			alert("[PDN IPV4ADD类型]为2G-APN静态地址,[APNQOS的模板ID]只能选择2G离线或2G在线!");
			return false;
		}
	}
	else if(apnType == "3")
	{
		if(title != "APN_LX4G_TPLID" && title != "APN_ZX4G_TPLID")
		{
			alert("[PDN IPV4ADD类型]为4G-APN动态地址,[APNQOS的模板ID]只能选择4G离线或4G在线!");
			return false;
		}
	}
	else if(apnType == "4")
	{
		if(title != "APN_LX4G_TPLID" && title != "APN_ZX4G_TPLID")
		{
			alert("[PDN IPV4ADD类型]为4G-APN静态地址,[APNQOS的模板ID]只能选择4G离线或4G在线!");
			return false;
		}
	}	
			
		
	//var userId = $("#USER_ID").val();
	var param = 'SERIAL_NUMBER=' + $("#AUTH_SERIAL_NUMBER").val();
	//param += '&USER_ID=' + userId;
	
	$.cssubmit.addParam(param);
	return true;
	//$.cssubmit.submitTrade(param);
}

function showApnName()
{  
	var apnName = $("#APN_NAME").val();
	$.beginPageLoading("正在查询数据...");
	$.ajax.submit('','queryUserApnByName','&APN_NAME=' + apnName ,'',
		function(data){
			$.endPageLoading();
			if(data && data.length > 0)
			{
				$("#APN_DESC").val(data.get("APN_DESC"));
				var apnLx2gTplId = data.get("APN_LX2G_TPLID");
				var apnZx2gTplId = data.get("APN_ZX2G_TPLID");
				var apnLx4gTplId = data.get("APN_LX4G_TPLID");
				var apnZx4gTplId = data.get("APN_ZX4G_TPLID");
				
				$('#APN_TPLID').empty();
				$("#APN_TPLID").append("<option value=\"\">--请选择--</option>");
				if(apnLx2gTplId != null && apnLx2gTplId != "")
				{
					$("#APN_TPLID").append("<option title=\"APN_LX2G_TPLID\"" + "value=\"" 
						+ apnLx2gTplId + "\">" + apnLx2gTplId + "|2G离线</option>");
				}
				
				if(apnZx2gTplId != null && apnZx2gTplId != "")
				{
					$("#APN_TPLID").append("<option title=\"APN_LX2G_TPLID\"" + "value=\"" 
						+ apnZx2gTplId + "\">" + apnZx2gTplId + "|2G在线</option>");
				}
				
				if(apnLx4gTplId != null && apnLx4gTplId != "")
				{
					$("#APN_TPLID").append("<option title=\"APN_LX4G_TPLID\"" + "value=\"" 
						+ apnLx4gTplId + "\">" + apnLx4gTplId + "|4G离线</option>");
				}
				
				if(apnZx4gTplId != null && apnZx4gTplId != "")
				{
					$("#APN_TPLID").append("<option title=\"APN_ZX4G_TPLID\"" + "value=\"" 
						+ apnZx4gTplId + "\">" + apnZx4gTplId + "|4G在线</option>");
				}
			}
			else 
			{
				$("#APN_DESC").val("");
			}
		},
		function(error_code,error_info){
			$.MessageBox.error(error_code,error_info);
			$.endPageLoading();
		}
	);
}


