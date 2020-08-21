/**
 * 查询集团业务稽核工单信息
 * @return
 */
function queryGrpBooktInfos(){

	$.beginPageLoading("数据查询中..");
	$.ajax.submit('ConditionPart', 'queryGrpBooktInfos', null, 'QueryListPart', function(data){
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}

/**
 * 注销
 * @return
 */
function submitCancel() {
	if (!queryBox(this, "viceCheckBox")) {
		return;
	}
	
	MessageBox.confirm("提示信息", "确定要注销吗？", function(btn) {
		if ("ok" == btn) {
			$.beginPageLoading("提交处理中...");
			var check = $("input[name='viceCheckBox']:checked");
			var params = $.DatasetList();
			for ( var i = 0; i < check.length; i++) {
				var checkedObj = $(check[i]);
				var param = new Wade.DataMap();
				param.put("USER_ID", checkedObj.val());
				params.add(param);
			}
			ajaxSubmit("ConditionPart", "submitCancel",
					"&CANCEL_INFOS=" + params.toString(), "QueryListPart", function(data) {
						$.endPageLoading();
						alert("注销成功！");
						queryGrpBooktInfos();
					}, function(error_code, error_info, derror) {
						$.endPageLoading();
						alert(error_info);
					});
		
		}

	});
}

/**
 * 更新附件
 * @return
 */
function updateUpload() {
	if (!queryBox(this, "viceCheckBox")) {
		return;
	}
	
	MessageBox.confirm("提示信息", "确定要更新附件吗？", function(btn) {
		if ("ok" == btn) {
			$.beginPageLoading("提交处理中...");
			var check = $("input[name='viceCheckBox']:checked");
			var params = $.DatasetList();
			for ( var i = 0; i < check.length; i++) {
				var checkedObj = $(check[i]);
				var param = new Wade.DataMap();
				var importFile = $("#simpleUpload").val();
				param.put("FILE_ID", importFile);
				param.put("USER_ID", checkedObj.val());
				params.add(param);
			}
			
			
			ajaxSubmit("UpdatePart", "updateUpload",
					"&UPDATE_INFOS=" + params.toString(), "QueryListPart", function(data) {
						$.endPageLoading();
						alert("更新成功！");
						queryGrpBooktInfos();
					}, function(error_code, error_info, derror) {
						$.endPageLoading();
						alert(error_info);
					});
		
		}

	});
}
function exportBeforeAction(domid) {
	var groupId = $("#GROUP_ID").val();
	var groupCustName = $("#GROUP_CUST_NAME").val();
	var serialNumber = $("#SERIAL_NUMBER").val();
	var params =  "&GROUP_ID=" + groupId;
	params = params + "&GROUP_CUST_NAME=" + groupCustName;
	params = params + "&SERIAL_NUMBER=" + serialNumber;
	$.Export.get(domid).setParams(params);
	return true;
}
