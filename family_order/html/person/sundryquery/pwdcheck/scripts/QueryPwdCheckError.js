function queryPwdCheckErrInfos(){
	if (!$.validate.verifyAll()){
		return false;
	}
	//日期时间
	var startDate = $("#cond_START_DATE").val();
	var endDate = $("#cond_END_DATE").val();
	
	//日期
	var startArray1 = startDate.split("-");
	var endArray1 = endDate.split("-");
	
	var dateStart = new Date(startArray1[0],startArray1[1],startArray1[2]);
	var endStart = new Date(endArray1[0],endArray1[1],endArray1[2]);
	var day = (endStart.getTime() - dateStart.getTime()) / (1000*60*60*24);
	if(day > 7){
		alert( "【起始、结束】日期时间段不能超过【7】天！");
		return;
	}
	$.beginPageLoading("正在查询数据...");
	$.ajax.submit('QueryCondPart', 'queryPwdCheckErrInfos', null, 'QueryListPart', function(data){
		$.endPageLoading();
		if(data.get("DATA_COUNT")=="0"){
			alert("查询无数据！");
		}
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
	
}

function changeAnotherDateEleValue(){	
	var offset="-7";
	var souEle =$("#cond_END_DATE");
	var tarEle = $("#cond_START_DATE");
	
	if(souEle.val().length != 10){//仅在格式满足的情况下才执行该操作
		return;
	}
	
	var offsetYear = '';
	var offsetMonth = ''
	var offsetDay = ''
	var dates = souEle.val().split('-');
	var intEndDay = parseInt(dates[2],10);
	
	if(intEndDay <= 7){
		offsetYear = dates[0];
		offsetMonth = dates[1];
		offsetDay = '01';
	}else{
		var curDate = new Date(dates[0],parseFloat(dates[1]) - 1,dates[2]);
		var curYear = curDate.getFullYear();
		var curMonth =  parseInt(curDate.getMonth()) + 1;
		var offsetDate = new Date(curDate.getTime()+1000*60*60*24*parseInt(offset));	
		
		offsetYear = offsetDate.getFullYear();
		offsetMonth = parseInt(offsetDate.getMonth()) + 1;
		offsetDay = offsetDate.getDate();
		
		if(parseInt(offsetMonth)<10){
			offsetMonth = '0' + offsetMonth;
		}
		if(parseInt(offsetDay)<10){
			offsetDay = '0' + offsetDay;
		}
	}
	var standardDate = offsetYear + '-' + offsetMonth + '-' + offsetDay;
	tarEle.val(standardDate);
}