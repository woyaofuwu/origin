/*$Id:$*/
var PAGE_FEE_LIST = $.DataMap();

// 备注信息特殊字符校验
function checkRemark() {
    var $remark = $("#REMARK");
    if ($remark.val() === "") {
        return true;
    }
    var textValue = $remark.val();
    var a = new Array('\\', '\"', '\/', '{', '}');
    for (var i = 0; i < a.length; i++) {
        if (textValue.indexOf(a[i]) >= 0) {
            MessageBox.alert("请不要输入特殊字符！[" + a[i] + "]");
            $remark.focus();
            return false;
        }
    }
    return true;
}

// 检查是否没有一个被选中
var isCheckBox = function (objId) {
    var obj = $("*[name=" + objId + "]");
    if (obj && obj.length) {
        obj.each(function () {
            if (this.checked)
                return true;
        });
    }
    return false;
};

/**********************************公用方法 开始*********************************/
// 去掉空格
function trim(str) {
    if (isNull(str)){
        return "";
    }
    return str.replace(/^\s+|\s+$/, "");
}

// 检查非空
function isNull(str) {
    return (str === undefined || str == null || str === "");
}

function isTel(str) {
    var reg = /^([0-9]|[\-])+$/g;
    if (str.length !== 11 && str.length !== 13) { // 增加物联网手机号码长度13位
        return false;
    } else {
        return reg.exec(str);
    }
}
/**********************************公用方法 结束*********************************/
function onSubmit() {
    // 表单数据有效性检查
    if (!verifyAll("BaseInfoPart")) return false;
    if (!verifyAll("CustInfoPart")) return false;
    if (!verifyAll("CustInfoMorePart")) return false;
    if (!verifyAll("AcctInfoPart")) return false;
    if ($("#DEFAULT_PWD_FLAG").val() !== "1") {
        if (!verifyAll("PasswdPart")) return false;
    }

    var birthdayObj = $("#BIRTHDAY");
    if (birthdayObj.val() === "") {
        birthdayObj.val("1900-01-01");
    }

    // 密码加密 2015-04-13
    var userPasswdObj = $("#USER_PASSWD");
    var userPasswd = userPasswdObj.val();
    if (userPasswd != null && userPasswd !== "" && userPasswd.length === 6) {
        $.getScript("scripts/iorder/icsserv/common/des/des.js",
            function () {
                var newPwd = userPasswd;
                var firstKey  = "c";
                var secondKey = "x";
                var thirdKey  = "y";
                userPasswdObj.val(strEnc(newPwd, firstKey, secondKey, thirdKey) + "xxyy");
            });
    }

    /**
     * 人像比对
     * AGENT_PIC_ID_value这是界面默认的值
     * 当没有对经办人人像摄像时，就把AGENT_PIC_ID置空
     */
    var agentPicIdObj = $("#AGENT_PIC_ID");
    if (agentPicIdObj.val() === "") {
        agentPicIdObj.val("AGENT_PIC_ID_value");
    }
    if (agentPicIdObj.val() === "AGENT_PIC_ID_value") {
        agentPicIdObj.val("");
    }

    /**
     * 添加判断物联网开户，不需要进行人像比对
     * @author zhuoyingzhi
     * @date 20170706
     */
    var cmpTag = "1";
    $.ajax.submit(null, "isCmpPic", null, null,
        function (ajaxData) {
            var flag = ajaxData.get("CMPTAG");
            if (flag === "0") {
                cmpTag = "0";
            }
            $.endPageLoading();
        },
        function (error_code, error_info) {
            $.MessageBox.error(error_code, error_info);
            $.endPageLoading();
        }, {
            "async": false
        });
    //add by zhangxing3 for QR-20190611-03关于经办人人像比对失败也开户成功问题
    var cmp_tag = $("#CMP_TAG").val();
    if (cmpTag === "0" || cmp_tag === "0" ) {
        var picId = $("#PIC_ID").val();
        /**
         * REQ201705270006_关于人像比对业务优化需求
         * 个人开户：用户个人身份证证件开户，判断户主或者经办人人像比对通过即可。
         * @author zhuoyingzhi
         * @date 20170620
         */
        var custName = $("#AGENT_CUST_NAME").val();      // 经办人名称
        var psptId = $("#AGENT_PSPT_ID").val();          // 经办人证件号码
        var agentPsptAddr = $("#AGENT_PSPT_ADDR").val(); // 经办人证件地址
        var agentPicId = $("#AGENT_PIC_ID").val();
        var agentTypeCode = $("#AGENT_PSPT_TYPE_CODE").val();
        var psptTypeCode = $("#PSPT_TYPE_CODE").val();

        // 客户证件类型为身份证
        if (psptTypeCode == "0" || psptTypeCode == "1" || psptTypeCode == "3") {
            if (picId != null && picId === "ERROR") { // 客户摄像失败
                MessageBox.error("告警提示", "客户" + $("#PIC_STREAM").val());
                return false;
            }else if(picId == ""){ // 未进行客户摄像
                if (agentPicId){// 经办人已摄像
                    if (agentPicId == "ERROR"){// 摄像失败
                        MessageBox.error("告警提示", "经办人" + $("#AGENT_PIC_STREAM").val());
                        return false;
                    }
                }else {// 经办人未摄像
                    MessageBox.error("告警提示", "请进行客户或经办人摄像！");
                    return false;
                }
            }

        }else if (psptTypeCode == "D" || psptTypeCode == "E" || psptTypeCode == "G" || psptTypeCode == "L" || psptTypeCode == "M"){
            if (agentTypeCode == ""){// 经办人证件类型为空
                MessageBox.error("告警提示", "请选择经办人的证件类型");
                return false;
            }
            // 客户证件类型是单位类型，经办人证件选择 身份证需要进行人像比对
            if(agentTypeCode == "0" || agentTypeCode == "1" || agentTypeCode == "3" ){
                if (agentPicId){// 经办人已摄像
                    if (agentPicId == "ERROR"){// 摄像失败
                        MessageBox.error("告警提示", "经办人" + $("#AGENT_PIC_STREAM").val());
                        return false;
                    }
                }else {// 经办人未摄像
                    MessageBox.error("告警提示", "请进行经办人摄像！");
                    return false;
                }
            }
        }

        var openType = $("#OPEN_TYPE").val();
        if (openType !== "IOT_OPEN") { // 非物联网开户
            /**
             * REQ201707060020_关于年龄外经办人限制变更的优化
             * 如果客户的证件类型为户口本且客户未进行摄像，则提示客户摄像
             * 不使用 if((psptTypeCode == "0" || psptTypeCode == "1") && picid == "")这个
             * 业务逻辑说明：证件类型为户口本时，不对经办人进行处理，为了不影响原来的判断逻辑，则单独写。
             * @author zhuoyingzhi
             * @date 20170803
             */
            if (psptTypeCode === "2" && picId === "") { // 未进行客户摄像
                var custPsptId = $("#PSPT_ID").val();   // 客户证件号码
                if (custPsptId !== ""
                        && checkCustAge(custPsptId, psptTypeCode)) {
                    // 11岁（含）至120岁（不含）之间的用户必须通过验证才可以办理（同身份证一致）
                    MessageBox.error("告警提示", "请进行客户摄像!");
                    return false;
                }
            }
        }

        var param = "&PIC_ID=" + picId + "&AGENT_PIC_ID=" + agentPicId;
        $.cssubmit.addParam(param);
    }
    

    var submitData = selectedElements.getSubmitData();
    if (submitData.length === 0) {
        MessageBox.alert("您没有选择产品信息，请选择产品！");
        return false;
    }
    var inParam = "&SELECTED_ELEMENTS=" + submitData.toString();
    $.cssubmit.addParam(inParam);

    return confirmParamAll();
}

/**
 * 封装confirmAll，处理特殊情况
 * 1、不同证件类型，不同的检查
 * 2、当需要后台校验的先执行ajax且其值不为空，再做客户端校验
 */
