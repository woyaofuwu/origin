
function refreshPartAtferAuth(data)
{
	var param = "&ROUTE_EPARCHY_CODE="+data.get("USER_INFO").get("EPARCHY_CODE")+"&USER_ID="+data.get("USER_INFO").get("USER_ID")
	 +"&TRADE_TYPE_CODE="+$("#TRADE_TYPE_CODE").val()+"&WIDE_TYPE="+$("#WIDE_TYPE").val()
	 +"&SERIAL_NUMBER="+data.get("USER_INFO").get("SERIAL_NUMBER");
	document.getElementById("EPARCHY_CODE").value = data.get("USER_INFO").get("EPARCHY_CODE");
	document.getElementById("INFO_USER_ID").value = data.get("USER_INFO").get("USER_ID");
	document.getElementById("OLD_PROD_ID").value = data.get("USER_INFO").get("PRODUCT_ID");
	
	var userProductId = data.get("USER_INFO").get("PRODUCT_ID");
	var userId = data.get("USER_INFO").get("USER_ID");
	var serialNumber = data.get("USER_INFO").get("SERIAL_NUMBER");
	var eparchyCode = data.get("USER_INFO").get("EPARCHY_CODE");
	var openDate = data.get("USER_INFO").get("OPEN_DATE");
	var custName = data.get("CUST_INFO").get("CUST_NAME");
	
	$("#CUST_NAME").val(custName);
	$("#OPEN_DATE").val(openDate);
	$("#USER_PRODUCT_ID").val(userProductId);
	$("#USER_ID").val(userId);
	$("#SERIAL_NUMBER").val(serialNumber);
	$("#USER_EPARCHY_CODE").val(eparchyCode);
	$("#NEW_PRODUCT_ID").val("");
	$("#NEXT_PRODUCT_ID").val("");
	$("#NEW_PRODUCT_START_DATE").val("");
	$("#OLD_PRODUCT_END_DATE").val("");
	
    $.beginPageLoading("宽带资料查询。。。");
	$.ajax.submit('', 'loadChildInfo', param, 'wideInfoPart,newWideInfoPart,productModePart,productTypePart,ModelModePart,saleActiveList,saleActiveDesc,userProdInfoShwo', function(data)
	{
		$("#WIDENETMOVE_FIRST").val(data.get(0).get("WIDENETMOVE_FIRST"));
		$("#IS_BUSINESS_WIDE").val(data.get(0).get("IS_BUSINESS_WIDE"));
		//add by danglt
		$("#USER_SALEACTIVE_CYCLE").val(data.get(0).get("USER_SALE_CYCLE"));
		$("#USER_SALEACTIVE_FEE").val(data.get(0).get("USER_SALE_FEE"));
		$("#USER_WIDE_RATE").val(data.get(0).get("USER_WIDE_RATE"));
		$.endPageLoading();
	},
	function(error_code, error_info,detail) { 
    $.endPageLoading();
    MessageBox.error("错误提示", error_info, $.auth.reflushPage, null, null, detail);
});
}

function checkSerialNumber(){
   var preWideType = $("#PREWIDE_TYPE").val();
   var serialNumberA = $("#SERIAL_NUMBER_A").val();

   if(preWideType==null||preWideType==""){
     MessageBox.alert("提示","请先选择账号类型");
     $("#SERIAL_NUMBER_A").val("");
     return false;
   }
   if(serialNumberA==null||serialNumberA==""){
   	  MessageBox.alert("提示","请输入主号码");
      return false;
   }
   if( $("#AUTH_SERIAL_NUMBER").val() == serialNumberA){
      MessageBox.alert("提示","移机号码不能与主号码一致!");
      $("#SERIAL_NUMBER_A").val("");
      return false;
   }
   var param = "&SERIAL_NUMBER_A="+serialNumberA +"&PREWIDE_TYPE="+preWideType+"&ROUTE_EPARCHY_CODE="+$.auth.getAuthData().get("USER_INFO").get("EPARCHY_CODE");
   $.beginPageLoading("主号码校验开始。。。");
   $.ajax.submit('', 'checkSerialNumber', param, 'newWideInfoPart', function(data){
            $("#GPON_USER_ID").val(data.get(0).get("GPON_USER_ID"));
            $("#GPON_SERIAL_NUMBER").val(data.get(0).get("GPON_SERIAL_NUMBER"));
            $("#USER_ID_A").val(data.get(0).get("USER_ID_A"));
		    $.endPageLoading();
	},
	function(error_code,error_info){
	         $("#SERIAL_NUMBER_A").val("");
			$.endPageLoading();
			$.MessageBox.error(error_code,error_info);
    });
}

