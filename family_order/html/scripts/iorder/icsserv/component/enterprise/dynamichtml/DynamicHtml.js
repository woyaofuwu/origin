var DynamicHtml = function(id, param) {
    this.id = id;
    this.param = param;
    
    if (param.iscache == undefined) {
    	this.param.iscache = true;
	}
    
    if (param.subpage && param.subpage != "null" && param.isiframe == "false") {
    	this.requestPage(param.subpage, param.listener, param.param, "", param.subsys);
	}
};
(function(c){
	var o = c.prototype;
    
	c.setIframeHeight = function (obj) {
		if (document.getElementById) {
			c._setHeight(obj);
			
			$(obj.contentWindow.document.body).click(function(){
				c._setHeight(obj);
			});
		}
	};
	
	c._setHeight = function(obj) {
		var win = obj.contentWindow, 
	    body = win.document.body;
		
		$(obj).parent().css("height", (body.scrollHeight - 16)/100 + "rem");
	};
	
	c._importJs = function(jspath) {
		document.write('<script src="' + jspath + '" type="text/javascript"></script>');
	};
	
	o.setReturnData = function (data) {
		var str = "";
		if (data instanceof Wade.DataMap
				|| data instanceof Wade.DatasetList) {
			str = data.toString();
		} else {
			str = JSON.stringify(data);
		}
		$("#"+this.id+"dataField").text(str);
	};
	
	o.getReturnData = function (isJson) {
		var str = $("#"+this.id+"dataField").text();
		if (str) {
			if (isJson) {
				return JSON.parse(str);
			} else {
				var start = str.substring(0,1);
				if (start == "{") {
					return new Wade.DataMap(str);
				} else if (start == "[") {
					return new Wade.DatasetList(str);
				}
			}
		}
	};
	
	o.requestPage = function (pageParam, func) {
		var self = this;
		ajaxPost(pageParam.subpage,pageParam.listener,pageParam.param,self.id,function(){
			if (pageParam.jslistener) {
				eval(pageParam.jslistener).call(this, pageParam);
			}
			if (func) {
				func.call(this, pageParam);
			}
		});
	};
	
	o.request = function(pageParam, func) {
		if (pageParam && pageParam.subpage) {
			var self = this;
			if (this.param.iscache) {
				if(this.param.subpage && pageParam.subpage && (this.param.subpage == pageParam.subpage)){
					if(this.param.listener && pageParam.listener && (this.param.listener == pageParam.listener)){
						if(this.param.param && pageParam.param && (this.param.param == pageParam.param)){
							if (func) {
								func.call(this, pageParam);
							}
							return;
						}
					}
				}
			}
			this.param.subpage = pageParam.subpage;
			this.param.listener = pageParam.listener;
			this.param.param = pageParam.param;
			
			if (this.param.isiframe == "true") {
				
				var url = "";
				if (pageParam.subsys) {
					url += pageParam.subsys;
				}
				
				url += "?service=page/" + pageParam.subpage
				
				if (pageParam.listener) {
					url += "&listener=" + pageParam.listener;
				}
				url += "&id=" + this.id;
				if (pageParam.param) {
					url += pageParam.param;
				}
				$("#"+this.id+"iframe").attr("src", url);
			} else {
				if (pageParam.jspath) {
					if (pageParam.jspath != this.param.jspath) {
						$.getScript(pageParam.jspath, function(){
							self.requestPage(pageParam,func);
						});
					}else{
						$.getScript(pageParam.jspath, function(){
							self.requestPage(pageParam,func);
						});
					}
				}
			}
		}
	};
	
	c.getHTMLScript = function (ajaxLoadedData) {
		var result = [];
		// 第一步：匹配加载的页面中是否含有js
		var regDetectJs = /<script(.|\n)*?>(.|\n|\r\n)*?<\/script>/ig;
		var jsContained = ajaxLoadedData.match(regDetectJs);
		
		// 第二步：如果包含js，则一段一段的取出js再加载执行
		if(jsContained) {
			// 分段取出js正则
			var regGetJS = /<script(.|\n)*?>((.|\n|\r\n)*)?<\/script>/im;

			// 按顺序分段执行js
			var jsNums = jsContained.length;
			for (var i=0; i<jsNums; i++) {
				var jsSection = jsContained[i].match(regGetJS);
				
				if (jsSection[2]) {
					var js = jsSection[2];
					if (js.indexOf("<![CDATA[") > 0) {
						js = jsSection[2].substring(jsSection[2].indexOf("<![CDATA[")+9, jsSection[2].indexOf("//]]>"));
					}
					result.push(js);
				}
			}
		}
		return result;
	};
	
	c.exeScript = function(script) {
		if(script) {
			if(window.execScript) {
				// 给IE的特殊待遇
				window.execScript(script);
			} else {
				// 给其他大部分浏览器用的
				window.eval(script);
			}
		}
	};
	
})(DynamicHtml);