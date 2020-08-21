function retInfo() {
	var params = '&USER_ID=' + parent.$("#USER_ID").val() + '&SERIAL_NUMBER='
	+ parent.$("#SERIAL_NUMBER").val() + '&CUST_ID=' + parent.$("#CUST_ID").val()
	+ '&EPARCHY_CODE=' + $("#EPARCHY_CODE").val()
	+ '&ROUTE_EPARCHY_CODE=' + parent.$("#ROUTE_EPARCHY_CODE").val()
	+ '&NORMAL_USER_CHECK=false';
	 
	var userid = parent.$("#USER_ID").val();
	var url = $.redirect.buildUrl("userview.NewUserInfo","queryInfo");
	if(userid != "")
    {
		$.getSrcWindow().$('#newUserInfoPage').attr('src',url +params);
    }
    else
    {
    	$.getSrcWindow().$('#newUserInfoPage').attr('src',url);
    }
    
    $.getSrcWindow().$('#newUserInfoPage').bind('readystatechange',function(e){
    	if(e.target.readyState=="complete"){
    		e.target.contentWindow.$.resizeHeight();
    		$.getSrcWindow().mytab.switchTo("用户基本信息");
    		$.closePopupPage(true);
    	}
    });
	
}
/*弹出框关闭后对父页面进行带参数重定向。*/
function redirectMyself(cust_id,serial_number,user_id){
parent.$("#CUST_ID").val(cust_id);
parent.$("#USER_ID").val(user_id);
var param = '&EPARCHY_CODE=' + parent.$("#EPARCHY_CODE").val()
	+ '&CUST_ID=' + parent.$("#CUST_ID").val() + '&USER_ID=' + parent.$("#USER_ID").val() +'&SERIAL_NUMBER='+parent.$("#SERIAL_NUMBER").val();
  //  var params = '&CUST_ID='+cust_id+'&SERIAL_NUMBER='+serial_number+'&USER360VIEW_VALIDTYPE='+$('#USER360VIEW_VALIDTYPE').val()+'&SERVICE_NUMBER='+$('#SERVICE_NUMBER').val()+'&PSPT_NUMBER='+$('#PSPT_NUMBER').val()+'&FLAG=1';
  //  alert("redirectMyself:"+params);
	retInfo();
	//window.parent.redirectTo('userview.User360View', 'queryInfo', params, 'window.parent');
}	