function Familynewims(tplObj) {
    Role.apply(this, ["3", tplObj.type, tplObj.partId]);
    var tpl = tplObj.tpl;
    var eparchyCode = tplObj.eparchyCode;
    var type = tplObj.type;
    var mainSn = tplObj.sn;//手机号码
    var areaCode = tplObj.areaCode;
    this.sn = '';//保存固话号码
    var uniqueid = this.uniqueId;
    this.custInfo = $.DataMap();
    this.share = false;
    this.type = type;
    this.mainSn = mainSn;
    this.cardInfoOver = false;//证件信息popup页面是否填写完整

    this.eparchyCode = eparchyCode;// 初始化父类字段
    this.areaCode = areaCode;
    this.imsUserId = tplObj.imsUserId;
    // 执行自己的操作;
    this.initDomTree(tpl);

    this.initOtherInfos(this.uniqueId, type, eparchyCode);
    //this.initinfos(type, mainSn, this.uniqueId);
    this.bindEvents(mainSn, areaCode, uniqueid);

}

Familynewims.tpl = 'family/component/tpl/familynewims.tpl';
Familynewims.prototype = {
    initOtherInfos: function (idx, type, eparchyCode) {
        var that = this;


        if (type == "NEW")// 新装固话
        {

        }

    },
    // 初始化方法
    initinfos: function (type, sn, idx) {
        var that = this;
        OfferPop.initSelectedOffers(that);
    },

    // 绑定事件
    bindEvents: function (sn, areaCode, uniqueid) {
        var that = this;
        if (sn == "" || sn == undefined || areaCode == "" || areaCode == undefined) {
            MessageBox.error("提示", "参数：areaCode 或 sn 未获取到，请检查");
        }
        //模块依赖取不到，暂时先这样
        this.find("button[handle=close]").bind("click", function () {
            that.destroy();
        });
        this.find("button[name=CHECK_BTN]").bind("click", function () {//校验固话号码
            that.checkImsNum(that, areaCode);
        });
        this.find("button[name=AUTH_CHECK]").bind("click", function () {//认证证件信息
            that.custInfoForIms(that, sn);
        });
        this.find("div[name=SelectedOffers]").bind("DOMNodeInserted", function () {
            that.showOfferInput(that);
        });
        this.find("input[name=is_TT_TRANSFER]").bind("change", function () {
            that.setTTtransferValue(that);
        });
    },
    setTTtransferValue: function (that) {
        var checked = this.find("input[name=is_TT_TRANSFER]")[0].checked;
        if (checked) {
            this.find("input[name=TT_TRANSFER]").val("1");
        } else {
            this.find("input[name=TT_TRANSFER]").val("0");
        }
    },
    showOfferInput: function (that) {
        var offerObj = that.find("div[name=SelectedOfferList] div[class=title]");
        that.find("input[name=IMS_OFFER_NAME]").val(offerObj.html());
    },
    custInfoForIms: function (that, sn) {
        //弹出客户信息页面
        var serial_number = sn;
        custInfoAdepter.popupPageCustInfoPage(that, sn);
    },
    checkImsNum: function (that, areaCode) {//校验固话号码

        if (that.find("input[name=FIX_NUMBER]").val() == "") {
            MessageBox.alert("提示", '请先填写固话号码!');
            return;
        }
        var oldFixNumber = that.find("input[name=OLD_FIX_NUMBER]").val();
        var fixNumber = that.find("input[name=FIX_NUMBER]").val();

        if (null != oldFixNumber && '' != oldFixNumber) {
            if (oldFixNumber == fixNumber) {
                MessageBox.alert("提示", '该号码已经校验通过，不需要再次校验!');
                return false
            }
        }
        //$.feeMgr.clearFeeList("6800");
        $.beginPageLoading("校验固话号码...");
        var cityCodeRsrvStr4 = areaCode;
        var param = "&FIX_NUMBER=" + fixNumber + "&CITYCODE_RSRVSTR4=" + cityCodeRsrvStr4;
        hhSubmit(null, window.constdata.HANDLER, "checkFixPhoneNum", param, function (rtnData) {
            $.endPageLoading();
            if (rtnData != null && rtnData.length > 0) {//吉祥号码待处理
                if (rtnData.get("RESULT_CODE") == "1") {
                    that.find("input[name=OLD_FIX_NUMBER]").val(fixNumber);
                    that.sn = fixNumber;
                    MessageBox.alert("提示", rtnData.get("RESULT_INFO"));
                    //$.cssubmit.disabledSubmitBtn(false,"submitButton");
                } else {
                    MessageBox.alert("提示", rtnData.get("RESULT_INFO"));
                    that.find("input[name=FIX_NUMBER]").val("");
                    that.find("input[name=OLD_FIX_NUMBER]").val("");
                }
            }
        }, function (error_code, error_info, detail) {
            $.endPageLoading();
            $.MessageBox.error(error_code, error_info);
            that.find("input[name=FIX_NUMBER]").val("");
            that.find("input[name=OLD_FIX_NUMBER]").val("");
        });
    },
    getImportData: function () {
        var that = this;
        var roleType = that.find("input[name=ROLE_TYPE]").val();

        var param = $.DataMap();
        var elements = that.getOffers();
        // 公共参数

        param.put("ROLE_CODE", "3");
        param.put("ROLE_TYPE", roleType);// 已有 ？ 新增
        param.put("EPARCHY_CODE", this.eparchyCode);

        if ("NEW" == roleType)// 新装
        {
            param.put("FIX_NUMBER", that.find("input[name=OLD_FIX_NUMBER]").val());
            param.put("TT_TRANSFER", that.find("input[name=TT_TRANSFER]").val());

            param.put("PSPT_ID", that.find("input[name=PSPT_ID]").val());
            param.put("PSPT_TYPE_CODE", that.find("input[name=PSPT_TYPE_CODE]").val());
            param.put("CUST_NAME", that.find("input[name=CUST_NAME]").val());
            param.put("BIRTHDAY", that.find("input[name=BIRTHDAY]").val());
            param.put("PSPT_END_DATE", that.find("input[name=PSPT_END_DATE]").val());
            param.put("PSPT_ADDR", that.find("input[name=PSPT_ADDR]").val());
            param.put("SEX", that.find("input[name=SEX]").val());
            param.put("FOLK_CODE", that.find("input[name=FOLK_CODE]").val());
            param.put("AGENT_CUST_NAME", that.find("input[name=AGENT_CUST_NAME]").val());
            param.put("AGENT_PSPT_TYPE_CODE", that.find("input[name=AGENT_PSPT_TYPE_CODE]").val());
            param.put("AGENT_PSPT_ID", that.find("input[name=AGENT_PSPT_ID]").val());

            param.put("AGENT_PSPT_ADDR", that.find("input[name=AGENT_PSPT_ADDR]").val());
            param.put("USE", that.find("input[name=USE]").val());
            param.put("USE_PSPT_TYPE_CODE", that.find("input[name=USE_PSPT_TYPE_CODE]").val());
            param.put("USE_PSPT_ID", that.find("input[name=USE_PSPT_ID]").val());
            param.put("USE_PSPT_ADDR", that.find("input[name=USE_PSPT_ADDR]").val());

            param.put("PIC_ID", that.find("input[name=PIC_ID]").val());
            param.put("AGENT_PIC_ID", that.find("input[name=AGENT_PIC_ID]").val());
        }
        if ("OLD" == roleType) {
        }
        return param;
    },
    checkSubmitData: function (data) {
        var that = this;
        var ims_sn = this.sn;
        if (ims_sn == "") {
            MessageBox.alert("提示", "未获取到校验通过的固话号码,请检查");
            return false;
        }
        if (ims_sn != "" && ims_sn.length == 8) {
            this.sn = "0898" + ims_sn;
        }
        if (!this.cardInfoOver && this.type == "NEW") {
            MessageBox.alert("提示", "新增固话角色时证件信息未填写完整，请检查");
            return false;
        }
        var agentPicIdObj = that.find("input[name=AGENT_PIC_ID]");
        if (agentPicIdObj.val() === "") {
            agentPicIdObj.val("AGENT_PIC_ID_value");
        }


        var birthdayObj = that.find("input[name=BIRTH_DAY]");
        if (birthdayObj.val() === "") {
            birthdayObj.val("1900-01-01");
        }
        if (agentPicIdObj.val() === "AGENT_PIC_ID_value") {
            agentPicIdObj.val("");
        }

        var cmpTag = "1";
        hhSubmit(null, window.constdata.HANDLER, "isCmpPic", null, null,
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

        if (cmpTag === "0") {
            var picId = that.find("input[name=PIC_ID]").val();
            if (picId != null && picId === "ERROR") { // 客户摄像失败
                MessageBox.error("告警提示", "客户" + that.find("input[name=PIC_STREAM]").val());
                return false;
            }

            var psptTypeCode = that.find("input[name=ID_CARD_NO]").val();
            var agentPicId = agentPicIdObj.val();
            var agentTypeCode = that.find("input[name=AGENT_PSPT_TYPE_CODE]").val();

            if ((psptTypeCode === "0" || psptTypeCode === "1")
                && picId === "") {
                var custName = that.find("input[name=AGENT_CUST_NAME]").val();   // 经办人名称
                var psptId = that.find("input[name=AGENT_PSPT_ID]").val();// 经办人证件号码
                var agentPsptAddr = that.find("input[name=AGENT_PSPT_ADDR]").val();// 经办人证件地址

                if (agentTypeCode === "" && custName === "" && psptId === ""
                    && agentPsptAddr === "") {
                    MessageBox.error("告警提示", "请进行客户或经办人摄像！");
                    return false;
                }
                if ((agentTypeCode === "0" || agentTypeCode === "1")
                    && agentPicId === "") {
                    MessageBox.error("告警提示", "请进行客户或经办人摄像！");
                    return false;
                }
            }

            if (agentPicId != null && agentPicId === "ERROR") { // 经办人摄像失败
                MessageBox.error("告警提示", "经办人" + that.find("input[name=AGENT_PIC_STREAM]").val());
                return false;
            }

            if ((agentTypeCode === "0" || agentTypeCode === "1")
                && agentPicId === "") { // 经办人未摄像
                MessageBox.error("告警提示", "请进行经办人摄像！");
                return false;
            }

            if (psptTypeCode === "2" && picId === "") { // 未进行客户摄像
                var custPsptId = that.find("input[name=ID_CARD_NO]").val();   // 客户证件号码
                if (custPsptId !== ""
                    && checkCustAge(custPsptId, psptTypeCode)) {
                    // 11岁（含）至120岁（不含）之间的用户必须通过验证才可以办理（同身份证一致）
                    MessageBox.error("告警提示", "请进行客户摄像!");
                    return false;
                }
            }
        }

        var productId = $("#PRODUCT_ID").val();
        //var topSetBoxSaleActiveList = $("#TOP_SET_BOX_SALE_ACTIVE_ID").val();
        if (!productId || productId == "") {
            MessageBox.alert('提示', "IMS固话产品不能为空！");
            return false;
        }
        if (productId == "84018059") {
            var feeFlag = checkZnyxFee(productId);
            if (!feeFlag) {
                return false;
            }
            var oldResId = $("#OLD_RES_ID").val();
            var resId = $("#RES_ID").val();
            if (resId == "") {
                MessageBox.alert('提示', "该固话产品为和家固话（智能音箱版）时，请输入音箱串号并校验！");
                return false;
            } else {
                if (resId != oldResId && topSetBoxSaleActiveList == "18059") {
                    MessageBox.alert('提示', "串号校验不通过，请重新校验！");
                    return false;
                }
            }
        }

        return true;
    },

    //校验办理智能音箱套餐时，手机号码的余额是否小于9元
    checkZnyxFee: function (productId) {

    },
    checkCustAge: function (idCard, psptTypeCode) {
        // 根据身份证  获取周岁
        var custAge = this.jsGetAge(idCard);
        return 11 <= custAge && custAge < 120; // 11岁（含）至120岁（不含）
    },
    jsGetAge: function (idCard) {
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
    },
    getRefreshTplData: function () {
        var btnStrs = "x";
        // 填充模板数据
        var data = {
            title: '固话',
            wideAcctId: this.widenetacctid,
            sn: this.widenetacctid,
            isLtIE9: window.constdata.isLtIE9,
            changeOffer: '商品选择',
            btnStrs: btnStrs,
            type: this.type,
            imsSn: this.sn
        };

        return data;
    },
    setPopupReturnValue: function (obj) {    //接收证件信息,缺哪项后面再补
        var that = this;
        this.cardInfoOver = obj.CARD_INFO_OVER;
        that.find("input[name=AUTH_FLOW]").val("已认证");
        that.find("input[name=pspt_type]").val(obj.PSPT_TYPE_TEXT == undefined ? "本地身份证" : obj.PSPT_TYPE_TEXT);
        that.find("input[name=cust_name]").val(obj.CUST_NAME);
        that.find("input[name=ID_CARD]").val(obj.ID_CARD_NO);

        that.find("input[name=PSPT_ID]").val(obj.ID_CARD_NO);
        that.find("input[name=CUST_NAME]").val(obj.CUST_NAME);
        that.find("input[name=PSPT_TYPE_CODE]").val(obj.PSPT_TYPE_CODE);
        that.find("input[name=BIRTHDAY]").val(obj.BIRTHDAY);

        that.find("input[name=PSPT_END_DATE]").val(obj.PSPT_END_DATE);
        that.find("input[name=PSPT_ADDR]").val(obj.PSPT_ADDR);
        that.find("input[name=SEX]").val(obj.SEX);
        that.find("input[name=FOLK_CODE]").val(obj.FOLK_CODE);

        that.find("input[name=AGENT_CUST_NAME]").val(obj.AGENT_CUST_NAME);
        that.find("input[name=AGENT_PSPT_TYPE_CODE]").val(obj.AGENT_PSPT_TYPE_CODE);
        that.find("input[name=AGENT_PSPT_ID]").val(obj.AGENT_PSPT_ID);
        that.find("input[name=AGENT_PSPT_ADDR]").val(obj.AGENT_PSPT_ADDR);

        that.find("input[name=USE]").val(obj.USE);
        that.find("input[name=USE_PSPT_TYPE_CODE]").val(obj.USE_PSPT_TYPE_CODE);
        that.find("input[name=USE_PSPT_ID]").val(obj.USE_PSPT_ID);
        that.find("input[name=USE_PSPT_ADDR]").val(obj.USE_PSPT_ADDR);

        that.find("input[name=AGENT_PIC_ID]").val(obj.AGENT_PIC_ID);
        that.find("input[name=PIC_ID]").val(obj.PIC_ID);
        that.find("input[name=PIC_STREAM]").val(obj.PIC_STREAM);
        that.find("input[name=AGENT_PIC_STREAM]").val(obj.AGENT_PIC_STREAM);

    },
    destroy: function () {//释放固话预占
        var that = this;
        var ims_sn = that.find("input[name=OLD_FIX_NUMBER]").val();
        if (ims_sn != "") {
            hhSubmit(null, window.constdata.HANDLER, "relaseImsOccupy", "&FIX_NUMBER=" + ims_sn, function (rtnData) {
                $.endPageLoading();
                if (rtnData != null && rtnData.length > 0) {
                    if (rtnData.get("X_RESULTCODE") == '0') {
                        //释放成功，暂不处理
                    }
                }
            }, function (error_code, error_info, detail) {
                $.endPageLoading();
                $.MessageBox.error(error_code, error_info);

            });
        }
    }
}

//针对证件信息回传新建对象
var custInfoAdepter = function () {
    var srcObj = null;

    function setPopupReturnValueToIms(obj) {
        srcObj.setPopupReturnValue(obj);
    }

    function popupPageCustInfoPage(that, sn) {
        srcObj = that;
        popupPage('客户信息', 'family.common.CustInfoFieldIms', 'init', '&SERIAL_NUMBER=' + sn, '', 'c_popup c_popup-full', '', '');
    }

    return {
        setPopupReturnValueToIms: setPopupReturnValueToIms,
        popupPageCustInfoPage: popupPageCustInfoPage
    }
}();