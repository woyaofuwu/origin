function reginalField(){
	//var param = "ADDR_NAME1="+$("#ADDR_NAME1").val()+"&ADDR_NAME2="+$("#ADDR_NAME2").val()+"&ADDR_NAME3="+$("#ADDR_NAME3").val()+"&ADDR_NAME4="+$("#ADDR_NAME4").val()+"&ADDR_NAME5="+$("#ADDR_NAME5").val();
    var param = "ADDR_NAME1="+""+"&ADDR_NAME2="+""+"&ADDR_NAME3="+""+"&ADDR_NAME4="+""+"&ADDR_NAME5="+"";

	//$.popupPage('res.popup.FiveLevelAddress','initMethod',param,'五级地址','800','500','','',subsys_cfg.pbossintf);
	$.popupPage('res.popup.FiveLevelAddress',null,param,'五级地址','800','500','','',subsys_cfg.pbossintf);

}


function onTradeSubmit()
{
	if(!$.validate.verifyAll("PreRegPart")) {
		return false;
	}
	var param = '&SERIAL_NUMBER='+$("#AUTH_SERIAL_NUMBER").val();
	//$.cssubmit.addParam(param); 
	//return true;
	
	$.ajax.submit('PreRegPart', 'onTradeSubmit', param, 'PreRegPart', function(returnData){
		$.endPageLoading();
		
		MessageBox.success("成功提示","需求收集成功！",function(btn){
			//页面跳转
    		$.redirect.toPage("order","broadband.widenet.wideprereg.WidePreReg","",'');
    	});
	},	
	function(error_code,error_info){
		$.endPageLoading();		
		/*$("#SERIAL_NUMBER").val("");
		$("#CUST_NAME").val("");
		$("#CONTACT_SN").val("");
		$("#ADDR_NAME").val("");
		$("#ADDR_NAME1").val("");
		$("#AUTH_SERIAL_NUMBER").val("");*/
		$.redirect.toPage("order","broadband.widenet.wideprereg.WidePreReg","",'');
		alert(error_info);
    });
}

