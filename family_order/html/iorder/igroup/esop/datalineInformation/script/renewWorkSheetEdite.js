
$(function(){
	debugger;
	var operType = $("#DEAL_TYPE").val();
	if("34" == operType){
		$("#paidanxinxi").css("display","");
		$("#openDefaultOp").css("display","none");
		$("#hideDefaultOp").css("display","");
		
	}else{
		$("#paidanxinxi").css("display","");
		$("#openDefaultOp").css("display","");
		$("#hideDefaultOp").css("display","none");
		
		
	}
	if("31" == operType||"35" == operType){
		 $("#pam_BANDWIDTH").attr('disabled','');
	}else{
		 $("#pam_BANDWIDTH").attr('disabled','disabled');
	}
	
	var  productId = $("#PRODUCT_ID").val();
	
	  $("#pam_NOTIN_RSRV_STR2").attr('disabled','disabled');
	  $("#pam_NOTIN_RSRV_STR3").attr('disabled','disabled');
	 
   var   bpmTeletId = $("#BPM_TEMPLET_ID").val();     
   var   productId = $("#PRODUCT_ID").val();
   var   changeMode = $("#CHANGEMODE").val();
   
   if("7010"==productId){
	   if("业务保障级别调整"==changeMode){
		   $("#pam_BIZSECURITYLV").attr('disabled','');
		   $("#pam_TRANSFERMODE").attr('disabled','disabled');
		   $("#pam_REPEATERNUM").attr('disabled','disabled');
		   $("#pam_AMOUNT").attr('disabled','disabled');
		   $("#pam_PORTACUSTOM").attr('disabled','disabled');
		   $("#pam_CITYA").attr('disabled','disabled');
		   $("#pam_AREAA").attr('disabled','disabled');
		   $("#pam_COUNTYA").attr('disabled','disabled');
		   $("#pam_VILLAGEA").attr('disabled','disabled');
		   $("#pam_PORTAINTERFACETYPE").attr('disabled','disabled');
		   $("#pam_PORTACONTACT").attr('disabled','disabled');
		   $("#pam_PORTACONTACTPHONE").attr('disabled','disabled');
		   $("#pam_PORTARATE").attr('disabled','disabled');
		   $("#pam_ISCUSTOMERPE").attr('disabled','disabled');
		   $("#pam_CUSTOMERDEVICEMODE").attr('disabled','disabled');
		   $("#pam_CUSTOMERDEVICETYPE").attr('disabled','disabled');
		   $("#pam_CUSTOMERDEVICEVENDOR").attr('disabled','disabled');
		   $("#pam_PHONEPERMISSION").attr('disabled','disabled');
		   $("#pam_PHONELIST").attr('disabled','disabled');
		   $("#pam_TRADENAME").attr('disabled','disabled');	 
		   $("#pam_SUPPORTMODE").attr('disabled','disabled');
	   }
	   if("扩容"==changeMode){
	   	   $("#pam_BANDWIDTH").attr('disabled',false);
	 	   $("#pam_TRANSFERMODE").attr('disabled','disabled');
		   $("#pam_SUPPORTMODE").attr('disabled','disabled');
		   $("#pam_BIZSECURITYLV").attr('disabled','disabled');
		   $("#pam_REPEATERNUM").attr('disabled','disabled');
		   $("#pam_AMOUNT").attr('disabled','disabled');
		   $("#pam_PORTACUSTOM").attr('disabled','disabled');
		   $("#pam_CITYA").attr('disabled','disabled');
		   $("#pam_AREAA").attr('disabled','disabled');
		   $("#pam_COUNTYA").attr('disabled','disabled');
		   $("#pam_VILLAGEA").attr('disabled','disabled');
		   $("#pam_PORTAINTERFACETYPE").attr('disabled','disabled');
		   $("#pam_PORTACONTACT").attr('disabled','disabled');
		   $("#pam_PORTACONTACTPHONE").attr('disabled','disabled');
		   $("#pam_PORTARATE").attr('disabled','disabled');
		   $("#pam_ISCUSTOMERPE").attr('disabled','disabled');
		   $("#pam_CUSTOMERDEVICEMODE").attr('disabled','disabled');
		   $("#pam_CUSTOMERDEVICETYPE").attr('disabled','disabled');
		   $("#pam_CUSTOMERDEVICEVENDOR").attr('disabled','disabled');
		   $("#pam_PHONEPERMISSION").attr('disabled','disabled');
		   $("#pam_PHONELIST").attr('disabled','disabled');
		   $("#pam_TRADENAME").attr('disabled','disabled');	 
	   }
	   if("异楼搬迁"==changeMode){
		   $("#pam_TRANSFERMODE").attr('disabled','');
		   $("#pam_SUPPORTMODE").attr('disabled','');
		   $("#pam_BIZSECURITYLV").attr('disabled','');
		   $("#pam_REPEATERNUM").attr('disabled','');
		   $("#pam_AMOUNT").attr('disabled','');
		   $("#pam_PORTACUSTOM").attr('disabled','');
		   $("#pam_CITYA").attr('disabled','');
		   $("#pam_AREAA").attr('disabled','');
		   $("#pam_COUNTYA").attr('disabled','');
		   $("#pam_VILLAGEA").attr('disabled','');
		   $("#pam_PORTAINTERFACETYPE").attr('disabled','');
		   $("#pam_PORTACONTACT").attr('disabled','');
		   $("#pam_PORTACONTACTPHONE").attr('disabled','');
		   $("#pam_PORTARATE").attr('disabled','');
		   $("#pam_ISCUSTOMERPE").attr('disabled','');
		   $("#pam_CUSTOMERDEVICEMODE").attr('disabled','');
		   $("#pam_CUSTOMERDEVICETYPE").attr('disabled','');
		   $("#pam_CUSTOMERDEVICEVENDOR").attr('disabled','');
		   $("#pam_PHONEPERMISSION").attr('disabled','');
		   $("#pam_PHONELIST").attr('disabled','');
		   $("#pam_TRADENAME").attr('disabled','');
	   }
	   if("同楼搬迁"==changeMode){
		   $("#pam_TRANSFERMODE").attr('disabled','');
		   $("#pam_SUPPORTMODE").attr('disabled','');
		   $("#pam_REPEATERNUM").attr('disabled','');
		   $("#pam_AMOUNT").attr('disabled','');
		   $("#pam_PORTACUSTOM").attr('disabled','');
		   $("#pam_CITYA").attr('disabled','');
		   $("#pam_AREAA").attr('disabled','');
		   $("#pam_COUNTYA").attr('disabled','');
		   $("#pam_VILLAGEA").attr('disabled','');
		   $("#pam_PORTAINTERFACETYPE").attr('disabled','');
		   $("#pam_PORTACONTACT").attr('disabled','');
		   $("#pam_PORTACONTACTPHONE").attr('disabled','');
		   $("#pam_PORTARATE").attr('disabled','');
		   $("#pam_ISCUSTOMERPE").attr('disabled','');
		   $("#pam_CUSTOMERDEVICEMODE").attr('disabled','');
		   $("#pam_CUSTOMERDEVICETYPE").attr('disabled','');
		   $("#pam_CUSTOMERDEVICEVENDOR").attr('disabled','');
		   $("#pam_PHONEPERMISSION").attr('disabled','');
		   $("#pam_PHONELIST").attr('disabled','');
		   $("#pam_TRADENAME").attr('disabled','');
		   $("#pam_BIZSECURITYLV").attr('disabled','disabled');
	   }
	   if("减容"==changeMode){
	       $("#pam_BANDWIDTH").attr('disabled',false);
	 	   $("#pam_TRANSFERMODE").attr('disabled','disabled');
		   $("#pam_REPEATERNUM").attr('disabled','disabled');
		   $("#pam_AMOUNT").attr('disabled','disabled');
		   $("#pam_PORTACUSTOM").attr('disabled','disabled');
		   $("#pam_CITYA").attr('disabled','disabled');
		   $("#pam_AREAA").attr('disabled','disabled');
		   $("#pam_COUNTYA").attr('disabled','disabled');
		   $("#pam_VILLAGEA").attr('disabled','disabled');
		   $("#pam_PORTAINTERFACETYPE").attr('disabled','disabled');
		   $("#pam_PORTACONTACT").attr('disabled','disabled');
		   $("#pam_PORTACONTACTPHONE").attr('disabled','disabled');
		   $("#pam_PORTARATE").attr('disabled','disabled');
		   $("#pam_ISCUSTOMERPE").attr('disabled','disabled');
		   $("#pam_CUSTOMERDEVICEMODE").attr('disabled','disabled');
		   $("#pam_CUSTOMERDEVICETYPE").attr('disabled','disabled');
		   $("#pam_CUSTOMERDEVICEVENDOR").attr('disabled','disabled');
		   $("#pam_PHONEPERMISSION").attr('disabled','disabled');
		   $("#pam_PHONELIST").attr('disabled','disabled');
		   $("#pam_TRADENAME").attr('disabled','disabled');	
		   $("#pam_SUPPORTMODE").attr('disabled','disabled');
		   $("#pam_BIZSECURITYLV").attr('disabled','disabled');
	   }
   }else if("7011"==productId||"70111"==productId||"70112"==productId){
	   if("业务保障级别调整"==changeMode){
		   $("#pam_BIZSECURITYLV").attr('disabled','');
		   $("#pam_TRADEID").closest("li").css("display","");
		   $('#pam_TRADEID').css('display',"");
		   $('#pam_TRADEID').attr('nullable',"no");
		   $("#pam_TRADEID").closest("li").attr("class", "link required");
	   }
	   if("扩容"==changeMode){
			$("#pam_BANDWIDTH").attr('disabled',false);
			$("#pam_TRADEID").closest("li").css("display","");
		 	$('#pam_TRADEID').css('display',"");
		 	$('#pam_TRADEID').attr('nullable',"no");
		 	$("#pam_TRADEID").closest("li").attr("class", "link required");
	   }
	   if("异楼搬迁"==changeMode){
		   $("#pam_AREAA").attr('disabled','');
		   $("#pam_BIZSECURITYLV").attr('disabled','');
		   $("#pam_CITYA").attr('disabled','');
		   $("#pam_COUNTYA").attr('disabled','');
		   $("#pam_CUSAPPSERVIPADDNUM").attr('disabled','');
		   $("#pam_DOMAINNAME").attr('disabled','');
		   $("#pam_IPCHANGE").attr('disabled','');
		   $("#pam_MAINDOMAINADD").attr('disabled','');
		   $("#pam_PORTACONTACT").attr('disabled','');
		   $("#pam_PORTACONTACTPHONE").attr('disabled','');
		   $("#pam_PORTACUSTOM").attr('disabled','');
		   $("#pam_PORTAINTERFACETYPE").attr('disabled','');
		   $("#pam_PRODUCTNO").attr('disabled','disabled');
		   $("#pam_TRANSFERMODE").attr('disabled','');
		   $("#pam_TRADENAME").attr('disabled','');
		   $("#pam_VILLAGEA").attr('disabled','');
		   $("#pam_CUSAPPSERVIPV6ADDNUM").attr('disabled','');
		   $("#pam_CUSAPPSERVIPADDNUM").attr('disabled','');
		   $("#pam_CUSAPPSERVIPV4ADDNUM").attr('disabled','');
		   $("#pam_DOMAINNAME").attr('disabled','');
		   $("#pam_MAINDOMAINADD").attr('disabled','');
		   $("#pam_IPTYPE").attr('disabled','');
		   $("#pam_COUNTYA").attr('disabled','');
		   $("#pam_TRADEID").closest("li").css("display","");
		   $('#pam_TRADEID').css('display',"");
		   $('#pam_TRADEID').attr('nullable',"no");
		   $("#pam_TRADEID").closest("li").attr("class", "link required");
	   }
	   if("IP地址调整"==changeMode){
		   $("#pam_TRADEID").closest("li").css("display","");
		   $('#pam_TRADEID').css('display',"");
		   $('#pam_TRADEID').attr('nullable',"no");
		   $("#pam_TRADEID").closest("li").attr("class", "link required");
		   $("#pam_IPCHANGE").closest("li").css("display","");
		   $('#pam_IPCHANGE').css('display',"");
		   $('#pam_IPCHANGE').attr('nullable',"no");
		   $("#pam_IPCHANGE").closest("li").attr("class", "link required");
		   $("#pam_IPCHANGE").attr('disabled','');
		   $("#pam_IPTYPE").attr('disabled','');
		   $("#pam_COUNTYA").attr('disabled','');
		   $("#pam_CUSAPPSERVIPV6ADDNUM").attr('disabled','');
		   $("#pam_CUSAPPSERVIPADDNUM").attr('disabled','');
		   $("#pam_CUSAPPSERVIPV4ADDNUM").attr('disabled','');
		   $("#pam_DOMAINNAME").attr('disabled','');
		   $("#pam_MAINDOMAINADD").attr('disabled','');

	   }
	   if("同楼搬迁"==changeMode){
		   $("#pam_AREAA").attr('disabled','');
		   $("#pam_CITYA").attr('disabled','');
		   $("#pam_COUNTYA").attr('disabled','');
		   $("#pam_CUSAPPSERVIPADDNUM").attr('disabled','');
		   $("#pam_DOMAINNAME").attr('disabled','');
		   $("#pam_IPCHANGE").attr('disabled','');
		   $("#pam_MAINDOMAINADD").attr('disabled','');
		   $("#pam_PORTACONTACT").attr('disabled','');
		   $("#pam_PORTACONTACTPHONE").attr('disabled','');
		   $("#pam_PORTACUSTOM").attr('disabled','');
		   $("#pam_PORTAINTERFACETYPE").attr('disabled','');
		   $("#pam_PRODUCTNO").attr('disabled','disabled');
		   $("#pam_TRANSFERMODE").attr('disabled','');
		   $("#pam_VILLAGEA").attr('disabled','');
		   $("#pam_TRADENAME").attr('disabled','');
		   $("#pam_CUSAPPSERVIPV6ADDNUM").attr('disabled','');
		   $("#pam_CUSAPPSERVIPADDNUM").attr('disabled','');
		   $("#pam_CUSAPPSERVIPV4ADDNUM").attr('disabled','');
		   $("#pam_DOMAINNAME").attr('disabled','');
		   $("#pam_MAINDOMAINADD").attr('disabled','');
		   $("#pam_IPTYPE").attr('disabled','');
		   $("#pam_COUNTYA").attr('disabled','');
		   $("#pam_TRADEID").closest("li").css("display","");
		   $('#pam_TRADEID').css('display',"");
		   $('#pam_TRADEID').attr('nullable',"no");
		   $("#pam_TRADEID").closest("li").attr("class", "link required");
		   
	   }
	   if("减容"==changeMode){
		   	$("#pam_BANDWIDTH").attr('disabled',false);
		 	$("#pam_TRADEID").closest("li").css("display","");
			$('#pam_TRADEID').css('display',"");
			$('#pam_TRADEID').attr('nullable',"no");
			$("#pam_TRADEID").closest("li").attr("class", "link required");

	   }
   }else if("7012"==productId||"70121"==productId||"70122"==productId){
	   if("业务保障级别调整"==changeMode){
		   	$("#pam_ROUTEMODE").attr('disabled','');
		   	$("#pam_BIZSECURITYLV").attr('disabled','');
		 	$("#pam_PORTACUSTOM").attr('disabled','disabled');
		 	$("#pam_PORTACONTACT").attr('disabled','disabled');
		 	$("#pam_PORTACONTACTPHONE").attr('disabled','disabled');
		 	$("#pam_PORTAINTERFACETYPE").attr('disabled','disabled');
		 	$("#pam_TRADENAME").attr('disabled','disabled');
		 	$("#pam_PORTZCUSTOM").attr('disabled','disabled');
		 	$("#pam_PORTZCONTACT").attr('disabled','disabled');
		 	$("#pam_PORTZINTERFACETYPE").attr('disabled','disabled');
		 	$("#pam_PORTZCONTACTPHONE").attr('disabled','disabled');
		 	$("#pam_TRADEID").closest("li").css("display","");
			$('#pam_TRADEID').css('display',"");
			$('#pam_TRADEID').attr('nullable',"no");
			$("#pam_TRADEID").closest("li").attr("class", "link required");
	   }
	   if("扩容"==changeMode){
		   	$("#pam_BANDWIDTH").attr('disabled',false);
		 	$("#pam_ROUTEMODE").attr('disabled','disabled');
		 	$("#pam_BIZSECURITYLV").attr('disabled','disabled');
		 	$("#pam_PORTACUSTOM").attr('disabled','disabled');
		 	$("#pam_PORTACONTACT").attr('disabled','disabled');
		 	$("#pam_PORTACONTACTPHONE").attr('disabled','disabled');
		 	$("#pam_PORTAINTERFACETYPE").attr('disabled','disabled');
		 	$("#pam_TRADENAME").attr('disabled','disabled');
		 	$("#pam_PORTZCUSTOM").attr('disabled','disabled');
		 	$("#pam_PORTZCONTACT").attr('disabled','disabled');
		 	$("#pam_PORTZINTERFACETYPE").attr('disabled','disabled');
		 	$("#pam_PORTZCONTACTPHONE").attr('disabled','disabled');
		 	$("#pam_TRADEID").closest("li").css("display","");
			$('#pam_TRADEID').css('display',"");
			$('#pam_TRADEID').attr('nullable',"no");
			$("#pam_TRADEID").closest("li").attr("class", "link required");
	   }
	   if("异楼搬迁"==changeMode){
		   $("#pam_VILLAGEZ").attr('disabled','');
		   $("#pam_VILLAGEA").attr('disabled','');
		   $("#pam_TRADENAME").attr('disabled','');
		   $("#pam_ROUTEMODE").attr('disabled','');
		   $("#pam_PORTZINTERFACETYPE").attr('disabled','');
		   $("#pam_PORTZCUSTOM").attr('disabled','');
		   $("#pam_PORTZCONTACTPHONE").attr('disabled','');
		   $("#pam_PORTZCONTACT").attr('disabled','');
		   $("#pam_PORTAINTERFACETYPE").attr('disabled','');
		   $("#pam_PORTACUSTOM").attr('disabled','');
		   $("#pam_PORTACONTACTPHONE").attr('disabled','');
		   $("#pam_PORTACONTACT").attr('disabled','');
		   $("#pam_COUNTYZ").attr('disabled','');
		   $("#pam_COUNTYA").attr('disabled','');
		   $("#pam_CITYZ").attr('disabled','');
		   $("#pam_CITYA").attr('disabled','');
		   $("#pam_BIZSECURITYLV").attr('disabled','');
		   $("#pam_AREAZ").attr('disabled','');
		   $("#pam_AREAA").attr('disabled','');
		   $("#pam_TRADEID").closest("li").css("display","");
		   $('#pam_TRADEID').css('display',"");
		   $('#pam_TRADEID').attr('nullable',"no");
		   $("#pam_TRADEID").closest("li").attr("class", "link required");
	   }
	   if("同楼搬迁"==changeMode){
		   $("#pam_VILLAGEZ").attr('disabled','');
		   $("#pam_VILLAGEA").attr('disabled','');
		   $("#pam_TRADENAME").attr('disabled','');
		   $("#pam_ROUTEMODE").attr('disabled','');
		   $("#pam_PORTZINTERFACETYPE").attr('disabled','');
		   $("#pam_PORTZCUSTOM").attr('disabled','');
		   $("#pam_PORTZCONTACTPHONE").attr('disabled','');
		   $("#pam_PORTZCONTACT").attr('disabled','');
		   $("#pam_PORTAINTERFACETYPE").attr('disabled','');
		   $("#pam_PORTACUSTOM").attr('disabled','');
		   $("#pam_PORTACONTACTPHONE").attr('disabled','');
		   $("#pam_PORTACONTACT").attr('disabled','');
		   $("#pam_COUNTYZ").attr('disabled','');
		   $("#pam_COUNTYA").attr('disabled','');
		   $("#pam_CITYZ").attr('disabled','');
		   $("#pam_CITYA").attr('disabled','');
		   $("#pam_AREAZ").attr('disabled','');
		   $("#pam_AREAA").attr('disabled','');
		   $("#pam_BIZSECURITYLV").attr('disabled','disabled');
		   $("#pam_TRADEID").closest("li").css("display","");
		   $('#pam_TRADEID').css('display',"");
		   $('#pam_TRADEID').attr('nullable',"no");
		   $("#pam_TRADEID").closest("li").attr("class", "link required");
	   }
	   if("减容"==changeMode){
		    $("#pam_BANDWIDTH").attr('disabled',false);
		 	$("#pam_ROUTEMODE").attr('disabled','disabled');
		 	$("#pam_BIZSECURITYLV").attr('disabled','disabled');
		 	$("#pam_PORTACUSTOM").attr('disabled','disabled');
		 	$("#pam_PORTACONTACT").attr('disabled','disabled');
		 	$("#pam_PORTACONTACTPHONE").attr('disabled','disabled');
		 	$("#pam_PORTAINTERFACETYPE").attr('disabled','disabled');
		 	$("#pam_TRADENAME").attr('disabled','disabled');
		 	$("#pam_PORTZCUSTOM").attr('disabled','disabled');
		 	$("#pam_PORTZCONTACT").attr('disabled','disabled');
		 	$("#pam_PORTZINTERFACETYPE").attr('disabled','disabled');
		 	$("#pam_PORTZCONTACTPHONE").attr('disabled','disabled');
		 	$("#pam_TRADEID").closest("li").css("display","");
			$('#pam_TRADEID').css('display',"");
			$('#pam_TRADEID').attr('nullable',"no");
			$("#pam_TRADEID").closest("li").attr("class", "link required");
	   }
   }
   else if("7016"==productId){
	   var headeStr="pam_"
	   if("业务保障级别调整"==changeMode){
		   $("#"+headeStr+"BIZSECURITYLV").attr('disabled','');
		   $("#"+headeStr+"TRADEID").closest("li").css("display","");
		   $("#"+headeStr+"TRADEID").css('display',"");
		   $("#"+headeStr+"TRADEID").attr('nullable',"no");
		   $("#"+headeStr+"TRADEID").closest("li").attr("class", "link required");
	   }
	   if("扩容"==changeMode){
			$("#"+headeStr+"BANDWIDTH").attr('disabled',false);
			$("#"+headeStr+"TRADEID").closest("li").css("display","");
		 	$("#"+headeStr+"TRADEID").css('display',"");
		 	$("#"+headeStr+"TRADEID").attr('nullable',"no");
		 	$("#"+headeStr+"TRADEID").closest("li").attr("class", "link required");
	   }
	   if("异楼搬迁"==changeMode){
		   $("#"+headeStr+"AREAA").attr('disabled','');
		   $("#"+headeStr+"BIZSECURITYLV").attr('disabled','');
		   $("#"+headeStr+"CITYA").attr('disabled','');
		   $("#"+headeStr+"COUNTYA").attr('disabled','');
		   $("#"+headeStr+"CUSAPPSERVIPADDNUM").attr('disabled','');
		   $("#"+headeStr+"DOMAINNAME").attr('disabled','');
		   $("#"+headeStr+"IPCHANGE").attr('disabled','');
		   $("#"+headeStr+"MAINDOMAINADD").attr('disabled','');
		   $("#"+headeStr+"PORTACONTACT").attr('disabled','');
		   $("#"+headeStr+"PORTACONTACTPHONE").attr('disabled','');
		   $("#"+headeStr+"PORTACUSTOM").attr('disabled','');
		   $("#"+headeStr+"PORTAINTERFACETYPE").attr('disabled','');
		   $("#"+headeStr+"PRODUCTNO").attr('disabled','disabled');
		   $("#"+headeStr+"TRANSFERMODE").attr('disabled','');
		   $("#"+headeStr+"TRADENAME").attr('disabled','');
		   $("#"+headeStr+"VILLAGEA").attr('disabled','');
		   $("#"+headeStr+"CUSAPPSERVIPADDNUM").attr('disabled','');
		   $("#"+headeStr+"DOMAINNAME").attr('disabled','');
		   $("#"+headeStr+"MAINDOMAINADD").attr('disabled','');
		   $("#"+headeStr+"IPTYPE").attr('disabled','');
		   $("#"+headeStr+"COUNTYA").attr('disabled','');
		   $("#"+headeStr+"SUPPORTMODE").attr('disabled','');
		   $("#"+headeStr+"ISCUSTOMERPE").attr('disabled','');
		   $("#"+headeStr+"CUSTOMERDEVICEMODE").attr('disabled','');
		   $("#"+headeStr+"CUSTOMERDEVICETYPE").attr('disabled','');
		   $("#"+headeStr+"CUSTOMERDEVICEVENDOR").attr('disabled','');
		   $("#"+headeStr+"CUSTOMERDEVICELIST").attr('disabled','');
		   $("#"+headeStr+"PHONELIST").attr('disabled','');
		   $("#"+headeStr+"AMOUNT").attr('disabled','');

		   $("#"+headeStr+"TRADEID").closest("li").css("display","");
		   $("#"+headeStr+"TRADEID").css('display',"");
		   $("#"+headeStr+"TRADEID").attr('nullable',"no");
		   $("#"+headeStr+"TRADEID").closest("li").attr("class", "link required");
	   }
	   if("IP地址调整"==changeMode){
		   $("#"+headeStr+"TRADEID").closest("li").css("display","");
		   $("#"+headeStr+"TRADEID").css('display',"");
		   $("#"+headeStr+"TRADEID").attr('nullable',"no");
		   $("#"+headeStr+"TRADEID").closest("li").attr("class", "link required");
		   $("#"+headeStr+"IPCHANGE").closest("li").css("display","");
		   $("#"+headeStr+"IPCHANGE").css('display',"");
		   $("#"+headeStr+"IPCHANGE").attr('nullable',"no");
		   $("#"+headeStr+"IPCHANGE").closest("li").attr("class", "link required");
		   $("#"+headeStr+"IPCHANGE").attr('disabled','');
		   $("#"+headeStr+"IPTYPE").attr('disabled','');
		   $("#"+headeStr+"COUNTYA").attr('disabled','');
		   $("#"+headeStr+"CUSAPPSERVIPADDNUM").attr('disabled','');
		   $("#"+headeStr+"DOMAINNAME").attr('disabled','');
		   $("#"+headeStr+"MAINDOMAINADD").attr('disabled','');

	   }
	   if("同楼搬迁"==changeMode){
		   $("#"+headeStr+"AREAA").attr('disabled','');
		   $("#"+headeStr+"CITYA").attr('disabled','');
		   $("#"+headeStr+"COUNTYA").attr('disabled','');
		   $("#"+headeStr+"CUSAPPSERVIPADDNUM").attr('disabled','');
		   $("#"+headeStr+"DOMAINNAME").attr('disabled','');
		   $("#"+headeStr+"IPCHANGE").attr('disabled','');
		   $("#"+headeStr+"MAINDOMAINADD").attr('disabled','');
		   $("#"+headeStr+"PORTACONTACT").attr('disabled','');
		   $("#"+headeStr+"PORTACONTACTPHONE").attr('disabled','');
		   $("#"+headeStr+"PORTACUSTOM").attr('disabled','');
		   $("#"+headeStr+"PORTAINTERFACETYPE").attr('disabled','');
		   $("#"+headeStr+"PRODUCTNO").attr('disabled','disabled');
		   $("#"+headeStr+"TRANSFERMODE").attr('disabled','');
		   $("#"+headeStr+"VILLAGEA").attr('disabled','');
		   $("#"+headeStr+"TRADENAME").attr('disabled','');
		   $("#"+headeStr+"CUSAPPSERVIPADDNUM").attr('disabled','');
		   $("#"+headeStr+"DOMAINNAME").attr('disabled','');
		   $("#"+headeStr+"MAINDOMAINADD").attr('disabled','');
		   $("#"+headeStr+"IPTYPE").attr('disabled','');
		   $("#"+headeStr+"COUNTYA").attr('disabled','');
		   
		   $("#"+headeStr+"SUPPORTMODE").attr('disabled','');
		   $("#"+headeStr+"ISCUSTOMERPE").attr('disabled','');
		   $("#"+headeStr+"CUSTOMERDEVICEMODE").attr('disabled','');
		   $("#"+headeStr+"CUSTOMERDEVICETYPE").attr('disabled','');
		   $("#"+headeStr+"CUSTOMERDEVICEVENDOR").attr('disabled','');
		   $("#"+headeStr+"CUSTOMERDEVICELIST").attr('disabled','');
		   $("#"+headeStr+"PHONELIST").attr('disabled','');
		   $("#"+headeStr+"AMOUNT").attr('disabled','');
		   
		   $("#"+headeStr+"TRADEID").closest("li").css("display","");
		   $("#"+headeStr+"TRADEID").css('display',"");
		   $("#"+headeStr+"TRADEID").attr('nullable',"no");
		   $("#"+headeStr+"TRADEID").closest("li").attr("class", "link required");
		   
	   }
	   if("减容"==changeMode){
		   	$("#"+headeStr+"BANDWIDTH").attr('disabled',false);
		 	$("#"+headeStr+"TRADEID").closest("li").css("display","");
			$("#"+headeStr+"TRADEID").css('display',"");
			$("#"+headeStr+"TRADEID").attr('nullable',"no");
			$("#"+headeStr+"TRADEID").closest("li").attr("class", "link required");

	   }
   }
   
   //快开不允许修改地址信息
	var lineopenTag = $("#LINEOPEN_TAG").val();
	
	if("1"==lineopenTag){
		 $("#pam_VILLAGEA").attr('disabled','disabled');
		 $("#pam_COUNTYA").attr('disabled','disabled');
	}
   
   
   
	$("#fileupload").select(function(){
		var fileList = new Wade.DatasetList();
		
		var obj = this.val();
		var fileIdArr = obj.ID.split(",");
		var fileNameArr = obj.NAME.split(",");
		for(var i = 0, size = fileIdArr.length; i < size; i++)
		{
			if(fileIdArr[i] != "")
			{
				var data = new Wade.DataMap();
				data.put("FILE_ID", fileIdArr[i]);
				data.put("FILE_NAME", fileNameArr[i]);
				data.put("ATTACH_TYPE", "P");
				fileList.add(data);
			}
		}
		$("#ATTACH_FILE_LIST").text(fileList.toString());
		
		$("#upfileText").text(obj.NAME);
		$("#upfileValue").val(obj.ID);
		
		if("34" == operType){
			$("#ATTACH_FILE_NAME1").val(obj.NAME);
		}else{
			$("#ATTACH_FILE_NAME").val(obj.NAME);
		}
		$("#ATTACH_FILE_ID").val(obj.ID);
		
		hidePopup("popup01");
	});
	
	$("#fileupload").clear(function(){
		$("#upfileText").text("");
		$("#upfileValue").val("");
		if("34" == operType){
			$("#ATTACH_FILE_NAME1").val("");
		}else{
			$("#ATTACH_FILE_NAME").val("");
		}
		$("#ATTACH_FILE_ID").val("");
		
		$("#ATTACH_FILE_LIST").text("");
	});
	
	if($("#FILEUPLOAD_FILELIST").val()){
		var fileListP = $("#FILEUPLOAD_FILELIST").val();
		fileupload.loadFile(fileListP);
	}
	
	$("#CONTRACT_FILE_LIST").afterAction(function(e, file){
		var data1 = new Wade.DataMap();
		data1.put("FILE_ID", file.fileId);
		data1.put("FILE_NAME", file.name);
		data1.put("ATTACH_TYPE", "C");
		$("#C_FILE_LIST").text(data1.toString());
		$("#C_FILE_LIST").val(file.fileId+':'+file.name);
		
		$("#C_FILE_LIST_NAME").val(file.name);
	});
	
});


