/**
 * 校验终端
 */
function checkTerminal(){
	var resID = $("#RES_ID").val();
	var serialNumber = $("#SERIAL_NUMBER").val();
	var tradeId = $("#TRADE_ID").val();
	var custName = $("#CUST_NAME").val();
	var userId = $("#USER_ID").val();
	var param = '&SERIAL_NUMBER='+serialNumber+'&CUST_NAME='+custName+'&RES_ID='+resID+'&TRADE_ID='+tradeId+'&USER_ID='+userId+'&refresh=true';
	if(resID == ""){
		alert("请输入终端串码");
		return false;
	}
	else{
		ajaxSubmit(this,'checkModem',param,null,function(data){
			if(data.get("DEAL_SUCCESS") != null) {
				MessageBox.confirm("提示","办理成功！",function(btn){			
					if(btn=="ok"){ 
						$.nav.getContentWindow();
					}
				},{ok:"确定"});
			}
		},function(e,i){
			$.endPageLoading();	
			MessageBox.alert("提示",i,function(){});
		});
	}
}