function setAddress(){
	$.popupPage('res.popup.AddressQryForCorrect','init','','标准地址','800','500','origAddress','',subsys_cfg.pbossintf);
}

function onTradeSubmit(){
	
	if(!$.validate.verifyAll("AddressCorrectPart")) {
		return false;
	}
	
	var staffName = $("#STAFF_NAME").val();        //提交人  
	var serialNumber = $("#SERIAL_NUMBER").val();  //提交人电话
	if(serialNumber == null || serialNumber == ""){
		alert("反馈人电话输入有误，请重新输入！");
		return false;
	}
	if(staffName == null || staffName == ""){
		alert("反馈人不能为空！");
		return false;
	}
	var origAddress = $("#origAddress").val();     // 原始地址 （固网资源传入）
	var newAddress = $("#newAddress").val();       // 新地址
	if(origAddress == null || origAddress == "")
	{
		alert("原始地址不能为空！");
		return false;
	}
	if(newAddress == null || newAddress == "")
	{
		alert("新地址不能为空！");
		return false;
	}
	if(newAddress == origAddress)
	{
		alert("您未修改地址，重新输入新地址！");
		return false;
	}
	var remark = $("#remark").val(); 
	var param = "&DEVICE_ID="+$("#DEVICE_ID").val()+"&STAFF_NAME="+staffName+"&SERIAL_NUMBER="+serialNumber
				+"&origAddress="+origAddress+"&newAddress="+newAddress
				+"&remark="+remark;
	
	$.beginPageLoading("数据提交中..");
	$.ajax.submit("","onTradeSubmit",param,'',
		function(returnData){
			$.endPageLoading();
			MessageBox.success("成功提示","反馈成功!",
					function(btn){
						$.redirect.toPage("order","broadband.widenet.AddressCorrect","",'');
					}
			);	
		},
		function(error_code,error_info){
				$.endPageLoading();
				alert(error_info);
		}
	);	
}

