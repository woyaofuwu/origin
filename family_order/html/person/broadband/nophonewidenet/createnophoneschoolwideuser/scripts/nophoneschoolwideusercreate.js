PAGE_FEE_LIST = $.DataMap();

(function($) {
	if (typeof ($.nophoneschoolwideusercreate == "undefined")) {
		
		$.nophoneschoolwideusercreate = {
			
			/**
			 * 校验宽带账号
			 */
			checkWidenetAcctId : function() 
			{
				var widenetAcctId = $("#SERIAL_NUMBER").val();
				var oldWidenetAcctId = $("#OLD_WIDENET_ACCT_ID").val();
				
				if (null == widenetAcctId || '' == widenetAcctId)
				{
					MessageBox.alert("提示","请先录入宽带账号再进行校验！");
					
					return false;
				}
				
				//已经校验过则不需要再次校验
				if (widenetAcctId == oldWidenetAcctId)
				{
					MessageBox.alert("提示","该宽带账号已校验通过，无需重复校验！");
					
					return;
				}
				
				$.beginPageLoading("宽带账号校验中。。。");
				
				$.ajax.submit(this, 'checkWidenetAcctId',
						'&WIDENET_ACCT_ID=' + widenetAcctId+'&OLD_WIDENET_ACCT_ID=' + oldWidenetAcctId, '',
						function(data) 
						{
							$("#OLD_WIDENET_ACCT_ID").val(widenetAcctId);
							
							//针对实名制，需要传递手机给组件
							$.custInfo.setSerialNumber(widenetAcctId);
							MessageBox.alert("提示","宽带账号校验通过，请继续录入其他资料！");
							$.nophoneschoolwideusercreate.changeProduct();
							$.cssubmit.disabledSubmitBtn(false);
							$.endPageLoading();
						}, function(error_code, error_info) 
						{
							$.endPageLoading();
							
							$("#SERIAL_NUMBER").val($("#OLD_WIDENET_ACCT_ID").val());
							
							$.MessageBox.error(error_code, error_info);
						});
			},
			
			/**
			 * 随机获得有效的宽带账号
			 */
			getValidWideNetAccountId : function() 
			{
				var oldWidenetAcctId = $("#OLD_WIDENET_ACCT_ID").val();
				
				$.beginPageLoading("获取宽带账号信息中。。。");
				
				$.ajax.submit(this, 'getValidWideNetAccountId',
						'&OLD_WIDENET_ACCT_ID=' + oldWidenetAcctId, '',
						function(data)
						{
							$("#SERIAL_NUMBER").val(data.get('ACCOUNT_ID'));
							$("#OLD_WIDENET_ACCT_ID").val(data.get('ACCOUNT_ID'));
							
							//针对实名制，需要传递手机给组件
							$.custInfo.setSerialNumber(data.get('ACCOUNT_ID'));
							$.nophoneschoolwideusercreate.changeProduct();
							$.cssubmit.disabledSubmitBtn(false);
							$.endPageLoading();
						}, function(error_code, error_info) 
						{
							$.endPageLoading();
							$.MessageBox.error(error_code, error_info);
						});
			},
			
			afterRenderSelectedElements : function(data) {
				var temp = data.get(0);
				$("#NEW_PRODUCT_START_DATE").val(temp.get("NEW_PRODUCT_START_DATE"));
			},
			
			changeProduct : function() 
			{
				$.feeMgr.clearFeeList($("#TRADE_TYPE_CODE").val());
				var productId = $("#WIDE_PRODUCT_ID").val();
				var eparchyCode = "0898";
				var param = "&NEW_PRODUCT_ID=" + productId;
				
				offerList.renderComponent(productId,eparchyCode);
				selectedElements.renderComponent(param, eparchyCode);
				
				var param = "&ROUTE_EPARCHY_CODE="+eparchyCode+"&TRADE_TYPE_CODE="+$("#TRADE_TYPE_CODE").val();
			    $.beginPageLoading("业务资料查询。。。");
				$.ajax.submit(this, 'loadChildInfo', param, 'productType', function(data)
				{
					$.nophoneschoolwideusercreate.initProduct();
					$.endPageLoading();
				},
				function(error_code,error_info)
				{
					$.endPageLoading();
					$.MessageBox.error(error_code,error_info);
			    });
				
			},
			
			/**初始化产品 */
			initProduct : function()
			{
				offerList.renderComponent("","0898");
				
				selectedElements.renderComponent("&NEW_PRODUCT_ID=", "0898");
			},
			
			initPage : function() 
			{
				//给账户信息设置默认值，并隐藏不需要的
				$("#PAY_MODE_CODE").val('0');
				$("#ACCT_DAY").val('1');
				
				$("#PAY_MODE_CODE").attr("disabled",true);
				$("#ACCT_DAY").attr("disabled",true);
				$
				$("#SuperBankCodeLi").css('display', 'none');
				$("#BankCodeLi").css('display', 'none');
				$("#BankAcctNoLi").css('display', 'none');
				$("#bankAgreementNoLi").css('display', 'none');
				$("#SHOW_MORE_INFO").css('display', 'none');
				$("#CUST_INPUT_CONTROL").css('display', 'none');
				$("#SHOT_IMG").show();
				$.ajax.submit(null, 'queryPsptTypeList',null,null,
						function(data) 
						{
							var psptTypeList = data.get("PSPT_TYPE_LIST");
							
							if (null != psptTypeList)
							{
								PSPT_TYPE_CODE.empty();
								
								for (var i = 0; i < psptTypeList.length; i++)
								{
									PSPT_TYPE_CODE.append(psptTypeList.get(i).get("DATA_NAME"),psptTypeList.get(i).get("DATA_ID"));
								}
								$("#PSPT_TYPE_CODE").val("0");
							}
							
							$.endPageLoading();
						}, function(error_code, error_info, derror) {
							$.endPageLoading();
							$.MessageBox.error(error_code, error_info);
						});

			},
			
			onSubmit : function(){ 
				
				if (!verifyAll('widePart')) 
				{
					return false;
				}
				if(!verifyAll('BaseInfoPart'))
			    {
				    return false;
			    }
			    if(!verifyAll('CustInfoPart'))
			    {
				    return false;
			    }
			    if(!verifyAll('AcctInfoPart'))
			    {
				    return false;
			    }
			    if(!verifyAll('PasswdPart'))
			    {
				   return false;
			    }
				
				var widenetAcctId = $("#SERIAL_NUMBER").val();
				var oldWidenetAcctId = $("#OLD_WIDENET_ACCT_ID").val();

				if (widenetAcctId != oldWidenetAcctId)
				{
					MessageBox.alert("提示","宽带服务号码未校验通过！");
			        return false;
				}
				
				var data = selectedElements.getSubmitData();
				if (data && data.length > 0) 
				{
					var param = "&SELECTED_ELEMENTS=" + data.toString();
					$.cssubmit.addParam(param);
					$.cssubmit.submitTrade();  
				} 
				else
				{
					MessageBox.alert("提示",'您未选择开户产品，不能提交！');
					return false;
				}
				
				if($("#REAL_NAME").val()=="1" && $("#REALNAME_LIMIT_CHECK_RESULT").val() != "true")
				{
			        MessageBox.alert("提示","实名制开户证件校验未通过！");
			        return false;
			    }
				
			    /**
			     * 添加判断物联网开户，不需要进行人像比对
			     * @author zhuoyingzhi
			     * @date 20170706
			     */
			    var cmpTag = "1";
			    $.ajax.submit(null, "isCmpPic", null, null,
			        function (ajaxData) {
			            var flag = ajaxData.get("CMPTAG");
			            if (flag === "0") {
			                cmpTag = "0";
			            }
			            $.endPageLoading();
			        },
			        function (error_code, error_info) {
			            $.MessageBox.error(error_code, error_info);
			            $.endPageLoading();
			        });
				
				if (cmpTag === "0") {
					
					var picId = $("#PIC_ID").val();
			    	var psptTypeCode = $("#PSPT_TYPE_CODE").val();
	
			        if (picId != null && picId === "ERROR") { // 客户摄像失败
			            MessageBox.error("告警提示", "客户" + $("#PIC_STREAM").val());
			            return false;
			        }
			        if ((psptTypeCode === "1" || psptTypeCode === "0") && picId === "") { // 未进行客户摄像
	                    MessageBox.error("告警提示", "请进行人像对比!");
	                    return false;
	                }
					var param = "&PIC_ID=" + picId;
			        $.cssubmit.addParam(param);
				}
			}
			
		}
	}
})(Wade);


