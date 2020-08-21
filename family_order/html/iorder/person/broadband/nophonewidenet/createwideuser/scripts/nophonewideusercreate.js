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
				    $("#modemPart").css("display", "none");
					
					$("#MODEM_STYLE").val('');
					$("#MODEM_DEPOSIT").val('0');
					
					//清空光猫押金费用
					$.nophonewideusercreate.PAGE_FEE_LIST.clear();
					$.feeMgr.removeFee($("#TRADE_TYPE_CODE").val(),"2","438");
				}
				else
				{
					$("#modemPart").css('display', '');
				}

                var twoCity = $("#TWO_CITY_SWITCH").val();
                var netWide = $("#NET_WIDE").val();

                var param = '&wideProductType=' + wideProductType
                    + '&TWO_CITY_WIDENET_FLAG=' + twoCity + '&NET_WIDE=' + netWide;

				$.beginPageLoading("产品信息查询。。。");
				
				$.ajax.submit(this, 'changeWideProductType', param, 'productType,modemPart,topSetBoxPart',
						function(data) 
						{
							$("#MODEM_STYLE").val("");
							$("#MODEM_DEPOSIT").val("0");
							$("#MODEM_STYLE").attr("disabled", false);
							
							$.nophonewideusercreate.initProduct();
							// 两城两宽用户仅允许光猫"租赁"
							if ('1' === $('#TWO_CITY_SWITCH').val()) $('#MODEM_STYLE').val('0');
							$.endPageLoading();
						}, function(error_code, error_info) 
						{
							$.endPageLoading();
							$.MessageBox.error(error_code, error_info);
						});
			},
			
			//根据产品ID获取产品速率
			getProductRateById:function(newProductId){
				var new_rate = "0";
				var productList = nophonewideusercreate.PRODUCT_LIST;
				if(productList != null && productList != '')
				{
					for (var i = 0; i < productList.length; i++)
					{
						if (newProductId == productList.get(i).get('PRODUCT_ID'))
						{
							new_rate = productList.get(i).get('WIDE_RATE');
						}
					}
				}
				return new_rate;
			},
			
			changeProduct : function() 
			{
				$.feeMgr.clearFeeList($("#TRADE_TYPE_CODE").val());
				var productId = $("#WIDE_PRODUCT_ID").val();
				var booktag = $("#BOOKTAG").val();

				var eparchyCode = "0898";
				var param = "&NEW_PRODUCT_ID=" + productId +"&BOOKTAG=" + booktag;
				
				//add by zhangxing3 for REQ201808030011优化200M及以上的宽带产品业务流程
				/*$.beginPageLoading("宽带产品速率查询。。。");
				$.ajax.submit(this, 'getProductRateByProductId',param, '',
						function(data) 
						{
							var new_rate = data.get("NEW_RATE");
							var bandwidth = $("#BANDWIDTH").val();
					        if((parseInt(new_rate) >= 1000*1024 && "200" == bandwidth)
					        		|| (parseInt(new_rate) >= 200*1024 && "" == bandwidth))
					        {
					        	MessageBox.alert("提示","您所选择的设备暂不支持"+parseInt(new_rate)/1024+"M产品");
					        	$("#WIDE_PRODUCT_ID").val("");
				                return false;
					        }
							$.endPageLoading();
						}, function(error_code, error_info) 
						{
				        	$("#WIDE_PRODUCT_ID").val("");
							$.endPageLoading();
							$.MessageBox.error(error_code, error_info);
						});*/
				//add by zhangxing3 for REQ201808030011优化200M及以上的宽带产品业务流程
				 
				 
				
				offerList.renderComponent(productId,eparchyCode);
				selectedElements.renderComponent(param, eparchyCode);
				
				//光猫费用
				var modemFeeData = $.nophonewideusercreate.PAGE_FEE_LIST.get("MODEM_FEE");
				
				if (null != modemFeeData && '' != modemFeeData)
				{
					$.feeMgr.insertFee(modemFeeData);
				}

				// 魔百和费用
                var topboxFeeData = $.nophonewideusercreate.PAGE_FEE_LIST.get("TOP_SET_BOX_FEE");
				if (null != topboxFeeData && '' != topboxFeeData) $.feeMgr.insertFee(topboxFeeData);

				// 机顶盒费用
                var topmodemFeeData = $.nophonewideusercreate.PAGE_FEE_LIST.get("TOP_SET_BOX_DEPOSIT");
                if (null != topmodemFeeData && '' != topmodemFeeData) $.feeMgr.insertFee(topmodemFeeData);
                if ("1" == booktag)
                {
    				$.feeMgr.clearFeeList($("#TRADE_TYPE_CODE").val());
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
                $.ajax.submit(null, 'getCreateWideUserStyle',null,null,function(data){
                    if (null != data)
                    {
                        HGS_WIDE.empty();

                        for (var i = 0; i < data.length; i++)
                        {
                            HGS_WIDE.append(data.get(i, "DATA_NAME"), data.get(i, "DATA_ID"));
                        }
                        $('#HGS_WIDE').val('0');
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
							PSPT_TYPE_CODE.empty();
							
							for (var i = 0; i < psptTypeList.length; i++)
							{
								PSPT_TYPE_CODE.append(psptTypeList.get(i, "DATA_NAME"),psptTypeList.get(i, "DATA_ID"));
							}
							$("#PSPT_TYPE_CODE").val("0");
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
				var wideProductType = $("#WIDE_PRODUCT_TYPE").val();
				
				// 移动FTTH和铁通FTTH需要申请光猫
				if ('3' == wideProductType || '5' == wideProductType)
				{
					var modemStyle = $("#MODEM_STYLE").val();
					var wideModeFee = $("#WIDE_MODE_FEE").val();
					if('0' == modemStyle && "" == wideModeFee){
						MessageBox.alert("告警提示","租赁光猫，调测费不能为空！");						
						return false;
					}

					if (null == modemStyle || '' == modemStyle)
					{
						MessageBox.alert("告警提示","FTTH宽带必须申领光猫，请选择MODEM方式！");
						
						return false;
					}
				}
                if (!verifyAll('widePart')) {
                    return false;
                }
                if (!verifyAll('modemPart')) {
                    return false;
                }
                if (!verifyAll('BaseInfoPart')) {
                    return false;
                }
                if (!verifyAll('CustInfoPart')) {
                    return false;
                }
                if (!verifyAll('AcctInfoPart')) {
                    return false;
                }
                if (!verifyAll('PasswdPart')) {
                    return false;
                }
                if (!verifyAll('wideProductPart')) {
                    return false;
                }                
				var topSetBoxProductId = $('#TOP_SET_BOX_PRODUCT_ID').val();
                if (null != topSetBoxProductId && '' !== topSetBoxProductId) {
				    var checkResultCode = $('#CHECK_RESULT_CODE').val(); // 校验通过标识
                    if ('-1' === checkResultCode) {
                        MessageBox.alert('提示', '宽带账号未校验通过！');
                        $('#SERIAL_NUMBER').focus();
                        return false;
                    } 
                    
//                  else if ('0' === checkResultCode) {
//                  MessageBox.alert('提示', 'SIM卡号校验未通过！');
//                  $('#SIM_CARD_NO').focus();
//                  return false;
//              }

                                        
                    if (!verifyAll('topSetBoxProductPart')) {
                        return false;
                    }
					var base = $("#BASE_PACKAGES").val();
					if(base==""){
						MessageBox.alert('告警提示',"开通魔百和，基础包为必选！");
                        return false;
                    }
					
					var boxModeFee = $("#BOX_MODE_FEE").val();
					if(boxModeFee==""){
						MessageBox.alert('告警提示',"开通魔百和，魔百和调测费为必选！");
						return false;
					}

                }else{
                	$("#BOX_MODE_FEE").val("");
                }
                
                var simTag = $('#INPUT_SIM_TAG').val();
                if("Y" === simTag){
    			    var checkResultCode = $('#CHECK_RESULT_CODE').val(); // 校验通过标识
                	if('0' === checkResultCode){//如果页面显示了si'm卡号，就得检验
                        MessageBox.alert('提示', 'SIM卡号校验未通过！');
                        $('#SIM_CARD_NO').focus();
                        return false;
                	}                    	
                }
                
				//2015-04-13 密码加密
			    if($("#USER_PASSWD").val()!=null&&$("#USER_PASSWD").val()!=""&&$("#USER_PASSWD").val().length==6){
				   var newPwd=$("#USER_PASSWD").val(); 
				   $.getScript("scripts/csserv/common/des/des.js",function(){  
						var data=newPwd;
						var firstKey="c";
						var secondKey="x";
						var thirdKey="y";
						$("#USER_PASSWD").val(strEnc(data,firstKey,secondKey,thirdKey)+"xxyy");
					});
			    }
			
				var widenetAcctId = $("#SERIAL_NUMBER").val();
				var oldWidenetAcctId = $("#OLD_SERIAL_NUMBER").val();
			
				if (widenetAcctId != oldWidenetAcctId)
				{
					MessageBox.alert("提示","宽带服务号码未校验通过！");
			        return false;
				}
				// 两城两宽开户不校验实名制
				if('0' === $('#TWO_CITY_SWITCH').val() && $("#REAL_NAME").val()=="1" && $("#REALNAME_LIMIT_CHECK_RESULT").val() != "true")
				{
			        MessageBox.alert("提示","实名制开户证件校验未通过！");
			        return false;
			    }
			
				var data = selectedElements.getSubmitData();

				//REQ201807230029一机多宽业务需求1.0版本
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
				var strPaySerialNumber = $("#PAY_SERIAL_NUMBER").val();
				if(strIsOneSnManyWide == '1'&&!oneSnFlag)
				{
					MessageBox.alert("提示","一机多宽无手机宽带开户必须选择一机多宽优惠！");
					return false;
				}
				if(strIsOneSnManyWide == '0'&&oneSnFlag)
				{
					MessageBox.alert("提示","此资费只适合一机多宽模式！");
					return false;
				}
				if ( '1' == strIsOneSnManyWide && ('3' != wideProductType && '5' != wideProductType))
				{
					MessageBox.alert("提示","一机多宽,请选择支持FTTH制式宽带的小区！");
					return false;
				}
				
				if(strIsOneSnManyWide == '1'&&oneSnFlag){
					 $.beginPageLoading("校验付费号码。。。");
		             $.ajax.submit('', 'checkPaySerialNumber','&PAY_SERIAL_NUMBER=' + strPaySerialNumber, '',function(data) {
		                    $.endPageLoading();
		                }, function(error_code, error_info)
		                {
		                    $.endPageLoading();
		                    $.MessageBox.error(error_code, error_info);
		                    return false;
		              },{
				            "async": false
				        });
				}
               

                $.cssubmit.setParam("IS_ONESN_MANYWIDE",strIsOneSnManyWide);
                $.cssubmit.setParam("PAY_SERIAL_NUMBER",strPaySerialNumber);
                //REQ201807230029一机多宽业务需求1.0版本
                
                //add by zhangxing3 for REQ201807310012无手机号码候鸟宽带资费
                var discntTag = false;
                for (var i=0;i<data.getCount();i++) {
                    var selElement = data.get(i);
                    var elementType = selElement.get("ELEMENT_TYPE_CODE", "");
                    if(elementType=="D")
                    {
                        discntTag = true;
                    }
                }
                if (!discntTag)
                {
                    MessageBox.alert("提示","你未选择无手机宽带优惠，不能提交！");
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
			        }, {
			            "async": false
			        });
			    
			    //REQ201809300014新增线上无手机宽带开户功能的需求—BOSS新增界面 
			    var bookTag = $("#BOOKTAG").val();
			    if("1" == bookTag) //线上无手机宽带预约,且有线上免人像比对权限，则不用人像比对
			    {
			    	cmpTag = "1"; 
			    }
			    if("2" == bookTag) //线上无手机宽带预约,且无线上免人像比对权限，则强制进行人像比对
			    {
			    	cmpTag = "0"; 
			    }
			    //REQ201809300014新增线上无手机宽带开户功能的需求—BOSS新增界面 
			    
			    //无手机宽带开户摄像提示
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
			    
			    var submitData = selectedElements.getSubmitData();
			    //alert("--------submitData-------"+submitData);

			    if (submitData.length === 0) {
			        MessageBox.alert("您没有选择产品信息，请选择产品！");
			        return false;
			    }
			    var inParam = "&SELECTED_ELEMENTS=" + submitData.toString();
//			    //关于第二批次调测费开发需求--开发时间要求   提交组件会拼此参数，导致后台预留字段存值有误，魔百和取消有问题  modify_by_duhj_kd
//				if (!(null == $("#BOX_MODE_FEE").val() || '' == $("#BOX_MODE_FEE").val()))
//				{
//					inParam = inParam+"&BOX_MODE_FEE=" + $("#BOX_MODE_FEE").val()
//				}
			    
			    $.cssubmit.addParam(inParam);

				var modemFee = "0";
				var wideFee = "0";
				var topSetBoxDeposit = "0";
				var topSetBoxFee = "0";
				var totalFee = 0;
				
				var tradeFeeSubList = $.feeMgr.getFeeList();
				if(tradeFeeSubList && tradeFeeSubList.length > 0)
				{
					for (var i = 0; i < tradeFeeSubList.length; i++) 
					{
						var item = tradeFeeSubList.get(i);
						var feeTypeCode = item.get("FEE_TYPE_CODE");					//费用类型编码
						var fee = item.get("FEE");	
						
						if ('438' == feeTypeCode)
						{
							modemFee = fee;
						}
						else if ('9021' == feeTypeCode)
						{
							wideFee = fee;
						}
						else if ('439' == feeTypeCode)
						{
							topSetBoxDeposit = fee; //魔百和调测费
						}
						else if('9082' == feeTypeCode)
						{
							topSetBoxFee = fee;     // 魔百和费用
						}

						totalFee += parseInt(fee);
					}
					
					if (totalFee > 0)
					{
						var tips = "您总共需缴纳现金："+totalFee/100+"元,其中预存宽带款："+parseFloat(wideFee)/100+"元，光猫调测费：" +parseFloat(modemFee)/100+"元，魔百和调测费"+parseFloat(topSetBoxDeposit)/100+"元，魔百和费用："+parseFloat(topSetBoxFee)/100+"元。";
						
						MessageBox.alert("提示",tips);
					}
				}
				return true;
			},
			
			setModemFee : function() 
			{
				if ($("#WIDE_MODE_FEE").val() == "" ||$("#WIDE_MODE_FEE").val() == null) {
                    $.feeMgr.removeFee($("#TRADE_TYPE_CODE").val(),"2","438");
				}else{
					if ($("#MODEM_STYLE").val() == "0" && "1" != $("#BOOKTAG").val()) {
						$.nophonewideusercreate.PAGE_FEE_LIST.clear();
	                    $.feeMgr.removeFee($("#TRADE_TYPE_CODE").val(),"2","438");
	                    //获取光猫租赁费用
	                    $.ajax.submit(null, 'getModemDeposit', null, null,
	                        function (data) {
	                            $.nophonewideusercreate.PAGE_FEE_LIST.clear();
	                            $.feeMgr.removeFee($("#TRADE_TYPE_CODE").val(),"2","438");

	                            var feeData = $.DataMap();
	                            feeData.clear();
	                            feeData.put("MODE", "2");
	                            feeData.put("CODE", "438");
	                            feeData.put("FEE", data.get("MODEM_DEPOSIT"));
	                            feeData.put("PAY", data.get("MODEM_DEPOSIT"));
	                            feeData.put("TRADE_TYPE_CODE", $("#TRADE_TYPE_CODE").val());
	                            $.feeMgr.insertFee(feeData);
	                            $.nophonewideusercreate.PAGE_FEE_LIST.put("MODEM_FEE", $.feeMgr.cloneData(feeData));

	                            $("#MODEM_DEPOSIT").val(data.get("MODEM_DEPOSIT")/100);
	                        }, function (error_code, error_info) {
	                            $.endPageLoading();
	                            $.MessageBox.error(error_code, error_info);
	                        });
	                } else {
	                    $("#MODEM_DEPOSIT").val('0');
	                    $.nophonewideusercreate.PAGE_FEE_LIST.clear();
	                    $.feeMgr.removeFee($("#TRADE_TYPE_CODE").val(),"2","438");
	                }
				}
				
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
				if ($("#MODEM_STYLE").val() != "0") 
				{
					$("#WIDE_MODE_FEE").val("");
				}
								
				if ($("#MODEM_STYLE").val() == "") 
				{
					$("#MODEM_DEPOSIT").val("0");
					
					//清除光猫押金预存
					$.nophonewideusercreate.PAGE_FEE_LIST.clear();
					$.feeMgr.removeFee($("#TRADE_TYPE_CODE").val(),"2","438");
					return false;
				}

				//如果是两城两宽开户，免光猫、魔百和押金
				if('1' === $('#TWO_CITY_SWITCH').val()) {
				    $('#MODEM_STYLE').val('0');
					$('#MODEM_DEPOSIT').val('0');
					$.nophonewideusercreate.PAGE_FEE_LIST.clear();
					$.feeMgr.removeFee($('#TRADE_TYPE_CODE').val(), '2', '438');
				} else {
                    //如果是租用
                    if ($("#MODEM_STYLE").val() == "0" && "1" != $("#BOOKTAG").val()) {
                    	$("#modemFeePart").css('display', '');
                        //获取光猫租赁费用
//                        $.ajax.submit(null, 'getModemDeposit', null, null,
//                            function (data) {
//                                $.nophonewideusercreate.PAGE_FEE_LIST.clear();
//                                $.feeMgr.removeFee($("#TRADE_TYPE_CODE").val(),"2","9002");
//
//                                var feeData = $.DataMap();
//                                feeData.clear();
//                                feeData.put("MODE", "2");
//                                feeData.put("CODE", "9002");
//                                feeData.put("FEE", data.get("MODEM_DEPOSIT"));
//                                feeData.put("PAY", data.get("MODEM_DEPOSIT"));
//                                feeData.put("TRADE_TYPE_CODE", $("#TRADE_TYPE_CODE").val());
//                                $.feeMgr.insertFee(feeData);
//                                $.nophonewideusercreate.PAGE_FEE_LIST.put("MODEM_FEE", $.feeMgr.cloneData(feeData));
//
//                                $("#MODEM_DEPOSIT").val(data.get("MODEM_DEPOSIT")/100);
//                            }, function (error_code, error_info) {
//                                $.endPageLoading();
//                                $.MessageBox.error(error_code, error_info);
//                            });
                    } else {
                    	$("#modemFeePart").css('display', 'none');
                        $("#MODEM_DEPOSIT").val('0');
                        $.nophonewideusercreate.PAGE_FEE_LIST.clear();
                        $.feeMgr.removeFee($("#TRADE_TYPE_CODE").val(),"2","438");
                    }
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
				if(h < 8 || h >= 16)
				{
					MessageBox.alert("提示",'预约施工时间为每天8：00--16：00,请重新选择时间!');
					$("#SUGGEST_DATE").val('');
					return ;
				}
			},

			// 两城两宽标识
            twoCitySwitchAction: function(obj) {
			    var $BJSerialLi = $('#BJ_SERIAL_NUMBER_LI'),
                    $BJSerial = $('#BJ_SERIAL_NUMBER');

			    if ('1' === obj.value) {
			        $BJSerialLi.css('display', '');
			        $BJSerial.attr('nullable', 'no');
                    // 两城两宽免押金
                    $('#MODEM_DEPOSIT').val('0');
                    $.nophonewideusercreate.PAGE_FEE_LIST.clear();
                    $.feeMgr.removeFee($('#TRADE_TYPE_CODE').val(), '2', '438');
                } else {
			        $BJSerialLi.css('display', 'none');
                    $BJSerial.attr('nullable', 'yes').val('');
                    $('#PSPT_ID').val('');
                    $('#PSPT_ADDR').val('');
                    $('#CUST_NAME').val('');
                    $('#PSPT_TYPE_CODE').val('');
                    $('#PHONE').val('');
                    $('#NET_WIDE').val('');
                    $('#PAY_NAME').val('');
                }
            },

            // 校验北京移动号码
            checkBJSerialNumber: function() {
			    setTimeout(function () { // 为了保证关闭两城两宽开关时校验方法晚于开关onchange方法执行，所以设置500ms延迟
                    if ('1' === $('#TWO_CITY_SWITCH').val()) { // 仅两城两宽开关开启状态才进行校验
                        var BJSerial = $('#BJ_SERIAL_NUMBER').val();

                        if (!$.validate.verifyField('BJ_SERIAL_NUMBER')) return false;

                        $.ajax.submit(null, 'checkBJWidenetSn', '&BJ_SERIAL_NUMBER=' + BJSerial, null,
                            function (ajaxData) {
                                var $psptId = $('#PSPT_ID'),
                                    psptId = ajaxData.get('PSPT_ID'),
                                    $psptTypeCode = $('#PSPT_TYPE_CODE'),
                                    psptTypeCode = ajaxData.get('PSPT_TYPE_CODE');

                                $('#PSPT_ADDR').val(ajaxData.get('PSPT_ADDR'));
                                $('#CUST_NAME').val(ajaxData.get('CUST_NAME'));
                                if ('1' === psptTypeCode) { // 身份证类型校验证件号码
                                    $psptTypeCode.val(psptTypeCode);
                                    $psptId.val(psptId);
                                    $psptId.trigger('change');
                                } else { // 非身份证类型不校验证件号码，出生日期可为空
                                    PSPT_TYPE_CODE.empty();
                                    PSPT_TYPE_CODE.append(ajaxData.get('PSPT_TYPE_NAME'), psptTypeCode);
                                    $psptTypeCode.unbind().val(psptTypeCode);
                                    $psptId.unbind().val(psptId);
                                    $('#BIRTHDAY').attr('nullable', 'yes');
                                }
                                $('#PHONE').val(ajaxData.get('PHONE'));
                                $('#NET_WIDE').val(ajaxData.get('NET_WIDE'));
                                $('#PAY_NAME').val(ajaxData.get('CUST_NAME'));
                                $("#SHOW_EDIT_AREA").trigger('click');
                            },
                            function (error_code, error_info) {
                                $('#BJ_SERIAL_NUMBER').val('');
                                MessageBox.error(BJSerial + "号码校验失败", error_info);
                            });
                    }
                }, 500);
            },
            changeIsOneSnManyWide : function()
    		{
    			var strIsOneSnManyWide = $("#IS_ONESN_MANYWIDE").val();
    			if(strIsOneSnManyWide == '1')
    			{
    				$("#PAY_SERIAL_NUMBER").attr("nullable","no");
    				$("#li_PAY_SERIAL_NUMBER").css('display', '');
    				$("#PAY_SERIAL_NUMBER").attr("disabled", false);
    			}
    			else
    			{
    				$("#PAY_SERIAL_NUMBER").attr("nullable","yes");
    				$("#li_PAY_SERIAL_NUMBER").css('display', 'none');
    				$("#PAY_SERIAL_NUMBER").val("");
    				$("#PAY_SERIAL_NUMBER").attr("disabled", true);
    			}
    		},
    		changePaySerialNumber : function()
    		{
    			var strPaySerialNumber = $("#PAY_SERIAL_NUMBER").val();
    			var psptId = $("#PSPT_ID").val();
    			//是否手机号码校验
    			if(!$.verifylib.checkMbphone(strPaySerialNumber)){
    				MessageBox.alert("提示",'统付主号码格式不正确，请重新输入！');
    				$("#PAY_SERIAL_NUMBER").val("");
    				$("#PAY_SERIAL_NUMBER").focus();
    				return;
    			}
    			$.beginPageLoading("身份证号校验。。。");
    			$.ajax.submit(null, 'getPsptBySn', '&SERIAL_NUMBER=' + strPaySerialNumber, null,
    					function(data) 
    					{
    						$.endPageLoading();
    						var psptIdPay=data.get('PSPT_ID');
    						if(psptId!=psptIdPay){
    							MessageBox.alert("提示","统付主号码的身份证号和界面上输入的身份证号必须一致");
    							$("#PAY_SERIAL_NUMBER").val("");
    							$("#PAY_SERIAL_NUMBER").focus();
    							return false;
    						}
    					}, function(error_code, error_info) 
    					{
    						$.endPageLoading();
    						$.MessageBox.error(error_code, error_info);
    						$("#PAY_SERIAL_NUMBER").val("");
    						$("#PAY_SERIAL_NUMBER").focus();
    						return false;
    					}
    				);
    			$.beginPageLoading("校验付费号码。。。");
	             $.ajax.submit('', 'checkPaySerialNumber','&PAY_SERIAL_NUMBER=' + strPaySerialNumber, null,
	            		function(data) 
	            		{
	                    	$.endPageLoading();
	            		}, function(error_code, error_info)
	            		{
		                	$.endPageLoading();
							$.MessageBox.error(error_code, error_info);
							$("#PAY_SERIAL_NUMBER").val("");
							$("#PAY_SERIAL_NUMBER").focus();
							return false;
	            		}
	            	);
    		}
		}
	}
})(Wade);


