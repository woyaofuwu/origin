function getZXInfoByGroupId() {
	var groupId = $("#cond_GROUP_ID").val();
	var productId = $("#cond_PRODUCT_ID").val();
	if(groupId == null || groupId == "")
	 	return;
	$.beginPageLoading();
	$.ajax.submit(this, 'getZXInfoByGroupId', '&GROUP_ID='+groupId+'&PRODUCT_ID='+productId,'createArea',afterQryGroupZX,function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});
	
}

function afterQryGroupZX(data) {
	$.endPageLoading();
	var resultcode = data.get("RESULT_CODE");
	
	var resultinfo = data.get("RESULT_INFO");

	if(resultcode == null || resultcode != '0'){
	    alert(resultinfo);
	    return false;
	}
	   
	var resultnum  = data.get("RESULT_NUM");
	if(resultnum =='0'){
	    alert('集团未办理专线!');
	}
	else if(resultnum =='1' ){
	    var groupId = data.get("GROUP_ID");
	    var productId = data.get("PRODUCT_ID");
	    var userId = data.get("USER_ID"); 
	    var userStateCode=$('#USER_STATE_CODESET').val();
	    $.ajax.submit(this,'getZXInfoByUserId','&GROUP_ID='+groupId+'&PRODUCT_ID='+productId+'&USER_ID='+userId+'&USER_STATE_CODESET='+userStateCode,'createArea',function(data){
	    	var opera= data.get("OPERA_ID");
	    	var epachyCode=data.get("EPARCHY_CODE");
	    	$('#cond_OPERA_TYPE').val('');
	    	$('#cond_OPERA_TYPE').attr("disabled",true);
	    	$('#cond_OPERA_TYPE').val(opera);
	    	$('#USER_ID').val(userId);
	    	$('#EPARCHY_CODE').val(epachyCode);
	    	
	    	
	    });
	}
	else{
	    var custid = data.get("CUST_ID");
	    var productid = data.get("PRODUCT_ID");
	    $.popupPageExternal('group.ttrh.QueryGroupProduct', 'queryGroupInfo', '&cond_CUST_ID='+custid+'&cond_PRODUCT_ID='+productid, '集团查询', '600', '240','cond_SERIAL_NUMBER');
	}
}

function getZXInfoBySerialNumber() {
	var serialnumber = $("#cond_SERIAL_NUMBER").val();
	var productId = $("#cond_PRODUCT_ID").val(); 
	var userStateCode=$('#USER_STATE_CODESET').val();
	
	if(serialnumber == null || serialnumber == "")
	   return;
	$.beginPageLoading();
	$.ajax.submit(this,'getZXInfoBySerialNumber','&SERIAL_NUMBER='+serialnumber+'&PRODUCT_ID='+productId+'&USER_STATE_CODESET='+userStateCode,'createArea',function(data){
		$.endPageLoading();
		var opera= data.get("OPERA_ID");
	    var epachyCode=data.get("EPARCHY_CODE");
	    $('#cond_OPERA_TYPE').val('');
	    $('#cond_OPERA_TYPE').attr("disabled",true);
	    $('#cond_OPERA_TYPE').val(opera);
	    $('#USER_ID').val(data.get("USER_ID"));
	    $('#EPARCHY_CODE').val(epachyCode);
	},function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});

}
function getZXInfoByUserid(obj) {
	var serialnumber = getElement("USER_ID").val();
	if(serialnumber == null || serialnumber == "")
	   return;
	redirectTo(this,'getZXInfoByUserId','&USER_ID='+serialnumber,'currentframe');

}
function checkCond() {
	var groupid = $("#cond_GROUP_ID").val();
	var serialnumber = $("#cond_SERIAL_NUMBER").val();
	var userid = $("#USER_ID").val();
	if((serialnumber == null || serialnumber == "") && (groupid == null || groupid == ""))
	 {
	 	alert('请输入查询条件集团编码或者服务编码！');
		return false;
	 }
	 if(userid == null || userid == "" || userid == '-1'){
	 	alert('请先查询专线信息!');
	 	return false;
	 }
	return true;

}
