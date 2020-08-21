/*!
 * 统一外框菜单层级加载js
 * auth:xiedx@asiainfo.com
 * date:2015/11/24
 */
(function(){
	var menuClazz = "com.ai.saas.ngboss.handler.MenuHandler";
	var activeL1MenuId, activeL2MenuId, activeL3MenuId
	var isLoading = false;
	
	//处理JSON数据
	function parseErrorData(data){
		if(data && $.isString(data)){
			var code_idx = data.indexOf("\"x_resultcode\":\"");
			var info_idx = data.indexOf("\"x_resultinfo\":\"");
			if(code_idx >0 && info_idx >0){
				data = data.substring(info_idx + 16);
				data = data.substring(0,data.indexOf("\""));
				data = "error:" + data;
			}
		}else if(data && $.isObject(data) && data.context){
			data = "error:" + data.context.x_resultinfo
		}
		return data;
	}
	
	$.navmenu = {	
		loadSecDomain: function(){
			
			isLoading = true;
			$("#domainLoading").css("display", "");
			$("#domainLoading_title").text("加载中");
			$("#domainLoading_content").text("loading");
			
			$.httphandler.get(menuClazz, "loadSecDomain", null, function(data){
				isLoading = false;
				data = parseErrorData(data);
				if(data){
					if(data.indexOf("error:") == 0){
					 	$("#domainLoading_title").text("加载失败");
					 	$("#domainLoading_content").text( data.replace(/error:/ig, "") );
					}else{
						$("#domainLoading").after(data); //插入在loading后
						$("#domainLoading").css("display", "none");
					}
					if($.browser.msie){
						CollectGarbage();
					}
				}
				if(window.fullmenuScroll){
					fullmenuScroll.refresh();
				}
			},null,{
				simple: true,
				dataType: "text"
			});
		},	
		loadL1Menu: function(domainId){
			if(!domainId){
				alert("domainId 为空！");
				return;
			}
			
			isLoading = true;
			$("#l1MenuLoading").css("display", "");
			$("#l1MenuLoading_title").text("加载中");
			$("#l1MenuLoading_content").text("loading");

			$.httphandler.get(menuClazz, "loadL1Menu", "&DOMAIN_ID=" + domainId, function(data){
				isLoading = false;
				data = parseErrorData(data);
				if(data){
					if(data.indexOf("error:") == 0){
					 	$("#l1MenuLoading_title").text("加载失败");
					 	$("#l1MenuLoading_content").text( data.replace(/error:/ig, "") );
					}else{
						$("#l1MenuLoading").after(data); //插入在loading后
						$("#l1MenuLoading").css("display", "none");
					}
				}
				if(window.fullmenuScroll){
					fullmenuScroll.refresh();
				}
				if($.browser.msie){
					CollectGarbage();
				}				
			},null,{
				simple : true,
				dataType : "text"
			});
		},
		loadL2Menu: function(menuId){
			if(!menuId){
				alert("menuId为空");
				return;
			}
			
			isLoading = true;
			$("#l2MenuLoading").css("display", "");
			$("#l2MenuLoading_title").text("加载中");
			$("#l2MenuLoading_content").text("loading");
			
			$.httphandler.get(menuClazz, "loadL2Menu", "&PARENT_MENU_ID=" + menuId, function(data){
				isLoading = false;
				data = parseErrorData(data);
				if(data.indexOf("error:") == 0){
					$("#l2MenuLoading_title").text("加载失败");
					$("#l2MenuLoading_content").text( data.replace(/error:/ig, "") );
				}else{
					$("#l2MenuLoading").after(data); //插入在loading后
					$("#l2MenuLoading").css("display", "none");
				}
				if(window.fullmenuScroll){
					fullmenuScroll.refresh();
				}
			},null,{
				simple : true,
				dataType : "text"
			});
		},
		loadL3Menu: function(menuId, r){
			if(!menuId){
				alert("menuId为空");
				return;
			}
			
			isLoading = true;
			$("#l3MenuLoading").css("display", "");
			$("#l3MenuLoading_title").text("加载中");
			$("#l3MenuLoading_content").text("loading");
			
			$.httphandler.get(menuClazz,"loadL3Menu", "&PARENT_MENU_ID=" + menuId, function(data){
				isLoading = false;
				data = parseErrorData(data);
				if(data.indexOf("error:") == 0){
					$("#l3MenuLoading_title").text("加载失败");
					$("#l3MenuLoading_content").text( data.replace(/error:/ig, "") );
				}else{
					$("#l3MenuLoading").after(data); //插入在loading后
					$("#l3MenuLoading").css("display", "none");
				}
				if(window.fullmenuScroll){
					fullmenuScroll.refresh();
				}
				if($.browser.msie){
					CollectGarbage();
				}
			},null,{
				simple : true,
				dataType : "text"
			});
		},	
		showL1Menu: function(domainId){
			if(!isLoading && domainId && domainId != activeL1MenuId){
				
				if(activeL1MenuId){ //隐藏原菜单
					
					//隐藏原活动三级菜单
					if(activeL3MenuId){
						$("#navMenu_L3_" + activeL3MenuId).css("display", "none");
						$("#navMenu_L2_" + activeL2MenuId).find("li[menuId=" + activeL3MenuId + "]").attr("className", "link");
						activeL3MenuId = null;
					}

					//隐藏原活动二级菜单
					if(activeL2MenuId){
						$("#navMenu_L2_" + activeL2MenuId).css("display", "none");
						$("#navMenu_L1_" + activeL1MenuId).find("li[menuId=" + activeL2MenuId + "]").attr("className", "link");
						activeL2MenuId = null;
					}
					
					//隐藏原活动一级菜单
					$("#navMenu_L1_" + activeL1MenuId).css("display", "none");
					$("#navDomainList").find("li[domainId=" + activeL1MenuId + "]").attr("className", "link");
				}
				
				var navMenu = $("#navMenu_L1_" + domainId);
				if(navMenu.length){
					navMenu.css("display", "");
					if(window.fullmenuScroll){
						fullmenuScroll.refresh();
					}
				}else{
					$.navmenu.loadL1Menu(domainId);
				}
				
				//设置活动
				activeL1MenuId = domainId;
				$("#navDomainList").find("li[domainId=" + activeL1MenuId + "]").attr("className", "link on");
			}
		},
		showL2Menu: function(menuId){
			if(!isLoading && menuId && menuId != activeL2MenuId){
				
				if(activeL2MenuId){
					
					//隐藏原活动三级菜单
					if(activeL3MenuId){
						$("#navMenu_L3_" + activeL3MenuId).css("display", "none");
						$("#navMenu_L2_" + activeL2MenuId).find("li[menuId=" + activeL3MenuId + "]").attr("className", "link");
						activeL3MenuId = null;
					}

					//隐藏原活动二级菜单
					$("#navMenu_L2_" + activeL2MenuId).css("display", "none");
					$("#navMenu_L1_" + activeL1MenuId).find("li[menuId=" + activeL2MenuId + "]").attr("className", "link");
				}
				
				var navMenu = $("#navMenu_L2_" + menuId);
				if(navMenu.length){
					navMenu.css("display", "");
					if(window.fullmenuScroll){
						fullmenuScroll.refresh();
					}
				}else{
					$.navmenu.loadL2Menu(menuId);
				}
				
				activeL2MenuId = menuId;
				$("#navMenu_L1_" + activeL1MenuId).find("li[menuId=" + activeL2MenuId + "]").attr("className", "link on");
			}
		},
		showL3Menu: function(menuId){
			if(!isLoading && menuId && menuId != activeL3MenuId){
				
				if(activeL3MenuId){	
					//隐藏原活动二级菜单
					$("#navMenu_L3_" + activeL3MenuId).css("display", "none");
					$("#navMenu_L2_" + activeL2MenuId).find("li[menuId=" + activeL3MenuId + "]").attr("className", "link");
				}
				
				var navMenu = $("#navMenu_L3_" + menuId);
				if(navMenu.length){
					navMenu.css("display", "");
					if(window.fullmenuScroll){
						fullmenuScroll.refresh();
					}
				}else{
					$.navmenu.loadL3Menu(menuId);
				}
				
				activeL3MenuId = menuId;
				$("#navMenu_L2_" + activeL2MenuId).find("li[menuId=" + activeL3MenuId + "]").attr("className", "link on");
			}			
		},
		onSecDomainTap: function(el){
			var domainId = $.attr(el, "domainId");
			$.navmenu.showL1Menu(domainId);
		},
		onL1MenuTap: function(el){
			var menuId = $.attr(el, "menuId");
			$.navmenu.showL2Menu(menuId);
		},
		onL2MenuTap: function(el){
			var menuId = $.attr(el, "menuId");
			$.navmenu.showL3Menu(menuId);
		},
		onMenuItemTap: function(el, menuId, modeCode, modeName){
			if(!el || !el.nodeType)
				return;
			
			var title = $.trim( $.text(el) ); 
			//$(el).parents("div[class=menu menu-col-3]:first").find("li[class=on]:first").removeClass("on");
			//el.className = "on";
			
			//打开菜单或添加到快捷菜单
			if(parent.$ && parent.$.fastmenu && parent.$.fastmenu.isEditMode()){
				parent.$.fastmenu.actions.addMenu(menuId, title, modeCode, modeName, [e.pageX, e.pageY]);
			}else if(parent.$ && parent.$.menuopener){
				parent.$.menuopener.openMenu(menuId, title, modeName);
			}
			
			//更新点击次数
			if($.hotmenu){
				$.hotmenu.actions.updateMenu(menuId);
			}
		},
		init: function(){
			//加载中心列表
			$.navmenu.loadSecDomain();
		}	
	};

	
	var activeSearch = null;
	var searchTxt = {};
	var currentListIdx = {};
	var listCount = {};
	var searchTimer = null;
	var searchTime = 200; //500ms
	$.navsearch = {
		init: function(){
			$("#menu_search").bind("tap",$.navsearch.onClick);
			$("#menu_search").bind("keydown",$.navsearch.onKeyDown);
			$("#menu_search").bind("keyup",$.navsearch.onKeyUp);
			$("#button_search").bind("tap",$.navsearch.showSearchPage);
			/*
			$("#menu_search_ct").bind("tap",$.navsearch.onListClick);
			
			$("div[class=m_wrapper]:first", document.body).bind("scroll", function(){ //滚动时隐藏
				$.navsearch.hideList();
			});
			*/
			$(document.body).bind("ontouchstart" in window ? "touchstart" : "mousedown", $.navsearch.onDocClick)
			//$(document.body).bind("click", $.navsearch.onDocClick);
			
		},
		showSearchPage:function(){
			var searchStr = $("#menu_search").val();
			if(searchStr.length < 2){
				alert("请输入搜索关键字，至少两个字符");
				return ;
			}
			openNav('搜索结果', 'SearchFrame');
			return false;
		},
		showList: function(){
			if(!activeSearch) return ;
			$("#" + activeSearch + "_ct").addClass("c_float-show");
			return false;
		},
		hideList:function(){
			$("#" + activeSearch + "_ct").removeClass("c_float-show");
		},
		clearList:function(){
			$("#" + activeSearch + "_list").empty();
		},
		doSearch:function(txt){
			if(searchTxt[activeSearch] == txt){
				return;
			}
			searchTxt[activeSearch] = txt;
			$.httphandler.getJsonp(menuClazz,"searchMenu","q=" + encodeURIComponent(txt),function(data){
				data = parseErrorData(data);
				currentListIdx[activeSearch] = -1;
				var list_ct = $("#" + activeSearch + "_list");
				list_ct.empty();
				if(data){
					if(data.indexOf("error:") == 0){
						alert("菜单搜索发生错误：" + data);
						return;
					}
					list_ct[0].innerHTML = data;
					listCount[activeSearch] = list_ct.children("li").length;
					if(listCount[activeSearch] > 0){
						currentListIdx[activeSearch] = 0;
						$(list_ct.children("li")[0]).addClass("on");
						$.navsearch.showList();
					}else{
						$.navsearch.hideList();
					}
				}
			});
			
		},
		onClick:function (e){
			if(!e.target) return;
			activeSearch = $.attr(e.target, "id");
			var offset = $(e.target).offset();
			var ct = $("#"+activeSearch+"_ct");
			ct.css("left", ($.format.px2rem(offset.left)+0.125) + "rem");
			//ct.css("top", (offset.top + 38)+ "px");
			var txt = this.value;
			if("" != txt){
				$.navsearch.showList();
			}
			//$(this).select();
			return false;
		},
		onKeyDown:function(e){
			//回车键
			if(e.keyCode==13 || e.keyCode==108){
				var idx = currentListIdx[activeSearch]; 
				if(idx > -1){
					var el = $("#" + activeSearch + "_list li:nth-child(" +(idx+1)+ ")");
					if(el.length){
						//打开菜单
						el.trigger("tap");
					}
				}
				return false;
			}
		},
		onKeyUp:function(e){
			//左右方向键
			if(e.keyCode==37 || e.keyCode==39){
				$(this).select();
				return false;
			}
			//上下方向键
			if(e.keyCode==38 || e.keyCode==40){
				var idx = currentListIdx[activeSearch];
				if(undefined == idx || idx < 0){
					idx = 0;
				}else{
					if(e.keyCode==38){
						if(idx == 0){
							return false;
						}else{ //向上
							idx--;
						}
					}else if(e.keyCode==40){
						if(idx == (listCount[activeSearch]-1)){
							return false;
						}else{ //向下
							idx++;
						}
					} 
				}
				currentListIdx[activeSearch] = idx;
							
				//alert("currentListIdx:" + currentListIdx);
				$("#" + activeSearch + "_list li[class*=on]").removeClass("on");
				$("#" + activeSearch + "_list li:nth-child(" +(idx+1)+ ")").addClass("on");
				return false;
			}		
			
			//字母数字和空格、退格
			if(e.keyCode==32 || e.keyCode==8
			    || (e.keyCode>64 && e.keyCode<91)
			    || (e.keyCode>47 && e.keyCode<58)
			    || (e.keyCode>95 && e.keyCode<106)){
			
				var txt = this.value;
				txt = $.trim(txt);
				
				//退格到输入框无内容时隐藏列表
				if(e.keyCode==8 && txt==""){
					return;
				}
				//至少2个字符进行搜索
				if(txt!="" && txt.length>1){
					if(searchTimer != null){
						clearTimeout(searchTimer);
					}
					searchTimer = setTimeout(function(){
						$.navsearch.doSearch(txt);
						if(searchTimer != null){
							clearTimeout(searchTimer);
							searchTimer = null;
						}
					}, searchTime);
				}else{
					$.navsearch.hideList();
				}
			}
		},
		onListClick:function(e){
			var el=e.target;
			if(el){
				if($.nodeName(el,"span")){
					el=el.parentNode;
				}
				if($.nodeName(el,"li")){
					//获取菜单数据
					//var idx=$.attr(el,"idx");
					//var m_idx=$.attr(el,"m_idx");
					var idx = $.inArray(el,$("#nav_search_list li"));
					//设置当前项
					if(idx > -1){
						currentListIdx = idx;
						$("#nav_search_list a[class=on]").removeClass("on");
						$("#nav_search_list a:nth-child(" +(currentListIdx+1)+ ")").addClass("on");
					}
					//$(el).trigger("click");
					//打开菜单
					//$.navsearch.openMenu($(el).text(),menu.MOD_NAME);
					
				}
			}
			return false;
		},
		
		onDocClick:function(e){
			e = e.target;
			while(e){
				if(e.nodeName=="BODY"){
					break;
				}
				var eid = $.attr(e, "id");
				if(eid=="menu_search_ct"||eid=="menu_search"||eid=="button_search"){
					return true;
				}
				e = e.parentNode;
			}
			var ipt = $("#" + activeSearch);
			var txt = ipt.val();
			if("" == $.trim(txt)){
				searchTxt[activeSearch] = null;
			}
			$.navsearch.hideList();
			return true;
		},
		onMenuItemTap: function(el, menuId, modeCode, modeName,title,idx){
			if(!el || !el.nodeType) return;
			currentListIdx[activeSearch] = idx;
			$("li[class*=on]:first", el.parentNode).removeClass("on");
			$(el).addClass("on");
			$.navsearch.hideList();
			
			//打开菜单或添加到快捷菜单
			if(parent.$ && parent.$.fastmenu && parent.$.fastmenu.isEditMode()){
				parent.$.fastmenu.actions.addMenu(menuId, title, modeCode, modeName, [e.pageX, e.pageY]);
			}else if(parent.$ && parent.$.menuopener){
				parent.$.menuopener.openMenu(menuId, title, modeName);
			}
			
			//更新点击次数
			if($.hotmenu){
				$.hotmenu.actions.updateMenu(menuId);
			}
		},
		onMenuItemClick:function(event, item, menu_id, mod_code, mod_name){
			var e = $.event.fix(event);
			var el = $(item);
			var title = el.text();
			$("li[class=on]:first", item.parentNode).removeClass("on");
			el.className="on";
			$.navsearch.hideList();
			
			//打开菜单或添加到快捷菜单
			if(parent.$ && parent.$.fastmenu && parent.$.fastmenu.isEditMode()){
				parent.$.fastmenu.actions.addMenu(menu_id, title, mod_code, mod_name, [e.pageX, e.pageY]);
			}else if(parent.$ && parent.$.menuopener){
				parent.$.menuopener.openMenu(menu_id, title, mod_name);
			}
			
			//更新点击次数
			if($.hotmenu){
				$.hotmenu.actions.updateMenu(menu_id);
			}
		}
	};
	
	$(function(){
		$.navmenu.init();
		$.navsearch.init();
	});
	
})();