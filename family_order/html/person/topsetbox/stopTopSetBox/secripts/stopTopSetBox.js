//用戶认证结束之后执行的js方法
function refreshPartAtferAuth(data)
{
	$.beginPageLoading("信息加载中......");
	$.ajax.submit('AuthPart', 'loadPageInfo', "&USER_INFO="+ data.get("USER_INFO").toString() + "&CUST_INFO=" + data.get("CUST_INFO").toString(), 'userInfoPart,sellTopSetBoxInfoPart',
			function(data) {
				$.cssubmit.disabledSubmitBtn(false,"submitButton");
				$.endPageLoading();
			}, function(error_code, error_info, derror) {
				$.cssubmit.disabledSubmitBtn(true,"submitButton");
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
