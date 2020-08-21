function qryVpmnManagerImp() {
	$.beginPageLoading("数据查询中......");
	$.ajax.submit("QueryCondPart", "qryVpmnManagerImp", "", "RefreshPart", function(data){
			$.endPageLoading();
		},
		function(error_code, error_info, derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
//			$.showWarnMessage(error_code,error_info);
		}
	);
}

function dealDispatch(obj) {
	var dealState = obj.getAttribute("state");
	var isDeal = obj.getAttribute("isDeal");
	var importId = obj.getAttribute("importid");
	if(dealState == 0 && isDeal == 0) {
		if (!window.confirm("确认处理吗？(确定之后请您点击查询按钮刷新)")) {
			return false; 
		}
		obj.setAttribute("isDeal", "1");
		$.ajax.submit(this, "doThisVpmnManagerInfo", "&IMPORT_ID="+importId, "", function(data){
			
			},
			function(error_code, error_info, derror){
				showDetailErrorInfo(error_code,error_info,derror);
			}
		);
	} else if(dealState == 0 && isDeal == 1) {
		alert("数据正在处理，请您点击查询按钮刷新！");
		return false;
	} else {
		alert("数据已处理完成！");
		return false;
	}
}