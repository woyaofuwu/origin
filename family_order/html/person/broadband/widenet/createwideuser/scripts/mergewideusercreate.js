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
	
	$.mergewideusercreate.afterSetDetailAddress();
}



(function($) {
	if (typeof ($.mergewideusercreate == "undefined")) {
		
		$.mergewideusercreate = {
			PAGE_FEE_LIST: $.DataMap(),
			SALE_ACTIVE_LIST: $.DatasetList(),
			TOP_SET_BOX_SALE_ACTIVE_LIST: $.DatasetList(),
			SALE_ACTIVE_LIST2: $.DatasetList(),
			TOP_SET_BOX_SALE_ACTIVE_LIST2: $.DatasetList(),
			/**
			 * 控制基本信息显示\隐藏
			 * @param btn
			 * @param o
			 */
			displaySwitch : function(btn, o) {
				var button = $(btn);
				var div = $('#' + o);

				if (div.css('display') != "none") 
				{
					div.css('display', 'none');
					button.children("i").attr('className', 'e_ico-unfold');
					button.children("span:first").text("展示客户基本信息");
				} 
				else 
				{
					div.css('display', '');
					button.children("i").attr('className', 'e_ico-fold');
					button.children("span:first").text("隐藏客户基本信息");
				}
			},
			
			/**
			 * 控制宽带信息显示\隐藏
			 * @param btn
			 * @param o
			 */
			topSetBoxInfoPartDisplaySwitch : function(btn, o) {
				var button = $(btn);
				var div = $('#' + o);

				if (div.css('display') != "none") 
				{
					div.css('display', 'none');
					button.children("i").attr('className', 'e_ico-unfold');
					button.children("span:first").text("展示魔百和信息");
				} 
				else 
				{
					div.css('display', '');
					button.children("i").attr('className', 'e_ico-fold');
					button.children("span:first").text("隐藏魔百和信息");
				}
			},

			/**
			 * auth查询后自定义查询
			 * @param data
			 * @returns
			 */
			refreshPartAtferAuth : function(data) {
				
				var param = "&ROUTE_EPARCHY_CODE="
						+ data.get("USER_INFO").get("EPARCHY_CODE")
						+ "&TRADE_TYPE_CODE=" + $("#TRADE_TYPE_CODE").val()
						+"&serialNumber="+$("#AUTH_SERIAL_NUMBER").val();

				$.beginPageLoading("业务资料查询。。。");
				$.ajax.submit(this, 'loadChildInfo', param, 'wideMode,productType,saleActivePart,saleActivePartAttr,topSetBoxProductPart,hiddenPart,topSetBoxSaleActivePart,modemPart,mergeWideUserStylePart',
						function(data) 
						{
							$.mergewideusercreate.initProduct();
							$.endPageLoading();
							
							$("#PHONE").val($("#AUTH_SERIAL_NUMBER").val());
							$("#MODEM_STYLE").val("");
							$("#MODEM_DEPOSIT").val("0");
							$("#MODEM_STYLE").attr("disabled", false);
							$('#WIDE_PRODUCT_ID').attr("disabled",false);
							$('#SALE_ACTIVE_ID').attr("disabled",false);
							$('#SALE_ACTIVE_IDATTR').attr("disabled",false);
							
							//是否商务宽带
							var isBusinessWide = data.get('IS_BUSINESS_WIDE');
							
							$("#IS_BUSINESS_WIDE").val(isBusinessWide);
							
							//如果是商务宽带
							if ('Y' == isBusinessWide)
							{
								$("#MODEM_STYLE").val("0");
								$("#MODEM_STYLE").attr("disabled", true);
								
								$("#TOP_SET_BOX_DEPOSIT").val("");  //商务宽带魔百和押金费用清0
								$("#SALE_ACTIVE_FEE").val("");      //商务宽带营销活动费用清0
								$("#TOP_SET_BOX_SALE_ACTIVE_FEE").val(""); //商务宽带魔百和营销活动费用清0
				
								$("#topSetBoxDiv").css('display', 'none');
								$("#saleActivePart").css('display', 'none');
								$("#saleActivePartAttr").css('display', 'none');
								$("#saleActiveExplainPart").css('display', 'none');
							}
							else
							{
								$("#MODEM_STYLE").val("");
								$("#MODEM_STYLE").attr("disabled", false);
								
								$("#topSetBoxDiv").css('display', '');
								$("#saleActivePart").css('display', '');
								$("#saleActiveExplainPart").css('display', '');
							}
							
							$.mergewideusercreate.changeWideProductType();
							
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
					wideProductType = "2";
				}
				
				$("#WIDE_PRODUCT_TYPE").val(wideProductType);
				
				if ($.auth.getAuthData() == undefined)
				{
					return false;
				}
				
				$.mergewideusercreate.changeWideProductType();
			},

			/**
			 * 初始化产品
			 * */
			initProduct : function()
			{			
				var productId = $("#WIDE_PRODUCT_ID").val();
				var eparchyCode = $.auth.getAuthData().get("USER_INFO").get("EPARCHY_CODE");
				var param = "&NEW_PRODUCT_ID=" + productId;
				offerList.renderComponent(productId,eparchyCode);
				
				selectedElements.renderComponent("&NEW_PRODUCT_ID=", $.auth.getAuthData().get("USER_INFO").get("EPARCHY_CODE"));
				
				//清空营销活动详情描述
				$("#SALE_ACTIVE_EXPLAIN").val("");
			},

			/**
			 * 切换产品类型
			 */
			changeWideProductType : function() 
			{
				if ($.auth.getAuthData() == undefined)
				{
					MessageBox.alert("告警提示","请先进行用户鉴权操作！");
					
					$("#WIDE_PRODUCT_TYPE").val('');
					
					return false;
				}
				
				var wideProductType = $("#WIDE_PRODUCT_TYPE").val();
				
				
				var isBusinessWide = $("#IS_BUSINESS_WIDE").val();

				// 只有有移动FTTH和铁通FTTH需要申请光猫
				if ('3' != wideProductType && '5' != wideProductType)
				{
				    $("#modemDiv").css("display", "none");
					
					$("#MODEM_STYLE").val('');
					$("#MODEM_DEPOSIT").val('0');
					
					//是否是家宽带开户界面
					var isFamilyWideUserCreate = $("#IS_FAMILY_WIDE_USER_CREATE").val();
					if (undefined != isFamilyWideUserCreate && 'Y' == isFamilyWideUserCreate)
					{
						var fixNumber = $("#FIX_NUMBER").val();
						
						if (null != fixNumber && '' != fixNumber)
						{
							alert("只有FTTH宽带才能办理固话业务，新选择的地址不支持将取消当前所选的固话业务！")
							
							$("#FIX_NUMBER").val("");
							$("#IMS_PRODUCT_TYPE_CODE").val("");
							$("#IMS_PRODUCT_ID").val("");
							$("#IMS_SALE_ACTIVE_ID").val("");
							$("#IMS_SALE_ACTIVE_FEE").val("");
							
							$("#IMSCheckBox").attr("checked",false);
							$("#IMSPart").css('display', 'none');
						}
					}
				}
				else
				{
					$("#modemDiv").css('display', '');
					//BUS201907310012关于开发家庭终端调测费的需求
					$("#MODEM_STYLE").val('0');
					$.ajax.submit(this, 'getSaleActiveByParaCode1','&ROUTE_EPARCHY_CODE='+$.auth.getAuthData().get("USER_INFO").get("EPARCHY_CODE"), 'modemPart',
					function(data) 
					{
						SALE_ACTIVE_LIST2 = data;
						$.endPageLoading();
					}, function(error_code, error_info) 
					{
						$.endPageLoading();
						$.MessageBox.error(error_code, error_info);
					});
					//BUS201907310012关于开发家庭终端调测费的需求
				}
				
				// ADSL和商务宽带不能办理魔百和业务
				if ('2' == wideProductType || 'Y' == isBusinessWide)
				{
					$("#TOP_SET_BOX_DEPOSIT").val("");  //魔百和押金费用清0
					$("#TOP_SET_BOX_SALE_ACTIVE_FEE").val(""); //魔百和营销活动费用清0
	
					$("#topSetBoxDiv").css('display', 'none');
				}
				else
				{
					$("#topSetBoxDiv").css('display', '');
				}
				
				$.beginPageLoading("产品信息查询。。。");
				
				$.ajax.submit(this, 'changeWideProductType',
						'&wideProductType=' + wideProductType+'&isBusinessWide='+isBusinessWide+'&SERIAL_NUMBER='+$("#AUTH_SERIAL_NUMBER").val(), 'productType,saleActivePart,saleActivePartAttr',
						function(data) 
						{
							$.mergewideusercreate.initProduct();
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
//				$.feeMgr.insertFee($.mergewideusercreate.PAGE_FEE_LIST.get("RES_FEE"));
				var productId = $("#WIDE_PRODUCT_ID").val();
				var eparchyCode = $.auth.getAuthData().get("USER_INFO").get("EPARCHY_CODE");
				var param = "&NEW_PRODUCT_ID=" + productId;
				offerList.renderComponent(productId, eparchyCode);
				selectedElements.renderComponent(param, eparchyCode);
				//$("#MODEM_STYLE").attr("disabled", true);
				
				var param1 = "&ROUTE_EPARCHY_CODE="
					+ $.auth.getAuthData().get("USER_INFO").get("EPARCHY_CODE")+'&productId=' + productId;
				
				$.beginPageLoading("营销活动信息查询。。。");
				$.ajax.submit(this, 'getSaleActiveByProductId',
						param1, 'saleActivePart,saleActivePartAttr,modemPart,platSvcPackage',
						function(data) 
						{
							SALE_ACTIVE_LIST = data;
							
							//变更产品需要清空魔百和营销活动信息
							$("#TOP_SET_BOX_SALE_ACTIVE_ID").val('');
							$("#TOP_SET_BOX_SALE_ACTIVE_EXPLAIN").val('');
							//BUS201907310012关于开发家庭终端调测费的需求
							// 移动FTTH和铁通FTTH需要办理终端调测费活动
							var wideProductType = $("#WIDE_PRODUCT_TYPE").val();
							if ('3' == wideProductType || '5' == wideProductType)
							{
								$.ajax.submit(this, 'getSaleActiveByParaCode1',param1, 'modemPart',
								function(data) 
								{
									SALE_ACTIVE_LIST2 = data;
									$.endPageLoading();
								}, function(error_code, error_info) 
								{
									$.endPageLoading();
									$.MessageBox.error(error_code, error_info);
								});
							}
							//BUS201907310012关于开发家庭终端调测费的需求
							$.endPageLoading();
						}, function(error_code, error_info) 
						{
							$.endPageLoading();
							$.MessageBox.error(error_code, error_info);
						});
				
				//是否是家宽带开户界面
				var isFamilyWideUserCreate = $("#IS_FAMILY_WIDE_USER_CREATE").val();
				if (undefined != isFamilyWideUserCreate && 'Y' == isFamilyWideUserCreate)
				{
					//获取和目营销活动
					$.ajax.submit(this, 'getHeMuSaleActive',param1, 'HeMuPart',
							function(data) 
							{
								//如果选择了和目业务
								if ($("#HeMuCheckBox").attr("checked"))
								{
									if (data.length == 0)
									{
										alert("该速率宽带产品不能办理和目业务！")
									}
								}
						
								$.endPageLoading();
							}, function(error_code, error_info) 
							{
								$.endPageLoading();
								$.MessageBox.error(error_code, error_info);
							});
				}
				
				//Add by zhangxing3 for REQ201807310009有手机号码候鸟宽带资费
				//宽带产品变更时，重置魔百和信息
				$.ajax.submit(null, 'initTopSetBox', param1, 'topSetBoxInfoPart',
						function(data) 
						{
							$("#TOP_SET_BOX_DEPOSIT").val('0');		
						}, function(error_code, error_info) 
						{
						}							
					);
				//add by zhangxing3 for REQ201807310009有手机号码候鸟宽带资费
				
				//REQ201904280013 关于高价值小区攻坚专项营销活动的开发需求
				var deviceId = $("#DEVICE_ID").val();
				var eparchyCode = $.auth.getAuthData().get("USER_INFO").get("EPARCHY_CODE");
				var inparam = "&TRADE_TYPE_CODE="+ $("#TRADE_TYPE_CODE").val() + "&ROUTE_EPARCHY_CODE="+ eparchyCode +"&DEVICE_ID="
				+deviceId + "&SERIAL_NUMBER="+$("#AUTH_SERIAL_NUMBER").val()+ "&WIDE_PRODUCT_ID="+$("#WIDE_PRODUCT_ID").val();
				    $.ajax.submit(null, 'checkHightActive', inparam, null,
						function(data) 
						{
							if(data.get('DEVICE_ID_HIGHT') != '1')
							{
								$("#IS_HIGHTACTIVE").attr("checked",false);
								$("#DEVICE_ID_HIGHT").val(0);
							}else{
								$("#DEVICE_ID_HIGHT").val(1);
							}
							
							if(data.get('RAT_HIGHT')!= '1')
							{
								$("#IS_HIGHTACTIVE").attr("checked",false);
								$("#RAT_HIGHT").val(0);
							}else{
								$("#RAT_HIGHT").val(1);
							}
							
							
						}, 
						function(error_code, error_info) 
						{
							$("#IS_HIGHTACTIVE").attr("checked",false);
							$.MessageBox.error(error_code, error_info);
						}							
					)

			},
			
			changeSaleActiveAttr : function()
			{
				var saleActiveId = $("#SALE_ACTIVE_IDATTR").val();
				if (null != saleActiveId && '' != saleActiveId)
				{
					//产品组件已选取元素列表
					var selectedList = selectedElements.selectedEls;
					
					var serviceIds = '';
					//过滤出优惠ID
					if (selectedList.length > 1)
					{
						selectedList.each(function(item,index,totalcount)
						{
							if (item.get("ELEMENT_TYPE_CODE")=="S")
							{
								if ('' != serviceIds)
								{
									serviceIds += '|'+item.get("ELEMENT_ID");
								}
								else
								{
									serviceIds = item.get("ELEMENT_ID");
								}
							}
						});
					}
					
					
					var para_code1 = $("#WIDE_PRODUCT_ID").val();
					var eparchyCode = $.auth.getAuthData().get("USER_INFO").get("EPARCHY_CODE");
					
					var inparam = "&TRADE_TYPE_CODE="+ $("#TRADE_TYPE_CODE").val() + "&ROUTE_EPARCHY_CODE="
							+ eparchyCode+"&WIDE_USER_SELECTED_SERVICEIDS="+serviceIds
							+"&SERIAL_NUMBER="+$("#AUTH_SERIAL_NUMBER").val()+"&CAMPN_TYPE=YX04"
							+"&PARA_CODE11="+saleActiveId+"&PARA_CODE1="+para_code1;
					$.beginPageLoading("宽带附加活动校验中。。。");
					
					$.ajax.submit(null, 'checkSaleActiveAttr', inparam, null,
							function(data) 
							{
								$.endPageLoading();
							}, function(error_code, error_info) 
							{
								$("#SALE_ACTIVE_IDATTR").val('');
								
								$.endPageLoading();
								$.MessageBox.error(error_code, error_info);
							}							
						);
				}
			},
			changeIsWideBing : function()
			{
				var strIsWideBing = $("#IS_WIDEBING").val();
				if(strIsWideBing == '1')
				{
					$("#span_TTWIDENET").addClass("e_required");	
					$("#TT_WIDENET").attr("disabled", false);
					$("#TT_WIDENET").attr("nullable","no");
				}
				else
				{
					$("#span_TTWIDENET").removeClass("e_required");
					$("#TT_WIDENET").val("");
					$("#TT_WIDENET").attr("disabled", true);
					$("#TT_WIDENET").attr("nullable","yes");
				}
			},
			changeIsHightActive : function()
			{
				
				//选择办理高价值小区专项营销活动
				if($("#IS_HIGHTACTIVE").attr("checked"))//是
				{
					var data = selectedElements.getSubmitData();
					if (data && data.length > 0) 
					{
						var wideProductId = $("#WIDE_PRODUCT_ID").val();
						var openType = $("#OPEN_TYPE").val();
						var deviceIdHight = $("#DEVICE_ID_HIGHT").val();
						if($("#RAT_HIGHT").val()!="1"){
							$("#IS_HIGHTACTIVE").attr("checked",false);
							MessageBox.alert("提示","100M及以上宽带业务才能参加高价值小区营销活动.");
							return false;
						}
						if (('84010440' != wideProductId&&'84011238' != wideProductId&&'84011240' != wideProductId&&'84011241' != wideProductId)
								||'FTTH' != openType||'1' != deviceIdHight	)
						{
							$("#IS_HIGHTACTIVE").attr("checked",false);
							MessageBox.alert("提示","用户不是高价值小区用户，无法办理该活动.");
						}
					} 
					else
					{
						$("#IS_HIGHTACTIVE").attr("checked",false);
						MessageBox.alert("告警提示",'您未选择开户产品，不能提交！');
						return false;
					}
											
				}
				
			},
		        changeIsZhanYiActive : function(){
		                //REQ202003180001 “共同战疫宽带助力”活动开发需求 add by liangdg3
		                if($("#AUTH_SERIAL_NUMBER").val()==""){
		                    $("#IS_ZHANYI_ACTIVE").attr("checked",false);
		                    MessageBox.alert("提示","请输入服务号码");
		                    return
		                }

		                if($("#IS_ZHANYI_ACTIVE").attr("checked")){
		                    var isBusinessWide = $("#IS_BUSINESS_WIDE").val();
		                    if (isBusinessWide=='Y'){
		                        $("#IS_ZHANYI_ACTIVE").attr("checked",false);
		                        MessageBox.alert("提示","商务宽带、校园宽带客户无法办理该活动");
		                        return
		                    }
		                    var inparam = "&TIME_LIMITED_SALE_ACTIVE=ZHANYI&TRADE_TYPE_CODE="+ $("#TRADE_TYPE_CODE").val() + "&ROUTE_EPARCHY_CODE=0898&SERIAL_NUMBER="+$("#AUTH_SERIAL_NUMBER").val();
		                    $.beginPageLoading("营销活动校验中。。。");
		                    $.ajax.submit(null, 'checkTimeLimitedSaleActive', inparam, null,
		                        function(data){
		                            $.endPageLoading();
		                        },
		                        function(error_code, error_info){
		                            $.endPageLoading();
		                            $.MessageBox.error(error_code, error_info);
		                            $("#IS_ZHANYI_ACTIVE").attr("checked",false);
		                            return false;
		                        }
		                    );
		                }
			},
			changeIsTTWideNet : function()
			{
				var strIsTTWideNet = $("#TT_WIDENET").val();
				if(null != strIsTTWideNet && '' != strIsTTWideNet)
				{
					var eparchyCode = '0898';//$.auth.getAuthData().get("USER_INFO").get("EPARCHY_CODE");
					var inparam = "ROUTE_EPARCHY_CODE="+ eparchyCode +"&SERIAL_NUMBER="+strIsTTWideNet;
				
						$.beginPageLoading("铁通号码校验中。。。");
						$.ajax.submit(null, 'checkTTWideNet', inparam, null,
								function(data) 
								{
									$.endPageLoading();
									if(data.get('X_RESULTCODE') == '-1')
									{
										var info = data.get("X_RESULTINFO");
										alert(info);
										$("#TT_WIDENET").val("");
										$("#TT_WIDENET").focus();
										return false;
									}
									else if(data.get('X_RESULTCODE') == '0' )
									{
										var strUserID = data.get('TT_USER_ID');
										$("#TT_USER_ID").val(strUserID);
									}
								}, 
								function(error_code, error_info) 
								{
									$.endPageLoading();
									$.MessageBox.error(error_code, error_info);
									$("#TT_WIDENET").val("");
									$("#TT_WIDENET").focus();
									return false;
								}							
							);
				}
			},
			changeSaleActive : function()
			{
				var saleActiveId = $("#SALE_ACTIVE_ID").val();
				
				var topSetBoxSaleActiveId = $("#TOP_SET_BOX_SALE_ACTIVE_ID").val();
				
				if (null != saleActiveId && '' != saleActiveId)
				{
					//产品组件已选取元素列表
					var selectedList = selectedElements.selectedEls;
					
					var discntIds = '';
					var serviceIds = '';
					
					//营销活动类型
					var saleActiveCampnType = '';
					
					//营销方案ID
					var saleActiveProductId = '';
					
				    //营销包ID
					var saleActivePackageId = '';
					
					//营销活动描述
					var saleActiveExplain = '';
					
					//营销活动预存费用
					var saleActiveFee = '';
					
					//过滤出优惠ID
					if (selectedList.length > 1)
					{
						selectedList.each(function(item,index,totalcount)
						{
							if(item.get("ELEMENT_TYPE_CODE")=="D" && "0" == item.get("MODIFY_TAG"))
							{
								if ('' != discntIds)
								{
									discntIds += ','+item.get("ELEMENT_ID");
								}
								else
								{
									discntIds = item.get("ELEMENT_ID");
								}
							}
							else if (item.get("ELEMENT_TYPE_CODE")=="S")
							{
								if ('' != serviceIds)
								{
									serviceIds += '|'+item.get("ELEMENT_ID");
								}
								else
								{
									serviceIds = item.get("ELEMENT_ID");
								}
							}
						});
					}
					
					//
					if (null != SALE_ACTIVE_LIST)
					{
						for (var i = 0; i < SALE_ACTIVE_LIST.length; i++)
						{
							if (saleActiveId == SALE_ACTIVE_LIST.items[i].get('PARA_CODE2'))
							{
								//营销活动类型
								saleActiveCampnType = SALE_ACTIVE_LIST.items[i].get("PARA_CODE6");
								
								//营销方案ID
								saleActiveProductId = SALE_ACTIVE_LIST.items[i].get("PARA_CODE4");
								
							    //营销包ID
								 saleActivePackageId = SALE_ACTIVE_LIST.items[i].get("PARA_CODE5");
								
								//营销活动描述
								saleActiveExplain = SALE_ACTIVE_LIST.items[i].get("PARA_CODE24");
								
								//营销活动预存费用
								saleActiveFee = SALE_ACTIVE_LIST.items[i].get("PARA_CODE7"); 
								 
								break;
							}
						}
					}
					
					var para_code1 = $("#WIDE_PRODUCT_ID").val();
					var eparchyCode = $.auth.getAuthData().get("USER_INFO").get("EPARCHY_CODE");
					
					var inparam = "&TRADE_TYPE_CODE="+ $("#TRADE_TYPE_CODE").val() + "&ROUTE_EPARCHY_CODE="
							+ eparchyCode + "&DISCNT_IDS="+discntIds+"&WIDE_USER_SELECTED_SERVICEIDS="+serviceIds
							+"&SERIAL_NUMBER="+$("#AUTH_SERIAL_NUMBER").val()+"&CAMPN_TYPE="+saleActiveCampnType
							+"&PRODUCT_ID="+saleActiveProductId+"&PACKAGE_ID="+saleActivePackageId
							+"&TOP_SET_BOX_SALE_ACTIVE_ID"+topSetBoxSaleActiveId+"&PARA_CODE1="+para_code1;
					
					$.beginPageLoading("宽带活动校验中。。。");
					
					$.ajax.submit(null, 'checkSaleActive', inparam, 'saleActivePartAttr',
							function(data) 
							{
								$.endPageLoading();

								if ('1' == data.get("FLAG"))
								{
									$("#saleActivePartAttr").css('display', '');
								}else{
									$("#saleActivePartAttr").css('display', 'none');
								}
								
								if ('-1' == data.get('resultCode'))
								{
									$.MessageBox.error('-1', '用户已经选择了包年优惠，不能再办理宽带营销活动，如果要办理请先取消包年优惠！');
									
									$("#SALE_ACTIVE_EXPLAIN").val('');
									$("#SALE_ACTIVE_ID").val('');
									$("#SALE_ACTIVE_FEE").val('');
									
								}else if ('-2' == data.get('resultCode')) {
									$.MessageBox.error('-1', '用户已经选择了VIP体验套餐，不能再办理宽带营销活动，如果要办理请先取消VIP体验套餐！');
									
									$("#SALE_ACTIVE_EXPLAIN").val('');
									$("#SALE_ACTIVE_ID").val('');
									$("#SALE_ACTIVE_FEE").val('');
								}
								else if ('-3' == data.get('resultCode'))
								{
									$.MessageBox.error('-1', '用户已经选择了50M包月套餐，不能再办理宽带营销活动，如果要办理请先取消50M包月套餐！');
									
									$("#SALE_ACTIVE_EXPLAIN").val('');
									$("#SALE_ACTIVE_ID").val('');
									$("#SALE_ACTIVE_FEE").val('');
								}
								else
								{
									$("#SALE_ACTIVE_EXPLAIN").val(saleActiveExplain);
				 					//宽带营销活动预存
				 					//营销活动费用需要重新取产品模型配置
				 					$.mergewideusercreate.checkWideNetSaleActiveFee(saleActiveCampnType,saleActiveProductId,saleActivePackageId);
				 					
				 					$.mergewideusercreate.setModemNumeric();
								}

							}, function(error_code, error_info) 
							{
								$("#SALE_ACTIVE_EXPLAIN").val('');
								$("#SALE_ACTIVE_ID").val('');
								$("#SALE_ACTIVE_FEE").val('');
								
								$.endPageLoading();
								$.MessageBox.error(error_code, error_info);
							}							
						);
				}
				else
				{
					//取消宽带1+营销活动，如果有魔百和营销活动则校验魔百和营销活动是否依赖于宽带营销活动
					if (null != topSetBoxSaleActiveId && '' != topSetBoxSaleActiveId)
					{
						var inparam = "&ROUTE_EPARCHY_CODE="+ eparchyCode +"&SERIAL_NUMBER="+$("#AUTH_SERIAL_NUMBER").val()
						+"&TOP_SET_BOX_SALE_ACTIVE_ID="+topSetBoxSaleActiveId;
				
						$.beginPageLoading("宽带活动校验中。。。");
						
						$.ajax.submit(null, 'checkSaleActiveDependence', inparam, null,
								function(data) 
								{
									$.endPageLoading();
							
									if ('-1' == data.get('RESULT_CODE'))
									{
										alert('用户选择的魔百和营销活动依赖于当前取消的宽带1+营销活动，取消宽带1+营销活动将同时取消魔百和营销活动！');
										
										$("#TOP_SET_BOX_SALE_ACTIVE_ID").val('');
										$("#TOP_SET_BOX_SALE_ACTIVE_EXPLAIN").val('');
										
										//魔百和费用设置为没有魔百和营销活动的费用
										$("#TOP_SET_BOX_DEPOSIT").val($("#HIDDEN_TOP_SET_BOX_DEPOSIT").val());
									}
								}, function(error_code, error_info) 
								{
									$.endPageLoading();
									$.MessageBox.error(error_code, error_info);
								}							
							);
					}
					
					$("#SALE_ACTIVE_EXPLAIN").val('');
					//宽带营销活动预存
					$("#SALE_ACTIVE_FEE").val('');

					
					$.ajax.submit(null, null, inparam, 'saleActivePartAttr',
							function(data) 
							{
							}, function(error_code, error_info) 
							{
							}							
						);
					$.mergewideusercreate.setModemNumeric();
				}
				
				//Add by zhangxing3 for REQ201807310009有手机号码候鸟宽带资费
				//宽带营销活动变更是时，重置魔百和信息
				$.ajax.submit(null, 'initTopSetBox', inparam, 'topSetBoxInfoPart',
						function(data) 
						{
							$("#TOP_SET_BOX_DEPOSIT").val('0');		
						}, function(error_code, error_info) 
						{
						}							
					);
				//add by zhangxing3 for REQ201807310009有手机号码候鸟宽带资费

			},

			changeSaleActive2 : function()
			{
				var saleActiveId = $("#SALE_ACTIVE_ID2").val();
				
				var topSetBoxSaleActiveId = $("#TOP_SET_BOX_SALE_ACTIVE_ID2").val();
				
				if (null != saleActiveId && '' != saleActiveId)
				{
					//产品组件已选取元素列表
					var selectedList = selectedElements.selectedEls;
					
					var discntIds = '';
					var serviceIds = '';
					
					//营销活动类型
					var saleActiveCampnType = '';
					
					//营销方案ID
					var saleActiveProductId = '';
					
				    //营销包ID
					var saleActivePackageId = '';
					
					//营销活动描述
					var saleActiveExplain = '';
					
					//营销活动预存费用
					var saleActiveFee = '';
					

					if (null != SALE_ACTIVE_LIST2)
					{
						for (var i = 0; i < SALE_ACTIVE_LIST2.length; i++)
						{
							if (saleActiveId == SALE_ACTIVE_LIST2.get(i).get('PARA_CODE2'))
							{
								//营销活动类型
								saleActiveCampnType = SALE_ACTIVE_LIST2.get(i).get("PARA_CODE6");
								
								//营销方案ID
								saleActiveProductId = SALE_ACTIVE_LIST2.get(i).get("PARA_CODE4");
								
							    //营销包ID
								 saleActivePackageId = SALE_ACTIVE_LIST2.get(i).get("PARA_CODE5");
								
								//营销活动描述
								saleActiveExplain = SALE_ACTIVE_LIST2.get(i).get("PARA_CODE24");
								
								//营销活动预存费用
								saleActiveFee = SALE_ACTIVE_LIST2.get(i).get("PARA_CODE7"); 
								 
								break;
							}
						}
					}
					
					var para_code1 = $("#WIDE_PRODUCT_ID").val();
					var eparchyCode = $.auth.getAuthData().get("USER_INFO").get("EPARCHY_CODE");
					
					var inparam = "&TRADE_TYPE_CODE="+ $("#TRADE_TYPE_CODE").val() + "&ROUTE_EPARCHY_CODE="
							+ eparchyCode + "&DISCNT_IDS="+discntIds+"&WIDE_USER_SELECTED_SERVICEIDS="+serviceIds
							+"&SERIAL_NUMBER="+$("#AUTH_SERIAL_NUMBER").val()+"&CAMPN_TYPE="+saleActiveCampnType
							+"&PRODUCT_ID="+saleActiveProductId+"&PACKAGE_ID="+saleActivePackageId
							+"&TOP_SET_BOX_SALE_ACTIVE_ID"+topSetBoxSaleActiveId+"&PARA_CODE1="+para_code1;
					
					$.beginPageLoading("调试费活动校验中。。。");
					
					$.ajax.submit(null, 'checkSaleActive', inparam, 'saleActivePartAttr',
							function(data) 
							{
								$.endPageLoading();

								
									$("#SALE_ACTIVE_EXPLAIN2").val(saleActiveExplain);
				 					//宽带营销活动预存
				 					//营销活动费用需要重新取产品模型配置
				 					$.mergewideusercreate.checkWideNetSaleActiveFee2(saleActiveCampnType,saleActiveProductId,saleActivePackageId);				 					
				 					//$.mergewideusercreate.setModemNumeric();

							}, function(error_code, error_info) 
							{
								$("#SALE_ACTIVE_EXPLAIN2").val('');
								$("#SALE_ACTIVE_ID2").val('');
								$("#SALE_ACTIVE_FEE2").val('');
								
								$.endPageLoading();
								$.MessageBox.error(error_code, error_info);
							}							
						);
				}
				else
				{
					//取消宽带1+营销活动，如果有魔百和营销活动则校验魔百和营销活动是否依赖于宽带营销活动
					if (null != topSetBoxSaleActiveId && '' != topSetBoxSaleActiveId)
					{
						var inparam = "&ROUTE_EPARCHY_CODE="+ eparchyCode +"&SERIAL_NUMBER="+$("#AUTH_SERIAL_NUMBER").val()
						+"&TOP_SET_BOX_SALE_ACTIVE_ID="+topSetBoxSaleActiveId;
				
						$.beginPageLoading("宽带活动校验中。。。");
						
						$.ajax.submit(null, 'checkSaleActiveDependence', inparam, null,
								function(data) 
								{
									$.endPageLoading();
							
									if ('-1' == data.get('RESULT_CODE'))
									{
										MessageBox.alert("提示",'用户选择的魔百和营销活动依赖于当前取消的宽带1+营销活动，取消宽带1+营销活动将同时取消魔百和营销活动！');
										
										$("#TOP_SET_BOX_SALE_ACTIVE_ID").val('');
										$("#TOP_SET_BOX_SALE_ACTIVE_EXPLAIN").val('');
										
										//魔百和费用设置为没有魔百和营销活动的费用
										$("#TOP_SET_BOX_DEPOSIT").val($("#HIDDEN_TOP_SET_BOX_DEPOSIT").val());
									}
								}, function(error_code, error_info) 
								{
									$.endPageLoading();
									$.MessageBox.error(error_code, error_info);
								}							
							);
					}
					
					$("#SALE_ACTIVE_EXPLAIN").val('');
					//宽带营销活动预存
					$("#SALE_ACTIVE_FEE").val('');

					
					$.ajax.submit(null, null, inparam, 'saleActivePartAttr',
							function(data) 
							{
							}, function(error_code, error_info) 
							{
							}							
						);
					//$.mergewideusercreate.setModemNumeric();
				}
				
			},
			
			afterRenderSelectedElements : function(data) {
				var temp = data.get(0);
				$("#NEW_PRODUCT_START_DATE").val(temp.get("NEW_PRODUCT_START_DATE"));
			},

			onSubmit : function() 
			{
				var serialNum = $("#AUTH_SERIAL_NUMBER").val();
				
				var wideProductType = $("#WIDE_PRODUCT_TYPE").val();

				// 移动FTTH和铁通FTTH需要申请光猫
				//如果是商务宽带
				var isBusinessWide = $("#IS_BUSINESS_WIDE").val();
				if (('3' == wideProductType || '5' == wideProductType) && 'Y' != isBusinessWide)
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
				
				//如果选择了魔百和产品则需要校验必填项
				if (null != $("#TOP_SET_BOX_PRODUCT_ID").val() && "" != $("#TOP_SET_BOX_PRODUCT_ID").val())
				{
					if (!verifyAll('topSetBoxProductPart'))
					{
						return false;
					}
				}
				
				var modemDeposit = $("#MODEM_DEPOSIT").val();
				var topSetBoxDeposit = $("#TOP_SET_BOX_DEPOSIT").val();
				var saleActiveFee = $("#SALE_ACTIVE_FEE").val();
				var topSetBoxSaleActiveFee = $("#TOP_SET_BOX_SALE_ACTIVE_FEE").val();
				
				if (null == modemDeposit || '' == modemDeposit)
				{
					modemDeposit = '0';
				}
				
				if (null == topSetBoxDeposit || '' == topSetBoxDeposit)
				{
					topSetBoxDeposit = '0';
				}
				
				if (null == saleActiveFee || '' == saleActiveFee)
				{
					saleActiveFee = '0';
				}
				
				if (null == topSetBoxSaleActiveFee || '' == topSetBoxSaleActiveFee)
				{
					topSetBoxSaleActiveFee = '0';
				}
				
				var feeFlag = true ;
				var totalFee = parseFloat(modemDeposit) + parseFloat(topSetBoxDeposit) + parseFloat(saleActiveFee)/100 + parseFloat(topSetBoxSaleActiveFee)/100;
				
				if (totalFee > 0)
				{
					feeFlag = false ;
					var tips = "您总共需要从现金存折中转出："+totalFee+"元,其中宽带活动预存："+parseFloat(saleActiveFee)/100+"元，魔百和营销活动预存："
								+parseFloat(topSetBoxSaleActiveFee)/100+"元，光猫押金："+modemDeposit+"元，魔百和押金："+topSetBoxDeposit
								+"元，请确认您的余额是否足够?";
								
					//宽带付费方式   A：先装后付，P：立即付费
					var widePayMode = $("#WIDENET_PAY_MODE").val();
					
					if ("A" == widePayMode)
					{
						tips = "本次宽带开户总费用为："+totalFee+"元,其中宽带活动预存："+parseFloat(saleActiveFee)/100+"元，魔百和营销活动预存："
						+parseFloat(topSetBoxSaleActiveFee)/100+"元，光猫押金："+modemDeposit+"元，魔百和押金："+topSetBoxDeposit
						+"元，费用将在施工完成后收取,请确认是否继续?";
					}
					
					if(window.confirm(tips))
					{
						//后付费不需要校验余额
						if ("A" == widePayMode)
						{
							feeFlag = true;
						}
						else
						{
							//后台余额校验
							feeFlag = $.mergewideusercreate.checkFeeBeforeSubmit();
						}
						
					}
					else
					{
						return false;
					}
				}
				
				if(!feeFlag)
				{
					return false ;
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
				
				return true;
			},

			setModemNumeric : function() 
			{
				if ($.auth.getAuthData() == undefined)
				{
					MessageBox.alert("告警提示","请先进行用户鉴权操作！");
					
					$("#MODEM_STYLE").val('');
					
					return false;
				}
				
				if ($("#MODEM_STYLE").val() == "") 
				{
					$("#MODEM_DEPOSIT").val("0");
					
//					$.feeMgr.removeFee($("#TRADE_TYPE_CODE").val(),"0","9205");
					return false;
				}
				
				//赠送校验
				if ($("#MODEM_STYLE").val() == "2") {
					$.ajax.submit('', 'checkModemFTTH', '', '', function(data){
						if("1"==data.get("status")){
							var warnning_value_u=parseInt(data.get("WARNNING_VALUE_U"));
							var warnning_value_d=parseInt(data.get("WARNNING_VALUE_D"));
							if(warnning_value_u>=warnning_value_d){
								alert("您已经达到办理限额");
								$("#MODEM_STYLE").val('');
								return;
							};
						}else if("0"==data.get("status")){
							alert("您不具备办理本业务的岗位");
							$("#MODEM_STYLE").val('');
							return;
						};
						
					});
				}
				
				// 如果是租用则需要查询押金额度已经校验用户现金存折余额是否足够
				if ($("#MODEM_STYLE").val() == "0") 
				{
					$.mergewideusercreate.PAGE_FEE_LIST.clear();
					$.feeMgr.removeFee($("#TRADE_TYPE_CODE").val(),"0","9205");
					
					var param = "&saleActiveId="+$("#SALE_ACTIVE_ID").val()
					+"&serialNumber="+$("#AUTH_SERIAL_NUMBER").val() 
					+ "&ROUTE_EPARCHY_CODE=" + $.auth.getAuthData().get("USER_INFO").get("EPARCHY_CODE")
					+ "&TOP_SET_BOX_DEPOSIT=" + $("#TOP_SET_BOX_DEPOSIT").val();;
					
					$.beginPageLoading("MODEM押金信息查询中......");
					
					$.ajax.submit('', 'checkModemDeposit', param, '', function(rtnData) { 
						if(rtnData!=null&&rtnData.length > 0)
						{
							if ('-1' == rtnData.get('resultCode'))
							{
								alert(rtnData.get('resultInfo'));
								$("#MODEM_STYLE").val('');
								$("#MODEM_DEPOSIT").val('0');
							}
							else
							{
								$("#MODEM_DEPOSIT").val(rtnData.get("MODEM_DEPOSIT"));
							}
								
							$.endPageLoading();
						}
						else
						{
								$.endPageLoading();
								alert("程序出错，未找到数据！");
								return false; 
							}
						}, function(error_code, error_info,detail) {
							$.endPageLoading();
							MessageBox.error("错误提示", error_info, null, null, null, detail);
							$("#MODEM_STYLE").val('');
							$("#MODEM_DEPOSIT").val('0');
						}); 
				} 
				//如果是购买需要交纳营业费
				else if ($("#MODEM_STYLE").val() == "1") 
				{
					$("#MODEM_DEPOSIT").val('0');
					
					//铁通MODEM购买费用 属于营业费用
//					$.mergewideusercreate.PAGE_FEE_LIST.clear();
//					
//					$.feeMgr.removeFee($("#TRADE_TYPE_CODE").val(),"0","9205");
//					
//					var feeData = $.DataMap();
//					feeData.clear();
//					feeData.put("MODE", "0");
//					feeData.put("CODE", "9205");
//					feeData.put("FEE", "30000");
//					feeData.put("PAY", "30000");
//					feeData.put("TRADE_TYPE_CODE", $("#TRADE_TYPE_CODE").val());
//					$.feeMgr.insertFee(feeData);
//					$.mergewideusercreate.PAGE_FEE_LIST.put("RES_FEE", $.feeMgr.cloneData(feeData));
				}
				else
				{
					$("#MODEM_DEPOSIT").val('0');
				}
			},
			/**
			 * 查询基础优惠包和可选优惠包、以及魔百和营销活动
			 */
			queryTopSetBoxPackages :function() 
			{
				var topSetBoxSaleActiveId = $("#TOP_SET_BOX_SALE_ACTIVE_ID").val();
				var saleActiveId = $("#SALE_ACTIVE_ID").val();
				var wideProductType = $("#WIDE_PRODUCT_TYPE").val();//选择魔百和IPTV需要用到wangsc10
				var wideProductId = $("#WIDE_PRODUCT_ID").val();//选择魔百和IPTV需要用到wangsc10

				var param = "&TOP_SET_BOX_PRODUCT_ID=" + $("#TOP_SET_BOX_PRODUCT_ID").val()
				+"&serialNumber="+$("#AUTH_SERIAL_NUMBER").val() 
				+ "&ROUTE_EPARCHY_CODE=" + $.auth.getAuthData().get("USER_INFO").get("EPARCHY_CODE")
				+ "&MODEM_DEPOSIT=" + $("#MODEM_DEPOSIT").val() + "&TOP_SET_BOX_SALE_ACTIVE_ID=" + topSetBoxSaleActiveId
				+ "&SALE_ACTIVE_ID=" + saleActiveId + "&WIDE_PRODUCT_TYPE=" + wideProductType+ "&WIDE_PRODUCT_ID=" + wideProductId;
				
				$.beginPageLoading("魔百和基础优惠包和可选优惠包查询中......");
				$.ajax.submit(null, 'queryTopSetBoxDiscntPackagesByPID',param , 'bPackagePart,oPackagePart,pPackagePart,topSetBoxSaleActivePart,topSetBoxSaleActivePart2',
						function(data) 
						{
							if ('-1' == data.get('resultCode'))
							{
								alert(data.get('resultInfo'));
							}
							//拦截提示
							if ('-1' == data.get('resultIPTVCode'))
							{
								$.MessageBox.error("拦截提示:", data.get('resultIPTVInfo'));
								$("#TOP_SET_BOX_PRODUCT_ID").val('');
							}
							
							$("#TOP_SET_BOX_DEPOSIT").val(data.get('TOP_SET_BOX_DEPOSIT')/100);
							
							if (null == $("#HIDDEN_TOP_SET_BOX_DEPOSIT").val() || '' == $("#HIDDEN_TOP_SET_BOX_DEPOSIT").val())
							{
								$("#HIDDEN_TOP_SET_BOX_DEPOSIT").val(data.get('TOP_SET_BOX_DEPOSIT')/100)
							}
							
							TOP_SET_BOX_SALE_ACTIVE_LIST = data.get('TOP_SET_BOX_SALE_ACTIVE_LIST');
							
							$("#TOP_SET_BOX_SALE_ACTIVE_EXPLAIN").val('');
							
							//BUS201907310012关于开发家庭终端调测费的需求
							TOP_SET_BOX_SALE_ACTIVE_LIST2 = data.get('TOP_SET_BOX_SALE_ACTIVE_LIST2');
							//BUS201907310012关于开发家庭终端调测费的需求
							
							$.endPageLoading();
						}, function(error_code, error_info, derror) {
							$.endPageLoading();
							showDetailErrorInfo(error_code, error_info, derror);
							$("#TOP_SET_BOX_PRODUCT_ID").val('');
						});
			},
			changeTopSetBoxSaleActive : function()
			{
				var topSetBoxSaleActiveId = $("#TOP_SET_BOX_SALE_ACTIVE_ID").val();
				var saleActiveId = $("#SALE_ACTIVE_ID").val();
				
				if (null != topSetBoxSaleActiveId && '' != topSetBoxSaleActiveId)
				{
					
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
					depWilens =''; 
					
					//过滤出优惠ID
					if (selectedList.length > 1)
					{
						selectedList.each(function(item,index,totalcount)
						{
							if (item.get("ELEMENT_TYPE_CODE")=="S")
							{
								if ('' != serviceIds)
								{
									serviceIds += '|'+item.get("ELEMENT_ID");
								}
								else
								{
									serviceIds = item.get("ELEMENT_ID");
								}
							}
						});
					}
					
					//
					if (null != TOP_SET_BOX_SALE_ACTIVE_LIST)
					{
						for (var i = 0; i < TOP_SET_BOX_SALE_ACTIVE_LIST.length; i++)
						{
							if (topSetBoxSaleActiveId == TOP_SET_BOX_SALE_ACTIVE_LIST.items[i].get('PARA_CODE2'))
							{
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
					
					var eparchyCode = $.auth.getAuthData().get("USER_INFO").get("EPARCHY_CODE");
					
					var inparam = "&TRADE_TYPE_CODE="+ $("#TRADE_TYPE_CODE").val() + "&ROUTE_EPARCHY_CODE="
							+ eparchyCode + "&WIDE_USER_SELECTED_SERVICEIDS="+serviceIds
							+"&SERIAL_NUMBER="+$("#AUTH_SERIAL_NUMBER").val()+"&CAMPN_TYPE="+topSetBoxSaleActiveCampnType
							+"&PRODUCT_ID="+topSetBoxSaleActiveProductId+"&PACKAGE_ID="+topSetBoxSaleActivePackageId+"&TOP_SET_BOX_SALE_ACTIVE_ID="+topSetBoxSaleActiveId
							+"&SALE_ACTIVE_ID="+saleActiveId+"&IS_TOP_SET_BOX_SALE_ACTIVE=Y";
					 
					var depflag="0"//校验通过规则，1=通过，0=不通过
					if(depTag=="1")
					{//依赖宽带产品标记，1=依赖。其他不依赖。
						var productId = $("#WIDE_PRODUCT_ID").val(); //宽带产品
						var wilens=depWilens.split("|");
						for(var k=0;k<wilens.length;k++){
							var depProdId=wilens[k];
							if(productId==depProdId){
								depflag="1";
							}
						}
						if(depflag=="0")
						{
							$.MessageBox.error("-1", "该宽带产品不允许办理该营销活动。");
							
							$("#TOP_SET_BOX_SALE_ACTIVE_EXPLAIN").val('');
							$("#TOP_SET_BOX_SALE_ACTIVE_ID").val('');
							$("#TOP_SET_BOX_SALE_ACTIVE_FEE").val('');
							
							$("#TOP_SET_BOX_DEPOSIT").val($("#HIDDEN_TOP_SET_BOX_DEPOSIT").val());
							
							return;
						}
					} 
					
					$.beginPageLoading("魔百和活动校验中。。。"); 
					$.ajax.submit(null, 'checkSaleActive', inparam, null,
							function(data) 
							{
								$.endPageLoading();
								if ('-1' == data.get('resultCode'))
								{
//										$.MessageBox.error('-1', '用户已经选择了包年优惠，不能再办理宽带营销活动，如果要办理请先取消包年优惠！');
									
									$("#TOP_SET_BOX_SALE_ACTIVE_EXPLAIN").val('');
									$("#TOP_SET_BOX_SALE_ACTIVE_ID").val('');
									$("#TOP_SET_BOX_SALE_ACTIVE_FEE").val('');
									
									$("#TOP_SET_BOX_DEPOSIT").val($("#HIDDEN_TOP_SET_BOX_DEPOSIT").val());
								}
								else
								{
									$("#TOP_SET_BOX_SALE_ACTIVE_EXPLAIN").val(topSetBoxSaleActiveExplain);
									 
									 //宽带营销活动预存,不能直接TD_S_COMMPARA表的配置，以防配错
									 //$("#TOP_SET_BOX_SALE_ACTIVE_FEE").val(topSetBoxSaleActiveFee);
									
									$("#TOP_SET_BOX_DEPOSIT").val("0");
									
									 //营销活动费用需要重新取产品模型配置
					 				$.mergewideusercreate.checkTopBoxSetSaleActiveFee(topSetBoxSaleActiveCampnType,topSetBoxSaleActiveProductId,topSetBoxSaleActivePackageId);
								}

							}, function(error_code, error_info) 
							{
								$("#TOP_SET_BOX_SALE_ACTIVE_EXPLAIN").val('');
								$("#TOP_SET_BOX_SALE_ACTIVE_ID").val('');
								$("#TOP_SET_BOX_SALE_ACTIVE_FEE").val('');
								
								$("#TOP_SET_BOX_DEPOSIT").val($("#HIDDEN_TOP_SET_BOX_DEPOSIT").val());
								
								$.endPageLoading();
								$.MessageBox.error(error_code, error_info);
							});
				}
				else
				{
					$("#TOP_SET_BOX_SALE_ACTIVE_EXPLAIN").val('');
					
					//宽带魔百和营销活动预存
					$("#TOP_SET_BOX_SALE_ACTIVE_FEE").val('');
					
					$("#TOP_SET_BOX_DEPOSIT").val($("#HIDDEN_TOP_SET_BOX_DEPOSIT").val());
				}
			},
			
			changeTopSetBoxSaleActive2 : function()
			{
				var topSetBoxSaleActiveId = $("#TOP_SET_BOX_SALE_ACTIVE_ID2").val();
				var saleActiveId = $("#SALE_ACTIVE_ID").val();

				if (null != topSetBoxSaleActiveId && '' != topSetBoxSaleActiveId)
				{
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
					depWilens =''; 
					//过滤出优惠ID
					if (selectedList.length > 1)
					{
						selectedList.each(function(item,index,totalcount)
						{
							if (item.get("ELEMENT_TYPE_CODE")=="S")
							{
								if ('' != serviceIds)
								{
									serviceIds += '|'+item.get("ELEMENT_ID");
								}
								else
								{
									serviceIds = item.get("ELEMENT_ID");
								}
							}
						});
					}
					if (null != TOP_SET_BOX_SALE_ACTIVE_LIST2)
					{
						for (var i = 0; i < TOP_SET_BOX_SALE_ACTIVE_LIST2.length; i++)
						{
							if (topSetBoxSaleActiveId == TOP_SET_BOX_SALE_ACTIVE_LIST2.get(i).get('PARA_CODE2'))
							{
								//魔百和营销活动类型
								topSetBoxSaleActiveCampnType = TOP_SET_BOX_SALE_ACTIVE_LIST2.get(i).get("PARA_CODE6");
								
								//魔百和营销方案ID
								topSetBoxSaleActiveProductId = TOP_SET_BOX_SALE_ACTIVE_LIST2.get(i).get("PARA_CODE4");
								
							    //魔百和营销包ID
								topSetBoxSaleActivePackageId = TOP_SET_BOX_SALE_ACTIVE_LIST2.get(i).get("PARA_CODE5");
								
								//魔百和营销活动描述
								topSetBoxSaleActiveExplain = TOP_SET_BOX_SALE_ACTIVE_LIST2.get(i).get("PARA_CODE24");
								
								//魔百和营销活动预存费用
								topSetBoxSaleActiveFee = TOP_SET_BOX_SALE_ACTIVE_LIST2.get(i).get("PARA_CODE7"); 
								
								//是否走依赖宽带产品的规则，为空或者不为1则不走。
								depTag = TOP_SET_BOX_SALE_ACTIVE_LIST2.get(i).get("PARA_CODE22"); 
								//依赖的宽带产品
								depWilens = TOP_SET_BOX_SALE_ACTIVE_LIST2.get(i).get("PARA_CODE23"); 
								 
								break;
							}
						}
					}
					
					var eparchyCode = $.auth.getAuthData().get("USER_INFO").get("EPARCHY_CODE");
					
					var inparam = "&TRADE_TYPE_CODE="+ $("#TRADE_TYPE_CODE").val() + "&ROUTE_EPARCHY_CODE="
							+ eparchyCode + "&WIDE_USER_SELECTED_SERVICEIDS="+serviceIds
							+"&SERIAL_NUMBER="+$("#AUTH_SERIAL_NUMBER").val()+"&CAMPN_TYPE="+topSetBoxSaleActiveCampnType
							+"&PRODUCT_ID="+topSetBoxSaleActiveProductId+"&PACKAGE_ID="+topSetBoxSaleActivePackageId+"&TOP_SET_BOX_SALE_ACTIVE_ID="+topSetBoxSaleActiveId
							+"&SALE_ACTIVE_ID="+saleActiveId+"&IS_TOP_SET_BOX_SALE_ACTIVE=Y";					  
					
					$.beginPageLoading("魔百和调测费活动校验中。。。"); 
					$.ajax.submit(null, 'checkSaleActive', inparam, null,
							function(data) 
							{
								$.endPageLoading();
								if ('-1' == data.get('resultCode'))
								{
//										$.MessageBox.error('-1', '用户已经选择了包年优惠，不能再办理宽带营销活动，如果要办理请先取消包年优惠！');
									
									$("#TOP_SET_BOX_SALE_ACTIVE_EXPLAIN2").val('');
									$("#TOP_SET_BOX_SALE_ACTIVE_ID2").val('');
									$("#TOP_SET_BOX_SALE_ACTIVE_FEE2").val('');
									
								}
								else
								{
									$("#TOP_SET_BOX_SALE_ACTIVE_EXPLAIN2").val(topSetBoxSaleActiveExplain);									
									 //营销活动费用需要重新取产品模型配置
					 				$.mergewideusercreate.checkTopBoxSetSaleActiveFee2(topSetBoxSaleActiveCampnType,topSetBoxSaleActiveProductId,topSetBoxSaleActivePackageId);
								}

							}, function(error_code, error_info) 
							{
								$("#TOP_SET_BOX_SALE_ACTIVE_EXPLAIN2").val('');
								$("#TOP_SET_BOX_SALE_ACTIVE_ID2").val('');
								$("#TOP_SET_BOX_SALE_ACTIVE_FEE2").val('');																
								$.endPageLoading();
								$.MessageBox.error(error_code, error_info);
							});
				}
				else
				{
					$("#TOP_SET_BOX_SALE_ACTIVE_EXPLAIN2").val('');					
					//宽带魔百和营销活动预存
					$("#TOP_SET_BOX_SALE_ACTIVE_FEE2").val('');
					
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
			},
			//查询宽带类营销活动预存费用
			checkWideNetSaleActiveFee:function(campnType,productId,packageId)
			{
				$.beginPageLoading("宽带活动费用校验中。。。");
				var serialNumber = $("#AUTH_SERIAL_NUMBER").val();
				var param = "&SERIAL_NUMBER="+ serialNumber + "&CAMPN_TYPE=" + campnType 
						+ "&PRODUCT_ID=" + productId + "&PACKAGE_ID=" + packageId + "&ACTIVE_FLAG=1"  ;
						
				$.ajax.submit(null, 'queryCheckSaleActiveFee', param, null,
					function(data) 
					{
						$.endPageLoading();
						//设置营销活动费用
						var fee = data.get("SALE_ACTIVE_FEE");
						$("#SALE_ACTIVE_FEE").val(fee);
					}, function(error_code, error_info) 
					{
						$("#SALE_ACTIVE_EXPLAIN").val('');
						$("#SALE_ACTIVE_ID").val('');
						$("#SALE_ACTIVE_FEE").val('');
						$.endPageLoading();
						$.MessageBox.error(error_code, error_info);
					}
				);
			},
			//查询魔百和营销活动预存费用
			checkTopBoxSetSaleActiveFee:function(campnType,productId,packageId)
			{
				$.beginPageLoading("魔百和营销活动费用校验中。。。");
				var serialNumber = $("#AUTH_SERIAL_NUMBER").val();
				var param = "&SERIAL_NUMBER="+ serialNumber + "&CAMPN_TYPE=" + campnType 
						+ "&PRODUCT_ID=" + productId + "&PACKAGE_ID=" + packageId + "&ACTIVE_FLAG=2"  ;
				
				$.ajax.submit(null, 'queryCheckSaleActiveFee', param, null,
					function(data) 
					{
						$.endPageLoading();
						var fee = data.get("SALE_ACTIVE_FEE");
						$("#TOP_SET_BOX_SALE_ACTIVE_FEE").val(fee);
					}, function(error_code, error_info) 
					{
						$("#TOP_SET_BOX_SALE_ACTIVE_EXPLAIN").val('');
						$("#TOP_SET_BOX_SALE_ACTIVE_ID").val('');
						$("#TOP_SET_BOX_SALE_ACTIVE_FEE").val('');
								
						$.endPageLoading();
						$.MessageBox.error(error_code, error_info);
					}
				);
			},
			
			//查询宽带类营销活动预存费用--BUS201907310012关于开发家庭终端调测费的需求
			checkWideNetSaleActiveFee2:function(campnType,productId,packageId)
			{
				$.beginPageLoading("调测费活动费用校验中。。。");
				var serialNumber = $("#AUTH_SERIAL_NUMBER").val();
				var param = "&SERIAL_NUMBER="+ serialNumber + "&CAMPN_TYPE=" + campnType 
						+ "&PRODUCT_ID=" + productId + "&PACKAGE_ID=" + packageId + "&ACTIVE_FLAG=1"  ;
						
				$.ajax.submit(null, 'queryCheckSaleActiveFee', param, null,
					function(data) 
					{
						$.endPageLoading();
						//设置营销活动费用
						var fee = data.get("SALE_ACTIVE_FEE");
						$("#SALE_ACTIVE_FEE2").val(fee);
					}, function(error_code, error_info) 
					{
						$("#SALE_ACTIVE_EXPLAIN2").val('');
						$("#SALE_ACTIVE_ID2").val('');
						$("#SALE_ACTIVE_FEE2").val('');
						$.endPageLoading();
						$.MessageBox.error(error_code, error_info);
					}
				);
			},	
			//查询魔百和调测费营销活动预存费用
			checkTopBoxSetSaleActiveFee2:function(campnType,productId,packageId)
			{
				$.beginPageLoading("魔百和调测费营销活动费用校验中。。。");
				var serialNumber = $("#AUTH_SERIAL_NUMBER").val();
				var param = "&SERIAL_NUMBER="+ serialNumber + "&CAMPN_TYPE=" + campnType 
						+ "&PRODUCT_ID=" + productId + "&PACKAGE_ID=" + packageId + "&ACTIVE_FLAG=2"  ;
				
				$.ajax.submit(null, 'queryCheckSaleActiveFee', param, null,
					function(data) 
					{
						$.endPageLoading();
						var fee = data.get("SALE_ACTIVE_FEE");
						$("#TOP_SET_BOX_SALE_ACTIVE_FEE2").val(fee);
					}, function(error_code, error_info) 
					{
						$("#TOP_SET_BOX_SALE_ACTIVE_EXPLAIN2").val('');
						$("#TOP_SET_BOX_SALE_ACTIVE_ID2").val('');
						$("#TOP_SET_BOX_SALE_ACTIVE_FEE2").val('');
								
						$.endPageLoading();
						$.MessageBox.error(error_code, error_info);
					}
				);
			},
			
			//提交前费用校验
			checkFeeBeforeSubmit:function()
			{
				var modemDeposit = $("#MODEM_DEPOSIT").val();
				var topSetBoxDeposit = $("#TOP_SET_BOX_DEPOSIT").val();
				var saleActiveFee = $("#SALE_ACTIVE_FEE").val();
				var topSetBoxSaleActiveFee = $("#TOP_SET_BOX_SALE_ACTIVE_FEE").val();
				var result = false ;
				if (null == modemDeposit || '' == modemDeposit)
				{
					modemDeposit = '0';
				}
				
				if (null == topSetBoxDeposit || '' == topSetBoxDeposit)
				{
					topSetBoxDeposit = '0';
				}
				
				if (null == saleActiveFee || '' == saleActiveFee)
				{
					saleActiveFee = '0';
				}
				
				if (null == topSetBoxSaleActiveFee || '' == topSetBoxSaleActiveFee)
				{
					topSetBoxSaleActiveFee = '0';
				}
				
				var param = "&MODEM_DEPOSIT=" + modemDeposit + "&TOPSETBOX_DEPOSIT=" + topSetBoxDeposit
							+ "&SALE_ACTIVE_FEE=" + saleActiveFee + "&TOPSETBOX_SALE_ACTIVE_FEE=" + topSetBoxSaleActiveFee
							+ "&SERIAL_NUMBER="+$("#AUTH_SERIAL_NUMBER").val();
				
				$.beginPageLoading("提交前费用校验中。。。");			
				$.ajax.submit(null, 'checkFeeBeforeSubmit', param, null,
					function(data) 
					{
						$.endPageLoading();
						var resultCode = data.get("X_RESULTCODE");
						if(resultCode == '0')
							result = true ;
					}, function(error_code, error_info) 
					{
						$.endPageLoading();
						$.MessageBox.error(error_code, error_info);
						result = false ;
					},
					{async:false}
				);
				
				return result ;
			},
			afterCheckBoxAction : function()
			{
				var saleActiveId =  $("#SALE_ACTIVE_ID").val();
				
				if (null != saleActiveId && '' != saleActiveId)
				{
					//产品组件已选取元素列表
					var selectedList = selectedElements.selectedEls;
					
					var discntIds = '';
					
					//过滤出优惠ID
					if (selectedList.length > 1)
					{
						selectedList.each(function(item,index,totalcount)
						{
							if(item.get("ELEMENT_TYPE_CODE")=="D" && "0" == item.get("MODIFY_TAG"))
							{
								if ('' != discntIds)
								{
									discntIds += ','+item.get("ELEMENT_ID");
								}
								else
								{
									discntIds = item.get("ELEMENT_ID");
								}
							}
						});
					}
					
					var inparam = "&ROUTE_EPARCHY_CODE="+ $.auth.getAuthData().get("USER_INFO").get("EPARCHY_CODE") + "&DISCNT_IDS="+discntIds
					
					$.beginPageLoading("加载中。。。");
					
					//调用后台重新算费
					ajaxSubmit(null,'checkSelectedDiscnts',inparam,'',function(data)
					{
						if ('-1' == data.get('resultCode'))
						{
							$.MessageBox.error('-1', '用户已经选择了宽带营销活动，不能再选择包年优惠，如果要办理请先取消宽带营销活动！');
							
							var discntCheckboxs = $("*[name='SELECTED_DISCNT_CHECKBOX']");
				    		
				    		for (var i = 0; i < discntCheckboxs.length; i++)
				    		{
				    			var discntCheckbox = discntCheckboxs[i];
				    			
				    			if (data.get('DISCNT_CODE') == discntCheckbox.value)
				    			{
				    				discntCheckbox.click();
				    			}
				    		}
				        }
						if ('-2' == data.get('resultCode')) {
							$.MessageBox.error('-1', '用户已经选择了VIP体验套餐，不能再办理宽带营销活动，如果要办理请先取消VIP体验套餐！');
							var discntCheckboxs = $("*[name='SELECTED_DISCNT_CHECKBOX']");
				    		
				    		for (var i = 0; i < discntCheckboxs.length; i++)
				    		{
				    			var discntCheckbox = discntCheckboxs[i];
				    			
				    			if (data.get('DISCNT_CODE') == discntCheckbox.value)
				    			{
				    				discntCheckbox.click();
				    			}
				    		}
						}
						
						$.endPageLoading();
					},
					function(error_code,error_info)
					{
						$.endPageLoading();
						$.MessageBox.error(error_code, error_info);
					});
		        }
			}
		}
	}
})(Wade);
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

/**
 * 地址建设规划查询
 * @author zhengkai5
 * 20170922
 */
function reginalField(){
	$.popupPage('pres.popup.villagePlan','init','&villageName=','小区建设规划排期数据','800','500','','',subsys_cfg.pbossintf);
}

//标准地址查询
function addressSelect(){
	var serialNumber= $("#AUTH_SERIAL_NUMBER").val();
	var contactSn = $("#CONTACT_PHONE").val();
	var custName = $("#CONTACT").val();
	var param = "&AUTH_SERIAL_NUMBER="+serialNumber+"&CUST_NAME="+encodeURIComponent(custName)+"&CONTACT_SN="+contactSn;
	$.popupPage('res.popup.AddressQry','init',param+'&TREE_TYPE=0','标准地址','1000','340','STAND_ADDRESS','',subsys_cfg.pbossintf);
}

//树状地址查询
function addressTreeSelect(){
	var serialNumber= $("#AUTH_SERIAL_NUMBER").val();
	var contactSn = $("#CONTACT_PHONE").val();
	var custName = $("#CONTACT").val();
	var param = "&AUTH_SERIAL_NUMBER="+serialNumber+"&CUST_NAME="+encodeURIComponent(custName)+"&CONTACT_SN="+contactSn;
	$.popupPage('res.popup.AddressQryTree','init',param+'&TREE_TYPE=0','树状地址','1000','340','STAND_ADDRESS','',subsys_cfg.pbossintf);
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


function setTTtransferValue(){
	var checked = $("#is_TT_TRANSFER")[0].checked;
	if(checked){
		$("#TT_TRANSFER").val("1");
	}else{
		$("#TT_TRANSFER").val("0");
	}
}