//业务提交
function onTradeSubmitNew(){
	if(!verifyAll('newWideInfoPart'))
   	{
		return false;
   	}

   	if(""==$("#STAND_ADDRESS").val()){
   		MessageBox.alert("提示","请选择标准地址后再提交！");
   		return false;
   	}

   	if("1"==$("#IS_NEED_CHG_PROD").val())
   	{
     	if($("#IS_CHG_PROD").val()!="TRUE"){
      		MessageBox.alert("提示","请选择您要办理的新产品！");
      		return false;
     	}
   	}
   	
   	var saleActiveFee = 0;
   	if(null!=$("#SALE_ACTIVE_FEE").val()&&""!=$("#SALE_ACTIVE_FEE").val()){
   		saleActiveFee = parseInt($("#SALE_ACTIVE_FEE").val())/100;
   	}
	
   	var modelFee = 0;
   	if(null!=$("#MODEM_DEPOSIT").val()&&""!=$("#MODEM_DEPOSIT").val()){
   		modelFee = parseInt($("#MODEM_DEPOSIT").val());
   	}
   	
    //BUS201907310012关于开发家庭终端调测费的需求
   	var saleActiveFee2 = 0;
   	if(null!=$("#SALE_ACTIVE_FEE2").val()&&""!=$("#SALE_ACTIVE_FEE2").val()){
   		saleActiveFee2 = parseInt($("#SALE_ACTIVE_FEE2").val())/100;
   	}
   	
   	var totalFee = saleActiveFee + modelFee + saleActiveFee2;
   	//var totalFee = saleActiveFee + modelFee;
   	//BUS201907310012关于开发家庭终端调测费的需求

   	var param="&NEW_STAND_ADDRESS="+$("#STAND_ADDRESS").val()+"&NEW_AREA_CODE="+$("#AREA_CODE").val()
	+"&NEW_DETAIL_ADDRESS="+$("#DETAIL_ADDRESS").val()+"&RSRV_STR2="+$("#RSRV_STR2").val()
	+"&WIDE_TYPE="+$("#WIDE_TYPE").val();
	
    if("636" == $("#TRADE_TYPE_CODE").val()){
	 	param += "&GPON_USER_ID="+$("#GPON_USER_ID").val()+"&GPON_SERIAL_NUMBER="+$("#GPON_SERIAL_NUMBER").val()
		 +"&USER_ID_A="+$("#USER_ID_A").val();
	}
	
	/*var canSubmit = selectedElements.checkForcePackage();
	if(!canSubmit){
		return false;
	}*/
			
	var data = selectedElements.getSubmitData();
	if(data&&data.length>0){
		param += "&SELECTED_ELEMENTS="+data.toString();
		if($("#EFFECT_NOW").attr("checked")){
			param+="&EFFECT_NOW=1";
		}
	}

    $.beginPageLoading("提交前校验开始。。。");
    var params = param + "&DEVICE_ID=" + $("#DEVICE_ID").val() + "&SERIAL_NUMBER=" + $("#SERIAL_NUMBER").val() +
        "&NEW_PRODUCT_START_DATE=" + $("#NEW_PRODUCT_START_DATE").val() + "&NEW_WIDE_TYPE=" + $("#NEW_WIDE_TYPE").val();;
    var flag = false;
    $.ajax.submit(null, 'checkSPAMDiscntRequirement', params, '',
        function (ajaxData) {
            $.endPageLoading();
            var resultCode = ajaxData.get("RESULT_CODE");
            if(resultCode == '1'){
                flag = true;
            }
        },
        function (error_code, error_info) {
            $.MessageBox.error(error_code, error_info);
            $.endPageLoading();
        }, {
            "async": false
        });
    if(flag) {
        MessageBox.confirm("温馨提示","用户有300M提速优惠，宽带产品变更、宽带移机业务变更非FTTH制式或者移机到非城区。继续办理则默认截止该优惠。",
            function (btn) {
                if (btn === "ok") {
                    confirmMessageBeforeSubmit(param,totalFee,saleActiveFee,saleActiveFee2);
                }
            });
        return;
    }

    confirmMessageBeforeSubmit(param,totalFee,saleActiveFee,saleActiveFee2);
}

