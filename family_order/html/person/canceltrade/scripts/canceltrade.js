function queryCancelTrade()
{
	if(!$.validate.verifyAll("QueryCondPart")){
		return false; 
	}
	var startDate = $("#cond_START_DATE").val();
	var endDate = $("#cond_END_DATE").val();
	if (startDate>endDate){
		alert("开始时间不能大于结束时间！请重新输入！");
		return false;
	}
	$.beginPageLoading("正在查询数据...");
	//用户可返销订单查询
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
	function(error_code,error_info,derror)
	{
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});
}


/**
 * 订单详细信息弹出页面
 * @param 
 * @return
 */
function popupTradeInfoPage(obj)
{
	var rowIndex = obj.rowIndex;//当前操作行
	var data = $.table.get("CancelTradeTable").getRowData(null,rowIndex);
	var eparchyCode = data.get("col_EPARCHY_CODE");//用户归属地市，用该编码作为服务的路由
	var tradeId = data.get("col_TRADE_ID");
	var tradeTypeCode = data.get("col_TRADE_TYPE_CODE");
	var param ="&TRADE_ID="+tradeId+"&EPARCHY_CODE="+eparchyCode+"&TRADE_TYPE_CODE="+tradeTypeCode;	
	popupPage('canceltrade.TradeInfo','initQueryTrade',param,'工单信息','800','600');
}

/**
 * 提交前的校验方法
 * @return
 */
var checkedTradeIdList = "";//票据检查通过流水列表 
var inCheckTradeIdList = "";//票据待检查流水列表 
	
function commitCheck()
{
	//金库验证
	var returnFlag = false;
	var tag = $("#4APARIS_TAG").val();
	if(tag == "0"){
		beginPageLoading("正进行金库认证...");
		$.treasury.auth("SALESERV_4A_CancelTrade",function(ret){
			endPageLoading();
			if(true == ret){
				alert("认证成功");
				$("#4APARIS_TAG").val("1");
			}else{
				alert("认证失败");
				$("#4APARIS_TAG").val("0");
			}
		});
		return returnFlag;
	}else{
		returnFlag = commitData();
		if(returnFlag && checkedTradeIdList != inCheckTradeIdList){
			$.beginPageLoading("正在检查票据信息...");
			$.ajax.submit(null, 'ticketCancelCheck', $.cssubmit.dynamicParams,null,function(data){
				$.endPageLoading();
				if(data.get(0).get("TICKET_INFO").length > 0){
					if(window.confirm("请回收之前打印的票据:"+"\r\n" + data.get(0).get("TICKET_INFO")+"是否继续进行返销操作?")){
						checkedTradeIdList = inCheckTradeIdList;
						$("#CSSUBMIT_BUTTON").click();
					}
				}else{
					window.alert("没有票据需要进行回收,请继续进行返销操作!");
					checkedTradeIdList = inCheckTradeIdList;
					$("#CSSUBMIT_BUTTON").click();
				}
			},
			function(error_code,error_info,derror){
				$.endPageLoading();
				showDetailErrorInfo(error_code,error_info,derror);
			});
			return false;
		}

		return returnFlag;
	}
}

function commitData()
{
	var obj=$("input[name=CANCEL_TRADE_ID]:checked");
	if(obj == null || obj.length == 0) {
			alert("请选择需要执行返销的工单！");
			return false;
	}
	var tradeIdList = "";
	obj.each(function(){
		tradeIdList += $(this).attr("tradeId")+",";
	});
	
	var cancelTableObj = $.table.get("CancelTradeTable");//获取资源表格对象
    var data = cancelTableObj.getCheckedRowDatas(); //获得当前选择数据
	if(data == null || data.length == 0) {
		alert("请选择需要执行返销的工单！");
		return false;
	}else {
		for(var i = 0,count=data.getCount(); i < count; i++) {
	  	    var tempData = data.get(i);
	  	    if(tempData){
				var cancelTag = tempData.get("col_CANCEL_TAG");	
				var tradeId = tempData.get("col_TRADE_ID");	
				if(cancelTag=="-1"){
					var msg = "该订单[" + tradeId + "]不允许返销，请重新选择需要执行返销的工单！";
					alert(msg);
					return false;
				}
			}
	  	}
	}
	
	inCheckTradeIdList = tradeIdList;
	var remarks = $("#REMARKS").val();
	var invoiceNo = $("#INVOICE_NO").val();
	var param = "TRADEID_LIST=" + tradeIdList + "&REMARKS=" + remarks + "&INVOICE_NO="+invoiceNo;
	$.cssubmit.addParam(param);
	return true;
}

function CreateControl(DivID)	
{ 
   document.getElementById(DivID).innerHTML  = "<OBJECT id='JunAct' classid='CLSID:BAB91EC4-6964-452C-8500-8BA8F1050903' width='0' height='0' style='display:none;' ></OBJECT>";  
}