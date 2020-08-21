var visibleFrame; // 当前显示标签页面
var isNoFuzzy; //判断是否模糊查询

$(document).ready(function () {
    var $serialNumber = $("#SERIAL_NUMBER_INPUT");

    if ($.os.phone) { // 移动端查询弹出层屏蔽返回按钮
        $("#queryPopup_item.c_popupItem > .c_header div:eq(0)").removeClass("back")
            .addClass("text").attr("ontap", "");
    }

    initSegmentComponent();
    if ($("#IN_MODE_CODE").val() === "1") {
        var callCenterNumber = getCallCenterSn();
        if (callCenterNumber) {
            $serialNumber.val(callCenterNumber);
            $("#QUERY_BTN").trigger("tap");
        } else if (typeof eval(window.top.getSubscriberInfo) === "function") { // 新客服打开
            $serialNumber.val(window.top.getSubscriberInfo().getBILL_ID());
            $("#QUERY_BTN").trigger("tap");
        }
    } else {
        if (top.window["LOGIN_NUM_ID"] && top.$("#LOGIN_NUM_ID").val() !== "") { // 从外框touchframe获取已登录号码
            $serialNumber.val($.trim(top.$("#LOGIN_NUM_ID").val()));
            if ($.params.get("AUTO_QUERY") === "1") { // 从外框点击"我的信息"进入
                $("#QUERY_BTN").trigger("tap");
            } else { // 从菜单点击进入
                showPopup("queryPopup", "queryPopup_item");
                $serialNumber.focus();
            }
        } else {
            showPopup("queryPopup", "queryPopup_item");
            $serialNumber.focus();
        }
    }
});

// 初始化用户状态@Segment组件
function initSegmentComponent() {
    var $userStateSegment = $("#userStateSegment");
    window["userStateSegment"] = new Wade.Segment("userStateSegment",{
        disabled:false
    });
    $userStateSegment.val("0");
    $userStateSegment.change(function () {
        if (this.value === "0")
            $("#NORMAL_USER_CHECK").attr("checked", true).val("on");
        else
            $("#NORMAL_USER_CHECK").attr("checked", false).val("off");
    });
}

function getCallCenterSn() {
    var serialNumber = null;
    var frame = top.document.getElementById("callcenter");
    var rhkfframe = window.top.document.getElementById("public_iframe");
    if (frame && frame.contentWindow && frame.contentWindow.document) {
        var callObj = frame.contentWindow.document.getElementById("CALL_PHONE");
        if (callObj) {
            var callPhone = callObj.value;
            var callParams = callPhone.split("JHJ");
            if (callParams && callParams.length === 4) {
                serialNumber = callParams[3];
            }
        }
    } else if (rhkfframe && rhkfframe.contentWindow && rhkfframe.contentWindow.document) {
        serialNumber = rhkfframe.contentWindow.document.getElementById("serviceNo").value;
    }
    return serialNumber;
}

// 根据SIM卡获取手机号码开关事件
function simQuerySwitchChange() {
    var $phoneInput = $("#SERIAL_NUMBER_INPUT");
    var $simInput = $("#SIM_NUMBER_INPUT");
    var switchVal = $("#simQuerySwitch").val();

    if (switchVal === "off") { // 默认值
        $phoneInput.attr("disabled", false).val("");
        $simInput.attr("disabled", true).val("");
        $("#SERIAL_NUMBER_LI").removeClass("e_dis");
        $("#SIM_NUMBER_LI").addClass("e_dis");
    } else {
        $phoneInput.attr("disabled", true).val("");
        $simInput.attr("disabled", false).val("");
        $("#SERIAL_NUMBER_LI").addClass("e_dis");
        $("#SIM_NUMBER_LI").removeClass("e_dis");
    }
}

