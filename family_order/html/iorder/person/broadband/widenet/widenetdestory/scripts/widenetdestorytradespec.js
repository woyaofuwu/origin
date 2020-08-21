//用戶认证结束之后执行的js方法
function refreshPartAtferAuth(data)
{
	var param = "&ROUTE_EPARCHY_CODE="+data.get("USER_INFO").get("EPARCHY_CODE")+"&USER_ID="+data.get("USER_INFO").get("USER_ID");
	param += "&SERIAL_NUMBER="+data.get("USER_INFO").get("SERIAL_NUMBER");
	
    $.beginPageLoading("宽带资料查询。。。");
	 $.ajax.submit('AuthPart,hiddenPart', 'loadChildInfo', param, 'wideInfoPart,BusiInfoPart,activeInfoPart,hiddenPart', function(data){
		 	var warmType=data.get("WARM_TYPE");
		 	if(warmType&&warmType=="1"){
		 		MessageBox.alert("告警提示", "客户已办理魔百和业务，不能办理拆机业务。");
		 	}
		 	var messageInfo01 ="该用户已办理了营销活动";
		 	var messageInfo02 ="，继续办理将立即终止活动";
		 	var messageInfo03 ="";
		 	var messageInfoEnd ="。";
		 	var activeFlag=false;
		 	var activeflag=data.get("ACTIVE_FLAG");
		 	if (activeflag&&activeflag=="1"){
                activeFlag=true;
		 		var activename=data.get("ACTIVE_NAME");
                messageInfo01+="【 "+activename+" 】";
            }
             //如果有非宽带类营销活动,提示
            var otherActive = $("#OTHER_ACTIVE_FEE_TAG").val();
			if(otherActive&&otherActive=='1'){
                activeFlag=true;
				var otherActiveName = $("#OTHER_ACTIVE_NAME").val();
				var otherActiveFee = $("#OTHER_ACTIVE_FEE").val();

                messageInfo01+= "【 "+otherActiveName+" 】" ;
				if(otherActiveFee>0){
                    messageInfo03+="，并缴纳【 "+otherActiveName+" 】活动违约金【"+otherActiveFee+"】元";
				}
			}
			if(activeFlag){
                MessageBox.alert("告警提示", messageInfo01+messageInfo02+messageInfo03+messageInfoEnd);
			}
             //add by zhangxing3 for 候鸟月、季、半年套餐（海南）
             var installFeeTag=data.get("INSTALL_FEE_TAG","0");
             if(installFeeTag&&installFeeTag=="1"){
            	var feeData = $.DataMap();
 				feeData.put("MODE", "0");
 				feeData.put("CODE", "512");
 				feeData.put("FEE",  "12000");
 				feeData.put("PAY",  "12000");		
 				feeData.put("TRADE_TYPE_CODE","615");				
 				$.feeMgr.insertFee(feeData);
             }
             //add by zhangxing3 for 候鸟月、季、半年套餐（海南）
		 	$.endPageLoading();
		},
		function(error_code,error_info){
			$.endPageLoading();
			MessageBox.alert("提示",error_info);
	    });
}
//点击展示，隐藏客户信息按钮
function displayUserInfo(btn,o)
{
	var button = $(btn);
    var div = $('#'+o);
    if (div.css('display') != "none")
    {
		  div.css('display', 'none');
		  button.empty();
		  button.html('<span class="e_ico-unfold"></span>展示客户信息'); 
    }else {
       div.css('display', '');
       button.empty();
	   button.html('<span class="e_ico-fold"></span>隐藏客户信息'); 
    }
}
//是否租赁光猫判断，只有租赁光猫才能选择“是”，退订光猫
function modem_is_return(id)
{
	var ret = $(id).val();
	var ret_mode=$("#MODEM_MODE").val();
	if (ret=="1")
	{
		if (ret_mode!="0") 
		{
			MessageBox.alert("告警提示","不是租赁的光猫，不用退订光猫，请选择“否”！");
			$(id).val("0");
		}
	}
}



