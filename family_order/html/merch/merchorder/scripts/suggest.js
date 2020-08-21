var Suggest = function(id) {
	this.id = id;
	this.dom = $("#" + id);
	this.input = this.dom.find("input");
	this.btn = this.dom.find("button");
	var that = this;
	// this.input.bind("input propertychange", function() {
	// that.search();
	// });
	this.input.bind("keypress", function(n) {
		if (n.keyCode == 13) {
			that.search();
		}
	});
	
	this.input.bind("keyup", function(n) {
//		that.search();
	});
	
	this.btn.bind("click", function() {
		that.search();
	});
	$(document.body).bind("click", function(n) {
		if (n && n.target) {
			var a = n.target;
			var f = $(a).parents("#" + that.id);
			if (f.length == 0) {
				that.hideSearchItemListPart();
			}
		}
	});
}
Suggest.prototype = {
	HANDLER : "com.asiainfo.veris.crm.iorder.web.merch.component.OfferHandler",
	param : $.DataMap(),
	search : function() {
		var searchText = this.input.val();
		if (!searchText || searchText.length < 2) {
			return;
		}
		var param = "&SEARCH_TEXT=" + searchText;
		this.param.eachKey(function(k, v) {
			param += "&" + k + "=" + v;
		});
		if (typeof (getSpecialAreaParam) == "function") {
			param += getSpecialAreaParam();
		}
		var that = this;
		$.beginPageLoading("加载中。。。");
		hhSubmit(null, this.HANDLER, "search", param, function(ajaxData) {
			$.endPageLoading();
			if (that.customShowAction) {
				that.customShowAction(ajaxData);
			} else {
				that.showAction(ajaxData);
			}
		}, function(errorCode, errorInfo) {
			alert(errorInfo);
			$.endPageLoading();
		});
	},
	setParam : function(key, value) {
		this.param.put(key, value);
	},

	setShowAction : function(action) {// 设置现实的方法
		if (typeof (action) == "function") {
			this.customShowAction = action;
		} else {
			this.customShowAction = null;
		}
	},
	setAfterAction : function(afterAction) {// 设置选择的action
		this.afterAction = afterAction;
	},
	showAction : function(ajaxData) {
		if (this.customShowAction) {
			this.customShowAction(ajaxData);
		} else {
			var drawArea = $("#searchResult_float");
			if (drawArea.length > 0) {
				var sr = drawArea.find("#searchResult");
				sr.empty();
				var html = this.getSearchLi(ajaxData);
				sr.append(html);
			} else {
				var html = '<div id="searchResult_float" class="c_float" style="width : 230px;" >';
				html += '<div class="bg"></div>';
				html += '<div class="content">';
				html += '<div class="c_scrollContent">';
				html += '<div class="c_list c_list-pc-s c_list-phone-line">';
				html += '<ul id="searchResult" >';
				html += this.getSearchLi(ajaxData);
				html += '</ul>';
				html += '</div>';
				html += '</div>';
				html += '</div>';
				html += '</div>';
				$(document.body).append(html);
				var that = this;
				$("#searchResult", "#searchResult_float").bind("click", function(n) {
					if (n && n.target && typeof (that.afterAction) == "function") {
						var obj = $(n.target).parent("li");
						that.afterAction(obj);
					}
				});
			}
			this.showSearchItemListPart();
		}
	},
	getSearchLi : function(ajaxData) {
		var html = '';
		for (var i = 0; i < ajaxData.length; i++) {
			var data = ajaxData.get(i);
			var arg = '';
			data.eachKey(function(k, v) {
				arg += k + '="' + v + '" ';
			});

			html += '<li class="link" ' + arg + ' >';
			html += '<div class="main  e_size-m" title="' + data.get("DESC") + '" >';
			html += '[' + data.get("CODE") + ']' + data.get("NAME");
			html += '</div>';
			html += '</li>';
		}
		return html;
	},
	showSearchItemListPart : function() {
		var searchFloat = $("#searchResult_float");
		if (searchFloat.find("li").length == 0 || searchFloat.hasClass("c_float-show")) {
			return false;
		}

		var obj = this.dom;
		var offset = obj.offset();
		var left = offset.left;
		var top = offset.top + obj.height();
		searchFloat.css({
			"left" : left,
			"top" : top
		});
		searchFloat.addClass("c_float-show");
	},
	hideSearchItemListPart : function() {
		$("#searchResult_float").removeClass("c_float-show");
	},
	hide : function() {
		this.dom.hide();
	},
	show : function() {
		this.dom.show();
	},
	clear : function() {
		this.input.val('');
	},
	test : function() {
		var ds = $.DatasetList();
		var d = $.DataMap();
		// d.put("OFFER_CODE", "123");
		// d.put("OFFER_NAME", "456");
		// d.put("OFFER_TYPE", "P");
		// d.put("DESC", " hehe");
		// var d2= $.DataMap();
		// ds.add(d);
		// d2.put("OFFERS",ds);
		// this.showAction(d2);

		this.setShowAction(null);
		d.put("CODE", "123");
		d.put("NAME", "456");
		d.put("OFFER_TYPE", "P");
		d.put("DESCRIPTION", " hehe");
		ds.add(d);
		this.showAction(ds);
		this.setAfterAction(function(obj) {
			alert(obj)
		});
	},

}
