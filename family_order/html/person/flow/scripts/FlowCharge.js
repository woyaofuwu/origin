
function refreshPartAtferAuth(data)
{
	$.ajax.submit('', 'loadChildInfo', "&USER_INFO="+data.get("USER_INFO")+"&CUST_NAME="+data.get("CUST_INFO").get("CUST_NAME"), 'BalancePart,refreshParts2', function()
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
	var strSn = $("#AUTH_SERIAL_NUMBER").val();
	if(strSn == '' && strSn.length == 0)
	{
		alert('请先查询服务号码');
		return;
	}
	var param = '&QR_USER_ID=' + $("#SEL_USER_ID").val();
	$.beginPageLoading("正在查询数据...");
	//$.ajax.submit('flowProdCond', 'flowProdQry', null, 'ProdListPart');
	$.ajax.submit('flowProdCond', 'flowProdQry', param,
			'ProdListPart', function(data) {
				$.endPageLoading();
			}, function(error_code,error_info) {
				$.endPageLoading();
				alert(error_info);
			});
}

function onFastPayCodeChange(obj)
{
	var strSn = $("#AUTH_SERIAL_NUMBER").val();
	if(strSn == '' && strSn.length == 0)
	{
		alert('请先查询服务号码');
		return;
	}
	var strFastPayCode = $("#FAST_PAY_CODE").val();
	var param = 'FP_FAST_PAY_CODE=' + strFastPayCode;
	$.beginPageLoading("查询中，请稍等...");
	$.ajax.submit('', 'qryFastPayCode', param, 'FastPayListPart');
	$.endPageLoading();
}

function fastPayFee(obj)
{
	var strSn = $("#AUTH_SERIAL_NUMBER").val();
	if(strSn == '' && strSn.length == 0)
	{
		alert('请先查询服务号码');
		return;
	}
	/*var content = "确定充值，是否继续？";
	if(!window.confirm(content))
	{
		return;
   	}*/
	
	var orderId = $(obj).attr("orderId");
	if(orderId == '')
	{
		alert('充值编号为空，请检查配置');
		return;
	}
	
	var strCommid = orderId;
	var strCommName = $(obj).attr("orderName");
	var strUserid = $("#SEL_USER_ID").val();
	
	$.ajax.submit('', 'flowProdQry', "&SEL_COMM_ID="+strCommid+'&QR_USER_ID=' + strUserid, '', function(data) {
			var size = data.length;
			if(size == 0)
			{
				alert('商品编码：' + orderId + '未配置，请检查配置');
				return;
			}
			
			var strAMOUNT = data.get(0).get("INIT_VALUE");
			var strCOMM_ID = data.get(0).get("COMM_ID");
			var strCOMM_NAME = data.get(0).get("COMM_NAME");
			var strCOMM_TYPE = data.get(0).get("COMM_TYPE");
			var strTRANS_FEE = data.get(0).get("FEE");
			var strSD = data.get(0).get("START_DATE");
			var strED = data.get(0).get("END_DATE");
			if('1' == strCOMM_TYPE)
			{
				$("#submit_TRANS_NEEDED").checked = true;
			}
			else
			{
				$("#submit_TRANS_NEEDED").checked = false;
			}
			$("#AMOUNT").val(strAMOUNT);
			$("#COMM_ID").val(strCOMM_ID);
			$("#COMM_NAME").val(strCOMM_NAME);
			$("#TRANS_FEE").val(strTRANS_FEE);
			$("#EFFECTIVE_DATE").val(strSD);
			$("#EXPIRE_DATE").val(strED);
			var content ="订购商品：" + strCommName + "（" + strCommid + "）"  + "，生效时间：" + strSD + "，失效时间：" + strED + "，是否继续？";
			
			if(!window.confirm(content))
			{
				return;
		   	}
			
			var strTRANS_NEEDED = '0';
			var TRANS_NEEDED = $('#submit_TRANS_NEEDED').attr('checked');
			if(TRANS_NEEDED)
			{
			   strTRANS_NEEDED = '1';
		   	}
			var param = 'FP_SERIAL_NUMBER=' + $("#AUTH_SERIAL_NUMBER").val();
			//param += '&AMOUNT=' + $("#AMOUNT").val();
			//param += '&TRANS_FEE=' + $("#TRANS_FEE").val();
			param += '&FP_COMM_ID=' + orderId;
			//param += '&EFFECTIVE_DATE=' + $("#EFFECTIVE_DATE").val();
			//param += '&EXPIRE_DATE=' + $("#EXPIRE_DATE").val();
			param += '&FP_TRANS_NEEDED=' + strTRANS_NEEDED;
			//param += '&REMARK=' + $('#REMARK').val();
			param += '&Fast_Pay_Fee=1';
			
			$.cssubmit.addParam(param);
			$.cssubmit.submitTrade(param);
			return true;
			
		}, function(error_code,error_info) {
			//$.endPageLoading();
			alert(error_info);
			return;
		});
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
			var strAMOUNT = boxList[i].getAttribute("INIT_VALUE");
			$("#AMOUNT").val(strAMOUNT);
			var strTRANS_FEE = boxList[i].getAttribute("PRICE");
			$("#TRANS_FEE").val(strTRANS_FEE);
			var strCOMM_ID = boxList[i].getAttribute("COMM_NO");
			$("#COMM_ID").val(strCOMM_ID);
			var strCOMM_NAME = boxList[i].getAttribute("COMM_NAME");
			$("#COMM_NAME").val(strCOMM_NAME);
			var strEFFECTIVE_DATE = boxList[i].getAttribute("START_DATE");
			$("#EFFECTIVE_DATE").val(strEFFECTIVE_DATE);
			var strEXPIRE_DATE = boxList[i].getAttribute("END_DATE");
			$("#EXPIRE_DATE").val(strEXPIRE_DATE);
			var strCOMM_TYPE = boxList[i].getAttribute("COMM_TYPE");
			if('1' == strCOMM_TYPE)
			{
				$("#submit_TRANS_NEEDED").checked = true;
				$("#submit_TRANS_NEEDED").attr("disabled",true);
			}
			else
			{
				$("#submit_TRANS_NEEDED").checked = false;
				$("#submit_TRANS_NEEDED").attr("disabled",false);
			}
			bIsSelect = true;
		}
	}
	if(bIsSelect == false)
	{
		$("#AMOUNT").val('');
		$("#TRANS_FEE").val('');
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
	}*/
	
	if(!$.validate.verifyBox(form,'custs','请选中要操作的数据!'))
	{
		return false;
	}
	
	if (!$.validate.verifyAll())
	{
		return false;
	}
	
	var strCommid = $("#COMM_ID").val();
	var strCommName = $("#COMM_NAME").val();
	var strUserid = $("#SEL_USER_ID").val();
	
	$.ajax.submit('', 'flowProdQry', "&SEL_COMM_ID="+strCommid+'&QR_USER_ID=' + strUserid, '', function(data) {
			var strSD = data.get(0).get("START_DATE");
			var strED = data.get(0).get("END_DATE");
			var content ="订购商品：" + strCommName + "（" + strCommid + "）"  + "，生效时间：" + strSD + "，失效时间：" + strED + "，是否继续？";
			$("#EFFECTIVE_DATE").val(strSD);
			$("#EXPIRE_DATE").val(strED);
			if(!window.confirm(content))
			{
				return;
		   	}
			var strTRANS_NEEDED = '0';
			var TRANS_NEEDED = $('#submit_TRANS_NEEDED').attr('checked');
			if(TRANS_NEEDED)
			{
			   strTRANS_NEEDED = '1';
		   	}
			var param = 'SERIAL_NUMBER=' + $("#AUTH_SERIAL_NUMBER").val();
			param += '&TRANS_NEEDED=' + strTRANS_NEEDED;
			param += '&Fast_Pay_Fee=0';
			
			$.cssubmit.addParam(param);
			//return true;
			$.cssubmit.submitTrade(param);
		}, function(error_code,error_info) {
			//$.endPageLoading();
			alert(error_info);
			return;
		});
}

