function queryGiftRelation(){
	$.beginPageLoading("查询中。。。");
		ajaxSubmit('AuthPart,QueryCondPart', 'GiftRelationQuery',"", 'GiftTablePart', function(data){
			$.endPageLoading();
		},
		function(error_code,error_info){
			$.endPageLoading();
			alert(error_info);
	    });
}
function refreshAfterAuth(data){
	$.beginPageLoading("查询中。。。");
	ajaxSubmit('QueryCondPart', 'interRoamPackageQuery',"&SERIAL_NUMBER="+data.get("USER_INFO").get("SERIAL_NUMBER") , 'PackagePart,GiftTablePart', function(data){
		$.endPageLoading();
		if("" != data.get("DISCNT_NAME") && null != data.get("DISCNT_NAME")){
			$("#bt_search").attr("disabled",false);
			$("#PROD_ID").val(data.get("PROD_ID"));
			$("#DISCNT_CODE").val(data.get("DISCNT_CODE"));
		}else{
			alert("未查询到可领取叠加包产品！");
			$("#bt_search").attr("disabled",true);
		}
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}
function getPackage(){
	$.beginPageLoading("业务受理中。。。");
	ajaxSubmit('PackagePart,AuthPart', 'getInterRoamPackage',"", '', function(data){
		$.endPageLoading();
		if("1" == data.get("RESULT")){
			alert("全球通国漫专属叠加包领取已受理，结果将以短信形式发送至您的手机，请注意查收！");
		}
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}
function roamPackageChange(){
	var discntCode = $("#ROAM_PACKAGE").val();
	$("#DISCNT_CODE").val(discntCode);
}
