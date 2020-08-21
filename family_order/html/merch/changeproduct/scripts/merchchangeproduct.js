if (typeof (ChangeProduct) == "undefined") {
	window["ChangeProduct"] = function() {
	};
	var changeProduct = new ChangeProduct();
}
(function() {
	$.extend(ChangeProduct.prototype, {
		refreshPartAfterAuth : function(data) {
			var userProductId = data.get("USER_INFO").get("PRODUCT_ID");
			var userId = data.get("USER_INFO").get("USER_ID");
			var custId = data.get("CUST_INFO").get("CUST_ID");
			var eparchyCode = data.get("USER_INFO").get("EPARCHY_CODE");
			var param = "&USER_ID=" + data.get("USER_INFO").get("USER_ID") + "&SERIAL_NUMBER=" + data.get("USER_INFO").get("SERIAL_NUMBER")
					+ "&EPARCHY_CODE=" + eparchyCode + "&PRODUCT_ID=" + userProductId + "&CUST_ID=" + custId;

			if ((data.get("USER_INFO").get("ACCT_TAG") != "0") && (data.get("USER_INFO").get("USER_TYPE_CODE") != "B")) {
				MessageBox.alert("激活状态校验", "此号码未激活不能进行产品变更");
				return false;
			}

			var newProductId = $("#NEW_PRODUCT_ID").val();

			if (newProductId) {// 初始化过来
				param += "&NEW_PRODUCT_ID=" + newProductId;
			}
			//gh
			
			$("#USER_PRODUCT_ID").val(userProductId);
//			$("#SHAREMEAL_NUMBERS").val("");
//			$("#SHAREMEAL_FLAG").val(0);
//			$("#SHAREMEAL_LI").css("display", "none");
//			$("#SHAREMEAL_CHECK_BTN").attr("disabled", true);
			
			// 海南添加
            if (typeof changeProductExtend !== "undefined"
                    && changeProductExtend.afterSubmitSerialNumber) {
                changeProductExtend.afterSubmitSerialNumber();
            }

			$.beginPageLoading("加载中。。。");
			$.ajax.submit('', 'loadChildInfo', param, '', changeProduct.afterLoadData, function(error_code, error_info) {
				$.endPageLoading();
				alert(error_info);
			});
		},
		afterLoadData : function(ajaxData) {
			$.endPageLoading();
			
			var userProductId = $.auth.getAuthData().get("USER_INFO").get("PRODUCT_ID");
			var userId = $.auth.getAuthData().get("USER_INFO").get("USER_ID");
			var eparchyCode = $.auth.getAuthData().get("USER_INFO").get("EPARCHY_CODE");
			selectedElements.setEnv(userId, eparchyCode, "110", userProductId);

			userProduct.renderProduct(ajaxData.get("USER_PRODUCT"));
			$("#OLD_PRODUCT_START_DATE").val(ajaxData.get("USER_PRODUCT").get("START_DATE"));
			//IS_EFFECT_NOW代表是否可以立即订购，true：checkbox可用，false：不可用
			userProduct.disableEffectNow(ajaxData.get("IS_EFFECT_NOW") != "true");

			var newProduct = ajaxData.get("NEW_PRODUCT");
			var newProductId = newProduct.get("OFFER_CODE");
			var productName = newProduct.get("OFFER_NAME");
			var productDesc = newProduct.get("DESCRIPTION");
			var startDate = newProduct.get("START_DATE");
			var endDate = newProduct.get("END_DATE");
			$("#SYS_DATE").val(startDate);
			$("#BOOKING_DATE").val($("#SYS_DATE").val().substring(0, 10)); // 重置预约时间为系统时间
			$("#BOOKING_DATE").attr("min",$("#SYS_DATE").val());
			$("#NEW_PRODUCT_NAME").val(productName); //提交时，弹框需要取值
			
			userProduct.changeProduct(newProductId, productName, productDesc, startDate, endDate);
			userProduct.resetBtnStyle("none", "none");
			
//			$("#USER_EPARCHY_CODE").val(eparchyCode);
//			$("#NEW_PRODUCT_START_DATE").val("");
//            $("#OLD_PRODUCT_END_DATE").val("");
//            
//			var acctDayInfo = $.auth.getAuthData().get("ACCTDAY_INFO");
//            selectedElements.setAcctDayInfo(acctDayInfo.get("ACCT_DAY"),
//                    acctDayInfo.get("FIRST_DATE"),
//                    acctDayInfo.get("NEXT_ACCT_DAY"),
//                    acctDayInfo.get("NEXT_FIRST_DATE"));
//            
//			selectedElements.renderComponent("&USER_ID=" + userId, eparchyCode);
			
			selectedElements.setNewProduct(newProductId);
			selectedElements.renderComponent();
			
			// 海南添加
            if (typeof changeProductExtend !== "undefined"
                    && changeProductExtend.afterLoadChildInfo) {
                changeProductExtend.afterLoadChildInfo(ajaxData);
            }
		},
		afterRenderSelectedElements : function(data) {
			if (data) {
				var temp = data.get(0);
				if (temp.get("OLD_PRODUCT_END_DATE")) {
					$("#OLD_PRODUCT_END_DATE").val(temp.get("OLD_PRODUCT_END_DATE"));
//					$("#OLD_PRODUCT_START_DATE").val(temp.get("OLD_PRODUCT_START_DATE"));
				}
				if (data.get(0).get("NEW_PRODUCT_START_DATE")) {
					$("#NEW_PRODUCT_START_DATE").val(temp.get("NEW_PRODUCT_START_DATE"));
				}
				//海南支持预约产品变更  add guohuan
				if (temp.get("EFFECT_NOW_DISABLED") === "false") {
                    $("#EFFECT_NOW").attr("disabled", "");
                } else {
                    $("#EFFECT_NOW").attr("disabled", true);
                }
                if (temp.get("EFFECT_NOW_CHECKED") === "true") {
                    $("#EFFECT_NOW").attr("checked", true).trigger("click");
                } else {
                    $("#EFFECT_NOW").attr("checked", "");
                }
			}
		  var userProductId = $("#USER_PRODUCT_ID").val();
		  var newProductId = $("#NEW_PRODUCT_ID").val();
//          $("#SHAREMEAL_NUMBERS").val("");
//          $("#SHAREMEAL_FLAG").val(0);
//          if ("80003014" === newProductId) {
//              $("#OTHER_DISPLAY_DIV").css("display", "");
//              $("#SHAREMEAL_LI").css("display", "");
//              $("#SHAREMEAL_CHECK_BTN").attr("disabled", false);
//          } else {
//              $("#SHAREMEAL_LI").css("display", "none");
//              $("#SHAREMEAL_CHECK_BTN").attr("disabled", true);
//          }

          if ("80003014" === userProductId) {
              MessageBox.alert("您将取消流量不限量套餐，取消后，办理套餐时默认开通的共享关系和统付关系将同步取消。");
          }

          // 海南添加
          if (typeof changeProductExtend !== "undefined"
                  && changeProductExtend.afterChangeProduct) {
              changeProductExtend.afterChangeProduct();
          }
		},
        checkSharePhone: function () {
            var shareMealLi = $("#SHAREMEAL_LI").css("display");

            if (shareMealLi !== "none") {
                var shareMealNumbers = $("#SHAREMEAL_NUMBERS").val().trim();
                if (shareMealNumbers.indexOf(" ") === -1) {
                    MessageBox.alert("请按格式正确填写共享副号！");
                    return;
                }
                var shareMealOneValue = shareMealNumbers.split(" ", 2)[0];
                var shareMealTwoValue = shareMealNumbers.split(" ", 2)[1];
                
                if ("" === shareMealOneValue || null == shareMealOneValue) {
                    MessageBox.alert("请正确填写共享副号一");
                    return;
                }
                if ("" === shareMealTwoValue || null == shareMealTwoValue) {
                    MessageBox.alert("请正确填写共享副号二");
                    return;
                }
                if (shareMealOneValue === shareMealTwoValue) {
                    MessageBox.alert("填写共享副号不能一样");
                    return;
                }
            }

            var params = "&NEW_PRODUCT_ID=" + $("#NEW_PRODUCT_ID").val()
                    + "&SERIAL_NUMBER=" + $("#SERIAL_NUMBER").val()
                    + "&SHAREMEALONE=" + shareMealOneValue
                    + "&SHAREMEALTWO=" + shareMealTwoValue
                    + "&BOOKING_DATE=" + $("#BOOKING_DATE").val();
            $.beginPageLoading("校验共享号码...");
            $.ajax.submit(null, "checkShareMealPhoneNum", params, null,
                function (rtnData) {
                    $.endPageLoading();
                    if (rtnData != null && rtnData.length > 0) {
                        $("#SHAREMEAL_FLAG").val("1");
                        $("#SHAREMEALONE_NAME").val(shareMealOneValue);
                        $("#SHAREMEALTWO_NAME").val(shareMealTwoValue);
                        MessageBox.success("校验通过！");
                    }
                },
                function (error_code, error_info, detail) {
                    $.endPageLoading();
                    $("#SHAREMEAL_FLAG").val("0");
                    MessageBox.error("错误提示", error_info, null, null, null, detail);
                });
        },
		submitBeforeAction : function() {
			$.tradeCheck.setListener('checkBeforeTrade');
			var confirmRelaDiscnt = "";
			var gyChangeProductFlag = "";
			var data = selectedElements.getSubmitData();
			var userProductId = $("#USER_PRODUCT_ID").val();
			var newProductId = selectedElements.newProductId;
			var remarkLength = changeProduct.getRemarkLength($("#REMARK").val());
			var serialNumber = $.auth.getAuthData().get("USER_INFO").get("SERIAL_NUMBER");
			
			if(!$.validate.verifyField("NEW_VPMN_DISCNT")){
	            return false;
	        }
			if (remarkLength > 100) {
				alert("备注最大长度为100！");
				return false;
			}
			if (data && data.length > 0) {
				
//				//整合产品变更业务校验、规则   guohuan  
//				//共享副号
//				var shareMealLi = $("#SHAREMEAL_LI").css("display");
//
//		        if (shareMealLi !== "none") {
//		            var shareMealNumbers = $("#SHAREMEAL_NUMBERS").val();
//		            if (shareMealNumbers.indexOf(",") === -1) {
//		                MessageBox.alert("请按格式正确填写共享副号！");
//		                return;
//		            }
//		            var shareMealOneValue = $("#SHAREMEALONE_NAME").val();
//		            var shareMealTwoValue = $("#SHAREMEALTWO_NAME").val();
//		
//		            if ("" === shareMealOneValue) {
//		                MessageBox.alert("请正确填写共享副号一");
//		                return;
//		            }
//		            if ("" === shareMealTwoValue) {
//		                MessageBox.alert("请正确填写共享副号二");
//		                return;
//		            }
//		            if (shareMealOneValue === shareMealTwoValue) {
//		                MessageBox.alert("填写共享副号不能一样");
//		                return;
//		            }
//		            if ("0" === $("#SHAREMEAL_FLAG").val()) {
//		                MessageBox.alert("请点击校验通过！");
//		                return;
//		            }
//		        }
//		        
		      //REQ201811300035 主套餐变更办理界面增加宽带权益判断提醒
                var paramDisct=null;
                var paramContent="";
                $.ajax.submit(null, 'getDisctTipsInfo', "&NEW_PRODUCT_ID="+$("#NEW_PRODUCT_ID").val()+"&SERIAL_NUMBER=" + serialNumber, null, function(paraData) { 
   					$.endPageLoading();
   					if(paraData!=null&&paraData.length > 0){
   						paramDisct=	paraData;   	
   						var submitData = selectedElements.getSubmitData();
   						submitData.each(function (item) {
   		                    var elementId = item.get("ELEMENT_ID");
   		                    if(paramDisct!=null){
   		                    	for(var i = 0; i < paramDisct.length; i++){
   		                    		if(paramDisct.get(i).get("PARA_CODE1")==elementId){
   		                    			paramContent=paramDisct.get(i).get("PARA_CODE20");
   		                    			break;
   		                    		}                  		
   		                    	}
   		                    }
   		                });
   						
   					}
   				}, function(error_code, error_info,detail) {
   					$.endPageLoading();
   				    MessageBox.error("错误提示", error_info, null, null, null, detail);
   				},{async:false});

				// 取消GPRS的时候，二次确认后，如果用户确认取消，则系统同时取消VOLTE服务。
				if (data && data.length > 0) {
				    var bResult22 = true;
				    var bResult190 = true;
				    for (var i = 0; i < data.length; i++) {
				        var item = data.get(i);
				        var strElementId = item.get("ELEMENT_ID");
				        var strElementTypeCode = item.get("ELEMENT_TYPE_CODE");
				        var strModifyTag = item.get("MODIFY_TAG");
				        if (strElementId === "22" && strElementTypeCode === "S"
				                && strModifyTag === "1") {
				            bResult22 = false;
				        }
				        if (strElementId === "190" && strElementTypeCode === "S"
				                && strModifyTag === "1") {
				            bResult190 = false
				        }
				    }
				
				    if (!bResult22 && bResult190) {
				        if (selectedElements.checkIsExist("190", "S")) {
				            var msg = "办理取消手机上网功能，则系统同时取消VOLTE服务，是否继续？";
				            MessageBox.confirm(msg, null,
				                function (btn) {
				                    if (btn === "ok") {
				                        confirmMessageBeforeSubmit(userProductId, newProductId);
				                    }
				                });
				            return;
				        }
				    }
				}
				
				var content1 = "是否要终止宽带1+活动？";
	   			 $.ajax.submit('', 'getCancelActiveInfos', "&NEW_PRODUCT_ID="+$("#NEW_PRODUCT_ID").val()+"&SERIAL_NUMBER=" + serialNumber, '', function(rtnData) { 
	   					$.endPageLoading();
	   					if(rtnData!=null&&rtnData.length > 0){
	   							if(newProductId==rtnData.get("PARAM_CODE"))
	   							{
		   							 MessageBox.confirm(content1, null,
		   	                                function (btn) {
		   	                                    if (btn === "ok") {
		   	                                    	var	content2 = "是否立即终止？";
		   	                                    	MessageBox.confirm(content2, null,
		   	     	   	                                function (btn) {
		   	     	   	                                    if (btn === "ok") {
				   	     	   	                                $("#WDACTICE_ENDFLAG").val("Y");
																//changeProduct.confirmSubmit();
				   	     	   	                                confirmMessageBeforeSubmit(userProductId, newProductId);
		   	     	   	                                    }else{
				   	     	   	                                $("#WDACTICE_ENDFLAG").val("N");
																//changeProduct.confirmSubmit();
				   	     	   	                                confirmMessageBeforeSubmit(userProductId, newProductId);
		   	     	   	                                    }
		   	     	   	                                });
		   	                                    }
		   	                                });
	   							}
	   					}
	   				}, function(error_code, error_info,detail) {
	   					$.endPageLoading();
	                                   MessageBox.error("错误提示", error_info, null, null, null, detail);
	   				});  
	   			
	   			confirmMessageBeforeSubmit(userProductId, newProductId);
	   			
	   			function confirmMessageBeforeSubmit(userProductId, newProductId) {
                    // 提示确认
                    var content = "";
                    var productName = $("#NEW_PRODUCT_NAME").val();
                    var bookingDate = $("#BOOKING_DATE").val();
                    var sysDate = $("#SYS_DATE").val();
                    if (bookingDate !== "undefined" && bookingDate !== ""
                            && sysDate !== "undefined" && sysDate !== "") {
                        if (bookingDate.substring(0, 10) === sysDate.substring(0, 10)) {
                            if (newProductId == null || newProductId === ""
                                    || userProductId === newProductId) {
                                content = "本次将受理【立即】产品变更业务，是否继续？"+paramContent;
                            } else {
                                content = "本次将受理【立即】【" + productName
                                        + "】产品变更业务，是否继续？"+paramContent;
                            }
                        } else {
                            if (newProductId == null || newProductId === ""
                                    || userProductId === newProductId) {
                                content = "本次将受理【预约】产品变更业务，是否继续？"+paramContent;
                            } else {
                                content = "本次将受理【预约"
                                        + bookingDate.substring(0, 10) + "】【"
                                        + productName + "】产品变更业务，是否继续？"+paramContent;
                            }
                        }
                    }
                    MessageBox.confirm("确认提示", content,
                        function (btn) {
                            if (btn === "ok") {
                              //  $.cssubmit.submitTrade(); // 提交台账
		                        var	param = "&SELECTED_ELEMENTS=" + data.toString() + "&SERIAL_NUMBER="
										+ $.auth.getAuthData().get("USER_INFO").get("SERIAL_NUMBER") + "&REMARK=" + $("#REMARK").val()
										+ + "&TERMINAL_ID="+$("#TERMINAL_ID").val() ;
								
								if (selectedElements.newProductId) {
									param += "&NEW_PRODUCT_ID=" + newProductId;
								}
								if ($("#EFFECT_NOW").attr("checked")) {
									param += "&EFFECT_NOW=1";
								}
								if (typeof changeProductExtend.getOtherSubmitParam === "function") {
				                    param += changeProductExtend.getOtherSubmitParam();
				                }
								if($("#WDACTICE_ENDFLAG").val()!= "undefined" && $("#WDACTICE_ENDFLAG").val() !=''&&$("#WDACTICE_ENDFLAG").val()!=null){
									param+="&WDACTICE_ENDFLAG="+$("#WDACTICE_ENDFLAG").val();
								}

								var checkParam = "&NEW_PRODUCT_ID=" + newProductId + "&USER_PRODUCT_ID=" + selectedElements.userProductId
										+ "&USER_EPARCHY_CODE=" + selectedElements.eparchyCode;
								$.tradeCheck.addParam(checkParam);
								$.cssubmit.addParam(param);
								$.cssubmit.submitTrade();
                            }else{
                            	$("#WDACTICE_ENDFLAG").val("");
                            }
                        });
                };
			} else {
				alert("您没有进行产品变更，不能提交");
				return false;
			}
		},
		getRemarkLength : function(remark) {
			if (remark && remark != "") {
				var realLength = 0, charCode = -1;
				for (var i = 0; i < remark.length; i++) {
					charCode = remark.charCodeAt(i);
					if (charCode >= 0 && charCode <= 128)
						realLength += 1;
					else
						realLength += 2;
				}
				return realLength;
			}
			return 0;
		},
		//预约时间 guohuan
		afterBookingDate: function () {
			 var bookingDate = $("#BOOKING_DATE").val();
			    var userStateDate = $("#OLD_PRODUCT_START_DATE").val();
			    if(typeof userStateDate !== "undefined" && userStateDate != null && userStateDate !== "") {
			    	userStateDate = userStateDate.length>10?userStateDate.substring(0,10):userStateDate;
			    }
			    if (bookingDate !== "") {
			    	if(dateUtils.toDate(bookingDate)-dateUtils.toDate($("#SYS_DATE").val().substring(0,10))<0){
			    		alert("预约时间不能小于"+$("#SYS_DATE").val().substring(0,10));
			    		$("#BOOKING_DATE").val($("#SYS_DATE").val().substring(0,10));
			    		return;
			    	}
			        var newProductId = $("#NEW_PRODUCT_ID").val();
			        if (typeof newProductId !== "undefined" && newProductId != null
			                && newProductId !== "") {
			            var sysDate = $("#SYS_DATE").val();
			            if (bookingDate.substring(0, 10) === sysDate.substring(0, 10)) {
			                $("#OLD_PRODUCT_END_DATE").val(dateUtils.addSeconds(-1, sysDate));
			                $("#NEW_PRODUCT_START_DATE").val(sysDate);
			                selectedElements.effectNow();
			                userProduct.effectOldProduct(true, userStateDate, dateUtils.addSeconds(-1, sysDate.substring(0,10)));
							userProduct.effectProduct(true, sysDate.substring(0,10), "2050-12-31");
			            } else {
			                var oldProductEndDate = dateUtils.toString(dateUtils.toDate(dateUtils.addDays(-1, bookingDate)), "YYYY-MM-DD");
			                $("#OLD_PRODUCT_END_DATE").val(oldProductEndDate);
			                $("#NEW_PRODUCT_START_DATE").val(bookingDate.substring(0,10));
			                selectedElements.unEffectNow();
			                userProduct.effectOldProduct(false, userStateDate.substring(0,10), oldProductEndDate);
			                userProduct.effectProduct(false, bookingDate.substring(0,10), "2050-12-31");
			            }
			        }
			        selectedElements.effectBookingDate();
			    }
		 },
		effect : function(obj) {
			if (obj.checked) {
				var startDate = dateUtils.today();
				selectedElements.effectNow();
				userProduct.effectProduct(true, startDate, "2050-12-31 23:59:59");
			} else {
				var startDate = dateUtils.addMonths(1, dateUtils.firstDayOfMonth());
				selectedElements.unEffectNow();
				userProduct.effectProduct(false, startDate, "2050-12-31 23:59:59");
			}
		}
	});
})();
