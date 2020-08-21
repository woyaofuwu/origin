
/***
 * 提交欠费拆机用户押金清单查询前校验及提交
 */
 function queryOweDestroyForegift(obj){
	//查询条件校验
	if(!$.validate.verifyAll("QueryCondPart")) {//先校验已配置的校验属性
		return false;
	}
 	var startDate = $("#cond_START_DATE").val();
 	var endDate = $("#cond_END_DATE").val();
	//校验起始日期范围
	if(!checkDateRange(startDate, endDate, 31)){
		return false;
	}
	$.ajax.submit('QueryCondPart', 'queryOweDestroyForegift', null, 'QueryListPart', function(data){
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
 
 /*修改结束日期，开始日期提前一个月*/
 function synStartDate2EndDate(endObj,startId){
		var endDate = endObj.value;
		if(endObj.value == '' || !verifyField(endObj)){//仅在格式满足的情况下才执行该操作
			endObj.select();
			return;
		}
		var dateArr = endDate.split("-");
		
		var edate = new Date(dateArr[0], parseInt(dateArr[1]-1), dateArr[2]);
		var sDate = new Date(dateArr[0], parseInt(dateArr[1]-1-1), dateArr[2]);
		//如果开始日期和终止日期的月份一样，则继续调整
		var i = 0;
		while( ((edate.getMonth()+1) - (sDate.getMonth()+1)) == 0 ){
			i++;
			sDate = new Date(dateArr[0], parseInt(dateArr[1]-1-1), dateArr[2]-i);
		}
		var month = (sDate.getMonth() + 1) + "";
		month = month.length==1 ? "0"+month : month;
		var day = sDate.getDate() + "";
		day = day.length==1 ? "0"+day : day;
		var sValue = sDate.getYear() + "-" + month + "-" + day;
		var e = document.getElementById(startId);
		e.value = sValue;
 }
 /**
  * 校验日期起始范围
  */
 function checkDateRange(startDate, endDate, range){
 	var startArray = startDate.split("-");
 	var endArray = endDate.split("-");
 	var dateStart = new Date(startArray[0],startArray[1],startArray[2]);
 	var endStart = new Date(endArray[0],endArray[1],endArray[2]);
 	var day = (endStart.getTime() - dateStart.getTime()) / (1000*60*60*24) ;
 	if(day > range){
 		MessageBox.alert("提示","【起始、终止】日期时间段不能超过"+range+"天~");
 		return false;
 	}
 	return true;
 }
 
