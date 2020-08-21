//$Id: querydepositinvoiceno.js,v 1.1 2009/10/27 13:36:49 wangmo Exp $

/**
 * REQ201512020036 用户押金发票号码查询界面优化
 * chenxy3 20151221
 * */
$(document).ready(function(){ 
	$("#cond_USER_SN_TYPE").val("0");  
});

function inita() {
	//tableedit = new TableEdit("DeptTable"); 
	querymodchg();
}	
	
/*选择不同的查询方法*/
function querymodchg(){
	var queryType=$("#cond_QUERY_TYPE").val();
	if(queryType==0)
	{
		document.getElementById("sn").style.display = "";
		document.getElementById("invoiceNo").style.display = "none";
		$("#userSnType").css("display","");//REQ201512020036 用户押金发票号码查询界面优化
		$("#cond_INVOICE_NO").val("");		
	}
	else if(queryType==1)
	{
		document.getElementById("sn").style.display = "none";
		document.getElementById("invoiceNo").style.display = "";
		$("#userSnType").css("display","none");//REQ201512020036 用户押金发票号码查询界面优化
		$("#cond_SERIAL_NUMBER").val("");		
		$("#userSnType").val("");
	}
}

function queryIntegrateCustInvoice(obj){
	//查询条件校验
	if(!$.validate.verifyAll("QueryCondPart")) {//先校验已配置的校验属性
		return false;
	}
	var queryType=$("#cond_QUERY_TYPE").val();
	var fieldShoudnotbenull;
	//查询条件校验
	if(queryType==0){
		fieldShoudnotbenull = document.getElementById("cond_SERIAL_NUMBER").value;
		if (fieldShoudnotbenull == "") {
			alert("服务号码不能为空!");
			return false;
		}
	}else if(queryType==1)
	{
		fieldShoudnotbenull = document.getElementById("cond_INVOICE_NO").value;
		if (fieldShoudnotbenull == "") {
			alert("发票号码不能为空!");
			return false;
		}
	}
	$.beginPageLoading("正在查询...");
	$.ajax.submit('QueryCondPart', 'queryIntegrateCustInvoice', null, 'QueryListPart,custInfoPart', function(data){
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
 }