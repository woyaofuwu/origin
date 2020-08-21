window.Wade_isStopResizeHeight=true
function logout(){
	$.ajax.get("Main","logout",null,null,function(){
		//alert("成功退出");
		$.redirect.toPage("Home");
		
	},function(code,msg){
		alert(msg);
	});
	return false;
}
$(function(){
	var d=new Date();
	$("#span_today").html(d.toLocaleDateString() + " 星期" + ("日一二三四五六日").charAt(d.getDay()));
	
	$("#btn_exit").bind("click",logout);
});

function openShoppingCart(){
    openNav('购物车','shopping.ShoppingCart','','');
}