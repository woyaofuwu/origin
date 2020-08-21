﻿function refreshPartAtferAuth(data)
{
	var userId = data.get('USER_INFO').get('USER_ID');
	var serialNumber = data.get('USER_INFO').get('SERIAL_NUMBER');
	var depart = data.get('USER_INFO').get('DEVELOP_DEPART_ID');
	var acctTag = data.get('USER_INFO').get('ACCT_TAG');
	var param = '&USER_ID='+userId;
	param += '&SERIAL_NUMBER=' + serialNumber+"&DEVELOP_DEPART_ID="+depart+"&ACCT_TAG="+acctTag;
	//alert('param============'+param);
	$.ajax.submit("", "loadInfo", param, "", function(data){
			$.endPageLoading();
			
		},function(code, info, detail){
			$.endPageLoading();
			MessageBox.error("错误提示","加载业务数据!", $.auth.reflushPage, null, info, detail);
		});
	
}