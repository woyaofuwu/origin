function calTotalFee(obj)
{
	var oldFeeMoney = $(obj).attr('oldFeeMoney');
	var value = $(obj).attr('value');
	if( value == oldFeeMoney)
	{
		return true;
	}
	var desc = $(obj).attr('desc');
	desc = desc + "[" + $(obj).attr('feeType') + "]" + "必须为有效值";
	if(value == ""){
		alert(desc);
		$(obj).val(oldFeeMoney);
		//obj.value=oldFeeMoney+".00";
		return false;
	}
	if(!isPInteger(obj, desc))
	{
		obj.setAttribute("value",oldFeeMoney);
		//obj.focus();
		return false;
	}
	
	//计算总费用
	var tableData =getAllTableData("feeTable");// $.table.get("feeTable").getTableData();
	var feeTotle =  0;
	for(var i=0;i<tableData.length - 1;i++){
		var feeTemp = tableData.get(i).get("FEE_MONEY");
		var fen_temp = (parseFloat(feeTemp)*100).toFixed(0);
		var fen_feeTotle = 	(parseFloat(feeTotle)*100).toFixed(0);
		var tempTotal = ((parseFloat(fen_temp) + parseFloat(fen_feeTotle))/100).toFixed(2);
		feeTotle = tempTotal;
	}
	$("#feeTotle").val(feeTotle);

	
	var fen_temp = (parseFloat(value)*100).toFixed(0);//- (parseFloat(oldFeeMoney)*100).toFixed(0);
	var fen_feeTotle = 	(parseFloat(feeTotle)*100).toFixed(0);
	var tempTotal = ((parseFloat(fen_temp) + parseFloat(fen_feeTotle))/100).toFixed(2);

	obj.setAttribute("oldFeeMoney",value);
	return true;
}

function isPInteger(field, desc)
{
	if (field.value != "" && !/^(([1-9][0-9]*)|0)([.]\d{1,2})?$/.test(field.value))
	{
		alert(desc);
		$(field).val('0.00');
		return false;
	}
	return true;
}

function saveFeeReg(flag)
{
	if(!alertHintInfo(flag))
	{
		return false;
	}
	var str1 = '';
	var feeTotle = $("#feeTotle").val();
	if(flag == 0)
	{
		str1 = '费用合计(元)：' + feeTotle + '\n' + '确定保存吗?';
	}
	else
	{
		str1 = '费用合计(元)：' + feeTotle + '\n' + '确定提交吗?';
	}
	if(feeTotle < 0){
		alert("费用合计不能小于0！");
		return false;
	}
	
	if(feeTotle > 9999999999){
		alert("费用合计大于10位数，请重新输入！");
		return false;
	}
	if(!confirm(str1))
	{
		return false;
	}else{
		var tableData =getAllTableData("feeTable");// $.table.get("feeTable").getTableData();
		var feeDataset=new Wade.DatasetList();
		for(var i=0;i<tableData.length;i++){
			var data = new Wade.DataMap();
			data.put('FEE_TYPE_CODE', tableData.get(i).get("FEE_TYPE_CODE"));
			data.put('FEE_MONEY', tableData.get(i).get("FEE_MONEY"));
			data.put('MODI_FLAG', tableData.get(i).get("MODI_FLAG"));
			feeDataset.add(data);
		}
		var logId = $("#LOG_ID").val();
		var regDate = $("#REG_DATE").val();
		var regStaffId = $("#REG_STAFF_ID").val();
		var regDepartId = $("#REG_DEPART_ID").val();
		var remark = $("#REMARK").val();
		var param = "&feeDataset="+feeDataset+"&REG_DATE="+regDate+"&REG_STAFF_ID="+regStaffId+"&REG_DEPART_ID="+regDepartId
		+"&REMARK="+remark+"&feeTotle="+feeTotle+"&REG_FLAG="+flag+"&LOG_ID="+logId;
		$.ajax.submit('', 'saveFeeRegCTT', param, RefreshTable, function(data){
			if(data.get('LOG_ID') && data.get('LOG_ID') != '')
			{
				if(flag == 0){
					alert("费用登记保存成功!");
				}else{
					alert("费用登记提交成功!");
				}
				$.redirect.toPage(null,'broadband.cttnet.cttnetfeereg.cttfeereg.CttFeeReg', 'initFeeRegCTT','LOG_ID='+data.get('LOG_ID'));
			}
			$.endPageLoading();
		},
		function(error_code,error_info){
			$.endPageLoading();
			alert(error_info);
	    });
	}
	
	return true;
}