function confirmParamAll() {
    if ($("#IMSI").val() === "") {
        MessageBox.alert("IMSI为空！");
        return false;
    }

    var agentIdObj;
    var isAgent = $("#IS_AGENT").val();
    if (isAgent === "1")
        agentIdObj = $("#AGENT_DEPART_ID");  // 开户代理商编码
    else
        agentIdObj = $("#AGENT_DEPART_ID1"); // 开户代理商编码
    if (agentIdObj.length) {                 // 不为undefined，表示代理商开户
        if (agentIdObj.val() === "") {
            MessageBox.alert("请选择开户代理商！");
            return false;
        }
    }

    var serialNumberObj = $("#SERIAL_NUMBER");
    var simCardNoObj = $("#SIM_CARD_NO");
    if (!$.validate.verifyField(serialNumberObj[0])) {
        return false;
    }
    if (!$.validate.verifyField(simCardNoObj[0])) {
        return false;
    }

    // CHECK_RESULT_CODE：服务号码与SIM校验结果，0-服务校验通过，1-SIM卡校验通过，初始值为-1
    var checkResultCode = $("#CHECK_RESULT_CODE").val();
    if (checkResultCode === "-1") {
        MessageBox.alert("新开户号码校验未通过！");
        serialNumberObj.focus();
        return false;
    }
    if (checkResultCode === "0") {
        MessageBox.alert("SIM卡号校验未通过！");
        simCardNoObj.focus();
        return false;
    }

    var realName = $("#REAL_NAME").val();
    if (realName === "1") {
        if ($("#PSPT_TYPE_CODE").val() === "Z") {
            MessageBox.alert("实名制开户，证件类型不能为其他，请重新选择！");
            return false;
        }
        var psptId = $("#PSPT_ID").val();
        if (psptId === "0" || psptId === "00" || psptId === "000"
                || psptId === "0000" || psptId === "00000" || psptId === "1"
                || psptId === "11" || psptId === "111" || psptId === "1111"
                || psptId === "11111" || psptId.indexOf("11111111") > -1) {
            MessageBox.alert("实名制开户，证件号码过于简单，请重新输入！");
            return false;
        }
        var custNameObj = $("#CUST_NAME");
        /*if (custNameObj.val().indexOf("海南通") > -1) {
            MessageBox.alert("实名制开户，客户名称不能为海南通，请重新输入！");
            custNameObj.val("");
            return false;
        }*/
        var psptTypeCode = $("#PSPT_TYPE_CODE").val();
        if(psptTypeCode=="D"||psptTypeCode=="L"||psptTypeCode=="G"||psptTypeCode=="M"||psptTypeCode=="E"){
            MessageBox.confirm("确认信息", "单位证件开户请及时进行使用人登记激活",
                function (btn) {
                    if (btn === "ok") {
                        MessageBox.confirm("您正在办理实名制，一旦提交资料将不能修改。请确认输入的资料无误！", null,
                            function (btn) {
                                if (btn === "ok") {
                                    realnameLimitCheck(realName);
                                }
                            });
                    }else{
                        return false;
                    }
                });
        }else{
            MessageBox.confirm("您正在办理实名制，一旦提交资料将不能修改。请确认输入的资料无误！", null,
                function (btn) {
                    if (btn === "ok") {
                        realnameLimitCheck(realName);
                    }
                });

        }
        return false;
    }
    realnameLimitCheck(realName);

    function realnameLimitCheck(realName) {
        if (realName === "1" && $("#REALNAME_LIMIT_CHECK_RESULT").val() !== "true") {
            MessageBox.alert("实名制开户数目校验未通过！");
            return false;
        }
        $.cssubmit.submitTrade();
    }
}

// 初始化产品
function initProduct() {
    var eparchyCode = $("#EPARCHY_CODE").val();
    offerList.renderComponent("", eparchyCode);
    selectedElements.renderComponent("&NEW_PRODUCT_ID=", eparchyCode);
    $("#SELECT_PRODUCT_BTN").attr("disabled", false).css("display", "");
    $("#PRODUCT_ID").val("");
    $("#PRODUCT_NAME").html("");
    $("#PRODUCT_DESC").html("").attr("title", "");
    $("#PRODUCT_DISPLAY").css("display", "none");
    $("#PRODUCT_COMPONENT_DISPLAY").css("display", "none");
}

function checkBeforeSelectProduct(el) {
    var serialNumberObj = $("#SERIAL_NUMBER");
    // CHECK_RESULT_CODE：服务号码与SIM校验结果，0-服务校验通过，1-SIM卡校验通过，初始值为-1
    var checkResultCode = $("#CHECK_RESULT_CODE").val();
    if (checkResultCode === "-1") {
        MessageBox.alert("新开户号码校验未通过！");
        serialNumberObj.focus();
        return;
    } else if (checkResultCode === "0") {
        MessageBox.alert("SIM卡号校验未通过！");
        $("#SIM_CARD_NO").focus();
        return;
    }

    if (!verifyAll("BaseInfoPart")) return;
    if (!verifyAll("CustInfoPart")) return;
    if (!verifyAll("CustInfoMorePart")) return;
    if (!verifyAll("AcctInfoPart")) return;
    if ($("#DEFAULT_PWD_FLAG").val() !== "1") {
        if (!verifyAll("PasswdPart")) return;
    }

    var $el = $(el);
    var productTypeCode = $("#PRODUCT_TYPE_CODE").val();
    var eparchyCode = $("#EPARCHY_CODE").val();
    var assignProductIds = ""; // 产品组件传值，如果有则只取传递产品 sunxin
    if (serialNumberObj.val().indexOf("147") === 0) { // 147号码判断
        $.ajax.submit(null, "getProductForSpc", null, null,
            function (ajaxData) {
                $.cssubmit.disabledSubmitBtn(false);
                ajaxData.each(function (item, index) {
                    if (item) {
                        if (index === 0)
                            assignProductIds = item.get("PARA_CODE1");
                        else
                            assignProductIds += "," + item.get("PARA_CODE1");
                    }
                });
                if ($el.attr("productId")) { // 热门或推荐产品
                    var productId = $el.attr("productId"),
                        productName = $el.attr("productName"),
                        productDesc = $el.attr("title"),
                        brandCode = $el.attr("brandCode");
                    afterChangeProduct(productId, productName, productDesc, brandCode);
                } else {
                    ProductSelect.popupProductSelect(productTypeCode, eparchyCode, "", assignProductIds);
                }
            },
            function (error_code, error_info, derror) {
                $.endPageLoading();
                showDetailErrorInfo(error_code, error_info, derror);
            });
    } else if ($("#INFO_TAG").val() === "1") { // 网上选号判断
        $.ajax.submit(null, "getProductForNet", null, null,
            function (ajaxData) {
                $.cssubmit.disabledSubmitBtn(false);
                ajaxData.each(function (item, index) {
                    if (item) {
                        if (index === 0)
                            assignProductIds = item.get("PARA_CODE1");
                        else
                            assignProductIds += "," + item.get("PARA_CODE1");
                    }
                });
                if ($el.attr("productId")) { // 热门或推荐产品
                    var productId = $el.attr("productId"),
                        productName = $el.attr("productName"),
                        productDesc = $el.attr("title"),
                        brandCode = $el.attr("brandCode");
                    afterChangeProduct(productId, productName, productDesc, brandCode);
                } else {
                    ProductSelect.popupProductSelect(productTypeCode, eparchyCode, "", assignProductIds);
                }
            },
            function (error_code, error_info, derror) {
                $.endPageLoading();
                showDetailErrorInfo(error_code, error_info, derror);
            });
    } else {
        if ($el.attr("productId")) { // 热门或推荐产品
            var productId = $el.attr("productId"),
                productName = $el.attr("productName"),
                productDesc = $el.attr("title"),
                brandCode = $el.attr("brandCode");
            afterChangeProduct(productId, productName, productDesc, brandCode);
        } else {
            ProductSelect.popupProductSelect(productTypeCode, eparchyCode, "");
        }
    }
}

