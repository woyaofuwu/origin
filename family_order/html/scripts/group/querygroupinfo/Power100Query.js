//集团资料查询成功后调用的方法
function selectGroupAfterAction(data) {
	$("#conn_CUST_ID").val(data.get('CUST_ID'));
	$.ajax.submit("QueryCondPart", "queryComboProductInfos", "", "RefreshPart,RefreshPart1,refreshHintBar", function(data){
			$.endPageLoading();
		},
		function(error_code, error_info, derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
		}
	);
	$.endPageLoading();
}

//集团资料查询失败后调用的方法
function selectGroupErrorAfterAction() {
	$.endPageLoading();

}