function confirmMessageBeforeSubmit(param,totalFee,saleActiveFee,saleActiveFee2) {
    $.cssubmit.addParam(param);

    var modelMode = $("#MODEL_MODE").val();
    if("1"==$("#IS_NEED_MODEL").val()&&(modelMode==""))
    {
        var isNeedChange = $("#IS_EXCHANGE_MODEL").val();
        if(isNeedChange=="0"||isNeedChange=="4"){
            MessageBox.alert("提示","您的宽带类型已经发生变化，当前的光猫在新地址下无法使用。请选择新的光猫");
            return false;
        }
        if(isNeedChange=="3"){
            MessageBox.alert("提示","您的新装宽带地址需要光猫，请选择调测费活动！");
            return false;
        }

        MessageBox.confirm("告警提示","您还没有选择需要的光猫，点击【OK】继续提交业务，点击【Cancel】或者\"关闭提示框\"返回页面选择光猫！",function(re){
            if(re=="ok"){
                if(totalFee>0){
                    //BUS201907310012关于开发家庭终端调测费的需求
                    //var tips = "您总共需要从现金存折中转出："+totalFee+"元，其中宽带营销活动预存："+saleActiveFee+"元，光猫押金："+modelFee+"元，请确认您的余额是否足够？";
                    var tips = "您总共需要从现金存折中转出："+totalFee+"元，其中宽带营销活动预存："+saleActiveFee+"元，宽带调试费活动："+saleActiveFee2+"元，请确认您的余额是否足够？";
                    //BUS201907310012关于开发家庭终端调测费的需求
                    MessageBox.confirm("告警提示",tips,function(re){
                        if(re=="ok"){
                            $.cssubmit.submitTrade();
                        }
                    });
                }else{
                    $.cssubmit.submitTrade();
                }
            }
        });

    }else{
        if(totalFee>0){
            //BUS201907310012关于开发家庭终端调测费的需求
            //var tips = "您总共需要从现金存折中转出："+totalFee+"元，其中宽带营销活动预存："+saleActiveFee+"元，光猫押金："+modelFee+"元，请确认您的余额是否足够？";
            var tips = "您总共需要从现金存折中转出："+totalFee+"元，其中宽带营销活动预存："+saleActiveFee+"元，宽带调试费活动："+saleActiveFee2+"元，请确认您的余额是否足够？";
            //BUS201907310012关于开发家庭终端调测费的需求
            MessageBox.confirm("告警提示",tips,function(re){
                if(re=="ok"){
                    $.cssubmit.submitTrade();
                }
            });
        }else{
            $.cssubmit.submitTrade();
        }
    }
}


//宽带业务综合优化项目 tab页呼唤
function myTabSwitchAction(v, v2) {
	var objTabset = $.tabset("mytab");
	var title = objTabset.getCurrentTitle();// 获取当前标签页标题
	return true;
}