// 处理产品选择后的js校验
function afterChangeProduct(productId, productName, productDesc, brandCode) {
    $.feeMgr.clearFeeList("10");
    $.feeMgr.insertFee(PAGE_FEE_LIST.get("NUMBER_FEE"));
    $.feeMgr.insertFee(PAGE_FEE_LIST.get("SIMCARD_FEE"));

    var feeData = $.DataMap();
    feeData.put("MODE", "2");
    feeData.put("CODE", "0");
    feeData.put("FEE",  "0");
    feeData.put("PAY",  "0");
    feeData.put("TRADE_TYPE_CODE", "10");
    $.feeMgr.insertFee(feeData);

    $("#PRODUCT_ID").val(productId);
    $("#PRODUCT_NAME").html(productName);
    $("#PRODUCT_DESC").html(productDesc).attr("title", productDesc);
    $("#SELECT_PRODUCT_BTN").attr("disabled", true).css("display", "none");  // 隐藏"产品目录"按钮
    $("#CHANGE_PRODUCT_BTN").attr("disabled", false).css("display", "");     // 展示"变更"按钮
    $("#PRODUCT_DISPLAY").css("display", "");                                // 展示已选产品
    $("#PRODUCT_COMPONENT_DISPLAY").css("display", "");                      // 展示"待选区"和"已选区"组件

    var eparchyCode = $("#EPARCHY_CODE").val();
    var param = "&NEW_PRODUCT_ID=" + productId;
    offerList.renderComponent(productId, eparchyCode);
    selectedElements.renderComponent(param, eparchyCode);
    //REQ201911220019关于优化无线宽带号码办理的需求
    var serialNumber = $("#SERIAL_NUMBER").val();
    var inParam = "&PRODUCT_ID=" + productId + "&BRAND_CODE=" + brandCode
            + "&EPARCHY_CODE=" + eparchyCode + "&SERIAL_NUMBER=" + serialNumber;
    //REQ201911220019关于优化无线宽带号码办理的需求
    $.ajax.submit(null, "getProductFeeInfo", inParam, null,
        function (data) {
            $.cssubmit.disabledSubmitBtn(false);
            for (var i = 0; i < data.getCount(); i++) {
                var data0 = data.get(i);
                if (data0) {
                    feeData.clear();
                    feeData.put("MODE", data0.get("FEE_MODE"));
                    feeData.put("CODE", data0.get("FEE_TYPE_CODE"));
                    feeData.put("FEE",  data0.get("FEE"));
                    feeData.put("PAY",  data0.get("FEE"));
                    feeData.put("TRADE_TYPE_CODE", "10");
                    $.feeMgr.insertFee(feeData);
                    PAGE_FEE_LIST.put("PRODUCT_FEE", $.feeMgr.cloneData(feeData));
                }
            }
        },
        function (error_code, error_info, derror) {
            $.endPageLoading();
            showDetailErrorInfo(error_code, error_info, derror);
        });
    
    //海南添加
	if(typeof(createPersonUserExtend) != "undefined" && createPersonUserExtend.afterChangeProduct)
	{
		createPersonUserExtend.afterChangeProduct(inParam);
	}	
}

function disableElements(data) {
    if (data) {
        var temp = data.get(0);
        if (data.get(0).get("NEW_PRODUCT_START_DATE")) {
            $("#NEW_PRODUCT_START_DATE").val(temp.get("NEW_PRODUCT_START_DATE"));
        }
    }
}

function SetAdvanceFeeForAgent() {
    // 先清空产品相关缓冲区预存款费用
    // 传参：tradeTypeCode feeMode feeTypeCode
    var agentAdvanceFee = $("#AGENT_ADVANCE_FEE").val();
    if (agentAdvanceFee !== "") {
        $.feeMgr.removeFee("10", "2", "0");
    } else {
        return;
    }
    // 重新插入用户选择的预存款费用
    var feeData = $.DataMap();
    feeData.clear();
    feeData.put("MODE", "2");
    feeData.put("CODE", "0");
    feeData.put("FEE", agentAdvanceFee * 100);
    feeData.put("TRADE_TYPE_CODE", "10");
    $.feeMgr.insertFee(feeData);
}

// 加载页面js
$(document).ready(function () {
    $("#SHOW_HOT_RECOMM_AREA,#HIDE_HOT_RECOMM_AREA").tap(function () {
        var $hotRecommArea = $("#HOT_RECOMM_AREA");
        if ($hotRecommArea.hasClass("e_hide-x")) {
            $hotRecommArea.removeClass("e_hide-x");
        } else {
            $hotRecommArea.addClass("e_hide-x");
        }
        $(this).css("display", "none").siblings().css("display", "");
    });

    $.acctInfo.delWidget(7);  // 删除银行协议号
    $.acctInfo.hideWidget(3); // 隐藏用户结账日
    $("#REAL_NAME").val("1");

    $("#ACTIVE_TAG").val("0");
    $("#TIETONG_NUMBER").bind("keydown",
        function (e) {
            if (e.keyCode == 13 || e.keyCode == 108) {
                // 回车事件
                getMobilePhoneByTieTongNumber();
                return false;
            }
        });

    $.developStaff.init("10");

    AGENT_PSPT_TYPE_CODE.empty();
    AGENT_PSPT_TYPE_CODE.append("本地身份证", "0");
    AGENT_PSPT_TYPE_CODE.append("外地身份证", "1");
    AGENT_PSPT_TYPE_CODE.append("户口本", "2");
    AGENT_PSPT_TYPE_CODE.append("护照", "A");
    AGENT_PSPT_TYPE_CODE.append("台湾居民来往大陆通行证", "N");
    AGENT_PSPT_TYPE_CODE.append("港澳居民来往内地通行证", "O");
    AGENT_PSPT_TYPE_CODE.append("外国人永久居留身份证", "P");

    USE_PSPT_TYPE_CODE.empty();
    USE_PSPT_TYPE_CODE.append("本地身份证", "0");
    USE_PSPT_TYPE_CODE.append("外地身份证", "1");
    USE_PSPT_TYPE_CODE.append("户口本", "2");
    USE_PSPT_TYPE_CODE.append("护照", "A");
    USE_PSPT_TYPE_CODE.append("台湾居民来往大陆通行证", "N");
    USE_PSPT_TYPE_CODE.append("港澳居民来往内地通行证", "O");
    USE_PSPT_TYPE_CODE.append("外国人永久居留身份证", "P");

    var openType = $("#OPEN_TYPE").val();
    if (openType === "IOT_OPEN") {
        $("#RsrvFieldPart").css("display", "");

        USER_TYPE_CODE.empty();
        USER_TYPE_CODE.append("个人", "0");
        USER_TYPE_CODE.append("集团用户", "8");
        USER_TYPE_CODE.append("测试机用户", "G");
        $("#USER_TYPE_CODE").val("0");
        
        /*PSPT_TYPE_CODE.empty();
        PSPT_TYPE_CODE.append("本地身份证", "0");
        PSPT_TYPE_CODE.append("外地身份证", "1");
        PSPT_TYPE_CODE.append("护照", "A");
        PSPT_TYPE_CODE.append("单位证明", "D");
        PSPT_TYPE_CODE.append("营业执照", "E");
        PSPT_TYPE_CODE.append("组织机构代码证", "M");
        PSPT_TYPE_CODE.append("事业单位法人证书", "G");
        $("#PSPT_TYPE_CODE").val("0");

        USE_PSPT_TYPE_CODE.append("军人身份证", "3");
        AGENT_PSPT_TYPE_CODE.append("军人身份证", "3");
        RSRV_STR3.append("军人身份证", "3");*/
    } else {
        $("#RsrvFieldPart").css("display", "none");
    }
});

/****************************校验开户号码及SIM资源 开始***************************/
// 重置手机号码校验
function resetSNCheck() {
    $("#SN_CHECK_BTN").css("display", "");
    $("#SN_SUCCESS_LABEL").css("display", "none");
    $("#SN_ERROR_LABEL").css("display", "none");
    $("#SIM_CHECK_BTN").css("display", "");
    $("#SIM_SUCCESS_LABEL").css("display", "none");
    $("#SIM_ERROR_LABEL").css("display", "none");
}

// 校验手机号码失败
function checkSerialNumberFailed() {
    $("#SN_CHECK_BTN").css("display", "none");
    $("#SN_ERROR_LABEL").css("display", "");
}

// 校验手机号码成功
function checkSerialNumberSucceed() {
    $("#SN_CHECK_BTN").css("display", "none");
    $("#SN_SUCCESS_LABEL").css("display", "");
    $.custInfo.setDefaultValueAfterSerialNumberCheck();
}

