
$(document).ready(function () {
    $("#scoreInfoTab").afterSwitchAction(function (e, idx) {
        if (1 === idx && "0" === $("#INIT_ISSUE_FLAG").val()) {
            initScoreTab("scoreIssueParam");
        } else if (2 === idx && "0" === $("#INIT_ACCUMULATE_FLAG").val()) {
            initScoreTab("scoreAccumulateParam");
        } else if (3 === idx && "0" === $("#QUERY_EXCHANGE_FLAG").val()) {
            queryScoreInfoFilter();
        }

        adjustTables(idx);
    });

    loadFnNavButtons();
    initScoreTab("scoreTradeParam");
});

// "家庭积分"分页标签初始化方法
function initScoreTab(partid) {
    var idx = -1 === scoreInfoTab.activeIdx ? 0 : scoreInfoTab.activeIdx,
        param = "&SERIAL_NUMBER=" + parent.$("#SERIAL_NUMBER").val()
            + "&TABINDEX=" + idx,
        tabName = $("#scoreInfoTab > .page > .content:eq(" + idx + ")").attr("title");

    $.beginPageLoading(tabName + "标签分页初始化。。。");
    $.ajax.submit(null, "initScoreQuery", param, partid,
        function () {
            $.endPageLoading();
            $("#" + partid + " button").tap(queryScoreInfoFilter);
            if (0 === idx) {
                getUserValidScore();
            } else if (1 === idx) {
                $("#INIT_ISSUE_FLAG").val("1");
            } else if (2 === idx) {
                $("#INIT_ACCUMULATE_FLAG").val("1");
            }
        },
        function (error_code, error_info) {
            $.endPageLoading();
            MessageBox.error(error_info);
        });
}

// 从"客户资料综合查询"外框获取用户积分
function getUserValidScore() {
    var score = parent.$("#FAMILY_SCORE").html();
    $("#validScore").html(score);
}

function queryScoreInfoFilter() {
    var idx = scoreInfoTab.activeIdx,
        tabName = $("#scoreInfoTab > .tab li:eq(" + idx + ")").html();

    if (0 === idx) { // 查询用户积分交易历史
        queryScoreInfo(tabName, "scoreTradeParam", "queryScoreTradeHistory", "scoreTradePart");
    } else if (1 === idx) { // 查询用户积分里程明细
        queryScoreInfo(tabName, "scoreIssueParam", "queryScoreIssueDetail", "scoreIssuePart");
    } else if (2 === idx) { // 查询用户年度累计积分
        queryScoreInfo(tabName, "scoreAccumulateParam", "queryScoreYearAccumulate", "scoreAccumulatePart");
    } else if (3 === idx) { // 查询用户年度可兑换积分
        queryScoreInfo(tabName, null, "queryScoreYearExchange", "scoreExchangePart");
    }
}

// 分页标签查询公共方法
function queryScoreInfo(loading, areaid, listener, partid) {
    var param = "&FAMILY_USER_ID=" + parent.$("#FAMILY_USER_ID").val()
        + "&USER_ID=" + parent.$("#FAMILY_USER_ID").val()
        + "&CUST_ID=" + parent.$("#CUST_ID").val()
        + "&EPARCHY_CODE=" + parent.$("#EPARCHY_CODE").val();

    if ("scoreTradeParam" !== areaid)
        param += "&SERIAL_NUMBER=" + parent.$("#SERIAL_NUMBER").val();

    $.beginPageLoading(loading + "查询中。。。");
    $.ajax.submit(areaid, listener, param, partid,
        function (ajaxData) {
            $.endPageLoading();
            var alertInfo = ajaxData.get("ALERT_INFO");
            if ("" !== alertInfo)
                MessageBox.alert(alertInfo);
            if (3 === scoreInfoTab.activeIdx)
                $("#QUERY_EXCHANGE_FLAG").val("1");

        },
        function (error_code, error_info) {
            $.endPageLoading();
            MessageBox.error(error_info);
        });
}

// 刷新当前标签页的表格
function adjustTables(idx) {
    $("#scoreInfoTab > .page > .content:eq(" + idx + ") .c_table").each(function () {
        window[this.id].adjust();
    });
}