function refreshPartAtferAuth(data)
{
	$.beginPageLoading();
	$.ajax.submit('AuthPart', 'loadChildInfo', "&USER_INFO="+data.get("USER_INFO").toString()+"&CUST_INFO="+data.get("CUST_INFO").toString(), 'phoneInfoPart', function(data){
		$.endPageLoading();
		//alert(data.toString());
		$("#OLD_SIM_CARD_NO").val(data.get(0).get("OLD_SIM_CARD_NO"));
		$("#OLD_IMSI").val(data.get(0).get("OLD_IMSI"));
		$("#PHONE_CHECK").val("1");
		$("#SIM_CARD_CHECK").val("1");
		$("#changeSn").attr("style","display:");
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
	});
}

//显示查询框
function showLayer(optionID) {
	$('#'+optionID).css("display","block");
}
//隐藏查询框
function hideLayer(optionID) {
	$('#'+optionID).css("display","none");
}

/*
查询网上选号号码
 */
function queryNetchoosePhone()
{
	var psptId = $("#NETCHOOSE_PSPT_ID").val();
	var chooseType="";
	if(psptId==""||(psptId.length!=15&&psptId.length!=18))
	{
		alert("证件号码为空或者不符合长度！");
		return false;
	}

	if($("input[name='NETCHOOSE_TYPE']:checked").val()=='1')
		chooseType="1";
	else
		chooseType="3";
	$.ajax.submit('AuthPart,NetChooseInputPart','queryNetChoosePhone','USER_ID='+$.auth.getAuthData().get('USER_INFO').get('USER_ID'),'netchoosePart',function(data){ 
		$.endPageLoading();
	},function(errorcode,errorinfo){
		alert(errorinfo);
		$.endPageLoading();
	});
}

//点击确定设置新开号码
function setNewPhone() {
	var allData = $.table.get("choseTable").getTableData(null,true);
	if(allData.length<1){
		alert("请选择一个号码")
		return false;
	}

	var rowData = $.table.get("choseTable").getRowData();
	$("#NEW_SERIAL_NUMBER").val(rowData.get("SERIAL_NUMBER"));
	hideLayer("serchPhone");
	/*由资源自动释放，不需要手动释放
	 * $.beginPageLoading();
	$.ajax.submit('AuthPart,phoneInfoPart','releaseNetChoosePhone',null,null,function(data){ 
		$.endPageLoading();
	},function(errorcode,errorinfo){
		alert(errorinfo);
		$.endPageLoading();
	});*/
	checkResource(0);
}


//校验号码的准确性
function checkResource(hTag){
	if(!$.validate.verifyAll("NEW_SERIAL_NUMBER")) {
		return false;
	}
	if(hTag == 0){
		$("#PHONE_CHECK").val("1");
		$.feeMgr.clearFeeList("143","2");
		var param = "&hTag="+hTag+'&USER_ID='+$.auth.getAuthData().get('USER_INFO').get('USER_ID');
		$.beginPageLoading();
		$.ajax.submit('AuthPart,phoneInfoPart','verifyResourse',param,'',function(data){ //verifyResourse
			$.endPageLoading();
			//alert(data.toString());
			if(data.get(0).get("FEE") != "0"){
				var obj = new Wade.DataMap();
				obj.put("TRADE_TYPE_CODE", "143");
				obj.put("MODE", "2"); 
				obj.put("CODE", "62"); 
				obj.put("FEE",  data.get(0).get("FEE"));  
				$.feeMgr.insertFee(obj);
			}
			//号码校验通过
			$("#PHONE_CHECK").val("0");

		},function(errorcode,errorinfo){
			alert(errorinfo);
			$.endPageLoading();
		});
	}else if(hTag == 1){
		$("#SIM_CARD_CHECK").val("1");
		$.feeMgr.clearFeeList("143","0");
		var param = "&hTag="+hTag+'&USER_ID='+$.auth.getAuthData().get('USER_INFO').get('USER_ID');
		$.beginPageLoading();
		$.ajax.submit('AuthPart,phoneInfoPart','verifyResourse',param,'',function(data){ 
			$.endPageLoading();
			//addFee
			var feeInfo = data.get(0).get("FEE_DATA").get("FEE_INFO");
			if(feeInfo != ""){
				$("#ALERT_INFO").text(data.get(0).get("FEE_DATA").get("FEE_INFO"));
				$("#ALERT_INFO_DIV").css("display","");
			}
			if(data.get(0).get("FEE_DATA").get("FEE_TAG")=='1'){
				var obj = new Wade.DataMap();
				obj.put("TRADE_TYPE_CODE", "143");
				obj.put("MODE", "0"); 
				obj.put("CODE", "10"); 
				obj.put("FEE", data.get(0).get("FEE_DATA").get("FEE"));  
				$.feeMgr.insertFee(obj);
			}
			$("#NEW_IMSI").val(data.get(0).get("RES_INFO").get("IMSI"));
			$("#SIM_CARD_CHECK").val("0");

		},function(errorcode,errorinfo){
			alert(errorinfo);
			$.endPageLoading();
		});
	}
}

function beforeSubmit(){
	if(!$.validate.verifyAll("phoneInfoPart")) {
		return false;
	}
	//alert($("#PHONE_CHECK").val()+" "+$("#SIM_CARD_CHECK").val());
	var phoneCheck = $("#PHONE_CHECK").val();
	var errorMsg = "";
	if(phoneCheck != "0"){
		errorMsg += "新手机号码未校验！";
	}
	var simCardCheck = $("#SIM_CARD_CHECK").val();
	if(simCardCheck != "0"){
		errorMsg += "新SIM卡号码未校验！";
	}
	if(errorMsg !=""){
		alert(errorMsg);
		return false;
	}
	return true;
}
