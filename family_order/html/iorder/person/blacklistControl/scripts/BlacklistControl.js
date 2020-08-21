
function qryUserData(){
	
	//$.beginPageLoading("正在查询数据...");
	ajaxSubmit('BlacklistControlPart','qryUserData','','QueryListPart',function(date){
		$.endPageLoading();
		if (date.get('ALERT_INFO') != '') {    //弹出返回的页面提示信息
			MessageBox.alert(date.get('ALERT_INFO'));
		}
		}, function(code, info, detail) {
		    $.endPageLoading();
		    MessageBox.error("错误提示", info, function(btn) {
		    }, null, detail);
		   }, function() {
		    $.endPageLoading();
		    MessageBox.alert("告警提示", "操作超时!", function(btn) {
		    });
		  });
	
}

//批量导入
function batImportBlack(){
//	backPopup('UI-popup');
	var importFile = $("#FILE_ID").val();
    if (importFile == "") {
        MessageBox.alert("提示", "请选择需要导入的文件！");
        return;
    }
	$.beginPageLoading("正在录入数据...");
	var param = "&params=" + importFile;
	 $.ajax.submit(null, "importUserDataa", param, "", function (data) {
	        $.endPageLoading();
	        var msg = data.get("RESULT_MSG");
	        MessageBox.success("提示：", msg, function (btn) {
	        });
	    }, function (error_code, error_info) {
	        $.endPageLoading();
	        var tip_info = "文件导入失败！";
	        var new_error_info = (function () {
	            if (error_info.indexOf(tip_info) == 1) {
	                return error_info.substring(tip_info.length + 1, error_info.length - 1);
	            } else {
	                tip_info = "";
	                return error_info;
	            }
	        })();
	        MessageBox.error("错误提示", tip_info, null, null, new_error_info);
	    });
}

//信息提交
function insertUserData(){
	
	var serial = $("#SERIAL_NUMBER").val();
	if(serial==null || serial==""){
		$.TipBox.show(document.getElementById("SERIAL_NUMBER"), "手机号码不能为空！", "red");
		return false;
	}

	//$.beginPageLoading("正在提交数据...");
	ajaxSubmit('AddBlacklistControlPart','insertUserData','','',function(data){
		$.endPageLoading();
		MessageBox.alert(data.get('SUCCESS'));
	}, function(code, info, detail) {
	    $.endPageLoading();
	    MessageBox.error("错误提示", info, function(btn) {
	    }, null, detail);
	   });
	
}

function openBlind(){
	popupPage('黑名单录入界面', 'blacklistControl.AddBlacklist', 'onInitTrade', null,"iorder","c_popup c_popup-full",null,null);
}


function delBlackUser(){
	var values = getCheckedValues("monitorInfoCheckBox");
	var param = "&SERIAL_NUMBER=" + values;
	if(values==null || values==""){
		MessageBox.error("提示信息", "至少选择一条数据");
		return false;
	}
	$.beginPageLoading("业务受理中..");
    $.ajax.submit('BlacklistControlPart', 'delBlackUser', param, 'QueryListPart', function(data){
		$.endPageLoading();
		MessageBox.alert("提示", "号码删除成功!");
    }, function(code, info, detail) {
	    $.endPageLoading();
	    MessageBox.error("错误提示", info, function(btn) {
	    }, null, detail);
	   });
    return true;
}
