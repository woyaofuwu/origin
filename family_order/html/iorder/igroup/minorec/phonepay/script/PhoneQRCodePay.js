var gloabObj = "";

function queryInfos(el) {

    var param = "";
    if($("#cond_GROUP_ID").val()){
        param += "&GROUP_ID="+$("#cond_GROUP_ID").val();
    }else{
        $.validate.alerter.one(document.getElementById("cond_GROUP_ID"), "集团编码不能为空！");
        return false;
    }
    if($("#cond_PRODUCT_ID").val()){
        param += "&PRODUCT_ID="+$("#cond_PRODUCT_ID").val();
    }/*else{
        $.validate.alerter.one(document.getElementById("cond_PRODUCT_ID"), "所选产品不能为空！");
        return false;
    }*/

    if($("#cond_SERIAL_NUMBER").val()){
        param += "&SERIAL_NUMBER="+$("#cond_SERIAL_NUMBER").val();
    }

    if($("#cond_ACCT_ID").val()){
        param += "&ACCT_ID="+$("#cond_ACCT_ID").val();
    }

    $.beginPageLoading("数据查询中......");
    $.ajax.submit("", "queryInfos", param, "queryPart", function(data){
            $.endPageLoading();
            hidePopup(el);
        },
        function(error_code,error_info,derror){
            $.endPageLoading();
            showDetailErrorInfo(error_code,error_info,derror);
        });
}

function showQryPart(str) {
    if(str == "order"){
        $("#queryInfo").css("display","none");
        $("#queryOrder").css("display","");
    }else{
        $("#queryInfo").css("display","");
        $("#queryOrder").css("display","none");
    }

    showPopup('qryPopup2','infoItem');
}

function queryOrderInfos(obj) {

    var param = "";
    if($("#cond_GROUP_ID").val()){
        param += "&GROUP_ID="+$("#cond_GROUP_ID").val();
    }else{
        $.validate.alerter.one(document.getElementById("cond_GROUP_ID"), "集团编码不能为空！");
        return false;
    }
    if($("#cond_PRODUCT_ID").val()){
        param += "&PRODUCT_ID="+$("#cond_PRODUCT_ID").val();
    }/*else{
        $.validate.alerter.one(document.getElementById("cond_PRODUCT_ID"), "所选产品不能为空！");
        return false;
    }*/

    if($("#cond_SERIAL_NUMBER").val()){
        param += "&SERIAL_NUMBER="+$("#cond_SERIAL_NUMBER").val();
    }

    if($("#cond_ACCT_ID").val()){
        param += "&ACCT_ID="+$("#cond_ACCT_ID").val();
    }

    $.beginPageLoading("数据查询中......");
    $.ajax.submit("", "queryOrderInfos", param, "queryOrderPart", function(data){
            $.endPageLoading();
            hidePopup(obj);
        },
        function(error_code,error_info,derror){
            $.endPageLoading();
            showDetailErrorInfo(error_code,error_info,derror);
        });

}

function queryCustInfoByName() {
    var param = "";
    if($("#cond_CUST_NAME").val()){
        param += "&CUST_NAME="+$("#cond_CUST_NAME").val();
    }else{
        $.validate.alerter.one(document.getElementById("cond_CUST_NAME"), "请输入集团名称！");
        return false;
    }

    $.beginPageLoading("数据查询中......");
    $.ajax.submit("", "queryCustInfoByName", param, "QueryCustInfo", function(data){
            $.endPageLoading();
        },
        function(error_code,error_info,derror){
            $.endPageLoading();
            showDetailErrorInfo(error_code,error_info,derror);
        });
}

function setReturnValue(el) {
    var div = $(el);
    var groupId = div.attr("groupId");
    var custName = div.attr("custName");
    $("#cond_GROUP_ID").val(groupId);
    $("#cond_GROUP_NAME").val(custName);
    hidePopup(el);

}

function cancelTrade(el) {
    var butten = $(el);
    var tradeId = butten.attr("tradeId");
    var orderId = butten.attr("orderId");
    var param = "&TRADE_ID="+tradeId+"&ORDER_ID="+orderId;
    MessageBox.confirm("温馨信息", "是否确定取消订单？", function(btn){
        if(btn == "ok"){
            $.beginPageLoading("数据提交中......");
            $.ajax.submit("", "cancelTrade", param, "", function(data){
                    $.endPageLoading();
                    MessageBox.success("提交成功", "订单已取消", function(btn){
                        queryOrderInfos();
                    });
                },
                function(error_code,error_info,derror){
                    $.endPageLoading();
                    showDetailErrorInfo(error_code,error_info,derror);
                }
            );
        }
    });

}

function doPayTrade(el) {
    var td = $(el);
    $("#SERIAL_NUMBER").val(td.attr("serialNumber"));
    $("#ACCT_ID").val(td.attr("acctId"));

    $("#PAY_NO_ORDER").css("display","");
    $("#PAY_ORDER").css("display","none");

    $("#PAY_NUM").val("");
    $("#PAY_NUM").removeAttr("disabled");

    showPopup("qryPopup3","PayItem",true);
}

