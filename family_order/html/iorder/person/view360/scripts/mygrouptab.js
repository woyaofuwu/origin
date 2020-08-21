window.cacheGroupData = new $.DatasetList();

$(document).ready(function () {
	var getParentVule = window.parent.toChildValue();
    loadFnNavButtons();
    loadTabInfo("groupInfoPart",undefined,getParentVule);
});

// 缓存集团信息查询结果
function cacheMyGroupTabReturnData(data) {
    window.cacheGroupData = data;
}

// 构建集团产品弹出层内的产品列表htmlDOM
function showGroupProductList(el) {
    var html = [];
    var grpOffers = window.cacheGroupData.get($(el).attr("idx"), "OFFERS");
    grpOffers.each(function () {
        html.push('<li><div class="main" title="' + this.get("PRODUCT_NAME") + '">'
            + this.get("PRODUCT_ID") + ' | ' + this.get("PRODUCT_NAME") + '</div></li>');
    });
    $("#PRODUCT_LIST_UL").html("").append(html.join(""));
    $("#PRODUCT_COUNT").html(grpOffers.length);

    showPopup("grpProductPopup", "grpProductItem", true);
}