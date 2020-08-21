
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
			var strA1 = boxList[i].getAttribute("DISCNT_NAME");
			$("#DISCNT_NAME").val(strA1);
			var strA2 = boxList[i].getAttribute("DISCNT_CODE");
			$("#DISCNT_CODE").val(strA2);
			var strA3 = boxList[i].getAttribute("ITEM_TYPE");
			$("#ITEM_TYPE").val(strA3);
			var strA4 = boxList[i].getAttribute("BALANCE");
			strA4 = parseInt(strA4);
			$("#RETURN_VALUE").val(strA4);
			$("#BALANCE").val(strA4);
			var strA5 = boxList[i].getAttribute("ITEM_VALUE");
			$("#ITEM_VALUE").val(strA5);
			var strA6 = boxList[i].getAttribute("USER_ID");
			$("#USER_ID").val(strA6);
			var strA7 = boxList[i].getAttribute("FEEPOLICY_INS_ID");
			$("#RES_INS_ID").val(strA7);
			var strA8 = boxList[i].getAttribute("START_DATE");
			$("#START_DATE").val(strA8);
			var strA9 = boxList[i].getAttribute("END_DATE");
			$("#END_DATE").val(strA9);
			var strA10 = boxList[i].getAttribute("CARRY_OVER_TAG");
			$("#CARRY_OVER_TAG").val(strA10);
			var strA11 = boxList[i].getAttribute("DETAIL_ITEM");
			$("#DETAIL_ITEM").val(strA11);
			var strA12 = boxList[i].getAttribute("USER_BEGIN_DATE");
			$("#USER_BEGIN_DATE").val(strA12);
			var strA13 = boxList[i].getAttribute("USER_END_DATE");
			$("#USER_END_DATE").val(strA13);
			
			bIsSelect = true;
		}
	}
	if(bIsSelect == false)
	{
		$("#DISCNT_NAME").val('');
		$("#DISCNT_CODE").val('');
		$("#ITEM_TYPE").val('');
		$("#RETURN_VALUE").val('');
		$("#BALANCE").val('');
		$("#ITEM_VALUE").val('');
		$("#RES_INS_ID").val('');
		$("#START_DATE").val('');
		$("#END_DATE").val('');
		$("#CARRY_OVER_TAG").val('');
		$("#DETAIL_ITEM").val('');
		$("#USER_BEGIN_DATE").val('');
		$("#USER_END_DATE").val('');
	}
}

function comparabalance()
{
	var nBALANCE = $("#RETURN_VALUE").val();
	var nBALANCE2 = $("#BALANCE").val();
	
	nBALANCE = parseInt(nBALANCE);
	//$("#RETURN_VALUE").val(nBALANCE);
	nBALANCE2 = parseInt(nBALANCE2);
	
	if(nBALANCE < 0)
	{
		$("#RETURN_VALUE").val('0');
		alert('返销金额不能小于0');
		return;
	}
	
	if(nBALANCE > nBALANCE2)
	{
		$("#RETURN_VALUE").val('0');
		alert('返销金额大于列表中的值');
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
	/*var rowData = $.table.get("ProdListTable").getRowData();
	if (rowData.length == 0) 
	{
		alert("请您选择记录后再进行提交！");
		return false;
	}
	
	var strRES_INS_ID = rowData.get("FEEPOLICY_INS_ID");
	var strRETURN_VALUE = rowData.get("RETURN_VALUE");*/
	
	if(!$.validate.verifyBox(form,'custs','请选中要操作的数据!'))
	{
		return false;
	}
	
	if (!$.validate.verifyAll())
	{
		return false;
	}
	
	//var strAMOUNT = $("#FEEPOLICY_INS_ID").val();
	
	var param = 'SERIAL_NUMBER=' + $("#AUTH_SERIAL_NUMBER").val();
	//param += '&RES_INS_ID=' + strRES_INS_ID;
	//param += '&TRANS_FEE=' + $("#TRANS_FEE").val();
	//param += '&COMM_ID=' + $("#COMM_ID").val();
	//param += '&EFFECTIVE_DATE=' + $("#EFFECTIVE_DATE").val();
	//param += '&EXPIRE_DATE=' + $("#EXPIRE_DATE").val();
	//param += '&RETURN_VALUE=' + strRETURN_VALUE;
	
	$.cssubmit.addParam(param);
	return true;
	//$.cssubmit.submitTrade(param);
}

function SuccFunc()
{
	alert("流量清退成功!");
	reloadSelfLocation();
}