/*密码组件前校验*/
function PasswdbeforeAction() {
	var psptId =$("#PSPT_ID").val();
	var serialNumber = $("#SERIAL_NUMBER").val();
     if(psptId == ""){
    	 MessageBox.alert("告警提示","证件号码不能为空！");
      return false;
    }
     if(serialNumber == ""){
    	 MessageBox.alert("告警提示","宽带服务号码不能为空！");
    	 return false;
     }
     
    //将值赋给组件处理
    $.password.setPasswordAttr(psptId, serialNumber);
    return true ;
}

/*密码组件后赋值*/
function PasswdafterAction(data) {

$("#USER_PASSWD").val(data.get("NEW_PASSWORD"));
}


function changeProduct()
{	 
    $.feeMgr.clearFeeList($("#TRADE_TYPE_CODE").val());
    $.feeMgr.insertFee(PAGE_FEE_LIST.get("RES_FEE"));
	var productId=$("#WIDE_PRODUCT_ID").val();
	var eparchyCode="0898";
    var param = "&NEW_PRODUCT_ID="+productId;
    offerList.renderComponent(productId,eparchyCode);
    selectedElements.renderComponent(param,eparchyCode);
    //$("#MODEM_STYLE").attr("disabled", true);
    //TODO 获取产品费用
    
    var feeData = $.DataMap();
    feeData.clear();
	feeData.put("MODE", "2");
	feeData.put("CODE", "0");
	feeData.put("FEE",  "0");
	feeData.put("PAY",  "0");		
	feeData.put("TRADE_TYPE_CODE",$("#TRADE_TYPE_CODE").val());
	$.feeMgr.insertFee(feeData);
  
}
 
