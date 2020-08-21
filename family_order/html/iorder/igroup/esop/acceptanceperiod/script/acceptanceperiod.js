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
//				var fileNameIndex = fileNameArr[i].lastIndexOf(".");
//				var fileName = fileNameArr[i].substring(0,fileNameIndex);
				if(containSpecial(fileNameArr[i])){
					MessageBox.alert("错误", "【"+fileNameArr[i]+"】文件名称包含特殊字符，请修改后再上传！");
					return false; 
				}
				var data = new Wade.DataMap();
				data.put("FILE_ID", fileIdArr[i]);
				data.put("FILE_NAME", fileNameArr[i]);
				data.put("ATTACH_TYPE", "A");
				fileList.add(data);
			}
		}
		$("#ATTACH_FILE_LIST").text(fileList.toString());
		
		$("#upfileText").text(obj.NAME);
		$("#upfileValue").val(obj.ID);
		
		$("#ATTACH_FILE_NAME").val(obj.NAME);
		$("#ATTACH_FILE_ID").val(obj.ID);
		
		hidePopup("popup01");
	});
	
	$("#fileupload").clear(function(){
		$("#upfileText").text("");
		$("#upfileValue").val("");
		
		$("#ATTACH_FILE_NAME").val("");
		$("#ATTACH_FILE_ID").val("");
		
		$("#ATTACH_FILE_LIST").text("");
	});
	
	//初始化公共信息
	changeBusinessType();
	ifNewProjectName();
	
});

function containSpecial(str){
	var containSpecial = RegExp("[%?？]");//过滤%,？,?特殊字符
	return (containSpecial.test(str));
}

function qryProductInfo(obj){
	var productNo = $(obj).attr("productNo");
	var ibsysid = $(obj).attr("ibsysid");
	var productId = $(obj).attr("productId");
	if(productNo == null || productNo == '' || productNo==undefined){
		alert("未获取到专线实例号！");
		return false;
	}
	if(ibsysid == null || ibsysid == '' || ibsysid==undefined){
		alert("未获取到IBSYSID！");
		return false;
	}
	if(productId == null || productId == '' || productId==undefined){
		alert("未获取到产品编码！");
		return false;
	}
	var eosCommonData = new Wade.DataMap($("#EOS_COMMON_DATA").text());
	var bpmTempletId = eosCommonData.get("BPM_TEMPLET_ID");
	if(bpmTempletId == 'ADDCREDITREDLIST'||bpmTempletId =='ACCEPTANCEPERIODCHGAUDIT'){
		return false;
	}
	var nodeId = $("#NODE_ID").val();
	$.beginPageLoading('正在查询专线业务信息...');
	$.ajax.submit(null,'qryByIbsysidProductNo','&IBSYSID='+ibsysid+'&PRODUCTNO='+productNo+'&PRODUCT_ID='+productId+"&BPM_TEMPLET_ID="+bpmTempletId,'ProductInfo',function(data){
		showPopup('qryPopup2','UI-moreProduct');
		$.endPageLoading();
	},function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});
}

function submit(){
	
	if(!$("#AUDIT_RESULT").val()){
		alert("请先选择审核结果！");
		return false;
	}
	
	var attachList = saveAttach();
	
	var submitParam = new Wade.DataMap();
	submitParam.put("COMMON_DATA", saveEosCommonData());
	submitParam.put("BUSI_SPEC_RELE", saveBusiSpecReleData());
	submitParam.put("NODE_TEMPLETE", saveNodeTempleteData());
	submitParam.put("ATTACH_LIST", attachList);
	//审核信息
	submitParam.put("OTHER_LIST", saveAuditData());
	
	if($("#END_DATE").val()){
		submitParam.put("END_DATE",$("#END_DATE").val());
	}
	
	$.beginPageLoading("数据提交中，请稍后...");
	$.ajax.submit("", "submit", "&SUBMIT_PARAM="+submitParam.toString(), "", function(data){
		$.endPageLoading();
		debugger;
		if(data.get("ASSIGN_FLAG") == "true")
		{
			MessageBox.success("审批完成", "定单号："+data.get("IBSYSID"), function(btn){
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
			MessageBox.success("审批完成", "定单号："+data.get("IBSYSID"), function(btn){
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
		}/*else if(data.get("INVALID_TAG")=="true"){
			MessageBox.error("提交提示", "变更专线已经开始计费,此审核已失效！", function(btn){
				if("ok" == btn){
					closeNav();
				}
			});
		}*/
		else
		{
			MessageBox.success("审批完成", "定单号："+data.get("IBSYSID"), function(btn){
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

function saveAttach()
{
	//其他附件
	var attachList = new Wade.DatasetList($("#ATTACH_FILE_LIST").text());
	for(var i = 0, size = attachList.length; i < size; i++)
	{
		attachList.get(i).put("REMARK", $("#ATTACH_REMARK").val()); 
	}
	
	return attachList;
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
	var isNewMebData = new Wade.DataMap();
	isNewMebData.put("ATTR_CODE","IS_NEW_GRP_USER");
	isNewMebData.put("ATTR_VALUE",$("#IS_NEW_GRP_USER").val());
	isNewMebData.put("ATTR_NAME",$("#IS_NEW_GRP_USER").attr("desc"));
	isNewMebData.put("RECORD_NUM","0");
	auditChaSpecList.add(isNewMebData);
	
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

function changeBusinessType(){
	var type = $("#pattr_BUSINESSTYPE").val();
	if(type=='0'){//项目
		$("#IF_NEW_PROJECTNAME").css("display", "");
		$("#CONTRACTREQUIREDATE").css("display", "");
		$("#pattr_PROJECTNAME").attr("nullable", "no");
		$("#pattr_IF_NEW_PROJECTNAME").attr("nullable", "no");
		$("#pattr_CONTRACTREQUIREDATE").attr("nullable", "no");
		
	}else if(type=='1'){//零星
		$("#PROJECTNAME").css("display", "none");
		$("#CONTRACTREQUIREDATE").css("display", "none");
		$("#IF_NEW_PROJECTNAME").css("display", "none");
		$("#PROJECTNAME").attr("nullable", "yes");
		$("#CONTRACTREQUIREDATE").attr("nullable", "yes");
		$("#pattr_PROJECTNAME").attr("nullable", "yes");
		$("#pattr_CONTRACTREQUIREDATE").attr("nullable", "yes");
		$("#pattr_IF_NEW_PROJECTNAME").attr("nullable", "yes");
		$("#PROJECTNAME_OLD").css("display", "none");
		$("#pattr_PROJECTNAME_OLD").attr("nullable", "yes");
		
	}
	
}
	
function ifNewProjectName(){
	debugger;
	var ifNew =  $("#pattr_IF_NEW_PROJECTNAME").val();
	
	if(ifNew=='0'){//新增
		$("#PROJECTNAME").css("display", "");
		$("#pattr_PROJECTNAME").attr("nullable", "no");
		$("#PROJECTNAME_OLD").css("display", "none");
		$("#pattr_PROJECTNAME_OLD").attr("nullable", "yes");
	}else if(ifNew=='1'){//已有
		$("#PROJECTNAME").css("display", "none");
		$("#pattr_PROJECTNAME").attr("nullable", "yes");
		$("#PROJECTNAME_OLD").css("display", "");
		$("#pattr_PROJECTNAME_OLD").attr("nullable", "no");
	}
}
	
