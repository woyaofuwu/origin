//用戶认证结束之后执行的js方法
function refreshPartAtferAuth(data)
 {
	$.beginPageLoading("信息加载中......");
	$.ajax.submit('AuthPart', 'loadPageInfo', "&USER_INFO="+ data.get("USER_INFO").toString() + "&CUST_INFO=" + data.get("CUST_INFO").toString(), 'userInfoPart,widenetInfoPart,sellTopSetBoxInfoPart',
			function(data) {
				$.endPageLoading();
				
				$("#Artificial_services").val("0");
				$("#Artificial_services").attr("disabled","true");
				
			}, function(error_code, error_info, derror) {
				$.endPageLoading();
				showDetailErrorInfo(error_code, error_info, derror);
			});
}
/**
 * 校验终端
 */
function checkTerminal(){
	var resID = $("#RES_ID").val();
	var serialNumber = $("#AUTH_SERIAL_NUMBER").val();
	if(resID == ""){
		alert("请输入终端串码");
		return false;
	}
	/**
	 * 老串号
	 * 宽带项目
	 * @author zhuoyingzhi
	 * 20160928
	 */
   var old_res_id=$("#OLD_RES_ID").val();
   if(old_res_id == ""){
	   alert("请先加载用户信息");
	   return false;
   }
	$.beginPageLoading("终端校验中......");
	$.ajax.submit('sellTopSetBoxInfoPart', 'checkTerminal', "&RES_ID="+resID + "&SERIAL_NUMBER=" + serialNumber, 'terminalPart',
			function(data) {
				$.endPageLoading();
				debugger;
				//清理费用
				$.feeMgr.clearFeeList("3800","0");
				var retCode = data.get("X_RESULTCODE");
				if(retCode == 0){
					$("#TERMINAL_CHECK_TAGE").val("1");
					//费用 saleActive=0 没有营销活动
					var saleActive = $("#SALE_ACTIVE").val();
					if(saleActive == "0"){
						var fee = $("#RES_FEE").val();
						var feeData = $.DataMap();
						feeData.put("MODE", "0"); //0:营业费  1：押金 2：预存
						feeData.put("CODE", "60");
						feeData.put("FEE",  fee*100);
						feeData.put("PAY",  fee*100);		
						feeData.put("TRADE_TYPE_CODE","3800");
						$.feeMgr.insertFee(feeData);	
					}else{
						//页面终端价格(元)展示去掉
						$("#RES_FEE").val("0.0");
					}
				}
			}, function(error_code, error_info, derror) {
				$.endPageLoading();
				showDetailErrorInfo(error_code, error_info, derror);
			});
}


/**
 * 业务提交校验
 */
function submitBeforeCheck()
{
	if($("#RES_ID").val()!=$("#RES_NO").val()){
		alert("终端串不一致，请重新校验！");
		return false;
	}
	if(!$.validate.verifyAll("widenetInfoPart")||!$.validate.verifyAll("topSetBoxInfoPart")) {
		return false;
	}
	
	var artitificialService=$("#Artificial_services").val();
	if(!artitificialService||artitificialService==""){
		alert("请选择“需要上门安装”选项！");
		return false;
	}
	
	return true;
}