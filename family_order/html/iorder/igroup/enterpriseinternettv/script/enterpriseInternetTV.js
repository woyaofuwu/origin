var flg ;//定义是否进行了终端编码校验
function queryGroupUser(){

	var serialNumber = $("#cond_SERIAL_NUMBER").val();
	if(""==serialNumber || undefined ==serialNumber){
		$.validate.alerter.one($("#cond_SERIAL_NUMBER")[0],"集团产品编码必须填写，请填写后再进行查询！");
		return false;
	}
	$.beginPageLoading("查询中，请稍后...");
	$.ajax.submit('', 'qryGrpUser','&SERIAL_NUMBER='+serialNumber,"infoListPart", function(data){
		$.endPageLoading();
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
	
}

function chooseinfoInfo(obj){
	debugger;
	var accessNum = $(obj).attr("SERIAL_NUMBER_B");
	var userIdA = $(obj).attr("USER_ID_A");
	var  catflag = $(obj).attr("CATFLAG");
	if(catflag=="1"){
		$.validate.alerter.one($("#SERIAL_NUMBER_B")[0],"机顶盒已被申领，无法选择，请选择未申领的机顶盒！");
		return false;
	}
	$("#RES_ID").val(accessNum);
	$("#cond_USER_ID_A").val(userIdA);
	 backPopup(obj);
}

function checkTerminal(){
	var resId = $("#RES_ID").val();
	var serialNumber = $("#cond_SERIAL_NUMBER").val();
	if(""==resId || undefined ==resId){
		$.validate.alerter.one($("#RES_ID")[0],"终端编码不能为空，请点击左侧进行机顶盒查询后获取终端编码！");
		return false;
	}
	$.beginPageLoading("终端校验中......");
	$.ajax.submit('', 'checkTerminal','&SERIAL_NUMBER='+serialNumber+'&RES_ID='+resId,"terminalPart", function(data){
		flg = "0";
		$.endPageLoading();
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
}

function addItem() {
	/* 校验所有的输入框 */ 	
	
	var kdNumber = $("#KD_NUMBER").val();
	var custId=$("#CUST_ID").val();
	if(flg!="0"){
		$.validate.alerter.one($("#RES_ID")[0],"终端编码未进行校验，请校验后再进行宽带帐号新增！");
		return false;
	}
	if(""==kdNumber || undefined ==kdNumber){
		$.validate.alerter.one($("#KD_NUMBER")[0],"宽带账号不能为空，请重新输入！");
		return false;
	}
	if(kdNumber.length < 11){
		$.validate.alerter.one($("#KD_NUMBER")[0],"宽带账号必须是大于11位数字，请重新输入！");
		return false; 
	}
	
	if(kdNumber.indexOf("KD_")>-1) {//宽带账号
	     if(kdNumber.split("_")[1].length < 11)
	     {
	    	$.validate.alerter.one($("#KD_NUMBER")[0],"宽带账号尾数必须是大于11位数字，请重新输入！");
	 		return false; 
	     }
	 }
	
	if(kdNumber.length > 11 && kdNumber.indexOf("KD_") < 0){
		kdNumber = "KD_"+kdNumber;
	}
	
	$.beginPageLoading("宽带号码校验中......");
	$.ajax.submit('', 'qryUserWidenet','&KD_NUMBER='+kdNumber+'&CUSTID_GROUP='+custId,"itemPart", function(data){
		var  result=data.get("RTNCODE");
		if(result=='8'){
			$.MessageBox.error("错误提示", "查询不到宽带号码信息!");
		}
		$.endPageLoading();
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });	
}


function onSubmitBaseTradeCheck(){
	debugger;
	var serialNum=$("#cond_SERIAL_NUMBER").val();
	var userIdA = $("#cond_USER_ID_A").val();
	var kdUserId = $("#KD_USERID").val();
	if(""==kdUserId || undefined ==kdUserId){
		$.validate.alerter.one($("#KD_NUMBER")[0],"宽带账号未进行新增校验，请新增校验后再进行提交！");
 		return false; 
	}
	var submitInfo = $.DatasetList();
	var submitData = new Wade.DataMap();//指令类型
	submitData.put("KD_ADDR", $("#KD_ADDR").val());
	submitData.put("KD_ARTIFICIAL_SERVICES", "1");
	submitData.put("KD_RES_STATE_NAME", $("#RES_STATE_NAME").val());
	submitData.put("KD_USERID",kdUserId);
	submitData.put("KD_RES_SUPPLY_COOPID", $("#RES_SUPPLY_COOPID").val());
	submitData.put("KD_REMARK", $("#REMARK").val());
	submitData.put("KD_PHONE", $("#KD_PHONE").val());
	submitData.put("KD_RES_ID", $("#RES_ID").val());
	submitData.put("KD_RES_KIND_CODE", $("#RES_KIND_CODE").val());
	submitData.put("KD_RES_KIND_NAME", $("#RES_KIND_NAME").val());
	submitData.put("KD_RES_STATE_CODE",$("#RES_STATE_CODE").val());
	submitData.put("KD_NUMBER",  $("#KD_NUMBER").val());
	submitData.put("KD_RES_TYPE_CODE", $("#RES_TYPE_CODE").val());
	submitData.put("KD_RES_BRAND_CODE", $("#RES_BRAND_CODE").val());
	submitData.put("KD_DEVICE_COST", $("#DEVICE_COST").val());
	submitData.put("KD_RES_FEE", $("#RES_FEE").val());
	submitData.put("CUST_NAME", $("#CUST_NAME").val());
	submitData.put("KD_RES_BRAND_NAME", $("#RES_BRAND_NAME").val());
	submitInfo.add(submitData);
	
//	$.beginPageLoading("业务受理中......");
//	$.ajax.submit('', 'onSubmitBaseTrade','&SERIAL_NUMBER='+serialNum+'&FTTH_DATASET='+submitInfo.toString()+'&USER_ID_A='+userIdA,null, function(data){
//		$.endPageLoading();
//		
//	},
//	function(error_code,error_info,derror){
//		$.endPageLoading();
//		showDetailErrorInfo(error_code,error_info,derror);
//    });	

	var param = '&SERIAL_NUMBER='+serialNum+'&FTTH_DATASET='+submitInfo.toString()+'&USER_ID_A='+userIdA;
	$.cssubmit.addParam(param); 
	
	return true;
}