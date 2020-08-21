function  showAll(){

	beginPageLoading("正进行金库认证..");
	$.treasury.auth("CRM9901_Q",null,function(ret){
		endPageLoading();
	if(true === ret){
		//alert("认证成功");
		showCustInfo();
	}else{
		alert("认证失败");
	}
	});
}

function showCustInfo(){
	var param = '&EPARCHY_CODE=' + parent.$("#EPARCHY_CODE").val() 
			+ '&CUST_ID=' + parent.$("#CUST_ID").val() +'&SERIAL_NUMBER='+parent.$("#SERIAL_NUMBER").val()+'&SHOW_ALL=0';
			$.ajax.submit(null, 'queryInfo', param, 'QueryListPart', function(){
				
			},
			function(error_code,error_info){
				$.endPageLoading();
				alert(error_info);
		});
}