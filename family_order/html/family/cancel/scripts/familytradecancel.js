$(document).ready(function() {
	var flag = false;// 测试开关
	if (flag) {
		var login = window.parent.$.login;
		if (login && login.isLogin) {
			$("#SERIAL_NUMBER").val(login.getAccessNum());
		} else {
			MessageBox.alert("提示信息", "请先在外框登录认证客户信息！", function() {
				closeNavByTitle(getNavTitle());
			});
		}
	} else {
		$("#SERIAL_NUMBER").attr("readonly", false);
	}
});


function getUnfiniedTradeList() {
	if (!$.validate.verifyAll("QueryCondPart")) {
		return false;
	}
	
	$.beginPageLoading("工单信息查询中...");
	ajaxSubmit("QueryCondPart", "getUserUnfinishedTradeList", null, "UnfinishedTradeTablePart,CancelTradePart",
	    function(data){
			hidePopup("mypop");
			CancelTradeTabset.switchToByTitle("订单列表");
    		if (data == null || data.length == 0) {
    			MessageBox.alert("提示信息", "该用户没有可以撤销的工单数据！")
    		}
    		$.endPageLoading();
	    },
		function(error_code, error_info){
		    alert(error_code + ":" + error_info);
		    $.endPageLoading();
		}
    );
}


var feeList;//订单费用集合

function getCancelTradeDetails(domObj) {
	if (!$.validate.verifyAll("QueryCondPart")) {
		return false;
	}
	
	var rowIndex = $(domObj).attr("rowId");
	var orderId = UnfinishedTradeTable.getRowData(rowIndex).get("COL_ORDER_ID");
	
	$("#OLD_ORDER_ID").val(orderId);
	
	var param = "&SERIAL_NUMBER=" + $("#SERIAL_NUMBER").val()
			  + "&ORDER_ID=" + orderId;
	
	$.beginPageLoading("订单信息查询中...");
	ajaxSubmit(null, "getCancelTradeDetails", param, "CancelTradePart,RefreshHiddenPart,Condition",
	    function(data){
    		if (!data.get("CANCEL_TRADE_LIST") || data.get("CANCEL_TRADE_LIST").length == 0) {
    			MessageBox.alert("提示信息", "数据异常，没有ORDER_ID[" + orderId + "]的订单信息！");
    		} else {
    			CancelTradeTabset.switchToByTitle("订单详情");
    			
    			normalCancelChooseRule();
    			if(data.get("IS_OVERMONTH")==1){
    				MessageBox.alert("提示信息", "产品变更已跨月生效，手机产品已生效无法返销，宽带相关可以撤单！");
    			}
    			feeList = data.get("FEE_LIST");
    			$.cssubmit.disabledSubmitBtn(false, "submitButton");
    		}
	        $.endPageLoading();	       
	    },
		function(error_code, error_info){
		    $.endPageLoading();
		    alert(error_code + ":" + error_info);
		}
    );
}



function disabledCheckbox(flag, checkbox){
    if (!checkbox.length) {
    	return;
    }
    
    if (flag == true) {
    	checkbox.attr("disabled", true).addClass("e_dis");
    } else {
    	checkbox.attr("disabled", false).removeClass("e_dis");
    }
}



function normalCancelChooseRule() {
	var rootTradeType = $("td[levelNum='1']").attr("tip");
	$("#CANCEL_ORDER_TYPE").val(rootTradeType);
	
	// 1. 禁用所有的checkbox
	disabledCheckbox(true, $(":checkbox[name='TradeCheckbox']"));
	
	// 2. 激活未完工的可以撤销的业务类型的checkbox
	disabledCheckbox(false, $("td[tip='101'][finishFlag='0']").find(":checkbox"));
	disabledCheckbox(false, $("td[tip='261'][finishFlag='0']").find(":checkbox"));
	disabledCheckbox(false, $("td[tip='64'][finishFlag='0']").find(":checkbox"));
	disabledCheckbox(false, $("td[tip='65'][finishFlag='0']").find(":checkbox"));
	disabledCheckbox(false, $("td[tip='103'][finishFlag='0']").find(":checkbox"));
	
	// 3. 如果有任意长流程工单完工，则不允许全部撤销！
	if ($("td[tip='101'][finishFlag='1']").length == 0
			&& $("td[tip='261'][finishFlag='1']").length == 0
			&& $("td[tip='64'][finishFlag='1']").length == 0
			&& $("td[tip='65'][finishFlag='1']").length == 0
			&& $("td[tip='103'][finishFlag='1']").length == 0) {
		disabledCheckbox(false, $("td[tip='301']").find(":checkbox"));
	}
	
	treeNodeBindEvents(rootTradeType);
}


