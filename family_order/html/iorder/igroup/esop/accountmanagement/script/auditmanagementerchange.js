
//提交方法
function submit(){
	
	if(!$("#AUDIT_RESULT").val()){
		alert("请选择稽核结果！");
		return false;
	}
	
	var otherList = saveOtherList();
	
	var submitParam = new Wade.DataMap();
	submitParam.put("OTHER_LIST",otherList);
	submitParam.put("COMMON_DATA",saveCommonDate());
	
	$.beginPageLoading("数据提交中，请稍后...");
	$.ajax.submit("", "submit", "&SUBMIT_PARAM="+submitParam.toString(), "", function(data){
		$.endPageLoading();
		debugger;
		if(data.get("ASSIGN_FLAG") == "true")
		{
			MessageBox.success("提交成功", "定单号："+data.get("IBSYSID"), function(btn){
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
			MessageBox.success("提交成功", "定单号："+data.get("IBSYSID"), function(btn){
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
			MessageBox.success("提交成功", "定单号："+data.get("IBSYSID"), function(btn){
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

function saveOtherList(){
	var otherList = new Wade.DatasetList();
	var otherData = new Wade.DataMap();
	otherData.put("ATTR_CODE","AUDIT_RESULT");
	otherData.put("ATTR_NAME","稽核结果");
	otherData.put("ATTR_VALUE",$("#AUDIT_RESULT").val());
	otherData.put("RECORD_NUM","0");
	otherList.add(otherData);
	
	var auditTextData = new Wade.DataMap();
	auditTextData.put("ATTR_CODE","AUDIT_TEXT");
	auditTextData.put("ATTR_NAME","稽核意见");
	auditTextData.put("ATTR_VALUE",$("#AUDIT_TEXT").val());
	auditTextData.put("RECORD_NUM","0");
	otherList.add(auditTextData);
	
	var auditStaffData = new Wade.DataMap();
	auditStaffData.put("ATTR_CODE","AUDIT_STAFF_ID");
	auditStaffData.put("ATTR_NAME","稽核人员工号");
	auditStaffData.put("ATTR_VALUE",$("#AUDIT_STAFF_ID").val());
	auditStaffData.put("RECORD_NUM","0");
	otherList.add(auditStaffData);
	return otherList;
}

function saveCommonDate(){
	var commonData = new Wade.DataMap($("#EOS_COMMON_DATA").text());
	if(!commonData.get("CUST_NAME"))
	{
		commonData.put("CUST_NAME", $("#CUST_NAME").text());
	}
	return commonData;
}