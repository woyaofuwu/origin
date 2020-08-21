//用戶认证结束之后执行的js方法
function refreshPartAtferAuth(data)
 {
	$.beginPageLoading("信息加载中......");
	$.ajax.submit('AuthPart', 'setPageInfo', "&USER_INFO="+ data.get("USER_INFO").toString() + "&CUST_INFO=" + data.get("CUST_INFO").toString(), 'userInfoPart,widenetInfoPart,topSetBoxInfoPart',
			function(data) {
				$.endPageLoading();
				debugger;
				
//				var saleActive = $("#SALE_ACTIVE").val()
//				if(saleActive=="0"){
//					var fee = $("#SALE_ACTIVE_MONEY").val();
//					var feeData = $.DataMap();
//					feeData.put("MODE", "1"); //0:营业费  1：押金 2：预存
//					feeData.put("CODE", "60");
//					feeData.put("FEE",  fee*100);
//					feeData.put("PAY",  fee*100);		
//					feeData.put("TRADE_TYPE_CODE","3800");
//					$.feeMgr.insertFee(feeData);
//				}else{
//					//清理费用
//					$.feeMgr.clearFeeList("3800","0");
//				}
				
				var isHasSaleActive=$("#IS_HAS_SALE_ACTIVE").val();
				
				if(isHasSaleActive=="0"){		//如果不免押金
					alert("先办理营销活动，可以减免押金。");
					//MessageBox.alert("提示","先办理营销活动，可以减免押金。", null, null, null, null);
					
					var saleActive=$("#SALE_ACTIVE").val();
					if(saleActive=="0"){
						var saleActiveMoney=$("#SALE_ACTIVE_MONEY").val();
						alert("客户押金不足，请保证账户余额大于200元！");	
						$.cssubmit.disabledSubmitBtn(true,"submitButton");
						return false;
					}
					
				}
				
				
//				var userAction = $("#USER_ACTION").val();
//				if(userAction == 0){
//					MessageBox.alert("","该用户为首次开通互联网电视业务，办理后优惠期内无法更换基础包和优惠包。",null,null);
//				}
				$.cssubmit.disabledSubmitBtn(false,"submitButton");
				
			}, function(error_code, error_info, derror) {
				$.endPageLoading();
				$.cssubmit.disabledSubmitBtn(true,"submitButton");
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
	$.beginPageLoading("终端校验中......");
	$.ajax.submit('userInfoPart', 'checkTerminal', "&RES_ID="+resID + "&SERIAL_NUMBER=" + serialNumber, 'terminalPart,productPart',
			function(data) {
				$.endPageLoading();
				debugger;
				//清理费用
//				$.feeMgr.clearFeeList("3800","0");
//				var retCode = data.get("X_RESULTCODE");
//				if(retCode == 0){
//					$("#TERMINAL_CHECK_TAGE").val("1");
//					//费用 saleActive=0 没有营销活动
//					var saleActive = $("#SALE_ACTIVE").val();
//					if(saleActive == "0"){
//						var fee = $("#RES_FEE").val();
//						var feeData = $.DataMap();
//						feeData.put("MODE", "1"); //0:营业费  1：押金 2：预存
//						feeData.put("CODE", "60");
//						feeData.put("FEE",  fee*100);
//						feeData.put("PAY",  fee*100);		
//						feeData.put("TRADE_TYPE_CODE","3800");
//						$.feeMgr.insertFee(feeData);	
//					}else{
//						//页面终端价格(元)展示去掉
//						$("#RES_FEE").val("0.0");
//					}
//				}
				//页面终端价格(元)展示去掉
				$("#RES_FEE").val("0.0");
				
			}, function(error_code, error_info, derror) {
				$.endPageLoading();
				showDetailErrorInfo(error_code, error_info, derror);
			});
}

/**
 * 查询基础优惠包和可选优惠包
 */
function queryPackages() {
	var productId = $("#PRODUCT_ID").val();
	$.beginPageLoading("基础优惠包和可选优惠包查询中......");
	$.ajax.submit(null, 'queryDiscntPackagesByPID', "&PRODUCT_ID=" + productId, 'bPackagePart,oPackagePart',
			function(data) {
				$.endPageLoading();
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