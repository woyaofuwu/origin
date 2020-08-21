
function queryValueCardInfo(){
	
	if (!$.validate.verifyAll()){
		return false;
	}
	$.beginPageLoading("正在查询数据...");
	$.ajax.submit('QueryCondPart', 'queryValueCardInfo', null, 'QueryDataPart', function(data){
		$.endPageLoading();
		if(data.get("DATA_COUNT")=="0"){
			alert("查询无数据！");
		}
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
	});
}

function exportBeforeAction(obj) {
	var data = $.table.get("QueryListTable").getTableData("DEVICE_NO_S", true);	
	if(data.length == 0) {
		alert("请先查询出数据再导出!");
		return false;
	}
	return true;
}

function changeTradeType(){
//	var tradeType = $("#cond_TRADE_TYPE_CODE").val();
//	if('420'== tradeType){
//		$("#QueryListTable2").css("display","");
//		$("#QueryListTable1").css("display","none");
//	}else{
//		$("#QueryListTable2").css("display","none");
//		$("#QueryListTable1").css("display","");
//	}
	var tradeType = $("#cond_TRADE_TYPE_CODE").val();
	if('420'== tradeType){
		$("#TH_DEVICE_NO_S").html("原卡号");
		$("#TH_DEVICE_NO_E").html("新卡号");
	}else{
		$("#TH_DEVICE_NO_S").html("起始卡号");
		$("#TH_DEVICE_NO_E").html("终止卡号");
	}
}

function textToUpperCase(obj){
	var v = obj.value;
	obj.value=v.toUpperCase();
}

/**
 * 每输入一个开始号码，自动填充终止号码
 */
function autoCopyNum(startId, endId){
	var s = $('#'+startId);
	var e = $('#'+endId);
	e.val(s.val());
}
function onblurCopyNum(startId, endId){
	var s = $('#'+startId);
	var e = $('#'+endId);
	if(s.val()!="" && e.val()==""){
		e.val(s.val());
	}
	if(e.val()!="" && s.val()==""){
		s.val(e.val());
	}
}

function checkStartDate(){
	var a = $("#cond_START_DATE").val();
	var b = $("#cond_END_DATE").val();
	if(b=="")return;
	var c = a.slice(5,7);
	var d = b.slice(5,7);
	var time = b.substring(0,8) + '01';
	if(a > b){
		alert('请输入正确的销售起始时间！');
		$("#cond_START_DATE").val(time);
	}
	if(c != d){
		alert('此查询只支持一个月内查询！');
		$("#cond_START_DATE").val(time);
	}
}

function getEndDate(){
	var time = $("#cond_END_DATE").val();
	var time_start = time.substring(0,8) + '01';
	$("#cond_START_DATE").val(time_start);	
}
