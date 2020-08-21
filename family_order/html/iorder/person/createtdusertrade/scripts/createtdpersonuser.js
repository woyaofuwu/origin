(function(){
	if(window.createTDUser == undefined){
		var PAGE_FEE_LIST = $.DataMap();
		function isNull(str){
			if(str==undefined || str==null || str=="") {
				return true;
			}
			return false;
		}
		function showLayer(optionID) {
			$("#" + optionID).css("display","");
		}
		function hideLayer(optionID) {
			$("#" + optionID).css("display","none");
		}
		window.createTDUser={
			init:function(){
				$.custInfo.pushWidget(5,"USER_TYPE_CODE_PART");
				$.custInfo.pushWidget(-1,"REMARK_PART");
				$.developStaff.init("10"); 
		    },
		    setRealNameValue:function(){
		    	var checked = $("#other_REAL_NAME")[0].checked;
		    	if(checked){
		    		$("#REAL_NAME").val("1");
		    	}else{
		    		$("#REAL_NAME").val("0");
		    	}
		    },
		    setTTtransferValue:function(){
		    	var checked = $("#is_TT_TRANSFER")[0].checked;
		    	if(checked){
		    		$("#TT_TRANSFER").val("1");
		    	}else{
		    		$("#TT_TRANSFER").val("0");
		    	}
		    },
			checkSerialNumber:function(){
				//如果开户号码为空，或格式不正确，则返回
				if(!$.validate.verifyField($("#SERIAL_NUMBER")[0])) {
					return false;
				}
				$("#CHECK_RESULT_CODE").val("-1");	//设置号码未校验
			    var serialNumber = $("#SERIAL_NUMBER").val();
			    $.custInfo.setSerialNumber(serialNumber);
			    if(serialNumber.length != 11){
			    	MessageBox.alert("提示","无线固话号码必须为11位");
			    	return false;
			    }
			    // 清空费用
			    $.feeMgr.clearFeeList("3820");
			    PAGE_FEE_LIST.clear();
			    
			    var param = '&SERIAL_NUMBER=' + serialNumber;
			    $.beginPageLoading("新开户号码校验中......");
			    $.ajax.submit(null,'checkSerialNumber',param,'CardPwdPart',function(data){
		    		$("#SERIAL_NUMBER_INPUT").addClass("e_elements-success");
			    	$("#CHECK_RESULT_CODE").val("0");//号码校验通过
			    	$.endPageLoading();
			    	//预配，预开处理密码因子和SIM卡信息
			    	var simCardNo =data.get(0).get("SIM_CARD_NO");
			    	var feeData;
			    	if(!isNull(simCardNo)) {
			    		$("#SIM_CARD_INPUT").addClass("e_elements-success");
				    	$("#CHECK_RESULT_CODE").val("1");	//SIM卡校验通过
				    	//设置SIM卡返回信息
				    	$("#SIM_CARD_NO").val(simCardNo);
				    	$("#SIM_CARD_NO").attr("disabled", true);
				    	$("#IMSI").val(data.get(0).get("IMSI"));
				    	$("#KI").val(data.get(0).get("KI"));
				    	$("#RES_KIND_CODE").val(data.get(0).get("RES_KIND_CODE"));
				    	$("#RES_TYPE_CODE").val(data.get(0).get("RES_TYPE_CODE"));
				    	//密码因子和SIM卡信息
				    	createTDUser.processCardPwdCode();
			    	}
			    	var beautifualTag =data.get(0).get("BEAUTIFUAL_TAG");
			    	// 号码需要交纳的预存费用
			        if (data.get(0).get("RESERVE_FEE")&&beautifualTag=="1") {
			        	$("#BEAUTIFUAL_TAG").val("1");
			            feeData = $.DataMap();
			            feeData.put("MODE",  "2");
			            feeData.put("CODE", "62"); // 选号预存收入
			            feeData.put("FEE", data.get(0).get("RESERVE_FEE"));
			            feeData.put("PAY", data.get(0).get("RESERVE_FEE"));
			            feeData.put("TRADE_TYPE_CODE", "3820");
			            feeData.put("SYSCHANGETDFEE", data.get(0).get("SYSCHANGETDFEE")); 
			            feeData.put("TDBEAUTIFUALTAG", data.get(0).get("TDBEAUTIFUALTAG")); 
			            $.feeMgr.insertFee(feeData);
			            PAGE_FEE_LIST.put("NUMBER_FEE", $.feeMgr.cloneData(feeData));
			        }
			    },function(error_code,error_info,derror){
					$.endPageLoading();
					$("#SERIAL_NUMBER_INPUT").addClass("e_elements-error");
					showDetailErrorInfo(error_code,error_info,derror);
			    });
		    },
		    processCardPwdCode:function(){
		    	var cardPwd = $("#CARD_PASSWD").val();
		    	var passCode = $("#PASSCODE").val(); 
		    	if(!isNull(cardPwd)&&!isNull(passCode)){
					MessageBox.confirm("提示","该SIM卡为初始密码卡，是否将初始密码作为用户服务密码？",function(re){
						if(re=="ok"){
							$("#DEFAULT_PWD_FLAG").val("1");
							hideLayer("PasswdPart");
						}else{
							$("#DEFAULT_PWD_FLAG").val("0");
							showLayer("PasswdPart");
						}
					});
		    	}else{
		    		$("#DEFAULT_PWD_FLAG").val("0");
					showLayer("PasswdPart");
		    	}
		    },
		    checkSimCardNo:function(){
		    	if($("#CHECK_RESULT_CODE").val()!="0"){
		    		MessageBox.alert("提示","号码校验未通过！");
		    		return false;
		    	}
				if(!$.validate.verifyField($("#SIM_CARD_NO")[0])) {
					return false;
				}
				$.feeMgr.clearFeeList("3820");
				
			    var simCardNo = $("#SIM_CARD_NO").val();
			    var serialNumber = $("#SERIAL_NUMBER").val();
			    var param = '&SIM_CARD_NO=' + simCardNo + '&SERIAL_NUMBER=' + serialNumber;
			    $.beginPageLoading("SIM卡校验中......");
			    $.ajax.submit(null,'checkSimCardNo',param,'CardPwdPart',function(data){
		    		$("#SIM_CARD_INPUT").addClass("e_elements-success");
			    	$("#CHECK_RESULT_CODE").val("1");	//SIM卡校验通过
			    	//设置SIM卡返回信息
			    	$("#IMSI").val(data.get(0).get("IMSI"));
			    	$("#KI").val(data.get(0).get("KI"));
			    	$("#RES_KIND_CODE").val(data.get(0).get("RES_KIND_CODE"));
			    	$("#RES_TYPE_CODE").val(data.get(0).get("RES_TYPE_CODE"));
			    	
			    	$.feeMgr.insertFee(PAGE_FEE_LIST.get("NUMBER_FEE"));
			        $.feeMgr.insertFee(PAGE_FEE_LIST.get("PRODUCT_FEE"));
			 
			        
			    	$.endPageLoading();
			    	//处理密码卡
			    	createTDUser.processCardPwdCode();
			    },function(error_code,error_info,derror){
					$.endPageLoading();
					$("#SIM_CARD_INPUT").addClass("e_elements-error");
					showDetailErrorInfo(error_code,error_info,derror);
			    });
		    },
		    changeSearchType:function(eventObj){
		    	var searchType = eventObj.value;
				var param = "&EPARCHY_CODE="+$("#EPARCHY_CODE").val();
				param += "&SEARCH_TYPE="+searchType;
				if(searchType == "2"){
					param += "&PRODUCT_ID="+$("#PRODUCT_ID").val();
				}
				$.Search.get("productSearch").setParams(param);
		    },
		    searchOptionEnter:function(){
		    	var searchType = $("#productSearchType").val();
				var searchLi = $("#Ul_Search_productSearch li[class=focus]");
				if(searchType == "1"){
					//产品搜索
					var productId = searchLi.attr("PRODUCT_ID");
					var productName = searchLi.attr("PRODUCT_NAME");
					var brandCode = searchLi.attr("BRAND_CODE");
					var brandName = searchLi.attr("BRAND");
					createTDUser.afterChangeProduct(productId,productName,brandCode,brandName);
				}
				else if(searchType == "2"){
					//元素搜索
					var reOrder = searchLi.attr("REORDER");
					var elementId = searchLi.attr("ELEMENT_ID");
					var elementName = searchLi.attr("ELEMENT_NAME");
					var productId = searchLi.attr("PRODUCT_ID");
					var packageId = searchLi.attr("PACKAGE_ID");
					var elementTypeCode = searchLi.attr("ELEMENT_TYPE_CODE");
					var forceTag = searchLi.attr("FORCE_TAG");
					
					if(reOrder!="R"&&selectedElements.checkIsExist(elementId,elementTypeCode)){
						MessageBox.alert("提示","您所选择的元素"+elementName+"已经存在于已选区，不能重复添加");
						return false;
					}
					var elementIds = $.DatasetList();
					var selected = $.DataMap();
					selected.put("PRODUCT_ID",productId);
					selected.put("PACKAGE_ID",packageId);
					selected.put("ELEMENT_ID",elementId);
					selected.put("ELEMENT_TYPE_CODE",elementTypeCode);
					selected.put("MODIFY_TAG","0");
					selected.put("ELEMENT_NAME",elementName);
					selected.put("FORCE_TAG",forceTag);
					selected.put("REORDER",reOrder);
					elementIds.add(selected);
					if(selectedElements.addElements){
						selectedElements.addElements(elementIds);
					}
				}
				$("#Div_Search_productSearch").css("visibility","hidden");
		    },
		    setBrandCode:function(){
		    	if($("#PRODUCT_TYPE_CODE").val()!=""){
		    		createTDUser.initProduct();
		    	}
		    },
		    initProduct:function(){
		    	offerList.renderComponent("",$("#EPARCHY_CODE").val());
				selectedElements.renderComponent("&NEW_PRODUCT_ID=",$("#EPARCHY_CODE").val());
				$("#PRODUCT_NAME").val('');
		    },
		    checkBeforeProduct:function(){
		    	createTDUser.setBrandCode();
		    	var checkResultCode = $("#CHECK_RESULT_CODE").val();
		    	if(checkResultCode=="-1"){
		    		MessageBox.alert("提示","新开户号码校验未通过！");
		    		$("#SERIAL_NUMBER").focus();
		    		return false;
		    	}
		        if(!verifyAll('BaseInfoPart') || !verifyAll('CustInfoPart') || !verifyAll('AcctInfoPart')
		        		|| !verifyAll('AssureInfoPart') || !verifyAll('PostInfoPart'))
		        {
		    	   return false;
		        }
		        if($("#DEFAULT_PWD_FLAG").val()!="1"){
		    	    if(!verifyAll('PasswdPart')) {
		    		   return false;
		    	   } 
		        }
		        ProductSelect.popupProductSelect($('#PRODUCT_TYPE_CODE').val(),$('#EPARCHY_CODE').val(),'');
		    },
		    afterChangeProduct:function(productId, productName, productDesc, brandCode){
		    	$.feeMgr.clearFeeList("3820");
		    	$.feeMgr.insertFee(PAGE_FEE_LIST.get("NUMBER_FEE"));
		    	
		        var feeData = $.DataMap();
		        $("#PRODUCT_ID").val(productId);
		        
		        $("#PRODUCT_NAME").val(productName);
		        $("#PRODUCT_NAME_DISPLAY").html(productName);
		        $("#PRODUCT_DESC").html(productDesc);
		        $("#PRODUCT_DESC").attr("title", productDesc);
		        
		        $("#ProductTypePart").hide();  // 隐藏"产品目录"按钮
		        $("#CHANGE_PRODUCT_BTN").attr("disabled", false).show(); // 展示"变更"按钮
		        $("#PRODUCT_DISPLAY").show();                            // 展示已选产品
		        
		        var param = "&NEW_PRODUCT_ID="+productId;
		        offerList.renderComponent($("#PRODUCT_ID").val(),$("#EPARCHY_CODE").val());
				selectedElements.renderComponent(param,$("#EPARCHY_CODE").val());
				var inparam = "&PRODUCT_ID="+productId + "&BRAND_CODE="+brandCode + "&EPARCHY_CODE="+$("#EPARCHY_CODE").val();
				var length = selectedElements.selectedEls.length;
				for(var i=0;i<length;i++){
					var temp = selectedElements.selectedEls.get(i);
					if(temp.get("ELEMENT_ID")=="15" && temp.get("PRODUCT_ID")!="10009432" && temp.get("MODIFY_TAG")=="0" && temp.get("ELEMENT_TYPE_CODE")=="S")
					{
					  inparam+="&ELEMENT_ID=15";
					}
				}
			    $.beginPageLoading("产品费用查询中......");
				$.ajax.submit(null, 'getProductFeeInfo', inparam, null, function(data) {
					$.endPageLoading();
				    $.cssubmit.disabledSubmitBtn(false);
				  	for(var i = 0; i < data.getCount(); i++) {
				  	     var data0 = data.get(i);
					     if(data0){
								feeData.clear();
								feeData.put("MODE", data0.get("FEE_MODE"));
								feeData.put("CODE", data0.get("FEE_TYPE_CODE"));
								feeData.put("FEE",  data0.get("FEE"));
								feeData.put("PAY",  data0.get("FEE"));		
								feeData.put("TRADE_TYPE_CODE","3820");							
								$.feeMgr.insertFee(feeData);			
							}
				  	}
				
			    },
			    function(error_code,error_info,derror){
					$.endPageLoading();
					showDetailErrorInfo(error_code,error_info,derror);
				});
		    },
		    disableElements:function(data){
		    	if(data){
		    	    var temp = data.get(0);
		    	    if(data.get(0).get("NEW_PRODUCT_START_DATE")){
		    			$("#NEW_PRODUCT_START_DATE").val(temp.get("NEW_PRODUCT_START_DATE"));
		    		}
		    	}
		    },
		    passwdbeforeAction:function(){
		    	if($("#PSPT_TYPE_CODE").val()==""){
		    		MessageBox.alert("提示","证件类型不能为空！");
		    	      return false;
		    	}
		    	if($("#PSPT_ID").val()==""){
		    		MessageBox.alert("提示","证件号码不能为空！");
		    	      return false;
		    	}
		    	if($("#BIRTHDAY").val()==""){
		    		MessageBox.alert("提示","出生日期不能为空！");
		    	      return false;
		    	}
		    	if($("#SERIAL_NUMBER").val()==""){
		    		MessageBox.alert("提示","新开户号码不能为空！");
		    		return false;
		    	}
	    	    //将值赋给组件处理
	    	    var psptId =$("#PSPT_ID").val();
	    	    var serialNumber = $("#SERIAL_NUMBER").val();
	    	    $.password.setPasswordAttr(psptId, serialNumber);
	    	    return true ;
		    },
		    passwdafterAction:function(data){
		    	$("#USER_PASSWD").val(data.get("NEW_PASSWORD"));
		    },
		    onSubmit:function(){
		    	if(!verifyAll('BaseInfoPart') || !verifyAll('CustInfoPart') || !verifyAll('AcctInfoPart')
		        		|| !verifyAll('AssureInfoPart') || !verifyAll('PostInfoPart'))
		        {
		    	   return false;
		        }
		    	if($("#DEFAULT_PWD_FLAG").val()!="1"){
		    	    if(!verifyAll('PasswdPart')) {
		    		   return false;
		    	   } 
		        }
				if ($("#BEAUTIFUAL_TAG").val() == "1") {
		        	   MessageBox.confirm("您正在办理固话吉祥号码开户，开户后将绑定营销活动,请确认是否受理！", null,
			    	            function (btn) {
			    	                if (btn === "ok") {
			    	                	var data = selectedElements.getSubmitData();
			    	    		        var param = "&SELECTED_ELEMENTS="+data.toString();
			    	    		        $.cssubmit.addParam(param);
			    	    		        $.cssubmit.submitTrade();
			    	                }
			    	            });
		        	   return false;
		        }
                var agentPicIdObj = $("#AGENT_PIC_ID");
                if (agentPicIdObj.val() === "") {
                    agentPicIdObj.val("AGENT_PIC_ID_value");
                }
                if (agentPicIdObj.val() === "AGENT_PIC_ID_value") {
                    agentPicIdObj.val("");
                }
                // 增加未进行人像比对的拦截
                // 1、工号是否免比对
                var iscmp ="1";
                $.ajax.submit(null,'isCmpPic','','',
                    function(data){
                        var flag=data.get("CMPTAG");
                        if(flag=="0"){
                            iscmp = "0";
                        }
                        $.endPageLoading();
                    },function(error_code,error_info){
                        $.MessageBox.error(error_code,error_info);
                        $.endPageLoading();
                    },{
                        "async" : false
                    });

                if (iscmp == "0"){
                    var picId = $("#PIC_ID").val();
                    var psptTypeCode = $("#PSPT_TYPE_CODE").val();
                    var custName = $("#AGENT_CUST_NAME").val();      // 经办人名称
                    var psptId = $("#AGENT_PSPT_ID").val();          // 经办人证件号码
                    var agentPsptAddr = $("#AGENT_PSPT_ADDR").val(); // 经办人证件地址
                    var agentPicId =  agentPicIdObj.val();
                    var agentTypeCode = $("#AGENT_PSPT_TYPE_CODE").val();

                    if (picId != null && picId === "ERROR") { // 客户摄像失败
                        MessageBox.error("告警提示", "客户" + $("#PIC_STREAM").val());
                        return false;
                    }else if(picId == ""){ // 未进行客户摄像
						if (psptTypeCode == "0" || psptTypeCode == "1" || psptTypeCode == "3" ){
                            if (agentPicId){// 经办人已摄像
                                if (agentPicId == "ERROR"){// 摄像失败
                                    MessageBox.error("告警提示", "经办人" + $("#AGENT_PIC_STREAM").val());
                                    return false;
                                }
                            }else {// 经办人未摄像
                                MessageBox.error("告警提示", "请进行客户或经办人摄像！");
                                return false;
                            }
						}

                        if (psptTypeCode == "D" || psptTypeCode == "E" || psptTypeCode == "G" || psptTypeCode == "L" || psptTypeCode == "M"){
                            if (agentTypeCode == ""){// 经办人证件类型为空
                                MessageBox.error("告警提示", "请选择经办人的证件类型");
                                return false;
                            }
                            // 客户证件类型是单位类型，经办人证件选择 身份证需要进行人像比对
                            if(agentTypeCode == "0" || agentTypeCode == "1" || agentTypeCode == "3" ){
                                if (agentPicId){// 经办人已摄像
                                    if (agentPicId == "ERROR"){// 摄像失败
                                        MessageBox.error("告警提示", "经办人" + $("#AGENT_PIC_STREAM").val());
                                        return false;
                                    }
                                }else {// 经办人未摄像
                                    MessageBox.error("告警提示", "请进行经办人摄像！");
                                    return false;
                                }
                            }
                        }
                    }
                }
		    	var data = selectedElements.getSubmitData();
		        var param = "&SELECTED_ELEMENTS="+data.toString();
		        $.cssubmit.addParam(param);
		        return true;
		    }
		}
	}
	$(createTDUser.init);
})();

//点击展示，隐藏产商品信息按钮
function displayOfferList(obj){
	var div = $('#'+obj);
	if($("#showCheckbox").attr('checked')){
		div.css('display', '');
	}
	else{
		div.css('display', 'none');
	}
}

