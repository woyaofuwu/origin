
var setSaleBookInfo = function() {
	var obj = $("input:checked");
	
	if(obj && obj.length != 1) {
		MessageBox.alert("请选择一条记录！");
		return ;
	}
	var NEW_USER_ID = obj.val();
	getNavContent().$("#cond_USER_ID").attr('value',NEW_USER_ID);
	parent.queryRealNameInfo();
	
	hidePopup(this);
};

$(document).ready(function(){
	$("#submitBtn").bind("click", setSaleBookInfo);
    $("#cancelBtn").bind("click", function(){
        hidePopup(this);
    });
});
