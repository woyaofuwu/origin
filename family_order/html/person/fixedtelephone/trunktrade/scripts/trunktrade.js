
function refreshPartAtferAuth(data)
{
	var tradeTypeCode = $("#TRADE_TYPE_CODE").val();
	var userId = data.get('USER_INFO').get('USER_ID');
	var part="";
	var param = '&USER_ID='+userId;
	if(tradeTypeCode == '9740'){
	part="changeTrunkId";
	}else if(tradeTypeCode == '9741'){
	part="changeTrunkMainUser";
	}
	$.ajax.submit("", "loadInfo", param, part, function(data){
			$.endPageLoading();
			
		},function(code, info, detail){
			$.endPageLoading();
			MessageBox.error("错误提示","加载业务数据!", $.auth.reflushPage, null, info, detail);
		});
}
