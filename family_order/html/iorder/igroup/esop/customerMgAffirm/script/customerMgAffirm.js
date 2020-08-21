$(function(){
	debugger;
	var exceptionId =$("#EXCEPTION").val();
	if(exceptionId){
		MessageBox.error("错误信息", exceptionId, function(btn){
			$.nav.close();
		});
	}
});



function qryProductInfo(obj){
	debugger;
	var productNo = $(obj).attr("productNo");
	var ibsysid = $(obj).attr("ibsysid");
	var productId = $(obj).attr("productId");
	if(productNo == null || productNo == '' || productNo==undefined){
		return false;
	}
	if(ibsysid == null || ibsysid == '' || ibsysid==undefined){
		return false;
	}
	if(productId == null || productId == '' || productId==undefined){
		return false;
	}
	var nodeId = "apply";
	$.beginPageLoading('正在查询专线业务信息...');
	$.ajax.submit(null,'qryByIbsysidProductNo','&IBSYSID='+ibsysid+'&PRODUCTNO='+productNo+'&PRODUCT_ID='+productId+'&NODE_ID='+nodeId,'ProductInfo',function(data){
		showPopup('qryPopup2','UI-moreProduct');
		$.endPageLoading();
	},function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});
}

function submit(){
	debugger;
	var submitParam = new Wade.DataMap();
	submitParam.put("COMMON_DATA", saveEosCommonData());
	submitParam.put("BUSI_SPEC_RELE", saveBusiSpecReleData());
	submitParam.put("NODE_TEMPLETE", saveNodeTempleteData());
	//审核信息
	submitParam.put("OTHER_LIST", saveAuditData());
	
	
	$.beginPageLoading("数据提交中，请稍后...");
	$.ajax.submit("", "submit", "&SUBMIT_PARAM="+submitParam.toString(), "", function(data){
		$.endPageLoading();
		
		if(data.get("ASSIGN_FLAG") == "true")
		{
			MessageBox.success("确认完成", "", function(btn){
				if("ext1" == btn){
					debugger;
					var urlArr = data.get("ASSIGN_URL").split("?");
					var pageName = getNavTitle();
					openNav('指派', urlArr[1].substring(13), '', '&BEFORE_NAV_TITLE='+getNavTitle(), urlArr[0]);
					closeNavByTitle(pageName);
				}
				if("ok" == btn){
					closeNav();
				}
			}, {"ext1" : "指派"});
		}
		else
		{
			MessageBox.success("确认完成", "", function(btn){
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
	staffData.put("ATTR_CODE","CONSTRUCTION_STAFF_ID");
	staffData.put("ATTR_VALUE",$("#CONSTRUCTION_STAFF_ID").val());
	staffData.put("ATTR_NAME",$("#CONSTRUCTION_STAFF_ID").attr("desc"));
	auditChaSpecList.add(staffData);
	var auditResultData = new Wade.DataMap();
	auditResultData.put("ATTR_CODE","CONSTRUCTION_STATE");
	auditResultData.put("ATTR_VALUE",$("#CONSTRUCTION_STATE").val());
	auditResultData.put("ATTR_NAME",$("#CONSTRUCTION_STATE").attr("desc"));
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