function setModemNumeric(){
    if($("#MODEM_STYLE").val() == ""){
      $("#MODEM_NUMERIC_CODE").val("");
      MessageBox.alert("提示",'MODEM方式不能为空！');
      return false;
    }
	if($("#MODEM_STYLE").val()!="1"){//1:自备
	        $("#MODEM_NUMERIC").attr("disabled", false);
		    $("#MODEM_NUMERIC").attr("nullable", "no");
		    $("#MODEM_NUMERIC_CODE").val('ADSL-S6307HV（销售）');
				    
		     if($("#MODEM_STYLE").val() == "2"){//铁通MODEM租用费用 属于预存
		            PAGE_FEE_LIST.clear();
			        var feeData = $.DataMap();
		          	feeData.clear();
					feeData.put("MODE", "2");
					feeData.put("CODE", "201");
					feeData.put("FEE",  "8800");
					feeData.put("PAY",  "8800");		
					feeData.put("TRADE_TYPE_CODE",$("#TRADE_TYPE_CODE").val());
					
					$.feeMgr.insertFee(feeData);	
					PAGE_FEE_LIST.put("RES_FEE", $.feeMgr.cloneData(feeData));	
			 }else if($("#MODEM_STYLE").val() == "3"){//铁通MODEM购买费用 属于营业费用
			        PAGE_FEE_LIST.clear();
			        var feeData = $.DataMap();
		          	feeData.clear();
					feeData.put("MODE", "0");
					feeData.put("CODE", "9205");
					feeData.put("FEE",  "8800");
					feeData.put("PAY",  "8800");		
					feeData.put("TRADE_TYPE_CODE",$("#TRADE_TYPE_CODE").val());
						
					$.feeMgr.insertFee(feeData);
					PAGE_FEE_LIST.put("RES_FEE", $.feeMgr.cloneData(feeData));
			 }
		    
	}else{
	        $("#MODEM_NUMERIC").attr("disabled", true);
		    $("#MODEM_NUMERIC").attr("nullable", "yes");
		    $("#MODEM_NUMERIC_CODE").val("");
		    
		    PAGE_FEE_LIST.clear();
		    var feeData = $.DataMap();
          	feeData.clear();
			feeData.put("MODE", "2");
			feeData.put("CODE", "200");
			feeData.put("FEE",  "0");
			feeData.put("PAY",  "0");		
			feeData.put("TRADE_TYPE_CODE",$("#TRADE_TYPE_CODE").val());
			$.feeMgr.insertFee(feeData);
			PAGE_FEE_LIST.put("RES_FEE", $.feeMgr.cloneData(feeData));
	}	

      $("#MODEM_STYLE").attr("disabled", true);
}

