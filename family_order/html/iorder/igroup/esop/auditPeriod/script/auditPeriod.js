

function submit(){
	
	if(!$("#AUDIT_RESULT").val()){
		alert("请先选择稽核结果！");
		return false;
	}
	
	var submitParam = new Wade.DataMap();
	submitParam.put("COMMON_DATA", saveEosCommonData());
	submitParam.put("BUSI_SPEC_RELE", saveBusiSpecReleData());
	submitParam.put("NODE_TEMPLETE", saveNodeTempleteData());
	//稽核信息
	submitParam.put("OTHER_LIST", saveAuditData());
	
	$.beginPageLoading("数据提交中，请稍后...");
	$.ajax.submit("", "submit", "&SUBMIT_PARAM="+submitParam.toString(), "", function(data){
		$.endPageLoading();
		if(data.get("ASSIGN_FLAG") == "true")
		{
			MessageBox.success("稽核完成", "", function(btn){
				if("ext1" == btn){
					var urlArr = data.get("ASSIGN_URL").split("?");
					var pageName = getNavTitle();
					openNav('指派', urlArr[1].substring(13), '', '', urlArr[0]);
					closeNavByTitle(pageName);
				}
				if("ok" == btn){
					closeNav();
				}
			}, {"ext1" : "指派"});
		}else
		{
			MessageBox.success("审批完成", "", function(btn){
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
	auditChaSpecList.add(staffData);
	var auditResultData = new Wade.DataMap();
	auditResultData.put("ATTR_CODE","AUDIT_RESULT");
	auditResultData.put("ATTR_VALUE",$("#AUDIT_RESULT").val());
	auditResultData.put("ATTR_NAME",$("#AUDIT_RESULT").attr("desc"));
	auditChaSpecList.add(auditResultData);
	var auditTextData = new Wade.DataMap();
	auditTextData.put("ATTR_CODE","AUDIT_TEXT");
	auditTextData.put("ATTR_VALUE",$("#AUDIT_TEXT").val());
	auditTextData.put("ATTR_NAME",$("#AUDIT_TEXT").attr("desc"));
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