//查询Vpmn客户经理
function qryManagerRightsList() {
	var staffId = $("#cond_STAFF_ID").val();
	$.beginPageLoading("数据查询中......");
	$.ajax.submit("QueryCondPart", "qryManagerRightsList", "",
			"refreshHintBar,RefreshPart", function(data) {
				$.endPageLoading();
			}, function(error_code, error_info, derror) {
				$.endPageLoading();
				showDetailErrorInfo(error_code, error_info, derror);
				// $.showWarnMessage(error_code,error_info);
			});
}

function redirectToEdit(obj) {
	var areaCode = obj.getAttribute("areacode");
	var staffId = obj.getAttribute("staffId");
	var userProductCode = obj.getAttribute("userProductCode");
	var rightCode = obj.getAttribute("rightCode");
	var linkPhone = obj.getAttribute("linkPhone");
	var startDate = obj.getAttribute("startDate");
	var remark = obj.getAttribute("remark") == null ? "" : obj
			.getAttribute("remark");
	var endDate = obj.getAttribute("endDate");
	$.ajax.submit(this, "redirectToEdit", "&AREA_CODE=" + areaCode
			+ "&con_STAFF_ID=" + staffId + "&USER_PRODUCT_CODE="
			+ userProductCode + "&RIGHT_CODE=" + rightCode + "&LINK_PHONE="
			+ linkPhone + "&START_DATE=" + startDate + "&REMARK=" + remark
			+ "&END_DATE=" + endDate, "MgrInfoPart", function(data) {

	}, function(error_code, error_info, derror) {

	});
}

// 新增权限
function doDispatch() {
	var staffId = $("#cond_STAFF_ID").val();
	var userProductCode = $("#con_USER_PRODUCT_CODE").val();
	var rightCode = $("#con_RIGHT_CODE").val();
	var startDate = $("#con_START_DATE").val();
	var endDate = $("#con_END_DATE").val();
	if (userProductCode == '') {
		alert('集团产品编码不能为空！');
		return false;
	}
	if (rightCode == '') {
		alert('权限编码不能为空！');
		return false;
	}
	if (startDate == '') {
		alert('开始时间不能为空！');
		return false;
	}
	if (endDate == '') {
		alert('结束时间不能为空！');
		return false;
	}
	if (startDate > endDate) {
		alert('开始时间不能大于结束时间！');
		return false;
	}
	$.beginPageLoading("正在保存数据......");
	$.ajax.submit("MgrInfoPart", "doDispatch", "&STAFF_ID=" + staffId,
			"RefreshPart,MgrInfoPart", function(data) {
				$.showSucMessage("数据新增成功", "", "");
				$.endPageLoading();
			}, function(error_code, error_info, derror) {
				$.endPageLoading();
				showDetailErrorInfo(error_code, error_info, derror);
			});
}

// 删除权限
function delStaffVpmnRight() {
	var rightCode = $("#con_RIGHT_CODE").val();
	var userProductCode = $("#con_USER_PRODUCT_CODE").val();
	var startDate = $("#con_START_DATE").val();
	if (userProductCode == '') {
		alert('集团产品编码不能为空！');
		return false;
	}
	if (rightCode == '') {
		alert('权限编码不能为空！');
		return false;
	}
	if (startDate == '') {
		alert('开始时间不能为空！');
		return false;
	}
	$.beginPageLoading("正在删除数据......");
	$.ajax.submit("MgrInfoPart", "delStaffVpmnRight", "",
			"RefreshPart,MgrInfoPart", function(data) {
				$.showSucMessage("数据删除成功", "", "");
				$.endPageLoading();
			}, function(error_code, error_info, derror) {
				$.endPageLoading();
				showDetailErrorInfo(error_code, error_info, derror);
			});
}

function sele() {
	var select = $("#searchByNum").get(0).checked;
	if (select == true) {
		$("#POP_cond_STAFF_ID").val("");
		$("#cond_STAFF_ID").val("");
		$("#POP_cond_STAFF_ID").attr("disable", true);
	} else {
		$("#POP_cond_STAFF_ID").attr("disable", false);
	}
}