
$(document).ready(function () {
    // 已订购业务分页标签切换事件
    $("#subscriptionTab").afterSwitchAction(function (e, idx) {
        if (idx === 0) {
            subscriptionTabQuery("discntTab");
        } else if (idx === 1) {
            subscriptionTabQuery("svcTab");
        } else if (idx === 2) {
            subscriptionTabQuery("platSvcTab");
        } else if (idx === 3) {
            subscriptionTabQuery("saleActiveTab");
        }
    });
   
    var getParentVule = window.parent.toChildValue();
    loadFnNavButtons();
    loadTabInfo("custInfoPart,productPart",undefined,getParentVule);
    subscriptionTab.switchTo(0);
    acctInfoQuery("billTablePart");
    acctInfoQuery("allowancePart");
});

// 查询责任人、使用人、经办人和携转信息
function extraCustInfoQuery(refreshPart) {
    var $queryFlag = $("#QUERY_EXTRA_USER_INFO_FINISHED");
    if ($queryFlag.val() === "0") {
        var param = "&SERIAL_NUMBER=" + parent.$("#SERIAL_NUMBER").val()
            + "&USER_ID=" + parent.$("#USER_ID").val()
            + "&CUST_ID=" + parent.$("#CUST_ID").val()
            + "&EPARCHY_CODE=" + parent.$("#EPARCHY_CODE").val()
            + "&NORMAL_USER_CHECK=" + parent.$("#NORMAL_USER").val();

        $("#extraLoading").css("display", "");
        $.ajax.submit(null, "extraCustInfoQuery", param, refreshPart,
            function () {
                $("#extraLoading").css("display", "none");
                $("#extraCustInfoPart").css("display", "");
                $queryFlag.val("1");
            },
            function () {
                $("#extraLoading").css("display", "none");
                $("#extraError").css("display", "");
            });
    } else {
        $("#extraCustInfoPart").css("display", "");
    }
}

// 已订购业务分页标签切换查询
function subscriptionTabQuery(refreshPart) {
    var tagName = refreshPart.substr(0, refreshPart.lastIndexOf("Tab")),
        $queryFlag = $("#QUERY_" + tagName.toUpperCase() + "_INFO_FINISHED");
    if ($queryFlag.val() === "0") {
        var param = "&SERIAL_NUMBER=" + parent.$("#SERIAL_NUMBER").val()
            + "&USER_ID=" + parent.$("#USER_ID").val()
            + "&EPARCHY_CODE=" + parent.$("#EPARCHY_CODE").val()
            + "&TAB_NAME=" + refreshPart;

        $("#" + tagName + "Loading").css("display", "");
        $.ajax.submit(null, "subscriptionTabQuery", param, refreshPart,
            function () {
                $("#" + tagName + "Loading").css("display", "none");
                $queryFlag.val("1");
            },
            function () {
                $("#" + tagName + "Loading").css("display", "none");
                $("#" + tagName + "Error").css("display", "");
            });
    }
}

// 近三月账单查询和当月套餐使用量查询
function acctInfoQuery(refreshPart) {
    var param = "&SERIAL_NUMBER=" + parent.$("#SERIAL_NUMBER").val()
        + "&USER_ID=" + parent.$("#USER_ID").val()
        + "&EPARCHY_CODE=" + parent.$("#EPARCHY_CODE").val()
        + "&QUERY_AREA=" + refreshPart;

    $.ajax.submit(null, "acctInfoQuery", param, refreshPart,
        function () {
            if (refreshPart === "billTablePart") {
                $("#billLoading").css("display", "none");
            } else if (refreshPart === "allowancePart") {
                $("#allowanceLoading").css("display", "none");
            }
            $("#" + refreshPart).css("display", "");
        },
        function () {
            if (refreshPart === "billTablePart") {
                $("#billLoading").css("display", "none");
                $("#billError").css("display", "");
            } else if (refreshPart === "allowancePart") {
                $("#allowanceLoading").css("display", "none");
                $("#allowanceError").css("display", "");
            }
        })
}

// 刷新顶部客户名称
function fillUpCustNameForHomeTab(ajaxData) {
    if (ajaxData && ajaxData.length > 0) {
        var custName = ajaxData.get("CUST_NAME");
        parent.$("#CUST_NAME_DIV").html(custName);
        parent.$("#PHONE_CUST_NAME_DIV").html(custName);
    }
}

// 展示客户额外信息
function showExtraCustInfo(el) {
    extraCustInfoQuery("extraCustInfoPart");
    $(el).parent().css("display", "none").prev().css("display", "");
}

// 隐藏客户额外信息
function hideExtraCustInfo(el) {
    $("#extraCustInfoPart").css("display", "none");
    $("#extraLoading").css("display", "none");
    $("#extraError").css("display", "none");
    $(el).parent().css("display", "none").next().css("display", "");
}

// 刷新客户额外信息
function refreshExtraArea() {
    $("#extraError").css("display", "none");
    extraCustInfoQuery("extraCustInfoPart");
}

// 刷新优惠标签
function refreshDiscntTab() {
    $("#discntError").css("display", "none");
    subscriptionTabQuery("discntTab");
}

// 刷新服务标签
function refreshSvcTab() {
    $("#svcError").css("display", "none");
    subscriptionTabQuery("svcTab");
}

// 刷新平台业务标签
function refreshPlatSvcTab() {
    $("#platSvcError").css("display", "none");
    subscriptionTabQuery("platSvcTab");
}

// 刷新活动标签
function refreshSaleActiveTab() {
    $("#saleActiveError").css("display", "none");
    subscriptionTabQuery("saleActiveTab");
}

// 刷新"我的消费"区域
function refreshBillArea() {
    $("#billLoading").css("display", "");
    $("#billError").css("display", "none");
    acctInfoQuery("billTablePart");
}

// 刷新"我的余量"区域
function refreshAllowanceArea() {
    $("#allowanceLoading").css("display", "");
    $("#allowanceError").css("display", "none");
    acctInfoQuery("allowancePart");
}