function alertHintInfo(flag)
{
	var obj = "";
	var str = "温馨提示：" + "\n";
	str = str + "保存：保存费用信息到数据库，财务未审核，保存后仍可以修改" + "\n";
	str = str + "提交：提交费用信息到数据库，财务可审核，提交后不可以修改" + "\n";
	
	str = str + "\n";
	str = str + "您当前的操作是";
	if(flag == 0)
	{
		str = str + "保存，";
	}
	else
	{
		str = str + "提交，";
	}
	str = str + "确定继续吗？" + "\n";
	
	str = str + "\n";
	str = str + "注意：记得每天最后需要提交一次哦";

	if(!confirm(str))
	{
		return false;
	}
	
	return true;
}

function saveFeeRegM(flag)
{
	
	var feeTotle = $("#feeTotle").val();
	var str1 = '';
	if(flag == 0)
	{
		str1 = '费用合计(元)：' + feeTotle + '\n' + '确定补录吗?';
	}
	else
	{
		str1 = '费用合计(元)：' + feeTotle + '\n' + '确定修改吗?';
	}
	if(feeTotle < 0){
		alert("费用合计不能小于0！");
		return false;
	}
	if(feeTotle > 9999999999){
		alert("费用合计大于10位数，请重新输入！");
		return false;
	}
	if(!confirm(str1))
	{
		return false;
	}else{
		var tableData =getAllTableData("feeTable");
		var feeDataset=new Wade.DatasetList();
		for(var i=0;i<tableData.length;i++){
			var data = new Wade.DataMap();
			data.put('FEE_TYPE_CODE', tableData.get(i).get("FEE_TYPE_CODE"));
			data.put('FEE_MONEY', tableData.get(i).get("FEE_MONEY"));
			data.put('MODI_FLAG', tableData.get(i).get("MODI_FLAG"));
			feeDataset.add(data);
		}
		
		var logId = $("#LOG_ID").val();
		var regDate = $("#REG_DATE").val();
		var regStaffId = $("#REG_STAFF_ID").val();
		var regDepartId = $("#REG_DEPART_ID").val();
		var remark = $("#REMARK").val();
		var param = "&feeDataset="+feeDataset+"&REG_DATE="+regDate+"&REG_STAFF_ID="+regStaffId+"&REG_DEPART_ID="+regDepartId
		+"&REG_FLAG="+flag+"&REMARK="+remark+"&feeTotle="+feeTotle+"&LOG_ID="+logId+"&MethodFlag=1";
		$.ajax.submit('', 'saveFeeRegCTT', param, RefreshTable, function(data){
			if(data.get('LOG_ID') && data.get('LOG_ID') != '')
			{
				alert("费用信息修改成功!");
				$.redirect.toPage(null,'broadband.cttnet.cttnetfeereg.cttfeereg.CttFeeRegM', 'initFeeRegCTT','LOG_ID='+data.get('LOG_ID'))
			}
			$.endPageLoading();
		},
		function(error_code,error_info){
			$.endPageLoading();
			alert(error_info);
	    });
	}
	
	return true;
}


function checkBank(){
	debugger;
	var  payMode = $("#RSRV_TAG1").val();
	var  superBank = $("#RSRV_STR1").val();
	if(payMode == "0"){
		return false;	
	}
	if(payMode == null || payMode == ""){
		alert("请选择缴款方式！");
		return false;	
	}
	if(superBank == null || superBank == ""){
		alert("上级银行不能为空！");
		return false;	
	}
	
}
function checkSuperBank(){
	$("#POP_acctinfo_BANK_CODE").val('') ;
	$("#bankFld").attr('disabled',false);
}

