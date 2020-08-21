var selectExistRole = function() {
	var ConstData = window.constdata
	var srcObj = null;
	var serialNumber = "";//当前角色的手机号码
	var thisPopGroupId = "";
	var showPopId = "";
	var partId = null;
	var eparchyCode = "";
	var title = "";
	var popRoleCode = "";
	var isFmMgr = false;
	function showPop(that, roleCode, roleType) {
		srcObj = that;
		isFmMgr = srcObj.isFmMgr;
		partId = srcObj.part;
		serialNumber = srcObj.roleCode == "1" ? srcObj.sn : srcObj.mainSn;
		eparchyCode = srcObj.eparchyCode;
		popRoleCode = roleCode;
		thisPopGroupId = ConstData.popExistIdRole[roleCode];
		showPopId = ConstData.showPopExistIdRole[roleCode];
		title = ConstData.busiTitle[roleCode];
		var html = renderTemplate(serialNumber, roleCode, roleType);// 得到渲染结果
		$("#" + thisPopGroupId).empty();
		$("#" + thisPopGroupId).append(html);// 展示模板
		bindEvents();// 绑定事件
		showPopup(showPopId);// 展示弹出层

	}

	function renderTemplate(serialNumber, roleCode, roleType) {
		var html = '';
		$.beginPageLoading("已有" + title + "加载中。。。");
		var param = "&SERIAL_NUMBER=" + serialNumber + "&ROLE_CODE=" + roleCode + "&ROLE_TYPE=" + roleType;
		hhSubmit(null, window.constdata.HANDLER, "queryRoleUserList", param, function(ajaxData) {
			$.endPageLoading();
			if (ajaxData) {
				html = drawWideHtml(ajaxData);
			}
		}, function(errorCode, errorInfo) {
			$.endPageLoading();
		}, {
			async : false
		});

		return html;
	}

	function drawWideHtml(roleList) {
		var html = '<div class="c_header">';
		html += '<div class="back" name="" handle="roleback"> 已有' + title + '</div>';
		html += '</div>';

		html += '<div class="c_scroll c_list-phone-col-1 c_scroll-float c_scroll-header c_scroll-submit">';
		html += '<div class="c_box">';
		html += '<div class="c_list c_list-s c_list-phone-col-1">';
		html += '<ul>';

		for (var i = 0; i < roleList.length; i++) {
			var roletemp = roleList.get(i);
			var roleSn = roletemp.get("ROLE_SERIAL_NUMBER");
			var roleUserId = roletemp.get("ROLE_USER_ID");
			var roleProductName = roletemp.get("ROLE_PRODUCT_NAME");
			html += '<li>';
			html += '<div class="fn">';
			html += '<input type="radio" name="oldRole" roleSn=' + roleSn + ' roleUserId=' + roleUserId + '  checked="" >'
			html += '</div>';
			html += '<div class="main">' + '【' + roleSn + '】' + roleProductName + '</div>';
			html += '</li>';
		}
		html += '</ul>';
		html += '</div>';
		html += '</div>';
		html += '</div>';

		html += '<div class="l_bottom">';
		html += '<div class="c_submit c_submit-full">';
		html += '<button type="button" name="cancel" class="e_button-l e_button-navy" handle="rolecancel">' + '取消' + '</button>';
		html += '<button type="button" name="ok" class="e_button-l e_button-r e_button-blue" handle="roleok">' + '确定' + '</button>';
		html += '</div>';
		html += '</div>';

		return html;
	}

	function bindEvents() {
		$("div[handle=roleback]").bind("click", function() {
			hidePopup(showPopId);
		});

		$("button[handle=rolecancel]").bind("click", function() {
			hidePopup(showPopId);
		});

		$("button[handle=roleok]").bind("click", function() {
			var roleSn = $("input[name=oldRole]:checked").attr("roleSn");
			var roleUserId = $("input[name=oldRole]:checked").attr("roleUserId");
			createOwnRole(roleSn, roleUserId);
		});

	}

	function createOwnRole(roleSn, roleUserId) {
		hidePopup(showPopId);
		$.beginPageLoading("规则检验中...");
		var param = "&USER_ID=" + roleUserId + "&SERIAL_NUMBER=" + roleSn + "&ROLE_CODE=" + popRoleCode + "&ROLE_TYPE=" + "OLD" + "&TRADE_TYPE_CODE=" + common.tradeTypeCode;
		hhSubmit(null, window.constdata.HANDLER, "checkAddChilrenRole", param, function(data) {
			$.endPageLoading();
			if ("2" == popRoleCode) {
				var wide = common.createRole(WidenetExsit, {
					partId : partId,
					sn : serialNumber,
					eparchyCode : eparchyCode,
					type : "OLD",
					oldWideSn : roleSn,
					isFmMgr: isFmMgr
				});
				if (wide) {
					srcObj.addSubRole(wide);
				}
			} else if ("3" == popRoleCode) {

				var areaCode = srcObj.find("input[name=USER_WIDE_AREA_CODE]").val();
				var ims = common.createRole(Familyexistims, {
					partId : partId,
					sn : serialNumber,
					eparchyCode : eparchyCode,
					type : "OLD",
					areaCode : areaCode,
					imsSn : roleSn,
					imsUserId : roleUserId
				});
				if (ims) {
					srcObj.addSubRole(ims);
				}
			}


		}, function(error_code, error_info) {
			$.endPageLoading();
			$.MessageBox.error(error_code, error_info);
		});

	}

	return {
		showPop : showPop,
	}
}();