function checkStaffNo(obj){
	var staffId = $("#con_MANAGER_STAFF_ID").val();
	if(staffId=='')
	{
		alert("请输入客户经理工号!");
		return false;
	}else if(staffId.length != 8)
	{
		alert("客户经理工号的长度必须为8个字符!");
		return false;
	}
				
	var start_date = $("#IN_DATE_START").val();
	var end_date = $("#IN_DATE_END").val;
	date=Date.parse(start_date.replace(/-/g,"/"));
	date1=Date.parse(end_date.replace(/-/g,"/"));
	if(date1<date)
	{
		alert("终止时间必须大起始时间");
		return false;
	}else if((date1-date)/(24*60*60*1000)>91)
	{
		alert("查询起始和终止时间间隔时间不能越过3个月！");
		return false;
	}
				
	return true;
}

function checkStaffNo(){
   if(!$.validate.verifyField($("#cond_MANAGER_STAFF_ID"))) return false; 
   
   /**var staffIdleg = $("#con_MANAGER_STAFF_ID").val().length;
   if(staffIdleg != 8)
	{
		alert("客户经理工号的长度必须为8个字符!");
		return false;
	}*/
	
	$.beginPageLoading("数据查询中......");
	$.ajax.submit("queryForm", "queryCustMgrTJNums", null, "CustManagerArea", 
		function(data){
			$.endPageLoading();
		},
		function(error_code,error_info, derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
    	}
    );
}
	
function searchManagerStaff() 
{
   var staffId = $("#cond_MANAGER_STAFF_ID").val();
	if(staffId=='')
	{
		alert("请输入客户经理工号!");
		return false;
	}else if(staffId.length != 8)
	{
		alert("客户经理工号的长度必须为8个字符!");
		return false;
	}
    if(!$.validate.verifyField($("#cond_MANAGER_STAFF_ID"))) 
    {
       return false;
    }  
	
	$.beginPageLoading("数据查询中......");
	$.ajax.submit('','queryCustManagerStaff','&cond_MANAGER_STAFF_ID='+staffId,'custmgrInfoPart',
	function(data){
			$.endPageLoading();
		},
		function(error_code,error_info, derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
    	}
	      );
}
function afterCheckCustManagerStaff(data)
{
	    var result = this.ajaxDataset.get(0, "RESULT_CODE");
	    var result = data.get("RESULT_CODE",false);
	    if(result!="1"){
	    	var msg=data.get("MSG",false);
	    	alert(msg,$("#con_MANAGER_STAFF_ID").val);
	    }else{
		    var name=data.get("MANGER_NAME",false);
		    $("#MANGER_NAME").value=name;
		}
}

function confirmFrom()
{
   var staffId = $("#con_MANAGER_STAFF_ID").val();
	var activeId= $("#ACTIVE_ID").val();
	var tjNumber= $("#TJNUMBER").val();
	if(staffId==""){
		alert("请输入客户经理工号!");
		return false;
	}
	if(tjNumber==""){
		alert("请输入推荐号码!");
		return false;
	}
	if(activeId==""){
		alert("请选择营销活动方案!");
		return false;
	}
    if(!verifyAll('custmgrInfoPart'))
   {
	   return false;
   }
   
   $.beginPageLoading("数据查询中......");
	$.ajax.submit('custmgrInfoPart','addCustMgrTJNum','&MANAGER_STAFF_ID='+staffId+'ACTIVE_ID='+activeId+'TJNUMBER='+tjNumber,'custmgrInfoPart',
	function(data){
			$.endPageLoading();
			alert(data.get("X_RESULTINFO"));
		},
		function(error_code,error_info, derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
    	}
	      );
   
}

function reset()
{
    $.ajax.submit("", "reset",null,"custmgrInfoPart","","");
}

function reset1()
{
    $.ajax.submit("", "reset",null,"queryForm","","");
}
	
/**
 * 
 */
function synOnKeyup(startObj, endId){
	var e = getElement(endId);
	e.value = startObj.value;
}

function completeEndSn(startObj, endId){
	var e = getElement(endId);
	e.value = startObj.value;
	focusEnd(e);
	
//id:domid
function exportBeforeAction(domid) {
	
	$.Export.get(domid).setParams("&a=a&b=b");
	return true;
	
}
//oper: 取消：cancel；终止：terminate；状态修改中的 确定：loading；导出完成后的确定：ok；导出失败时的确定：fail；
function exportAction(oper, domid) {
	
	if (oper == "cancel") {
		alert("点击[取消]按钮时执行的JS方法exportAction,当前状态[" + oper + "]");
	} else if (oper == "terminate") {
		alert("点击[终止]按钮时执行的JS方法exportAction,当前状态[" + oper + "]");
	} else if (oper == "loading") {
		alert("点击[加载]按钮时执行的JS方法exportAction,当前状态[" + oper + "]");
	} else if (oper == "ok") {
		alert("成功时点击[确定]按钮时执行的JS方法exportAction,当前状态[" + oper + "]");
	} else if (oper == "fail") {
		alert("失败时点击[确定]按钮时执行的JS方法exportAction,当前状态[" + oper + "]");
	} 
	return true;
}	
	
}	