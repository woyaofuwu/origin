$(document).ready(function(){
	$("#cond_PAY_MODE").val("1"); 
});
function getInfoBySimCardNo(){
	if(!$.validate.verifyAll("AuthPart"))
	{
		return false;
	}
	$.beginPageLoading("正在查询数据");
	$.ajax.submit('AuthPart','getInfoBySimCardNo','','busiInfoPart',
			function(data){ 
				disabledBtn($("#CSSUBMIT_BUTTON"),false);//submitBtn
				$("#cond_PAY_MODE").val("1"); 
			  	$.endPageLoading();
			},function(error_code,error_info){
				$.MessageBox.error(error_code,error_info);
				$.endPageLoading();
			});
}

function beforeCommitCheck(){
	if(!$.validate.verifyAll("busiInfoPart"))
	{
		return false;
	}
	$.cssubmit.bindCallBackEvent(specompensateCardCallBack);
	return true;
}
function specompensateCardCallBack(data){
	
	/*$.printMgr.setPrintParam("FEE_TYPE","SIM卡");
	$.printMgr.setPrintParam("BRAND_MODEL",$("#cond_SIM_CARD_TYPE").val());
	$.printMgr.setPrintParam("UNIT","张");
	$.printMgr.setPrintParam("QUANTITY","1");
	$.printMgr.setPrintParam("PRICE",$("#cond_FEE").val());
	$.printMgr.setPrintParam("FEE",$("#cond_FEE").val());*/
	
	$.printMgr.bindPrintEvent(printSpecompensateCard);
	
	var title = "业务受理成功";
	var content = "点【确定】继续业务受理。";
	content = "客户订单标识：" + data.get(0).get("ORDER_ID") + "<br/>点【确定】继续业务受理。";
	$.cssubmit.showMessage("success", title, content, true);
}
function printSpecompensateCard(data){
	
	var param = "&TRADE_ID="+data.get("TRADE_ID");
	param += "&ORDER_ID="+data.get("ORDER_ID");
	param += "&FEE_AMOUNT="+(Number($("#cond_FEE").val())*100);
	param += "&FEE_NAME="+"SIM卡";
	param += "&BRAND_MODEL="+$("#cond_SIM_CARD_TYPE").val();
	param += "&FEE_TYPE=SIM卡";
	param += "&PRICE="+$("#cond_FEE").val();
	param += "&FEE="+$("#cond_FEE").val();
	param += "&SIM_CARD_NO="+$("#cond_SIM_CARD_NO1").val();
	param += "&SIM_CARD_TYPE="+$("#cond_SIM_CARD_TYPE").val();	
	param += "&STAFF_ID="+$("#cond_STAFF_ID").val();
	param += "&STOCK_ID="+$("#cond_STOCK_ID").val();
	

	$.beginPageLoading("加载打印数据。。。");
	$.ajax.submit("", "printSpecompensateCard", param, null, 
		function(printDataset){
			$.endPageLoading();
			//设置打印数据
			$.printMgr.setPrintData(printDataset);
			//启动打印
			$.printMgr.printReceipt();
		},
		function(code, info, detail){
			$.endPageLoading();
			MessageBox.error("错误提示","加载打印数据错误！", null, null, info, detail);
		},function(){
			$.endPageLoading();
			MessageBox.alert("告警提示","加载打印数据超时！");
	});		
}





function disabledBtn(obj,flag){
	if(!obj.length){
		return ;
	}

	if(flag == true){
		obj.attr("disabled", true);
		obj.addClass("e_dis");
	}else{
		obj.attr("disabled", false);
		obj.removeClass("e_dis");
	}
}


