function checkFTTH()
{
	//先判断是不是FTTH宽带开户，613FTTH宽带开户
	if("613" == $("#TRADE_TYPE_CODE").val()){
		
		var serialNumber = $("#AUTH_SERIAL_NUMBER").val();
		
		MessageBox.confirm("告警提示","是否申领光猫？",function(re){
			if(re=="ok"){
				//再判断是个人宽带还是商务宽带
				if(serialNumber.substring(0,1)=="1"){//个人手机用户宽带
					openNav('FTTH光猫申领','broadband.widenet.ftthmodermapply.FTTHModermApply', '','');
				}else{//商务宽带
					openNav('FTTH商务宽带光猫申领','ftthbusimodemapply.FTTHBusiModemApply', '','');
				}
			}else{
				var href = window.location.href;
				if(href){
					if(href.lastIndexOf("#nogo") == href.length-5){
						href = href.substring(0, href.length-5);
					}
					var url = href.substring(0, href.indexOf("?"));
					var reqParam = href.substr(href.indexOf("?")+1);
						
					var paramObj = $.params.load(reqParam);
					paramObj.remove("SERIAL_NUMBER");
					paramObj.remove("DISABLED_AUTH");
					paramObj.remove("AUTO_AUTH");
					var param = paramObj.toString();
					param += "&AUTO_AUTH=false";   ////受理成功以后，返回页面界面，禁用之前逻辑中的自动刷新
					window.location.href = url+"?"+param;
				}
			}
		});
	}else{
		var href = window.location.href;
		if(href){
			if(href.lastIndexOf("#nogo") == href.length-5){
				href = href.substring(0, href.length-5);
			}
			var url = href.substring(0, href.indexOf("?"));
			var reqParam = href.substr(href.indexOf("?")+1);
					
			var paramObj = $.params.load(reqParam);
			paramObj.remove("SERIAL_NUMBER");
			paramObj.remove("DISABLED_AUTH");
			paramObj.remove("AUTO_AUTH");
			var param = paramObj.toString();
			param += "&AUTO_AUTH=false";   ////受理成功以后，返回页面界面，禁用之前逻辑中的自动刷新
			window.location.href = url+"?"+param;
		}
	}
}

function changeType(){
	
//	alert($("#ADSL_TYPE").val()+$.auth.getAuthData());
//	if($("#ADSL_TYPE").val() == "1")
//	{
//		$("#FtthAddress").css("display", "none");
//		$("#AdslAddress").css("display", "");
//		$("#fttb-modem").css("display", "none");
//		$("#adsl-modem").css("display", "");
//	}
//	else if ($("#ADSL_TYPE").val() == "2")
//	{
//		$("#FtthAddress").css("display", "");
//		$("#AdslAddress").css("display", "none");
//		$("#fttb-modem").css("display", "");
//		$("#adsl-modem").css("display", "none");
//	}
	
	if($("#ADSL_TYPE").val() == '' || $.auth.getAuthData() == 'undefined')
		return false; 
	
	var eparchyCode=$.auth.getAuthData().get("USER_INFO").get("EPARCHY_CODE");
	
	var inparam = "&ADSL_TYPE="+$("#ADSL_TYPE").val() + "&TRADE_TYPE_CODE="+$("#TRADE_TYPE_CODE").val()+"&ROUTE_EPARCHY_CODE="+eparchyCode;
	$.ajax.submit(null, 'getProductInfo', inparam, 'productType,widePart,modemPart', function(data) {
	   if(data.getCount() == 0)
	   {
		  MessageBox.alert("提示","未获取到FTTB/FTTH宽带产品信息，请检查产品权限！");
	      return false;
	   }
//	   	$("#STAND_ADDRESS").val("");
//	   	$("#STAND_ADDRESS_CODE").val("");
      },
	    function(error_code,error_info){
			$.endPageLoading();
			$.MessageBox.error(error_code,error_info);
   });
	
}

//点击展示，隐藏产商品信息按钮
function displayOfferList(btn,o)
{
	var button = $(btn);
    var div = $('#'+o);
    if (div.css('display') != "none")
    {
		  div.css('display', 'none');
		  button.empty();
		  button.html('<span class="e_ico-unfold"></span>显示服务/优惠'); 
    }else {
       div.css('display', '');
       button.empty();
	   button.html('<span class="e_ico-fold"></span>隐藏服务/优惠'); 
    }
}

