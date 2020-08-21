/**
 * 拒收挂机提醒短信查询
 * @author zhouwu
 * @date 2014年6月18日 14:17:08 
 */
function queryReject(){
	if($.validate.verifyAll("query_div")){
	debugger;
		var serialNumber = $("#SERIAL_NUMBER").val();
		
		//此校验方法不是很准确
		if(!$.verifylib.checkMbphone(serialNumber)){
			alert('服务号码不是手机号，请重新输入！');
			$('#SERIAL_NUMBER').val('');
			return;
		}
		
		$.beginPageLoading("查询中...");
		$.ajax.submit("query_div","queryReject",null,"viceInfopart",
			function(){
				$.endPageLoading();
			},function(code, msg){
				$.endPageLoading();
				$.showErrorMessage("查询失败", msg);
			});
	}
}