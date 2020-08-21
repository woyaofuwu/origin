function qryVpmnManagerList() {
	var staffId = $('#cond_STAFF_ID').val();
	if(staffId == '') {
		alert('请输入查询条件！');
		return false;
	}
	$.beginPageLoading("数据查询中......");
	$.ajax.submit("QueryCondPart", "qryVpmnManagerList", "", "refreshHintBar,RefreshPart", function(data){
			$.endPageLoading();
		},
		function(error_code, error_info, derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
		}
	);
}

function doDispatch() {
	if (!check_any()) 
	{
		return false; 
	}
		
	if (!check_manager()) 
	{
		return false; 
	}
	$("#vpmnManagerList2").val(getCheckedValues('vpmnManagerList'));
	
	//新增权限
	var custMgrId = $("#MANAGER_ID").val();
 	if(custMgrId == ''){
 		alert('客户经理编码不能为空！');
 		return false;
 	}
 	$.beginPageLoading("正在保存数据......");
	$.ajax.submit("MgrInfoPart", "doDispatch", "", "RefreshPart,MgrInfoPart", function(data){
			vpmnManagerDispatchInit();
			$.endPageLoading();
		},
		function(error_code, error_info, derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
		}
	);
}

function vpmnManagerDispatchInit() {
	var resultCode = $("#resultCode").val();
	var isSucc = $("#isSuccess").val();
	if(resultCode == 0) 
	{
		$.showSucMessage("数据保存成功", isSucc,"");
		qryVpmnManagerList();
	}
	else 
	{
		$.showWarnMessage("数据保存失败", isSucc);
	}
}

function check_manager() {
	var oldManagerId = $("#cond_STAFF_ID").val();
	var newManagerId = $("#MANAGER_ID").val();
	if(newManagerId == '') 
	{	
   		alert("请选择分配的VPMN客户经理！");
   		return false;	
   	}
   	if(oldManagerId == newManagerId) 
   	{
   		alert("分配员工不能相同！");
   		return false;	
   	}
    return true;	
}

function check_any() {   
	if(getCheckedBoxNum("vpmnManagerList") != 0) 
	{
		return true;
	}
	alert('请选择待调配的VPMN权限！');
	return false;
}