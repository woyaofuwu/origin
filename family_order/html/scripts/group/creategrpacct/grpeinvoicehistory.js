//根据集团客户Id查询电子发票打印历史信息
function queryByGroupId(){
	if(!$.validate.verifyAll("queryPart")) return false;
	var condStartDate = $("#cond_START_DATE").val();
	var condEndDate = $("#cond_END_DATE").val();
	if(condStartDate > condEndDate){
		alert("开始时间不能大于结束时间");
		$("#cond_START_DATE").focus();
		return false;
	}
	beginPageLoading("正在查询数据..");
	$.ajax.submit("queryPart", "queryByGroupId", "", "RefreshTable", function(data) {
				endPageLoading();
				if(data.length==0){
					MessageBox.alert("信息提示", "未找到对应的电子发票信息!");
				}
			}, function(code, info) {
				endPageLoading();
				MessageBox.error("查询数据时发生错误！", info);
			});
}
//点击选择一条电子发票数据
function getInvoiceData(obj)
{
	var tradeId = $(obj).val();
	var invoiceTrade = $(obj).parent().siblings(); 	
	var acctId = $(invoiceTrade[0]).text();
	var custId = $(invoiceTrade[1]).text();
	var tradeId = $(invoiceTrade[3]).text();
	var printFlag = $(invoiceTrade[4]).text();	
	var printId = $(invoiceTrade[7]).text();
	var userId=$(invoiceTrade[8]).text();
	$("#EINVOICE_DETAIL").attr('disabled',false);
	$("#TO_CREDIT").attr('disabled',false);
	$("#seleced_print_id").val(printId);
	$("#seleced_acct_id").val(acctId);
	$("#seleced_trade_id").val(tradeId);
	$("#seleced_print_flag").val(printFlag);
	$("#seleced_cust_id").val(custId);
	$("#seleced_user_id").val(userId);
}

function toCredit(){
	var printId = $("#seleced_print_id").val();
	var custId = $("#seleced_cust_id").val();
	var tradeId = $("#seleced_trade_id").val();
	var acctId = $("#seleced_acct_id").val();
	var userId=$("#seleced_user_id").val();
	if($("#seleced_trade_id").val()==""){
		alert("请选择一条电子发票数据");
		return false;
	}
	if($("#seleced_print_flag").val()=="冲红发票"){
		alert("请选择正常发票进行冲红");
		return false;
	}
	MessageBox.confirm("提示信息",'确定要将该发票冲红吗？',
		    function(btn){
				if(btn=='cancel'){
					return;
				}else{
					var param ="&PRINT_ID="+printId+"&CUST_ID="+custId+"&USER_ID="+userId+"&ACCT_ID="+acctId+"&TRADE_ID="+tradeId;
					$.beginPageLoading("正在冲红....");
					$.ajax.submit("","toCreditNote",param,"",function(data){
						MessageBox.alert("信息提示", "发票已冲红");
						queryByGroupId(data);       
				        $("#seleced_trade_id").val("");
				        $.endPageLoading();
					},function(code,info,detail){
						$.endPageLoading();
						MessageBox.alert("信息提示", info);
					});
				}		
			});	
	
}
function toTicketDetil(){
	var printId = $("#seleced_print_id").val();	
	var userId=$("#seleced_user_id").val();
	openNav('电子发票统一查询','elecnote.QueryElecInvoice', 'myInitialize', '&REQUEST_ID='+printId+'&USER_ID='+userId, subsys_cfg.acctmanm);
	//$.redirect.toPage('subsys_cfg.acctmanm','elecnote.QueryElecInvoice','myInitialize',"ACCT_ID="+acctId+"&REQUEST_ID="+printId)
	//$("#EINVOICE_DETAIL").attr("href","amnote.postbill.QueryElecInvoice?ACCT_ID="+acctId+"&REQUEST_ID="+printId);
	}

