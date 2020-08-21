// 缓存对象
window.QYCommonCache = new $.DatasetList();
window.QYDetailCache = new $.DatasetList();
window.NOQYCommonCache = new $.DatasetList();
window.NOQYDetailCache = new $.DatasetList();

$(document).ready(function () {
    // 查询所有营销活动复选框绑定tap事件
    $("#queryAllCheckboxLabel").tap(function () {
        if ($.os.phone) {
            backToListViewForPhone(0); // "签约类"标签返回列表模式
            backToListViewForPhone(1); // "非签约类"标签返回列表模式
        } else {
            backToListViewForPC(0);    // "签约类"标签返回列表模式
            backToListViewForPC(1);    // "非签约类"标签返回列表模式
            $("#listViewButton").css("display", "none"); // 隐藏"返回多列试图"按钮
        }
        var param = "&QUERY_ALL=" + $("#queryAllCheckbox").attr("checked");
        loadTabInfo("QYSaleActiveListCol,NOQYSaleActiveListCol", null, param);
    });

    // 返回列表模式按钮绑定tap事件
    $("#listViewButton").tap(function () {
        $("#listViewButton").css("display", "none"); // 隐藏"返回多列试图"按钮
        backToListViewForPC(saleActiveTab.activeIdx);
    });

    loadFnNavButtons();
    loadTabInfo("QYSaleActiveListCol,NOQYSaleActiveListCol");
});

// 再次查询已选中营销活动的详细信息
function refreshSaleActiveInfo() {
    var tradeId, refreshArea;
    if (saleActiveTab.activeIdx === 0) {
        $("#QYSaleActiveInfoError").css("display", "none");
        tradeId = $("#QYSaleActiveListCol li.cur.gray").attr("relaTradeId");
        refreshArea = "QYSaleActiveInfoCol";
    } else if (saleActiveTab.activeIdx === 1) {
        $("#NOQYSaleActiveInfoError").css("display", "none");
        tradeId = $("#NOQYSaleActiveListCol li.cur.gray").attr("relaTradeId");
        refreshArea = "NOQYSaleActiveInfoCol";
    }
    querySaleActiveDetails(tradeId, refreshArea, true);
}

// "我的活动"页面从列表中选择营销活动
function selectSaleActive(el) {
    if ($(el).hasClass("cur")) return true;

    $("#listViewButton").css("display", ""); // 显示"返回多列试图"按钮
    $(el).parent().find(".cur").removeClass("cur gray").addClass("link");
    $(el).addClass("cur gray").removeClass("link");

    var relaTradeId = $(el).attr("relaTradeId");
    if (saleActiveTab.activeIdx === 0) {
        $("#QYSaleActiveInfoDetail").css("display", "none");
        $("#QYSaleActiveInfoError").css("display", "none");
        $("#QYSaleActiveListCol > .c_list").removeClass("c_list-col-2");
        $("#QYSaleActiveListCol").css("width", "20em").next().removeClass("e_show-phone");
        if ($.os.phone) $("#QYSaleActivePart").addClass("l_col-cur-2"); // "l_col-cur-2"样式仅在移动端有效
        querySaleActiveDetails(relaTradeId, "QYSaleActiveInfoCol");
    } else if (saleActiveTab.activeIdx === 1) {
        $("#NOQYSaleActiveInfoDetail").css("display", "none");
        $("#NOQYSaleActiveInfoError").css("display", "none");
        $("#NOQYSaleActiveListCol > .c_list").removeClass("c_list-col-2");
        $("#NOQYSaleActiveListCol").css("width", "20em").next().removeClass("e_show-phone");
        if ($.os.phone) $("#NOQYSaleActivePart").addClass("l_col-cur-2");
        querySaleActiveDetails(relaTradeId, "NOQYSaleActiveInfoCol");
    }
    $("#saleActiveScroller").attr("scrollTop", 0);
}

