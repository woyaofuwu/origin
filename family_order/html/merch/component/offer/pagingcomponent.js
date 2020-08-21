if (typeof (PagingComponent) == "undefined") {
	PagingComponent = function(obj, observerUpdate) {
		this.dom = obj;
		this.observerUpdate = observerUpdate;
		this.current = 1;// 当前页
		this.onePageCount = 21;// 每页条数
		this.totalPageSize = 0;// 页总数
		this.listCount = 0;// 列表总数

		this.currentDom = $(this.dom).find("#current");
		this.totalPageSizeDom = $(this.dom).find("#totalPageSize");// 总页数
		this.firstDom = $(this.dom).find(".first");// 首页
		this.lastDom = $(this.dom).find(".last");// 尾页
		this.prevDom = $(this.dom).find(".prev");// 上一页
		this.nextDom = $(this.dom).find(".next");// 下一页
		this.onePageCountDom = $(this.dom).find("#onePageCount");// 每页条数
		this.onePageCountDom.html(this.onePageCount);
		this.turnPageSizeDom = $(this.dom).find("#turnPageSize");// 跳转条数

		var that = this;

		$(this.dom).find("div.turn,div.option").bind("click", function(n) {
			var obj = n.target;
			var handle = obj.getAttribute("handle");
			if (handle == null || handle == "") {
				handle = obj.parentNode.getAttribute("handle");
				obj = obj.parentNode;
			}
			if (handle != null && handle != "" && obj.className.indexOf("e_dis") < 0) {
				that[handle].call(that, obj);
			}
		});
		this.resetCurrent(1);
	};
	PagingComponent.prototype = {
		initTotalPageSize : function(listCount) {
			if (listCount) {
				this.listCount = listCount;
				this.setTotalPageSize(Math.ceil(listCount / this.onePageCount));
			} else {
				this.listCount = 0;
				this.setTotalPageSize(0);
			}
			if (this.current == 1) {
				this.resetBtnStyle();
			}
			
			if(!listCount || listCount == 0) {
				this.nextDom.addClass("e_dis");
				this.lastDom.addClass("e_dis");
			}
		},

		resetCurrent : function() {
			this.setCurrent(1);
			this.resetBtnStyle();
		},

		setChange : function(current) {
			this.setCurrent(current);
			this.resetBtnStyle();
			this.observerUpdate(current);
		},
		resetBtnStyle : function() {
			this.nextDom.removeClass("e_dis");
			this.lastDom.removeClass("e_dis");
			this.firstDom.removeClass("e_dis");
			this.prevDom.removeClass("e_dis");
			if (this.current == this.totalPageSize) {
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
			this.currentDom.html(current);
		},
		setTotalPageSize : function(totalPageSize) {
			this.totalPageSize = totalPageSize;
			this.totalPageSizeDom.html(totalPageSize);
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
			this.setChange(this.totalPageSize);
		},
		turnPage : function(obj) {
			var n = $(obj).prev().val() * 1;
			if (n) {
				if (n > this.totalPageSize) {
					alert("输入的页码有误！");
					$(obj).prev().val("");
					return;
				}
				n != this.current && this.setChange(n);
			}
			$(obj).parent().parent().hide();
		},

		statisticl : function(obj) {
			$(obj).html(this.listCount);
		}
	}
}
