function Familyexistims(tplObj) {
    Role.apply(this, ["3", tplObj.type, tplObj.partId]);
    var tpl = tplObj.tpl;
    var eparchyCode = tplObj.eparchyCode;
    var type = tplObj.type;
    var mainSn = tplObj.sn;//手机号码
    var areaCode = tplObj.areaCode;
    this.sn = tplObj.imsSn;//存量固话号码
    var uniqueid = this.uniqueId;
    this.share = false;
    this.type = type;
    this.mainSn = mainSn;

    this.eparchyCode = eparchyCode;// 初始化父类字段
    this.areaCode = areaCode;
    this.imsUserId = tplObj.imsUserId;
    // 执行自己的操作;
    this.initDomTree(tpl);

    this.initOtherInfos(this.uniqueId, type, eparchyCode);
    //this.initinfos(type, mainSn, this.uniqueId);
    this.bindEvents(mainSn, uniqueid);

}

Familyexistims.tpl = 'family/component/tpl/familyexistims.tpl';
Familyexistims.prototype = {
    initOtherInfos: function (idx, type, eparchyCode) {
        var that = this;
        if (type == "OLD")// 已有固话
        {
            var ims_sn = this.sn;
            var ims_userid = this.imsUserId;
            if (ims_userid == undefined || ims_userid == "") {
                MessageBox.alert("提示", "已有固话userId未获取到，请检查");
                return;
            }
            $.beginPageLoading("加载固话已有商品信息...");
            var param = "&IMS_USER_ID=" + ims_userid + "&PHONE_NUMBER="+this.mainSn+"&EPARCHY_CODE=" + eparchyCode;
            hhSubmit(null, window.constdata.HANDLER, "queryImsExistOffers", param, function (rtnData) {
                $.endPageLoading();
                if (rtnData != null && rtnData.length > 0) {
                    that.find("li[name=EXIST_IMS_OFFER]").css("display","block");
                    that.find("input[name=FIX_NUMBER]").attr("disabled",true);
                    that.find("input[name=EXIST_IMS_OFFER_NAME]").attr("disabled",true);
                    that.find("input[name=EXIST_IMS_OFFER_NAME]").val("【"+rtnData.get("PRODUCT_ID")+"】"+rtnData.get("PRODUCT_NAME"));
                    that.find("input[name=MOBILE_PRODUCT_ID]").val(rtnData.get("MOBILE_PRODUCT_ID"));
                    that.find("input[name=IMS_PRODUCT_ID]").val(rtnData.get("PRODUCT_ID"));
                }
            }, function (error_code, error_info, detail) {
                $.endPageLoading();
                $.MessageBox.error(error_code, error_info);
            });
        }

    },
    // 初始化方法
    initinfos: function (type, sn, idx) {
        var that = this;
        OfferPop.initSelectedOffers(that);
    },

    // 绑定事件
    bindEvents: function (sn, uniqueid) {
        var that = this;
        //模块依赖取不到，暂时先这样
        this.find("button[handle=close]").bind("click", function () {
            that.destroy();
        });

        this.find("div[name=SelectedOffers]").bind("DOMNodeInserted", function () {
            that.showOfferInput(that);
        });
    },
    showOfferInput: function (that) {
        var offerObj = that.find("div[name=SelectedOfferList] div[class=title]");
        that.find("input[name=IMS_OFFER_NAME]").val(offerObj.html());
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
        }
        if ("OLD" == roleType) {
            param.put("WIDE_SERIAL_NUMBER", this.sn);//完整固话号码
            param.put("USER_ID",this.imsUserId);//固话userId
            param.put("MOBILE_PRODUCT_ID",this.find("input[name=MOBILE_PRODUCT_ID]").val());
            param.put("IMS_PRODUCT_ID",this.find("input[name=IMS_PRODUCT_ID]")).val();
        }
        return param;
    },
    checkSubmitData: function (data) {
        //1,校验固话号码信息   2,校验商品信息
        var that = this;
        var ims_sn = this.sn;
        if (ims_sn == "") {
            MessageBox.alert("提示", "未获取到校验通过的固话号码,请检查");
            return false;
        }
        if (ims_sn != "" && ims_sn.length == 8) {
            this.sn = "0898" + ims_sn;
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
    getRefreshTplData: function () {
        var btnStrs = "x";

        // 填充模板数据
        var data = {
            title: '已有固话',
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
}