function checkOldSNPwd(flag) {
    var serialNumberObj = $("#SERIAL_NUMBER");
    var serialNumber = serialNumberObj.val();
    var oldSerialNumber = $("#OLD_SERIAL_NUMBER").val();

    if (!isNull(oldSerialNumber)) {
        // 同一个号码时，不再校验 sunxin
        if (oldSerialNumber === serialNumber) {
            var checkCode = $("#CHECK_RESULT_CODE").val();
            if (checkCode === "0" || checkCode === "1") {
                checkSerialNumberSucceed();
            }
            return false;
        }
    }

    // 初始化部分界面数据
    intiAllElements();

    //初始化esim开户部分界面数据
    initEsimElements();

    function initEsimElements(){
        if ("8" == $("#OPEN_TYPE").val()){
            $("#REFRESH_ESIM_SERIAL_NUMBER").attr('disabled',true);
            $("#REFRESH_ESIM_SERIAL_NUMBER").addClass("e_dis");
            $("#CHECK_ESIM_SERIAL_NUMBER").attr('disabled',true);
            $("#CHECK_ESIM_SERIAL_NUMBER").addClass("e_dis");
        }
    }


    // 清空费用
    $.feeMgr.clearFeeList("10");
    PAGE_FEE_LIST.clear();

    // 重置按钮
    $.cssubmit.disabledSubmitBtn(true);

    // 清空产品资料
    if ($("#EPARCHY_CODE").val() !== "") {
        initProduct();
    }

    if (serialNumber === "" || serialNumber.length < 11) {
        serialNumberObj.val("");
        return false;
    }
    if (!isTel(serialNumber)) {
        if (flag === 1) // 通过回车
            MessageBox.alert("输入的手机号码不对，请重新输入！");
        return false;
    }

    // 如果开户号码为空，或格式不正确，则返回
    if (!$.validate.verifyField(serialNumberObj[0])) {
        return false;
    }

    checkMphone(0);
}

// 校验手机号码
function checkMphone(flag) {
    var serialNumberObj = $("#SERIAL_NUMBER");
    var serialNumber = serialNumberObj.val();
    var oldSimCardNo = $("#OLD_SIM_CARD_NO").val();	// 考虑预配情况
    $.custInfo.setSerialNumber(serialNumber);       // 针对实名制，需要传递手机给组件 sunxin
    $("#CHECK_RESULT_CODE").val("-1");              // 设置号码未校验

    var oldSerialNumber = $("#OLD_SERIAL_NUMBER").val();
    if (isNull(oldSerialNumber)) {
        oldSerialNumber = "";
    }

    if (!isTel(serialNumber)) {
        if (flag === 1) // 通过回车
            MessageBox.alert("输入的手机号码不对，请重新输入！");
        return false;
    }

    // 先将SIM卡号清空
    var simCardNoObj = $("#SIM_CARD_NO");
    simCardNoObj.val("898600");
    simCardNoObj.attr("disabled", false);
    if (serialNumber === "" || serialNumber.length < 11) {
        return false;
    }
    if (!$.validate.verifyField(serialNumberObj[0])) {
        return false;
    }

    var agentIdObj;
    var agentCode = "";
    var isAgent = $("#IS_AGENT").val();
    if (isAgent === "1")
        agentIdObj = $("#AGENT_DEPART_ID");  // 开户代理商编码
    else
        agentIdObj = $("#AGENT_DEPART_ID1"); // 开户代理商编码
    var agentId = agentIdObj.val();
    if (agentIdObj.length) {                 // 不为undefined，表示代理商开户
        if (agentId === "") {
            MessageBox.alert("请选择开户代理商！");
            return false;
        } else {
            agentCode = agentId.substring(0, 5);
        }
    }

    var psptIdSelected = "";
    var infoTag = "0";
    if (flag === 1) {
        psptIdSelected = $("#OLD_NET_CHOOSE_ID").val();
        infoTag = $("#INFO_TAG").val();
    } else {
        $("#INFO_TAG").val("0");
    }
    var authFlag = $("#PERSON_AUTH_FLAG").val();
    var authSerialNumber = $("#AUTH_FOR_PERSON_SERIAL_NUMBER").val();
    var params = "&AUTH_FOR_PERSON_SERIAL_NUMBER=" + authSerialNumber
            + "&PERSON_AUTH_FLAG=" + authFlag
            + "&PSPTID_SELECTED=" + psptIdSelected + "&INFO_TAG=" + infoTag
            + "&SERIAL_NUMBER=" + serialNumber
            + "&OLD_SERIAL_NUMBER=" + oldSerialNumber
            + "&OLD_SIM_CARD_NO=" + oldSimCardNo
            + "&AGENT_DEPART_ID=" + agentCode
            + "&RES_CHECK_BY_DEPART=" + $("#RES_CHECK_BY_DEPART").val()
            + "&OPEN_TYPE=" + $("#OPEN_TYPE").val();
    $.beginPageLoading("新开户号码校验中......");
    $.ajax.submit(null, "checkSerialNumber", params, "CheckSimCardNoHidePart,CheckSerialNumberHidePart,ProductTypePart,SalePackage",
        function (ajaxData) {
            // 绑定"更多产品"和"变更"按钮点击事件
            $("#SELECT_PRODUCT_BTN,#CHANGE_PRODUCT_BTN").tap(checkBeforeSelectProduct);
            $("#HIDE_HOT_RECOMM_AREA").css("display", "");

            // 增加开户可提示相关信息（规则已终止，省略）
            $("#OLD_SERIAL_NUMBER").val(serialNumber);

            // 校验完后界面部分数据处理
            var rtnData = ajaxData.get(0);
            setAjaxAtferCheckMphone(rtnData);
            $.endPageLoading();

            // 处理手机号码预配产品 sunxin
            var singleProduct = rtnData.get("EXISTS_SINGLE_PRODUCT");
            if (singleProduct) {
                afterChangeProduct(singleProduct, rtnData.get("PRODUCT_NAME"), rtnData.get("PRODUCT_DESC"), rtnData.get("BRAND_CODE"));
                $("#CHANGE_PRODUCT_BTN").attr("disabled", true).css("display", "none");
                $("#PRODUCT_TYPE_CODE").attr("disabled", true).css("display", "none");
                $.cssubmit.disabledSubmitBtn(false);
            }
            if ($("#PAY_MODE_CODE").val() === "0") {
                $("#SUPER_BANK_CODE").attr("disabled", true);
                $("#BANK_CODE").attr("disabled", true);
                $("#BANK_ACCT_NO").attr("disabled", true);
            }

            // 网上选号绑定了套餐包默认勾选
            var salePackageObj = $("#SalePackage");
            var xCoding = rtnData.get("A_X_CODING_STR");
            if (xCoding) {
                salePackageObj.css("display", "");
                var saleProductId = xCoding.split("|")[0];
                var salePackageId = xCoding.split("|")[1];
                $("#SALE_PRODUCT_ID").val(saleProductId);
                $("#SALE_PACKAGE_ID").val(salePackageId);
                $("#BIND_SALE_TAG").val("1"); // 绑定营销活动

                var checkboxes = $("#packageTable input[name=packageCheckbox]"); // 获取表格全部复选框
                checkboxes.each(function () {
                    if (this.value === salePackageId) {
                        this.click();
                        $(this).parent().parent().addClass("on");
                    }
                });

                // 无更改权限时隐藏其他可选包并设置营销包必选 wenhj
                if ($("#SYSCHANGPACKAGE").val() !== "1") {
                    checkboxes.each(function () {
                        if (this.value !== salePackageId)
                            $(this).parent().parent().css("display", "none");
                        else if (rtnData.get("SYSCHANGPACKAGE4") !== "1")
                            $(this).parent().parent().attr("disabled", true);
                    });
                }
            } else {
                $("#SALE_PRODUCT_ID").val("");
                $("#SALE_PACKAGE_ID").val("");
                $("#BIND_SALE_TAG").val("0");
                salePackageObj.css("display", "none");
            }

            /**
             * REQ201602290007 关于入网业务人证一致性核验提醒的需求
             * chenxy3 2016-03-08
             */
            $.beginPageLoading("正在查询数据...");
            $.ajax.submit(null, "checkNeedBeforeCheck", null, null,
                function (ajaxData) {
                    var flag = ajaxData.get("PARA_CODE1");
                    if (flag === "1") {
                        var params = "&TRADE_ID=10" + "&EPARCHY_CODE=0898"
                                + "&TRADE_TYPE_CODE=10" + "&RAN=" + Math.random(); // RAN参数保证每次弹窗都刷新页面内容
                        popupPage("业务检查", "beforecheck.BeforeCheckNew", "init", params, null, "full");
                    }
                    $.endPageLoading();
                },
                function (error_code, error_info) {
                    $.endPageLoading();
                    MessageBox.error(error_code, error_info);
                });
        },
        function (error_code, error_info, derror) {
            $.endPageLoading();
            checkSerialNumberFailed();
            showDetailErrorInfo(error_code, error_info, derror);
        });
}

