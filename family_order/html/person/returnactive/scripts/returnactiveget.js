function refreshPartAtferAuth(data)
{
	var serialNumber = data.get('USER_INFO').get('SERIAL_NUMBER');
	var param = '&SERIAL_NUMBER=' + serialNumber;
	param += '&USER_ID=' + data.get('USER_INFO').get('USER_ID');
	$.ajax.submit(null, 'loadInfo', param, '',loadInfoSucc,loadInfoError);
}

function loadInfoSucc(ajaxData)
{
	$('#HAVE_NUM').val(ajaxData.get('HAVE_NUM'));
}

function loadInfoError(code,info){
	$.cssubmit.disabledSubmitBtn(true);
	$.showErrorMessage("错误",info);
}

function onTradeSubmit()
{
	var param = '&SERIAL_NUMBER='+$("#AUTH_SERIAL_NUMBER").val();
	var cardList = $.table.get("GGCardTable").getTableData();
	param += '&REMARK='+$('#REMARK').val();
	param += '&CARD_LIST=' + cardList;
	
	$.cssubmit.addParam(param);
	
	return true;
}

function addGGCard()
{
	var ggCardNo = $('#GGCARD_NO').val();
	if(ggCardNo == '')
	{
		alert('刮刮卡序列号必填');
		return false;
	}
	var haveNum = $('#HAVE_NUM').val();
	if(haveNum <= 0)
	{
		alert('未领取卡总数不足');
		$('#GGCARD_NO').val('');
		return false;
	}
		
	var param = '&GGCARD_NO=' + ggCardNo;
	$.beginPageLoading("刮刮卡号校验。。。");
	$.ajax.submit(null, 'checkCardCodeRes', param, '',checkCardCodeResSucc,
		function(errorcode, errorinfo){
			$.endPageLoading();
			$('#GGCARD_NO').val('');
			alert(errorinfo);
		});	
}

function checkCardCodeResSucc(ajaxData)
{
	var ggCardEdit = new Array();
    ggCardEdit["GGCARD_NO"] = $('#GGCARD_NO').val();
    ggCardEdit["REMARK"] = $('#GGCARD_REMARK').val();
    $.table.get("GGCardTable").addRow(ggCardEdit);
    
    $('#GGCARD_NO').val('');//清空
    var haveNum = $('#HAVE_NUM').val();
    $('#HAVE_NUM').val(haveNum - 1);
	$.endPageLoading();
}

function delGGCard()
{
	var table = $.table.get("GGCardTable");
	var dom = table.getSelected();
	if(dom == null)
	{
		alert('请先选中一行数据');
		return false;
	}
	
	table.deleteRow();
	
	var haveNum = $('#HAVE_NUM').val();
	$('#HAVE_NUM').val(parseInt(haveNum) + 1);
}	