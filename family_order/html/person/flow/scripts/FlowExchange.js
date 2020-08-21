
function refreshPartAtferAuth(data)
{
	$.ajax.submit('', 'loadChildInfo', "&USER_INFO="+data.get("USER_INFO")+"&CUST_NAME="+data.get("CUST_INFO").get("CUST_NAME"), 'refreshParts1', function()
	{
		/*$("#CHNAGE_PHONE").attr('disabled',false);
		$("#VALUE_CARD").css('display','none');
		$("#OBJECT_PHONE").css('display','none');
		$("#otherinfo_OBJECT_SERIAL_NUMBER").val('');
		$("#otherinfo_OBJECT_SERIAL_NUMBER").bind("keydown",serialNumberKeydown);*/
	});
}

function myTabSwitchAction(v, v2) 
{
	var objTabset = $.tabset("mytab");
	var title = objTabset.getCurrentTitle();// 获取当前标签页标题
	if (v != title) 
	{
		objTabset.switchTo(v);
	}
	if (v2 != title) 
	{
		objTabset.switchTo(v2);
	}
}

function queryFlowProduct()
{
	$.beginPageLoading("查询中，请稍等...");
	$.ajax.submit(flowProdCond, 'flowProdQry', null, 'ProdListPart');
	$.endPageLoading();
}

/**对应输入数字框*/
function chkInput(obj, boxName) 
{
	var nIndex = $(obj).attr("index");
	var bIsSelect = false;
	var boxList = $("*[name=" + boxName +"]");
	
	for (var i = 0; i < boxList.length; i++) 
	{
		if(i != nIndex)
		{
			boxList[i].checked = false;
		}
		if (boxList[i].checked) 
		{
			var strA1 = boxList[i].getAttribute("FM_BALANCE_ID");
			$("#FM_BALANCE_ID").val(strA1);
			var strA2 = boxList[i].getAttribute("FM_ACCT_ID");
			$("#FM_ACCT_ID").val(strA2);
			var strA3 = boxList[i].getAttribute("COMM_ID");
			$("#COMM_ID").val(strA3);
			var strA4 = boxList[i].getAttribute("BALANCE");
			$("#TRANS_VALUE").val(strA4);
			$("#BALANCE").val(strA4);
			var strA5 = boxList[i].getAttribute("INIT_FEE");
			$("#INIT_FEE").val(strA5);
			var strA6 = boxList[i].getAttribute("FM_ASSET_TYPE_ID");
			$("#FM_ASSET_TYPE_ID").val(strA6);
			var strA7 = boxList[i].getAttribute("INIT_FLOW");
			$("#INIT_FLOW").val(strA7);
			var strA8 = boxList[i].getAttribute("EFFECTIVE_DATE");
			$("#EFFECTIVE_DATE").val(strA8);
			var strA9 = boxList[i].getAttribute("EXPIRE_DATE");
			$("#EXPIRE_DATE").val(strA9);
			bIsSelect = true;
		}
	}
	if(bIsSelect == false)
	{
		$("#FM_BALANCE_ID").val('');
		$("#FM_ACCT_ID").val('');
		$("#COMM_ID").val('');
		$("#TRANS_VALUE").val('');
		$("#BALANCE").val('');
		$("#INIT_FEE").val('');
		$("#FM_ASSET_TYPE_ID").val('');
		$("#INIT_FLOW").val('');
		$("#EFFECTIVE_DATE").val('');
		$("#EXPIRE_DATE").val('');
	}
}

function comparabalance()
{
	var nBALANCE = $("#TRANS_VALUE").val();
	var nBALANCE2 = $("#BALANCE").val();
	
	nBALANCE = parseInt(nBALANCE);
	//$("#TRANS_VALUE").val(nBALANCE);
	nBALANCE2 = parseInt(nBALANCE2);
	
	if(nBALANCE < 0)
	{
		$("#TRANS_VALUE").val('0');
		alert('返销金额不能小于0');
		return;
	}
	
	if(nBALANCE > nBALANCE2)
	{
		$("#TRANS_VALUE").val('0');
		alert('账本余额不能大于列表中的值');
	}
}

//点击表格行，初始化编辑区
function tableRowClick(){
	var rowData = $.table.get("ProdListTable").getRowData();
	var strAMOUNT = rowData.get("INIT_VALUE");
	var strTRANS_FEE = rowData.get("PRICE");
	var strCOMM_ID = rowData.get("COMM_NO");
	var strEFFECTIVE_DATE = rowData.get("START_DATE");
	var strEXPIRE_DATE = rowData.get("END_DATE");
	$("#AMOUNT").val(strAMOUNT);
	$("#TRANS_FEE").val(strTRANS_FEE);
	$("#COMM_ID").val(strCOMM_ID);
	$("#EFFECTIVE_DATE").val(strEFFECTIVE_DATE);
	$("#EXPIRE_DATE").val(strEXPIRE_DATE);
}

//业务提交
function submitTrade(form)
{
	/*var rowData = $.table.get("BalanceTable").getRowData();
	if (rowData.length == 0) 
	{
		alert("请您选择记录后再进行提交！");
		return false;
	}*/
	
	if(!$.validate.verifyBox(form,'custs','请选中要操作的数据!'))
	{
		return false;
	}
	
	if (!$.validate.verifyAll())
	{
		return false;
	}
	
	//var strAMOUNT = rowData.get("BALANCE");
	//var strCOMM_ID = rowData.get("COMM_ID");
	
	//var strAMOUNT = $("#TRANS_VALUE").val();
	//var strCOMM_ID = $("#BALANCE").val();
	
	var param = 'SERIAL_NUMBER=' + $("#AUTH_SERIAL_NUMBER").val();
	//param += '&AMOUNT=' + strAMOUNT;
	//param += '&TRANS_FEE=' + $("#TRANS_FEE").val();
	//param += '&COMM_ID=' + $("#COMM_ID").val();
	//param += '&EFFECTIVE_DATE=' + $("#EFFECTIVE_DATE").val();
	//param += '&EXPIRE_DATE=' + $("#EXPIRE_DATE").val();
	//param += '&COMM_ID=' + strCOMM_ID;
	//param += '&REMARK=' + $('#REMARK').val();
	
	$.cssubmit.addParam(param);
	return true;
	//$.cssubmit.submitTrade(param);
}

function SuccFunc()
{
	alert("流量清退成功!");
	reloadSelfLocation();
}

