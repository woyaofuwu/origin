//认证后的刷新
function refreshPartAtferAuth(data)
{
	var eparchyCode = data.get("USER_INFO").get("EPARCHY_CODE");
	var userId = data.get("USER_INFO").get("USER_ID");
	var serialNumber = data.get("USER_INFO").get("SERIAL_NUMBER");
	var userProductId = data.get("USER_INFO").get("PRODUCT_ID");

	$("#EPARCHY_CODE").val(eparchyCode);
	$("#USER_EPARCHY_CODE").val(eparchyCode);
	$("#USER_ID").val(userId);
	$("#INFO_USER_ID").val(userId);
	$("#SERIAL_NUMBER").val(serialNumber);
	$("#USER_PRODUCT_ID").val(userProductId);
	$("#OLD_PROD_ID").val(userProductId);
	
	var custName = data.get("CUST_INFO").get("CUST_NAME");//作用未知
	var openDate = data.get("USER_INFO").get("OPEN_DATE");//作用未知
	$("#CUST_NAME").val(custName);//UCAViewPart1@Part中的值  作用未知
	$("#OPEN_DATE").val(openDate);//UCAViewPart1@Part中的值  作用未知
	
	$("#NEW_PRODUCT_ID").val("");
	$("#NEXT_PRODUCT_ID").val("");
	$("#NEW_PRODUCT_START_DATE").val("");
	$("#OLD_PRODUCT_END_DATE").val("");
	
	var param = "&ROUTE_EPARCHY_CODE="+eparchyCode
			  + "&USER_ID="+userId
	 		  + "&SERIAL_NUMBER="+serialNumber;

    $.beginPageLoading("宽带资料查询。。。");
	$.ajax.submit('', 'loadChildInfo', param, 'wideInfoPart,newWideInfoPart,productModePart,productTypePart,ModelModePart,userProdInfoShow', function(data)
	{
		$("#WIDENETMOVE_FIRST").val(data.get(0).get("WIDENETMOVE_FIRST"));
		$.endPageLoading();
	},
	function(error_code, error_info,detail) { 
    $.endPageLoading();
    MessageBox.error("错误提示", error_info, $.auth.reflushPage, null, null, detail);
});
}

//tab页呼唤
function myTabSwitchAction(v, v2) {
	var objTabset = $.tabset("mytab");
	var title = objTabset.getCurrentTitle();// 获取当前标签页标题
	return true;
}

//光猫模式选择
function selectModelMode(){
	$.feeMgr.clearFeeList($("#TRADE_TYPE_CODE").val());//清除费用
	$.feeMgr.removeFee($("#TRADE_TYPE_CODE").val(),"2","9002");//清除费用
	$("#MODEM_DEPOSIT").val("0");
	var modelMode = $("#MODEL_MODE").val();

	if(modelMode==2 || modelMode==3){//赠送和自备
		$.feeMgr.clearFeeList($("#TRADE_TYPE_CODE").val());
	}else if(modelMode==1){//购买（已废弃）
		alert("您选择的是购买光猫，需要缴纳费用300元！");
		var feeData = $.DataMap();
		feeData.clear();
		feeData.put("MODE", "0");
		feeData.put("CODE", "9205");
		feeData.put("FEE", "30000");
		feeData.put("PAY", "30000");
		feeData.put("TRADE_TYPE_CODE", $("#TRADE_TYPE_CODE").val());
		$.feeMgr.insertFee(feeData);
	}else if(modelMode==0){//租赁
			var serialNumber = $("#SERIAL_NUMBER").val();
			var eparchyCode = $("#EPARCHY_CODE").val()
			var firstReng = $("#FIRST_RENT").val();
			var isExchangeModel = $("#IS_EXCHANGE_MODEL").val();
   			var moveFtthMoney = $("#MOVE_FTTH_MONEY").val();
			
			var param = "&SERIAL_NUMBER="+serialNumber+"&ROUTE_EPARCHY_CODE="+eparchyCode
					  + "&FIRST_RENT="+firstReng+"&IS_EXCHANGE_MODEL="+isExchangeModel+ "&MOVE_FTTH_MONEY="+moveFtthMoney;
					  
			$.beginPageLoading("光猫。。。");
			$.ajax.submit('', 'dealModelMoney', param, '', function(data){
				$.endPageLoading();
				var modelNotReturn = data.get(0).get("MODEL_NOT_RETURN");
				if(parseInt(modelNotReturn)>0){
					alert("您有租借的尚未退还的光猫，请先退还或办理丢失之后，再办理光猫租借！");
					$("#MODEM_DEPOSIT").val("0");
					$("#MODEL_MODE").val("");
				}else{
					var modelShowInfo = data.get(0).get("MODEL_SHOW_INFO");
					alert(modelShowInfo);
					var modelDeposit = data.get(0).get("MODEM_DEPOSIT");
					$("#MODEM_DEPOSIT").val(parseInt(modelDeposit)/100);
					
					//加载费用列表
					$.feeMgr.removeFee($("#TRADE_TYPE_CODE").val(),"2","9002");
					var feeData = $.DataMap();
					feeData.clear();
					feeData.put("MODE", "2");
					feeData.put("CODE", "9002");
					feeData.put("FEE", modelDeposit);
					feeData.put("PAY", modelDeposit);
					feeData.put("TRADE_TYPE_CODE", $("#TRADE_TYPE_CODE").val());
					$.feeMgr.insertFee(feeData);
				}
			},
			function(error_code,error_info){
					$.endPageLoading();
					$.MessageBox.error(error_code,error_info);
		    });
	}
}