function selectModelMode(){
	$.feeMgr.clearFeeList($("#TRADE_TYPE_CODE").val());
	$("#MODEM_DEPOSIT").val("0");
	var modelCnt = $("#PRE_MODEL_CNT").val();
	var modelMode = $("#MODEL_MODE").val();
	var isBusiness = $("#IS_BUSINESS_WIDE").val();
	if(isBusiness=="1") return ;
	if(modelMode==2 || modelMode==3){
		$.feeMgr.clearFeeList($("#TRADE_TYPE_CODE").val());
	}else if(modelMode==1){
		MessageBox.alert("提示","您选择的是购买光猫，需要缴纳费用300元！");
		var feeData = $.DataMap();
		feeData.clear();
		feeData.put("MODE", "0");
		feeData.put("CODE", "9205");
		feeData.put("FEE", "30000");
		feeData.put("PAY", "30000");
		feeData.put("TRADE_TYPE_CODE", $("#TRADE_TYPE_CODE").val());
		$.feeMgr.insertFee(feeData);
	}else if(modelMode==0){
			var firstReng = $("#FIRST_RENT").val();
			var cashBalance = $("#CASH_BALANCE").val();
			var secondRent = $("#SECOND_RENT").val();
			var isDMB = $("#IS_DISCNT_MODEL_BEFORE").val();
			var isDMN = $("#IS_DISCNT_MODEL_NOW").val();
			var dMNM = $("#DISCNT_MODEL_NOW_MONEY").val();
			var ndmb = $("#NUM_DISCNT_MODEL_BEFORE").val();
			var userId = $("#USER_ID").val();
			var eparchyCode = $("#EPARCHY_CODE").val();
			var activeName = $("#SALE_PACKAGE_NAME").val();
   			var productId = $("#CHG_PRODUCT_ID").val();
			var oldProductId = $("#OLD_PROD_ID").val();
			if(typeof(productId)=="undefined"||productId==""||productId==null){
				productId="";
			}
			
			var param = "&IS_EXCHANGE_MODEL="+$("#IS_EXCHANGE_MODEL").val()+"&IS_BUSINESS_WIDE="+$("#IS_BUSINESS_WIDE").val()+"&SERIAL_NUMBER="+$("#SERIAL_NUMBER").val()+"&USER_ID="+userId+"&ROUTE_EPARCHY_CODE="+eparchyCode;
			param=param+"&IS_USE_MODEL_NOW="+$("#IS_USE_MODEL_NOW").val()+"&MOVE_FTTH_MONEY="+$("#MOVE_FTTH_MONEY").val()+"&TRADE_TYPE_CODE="+$("#TRADE_TYPE_CODE").val()+"&FIRST_RENT="+$("#FIRST_RENT").val()+"&SECOND_RENT="+$("#SECOND_RENT").val();
			param=param+"&IS_DISCNT_MODEL_BEFORE="+isDMB+"&IS_DISCNT_MODEL_NOW="+isDMN+"&DISCNT_MODEL_NOW_MONEY="+dMNM+"&IS_CHG_SALE="+$("#IS_CHG_SALE").val();
			param=param+"&PACKAGE_ID="+$("#SALEACTIVE_PACKAGE_ID").val()+"&NUM_DISCNT_MODEL_BEFORE="+ndmb+"&OLD_SALE_PACKAGE_NAME="+activeName;
			param=param+"&NEW_PROD_ID="+productId+"&OLD_PROD_ID="+oldProductId+"&HAS_End_YEAR_ACTIVE="+$("#HAS_End_YEAR_ACTIVE").val();
			$.beginPageLoading("光猫。。。");
			$.ajax.submit('', 'dealModelMoney', param, '', function(data){
				$.endPageLoading();
				var modelNotReturn = data.get(0).get("MODEL_NOT_RETURN");
				if(parseInt(modelNotReturn)>0){
					MessageBox.alert("提示","您有租借的尚未退还的光猫，请先退还或办理丢失之后，再办理光猫租借！");
					$("#MODEM_DEPOSIT").val("0");
					$("#MODEL_MODE").val("");
				}else{
					var modelShowInfo = data.get(0).get("MODEL_SHOW_INFO");
					MessageBox.alert("提示",modelShowInfo);
					var modelDeposit = data.get(0).get("MODEM_DEPOSIT");
					$("#MODEM_DEPOSIT").val(parseInt(modelDeposit)/100);
					var isDependActive = data.get(0).get("IS_DEPEND_ACTIVE");
					$("#IS_DEPEND_ACTIVE").val(isDependActive);
				}
			},
			function(error_code,error_info){
					$.endPageLoading();
					$.MessageBox.error(error_code,error_info);
		    });
	}
}

