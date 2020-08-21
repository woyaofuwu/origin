$(document).ready(function(){
	$("#QUERY_BTN").unbind("click");
	$("#QUERY_BTN").bind("click",function(){
		queryInfos();
	});
});


function queryInfos(){
//	var serial_number = $("#SERIAL_NUMBER").val();
//	if(serial_number == ""){
//		alert("手机号码不能为空！");
//		return false;
//	}
	
	var area_code = $("#AREA_CODE").val();
	if(area_code == ""){
		alert("请选择地区！");
		return false;
	}
	var start_date = $("#START_DATE").val();
	if(start_date == ""){
		alert("开始时间不能为空！");
		return false;
	}
	var end_date = $("#END_DATE").val();
	if(end_date == ""){
		alert("结束时间不能为空！");
		return false;
	}
	
	if(start_date > end_date){
		alert("起始时间大于终止时间！请重新选择日期！");
		return false;
	}
	if(DateDiff(start_date,end_date) > 30){
		alert("【起始、终止】时间差必须不超过30天！");
		return false;
	}
	
	$.beginPageLoading("正在查询数据...");
	var param = "";
	$.ajax.submit('QueryCondPart,infofonav', 'getInfos', param, 'QueryListPart', function(data){
		$.cssubmit.disabledSubmitBtn(false);
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}

function setSubmitData(a){
	//如果当前选中
	if($(a).attr("checked")){
		
		   var rowIndex = a.parentNode.parentNode.rowIndex;
		   var table = $.table.get("infoTable");
		   var json = table.getRowData(null, rowIndex);
		   var serial_number = json.get("SERIAL_NUMBER");
		   var month = json.get("MONTH");
		   var message_id = json.get("MESSAGE_ID");
		   var accept_date = json.get("ACCEPT_DATE");
		   $("#EDIT_SERIAL_NUMBER").val(serial_number);
		   $("#EDIT_ACCEPT_DATE").val(accept_date);
		   
		   $("input[name='useridbs']:checked").attr("checked",false);
		   $(a).attr("checked", true);
		   
	}else{
		 $("#EDIT_SERIAL_NUMBER").val("");
		 $("#EDIT_ACCEPT_DATE").val("");
	}
	
}

function setEditArea(){
	var table = $.table.get("infoTable");//
	var datas = table.getRowData();
	$("#EDIT_SERIAL_NUMBER").val(datas.get("SERIAL_NUMBER"));
	$("#EDIT_ACCEPT_DATE").val(datas.get("ACCEPT_DATE"));
	//alert(datas);
}

function submitBeforeAction(){

	
	var table = $.table.get("infoTable");//
	var datas = table.getRowData();
	var np_reason = $("#NP_REASON").val();
	var np_measure = $("#NP_MEASURE").val();
	var edit_remark1 = $("#EDIT_REMARK1").val();
	var edit_remark2 = $("#EDIT_REMARK2").val();
	if(np_reason == ""){
		alert("携转原因不能为空！");
		return false;
	}
	
	if(np_reason == ""){
		alert("采取措施不能为空！");
		return false;
	}
	
	var message_id = datas.get("MESSAGE_ID");
	var month = datas.get("MONTH");
	var serial_number = $("#EDIT_SERIAL_NUMBER").val();
	var accept_date =  $("#EDIT_ACCEPT_DATE").val();
	if(serial_number == ""){
		alert("号码不能为空！");
		return false;
	}
	
	if(accept_date == ""){
		alert("申请时间不能为空！");
		return false;
	}
	var param = "&REMARK2="+edit_remark2+"&REMARK1="+edit_remark1+"&ACCEPT_DATE="+accept_date+"&MONTH="+month+"&SERIAL_NUMBER="+serial_number+"&MESSAGE_ID="+message_id+"&NP_REASON="+np_reason+"&NP_MEASURE="+np_measure;
	$.cssubmit.addParam(param);
	return true;
}

function dateCompare(s_date,e_data){
	s_date = s_date.replace(/\-/gi,"/");
	e_data = e_data.replace(/\-/gi,"/");
	var s_time = new Date(s_date).getTime();
	var e_time = new Date(e_data).getTime(); 
	if(e_data>s_date){
		return 1;
	}else if(e_data == s_date){
		return 0;
	}else{
		return -1;
	}
}


//计算两个日期的间隔天数 
//wukw3 add 2011-09-30
function DateDiff(sDate1, sDate2){ //sDate1和sDate2是2002-12-18格式 
	
	var aDate, oDate1, oDate2, iDays 
	aDate = sDate1.split("-") 
	oDate1 = new Date(aDate[1] + '-' + aDate[2] + '-' + aDate[0]) //转换为12-18-2002格式 
	aDate = sDate2.split("-") 
	oDate2 = new Date(aDate[1] + '-' + aDate[2] + '-' + aDate[0]) 
	iDays = parseInt(Math.abs(oDate1 - oDate2) / 1000 / 60 / 60 /24) //把相差的毫秒数转换为天数 
	
	return iDays + 1
	
}
