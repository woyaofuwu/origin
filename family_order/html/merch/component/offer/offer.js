if (typeof (Offer) == "undefined") {
    Offer = function (id, n) {
        return new Offer.fn.init(id, n);
    };
    Offer.fn = {
        init: function (id, n) {
            this.id = id;
            this.event = new Object();// Object.create(null);
            this.setOfferList();
            this.selectedTag = $.DatasetList();
            this.dom = $("#" + this.id);
            if ($.isObject(n))
                $.extend(this, n);
            this.bindEvent();
            var that = this;
            this.paging = new PagingComponent(this.find("div[x_id=pagingComponent]"), function () {
                that.refreshOfferList();
            });
        }
    };
    Offer.prototype = {
        HANDLER: "com.asiainfo.veris.crm.iorder.web.merch.component.OfferHandler",

        setEnv: function (eparchyCode, productId) {
            this.eparchyCode = eparchyCode;
            this.productId = productId;
        },

        getCommonParam: function () {
            var param = "&TYPE=" + this.type + "&EPARCHY_CODE=" + this.eparchyCode;
            if (this.productId) {
                param += "&PRODUCT_ID=" + this.productId;
            }
            if (this.isCalcDate) {
                param += "&IS_CALC_DATE=" + this.isCalcDate;
            }
            return param;
        },

        bindEvent: function () {
            var that = this;
            this.find("[x_id=tagArea],[x_id=selectTagArea],[x_id=itemListArea]").bind("click", function (n) {
                var obj = n.target;
                var handle = obj.getAttribute("handle");
                if (handle == null || handle == "") {
                    handle = obj.parentNode.getAttribute("handle");
                    obj = obj.parentNode;
                }
                if (handle != null && handle != "") {
                    that[handle].call(that, obj);
                }
            });
        },

        initData: function () {
            this.hideError();
            var data = this.getCommonParam();
            data += this.execEventAction("initDataParam");
            var that = this;
            if (typeof (getSpecialAreaParam) == "function") {
                data += getSpecialAreaParam();
            }
            $.beginPageLoading("加载中。。。");
            hhSubmit(null, this.HANDLER, "getTagList", data, function (ajaxData) {
                $.endPageLoading();
                if (ajaxData) {
                    if (that.labelsTpl && that.labelsTpl.length > 0 && window.template && typeof (window.template) == "function") {
                        that.drawLabelByTpl(ajaxData.get("TAG_LIST"));
                    } else {
                        that.drawLabel(ajaxData.get("TAG_LIST"));
                    }
                    that.paging.resetCurrent();
                    that.refreshOfferList();
                }
            }, function (errorCode, errorInfo) {
                $.endPageLoading();
                that.showError(errorCode + ":" + errorInfo);
            });
        },

        refreshOfferList: function () {
            var data = this.getCommonParam();
            data += this.execEventAction("refreshOfferParam");

            data += "&TAG_LIST=" + this.selectedTag.toString();
            data += this.getPaginData();
            if (typeof (getSpecialAreaParam) == "function") {
                data += getSpecialAreaParam();
            }
            var that = this;
            $.beginPageLoading("加载中。。。");
            hhSubmit(null, this.HANDLER, "refreshOffers", data, function (ajaxData) {
                that.afterRefreshOfferList(ajaxData);
                $.endPageLoading();
            }, function (errorCode, errorInfo) {
                $.endPageLoading();
                that.showError(errorCode + ":" + errorInfo);
            });
        },

        afterRefreshOfferList: function (ajaxData) {
            if (ajaxData) {
                this.setOfferList(ajaxData.get("OFFERS"));
                if (this.offersTpl && this.offersTpl.length > 0 && window.template && typeof (window.template) == "function") {
                    this.drawOfferByTpl();
                } else {
                    this.drawOffer();
                }
                this.paging.initTotalPageSize(ajaxData.get("OFFERS_TOTAL_SIZE"));
            }
        },
        afterSearchRefreshOfferList: function (ajaxData) {
            this.paging.resetCurrent();
            if (this.selectedTag.length > 0) {
                this.selectedTag = $.DatasetList();
                this.find("ul[x_id=selectTagArea]").empty();
                this.find("ul[x_id=tagArea] li.on[tvid]").removeClass("on");
            }
            if (ajaxData) {
                this.setOfferList(ajaxData.get("OFFERS"));
                if (this.offersTpl && this.offersTpl.length > 0 && window.template && typeof (window.template) == "function") {
                    this.drawOfferByTpl();
                } else {
                    this.drawOffer();
                }
                this.paging.initTotalPageSize(ajaxData.get("OFFERS_TOTAL_SIZE"));
            }
        },
        setOfferList: function (offers) {
            if (offers && offers.length > 0) {
                this.currentOfferList = offers;
            } else {
                this.currentOfferList = $.DatasetList();
            }
        },
        drawOffer: function () {
            var offers = this.currentOfferList;
            var drawArea = this.find("[x_id=itemListArea]");
            drawArea.empty();
            var html = '';
            for (var i = 0; i < offers.length; i++) {
                var offer = offers.get(i);
                var offerCode = offer.get("OFFER_CODE");
                var offerName = offer.get("OFFER_NAME");
                var offerType = offer.get("OFFER_TYPE");
                var offerDesc = offer.get("DESCRIPTION");
                var orderMode = offer.get("ORDER_MODE");
                var stateName = offer.get("STATE_NAME", "");// HOT,NEW,已订购
                var tagValue = offer.get("TAG_VALUE", "");// HOT,NEW,已订购
                var disabled = offer.get("DISABLED") == "true";// 禁用选择商品标记

                html += '<li idx="' + i + '" title="' + offerDesc + '" class="link">';
                if ("P" == offerType || "K" == offerType) {
                    html += '<div class="main" ' + (this.isOpenPage && !disabled ? 'handle = "openPage"' : '') + '>';
                } else {
                    html += '<div class="main" ' + '>';
                }
                html += '<div class="title">';
                html += '<span class="e_blue">[' + offerCode + ']</span>' + offerName;
                html += '</div>';
                html += '<div class="content e_size-s e_x" >';
                html += (offerDesc && offerDesc.length == 0 ? offerName : offerDesc);
                html += '</div>';
                html += '</div>';

                if (!disabled && stateName) {
                    html += '<div class="side"><span class="e_size-s e_tag';
                    if (tagValue == "NEW") {
                        html += ' e_tag-green';
                    } else if (tagValue == "HOT") {
                        html += ' e_tag-red';
                    } else {
                        html += ' e_tag-orange';
                    }
                    html += '">' + stateName + '</span></div>';
                }
                html += '</span></div><div class="e_space"></div>';
                if (!disabled) {
                    if (this.isShowAddShoppingCart && offer.get("DISABLED_ADD_SHOPPING_CART") != "true" && offer.get("OFFER_TYPE") != "K") {
                        html += '<div class="fn" handle="addToShoppingCart"><span class="e_ico-cart"></span></div>';
                    }
                }
                if (disabled && stateName) {
                    html += '<div class="statu statu-right statu-blue">' + stateName + '</div>';
                }

                html += '</li>\r\n';
            }
            drawArea.append(html);
            this.afterBind();
        },
        drawOfferByTpl: function () {
            var offers = this.currentOfferList;
            var drawArea = this.find("[x_id=itemListArea]");
            drawArea.empty();
            var html = template(this.offersTpl, {
                offers: this.currentOfferList,
                isOpenPage: this.isOpenPage,
                isCalcDate: this.isCalcDate,
                isShowOrder: this.isShowOrder,
                isShowAddShoppingCart: this.isShowAddShoppingCart
            });
            drawArea.append(html);
        },
        getPaginData: function () {
            if (this.paging) {
                return "&CURRENT=" + this.paging.current + "&PAGE_SIZE=" + this.paging.onePageCount;
            }
            return "";
        },

        orderOffer: function (obj) {
            this.offerHandleAction("order", obj);
        },
        addToShoppingCart: function (obj) {
            this.offerHandleAction("add", obj);
        },
        openPage: function (obj) {
            this.offerHandleAction("open", obj);
        },
        offerHandleAction: function (type, obj) {
            var li = obj.parentNode;
            var idx = li.getAttribute("idx");
            var offer = this.getOfferByIdx(idx);
            this.execEventAction(type, offer);
        },
        execEventAction: function () {
            var type = arguments[0];
            var args = [];
            for (var i = 1; i < arguments.length; i++) {
                args[i - 1] = arguments[i];
            }
            var fn = this.event[type];
            if (fn)
                return fn.apply(this, args);
            else
                return "";
        },
        modifyTime: function (obj) {
            var idx = $(obj).attr("idx");
            var offer = this.getOfferByIdx(idx);
            if (offer != null) {
                modifyTime.render(offer, function () {
                    var offerCode = offer.get("OFFER_CODE");
                    var offerType = offer.get("OFFER_TYPE");
                    $(obj).siblings("[name=" + offerCode + "_" + offerType + "_START_DATE" + "]").html(offer.get("START_DATE").substring(0, 10));
                    $(obj).siblings("[name=" + offerCode + "_" + offerType + "_END_DATE" + "]").html(offer.get("END_DATE").substring(0, 10));
                });
            }
        },

        modifyPlatTime: function (obj) {
            var idx = $(obj).attr("idx");
            var offer = this.getOfferByIdx(idx);
            if (offer != null) {
                modifyTime.renderPlat(offer, function () {
                    var offerCode = offer.get("OFFER_CODE");
                    var offerType = offer.get("OFFER_TYPE");
                });
            }
        },

        getOfferByIdx: function (idx) {
            var offerList = this.currentOfferList;
            if (idx > -1 && offerList && offerList.length > idx) {
                return offerList[idx];
            }
            return null;
        },

        /* tag */
        clearTags: function (obj) {
            if (this.selectedTag.length > 0) {
                this.selectedTag = $.DatasetList();
                this.find("ul[x_id=selectTagArea]").empty();
                this.find("ul[x_id=tagArea] li.on[tvid]").removeClass("on");
                this.paging.resetCurrent();
                this.refreshOfferList();
            }
        },
        clearSelectTags: function () {
            if (this.selectedTag.length > 0) {
                this.selectedTag = $.DatasetList();
                this.find("ul[x_id=selectTagArea]").empty();
                this.find("ul[x_id=tagArea] li.on[tvid]").removeClass("on");
            }
        },
        addTag: function (tagValueId, tagValueName, tagId, tagName) {
            var selectedTag = this.getSelectTag(tagId);
            if (selectedTag == null || tagValueId != selectedTag.get("TAG_VALUE_ID")) {
                if (selectedTag != null) {
                    this.removeSelectedTag(tagId);
                }
                var html = '<li>';
                html += '<div class="e_tag">';
                html += '<span class="e_tagText">' + tagName + ":" + tagValueName + '</span>';
                html += '<span class="e_tagDelete" tagId="' + tagId + '" tagValueId="' + tagValueId + '"  handle="delTag" ></span>';
                html += '</div>';
                html += '</li>';
                this.find("[x_id=selectTagArea]").append(html);
                var tag = $.DataMap();
                tag.put("TAG_ID", tagId);
                tag.put("TAG_VALUE_ID", tagValueId);
                this.selectedTag.add(tag);
                this.paging.resetCurrent();
                this.refreshOfferList();
            }
        },
        getSelectTag: function (tagId) {
            for (var i = 0; i < this.selectedTag.length; i++) {
                var tag = this.selectedTag[i];
                if (tag.get("TAG_ID") == tagId) {
                    return tag;
                }
            }
            return null;
        },

        delTag: function (obj) {
            var tagId = $(obj).attr("tagId");
            this.removeSelectedTag(tagId);
            this.find("ul[x_id=tagArea] ul[tagId=" + tagId + "]").children().removeClass("on");
            this.paging.resetCurrent();
            this.refreshOfferList();
        },
        removeSelectedTag: function (tagId, tvId) {
            if (tagId) {
                var tag = this.getSelectTag(tagId);
                if (tag != null) {
                    this.selectedTag.remove(tag);
                }
            }
            this.find("span[tagId=" + tagId + "]").parents("li").remove();
        },
        /* label */
        foldLabels: function (obj) {
            $(obj).parent().next().next().toggleClass('option-fold');
            if ($(obj).data("fold") == "true") {
                $(obj).html("更多");
                $(obj).data("fold", "false");
            } else {
                $(obj).html("收起");
                $(obj).data("fold", "true");
            }
        },
        foldLabelArea: function (obj) {
            this.find(".c_filter").toggle();
            $(obj).children().toggleClass("e_ico-fold e_ico-unfold");
        },

        drawLabelByTpl: function (tags) {
            var that = this;
            // template.defaults.debug = true;
            template.defaults.imports.getSelectTag = function (tagId) {
                return that.getSelectTag(tagId)
            };
            var drawArea = this.find("[x_id=tagArea]");
            drawArea.empty();
            var html = template(this.labelsTpl, {
                tags: tags
            });
            drawArea.append(html);
        },
        drawLabel: function (tags) {
            var drawArea = this.find("[x_id=tagArea]");
            drawArea.empty();
            var html = '';
            for (var i = 0; i < tags.length; i++) {
                var tag = tags.get(i);
                var tagId = tag.get("TAG_ID");
                var selectedTag = this.getSelectTag(tagId);
                var tagName = tag.get("TAG_NAME");
                var tagValues = tag.get("TAG_VALUE_LIST");
                html += '<li>';
                html += '<div class="more"><button class="e_button-s e_button-r" handle="foldLabels" >更多</button></div>';
                html += '<div class="label">' + tagName + ':</div>';
                html += '<div class="option option-fold">';
                html += '<ul tagId="' + tagId + '" labelName="' + tagName + '">';

                if (tagValues) {
                    for (var j = 0; j < tagValues.length; j++) {
                        var tv = tagValues.get(j);
                        var tvId = tv.get("TAG_VALUE_ID");
                        var tvName = tv.get("TAG_VALUE_NAME");
                        var clazz = (selectedTag != null && selectedTag.get("TAG_VALUE_ID") == tvId) ? 'class="on"' : '';
                        html += '<li handle="switchLabel" tvId="' + tvId + '" ' + clazz + ' >' + tvName + '</li>';
                    }
                }
                html += '</ul>';
                html += '</div>';
                html += '</li>';
            }
            drawArea.append(html);
        },
        switchLabel: function (obj) {
            var o = $(obj);
            var tvId = o.attr("tvId");
            var tagId = o.parent().attr("tagId");
            var tagName = o.parent().attr("labelName");
            var tvName = $(obj).html();
            o.addClass("on").siblings(".on").removeClass("on");
            this.addTag(tvId, tvName, tagId, tagName);
        },

        /* tools */
        bind: function (e, n) {
            if (e) {
                if (typeof (e) == "object") {
                    this.event = e;
                } else {
                    if (e && (typeof (n) == "function"))
                        this.event[e] = n;
                }
            }
        },
        find: function (name) {
            return this.dom.find(name);
        },
        showError: function (err) {
            this.find("div[x_id=OfferContent]").hide();
            this.find("div[x_id=errMsg]").html(err);
            this.find("div[x_id=errMsgContent]").show();
        },
        hideError: function () {
            this.find("div[x_id=errMsgContent]").hide();
            this.find("div[x_id=OfferContent]").show();
        },
        extendFn: function (name, fnBody) {
            if (typeof (fnBody) == "function") {
                this[name] = fnBody;
            }
        },
        switchDesc: function (o) {
            var dom = this.find("[x_id=listArea]");
            if ($(o).is(':checked')) {
                $(dom).addClass("e_hide-x");
            } else {
                $(dom).removeClass("e_hide-x");
            }
        },
        afterBind: function () {
            var that = this;
            this.find("[x_id=itemListArea] li").tap(function () {
                if ($(this).hasClass("checked")) {
                    $(this).removeClass("checked");
                } else {
                    that.find("[x_id=itemListArea] .checked").removeClass("checked");
                    $(this).addClass("checked");
                }
            })
        },
    };

    Offer.fn.init.prototype = Offer.prototype;
}