function refreshPartAtferAuth(data)
{
	saleactiveModule.clearSaleActive();
	var eparchyCode = data.get('USER_INFO').get('EPARCHY_CODE');
	$('#custinfo_EPARCHY_CODE').val(eparchyCode);
	var acctDayInfo = data.get("ACCTDAY_INFO");
	saleactive.readerComponent(data.get('USER_INFO').get('USER_ID'), acctDayInfo.get("ACCT_DAY"),acctDayInfo.get("FIRST_DATE"),acctDayInfo.get("NEXT_ACCT_DAY"),acctDayInfo.get("NEXT_FIRST_DATE"));
}

function depositForScore(item) {

	if (item.get('ELEMENT_TYPE_CODE') == 'J' && item.get('DEPOSIT_VALUE', '0') != '0') {
		MessageBox.confirm('预存补积分提示', '该用户当前积分不够，需要补充预存费用【' + parseFloat(item.get('DEPOSIT_VALUE', '0')) / 100 + '】元，是否继续业务？', 
			function(btn) {
				if (btn == 'no') {
					getElement('PackagePart').innerHTML = '';
					getElement('START_DATE').value		= '';
					getElement('END_DATE').value		= '';
					deleteAllFee();
				} else {
				}
			});

		item.put('FEE',	item.get('DEPOSIT_VALUE'));
		item.put('FEE_MODE', '2');
		item.put('FEE_TYPE_CODE', item.get('PAYMENT_ID'));
		//insertFee(item);
		alert('新增预存款');
	}
}

function onTradeSubmit()
{
	if(!saleactiveModule.saleactiveSubmitJSCheck())
	{
		return false;
	}
	var saleactiveData = saleactiveModule.getSaleActiveSubmitData();
	var param = '&SERIAL_NUMBER='+$("#AUTH_SERIAL_NUMBER").val();
	param += '&SALEACITVEDATA='+saleactiveData.toString();
	param += '&REMARK='+$('#REMARK').val();
	$.cssubmit.addParam(param);

	var checkParam = "";
	checkParam += '&PACKAGE_ID_A='+saleactiveData.get('PACKAGE_ID');
	checkParam += '&PRODUCT_ID_A='+saleactiveData.get('PRODUCT_ID');
	if(saleactiveData.get('OTHER_NUMBER','') != '')
	{
		checkParam += '&OTHER_NUMBER=' + saleactiveData.get('OTHER_NUMBER','');
	}
	
	$.tradeCheck.addParam(checkParam);

	return true;
}

function afterTradeSubmit(ajaxDataset) {
	$.showSucMessage("受理成功",ajaxDataset.get(0).get('ORDER_ID'));
}

function displaySwitch(btn,o) {
	var button = $(btn);
	var div = $('#'+o);

	if (div.css('display') != "none")
	{
		div.css('display', 'none');
		button.children("i").attr('className', 'e_ico-unfold'); 
		button.children("span:first").text("展示客户信息");
	}
	else {
		div.css('display', '');
		button.children("i").attr('className', 'e_ico-fold'); 
		button.children("span:first").text("不展示客户信息");
	}
}

function checkRemark()
{
	var flag = $("#remarkBox").attr("checked");
	if(flag == true)
	{
		$("#remarkArea").css("display", "");
		$("#REMARK").focus();
		var bodyScroll = $("#bodyScroll");
		$("#bodyScroll").scrollTop($("#bodyScroll").height());
	}
	else
	{
		$("#remarkArea").css("display", "none");
	}
}