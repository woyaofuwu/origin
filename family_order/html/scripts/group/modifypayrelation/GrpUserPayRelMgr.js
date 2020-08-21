/**
 * 查询集团信控已暂停的代付关系集团用户
 * @param data
 * @return
 */
function queryCreditStopPayRel(data){
	beginPageLoading("正在查询..");
	$.ajax.submit('queryForm', 'queryCreditStopPayRel', null, 'StopPayRelGrpUsr', function(data){		
		$.endPageLoading();	
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
}


function submitRestart(sel) {
	var eachUser = $.DataMap();
	var groupSn = $(sel).attr('groupSn');
	var groupUserId = $(sel).attr('groupUserId');
	eachUser.put("SERIAL_NUMBER", groupSn);
	eachUser.put("USER_ID", groupUserId);
	$('#checked_UserInfos').val(eachUser.toString());
	MessageBox.confirm("提示信息","您确认要发起["+groupSn+"]用户的集团代付信控恢复吗？",function(btn){
		if(btn=="ok"){
			beginPageLoading("正在恢复处理..");
			$.ajax.submit('refreashPart2', 'submitRestart', null, '', function(data){	
				var desc = data.get(0).get("RESULT_INFO");
				MessageBox.success("处理结果提示", desc, null, null, null);
				$("#"+groupUserId).attr('disabled', true);
				if(data.get(0).get("RESULT_CODE")=='0'){
					$("#"+groupUserId).attr('disabled', true);
				}
				$.endPageLoading();	
			},
			function(error_code,error_info,derror){
				$.endPageLoading();
				showDetailErrorInfo(error_code,error_info,derror);
		    });
		}else{
			return false;
		}
	},{ok:"确定",cancel:"取消"});

	return true;
}