//业务提交----立即拆机
/*如果用户有积分，则提示先进行兑换*/
function submitBeforeCheck() {

    /* 校验所有的输入框 */
    if (!$.validate.verifyAll("destroyReasonPart")) {
        return false;
    }

    if (!checkDestroyReason()) {
        return false;
    }

    //add by zhangxing3 for 候鸟月、季、半年套餐（海南）
    var installFeeTag = $("#INSTALL_FEE_TAG").val();
    //alert("-------------installFeeTag-------------"+installFeeTag);
    if (installFeeTag == "1") {
        MessageBox.confirm("告警提示", "您办理过度假宽带活动，宽带开户未满两年，拆机需要缴纳120元宽带装机费。是否继续?", function (re) {
            if (re == "ok") {
                //如果还有固话，判断是否也拆固话
                var IMSTag = $("#IMS_TAG").val();
                if (IMSTag == '1') {
                    var message = "您办理的IMS固话，宽带拆机时需同时拆除家庭固话，请确认要继续拆机吗?";
                    MessageBox.confirm("告警提示", message, function (re) {
                        if (re == "ok") {
                            if ($("#WIDE_TYPE_CODE").val() == "3" || $("#WIDE_TYPE_CODE").val() == "5") {
                                //退光猫选项必填校验
                                if ($("#MODEM_RETUAN").val() == "") {
                                    MessageBox.alert("提示", "请选择是否退光猫", "", null);
                                    return false;
                                }
                                if ($("#MODEM_CODE").text() == "" && $("#MODEM_MODE").val() == "0")//在租赁的模式下要求有串号
                                {
                                    if ($("#MODEM_RETUAN").val() == "1") {
                                        MessageBox.alert("告警提示", "光猫串号不能为空，或是数据不全！");
                                        return false;
                                    }
                                }

                                //如果客户选择了不退光猫，提示90内如果不退光猫，押金将沉淀
                                if ($("#MODEM_RETUAN").val() == "0") {
                                    if ($("#MODEM_MODE").val() == "0") {
                                        //光猫模式为租赁
                                        MessageBox.confirm("告警提示", "您当前有光猫须退还，您本次选择暂不退还，请90天内到移动营业厅退还光猫，如不退还押金将自动沉淀。是否继续?", function (re) {
                                            if (re == "ok") {
                                                var hiddenState = $("#HIDDEN_STATE").val();
                                                if (hiddenState != "")//已预约的
                                                {
                                                    MessageBox.alert("告警提示", "已办理预约拆机！不能再次办理拆机业务！");
                                                    return false;
                                                }

                                                MessageBox.confirm("告警提示", "提交成功后，您的宽带将停止使用，请确认要继续拆机吗?", function (re) {
                                                    if (re == "ok") {
                                                        var param = "&ROUTE_EPARCHY_CODE=" + $.auth.getAuthData().get("USER_INFO").get("EPARCHY_CODE");
                                                        $.cssubmit.addParam(param);
                                                        $.cssubmit.submitTrade();
                                                    }
                                                });
                                            }
                                        });
                                    } else {
                                        var hiddenState = $("#HIDDEN_STATE").val();
                                        if (hiddenState != "")//已预约的
                                        {
                                            MessageBox.alert("告警提示", "已办理预约拆机！不能再次办理拆机业务！");
                                            return false;
                                        }

                                        MessageBox.confirm("告警提示", "提交成功后，您的宽带将停止使用，请确认要继续拆机吗?", function (re) {
                                            if (re == "ok") {
                                                var param = "&ROUTE_EPARCHY_CODE=" + $.auth.getAuthData().get("USER_INFO").get("EPARCHY_CODE");
                                                $.cssubmit.addParam(param);
                                                $.cssubmit.submitTrade();
                                            }
                                        });
                                    }
                                } else {
                                    var hiddenState = $("#HIDDEN_STATE").val();
                                    if (hiddenState != "")//已预约的
                                    {
                                        MessageBox.alert("告警提示", "已办理预约拆机！不能再次办理拆机业务！");
                                        return false;
                                    }

                                    MessageBox.confirm("告警提示", "提交成功后，您的宽带将停止使用，请确认要继续拆机吗?", function (re) {
                                        if (re == "ok") {
                                            var param = "&ROUTE_EPARCHY_CODE=" + $.auth.getAuthData().get("USER_INFO").get("EPARCHY_CODE");
                                            $.cssubmit.addParam(param);
                                            $.cssubmit.submitTrade();
                                        }
                                    });
                                }
                            } else {
                                var hiddenState = $("#HIDDEN_STATE").val();
                                if (hiddenState != "")//已预约的
                                {
                                    MessageBox.alert("告警提示", "已办理预约拆机！不能再次办理拆机业务！");
                                    return false;
                                }

                                MessageBox.confirm("告警提示", "提交成功后，您的宽带将停止使用，请确认要继续拆机吗?", function (re) {
                                    if (re == "ok") {
                                        var param = "&ROUTE_EPARCHY_CODE=" + $.auth.getAuthData().get("USER_INFO").get("EPARCHY_CODE");
                                        $.cssubmit.addParam(param);
                                        $.cssubmit.submitTrade();
                                    }
                                });
                            }
                        }
                    });
                } else {
                    if ($("#WIDE_TYPE_CODE").val() == "3" || $("#WIDE_TYPE_CODE").val() == "5") {
                        //退光猫选项必填校验
                        if ($("#MODEM_RETUAN").val() == "") {
                            MessageBox.alert("提示", "请选择是否退光猫", "", null);
                            return false;
                        }
                        if ($("#MODEM_CODE").text() == "" && $("#MODEM_MODE").val() == "0")//在租赁的模式下要求有串号
                        {
                            if ($("#MODEM_RETUAN").val() == "1") {
                                MessageBox.alert("告警提示", "光猫串号不能为空，或是数据不全！");
                                return false;
                            }
                        }

                        //如果客户选择了不退光猫，提示90内如果不退光猫，押金将沉淀
                        if ($("#MODEM_RETUAN").val() == "0") {
                            if ($("#MODEM_MODE").val() == "0") {
                                //光猫模式为租赁
                                MessageBox.confirm("告警提示", "您当前有光猫须退还，您本次选择暂不退还，请90天内到移动营业厅退还光猫，如不退还押金将自动沉淀。是否继续?", function (re) {
                                    if (re == "ok") {
                                        var hiddenState = $("#HIDDEN_STATE").val();
                                        if (hiddenState != "")//已预约的
                                        {
                                            MessageBox.alert("告警提示", "已办理预约拆机！不能再次办理拆机业务！");
                                            return false;
                                        }

                                        MessageBox.confirm("告警提示", "提交成功后，您的宽带将停止使用，请确认要继续拆机吗?", function (re) {
                                            if (re == "ok") {
                                                var param = "&ROUTE_EPARCHY_CODE=" + $.auth.getAuthData().get("USER_INFO").get("EPARCHY_CODE");
                                                $.cssubmit.addParam(param);
                                                $.cssubmit.submitTrade();
                                            }
                                        });
                                    }
                                });
                            } else {
                                var hiddenState = $("#HIDDEN_STATE").val();
                                if (hiddenState != "")//已预约的
                                {
                                    MessageBox.alert("告警提示", "已办理预约拆机！不能再次办理拆机业务！");
                                    return false;
                                }

                                MessageBox.confirm("告警提示", "提交成功后，您的宽带将停止使用，请确认要继续拆机吗?", function (re) {
                                    if (re == "ok") {
                                        var param = "&ROUTE_EPARCHY_CODE=" + $.auth.getAuthData().get("USER_INFO").get("EPARCHY_CODE");
                                        $.cssubmit.addParam(param);
                                        $.cssubmit.submitTrade();
                                    }
                                });
                            }
                        } else {
                            var hiddenState = $("#HIDDEN_STATE").val();
                            if (hiddenState != "")//已预约的
                            {
                                MessageBox.alert("告警提示", "已办理预约拆机！不能再次办理拆机业务！");
                                return false;
                            }

                            MessageBox.confirm("告警提示", "提交成功后，您的宽带将停止使用，请确认要继续拆机吗?", function (re) {
                                if (re == "ok") {
                                    var param = "&ROUTE_EPARCHY_CODE=" + $.auth.getAuthData().get("USER_INFO").get("EPARCHY_CODE");
                                    $.cssubmit.addParam(param);
                                    $.cssubmit.submitTrade();
                                }
                            });
                        }
                    } else {
                        var hiddenState = $("#HIDDEN_STATE").val();
                        if (hiddenState != "")//已预约的
                        {
                            MessageBox.alert("告警提示", "已办理预约拆机！不能再次办理拆机业务！");
                            return false;
                        }

                        MessageBox.confirm("告警提示", "提交成功后，您的宽带将停止使用，请确认要继续拆机吗?", function (re) {
                            if (re == "ok") {
                                var param = "&ROUTE_EPARCHY_CODE=" + $.auth.getAuthData().get("USER_INFO").get("EPARCHY_CODE");
                                $.cssubmit.addParam(param);
                                $.cssubmit.submitTrade();
                            }
                        });
                    }
                }
            }
        });
    } else {
		//如果还有固话，判断是否也拆固话
		var IMSTag = $("#IMS_TAG").val();
		if (IMSTag == '1') {
			var message = "您办理的IMS固话，宽带拆机时需同时拆除家庭固话，请确认要继续拆机吗?";
			MessageBox.confirm("告警提示", message, function (re) {
				if (re == "ok") {
					if ($("#WIDE_TYPE_CODE").val() == "3" || $("#WIDE_TYPE_CODE").val() == "5") {
						//退光猫选项必填校验
						if ($("#MODEM_RETUAN").val() == "") {
							MessageBox.alert("提示", "请选择是否退光猫", "", null);
							return false;
						}
						if ($("#MODEM_CODE").text() == "" && $("#MODEM_MODE").val() == "0")//在租赁的模式下要求有串号
						{
							if ($("#MODEM_RETUAN").val() == "1") {
								MessageBox.alert("告警提示", "光猫串号不能为空，或是数据不全！");
								return false;
							}
						}

						//如果客户选择了不退光猫，提示90内如果不退光猫，押金将沉淀
						if ($("#MODEM_RETUAN").val() == "0") {
							if ($("#MODEM_MODE").val() == "0") {
								//光猫模式为租赁
								MessageBox.confirm("告警提示", "您当前有光猫须退还，您本次选择暂不退还，请90天内到移动营业厅退还光猫，如不退还押金将自动沉淀。是否继续?", function (re) {
									if (re == "ok") {
										var hiddenState = $("#HIDDEN_STATE").val();
										if (hiddenState != "")//已预约的
										{
											MessageBox.alert("告警提示", "已办理预约拆机！不能再次办理拆机业务！");
											return false;
										}

										MessageBox.confirm("告警提示", "提交成功后，您的宽带将停止使用，请确认要继续拆机吗?", function (re) {
											if (re == "ok") {
												var param = "&ROUTE_EPARCHY_CODE=" + $.auth.getAuthData().get("USER_INFO").get("EPARCHY_CODE");
												$.cssubmit.addParam(param);
												$.cssubmit.submitTrade();
											}
										});
									}
								});
							} else {
								var hiddenState = $("#HIDDEN_STATE").val();
								if (hiddenState != "")//已预约的
								{
									MessageBox.alert("告警提示", "已办理预约拆机！不能再次办理拆机业务！");
									return false;
								}

								MessageBox.confirm("告警提示", "提交成功后，您的宽带将停止使用，请确认要继续拆机吗?", function (re) {
									if (re == "ok") {
										var param = "&ROUTE_EPARCHY_CODE=" + $.auth.getAuthData().get("USER_INFO").get("EPARCHY_CODE");
										$.cssubmit.addParam(param);
										$.cssubmit.submitTrade();
									}
								});
							}
						} else {
							var hiddenState = $("#HIDDEN_STATE").val();
							if (hiddenState != "")//已预约的
							{
								MessageBox.alert("告警提示", "已办理预约拆机！不能再次办理拆机业务！");
								return false;
							}

							MessageBox.confirm("告警提示", "提交成功后，您的宽带将停止使用，请确认要继续拆机吗?", function (re) {
								if (re == "ok") {
									var param = "&ROUTE_EPARCHY_CODE=" + $.auth.getAuthData().get("USER_INFO").get("EPARCHY_CODE");
									$.cssubmit.addParam(param);
									$.cssubmit.submitTrade();
								}
							});
						}
					} else {
						var hiddenState = $("#HIDDEN_STATE").val();
						if (hiddenState != "")//已预约的
						{
							MessageBox.alert("告警提示", "已办理预约拆机！不能再次办理拆机业务！");
							return false;
						}

						MessageBox.confirm("告警提示", "提交成功后，您的宽带将停止使用，请确认要继续拆机吗?", function (re) {
							if (re == "ok") {
								var param = "&ROUTE_EPARCHY_CODE=" + $.auth.getAuthData().get("USER_INFO").get("EPARCHY_CODE");
								$.cssubmit.addParam(param);
								$.cssubmit.submitTrade();
							}
						});
					}
				}
			});
		} else {
			if ($("#WIDE_TYPE_CODE").val() == "3" || $("#WIDE_TYPE_CODE").val() == "5") {
				//退光猫选项必填校验
				if ($("#MODEM_RETUAN").val() == "") {
					MessageBox.alert("提示", "请选择是否退光猫", "", null);
					return false;
				}
				if ($("#MODEM_CODE").text() == "" && $("#MODEM_MODE").val() == "0")//在租赁的模式下要求有串号
				{
					if ($("#MODEM_RETUAN").val() == "1") {
						MessageBox.alert("告警提示", "光猫串号不能为空，或是数据不全！");
						return false;
					}
				}

				//如果客户选择了不退光猫，提示90内如果不退光猫，押金将沉淀
				if ($("#MODEM_RETUAN").val() == "0") {
					if ($("#MODEM_MODE").val() == "0") {
						//光猫模式为租赁
						MessageBox.confirm("告警提示", "您当前有光猫须退还，您本次选择暂不退还，请90天内到移动营业厅退还光猫，如不退还押金将自动沉淀。是否继续?", function (re) {
							if (re == "ok") {
								var hiddenState = $("#HIDDEN_STATE").val();
								if (hiddenState != "")//已预约的
								{
									MessageBox.alert("告警提示", "已办理预约拆机！不能再次办理拆机业务！");
									return false;
								}

								MessageBox.confirm("告警提示", "提交成功后，您的宽带将停止使用，请确认要继续拆机吗?", function (re) {
									if (re == "ok") {
										var param = "&ROUTE_EPARCHY_CODE=" + $.auth.getAuthData().get("USER_INFO").get("EPARCHY_CODE");
										$.cssubmit.addParam(param);
										$.cssubmit.submitTrade();
									}
								});
							}
						});
					} else {
						var hiddenState = $("#HIDDEN_STATE").val();
						if (hiddenState != "")//已预约的
						{
							MessageBox.alert("告警提示", "已办理预约拆机！不能再次办理拆机业务！");
							return false;
						}

						MessageBox.confirm("告警提示", "提交成功后，您的宽带将停止使用，请确认要继续拆机吗?", function (re) {
							if (re == "ok") {
								var param = "&ROUTE_EPARCHY_CODE=" + $.auth.getAuthData().get("USER_INFO").get("EPARCHY_CODE");
								$.cssubmit.addParam(param);
								$.cssubmit.submitTrade();
							}
						});
					}
				} else {
					var hiddenState = $("#HIDDEN_STATE").val();
					if (hiddenState != "")//已预约的
					{
						MessageBox.alert("告警提示", "已办理预约拆机！不能再次办理拆机业务！");
						return false;
					}

					MessageBox.confirm("告警提示", "提交成功后，您的宽带将停止使用，请确认要继续拆机吗?", function (re) {
						if (re == "ok") {
							var param = "&ROUTE_EPARCHY_CODE=" + $.auth.getAuthData().get("USER_INFO").get("EPARCHY_CODE");
							$.cssubmit.addParam(param);
							$.cssubmit.submitTrade();
						}
					});
				}
			} else {
				var hiddenState = $("#HIDDEN_STATE").val();
				if (hiddenState != "")//已预约的
				{
					MessageBox.alert("告警提示", "已办理预约拆机！不能再次办理拆机业务！");
					return false;
				}

				MessageBox.confirm("告警提示", "提交成功后，您的宽带将停止使用，请确认要继续拆机吗?", function (re) {
					if (re == "ok") {
						var param = "&ROUTE_EPARCHY_CODE=" + $.auth.getAuthData().get("USER_INFO").get("EPARCHY_CODE");
						$.cssubmit.addParam(param);
						$.cssubmit.submitTrade();
					}
				});
			}
		}
	}
}

function destoryReasonChange(obj)
{ 
	var o = $(obj);
	o.siblings().attr("class","link");
	o.attr("class","link checked");
	var reason = o.find("span:first-child").attr("value");
	$("#DESTORYREASON").val(reason);
	if("9"==reason){
		$("#reasonremark").show();
	}else{
		$("#reasonremark").hide();
	}
}

function reasonElseChange(obj){
	var reasonElse = obj.value;
	if(reasonElse.length>50){
		MessageBox.alert("提示","请简单输入销号原因备注，不超过50个汉字。");
		$("#REASONELSE").val("");
		$("#REASONELSE").focus(); 
	}
}

//校验销号原因是否选择
function checkDestroyReason(){
	var destroyReason = $("#DESTORYREASON").val();
	if(destroyReason == '')
	{
		MessageBox.alert("告警提示","销号原因未选择！");
		return false;
	}
	if("9"==destroyReason){
		if($("#REASONELSE").val() == ''){
			MessageBox.alert("告警提示","备注不能为空！");
			return false;
		}
	}
	return true;
}