// 用户信息查询校验
function checkQueryInput() {
    var switchVal = $("#simQuerySwitch").val();
    if (switchVal === "off") { // 默认值
        var $serial = $("#SERIAL_NUMBER_INPUT");
        $serial.val($.trim($serial.val()));
        if (!$.validate.verifyField("SERIAL_NUMBER_INPUT"))
            return false;
    } else {
        var $sim = $("#SIM_NUMBER_INPUT");
        $sim.val($.trim($sim.val()));
        if (!$.validate.verifyField("SIM_NUMBER_INPUT"))
            return false;
    }
    return true;
}

// 用户信息查询
function queryUserInfo() {
    if (!checkQueryInput())
        return;
    isNoFuzzy = '&PARAM=yes';//模糊化查询
    hidePopup("queryPopup");
    $.beginPageLoading("数据查询中。。。");
    $.ajax.submit("queryCondPart,hiddenPart", "queryInfo", null, "hiddenPart,queryCondPart",
        function (ajaxData) {
            $.endPageLoading();
            initSegmentComponent();
            var alertInfo = ajaxData.get("ALERT_INFO");
            if (alertInfo && typeof alertInfo !== "undefined") {
                MessageBox.error("错误提示", alertInfo);
            } else {
                afterQueryUserInfoAction();
            }
        },
        function (error_code, error_info) {
            $.endPageLoading();
            MessageBox.alert(error_info);
        });
}

//新增免模糊化查询按钮queryUserInfoFor4A()
function queryUserInfoFor4A() {
    if (!checkQueryInput())
        return;
    //4A校验
    $.beginPageLoading("正进行金库认证...");
    $.treasury.auth("USER360VIEW_4A_VAGUE",
        function (ret) {
            $.endPageLoading();
            if (true === ret) {
                MessageBox.alert("认证成功");
                isNoFuzzy = '&PARAM=no';//免模糊化查询
                hidePopup("queryPopup");
                $.beginPageLoading("数据查询中。。。");
                $.ajax.submit("queryCondPart,hiddenPart", "queryInfo", null, "hiddenPart,queryCondPart",
                    function (ajaxData) {
                        $.endPageLoading();
                        initSegmentComponent();
                        var alertInfo = ajaxData.get("ALERT_INFO");
                        if (alertInfo && typeof alertInfo !== "undefined") {
                            MessageBox.error("错误提示", alertInfo);
                        } else {
                            afterQueryUserInfoAction();
                        }
                    },
                    function (error_code, error_info) {
                        $.endPageLoading();
                        MessageBox.alert(error_info);
                    });
            } else {
                MessageBox.alert("认证失败");
            }
        });
     
}


// 用户信息查询后事件
function afterQueryUserInfoAction() {
    resetAllFrames();
    manyRecordsVerify();
    if ($("#IN_MODE_CODE").val() === "1") {
        checkIfManager();
    }
}

// 重置界面全部@Frame框架
function resetAllFrames() {
    visibleFrame = null;
    if ($.os.phone)
        $("#navListPhoneCol li").removeClass("on");
    else
        $("#navListCol li").removeClass("on");
    $("#navFrame .l_colItem").css("display", "none");
    $("#navFrame iframe").each(function () {
        if (window[this.id])
            window[this.id].inited = false;
    });
}

// 判断服务号码是否查询到了多条记录，如果不是，跳过，否则弹出页面进行查询
function manyRecordsVerify() {
    // 通过判断页面隐藏字段MANY_RECORDS值判断，0:单条或无数据 1:多条记录
    var manyRecords = $("#MANY_RECORDS").val();
    var normalUserCheck = $("#NORMAL_USER").val() === "on" ? "true" : "false";
    if (manyRecords === "1") {
        hidePopup("queryPopup");
        var params = "&SERIAL_NUMBER=" + $("#SERIAL_NUMBER_INPUT").val()
                + "&USER360VIEW_VALIDTYPE=" + $("#USER360VIEW_VALIDTYPE").val()
                + "&SERVICE_NUMBER=" + $("#SERVICE_NUMBER").val()
                + "&PSPT_NUMBER=" + $("#PSPT_NUMBER").val()
                + "&EPARCHY_CODE=" + $("#ROUTE_EPARCHY_CODE").val()
                + "&NORMAL_USER_CHECK=" + normalUserCheck;
        popupPage("客户列表", "userview.CheckUserInfoNew", "getCheckUserInfo", params, null, "full");
    } else {
        updateTouchFrameLoginInfo();
        setParamsForAcctPages();
        queryCRMInfo();
        queryAcctInfo();
        retInfo();
    }
}

