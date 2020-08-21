function refreshPartAtferAuth(data)
{
	var userId = data.get('USER_INFO').get('USER_ID');
	var serialNumber = data.get('USER_INFO').get('SERIAL_NUMBER');
	var param = '&USER_ID='+userId;
	param += '&SERIAL_NUMBER=' + serialNumber;
	$.ajax.submit(null, 'loadInfo', param, 'viceInfopart',null,loadInfoError);
}

function loadInfoError(code,info){
	$.cssubmit.disabledSubmitBtn(true);
	$.showErrorMessage("错误",info);
}

function addMeb()
{
	var mebSn = $('#FMY_SERIAL_NUMBER').val();
	var mainSn = $("#AUTH_SERIAL_NUMBER").val();
	if(mebSn == '')
	{
		alert('请先输入亲情号码');
		return;
	}
	
	//是否手机号码校验
	//if(!$.verifylib.checkMbphone(mebSn)){
		//alert('不是手机号码，请重新输入！');
		//$('#FMY_SERIAL_NUMBER').val('')
		//return;
	//}
	
	//亲情号码不能与主号一致
	if(mebSn == mainSn){
		alert('对不起，亲情号码不能和主卡号码一样，请重新输入！');
		$('#FMY_SERIAL_NUMBER').val('')
		return;
	}
	
	debugger;
	var list = $.table.get("viceInfoTable").getTableData();
	for(var i = 0, size = list.length; i < size; i++)
	{
		var tmp = list.get(i);
		var sn = tmp.get('SERIAL_NUMBER_B');
		var tag = tmp.get('tag');
		if(mebSn == sn && tag != '1')
		{
			alert('号码'+mebSn+'已经在成员列表');
			return false;
		}
	}
	
	var param = "&SERIAL_NUMBER="+$("#AUTH_SERIAL_NUMBER").val();
	param += '&SERIAL_NUMBER_B='+mebSn+'&MEB_LIST='+list;;
	$.beginPageLoading("成员号码校验...");
	ajaxSubmit(null, 'checkAddMeb', param, '', checkAddMebSucc, 
		function(errorcode, errorinfo){
			$.endPageLoading();
			$('#FMY_SERIAL_NUMBER').val('');
			alert(errorinfo);
		});
}

function checkAddMebSucc(ajaxData)
{
	$.endPageLoading();
	$('#FMY_SERIAL_NUMBER').val('');
	
	var mebSn = ajaxData.get('SERIAL_NUMBER_B');
	var familyEdit = new Array();
	familyEdit["INST_ID"]='<input type="checkbox" id="viceCheckBox" name="viceCheckBox" value="">';
    familyEdit["SERIAL_NUMBER_B"] = mebSn;
    familyEdit["START_DATE"] = "立即";
	familyEdit["END_DATE"] = "2050-12-31 23:59:59";
    $.table.get("viceInfoTable").addRow(familyEdit);
}

function delMeb()
{
	$("#viceInfoTable_Body input[name=viceCheckBox]").each(function(){
	   if(this.checked){
	     	this.click();
	     	//删除时修改结束时间
	        var rowIndex = this.parentNode.parentNode.rowIndex;
	   	    var table = $.table.get("viceInfoTable");
            var json = table.getRowData(null, rowIndex);
	     
	        table.deleteRow();
	   }
	});
}	

function onTradeSubmit()
{
	var data = $.table.get("viceInfoTable").getTableData();
	var param = '&SERIAL_NUMBER='+$("#AUTH_SERIAL_NUMBER").val();
	param += '&MEB_LIST='+data;
	param += '&REMARK='+$('#REMARK').val();
	
	if(data.length == 0) {
		alert("您没有进行任何操作，不能提交！");
		return false;
	}
	
	$.cssubmit.addParam(param);
	
	return true;
}