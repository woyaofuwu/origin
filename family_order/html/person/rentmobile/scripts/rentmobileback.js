function refreshPartAtferAuth(data)
{
	//resetArea('contentPart', true);
	
	//$.table.get("RentMobileTable").cleanRows();
	$.table.get("RefundMobileTable").cleanRows();
	
	$.ajax.submit('', 'loadChildInfo', "&&RENT_TAG=0&USER_ID="+data.get("USER_INFO").get("USER_ID").toString()+"&SERIAL_NUMBER="+data.get("USER_INFO").get("SERIAL_NUMBER").toString(), 'ResInfoPart,rentMobileInfoPart', function(data){
		$.endPageLoading();
	},
	function(error_code,error_info,detail){
		$.endPageLoading();
		MessageBox.error("错误提示", error_info, $.auth.reflushPage, null, null, detail);
    });
    
    $('#RENT_TAG').val('0');
    $('#RENT_TYPE_CODE').attr('disabled', true);
    $("#bquery").attr("disabled", false).removeClass("e_dis");
}

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
*删除预存款
*/
function removePayMoney() {
	$.feeMgr.removeFee('243','2','0');
}

/**
*删除押金
*/
function remobeForeGift() {
	$.feeMgr.removeFee('243','1','1');
}

/**
*增加营业费用
*/
function addBusiFee(data)
{
	var rentStartDate = data.get("START_DATE");
	var rsrvDate = data.get("RSRV_DATE");
	var rentModeCode = data.get("RENT_MODE_CODE");
	var startDate = rsrvDate.substr(0,10);
	var rsrvDays = daysBetween(getCurrDate(), startDate);
	//alert("rentStartDate" + rentStartDate + "~~~rsrvDate" + rsrvDate + "~~~rsrvDays" + rsrvDays);
	if(rentStartDate!=rsrvDate && rsrvDays==0) {
		var isBlance = confirm("该用户今天已经结算过租金，如需退租请直接点击确认!");
		if(!isBlance) {
			return false;
		}
	}else {
		var rentDays =daysBetween(getCurrDate(), rsrvDate);
		var months = 0;
		if(rentModeCode == 1) {
		 	remobeForeGift();
		 	removePayMoney()
		 
		 	//删除老费用
			$.feeMgr.removeFee('243','0','160');
			//新增费用
			var rentMoney = rentDays * 30;
			var feeData = new $.DataMap();
			feeData.put("TRADE_TYPE_CODE", "243");
			feeData.put("MODE", "0");
			feeData.put("CODE", "160");
			feeData.put("FEE", rentMoney * 100);
			$.feeMgr.insertFee(feeData);
	  	}else {
		 	remobeForeGift();
		 	removePayMoney();
		 
			//删除老费用
			$.feeMgr.removeFee('243','0','160');
			//新增费用
			months = Math.ceil(rentDays / 15);
			var rentMoney = months * 250;
			var feeData = new $.DataMap();
			feeData.put("TRADE_TYPE_CODE", "243");
			feeData.put("MODE", "0");
			feeData.put("CODE", "160");
			feeData.put("FEE", rentMoney * 100);
			$.feeMgr.insertFee(feeData);
	  	}
	}
	return true;
}

function onTradeSubmit()
{
	var param = "";
	var authData = $.auth.getAuthData();
	var data;
	var rentTag = $('#RENT_TAG').val();
	if(rentTag == '')
	{
		alert('请选择租机状态!');
		return false;
	}
	if(rentTag == '0')
	{
		data = $.table.get("RefundMobileTable").getCheckedRowDatas();//获取选择中的数据
	}
	else if(rentTag == '1')
	{
		if($('#RENT_TYPE_CODE').val() == '')
		{
			alert('请选择租机类型!');
			return false;
		}
		data = $.table.get("RentMobileTable").getCheckedRowDatas();//获取选择中的数据
	}
	
	if(data == '' || data.length == 0) {
		alert('请选择一部手机办理退租或者租机!');
		return false;
	}
	
	if(data.length > 1)
	{
		alert('只能选择一部手机!');
		return false;
	}
	
	var map = data.get(0);
	if(!addBusiFee(map)) {
		return false;
	}

	param += "&SERIAL_NUMBER="+authData.get("USER_INFO").get("SERIAL_NUMBER");
	param += "&RENT_LIST="+map;
	param += "&RENT_TAG="+$('#RENT_TAG').val();
	param += "&RENT_TYPE_CODE="+$('#RENT_TYPE_CODE').val();
	param += "&RENT_MODE_CODE="+map.get('RENT_MODE_CODE');
	param += "&INVOICE_NO="+$('#INVOICE_NO').val();
	$.cssubmit.addParam(param);
	//$.cssubmit.bindCallBackEvent(reprintCallBack);		//设置提交业务后回调事件
	return true;
}

function reprintCallBack(data)
{
	if(data && data.length>0) {
		var content = "退租成功，押金请到押金业务界面清退！<br/>客户订单标识：" + data.get(0).get("ORDER_ID") + "<br/>点【确定】继续业务受理。";
		$.cssubmit.showMessage("success", "业务受理成功", content, true);
	}
}

 /*比较两日期相关天数*/
function daysBetween(DateOne,DateTwo){   
    var OneMonth = DateOne.substring(5,DateOne.lastIndexOf ('-'));  
    var OneDay = DateOne.substring(DateOne.length,DateOne.lastIndexOf ('-')+1);  
    var OneYear = DateOne.substring(0,DateOne.indexOf ('-'));  
  
    var TwoMonth = DateTwo.substring(5,DateTwo.lastIndexOf ('-'));  
    var TwoDay = DateTwo.substring(DateTwo.length,DateTwo.lastIndexOf ('-')+1);  
    var TwoYear = DateTwo.substring(0,DateTwo.indexOf ('-'));  
  
    var cha=((Date.parse(OneMonth+'/'+OneDay+'/'+OneYear)- Date.parse(TwoMonth+'/'+TwoDay+'/'+TwoYear))/86400000);
	return Math.ceil(Math.abs(cha));
}

/*当前时间YYYY-MM-DD*/
function getCurrDate() {   
  	var now = new Date();
  	y = now.getFullYear();
  	m = now.getMonth()+1;
  	d = now.getDate();
  	m = m<10?"0"+m:m;
  	d = d<10?"0"+d:d;
  	return y+"-"+m+"-"+d;
}