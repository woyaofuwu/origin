//重新计算违约金
var selectActiveHtmlElement;
//信用购机查询标识
var creditPurchasesFlag = true;
function checkBackTerm(obj){
	if(obj.checked){
		selectSaleActive(selectActiveHtmlElement,'1');
	}else{
		selectSaleActive(selectActiveHtmlElement,'0');
	}
}
function refreshPartAfterAuth(data) {
    var userInfo = data.get("USER_INFO");
    var param = "&USER_ID=" + userInfo.get("USER_ID")
            + "&EPARCHY_CODE=" + userInfo.get("EPARCHY_CODE")
            + "&SERIAL_NUMBER=" + userInfo.get("SERIAL_NUMBER");
    $.ajax.submit(null, "loadBaseTradeInfo", param, "saleActivePart,saleActiveDetailPart,userOperPart",
        function () {
            $.endPageLoading();
        },
        function (error_code, error_info) {
            $.endPageLoading();
            MessageBox.error(error_info);
        });
}

function getSelectedData(obj) {
    if (obj && typeof obj !== "undefined") {
        return saleActiveTable.getRowData(obj.attr("rowIndex") - 1);
    } else {
        if ($("#saleActiveTable tbody .on").length === 1) {
            return saleActiveTable.getSelectedRowData();
        } else {
            return new $.DataMap();
        }
    }
}

