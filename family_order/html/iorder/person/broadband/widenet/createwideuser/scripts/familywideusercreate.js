(function($) {
	if (typeof ($.familywideusercreate == "undefined")) {
		
		$.familywideusercreate = {
				
			IMS_SALE_ACTIVE_LIST: $.DatasetList(),
			/**
			 * 控制宽带信息显示\隐藏
			 * @param btn
			 * @param o
			 */
			infoPartDisplaySwitch : function(checkbox, o) {
				var div = $('#' + o);

				if (checkbox.checked == true) 
				{
					if (o == 'IMSPart')
					{
						var wideProductType = $("#WIDE_PRODUCT_TYPE").val();

						// 移动FTTH和铁通FTTH需要申请光猫
						if ('3' != wideProductType && '5' != wideProductType)
						{
							checkbox.checked = false;
							alert("只有FTTH宽带才能办理IMS固话业务！");
							
							return false;
						}
						
						//勾选IMS固话业务显示用户信息
						$("#IMSCustInfoPart").css("display", "");
						if(document.getElementById("CUST_NAME").innerHTML){
							document.getElementById("CUST_NAME").id="CUST_NAME_VIEW";
							document.getElementById("PSPT_ID").id="PSPT_ID_VIEW";
							document.getElementById("PSPT_TYPE_CODE").id="PSPT_TYPE_CODE_VIEW";
							document.getElementById("SEX").id="SEX_VIEW";
							document.getElementById("PSPT_END_DATE").id="PSPT_END_DATE_VIEW";
						}
					}
					
					div.css('display', '');
				} 
				else 
				{
					if (window.confirm("点击取消办理该业务会清除该项业务界面录入的信息，请确认是否继续？"))
					{
						div.css('display', 'none');
						
						if (o == 'topSetBoxPart')
						{
							$("#TOP_SET_BOX_PRODUCT_ID").val("");
							$("#BASE_PACKAGES").val("");
							$("#OPTION_PACKAGES").val("");
							$("#TOP_SET_BOX_SALE_ACTIVE_ID").val("");
							$("#TOP_SET_BOX_DEPOSIT").val("");
							$("#TOP_SET_BOX_SALE_ACTIVE_FEE").val("");
							$("#TOP_SET_BOX_SALE_ACTIVE_EXPLAIN").val("");
							
							$("#TOP_SET_BOX_SALE_ACTIVE_ID2").val("");
							$("#TOP_SET_BOX_SALE_ACTIVE_FEE2").val("");
							$("#TOP_SET_BOX_SALE_ACTIVE_EXPLAIN2").val("");
							
						}
						else if (o == 'IMSPart')
						{
							$("#FIX_NUMBER").val("");
							$("#IMS_PRODUCT_TYPE_CODE").val("");
							$("#IMS_PRODUCT_ID").val("");
							$("#TOP_SET_BOX_SALE_ACTIVE_ID").val("");
							
							//不勾选IMS固话业务不显示用户信息
							$("#IMSCustInfoPart").css("display", "none");
							$("#CUST_NAME").val("");
							$("#SEX").val("");
							$("#BIRTHDAY").val("");
							$("#PSPT_ID").val("");
							$("#PSPT_END_DATE").val("");
							$("#PSPT_ADDR").val("");
							$("#AGENT_CUST_NAME").val("");
							$("#AGENT_PSPT_ID").val("");
							$("#AGENT_PSPT_ADDR").val("");
						}
						else if (o == 'HeMuPart')
						{
							$("#HEMU_SALE_ACTIVE_ID").val("");
							$("#HEMU_RES_ID").val("");
							$("#HEMU_SALE_ACTIVE_FEE").val("");
						}
					}
					else
					{
						checkbox.checked = true;
					}
				}
			},
			
			allPartDisplayShow : function() {
				$("#topSetBoxPart").css('display', '');
				$("#HeMuPart").css('display', '');
				
				$("#topSetBoxInfoCheckBox").attr("checked",true);
				$("#HeMuCheckBox").attr("checked",true);
				
				var wideProductType = $("#WIDE_PRODUCT_TYPE").val();

				// 移动FTTH和铁通FTTH需要申请光猫
				if ('3' == wideProductType || '5' == wideProductType)
				{
					$("#IMSPart").css('display', '');
					$("#IMSCheckBox").attr("checked",true);
					
					//勾选IMS固话业务显示用户信息
					$("#IMSCustInfoPart").css("display", "");
					if(document.getElementById("CUST_NAME").innerHTML){
						document.getElementById("CUST_NAME").id="CUST_NAME_VIEW";
						document.getElementById("PSPT_ID").id="PSPT_ID_VIEW";
						document.getElementById("PSPT_TYPE_CODE").id="PSPT_TYPE_CODE_VIEW";
						document.getElementById("SEX").id="SEX_VIEW";
						document.getElementById("PSPT_END_DATE").id="PSPT_END_DATE_VIEW";
					}
					$("#CUST_NAME").val("");
					$("#SEX").val("");
					$("#BIRTHDAY").val("");
					$("#PSPT_ID").val("");
					$("#PSPT_END_DATE").val("");
					$("#PSPT_ADDR").val("");
					$("#AGENT_CUST_NAME").val("");
					$("#AGENT_PSPT_ID").val("");
					$("#AGENT_PSPT_ADDR").val("");
				}
				else
				{
					alert("只有FTTH宽带才能办理IMS固话业务！");
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
				$.ajax.submit(this, 'loadChildInfo', param, 'wideMode,productType,saleActivePart,saleActivePartAttr,topSetBoxProductPart,hiddenPart,topSetBoxSaleActivePart,modemPart,IMSPart,HeMuPart',
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
							
							$("#FIX_NUMBER").attr("disabled",false);
							$("#CHECK_BTN").attr("disabled",false); 
							
							$.mergewideusercreate.changeWideProductType();
							
						}, function(error_code, error_info)
						{
							$.endPageLoading();
							$.MessageBox.error(error_code, error_info);
						});
				
			},
			/**
			 * 选择IMS固话产品类型
			 */
			changeIMSProductType :function()
			{
				$.beginPageLoading("固话产品信息查询...");
				$.ajax.submit('', 'getIMSProductByType', "&IMS_PRODUCT_TYPE_CODE="+$("#IMS_PRODUCT_TYPE_CODE").val(), 'IMSProductListPart', function(rtnData) {
					$.endPageLoading();
				}, function(error_code, error_info,detail) {
					$.endPageLoading();
					$.MessageBox.error(error_code, error_info);
				}); 
			},
			
			/**
			 * IMS固话号码校验
			 */
			checkFixPhone :function()
			{
				var wideProductType = $("#WIDE_PRODUCT_TYPE").val();
				
				if (null == wideProductType || "" == wideProductType)
				{
					alert("请先查询选择宽带标准地址，只有FTTH宽带类型才能办理IMS固话业务!");
					return false;
				}
				
				if ("3" != wideProductType && "5" != wideProductType)
				{
					alert("只有FTTH宽带类型才能办理IMS固话业务!");
					return false;
				}
				
				if(!$.validate.verifyAll("FixPhonePart"))
				{
					return false;
				}
				
				
				var oldFixNumber = $("#OLD_FIX_NUMBER").val();
				var fixNumber=$("#FIX_NUMBER").val();
				var cityCode=$("#AREA_CODE").val();
				
				if (null != oldFixNumber && '' != oldFixNumber)
				{
					if (oldFixNumber == fixNumber)
					{
						alert('该号码已经校验通过，不需要再次校验!');
						return false
					}
				}
				
				$.beginPageLoading("校验固话号码...");
				$.ajax.submit('', 'checkFixPhoneNum', "&FIX_NUMBER="+fixNumber+"&CITYCODE_RSRVSTR4="+cityCode, '', function(rtnData) {
					$.endPageLoading();
					if(rtnData!=null&&rtnData.length > 0){
						if(rtnData.get("RESULT_CODE")=="1"){
							$("#OLD_FIX_NUMBER").val(fixNumber);
							alert(rtnData.get("RESULT_INFO"));
							$.cssubmit.disabledSubmitBtn(false,"submitButton");
						}else{
							alert(rtnData.get("RESULT_INFO"));
							$("#FIX_NUMBER").val("");
							$("#OLD_FIX_NUMBER").val("");
						} 
					}
				}, function(error_code, error_info,detail) {
					$.endPageLoading();
					$.MessageBox.error(error_code, error_info);
					$("#FIX_NUMBER").val("");
					$("#OLD_FIX_NUMBER").val("");
				}); 

			},
			
			/**
			 * 选择IMS固话产品后查询获取IMS固话营销活动
			 */
			changeIMSProduct :function()
			{
				var imsProductId = $("#IMS_PRODUCT_ID").val();
				var param = "&IMS_PRODUCT_ID="+imsProductId;

				$.beginPageLoading("IMS固话营销活动查询中......");
				$.ajax.submit(null, 'getImsSaleActive', param, 'IMSSaleActivePart',
						function(data) {
							
							IMS_SALE_ACTIVE_LIST = data;
							
							$("#IMS_SALE_ACTIVE_PRODUCT_ID").val('');
							$("#IMS_SALE_ACTIVE_ID").val('');
							
							$.endPageLoading();
						}, function(error_code, error_info, derror) {
							$.endPageLoading();
							showDetailErrorInfo(error_code, error_info, derror);
						});
						
				//每次重新选择固话产品，都将营销活动的预存置为空
				$("#IMS_SALE_ACTIVE_FEE").val('');
			},
			
			changeIMSSaleActive :function ()
			{

				var imsSaleActiveId = $("#IMS_SALE_ACTIVE_ID").val();
				
				var imsSaleActiveProductId = '';//IMS营销方案ID
				
				//如果选中的营销活动有值
				if (null != imsSaleActiveId && '' != imsSaleActiveId)
				{
					if (null != IMS_SALE_ACTIVE_LIST)
					{
						for (var i = 0; i < IMS_SALE_ACTIVE_LIST.length; i++)
						{
							if (imsSaleActiveId == IMS_SALE_ACTIVE_LIST[i].get('PARA_CODE5'))
							{
								imsSaleActiveProductId = IMS_SALE_ACTIVE_LIST[i].get("PARA_CODE4");
							}
						}
					}

					var eparchyCode = $.auth.getAuthData().get("USER_INFO").get("EPARCHY_CODE");
					var inparam = "&ROUTE_EPARCHY_CODE=" + eparchyCode 
								+ "&SERIAL_NUMBER=" + $("#AUTH_SERIAL_NUMBER").val()
								+ "&PRODUCT_ID=" + imsSaleActiveProductId
								+ "&PACKAGE_ID=" + imsSaleActiveId;
								
					$.beginPageLoading("IMS营销活动校验中。。。");

					$.ajax.submit(null, 'checkImsSaleActive', inparam, null,
									function(data) 
									{
										$.endPageLoading();
										$("#IMS_SALE_ACTIVE_PRODUCT_ID").val(imsSaleActiveProductId);
										
										//营销活动费用需要重新取产品模型配置
										$.familywideusercreate.checkIMSSaleActiveFee(imsSaleActiveProductId,imsSaleActiveId);
										
									}, 
									function(error_code, error_info) 
									{
										$("#IMS_SALE_ACTIVE_PRODUCT_ID").val('');
										$("#IMS_SALE_ACTIVE_ID").val('');
										$("#IMS_SALE_ACTIVE_FEE").val('');//如果不选营销活动，则将活动预存值置为空
											
										$.endPageLoading();
										$.MessageBox.error(error_code, error_info);
									}
								);
				}
				else
				{
					$("#IMS_SALE_ACTIVE_PRODUCT_ID").val('');
					$("#IMS_SALE_ACTIVE_ID").val('');
					$("#IMS_SALE_ACTIVE_FEE").val('');//如果不选营销活动，则将活动预存值置为空
				}
			},
			
			checkIMSSaleActiveFee :function(productId,packageId)
			{
				$.beginPageLoading("IMS固话营销活动费用校验中。。。");
				var serialNumber = $("#AUTH_SERIAL_NUMBER").val();
				var param = "&SERIAL_NUMBER="+ serialNumber + "&PRODUCT_ID=" + productId + "&PACKAGE_ID=" + packageId;
							
				$.ajax.submit(null, 'queryCheckSaleActiveFee', param, null,
								function(data) 
								{
									$.endPageLoading();
									var fee = data.get("SALE_ACTIVE_FEE");
									$("#IMS_SALE_ACTIVE_FEE").val(fee);//很重要，将活动下的元素费用传出
								}, function(error_code, error_info) 
								{
									$("#IMS_SALE_ACTIVE_PRODUCT_ID").val('');
									$("#IMS_SALE_ACTIVE_ID").val('');
									$("#IMS_SALE_ACTIVE_FEE").val('');
											
									$.endPageLoading();
									$.MessageBox.error(error_code, error_info);
								}
							);
			},
			
			/**
			 * 和目终端查询校验
			 */
			checkHeMuTerminal :function()
			{
				var heMuSaleActiveId = $("#HEMU_SALE_ACTIVE_ID").val();
				var heMuResId = $("#HEMU_RES_ID").val();
				var oldHeMuResId = $("#OLD_HEMU_RES_ID").val();
				
				if (null == heMuSaleActiveId || '' == heMuSaleActiveId)
				{
					alert("请先选择和目营销活动，再进行和目终端校验!");
					return false;
				}
				
				if (null == heMuResId || "5" == heMuResId)
				{
					alert("请输入和目终端编码!");
					return false;
				}
				
				if (null != oldHeMuResId && '' != oldHeMuResId)
				{
					if (oldHeMuResId == heMuResId)
					{
						alert('该终端编码已校验通过，不需要再次校验!');
						return false
					}
				}
				
				$.beginPageLoading("校验和目终端...");
				$.ajax.submit('', 'checkHeMuTerminal', "&HEMU_SALE_ACTIVE_ID="+heMuSaleActiveId
													  +"&HEMU_RES_ID="+heMuResId
													  +"&HEMU_SALE_ACTIVE_PRODUCT_ID="+$("#HEMU_SALE_ACTIVE_PRODUCT_ID").val()
													  +"&SERIAL_NUMBER="+$("#AUTH_SERIAL_NUMBER").val(), '', function(rtnData) {
					$.endPageLoading();
					alert("和目终端编码校验通过！");
					$("#OLD_HEMU_RES_ID").val(heMuResId);
				}, function(error_code, error_info,detail) {
					$.endPageLoading();
					$.MessageBox.error(error_code, error_info);
					$("#HEMU_RES_ID").val("");
					$("#OLD_HEMU_RES_ID").val("");
				}); 
			},
			
			changeHeMuSaleActive : function()
			{
				var heMuSaleActiveId = $("#HEMU_SALE_ACTIVE_ID").val();
				
				if (null != heMuSaleActiveId && '' != heMuSaleActiveId)
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
					
					
					var eparchyCode = $.auth.getAuthData().get("USER_INFO").get("EPARCHY_CODE");
					
					var inparam = "&TRADE_TYPE_CODE="+ $("#TRADE_TYPE_CODE").val() + "&ROUTE_EPARCHY_CODE="
							+ eparchyCode + "&WIDE_USER_SELECTED_SERVICEIDS="+serviceIds
							+"&SERIAL_NUMBER="+$("#AUTH_SERIAL_NUMBER").val()					
							+"&HEMU_SALE_ACTIVE_ID="+heMuSaleActiveId;
					 
					
					$.beginPageLoading("和目活动校验中。。。"); 
					$.ajax.submit(null, 'checkHeMuSaleActive', inparam, null,
							function(data) 
							{
								$.endPageLoading();
								$("#HEMU_SALE_ACTIVE_PRODUCT_ID").val(data.get('PRODUCT_ID'));
								$.familywideusercreate.checkHeMuSaleActiveFee(data.get('PRODUCT_ID'),data.get('PACKAGE_ID'));
							}, function(error_code, error_info) 
							{
								$.endPageLoading();
								$.MessageBox.error(error_code, error_info);
							});
				}
			},
			//查询和目营销活动预存费用
			checkHeMuSaleActiveFee:function(productId,packageId)
			{
				$.beginPageLoading("和目营销活动费用校验中。。。");
				var serialNumber = $("#AUTH_SERIAL_NUMBER").val();
				var param = "&SERIAL_NUMBER="+ serialNumber
						+ "&PRODUCT_ID=" + productId + "&PACKAGE_ID=" + packageId;
				
				$.ajax.submit(null, 'queryCheckSaleActiveFee', param, null,
					function(data) 
					{
						$.endPageLoading();
						var fee = data.get("SALE_ACTIVE_FEE");
						$("#HEMU_SALE_ACTIVE_FEE").val(fee);
					}, function(error_code, error_info) 
					{
						$("#HEMU_SALE_ACTIVE_ID").val('');
						$("#HEMU_SALE_ACTIVE_FEE").val('');
								
						$.endPageLoading();
						$.MessageBox.error(error_code, error_info);
					}
				);
			},
			onSubmit : function() 
			{
				var serialNum = $("#AUTH_SERIAL_NUMBER").val();
				
				var wideProductType = $("#WIDE_PRODUCT_TYPE").val();
				var isBusinessWide = $("#IS_BUSINESS_WIDE").val();
				// 移动FTTH和铁通FTTH需要申请光猫
				if (('3' == wideProductType || '5' == wideProductType) && 'Y' != isBusinessWide)
				{
					var modemStyle = $("#SALE_ACTIVE_ID2").val();
					
					if (null == modemStyle || '' == modemStyle)
					{
						MessageBox.alert("告警提示","FTTH宽带必须办理调试费活动！");
						
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
				
				//如果选择了魔百和业务则需要校验必填项
				if ($("#topSetBoxInfoCheckBox").attr("checked"))
				{
					if (!verifyAll('topSetBoxProductPart'))
					{
						return false;
					}
				}
				
				//如果选择了固话业务则需要校验必填项
				if ($("#IMSCheckBox").attr("checked"))
				{
					//REQ201810190032++和家固话开户界面增加实名制校验—BOSS侧   by mqx
					if("ERROR" == $("#PIC_ID").val())
					{
						alert("人像比对信息错误，请重新拍摄！");
				        return false;
					}
					// 人像比对
				    var agentPicIdObj = $("#AGENT_PIC_ID");
				    if (agentPicIdObj.val() === "") {
				        agentPicIdObj.val("AGENT_PIC_ID_value");
				    }
				    
				    if(!$.validate.verifyAll('CustInfoFieldPart'))
				    {
					   return false;
				    }
				    
				    var birthdayObj = $("#BIRTHDAY");
				    if (birthdayObj.val() === "") {
				        birthdayObj.val("1900-01-01");
				    }
				    /**
				     * 人像比对
				     * AGENT_PIC_ID_value这是界面默认的值
				     * 当没有对经办人人像摄像时，就把AGENT_PIC_ID置空
				     */
				    if (agentPicIdObj.val() === "AGENT_PIC_ID_value") {
				        agentPicIdObj.val("");
				    }
					
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

				    if (cmpTag === "0") {
				        var picId = $("#PIC_ID").val();
				        if (picId != null && picId === "ERROR") { // 客户摄像失败
				            MessageBox.error("告警提示", "客户" + $("#PIC_STREAM").val());
				            return false;
				        }

				        var psptTypeCode = $("#PSPT_TYPE_CODE").val();
				        var agentPicId = agentPicIdObj.val();
				        var agentTypeCode = $("#AGENT_PSPT_TYPE_CODE").val();

				        if ((psptTypeCode === "0" || psptTypeCode === "1")
				            && picId === "") {
				            /**
				             * REQ201705270006_关于人像比对业务优化需求
				             * 个人开户：用户个人身份证证件开户，判断户主或者经办人人像比对通过即可。
				             * @author zhuoyingzhi
				             * @date 20170620
				             */
				            var custName = $("#AGENT_CUST_NAME").val();      // 经办人名称
				            var psptId = $("#AGENT_PSPT_ID").val();          // 经办人证件号码
				            var agentPsptAddr = $("#AGENT_PSPT_ADDR").val(); // 经办人证件地址

				            if (agentTypeCode === "" && custName === "" && psptId === ""
				                    && agentPsptAddr === "") {
				                MessageBox.error("告警提示", "请进行客户或经办人摄像！");
				                return false;
				            }
				            if ((agentTypeCode === "0" || agentTypeCode === "1")
				                    && agentPicId === "") {
				                MessageBox.error("告警提示", "请进行客户或经办人摄像！");
				                return false;
				            }
				        }

				        if (agentPicId != null && agentPicId === "ERROR") { // 经办人摄像失败
				            MessageBox.error("告警提示", "经办人" + $("#AGENT_PIC_STREAM").val());
				            return false;
				        }

				        if ((agentTypeCode === "0" || agentTypeCode === "1")
				                && agentPicId === "") { // 经办人未摄像
				            MessageBox.error("告警提示", "请进行经办人摄像！");
				            return false;
				        }
				        
				        if (psptTypeCode === "2" && picId === "") { // 未进行客户摄像
				            var custPsptId = $("#PSPT_ID").val();   // 客户证件号码
				            if (custPsptId !== ""
				                    && checkCustAge(custPsptId, psptTypeCode)) {
				                // 11岁（含）至120岁（不含）之间的用户必须通过验证才可以办理（同身份证一致）
				                MessageBox.error("告警提示", "请进行客户摄像!");
				                return false;
				            }
				        }
				        
				        var param = "&PIC_ID=" + picId + "&AGENT_PIC_ID=" + agentPicId;
				        $.cssubmit.addParam(param);
				    }
					//REQ201810190032++和家固话开户界面增加实名制校验—BOSS侧   by mqx end
					
					
					if (!verifyAll('IMSPart'))
					{
						return false;
					}
					
					var oldFixNumber = $("#OLD_FIX_NUMBER").val();
					var fixNumber=$("#FIX_NUMBER").val();
					
					if (null == oldFixNumber || '' == oldFixNumber)
					{
						alert("固话号码没有校验通过，无法提交业务！");
						return false;
					}
					
					if (oldFixNumber != fixNumber)
					{
						alert("当前业务提交的固话号码与校验通过的固话号码不一致，请对当前固话号码重新进行校验！");
						return false;
					}
				}
				
				//如果选择了和目业务则需要校验必填项
				if ($("#HeMuCheckBox").attr("checked"))
				{
					//只有默认请选择选择，没有可办理的和目营销活动
					if ($("#HEMU_SALE_ACTIVE_ID")[0].length <= 1)
					{
						alert("该宽带产品下没有可办理的和目营销活动，请取消勾选和目业务！");
						return false;
					}
					
					if (!verifyAll('HeMuPart'))
					{
						return false;
					}
					
					var heMuResId = $("#HEMU_RES_ID").val();
					var oldHeMuResId = $("#OLD_HEMU_RES_ID").val();
					
					if (null == oldHeMuResId || '' == oldHeMuResId)
					{
						alert("和目终端没有校验通过，无法提交业务！");
						return false;
					}
					
					if (oldHeMuResId != heMuResId)
					{
						alert("当前业务提交的和目终端编码与校验通过和目终端编码不一致，请对当前目终端重新进行校验！");
						return false;
					}
				}
				
				//选择办理高价值小区专项营销活动
				if($("#IS_HIGHTACTIVE").attr("checked"))
				{
					if($("#MODEM_DEPOSIT").val()<100){
						$("#IS_HIGHTACTIVE").attr("checked",false);
						alert("用户的光猫押金不足100元，无法办理高价值小区专项营销活动！");
						return false;
					}
					
				}
				
				var modemDeposit = $("#MODEM_DEPOSIT").val();
				var topSetBoxDeposit = $("#TOP_SET_BOX_DEPOSIT").val();
				var saleActiveFee = $("#SALE_ACTIVE_FEE").val();
				var topSetBoxSaleActiveFee = $("#TOP_SET_BOX_SALE_ACTIVE_FEE").val();
				var imsSaleActiveFee = $("#IMS_SALE_ACTIVE_FEE").val();
				var heMuSaleActiveFee = $("#HEMU_SALE_ACTIVE_FEE").val();
				//BUS201907310012关于开发家庭终端调测费的需求
				var saleActiveFee2 = $("#SALE_ACTIVE_FEE2").val();
				var topSetBoxSaleActiveFee2 = $("#TOP_SET_BOX_SALE_ACTIVE_FEE2").val();
				//BUS201907310012关于开发家庭终端调测费的需求
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
				
				if (null == imsSaleActiveFee || '' == imsSaleActiveFee)
				{
					imsSaleActiveFee = '0';
				}
				
				if (null == heMuSaleActiveFee || '' == heMuSaleActiveFee)
				{
					heMuSaleActiveFee = '0';
				}
				if (null == saleActiveFee2 || '' == saleActiveFee2)
				{
					saleActiveFee2 = '0';
				}
				
				if (null == topSetBoxSaleActiveFee2 || '' == topSetBoxSaleActiveFee2)
				{
					topSetBoxSaleActiveFee2 = '0';
				}
				
				//BUG20190531145700魔百和押金问题优化
/*				if (null != $("#TOP_SET_BOX_PRODUCT_ID").val() && "" != $("#TOP_SET_BOX_PRODUCT_ID").val())
				{
					var topsetBoxActiveId = $("#TOP_SET_BOX_SALE_ACTIVE_ID").val();
					if( (null == topsetBoxActiveId || '' == topsetBoxActiveId) && '0' == topSetBoxDeposit)
					{
						MessageBox.alert("告警提示","未选择魔百和营销活动，魔百和押金不能为0！");
						return false;
					}
				}*/
				//BUG20190531145700魔百和押金问题优化
				
				var feeFlag = true ;
				var totalFee = parseFloat(saleActiveFee2)/100 + parseFloat(topSetBoxSaleActiveFee2)/100 + parseFloat(saleActiveFee)/100 + parseFloat(topSetBoxSaleActiveFee)/100 + parseFloat(imsSaleActiveFee)/100 + parseFloat(heMuSaleActiveFee)/100;
				
				if (totalFee > 0)
				{
					feeFlag = false ;
					var tips = "您总共需要从现金存折中转出："+totalFee+"元,其中宽带活动预存："+parseFloat(saleActiveFee)/100
					            +"元，魔百和营销活动预存："+parseFloat(topSetBoxSaleActiveFee)/100
								+"元，IMS固话营销活动预存："+parseFloat(imsSaleActiveFee)/100
								+"元，和目营销活动预存："+parseFloat(heMuSaleActiveFee)/100
								+"元，宽带调测费："+parseFloat(saleActiveFee2)/100+"元，魔百和调测费："+parseFloat(topSetBoxSaleActiveFee2)/100
								+"元，请确认您的余额是否足够?";
								
					//宽带付费方式   A：先装后付，P：立即付费
					var widePayMode = $("#WIDENET_PAY_MODE").val();
					
					if ("A" == widePayMode)
					{
						tips = "本次宽带开户总费用为："+totalFee+"元,其中宽带活动预存："+parseFloat(saleActiveFee)/100+"元，魔百和营销活动预存："
						+parseFloat(topSetBoxSaleActiveFee)/100
						+"元，IMS固话营销活动预存："+parseFloat(imsSaleActiveFee)/100
						+"元，和目营销活动预存："+parseFloat(heMuSaleActiveFee)/100
						+"元，宽带调测费："+parseFloat(saleActiveFee2)/100+"元，魔百和调测费："+parseFloat(topSetBoxSaleActiveFee2)/100
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
							feeFlag = $.familywideusercreate.checkFeeBeforeSubmit();
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
					param += "&IS_HIGHTACTIVE=" + $("#DEVICE_ID_HIGHT").val();
					$.cssubmit.addParam(param);
				} 
				else
				{
					alert('您未选择开户产品，不能提交！');
					return false;
				}
				
				return true;
			},
			//提交前费用校验
			checkFeeBeforeSubmit:function()
			{
				var modemDeposit = $("#MODEM_DEPOSIT").val();
				var topSetBoxDeposit = $("#TOP_SET_BOX_DEPOSIT").val();
				var saleActiveFee = $("#SALE_ACTIVE_FEE").val();
				var topSetBoxSaleActiveFee = $("#TOP_SET_BOX_SALE_ACTIVE_FEE").val();
				var imsSaleActiveFee = $("#IMS_SALE_ACTIVE_FEE").val();
				var heMuSaleActiveFee = $("#HEMU_SALE_ACTIVE_FEE").val();
				var saleActiveFee2 = $("#SALE_ACTIVE_FEE2").val();
				var topSetBoxSaleActiveFee2 = $("#TOP_SET_BOX_SALE_ACTIVE_FEE2").val();
				
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
				
				if (null == heMuSaleActiveFee || '' == heMuSaleActiveFee)
				{
					heMuSaleActiveFee = '0';
				}
				
				if (null == imsSaleActiveFee || '' == imsSaleActiveFee)
				{
					imsSaleActiveFee = '0';
				}
				if (null == saleActiveFee2 || '' == saleActiveFee2)
				{
					saleActiveFee2 = '0';
				}
				
				if (null == topSetBoxSaleActiveFee2 || '' == topSetBoxSaleActiveFee2)
				{
					topSetBoxSaleActiveFee2 = '0';
				}
				
				var param = "&MODEM_DEPOSIT=" + modemDeposit + "&TOPSETBOX_DEPOSIT=" + topSetBoxDeposit
							+ "&SALE_ACTIVE_FEE=" + saleActiveFee + "&TOPSETBOX_SALE_ACTIVE_FEE=" + topSetBoxSaleActiveFee
							+ "&SERIAL_NUMBER="+$("#AUTH_SERIAL_NUMBER").val() + "&HEMU_SALE_ACTIVE_FEE="+heMuSaleActiveFee
							+ "&SALE_ACTIVE_FEE2=" + saleActiveFee2 + "&TOPSETBOX_SALE_ACTIVE_FEE2=" + topSetBoxSaleActiveFee2
							+ "&IMS_SALE_ACTIVE_FEE="+imsSaleActiveFee;
				
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
			}
		}
	}
})(Wade);


