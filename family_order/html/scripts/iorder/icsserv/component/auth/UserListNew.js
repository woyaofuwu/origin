/**
 * 设置用户信息
 */
var setUserInfo = function() {
	var userObj = $("input:checked");
	
	if(userObj && userObj.length != 1) {
		alert("请选择一个用户！");
		return ;
	}
	
	//$.setReturnValue({"SELECTED_AUTH_USER":userObj.val()}, true);
	setPopupReturnValue(this, {"SELECTED_AUTH_USER":userObj.val()},true);
};
$(document).ready(function(){
	$("#cancelBtn").bind("click", function(){
		hidePopup(this);
//		$.closePopupPage(true,null,null,null,null,true);
	});
	
	$("#submitBtn").bind("click", setUserInfo);
	
	$("#userList tr").bind("click", function(e){
		$("input[type='radio']").attr("checked", false);
		$(this).find("input[type='radio']").attr("checked", true);
	});
	setTimeout((function(){
		// $("#userListScroll").html($("#userListScroll").html());
        //重新调整SIZE
        if(parent.$(".c_dialog").length==1){
            var okBtnTop = $("#cancelBtn").offset().top;
            var okBtnHeight = $("#cancelBtn").height();
            var totalHeight = okBtnTop+okBtnHeight;
            parent.$(".c_dialog").find(".content").attr("style","height:"+totalHeight+"px");
        }
        resultTab.adjust();
	}),1);
});
