/*修改结束日期，开始日期提前一个月*/
function chgEndDateSynStartDate(endObj, startId){
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
	document.getElementById(startId).value = sValue;
}

function initQueryUserPostRepair(){

   if($("#cond_PROCESS_TAG").val()=="0")
	{
	  $("#dealDiv").style.display="block";	
	}else
	{
		$("#dealDiv").style.display="none";	
	}
}

function resetCondition(){
    
     $("#cond_PROCESS_TAG").val() ="0";
     resetConditionDate("cond_START_TIME","cond_END_TIME",10);
}
/*设置查询条件的时间 ，结束时间为当前时间，开始时间为当前时间的前days天数 格式为yyyy-mm-dd*/
function resetConditionDate(startDateId,endDateId,days){

    var sDate=new Date();
    var nyear=sDate.getYear();
    var nmonth=sDate.getMonth();
    var ndate=sDate.getDate();
    

    var month = (nmonth + 1) + "";
	month = month.length==1 ? "0"+month : month;
	
	var day=ndate+"";
	day = day.length==1 ? "0"+day : day;
    var eValue=nyear+"-"+month+"-"+day;//结束时间为当前时间
    
     var startDate=new Date(sDate - (1000*60*60*24)*days); //开始时间=结束时间之前的第days天
     month = (startDate.getMonth() + 1) + "";
     month = month.length==1 ? "0"+month : month;  
     
     day=startDate.getDate()+"";
     day = day.length==1 ? "0"+day : day;
     var sValue = startDate.getYear() + "-" + month + "-" + day;//开始时间
	
     document.getElementById(startDateId).value = sValue;
     document.getElementById(endDateId).value = eValue;
}


function submitModifyRepairPost() {
	if(!verifyAll('DealPart'))
   	{
	   return false;
   	}
	debugger;
	var data = $.table.get("postTable").getCheckedRowDatas();//获取选择中的数据
	if(data == null || data.length == 0) {
		alert('请选择待处理信息后，再继续办理业务!');
		return false;
	}
	var param = "&POSTREPAIR_TABLE=" + data;
	param += "&sub_PROCESS_REMARK=" + $("#PROCESS_REMARK").val();
	$.beginPageLoading("信息处理中..");
     $.ajax.submit('DealPart', 'submitUserRepairPost', param, null, function(data){
		$.endPageLoading();
		if(data.get('ALERT_INFO') != null && data.get('ALERT_INFO') != '') {
			$.showWarnMessage(data.get('ALERT_INFO'));
		}
	},
	
	function(error_code,error_info,detail){
		$.endPageLoading();
		MessageBox.error("错误提示", error_info, null, null, null, detail);
    });
}

/***
 * 
 */
 function queryUserPostRepair(obj){
 	//查询条件校验
	if(!$.validate.verifyAll("QueryCondPart")) {//先校验已配置的校验属性
		return false;
	}

	$.ajax.submit('QueryCondPart', 'queryUserPostRepair', null, 'ResultPart', function(data){
		$.endPageLoading();
		if(data.get('ALERT_INFO') != null && data.get('ALERT_INFO') != '') {
			$.showWarnMessage(data.get('ALERT_INFO'));
		}
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
 }

 function resetWeb(){
		$.ajax.submit('','init','','QueryCondPart',function(data){
			$.endPageLoading();
		},
		function(error_code,error_info){
			$.endPageLoading();
			alert(error_info);
		});
	}