/*密码组件前校验*/
function PasswdbeforeAction() {
	 if($("#PSPT_TYPE_CODE").val()==""){
		 MessageBox.alert("告警提示","证件类型不能为空！");
      return false;
    }
     if($("#PSPT_ID").val()==""){
    	 MessageBox.alert("告警提示","证件号码不能为空！");
      return false;
    }
     if($("#SERIAL_NUMBER").val()==""){
    	 MessageBox.alert("告警提示","宽带账号不能为空！");
    	 return false;
     }
     
    //将值赋给组件处理
    var psptId =$("#PSPT_ID").val();
    var serialNumber = $("#SERIAL_NUMBER").val();
    $.password.setPasswordAttr(psptId, serialNumber);
    return true ;
}

/*密码组件后赋值*/
function PasswdafterAction(data) {
	$("#USER_PASSWD").val(data.get("NEW_PASSWORD"));
}

/**
 * 9级地址改造涉及
 * @author zhuoyingzhi
 * 20161014
 */
function afterFloorAndRoomNum() {
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

/******************** 号码和SIM卡校验 开始 ********************/
// 重置手机号码校验
function resetSNCheck() {
    $("#SN_CHECK_BTN").css("display", "");
    $("#SN_SUCCESS_LABEL").css("display", "none");
    $("#SN_ERROR_LABEL").css("display", "none");
    $("#SIM_CHECK_BTN").css("display", "");
    $("#SIM_SUCCESS_LABEL").css("display", "none");
    $("#SIM_ERROR_LABEL").css("display", "none");
}

// 校验手机号码失败
function checkSerialNumberFailed() {
    $("#SN_CHECK_BTN").css("display", "none");
    $("#SN_ERROR_LABEL").css("display", "");
}

// 校验手机号码成功
function checkSerialNumberSucceed() {
    $("#SN_CHECK_BTN").css("display", "none");
    $("#SN_SUCCESS_LABEL").css("display", "");
    $.custInfo.setDefaultValueAfterSerialNumberCheck();
}

// 绑定魔百和新开户号码校验
function checkOldSNPwd() {
    if (!$.validate.verifyField('SERIAL_NUMBER')) return false;

    var $serialNumber = $("#SERIAL_NUMBER"),
        serialNumber = $serialNumber.val(),
        oldSerialNumber = $("#OLD_SERIAL_NUMBER").val(),
        $checkCode = $("#CHECK_RESULT_CODE");

    if (serialNumber === "") {
        MessageBox.alert("提示","请选择宽带账号后再进行校验！");
        return false;
    }

    // 同一个号码时，不再校验
    if ("" !== oldSerialNumber && serialNumber === oldSerialNumber) {
        MessageBox.alert("提示","该宽带账号已校验通过，无需重复校验！");
        return false;
    }

	//测试号码的拦截@tanzheng add by 20190417 start
	var suffixNum = serialNumber.substring(7);
	if(suffixNum=='1414'||suffixNum=='2424'||suffixNum=='7474'){
		MessageBox.alert("告警提示","测试号码不能用于开户！");
		$("#SERIAL_NUMBER_B_INPUT").addClass("e_elements e_elements-error");
		return false;
	}
	//测试号码的拦截@tanzheng add by 20190417 end
    
	//无手机宽带开户178开户，不要sim卡，号码不需要配置预配卡信息 modify_by_duhj_kd
    var preCodetag = $("#PRECODE_TAG").val();
    var isNeedsimTag = $('#INPUT_SIM_TAG').val();
    if("N" == isNeedsimTag && "1" == preCodetag){
        MessageBox.alert("提示","预配卡号码不能开户,请重新选择其他号码！");
        return false;
    }
        	
    // 清空费用
    $.feeMgr.clearFeeList("10");
    // 重置按钮
    $.cssubmit.disabledSubmitBtn(true);
    // 设置号码未校验
    $checkCode.val("-1");
    // 先将SIM卡号清空
    $("#SIM_CARD_NO").attr("disabled", false).val("898600");

    var params = "&INFO_TAG=" + $("#INFO_TAG").val() + "&SERIAL_NUMBER=" + serialNumber + "&OLD_SERIAL_NUMBER" + $("#OLD_SERIAL_NUMBER").val()
        + "&OLD_SIM_CARD_NO=" + $("#OLD_SIM_CARD_NO").val();
    $.beginPageLoading("新开户号码校验中......");
    $.ajax.submit(null, "checkSerialNumber", params, "CheckSimCardNoHidePart,CheckSerialNumberHidePart",
        function (ajaxData) {
            $.endPageLoading();
            $("#OLD_SERIAL_NUMBER").val(serialNumber);
            $.custInfo.setSerialNumber(serialNumber);
            setAjaxAtferCheckMphone(ajaxData.get(0));
            $.cssubmit.disabledSubmitBtn(false);
        },
        function (error_code, error_info) {
            $.endPageLoading();
            checkSerialNumberFailed();
            $("#OLD_SERIAL_NUMBER").val("");
            MessageBox.error("服务调用异常", error_info);
        });
}

// 新开户号码校验完后返回值的处理
function setAjaxAtferCheckMphone(data) {
    var simCardNo = data.get("SIM_CARD_NO"),
        checkResultCode = data.get("CHECK_RESULT_CODE"),
        simCardNoObj = $("#SIM_CARD_NO");

    // 预配预开时，SIM卡自动带出来，且为不可修改
    if (null != simCardNo && "" !== simCardNo && undefined !== simCardNo) {
        simCardNoObj.attr("disabled", true).val(simCardNo);
        $("#OLD_SIM_CARD_NO").val(simCardNo);
        $("#CHECK_RESULT_CODE").val(checkResultCode); // 设置SIM卡校验通过
        checkSerialNumberSucceed();
        checkSIMSucceed();
    } else {
        simCardNoObj.attr("disabled", false).val("898600");
        $("#CHECK_RESULT_CODE").val(checkResultCode); // 设置号码校验通过
        checkSerialNumberSucceed();
    }
}

//重置SIM卡校验
function resetSIMCheck() {
    $("#SIM_CHECK_BTN").css("display", "");
    $("#SIM_SUCCESS_LABEL").css("display", "none");
    $("#SIM_ERROR_LABEL").css("display", "none");
}

// 校验SIM卡失败
function checkSIMFailed() {
    $("#SIM_CHECK_BTN").css("display", "none");
    $("#SIM_ERROR_LABEL").css("display", "");
}

// 校验SIM卡成功
function checkSIMSucceed() {
    $("#SIM_CHECK_BTN").css("display", "none");
    $("#SIM_SUCCESS_LABEL").css("display", "");
}

// SIM卡号校验
function checkSimCardNo() {
    var checkResultCode = $("#CHECK_RESULT_CODE").val(), // 校验通过标识
        serialNumber = $("#SERIAL_NUMBER").val(),
        simCardNo = $("#SIM_CARD_NO").val(),
        oldSimCardNo = $("#OLD_SIM_CARD_NO").val();

    if ("" === simCardNo || simCardNo.length < 20) {
        MessageBox.alert("提示", "SIM卡号输入不正确！");
        return false;
    }
    
    if (oldSimCardNo === simCardNo && "1" === checkResultCode) {
        checkSIMSucceed();
        return true;
    }

    $.cssubmit.disabledSubmitBtn(true);
    $.feeMgr.clearFeeList("10"); // 防止多次点击校验产生多条费用

    // 先检查宽带账号是否校验通过
    if (serialNumber === "") {
        MessageBox.alert("提示", "请先校验宽带账号！");
        return false;
    }

    if ("-1" === checkResultCode) {
        MessageBox.alert("提示", "宽带账号未校验通过！");
        return false;
    }

    var params = "&CHECK_RESULT_CODE=" + checkResultCode
        + "&OLD_SIM_CARD_NO=" + oldSimCardNo + "&SIM_CARD_NO=" + simCardNo
        + "&SERIAL_NUMBER=" + serialNumber;
    $.beginPageLoading("SIM卡号校验中......");
    $.ajax.submit(null, "checkSimCardNo", params, "CheckSimCardNoHidePart",
        function (ajaxData) {
            $.endPageLoading();
            $("#OLD_SIM_CARD_NO").val(simCardNo);
            $("#CHECK_RESULT_CODE").val(ajaxData.get(0, "CHECK_RESULT_CODE"));
            checkSIMSucceed();
            $.cssubmit.disabledSubmitBtn(false);
        },
        function (error_code, error_info) {
            $.endPageLoading();
            checkSIMFailed();
            MessageBox.error("服务调用异常", error_info);
        });
}
/******************** 号码和SIM卡校验 结束 ********************/

/******************** 读写卡 开始 ********************/
function beforeReadCard() {
//    $.simcard.setSerialNumber($("#SERIAL_NUMBER_B").val());
    $.simcard.setSerialNumber($("#SERIAL_NUMBER").val());
    return true;
}

function beforeCheckSimCardNo(data) {
    if ("1" == data.get("IS_WRITED")) { // 用来判断卡是否被写过
        $("#SIM_CARD_NO").val(data.get("SIM_CARD_NO"));
        resetSIMCheck();
        checkSimCardNo();
    }
}

function afterWriteCard(data){
    if ("0" == data.get("RESULT_CODE")) {
        $.simcard.readSimCard();
    }
}
/******************** 读写卡 结束 ********************/
function queryTopSetBoxFee() {
    var tradeTypeCode = $("#TRADE_TYPE_CODE").val(),
        twoCitySwitch = $('#TWO_CITY_SWITCH').val();
    
	if($("#BOX_MODE_FEE").val()==""||$("#BOX_MODE_FEE").val()==null){
		  $.feeMgr.removeFee(tradeTypeCode, "2", "439");
	}else{
		var wideProductType = $("#WIDE_PRODUCT_TYPE").val();//选择魔百和IPTV需要用到wangsc10
		var wideProductId = $("#WIDE_PRODUCT_ID").val();//选择魔百和IPTV需要用到wangsc10
		//BUS201907300031新增度假宽带季度半年套餐开发需求
		var TOP_SET_BOX_TIME = 1;
		
	    var param = "&TOP_SET_BOX_PRODUCT_ID=" + $("#TOP_SET_BOX_PRODUCT_ID").val()
	    + "&serialNumber=" + $("#AUTH_SERIAL_NUMBER").val()
	    + "&MODEM_DEPOSIT=" + $("#MODEM_DEPOSIT").val()
	    + "&TOP_SET_BOX_TIME=" + $("#TOP_SET_BOX_TIME").val()
	    + "&WIDE_PRODUCT_TYPE=" + wideProductType
	    + "&WIDE_PRODUCT_ID=" + wideProductId ;
	//BUS201907300031新增度假宽带季度半年套餐开发需求
	//$.beginPageLoading("魔百和基础优惠包和可选优惠包查询中......");
	//$.ajax.submit(null, 'queryTopSetBoxDiscntPackagesByPID', param, 'bPackagePart,oPackagePart',
	$.ajax.submit(null, 'queryTopSetBoxDiscntPackagesByPID', param, 'oPackagePart',
	    function (data) {
//	        $.endPageLoading();
//	        if ('-1' == data.get('resultCode')) {
//	            MessageBox.alert('提示', data.get('resultInfo'));
//	        }
//
//	        //拦截提示
//			if ('-1' == data.get('resultIPTVCode'))
//			{
//				$.MessageBox.error("拦截提示:", data.get('resultIPTVInfo'));
//				$("#TOP_SET_BOX_PRODUCT_ID").val('');
//			}


        // 清空魔百和调测费
	        $.feeMgr.removeFee(tradeTypeCode, "2", "439");
	        $.nophonewideusercreate.PAGE_FEE_LIST.remove("TOP_SET_BOX_DEPOSIT");
	        var feeData = $.DataMap();
	        feeData.clear();
	        feeData.put("MODE", "2");
	        feeData.put("CODE", "439");
	        feeData.put("TRADE_TYPE_CODE", tradeTypeCode);
	        if ('1' === twoCitySwitch) {
	            feeData.put("FEE", 0);
	            feeData.put("PAY", 0);
	            $("#TOP_SET_BOX_DEPOSIT").val('0');
	            $("#HIDDEN_TOP_SET_BOX_DEPOSIT").val('0');
	        } else {
	            feeData.put("FEE", data.get('TOP_SET_BOX_DEPOSIT'));
	            feeData.put("PAY", data.get('TOP_SET_BOX_DEPOSIT'));
	            $("#TOP_SET_BOX_DEPOSIT").val(data.get('TOP_SET_BOX_DEPOSIT') / 100);
	            if ('' === $("#HIDDEN_TOP_SET_BOX_DEPOSIT").val()) {
	                $("#HIDDEN_TOP_SET_BOX_DEPOSIT").val(data.get('TOP_SET_BOX_DEPOSIT') / 100)
	            }
	        }
	        $.feeMgr.insertFee(feeData);
	        $.nophonewideusercreate.PAGE_FEE_LIST.put("TOP_SET_BOX_DEPOSIT", $.feeMgr.cloneData(feeData));

	        // 清空魔百和时长缴费
//	        $.feeMgr.removeFee(tradeTypeCode, "2", "9082");
//	        $.nophonewideusercreate.PAGE_FEE_LIST.remove("TOP_SET_BOX_FEE");
	        var topSetBoxProductId = $("#TOP_SET_BOX_PRODUCT_ID").val();
	        if ('' === topSetBoxProductId) {
//	            $("#TOP_SET_BOX_FEE").val('');
//	            $("#ResNumberPart").css('display', 'none');
	        } else {
	            // 魔百和缴费
//	            feeData.clear();
//	            feeData.put("MODE", "2");
//	            feeData.put("CODE", "9082");
//	            feeData.put("TRADE_TYPE_CODE", tradeTypeCode);
//	                feeData.put("FEE", 0);
//	                feeData.put("PAY", 0);
//	                $("#TOP_SET_BOX_FEE").val('0');
//	                if(houNiaoTag)
//	                {
//	                    $("#TOP_SET_BOX_TIME").val(TOP_SET_BOX_TIME);
//	                    $("#TOP_SET_BOX_TIME").attr("disabled", true);
//	                }
//	            } else {
//	                feeData.put("FEE", data.get('TOP_SET_BOX_FEE'));
//	                feeData.put("PAY", data.get('TOP_SET_BOX_FEE'));
//	                $("#TOP_SET_BOX_FEE").val(data.get('TOP_SET_BOX_FEE') / 100);
//	            }
//	            $.feeMgr.insertFee(feeData);
//	            $.nophonewideusercreate.PAGE_FEE_LIST.put("TOP_SET_BOX_FEE", $.feeMgr.cloneData(feeData));
	//
//	            // 只有办理魔百和用户才允许校验绑定开户号码和SIM卡号
//	            $("#ResNumberPart").css('display', '');
	          //  MessageBox.alert("提示", "您选择了无手机魔百和开户，该业务必须办理147号码的个人开户，请输入开户号码！");
	        }
	    },
	    function (error_code, error_info) {
	        $.endPageLoading();
	        showDetailErrorInfo('服务调用异常', error_info);
	        $("#TOP_SET_BOX_PRODUCT_ID").val('');
	    });
	}
	
	
}

// 查询基础优惠包和可选优惠包、以及魔百和营销活动
function queryTopSetBoxPackages() {
	var wideProductType = $("#WIDE_PRODUCT_TYPE").val();//选择魔百和IPTV需要用到wangsc10
	var wideProductId = $("#WIDE_PRODUCT_ID").val();//选择魔百和IPTV需要用到wangsc10
	//BUS201907300031新增度假宽带季度半年套餐开发需求
	var TOP_SET_BOX_TIME = 1;
	var houNiaoTag = false;
	var selectedList = selectedElements.selectedEls;
	 if (selectedList.length > 1) {
         selectedList.each(function (item, index, totalcount) {
             if (item.get("ELEMENT_TYPE_CODE") == "D") {           	 
                 if ('84071448' == item.get("ELEMENT_ID")) {
                	 houNiaoTag = true;
                	 TOP_SET_BOX_TIME = 4;
                 }
                 if ('84071449' == item.get("ELEMENT_ID")) {
                	 houNiaoTag = true;
                	 TOP_SET_BOX_TIME = 6;
                 }
             }
         });
     }
    var param = "&TOP_SET_BOX_PRODUCT_ID=" + $("#TOP_SET_BOX_PRODUCT_ID").val()
        + "&serialNumber=" + $("#AUTH_SERIAL_NUMBER").val()
        + "&MODEM_DEPOSIT=" + $("#MODEM_DEPOSIT").val()
        + "&TOP_SET_BOX_TIME=" + $("#TOP_SET_BOX_TIME").val()
        + "&WIDE_PRODUCT_TYPE=" + wideProductType
        + "&WIDE_PRODUCT_ID=" + wideProductId + "&HOU_NIAO_TAG="+houNiaoTag;
    //BUS201907300031新增度假宽带季度半年套餐开发需求
    $.beginPageLoading("魔百和基础优惠包和可选优惠包查询中......");
    $.ajax.submit(null, 'queryTopSetBoxDiscntPackagesByPID', param, 'bPackagePart,oPackagePart',
        function (data) {
            $.endPageLoading();
            if ('-1' == data.get('resultCode')) {
                MessageBox.alert('提示', data.get('resultInfo'));
            }

            //拦截提示
			if ('-1' == data.get('resultIPTVCode'))
			{
				$.MessageBox.error("拦截提示:", data.get('resultIPTVInfo'));
				$("#TOP_SET_BOX_PRODUCT_ID").val('');
			}

            var tradeTypeCode = $("#TRADE_TYPE_CODE").val(),
                twoCitySwitch = $('#TWO_CITY_SWITCH').val();

            // 清空魔百和调测费
            $.feeMgr.removeFee(tradeTypeCode, "2", "439");
            $("#BOX_MODE_FEE").val('');//modify_by_duhj_kd 清空费用也把下拉框清除一下，容易给人造成错觉
//            $.nophonewideusercreate.PAGE_FEE_LIST.remove("TOP_SET_BOX_DEPOSIT");
            var feeData = $.DataMap();
//            feeData.clear();
//            feeData.put("MODE", "2");
//            feeData.put("CODE", "439");
//            feeData.put("TRADE_TYPE_CODE", tradeTypeCode);
//            if ('1' === twoCitySwitch) {
//                feeData.put("FEE", 0);
//                feeData.put("PAY", 0);
//                $("#TOP_SET_BOX_DEPOSIT").val('0');
//                $("#HIDDEN_TOP_SET_BOX_DEPOSIT").val('0');
//            } else {
//                feeData.put("FEE", data.get('TOP_SET_BOX_DEPOSIT'));
//                feeData.put("PAY", data.get('TOP_SET_BOX_DEPOSIT'));
//                $("#TOP_SET_BOX_DEPOSIT").val(data.get('TOP_SET_BOX_DEPOSIT') / 100);
//                if ('' === $("#HIDDEN_TOP_SET_BOX_DEPOSIT").val()) {
//                    $("#HIDDEN_TOP_SET_BOX_DEPOSIT").val(data.get('TOP_SET_BOX_DEPOSIT') / 100)
//                }
//            }
//            $.feeMgr.insertFee(feeData);
//            $.nophonewideusercreate.PAGE_FEE_LIST.put("TOP_SET_BOX_DEPOSIT", $.feeMgr.cloneData(feeData));

            // 清空魔百和时长缴费
            $.feeMgr.removeFee(tradeTypeCode, "2", "9082");
            $.nophonewideusercreate.PAGE_FEE_LIST.remove("TOP_SET_BOX_FEE");
            var topSetBoxProductId = $("#TOP_SET_BOX_PRODUCT_ID").val();
            if ('' === topSetBoxProductId) {
            	$("#BOX_MODE_FEE").val('');
                $("#TOP_SET_BOX_FEE").val('');
            } else {
                // 魔百和缴费
                feeData.clear();
                feeData.put("MODE", "2");
                feeData.put("CODE", "9082");
                feeData.put("TRADE_TYPE_CODE", tradeTypeCode);
                if ('1' === twoCitySwitch || houNiaoTag) {
                    feeData.put("FEE", 0);
                    feeData.put("PAY", 0);
                    $("#TOP_SET_BOX_FEE").val('0');
                    if(houNiaoTag)
                    {
                        $("#TOP_SET_BOX_TIME").val(TOP_SET_BOX_TIME);
                        $("#TOP_SET_BOX_TIME").attr("disabled", true);
                    }
                } else {
                    feeData.put("FEE", data.get('TOP_SET_BOX_FEE'));
                    feeData.put("PAY", data.get('TOP_SET_BOX_FEE'));
                    $("#TOP_SET_BOX_FEE").val(data.get('TOP_SET_BOX_FEE') / 100);
                }
                $.feeMgr.insertFee(feeData);
                $.nophonewideusercreate.PAGE_FEE_LIST.put("TOP_SET_BOX_FEE", $.feeMgr.cloneData(feeData));
            }
        },
        function (error_code, error_info) {
            $.endPageLoading();
            showDetailErrorInfo('服务调用异常', error_info);
            $("#TOP_SET_BOX_PRODUCT_ID").val('');
        });
}

// 魔百和营销活动（无手机宽带开户界面无魔百和营销活动内容，临时注释该方法）by huangls5
/*function changeTopSetBoxSaleActive() {
    var topSetBoxSaleActiveId = $("#TOP_SET_BOX_SALE_ACTIVE_ID").val();
    var saleActiveId = $("#SALE_ACTIVE_ID").val();

    if (null != topSetBoxSaleActiveId && '' != topSetBoxSaleActiveId) {
        //产品组件已选取元素列表
        var selectedList = selectedElements.selectedEls;
        var serviceIds = '';
        //魔百和营销活动类型
        var topSetBoxSaleActiveCampnType = '';
        //魔百和营销方案ID
        var topSetBoxSaleActiveProductId = '';
        //魔百和营销包ID
        var topSetBoxSaleActivePackageId = '';
        //魔百和营销活动描述
        var topSetBoxSaleActiveExplain = '';
        //魔百和营销活动预存费用
        var topSetBoxSaleActiveFee = '';
        //是否走依赖宽带产品的规则，为空或者不为1则不走。
        depTag = '';
        //依赖的宽带产品
        depWilens = '';
        //过滤出优惠ID
        if (selectedList.length > 1) {
            selectedList.each(function (item, index, totalcount) {
                if (item.get("ELEMENT_TYPE_CODE") == "S") {
                    if ('' != serviceIds) {
                        serviceIds += '|' + item.get("ELEMENT_ID");
                    } else {
                        serviceIds = item.get("ELEMENT_ID");
                    }
                }
            });
        }

        if (null != TOP_SET_BOX_SALE_ACTIVE_LIST) {
            for (var i = 0; i < TOP_SET_BOX_SALE_ACTIVE_LIST.length; i++) {
                if (topSetBoxSaleActiveId == TOP_SET_BOX_SALE_ACTIVE_LIST.items[i].get('PARA_CODE2')) {
                    //魔百和营销活动类型
                    topSetBoxSaleActiveCampnType = TOP_SET_BOX_SALE_ACTIVE_LIST.items[i].get("PARA_CODE6");
                    //魔百和营销方案ID
                    topSetBoxSaleActiveProductId = TOP_SET_BOX_SALE_ACTIVE_LIST.items[i].get("PARA_CODE4");
                    //魔百和营销包ID
                    topSetBoxSaleActivePackageId = TOP_SET_BOX_SALE_ACTIVE_LIST.items[i].get("PARA_CODE5");
                    //魔百和营销活动描述
                    topSetBoxSaleActiveExplain = TOP_SET_BOX_SALE_ACTIVE_LIST.items[i].get("PARA_CODE24");
                    //魔百和营销活动预存费用
                    topSetBoxSaleActiveFee = TOP_SET_BOX_SALE_ACTIVE_LIST.items[i].get("PARA_CODE7");
                    //是否走依赖宽带产品的规则，为空或者不为1则不走。
                    depTag = TOP_SET_BOX_SALE_ACTIVE_LIST.items[i].get("PARA_CODE22");
                    //依赖的宽带产品
                    depWilens = TOP_SET_BOX_SALE_ACTIVE_LIST.items[i].get("PARA_CODE23");
                    break;
                }
            }
        }

        var inparam = "&TRADE_TYPE_CODE=" + $("#TRADE_TYPE_CODE").val() + "&WIDE_USER_SELECTED_SERVICEIDS=" + serviceIds
            + "&SERIAL_NUMBER=" + $("#AUTH_SERIAL_NUMBER").val() + "&CAMPN_TYPE=" + topSetBoxSaleActiveCampnType
            + "&PRODUCT_ID=" + topSetBoxSaleActiveProductId + "&PACKAGE_ID=" + topSetBoxSaleActivePackageId + "&TOP_SET_BOX_SALE_ACTIVE_ID=" + topSetBoxSaleActiveId
            + "&SALE_ACTIVE_ID=" + saleActiveId + "&IS_TOP_SET_BOX_SALE_ACTIVE=Y";

        var depflag = "0"//校验通过规则，1=通过，0=不通过
        if (depTag == "1") {//依赖宽带产品标记，1=依赖。其他不依赖。
            var productId = $("#WIDE_PRODUCT_ID").val(); //宽带产品
            var wilens = depWilens.split("|");
            for (var k = 0; k < wilens.length; k++) {
                var depProdId = wilens[k];
                if (productId == depProdId) {
                    depflag = "1";
                }
            }
            if (depflag == "0") {
                $.MessageBox.error("-1", "该宽带产品不允许办理该营销活动。");
                $("#TOP_SET_BOX_SALE_ACTIVE_EXPLAIN").val('');
                $("#TOP_SET_BOX_SALE_ACTIVE_ID").val('');
                $("#TOP_SET_BOX_SALE_ACTIVE_FEE").val('');
                $("#TOP_SET_BOX_DEPOSIT").val($("#HIDDEN_TOP_SET_BOX_DEPOSIT").val());
                return;
            }
        }

        $.beginPageLoading("魔百和活动校验中。。。");
        $.ajax.submit(null, 'checkSaleActiveDependence', inparam, null,
            function (data) {
                $.endPageLoading();
                if ('-1' == data.get('resultCode')) {
                    $("#TOP_SET_BOX_SALE_ACTIVE_EXPLAIN").val('');
                    $("#TOP_SET_BOX_SALE_ACTIVE_ID").val('');
                    $("#TOP_SET_BOX_SALE_ACTIVE_FEE").val('');
                    $("#TOP_SET_BOX_DEPOSIT").val($("#HIDDEN_TOP_SET_BOX_DEPOSIT").val());
                } else {
                    $("#TOP_SET_BOX_SALE_ACTIVE_EXPLAIN").val(topSetBoxSaleActiveExplain);
                    $("#TOP_SET_BOX_DEPOSIT").val("0");
                    //营销活动费用需要重新取产品模型配置
                    checkTopBoxSetSaleActiveFee(topSetBoxSaleActiveCampnType, topSetBoxSaleActiveProductId, topSetBoxSaleActivePackageId);
                }
            },
            function (error_code, error_info) {
                $("#TOP_SET_BOX_SALE_ACTIVE_EXPLAIN").val('');
                $("#TOP_SET_BOX_SALE_ACTIVE_ID").val('');
                $("#TOP_SET_BOX_SALE_ACTIVE_FEE").val('');

                $("#TOP_SET_BOX_DEPOSIT").val($("#HIDDEN_TOP_SET_BOX_DEPOSIT").val());

                $.endPageLoading();
                $.MessageBox.error(error_code, error_info);
            });
    } else {
        $("#TOP_SET_BOX_SALE_ACTIVE_EXPLAIN").val('');
        $("#TOP_SET_BOX_SALE_ACTIVE_FEE").val('');
        $("#TOP_SET_BOX_DEPOSIT").val($("#HIDDEN_TOP_SET_BOX_DEPOSIT").val());
    }
}
// 查询魔百和营销活动预存费用
function checkTopBoxSetSaleActiveFee(campnType, productId, packageId) {
    $.beginPageLoading("魔百和营销活动费用校验中。。。");
    var serialNumber = $("#AUTH_SERIAL_NUMBER").val();
    var param = "&SERIAL_NUMBER=" + serialNumber + "&CAMPN_TYPE=" + campnType
        + "&PRODUCT_ID=" + productId + "&PACKAGE_ID=" + packageId + "&ACTIVE_FLAG=2";

    $.ajax.submit(null, 'queryCheckSaleActiveFee', param, null,
        function (data) {
            $.endPageLoading();
            var fee = data.get("SALE_ACTIVE_FEE");
            $("#TOP_SET_BOX_SALE_ACTIVE_FEE").val(fee);
        },
        function (error_code, error_info) {
            $("#TOP_SET_BOX_SALE_ACTIVE_EXPLAIN").val('');
            $("#TOP_SET_BOX_SALE_ACTIVE_ID").val('');
            $("#TOP_SET_BOX_SALE_ACTIVE_FEE").val('');
            $.endPageLoading();
            $.MessageBox.error(error_code, error_info);
        });
}*/

// 设置魔百和费用
function settopsetboxfee() {
    if ('' === $("#TOP_SET_BOX_PRODUCT_ID").val()) {
        return false;
    }

    var param = '&TOP_SET_BOX_TIME=' + $("#TOP_SET_BOX_TIME").val();
    $.ajax.submit(null, 'settopsetboxfee', param, null,
        function (data) {
            $.endPageLoading();
            var tradeTypeCode = $("#TRADE_TYPE_CODE").val();
            // 魔百和缴费
            $.feeMgr.removeFee(tradeTypeCode, "2", "9082");
            $.nophonewideusercreate.PAGE_FEE_LIST.remove("TOP_SET_BOX_FEE");
            var feeData = $.DataMap();
            feeData.put("MODE", "2");
            feeData.put("CODE", "9082");
            feeData.put("TRADE_TYPE_CODE", tradeTypeCode);
            if ('1' === $('#TWO_CITY_SWITCH').val()) {
                feeData.put("FEE", 0);
                feeData.put("PAY", 0);
                $("#TOP_SET_BOX_FEE").val('0');
            } else {
                feeData.put("FEE", data.get('TOP_SET_BOX_FEE'));
                feeData.put("PAY", data.get('TOP_SET_BOX_FEE'));
                $("#TOP_SET_BOX_FEE").val(data.get('TOP_SET_BOX_FEE') / 100);
            }
            $.feeMgr.insertFee(feeData);
            $.nophonewideusercreate.PAGE_FEE_LIST.put("TOP_SET_BOX_FEE", $.feeMgr.cloneData(feeData));
        }, function (error_code, error_info) {
            $.endPageLoading();
            $.MessageBox.error(error_code, error_info);
        });
}
function getValidWideNetAccountId()
{
    popupPage('选号', 'nophonewideusercreate.ChooseIdleWideAcctId', 'onInitTrade', null, null, 'c_popup c_popup-half c_popup-half-hasBg', function() {
        resetSNCheck();
        checkOldSNPwd();
	});
}
