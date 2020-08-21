
$(document).ready(function () {
// 终端对象参数 1：移动端，0：PC端
    window.TERMINAL = $.os.phone ? "1" : "0";

    // 我的业务历史分页标签切换事件
    $("#tradeHisTab").afterSwitchAction(function (e, idx) {
        if (1 === idx) {
            $("#bookCheckCheckboxLabel").css("display", "none");
            initTradeHistoryBeforeTab();
        } else {
            $("#bookCheckCheckboxLabel").css("display", "");
        }
    });

    // 查询预约业务复选框绑定tap事件
    $("#bookCheckCheckboxLabel").tap(function () {
        if ($("#bookCheckCheckbox").attr("checked")) {
            $("#timeCheckCheckbox").attr("checked", false).attr("disabled", true);
            $("#TRADE_TYPE_CODE").attr("disabled", true).val("");
            $("#START_DATE").attr("disabled", true);
            $("#END_DATE").attr("disabled", true);
            $("#BOOK_CHECK").val("on");
        } else {
            $("#timeCheckCheckbox").attr("checked", true).attr("disabled", false);
            $("#TRADE_TYPE_CODE").attr("disabled", false);
            $("#START_DATE").attr("disabled", false);
            $("#END_DATE").attr("disabled", false);
            $("#BOOK_CHECK").val("");
        }
    });

    // 返回表格模式按钮绑定tap事件
    $("#tableViewButton").tap(function () {
        $("#tableViewButton").css("display", "none");
        backToTableViewForPC(tradeHisTab.activeIdx);
        adjustTradeHistoryTable();
    });

    // 翻页后绑定afterAction事件
    if ($.os.phone) {
        $("#tradeHisListNavBar,#tradeHisBeforeListNavBar").afterAction(function (t, c) {
            cacheMyTradeHistoryTabReturnData(c);
        });
    } else {
        $("#tradeHisTableNavBar,#tradeHisBeforeTableNavBar").afterAction(function (t, c) {
            cacheMyTradeHistoryTabReturnData(c);
            updateListNavBarPageInfo(tradeHisTab.activeIdx);
        });
    }

    loadFnNavButtons();
    initTradeHistoryTab();
});

// "我的业务历史"页面"最近"标签初始化方法
function initTradeHistoryTab() {
    var param = "&SERIAL_NUMBER=" + parent.$("#SERIAL_NUMBER").val()
        + "&USER_ID=" + parent.$("#USER_ID").val() + "&TERMINAL=" + window.TERMINAL;

    $.beginPageLoading("业务历史页面初始化。。。");
    $.ajax.submit(null, "initTradeHistoryQuery", param, "initParamPart",
        function () {
            $.endPageLoading();

            // 启用时间条件查询复选框绑定tap事件
            $("#timeCheckCheckbox").tap(function () {
                if ($("#timeCheckCheckbox").attr("checked")) {
                    $("#START_DATE").attr("disabled", false);
                    $("#END_DATE").attr("disabled", false);
                } else {
                    $("#START_DATE").attr("disabled", true);
                    $("#END_DATE").attr("disabled", true);
                }
            });

            // 页面上两个查询按钮绑定tap事件
            $("#tradeHisQueryButton").tap(function () {
                if (!$.os.phone) $("#tableViewButton").trigger("tap");
                loadTabInfo("tradeHisTablePart,tradeHisListCol", "initParamPart");
            });
            loadTabInfo("tradeHisTablePart,tradeHisListCol", "initParamPart");
        },
        function (error_code, error_info) {
            $.endPageLoading();
            MessageBox.error(error_info);
        });
}