function selectSaleActive(obj,backTerm) {
	selectActiveHtmlElement=obj;
    if(typeof(backTerm)=='undefined'){
	   backTerm="0";
    }
    var $obj = $(obj);
    var authData = $.auth.getAuthData();
    var userInfo = authData.get("USER_INFO");
    var serialNumber = userInfo.get("SERIAL_NUMBER");
    $.feeMgr.clearFeeList("237");

    var saleActiveInfo = getSelectedData($obj);
    var packageId = saleActiveInfo.get("PACKAGE_ID");
    var productId = saleActiveInfo.get("PRODUCT_ID");
    var productName = saleActiveInfo.get("PRODUCT_NAME");
    var relationTradeId = saleActiveInfo.get("RELATION_TRADE_ID");

    var resultTipInfo = "";
    var flag = saleActiveInfo.get("PRODT_FLAG");
    if (flag === "0") {
        resultTipInfo = "您没有产品【" + productId + "|" + productName + "】终止权限！";
        MessageBox.alert(resultTipInfo, null,
            function () {
                $obj.removeClass("on");
            });
        return;
    }

    if (productId === "66000602") { // 宽带候鸟营销活动，不能提前终止
        resultTipInfo = "根据业务部门规定，该活动不能提前终止，如有疑问请咨询业务部门活动负责人！";
        MessageBox.alert(resultTipInfo, null,
            function () {
                $obj.removeClass("on");
            });
        return;
    }

    var param = "&SERIAL_NUMBER=" + serialNumber + "&PRODUCT_ID=" + productId
            + "&PACKAGE_ID=" + packageId
            + "&RELATION_TRADE_ID=" + relationTradeId
            + "&PRODUCT_MODE=" + saleActiveInfo.get("PRODUCT_MODE")
            + "&EPARCHY_CODE=" + userInfo.get("EPARCHY_CODE");
    $.beginPageLoading("活动校验中......");
    $.ajax.submit(null, "checkSaleActive", param, null,
        function (ajaxData) {
            /*var isReturnObj = $("#IS_RETURN");
            isReturnObj.attr("checked", false);
            isReturnObj.attr("disabled", true);*/
            // 应蔡世泳要求，去掉费用的处理
            /*var deposit = data.get(0,"DEPOSIT_MONEY");
            insertFee("237", "2", "102", deposit);
            var present = ajaxData.get(0,"PRESENT_MONEY");
            insertFee("237", "0", "601", present);*/
            param += "&USER_ID=" + userInfo.get("USER_ID")+"&BACK_TERM="+backTerm;
            $.ajax.submit(null, "loadActiveDetailInfo", param, "saleActiveDetailPart,userOperPart",
                function (detailData) {
            	  //加退还摄像头显示
	  		      var backTermShow = ajaxData.get(0,"BACK_TERM_SHOW");
	  		      if(backTermShow=="1"){
	  		    	  $("#BACK_TERM_LI").css("display", "block");
	  		      }else{
	  		    	  $("#BACK_TERM_LI").css("display", "none");
	  		      }
	  		      if(backTerm=='0'){
	  		    	  $('#BACK_TERM').attr("checked",false);
	  		      }else{
	  		    	  $('#BACK_TERM').attr("checked",true);
	  		      }
	  		      $('#BACK_TERM').val(backTerm);
                    /*if (detailData && detailData.length > 0) {
                        var saleGoods = detailData.get("SALE_GOODS");
                        for (var i = 0; i < saleGoods.length; i++) {
                            var resTypeCode = saleGoods.get(i, "RES_TYPE_CODE");
                            if (resTypeCode === "4") {
                                isReturnObj.val("0");
                                break;
                            }
                        }
                    }*/

                    // 获取应收违约金
                    var trueReturnFeeCostObj = $("#TRUE_RETURNFEE_COST");
                    var trueReturnFeePriceObj = $("#TRUE_RETURNFEE_PRICE");
                    var refundPriceFunc;

                    var refundData = detailData.get("REFUND_MONEY");
                    var resultTip = refundData.get("RESULT_TIP");
                    if (resultTip !== "0") {
                        var refundActiveFunc = detailData.get("REFUND_ACTIVE_FUNC");
                        if (refundActiveFunc === "1") {
                            refundPriceFunc = detailData.get("REFUND_PRICE_FUNC");
                            if (refundPriceFunc === "1") {
                                trueReturnFeeCostObj.attr("disabled", false);
                                trueReturnFeePriceObj.attr("disabled", false);
                            }
                        } else {
                            resultTipInfo = "根据业务部门规定，该活动不能提前终止，如有疑问请咨询业务部门活动负责人！或者申请：营销活动终止无配置活动终止权限（REFUNDACTIVEFUNC）再处理。";
                            MessageBox.alert(resultTipInfo, null,
                                function () {
                                    $obj.removeClass("on");
                                });
                        }
                    } else {
                        refundPriceFunc = detailData.get("REFUND_PRICE_FUNC");
                        if (refundPriceFunc === "1") {
                            trueReturnFeeCostObj.attr("disabled", false);
                            trueReturnFeePriceObj.attr("disabled", false);
                        }
                    }

                    /**
                     * 携号转网背景下吉祥号码业务规则优化需求（上）
                     * 吉祥号码营销活动不需要缴纳违约金的，要实现免收违约金功能，但需要配置特殊数据权限。
                     * trueReturnFeePriceObj
                     */
                    var beautyActivePriceFunc = detailData.get("BEAUTY_ACTIVE_PRICE_FUNC");
                    if (productId && productId === "69900703") {
                        trueReturnFeeCostObj.attr("disabled", true);
                        trueReturnFeePriceObj.attr("disabled", true);
                        if(beautyActivePriceFunc === "1"){
                            trueReturnFeePriceObj.attr("disabled", false);
                        }

                        //局方要求，违约金改为赔付金
                        // TRUE_RETURNFEE_COST/违约成本金
                        // TRUE_RETURNFEE_PRICE/违约金
                        // RETURNFEE/应收违约金
                        // TRUE_RETURNFEE/实收违约金
                        $("#TRUE_RETURNFEE_COST").parent().prev().html("赔付成本金");
                        $("#TRUE_RETURNFEE_PRICE").parent().prev().html("赔付金");
                        $("#RETURNFEE").parent().prev().html("应收赔付金");
                        $("#TRUE_RETURNFEE").parent().prev().html("实收赔付金");
                    }else {
                        $("#TRUE_RETURNFEE_COST").parent().prev().html("违约成本金");
                        $("#TRUE_RETURNFEE_PRICE").parent().prev().html("违约金");
                        $("#RETURNFEE").parent().prev().html("应收违约金");
                        $("#TRUE_RETURNFEE").parent().prev().html("实收违约金");
                    }
                    //END 携号转网背景下吉祥号码业务规则优化需求（上）

                    var refundMoney = refundData.get("REFUND_MONEY");
                    $("#RETURNFEE").val(refundMoney);
                    $("#TRUE_RETURNFEE").val(refundMoney);

                    var refundMoneyCost = refundData.get("REFUND_COST");
                    trueReturnFeeCostObj.val(refundMoneyCost);
                    var refundMoneyPrice = refundData.get("REFUND_PRICE");
                    trueReturnFeePriceObj.val(refundMoneyPrice);

                    $("#saleActiveDetailPart").css("display", "");
                    $.endPageLoading();

                    var isNeedWarm = ajaxData.get(0, "IS_NEED_WARM");
                    if (isNeedWarm === "1") {
                        var packageName = saleActiveInfo.get("PACKAGE_NAME");
                        resultTipInfo = "取消活动【" + productName + "(" + packageName + ")】后，将不能再参加！";
                        MessageBox.alert("告警提示", resultTipInfo);
                    }

                    var alertInfo = detailData.get("ALERT_INFO");
                    if (alertInfo.length > 0) {
                        MessageBox.alert(alertInfo);
                    }
                    
                    var creditPurchases = detailData.get("QUERY_CREDIT_PURCHASES");
                    if (creditPurchases === "0") {
                        MessageBox.alert("信用购机退货查询失败,请联系和包平台");
                        creditPurchasesFlag = false;
                    }else{
                    	 creditPurchasesFlag = true;
                    }
                },
                function (error_code, error_info) {
                    $.endPageLoading();
                    MessageBox.error(error_info);
                });
        },
        function (error_code, error_info) {
            $obj.attr("disabled", true).removeClass("on");
            $.endPageLoading();
            MessageBox.error(error_info);
        });
}

