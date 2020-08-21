"use strict"
var OfferPop = function () {
    var ConstData = window.constdata
    var srcObj = null;
    var popId = "offerPop";
    var offerSet = {};
    var roleGroup = {};
    var offerTypeListTpl = common.getTpl("family/component/tpl/offerTypeList.tpl");
    // 更多收起按钮
    var fn = {
        foldOffers : function (obj) {
            if ($(obj).data("fold") == "true") {
                $(obj).parent().parent().parent().next(".c_list").find("li:gt(5)").css("display", "none");
                $(obj).html("更多");
                $(obj).data("fold", "false");
            } else {
                $(obj).parent().parent().parent().next(".c_list").find("li").show();
                $(obj).html("收起");
                $(obj).data("fold", "true");
            }
        },
        ok : function () {
            checkOffersRule();
        },
        back : function () {
            hidePopup(popId);
            hidePop();
        }
    }

    // 暴露给其他对象的方法，点击商品选择按钮
    function showOffersPop(roleObj, isInit) {
        srcObj = roleObj;// 保存存量用户对象
        var roleCode = srcObj.roleCode;// 角色编码  
        var roleType = srcObj.roleType;
        var tradeTypeCode = common.tradeTypeCode;
        var condData = null;

        var roleOfferTypes = offerSet[roleCode];
        if (srcObj.checkAddress && !srcObj.checkAddress() && (roleCode == "2" || roleCode == "3" || roleCode == "4")) {
            return;
        }

        if ("getFilterOfferParam" in srcObj) {
            condData = srcObj.getFilterOfferParam();
            if (condData == null) {
                return popOfferDiv(roleOfferTypes, roleCode, roleObj, isInit);
            }
        }

        var data = $.DataMap();
        for ( var key in roleOfferTypes) {
            if (key in ConstData.offerType) {
                data.put(key, $.DatasetList('[' + roleOfferTypes[key] + ']'));
            } else {
                data.put(key, roleOfferTypes[key]);
            }
        }
        familyCaller.setParam("ROUTE_EPARCHY_CODE", common.eparchyCode);
        familyCaller.filterOffers(tradeTypeCode, roleCode, roleType, data, condData, function (ajaxData) {
            var roleData = null;
            if (ajaxData && ajaxData.length > 0) {
                roleData = convertData(ajaxData);
            } else {
                roleData = roleOfferTypes;
            }
            popOfferDiv(roleData, roleCode, roleObj, isInit);
        });
    }

    function popOfferDiv(roleData, roleCode, roleObj, isInit) {
        if (drawPopupItem(roleData)) {
            var popItemId = 'offerItem' + roleCode;
            if (!isInit) {
                showPopup(popId, popItemId);// 弹出商品选购对象
            }else{
                checkOffersRule();
            }
        }
    }

    function initDatas(roleList) {
        if (roleList != null)
            for (var i = 0, len = roleList.length; i < len; i++) {
                var roleCode = roleList.get(i).get("ROLE_CODE");
                if (!(roleCode in roleGroup)) {
                    roleGroup[roleCode] = 1;
                }
                var offers = roleList.get(i).get("OFFERS");
                if (offers && offers.length > 0) {
                    var typeOfferSet;
                    if (roleCode in offerSet) {
                        typeOfferSet = offerSet[roleCode];// .push(offers);
                    } else {
                        offerSet[roleCode] = typeOfferSet = {};
                    }
                    for (var j = 0, offer; offer = offers.get(j++);) {
                        var offerType = offer.get("OFFER_TYPE");
                        if (!(offerType in typeOfferSet)) {
                            typeOfferSet[offerType] = [];
                        }
                        typeOfferSet[offerType].push(offer);
                    }
                }
            }
    }

    // 暴露给其他对象的方法，隐藏商品选择POP
    function hidePop() {
        srcObj = null;
        $("#level2Item1").empty();
        $("#PopGroup").empty();
    }

    // 通过模板去渲染页面
    function drawPopupItem(roleData) {
        var roleCode = srcObj.roleCode;// 角色编码
        var popItemId = "offerItem" + roleCode;
        var roleName = ConstData.busiOfferTitle[roleCode];
        if (roleData == null) {
            MessageBox.alert("提示信息", "您选购的商品不支持该角色！");
            return false;
        } else {
            var data = {
                id : popItemId,
                roleName : roleName,
                roleCode : roleCode,
                offerTypes : roleData
            };
            var html = template.render(offerTypeListTpl, data);
            $("#level2Item1").empty();
            $("#PopGroup").empty().append(html);
            bindHandle();
            resetDisabled(roleCode, srcObj);
            return true;
        }
        return false;
    }

    function bindHandle() {
        $("#PopGroup [handle]").bind("click", function (n) {
            var obj = n.target;
            var handle = obj.getAttribute("handle");
            if (handle == null || handle == "") {
                handle = this.parentNode.getAttribute("handle");
                obj = obj.parentNode;
            }
            if (handle != null && handle != "") {
                fn[handle].call(Object, obj);
            }
        });
    }

    // 判断用户的元素是已订购还是新订购，重置CHECKBOX
    function resetDisabled(roleCode, srcObj) {
        var flag = false;
        $("input:checkbox,input:radio:not(:checked)", "#offerItem" + roleCode).each(function () {
            var offerType = this.getAttribute("offerType");
            var offerCode = this.getAttribute("offerCode");
            var userOffer = getUserOffer(offerCode, offerType);
            var orderOffer = getOrderOffer(offerCode, offerType);
            if (userOffer != null) {
                if ('exist' == userOffer.get('MODIFY_TAG') && "R" == this.getAttribute("orderMode")) {
                    this.diabled = false;
                }
                if ('exist' == userOffer.get('MODIFY_TAG') && "R" != this.getAttribute("orderMode")) {
                    this.diabled = true;
                }
            }
            if (orderOffer != null) {
                if ('0' == orderOffer.get('MODIFY_TAG')) {
                    this.checked = true;
                }
            }
            if (!$(this).attr('checked')) {
                // 没有点确定，没有选营销活动，校验过资源，点击返回需要释放资源重置数据
                if ("K" == offerType) {
                    dealGoodsInfosAndFees(srcObj, offerCode);
                }
            }
        });
    }

    function getOffer(roleCode, offerCode, offerType) {
        var role = offerSet[roleCode];
        var offers = role[offerType];
        var data = $.DataMap();
        if (offers == null || offers.length <= 0)
            return data;

        for (var i = 0; i < offers.length; i++) {
            var tmpOffer = offers[i];
            if (offerCode == tmpOffer.get("OFFER_CODE") && offerType == tmpOffer.get("OFFER_TYPE")) {
                data.put("ROLE_CODE", roleCode);
                data.put("ELEMENT_ID", tmpOffer.get("OFFER_CODE"));
                data.put("ELEMENT_NAME", tmpOffer.get("OFFER_NAME"));
                data.put("PACKAGE_ID", tmpOffer.get("GROUP_ID", "-1"));
                data.put("ELEMENT_TYPE_CODE", tmpOffer.get("OFFER_TYPE"));
                data.put("PRODUCT_ID", tmpOffer.get("PRODUCT_ID"));
                break;
            }
        }

        return data;
    }

    // 用户已订购元素
    function getUserOffer(elementId, elementType) {
        var userOffers = srcObj.getUserOffers();
        if (userOffers != null && userOffers != "") {
            for (var i = 0, len = userOffers.length; i < len; i++) {
                if (userOffers.get(i).get("ELEMENT_ID") == elementId
                        && userOffers.get(i).get("ELEMENT_TYPE_CODE") == elementType) {
                    return userOffers.get(i);
                }
            }
        }

        return null;
    }

    // 用户新订购元素
    function getOrderOffer(elementId, elementType) {
        var orderOffers = srcObj.getOffers();
        if (orderOffers != null && orderOffers != "") {
            for (var i = 0, len = orderOffers.length; i < len; i++) {
                if (orderOffers.get(i).get("ELEMENT_ID") == elementId
                        && orderOffers.get(i).get("ELEMENT_TYPE_CODE") == elementType) {
                    return orderOffers.get(i);
                }
            }
        }

        return null;
    }

    // 元素规则校验
    function checkOffersRule() {
        var roleCode = srcObj.roleCode;// 角色编码
        var elements = $.DatasetList();

        $("#offerItem" + roleCode).find("ul[offerType]").each(function () {
            $(this).find("input:checkbox:checked,input:radio:checked").each(function (index, val) {// checked
                var offerCode = $(this).attr("offerCode");
                var offerType = $(this).attr("offerType");
                var selectEle = getOffer(roleCode, offerCode, offerType);
                selectEle.put("MODIFY_TAG", "0");
                elements.add(selectEle);
            });
        });

        var roleType = srcObj.roleType;
        var param = "&OFFERS=" + elements + "&ROLE_CODE=" + roleCode + "&ROLE_TYPE=" + roleType + "&TRADE_TYPE_CODE="
                + common.tradeTypeCode;//"&NEW_FUSION_PRODUCT_ID=" + $("#PRODUCT_ID").val() 
        if ((roleCode == "0" || roleCode == "1" || roleCode == "2" || roleCode == "3" || roleCode == "4") && roleType == "OLD") {
            param += "&EPARCHY_CODE=" + srcObj.getEparchyCode();
            var sn = srcObj.getSn();
            if (!sn) {
                return;
            }
            param += "&SERIAL_NUMBER=" + sn ;
        } else {
            param += "&EPARCHY_CODE=" + common.eparchyCode;
        }

        $.beginPageLoading("商品校验中......");
        hhSubmit(null, ConstData.HANDLER, "dealAddOffers", param, function (data) {
            srcObj.setOffers(data.get('OFFERS'));// 将选购的元素赋值给对象
            fn.back();
            $.endPageLoading();
        }, function (error_code, error_info, derror) {
            $.endPageLoading();
            $.MessageBox.error(error_code, error_info);
        });
    }

    // 清空集合
    function clearOfferSet() {
        offerSet = {};
    }

    function checkExistGroupByRoleCode(roleCode) {
        if (roleCode in roleGroup) {
            return true;
        }
        return false;
    }

    function checkExistOffersByRoleCode(roleCode) {
        if (roleCode in offerSet) {
            return true;
        }
        return false;
    }

    function getExistOffersRoleTypeStrs(roleArrs) {
        var result = "";
        for (var i = 0; i < roleArrs.length; i++) {
            var roleType = roleArrs[i];
            var arr = roleType.split("_");
            var roleCode = arr[0];
            if (checkExistGroupByRoleCode(roleCode)) {
                result += roleType;
            }
        }
        return result;
    }

    function initSelectedOffers(roleObj) {
        showOffersPop(roleObj, true);
    }

    return {
        initDatas : initDatas,
        showOffersPop : showOffersPop,
        hidePop : hidePop,
        clearOfferSet : clearOfferSet,
        initSelectedOffers : initSelectedOffers,
        checkExistGroupByRoleCode : checkExistGroupByRoleCode,
        getExistOffersRoleTypeStrs : getExistOffersRoleTypeStrs,
        checkExistOffersByRoleCode : checkExistOffersByRoleCode
    }
}();
