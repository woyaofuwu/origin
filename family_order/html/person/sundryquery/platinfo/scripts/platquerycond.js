function btnOK() {
	if(!$.validate.verifyAll("QueryCondPart")) {
		return false;
	}
	
	var startDate = $("#START_DATE").val();
	var endDate = $("#END_DATE").val();
	if(!checkDate(startDate, endDate)) {
		return false;
	}
	
	var data = $.DataMap();
	data.put("SERIAL_NUMBER", $("#SERIAL_NUMBER").val());
	data.put("START_DATE", startDate);
	data.put("END_DATE", endDate);
	//alert(data);
	$.setReturnValue({'POP_CODING_STR':'查询条件'},false); 
	$.setReturnValue({'CODING_STR':data},true);
	return true;
}

function btnCancel() {
	setPopupReturnValue("", "");
	return true;
}

function checkDate(startDate,endDate){
	if(startDate > getCurrDate()){
		alert("开始时间不能大于当前时间！");
		return false;
	}
	if(startDate > endDate){
		alert("开始时间不能大于结束时间！");
		return false;
	}
	if(daysBetween(endDate,startDate) > 5){
		alert("查询时间不能超过5天！");
		return false;
	}
	return true;
}

/*当前时间YYYY-MM-DD*/
function getCurrDate() {   
  	var now = new Date();
  	y = now.getFullYear();
  	m = now.getMonth()+1;
  	d = now.getDate();
  	m = m<10?"0"+m:m;
  	d = d<10?"0"+d:d;
  	return y+"-"+m+"-"+d;
}

function daysBetween(DateOne,DateTwo)
{ 
　　var OneMonth = DateOne.substring(5,DateOne.lastIndexOf ('-')); 
　　var OneDay = DateOne.substring(DateOne.length,DateOne.lastIndexOf ('-')+1); 
　　var OneYear = DateOne.substring(0,DateOne.indexOf ('-')); 
　　 
　　var TwoMonth = DateTwo.substring(5,DateTwo.lastIndexOf ('-')); 
　　var TwoDay = DateTwo.substring(DateTwo.length,DateTwo.lastIndexOf ('-')+1); 
　　var TwoYear = DateTwo.substring(0,DateTwo.indexOf ('-')); 
　　 
　　var cha=((Date.parse(OneMonth+'/'+OneDay+'/'+OneYear)- Date.parse(TwoMonth+'/'+TwoDay+'/'+TwoYear))/86400000); 
　　return Math.abs(cha); 
} 

function init() {
	var operCode = $("#OPER_CODE").val();
	if(operCode == "01001" || operCode == "01011") {
		$("#StartDatePart").attr("style", "display:none");
		$("#EndDatePart").attr("style", "display:none");
		$("#START_DATE").attr("nullable", "yes");
		$("#END_DATE").attr("nullable", "yes");
	}else {
		$("#StartDatePart").attr("style", "display:")
		$("#EndDatePart").attr("style", "display:")
		$("#START_DATE").attr("nullable", "no");
		$("#END_DATE").attr("nullable", "no");
	}
}