function checkSub(obj)
{
	debugger;
	var productId =$("#PRODUCT_ID").val();
	var dealType =$("#DEAL_TYPE").val();
	var attachInfos = saveAttach();
	var contractInfos = contractAttach();
	var buildingsection =$("#BUILDINGSECTION").val();
	var bandwidthA =$("#BANDWIDTH_A").val();//修改前的默认带宽
	var publicAttrInfo = new Wade.DataMap($("#PUBLIC_ATTR_LIST").text());
	var changeMode = $("#CHANGEMODE").val();
	var bandwidth =$("#pam_BANDWIDTH").val();
	var ipChange =$("#pam_IPCHANGE").val();
	var areaa =$("#pam_AREAA").val();
	var areaz =$("#pam_AREAZ").val();
	var hiddbandwidth =$("#HIDDEN_BANDWIDTH").val();//勘察单带宽
	
	if("34"!=dealType){
		if(""==areaa||null ==areaa || undefined==areaa){
			$.validate.alerter.one($("#pam_AREAA")[0], "您修改了A端所属地市，未选择所属区县，请选择！");
			return false;
		}
		if("7012"==productId||"70121"==productId||"70122"==productId){
			if(""==areaz||null ==areaz || undefined==areaz){
				$.validate.alerter.one($("#pam_AREAZ")[0], "您修改了Z端所属地市，未选择所属区县，请选择！");
				return false;
			}
		}
	}
	if(bandwidth > bandwidthA && "减容"==changeMode){
		$.validate.alerter.one($("#pam_BANDWIDTH")[0], "您的减容变更重派，带宽大于开通时的带宽【" +bandwidthA+ "M】，请重新输入！");
        return false;
	}
	if(bandwidth > hiddbandwidth && "扩容"==changeMode){
		$.validate.alerter.one($("#pam_BANDWIDTH")[0], "您的扩容变更重派，带宽大于勘察时的带宽【" +hiddbandwidth+ "M】，请重新输入！");
        return false;
	}
	if("IP地址调整"==changeMode&&"7016"!=productId){
		if(""==ipChange||null ==ipChange || undefined==ipChange){
			$.validate.alerter.one($("#pam_IPCHANGE")[0], "您未输入IP地址调整，请输入！");
			return false;
		}
	}
	var pram ;
	if("34"!=dealType){
		var notinrsrvstr2A = $("#pam_NOTIN_RSRV_STR2").val();
		var notinrsrvstr3A = $("#pam_NOTIN_RSRV_STR3").val();
		var bandwidthA = $("#pam_BANDWIDTH").val();
		var offerChaSpecData = submitOfferCha();
		 pram = "&BUILDINGSECTION="+buildingsection+"&PUBLIC_ATTR_LIST="+publicAttrInfo.toString()+"&ATTACH_LIST="+attachInfos+"&CONTRACTATTACH_LIST="+contractInfos+"&DATALINE_LIST="+offerChaSpecData.toString();
	}else{
		var buildingsection1 = $("#BUILDINGSECTION1").val();
		var backoutcause = $("#BACKOUTCAUSE").val();
		var canceltime = $("#CANCELTIME").val();
		 pram = "&CANCELTIME="+canceltime+"&BACKOUTCAUSE="+backoutcause+"&BUILDINGSECTION="+buildingsection1+"&PUBLIC_ATTR_LIST="+publicAttrInfo.toString()+"&ATTACH_LIST="+attachInfos;
	}
	$.beginPageLoading("正在重派...");
   	ajaxSubmit('yinchangquyu','checkinWorkSheet',pram,'',function(data){
   		$.endPageLoading();
   		MessageBox.success("提交", "操作成功！",function(){
   			closeNav();
   		});
   	},function(error_code,error_info,derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
   	});
}	



