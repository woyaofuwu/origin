function refreshPartAtferAuth(data)
{
	$.ajax.submit('AuthPart', 'getCustInfo', "&CUST_INFO="+data.get("CUST_INFO").toString(), 'custInfoPart', 
	function(data){
//		$("#CSSUBMIT_BUTTON").attr("disabled", true);
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}

function checkRealName(){	
	var psptTypeCode = $("#custInfo_PSPT_TYPE_CODE").val();
	
	$("#CHECK_FAIL").attr("disabled", false);
	$("#CHECK_SUCCEED").attr("disabled", false);
    $("#print").attr("disabled", false);

	var bossid = $("#custInfo_BOSS_ID").val(); 
	
    var custName = $("#custInfo_CUST_NAME").val(); 
	var custPSPT = $("#custInfo_PSPT_ID").val(); 
	
	var serialnumber = $("#AUTH_SERIAL_NUMBER").val();
	
	if(custPSPT==null || custPSPT=="")
	{	
			alert("证件号码不能为空！");
			return false;
	}
	if(bossid==null || bossid=="")
	{	
			alert("BOSS流水号不能为空！");
			return false;
	}
	if(psptTypeCode==null || psptTypeCode=="")
	{	
			alert("身份证类型不能为空！");
			return false;
	}
	else if(custName==null || custName=="")
	{
		alert("客户名称不能为空！");
		return false;
	}
	if(psptTypeCode=='0'||psptTypeCode=='1')
	{
		
		$.beginPageLoading();
		//ajaxDirect(this,'checkRealName','&custinfo_CUST_NAME='+custName+'&custinfo_PSPT_ID='+custPSPT+'&custinfo_BOSS_ID='+bossid+'&custinfo_PSPT_TYPE_CODE='+psptTypeCode+'&SERIAL_NUMBER='+serialnumber,'checkrealname');
		$.ajax.submit('custInfoPart,AuthPart', 'checkRealName', '&custinfo_CUST_NAME='+custName+'&custinfo_PSPT_ID='+custPSPT+'&custinfo_BOSS_ID='+bossid+'&custinfo_PSPT_TYPE_CODE='+psptTypeCode+'&SERIAL_NUMBER='+serialnumber, 'checkrealname', 
		function(data){
			$.endPageLoading();
			/*
			if(data.get('ALERT_INFO') != '')
			{
				MessageBox.alert("提示",data.get('ALERT_INFO'));
				$.endPageLoading();
			}
			*/
			$("#CHECK_FAIL").attr("disabled",false);
    		$("#CHECK_SUCCEED").attr("disabled",false);
    		$("#print").attr("disabled",false);
		},
		function(error_code,error_info){
			$.endPageLoading();
			alert(error_info);
   		 });
	}
	else
	{
		MessageBox.alert("提示信息：","你的证件类型不是身份证，请先做资料变更再办理本业务！");
		return false;
	}
}

function checkRealNameSuccess (){
	alert('实名验证成功！');
	$("#CSSUBMIT_BUTTON").attr("disabled", false);
	$("#cancel").attr("disabled", false);
}


function checkRealNameFail(){
   	alert('实名验证失败！');
   	
	$("#CSSUBMIT_BUTTON").attr("disabled", true);
	var psptTypeCode = $("#custInfo_PSPT_TYPE_CODE").val();
	var bossid = $("#custInfo_BOSS_ID").val();
	var custName = $("#custInfo_CUST_NAME").val();
	var custPSPT =  $("#custInfo_PSPT_ID").val();
	var serialnumber = $("#AUTH_SERIAL_NUMBER").val();
	var rspcode = $("#custInfo_RSP_CODE").val();
    
    //alert("psptTypeCode:"+psptTypeCode +" bossid:"+bossid+" custName:"+custName+" custPSPT:"+custPSPT+" serialnumber:"+serialnumber
    //+" rspcode:"+rspcode);
	if(custPSPT==null || custPSPT=="")
	{	
			alert("证件号码不能为空！");
			return false;
	}
	if(bossid==null || bossid=="")
	{	
			alert("BOSS流水号不能为空！");
			return false;
	}
	if(psptTypeCode==null || psptTypeCode=="")
	{	
			alert("身份证类型不能为空！");
			return false;
	}
	else if(custName==null || custName=="")
	{
		alert("客户名称不能为空！");
		return false;
	}
	
	//ajaxDirect(this,'checkRealNameFail','&custinfo_CUST_NAME='+custName+'&custinfo_PSPT_ID='+custPSPT+'&custinfo_BOSS_ID='+bossid+'&custinfo_PSPT_TYPE_CODE='+psptTypeCode+'&custInfo_RSP_CODE='+rspcode+'&SERIAL_NUMBER='+serialnumber,'checkrealname');
	$.beginPageLoading();
	$.ajax.submit('custInfoPart,AuthPart', 'checkRealNameFail', '&custinfo_CUST_NAME='+custName+'&custinfo_PSPT_ID='+custPSPT
						+'&custinfo_BOSS_ID='+bossid+'&custinfo_PSPT_TYPE_CODE='+psptTypeCode+'&custInfo_RSP_CODE='+rspcode+
								'&SERIAL_NUMBER='+serialnumber, 'checkrealname', 
		function(data){
			$.endPageLoading();
			/*
			if(data.get('ALERT_INFO') != '')
			{
				MessageBox.alert("提示",data.get('ALERT_INFO'));
				$.endPageLoading();
			}
			*/
		},
		function(error_code,error_info){
			$.endPageLoading();
			alert(error_info);
   		 });	     
}

function checkBeforeSubmit(){
 	//alert("in checkBeforeSubmit");
 	if(!$.validate.verifyAll("acceptPart")) {
		return false;
	}
	if(!$.validate.verifyAll("AuthPart")) {
		return false;
	}
	return true;
}

//实名认证打印
function printContext(){

	var psptTypeCode = $("#custInfo_PSPT_TYPE_CODE_NAME").val();
	var bossid = $("#custInfo_BOSS_ID").val();
	
    var custName = $("#custInfo_CUST_NAME").val();
	var custPSPT = $("#custInfo_PSPT_ID").val();
	var psptAddr = $("#custInfo_PSPT_ADDR").val();
	var serialnumber = $("#AUTH_SERIAL_NUMBER").val();
	//图片URL需要拼全路径。
	var url = $("#printContext").val();
	/*var href = window.location.href;
	var index = href.lastIndexOf("/?");
	if(!index){
		index = href.lastIndexOf("/");
	}
	href = href.substring(0, index);
	url = href + "/" + url;
	*/
	var rspcode = $("#custInfo_RSP_CODE_NAME").val();
	var depart = $("#custInfo_IDCARD_DEPARTMENT").val();
	var updateStaff = $("#custInfo_UPDATE_STAFF_ID").val();
	var contactPhone = $("#custInfo_CONTACT_PHONE").val();
	
	var data = new $.DataMap();
	data.put("TEMP_PATH","common/plat/createwalletcard/userProfileInfo.html?template_code=CREATE_WALLET_CARD_00001&version=009");
	data.put("TEMP_TYPE","HTML");
	data.put("CUST_NAME",custName);
	data.put("SERIAL_NUMBER",serialnumber);
	data.put("PSPT_TYPE_CODE",psptTypeCode);
	data.put("PSPT_ID",custPSPT);
	data.put("PSPT_ADR",psptAddr);
	data.put("SERIAL_NUMBER1",contactPhone);
	data.put("TRADE_TYPE","手机支付实名验证");
	data.put("BOSS_ID",bossid);
	data.put("RSP_CODE",rspcode);
	data.put("IDCARD_DEPARTMENT",depart);
	data.put("UPDATE_STAFF_ID",updateStaff);
	data.put("URL",url);
	$.printMgr.startupPrint(data);
//	runPrint(data);
}

function runPrint(data){
	var href = window.location.href;
	var index = href.lastIndexOf("/?");
	if(!index){
		index = href.lastIndexOf("/");
	}
	href = href.substring(0, index);
	
	data.put("TEMP_PATH", href+data.get("TEMP_PATH"));
	var printData = new $.DatasetList();
	printData.add(data);
	var ocx = new ActiveXObject("Wade3Printer.Printer");
	if(ocx!=null){
		ocx.DoPrint(printData.toString());
	}else{
		alert('打印机组件不存在！');
	}
}
