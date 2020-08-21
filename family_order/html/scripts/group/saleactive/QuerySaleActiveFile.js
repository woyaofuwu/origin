
function querySaleFile(){
		//查询条件校验
	if(!$.validate.verifyAll("ConditionPart")) {
		return false;
	}
		
	var startDate = $("#START_DATE").val();
	var endDate = $("#END_DATE").val();
	if($.trim(startDate).length > 7){
		startDate = startDate.substring(0,7);
	}
	if($.trim(endDate).length > 7){
		endDate = endDate.substring(0,7);
	}
	if(startDate != endDate){
		alert("开始时间和结束时间必须在同一个月内!");
		return false;
	}
	
	$.beginPageLoading("数据查询中..");
	$.ajax.submit('ConditionPart', 'querySaleFileInfos', null, 'QueryListPart', function(data){
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}


function deleteFile() {

	if (!queryBox(this, "viceCheckBox")) {
		return;
	}

	MessageBox.confirm("提示信息", "确定要删除文件吗", function(btn) {

		if ("ok" == btn) {
			$.beginPageLoading("删除文件中...");
			var check = $("input[name='viceCheckBox']:checked");

			var param = "";

			for ( var i = 0; i < check.length; i++) {
				param += check[i].value + ",";
			}

			ajaxSubmit("ConditionPart,LogNav,QueryListPart", "deleteFile",
					"&param=" + param, "QueryListPart", function(data) {
						$.endPageLoading();
					}, function(error_code, error_info, derror) {
						$.endPageLoading();
						showDetailErrorInfo(error_code, error_info, derror);
					});

		}

	});
}

