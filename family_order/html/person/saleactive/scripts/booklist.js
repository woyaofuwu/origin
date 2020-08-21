var setSaleBookInfo = function() {
	var obj = $("input:checked");
	
	if(obj && obj.length != 1) {
		alert("请选择一条记录！");
		return ;
	}
	var netOrderId = obj.val();
	parent.$.SaleActiveTrade.setParam(netOrderId);
	parent.$.SaleActiveTrade.refreshPartAtferAuth();
	$.closePopupPage(true,null,null,null,null,true);
};

$(document).ready(function(){
	$("#submitBtn").bind("click", setSaleBookInfo);
});
