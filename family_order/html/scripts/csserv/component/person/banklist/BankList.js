/**
 * 设置银行信息
 */
var setBankInfo = function() {
	var obj = $("input:checked");
	
	if(!obj || !obj.length) {
		alert("请选择一条记录！");
		return ;
	}
	
	$.setReturnValue({"BANK_CODE":obj.val(),"BANK":obj.attr("bank")}, true);
};

$(document).ready(function(){
	$("#qryBankBtn").bind("click", function(){
		if(!$.validate.verifyAll("QueryBankPart")){
			return;
		}
		$.beginPageLoading("加载银行。。。");
		$.ajax.submit("QueryBankPart", "queryBankList", null, "QueryListPart",
			function(data){
				$.endPageLoading();
			},function(code, info, detail){
				$.endPageLoading();
				alert("加载银行报错:"+info);
		});	
	});
	$("#cancelBtn").bind("click", function(){
		$.closePopupPage(true,null,null,null,null,true);
	});
	
	$("#submitBtn").bind("click", setBankInfo);
	
	$("#QueryListPart tbody tr").bind("click", function(e){
		$("input[name=bank]").attr("checked", false);
		$(this).find("input[name=bank]").attr("checked", true);
	});
});