function saveBankFeeReg(flag)
{
	var rsrvTag1 = $("#RSRV_TAG1").val();//缴款方式
	var rsrvStr1 = $("#RSRV_STR1").val();//上级银行
	var bankCode = $("#acctinfo_BANK_CODE").val();//银行名称
	
	if(rsrvTag1 == null || rsrvTag1 == ""){
		alert("请选择缴款方式！");
		return false;	
	}
	if(rsrvStr1 == null || rsrvStr1 == ""){
		alert("上级银行不能为空！");
		return false;	
	}
	if(bankCode == null || bankCode == ""){
		alert("银行名称不能为空！");
		return false;	
	}
	
	if(!alertHintInfo(flag))
	{
		return false;
	}
	var str1 = '';
	var feeTotle = $("#feeTotle").val();
	if(flag == 0)
	{
		str1 = '费用合计(元)：' + feeTotle + '\n' + '确定保存吗?';
	}
	else
	{
		str1 = '费用合计(元)：' + feeTotle + '\n' + '确定提交吗?';
	}
	
	if(feeTotle < 0){
		alert("费用合计不能小于0！");
		return false;
	}
	if(feeTotle > 9999999999){
		alert("费用合计大于10位数，请重新输入！");
		return false;
	}
	if(!confirm(str1))
	{
		return false;
	}else{
		var tableData =getAllTableData("feeTable");// $.table.get("feeTable").getTableData();
		var feeDataset=new Wade.DatasetList();
		for(var i=0;i<tableData.length;i++){
			var data = new Wade.DataMap();
			data.put('FEE_TYPE_CODE', tableData.get(i).get("FEE_TYPE_CODE"));
			data.put('FEE_MONEY', tableData.get(i).get("FEE_MONEY"));
			data.put('MODI_FLAG', tableData.get(i).get("MODI_FLAG"));
			feeDataset.add(data);
		}
		
		var logId = $("#LOG_ID").val();
		var regDate = $("#REG_DATE").val();
		var regStaffId = $("#REG_STAFF_ID").val();
		var regDepartId = $("#REG_DEPART_ID").val();
		var remark = $("#REMARK").val();
		var param = "&feeDataset="+feeDataset+"&REG_DATE="+regDate+"&REG_STAFF_ID="+regStaffId+"&REG_DEPART_ID="+regDepartId+"&RSRV_TAG1="+rsrvTag1
		+"&RSRV_STR1="+rsrvStr1+"&RSRV_STR2="+bankCode+"&REMARK="+remark+"&feeTotle="+feeTotle+"&REG_FLAG="+flag+"&LOG_ID="+logId;
		$.ajax.submit('', 'saveBankFeeRegCTT', param, RefreshTable, function(data){
			if(data.get('LOG_ID') && data.get('LOG_ID') != '')
			{
				if(flag == 0){
					alert("费用登记保存成功!");
				}else{
					alert("费用登记提交成功!");
				}
				$.redirect.toPage(null,'broadband.cttnet.cttnetfeereg.cttfeereg.CttBankFeeReg', 'initBankFeeRegCTT','LOG_ID='+data.get('LOG_ID'));
			}
			$.endPageLoading();
		},
		function(error_code,error_info){
			$.endPageLoading();
			alert(error_info);
	    });
	}
	
	return true;
}

function saveBankFeeRegM()
{
	var rsrvTag1 = $("#RSRV_TAG1").val();//缴款方式
	var rsrvStr1 = $("#RSRV_STR1").val();//上级银行
	var bankCode = $("#acctinfo_BANK_CODE").val();//银行名称
	
	if(rsrvTag1 == null || rsrvTag1 == ""){
		alert("请选择缴款方式！");
		return false;	
	}
	if(rsrvStr1 == null || rsrvStr1 == ""){
		alert("上级银行不能为空！");
		return false;	
	}
	if(bankCode == null || bankCode == ""){
		alert("银行名称不能为空！");
		return false;	
	}
	var str1 = '';
	var feeTotle = $("#feeTotle").val();

	str1 = '费用合计(元)：' + feeTotle + '\n' + '确定修改吗?';
	if(feeTotle < 0){
		alert("费用合计不能小于0！");
		return false;
	}
	if(feeTotle > 9999999999){
		alert("费用合计大于10位数，请重新输入！");
		return false;
	}
	if(!confirm(str1))
	{
		return false;
	}else{
		var tableData =getAllTableData("feeTable");// $.table.get("feeTable").getTableData();
		var feeDataset=new Wade.DatasetList();
		for(var i=0;i<tableData.length;i++){
			var data = new Wade.DataMap();
			data.put('FEE_TYPE_CODE', tableData.get(i).get("FEE_TYPE_CODE"));
			data.put('FEE_MONEY', tableData.get(i).get("FEE_MONEY"));
			data.put('MODI_FLAG', tableData.get(i).get("MODI_FLAG"));
			feeDataset.add(data);
		}
		
		var logId = $("#LOG_ID").val();
		var regDate = $("#REG_DATE").val();
		var regStaffId = $("#REG_STAFF_ID").val();
		var regDepartId = $("#REG_DEPART_ID").val();	
		var remark = $("#REMARK").val();
		var param = "&feeDataset="+feeDataset+"&REG_DATE="+regDate+"&REG_STAFF_ID="+regStaffId+"&REG_DEPART_ID="+regDepartId+"&RSRV_TAG1="+rsrvTag1
		+"&RSRV_STR1="+rsrvStr1+"&RSRV_STR2="+bankCode+"&REMARK="+remark+"&feeTotle="+feeTotle+"&LOG_ID="+logId;
		$.ajax.submit('', 'saveBankFeeRegCTTM', param, 'RefreshTable', function(data){
			if(data.get('LOG_ID') && data.get('LOG_ID') != '')
			{
				alert("费用信息修改成功!");
				$.redirect.toPage(null,'broadband.cttnet.cttnetfeereg.cttfeereg.CttBankFeeRegM', 'initBankFeeRegCTT','LOG_ID='+data.get('LOG_ID'));
			}
			$.endPageLoading();
		},
		function(error_code,error_info){
			$.endPageLoading();
			alert(error_info);
	    });
	}
	
	return true;
}