function treeNodeBindEvents(tradetype) {
	$(":checkbox[name='TradeCheckbox']:enabled").bind("click", function() {
		var isChecked = $(this).attr("checked");
		childrenTreeNodeOper($(this), isChecked);
		
		if (tradetype == "301" && $("td[tip='301']").find("input:checked").length == 0) {
			checkRoleCountLimit($(this));
		}
		
		if ($(this).parent().parent().attr("tip") == "301" && $(this).attr("checked") == true) {
			MessageBox.confirm("确认信息", "您选择撤销全部融合订单，请确认！", function(btn){
				if (btn == "ok") {
					$(":checkbox[name='TradeCheckbox']").attr("checked", true);
					disabledCheckbox(true, $(":checkbox[name='TradeCheckbox']"));
				} else if (btn == "cancel") {
					$(":checkbox[name='TradeCheckbox']").attr("checked", false);
				}
				
			});
		}
	});
}


function childrenTreeNodeOper(eventObj, isChecked) {
	var children = getTreeNodeChildren(eventObj);
	
	if (children != null && children.length != 0) {
		$.each(children, function(i, item) {
			item.attr("checked", isChecked);
			var tradeTypeSpecial = item.parent().parent().attr("tip");
			if (tradeTypeSpecial == "261" 
				|| tradeTypeSpecial == "64"
				|| tradeTypeSpecial == "103"
				|| tradeTypeSpecial == "65") {
				if (isChecked) {
					disabledCheckbox(true, item);
				} else {
					disabledCheckbox(false, item);
				}
			}
		});
	}
}

// 当选中一个节点时，计算该节点的角色选中数量，如果小于该角色的最小数，则提示
// 如果该角色中有已经完工的，则不能全部撤销，需要人工干预
function checkRoleCountLimit(eventObj) {
	// 宽带成员总数
	var widenetRoleCount = $("td[tip='302'][roleCode='2']").length;
	var widenetCreate = $("td[tip='101']");
	if (widenetCreate && widenetCreate.length > 0) {
		var widenetCancelCount = widenetCreate.find("input:checked").length;
		var widenetCountLeft = widenetRoleCount - widenetCancelCount;
		var widenetMinCount = $("#FUSION_WIDENET_MIN").val();
		if (widenetCountLeft - widenetMinCount < 0) {
			var widenetFinishedCount = $("td[tip='302'][roleCode='2'][finishFlag='1']").length;
			// 同类成员中已经有完工的
			if (widenetFinishedCount > 0) {
				MessageBox.error("错误信息", "您订购的融合宽带成员数已经小于最小宽带成员数限制，不能撤销，需要人工干预处理！", function(btn){
					eventObj.attr("checked", false);
					childrenTreeNodeOper(eventObj, false);
				});
			} else {
				MessageBox.confirm("确认信息", "您订购的融合宽带成员数已经小于最小宽带成员数限制，必须撤销全部融合订单，请确认！", function(btn){
					if (btn == "ok") {
						// 如果确认，则选中所有订单，禁用所有复选框
						$(":checkbox[name='TradeCheckbox']").attr("checked", true);
						disabledCheckbox(true, $(":checkbox[name='TradeCheckbox']"));
					} else if (btn == "cancel") {
						// 如果取消，则放弃选中Checkbox
						eventObj.attr("checked", false);
						childrenTreeNodeOper(eventObj, false);
					}
					
				});
			}
		}
	}
}


/**
 * 获取当前操作节点的子节点
 */
function getTreeNodeChildren(treeNode) {
	var children = [];
	var trObj = treeNode.parent().parent().parent();
	var tdLevel = trObj.children(":first").attr("levelNum");
	trObj.nextAll().each(function(i, item) {
		var firstTd = $(item).children(":first");
		if (firstTd.attr("levelNum") > tdLevel) {
			children.push(firstTd.find(":checkbox"));
		} else {
			return false;//break;
		}
	});
	
	return children;
}


