function queryInfos(){  
	var departId=$("#AGENT_DEPART_ID1").val(); 
	if(departId == null || departId ==""){
		alert("请先选择要查询的代理商。");
		return false;
	}
	var startTime=$("#START_TIME").val(); 
	if(startTime == null || startTime ==""){
		alert("请输入开户开始时间。");
		return false;
	}
	var endTime=$("#END_TIME").val();  
	if(endTime == null || endTime ==""){
		alert("请输入开户终止时间。");
		return false;
	}
	//效验是否跨月
	if(!checkDate()){
		return false;
	}
	$.beginPageLoading("正在查询数据...");
	$.ajax.submit('QueryRecordPart','queryCardStorage','','QueryListPart',
			function(data){ 
			  	$.endPageLoading();
			},function(error_code,error_info){
				$.MessageBox.error(error_code,error_info);
				$.endPageLoading();
			});
}

function checkDate(){
	
	var startTime=$("#START_TIME").val();
	var endTime=$("#END_TIME").val();
	
	var beginYear=startTime.substr(0,4);
	var beginMonth=startTime.substr(5,2);
	var beginDay=startTime.substr(8)
	
	var endYear=endTime.substr(0,4);
	var endMonth=endTime.substr(5,2);
	var endDay=endTime.substr(8)
	 
	if(startTime != '' && endTime == '')
	{
		alert("请选择要查询的终止日期，不允许跨月。");
		return false;
	}
	
	if(startTime == '' && endTime != '')
	{
		alert( "请选择要查询的开始日期，不允许跨月。");
		return false;
	}
	if(startTime != '' && endTime != ''){ 
		if(!(beginYear==endYear&&beginMonth==endMonth)){
			alert( "查询日期必须是同一个月，不支持跨月查询！");
			return false;
		}else{
			if(parseInt(beginDay)>parseInt(endDay)){
				alert( "开始日期应小于或者等于终止日期！");
				return false;
			}
		}
	}
	
	return true;
}

function resetQry(){
	$("#AGENT_DEPART_ID1").val(""); 
}
