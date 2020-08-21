function dealInfo(){
    var refuseRuleList = $("#REFUSE_RULE_LIST").val();
    var tpRuleList = $("#TP_RULE_LIST").val();
    var unTpRuleList = $("#UNTP_RULE_LIST").val();
    var accessNumber = $("#ACCESS_NUMBER").val();
    var custName = $("#CUST_NAME").val();
    var tradeTypeCode = $("#TRADE_TYPE_CODE").val();

    if(refuseRuleList != '' && refuseRuleList != undefined && refuseRuleList != '[]'){
        MessageBox.alert("存在阻断性规则，无法进行甩单提交！");
        return false;
    }else if(unTpRuleList != '' && unTpRuleList != undefined && unTpRuleList != '[]'){
        MessageBox.alert("存在未完成跳转规则，请先点击连接完成规则处理后重试！");
        return false;
    }else{
        $.beginPageLoading("正在处理数据...");
        //播记录查询
        var param = '&TP_RULE_LIST='+encodeURI(encodeURI(tpRuleList)) + '&ACCESS_NUMBER=' + accessNumber +
            '&CUST_NAME=' + encodeURI(encodeURI(custName)) + '&TRADE_TYPE_CODE=' + tradeTypeCode;
        $.ajax.submit('','submit', param, '', function(data){
                $.endPageLoading();
                var tpOrderIds = window.parent.document.getElementById("TP_ORDER_IDS");
                tpOrderIds.val(data.get("ORDER_IDS"));
                MessageBox.sucess("成功","甩单成功，甩单单号分别是："+data.get("ORDER_IDS")+"!", function(btn){
                    backPopup(this);
                });
            },
            function(error_code,error_info){
                $.endPageLoading();
                MessageBox.error("错误信息",error_info);
            });
    }
}

function toPage(obj) {
    var url = $(obj).attr("url");
    var title = $(obj).attr("menuTitle");
    if(url == undefined || url == ''){
        $.MessageBox.alert("未获取到跳转页面地址。");
        return;
    }else if(url == '无权访问'){
        $.MessageBox.alert("您无权访问此页面。");
        return;
    }
    openNavByUrl(title, url);
}

function refushRuleList() {
    //获取父页面的某个元素
    var node = window.parent.document.getElementById("AUTH_SUBMIT_BTN");
    //调用该元素的Click事件
    node.click();
}

function reflushPage(){
    var href = window.parent.location.href;
    if(href){
        if(href.lastIndexOf("#nogo") == href.length-5){
            href = href.substring(0, href.length-5);
        }
        var url = href.substring(0, href.indexOf("?"));
        var reqParam = href.substr(href.indexOf("?")+1);

        var paramObj = $.params.load(reqParam);
        paramObj.remove("DISABLED_AUTH");
        paramObj.remove("AUTO_AUTH");
        paramObj.remove("SERIAL_NUMBER");
        var param = paramObj.toString();
        if(param.indexOf("SERIAL_NUMBER") == -1 && $("#AUTH_SERIAL_NUMBER").length){
            param += "&SERIAL_NUMBER="+$("#AUTH_SERIAL_NUMBER").val();
        }
        param += "&AUTO_AUTH=false";
        window.parent.location.href = url+"?"+param;
        backPopup(this);
    }
}