// 查询用户CRM信息
function queryCRMInfo() {
    var params = "&SERIAL_NUMBER=" + $("#SERIAL_NUMBER").val()
        + "&USER_ID=" + $("#USER_ID").val();
    $.ajax.submit(null, "queryCRMInfo", params, "CRMInfoPart,custInfoPart,phoneCRMInfoPart,phoneCustInfoPart");
}

// 查询用户账管信息
function queryAcctInfo() {
    var params = "&SERIAL_NUMBER=" + $("#SERIAL_NUMBER").val()
        + "&USER_ID=" + $("#USER_ID").val() + "&CUST_ID=" + $("#CUST_ID").val()
        + "&NORMAL_USER_CHECK=" + $("#NORMAL_USER").val();
    $.ajax.submit(null, "queryAcctInfo", params, "acctInfoPart,phoneAcctInfoPart");
}

// 接入号码是否为服务号码的服务经理
function checkIfManager() {
    if ($("#SERIAL_NUMBER_B").val() === "") {
        return true;
    }

    var custManagerPass = $("#VIP_MANAGER_PASS").val();
    var isVipManager = $("#IS_VIP_MANAGER").val();
    var bothSn = $("#IS_BOTH_SN").val();

    top["VIP_MANAGER_PASS"] = custManagerPass === "1";
    top["IS_VIP_MANAGER"] = isVipManager === "1";
    top["BOTH_SN_NUMBER"] = bothSn; // 是否一卡双号
}

function retInfo() {
    // 客户信息
    /*var serialNumber = $("#SERIAL_NUMBER").val();
    if (top.showCustInfo && typeof top.showCustInfo === "function") {
        var param = "&SERIAL_NUMBER=" + serialNumber + "&TRADE_TYPE_CODE=2101";
        $.ajax.submit(null, "getHintInfo", param, null,
            function (ajaxData) {
                if (!ajaxData || ajaxData.get("RESULT_CODE") !== "0") {
                    top.clearCustInfo();
                    return;
                }
                var custInfo = $.DataMap();
                var custName = $("#CUST_NAME").val();
                var custNameFuzzy = custName.substring(0, 1);
                var paddingMask = new Array(custName.length).join("*");
                custInfo.put("CUST_NAME", custNameFuzzy + paddingMask);
                custInfo.put("PRODCUT_NAME", ajaxData.get("PRODCUT_NAME"));
                custInfo.put("HINT_INFO", ajaxData.get("HINT_INFO1", "") + ajaxData.get("HINT_INFO2", ""));
                top.showCustInfo(custInfo.toString());
            }, function (err_code, err_info, err_de) {
                $.endPageLoading();
                MessageBox.error("错误提示", "加载客户信息错误！", null, null, err_info, err_de);
            });
    }
	
	// 营销推荐信息（无意义代码）
    if ($("#CRM_REALTIMEMARKETING_WEBSWITCH").val() === "1") {
        if (top.triggerPushInfos && typeof top.triggerPushInfos === "function") {
            $.ajax.submit(null, "newcheckPushInfoForUser360View", params, null,
                function (ajaxData) {
                    if (!ajaxData || (ajaxData && ajaxData.get("PUSH_FLAG") !== "1")) {
                        return;
                    }
                }, function () {
                    $.endPageLoading();
                });
        }
    }*/

    if ($("#USER_ID").val() !== "") {
        initNavList();
    } else {
        MessageBox.error("错误提示", "查询客户数据失败！");
    }
}

