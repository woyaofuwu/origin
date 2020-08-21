
function setEndStaffID(){ 
    synOnKeyup(document.getElementById("cond_SERIAL_NUMBER1"),"cond_SERIAL_NUMBER2");
    focusEnd(document.getElementById("cond_SERIAL_NUMBER2"));
}

/**
 * 光标停在最后
 */
function focusEnd(e){ 
	 var r =e.createTextRange(); 
	 r.moveStart('character',e.value.length); 
	 r.collapse(true); 
	 r.select(); 
}

/**
 * 每输入一个开始号码，自动填充终止号码
 */
function synOnKeyup(startObj, endId){
	var e = document.getElementById(endId);
	e.value = startObj.value;
}

function setStartDate(){
    changeAnotherDateEleValue("cond_ACCEPT_DATE2","cond_ACCEPT_DATE1","-6");
}

function checkform(){
	var maxNumber = 2000;
	var start_number ;
	var end_number ;
	var start_date ;
	var end_date ;
	if( !verifyField(document.getElementById("cond_SERIAL_NUMBER1")) || !verifyField(document.getElementById("cond_SERIAL_NUMBER2"))
		|| !verifyField(document.getElementById("cond_ACCEPT_DATE1")) || ! verifyField(document.getElementById("cond_ACCEPT_DATE2"))) return false;
	if(!$.trim(document.getElementById('cond_SERIAL_NUMBER1').value) == "" &&($.trim(document.getElementById('cond_SERIAL_NUMBER2').value) == null || $.trim(document.getElementById('cond_SERIAL_NUMBER2').value) == "")){
		alert("服务号码结束不能为空！");
		return false;	
	}
	if(!$.trim(document.getElementById('cond_SERIAL_NUMBER2').value) == "" &&($.trim(document.getElementById('cond_SERIAL_NUMBER1').value) == null || $.trim(document.getElementById('cond_SERIAL_NUMBER1').value) == "")){
		alert("服务号码开始不能为空！");
		return false;	
	}
	if(!$.trim(document.getElementById('cond_SERIAL_NUMBER1').value) == "" && !$.trim(document.getElementById('cond_SERIAL_NUMBER2').value) == ""){
		start_number = parseInt(document.getElementById('cond_SERIAL_NUMBER1').value);
		end_number = parseInt(document.getElementById('cond_SERIAL_NUMBER2').value);
		if((end_number-start_number) > maxNumber){
			alert("起始号码与终止号码的差值不能超过"+ maxNumber +"！");
			return false;
		}
	}
	if(!$.trim(document.getElementById('cond_ACCEPT_DATE1').value) == "" && ($.trim(document.getElementById('cond_ACCEPT_DATE2').value) == null || $.trim(document.getElementById('cond_ACCEPT_DATE2').value) == "")){
		alert("结束时间不能为空！");
		return false;	
	}
	if(!$.trim(document.getElementById('cond_ACCEPT_DATE2').value) == "" && ($.trim(document.getElementById('cond_ACCEPT_DATE1').value) == null || $.trim(document.getElementById('cond_ACCEPT_DATE1').value) == "")){
		alert("开始时间不能为空！");
		return false;	
	}
	if(!$.trim(document.getElementById('cond_ACCEPT_DATE1').value) == "" && !$.trim(document.getElementById('cond_ACCEPT_DATE2').value) == ""){
		start_date = $.trim(document.getElementById('cond_ACCEPT_DATE1').value);
		end_date = $.trim(document.getElementById('cond_ACCEPT_DATE2').value);
		
		var startArray = start_date.split("-");
		var endArray = end_date.split("-");
		var dateStart = new Date(startArray[0],startArray[1]-1,startArray[2]);
		var endStart = new Date(endArray[0],endArray[1]-1,endArray[2]);
		var day = (endStart.getTime() - dateStart.getTime()) / (1000*60*60*24) + 1;
		
		if(dateStart.getTime() > endStart.getTime()){
			alert("结束时间不能小于开始时间!");
			return false;
		}
		if(day > 7 ){
			alert("选择的时间段不能超过7天!");
			return false;
		}
	}
	return true;
}

/**
 * 提交查询前校验并提交
 */
function queryUserEasyPrompt(obj){
	if (!checkform()) return false;
	$.ajax.submit('QueryCondPart', 'queryUserEasyPrompt', null, 'QueryListPart', function(data){
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

/**
 * 根据souId的值自动填充或修改tarId的值，偏移量由offset决定,针对日期类型 
 * souId元素必须有format="yyyy-MM-dd"属性
 */
function changeAnotherDateEleValue(souId,tarId,offset){		
	var souEle = document.getElementById(souId);
	if(souEle.value.length != 10){//仅在格式满足的情况下才执行该操作
		return;
	}
	
	var offsetYear = '';
	var offsetMonth = ''
	var offsetDay = ''
	
	var tarEle = document.getElementById(tarId);
	var dates = souEle.value.split('-');
	
	var intEndDay = parseInt(dates[2],10);
		//alert(dates[2]);
	
	//alert(intEndDay);
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
	tarEle.value = standardDate;
}
