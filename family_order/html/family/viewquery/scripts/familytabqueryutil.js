
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
        TDUser = "0",
        KDUser = "0";

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
            		+ btn.get("REMARK")
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
    var param = "&SERIAL_NUMBER=" + parent.$("#SERIAL_NUMBER").val()
    		  + "&HEAD_CUST_ID=" + parent.$("#HEAD_CUST_ID").val()
              + "&EPARCHY_CODE=" + parent.$("#EPARCHY_CODE").val()
              + "&USER_ID=" + parent.$("#FAMILY_USER_ID").val()
              + "&FAMILY_USER_ID=" + parent.$("#FAMILY_USER_ID").val()
              + "&NORMAL_FAMILY_CHECK=" + parent.$("#NORMAL_FAMILY").val();

    if(!null == inParam){

    	param += inParam;
    }
    var activeNav = $.trim(parent.$("#navListCol .on div").html());

    $.beginPageLoading("数据查询中。。。");
    $.ajax.submit(areaPart, "queryFamilyInfo", param, refreshPart,
        function (ajaxData) {
            $.endPageLoading();

            // "首页"查询完成后刷新顶部客户名称
            if (activeNav === "首页") {
            }

            if(activeNav === "业务历史"){
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

//缓存营销活动基本信息查询结果
function cacheMySaleActiveTabReturnData(data) {
    if (data.get("QY_COMMON"))
        window.QYCommonCache = data.get("QY_COMMON");
    if (data.get("NOQY_COMMON"))
        window.NOQYCommonCache = data.get("NOQY_COMMON");
}

//刷新营销活动标签分页
function adjustSaleActiveTab() {
    saleActiveTab.adjust();
}