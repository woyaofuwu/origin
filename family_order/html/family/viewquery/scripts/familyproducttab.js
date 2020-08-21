/**
 * zhangxi
 */

$(document).ready(function() {

	// 查询所有信息复选框绑定tap事件
	$("#queryAllCheckboxLi").tap(function() {
		var param = "&QUERY_ALL=" + $("#queryAllCheckbox").attr("checked");
		loadTabInfo("productPart,svcTab,discntTab,platSvcTab", null, param);
	});

	var param = "&SERIAL_NUMBER=" + parent.$("#SERIAL_NUMBER").val();

	loadFnNavButtons();
    loadTabInfo("productPart,svcTab,discntTab,platSvcTab");

});