function selectSaleActive(packageId){
	$("#IS_CHG_SALE").val("FALSE");
	$("#SALEACTIVE_PACKAGE_ID").val("");
	$("#SALEACTIVE_PRODUCT_ID").val("");
	$("#SALE_ACTIVE_FEE").val("");
	$("#SALE_ACTIVE_FEE_SHOW").val("");
	var modelCode = $("#MODEL_MODE").val();
	var productId = $("#CHG_PRODUCT_ID").val();
	var newProductId = $("#CHG_PRODUCT_ID").val();
	var oldProductId = $("#OLD_PROD_ID").val();
	var isDependActive = $("#IS_DEPEND_ACTIVE").val();
	var isNeedChgProd = $("#IS_NEED_CHG_PROD").val();
	var oldPackageName = $("#SALE_PACKAGE_NAME").val();
    //add by zhangxing3 for 候鸟月、季、半年套餐（海南）
    var saleProductId = $("#SALE_PRODUCT_ID").val();
    if( saleProductId !="" && saleProductId!='' && saleProductId == "66002202" &&  packageId != "" &&  packageId != '')
    {
        $.MessageBox.alert("提示","用户有生效的候鸟活动，移机后可以继续使用，不需要再选择营销活动！");
        $("#SALEACTIVE_PACKAGE_ID").val("");
        $("#SALEACTIVE_PRODUCT_ID").val("");
        return;
    }
    //add by zhangxing3 for 候鸟月、季、半年套餐（海南）
    //add by zhangxing3 for BUS201907300031新增度假宽带季度半年套餐开发需求
    if( saleProductId !="" && saleProductId!='' && saleProductId == "66004809" &&  packageId != "" &&  packageId != '')
    {
        $.MessageBox.alert("提示","用户有生效的度假宽带活动，移机后可以继续使用，不需要再选择营销活动！");
        $("#SALEACTIVE_PACKAGE_ID").val("");
        $("#SALEACTIVE_PRODUCT_ID").val("");
        return;
    }
    //add by zhangxing3 for BUS201907300031新增度假宽带季度半年套餐开发需求

	if(productId==""||productId=='') productId = $("#USER_PRODUCT_ID").val();
	var para = "&PACKAGE_ID="+packageId+"&SERIAL_NUMBER="+$("#SERIAL_NUMBER").val()+"&USER_PRODUCT_ID="+$("#USER_PRODUCT_ID").val()+"&NEW_PRODUCT_ID="+productId+"&ROUTE_EPARCHY_CODE="+$("#USER_EPARCHY_CODE").val()+"&USER_ID="+$("#USER_ID").val()+"&EPARCHY_CODE="+$("#USER_EPARCHY_CODE").val()+"&TRADE_TYPE_CODE="+$("#TRADE_TYPE_CODE").val();
	$.ajax.submit('','getSaleActiveDesc',para,'saleActiveDesc',function(data){
			//add by danglt
			var newSaleFee = data.get(0).get("SALE_ACTIVE_FEE","0");
			var userSaleFee = $("#USER_SALEACTIVE_FEE").val();

			var userSaleCycle = $("#USER_SALEACTIVE_CYCLE").val();
			var newSaleCycle = data.get(0).get("NEW_SALE_CYCLE", "0");

			var userWideRate = $("#USER_WIDENET_RATE").val();
			var newWideRate = $("#USER_WIDE_RATE").val();

			var userProductMode = $("#NEW_PRODUCT_MODE").val();
			var newProductMode = $("#OLD_PROD_MODE").val();

			var userSaleProductId = $("#SALE_PRODUCT_ID").val();
			var newSaleProductId = data.get(0).get("PRODUCT_ID");
			if(newSaleFee != "0" && userSaleFee != "0" && newSaleFee == userSaleFee
				&& userSaleCycle != "0" && newSaleCycle != "0" && newSaleCycle == userSaleCycle
				&& userWideRate == newWideRate
				&& userProductMode != newProductMode
				&& userSaleProductId == newSaleProductId) {
				$("#SALE_ACTIVE_LIST").val("");
				$("#SALEACTIVE_PACKAGE_ID").val("");
				$("#SALEACTIVE_PRODUCT_ID").val("");
				$("#SALE_ACTIVE_FEE").val("0");
				$("#SALE_ACTIVE_FEE_SHOW").val("0");
				$("#SALE_ACTIVE_DESC").val("");
				$("#SAME_YEAR_SALE_ACTIVE_FLAG").val("1");
				MessageBox.alert("提示", "您选择办理的宽带营销活动与原宽带营销活动一样，移机后可以继续使用，无需再次选择相同营销活动！");
				return false;
			}
			if(typeof(packageId)!="undefined"&&packageId!=null&&packageId!=''&&packageId!=""){
				$("#IS_CHG_SALE").val("TRUE");
				if(modelCode=="3"&&isDependActive=="1")
					$("#MODEM_DEPOSIT").val(parseInt($("#SECOND_RENT").val())/100);
			}else{
				if(modelCode=="3"&&isDependActive=="1"){
					$("#MODEM_DEPOSIT").val(parseInt($("#FIRST_RENT").val())/100);
					if(oldPackageName!=null&&oldPackageName!=""&&isNeedChgProd!="1"&&(newProductId==null||newProductId==""))
						$("#MODEM_DEPOSIT").val(parseInt($("#SECOND_RENT").val())/100);
				}
			}
			$("#SALEACTIVE_PACKAGE_ID").val(packageId);
			var productIdS = data.get(0).get("PRODUCT_ID");
			$("#SALEACTIVE_PRODUCT_ID").val(productIdS);
			$("#SALE_ACTIVE_FEE").val(data.get(0).get("SALE_ACTIVE_FEE","0"));
			var feeInfo = parseInt(data.get(0).get("SALE_ACTIVE_FEE","0"));
			if(feeInfo>0){
				var hintInfo = data.get(0).get("HINT_INFO","");
				if(hintInfo!=null&&hintInfo!=""&&hintInfo!='')
					MessageBox.alert("提示",hintInfo);
			}
			var feeShow = feeInfo/100;
			$("#SALE_ACTIVE_FEE_SHOW").val(feeShow+"元");
			$("#SALEACTIVE_CAMPN_TYPE").val(data.get(0).get("CAPM_TYPE"));
			$("#SALE_STAFF_ID").val(data.get(0).get("SALE_STAFF_ID"));
			$("#SALE_ACTIVE_DESC").val(data.get(0).get("ACTIVE_DESC"));	
		},
		function(error_code,error_info){
			$.endPageLoading();
			$.MessageBox.error(error_code,error_info);
    });
}


