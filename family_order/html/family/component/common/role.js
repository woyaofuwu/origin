"use strict"

function Role(roleCode, roleType, e) {
    this.roleCode = roleCode;
    this.roleType = roleType;
    this.sn = null;
    this.eparchyCode = null;
    this.uniqueId = ($.guid++);
    this.offers = null;
    this.userOffers = null;
    this.part = typeof (e) == "string" ? $("#" + e) : e;
    this.boxDom = null;
    this.subRoles = [];
    this.familyPay = true;
    this.share = true;
    this.main = null;

    this.addSubRole = function (r) {
        this.subRoles.push(r);
    };
    this.getSubRoles = function () {
        return this.subRoles;
    };
    this.getFilterOfferParam = function () {
        var data = $.DataMap();
        data.put("ROLE_CODE", this.roleCode);
        data.put("ROLE_TYPE", this.roleType);
        data.put("SERIAL_NUMBER", this.sn);
        if ("getFilterOfferParam" in this.__proto__) {
            var r = this.__proto__["getFilterOfferParam"].call(this);
            if (r == false) {
                return null;
            }
            if (r && r.length > 0) {
                // data.putAll(r);
                r.eachKey(function (key) {
                    data.put(key, r.get(key));
                });
            }
        }
        return data;
    };
    this.createSubRole = function (roleCode, roleType) {
        if ("createSubRole" in this.__proto__) {
            return this.__proto__["createSubRole"].call(this, roleCode, roleType);
        } else {
            return null;
        }
    };

    this.initDomTree = function (tpl) {
        after(this.createHtmlPart, this.afterInitDom, this)(tpl);
    }
    this.afterInitDom = function () {
        if ("afterInitDom" in this.__proto__) {
            this.__proto__["afterInitDom"].call(this);
        }
    };
    this.createHtmlPart = function (tpl) {
        var data = this.getRefreshTplData();
        data.btnsArt = common.getTpl('family/component/tpl/btns.art');
        data.isLtIE9 = constdata.isLtIE9;
        data.uniqueId = this.uniqueId;
        data.template = template;
        var html = template.render(tpl, data);// 获取html模版
        this.boxDom = $(html).appendTo(this.part);
        this.initSelectOfferBtnEvents();
        this.initBaseDomEvents();
    };
    this.initSelectOfferBtnEvents = function () {
        this.selectedOffersPart = this.boxDom.find("[partId=SelectedOffers]");
        if (this.selectedOffersPart && this.selectedOffersPart.length > 0) {
            var _self = this;
            if (OfferPop.checkExistOffersByRoleCode(this.roleCode)) {
                this.find("[name=selectOffer]").bind("click", function () {
                    OfferPop.showOffersPop(_self);
                });
            } else {
                this.find("[name=selectOffer]").parents("li").css("display", "none");
            }
        }
    };
    this.initBaseDomEvents = function () {
        var _self = this;
        this.find("div.fn[name=titleFn] [handle]").bind("click", function (n) {
            var handle = this.getAttribute("handle");
            if (handle != null && handle != "") {
                _self[handle].call(_self, this);
            }
        });
        this.find("div.fn[name=titleFn] button").bind("click", function () {
            var roleCode = this.getAttribute("roleCode");
            var typeCode = this.getAttribute("typeCode");
            var role = _self.createSubRole(roleCode, typeCode);
            if (role)
                _self.addSubRole(role);
        });
    }

    this.destroy = function () {
        //FeeList.clearUniqueFee(this.uniqueId);// 清除费用
        //fusionOrder.deleteObject(this.uniqueId);
        if ("destroy" in this.__proto__) {
            this.__proto__["destroy"].call(this);
        }
        for (var i = 0; i < this.subRoles.length; i++) {
            var role = this.subRoles[i];
            if (role.isEmpty) {
                continue;
            }
            role.destroy();
        }
        this.boxDom.remove(true);
        for (var key in this) {
            delete this[key];
        }
        this.isEmpty = true;
    };
    this.find = function (name) {
        return this.boxDom.find(name);
    };
    this.refreshPart = function (part, html, callBack) {
        this.find(part).empty().append(html);
        if (callback && typeof (callback) == "function") {
            callback.call(this);
        }
    };

    this.setUserOffers = function (userOffers) {
        this.userOffers = userOffers;
    };
    this.getUserOffers = function () {
        return this.userOffers;
    };
    this.setOffers = function (offers) {
        this.setOfferFee("1");
        this.offers = offers;
        this.setOfferFee("0");
        SelectedOffer.renderSelectedOffer(this.offers, this.selectedOffersPart, this);
        if (this.find("[name=showHideBtn]")) {
            // 重置商品个数
            if (this.offers && this.offers.length > 0) {
                this.find("[name=showHideBtn]").find("[name=count]").html(" " + this.offers.length);
            }
        }
    };
    this.setOfferFee = function (modifyTag) {
        if (this.offers != null) {
            for (var i = 0, len = this.offers.length; i < len; i++) {
                var offer = this.offers.get(i);
                if (offer.containsKey("FEE_DATA")) {
                    var feeDatas = offer.get("FEE_DATA");
                    if (feeDatas != null && typeof (feeDatas) != "undefined" && feeDatas.length > 0) {
                        for (var j = 0, feeSize = feeDatas.length; j < feeSize; j++) {
                            var feeData = feeDatas.get(j);
                            var fee = feeData.get("FEE"), code = feeData.get("CODE"), mode = feeData.get("MODE");
                            if (modifyTag == "0") {
                                FeeList.addFee(fee, code, mode, this.uniqueId, offer.get("ELEMENT_ID"), offer
                                    .get("ELEMENT_NAME"), "240");
                            } else {
                                FeeList.removeFee(fee, code, mode, this.uniqueId, offer.get("ELEMENT_ID"), "240");
                            }
                        }
                    }
                }
            }
        }
    };
    this.getOffers = function () {
        return this.offers;
    };
    this.getSn = function () {
        return this.sn;
    };
    this.getEparchyCode = function () {
        return this.eparchyCode;
    };
    this.setFamilyPay = function (obj) {
        if (obj.checked) {
            this.familyPay = true;
        } else {
            this.familyPay = false;
        }
    };
    this.setShare = function (obj) {
        if (obj.checked) {
            this.share = true;
        } else {
            this.share = false;
        }
    }

    this.checkSubmitData = function () {
        var errInfo = $.DataMap();
        if ("checkSubmitData" in this.__proto__) {
            return this.__proto__["checkSubmitData"].call(this);
        } else {
            return true;
        }
    };
    this.getSubmitData = function () {
        var data = $.DataMap();
        if (!this.checkSubmitData()) {//FeeList.checkFee(this.uniqueId)
            return false;
        }
        data.put("SERIAL_NUMBER", this.sn);
        data.put("MEMBER_MAIN_SN", this.mainSn != null ? this.mainSn : this.sn);
        data.put("EPARCHY_CODE", this.eparchyCode);
        data.put("ROLE_CODE", this.roleCode);
        data.put("ROLE_TYPE", this.roleType);
        data.put("FAMILY_PAY", this.familyPay);
        data.put("FAMILY_SHARE", this.share);
        if ("getImportData" in this) {
            var importInfo = this.getImportData();
            importInfo.eachKey(function (key) {
                data.put(key, importInfo.get(key));
            });
        }
        var offers = this.getOffers();
        if (this.isNeedSelectOffer && offers == null) {
            MessageBox.alert("业务提示","请选择成员[" + this.sn+ "]的家庭商品！");
            return null;
        }
        data.put("OFFERS", offers);
        var subRoles = this.getSubRoles();
        if (subRoles != null && subRoles.length > 0) {
            var subRoleDatas = $.DatasetList();
            for (var i = 0; i < subRoles.length; i++) {
                var subRole = subRoles[i];
                if (!subRole.isEmpty) {
                    var subRolesData = subRole.getSubmitData();
                    if (subRolesData == false) {
                        return false;
                    }
                    subRoleDatas.add(subRolesData);
                }
            }
            data.put("SUB_ROLES", subRoleDatas);
        }
        // var feeData = FeeList.getUniqueFee(this.uniqueId);
        // if (feeData && feeData.length > 0) {
        //     data.put("FEE_DATA", feeData);
        // }
        return data;
    };

    this.execWebMethod = function (param, methodName, afterMethodName) {
        var _self = this;
        loading.beginBoxLoading(this.boxDom);
        $.ajax.submit('', methodName, param, '', function (ajaxData) {
            afterMethodName.call(_self, ajaxData);
            loading.endBoxLoading(_self.boxDom);
        }, function (code, info, detail) {
            loading.endBoxLoading(_self.boxDom);
            MessageBox.alert("错误提示", "加载元素信息错误！", null, null, info, detail);
        });
    };
}