/**
 * 获取当前操作节点的父节点
 */
function getTreeNodeParent(treeNode) {
	var parent = [];
	var trObj = treeNode.parent().parent().parent();
	var tdLevel = trObj.children(":first").attr("levelNum");
	trObj.prevAll().each(function(i, item) {
		var firstTd = $(item).children(":first");
		if (firstTd.attr("levelNum") < tdLevel) {
			parent.push(firstTd.find(":checkbox"));
			return false;
		}
	});
	return parent;
}


function causeTypeChangeAction() {
	var causeTypeVal = $("#CANCEL_CAUSE_TYPE").val();
	if (causeTypeVal != '5') {
		$("#SelectCausesPart").css("display", "");
		$("#CANCEL_MAIN_CAUSE").attr("nullable", "no");
		$("#CUSTOM_SECOND_LEVEL_CAUSE").css("display", "none");
		$("#CANCEL_OTHER_CAUSE").attr("nullable", "yes").val("");
	} else {
		$("#SelectCausesPart").css("display", "none");
		$("#CANCEL_MAIN_CAUSE").attr("nullable", "yes").val("");
		$("#CUSTOM_SECOND_LEVEL_CAUSE").css("display", "");
		$("#CANCEL_OTHER_CAUSE").attr("nullable", "no");
		return;
	}
	$.beginPageLoading("数据查询中...");
	ajaxSubmit(null, "getMainCauseList", "&CANCEL_CAUSE_TYPE=" + causeTypeVal, "SelectCausesPart",
		function(data) {
			$.endPageLoading();
		},
		function(error_code, error_info) {
			alert(error_code + ":" + error_info);
			$.endPageLoading();
		}
	);
}


function checkBeforeSubmit() {
	if (!$.validate.verifyAll("QueryCondPart")) {
		return false;
	}
	
	if (!$.validate.verifyAll("CancelCausePart")) {
		return false;
	}
	
	var cancelTradeInfos = CancelTradeTable.getCheckedRowsData("TradeCheckbox");
	if (!cancelTradeInfos || cancelTradeInfos.length == 0) {
		MessageBox.alert("提示信息", "请选择表格中需要撤销的订单数据！");
		return false;
	}
	
	//重新提交清空所有费用
	$.feeMgr.clearFeeList();
	cancelTradeInfos.each(function(item, index) {
		var tradeId = item.get("COL_TRADE_ID");
		for (var i = 0; i < feeList.length; i++) {
			var feeData = feeList.get(i);
			var feeTradeId = feeData.get("PAY_TRADE_ID");
			if (feeTradeId == tradeId) {
				if (feeData.get("FEE_MODE") != "2" || item.get("COL_FINISH_FLAG") != "1") {
					var fee = $.DataMap();
					fee.put("MODE", feeData.get("FEE_MODE"));
					fee.put("FEE", parseInt(feeData.get("FEE")) * (-1));
					fee.put("CODE", feeData.get("FEE_TYPE_CODE"));
					fee.put("TRADE_TYPE_CODE", feeData.get("TRADE_TYPE_CODE"));
					fee.put("PAY_TRADE_ID", feeData.get("PAY_TRADE_ID"));
					$.feeMgr.insertFee(fee);
				}
			}
		}
	});
	
	var submitTableData = CancelTradeTable.getCheckedRowsData("TradeCheckbox","COL_TRADE_ID,COL_TRADE_TYPE_CODE,COL_FINISH_FLAG,COL_SERIAL_NUMBER");
	var param = "CANCEL_TRADE_LIST=" + submitTableData;	
	$.cssubmit.addParam(param);
	$.cssubmit.bindCallBackEvent(afterSubmitSuccess);
	return true;
}

function afterSubmitSuccess(data) {
	var undoStr = ""; 
	if (data.get(0).get("UNDO_ORDER_ID") != null) {
		undoStr = "</br>客户返销订单标识：" + data.get(0).get("UNDO_ORDER_ID");
	}
	
	var msg = "客户撤单订单标识：" + data.get(0).get("CANCEL_ORDER_ID") 
					+ undoStr + "</br>点【确定】继续业务受理。"
	MessageBox.success('业务受理成功', msg, $.cssubmit.resetTrade);
}


