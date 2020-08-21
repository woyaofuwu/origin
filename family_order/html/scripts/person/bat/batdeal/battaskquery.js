function getParams(elem)
{
	var params = '&' + 'cond_BATCH_TASK_ID=' + elem.battaskid_value;
	params += '&' + 'cond_CREATE_TIME=' + elem.accepttime_value;
	params += '&' + 'cond_CREATE_STAFF_ID=' + getElementValue('cond_CREATE_STAFF_ID');
	params += '&' + 'cond_BATCH_OPER_NAME=' + getElementValue('cond_BATCH_OPER_NAME');
	return params;
}

/*
function checkpriv(){
	if (!hasPriv("SYS_BATTASK_SUPEROPER")){
 		var staffid = getElementValue("cond_CREATE_STAFF_ID");
 		var opertype = getElementValue("cond_BATCH_OPER_TYPE");
 		var serialnum = document.getElementById("cond_SERIAL_NUMBER")!=null?getElementValue("cond_SERIAL_NUMBER"):"";
 		var taskid = document.getElementById("cond_BATCH_TASK_ID")!=null?getElementValue("cond_BATCH_TASK_ID"):"";;
 		if (staffid==""&&opertype==""&&serialnum==""&&taskid=="") {
 			alert("staffid|"+staffid+"|opertype|"+opertype+"|serialnum|"+serialnum+"|taskid|"+taskid+"|");
 			alert("该工号没有SYS_BATTASK_SUPEROPER操作权限! 查询条件必填!");
 			return false 
 		}
	}else{
		return true
	}
}
*/

function redirecttodetail(rowid, table)  {
	var battaskid = table.rows[rowid].cells[1].innerHTML;
	var params = "&BATCH_TASK_ID=" + battaskid ;
	popupDialog('person.bat.BatTaskDetailQuery', 'queryTaskDetailInfo', params, '业务详情', '800', '400');

}

function changequerytype(querytype){
	if (querytype=="0"){
		//$("#cond_SERIAL_NUMBER").val('');
		$("#cond_BATCH_TASK_ID").val('');
		//$("#cond_SERIAL_NUMBER").val('');
		//$("#cond_SERIAL_NUMBER").attr('disabled',true);
		$("#cond_BATCH_TASK_ID").attr('disabled',true);
		$("#cond_CREATE_STAFF_ID").attr('disabled',false);
	}
	/*
	else if (querytype=="1") {
		$("#cond_BATCH_TASK_ID").val('');
		$("#cond_CREATE_STAFF_ID").val('');
		$("#cond_SERIAL_NUMBER").attr('disabled',false);
		$("#cond_BATCH_TASK_ID").attr('disabled',true);
		$("#cond_CREATE_STAFF_ID").attr('disabled',true);
	}
	*/
	else if (querytype=="2"){
		//$("#cond_SERIAL_NUMBER").val('');
		$("#cond_CREATE_STAFF_ID").val('');
		//$("#cond_SERIAL_NUMBER").attr('disabled',true);
		$("#cond_BATCH_TASK_ID").attr('disabled',false);
		$("#cond_CREATE_STAFF_ID").attr('disabled',true);
	}
}

function querytaskinfo() {
	if(!$.validate.verifyAll("QueryCondPart")) {
		return false;
	}
	
	//加权限判断
	//if (!hasPriv("SYS_BATTASK_SUPEROPER")){
		var qtype = $("#cond_BATCH_QUERY_TYPE").val();
		if(qtype==0) {
			var cstaffid = $("#cond_CREATE_STAFF_ID").val();
			if(cstaffid=="" || cstaffid==null) {
	 			alert("查询条件【创建员工工号】必填!")
	 			return false;
			}
		}
		/*
		else if(qtype==1) {
			var sn = $("#cond_SERIAL_NUMBER").val();
			if(sn=="" || sn==null) {
	 			alert("查询条件【服务号码】必填!")
	 			return false;
			}
		}
		*/
		else if(qtype==2) {
			var btaskid = $("#cond_BATCH_TASK_ID").val();
			if(btaskid=="" || btaskid==null) {
	 			alert("查询条件【批量任务标识】必填!")
	 			return false;
			}
		}
	//}
	
	var range = 31;
	var startDate = $("#cond_START_DATE").val();
	var endDate = $("#cond_END_DATE").val();
	//var batch_oper_name =  $("#cond_BATCH_OPER_TYPE")[0].options($("#cond_BATCH_OPER_TYPE")[0].selectedIndex).text;
	var batch_oper_name = $('#cond_BATCH_OPER_TYPE option:selected').text();
	$("#cond_BATCH_OPER_NAME").val(batch_oper_name);
	var startArray = startDate.split("-");
	var endArray = endDate.split("-");
	var dateStart = new Date(startArray[0],startArray[1]-1,startArray[2]);
	var endStart = new Date(endArray[0],endArray[1]-1,endArray[2]);
	var day = (endStart.getTime() - dateStart.getTime()) / (1000*60*60*24) + 1;
	if(day > range){
		alert("【起始、终止】日期时间段不能超过"+range+"天~");
		return false;
	}
	var batType = $("#BATTYPE").val();
	var param = "&BAT_TYPE_PARAM=" + batType;
	$.beginPageLoading("数据查询中..");
	$.ajax.submit('QueryCondPart', 'queryBatchTaskList', param, 'QueryListPart', function(data){
		$.endPageLoading();
		//var is_pop = $("#IS_POP").val();
		//if('NO' != is_pop){
			//$("#TipInfoPart").css("display","block");
		//}
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}


function tableRowDBClick() {
	var isPop = $("#IS_POP").val();
	if("DATAIMPORT" == isPop || "BATDELETE" == isPop || "INFOQUERY" == isPop || "BATAUDIT" == isPop) {
		var table = $.table.get("QueryListTable");
		var json = table.getRowData();
		var dBatchTaskId = json.get('BATCH_TASK_ID','');
		var dBatchTaskName = json.get('BATCH_TASK_NAME','');
		setPopupReturnValue(dBatchTaskId,dBatchTaskName);
	}
	
}

function taskclick(obj){
	var isPop = $("#IS_POP").val();
	if("DATAIMPORT" == isPop || "BATDELETE" == isPop || "INFOQUERY" == isPop || "BATAUDIT" == isPop) {
		var dBatchTaskId = $(obj).attr('battaskid');
		var dBatchTaskName = $(obj).attr('battaskname')
		setPopupReturnValue(dBatchTaskId,dBatchTaskName);
	}else{
		var battaskname = encodeURIComponent($(obj).attr('battaskname'));
		openNav($(obj).attr('battaskid'),'bat.battaskquery.BatBatchQuery', 'queryBatchInfo','&BATCH_TASK_ID='+$(obj).attr('battaskid')+'&BATCH_TASK_NAME='+ battaskname +'&FROM_PAGE=BatTaskQuery');
	}
}
