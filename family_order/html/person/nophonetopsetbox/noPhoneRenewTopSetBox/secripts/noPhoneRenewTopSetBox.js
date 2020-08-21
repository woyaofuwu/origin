//用戶认证结束之后执行的js方法
function refreshPartAtferAuth(data)
{
	var topsetboxtime = $("#TOP_SET_BOX_TIME").val();
	$.beginPageLoading("信息加载中......");
	$.ajax.submit('AuthPart', 'loadPageInfo', "&USER_INFO="+ data.get("USER_INFO").toString() + "&CUST_INFO=" + data.get("CUST_INFO").toString()+"&TOP_SET_BOX_TIME="+topsetboxtime, 'userInfoPart,TOPSETBOXTIME,sellTopSetBoxInfoPart',
			function(data) {
				
				$("#TOP_SET_BOX_FEE").val(data.get('TOP_SET_BOX_FEE')/100);
				
				//魔百和缴费
				$.feeMgr.removeFee($("#TRADE_TYPE_CODE").val(),"2","9082");
				var feeData = $.DataMap();
				feeData.clear();
				feeData.put("MODE", "2");
				feeData.put("CODE", "9082");
				feeData.put("FEE", data.get('TOP_SET_BOX_FEE') );
				feeData.put("PAY", data.get('TOP_SET_BOX_FEE') );
				feeData.put("TRADE_TYPE_CODE", $("#TRADE_TYPE_CODE").val());
				$.feeMgr.insertFee(feeData);
				
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

//设置魔百和费用
function settopsetboxfee(){
	var topsetboxtime = $("#TOP_SET_BOX_TIME").val();
	$.ajax.submit(this, 'settopsetboxfee',
		'&TOP_SET_BOX_TIME=' + topsetboxtime, '',
		function(data)
		{
			$("#TOP_SET_BOX_FEE").val(data.get('TOP_SET_BOX_FEE')/100);
			
			//魔百和缴费
			$.feeMgr.removeFee($("#TRADE_TYPE_CODE").val(),"2","9082");
			var feeData = $.DataMap();
			feeData.clear();
			feeData.put("MODE", "2");
			feeData.put("CODE", "9082");
			feeData.put("FEE", data.get('TOP_SET_BOX_FEE') );
			feeData.put("PAY", data.get('TOP_SET_BOX_FEE') );
			feeData.put("TRADE_TYPE_CODE", $("#TRADE_TYPE_CODE").val());
			$.feeMgr.insertFee(feeData);
			
			$.endPageLoading();
		}, function(error_code, error_info) 
		{
			$.endPageLoading();
			$.MessageBox.error(error_code, error_info);
		});
};

//提交前费用校验
function checkFeeBeforeSubmit() 
{
	var fee = $("#TOP_SET_BOX_FEE").val();
	var result = false ;
	if (null == fee || '' == fee)
	{
		fee = '0';
	}
	var param = "&TOPSETBOX_FEE=" + fee
	+ "&SERIAL_NUMBER="+$("#AUTH_SERIAL_NUMBER").val();
	$.beginPageLoading("提交前费用校验中。。。");			
	$.ajax.submit(null, 'checkFeeBeforeSubmit', param, null,
		function(data) 
		{
			$.endPageLoading();
			var resultCode = data.get("X_RESULTCODE");
			if(resultCode == '0')
				result = true ;
		}, function(error_code, error_info) 
		{
			$.endPageLoading();
			$.MessageBox.error(error_code, error_info);
			result = false ;
		},
		{async:false}
	);
	
	return result ;
}
