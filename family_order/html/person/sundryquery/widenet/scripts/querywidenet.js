

//查询
function queryTransFee()
{
	//查询条件校验
	if(!$.validate.verifyAll("QueryWidenetPart")) {
		return false;
	}
	 //校验起始日期范围
	if(!checkDateRange()){
		return false;
	}
	
	 //【起始、终止】日期时间段不能跨月的校验
	if(!isSameMonth()){
		return false;
	}
	 var acceptMonth=$("#START_DATE").val().substr(5,2);
	$.beginPageLoading("数据查询中..");

	$.ajax.submit('QueryWidenetPart', 'queryTransFee', null, 'QueryListPart', function(data){
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
function checkDateRange(){
 var startDate = $("#START_DATE").val(); 
 var endDate = $("#END_DATE").val(); 
 var startArray = startDate.split("-");
	var endArray = endDate.split("-");
	var dateStart = new Date(startArray[0],startArray[1]-1,startArray[2]);
	var endStart = new Date(endArray[0],endArray[1]-1,endArray[2]);
	var day = (endStart.getTime() - dateStart.getTime()) / (1000*60*60*24) ;
	if(day > 31){
		alert( "【起始、终止】日期时间段不能超过"+31+"天~");
		return false;
	}
	
	return true;
}

/**
 * 【起始、终止】日期时间段不能跨月的校验
 */
function isSameMonth(){
   var startDate = $("#START_DATE").val(); 
   var endDate = $("#END_DATE").val(); 
	if(startDate == '' || endDate == ''){//为空不判断
		return true;
	}
	if(startDate.substr(0,7)!=endDate.substr(0,7)){
		alert('【起始、终止】日期时间段不能跨月');
		return false;
	}
	return true;
}