function doPayThisTrade(el){
    var butten = $(el);
    var fee = butten.attr("fee");
    //换算费用
    if(fee){
        fee = (fee/100).toFixed(2);
    }

    var tradeId = butten.attr("tradeId");
    var orderId = butten.attr("orderId");
    var serialNumber = butten.attr("serialNumber");
    var acctId = butten.attr("acctId");

    $("#SERIAL_NUMBER").val(serialNumber);
    $("#ACCT_ID").val(acctId);
    $("#PAY_NUM").val(fee);
    $("#PAY_NUM").attr("disabled","disabled");
    $("#TRADE_ID").val(tradeId);
    $("#ORDER_ID").val(orderId);

    $("#PAY_NO_ORDER").css("display","none");
    $("#PAY_ORDER").css("display","");

    showPopup("qryPopup3","PayItem",true);

}

function submitOrderPay(el) {
    if(!$.validate.verifyAll("PayAcctInfo")){
        return false;
    }

    var param = "&FEE="+$("#PAY_NUM").val()+"&TRADE_ID="+$("#TRADE_ID").val()
        +"&ORDER_ID="+$("#ORDER_ID").val()+"&SERIAL_NUMBER="+$("#SERIAL_NUMBER").val();

    $.beginPageLoading("数据提交中......");
    $.ajax.submit("", "doPayThisTrade", param, "", function(data){
            $.endPageLoading();
            data.put("PAY_TYPE",$("#PAY_TYPE").val());
            gloabObj = data;
            goToPayMain(data);

        },
        function(error_code,error_info,derror){
            $.endPageLoading();
            showDetailErrorInfo(error_code,error_info,derror);
        }
    );

}

function submitPay(el) {
    if(!$.validate.verifyAll("PayAcctInfo")){
        return false;
    }

    if(!$.isNumeric($("#PAY_NUM").val())){
        MessageBox.alert("支付金额填写不正确，请重新填写！");
        return false;
    }

    $.beginPageLoading("数据提交中......");
    $.ajax.submit("PayAcctInfo", "submitPay", "", "", function(data){
            $.endPageLoading();
            data.put("PAY_TYPE",$("#PAY_TYPE").val());
            gloabObj = data;
            //MessageBox.success("提交成功", "订单号为："+data.get("PEER_ORDER_ID"), function(btn){
            goToPayMain(data);
            //});

        },
        function(error_code,error_info,derror){
            $.endPageLoading();
            showDetailErrorInfo(error_code,error_info,derror);
        }
    );
}

function goToPayMain(data) {
    var orderId = data.get("ORDER_ID");
    var peerOrderId = data.get("PEER_ORDER_ID");
    var payType = data.get("PAY_TYPE","");
    var param ="&ORDER_ID="+orderId+"&PEER_ORDER_ID="+peerOrderId+"&DEFAULT_PAYTYPE="+payType+"&PAYONLY=ONLY";
    if($.os.phone){
        var popupId= popupPage("支付收银", "pay.order.PhonePayMain","queryOrderInfo", param+'&PARENT_EVENT_ID=_PAY_FEE_ID', subsys_cfg.payment, "c_popup c_popup-full", null, null);
        $('#_PAY_FEE_ID').val(popupId);

    }else{
        var popupId= popupPage("支付收银", "pay.order.PayMain","queryOrderInfo", param+'&PARENT_EVENT_ID=_PAY_FEE_ID', subsys_cfg.payment, "c_popup c_popup-full", null, null);
        $('#_PAY_FEE_ID').val(popupId);
    }
}

function updatePayState(result) {
    if (typeof (result) != "undefined") {
        var payresultMap = $.DataMap(result);
        var state = payresultMap.get("STATE");
        if (state == "2") {
            var obj = gloabObj;
            var param = "&ORDER_ID=" + obj.get("PEER_ORDER_ID");
            param += "&TRADE_ID=" + obj.get("TRADE_ID");
            param += "&SERIAL_NUMBER=" + obj.get("SERIAL_NUMBER");
            param += "&TRADE_EPARCHY_CODE=" + obj.get("TRADE_EPARCHY_CODE");
            param += "&PAY_DETAIL=" + payresultMap.get("PAY_DETAIL");

            ajaxSubmit('', 'payTrade', param, '', function(data) {
                if (data && data.get("RESULT") == "SUCCESS") {
                    //doPrintReceipt(obj);
                    gloabObj = null;
                }
                $.endPageLoading();
                MessageBox.success("提示信息", "支付成功", function(btn){
                    closeNav();
                });
            }, function(code, info, detail) {
                $.endPageLoading();
                MessageBox.error(code, info);
            }, {
                async : false
            });
        } else {
            MessageBox.alert("支付失败");
        }
    }
}

function setCheckedToValue(el){
    var vals = '';
    var texts = '';
    var num = 0;
    $("#PRODUCT_TREE input[name]").each(function(){
        if(this.checked){
            var val  = this.getAttribute("value");
            var text = $(this).parent().siblings("div.text").attr("title");
            if(num > 0){
                vals = vals + ",";
                texts = texts + ',';
            }
            num++;
            vals = vals + val;
            texts = texts + text;
        }

    });

    $("#cond_PRODUCT_ID").val(vals);
    $("#POP_PRODUCT_ID").val(texts);
    hidePopup(el);
}