// 变更营销包权限判断 add by wenhj 2013.01.26
function chooseSaleActivePackage(o) {
    if ($("#SYSCHANGPACKAGE").val() === "1") {
        if (o.checked) {
            $("#SALE_PACKAGE_ID").val(o.value);
            $("#BIND_SALE_TAG").val("1");
        }
    }

    if ($("input[name=packageCheckbox]:checked").length === 0) {
        $("#SALE_PACKAGE_ID").val("");
        $("#BIND_SALE_TAG").val("0");
    }
}

// 新开户号码校验完后返回值的处理
function setAjaxAtferCheckMphone(data) {
    var simCardNo = data.get("SIM_CARD_NO");
    var checkResultCode = data.get("CHECK_RESULT_CODE");

    // 预配预开时，SIM卡自动带出来，且为不可修改
    var feeData;
    var simCardNoObj = $("#SIM_CARD_NO");
    if (!isNull(simCardNo)) {
        simCardNoObj.val(simCardNo);
        simCardNoObj.attr("disabled", true);
        $("#OLD_SIM_CARD_NO").val(simCardNo);
        $("#CHECK_RESULT_CODE").val(checkResultCode); // 设置SIM卡校验通过
        checkSerialNumberSucceed();
        checkSIMSucceed();

        // 预配卡费用 sunxin
        if (data.get("FEE")) {
            feeData = $.DataMap();
            feeData.put("MODE", data.get("FEE_MODE"));
            feeData.put("CODE", data.get("FEE_TYPE_CODE"));
            feeData.put("FEE", data.get("FEE"));
            feeData.put("PAY", data.get("FEE"));
            feeData.put("TRADE_TYPE_CODE", "10");
            feeData.put("SYSCHANGPACKAGE4", data.get("SYSCHANGPACKAGE4")); // REQ201608310006 关于海口分公司四级吉祥号码规则优化（二）
            $.feeMgr.insertFee(feeData);
            PAGE_FEE_LIST.put("SIMCARD_FEE", $.feeMgr.cloneData(feeData));
        }

        // 处理密码卡 sunxin
        var cardPasswd = $("#CARD_PASSWD").val(); // 密码
        var passCode = $("#PASSCODE").val();      // 密码因子
        if (cardPasswd !== "" && passCode !== "") {
            MessageBox.confirm("提示信息", "该SIM卡为初始密码卡，是否将初始密码作为用户服务密码？",
                function(btn){
                    if (btn === "ok") {
                        $("#DEFAULT_PWD_FLAG").val("1");  // 使用初始密码 sunxin
                        $("#PasswdPart").css("display", "none");
                    } else {
                        $("#DEFAULT_PWD_FLAG").val("0");  // 不使用初始密码 sunxin
                        $("#PasswdPart").css("display", "");
                    }
                });
        } else {
            $("#DEFAULT_PWD_FLAG").val("0");              // 不使用初始密码 sunxin
            $("#PasswdPart").css("display", "");
        }
    } else {
        simCardNoObj.val("898600");
        simCardNoObj.attr("disabled", false);
        $("#CHECK_RESULT_CODE").val(checkResultCode); // 设置号码校验通过
        checkSerialNumberSucceed();
    }

    // 号码需要交纳的预存费用 sunxin
    if (data.get("FEE_CODE_FEE")) {
        feeData = $.DataMap();
        feeData.put("MODE",  "2");
        feeData.put("CODE", "62"); // 选号预存收入
        feeData.put("FEE", data.get("FEE_CODE_FEE"));
        feeData.put("PAY", data.get("FEE_CODE_FEE"));
        feeData.put("TRADE_TYPE_CODE", "10");
        feeData.put("SYSCHANGPACKAGE4", data.get("SYSCHANGPACKAGE4")); // REQ201608310006 关于海口分公司四级吉祥号码规则优化（二）
        $.feeMgr.insertFee(feeData);
        PAGE_FEE_LIST.put("NUMBER_FEE", $.feeMgr.cloneData(feeData));
    }

    // 物联网开户
    if ($("#OPEN_TYPE").val() === "IOT_OPEN") {
        $("#PRODUCT_SEARCH_TEXT").attr("disabled", true);
        $("#offerSuggest").attr("disabled", true);
    }
}

// 新开户号码校验，初始化界面部分数据，后续可能不需要 sunxin
function intiAllElements() {
    $("#PSPT_END_DATE").val("");
    $("#PSPT_ID").val("");
    $("#CUST_NAME").val("");
    $("#POST_ADDRESS").val("");
    $("#POST_CODE").val("");
    $("#PHONE").val("");
    $("#PSPT_ADDR").val("");
    $("#CONTACT").val("");
    $("#CONTACT_PHONE").val("");
    $("#WORK_NAME").val("");
    $("#WORK_DEPART").val("");
    $("#EMAIL").val("");
    $("#FAX_NBR").val("");
    $("#HOME_ADDRESS").val("");
    $("#BIRTHDAY").val("");

    $("#PAY_NAME").val("");
    $("#PAY_MODE_CODE").val("");
    $("#BANK_CODE").val("");
    $("#BANK_ACCT_NO").val("");

    var userPasswdObj = $("#USER_PASSWD");
    if (userPasswdObj) {
        userPasswdObj.val("");
    }
}

function beforeReadCard() {
    var flag = $("#M2M_FLAG").val();
    if (flag === "1") {
        $.simcard.setNetTypeCode("07");
    }
    var sn = $("#SERIAL_NUMBER").val();
    $.simcard.setSerialNumber(sn);
    return true;
}

function beforeCheckSimCardNo(data) {
    var isWrited = data.get("IS_WRITED"); // 用来判断卡是否被写过
    if (isWrited === "1") {
        var simno = data.get("SIM_CARD_NO");
        $("#SIM_CARD_NO").val(simno);
        resetSIMCheck();
        checkSimCardNo(1);
    }
}

function afterWriteCard(data) {
    if (data.get("RESULT_CODE") === "0") {
        if ($.os.phone) {
            $.simcard.readSimCardPhone();
        } else {
            $.simcard.readSimCard();
        }
    }
}

// 重置SIM卡校验
function resetSIMCheck() {
    $("#SIM_CHECK_BTN").css("display", "");
    $("#SIM_SUCCESS_LABEL").css("display", "none");
    $("#SIM_ERROR_LABEL").css("display", "none");
}

// 校验SIM卡失败
function checkSIMFailed() {
    $("#SIM_CHECK_BTN").css("display", "none");
    $("#SIM_ERROR_LABEL").css("display", "");
}

// 校验SIM卡成功
function checkSIMSucceed() {
    $("#SIM_CHECK_BTN").css("display", "none");
    $("#SIM_SUCCESS_LABEL").css("display", "");
}

