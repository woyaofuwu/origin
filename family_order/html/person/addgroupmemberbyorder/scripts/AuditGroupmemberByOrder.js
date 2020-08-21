/**
 * 查询集团信息
 * @return
 */
function queryGrpInfos(){

	$.beginPageLoading("数据查询中..");
	$.ajax.submit('ConditionPart', 'queryGrpInfos', null, 'QueryListPart', function(data){
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}

/**
 * 根据集团编码查询集团下的所有通讯录成员
 * @return
 */
function queryGrpBooktInfos(data) {
	
	var groupId = $(data).text();
	var params = "&GROUP_ID="+groupId;
	$.beginPageLoading("数据查询中..");
	$.ajax.submit(null, 'queryGrpBooktInfos', params, 'QueryBookPart', function(data){
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}
/**
 * 提交稽核
 * @return
 */
function submitAudit() {
	//判断是否选中
	if (!queryBox(this, "viceCheckBox")) {
		return;
	}
	var auditReason = $("#AUDIT_REASON").val();
	var auditPass = $("#AUDIT_PASS_TAG").val();
	if(auditReason == '' || auditPass == ''){
		alert("请填写稽核意见和结果");
		return;
	}
	
	MessageBox.confirm("提示",'确认提交稽核?',function(ret){
		if ("ok" == ret) {
			$.beginPageLoading("数据处理中..");
			var auditReason = $("#AUDIT_REASON").val();//稽核理由
			var passTag = $("#AUDIT_PASS_TAG").val();//稽核结果
			var check = $("input[name='viceCheckBox']:checked");
			var params = $.DatasetList();
			for ( var i = 0; i < check.length; i++) {
				var checkedObj = $(check[i]);
				var param = new Wade.DataMap();
				param.put("USER_ID", checkedObj.val());
				param.put("GROUP_ID", checkedObj.attr("groupId"));
				param.put("EPARCHY_CODE", checkedObj.attr("eparchyCode"));
				param.put("CITY_CODE", checkedObj.attr("cityCode"));
				param.put("GROUP_CUST_NAME", checkedObj.attr("groupCustName"));
				param.put("CUST_MANAGER_ID", checkedObj.attr("custManagerId"));
				param.put("AUDIT_REASON", auditReason);
				param.put("AUDIT_PASS_TAG", passTag);
				params.add(param);
			}
			$.ajax.submit('QueryBookPart', 'submitAudit', "&AUDIT_INFOS=" + params.toString(), null, function(data){
				$.endPageLoading();
				alert("提交稽核成功！");
			},
			function(error_code,error_info){
				$.endPageLoading();
				alert(error_info);
		    });
			
		}
		
	});
	
	
}


