pageHide();
function pageHide() {
	var valueData = $.DataMap();
	valueData.put("X_TRANS_CODE", "GrpBat");
	
	parent.$('#POP_CODING_STR').val('学护卡批量查询代付号码是否满足');
	parent.$('#CODING_STR').val(valueData);
	parent.hiddenPopupPageGrp();
}