//业务提交
function onTradeSubmitNew(){
	if(!verifyAll('newWideInfoPart'))
   	{
		return false;
   	}

   	if(""==$("#STAND_ADDRESS").val()){
   		alert("请选择标准地址后再提交！");
   		return false;
   	}

   	if("1"==$("#IS_NEED_CHG_PROD").val())
   	{
     	if($("#IS_CHG_PROD").val()!="TRUE"){
      		alert("请选择您要办理的新产品！");
      		return false;
     	}
   	}
   	
   	var modelMode = $("#MODEL_MODE").val();
   	if("1"==$("#IS_NEED_MODEL").val()&&(modelMode==""))
   	{
   		var isNeedChange = $("#IS_EXCHANGE_MODEL").val();
   		if(isNeedChange=="0"||isNeedChange=="4"){
   			alert("您的宽带类型已经发生变化，当前的光猫在新地址下无法使用。请选择新的光猫");
   			return false;
   		}
   		if(isNeedChange=="3"){
   			alert("您的新装宽带地址需要光猫，请选择您的光猫");
   			return false;
   		}
   		
   		var wideModelDeal=confirm("您还没有选择需要的光猫，点击【OK】继续提交业务，点击【Cancel】或者\"关闭提示框\"返回页面选择光猫！");
		if(!wideModelDeal){
			return false;
		}
   	}
   	
   	var oldWideType=$('#OLD_WIDE_TYPE').val();
	var newWideType=$('#NEW_WIDE_TYPE').val();
	//FTTH移机成FTTB
	if(oldWideType=='3'&&(newWideType=='1'||newWideType=='6')){
		alert('如有押金光猫无清退，移机后请到无手机光猫管理界面清退押金并收回光猫');
	}
   
   	var param = "&NEW_STAND_ADDRESS="+$("#STAND_ADDRESS").val()
   			  + "&NEW_AREA_CODE="+$("#AREA_CODE").val()
   			  + "&NEW_DETAIL_ADDRESS="+$("#DETAIL_ADDRESS").val()
   			  + "&RSRV_STR2="+$("#RSRV_STR2").val()
   			  + "&BOOKING_DATE="+$("#FIRST_DAY_NEXT_MONTH").val();
			
	var data = selectedElements.getSubmitData();
	if(data&&data.length>0){
		param += "&SELECTED_ELEMENTS="+data.toString();
		if($("#EFFECT_NOW").attr("checked")){
			param+="&EFFECT_NOW=1";
		}
	}
	
   	$.cssubmit.addParam(param);
   	return true;
}