// SIM卡号校验
function checkSimCardNo(flag) {
    var checkResultCode = $("#CHECK_RESULT_CODE").val(); // 校验通过标识
    var personAuthFlag = $("#PERSON_AUTH_FLAG").val();   // 签约赠188号码活动新增

    var simCardNo = $("#SIM_CARD_NO").val();
    if (simCardNo === "" || simCardNo.length < 20) {
        if (flag === 1)
            MessageBox.alert("SIM卡号输入不正确！");
        return false;
    }

    var oldSimCardNo = $("#OLD_SIM_CARD_NO").val();
    if (isNull(oldSimCardNo)) {
        oldSimCardNo = "";
    }
    if (oldSimCardNo === simCardNo) {
        if (checkResultCode === "1")
            checkSIMSucceed();
        return false;
    }

    $.cssubmit.disabledSubmitBtn(true);
    $.feeMgr.clearFeeList("10"); // 防止多次点击校验产生多条费用

    var agentIdObj;
    var agentCode = "";
    var isAgent = $("#IS_AGENT").val();
    if (isAgent === "1")
        agentIdObj = $("#AGENT_DEPART_ID");  // 开户代理商编码
    else
        agentIdObj = $("#AGENT_DEPART_ID1"); // 开户代理商编码
    var agentId = agentIdObj.val();
    if (agentIdObj.length) {                 // 不为undefined，表示代理商开户
        if (agentId === "") {
            MessageBox.alert("请选择开户代理商！");
            return false;
        } else {
            agentCode = agentId.substring(0, 5);
        }
    }

    // 先检查服务号码是否校验通过
    var serialNumber = $("#SERIAL_NUMBER").val();
    if (serialNumber === "") {
        MessageBox.alert("请先校验新开户号码！");
        return false;
    }

    if (isNull(checkResultCode) && personAuthFlag === "false") // 签约赠188号码活动新增
        checkResultCode = "-1";

    if (checkResultCode === "-1") {
        MessageBox.alert("新开户号码未校验通过！");
        return false;
    }

    var params = "&CHECK_RESULT_CODE=" + checkResultCode
            + "&OLD_SIM_CARD_NO=" + oldSimCardNo + "&SIM_CARD_NO=" + simCardNo
            + "&SERIAL_NUMBER=" + serialNumber
            + "&M2M_FLAG=" + $("#M2M_FLAG").val()
            + "&AUTH_FOR_SALE_ACTIVE_TAG=" + $("#AUTH_FOR_SALE_ACTIVE_TAG").val()
            + "&SPEC_SN_SECTNO_SIM_FEE=" + $("#SPEC_SN_SECTNO_SIM_FEE").val()
            + "&NO_CARD_FEE_BRAND=" + $("#NO_CARD_FEE_BRAND").val()
            + "&OPEN_TYPE=" + $("#OPEN_TYPE").val()
            + "&PROV_OPEN_ADVANCE_PAY_FLAG=" + $("#PROV_OPEN_ADVANCE_PAY_FLAG").val()
            + "&PROV_OPEN_OPERFEE_FLAG=" + $("#PROV_OPEN_OPERFEE_FLAG").val()
            + "&RES_CHECK_BY_DEPART=" + $("#RES_CHECK_BY_DEPART").val()
            + '&AGENT_DEPART_ID=' + agentCode;
    $.beginPageLoading("SIM卡号校验中......");
    $.ajax.submit(null, "checkSimCardNo", params, "CheckSimCardNoHidePart",
        function (ajaxData) {
            var rtnData = ajaxData.get(0);

            $("#OLD_SIM_CARD_NO").val(simCardNo);
            $("#CHECK_RESULT_CODE").val(rtnData.get("CHECK_RESULT_CODE"));

            $.feeMgr.insertFee(PAGE_FEE_LIST.get("NUMBER_FEE"));
            $.feeMgr.insertFee(PAGE_FEE_LIST.get("PRODUCT_FEE"));
            if (rtnData.get("FEE")) {
                var feeData = $.DataMap();
                feeData.put("MODE", rtnData.get("FEE_MODE"));
                feeData.put("CODE", rtnData.get("FEE_TYPE_CODE"));
                feeData.put("FEE",  rtnData.get("FEE"));
                feeData.put("PAY",  rtnData.get("FEE"));
                feeData.put("TRADE_TYPE_CODE", "10");
                $.feeMgr.insertFee(feeData);
                PAGE_FEE_LIST.put("SIMCARD_FEE", $.feeMgr.cloneData(feeData));
            }

            checkSIMSucceed();

            // 产品结账日设置
            var acctDay = $("#ACCT_DAY").val();
            selectedElements.setAcctDayInfo(acctDay, "", "", "");
            $.cssubmit.disabledSubmitBtn(false);

            // POS机刷卡参数加载
            $.feeMgr.setPosParam("10", serialNumber, $("#EPARCHY_CODE").val());
            $.endPageLoading();

            // 处理密码卡 sunxin
            var cardPasswd = $("#CARD_PASSWD").val(); // 密码
            var passCode = $("#PASSCODE").val();      // 密码因子
            if (cardPasswd !== "" && passCode !== "") {
                MessageBox.confirm("提示信息", "该SIM卡为初始密码卡，是否将初始密码作为用户服务密码？",
                    function(btn){
                        if (btn === "ok") {
                            $("#DEFAULT_PWD_FLAG").val("1");  // 使用初始密码 sunxin
                            $("#PasswdPart").css("display", "none");
                        } else {
                            $("#DEFAULT_PWD_FLAG").val("0");  // 不使用初始密码 sunxin
                            $("#PasswdPart").css("display", "");
                        }
                    });
            } else {
                $("#DEFAULT_PWD_FLAG").val("0");              // 不使用初始密码 sunxin
                $("#PasswdPart").css("display", "");
            }
        },
        function (error_code, error_info, derror) {
            $.endPageLoading();
            checkSIMFailed();
            showDetailErrorInfo(error_code, error_info, derror);
        });
}
/****************************校验开户号码及SIM资源 结束***************************/

// 密码组件前校验
function passwdBeforeAction() {
    if ($("#PSPT_TYPE_CODE").val() === "") {
        MessageBox.alert("证件类型不能为空！");
        return false;
    }
    var psptId = $("#PSPT_ID").val();
    if (psptId === "") {
        MessageBox.alert("证件号码不能为空！");
        return false;
    }

    // 将值赋给组件处理
    var serialNumber = $("#SERIAL_NUMBER").val();
    $.password.setPasswordAttr(psptId, serialNumber);
    return true;
}

// 密码组件后赋值
function passwdAfterAction(data) {
    $("#USER_PASSWD").val(data.get("NEW_PASSWORD"));
}

function verifyenterprise() {
    $.custInfo.verifyEnterpriseCard();
}

function verifyorg() {
    $.custInfo.verifyOrgCard();
}

/**
 * REQ201707060020_关于年龄外经办人限制变更的优化
 * 判断是否在11岁（含）至120岁（不含）之间的
 * @param idCard
 * @param psptTypeCode
 */
function checkCustAge(idCard, psptTypeCode) {
    // 根据身份证  获取周岁
    var custAge = this.jsGetAge(idCard);
    return 11 <= custAge && custAge < 120; // 11岁（含）至120岁（不含）
}

/**
 * REQ201707060020_关于年龄外经办人限制变更的优化
 * 使用客户资料变更里面的方法(在生产上已经存在的方法)
 * @author  zhuoyinghi
 * @date 20170803
 * @param idCard
 * @returns
 */
function jsGetAge(idCard) {
    var returnAge;
    var bstr = idCard.substring(6, 14);
    var birthYear = bstr.substring(0, 4);
    var birthMonth = bstr.substring(4, 6);

    var d = new Date();
    var nowYear = d.getFullYear();
    var nowMonth = d.getMonth() + 1;

    if (nowYear === birthYear) {
        returnAge = 0; // 同年，则为0岁
    } else {
        var ageDiff = nowYear - birthYear;         // 年之差
        if (ageDiff > 0) {
            var monthDiff = nowMonth - birthMonth; // 月之差
            if (monthDiff <= 0) {
                returnAge = ageDiff - 1;
            } else {
                returnAge = ageDiff;
            }
        } else {
            returnAge = -1; // 返回-1，表示出生日期输入错误，晚于今天
        }
    }
    return returnAge;       // 返回周岁年龄
}

/**
 * 操作员个人用户开户-用户类型开户权限校验
 * @author zhaohj3
 * @date 2018-1-4 16:50:20
 */
function checkUserTypePriv() {
    var userTypeCode = $("#USER_TYPE_CODE").val();
    var privId = "";
    if (userTypeCode === "A") { // 测试机用户
        privId = "SYS_CRM_CREATE_USERTYPE_A";
    }

    if (privId !== "") {
        $.beginPageLoading("个人用户开户-用户类型开户权限校验中...");
        var param = "&PRIV_ID=" + privId;
        var hasPrivFlag = "true";
        $.ajax.submit(null, "hasPriv", param, null,
            function (ajaxData) {
                hasPrivFlag = ajaxData.get("HAS_PRIV");
                var staffId = ajaxData.get("STAFF_ID");
                if (hasPrivFlag === "false") {
                    MessageBox.alert("操作员[" + staffId + "]没有[个人用户开户-用户类型（测试机用户）开户权限]，权限编码为：[" + privId + "]");
                    $("#USER_TYPE_CODE").val("0");
                }
                $.endPageLoading();
            }, function (error_code, error_info) {
                MessageBox.error(error_code, error_info);
                $.endPageLoading();
            }, {async: false});

        if (hasPrivFlag === "false") {
            return false;
        }
    }
}

/**
 * 手机页面MOP个人开户办理定位
 * @author fusr
 * @date 2018-8-15 16:50:20
 */
