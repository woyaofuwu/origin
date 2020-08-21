function queryMdoIvrInfo(){
	//查询条件校验
	if(!$.validate.verifyAll("QueryModIvrCondPart")) {
		return false;
	}
	var startDate = $("#cond_START_DATE").val();
	if(startDate > getCurrentDate()){
		MessageBox.alert("提示","开始时间不能大于当前时间！");
		return false;
	}
	
	$.beginPageLoading("正在查询数据...");
	 
	$.ajax.submit('QueryModIvrCondPart', 'queryMdoIvrInfo', null, 'CustInfoPart,QueryListPart', function(data){
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		MessageBox.error("错误",error_info);
    });
}

function getCurrentDate(){
   var s = "";
   var d = new Date();                           // 创建 Date 对象。
   year= d.getYear();                            // 获取年份。
   month= (d.getMonth() + 1);          	         // 获取月份。
   day= d.getDate();                   			 // 获取日。
   if(month<10){
		month="0"+month;
	}
	if(day<10) {
		day ="0"+day;
	}
   s=year+"-"+month+"-"+day;
   return(s);                                	  // 返回日期。
}
