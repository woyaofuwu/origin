//成员号码订购集团信息查询成功后调用的方法
function selectMemberInfoAfterAction(data){  
	if(data == null)
		return; 
	insertGroupUserInfo(data.get("GRP_USER_INFO"));
	
	var memcustInfo = data.get("MEB_CUST_INFO");
	var memuserInfo = data.get("MEB_USER_INFO");
	var orderInfos = data.get("ORDERED_GROUPINFOS");
 
	insertMemberUserInfo(memuserInfo);  
	//刷新退订信息 
	qryPreViewInfo();
} 

function qryPreViewInfo(){
	$.beginPageLoading();
	$.ajax.submit("previewInfoPart", "initPreview", null, "ProductElementPart", 
		function(data){
		$.endPageLoading();
//			renderProductInfos(data);
			
		},
		function(error_code,error_info, derror){
			$.endPageLoading(); 
			showDetailErrorInfo(error_code,error_info,derror);
    	}
    );
}
//成员号码订购集团失败后调用的方法
function selectMemberInfoErrAfterAction(){ 
  clearMemberUserInfo();
  clearGroupUserInfo();  
}
function onSubmitBaseTradeCheck(){
	return $.validate.verifyAll('previewInfoPart');
}
// j2ee 以下老代码暂保留
function searchMemberBySerialNumber() {
   var serialValue = $("#cond_SERIAL_NUMBER").val();

  var reg= /^([0-9]|(0[.]))[0-9]{0,}(([.]*\d{1,2})|[0-9]{0,})$/; 
	
	if (!reg.test(serialValue) ){
		alert('服务号码必须为数字，请重新输入！');
		return false;
	}
	if(serialValue.length <= 0){
	 	alert('请输入成员服务号码！');
	 	return false;
	}
  	var flag = $('#GROUP_AUTH_FLAG').val();
 
    $('#TRADE_TYPE_CODE').val('3037');
    
    ajaxDirect4CS("group.destroyvpnmember.DestroyVpnMember","checkMemberInfo","&cond_SERIAL_NUMBER="+serialValue, null,false,aftersearchMemberBySerialNumber);
   
    return false;
}

function aftersearchMemberBySerialNumber(){
    $('#TRADE_TYPE_CODE').val('3037');
	$('#AUTH_SERIAL_NUMBER').val($('#cond_SERIAL_NUMBER').val());
    $('#authButton').click();
}

function authAfterFunction() {
	var flag = $('#GROUP_AUTH_FLAG').val();
	if (flag == "true") {
	var nvf=Wade.nav.getActiveNavFrame();
	nvf=nvf?nvf:parent;
		if(nvf){
        	 $('#otherMemSerial').click();
       	}
   	}
   	else {
   		return false;
   	}
      
}

function checkInfo(){
    var flagpass = $('#FLAG_PASS').val();
    if(flagpass != '1')
  	return false;
    var hitInfo = $('#HitInfo').val();
    if (hitInfo != "") {
        var choose = confirm(hitInfo);
        if (choose == false) {
        	return false;
        }
        else {
        	return true;
        }    
    }
    //start add by wangyf6 for 乡情网
    var S_XQWFLAG = $('#XQW_FLAG').val();
    if(S_XQWFLAG == "true"){
    	var xqwflag = confirm("您已办理了“入乡情网送农信通活动”,如果您不再加入集团V网,\r\n月底系统判断无V网业务,则收取农信通业务功能费(2元/月),是否继续？");
    	if(!xqwflag){
    		return false;
    	}
    }
    //end add by wangyf6
    return true;
}

function clickcheck() {
    var effectNow=$("#effectNow");
	if(effectNow.checked) {
	    $("#ifBooking").val('true');
	}
	else {
	   	$("#ifBooking").val('false');	    	
	}
}