$(function(){
	if($.os.phone){
		$('#MOP_LONGITUDE').val("0");
		$('#MOP_LATITUDE').val("0");
		var params={};
		params.callback='getLocationInfo';
		window.MBOP.getCurrentLocationInfo(JSON.stringify(params));
	}
});

function getLocationInfo(data){
	data=JSON.parse(data);
	if(data.exeResult == "0"||data.exeResult == 0){
		$('#MOP_LONGITUDE').val(data.longitude);
		$('#MOP_LATITUDE').val(data.latitude);
		var longitude = $('#MOP_LONGITUDE').val();
		var latitude  = $('#MOP_LATITUDE').val();
		if(longitude == "" || longitude == null){
			$('#MOP_LONGITUDE').val("0");
		}
		if(latitude == "" || latitude == null){
			$('#MOP_LATITUDE').val("0");
		}
		$('#MOP_LOCATIONINFO').val(data.locationInfo);
	} else{
		$('#MOP_LONGITUDE').val("0");
		$('#MOP_LATITUDE').val("0");
	}
	
}

function checkIMEI(){
    var newEid = $.trim($("#EID").val());
    var newImei = $.trim($("#IMEI").val());
    var checkFlag = $("#IS_CHECKED").val();
    if("1" == checkFlag){
        alert("校验请求已发送,请点击检索按钮查询结果！");
        return false;
    }
    if(newEid == ""){
        MessageBox.alert("提示",'新设备EID号不能为空！');
        return false;
    }
    if(newImei == ""){
        MessageBox.alert("提示",'新设备IMEI不能为空！');
        return false;
    }
    var expressionIMEI = /^[0-9]+.?[0-9]*$/;
    if(!expressionIMEI.test(newImei)){
        MessageBox.alert("提示信息",'IMEI格式错误！IMEI应为纯数字！');
        return false;
    }
    if((newImei.toString().length) != 15){
        MessageBox.alert("提示信息",'IMEI格式错误！IMEI应为15位数字！');
        return false;
    };

    $.beginPageLoading();
    setTimeout(function() {
        $.ajax.submit('eSimInfoPart,CheckeSimHidePart', 'verifyIMEI', null, '', function(data){
                var resultCode = data.get('X_RESULTCODE');
                if('0'==resultCode){
                    $("#IS_CHECKED").val("1"); //用来标记是否发送校验请求成功
                    $("#EID").attr("disabled","true");
                    $("#IMEI").attr("disabled","true");
                    MessageBox.alert('提示信息','发送校验请求成功,请点击检索按钮查询校验结果！');
                }else{
                    MessageBox.error("平台校验异常:" + data.get('X_RESULT_INFO'));
                }
                $.endPageLoading();
            },
            function(error_code,error_info){
                $.endPageLoading();
                MessageBox.alert(error_info);
            });
    }, 30);

}


function queryCheckResult(){
    var checkFlag = $("#IS_CHECKED").val();
    if("1" != checkFlag){
        alert("请先输入新设备的EID、IMEI号并校验！");
        return false;
    }
    $.beginPageLoading();
    setTimeout(function() {
        $.ajax.submit('eSimInfoPart,CheckeSimHidePart', 'queryCheckResult', null, '', function(data){
                var resultCode = data.get('X_RESULTCODE');
                if('0'==resultCode){
                    $("#REFRESH_ESIM_SERIAL_NUMBER").attr('disabled',false);
                    $("#REFRESH_ESIM_SERIAL_NUMBER").removeClass("e_dis");
                    $("#CHECK_ESIM_SERIAL_NUMBER").attr('disabled',false);
                    $("#CHECK_ESIM_SERIAL_NUMBER").removeClass("e_dis");
                }else if('-1'==resultCode){
                    MessageBox.error("平台校验校验失败:"+data.get('X_RESULT_INFO'));
                }
                else if('1'==resultCode){
                    MessageBox.alert("处理异常:"+data.get('X_RESULT_INFO'));
                }
                $.endPageLoading();
            },
            function(error_code,error_info){
                $.endPageLoading();
                MessageBox.alert(error_info);
            });
    }, 30);
}

function refreshSN() {
    $.ajax.submit(null, 'getESIMSerialNumber', null, 'SNPart', function(data){
            $.endPageLoading();
        },
        function(error_code,error_info){
            $.endPageLoading();
            MessageBox.alert("提示",error_info);
        });
}


