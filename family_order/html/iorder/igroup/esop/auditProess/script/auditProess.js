$(function(){
	var attachStr = $("#ATTACH_FILE_ID_STR").val();
	if(attachStr != "")
	{
		fileupload.loadFile(attachStr);
	}
});
function initPageParamCommon()
{
 
}
$(function(){
	$("#fileupload").select(function(){
		var fileList = new Wade.DatasetList();
		
		var obj = this.val();
		var fileIdArr = obj.ID.split(",");
		var fileNameArr = obj.NAME.split(",");
		for(var i = 0, size = fileIdArr.length; i < size; i++)
		{
			if(fileIdArr[i] != "")
			{
				var data = new Wade.DataMap();
				data.put("FILE_ID", fileIdArr[i]);
				data.put("FILE_NAME", fileNameArr[i]);
				fileList.add(data);
			}
		}
		$("#ATTACH_FILE_LIST").text(fileList.toString());
		
		$("#upfileText").text(obj.NAME);
		$("#upfileValue").val(obj.ID);
		
		$("#ATTACH_FILE_NAME").val(obj.NAME);
		$("#ATTACH_FILE_ID_STR").val(obj.ID);
		
		hidePopup("popup01");
	});
	
	$("#fileupload").clear(function(){
		$("#upfileText").text("");
		$("#upfileValue").val("");
		
		$("#ATTACH_FILE_NAME").val("");
		$("#ATTACH_FILE_ID_STR").val("");
		
		$("#ATTACH_FILE_LIST").text("");
	});
});

function submitAudit()
{
	if(!$.validate.verifyAll("AuditPart"))
	{
		return false;
	}
	var auditChaSpecList = new Wade.DatasetList();
	$("#AuditPart input").each(function(){
		var chaSpecCode = $(this).attr("id");
		var chaSpecData = new Wade.DataMap();
		if(chaSpecCode.indexOf("pattr_") == 0)
		{
			chaSpecData.put("ATTR_CODE", chaSpecCode.substring(6));
		}
		else
		{
			chaSpecData.put("ATTR_CODE", chaSpecCode);
		}
		
		var chaValue = $(this).val();
		if($(this).attr("type") == "radio" || $(this).attr("type") == "checkbox")
		{
			chaValue = $(this).attr("checked") ? 1 : 0;
		}
		chaSpecData.put("ATTR_VALUE", chaValue);
		
		chaSpecData.put("ATTR_NAME", $(this).attr("desc"));
		
		auditChaSpecList.add(chaSpecData);
	});
	//审核意见
	$("#AuditPart textarea").each(function(){
		var chaSpecCode = $(this).attr("id");
		var chaSpecData = new Wade.DataMap();
		if(chaSpecCode.indexOf("pattr_") == 0)
		{
			chaSpecData.put("ATTR_CODE", chaSpecCode.substring(6));
		}
		else
		{
			chaSpecData.put("ATTR_CODE", chaSpecCode);
		}
		
		chaSpecData.put("ATTR_VALUE", $(this).val());
		
		chaSpecData.put("ATTR_NAME", $(this).attr("desc"));
		
		auditChaSpecList.add(chaSpecData);
	});
	
	var eomsAttrData = new Wade.DatasetList();
	eomsAttrData = saveMarkeData(eomsAttrData);
	
	var submitParam = new Wade.DataMap();
	submitParam.put("OTHER_LIST", auditChaSpecList);
	submitParam.put("ATTACH_LIST", saveAttach());
	submitParam.put("EOMS_ATTR_LIST", eomsAttrData);
	
	submitParam.put("COMMON_DATA", saveEosCommonData());
	
	$.beginPageLoading("数据提交中，请稍后...");
	$.ajax.submit("", "submit", "&SUBMIT_PARAM="+submitParam.toString(), "", function(data){
		$.endPageLoading();
		if(data.get("ASSIGN_FLAG") == "true")
		{
			MessageBox.success("审批完成", "", function(btn){
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

function saveBusiSpecReleData()
{
	var eosCommonData = new Wade.DataMap($("#EOS_COMMON_DATA").text());
	
	var busiSpecRele = new Wade.DataMap();
	busiSpecRele.put("NODE_ID", eosCommonData.get("NODE_ID"));
	
	return busiSpecRele;
}

function saveAttach()
{
	var attachList = new Wade.DatasetList($("#ATTACH_FILE_LIST").text());
	return attachList;
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

function saveEosCommonData()
{
	var eosCommonData = new Wade.DataMap($("#EOS_COMMON_DATA").text());
	if(!eosCommonData.get("CUST_NAME"))
	{
		eosCommonData.put("CUST_NAME", $("#cond_CUST_NAME").val());
	}
	return eosCommonData;
}

function downAttachLib1(contractId)
{
	$.beginPageLoading("附件下载中，请稍后...");
	$.ajax.submit("", "downLoadList", "&CONTRACT_ID="+contractId, "", function(data){
		$.endPageLoading();

		var failUrl = data.get("FAILD_FILE_URL");
		MessageBox.alert("提示信息", "", function(btn){
			if(btn == "ext1")
			{
				window.location.href = failUrl;
			}
		}, {"ext1" : "下载"});
			
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
	
	
}

//查询合同信息
function downAttachLib(contractId)
{
	if(contractId == ''){
    	MessageBox.alert('','合同信息不存在不能下载合同！');
    	return ;
    }
	
	$.beginPageLoading("数据查询中......");
	$.ajax.submit("", "downLoadList", "&CONTRACT_ID="+contractId, "", function(data){
		$.endPageLoading();
		var hasContract = data.get("CONTRACT_TAG");
		var custId = data.get("CUST_ID");
		if(hasContract == "ture")
		{
			MessageBox.alert("提示信息", "因系统改造，合同附件下载需到客管合同维护界面下载！", function(btn){
				if("ext1" == btn){
					openNav('合同维护', 'custgroupcontract.CustGroupContractEdit','qryContract', '&refresh=true&typeCode=group&CUST_ID='+custId+'&CONTRACT_ID='+contractId,'/custmanm/custmanm');
				}
			}, {"ext1" : "合同维护"});
		}
		else{
			MessageBox.alert('','合同信息不存在不能下载合同！');
		}
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
}

function saveMarkeData(eomsAttrData){
	debugger;
	var chaSpecObjs = $("#MarketPart input");

	for (var i = 0, size = chaSpecObjs.length; i < size; i++) {
		var chaSpecCode = chaSpecObjs[i].id;
		var chaValue = "";
		if (chaSpecObjs[i].type == "checkbox" || chaSpecObjs[i].type == "radio") {
			chaValue = chaSpecObjs[i].checked ? 1 : 0;
			chaSpecCode = chaSpecObjs[i].name;
		} else {
			chaValue = $("#" + chaSpecCode).val();
		}

		var MarkeData = new Wade.DataMap();
		MarkeData.put("ATTR_VALUE", chaValue);
		MarkeData.put("ATTR_NAME", chaSpecObjs[i].getAttribute("desc"));
		MarkeData.put("ATTR_CODE", chaSpecCode.substring(6));

		eomsAttrData.add(MarkeData);
	}
	
	return eomsAttrData;
	
}


