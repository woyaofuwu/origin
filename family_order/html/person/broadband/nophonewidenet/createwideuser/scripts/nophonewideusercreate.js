function setAddressInfo()
{
	$("#STAND_ADDRESS_CODE").val("12306");
	$("#STAND_ADDRESS").val("海口市滨海大道");
	
	var tempSendAddressCode = $("#TEMP_STAND_ADDRESS_CODE").val();
	var tempSendaddress = $("#TEMP_STAND_ADDRESS").val();
	var tempDeviceId = $("#TEMP_DEVICE_ID").val();
	var tempOpenType = $("#TEMP_OPEN_TYPE").val();
	
//	$("#STAND_ADDRESS_CODE").val(tempSendAddressCode);
//	$("#STAND_ADDRESS").val(tempSendaddress);
	$("#DEVICE_ID").val("123111");
	$("#OPEN_TYPE").val("FTTH");
	
	$.nophonewideusercreate.afterSetDetailAddress();
}



(function($) {
	if (typeof ($.nophonewideusercreate == "undefined")) {
		
		$.nophonewideusercreate = {
			PAGE_FEE_LIST: $.DataMap(),
			
			/**
			 * 校验宽带账号
			 */
			checkWidenetAcctId : function() 
			{
				var widenetAcctId = $("#SERIAL_NUMBER").val();
				var oldWidenetAcctId = $("#OLD_WIDENET_ACCT_ID").val();
				
				if (null == widenetAcctId || '' == widenetAcctId)
				{
					alert("请先录入宽带账号再进行校验！");
					
					return false;
				}
				
				//已经校验过则不需要再次校验
				if (widenetAcctId == oldWidenetAcctId)
				{
					alert("该宽带账号已校验通过，无需重复校验！");
					
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
							alert("宽带账号校验通过，请继续录入其他资料！");
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
							$.cssubmit.disabledSubmitBtn(false);
							$.endPageLoading();
						}, function(error_code, error_info) 
						{
							$.endPageLoading();
							$.MessageBox.error(error_code, error_info);
						});
			},
			
			afterSetDetailAddress : function()
			{
				/**
				 * 判断  楼层和房号  情况
				 * @author zhuoyingzhi
				 */
				afterFloorAndRoomNum();
				//默认将标准地址放置到安装地址处
				$("#DETAIL_ADDRESS").val($("#STAND_ADDRESS").val());
				
				//PBOSS那边传过来的宽带类型,1-移动FTTH、2-铁通FTTH、3-移动FTTB、4-铁通FTTB、5-铁通ADSL
				var openType = $("#OPEN_TYPE").val();
				
				//1：移动GPON，2：ADSL，3：移动FTTH，4：校园宽带，5：海南铁通FTTH，6：海南铁通FTTB
				var wideProductType = "1";
				
				if ('FTTH' == openType)
				{
					wideProductType = '3';
				}
				else if ('TTFTTH' == openType)
				{
					wideProductType = '5';
				}
				else if ('GPON' == openType)
				{
					wideProductType = "1";
				}
				else if ('TTFTTB' == openType)
				{
					wideProductType = "6";
				}
				else if ('TTADSL' == openType)
				{
					MessageBox.alert("告警提示","无手机宽带不能选择ADSL宽带类型，请重新选择开户地址！");
					$("#WIDE_PRODUCT_TYPE").val("");
					$("#FLOOR_AND_ROOM_NUM").val("");
					$("#STAND_ADDRESS").val("");
					$("#DEVICE_ID").val("");
					$("#OPEN_TYPE").val("");
				}
				
				$("#WIDE_PRODUCT_TYPE").val(wideProductType);
				
				$.nophonewideusercreate.changeWideProductType();
			},

			/**
			 * 初始化产品
			 * */
			initProduct : function()
			{
				offerList.renderComponent("","0898");
				
				selectedElements.renderComponent("&NEW_PRODUCT_ID=", "0898");
			},

			/**
			 * 切换产品类型
			 */
			changeWideProductType : function() 
			{
				var wideProductType = $("#WIDE_PRODUCT_TYPE").val();

				// 只有有移动FTTH和铁通FTTH需要申请光猫
				if ('3' != wideProductType && '5' != wideProductType)
				{
				    $("#modemDiv").css("display", "none");
					
					$("#MODEM_STYLE").val('');
					$("#MODEM_DEPOSIT").val('0');
					
					//清空光猫押金费用
					$.nophonewideusercreate.PAGE_FEE_LIST.clear();
					$.feeMgr.removeFee($("#TRADE_TYPE_CODE").val(),"2","9002");
				}
				else
				{
					$("#modemDiv").css('display', '');
				}
				
				
				$.beginPageLoading("产品信息查询。。。");
				
				$.ajax.submit(this, 'changeWideProductType',
						'&wideProductType=' + wideProductType, 'productType,modemPart',
						function(data) 
						{
							$("#MODEM_STYLE").val("");
							$("#MODEM_DEPOSIT").val("0");
							$("#MODEM_STYLE").attr("disabled", false);
							
							$.nophonewideusercreate.initProduct();
							$.endPageLoading();
						}, function(error_code, error_info) 
						{
							$.endPageLoading();
							$.MessageBox.error(error_code, error_info);
						});
			},
			
			changeProduct : function() 
			{
				$.feeMgr.clearFeeList($("#TRADE_TYPE_CODE").val());
				var productId = $("#WIDE_PRODUCT_ID").val();
				var eparchyCode = "0898";
				var param = "&NEW_PRODUCT_ID=" + productId;
				
				offerList.renderComponent(productId,eparchyCode);
				selectedElements.renderComponent(param, eparchyCode);
				
				//光猫费用
				var modemFeeData = $.nophonewideusercreate.PAGE_FEE_LIST.get("MODEM_FEE");
				
				if (null != modemFeeData && '' != modemFeeData)
				{
					$.feeMgr.insertFee(modemFeeData);
				}
			},

			initPage : function() 
			{
				//给账户信息设置默认值，并隐藏不需要的
				$("#PAY_MODE_CODE").val('0');
				$("#ACCT_DAY").val('1');
				
				$("#PAY_MODE_CODE").attr("disabled",true);
				$("#ACCT_DAY").attr("disabled",true);
				
				$("#SuperBankCodeLi").css('display', 'none');
				$("#BankCodeLi").css('display', 'none');
				$("#BankAcctNoLi").css('display', 'none');
				$("#bankAgreementNoLi").css('display', 'none');
                $.ajax.submit(null, 'getCreateWideUserStyle',null,'mergeWideUserStylePart',function(data){
                    if (null != data)
                    {
                        $("#HGS_WIDE").empty();

                        for (var i = 0; i < data.length; i++)
                        {
                            $("#HGS_WIDE").append("<option value='"+data.items[i].get("DATA_ID")+"'>"+data.items[i].get("DATA_NAME")+"</option>");
                        }
                    }
				}, function(error_code, error_info, derror) {
                    $.MessageBox.error(error_code, error_info);
                });
				$.ajax.submit(null, 'queryPsptTypeList',null,null,
					function(data) 
					{
						var psptTypeList = data.get("PSPT_TYPE_LIST");
						
						if (null != psptTypeList)
						{
							$("#PSPT_TYPE_CODE").empty();
							
							for (var i = 0; i < psptTypeList.length; i++)
							{
								$("#PSPT_TYPE_CODE").append("<option value='"+psptTypeList.items[i].get("DATA_ID")+"'>"+psptTypeList.items[i].get("DATA_NAME")+"</option>");
							}
						}
						
//						$("#PSPT_ID").attr("disabled",true);
//						$("#CUST_NAME").attr("disabled",true);
//						$("#PSPT_ADDR").attr("disabled",true);
//						$("#BIRTHDAY").attr("disabled",true); 
						
						$.endPageLoading();
					}, function(error_code, error_info, derror) {
						$.endPageLoading();
						$.MessageBox.error(error_code, error_info);
					});

			},

			
			afterRenderSelectedElements : function(data) {
				var temp = data.get(0);
				$("#NEW_PRODUCT_START_DATE").val(temp.get("NEW_PRODUCT_START_DATE"));
			},

			onSubmit : function() 
			{
				var serialNum = $("#SERIAL_NUMBER").val();
				
				var wideProductType = $("#WIDE_PRODUCT_TYPE").val();
				
				// 移动FTTH和铁通FTTH需要申请光猫
				if ('3' == wideProductType || '5' == wideProductType)
				{
					var modemStyle = $("#MODEM_STYLE").val();
					
					if (null == modemStyle || '' == modemStyle)
					{
						MessageBox.alert("告警提示","FTTH宽带必须申领光猫，请选择MODEM方式！");
						
						return false;
					}
				}

				if (!verifyAll('widePart')) 
				{
					return false;
				}
				if (!verifyAll('modemPart'))
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
				   
				 //2015-04-13 密码加密
			    if($("#USER_PASSWD").val()!=null&&$("#USER_PASSWD").val()!=""&&$("#USER_PASSWD").val().length==6){
				   var newPwd=$("#USER_PASSWD").val(); 
				   $.getScript("scripts/csserv/common/des/des.js",function(){  
						var data=newPwd;
						var firstKey="c";
						var secondKey="x"
						var thirdKey="y"
						$("#USER_PASSWD").val(strEnc(data,firstKey,secondKey,thirdKey)+"xxyy");
					});
			    }
				
				var modemDeposit = $("#MODEM_DEPOSIT").val();
				
				if (null == modemDeposit || '' == modemDeposit)
				{
					modemDeposit = '0';
				}
				
				var widenetAcctId = $("#SERIAL_NUMBER").val();
				var oldWidenetAcctId = $("#OLD_WIDENET_ACCT_ID").val();
				
				if (widenetAcctId != oldWidenetAcctId)
				{
					alert("宽带服务号码未校验通过！");
			        return false;
				}
				
				
				if($("#REAL_NAME").val()=="1" && $("#REALNAME_LIMIT_CHECK_RESULT").val() != "true")
				{
			        alert("实名制开户证件校验未通过！");
			        return false;
			    }
				
				var data = selectedElements.getSubmitData();
				if (data && data.length > 0) 
				{
					var param = "&SELECTED_ELEMENTS=" + data.toString();
					$.cssubmit.addParam(param);
				} 
				else
				{
					alert('您未选择开户产品，不能提交！');
					return false;
				}
				
				
				var modemFee = "0";
				var wideFee = "0";
				var totalFee = 0;
				
				var tradeFeeSubList = $.feeMgr.getFeeList();
				
				if(tradeFeeSubList && tradeFeeSubList.length > 0)
				{
					for (var i = 0; i < tradeFeeSubList.length; i++) 
					{
						var item = tradeFeeSubList.get(i);
						var feeTypeCode = item.get("FEE_TYPE_CODE");					//费用类型编码
						var fee = item.get("FEE");	
						
						if ('9002' == feeTypeCode)
						{
							modemFee = fee;
						}
						else if ('9021' == feeTypeCode)
						{
							wideFee = fee;
						}
						
						totalFee += parseInt(fee);
					}
					
					if (totalFee > 0)
					{
						var tips = "您总共需缴纳现金："+totalFee/100+"元,其中预存宽带款："+parseFloat(wideFee)/100+"元，光猫冻结预存款：" +parseFloat(modemFee)/100+"元。"
								
						alert(tips);
					}
				}	
				
				return true;
			},

			setModemNumeric : function() 
			{
				var serialNum = $("#SERIAL_NUMBER").val();
				
				if (null == serialNum || '' == serialNum)
				{
					MessageBox.alert("告警提示","请先录入宽带账号！");
					
					$("#MODEM_STYLE").val('');
					
					return false;
				}
				
				if ($("#MODEM_STYLE").val() == "") 
				{
					$("#MODEM_DEPOSIT").val("0");
					
					//清楚光猫押金预存
					$.nophonewideusercreate.PAGE_FEE_LIST.clear();
					$.feeMgr.removeFee($("#TRADE_TYPE_CODE").val(),"2","9002");
					return false;
				}
				
				//如果是租用
				if ($("#MODEM_STYLE").val() == "0") 
				{
					//获取光猫租赁费用
					$.ajax.submit(null, 'getModemDeposit', null, null,
							function(data) 
							{
								$.nophonewideusercreate.PAGE_FEE_LIST.clear();
								
								$.feeMgr.removeFee($("#TRADE_TYPE_CODE").val(),"2","9002");
								
								var feeData = $.DataMap();
								feeData.clear();
								feeData.put("MODE", "2");
								feeData.put("CODE", "9002");
								feeData.put("FEE", data.get("MODEM_DEPOSIT"));
								feeData.put("PAY", data.get("MODEM_DEPOSIT"));
								feeData.put("TRADE_TYPE_CODE", $("#TRADE_TYPE_CODE").val());
								$.feeMgr.insertFee(feeData);
								$.nophonewideusercreate.PAGE_FEE_LIST.put("MODEM_FEE", $.feeMgr.cloneData(feeData));

								$("#MODEM_DEPOSIT").val(data.get("MODEM_DEPOSIT")/100);
								
							}, function(error_code, error_info) 
							{
								$.endPageLoading();
								$.MessageBox.error(error_code, error_info);
							});
				} 
				else
				{
					$("#MODEM_DEPOSIT").val('0');
					
					$.nophonewideusercreate.PAGE_FEE_LIST.clear();
					$.feeMgr.removeFee($("#TRADE_TYPE_CODE").val(),"2","9002");
				}
			},
			
			setContactInfo : function() 
			{
				var wideContactphone = $("#WIDE_CONTACT_PHONE").val();
				var wideContact = $("#WIDE_CONTACT").val();
				var widePhone = $("#WIDE_PHONE").val();
				var phone = $("#PHONE").val();
				var custName = $("#CUST_NAME").val();
				
				//将客户信息的手机号码设置到宽带信息中
				if (null != phone && '' != phone)
				{
					if (null == wideContactphone || '' == wideContactphone)
					{
						$("#WIDE_CONTACT_PHONE").val(phone);
					}
					
					if (null == widePhone || '' == widePhone)
					{
						$("#WIDE_PHONE").val(phone);
					}
				}
				
				//将客户姓名设置到宽带联系人
				if (null != custName && '' != custName)
				{
					if (null == wideContact || '' == wideContact)
					{
						$("#WIDE_CONTACT").val(custName);
					}
				}
			},
			
			/**
				选择预约时间后校验
			*/
			afterSelSuggestDate:function(obj)
			{
				var suggerDate = $("#SUGGEST_DATE").val();
				
				//判断预约选择的时间是否在工作时间范围内，每天8点-16点为工作时间
				var strHour = suggerDate.substr(11,2);
				var h = Number(strHour);
				if(h < 8 || h >= 16)
				{
					alert('预约施工时间为每天8：00--16：00,请重新选择时间!');
					$("#SUGGEST_DATE").val('');
					return ;
				}
			}
		}
	}
})(Wade);