// "我的业务历史"页面"历史"标签初始化方法
function initTradeHistoryBeforeTab() {
    var $initFlag = $("#INIT_TRADE_HISTORY_BEFORE_TAB_FINISHED"),
        param = "&SERIAL_NUMBER=" + parent.$("#SERIAL_NUMBER").val()
            + "&USER_ID=" + parent.$("#USER_ID").val() + "&TERMINAL=" + window.TERMINAL;

    if ("0" === $initFlag.val()) {
        $.beginPageLoading("历史标签页面初始化。。。");
        $.ajax.submit(null, "initTradeHistoryBeforeQuery", param, "initHisParamPart",
            function () {
                $.endPageLoading();
                $("#tradeHisBeforeQueryButton").tap(function () {
                    if (!$.os.phone) $("#tableViewButton").trigger("tap");
                    loadTabInfo("tradeHisBeforeTablePart,tradeHisBeforeListCol", "initHisParamPart");
                });
                $initFlag.val("1");
            },
            function (error_code, error_info) {
                $.endPageLoading();
                MessageBox.error(error_info);
            });
    }
}

// "我的业务历史"页面从列表中选择业务台账
function selectTradeFromList(el) {
    if ($(el).hasClass("cur")) return true;

    $("#tableViewButton").css("display", ""); // 显示"返回表格试图"按钮
    $(el).parent().find(".cur").removeClass("cur gray").addClass("link");
    $(el).addClass("cur gray").removeClass("link");

    var tabIdx = tradeHisTab.activeIdx,
        tradeId = $(el).attr("tradeId");
    if (0 === tabIdx) {
        $("#tradeHisContent").css("display", "none");
        $("#tradeHisError").css("display", "none");
        if ($.os.phone) $("#tradeHisCol").addClass("l_col-cur-2");
    } else if (1 === tabIdx) {
        $("#tradeHisBeforeContent").css("display", "none");
        $("#tradeHisBeforeError").css("display", "none");
        if ($.os.phone) $("#tradeHisBeforeCol").addClass("l_col-cur-2");
    }
    queryTradeTables(tabIdx, tradeId, "MAIN");
    queryTradeTables(tabIdx, tradeId, "SUB");
    $("#tradeHisScroller").attr("scrollTop", 0);
}

// "家庭业务历史"页面从表格中选择业务台账
function selectTradeFromTable(el) {
    $("#tableViewButton").css("display", ""); // 显示"返回表格试图"按钮

    var tabIdx = tradeHisTab.activeIdx,
        tradeId = $(el).attr("tradeId");
    if (0 === tabIdx) {
        $("#tradeHisTablePart").css("display", "none");
        $("#tradeHisListPart").removeClass("e_show-phone");
        $("#tradeHisListCol li").removeClass("cur gray");
        $("#tradeHisListCol li:nth-child(" + $(el).attr("rowIndex") + ")").addClass("cur gray");
    } else if (1 === tabIdx) {
        $("#tradeHisBeforeTablePart").css("display", "none");
        $("#tradeHisBeforeListPart").removeClass("e_show-phone");
        $("#tradeHisBeforeListCol li").removeClass("cur gray");
        $("#tradeHisBeforeListCol li:nth-child(" + $(el).attr("rowIndex") + ")").addClass("cur gray");
    }
    queryTradeTables(tabIdx, tradeId, "MAIN");
    queryTradeTables(tabIdx, tradeId, "SUB");
    updateListNavBarPageInfo(tabIdx);
}

