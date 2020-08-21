(function($){
	$.extend({ShoppingCart:{
			detailOrderIds:"",
			shoppingData: $.DataMap(),
			shppingFeeData: $.DataMap(),
			shoppingElements: new $.DatasetList(),//购物车所有商品
			loadShoppingCart:function(data){
				var userInfo = data.get("USER_INFO");
				var custInfo = data.get("CUST_INFO");
				var params = "&SERIAL_NUMBER="+userInfo.get("SERIAL_NUMBER")+"&USER_ID="+userInfo.get("USER_ID") + 
							 "&ROUTE_EPARCHY_CODE="+userInfo.get("EPARCHY_CODE")+"&CUST_ID="+userInfo.get("CUST_ID");
				$.beginPageLoading("查询购物车信息...");
				$.ajax.submit('', 'loadShoppingCart', params, '', 
					function(data){
						var orderSet = data.get("ORDER_SET");
						$.ShoppingCart.shoppingElements = $.ShoppingCart.buildShoppingElements(orderSet);
						var shoppingCartId = data.get("SHOPPING_CART_ID");
						$("#PAY_NAME").html(custInfo.get("CUST_NAME")+"("+userInfo.get("SERIAL_NUMBER")+")");
						$("#BEGIN_MESSAGE").css("display","none");
						$.ShoppingCart.drawShoppingCart(shoppingCartId, orderSet, userInfo.get("SERIAL_NUMBER"));
						$.endPageLoading();
					},
					function(error_code,error_info){
						$.endPageLoading();
						MessageBox.alert(error_info);
					});
				
				//shoppingList.renderComponent(userInfo.get("USER_ID"), userInfo.get("EPARCHY_CODE"));
			},
			drawShoppingCart:function(shoppingCartId, orderSet, serialNumber){
				var tl = 0;
				if(orderSet){
					tl=orderSet.length;
				}
				$("#tradeCount").html(tl);
				$("#tradeCount").attr("value",tl);
				var shoppingFee = 0;
				$("#tradeListTable").empty();
				$("#tradeListTable").attr("shoppingCartId", shoppingCartId);
				var tradeTrArr = [];
				tradeTrArr.push("<tbody>");
				for(var i=0; i<tl; i++){
					var orderDetail = orderSet.get(i);
					tradeTrArr.push('<tr id="'+orderDetail.get("DETAIL_ORDER_ID")+'">');
					var acceptDate = $.format.substr(orderDetail.get("ACCEPT_DATE"),0,10);
					var tradeType = orderDetail.get("TRADE_TYPE");
					var totalFee = orderDetail.get("TOTAL_FEE");
					shoppingFee += Math.round(parseFloat(totalFee));
					var detailOrderId = orderDetail.get("DETAIL_ORDER_ID");
					var orderTypeCode = orderDetail.get("ORDER_TYPE_CODE");
					//是否允许删除的标志
					var shoppingDelFlag = orderDetail.get("SHOPPING_DEL_FLAG");
					if($.ShoppingCart.detailOrderIds==""){
						$.ShoppingCart.detailOrderIds += detailOrderId;
					}else{
						$.ShoppingCart.detailOrderIds += ","+detailOrderId;
					}
					
					if("240"==orderTypeCode){
						tradeTrArr.push('<td class="pic"><span class="e_ico-pic-blue e_ico-pic-xxl e_ico-activity"></span></td>');
					}else if("3700"==orderTypeCode){
						tradeTrArr.push('<td class="pic"><span class="e_ico-pic-blue e_ico-pic-xxl e_ico-task"></span></td>');
					}else{
						tradeTrArr.push('<td class="pic"><span class="e_ico-pic-blue e_ico-pic-xxl e_ico-change"></span></td>');	
					}			
					tradeTrArr.push('<td class="name">');
					tradeTrArr.push('<div class="content e_show-phone">'+serialNumber+'</div>');
					tradeTrArr.push('<div class="content">'+acceptDate+'</div>');
					tradeTrArr.push('<div class="title">'+tradeType+'</div>');
					tradeTrArr.push('</td>');
					tradeTrArr.push('<td class="detail">');
					tradeTrArr.push('<ul>');
					var elementTradeSet = orderDetail.get("ELEMENT_TRADE_SET");
					var length = elementTradeSet.length;
					var startEnd = null;
					var mainStartEnd = null;
					var el = length > 2 ? 2:length;  //需要修改
					for(var j=0; j<el; j++){
						var elementTrade = elementTradeSet.get(j);
						var elementId = "[" + elementTrade.get("ELEMENT_ID") + "]";
						var elementName = elementTrade.get("ELEMENT_NAME");
						var elementTypeCode = elementTrade.get("ELEMENT_TYPE_CODE");
						var modifyTag = elementTrade.get("MODIFY_TAG");
						var startDate = elementTrade.get("START_DATE");
						var endDate = elementTrade.get("END_DATE");
						var dateStr = startDate+"~"+endDate;
						if("P"==elementTrade.get("ELEMENT_TYPE_CODE") && "0"==modifyTag) {
							mainStartEnd = startDate.substring(0, 10)+"~"+endDate.substring(0, 10);
						}else {
							startEnd = startDate.substring(0, 10)+"~"+endDate.substring(0, 10);
						}
						if(modifyTag == "1"){
							tradeTrArr.push('<li title="【'+dateStr+'】"><span class="e_delete">'+elementId+elementName+'</span><br/>'+'</li>');
						} else if(modifyTag == "2") {
							tradeTrArr.push('<li title="【'+dateStr+'】"><span class="e_red">'+elementId+elementName+'</span><br/>'+'</li>');
						} else {
							tradeTrArr.push('<li title="【'+dateStr+'】"><span class="e_black">'+elementId+elementName+'</span><br/>'+'</li>');
						}
						//tradeTrArr.push('<li><span class="e_black">'+startDate+'~'+endDate+'</span><br/>'+'</li>');
					}
					if(length > 2){
						$.ShoppingCart.shoppingData.put(detailOrderId, elementTradeSet);
						tradeTrArr.push('<li>');
						tradeTrArr.push('	<div class="main link">');
						tradeTrArr.push('		<div class="content content-row-2">');
						tradeTrArr.push('			<button ontap="$.ShoppingCart.showOrderDetail('+detailOrderId+')" class="e_button-s " type="button"><span>更多...</span></button>');
						tradeTrArr.push('		</div>');
						tradeTrArr.push('	</div>');
						tradeTrArr.push('</li>');
					}
					tradeTrArr.push('</ul>');
					tradeTrArr.push('</td>');
					
					var betweenData = null==mainStartEnd ? startEnd : mainStartEnd;
					tradeTrArr.push('<td class="detail">');
					tradeTrArr.push('<div class="e_black">'+betweenData+'</div>');
					tradeTrArr.push('</td>');
					
					var tipInfos = "";
					var feeDatas = orderDetail.get("FEE_SET");
					if(feeDatas && feeDatas.length>0) {
						for(var k=0; k<feeDatas.length; k++){
							var feeData = feeDatas.get(k);
							if(tipInfos.length>0){
								tipInfos += "</br>";
							}
//							tipInfos += "<span class='e_tag e_tag-blue'>" + feeData.get("FEE_TYPE_NAME") + "：" + feeData.get("FEE") + "</span>";
							tipInfos += "<span class='e_ico-money'></span><span>" + feeData.get("FEE_TYPE_NAME") + "：" + feeData.get("FEE")/100 + "</span>";
						}
					}
					
//					var tipInfos = "<span class='e_tag e_tag-blue'>话费押金：" + 200 + "</span>";
//					tipInfos += "</br><span class='e_tag e_tag-blue'>预存宽带款：" + 888 + "</span>";
//					tipInfos += "</br><span class='e_tag e_tag-blue'>换卡费：" + 200 + "</span>";
//					tipInfos += "</br><span class='e_tag e_tag-blue'>开户费：" + 200 + "</span>";
					
					tradeTrArr.push('<td class="price">');
					if(tipInfos.length>0) {
						tradeTrArr.push('<span class="title" tip="' + tipInfos + '" id="ORDER_FEE_'+detailOrderId+'" detailOrderFee="'+totalFee+'">￥'+totalFee+'</span>');
					}else {
						tradeTrArr.push('<span class="title" id="ORDER_FEE_'+detailOrderId+'" detailOrderFee="'+totalFee+'">￥'+totalFee+'</span>');
					}
					tradeTrArr.push('</td>');
					//
					var delId = "deleteTrade_"+i;
					var checkBoxId = "checkBoxId_" + i;
					
					var showStyle = "none";
					if(i==tl-1){
						showStyle = "";
					} else {
						showStyle = false;
					}
					showStyle = "";//先全部显示吧
					tradeTrArr.push('<td class="fn"><span style="display:'+showStyle+'" onclick="$.ShoppingCart.deleteOrder('+detailOrderId+','+i+','+serialNumber+')" class="e_ico-delete" id="'+delId+'" detailOrderId="'+detailOrderId+'"></span></td>');
					tradeTrArr.push('<td class="fn"><input style="display:'+showStyle+'" onclick="$.ShoppingCart.checkBoxAction('+i+','+tl+','+detailOrderId+','+serialNumber+');" type="checkbox" id="'+checkBoxId+'" name="shoppingCheckBox" value="'+detailOrderId+'" checked="checked"/></td>');
					tradeTrArr.push('</tr>');
					
//					var feeSet = orderDetail.get("FEE_SET");
//					$.ShoppingCart.showFeeInfo(feeSet, "add");
//					$.ShoppingCart.shppingFeeData.put("FEE_" + i, feeSet);
				}
				tradeTrArr.push("</tbody>");
				if(tl>0){
					$("#NULL_MESSAGE").css("display","none");
					$("#tradeListTable").html(tradeTrArr.join(""));
					$("#TOTAL_FEE").val(shoppingFee);
					$("#payFeePart").css("display","");
					$("#selectedOrderCount").html(tl);
					$("#checkAll").attr("checked", "checked")
				}else{
					$("#NULL_MESSAGE").css("display","");
					$("#payFeePart").css("display","none");
				}
			},
			
			//展示费用
			showFeeInfo:function(feeSet, flag){
				if(!feeSet)
					return;
				for(var j=0; j<feeSet.length; j++){
					var feeData = feeSet.get(j);
					var fee = $.DataMap();
					fee.put("MERCH_ID", feeData.get("MERCH_ID"));
					fee.put("MERCH_NAME", feeData.get("MERCH_NAME"));
					fee.put("TRADE_TYPE_CODE", feeData.get("TRADE_TYPE_CODE"));
					fee.put("MODE", feeData.get("FEE_MODE"));// 大类 押金
					fee.put("CODE", feeData.get("FEE_TYPE_CODE"));// 小类
					fee.put("FEE", feeData.get("OLDFEE"));// 应缴金额
					fee.put("PAY", feeData.get("FEE"));// 实际缴纳
					if(feeData.containsKey("USER_ID") && feeData.get("USER_ID")){
						fee.put("USER_ID", feeData.get("USER_ID"));
					}
					
					if(flag && flag == "add"){
						$.feeMgr.insertFee(fee);
					}else if(flag && flag == "del"){
						$.feeMgr.deleteFee(fee);
					}
				}
			},
			
			execFee: function(i, flag){
				var feeSet = $.ShoppingCart.shppingFeeData.get("FEE_" + i);
				$.ShoppingCart.showFeeInfo(feeSet, flag);
			},
			
			checkBoxAction : function(itemIndex, count, detailOrderId, serialNumber) {
				var selectedElements = $.ShoppingCart.getSubmitData();//购物车选中的商品信息,遍历购物车，过滤没有勾选的商品
				var elements = $.ShoppingCart.shoppingElements.get(itemIndex);//$.ShoppingCart.getElementData(detailOrderId);//当前选择的商品信息
				
				var checkedFlag = $("#checkBoxId_" + itemIndex).attr("checked");
				if(checkedFlag) {
					elements.put("CHECK_TAG","0");
				}else {
					elements.put("CHECK_TAG","exist");
				}
				
				if(elements.get("CHECK_TAG")=="exist") {
					var shoppingCartId = $("#tradeListTable").attr("shoppingCartId");					
					var params = "ELEMENTS="+elements.toString() + "&SERIAL_NUMBER=" + serialNumber;
					params+="&SELECTED_ELEMENTS="+ encodeURIComponent(selectedElements.toString())+"&TRADE_TYPE_CODE=110";
					params+="&SHOPPING_CART_ID="+shoppingCartId+"&DETAIL_ORDER_ID="+detailOrderId;
					$.beginPageLoading("规则校验中...");
					$.ajax.submit('', 'checkBoxAction', params, '', 
						function(data){
							if(data.get(0).get("ERROR_INFO")) {
								MessageBox.confirm("提示信息", data.get(0).get("ERROR_INFO")+"<br/>点击“确定”按钮继续本次操作，但请按照提示处理不符合要求的元素<br/>点击“取消”按钮取消本次操作", function(btn) {
									if(btn == "cancel") {
										elements.put("CHECK_TAG","0");
										$("#checkBoxId_" + itemIndex).attr("checked", "checked");
				        			}else {
										$.ShoppingCart.afterShowCheckBox(itemIndex, count);
				        			}
								});
								$.endPageLoading();
							}else {
								$.ShoppingCart.afterShowCheckBox(itemIndex, count);
								$.endPageLoading();
							}
						},
						function(error_code,error_info){
							$.endPageLoading();
							elements.put("CHECK_TAG","0");
							$("#checkBoxId_" + itemIndex).attr("checked", "checked");
							MessageBox.alert(error_info);
						});
				}else {
					$.ShoppingCart.afterShowCheckBox(itemIndex, count);
				}
					
			},
			
			//选中事件
			afterShowCheckBox: function(n, count){
				var checkSwitch = $("#checkBoxSwitch").val();
				var checkBoxFlag = (checkSwitch && checkSwitch=="on")?true:false;
				
				var selectCount = parseInt($("#selectedOrderCount").html());
				var preCheck = "checkBoxId_";
				var checkedFlag = $("#"+preCheck+n).attr("checked");
				if(checkedFlag){
					selectCount += 1;
					$.ShoppingCart.execFee(n, "add");
					
					if(checkBoxFlag){
						for(var i=0; i<n; i++){
							if($("#"+preCheck+i).length>0){
								if(!$("#"+preCheck+i).attr("checked")){
									$("#"+preCheck+i).attr("checked", "checked");
									selectCount += 1;
									$.ShoppingCart.execFee(i, "add");
								}
							}
						}
					}
				}else{
					selectCount -= 1;
					$.ShoppingCart.execFee(n, "del");
					
					if(checkBoxFlag){
						if(n-1>=0 && $("#"+preCheck+(n-1)).length>0)
							$("#"+preCheck+(n-1)).css("display", "");
						
						for(var j=n+1; j<count; j++){
							if($("#"+preCheck+j).length>0){
								if($("#"+preCheck+j).attr("checked")){
									$("#"+preCheck+j).attr("checked", "");
									selectCount -= 1;
									$.ShoppingCart.execFee(j, "del");
								}
							}
						}
					}
				}
				
				var allCheckbox=$("input[name='shoppingCheckBox']");
				if(selectCount == allCheckbox.length)
					$("#checkAll").attr("checked", "checked");
				else
					$("#checkAll").attr("checked", "");
				$("#selectedOrderCount").html(selectCount);
				
				$.ShoppingCart.calculateFee();
			},
			//全选
			selectedAllCheckBox:function(){
				var checkedFlag = $("#checkAll").attr("checked");
				
				var selectCount = parseInt($("#selectedOrderCount").html());
				var allCheckbox=$("input[name='shoppingCheckBox']");
			    for(i=0; i<allCheckbox.length; i++){
			    	var checkId = allCheckbox[i].id;
			    	var index = checkId.substring(checkId.indexOf("_")+1);
			        if(!allCheckbox[i].checked && checkedFlag){
			            $(allCheckbox[i]).attr("checked", "checked");
			            selectCount += 1;
			            $.ShoppingCart.execFee(index, "add");
			        }
			        if(allCheckbox[i].checked && !checkedFlag){
			        	$(allCheckbox[i]).attr("checked", "");
			            selectCount -= 1;
			            $.ShoppingCart.execFee(index, "del");
			        }
			    }
			    $("#selectedOrderCount").html(selectCount);
			    
			    $.ShoppingCart.calculateFee();
			},
			
			//费用计算
			calculateFee:function(){
				var detailOrderId;
				var shoppingFee = 0;
				var allCheckbox=$("input[name='shoppingCheckBox']");
			    for(i=0; i<allCheckbox.length; i++){
			        if(allCheckbox[i].checked){
			        	detailOrderId = allCheckbox[i].value;
			        	shoppingFee += Math.round(parseFloat($("#ORDER_FEE_"+detailOrderId).attr("detailOrderFee")));
			        }
			    }
			    $("#TOTAL_FEE").val(shoppingFee);
			},
			
			//商品详细信息展示
			showOrderDetail:function(detailOrderId){
				var elementSet = $.ShoppingCart.shoppingData.get(detailOrderId);
				var detailHtml=[];	
				detailHtml.push('<div class="c_space"></div>');
				var productFlag = false;
				var svcFlag = false;
				var disFlag = false;
				var saleFlag = false;
				for(var j=0; j<elementSet.length; j++){
					var elementData = elementSet.get(j);
					var elementId = "["+elementData.get("ELEMENT_ID")+"]";
					var elementName = elementData.get("ELEMENT_NAME");
					var modifyTag = elementData.get("MODIFY_TAG");
					var sort = elementData.get("SORT");
					var startDate = elementData.get("START_DATE");
					var endDate = elementData.get("END_DATE");
					//0: 产品, 1:服务, 2:平台服务, 3:优惠, 4:营销活动
					if(sort=="0" && !productFlag){
						detailHtml.push('<div class="c_title">');
						detailHtml.push('	<span class="e_red e_strong">组合商品</span>');
						detailHtml.push('</div>');
						detailHtml.push('<div class="c_param c_param-col-2">');
						detailHtml.push('	<ul>');
						productFlag = true;
					} else if((sort == "1" || sort == "2") && !svcFlag){
						if(productFlag){
							detailHtml.push('	</ul>');
							detailHtml.push('</div>');
						}
						detailHtml.push('<div class="c_title">');
						detailHtml.push('	<span class="e_red e_strong">功能类商品</span>');
						detailHtml.push('</div>');
						detailHtml.push('<div class="c_param c_param-col-2">');
						detailHtml.push('	<ul>');
						svcFlag = true;
					} else if(sort == "3" && !disFlag){
						if(svcFlag){
							detailHtml.push('	</ul>');
							detailHtml.push('</div>');
						}
						detailHtml.push('<div class="c_title">');
						detailHtml.push('	<span class="e_red e_strong">内容类商品</span>');
						detailHtml.push('</div>');
						detailHtml.push('<div class="c_param c_param-col-2">');
						detailHtml.push('	<ul>');
						disFlag = true;
					} else if(sort == "4" && !saleFlag){
						if(disFlag){
							detailHtml.push('	</ul>');
							detailHtml.push('</div>');
						}
						detailHtml.push('<div class="c_title">');
						detailHtml.push('	<span class="e_red e_strong">营销活动商品</span>');
						detailHtml.push('</div>');
						detailHtml.push('<div class="c_param c_param-col-2">');
						detailHtml.push('	<ul>');
						saleFlag = true;
					} 
					var dateStr = startDate+"~"+endDate;
					detailHtml.push('<li title="【'+dateStr+'】">');
					detailHtml.push('	<div class="main link">');
					detailHtml.push('		<div class="content content-row-2">');
					if(modifyTag == "1"){
						detailHtml.push('<span class="e_delete">'+elementId+elementName+'</span>');
					} else if(modifyTag == "2"){
						detailHtml.push('<span class="e_red">'+elementId+elementName+'</span>');
					} else {
						detailHtml.push('<span class="e_black">'+elementId+elementName+'</span>');
					}
					//detailHtml.push('</br><span class="e_black">'+startDate+'~'+endDate+'</span>');
					detailHtml.push('		</div>');
					detailHtml.push('	</div>');
					detailHtml.push('</li>');
				}
				detailHtml.push('	</ul>');
				detailHtml.push('</div>');
				
				$("#goodsDetailContent").html(detailHtml.join(""));
				goodsDetail.show();
			},	
			
			deleteOrder:function(detailOrderId, n, serialNumber){
				var shoppingCartId = $("#tradeListTable").attr("shoppingCartId");
				var selectedElements = $.ShoppingCart.getSubmitData();//购物车选中的商品信息,遍历购物车，过滤没有勾选的商品
				var elements = $.ShoppingCart.shoppingElements.get(n);//$.ShoppingCart.getElementData(detailOrderId);//当前选择的商品信息
				
		    	var params = "&SHOPPING_CART_ID="+shoppingCartId+"&DETAIL_ORDER_ID="+detailOrderId+"&SERIAL_NUMBER="+serialNumber;
		    	params += "&ELEMENTS="+elements.toString();
		    	params += "&SELECTED_ELEMENTS="+ encodeURIComponent(selectedElements.toString())+"&TRADE_TYPE_CODE=110";
		    	
		    	$.beginPageLoading("业务删除中...........");
		    	$.ajax.submit('', 'deleteOrder', params, '', function(data){
//		    		$.endPageLoading();
//		    		//更新商品费用
//		    		var detailOrderFee = Math.round(parseFloat($("#ORDER_FEE_"+detailOrderId).attr("detailOrderFee")));
//		    		var allPayFee = parseFloat($("#TOTAL_FEE").val());
//		    		var curAllPayFee = allPayFee-detailOrderFee;
//		    		$("#TOTAL_FEE").val(curAllPayFee);
//		    		
//		    		//费用组件删除费用
//		    		var feeSet = $.ShoppingCart.shppingFeeData.get("FEE_" + n);
//		    		$.ShoppingCart.showFeeInfo(feeSet, "del");
//		    		
//		    		//更新商品总数
//		    		var tradeCount = parseInt($("#tradeCount").attr("value"));
//		    		var curTradeCount = tradeCount-1;
//		    		$("#tradeCount").attr("value",curTradeCount);
//		    		$("#tradeCount").html(curTradeCount);
//		    		//购物车删光了
//		    		if(curTradeCount == 0){
//		    			$("#checkAll").attr("checked", "");
//		    			$("#NULL_MESSAGE").css("display","");
//						$("#payFeePart").css("display","none");
//		    		}
//		    		//更新选中商品数量
//		    		if($("#checkBoxId_"+n).attr("checked")){
//		    			var selectCount = parseInt($("#selectedOrderCount").html());
//		    			selectCount -= 1;
//		    			$("#selectedOrderCount").html(selectCount)
//		    		}
//		    		//展示下一个删除按钮
//		    		if(n-1>=0){
//		    			$("#deleteTrade_" + (n-1)).css("display", "");
//		    		}
//		    		$("#"+detailOrderId).remove();
//		    		
//		    		$.ShoppingCart.shoppingElements.removeAt(n);
		    		
		    		var orderSet = data.get("ORDER_SET");
					$.ShoppingCart.shoppingElements = $.ShoppingCart.buildShoppingElements(orderSet);
					var shoppingCartId = data.get("SHOPPING_CART_ID");
					$.ShoppingCart.drawShoppingCart(shoppingCartId, orderSet, serialNumber);
					$.endPageLoading();
					
		    		MessageBox.alert("删除成功！");
		    	},function(error_code,error_info){
		    		$.endPageLoading();
		    		MessageBox.error("提示信息", error_info);
		    	});
			},
			
			//提交
			submitShoppingCart:function(){
				var shoppingCartId = $("#tradeListTable").attr("shoppingCartId");
		    	var selectedCount = $("#selectedOrderCount").html();
		    	if(!shoppingCartId || selectedCount=="0"){
		    		alert("购物车为空，不能提交！");
		    		return false;
		    	}
		    	
		    	var totalFee = $("#TOTAL_FEE").val();
//		    	if(totalFee >= 0) {
//		    		alert("请向用户收取" + totalFee + "元费用！");
//		    	}
		    	
		    	MessageBox.confirm("费用提示","本次最终收取费用共"+ totalFee +"元，如金额无误请点击确认！",function(btn){
		    		if(btn=='ok'){
		    			var detailOrderIds = "";
				    	var count = 0;
				    	var allCheckbox=$("input[name='shoppingCheckBox']");
					    for(i=0; i<allCheckbox.length; i++){
					        if(allCheckbox[i].checked){
					        	detailOrderIds += allCheckbox[i].value + ",";
					        	count += 1;
					        }
					    }
					    $.cssubmit.setParam("SHOPPING_CART_FLAG", "true");
					    $.cssubmit.setParam("SHOPPING_CART_COUNT", count);
					    detailOrderIds = detailOrderIds.substring(0, detailOrderIds.length-1);
				        var param = "&SHOPPING_CART_ID="+shoppingCartId+"&SERIAL_NUMBER="+$("#AUTH_SERIAL_NUMBER").val()+"&DETAIL_ORDER_IDS="+detailOrderIds;
						$.cssubmit.addParam(param);
						//$.cssubmit.setAfterSubmitAction($.ShoppingCart.afterSubmitAction);
						
						$.beginPageLoading("购物车校验中...");
				    	$.ajax.submit('', 'submitShoppingCartBeforeCheck', param, '', function(data){
				    		$.endPageLoading();
				    		if(data && data.get("TIP_INFO")){
				    			MessageBox.show({
				    				success : true,
				    				msg : "确认提示",
				    				content : "<span class='e_red'>"+data.get("TIP_INFO")+"</span>",
				    				buttons : {"ok" : "继续办理", "cancel" : "取消受理"},
				    				fn : function(btn){
				    					if("cancel" == btn){
				    						return false;
				    					} else if("ok" == btn){
				    						$.cssubmit.submitTrade();
				    					}
				    				}
				    			});
				    		} else {
				    			$.cssubmit.submitTrade();
				    		}
				    	},function(error_code,error_info){
				    		$.endPageLoading();
				    		MessageBox.alert(error_info);
				    	});
				    	return false;
		    		}
		    	});
			},
			
			afterSubmitAction : function(type){
		    	//alert(type);
		    },
		    
		    buildShoppingElements : function(orderSet) {
		    	if(orderSet && orderSet.length>0) {
		    		var length = orderSet.length;
					for(var i=0;i<length;i++) {
						var orderInfo = orderSet.get(i);
						orderInfo.put("CHECK_TAG", "0");
					}
		    	}
		    	return orderSet;
		    },
		    
		    getSubmitData:function(){
				var length = $.ShoppingCart.shoppingElements.length;
				var submitData = $.DatasetList();
				for(var i=0;i<length;i++){
					var element = $.ShoppingCart.shoppingElements.get(i);
					if(element.get("CHECK_TAG")=="0"){
						submitData.add(element);
					}
				}
				
				return submitData;
		    },
		    
		    getElementData:function(detailOrderId){
				var length = $.ShoppingCart.shoppingElements.length;
				var submitData = $.DatasetList();
				for(var i=0;i<length;i++){
					var element = $.ShoppingCart.shoppingElements.get(i);
					if(element.get("DETAIL_ORDER_ID") == detailOrderId){
						submitData.add(element);
					}
				}
				return submitData;
		    }
		}
	});
})(Wade);

$(document).ready(function(){
//	$.custInfo.setSerialNumber("18729222377");
//	$.custInfo.setOpenModel("0");
//	$.custInfo.setTradeTypeCode("10");
	
//	MessageBox.show({
//		success : true,
//		msg : "确认提示",
//		content : "<span class='e_red e_strong'>是否【注销该用户】或【继续办理业务】？</span>",
//		buttons : {"ok" : "继续办理", "cancel" : "注销用户"},
//		fn : function(btn){
//			if("cancel" == btn){
//				alert("注销用户");
////				if($.topPersonLogin){
////    				$.topPersonLogin.logout();
////    			}else
////    				$.cssubmit.closeMessage(succFlag);
//			} else if("ok" == btn){
//				alert("继续办理");
////				$.cssubmit.closeMessage(succFlag);
//			}
//		}
//	});
	
});

/**
var myDialog;
function testPop(){
	myDialog = popupDialog('规则管理', 'person.taskalarm.ManageRule', '', '', '/order/order', '800px', '400px', false, null, "");
	$(myDialog.titleFnClose).css("display", "none");
}
function closeDialog(){
	myDialog.hide();
}*/