function saveAttach()
{
	//附件
	var attachList = new Wade.DatasetList($("#ATTACH_FILE_LIST").text());
	for(var i = 0, size = attachList.length; i < size; i++)
	{
		attachList.get(i).put("REMARK", $("#ATTACH_REMARK").val()); 
	}
	return attachList;
}

function contractAttach()
{
	debugger;
	var attachList = new Wade.DatasetList();
	//合同附件
	var contractFile = new Wade.DataMap($("#C_FILE_LIST").text());
	if(contractFile.items!=""){
		attachList.add(contractFile);
	}
	return attachList;
}


function changeDefaultOp(obj,tp){
	if("1"==tp){
		 $("#paidanxinxi").css("display","");
		 $("#openDefaultOp").css("display","none");
		 $("#hideDefaultOp").css("display","");
	}
	if("2"==tp){
		 $("#paidanxinxi").css("display","none");
		 $("#openDefaultOp").css("display","");
		 $("#hideDefaultOp").css("display","none");
	}
	
}

function submitOfferCha(){	
debugger;
		var chaSpecObjs = $("#offerChaPopupItem input");
	
		var offerChaSpecDataset = new Wade.DatasetList();
		
		for(var i = 0, size = chaSpecObjs.length; i < size; i++)
		{
			var chaSpecCode = chaSpecObjs[i].id;
			var chaValue = "";
			if (!chaSpecCode)
			{
				if(chaSpecObjs[i].type != "checkbox" && chaSpecObjs[i].type != "radio") // 复选框 只有name没有id
				{
					continue;
				}
			}
			if(chaSpecObjs[i].type == "checkbox" ||  chaSpecObjs[i].type == "radio")
			{
				chaValue = chaSpecObjs[i].checked ? 1 : 0;
				chaSpecCode = chaSpecObjs[i].name;
			}
			else
			{
				chaValue = $("#"+chaSpecCode).val();
			}
			
			var offerChaSpecData = new Wade.DataMap();
			offerChaSpecData.put("ATTR_VALUE", chaValue);
			offerChaSpecData.put("ATTR_NAME", chaSpecObjs[i].getAttribute("desc"));
			offerChaSpecData.put("ATTR_CODE", chaSpecCode);
			
			offerChaSpecDataset.add(offerChaSpecData);
			
		}
		return offerChaSpecDataset;
}

