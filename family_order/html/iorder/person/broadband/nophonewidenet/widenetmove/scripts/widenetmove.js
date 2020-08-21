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

		var wsn = data.get(0).get("TOPSETBOX_SERIAL_NUMBER"); //147手机号码
	 	if ('' !== wsn && null != wsn && undefined !== wsn) {
	 		MessageBox.alert("告警提示", "客户已办理魔百和业务，魔百和需移机!");
	 	}

		$.endPageLoading();
	},
	function(error_code, error_info,detail) { 
    $.endPageLoading();
    MessageBox.error("错误提示", error_info, $.auth.reflushPage, null, null, detail);
});
    //如果是两城两宽号码不允许办理产品变更
    if($("#IS_TWOCITYNUM").val() == "true") {
        MessageBox.alert("提示", "两城两宽号码不允许办理无手机宽带产品变更");
        $("#mytab_tab").find("li[idx=1]").attr("style","display:none");
    }
}

//tab页呼唤
function myTabSwitchAction(v, v2) {
	var objTabset = $.tabset("mytab");
	var title = objTabset.getCurrentTitle();// 获取当前标签页标题
	return true;
}


//检查是否是两城两宽手机号码
function checkIsTwoCityNum() {
    var serialNumber = $("#AUTH_SERIAL_NUMBER").val();
    if(serialNumber.length == 11) {
        $.ajax.submit('',"checkIsTwoCityNum","&SERIAL_NUMBER="+serialNumber,null,function (data) {
            var twoCityNum = data.get("SERIAL_NUMBER_B");
            if( twoCityNum  != "") {
                //重新给auth手机号码设值
                $("#AUTH_SERIAL_NUMBER").val(twoCityNum);
                //设置是北京号码的标识
                $("#IS_TWOCITYNUM").val("true");

            }
        },function (error_code,error_info,detail) {
        },{
            "async" : false
        });
    }
}
//光猫模式选择
function selectModelMode(){
	$.feeMgr.clearFeeList($("#TRADE_TYPE_CODE").val());//清除费用
	$.feeMgr.removeFee($("#TRADE_TYPE_CODE").val(),"2","9002");//清除费用
	$.feeMgr.removeFee($("#TRADE_TYPE_CODE").val(),"2","438");//清除费用
	$("#MODEM_DEPOSIT").val("0");
	var modelMode = $("#MODEL_MODE").val();

	if(modelMode==2 || modelMode==3){//赠送和自备
		$.feeMgr.clearFeeList($("#TRADE_TYPE_CODE").val());
	}else if(modelMode==1){//购买（已废弃）
		MessageBox.alert("提示","您选择的是购买光猫，需要缴纳费用300元！");
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
					MessageBox.alert("提示","您有租借的尚未退还的光猫，请先退还或办理丢失之后，再办理光猫租借！");
					$("#MODEM_DEPOSIT").val("0");
					$("#MODEL_MODE").val("");
				}else{
					$("#modemFeePart").css('display', '');
//					var modelShowInfo = data.get(0).get("MODEL_SHOW_INFO");
//					MessageBox.alert("提示",modelShowInfo);
//					var modelDeposit = data.get(0).get("MODEM_DEPOSIT");
//					$("#MODEM_DEPOSIT").val(parseInt(modelDeposit)/100);
//					
//					//加载费用列表
//					$.feeMgr.removeFee($("#TRADE_TYPE_CODE").val(),"2","9002");
//					var feeData = $.DataMap();
//					feeData.clear();
//					feeData.put("MODE", "2");
//					feeData.put("CODE", "9002");
//					feeData.put("FEE", modelDeposit);
//					feeData.put("PAY", modelDeposit);
//					feeData.put("TRADE_TYPE_CODE", $("#TRADE_TYPE_CODE").val());
//					$.feeMgr.insertFee(feeData);
				}
			},
			function(error_code,error_info){
					$.endPageLoading();
					$.MessageBox.error(error_code,error_info);
		    });
	}
}
function setModemFee2() 
{
	if ($("#WIDE_MODE_FEE").val() == "" ||$("#WIDE_MODE_FEE").val() == null) {
        $.feeMgr.removeFee($("#TRADE_TYPE_CODE").val(),"2","438");
	}else{
		if ( $("#MODEL_MODE").val() == "0" ) {
			//$.nophonewideusercreate.PAGE_FEE_LIST.clear();
            $.feeMgr.removeFee($("#TRADE_TYPE_CODE").val(),"2","438");
            
            var serialNumber = $("#SERIAL_NUMBER").val();
			var eparchyCode = $("#EPARCHY_CODE").val()
			var firstReng = $("#FIRST_RENT").val();
			var isExchangeModel = $("#IS_EXCHANGE_MODEL").val();
   			var moveFtthMoney = $("#MOVE_FTTH_MONEY").val();
			
			var param = "&SERIAL_NUMBER="+serialNumber+"&ROUTE_EPARCHY_CODE="+eparchyCode
					  + "&FIRST_RENT="+firstReng+"&IS_EXCHANGE_MODEL="+isExchangeModel+ "&MOVE_FTTH_MONEY="+moveFtthMoney;
            //获取光猫租赁费用
            $.ajax.submit('', 'dealModelMoney', param, '',
                function (data) {
            	    var modelDeposit = data.get(0).get("MODEM_DEPOSIT");
					//加载费用列表
					$.feeMgr.removeFee($("#TRADE_TYPE_CODE").val(),"2","438");
					var feeData = $.DataMap();
					feeData.clear();
					feeData.put("MODE", "2");
					feeData.put("CODE", "438");
					feeData.put("FEE", modelDeposit);
					feeData.put("PAY", modelDeposit);
					feeData.put("TRADE_TYPE_CODE", $("#TRADE_TYPE_CODE").val());
					$.feeMgr.insertFee(feeData);
                }, 
                function (error_code, error_info) {
                    $.endPageLoading();
                    $.MessageBox.error(error_code, error_info);
                });
            
        } else {
            $("#MODEM_DEPOSIT").val('0');
          //  $.nophonewideusercreate.PAGE_FEE_LIST.clear();
            $.feeMgr.removeFee($("#TRADE_TYPE_CODE").val(),"2","438");
        }
	}
	
}

