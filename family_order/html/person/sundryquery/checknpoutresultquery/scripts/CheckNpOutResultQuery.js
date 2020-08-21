/**
 * 
 */
function getQuery(obj){
	//查询条件校验
	if(!$.validate.verifyAll("QueryCondPart")) {//先校验已配置的校验属性
		return false;
	}
	var start_date = $("#START_DATE").val();
	var end_date = $("#FINISH_DATE").val();
	if(start_date == "" || end_date == ""){
		alert('开始时间和结束时间,不能为空！');
		return false;
	}
	if(start_date != "" && end_date != ""){
		
		if(!checkDateRange(start_date,end_date,31)){
			return false;
		}
		
	}
	$.ajax.submit('QueryCondPart', 'queryCheckNpOut', null, 'QueryListPart', function(data){
		if(data.get('ALERT_INFO') != '')
		{
			alert(data.get('ALERT_INFO'));
		}
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}

function inputCtrlForStaff(obj){
	staffId = $(obj).val().toUpperCase();
	if(staffId.length > 8){
		obj.value = staffId.substring(0,8);
	} else {
		obj.value = staffId;
	}
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