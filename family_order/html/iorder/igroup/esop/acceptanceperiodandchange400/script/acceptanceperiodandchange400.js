$(function(){
	if($("#ELEMENT_DISABLED").val()!=1){
		var chaSpecObjs = $("#orderInfo input");
		for(var i = 0, size = chaSpecObjs.length; i < size; i++)
		{
			var chaSpecCode = chaSpecObjs[i].id;
			if(chaSpecCode){
				var chaSpecObj = $("#"+chaSpecCode);
				chaSpecObj.attr("disabled","true");
			}
		}
	}
	$("#offerChaInfo").css("display","none");
	$("#openDefaultOp").css("display","");
	$("#hideDefaultOp").css("display","none");
	
});

function submit(){
	debugger;
	var message = '提交成功';
	if($("#ELEMENT_DISABLED").val()==1){
		if(!$.validate.verifyAll("orderInfo")){
			return false;
		}
	}
	
	if($("#ELEMENT_DISPLAY").val()==1){
		if(!$("#AUDIT_RESULT").val()){
			alert("请先选择审核结果！");
			return false;
		}
	}

	var submitParam = new Wade.DataMap();
	submitParam.put("COMMON_DATA", saveEosCommonData());
	submitParam.put("BUSI_SPEC_RELE", saveBusiSpecReleData());
	submitParam.put("NODE_TEMPLETE", saveNodeTempleteData());
	
	var otherList = new Wade.DatasetList();
	//审核信息
	if($("#ELEMENT_DISPLAY").val()==1){
		otherList = saveAuditData();
	}
	//移动400域名申请与域名确认信息
	var domain = $("#ELEMENT_MOBILE400").val();
	if("1"==domain||1==domain){
		otherList = saveAttrDataMobile400();
	}
	if("2"==domain||2==domain){
		otherList = saveAttrDomainConfirm400();
	}
	
	var bpmTempletId = $("#BPM_TEMPLET_ID").val();
	if(bpmTempletId=="FOURMANAGE"||bpmTempletId=="TIMERREVIEWFOURMANAGE"){
		otherList = save400CoditionData(otherList);
	}
	submitParam.put("OTHER_LIST", otherList);
	if($("#ELEMENT_DISABLED").val()==1){
		
		submitParam.put("CUSTOM_ATTR_LIST",saveAttrData());
	}
	$.beginPageLoading("数据提交中，请稍后...");
	$.ajax.submit("", "submit", "&SUBMIT_PARAM="+submitParam.toString(), "", function(data){
		$.endPageLoading();
		debugger;
		if(data.get("ASSIGN_FLAG") == "true")
		{
			MessageBox.success(message, "定单号："+data.get("IBSYSID"), function(btn){
				if("ext1" == btn){
					debugger;
					var urlArr = data.get("ASSIGN_URL").split("?"); 
					var pageName = getNavTitle(); 
					openNav('指派', urlArr[1].substring(13), '', '', urlArr[0]); 
					closeNavByTitle(pageName);
				}
				if("ok" == btn){
					closeNav();
				}
			}, {"ext1" : "指派"});
		}else if(data.get("ALERT_FLAG")== "true"){
			MessageBox.success(message, "定单号："+data.get("IBSYSID"), function(btn){
				if("ext1" == btn){
					var urlArr = data.get("ALERT_URL").split("?");
					var ALERT_NAME = data.get("ALERT_NAME");
					var pageName = getNavTitle();
					openNav(ALERT_NAME, urlArr[1].substring(13), '', '', urlArr[0]); 
					closeNavByTitle(pageName);
				}
				if("ok" == btn){
					closeNav();
				}
			}, {"ext1" : "下一步"});
		}
		else
		{
			MessageBox.success(message, "定单号："+data.get("IBSYSID"), function(btn){
				if("ok" == btn){
					closeNav();
				}
			});
		}
		
	}, 
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});
}

function saveEosCommonData()
{
	var eosCommonData = new Wade.DataMap($("#EOS_COMMON_DATA").text());
	if(!eosCommonData.get("CUST_NAME"))
	{
		eosCommonData.put("CUST_NAME", $("#CUST_NAME").val());
	}
	return eosCommonData;
}

