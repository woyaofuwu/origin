$(document).ready(function(){
	$("#QUERY_BTN").unbind("click");
	$("#QUERY_BTN").bind("click",function(){
		queryInfos();
	});
});


function queryInfos(){
	
	var _serial_number = $("#SERIAL_NUMBER").val();
	var npout_query_type = $("#NPOUT_QUERY_TYPE").val();
//    _serial_number = $.trim(_serial_number);
//	if(_serial_number == "" || _serial_number == null){
//		alert('手机号码,不能为空！');
//		return false;
//	}
	
	if(npout_query_type == ""){
		alert('请选择查询类型！');
		return false;
	}
	
	var start_date = $("#START_DATE").val();
	var end_date = $("#END_DATE").val();
	
	if(start_date == "" || end_date == ""){
		alert('开始时间和结束时间,不能为空！');
		return false;
	}
	
	if(start_date != "" && end_date != ""){
		
		if(!checkDateRange(start_date,end_date,31)){
			return false;
		}
		
	}
	
	$.beginPageLoading("正在查询数据...");
	var param = "";
	$.ajax.submit('QueryCondPart,userNpINfonav', 'getInfos', param, 'QueryListPart', function(data){
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}


/**
 * 校验日期起始范围
 */
function checkDateRange(startDate, endDate, range){

	if(startDate == "" || endDate == "") {
		return true;
	}
	var startArray = startDate.split("-");
	var endArray = endDate.split("-");
	var dateStart = new Date(startArray[0],startArray[1]-1,startArray[2]);
	var endStart = new Date(endArray[0],endArray[1]-1,endArray[2]);
	var day = (endStart.getTime() - dateStart.getTime()) / (1000*60*60*24) + 1;
	if(day > range){
		alert("【起始、终止】日期时间段不能超过"+range+"天");
		return false;
	}
	
	return true;
}