// 查询营销活动详细信息
function querySaleActiveDetails(tradeId, refreshPart, queryAgain) {
    var params = "&EPARCHY_CODE=" + parent.$("#EPARCHY_CODE").val()
        + "&NORMAL_USER_CHECK=" + parent.$("#NORMAL_USER").val(),
        tabIdx = saleActiveTab.activeIdx,
        cacheData = null;
    if (tabIdx === 0) {
        $("#QYSaleActiveInfoLoading").css("display", "");

        if (queryAgain === true) {
            params += getCacheDataByTradeId(tradeId, window.QYCommonCache);
        } else {
            cacheData = getCacheDataByTradeId(tradeId, window.QYDetailCache);
            if (cacheData)
                params += cacheData + "&JUMP_TAG=1";
            else
                params += getCacheDataByTradeId(tradeId, window.QYCommonCache);
        }
    } else if (tabIdx === 1) {
        $("#NOQYSaleActiveInfoLoading").css("display", "");

        if (queryAgain === true) {
            params += getCacheDataByTradeId(tradeId, window.NOQYCommonCache);
        } else {
            cacheData = getCacheDataByTradeId(tradeId, window.NOQYDetailCache);
            if (cacheData)
                params += cacheData + "&JUMP_TAG=1";
            else
                params += getCacheDataByTradeId(tradeId, window.NOQYCommonCache);
        }
    }
    $.ajax.submit(null, "querySaleActiveDetails", params, refreshPart,
        function (ajaxData) {
            var cache, tabsetId;
            if (tabIdx === 0) {
                $("#QYSaleActiveInfoLoading").css("display", "none");
                $("#QYSaleActiveInfoDetail").css("display", "");
                cache = window.QYDetailCache;
                tabsetId = "#elementsTabset_QY_" + tradeId;
            } else if (tabIdx === 1) {
                $("#NOQYSaleActiveInfoLoading").css("display", "none");
                $("#NOQYSaleActiveInfoDetail").css("display", "");
                cache = window.NOQYDetailCache;
                tabsetId = "#elementsTabset_NOQY_" + tradeId;
            }

            if (!existInCacheByTradeId(tradeId, cache))
                cache.add(ajaxData);

            elementTabsetAfterSwitchAction(tabsetId);
            adjustSaleActiveTab();
        },
        function () {
            if (tabIdx === 0) {
                $("#QYSaleActiveInfoLoading").css("display", "none");
                $("#QYSaleActiveInfoError").css("display", "");
            } else if (tabIdx === 1) {
                $("#NOQYSaleActiveInfoLoading").css("display", "none");
                $("#NOQYSaleActiveInfoError").css("display", "");
            }
        });
}

// 根据TRADE_ID获取营销活动缓存数据
function getCacheDataByTradeId(id, cache) {
    var data = null;
    cache.each(function () {
        if (this.get("RELATION_TRADE_ID") === id) {
            data = "&PARAMS_STR=" + this.toString();
            return false;
        }
    });
    return data;
}

// 根据TRADE_ID判断该活动数据是否已缓存
function existInCacheByTradeId(id, cache) {
    var flag = false;
    cache.each(function () {
        if (this.get("RELATION_TRADE_ID") === id) {
            flag = true;
            return false;
        }
    });
    return flag;
}

// 缓存营销活动基本信息查询结果
function cacheMySaleActiveTabReturnData(data) {
    if (data.get("QY_COMMON"))
        window.QYCommonCache = data.get("QY_COMMON");
    if (data.get("NOQY_COMMON"))
        window.NOQYCommonCache = data.get("NOQY_COMMON");
}

// PC端返回列表模式
function backToListViewForPC(idx) {
    if (idx === 0) {
        $("#QYSaleActiveListCol").css("width", "100%").next().addClass("e_show-phone");
        $("#QYSaleActiveListCol > .c_list").addClass("c_list-col-2");
        $("#QYSaleActiveListCol li").removeClass("cur gray").addClass("link");
    } else if (idx === 1) {
        $("#NOQYSaleActiveListCol").css("width", "100%").next().addClass("e_show-phone");
        $("#NOQYSaleActiveListCol > .c_list").addClass("c_list-col-2");
        $("#NOQYSaleActiveListCol li").removeClass("cur gray").addClass("link");
    }
}

// 移动端返回列表模式
function backToListViewForPhone(idx) {
    if (idx === 0) {
        $("#QYSaleActivePart").removeClass("l_col-cur-2");
        $("#QYSaleActiveListCol li").removeClass("cur gray");
    } else if (idx === 1) {
        $("#NOQYSaleActivePart").removeClass("l_col-cur-2");
        $("#NOQYSaleActiveListCol li").removeClass("cur gray");
    }
}

// 绑定活动元素信息分页标签切换事件
function elementTabsetAfterSwitchAction(tabsetId) {
    $("#" + tabsetId).afterSwitchAction(function (e, idx) {
        var $table = $("#" + tabsetId + " > .page > .content:eq(" + idx + ") .c_table");
        if ($table.length > 1) window[$table[0].id].adjust();
    });
}

// 刷新营销活动标签分页
function adjustSaleActiveTab() {
    saleActiveTab.adjust();
}