function checkEsimSerialNumber() {
    var serialNumber = $("#SERIAL_NUMBER").val();
    if($.os.pad || $.os.phone){
        var param = '&SERIAL_NUMBER=' + serialNumber + '&OPEN_TYPE='+$("#OPEN_TYPE").val() +"&LIMIT_PRODUCT_ID="+$("#LIMIT_PRODUCT_ID").val()+"&LIMIT_BEAUTIFUL_SN="+$("#LIMIT_BEAUTIFUL_SN").val()+"&OPEN_TERMINAL=phone";
    }else{
        var param = '&SERIAL_NUMBER=' + serialNumber + '&OPEN_TYPE='+$("#OPEN_TYPE").val() +"&LIMIT_PRODUCT_ID="+$("#LIMIT_PRODUCT_ID").val()+"&LIMIT_BEAUTIFUL_SN="+$("#LIMIT_BEAUTIFUL_SN").val();
    }
    $("#CHECK_RESULT_CODE").val("-1");	//设置号码未校验
    $.ajax.submit(null, "checkSerialNumber", param, "CheckSimCardNoHidePart,CheckSerialNumberHidePart,ProductTypePart,SalePackage", function(ajaxData){
            // $.endPageLoading();
            // var data0 = data.get(0);
            // var simCardNo =data0.get("SIM_CARD_NO");
            // if(!isNull(simCardNo)){
            //     $("#SIM_CARD_NO").val(simCardNo);
            //     $("#EPARCHY_CODE").val(data0.get("EPARCHY_CODE"));
            //     $("#IMSI").val(data0.get("IMSI"));
            //     $("#KI").val(data0.get("KI"));
            //     $("#RES_KIND_CODE").val(data0.get("RES_KIND_CODE"));
            //     $("#RES_KIND_NAME").val(data0.get("RES_KIND_NAME"));
            //     $("#RES_TYPE_CODE").val(data0.get("RES_TYPE_CODE"));
            //     $("#OPC_VALUE").val(data0.get("OPC_VALUE"));
            //     $("#OPC_CODE").val(data0.get("OPC_CODE"));
            //     $("#FLAG_4G").val("1");
            //     $("#CHECK_RESULT_CODE").val("1"); //SIM卡校验通过
            // }
            // 绑定"更多产品"和"变更"按钮点击事件
            $("#SELECT_PRODUCT_BTN,#CHANGE_PRODUCT_BTN").tap(checkBeforeSelectProduct);
            $("#HIDE_HOT_RECOMM_AREA").css("display", "");

            // 增加开户可提示相关信息（规则已终止，省略）
            $("#OLD_SERIAL_NUMBER").val(serialNumber);

            // 校验完后界面部分数据处理
            var rtnData = ajaxData.get(0);
            setAjaxAtferCheckMphone(rtnData);
            $.endPageLoading();

            // 处理手机号码预配产品 sunxin
            var singleProduct = rtnData.get("EXISTS_SINGLE_PRODUCT");
            if (singleProduct) {
                afterChangeProduct(singleProduct, rtnData.get("PRODUCT_NAME"), rtnData.get("PRODUCT_DESC"), rtnData.get("BRAND_CODE"));
                $("#CHANGE_PRODUCT_BTN").attr("disabled", true).css("display", "none");
                $("#PRODUCT_TYPE_CODE").attr("disabled", true).css("display", "none");
                $.cssubmit.disabledSubmitBtn(false);
            }
            if ($("#PAY_MODE_CODE").val() === "0") {
                $("#SUPER_BANK_CODE").attr("disabled", true);
                $("#BANK_CODE").attr("disabled", true);
                $("#BANK_ACCT_NO").attr("disabled", true);
            }

            // 网上选号绑定了套餐包默认勾选
            var salePackageObj = $("#SalePackage");
            var xCoding = rtnData.get("A_X_CODING_STR");
            if (xCoding) {
                salePackageObj.css("display", "");
                var saleProductId = xCoding.split("|")[0];
                var salePackageId = xCoding.split("|")[1];
                $("#SALE_PRODUCT_ID").val(saleProductId);
                $("#SALE_PACKAGE_ID").val(salePackageId);
                $("#BIND_SALE_TAG").val("1"); // 绑定营销活动

                var checkboxes = $("#packageTable input[name=packageCheckbox]"); // 获取表格全部复选框
                checkboxes.each(function () {
                    if (this.value === salePackageId) {
                        this.click();
                        $(this).parent().parent().addClass("on");
                    }
                });

                // 无更改权限时隐藏其他可选包并设置营销包必选 wenhj
                if ($("#SYSCHANGPACKAGE").val() !== "1") {
                    checkboxes.each(function () {
                        if (this.value !== salePackageId)
                            $(this).parent().parent().css("display", "none");
                        else if (rtnData.get("SYSCHANGPACKAGE4") !== "1")
                            $(this).parent().parent().attr("disabled", true);
                    });
                }
            } else {
                $("#SALE_PRODUCT_ID").val("");
                $("#SALE_PACKAGE_ID").val("");
                $("#BIND_SALE_TAG").val("0");
                salePackageObj.css("display", "none");
            }



            var checkResultCode = $("#CHECK_RESULT_CODE").val(); // 校验通过标识
            var oldSimCardNo = $("#OLD_SIM_CARD_NO").val();
            var simCardNo = $("#SIM_CARD_NO").val();
            var agentIdObj;
            var agentCode = "";
            var isAgent = $("#IS_AGENT").val();
            if (isAgent === "1")
                agentIdObj = $("#AGENT_DEPART_ID");  // 开户代理商编码
            else
                agentIdObj = $("#AGENT_DEPART_ID1"); // 开户代理商编码
            var agentId = agentIdObj.val();
            if (agentIdObj.length) {                 // 不为undefined，表示代理商开户
                if (agentId === "") {
                    MessageBox.alert("请选择开户代理商！");
                    return false;
                } else {
                    agentCode = agentId.substring(0, 5);
                }
            }
            var params = "&CHECK_RESULT_CODE=" + checkResultCode
                + "&OLD_SIM_CARD_NO=" + oldSimCardNo + "&SIM_CARD_NO=" + simCardNo
                + "&SERIAL_NUMBER=" + serialNumber
                + "&M2M_FLAG=" + $("#M2M_FLAG").val()
                + "&AUTH_FOR_SALE_ACTIVE_TAG=" + $("#AUTH_FOR_SALE_ACTIVE_TAG").val()
                + "&SPEC_SN_SECTNO_SIM_FEE=" + $("#SPEC_SN_SECTNO_SIM_FEE").val()
                + "&NO_CARD_FEE_BRAND=" + $("#NO_CARD_FEE_BRAND").val()
                + "&OPEN_TYPE=" + $("#OPEN_TYPE").val()
                + "&PROV_OPEN_ADVANCE_PAY_FLAG=" + $("#PROV_OPEN_ADVANCE_PAY_FLAG").val()
                + "&PROV_OPEN_OPERFEE_FLAG=" + $("#PROV_OPEN_OPERFEE_FLAG").val()
                + "&RES_CHECK_BY_DEPART=" + $("#RES_CHECK_BY_DEPART").val()
                + '&AGENT_DEPART_ID=' + agentCode;
            $.beginPageLoading("SIM卡号校验中......");
            $.ajax.submit(null, "checkSimCardNo", params, "CheckSimCardNoHidePart",
                function (ajaxData) {
                    var rtnData = ajaxData.get(0);

                    $("#OLD_SIM_CARD_NO").val(simCardNo);
                    $("#CHECK_RESULT_CODE").val(rtnData.get("CHECK_RESULT_CODE"));
                    $("#SIM_CARD_NO").val(rtnData.get("SIM_CARD_NO"));
                    var simCardNoObj = $("#SIM_CARD_NO");
                    if (!isNull(simCardNo)) {
                        simCardNoObj.attr("disabled", true);
                    }
                    //     $("#EPARCHY_CODE").val(data0.get("EPARCHY_CODE"));
                    //     $("#IMSI").val(data0.get("IMSI"));
                    //     $("#KI").val(data0.get("KI"));
                    //     $("#RES_KIND_CODE").val(data0.get("RES_KIND_CODE"));
                    //     $("#RES_KIND_NAME").val(data0.get("RES_KIND_NAME"));
                    //     $("#RES_TYPE_CODE").val(data0.get("RES_TYPE_CODE"));
                    //     $("#OPC_VALUE").val(data0.get("OPC_VALUE"));
                    //     $("#OPC_CODE").val(data0.get("OPC_CODE"));
                    $("#FLAG_4G").val("1");
                    //     $("#CHECK_RESULT_CODE").val("1"); //SIM卡校验通过

                    $.feeMgr.insertFee(PAGE_FEE_LIST.get("NUMBER_FEE"));
                    $.feeMgr.insertFee(PAGE_FEE_LIST.get("PRODUCT_FEE"));
                    if (rtnData.get("FEE")) {
                        var feeData = $.DataMap();
                        feeData.put("MODE", rtnData.get("FEE_MODE"));
                        feeData.put("CODE", rtnData.get("FEE_TYPE_CODE"));
                        feeData.put("FEE",  rtnData.get("FEE"));
                        feeData.put("PAY",  rtnData.get("FEE"));
                        feeData.put("TRADE_TYPE_CODE", "10");
                        $.feeMgr.insertFee(feeData);
                        PAGE_FEE_LIST.put("SIMCARD_FEE", $.feeMgr.cloneData(feeData));
                    }

                    checkSIMSucceed();

                    // 产品结账日设置
                    var acctDay = $("#ACCT_DAY").val();
                    selectedElements.setAcctDayInfo(acctDay, "", "", "");
                    $.cssubmit.disabledSubmitBtn(false);

                    // POS机刷卡参数加载
                    $.feeMgr.setPosParam("10", serialNumber, $("#EPARCHY_CODE").val());
                    $.endPageLoading();

                    // 处理密码卡 sunxin
                    var cardPasswd = $("#CARD_PASSWD").val(); // 密码
                    var passCode = $("#PASSCODE").val();      // 密码因子
                    if (cardPasswd !== "" && passCode !== "") {
                        MessageBox.confirm("提示信息", "该SIM卡为初始密码卡，是否将初始密码作为用户服务密码？",
                            function(btn){
                                if (btn === "ok") {
                                    $("#DEFAULT_PWD_FLAG").val("1");  // 使用初始密码 sunxin
                                    $("#PasswdPart").css("display", "none");
                                } else {
                                    $("#DEFAULT_PWD_FLAG").val("0");  // 不使用初始密码 sunxin
                                    $("#PasswdPart").css("display", "");
                                }
                            });
                    } else {
                        $("#DEFAULT_PWD_FLAG").val("0");              // 不使用初始密码 sunxin
                        $("#PasswdPart").css("display", "");
                    }

                    /**
                     * REQ201602290007 关于入网业务人证一致性核验提醒的需求
                     * chenxy3 2016-03-08
                     */
                    $.beginPageLoading("正在查询数据...");
                    $.ajax.submit(null, "checkNeedBeforeCheck", null, null,
                        function (ajaxData) {
                            var flag = ajaxData.get("PARA_CODE1");
                            if (flag === "1") {
                                var params = "&TRADE_ID=10" + "&EPARCHY_CODE=0898"
                                    + "&TRADE_TYPE_CODE=10" + "&RAN=" + Math.random(); // RAN参数保证每次弹窗都刷新页面内容
                                popupPage("业务检查", "beforecheck.BeforeCheckNew", "init", params, null, "full");
                            }
                            $.endPageLoading();
                        },
                        function (error_code, error_info) {
                            $.endPageLoading();
                            MessageBox.error(error_code, error_info);
                        });
                },
                function (error_code, error_info, derror) {
                    $.endPageLoading();
                    checkSIMFailed();
                    showDetailErrorInfo(error_code, error_info, derror);
                });
        },
        function(error_code,error_info){
            $.endPageLoading();
            MessageBox.alert("提示",error_info);
        });
}

function changeSerialNumber(){
    //手机号码改变
    $("#CHECK_RESULT_CODE").val("-1");//号码未校验
    $("#SIM_CARD_NO").val("");
}

function isNull(str){
    if(str==undefined || str==null || str=="") {
        return true;
    }
    return false;
}
