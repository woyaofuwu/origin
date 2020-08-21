function Phone(tplObj) {
    Role.apply(this, ["1", "OLD", tplObj.partId]);
    var tpl = tplObj.tpl;
    var sn = tplObj.sn;
    var eparchyCode = tplObj.eparchyCode;
    var isFmMgr = tplObj.isFmMgr;
    this.sn = sn;
    this.eparchyCode = eparchyCode;
    this.isFmMgr = isFmMgr;
    this.isNeedSelectOffer = true;
    this.isCheckTag = false;//手机检验通过标志，存在管理员检验不过可以添加宽带情况   刚哥来了看是否需要
    this.initDomTree(tpl);
}

Phone.tpl = "family/component/tpl/familyphone.tpl";

Phone.prototype = {
    getImportData: function () {
        //获取需要提交数据
        var data = $.DataMap();
        return data;
    },
    checkSubmitData: function () {
        //1.填写手机号码是否初始化加载校验
        var phoneSn = this.find("[name=PHONE_NUM]").val();
        if (!this.checkPhoneSn(phoneSn)) {
            return false;
        }
        //2.填写手机号码是否和初始化号码一致
        if (phoneSn && (this.sn == null || this.sn.length == 0)) {
            MessageBox.alert("提交提示", "请点击查询校验手机成员号码！");
            return false;
        }
        if (phoneSn && this.sn && phoneSn != this.sn) {
            MessageBox.alert("提交提示", "填写的号码和已校验号码不一致，请重新查询！");
            return false;
        }

        if (!this.isCheckTag) {
            MessageBox.alert("提交提示", "请先校验手机成员号码！");
            return false;
        }

        return true;
    },
    getRefreshTplData: function () {
        //填充模板数据
        var btnStrs = 'SHARE,PAY';
        var title = "[管理员]";
        if (!this.isFmMgr) {
            btnStrs += ",x";
            title = "[普通]";
        }
        btnStrs += OfferPop.getExistOffersRoleTypeStrs(['2_NEW', '2_OLD']);

        var data = {
            title: title,
            sn: this.sn,
            btnStrs: btnStrs
        };
        return data;
    },
    createSubRole: function (roleCode, roleType) {

        if (!this.checkSubmitData()) {
            return false;
        }
        if (!roleCheck.checkSubRolesCount(this, roleCode)) {
            return false;
        }
        if (!roleCheck.checkRoleCountLimit(roleCode)) {
            return false;
        }

        //非管理员新开按钮已经隐藏掉了，再加下检验
        if (roleType == "NEW" && !this.isFmMgr) {
            MessageBox.alert("提示信息", this.sn + "不是管理员号码，不允许新开宽带！");
            return false;
        }

        if (roleCode == "2") {
            var part = this.find("[name=BuildWideNetRolePart]");
            if (roleType == "NEW") {
                //新增宽带规则检验
                if (this.checkIsCanAddNewWide()) {
                    var widenet = common.createRole(WidenetNew, {partId: part, sn: this.sn, eparchyCode: this.eparchyCode, type: roleType, isFmMgr: this.isFmMgr});
                    return widenet;
                }
            } else {
                var that = this;
                selectExistRole.showPop(that, roleCode, roleType);//已有宽带pop框
            }
        }
    },
    afterInitDom: function () {
        var _self = this;
        this.find("[handle=query]").bind("tap", function (n) {
            var handle = this.getAttribute("handle");
            if (handle != null && handle != "") {
                _self[handle].call(_self, this);
            }
        });
        if (this.isFmMgr) {
            this.find("[name=PHONE_NUM]").attr("disabled", true);
            this.find("[handle=query]").trigger("tap");
        }
    },
    query: function () {
        var phoneSn = this.find("[name=PHONE_NUM]").val();
        if (!this.checkPhoneSn(phoneSn)) {
            return false;
        }
        if (roleCheck.checkSnExistInRoleList("1", phoneSn)) {
            return false;
        }
        var _self = this;
        Loading.beginBoxLoading(this.boxDom);
        var param = "&SERIAL_NUMBER=" + phoneSn + "&ROLE_CODE=" + this.roleCode + "&ROLE_TYPE=" + this.roleType + "&TRADE_TYPE_CODE=" + common.tradeTypeCode;
        hhSubmit(null, constdata.HANDLER, "checkPhoneRole", param, function (data) {
            // 号码校验过后初始化对象值
            _self.isCheckTag = true;
            _self.sn = phoneSn;
            _self.eparchyCode = data.get("EPARCHY_CODE");
            _self.afterQuery(data);
            _self.checkPhoneIsHasWide(phoneSn);//modify by duhj
            Loading.endBoxLoading(_self.boxDom);
        }, function (error_code, error_info) {
            Loading.endBoxLoading(_self.boxDom);
            $.MessageBox.error(error_code, error_info);
        });
    },
    checkPhoneSn: function (phoneSn) {
        if (phoneSn == null || phoneSn.length == 0) {
            MessageBox.alert("查询提示", "请输入手机号码后点击查询！");
            return false;
        }

        if (phoneSn.length != 11) {
            MessageBox.alert("查询提示", "请输入11位手机号码！");
            return false;
        }
        return true;
    },
    afterQuery: function (data) {
        var memberName = data.get("MEMBER_NAME");
        var drawArea = this.find("ul[name=phoneMem]");
        var html = '<li>';
        html += '<div class="pic">';
        html += '<span class="e_ico-pic-r ';
        if (this.isFmMgr) {
            html += 'e_ico-pic-red';
        } else {
            html += 'e_ico-pic-blue';
        }
        html += ' e_ico-user"></span>';
        html += '</div>';
        html += '<div class="main">';
        html += '<div class="title e_size-xl"><span>' + this.sn + '</span>';
        html += '<span class="e_black-light"> | ' + memberName + '</span><span class="e_space-3"></span>';
        html += '<button class="e_button e_button-s e_button-r e_size-xxs" name="showHideBtn"><span class="e_ico-show"></span>已选商品&nbsp;&nbsp;<span name="count">0</span></button>';
        html += '</div></div>';
        html += '</li>';
        drawArea.append(html);
        var _self = this;
        this.find("[name=showHideBtn]").bind("tap", function (obj) {
            _self.showHideOffer(this);
        });
        // 校验完成后，自动加载默认必选商品
        // var _self = this;
        // setTimeout(function () {
        //     OfferPop.initSelectedOffers(_self);
        // }, 1200);
    },
    showHideOffer: function (obj) {
        this.find("[partId=SelectedOffers]").toggle();
        $(obj).find("span:first").toggleClass('e_ico-hide').toggleClass('e_ico-show');
    },

    checkPhoneIsHasWide: function (phoneSn) {
        var that = this;
        hhSubmit(null, constdata.HANDLER, "checkIsHasChildrenRole", "&SERIAL_NUMBER=" + this.sn + "&ROLE_CODE=" + this.roleCode, function (ajaxData) {
            var ishasWide = ajaxData.get("IS_HAS_WIDE");
            if ("Y" == ishasWide) {// 宽带
                that.find("div[name=titleFn] button[typecode=NEW]").css("display", "none");
            } else {
                that.find("div[name=titleFn] button[typecode=OLD]").css("display", "none");
                if (!that.isFmMgr) {//非管理员不允许新开
                    that.find("div[name=titleFn] button[typecode=NEW]").css("display", "none");
                }
            }
        }, function (error_code, error_info) {
            $.endPageLoading();
            alert(error_info);
        });
    },

    checkIsCanAddNewWide: function () {
        var that = this;
        var flag = false;
        var param = "&SERIAL_NUMBER=" + that.sn + "&ROLE_CODE=" + "2" + "&ROLE_TYPE=" + "NEW" + "&TRADE_TYPE_CODE=" + common.tradeTypeCode;
        hhSubmit(null, constdata.HANDLER, "checkAddChilrenRole", param, function (data) {
            flag = true;
        }, function (error_code, error_info) {
            $.MessageBox.error(error_code, error_info);
        }, {
            async: false
        });

        return flag;
    }


};
