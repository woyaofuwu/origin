function refreshPartAtferAuth(data)
{
	//resetArea('contentPart', true);
	
	$.table.get("RentMobileTable").cleanRows();
	//$.table.get("RefundMobileTable").cleanRows();
	
	$.ajax.submit('', 'loadChildInfo', "&RENT_TAG=1&USER_ID="+data.get("USER_INFO").get("USER_ID").toString()+"&SERIAL_NUMBER="+data.get("USER_INFO").get("SERIAL_NUMBER").toString(), 'ResInfoPart,rentMobileInfoPart', function(data){
		$.endPageLoading();
	},
	function(error_code,error_info,detail){
		$.endPageLoading();
		MessageBox.error("错误提示", error_info, $.auth.reflushPage, null, null, detail);
    });
    
    $("#bquery").attr("disabled", false).removeClass("e_dis");
}

/**
function changeRentTag()
{
	var rentTag = $('#RENT_TAG').val();
	var userRentMobilePart = $('#RefundMobilePart');
	var rentMobilePart = $('#RentMobilePart');
	if(rentTag == '0')
	{//退租
		rentMobilePart.css('display', 'none');
		userRentMobilePart.css('display', '');
		$('#RENT_TYPE_CODE').attr('disabled', true);
		$('#MONEY').attr('disabled', true);
		$('#MONEY').val('0');
		$.feeMgr.removeFee('242','1','1');
	}
	else if(rentTag == '1')
	{//租机
		rentMobilePart.css('display', '');
		userRentMobilePart.css('display', 'none');
		$('#RENT_TYPE_CODE').attr('disabled', false);
		$('#MONEY').attr('disabled', false);
	}
}
*/

function queryRentMobile() {
	var rentTag = $('#RENT_TAG').val();
	var rentTypeCode = $('#RENT_TYPE_CODE').val();
	var authData = $.auth.getAuthData();
	var serialNumber = authData.get('USER_INFO').get('SERIAL_NUMBER');
	var userId = authData.get('USER_INFO').get('USER_ID');
	
	var param = '&RENT_TAG='+rentTag;
	param += '&RENT_TYPE_CODE='+rentTypeCode;
	param += '&USER_ID=' + userId;
	param += '&SERIAL_NUMBER=' + serialNumber;
	if(rentTag == '')
	{
		alert('请选择租机状态!');
		return false;
	}
	else if(rentTag == '0')
	{
		//退机
		//$.beginPageLoading("信息查询中..");
		//ajaxSubmit(null,'queryRentMobile',param,'RefundMobilePart');
		//$.endPageLoading();
		$.beginPageLoading("信息查询中..");
     	$.ajax.submit(null, 'queryRentMobile', param, 'RefundMobilePart',function(data){
			$.endPageLoading();
		},
	
		function(error_code,error_info,detail){
			$.endPageLoading();
			MessageBox.error("错误提示", error_info, null, null, null, detail);
    	});
	}
	else if(rentTag == '1')
	{
		//租机
		if(rentTypeCode == '')
		{
			alert('请选择租机类型!');
			return false;
		}
		//$.beginPageLoading("信息查询中..");
		//ajaxSubmit(null,'queryRentMobile',param,'RentMobilePart');
		//$.endPageLoading();
		$.beginPageLoading("信息查询中..");
     	$.ajax.submit(null, 'queryRentMobile', param, 'RentMobilePart',function(data){
			$.endPageLoading();
		},
	
		function(error_code,error_info,detail){
			$.endPageLoading();
			MessageBox.error("错误提示", error_info, null, null, null, detail);
    	});
	}
}

/**
*增加押金
*/
function addForeGift()
{
	var fee = $('#MONEY').val();
	if(fee==null || fee=="") {
		fee = 0;
		$('#MONEY').val(fee);
	}
	
	if($.isNumeric(fee)) {
		//alert(fee);
		//删除老费用
		$.feeMgr.removeFee('242','1','1');
		//新增费用
		var feeData = $.DataMap();
		feeData.put("TRADE_TYPE_CODE", "242");
		feeData.put("MODE", "1");
		feeData.put("CODE", "1");
		feeData.put("FEE", fee*100);
		$.feeMgr.insertFee(feeData);
	}else {
		alert('数字格式不正确!');
	}
	
}

/**
	增加预存款
*/

function addPayMoney()
{
	var fee = $('#PAY_MONEY').val();
	if(fee==null || fee=="") {
		fee = 0;
		$('#PAY_MONEY').val(fee);
	}
	if($.isNumeric(fee)) {
		//alert(fee);
		//删除老费用
		$.feeMgr.removeFee('242','2','0');
		//新增费用
		var feeData = $.DataMap();
		feeData.put("TRADE_TYPE_CODE", "242");
		feeData.put("MODE", "2");
		feeData.put("CODE", "0");
		feeData.put("FEE", fee*100);
		$.feeMgr.insertFee(feeData);
	}else {
		alert('数字格式不正确!');
	}
}

function onTradeSubmit()
{
	if(!verifyAll('rentMobileInfoPart'))
   	{
	   return false;
   	}
   	
	var param = "";
	var authData = $.auth.getAuthData();
	//var checkboxValue;
	var data;
	var rentTag = $('#RENT_TAG').val();
	if(rentTag == '')
	{
		alert('请选择租机状态!');
		return false;
	}
	if(rentTag == '0')
	{
		//checkboxValue = getCheckedValues('refundMobileCheck');
		data = $.table.get("RefundTable").getCheckedRowDatas();//获取选择中的数据
	}
	else if(rentTag == '1')
	{
		if($('#RENT_TYPE_CODE').val() == '')
		{
			alert('请选择租机类型!');
			return false;
		}
		//checkboxValue = getCheckedValues('rentMobileCheck');
		data = $.table.get("RentMobileTable").getCheckedRowDatas();//获取选择中的数据
		//alert(data);
		//alert(data.get(0).get("PARA_CODE3"));
	}
	
	/**
	if(checkboxValue == '')
	{
		alert('请选择一部手机办理退租或者租机!');
		return false;
	}
	
	var arrRentMobile = checkboxValue.split(',');
	
	if(arrRentMobile.length > 1)
	{
		alert('只能选择一部手机!');
		return false;
	}
	var rentMobile = arrRentMobile[0];
	*/
	if(data == '' || data.length == 0) {
		alert('请选择一部手机办理退租或者租机!');
		return false;
	}
	
	if(data.length > 1)
	{
		alert('只能选择一部手机!');
		return false;
	}
	
	addForeGift();
	addPayMoney();
	
	var map = data.get(0);
	
	param += "&SERIAL_NUMBER="+authData.get("USER_INFO").get("SERIAL_NUMBER");
	param += "&RENT_LIST="+map;
	param += "&RENT_TAG="+$('#RENT_TAG').val();
	param += "&RENT_TYPE_CODE="+$('#RENT_TYPE_CODE').val();
	param += "&RENT_MODE_CODE="+$('#RENT_MODE_CODE').val();
	param += "&INVOICE_NO="+$('#INVOICE_NO').val();
	param += "&START_DATE="+$('#RENT_DATE').val();
	
	$.cssubmit.addParam(param);
	return true;
}
