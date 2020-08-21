function onSearchBtnSubmitClick() {
	// 查询条件校验
	if (!$.validate.verifyAll("QueryCondPart")) {
		return false;
	}

	// 查询
	ajaxSubmit('QueryCondPart,infonav', 'queryServCodeInfo', null,
			'QueryListPart,ctrlInfoPart', new function(data) {
				$("#ctrlInfoPart").attr("style", "display:;");
			});
}

function popCampOnPage() {
	popupPage('group.querygroupinfo.AdcServCodeCampOn', null, null, '服务号码预占',680,400,'btnCampon');
}

//点确定按钮返回数据到父页面隐藏字段
function setData()
{
	if(!$.validate.verifyAll('memParamsInfoForm')) return false;

	var servCode = $("#SVR_CODE").val();
	if (servCode == "" || servCode == null) {
		alert("服务代码不能为空！");
		return false;
	}

	var custManageId = $("#CUST_MANAGER_ID").val();
	if (custManageId == "" || custManageId == null) {
		alert("客户经理不能为空！");
		return false;
	}

	var param = "&SVR_CODE=" + servCode + "&CUST_MANAGER_ID=" + custManageId;

	// 提交
	$.ajax.submit('', 'insertCamponServCode', param, '', function(data) {
		if (data.map.result == "true") {
			alert("预占成功！");
		} else {
			alert("预占失败！" + data.map.message);
		}
	}, function(error_code, error_info) {
	}, {
		async : false
	});

	$.setReturnValue();

}
//点取消按钮返回原值数据到父页面隐藏字段
function setCancleData()
{
	$.setReturnValue();
}

function deteleServCode(e) {
	var svrCode = $(e).attr('svrCode');
	var custManageId = $(e).attr('custManageId');

	var param = '&svrCode=' + svrCode + '&custManageId=' + custManageId;

	$.ajax.submit('QueryCondPart,infonav', 'deteleServCode', param, 'QueryListPart,ctrlInfoPart', function(data) {
		if (data.map.result == "true") {
			alert("删除成功！");
		} else {
			alert("删除失败！");
		}
	}, function(error_code, error_info) {
	}, {
		async : false
	});
}


