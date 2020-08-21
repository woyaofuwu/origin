$(document).ready(function(){
	$("#QUERY_BTN").bind("click",function(){
		queryUserNpInfos();
	});

	
});

function queryUserNpInfos(){
	var _serial_number = $("#SERIAL_NUMBER").val();
	    _serial_number = $.trim(_serial_number);
//	if(_serial_number == "" || _serial_number == null){
//		alert('手机号码,不能为空！');
//		return false;
//	}
	var start_date = $("#START_DATE").val();
	var end_date = $("#END_DATE").val();
	
	if((start_date == "" || end_date == "")&& _serial_number == "" ){
		alert('【起始日期】和【终止日期】必填！');
		return false;
	}
	
	if(start_date != "" && end_date != ""){
		 if(!checkDateRange(start_date,end_date,31)){
			 return false;
		 }
	}
	
	$.beginPageLoading("正在查询数据...");
	var param = "";
	$.ajax.submit('QueryCondPart,infofonav', 'getUserNpInfos', param, 'QueryListPart', function(data){
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}




function updateDealTag(rowIndex)
{
	
	var _idbtn = rowIndex+'_del_btn';
	var _idtradeid = rowIndex+'_trade_id';
	
	var tradeId = $("#"+_idtradeid).val();
	$.beginPageLoading("正在处理数据...");
	var param = "&TRADE_ID="+tradeId;
	$.ajax.submit(null, 'updateDealTag', param, null, function(data){
		if(data){
			var tag = data.get(0).get("TAG");
			if(tag == 1){
				$("#"+_idbtn).html("已处理");
			}
		}
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
		
	if(startDate == '' || endDate == '') {
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