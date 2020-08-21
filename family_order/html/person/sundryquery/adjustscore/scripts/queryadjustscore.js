function queryAdjustScore()
{
	if(checksubmit())
	{
		$.ajax.submit('QueryPart', 'getAdjustScore', null, 'AdjustScorPart,buttonPart', function(data){
		
			if(data.get('ALERT_INFO') != '')
			{
				alert(data.get('ALERT_INFO'));
			}
		},
		function(error_code,error_info){
			$.endPageLoading();
			alert(error_info);
    	});
	}

}

//初始化页面
$(function(){

	showTD();	
});

/* show TD */
function showTD(){
	var trade_staff_id = $("#X_TRADE_STAFF_ID");
	var trade_staff_id_s = $("#TRADE_STAFF_ID_S");
	var trade_staff_id_e = $("#TRADE_STAFF_ID_E");
	var sn = $("#SERIAL_NUMBER");

	var query_mode = $("#QUERY_MODE").val();

	if(query_mode=="1"){
		$("#STAFF_ID").css('display','');
		$("#STAFF_ID_S").css('display','none');
		$("#STAFF_ID_E").css('display','none');
		$("#SERIAL_NUMBER_SN").css('display','none');

		trade_staff_id_s.val('');
		trade_staff_id_e.val('');
		sn.val('');
	}
	if(query_mode=="2"){
		$("#STAFF_ID").css('display','none');
		$("#STAFF_ID_S").css('display','');
		$("#STAFF_ID_E").css('display','');
		$("#SERIAL_NUMBER_SN").css('display','none');

		trade_staff_id.val('');
		sn.val('');

	}
	
	if(query_mode=="3"){
		$("#SERIAL_NUMBER_SN").css('display','');

		$("#STAFF_ID").css('display','none');
		$("#STAFF_ID_S").css('display','none');
		$("#STAFF_ID_E").css('display','none');
		trade_staff_id.val('');
		trade_staff_id_s.val('');
		trade_staff_id_e.val('');

	}
}


/* check staffid */
function checkstaffid(){
	var trade_staff_id = $("#X_TRADE_STAFF_ID");
	var trade_staff_id_s = $("#TRADE_STAFF_ID_S");
	var trade_staff_id_e = $("#TRADE_STAFF_ID_E");
	var query_mode = $("#QUERY_MODE");
	if(query_mode.val()=="2")
	{
	   if(trade_staff_id_s.val().length==0 || trade_staff_id_s.val()=="")
	   {
			alert("请输入起始工号!");
			return false;
	   }
	   if(trade_staff_id_e.val().length==0 || trade_staff_id_e.val()=="")
	   {
			alert("请输入终止工号!");
			return false;
	   }
	}
	return true;
}

/* check staffid length */
function checkstaffidlength(obj){
	staffId = $(obj).val().toUpperCase();
	if(staffId.length > 8){
		obj.value = staffId.substring(0,8);
	} else {
		obj.value = staffId;
	}
}


/* check daterange */
function checkdaterange(range){
	var startDate = $('#START_DATE').val().substring(0,10);
	var endDate =   $('#END_DATE').val().substring(0,10);	
	var startArray = startDate.split("-");
	var endArray = endDate.split("-");
	var dateStart = new Date(startArray[0],startArray[1]-1,startArray[2]);
	var endStart = new Date(endArray[0],endArray[1]-1,endArray[2]);
	var day = (endStart.getTime() - dateStart.getTime()) / (1000*60*60*24) ;
	if(day > range){
		alert("选择时间段不能超过"+range+"天!");
		return false;
	}
	
	return true;
}

/* check date null */
function checkdatenull(startId, endId)
{
	if(!$.validate.verifyField($("#START_DATE")[0]) || !$.validate.verifyField($("#END_DATE")[0]))
	{
		return false;
	}
	return true;
}

/* put staffid */
function putstaffid(){
	$("#TRADE_STAFF_ID_E").val($("#TRADE_STAFF_ID_S").val());
}

/* check submit */
function checksubmit(){     
	if (!checkstaffid()) {
		return false;
	} 
    if($("#QUERY_MODE").val() == '1'){
    	$("#X_TRADE_STAFF_ID").val($("#X_TRADE_STAFF_ID").val()) ;
    }
    if($("#QUERY_MODE").val() == '2'){
    	$("#TRADE_STAFF_ID_S").val($("#TRADE_STAFF_ID_S").val());
    	$("#TRADE_STAFF_ID_E").val($("#TRADE_STAFF_ID_E").val());
    }
	if(!checkdatenull()){return false;}
	if (!checkdaterange(31)) {
		return false;
	}
	
	return true;
}

