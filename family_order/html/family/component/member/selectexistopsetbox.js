var selectExistTopsetBox = function() {

	var ConstData = window.constdata;
	var _this = null;
	var serialNumber = "";
	var eparchyCode = "";
	var popRoleCode = "";
	var thisPopGroupId = "";
	var title = "";
	var productId = "";
	var topSetBoxAreaId = "";

	function showPop(obj, prId) {

		_this = obj;
		serialNumber = _this.mainSn;
		eparchyCode = _this.eparchyCode;
		popRoleCode = _this.roleCode;
		thisPopGroupId = ConstData.popExistIdRole[_this.roleCode];
		showPopId = ConstData.showPopExistIdRole[_this.roleCode];
		title = ConstData.busiTitle[_this.roleCode];
		productId = prId;
		topSetBoxAreaId = _this.topSetBoxAreaId;
		var html = renderTemplate(serialNumber, productId);// 得到渲染结果
		$("#" + thisPopGroupId).empty();
		$("#" + thisPopGroupId).append(html);// 展示模板
		bindEvents();// 绑定事件
		showPopup(showPopId);// 展示弹出层
	}

	function renderTemplate(serialNumber, productId) {
		var html = '';
		$.beginLoading('已有' + title + '加载中...', topSetBoxAreaId);
		var param = "&OPER_FLAG=queryTopsetBoxInfo"
				  + "&SERIAL_NUMBER=" + serialNumber
				  + "&ROLE_CODE=" + _this.roleCode
				  + "&ROLE_TYPE=" + _this.roleType
				  + "&PRODUCT_ID=" + productId;
		hhSubmit(null, window.constdata.HANDLER, "familyTvOper", param, function(ajaxData) {
			$.endLoading(topSetBoxAreaId);
			if (ajaxData) {
				html = drawWideHtml(ajaxData);
			}
		}, function(errorCode, errorInfo) {
			$.endLoading(topSetBoxAreaId);
		}, {
			async : false
		});
		return html;
	}

	function drawWideHtml(topsetBoxList) {
		var html = '<div class="c_header">';
			html += '<div class="back" name="" handle="topsetboxback"> 已有' + title + '</div>';
			html += '</div>';

			html += '<div class="c_scroll c_list-phone-col-1 c_scroll-float c_scroll-header c_scroll-submit">';
			html += '<div class="c_box">';
			html += '<div class="c_list c_list-s c_list-phone-col-1">';
			html += '<ul>';

		for (var i = 0; i < topsetBoxList.length; i++) {
			var topsetBox = topsetBoxList.get(i);
			var roleSn = topsetBox.get("SERIAL_NUMBER");
			var roleUserId = topsetBox.get("USER_ID");
			var oldBasePackage = topsetBox.get("OLD_BASEPACKAGE");
			var oldBasePackageName = topsetBox.get("OLD_BASEPACKAGE_NAME");
			var oldResNo = topsetBox.get("OLD_RES_NO");

			if(oldBasePackage === "undefined" && oldResNo === "undefined"){
				continue;
			}

			html += '<li>';
			html += '<div class="fn">';
			html += '<input type="radio" name="oldtopsetbox" roleSn=' + roleSn
				 + ' roleUserId=' + roleSn
				 + ' oldBasePackage='+oldBasePackage
				 + ' oldBasePackageName=' +oldBasePackageName
				 + ' oldResNo=' +oldResNo
				 + ' checked="" >'
			html += '</div>';
			html += '<div class="main">' + '【' + oldBasePackage + '】' + oldBasePackageName + '|&nbsp;&nbsp;'  + oldResNo +'</div>';
			html += '</li>';
		}
		html += '</ul>';
		html += '</div>';
		html += '</div>';
		html += '</div>';

		html += '<div class="l_bottom">';
		html += '<div class="c_submit c_submit-full">';
		html += '<button type="button" name="cancel" class="e_button-l e_button-navy" handle="topsetboxcancel">' + '取消' + '</button>';
		html += '<button type="button" name="ok" class="e_button-l e_button-r e_button-blue" handle="topsetboxok">' + '确定' + '</button>';
		html += '</div>';
		html += '</div>';

		return html;
	}

	function bindEvents() {
		$("div[handle=topsetboxback]").bind("click", function() {
			hidePopup(showPopId);
		});

		$("button[handle=topsetboxcancel]").bind("click", function() {
			hidePopup(showPopId);
		});

		$("button[handle=topsetboxok]").bind("click", function() {
			var roleSn = $("input[name=oldtopsetbox]:checked").attr("roleSn");
			var roleUserId = $("input[name=oldtopsetbox]:checked").attr("roleUserId");
			var oldBasePackage = $("input[name=oldtopsetbox]:checked").attr("oldBasePackage");
			var oldBasePackageName = $("input[name=oldtopsetbox]:checked").attr("oldBasePackageName");
			var oldResNo = $("input[name=oldtopsetbox]:checked").attr("oldResNo");

			//添加存量魔百和规则校验
			if (!_this.checkIsCanAddOldTv(oldResNo)) {
				return false;
			}

			_this.find("input[id=OLD_TOP_SET_BOX_BASE_PACKAGES_"+_this.uniqueId+"]").val(oldBasePackage);
			_this.find("input[id=OLD_TOP_SET_BOX_BASE_PACKAGES_NAME_"+_this.uniqueId+"]").val(oldBasePackageName);
			_this.find("input[id=OLD_TOP_SET_BOX_RES_NO_"+_this.uniqueId+"]").val(oldResNo);
			hidePopup(showPopId);

		});
	}

	return {
		showPop : showPop,
	}
}();