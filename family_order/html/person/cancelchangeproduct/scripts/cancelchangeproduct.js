$(function(){
	var inModeCode = $("#cond_SERIAL_NUMBER").attr("inModeCode");
	if(inModeCode == 1 && typeof(eval(window.top.getCustorInfo))=="function"){
		var sn = window.top.getCustorInfo();
		$("#cond_SERIAL_NUMBER").val(sn);
	}
})
function queryCancelTrade()
{
	if(!$.validate.verifyAll("QueryCondPart"))return false; 
	
	$.beginPageLoading("正在查询数据...");
	//用户优惠查询
	$.ajax.submit('QueryCondPart', 'queryCancelTrade', null, 'TradeInfoPart', function(data){
		$.endPageLoading();
		if(data && data.get("QUERY_CODE") == "N")
		{
			alert(data.get("QUERY_INFO"));
			$("#CSSUBMIT_BUTTON").attr("disabled",true);
		}else
		{
			$("#CSSUBMIT_BUTTON").attr("disabled",false);
			$("#CSSUBMIT_BUTTON").attr("className","e_button-page-ok");
		}
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}

/**
 * 订单详细信息弹出页面
 * @param obj
 * @return
 */
function popupTradeInfoPage()
{
	var rowData = $.table.get("CancelTradeTable").getRowData("col_TRADE_ID,col_EPARCHY_CODE")
	var eparchyCode = rowData.get("col_EPARCHY_CODE");//用户归属地市，用该编码作为服务的路由
	var tradeId = rowData.get("col_TRADE_ID");//
	var param ="&TRADE_ID="+tradeId+"&EPARCHY_CODE="+eparchyCode;
	popupPage('cancelchangeproduct.TradeInfo','initQueryTrade',param,'工单信息','880','400');
}


/**
 * 提交前的校验方法
 * @return
 */
function commitCheck()
{
	var cancel_trade="";
	var r=document.getElementsByName("CANCEL_TRADE_ID");
	for(var i=0;i<r.length;i++)
	{
		if(r.item(i).checked)
		{
			cancel_trade = r.item(i).value;
			break;
		}
	}
	
	if(cancel_trade == "")
	{
		alert("请先选择需要取消的业务！");
		return false;
	}
	
	var arr_cancel_trade = cancel_trade.split(";");
	var tradeId= arr_cancel_trade[0];
	var serialNumber= arr_cancel_trade[4];
	var allFee= parseFloat(arr_cancel_trade[1])+parseFloat(arr_cancel_trade[2])+parseFloat(arr_cancel_trade[3]);
	var tipflag = arr_cancel_trade[6];
	if(confirm("取消该业务共需向用户清退费用【"+allFee+"】元！"))
	{
		/*********提交数据****************/
		var remarks = $("#REMARKS").val();
		var invoiceNo = $("#INVOICE_NO").val();
		var param ="TRADE_ID="+tradeId+"&REMARKS="+remarks+"&INVOICE_NO="+invoiceNo+"&SERIAL_NUMBER="+serialNumber;
		if("Y" == tipflag)
		{
			if(confirm("您将取消流量不限量套餐，取消后，办理套餐时默认开通的共享关系和统付关系将同步取消。"))
			{
				$.cssubmit.addParam(param);
				return true;
			}
		}else{
			$.cssubmit.addParam(param);
			return true;
		}
		
	}
	return false;
}