$(function(){
	$("#CONTRACT_FILE_LIST").afterAction(function(e, file){
		var data1 = new Wade.DataMap();
		data1.put("FILE_ID", file.fileId);
		data1.put("FILE_NAME", file.name);
		data1.put("ATTACH_TYPE", "C");
		$("#C_FILE_LIST").text(data1.toString());
		$("#C_FILE_LIST").val(file.fileId+':'+file.name);
		
		$("#C_FILE_LIST_NAME").val(file.name);
	});
});

function submit(){
	
	var contractList;
	contractList = saveContractInfo();//合同信息
	var attachList = saveAttach();
	var submitParam = new Wade.DataMap();
	submitParam.put("COMMON_DATA", saveEosCommonData());
	submitParam.put("BUSI_SPEC_RELE", saveBusiSpecReleData());
	submitParam.put("NODE_TEMPLETE", saveNodeTempleteData());
	//稽核信息
//	submitParam.put("OTHER_LIST", saveAuditData());
	submitParam.put("CONTRACT_DATA", contractList);
	submitParam.put("ATTACH_LIST", attachList);


	$.beginPageLoading("数据提交中，请稍后...");
	$.ajax.submit("", "submit", "&SUBMIT_PARAM="+submitParam.toString(), "", function(data){
		$.endPageLoading();
		if(data.get("ASSIGN_FLAG") == "true")
		{
			MessageBox.success("提交成功", "", function(btn){
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
			MessageBox.success("提交成功", "", function(btn){
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


function saveContractInfo(){
	var ifNewContract = $("#C_IF_NEW_CONTRACT").val();
	var contractDataset = new Wade.DatasetList();
	
	var ifNew = $("#ContractPart input");
	for(var i = 0, size = ifNew.length; i < size; i++)
	{
		var ifNewCode = ifNew[i].id;
		var ifNewValue = $("#"+ifNewCode).val();
		
		var ifNewData = new Wade.DataMap();
		ifNewData.put("ATTR_VALUE", ifNewValue);
		ifNewData.put("ATTR_NAME", ifNew[i].getAttribute("desc"));
		ifNewData.put("ATTR_CODE", ifNewCode);
		
		contractDataset.add(ifNewData);
	}
	
	if(ifNewContract=='0'){
		var newContract = $("#newContractPart input");
		for(var i = 0, size = newContract.length; i < size; i++)
		{
			var newContractCode = newContract[i].id;
			if(newContractCode==''||newContractCode=='CONTRACT_FILE_LIST'){
				continue;
			}
			var newContractValue = $("#"+newContractCode).val();
			
			var contractData = new Wade.DataMap();
			contractData.put("ATTR_VALUE", newContractValue);
			contractData.put("ATTR_NAME", newContract[i].getAttribute("desc"));
			contractData.put("ATTR_CODE", newContractCode);
			
			contractDataset.add(contractData);
		}
	}else if(ifNewContract=='1'){
		var contract = $("#C_CONTRACT").val();
		var contracts = $("#contractContentPart input");
		for(var i = 0, size = contracts.length; i < size; i++)
		{
			var contractsCode = contracts[i].id;
			var contractsValue = $("#"+contractsCode).val();
			
			var contractData = new Wade.DataMap();
			contractData.put("ATTR_VALUE", contractsValue);
			contractData.put("ATTR_NAME", contracts[i].getAttribute("desc"));
			contractData.put("ATTR_CODE", contractsCode);
			
			contractDataset.add(contractData);
		}
	}else{
		var contract = $("#C_CONTRACT").val();
		var contracts = $("#newContractPart input");
		for(var i = 0, size = contracts.length; i < size; i++)
		{
			var contractsCode = contracts[i].id;
			if(contractsCode!=null&&contractsCode!=''){
				var contractsValue = $("#"+contractsCode).val();
				
				var contractData = new Wade.DataMap();
				contractData.put("ATTR_VALUE", contractsValue);
				contractData.put("ATTR_NAME", contracts[i].getAttribute("desc"));
				contractData.put("ATTR_CODE", contractsCode);
				
				contractDataset.add(contractData);
			}
			
		}
		var contractLineInfo = $("#contractLineInfoPart span[class=value]");
		var recordNum=0;
		for(var i = 0, size = contractLineInfo.length; i < size; i++)
		{
			var contractsCode = contractLineInfo[i].id;
			if(contractsCode!=null&&contractsCode!=''){
				if(contractsCode=='NOTIN_LINE_NO'){
					recordNum++;
				}
				var contractsValue = $("#"+contractsCode).text();
				
				var contractData = new Wade.DataMap();
				contractData.put("ATTR_VALUE", contractsValue);
				contractData.put("ATTR_NAME", "");
				contractData.put("ATTR_CODE", contractsCode);
				contractData.put("RECORD_NUM", recordNum);
				contractDataset.add(contractData);
			}
			
		}
		
	}
	return contractDataset;
}
function saveAttach()
{
	var attachList = new Wade.DatasetList();
//	for(var i = 0, size = attachList.length; i < size; i++)
//	{
//		attachList.get(i).put("REMARK", $("#ATTACH_REMARK").val()); 
//	}
	//合同附件
	var contractFile = new Wade.DataMap($("#C_FILE_LIST").text());
	if(contractFile.items!=""){
		attachList.add(contractFile);
	}
	return attachList;
}