// 查询订单台账信息
function queryTradeTables(tabIdx, tradeId, tableTag, queryAgain) {
    var params = "&TRADE_ID=" + tradeId,
        listener = "MAIN" === tableTag ? "queryMainTradeTable" : "querySubTradeTable",
        contentPart, loadingPart, errorPart,
        paramCache = 0 === tabIdx ? window.paramCache : window.paramBeforeCache,
        resultCache = 0 === tabIdx ? window.resultCache : window.resultBeforeCache,
        tradeCache, cacheData = null;

    if (0 === tabIdx) {
        contentPart = "MAIN" === tableTag ? "mainTradeContent" : "subTradeContent";
        loadingPart = "MAIN" === tableTag ? "mainTradeLoading" : "subTradeLoading";
        errorPart   = "MAIN" === tableTag ? "mainTradeError"   : "subTradeError";
        tradeCache  = "MAIN" === tableTag ? window.mainTradeCache : window.subTradeCache;
    } else if (1 === tabIdx) {
        contentPart = "MAIN" === tableTag ? "mainTradeBeforeContent" : "subTradeBeforeContent";
        loadingPart = "MAIN" === tableTag ? "mainTradeBeforeLoading" : "subTradeBeforeLoading";
        errorPart   = "MAIN" === tableTag ? "mainTradeBeforeError"   : "subTradeBeforeError";
        tradeCache  = "MAIN" === tableTag ? window.mainTradeBeforeCache : window.subTradeBeforeCache;
    }

    $("#" + errorPart).css("display", "none");
    $("#" + contentPart).css("display", "none");
    $("#" + loadingPart).css("display", "");

    params += "&QUERY_COND=" + paramCache.toString();
    if (queryAgain) {
        params += getCacheDataByTradeId(tradeId, resultCache);
    } else {
        cacheData = getCacheDataByTradeId(tradeId, tradeCache);
        params += cacheData ? (cacheData + "&JUMP_TAG=1") : getCacheDataByTradeId(tradeId, resultCache);
    }

    $.ajax.submit(null, listener, params, contentPart,
        function (ajaxData) {
            $("#" + loadingPart).css("display", "none");
            $("#" + contentPart).css("display", "");

            if (!existInCacheByTradeId(tradeId, tradeCache))
                tradeCache.add(ajaxData);
        },
        function () {
            $("#" + loadingPart).css("display", "none");
            $("#" + errorPart).css("display", "");
        });
}

// 再次查询已选中订单台账信息
function refreshTradeInfo(tableTag) {
    var tabIdx = tradeHisTab.activeIdx,
        listColId = 0 === tabIdx ? "tradeHisListCol" : "tradeHisBeforeListCol",
        tradeId = $("#" + listColId + " li.cur.gray").attr("tradeId");
    queryTradeTables(tabIdx, tradeId, tableTag, true);
}

// 根据TRADE_ID获取业务历史缓存数据
function getCacheDataByTradeId(id, cache) {
    var data = null;
    cache.each(function () {
        if (this.get("TRADE_ID") === id) {
            data = "&PARAMS_STR=" + this.toString();
            return false;
        }
    });
    return data;
}

// 根据TRADE_ID判断该台账数据是否已缓存
function existInCacheByTradeId(id, cache) {
    var flag = false;
    cache.each(function () {
        if (this.get("TRADE_ID") === id) {
            flag = true;
            return false;
        }
    });
    return flag;
}

// 缓存业务历史基本信息查询结果
function cacheMyTradeHistoryTabReturnData(data) {
    var tabIdx = tradeHisTab.activeIdx;
    emulateTableNavBar(tabIdx);
    if (0 === tabIdx) {
        window.paramCache = data.get("PARAM_DATA");
        data.get("INFO_DATA").each(function (info) {
            if (!existInCacheByTradeId(info.get("TRADE_ID"), window.resultCache))
                window.resultCache.add(info);
        });
    } else if (1 === tabIdx) {
        window.paramBeforeCache = data.get("PARAM_DATA");
        data.get("INFO_DATA").each(function (info) {
            if (!existInCacheByTradeId(info.get("TRADE_ID"), window.resultBeforeCache))
                window.resultBeforeCache.add(info);
        });
    }
}

// 模拟表格分页组件翻页动作和统计
function emulateTableNavBar(tabIdx) {
    if (0 === tabIdx) {
        $("#prevBtn").tap(function () {$(tradeHisTableNavBar.btnPrev).trigger("tap")});
        $("#nextBtn").tap(function () {$(tradeHisTableNavBar.btnNext).trigger("tap")});
    } else if (1 === tabIdx) {
        $("#prevHisBtn").tap(function () {$(tradeHisBeforeTableNavBar.btnPrev).trigger("tap")});
        $("#nextHisBtn").tap(function () {$(tradeHisBeforeTableNavBar.btnNext).trigger("tap")});
    }
}

