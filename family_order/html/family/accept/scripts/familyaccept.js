/**
 * @date 2020年7月1日
 * @author zhenggang
 * @description 家庭受理js
 */
var familyAccept = {
    // 手机角色集合
    phoneRoleList : $.DatasetList(),
    // 初始化
    init : function () {
        Role.apply(this, [ "0", "NEW", $("#fmyOffer") ]);
        this.boxDom = this.part;
        this.mainSn = $("#hiddenPart").find("#SERIAL_NUMBER").val();
        var productId = $("#hiddenPart").find("#PRODUCT_ID").val();
        var param = "&FAMILY_PRODUCT_ID=" + productId + "&SERIAL_NUMBER=" + this.mainSn;
        var _self = this;
        $.beginPageLoading("家庭资料初始化...");
        $.ajax.submit("", "initFamily", param, "familyInfoPart", function (data) {
            // 初始化地州
            _self.eparchyCode = data.get("EPARCHY_CODE");
            // 业务大类设置公共参数
            common.setEnv(constdata.tradeType.ACCEPT, _self.eparchyCode);
            // 角色商品初始化
            var rolesList = data.get("ROLES");
            OfferPop.initDatas(rolesList);
            // 自动加载必选默认商品
            OfferPop.initSelectedOffers(_self);
            // 成员最大最小数
            var num = data.get("MEMBER_NUM");
            // 成员个数校验
            roleCheck.setMemberCount(num);
            // 创建管理员角色区域
            var masterPhone = common.createRole(Phone, {
                partId : "BuildRolePart",
                sn : _self.mainSn,
                eparchyCode : _self.eparchyCode,
                isFmMgr : true
            });
            _self.phoneRoleList.add(masterPhone);
            // 显示家庭商品
            _self.showMainOffer(productId, data);
            // 绑定商品选择事件
            _self.bindFmySelOfferEvent();
            // 提交组件处理
            _self.bindSubmitBtnEvent();
            // 绑定创建按钮事件
            _self.bindCreatePhoneEvent();
            $.endPageLoading();
        }, function (error_code, error_info) {
            $.endPageLoading();
            $.MessageBox.alert(error_code, error_info, function () {
                getNavContent().closeNav();
            });
        });
    },
    // 给提交按钮绑定点击事件
    bindSubmitBtnEvent : function () {
        var _self = this;
        $("#CSSUBMIT_BUTTON").unbind();
        $("#CSSUBMIT_BUTTON").bind("tap", function () {
            _self.beforeSubmit();
        });
        // 关闭页面
        $("#CSSUBMIT_BUTTON").attr("affirmAction", "getNavContent().closeNav()");
        $.cssubmit.disabledSubmitBtn(false, "submitButton");
    },
    getImportData : function () {
        var data = $.DataMap();
        data.put("FAMILY_PRODUCT_ID", $("#hiddenPart").find("#PRODUCT_ID").val());
        data.put("FAMILY_BRAND_CODE", $("#hiddenPart").find("#BRAND_CODE").val());
        data.put("TRADE_TYPE_CODE", $("#hiddenPart").find("#TRADE_TYPE_CODE").val());
        data.put("MANAGER_SN", $("#hiddenPart").find("#SERIAL_NUMBER").val());// 管理员号码
        data.put("EPARCHY_CODE", $("#hiddenPart").find("#EPARCHY_CODE").val());
        data.put("FAMILY_PRODUCT_MODE", $("#hiddenPart").find("#PRODUCT_MODE").val());
        data.put("REMARK", $("#REMARK").val());
        data.put("IS_EFFECT_NOW", $("#IS_EFFECT_NOW").val());
        data.put("HOME_NAME", $("#familyInfoPart").find("#HOME_NAME").val());
        data.put("HOME_ADDRESS", $("#familyInfoPart").find("#HOME_ADDRESS").val());
        data.put("HOME_PHONE", $("#familyInfoPart").find("#HOME_PHONE").val());
        data.put("CUST_NAME", $("#familyInfoPart").find("#CUST_NAME").val());
        data.put("HEAD_SERIAL_NUMBER", $("#familyInfoPart").find("#HEAD_SERIAL_NUMBER").val());
        data.put("HEAD_PSPT_TYPE_CODE", $("#familyInfoPart").find("#HEAD_PSPT_TYPE_CODE").val());
        data.put("HEAD_PSPT_ID", $("#familyInfoPart").find("#HEAD_PSPT_ID").val());
        return data;
    },
    // 提交前处理
    beforeSubmit : function () {
        if (!$.validate.verifyAll("familyInfoPart")) {
            return false;
        }
        if (!roleCheck.checkMemCountLimit()) {
            return false;
        }
        var submitData = this.getSubmitData();
        var roleDatas = $.DatasetList();
        for (var i = 0; i < this.phoneRoleList.getCount(); i++) {
            var phoneRole = this.phoneRoleList.get(i);
            var roleData = phoneRole.getSubmitData();
            if (roleData == undefined || roleData == null || roleData == "") {
                return false;
            }
            roleDatas.add(roleData);
        }
        submitData.put("SUB_ROLES", roleDatas.toString());
        submitData.eachKey(function(k,v){
            $.cssubmit.setParam(k,v);
        });
        
        familyCaller.setParam("ROUTE_EPARCHY_CODE", common.eparchyCode);
        familyCaller.setParam("ROLE_CODE", this.roleCode);
        familyCaller.setParam("ROLE_TYPE", this.roleType);
        familyCaller.check("450", "BEFORE_CHECK", $.cssubmit.dynamicParamData, function (ajaxData) {
            $.cssubmit.submitTrade();
        });

        return false;
    },
    // 初始化展示家庭产品信息
    showMainOffer : function (productId, data) {
        var comCha = data.get("COMCHA");
        var productName = data.get('FAMILY_PRODUCT_NAME');
        var productDesc = data.get('FAMILY_PRODUCT_DESC');
        $("#hiddenPart").find("#PRODUCT_NAME").val(productName);
        $("#hiddenPart").find("#EPARCHY_CODE").val(this.eparchyCode);
        $("#hiddenPart").find("#RELATION_TYPE_CODE").val(comCha.get('RELATION_TYPE_CODE'));
        $("#hiddenPart").find("#BRAND_CODE").val(comCha.get('BRAND_CODE'));
        $("#hiddenPart").find("#PRODUCT_MODE").val(comCha.get('PRODUCT_MODE'));
        $("#hiddenPart").find("#PAY_TOGETHER").val(comCha.get('PAY_TOGETHER'));
        var part = this.find("#newMain");
        if (part) {
            var prodName = "[" + productId + "]" + productName;
            part.find("div[name=productName]").html(prodName);
            part.find("div[name=productDesc]").html(productDesc);
            part.attr("tip", productDesc);
        }
    },
    // 绑定选择商品事件
    bindFmySelOfferEvent : function () {
        this.selectedOffersPart = this.find("[partId=SelectedOffers]");
        if (this.selectedOffersPart && this.selectedOffersPart.length > 0) {
            var _self = this;
            if (OfferPop.checkExistOffersByRoleCode("0")) {
                this.find("[name=selectOffer]").bind("tap", function () {
                    OfferPop.showOffersPop(_self);
                });
            } else {
                this.find("[name=selectOffer]").parents("li").css("display", "none");
            }
        }
    },
    bindCreatePhoneEvent : function () {
        var _self = this;
        $("#fmyPart").find("#createPhone").bind("tap", function () {
            _self.createPhone();
        });
    },
    createPhone : function () {
        if (!roleCheck.checkRoleCountLimit("1")) {
            return false;
        }
        var memPhone = common.createRole(Phone, {
            partId : "BuildRolePart",
            sn : "",
            eparchyCode : this.eparchyCode,
            isFmMgr : false
        });
        this.phoneRoleList.add(memPhone);
    },
    getPhoneRoleList : function () {
        return this.phoneRoleList;
    }
}
$(familyAccept.init());
