
function setEndStaffID()
{ 
    $("#cond_END_STAFFID").val() = $("#cond_START_STAFFID").val();
}

function textToUpperCase(){
  if(event.keyCode>=65 && event.keyCode<=90 || event.type=="change"){
    event.srcElement.value = event.srcElement.value.toUpperCase();
  }
}

/**
 * 提交查询前校验及提交
 */
function queryAccountChgInfo(obj){
	//查询条件校验
	if(!$.validate.verifyAll("QueryCondPart")) {//先校验已配置的校验属性
		return false;
	}
 	var startDate = $("#cond_START_DATE").val();
 	var endDate = $("#cond_END_DATE").val();
	//校验起始日期范围
	if(!checkDateRanges(startDate, endDate, 31)){
		return false;
	}
	if(startDate>endDate)
	{
	    alert("终止日期不能小于起始日期");
	    return false;
	}
	$.ajax.submit('QueryCondPart', 'queryAccountChgInfo', null, 'QueryListPart', function(data){
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
* 校验日期起始范围
*/
function checkDateRanges(startDate, endDate, range){
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


/**
* 自动完成终止员工ID
*/
function completeEndStaffId(startObj, endId){
	//var e = getElement(endId);
	var e = document.getElementById(endId);
	e.value = startObj.value;
	focusEnd(e);
}

/**
* 光标停在最后
*/
function focusEnd(e){ 
	 //var e = event.srcElement; 
	 var r =e.createTextRange(); 
	 r.moveStart('character',e.value.length); 
	 r.collapse(true); 
	 r.select(); 
}

function synStaffIdOnkeyup(startObj, endId){
	inputCtrlForStaff(startObj);
	var e = document.getElementById(endId);
	e.value = startObj.value;
}

/**
 * 控制输入的员工号码为大写  8位
 */
function inputCtrlForStaff(obj){
	var staffId = obj.value.toUpperCase();
	if(staffId.length > 8){
		obj.value = staffId.substring(0,8);
	} else {
		obj.value = staffId;
	}
}
