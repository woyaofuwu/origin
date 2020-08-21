$(document).ready(function () {
    loadFnNavButtons();
    loadTabInfo("relationInfoPart");
});

// 显示角色A/B信息下拉按钮事件
function toggleInfo(el) {
    var subEl = $(el).parent().next();
    if (subEl.css("display") === "none") subEl.css("display", "");
    else subEl.css("display", "none");

    var foldEl = el.children[0];
    foldEl.className = foldEl.className === "e_ico-unfold" ? "e_ico-fold" : "e_ico-unfold";
}