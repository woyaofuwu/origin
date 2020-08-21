var setSaleBookInfo = function() {
	var obj = $("input:checked");
	
	if(obj && obj.length != 1) {
		alert("请选择一条记录！");
		return ;
	}
	var NEW_USER_ID = obj.val();
	$.getSrcWindow().$("#cond_USER_ID").attr('value',NEW_USER_ID);
	parent.queryRequest();
	
	$.closePopupPage(true,null,null,null,null,true);
};

$(document).ready(function(){
	$("#submitBtn").bind("click", setSaleBookInfo);
});