// 更新列表分页组件的分页信息
function updateListNavBarPageInfo(tabIdx) {
    var currPage, pageCount, pageId, pageText;
    if (0 === tabIdx) {
        currPage = tradeHisTableNavBar.currPage;
        pageCount = tradeHisTableNavBar.pageCount;
        pageId = "pageArea";
    } else if (1 === tabIdx) {
        currPage = tradeHisBeforeTableNavBar.currPage;
        pageCount = tradeHisBeforeTableNavBar.pageCount;
        pageId = "pageHisArea";
    }
    pageText = 0 === pageCount ? currPage.toString() : (currPage.toString() + "/" + pageCount.toString());
    $("#" + pageId).html(pageText);
}

// 清空全部缓存对象
function emptyAllCaches() {
    if (0 === tradeHisTab.activeIdx) {
        window.paramCache = new $.DataMap();
        window.resultCache = new $.DatasetList();
        window.mainTradeCache = new $.DatasetList();
        window.subTradeCache = new $.DatasetList();
    } else if (1 === tradeHisTab.activeIdx) {
        window.paramBeforeCache = new $.DataMap();
        window.resultBeforeCache = new $.DatasetList();
        window.mainTradeBeforeCache = new $.DatasetList();
        window.subTradeBeforeCache = new $.DatasetList();
    }
}

// 订单辅助信息下拉按钮事件
function toggleTradeInfo(el) {
    var subEl = $(el).parent().parent().find(".sub");
    if (subEl.css("display") === "none") {
        subEl.css("display", "");
    } else {
        subEl.css("display", "none");
    }

    var foldEl = el.children[0];
    foldEl.className = foldEl.className === "e_ico-unfold" ? "e_ico-fold" : "e_ico-unfold";
}

// 订单预留字段下拉按钮事件
function toggleRsrvInfo(el) {
    var parent = $(el).parent();
    if (el.children[0].className === "e_ico-unfold") {
        parent.prev().prev().css("display", "");
        parent.css("display", "none").prev().css("display", "");
    } else {
        parent.prev().css("display", "none");
        parent.css("display", "none").next().css("display", "");
    }
}

// PC端返回表格模式
function backToTableViewForPC(idx) {
    if (0 === idx) {
        $("#tradeHisTablePart").css("display", "");
        $("#tradeHisListPart").addClass("e_show-phone");
        $("#tradeHisContent").css("display", "none");
        $("#tradeHisError").css("display", "none");
    } else if (1 === idx) {
        $("#tradeHisBeforeTablePart").css("display", "");
        $("#tradeHisBeforeListPart").addClass("e_show-phone");
        $("#tradeHisBeforeContent").css("display", "none");
        $("#tradeHisBeforeError").css("display", "none");
    }
}

// 移动端返回列表模式
function backToListViewForPhone(idx) {
    if (0 === idx) {
        $("#tradeHisCol").removeClass("l_col-cur-2");
        $("#tradeHisListCol li").removeClass("cur gray");
    } else if (1 === idx) {
        $("#tradeHisBeforeCol").removeClass("l_col-cur-2");
        $("#tradeHisBeforeListCol li").removeClass("cur gray");
    }
}

// 将查询时间范围设置在所选年份内
function shiftTimeWindow(el) {
    var $startDate = $("#initHisParamPart input[name=START_DATE]"),
        $endDate = $("#initHisParamPart input[name=END_DATE]"),
        year = el.value;
    $startDate.val(year + $startDate.val().substr(4));
    $endDate.val(year + $endDate.val().substr(4));
}

// 刷新业务历史表格
function adjustTradeHistoryTable() {
    tradeHisTable.adjust();
    if (window["tradeHisBeforeTable"])
        tradeHisBeforeTable.adjust();
}