function checkCityA(){
	debugger;
	var serviceType = $("#pattr_SERVICETYPE").val(); 
	if(serviceType ==""){
		MessageBox.alert("提示", "请先在业务区域选择专线类型！");
		return false;
	}else if(serviceType!=""){
		if (serviceType == "0" && $("#pattr_CITYZ").val() != ""	&& $("#pattr_CITYA").val() != $("#pattr_CITYZ").val()) {
			$("#pattr_CITYA").val("");
			MessageBox.alert("错误", "专线类型为本地市传输专线，城市A和城市Z必须为同一地市，请修改！");
			return false;
		}
		if (serviceType == "1" && $("#pattr_CITYZ").val() != ""	&& $("#pattr_CITYA").val() == $("#pattr_CITYZ").val()) {
			$("#pattr_CITYA").val("");
			MessageBox.alert("错误", "专线类型为跨地市传输专线，城市A和城市Z为不同地市，请修改！");
			return false;
		}
	}
	changeAreaAByCityA();
}

function checkCityZ(){
	debugger;
	var serviceType = $("#pattr_SERVICETYPE").val(); 
	if(serviceType ==""){
		MessageBox.alert("提示", "请先在业务区域选择专线类型！");
		return false;
	}else if(serviceType!=""){
		if (serviceType == "0" && $("#pattr_CITYA").val() != ""	&& $("#pattr_CITYA").val() != $("#pattr_CITYZ").val()) {
			$("#pattr_CITYZ").val("");
			MessageBox.alert("错误", "专线类型为本地市传输专线，城市A和城市Z必须为同一地市，请修改！");
			return false;
		}
		if (serviceType == "1" && $("#pattr_CITYA").val() != ""	&& $("#pattr_CITYA").val() == $("#pattr_CITYZ").val()) {
			$("#pattr_CITYZ").val("");
			MessageBox.alert("错误", "专线类型为跨地市传输专线，城市A和城市Z为不同地市，请修改！");
			return false;
		}
	}
	changeAreaZByCityZ();
}

