// 包元素列表对象
if (typeof OfferList == "undefined") {
    window["OfferList"] = function () {
        this.categoryMap = new $.DataMap();
        this.categoryLabelMap = new $.DataMap();
        this.selectedList = new $.DatasetList();
        this.mainProductId = null;
    };
    var offerList = new OfferList();
}

// 元素对象
if (typeof Offer == "undefined") {
    window["Offer"] = function () {
        this.elementId = null;
        this.elementName = null;
        this.elementType = null;
        this.selectFlag = null;
        this.reOrder = null;
        this.explain = null;
    };
}

if (typeof Category == "undefined") {
    window["Category"] = function () {
        this.categoryId = null;
        this.categoryName = null;
        this.offerList = [];
    }
}

if (typeof OfferGroup == "undefined") {
    window["OfferGroup"] = function () {
        this.offerId = null;
        this.groupId = null;
        this.groupName = null;
        this.minNumber = null;
        this.maxNumber = null;
        this.selectFlag = null;
    };
}

//包元素列表对象
if (typeof(SelectedElements) == "undefined") {
    window["SelectedElements"] = function () {
        this.selectedEls = new $.DatasetList();
    };
    var selectedElements = new SelectedElements();
}

if (typeof ProductEnv == "undefined") {
    window["ProductEnv"] = function () {
        this.eparchyCode = null;
        this.userId = null;
        this.serialNumber = null;
    };
    var productEnv = new ProductEnv();
}

(function () {
    $.extend(ProductEnv.prototype, {
        setEnv: function (eparchyCode, userId, serialNumber) {
            if (this.eparchyCode == null) {
                if (eparchyCode != "" && typeof eparchyCode != "undefined") {
                    this.eparchyCode = eparchyCode;
                }
            }
            if (this.userId == null) {
                if (userId != "" && typeof userId != "undefined") {
                    this.userId = userId;
                }
            }
            if (this.serialNumber == null) {
                if (serialNumber != "" && typeof serialNumber != "undefined") {
                    this.serialNumber = serialNumber;
                }
            }
        }
    });
})();