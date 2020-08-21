
function refreshPartAtferAuth(data)
{
	var param = "&ROUTE_EPARCHY_CODE="+data.get("USER_INFO").get("EPARCHY_CODE")+"&USER_ID="+data.get("USER_INFO").get("USER_ID");

    $.beginPageLoading("宽带资料查询。。。");
	$.ajax.submit('', 'loadChildInfo', param, 'wideInfoPart', function(data){
		    $("#SubmitPart").removeClass("e_dis");
		    $.endPageLoading();
	},
	function(error_code, error_info,detail) { 
    $.endPageLoading();
    MessageBox.error("错误提示", error_info, $.auth.reflushPage, null, null, detail);
});
}


//业务提交
function submitBeforeCheck(){
   if(!verifyAll('wideInfoPart'))
   {
	   return false;
   }
   return true;
}