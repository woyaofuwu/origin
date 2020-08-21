/**
 * zhangxi
 */

$(document).ready(function() {

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
        if (top.window["LOGIN_NUM"] && top.$("#LOGIN_NUM").val() !== "") { // 从外框touchframe获取已登录号码
            $serialNumber.val($.trim(top.$("#LOGIN_NUM").val()));
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

//初始化用户状态@Segment组件
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

function refreshFamily360View(){
	queryFamilyInfo();
}

// 家庭信息查询
function queryFamilyInfo() {
    if (!checkQueryInput()){
    	resetAllFrames() ;
        return;
    }
    hidePopup("queryPopup");
    $.beginPageLoading("数据查询中。。。");
    $.ajax.submit("queryCondPart,hiddenPart", "queryInfo", null, "hiddenPart,queryCondPart,custInfoPart,acctInfoPart",
        function (ajaxData) {
            $.endPageLoading();
            initSegmentComponent();
            var alertInfo = ajaxData.get("ALERT_INFO");
            if (alertInfo && typeof alertInfo !== "undefined") {
                MessageBox.error("错误提示", alertInfo);
                resetAllFrames() ;
            } else {
                afterQueryFamilyInfoAction();
            }
        },
        function (error_code, error_info) {
            $.endPageLoading();
            MessageBox.alert(error_info);
            resetAllFrames() ;
        });
}

//家庭基础信息查询
function familyBaseInfoQuery() {

    queryCRMInfo();
	queryAcctInfo();
	retInfo();
}

//查询家庭账管信息
function queryAcctInfo() {

    var params = "&SERIAL_NUMBER=" + $("#SERIAL_NUMBER").val()
        	   + "&FAMILY_USER_ID=" + $("#FAMILY_USER_ID").val()
        	   + "&HEAD_CUST_ID=" + $("#HEAD_CUST_ID").val()
        	   + "&NORMAL_USER_CHECK=" + $("#NORMAL_FAMILY").val();

    $.ajax.submit(null, "queryAcctInfo", params, "acctInfoPart");
}

//查询用户CRM信息
function queryCRMInfo() {
    var params = "&SERIAL_NUMBER=" + $("#SERIAL_NUMBER").val()
        	   + "&FAMILY_USER_ID=" + $("#FAMILY_USER_ID").val();
    $.ajax.submit(null, "queryCRMInfo", params, "CRMInfoPart");
}

// 用户信息查询校验
function checkQueryInput() {

   var $serial = $("#SERIAL_NUMBER_INPUT");
   $serial.val($.trim($serial.val()));
   if (!$.validate.verifyField("SERIAL_NUMBER_INPUT")){
        return false;
   }
    return true;
}

// 显示"更多"浮动窗口
function showFloatMoreInfo(partId) {
	showFloatLayer(partId, "block");
}

// 隐藏"更多"浮动窗口
function hideFloatMoreInfo(partId) {
	hideFloatLayer(partId, "block");
}

// 用户信息查询后事件
function afterQueryFamilyInfoAction() {
	resetAllFrames();
	familyBaseInfoQuery();
}

// 重置界面全部@Frame框架
function resetAllFrames() {
	visibleFrame = null;
	if ($.os.phone)
		$("#navListPhoneCol li").removeClass("on");
	else
		$("#navListCol li").removeClass("on");
	$("#navFrame .l_colItem").css("display", "none");
	$("#navFrame iframe").each(function() {
		if (window[this.id])
			window[this.id].inited = false;
	});
}

function retInfo() {
 if ($("#FAMILY_USER_ID").val() !== "") {
		initNavList();
	} else {
		MessageBox.error("错误提示", "查询家庭数据失败！");
	}
}

// 右侧导航标签初始化
function initNavList() {
	if ($.os.phone) {
		$("#navListPhoneCol li").first().trigger("tap");
		$("#queryPopup_item.c_popupItem > .c_header div:eq(0)")
				.addClass("back").removeClass("text").attr("ontap",
						"backPopup(this)"); // 移动端查询弹出层恢复返回按钮
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