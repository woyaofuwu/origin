function qryVpmnGroupInfo() {
	var groupId = $('#con_GROUP_ID').val();
	var vpmnNo = $('#con_VPN_NO').val();
	var vpnName = $('#con_VPN_NAME').val();
	if(groupId == '' && vpmnNo == '' && vpnName == '') {
		alert('请输入查询条件！');
		return false;
	}
	$.beginPageLoading("数据查询中......");
	$.ajax.submit("QueryCondPart", "qryVpmnGroupInfo", "", "refreshHintBar,RefreshPart", function(data){
			$.endPageLoading();
		},
		function(error_code, error_info, derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
//			$.showWarnMessage(error_code,error_info);
		}
	);
}