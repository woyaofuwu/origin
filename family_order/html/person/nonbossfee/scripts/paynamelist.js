$(document).ready(function(){
	$("#PAY_NAME").bind("click", function(){
		$(":radio").attr("checked", false);
	});
	$("input[name=PAY_NAME_DESC]").bind("click", function(){
		if(!$(this).attr("checked")){
			return;
		}
		$("#PAY_NAME").val($(this).val());
	});
	$("#PayNameSubmitBtn").bind("click", function(){
		$.setReturnValue({"PAY_NAME_REMARK": $("#PAY_NAME").val()}, true);
	});
	$("#PayNameCancelBtn").bind("click", function(){
		$.closePopupPage(true,null,null,null,null,true)
	});
});

	