function insertFee(tradeTypeCode, feeMode, feeTypeCode, fee) {
    var feeData = new $.DataMap();
    feeData.put("TRADE_TYPE_CODE", tradeTypeCode);
    feeData.put("MODE", feeMode);
    feeData.put("CODE", feeTypeCode);
    feeData.put("FEE", fee);
    $.feeMgr.insertFee(feeData);
}

function addFeeList(obj) {
    var fee = $(obj).val();
    if (fee === "") {
        return false;
    }
    if (!$.isNumeric(fee)) {
        MessageBox.alert("费用输入非法！");
        return false;
    }

    var yearFee = fee * 100;
    $.feeMgr.removeFee("237", "0", "602");
    insertFee("237", "0", "602", yearFee);
}

function addFee() {
    var price = $("#TRUE_RETURNFEE_PRICE").val();
    var cost = $("#TRUE_RETURNFEE_COST").val();
    var total = parseFloat(price)+parseFloat(cost);
    $("#TRUE_RETURNFEE").val(total.toFixed(2));
}

function checkSubmitBefore() {
	
	if(!creditPurchasesFlag){
		 MessageBox.alert("信用购机退货查询失败,请联系和包平台");
		 return false;
	}
	/**
	 * REQ201805240036 终止营销活动收取违约金操作界面优化需求 by mengqx 2018-6-28
	 * @return
	 */
	var fee = $("#TRUE_RETURNFEE").val();
	MessageBox.confirm("费用提示","本次最终收取费用共"+ fee +"元，如金额无误请点击确认！",function(bt){
		if(bt=='ok'){
	var saleActiveInfo = getSelectedData();
	if (saleActiveInfo.length === 0) {
	    MessageBox.alert("请选择结束的活动后提交！");
	    return false;
	}
	
	if (!$.validate.verifyAll("userOperPart")) {
	    return false;
	}
	
	//加是否退还摄像头标识
    var backTerm="";
    if( $("#BACK_TERM_LI").css("display")=='block'){
 	   backTerm= $('#BACK_TERM').val();
    }
    
	var authData = $.auth.getAuthData();
	var userInfo = authData.get("USER_INFO");
	var param = "&SERIAL_NUMBER=" + userInfo.get("SERIAL_NUMBER")
	        + "&EPARCHY_CODE=" + userInfo.get("EPARCHY_CODE")
	        + "&PRODUCT_ID=" + saleActiveInfo.get("PRODUCT_ID")
	        + "&PACKAGE_ID=" + saleActiveInfo.get("PACKAGE_ID")
	        + "&RELATION_TRADE_ID=" + saleActiveInfo.get("RELATION_TRADE_ID")
	        + "&CAMPN_TYPE=" + saleActiveInfo.get("CAMPN_TYPE")
	        + "&END_DATE_VALUE=" + $("#END_DATE_VALUE").val()
	        + "&REMARK=" + $("#REMARK").val()
	        /*+ "&IS_RETURN=" + $("#IS_RETURN").val()*/
	        + "&RETURNFEE=" + $("#TRUE_RETURNFEE").val()
	        + "&YSRETURNFEE=" + $("#RETURNFEE").val()
	        + "&TRUE_RETURNFEE_COST=" + $("#TRUE_RETURNFEE_COST").val()
	        + "&TRUE_RETURNFEE_PRICE=" + $("#TRUE_RETURNFEE_PRICE").val()
	        + "&RSRVSTR6=" + $("#RSRVSTR6").val()
	        + "&BACK_TERM="+backTerm;
	
	$.cssubmit.addParam(param);
	$.cssubmit.submitTrade();//提交台账
			}
		}); 
}