function setModemFee() 
{
	var modelMode = $("#MODEL_MODE").val();
	if(modelMode==0){
		
		var serialNumber = $("#SERIAL_NUMBER").val();
		var eparchyCode = $("#EPARCHY_CODE").val()
		var firstReng = $("#FIRST_RENT").val();
		var isExchangeModel = $("#IS_EXCHANGE_MODEL").val();
			var moveFtthMoney = $("#MOVE_FTTH_MONEY").val();
		
		var param = "&SERIAL_NUMBER="+serialNumber+"&ROUTE_EPARCHY_CODE="+eparchyCode
				  + "&FIRST_RENT="+firstReng+"&IS_EXCHANGE_MODEL="+isExchangeModel+ "&MOVE_FTTH_MONEY="+moveFtthMoney;
				  
		$.ajax.submit('', 'dealModelMoney', param, '', function(data){
			$.endPageLoading();
			var modelNotReturn = data.get(0).get("MODEL_NOT_RETURN");
			var modelShowInfo = data.get(0).get("MODEL_SHOW_INFO");
			MessageBox.alert("提示",modelShowInfo);
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
   	
   	var modelMode = $("#MODEL_MODE").val();
   	
   	var oldWideType=$('#OLD_WIDE_TYPE').val();
	var newWideType=$('#NEW_WIDE_TYPE').val();
	//FTTH移机成FTTB
	if(oldWideType=='3'&&(newWideType=='1'||newWideType=='6')){
		MessageBox.alert('提示','如有押金光猫无清退，移机后请到无手机光猫管理界面清退押金并收回光猫');
	}
	

   
   	var param = "&NEW_STAND_ADDRESS="+$("#STAND_ADDRESS").val()
   			  + "&NEW_AREA_CODE="+$("#AREA_CODE").val()
   			  + "&NEW_DETAIL_ADDRESS="+$("#DETAIL_ADDRESS").val()
   			  + "&RSRV_STR2="+$("#RSRV_STR2").val()
   			  + "&BOOKING_DATE="+$("#FIRST_DAY_NEXT_MONTH").val();
			
	var data = selectedElements.getSubmitData();
	
	//REQ201903080003一机多宽优惠活动开发需求调整
	var oneSnFlag=false;
	data.each(function (item) {
        var elementId = item.get("ELEMENT_ID");
        var elementType = item.get("ELEMENT_TYPE_CODE");
        var modifyTag = item.get("MODIFY_TAG");
        if (("84018242" == elementId||"84018243" == elementId ||"84020642" == elementId ||"84020643" == elementId
        		||"84020644" == elementId ||"84020645" == elementId ) && elementType == "D" && "0" == modifyTag) 
        {
        	oneSnFlag=true;
        }
	});
	var strIsOneSnManyWide = $("#IS_ONESN_MANYWIDE").val();
	var wideProductType = $("#NEW_WIDE_TYPE").val();
	if( '1' == strIsOneSnManyWide &&!oneSnFlag)
	{
		MessageBox.alert("提示","一机多宽无手机宽带开户必须选择一机多宽优惠！");
		return false;
	}
	if( '0' == strIsOneSnManyWide &&oneSnFlag)
	{
		MessageBox.alert("提示","此资费只适合一机多宽模式！");
		return false;
	}
	if ( '1' == strIsOneSnManyWide && ('3' != wideProductType && '5' != wideProductType))
	{
		MessageBox.alert("提示","一机多宽,请选择支持FTTH制式宽带的小区！");
		return false;
	}
	//REQ201903080003一机多宽优惠活动开发需求调整
	
	if(data&&data.length>0){
		param += "&SELECTED_ELEMENTS="+data.toString();
		if($("#EFFECT_NOW").attr("checked")){
			param+="&EFFECT_NOW=1";
		}
	}
	
   	$.cssubmit.addParam(param);
   	
   	if("1"==$("#IS_NEED_MODEL").val()&&(modelMode==""))
   	{
   		var isNeedChange = $("#IS_EXCHANGE_MODEL").val();
   		if(isNeedChange=="0"||isNeedChange=="4"){
   			MessageBox.alert("提示","您的宽带类型已经发生变化，当前的光猫在新地址下无法使用。请选择新的光猫");
   			return false;
   		}
   		if(isNeedChange=="3"){
   			MessageBox.alert("提示","您的新装宽带地址需要光猫，请选择您的光猫");
   			return false;
   		}
   		
		MessageBox.confirm("告警提示","您还没有选择需要的光猫，点击【OK】继续提交业务，点击【Cancel】或者\"关闭提示框\"返回页面选择光猫！",function(re){
			if(re=="ok"){
				$.cssubmit.submitTrade();
			}
		});
   	}else{
   		return true;
   	}
}