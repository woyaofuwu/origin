 function changeQueryType() 
{
	var grpPart = $("#selectGroupPart");
	var choose = $("#QueryType").val();	
	
	if(choose == "0")   //按集团编码
	{  
		grpPart.css("display","");		
	}
	else if(choose == "1")   //按成员手机号码
	{ 
		grpPart.css("display","none");
	}   
}
//根据服务号码查成员信息
function queryMebBySN()
{
	var choose = $("#QueryType").val();	
	var sn = $("#cond_SERIAL_NUMBER");
	var serialValue = sn.val();	
	if(!$.validate.verifyField(sn))
	{
		return false;
	}
	//if(!checkMbphone(serialValue)){
	//	return false;
	//}
	
	if (!/^[1][134578][0-9](\d{8})$/.test(serialValue) && sn != "") {
		//alert('手机号码格式不正确！');
		//return false;
		$.beginPageLoading();
		$.ajax.submit('', 'queryCrmMsison', '&SERIAL_NUMBER='+serialValue, '', 
		function(data)
		{
			if("true" != data.get("IS_TRUE")){
				alert('手机号码格式不正确！');
				$.endPageLoading();
				return false;
			}
			$.endPageLoading();
			
			//放在ajax里执行-----------
			$.beginPageLoading();	
			if (choose == "0")  //按集团编码
			{ 
				$.ajax.submit('QryBlackWhiteOutPart', 'checkSerialNumber', null, null, 
				function(data)
				{
					$("#SERVICE_TYPE").attr("disabled","");
					$.endPageLoading();		
				},
				function(error_code,error_info,derror)
				{		
					$.endPageLoading();
					showDetailErrorInfo(error_code,error_info,derror);
				});
			} 
			else  //按成员手机号码
			{ 
		        $.ajax.submit('QryBlackWhiteOutPart', 'queryMemberInfo', null, 'MemInfo', 
				function(data)
				{
					var num = data.get("GROUPINFO_NUM");  //得到黑白名单数量
					if(num >0) 
					{
						var infos = data.get("GROUPINFOS");  //得到加入集团
						renderGrpList(infos);
					}
					$.endPageLoading();		
				},
				function(error_code,error_info,derror)
				{		
					$.endPageLoading();
					$("#cond_SERIAL_NUMBER").val("");
					showDetailErrorInfo(error_code,error_info,derror);
			    });
			}	
			//------
		},
		function(error_code,error_info,derror)
		{		
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
	    },{async:false});
	}else{
		//放在ajax里执行-----------
		$.beginPageLoading();	
		if (choose == "0")  //按集团编码
		{ 
			$.ajax.submit('QryBlackWhiteOutPart', 'checkSerialNumber', null, null, 
			function(data)
			{
				$("#SERVICE_TYPE").attr("disabled","");
				$.endPageLoading();		
			},
			function(error_code,error_info,derror)
			{		
				$.endPageLoading();
				showDetailErrorInfo(error_code,error_info,derror);
			});
		} 
		else  //按成员手机号码
		{ 
	        $.ajax.submit('QryBlackWhiteOutPart', 'queryMemberInfo', null, 'MemInfo', 
			function(data)
			{
				var num = data.get("GROUPINFO_NUM");  //得到黑白名单数量
				if(num >0) 
				{
					var infos = data.get("GROUPINFOS");  //得到加入集团
					renderGrpList(infos);
				}
				$.endPageLoading();		
			},
			function(error_code,error_info,derror)
			{		
				$.endPageLoading();
				$("#cond_SERIAL_NUMBER").val("");
				showDetailErrorInfo(error_code,error_info,derror);
		    });
		}	
		//------
	}
}
/**
 *判断是否符合手机号码格式
 *add by wusf
 */
function checkMbphone(sn){
	if (!/^[1][134578][0-9](\d{8})$/.test(sn) && sn != "") {
		alert('手机号码格式不正确！');
		return false;
	}
	return true;
}
function initGroupInfoForMem(data){
	var groupInfo =  data.get("GRP_CUST_INFO");
	initGroupInfo(groupInfo);
}
function initGroupInfo(data){
	var grpId = data.get("GROUP_ID");
	insertGroupCustInfo(data);
	$("#POP_cond_GROUP_ID").val(grpId);
	getServiceInfo();
}
function getServiceInfo()
{	
	var groupId = $("#POP_cond_GROUP_ID").val();
	
	if (groupId == "")
	{
		alert ("请输入正确的集团编码！");
		return false;
	}
	$.beginPageLoading();
	$.ajax.submit('QryBlackWhiteOutPart', 'queryGrpUserSvc', '&GROUP_ID='+groupId, 'ServicePart', 
	function(data)
	{
		initOperType();
		$.endPageLoading();		
	},
	function(error_code,error_info,derror)
	{		
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
}

function initOperType()
{
	var serialNumber = $("#cond_SERIAL_NUMBER").val(); 
	if(serialNumber != '')
	{
		$("#SERVICE_TYPE").attr("disabled","");
	}
	$("#cond_SERIAL_NUMBER").focus();
}

function changeOperType()
{
	var serviceId = $("#SERVICE_TYPE").val();	
	if(serviceId != null && serviceId != '')
	{
		var serviceInfos = $("#SERVICE_INFOS").val(); 
		var serialNumber = $("#cond_SERIAL_NUMBER").val(); 
		var groupId = $("#POP_cond_GROUP_ID").val(); 
		
		var param = '&SERVICE_ID='+serviceId+'&SERIAL_NUMBER='+serialNumber+'&GROUP_ID='+groupId+'&serviceInfos='+serviceInfos;
		$.beginPageLoading();
		$.ajax.submit('', 'getCurrentServiceInfo', param, 'ServiceParamPart', 
		function(data)
		{			
			//设置提交按钮可见
			$.cssubmit.disabledSubmitBtn("false");
			$.endPageLoading();		
		},
		function(error_code,error_info,derror)
		{		
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
	    });
	}
}

function onSubmitBaseTradeCheck()
{
	return true;     
}