function selectSaleActive2(packageId){
	$("#SALEACTIVE_PACKAGE_ID2").val("");
	$("#SALEACTIVE_PRODUCT_ID2").val("");
	$("#SALE_ACTIVE_FEE2").val("");
	$("#SALE_ACTIVE_DESC2").val("");
	var productId = $("#CHG_PRODUCT_ID").val();

	if(productId==""||productId=='') productId = $("#USER_PRODUCT_ID").val();
	var para = "&PACKAGE_ID="+packageId+"&SERIAL_NUMBER="+$("#SERIAL_NUMBER").val()+"&USER_PRODUCT_ID="+$("#USER_PRODUCT_ID").val()
		+"&NEW_PRODUCT_ID="+productId+"&ROUTE_EPARCHY_CODE="+$("#USER_EPARCHY_CODE").val()+"&USER_ID="+$("#USER_ID").val()
		+"&EPARCHY_CODE="+$("#USER_EPARCHY_CODE").val()+"&TRADE_TYPE_CODE="+$("#TRADE_TYPE_CODE").val();
	$.ajax.submit('','getSaleActiveDesc2',para,'saleActiveDesc2',function(data){			
			$("#MODEM_DEPOSIT").val(0);
			$("#SALEACTIVE_PACKAGE_ID2").val(packageId);
			var productIdS = data.get(0).get("PRODUCT_ID");
			$("#SALEACTIVE_PRODUCT_ID2").val(productIdS);
			$("#SALE_ACTIVE_FEE2").val(data.get(0).get("SALE_ACTIVE_FEE","0"));
			var feeInfo = parseInt(data.get(0).get("SALE_ACTIVE_FEE","0"));
			if(feeInfo>0){
				var hintInfo = data.get(0).get("HINT_INFO","");
				if(hintInfo!=null&&hintInfo!=""&&hintInfo!='')
					MessageBox.alert("提示",hintInfo);
			}
			//var feeShow = feeInfo/100;
			//$("#SALE_ACTIVE_FEE_SHOW").val(feeShow+"元");
			$("#SALEACTIVE_CAMPN_TYPE2").val(data.get(0).get("CAPM_TYPE"));
			$("#SALE_ACTIVE_DESC2").val(data.get(0).get("ACTIVE_DESC"));
			$("#MODEL_MODE").val("0");	
			
			//start  REQ201912030008宽带客户FTTH迁移营销活动需求2—BOSS侧
			//判断没有勾选办理迁移活动的用户不能选择"FTTB用户移机专用0折调测费营销包"，并提示：该用户不符合FTTB用户迁移调测费减免活动要求
			var saleActive = $("#SALE_ACTIVE_LIST2").val();
			var migrateActivity = $("#MIGRATE_ACTIVITY").val();
			if(saleActive == '79082840' ){
				if(migrateActivity != '1'){
					MessageBox.alert("提示","该用户不符合FTTB用户迁移调测费减免活动要求");
					$("#SALE_ACTIVE_LIST2").val("");
					$("#MODEL_MODE").val("");
					$("#SALE_ACTIVE_DESC2").val("");
					
				}
				
				
			}
			//end  REQ201912030008宽带客户FTTH迁移营销活动需求2—BOSS侧
			
		},
		function(error_code,error_info){
			$.endPageLoading();
			$.MessageBox.error(error_code,error_info);
    });
}


