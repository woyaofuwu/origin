/**
 * 自助终端手机绑定查询
 */
function queryTerminalBind() {
	//alert("queryTerminalBind");
	$.beginPageLoading("数据加载中...");
    $.ajax.submit('QueryCondPart,navt', 'queryTerminals', '', 'QueryListPart', function() {
    	$.endPageLoading();
    }, function(error_code, error_info) {
        $.endPageLoading();
        alert(error_info);
    });
}

 
function submitBeforeCheck() {
	//alert("ff");
	 
	var logObj=$("#QueryListPart").find("input[name=RADIO_LIST]:checked");
	if(!logObj || !logObj.length){
		alert("请选择一项");
		return false;
	}
	
	var ticketList=$.DatasetList();
	var checkTag=true;
	logObj.each(function(){ 
		   
		var m=$.DataMap();  
		m.put("OPER_STAFF_ID", $(this).attr("operstaffid"));
		m.put("RES_CODE", $(this).attr("rescode"));
		m.put("SERIAL_NUMBER", $(this).attr("serialnumber"));
		m.put("BIND_TIME", $(this).attr("bindtime"));
		m.put("BIND_FLAG", $(this).attr("bindflag")); 
		m.put("UNBIND_TIME", $(this).attr("unbindtime")); 
		m.put("UNBIND_STAFF_ID", $(this).attr("unbindstaffid")); 
		
		var flag =$(this).attr("bindflag");
		if ( flag !="未解绑"){
			alert("该项非未解绑状态，不可以解绑");
			checkTag=false;
			return false;
		}
		
		
		
		
		ticketList.add(m);
		var tikInfo="&TICKET_INFO="+ticketList.toString();
		tikInfo=tikInfo.replace(/%/g,"%25"); 
		$.cssubmit.addParam(tikInfo);
		
	}); 
	
	if(checkTag){
		return true;
	}
}

function tableRowClick(){

}