function saveAuditData(){
	var auditChaSpecList = new Wade.DatasetList();
	var staffData = new Wade.DataMap();
	staffData.put("ATTR_CODE","AUDIT_STAFF_ID");
	staffData.put("ATTR_VALUE",$("#AUDIT_STAFF_ID").val());
	staffData.put("ATTR_NAME",$("#AUDIT_STAFF_ID").attr("desc"));
	staffData.put("RECORD_NUM","0");
	auditChaSpecList.add(staffData);
	var auditResultData = new Wade.DataMap();
	auditResultData.put("ATTR_CODE","AUDIT_RESULT");
	auditResultData.put("ATTR_VALUE",$("#AUDIT_RESULT").val());
	auditResultData.put("ATTR_NAME",$("#AUDIT_RESULT").attr("desc"));
	auditResultData.put("RECORD_NUM","0");
	auditChaSpecList.add(auditResultData);
	var auditTextData = new Wade.DataMap();
	auditTextData.put("ATTR_CODE","AUDIT_TEXT");
	auditTextData.put("ATTR_VALUE",$("#AUDIT_TEXT").val());
	auditTextData.put("ATTR_NAME",$("#AUDIT_TEXT").attr("desc"));
	auditTextData.put("RECORD_NUM","0");
	auditChaSpecList.add(auditTextData);
	
	return auditChaSpecList;
}

function saveBusiSpecReleData()
{
	var eosCommonData = new Wade.DataMap($("#EOS_COMMON_DATA").text());
	
	var busiSpecRele = new Wade.DataMap();
	busiSpecRele.put("NODE_ID", eosCommonData.get("NODE_ID"));
	
	return busiSpecRele;
}

function saveNodeTempleteData()
{
	var eosCommonData = new Wade.DataMap($("#EOS_COMMON_DATA").text());
	
	var nodeTemplete = new Wade.DataMap();
	nodeTemplete.put("BPM_TEMPLET_ID", eosCommonData.get("BPM_TEMPLET_ID"));
	nodeTemplete.put("BUSI_TYPE", eosCommonData.get("BUSI_TYPE"));
	nodeTemplete.put("BUSI_CODE", eosCommonData.get("BUSI_CODE"));
	nodeTemplete.put("IN_MODE_CODE", eosCommonData.get("IN_MODE_CODE"));
	
	return nodeTemplete;
}

function saveAttrData(){
	var chaSpecObjs = $("#orderInfo input");
	var attrChaSpecList = new Wade.DatasetList();
	
	for(var i = 0, size = chaSpecObjs.length; i < size; i++)
	{
		var chaSpecCode = chaSpecObjs[i].id;
		var chaValue='';
		if(chaSpecCode){
			chaValue = $("#"+chaSpecCode).val();
			
			var attrChaSpecData = new Wade.DataMap();
			attrChaSpecData.put("ATTR_VALUE", chaValue);
			attrChaSpecData.put("ATTR_NAME", chaSpecObjs[i].getAttribute("desc"));
			attrChaSpecData.put("ATTR_CODE", chaSpecCode.substring(6));
			attrChaSpecData.put("RECORD_NUM","1");
			
			attrChaSpecList.add(attrChaSpecData);
		}
		
		
	}
	return attrChaSpecList;
	
}


function saveAttrDataMobile400(){
	
	var auditChaSpecList = new Wade.DatasetList();
	var staffData = new Wade.DataMap();
	staffData.put("ATTR_CODE","DOMAIN_NAME");
	staffData.put("ATTR_VALUE",$("#DOMAIN_NAME").val());
	staffData.put("ATTR_NAME",$("#DOMAIN_NAME").attr("desc"));
	staffData.put("RECORD_NUM","0");
	auditChaSpecList.add(staffData);
	var auditResultData = new Wade.DataMap();
	auditResultData.put("ATTR_CODE","IS_DOMAIN_OK");
	auditResultData.put("ATTR_VALUE",$("#IS_DOMAIN_OK").val());
	auditResultData.put("ATTR_NAME",$("#IS_DOMAIN_OK").attr("desc"));
	auditResultData.put("RECORD_NUM","0");
	auditChaSpecList.add(auditResultData);
	var auditTextData = new Wade.DataMap();
	auditTextData.put("ATTR_CODE","AUDIT_STAFF");
	auditTextData.put("ATTR_VALUE",$("#AUDIT_STAFF").val());
	auditTextData.put("ATTR_NAME",$("#AUDIT_STAFF").attr("desc"));
	auditTextData.put("RECORD_NUM","0");
	auditChaSpecList.add(auditTextData);
	
	return auditChaSpecList;
	
}