// 右侧导航标签初始化
function initNavList() {
    if ($.os.phone) {
        $("#navListPhoneCol li").first().trigger("tap");
        $("#queryPopup_item.c_popupItem > .c_header div:eq(0)").addClass("back")
            .removeClass("text").attr("ontap", "backPopup(this)"); // 移动端查询弹出层恢复返回按钮
    } else {
        $("#navListCol").css("display", "");
        $("#navListCol li").first().trigger("click"); // 点击"首页"标签
    }
}

// 右侧导航标签切换
function navTabSwitch(el, nextFrame) {
    if (nextFrame === "homeFrame" && visibleFrame === null) {
        $(el).addClass("on");
        $("#" + nextFrame).parent().css("display", "");
        homeFrame.init();
        visibleFrame = "homeFrame";
    } else if (nextFrame !== visibleFrame) {
        $(el).parent().find(".on").removeClass("on");
        $(el).addClass("on");
        $("#" + visibleFrame).parent().css("display", "none");
        $("#" + nextFrame).parent().css("display", "");
        window[nextFrame].init();
        visibleFrame = nextFrame;
    }

    if ($.os.phone) { // 移动端自动收回导航菜单
        $("#phoneMenu").removeClass("c_float-show");
    }
}

// 显示"更多"浮动窗口
function showFloatMoreInfo(partId) {
    showFloatLayer(partId, "block");
    if (window["foregiftTable"]) {
        foregiftTable.adjust(); // 刷新用户押金表格
    }
}

// 隐藏"更多"浮动窗口
function hideFloatMoreInfo(partId) {
    hideFloatLayer(partId, "block");
}

// 刷新TouchFrame外框客户信息
function updateTouchFrameLoginInfo() {
    if (top.document.getElementById("LOGIN_USER_ID")) {
        var loginNum = top.document.getElementById("LOGIN_NUM"),
            loginUserId = top.document.getElementById("LOGIN_USER_ID");
        loginNum.value = $("#SERIAL_NUMBER_INPUT").val();
        loginUserId.value = $("#USER_ID").val();
        if (top.loginConfirm) {
            top.$("#LOGIN_PARAMS").val("noNeedLog");
            top.loginConfirm();
        }
    }
}

// 设置"我的消费"和"我的余量"子页面查询参数
function setParamsForAcctPages() {
    var userId = $("#USER_ID").val();
    window["billFrame"].params = "&USER_ID=" + userId + "&CRM_TAG=1"; // CRM_TAG=1 隐藏号码查询输入框
    window["allowanceFrame"].params = "&USER_ID=" + userId + "&CRM_TAG=1";
}

// 从外框获取手机号码刷新页面
function refreshUser360View() {
    var $serialNumber = $("#SERIAL_NUMBER_INPUT");

    if ($("#IN_MODE_CODE").val() === "1") {
        var callCenterNumber = getCallCenterSn();
        if (callCenterNumber) {
            $serialNumber.val(callCenterNumber);
            $("#QUERY_BTN").trigger("tap");
        } else if (typeof eval(window.top.getSubscriberInfo) === "function") { // 新客服打开
            $serialNumber.val(window.top.getSubscriberInfo().getBILL_ID());
            $("#QUERY_BTN").trigger("tap");
        }
    } else {
        if (top.window["LOGIN_NUM_ID"] && top.$("#LOGIN_NUM_ID").val() !== "") { // 从外框touchframe获取已登录号码
            $serialNumber.val($.trim(top.$("#LOGIN_NUM_ID").val()));
            $("#QUERY_BTN").trigger("tap");
        } else {
            showPopup("queryPopup", "queryPopup_item");
            $serialNumber.focus();
        }
    }
}

//给子页面传入数据
function toChildValue(){
    return isNoFuzzy;
}