//业务提交
function onTradeSubmit(){
   if(!verifyAll('newWideInfoPart'))
   {
	   return false;
   }
     var param="&NEW_STAND_ADDRESS="+$("#STAND_ADDRESS").val()+"&NEW_STAND_ADDRESS_CODE="+$("#STAND_ADDRESS_CODE").val()
		 +"&NEW_AREA_CODE="+$("#AREA_CODE").val() +"&NEW_DETAIL_ADDRESS="+$("#DETAIL_ADDRESS").val()+"&RSRV_STR2="+$("#RSRV_STR2").val()+"&WIDE_TYPE="+$("#WIDE_TYPE").val();
     
     if("606" == $("#TRADE_TYPE_CODE").val() || "622" == $("#TRADE_TYPE_CODE").val() || "623" == $("#TRADE_TYPE_CODE").val()){
    	 param +="&NEW_DETAIL_ROOM_NUM="+$("#DETAIL_ROOM_NUM").val();
     }
     if("636" == $("#TRADE_TYPE_CODE").val()){
	 param +="&GPON_USER_ID="+$("#GPON_USER_ID").val()+"&GPON_SERIAL_NUMBER="+$("#GPON_SERIAL_NUMBER").val()
		 +"&USER_ID_A="+$("#USER_ID_A").val();
	 	
	}
   $.cssubmit.addParam(param);
   return true;
}

function afterSelSuggestDate(obj)
{
	var suggerDate = $("#SUGGEST_DATE").val();
	
	var min=$(obj).attr("min");
	var chooseDate=new Date(Date.parse(suggerDate));
	var minDate=new Date(Date.parse(min));
	if(chooseDate < minDate){
		$.MessageBox.alert('提示','预约受理时间不能小于'+min+'!');
		$("#SUGGEST_DATE").val('');
		return;
	}
	
	//判断预约选择的时间是否在工作时间范围内，每天8点-16点为工作时间
	var strHour = suggerDate.substr(11,2);
	var h = Number(strHour);
	if(h < 8 || h >= 17)
	{
		MessageBox.alert('提示','预约施工时间为每天8：00--17：00,请重新选择时间!');
		$("#SUGGEST_DATE").val('');
		return ;
	}
}

//树状地址查询
function addressTreeSelect(){
	var serialNumber= $("#AUTH_SERIAL_NUMBER").val();
	var contactSn = $("#CONTACT_PHONE").val();
	var custName = $("#CONTACT").val();
	var param = "&AUTH_SERIAL_NUMBER="+serialNumber+"&CUST_NAME="+encodeURIComponent(custName)+"&CONTACT_SN="+contactSn;
//	$.popupPage('res.popup.AddressQryTree','init',param+'&TREE_TYPE=0','树状地址','1000','340','STAND_ADDRESS','',subsys_cfg.pbossintf);
	popupPage("树状地址选择","res.popup.AddressQryTreeNew","init",param+'&TREE_TYPE=0',"iorder","c_popup c_popup-full",changeWidenetProduct.selectProductModeA,null);
}