function saveAttrDomainConfirm400(){
	
	var auditChaSpecList = new Wade.DatasetList();
	var staffData = new Wade.DataMap();
	staffData.put("ATTR_CODE","DOMAIN_CONFIRM_NAME");
	staffData.put("ATTR_VALUE",$("#DOMAIN_CONFIRM_NAME").val());
	staffData.put("ATTR_NAME",$("#DOMAIN_CONFIRM_NAME").attr("desc"));
	staffData.put("RECORD_NUM","0");
	auditChaSpecList.add(staffData);
	var auditResultData = new Wade.DataMap();
	auditResultData.put("ATTR_CODE","DOMAIN_CONFIRM_TYPE");
	auditResultData.put("ATTR_VALUE",$("#DOMAIN_CONFIRM_TYPE").val());
	auditResultData.put("ATTR_NAME",$("#DOMAIN_CONFIRM_TYPE").attr("desc"));
	auditResultData.put("RECORD_NUM","0");
	auditChaSpecList.add(auditResultData);
	var auditTextData = new Wade.DataMap();
	auditTextData.put("ATTR_CODE","DOMAIN_DEAL_OVER");
	auditTextData.put("ATTR_VALUE",$("#DOMAIN_DEAL_OVER").val());
	auditTextData.put("ATTR_NAME",$("#DOMAIN_DEAL_OVER").attr("desc"));
	auditTextData.put("RECORD_NUM","0");
	auditChaSpecList.add(auditTextData);
	var confirmTime = new Wade.DataMap();
	confirmTime.put("ATTR_CODE","DOMAIN_CONFIRM_TIME");
	confirmTime.put("ATTR_VALUE",$("#DOMAIN_CONFIRM_TIME").val());
	confirmTime.put("ATTR_NAME",$("#DOMAIN_CONFIRM_TIME").attr("desc"));
	confirmTime.put("RECORD_NUM","0");
	auditChaSpecList.add(confirmTime);
	var auditRemak = new Wade.DataMap();
	auditRemak.put("ATTR_CODE","AUDIT_REMARK");
	auditRemak.put("ATTR_VALUE",$("#AUDIT_REMARK").val());
	auditRemak.put("ATTR_NAME",$("#AUDIT_REMARK").attr("desc"));
	auditRemak.put("RECORD_NUM","0");
	auditChaSpecList.add(auditRemak);
	
	return auditChaSpecList;
	
}

function save400CoditionData(otherList){
	var busiNumData = new Wade.DataMap();
	busiNumData.put("ATTR_CODE","BUSI_NUMBER");
	busiNumData.put("ATTR_VALUE", $("#pattr_BUSI_NUMBER").val());
	busiNumData.put("ATTR_NAME", $("#pattr_BUSI_NUMBER").attr("desc"));
	busiNumData.put("RECORD_NUM","0");
	otherList.add(busiNumData);
	
	var underNumData = new Wade.DataMap();
	underNumData.put("ATTR_CODE","UNDER_NUMBER");
	underNumData.put("ATTR_VALUE", $("#pattr_UNDER_NUMBER").val());
	underNumData.put("ATTR_NAME", $("#pattr_UNDER_NUMBER").attr("desc"));
	underNumData.put("RECORD_NUM","0");
	otherList.add(underNumData);
	
	return otherList;
}


function changeDefaultOp(obj,tp){
	if("1"==tp){
		 $("#offerChaInfo").css("display","");
		 $("#openDefaultOp").css("display","none");
		 $("#hideDefaultOp").css("display","");
	}
	if("2"==tp){
		 $("#offerChaInfo").css("display","none");
		 $("#openDefaultOp").css("display","");
		 $("#hideDefaultOp").css("display","none");
	}
	
}