function changeAreaAByCityA()
{
	debugger;
	var cityArr = new Array('白沙','保亭','昌江','乐东','陵水','澄迈','儋州','定安','东方','临高','琼海','琼中','三亚','屯昌','万宁','文昌','五指山','三沙');
	var groupArr = new Array(new Array(), new Array());
	groupArr[0] = new Array('美兰区','龙华区','琼山区','秀英区');
	groupArr[1] = new Array('白沙黎族自治县','保亭黎族苗族自治县','昌江黎族自治县','乐东黎族自治县','陵水黎族自治县','澄迈县','儋州市','定安县','东方市','临高县','琼海市','琼中黎族苗族自治县','三亚市','屯昌县','万宁市','文昌市','五指山市','三沙市');
	groupArr[2] = new Array('天涯区','吉阳区','海棠区','崖州区');
	var cityField = document.getElementById("pam_CITYA");
	var cityField = document.getElementById("pam_CITYA");
	if (cityField.value == '海口')	{
		pam_AREAA.empty();
		for (var i = 0; i < groupArr[0].length; i++){
			pam_AREAA.append(groupArr[0][i],groupArr[0][i]);
		}
	} else if (cityField.value == '三亚'){
		pam_AREAA.empty();
		for (var i = 0; i < groupArr[0].length; i++)
			pam_AREAA.append(groupArr[2][i], groupArr[2][i]);
	}
	else	{
		pam_AREAA.empty();
		for (var i = 0; i < cityArr.length; i++) {
			if (cityField.value == cityArr[i]) {
				pam_AREAA.append(groupArr[1][i], groupArr[1][i]);
				break;
			}
		}
	}
};