function checkAddress(textbox){
	/*var IllegalString = "[`~!#$^&*()=|{}':;',\\[\\].<>/?~！#￥……&*（）——|{}【】‘；：”“'。，、？]‘'@%";
	var textboxvalue = textbox.value;
	var index = textboxvalue.length - 1;

	var s = textbox.value.charAt(index);
	if (IllegalString.indexOf(s) >= 0) {
		s = textboxvalue.substring(0, index);
		textbox.value = s;
	}*/
	//var pattern = new RegExp("[`~!@#$^&*()=|{}':;',\\[\\].<>/?~！@#￥……&*（）——|{}【】‘；：”“'。，、？]") ;
	var pattern=new RegExp("[`~!#$^&*()=|{}':;',\\[\\].<>/?~！#￥……&*（）——|{}【】‘；：”“'。，、？‘'@%]");
	var textboxvalue = textbox.value;
	var rs="";
	for(var i=0;i<textboxvalue.length;i++){
		rs=rs+textboxvalue.substr(i,1).replace(pattern,'');
	}
	
	textbox.value=rs;
}


//办理迁移活动
//REQ201912030008宽带客户FTTH迁移营销活动需求2—BOSS侧
function chooseMigrateActivity(){
	
	var deviceId = $("#OLD_DEVICE_ID").val();//原设备号
	if(deviceId != null &&  deviceId !=  ""){
		var checkNP=$("#MIGRATE_ACTIVITY").attr("checked");
		if(checkNP){
			
			var param = "&DEVICE_ID="+deviceId;
			
			 $.beginPageLoading("正在查询。。。");
			 $.ajax.submit('', 'chooseMigrateActivity', param, '', function(data){
				 $.endPageLoading();
				 if(data.length > 0){
					 $("#MIGRATE_ACTIVITY").val("1");
				 }else{
					 MessageBox.alert('提示','该用户不是迁移目标小区的目标用户，不能办理！');
					 $("#MIGRATE_ACTIVITY").attr("checked",false);
					 $("#MIGRATE_ACTIVITY").val("0");
					 var saleActive = $("#SALE_ACTIVE_LIST2").val();
					if(saleActive == '79082840'){
						$("#SALE_ACTIVE_LIST2").val("");
						$("#MODEL_MODE").val("");
						$("#SALE_ACTIVE_DESC2").val("");
					}
					 
				 }
			},
			function(error_code,error_info){
				$("#MIGRATE_ACTIVITY").attr("checked",false);
				$("#MIGRATE_ACTIVITY").val("0");
				var saleActive = $("#SALE_ACTIVE_LIST2").val();
				if(saleActive == '79082840'){
					$("#SALE_ACTIVE_LIST2").val("");
					$("#MODEL_MODE").val("");
					$("#SALE_ACTIVE_DESC2").val("");
				}
				$.endPageLoading();
				$.MessageBox.error(error_code,error_info);
			});
			
		}else{
			$("#MIGRATE_ACTIVITY").val("0");
			
			var saleActive = $("#SALE_ACTIVE_LIST2").val();
			if(saleActive == '79082840'){
				$("#SALE_ACTIVE_LIST2").val("");
				$("#MODEL_MODE").val("");
				$("#SALE_ACTIVE_DESC2").val("");
			}
		}
	}else{
		 MessageBox.alert('提示','原设备号获取失败！');
		$("#MIGRATE_ACTIVITY").attr("checked",false);
		$("#MIGRATE_ACTIVITY").val("0");
		var saleActive = $("#SALE_ACTIVE_LIST2").val();
		if(saleActive == '79082840'){
			$("#SALE_ACTIVE_LIST2").val("");
			$("#MODEL_MODE").val("");
			$("#SALE_ACTIVE_DESC2").val("");
		}
	}
	
}






