function refreshPartAtferAuth(data)
{
	var serialNumber = data.get('USER_INFO').get('SERIAL_NUMBER');
	var param = '&SERIAL_NUMBER=' + serialNumber;
	param += '&USER_ID=' + data.get('USER_INFO').get('USER_ID');
	$.ajax.submit(null, 'loadInfo', param, 'GGCardPart',loadInfoSucc,loadInfoError);
}

function loadInfoSucc(ajaxData)
{
	//$('#HAVE_NUM').val(ajaxData.get('HAVE_NUM'));
}

function loadInfoError(code,info){
	$.cssubmit.disabledSubmitBtn(true);
	$.showErrorMessage("错误",info);
}

function onTradeSubmit()
{
	var param = '&SERIAL_NUMBER='+$("#AUTH_SERIAL_NUMBER").val();
	var cardList = $.table.get("GGCardTable").getTableData();
	param += '&CARD_LIST=' + cardList;
	
	$.cssubmit.addParam(param);
	
	return true;
}

function exchangeGGCard()
{
	if(!$.validate.verifyAll('GGCardEdit'))
	{
		return;
	}
	var validFlag = $('#VALID_FLAG').val();
	if(validFlag != 'true')
	{
		alert('刮刮卡没校验或校验没通过');
		return;
	}
		
	var cardCode = $('#CARD_CODE').val();
	var cardPassWord = $('#CARD_PASS_WORD').val();
	var itemName = $('#ITEM_NAME').val();
	var giftCode = $('#GIFT_CODE').val();
	var giftName = $("#GIFT_CODE option:selected").text();
	var resCode = $('#RES_CODE').val();
	var remark = $('#REMARK').val();
	
	var ggCardEdit = new Array();
    ggCardEdit["CARD_CODE"] = cardCode;
    ggCardEdit["CARD_PASS_WORD"] = cardPassWord;
    ggCardEdit["ITEM_NAME"] = itemName;
    ggCardEdit["GIFT_CODE"] = giftCode;
    ggCardEdit["GIFT_NAME"] = giftName;
    ggCardEdit["REMARK"] = remark;
    ggCardEdit['RES_CODE'] = resCode;
    
    $.table.get("GGCardTable").updateRow(ggCardEdit);
    
    cleanGGCardEdit();
}

function selectGGCard()
{
	cleanGGCardEdit();
	
	var table = $.table.get("GGCardTable");
	var rowData = table.getRowData();
	$('#CARD_CODE').val(rowData.get('CARD_CODE'));
}	

function cleanGGCardEdit()
{
	resetArea("GGCardEdit");
	$("#GIFT_CODE").empty();
	$("#GIFT_CODE").css('width', '100%');
	$("#GIFT_CODE").css('width', '');
	$("#GIFT_CODE").append("<option value=\"\">--请选择--</option>");	
}

function checkGGCard()
{
	var cardCode = $('#CARD_CODE').val();
	var cardPassWord = $('#CARD_PASS_WORD').val();
	var serialNumber = $.auth.getAuthData().get('USER_INFO').get('SERIAL_NUMBER');
	
	var param = '&CARD_CODE='+cardCode + '&CARD_PASS_WORD=' + cardPassWord + '&SERIAL_NUMBER=' + serialNumber;
	
	$.beginPageLoading("刮刮卡号校验。。。");
	$.ajax.submit(null, 'checkGGCard', param, '',
		function(ajaxData){
			$.endPageLoading();
			$('#ITEM_NAME').val(ajaxData.get('0').get('ITEM_NAME'));
			$('#VALID_FLAG').val("true");
			$("#GIFT_CODE").empty();
			$("#GIFT_CODE").css('width', '100%');
			$("#GIFT_CODE").css('width', '');
			$("#GIFT_CODE").append("<option value=\"\">--请选择--</option>");	
			ajaxData.each(function(item){
				$("#GIFT_CODE").append("<option title=\"" + item.get('PARA_NAME') + "\"" + "value=\"" + item.get("PARA_CODE") + "\">" + item.get('PARA_NAME') + "</option>");	
			});
		},
		function(code, info){
			$.endPageLoading();
			alert(info);
		});
}