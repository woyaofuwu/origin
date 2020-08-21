if (typeof (PagingComponent) == "undefined") {
	PagingComponent = function(obj, observerUpdate) {
		this.dom = obj;
		this.observerUpdate = observerUpdate;
		this.current = 1;
		this.count = 0;

		this.dataSourceCountDom = $(this.dom).find("#dataSourceCount");
		this.currentDom = $(this.dom).find("#current");
		this.pageSizeDom = $(this.dom).find("#pageSize");
		this.firstDom = $(this.dom).find(".first");
		this.lastDom = $(this.dom).find(".last");
		this.prevDom = $(this.dom).find(".prev");
		this.nextDom = $(this.dom).find(".next");

		var that = this;

		$(this.dom).find("div.turn").bind("click", function(n) {
			var obj = n.target;
			var cls = obj.className;
			if ("e_ico-first" == cls || "e_ico-last" == cls) {
				obj = obj.parentNode;
				cls = obj.className;
			}
			if (!$(obj).hasClass("e_dis")) {
				var fn = that[cls];
				if (fn) {
					fn.call(that, obj);
				} else {

				}
			}
		});
	};
	PagingComponent.prototype = {
		pageCount : 20,
		setDataSource : function(datas) {
			this.dataSource = datas;
			this.setChange(1, datas.length);
		},
		setChange : function(current, count) {
			this.currentDom.html(current);
			this.setCurrent(current);
			if (count) {
				this.dataSourceCount = count;
				this.dataSourceCountDom.html("总条数:" + count);
				this.pageSize = Math.ceil(count / this.pageCount);
				this.pageSizeDom.html(this.pageSize);
			}
			this.resetBtnStyle();

			var end = this.pageCount * this.current;
			var start = end - this.pageCount + 1;

			if (end >= this.dataSourceCount) {
				end = this.dataSourceCount;
			}
			start -= 1, end -= 1;
			var datas = this.subList(start, end);
			this.observerUpdate(datas);
		},
		subList : function(start, end) {
			var datas = this.dataSource;
			var length = datas.length;
			var result = $.DatasetList();
			for (var i = 0; i < length; i++) {
				if (end >= i && i >= start) {
					result.add(datas.get(i));
				}
			}
			return result;
		},
		resetBtnStyle : function() {
			this.nextDom.removeClass("e_dis");
			this.lastDom.removeClass("e_dis");
			this.firstDom.removeClass("e_dis");
			this.prevDom.removeClass("e_dis");
			if (this.current == this.pageSize) {
				this.nextDom.addClass("e_dis");
				this.lastDom.addClass("e_dis");
			}
			if (this.current == 1) {
				this.firstDom.addClass("e_dis");
				this.prevDom.addClass("e_dis");
			}
		},
		setCurrent : function(current) {
			this.current = current;
		},
		next : function(obj) {
			this.setChange(this.current + 1);
		},
		prev : function(obj) {
			this.setChange(this.current - 1);
		},
		first : function(obj) {
			this.setChange(1);
		},
		last : function(obj) {
			this.setChange(this.pageSize);
		}
	}
}
