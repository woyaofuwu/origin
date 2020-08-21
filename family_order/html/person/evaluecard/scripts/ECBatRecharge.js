$(document).ready(function(){
});

//批量充值页面
function refreshPartAtferAuth(data)
{
	$.ajax.submit('AuthPart', 'loadChildInfo', "&USER_INFO="+data.get("USER_INFO").toString()+"&CUST_INFO="+data.get("CUST_INFO").toString(), 'freshPart,QueryCondPart,hiddenCondPart', function(data){
		 disabledArea('QueryBatchRechargeReqPart', false);
		 disabledArea('BatchRechargeReqALLPart', false);
		 $("#QueryBatchRechargeReqPart").removeClass("e_dis")
		 $("#BatchRechargeReqALLPart").removeClass("e_dis");

		 var operRadio = document.getElementById("cond_OPERTYPE1");
		 operRadio.checked=true;
		 initDisplayDiv();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}

function queryBatchReqInfo(){
	var startData = $("#cond_START_DATE").val();
	var endData = $("#cond_END_DATE").val();
 	if (!$.validate.verifyAll("QueryCondPart")){
		return false; 
	}
 	var day=31;
	//校验起始日期范围
	if(dateDiff(startData, endData)>day){
		alert("开始时间和结束时间跨度不能超过"+day+"天");
		return false;
	}
	
	$.beginPageLoading();
//	$.message.showSucTradeMessage(data);
	$.ajax.submit('QueryCondPart,hiddenCondPart','queryBatchReqInfo', null,'refreshtable',function(data)
	{ 	
		$.endPageLoading();	  		
	},function(errorcode,errorinfo){
		alert(errorinfo);
		$.endPageLoading();
	}); 
}
	
function dateDiff(start,end){
	var day = 0;
	var startDt = new Date(Date.parse(start.replace(/-/g,   "/")));
	var endDt = new Date(Date.parse(end.replace(/-/g,   "/")));
	day = (endDt.getTime()-startDt.getTime()) / (1000*60*60*24) ;
	return day;
}

function toQueryTrans() {
	var queryRadio = document.getElementById("cond_OPERTYPE0");
	queryRadio.checked=true;
	initDisplayDiv();
}

function initDisplayDiv() {
	var checkRadio = $("input[name='cond_OPERTYPE'][type='radio']:checked");
	var checkRadioValue = checkRadio.val();
	if(checkRadioValue=='0'){
		$('#BatchRechargeReqALLPart').css('display','none');
		$('#QueryBatchRechargeReqPart').css('display','');
	}else{
		$('#BatchRechargeReqALLPart').css('display','');
		$('#QueryBatchRechargeReqPart').css('display','none');
	}
}

function downLoadFile(rowIndex) {
    var selTable = $.table.get("QueryListTable");
    var rowValue = selTable.getRowData(null,rowIndex);
    var fileid = rowValue.get("FILE_ID");
    var filename = rowValue.get("FILE_NAME");
    window.location.href="attach?action=download&realName="+filename+"&fileId="+fileid;
}

function importBeforeAction(){
	//查询条件校验
	if(!$.validate.verifyAll("Info"))
	{
		return false;
	}
	if(!$.validate.verifyAll("hiddenCondPart"))
	{
		return false;
	}
	
	return true;
}

function importAfterAction(){
	$("#cond_TRADE_ID").val($("#cond_INIT_TRADE_ID").val());
	toQueryTrans();
}