function changeAreaZByCityZ()
{
	debugger;
	var cityArr = new Array('白沙','保亭','昌江','乐东','陵水','澄迈','儋州','定安','东方','临高','琼海','琼中','三亚','屯昌','万宁','文昌','五指山','三沙');
	var groupArr = new Array(new Array(), new Array());
	groupArr[0] = new Array('美兰区','龙华区','琼山区','秀英区');
	groupArr[1] = new Array('白沙黎族自治县','保亭黎族苗族自治县','昌江黎族自治县','乐东黎族自治县','陵水黎族自治县','澄迈县','儋州市','定安县','东方市','临高县','琼海市','琼中黎族苗族自治县','三亚市','屯昌县','万宁市','文昌市','五指山市','三沙市');
	groupArr[2] = new Array('天涯区','吉阳区','海棠区','崖州区');
	var cityField = document.getElementById("pam_CITYZ");
	if (cityField.value == '海口')	{
		pam_AREAZ.empty();
		for (var i = 0; i < groupArr[0].length; i++){
			pattr_AREAZ.append(groupArr[0][i],groupArr[0][i]);
		}
	} else if (cityField.value == '三亚'){
		pam_AREAZ.empty();
		for (var i = 0; i < groupArr[0].length; i++)
			pam_AREAZ.append(groupArr[2][i], groupArr[2][i]);
	}
	else	{
		pam_AREAZ.empty();
		for (var i = 0; i < cityArr.length; i++) {
			if (cityField.value == cityArr[i]) {
				pam_AREAZ.append(groupArr[1][i], groupArr[1][i]);
				break;
			}
		}
	}
};

//业务保障等级说明
function showExplian(){
	showPopup("popup06", "queryshowExplianPopup", true);
}