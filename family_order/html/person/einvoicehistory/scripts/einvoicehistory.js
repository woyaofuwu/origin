//认证回调方法，加载重打印业务
function queryEInvoiceTrade(data){
	 var param = "&TRADE_TYPE_CODE="+$("#TRADE_TYPE_CODE").val()+"&SERIAL_NUMBER="+$("#AUTH_SERIAL_NUMBER").val();
	 $.beginPageLoading("正在查询....");
	 $.ajax.submit("queryPart", "queryEInvoiceTrade", param, "RefreshTable", 
	 	function(data){
		    $.endPageLoading();
//			if(data.length==0){
//				MessageBox.alert("信息提示", "该用户不存在电子发票信息!");
//			}
		},
		function(error_code,error_info){
			$.endPageLoading();
			alert(error_info);
			$.auth.reflushPage();
    });
}

//点击选择一条电子发票数据
function getInvoiceData(obj)
{
	var tradeId = $(obj).val();
	var invoiceTrade = $(obj).parent().siblings(); 
	var printId = $(invoiceTrade[6]).text();
	var serialNumber = $(invoiceTrade[0]).text();
	var tradeId = $(invoiceTrade[2]).text();
	var printFlag = $(invoiceTrade[3]).text();
//	$("#EINVOICE_DETAIL").attr('disabled',false);
//	$("#TO_CREDIT").attr('disabled',false);
//	$("#EINVOICE_DETAIL").attr("href","amnote.postbill.QueryElecInvoice?SERIAL_NUMBER="+serialNumber+"&REQUEST_ID="+printId);
	$("#seleced_print_id").val(printId);
	$("#seleced_serial_number").val(serialNumber);
	$("#seleced_trade_id").val(tradeId);
	$("#seleced_print_flag").val(printFlag);
	
}


function toDetail(){
	var printId = $("#seleced_print_id").val();
	var serialNumber = $("#seleced_serial_number").val();
	if($("#seleced_trade_id").val()==""){
		alert("请选择一条电子发票数据");
		return false;
	}
//	$.redirect.toPage('subsys_cfg.acctmanm','amnote.postbill.QueryElecInvoice','',"SERIAL_NUMBER="+serialNumber+"&REQUEST_ID="+printId)
    openNav('电子发票统一查询','elecnote.QueryElecInvoice', 'myInitialize', '&REQUEST_ID='+printId+'&SERIAL_NUMBER='+serialNumber, subsys_cfg.acctmanm);
}

function toCredit(){
	var printId = $("#seleced_print_id").val();
	var serialNumber = $("#seleced_serial_number").val();
	var tradeId = $("#seleced_trade_id").val();
	if($("#seleced_trade_id").val()==""){
		alert("请选择一条电子发票数据");
		return false;
	}
	if($("#seleced_print_flag").val()=="冲红发票"){
		alert("请选择正常发票进行冲红");
		return false;
	}
	if(confirm('确定要冲红该电子发票吗？')){
		
	}else{
		return false;
	}
	var param = "&PRINT_ID="+printId+"&SERIAL_NUMBER="+serialNumber+"&TRADE_ID="+tradeId;
	$.beginPageLoading("正在冲红....");
	$.ajax.submit("","toCreditNote",param,"",function(data){
        $.endPageLoading();
        queryEInvoiceTrade(data);
        MessageBox.alert("信息提示", "发票已冲红");
        $("#seleced_trade_id").val("");
	},function(code,info,detail){
		$.endPageLoading();
		MessageBox.error("提示","操作失败",null,null,info,detail);
	});
}



