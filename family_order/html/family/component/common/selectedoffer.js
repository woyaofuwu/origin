var SelectedOffer = function () {
    function renderSelectedOffer(offerList, partId, srcObj) {
        var part = typeof (partId) == "string" ? $("#" + partId) : partId;
        if (offerList != null && offerList.length > 0) {
            var data = {
                offerList : offerList
            };
            var selectedofferTpl = common.getTpl("family/component/tpl/selectedoffer.tpl");
            var html = template.render(selectedofferTpl, data);
            part.empty().append(html)
            bindEvents(part, offerList, srcObj);
        } else {
            part.empty();
        }
    }

    function bindEvents(part, offerList, srcObj) {
        var toggleObj = part.find("span[name='OFFER_VIEW']");
        toggleObj.bind("click", function () {
            if (toggleObj.hasClass("e_ico-show")) {
                part.find("div[name='SelectedOfferList']").css("display", "block");
                toggleObj.removeClass("e_ico-show");
                toggleObj.addClass("e_ico-hide");
            } else {
                part.find("div[name='SelectedOfferList']").css("display", "none");
                toggleObj.removeClass("e_ico-hide");
                toggleObj.addClass("e_ico-show");
            }
        });

        part.find("div[name='SelectedOfferList'] .e_ico-unfold").bind("click", function () {
            var param = "&EPARCHY_CODE=" + $("#EPARCHY_CODE").val();
            var itemIndex = $(this).attr("itemIndex");
            var elementId = $(this).attr("elementId");
            var elementType = $(this).attr("elementType");
            var tempElement = offerList.get(itemIndex);
            elementAttr.renderComponent(param, tempElement, itemIndex, elementId, elementType);
            attrAdapter.srcObj = srcObj;
        });
    }

    return {
        renderSelectedOffer : renderSelectedOffer
    };
}();
