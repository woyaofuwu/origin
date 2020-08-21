function queryProsecution() {
	if(!checkProcution()) {
		return false;
	}
	$.beginPageLoading("数据查询中..");
	$.ajax.submit('QueryCondPart', 'queryProsecution', null, 'QueryListPart',function(data){
		$.endPageLoading();
		if(data.length>0) {
			$.showWarnMessage(data.get('ALERT_INFO'));
		}
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}

 function checkProcution(){
 	var serialNum = $('#cond_SERIAL_NUMBER').val();
 	var procuteNum = $('#cond_PROSECUTEENUM').val();
 	var startDate = $('#cond_START_DATE').val();
 	var endDate = $('#cond_END_DATE').val();
 	if((serialNum == null||serialNum=="") && (procuteNum == null||procuteNum=="") && (startDate == null||startDate==""))
 	{
	 	alert("请选择正确的查询方式!");
	 	return false;
 	}
 	
 	if (!verifyAll('QueryCondPart')){
		return false; //先校验已配置的校验属性
	}
	
	//校验起始日期范围
	if(!checkDateRanges(startDate, endDate, 31)){
		return false;
	}
	return true;
 }
 
 /**
 * 校验日期起始范围
 */
function checkDateRanges(startDate, endDate, range){
	var startArray = startDate.split("-");
	var endArray = endDate.split("-");
	var dateStart = new Date(startArray[0],startArray[1],startArray[2]);
	var endStart = new Date(endArray[0],endArray[1],endArray[2]);
	var day = (endStart.getTime() - dateStart.getTime()) / (1000*60*60*24) ;
	if(day > range){
		MessageBox.alert("状态", "【起始、终止】日期时间段不能超过"+range+"天~");
		return false;
	}
	return true;
}

function exportBeforeAction(obj) {
	var data = $.table.get("QueryInfoTable").getTableData("PARA_CODE1", true);
	//alert(data);
	if(data.length == 0) {
		alert("请先查询出数据再导出!");
		return false;
	}
	return true;
}