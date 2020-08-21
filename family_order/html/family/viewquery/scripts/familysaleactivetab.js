/**
 * zhangxi
 */

// 缓存对象
window.QYCommonCache = new $.DatasetList();
window.QYDetailCache = new $.DatasetList();
window.NOQYCommonCache = new $.DatasetList();
window.NOQYDetailCache = new $.DatasetList();

$(document).ready(function() {
	// 查询所有营销活动复选框绑定tap事件
	$("#queryAllCheckboxLabel").tap(function() {
		if ($.os.phone) {

		} else {
			backToListViewForPC(0); // "签约类"标签返回列表模式
			backToListViewForPC(1); // "非签约类"标签返回列表模式
			$("#listViewButton").css("display", "none"); // 隐藏"返回多列试图"按钮
		}
		var param = "&QUERY_ALL=" + $("#queryAllCheckbox").attr("checked");
		loadTabInfo("QYSaleActiveListCol,NOQYSaleActiveListCol", null, param);
	});

	// 返回列表模式按钮绑定tap事件
    $("#listViewButton").tap(function () {
        $("#listViewButton").css("display", "none"); // 隐藏"返回多列试图"按钮
        backToListViewForPC(saleActiveTab.activeIdx);
    });

    loadFnNavButtons();
    loadTabInfo("QYSaleActiveListCol,NOQYSaleActiveListCol");
});

//PC端返回列表模式
function backToListViewForPC(idx) {
    if (idx === 0) {
        $("#QYSaleActiveListCol").css("width", "100%").next().addClass("e_show-phone");
        $("#QYSaleActiveListCol > .c_list").addClass("c_list-col-2");
        $("#QYSaleActiveListCol li").removeClass("cur gray").addClass("link");
    } else if (idx === 1) {
        $("#NOQYSaleActiveListCol").css("width", "100%").next().addClass("e_show-phone");
        $("#NOQYSaleActiveListCol > .c_list").addClass("c_list-col-2");
        $("#NOQYSaleActiveListCol li").removeClass("cur gray").addClass("link");
    }
}