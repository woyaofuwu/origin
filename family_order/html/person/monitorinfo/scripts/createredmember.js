function setEndTime() { 
	var time_select = $("#TIME_SELECT");
	var end_time = $("#END_DATE"); 
	 
	if(time_select.attr('checked')) 
	{ 
		var d = new Date(); 
		var year = d.getFullYear();
		var month = (d.getMonth()+1)+''; 
		if(month.length == "1") {
			month = '0' + month;
		}
		var date = d.getDate()+'';
		if(date.length == "1") {
			date = '0' + date;
		} 
		$("#END_DATE").val(year + '-' + month + '-' + date);
	} 
	else 
	{
		$("#END_DATE").val('2050-12-31');
	}
}

//Auth组件查询后调用方法
function refreshPartAtferAuth(data)
{
	var user_info = data.get("USER_INFO").toString();
	var cust_info = data.get("CUST_INFO").toString();
	
	var param = "&USER_INFO="+user_info+"&CUST_INFO="+cust_info;
	$.beginPageLoading("数据处理中...");
	$.ajax.submit('AuthPart', 'loadChildInfo', param, 'QueryPartTwo,custInfoPart,openMobilePart', function(){
		$.endPageLoading();
	},
	function(error_code,error_info)
	{
		$.endPageLoading();
		alert(error_info);
    });
}

function createMember(){
	//查询条件校验
	if(!$.validate.verifyAll("openMobilePart")) {
		return false;
	}
	
//	$.cssubmit.bindCallBackEvent(function(data){
//		var result = data.get("MSG");
//		if(result=="S")
//		{
//			$.cssubmit.showMessage("success", "操作成功","白名单已添加！", false);
//		}else{
//			$.cssubmit.showMessage("error", "操作失败","白名单未添加！", false);
//		}
//	});
	
	return true;
}