/*密码组件前校验*/
function PasswdbeforeAction() {
	 if($("#PSPT_TYPE_CODE").val()==""){
      alert("证件类型不能为空！");
      return false;
    }
     if($("#PSPT_ID").val()==""){
      alert("证件号码不能为空！");
      return false;
    }
     
    //将值赋给组件处理
    var psptId =$("#PSPT_ID").val();
    var serialNumber = $("#SERIAL_NUMBER").val();
    $.password.setPasswordAttr(psptId, serialNumber);
    return true ;
}

/*密码组件后赋值*/
function PasswdafterAction(data) 
{
	$("#USER_PASSWD").val(data.get("NEW_PASSWORD"));
}

/**
 * 9级地址改造涉及
 * @author zhuoyingzhi
 * 20161014
 */
function afterFloorAndRoomNum(){
	 //获取楼层和房号
	if($("#FLOOR_AND_ROOM_NUM")){
		var floor_and_room_num=$("#FLOOR_AND_ROOM_NUM").val();
		if(floor_and_room_num !=''){
			 //有值
			$("#FLOOR_AND_ROOM_NUM").attr("disabled", true);
			
			//给楼层隐藏赋值  1 
			$("#FLOOR_AND_ROOM_NUM_FLAG").val('1');
		}else{
			//没有值
			$("#FLOOR_AND_ROOM_NUM").val(''); 
			$("#FLOOR_AND_ROOM_NUM").attr("disabled", false);
			
			//给楼层隐藏赋值
			$("#FLOOR_AND_ROOM_NUM_FLAG").val('0');
		}
	}
}
