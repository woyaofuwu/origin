function qryBuyoutTelEqu(){
	if($("#START_REG_DATE").val()==""){
		alert("起始日期不能为空！");
		return false;
	}
	if($("#END_REG_DATE").val()==""){
		alert("终止日期不能为空！");
		return false;
	}
	$.ajax.submit('QueryPart', 'qryBuyoutTelEqu', null, 'ResultDataPart', function(data){
		if(data.get('ALERT_INFO') && data.get('ALERT_INFO') != '')
		{
			alert(data.get('ALERT_INFO'));
		}
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}
function saveBuyoutTelEqu()
{
	var telequ_price = $('#TELEQU_PRICE').val();
	var telequ_count = $('#TELEQU_COUNT').val();
	var telequ_fee_totle = $('#TELEQU_FEE_TOTLE').val();
	var chnlId = $('#CHNL_ID').val();
	if(chnlId == ""){
		alert("代理商不能为空！");
		return false;
	}
	if(telequ_count == "0"){
		alert("输入数量不能为0");
		return false;
	}
	
	
	
	var str1 = '';
	str1 = str1 + "单价(元)：" + telequ_price + "\n";
	str1 = str1 + "数量：" + telequ_count + "\n";
	str1 = str1 + "总计(元)：" + telequ_fee_totle + "\n";
	str1 = str1 + "\n";
	str1 = str1 + "确认提交吗?";
	
	if(!confirm(str1))
	{
		return false;
	}
	$.cssubmit.bindCallBackEvent(function(data){
		var result,title,content;
		if(data.get('LOG_ID') && data.get('LOG_ID') != '')
		{
			result="success";
			title="返回提示";
			content="买断信息保存成功！";
		}else{
			result="error";
			title="返回提示";
			content="买断信息保存失败！";
		}
		$.cssubmit.showMessage(result, title, content, false);
	});
	return true;
}

function checkFee(obj)
{
	if("" == $("#TELEQU_PRICE").val()){
		alert("请选择单价！");
		$(obj).val('0');
		return false;
	}
	if(obj.value > 999999){
		alert("数量不能大于999999,请重新设值!");
		$(obj).val('0');
		return false;
	}
	var desc = $(obj).attr('desc') + "必须为有效值";
	if(!isPInteger(obj, desc))
	{
		return false;
	}
	
	calTotalFee();
	
	return true;
}

function checkFee2(obj)
{
	if("" == $("#TELEQU_PRICE").val()){
		alert("请选择单价!");
		$(obj).val('0');
		return false;
	}
	if(obj.value > 999999){
		alert("数量不能大于999999,请重新设值!");
		$(obj).val('0');
		return false;
	}
	var desc = $(obj).attr('desc') + "必须为有效值";
	if(!isPInteger2(obj, desc))
	{
		return false;
	}
	
	calTotalFee();
	
	return true;
}

function calTotalFee()
{
	var telequ_price = $('#TELEQU_PRICE').val();
	var telequ_count = $('#TELEQU_COUNT').val();
	
	var price = (parseFloat(telequ_price)*100).toFixed(0);
	var count = parseInt(telequ_count);
	var total = ((price*count)/100).toFixed(2)
	$('#TELEQU_FEE_TOTLE').val(total);
}

function isPInteger(field, desc)
{
	if (field.value != "" && !/^(([1-9][0-9]*)|0)([.]\d{1,2})?$/.test(field.value))
	{
		alert(desc);
		$(field).val('0');
		return false;
	}
	return true;
}

function isPInteger2(field, desc)
{
	if ((!/^\d+$/.test(field.value) || !/[^0]/.test(field.value)) && field.value != "")
	{
		alert(desc);
		$(field).val('0');
		return false;
	}
	return true;
}