//办理校验
function checkAndSubmit()
{
	//查询条件校验
	if(!$.validate.verifyAll("blackListProcessPart"))
	{
		return false;
	}
	$.beginPageLoading("业务受理中...");
	
	$.ajax.submit('blackListProcessPart', 'submitProcess', null, 'blackListProcessPart', function(data)
	{
		MessageBox.alert("提示","操作成功！",function(btn)
	{
		if(btn=="ok")
		{
			window.location.href = window.location.href;
		}
	});
		
		$.endPageLoading();
	},
	function(error_code,error_info)
	{
		$.endPageLoading();
		alert(error_info);
    });
}


/**
 * 服务动作代码
 */

function qryActionMark(){
	var selector = $("#cond_SUBMIT_MODE2").val();
	if("2" == selector){
		 $("#blacktime").css('display','none');
		$("#cond_SUBMIT_MODE3").val("");
		$("#cond_SUBMIT_MODE3").attr("nullable","yes");
	}else{
		 $("#blacktime").css('display','');
		$("#cond_SUBMIT_MODE3").attr("nullable","no");
	}
}

