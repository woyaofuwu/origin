
// 移动端外框用户信息按钮
function tapUserIcon() {
    toggleClass("phoneUserInfoPart", "c_float-show");
    parent.$("#phoneMenu").removeClass("c_float-show");
}

// 移动端外框查询按钮
function tapSearchIcon() {
    parent.showPopup("queryPopup", "queryPopup_item");
    parent.$("#phoneMenu").removeClass("c_float-show");
    parent.$("#phoneUserInfoPart").removeClass("c_float-show");
}

// 移动端外框菜单按钮
function tapMenuIcon() {
    toggleClass("phoneMenu", "c_float-show");
    parent.$("#phoneUserInfoPart").removeClass("c_float-show");
}

// 给DOM对象添加或删除class样式
function toggleClass(elem, className) {
    var $elem;
    if (typeof elem === "object") {
        $elem = $(elem);
    } else if (typeof elem === "string") {
        $elem = parent.$("#" + elem);
    }

    if ($elem.hasClass(className)) {
        $elem.removeClass(className);
    } else {
        $elem.addClass(className);
    }
}

// 页面初始化时加载跳转按钮
function loadFnNavButtons() {
    var frameName = getPageName(parent.window.location.href),
        navTabName = getPageName(window.location.href),
        titleCount = $("div.c_scroll .c_title").length,
        TDUser = parent.$("#IS_TD_USER").val(),
        KDUser = parent.$("#IS_KD_USER").val();

    // 如果用户已销户，默认不是无线固话或无手机宽带用户
    TDUser = TDUser === "" ? "0" : TDUser;
    KDUser = KDUser === "" ? "0" : KDUser;

    var param = "&FRAME_NAME=" + frameName + "&NAVTAB_NAME=" + navTabName
            + "&TITLE_COUNT=" + titleCount + "&TD_USER=" + TDUser
            + "&KD_USER=" + KDUser;
    $.ajax.submit(null, "loadFnNavButtons", param, null,
        function (ajaxData) {
            if (ajaxData.length > 0) {
                drawButtons(ajaxData);
            }
        },
        function (error_code, error_info) {
            MessageBox.error(error_info);
        });
}

// 获取页面的注册名
function getPageName(s) {
    s = s.substring(s.lastIndexOf("/") + 1);
    if (s.indexOf("&") !== -1)
        s = s.substring(0, s.indexOf("&"));
    if (s.indexOf("#") !== -1)
        s = s.substring(0, s.indexOf("#"));
    return s;
}

// 构建跳转按钮相关htmlDOM
function drawButtons(list) {
    list.each(function (btnList) {
        var html = [],
            titlePosition = btnList.get("POSITION");
        html.push('<div class="fn">');
        btnList.get("BUTTON_INFO").each(function (btn) {
            var subsys = btn.get("PAGE_SUBSYS_CODE", ""),
                page = btn.get("PAGE_NAME"),
                listener = btn.get("PAGE_LISTENER"),
                params = btn.get("PAGE_PARAMS"),
                url = $.redirect.buildUrl(subsys, page, listener, params);
            html.push('<button type="button" class="e_button-r e_button-navy" '
                    + 'ontap="top.$.menuopener.openMenu(\'' + btn.get("RIGHT_CODE") + '\', \'' + btn.get("PAGE_TITLE") + '\', \'' + url + '\', \'' + btn.get("PATH_NAME") + '\')">');
            html.push('<span class="' + btn.get("BUTTON_IMG") + '"></span>'
                    + '<span>' + btn.get("BUTTON_TEXT") + '</span></button> ');
        });
        html.push('</div>');
        $("div.c_scroll .c_title").eq(titlePosition).append(html.join(""));
    });
}

// 右侧导航栏标签加载数据的通用方法
function loadTabInfo(refreshPart, areaPart, inParam) {
    areaPart = (areaPart === undefined) ? null : areaPart;
    inParam = (inParam === undefined) ? "" : inParam;
    var param = "&CUST_ID=" + parent.$("#CUST_ID").val()
            + "&EPARCHY_CODE=" + parent.$("#EPARCHY_CODE").val()
            + "&NORMAL_USER_CHECK=" + parent.$("#NORMAL_USER").val()
            + inParam;
    var activeNav = $.os.phone ? $.trim(parent.$("#navListPhoneCol .on div").html()) : $.trim(parent.$("#navListCol .on div").html());

    // 如果从"我的业务历史"页面调入查询，不需要再拼SERIAL_NUMBER和USER_ID，因为areaPart区域内已经有这两个参数了
    if (activeNav !== "我的业务历史") {
        param += "&SERIAL_NUMBER=" + parent.$("#SERIAL_NUMBER").val()
            + "&USER_ID=" + parent.$("#USER_ID").val();
    }

    $.beginPageLoading("数据查询中。。。");
    $.ajax.submit(areaPart, "queryInfo", param, refreshPart,
        function (ajaxData) {
            $.endPageLoading();

            // "首页"查询完成后刷新顶部客户名称
            if (activeNav === "首页") {
                fillUpCustNameForHomeTab(ajaxData);
            }

            // "我的活动"查询完成后刷新分页标签
            if (activeNav === "我的活动") {
                cacheMySaleActiveTabReturnData(ajaxData);
                adjustSaleActiveTab();
            }

            // "我的集团"查询完成后缓存返回数据
            if (activeNav === "我的集团") {
                cacheMyGroupTabReturnData(ajaxData);
            }

            // "我的业务历史"查询完成后刷新业务历史表格
            if (activeNav === "我的业务历史") {
                emptyAllCaches();
                cacheMyTradeHistoryTabReturnData(ajaxData);
                adjustTradeHistoryTable();
            }

            // 刷新滚动条
            var scrollerName = $("div[name$=Scroller].c_scroll").attr("name");
            window[scrollerName].refresh();
        },
        function (error_code, error_info) {
            $.endPageLoading();
            MessageBox.error("数据查询失败", error_info);
        });
}