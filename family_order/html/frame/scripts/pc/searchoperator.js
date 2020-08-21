/*!
 * 统一外框菜单搜索
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
	
	
	
	var activeSearch = "menu_search";
	var searchTxt = {};
	var currentListIdx = {};
	var listCount = {};
	var searchTimer = null;
	var searchTime = 200; //500ms
	$.navsearch = {
		init: function(){
			$("#show_search").bind("tap",$.navsearch.showSearch);
			$("#close_search").bind("tap",$.navsearch.closeSearch);
			
			
			$("#menu_search").bind("tap",$.navsearch.onClick);
			$("#menu_search").bind("keydown",$.navsearch.onKeyDown);
			$("#menu_search").bind("keyup",$.navsearch.onKeyUp);
			$("#button_search").bind("tap",$.navsearch.toSearchPage);
			
			$(document.body).bind("ontouchstart" in window ? "touchstart" : "mousedown", $.navsearch.onDocClick)
		},
		showSearch:function(){
			$("#searchPart").addClass("search-show");
			$("#mainCover").css("display","");
			setTimeout(function(){$("#menu_search").focus();},300)
		},
		closeSearch:function(){
			$("#menu_search").val("");
			$.navsearch.hideList();
			$("#mainCover").css("display","none");
			$("#searchPart").removeClass("search-show");
		},
		mouseWheel:function(e){
			e = e.target;
			var re = true;
			while(e){
				if(e.nodeName=="BODY"){
					break;
				}
				var eid = $.attr(e, "id");
				if(eid=="menu_search_ct"){
					re = false;
					break;
				}
				e = e.parentNode;
			}
			if(re){
				$.navsearch.hideList();
			}
		},
		toSearchPage:function(){
			var searchStr = $("#menu_search").val();
			if(searchStr.length < 2){
				$.MessageBox.alert("请输入搜索关键字，至少两个字符");
				return ;
			}
			$.navsearch.closeSearch();
			openNav('搜索结果', 'ngboss.frame.pc.common.SearchFrame','searchAll','&keyWords='+searchStr);
			return false;
		},
		showList: function(){
			if(!activeSearch) return ;
			$("#" + activeSearch + "_ct").addClass("c_float-show");
			$("#floatScroll").css("height","100%");
			var h = $("#" + activeSearch + "_ct").height();
			h = $.format.px2rem(parseFloat(h));
			if(h>4.5) h = 4.5;
			$("#floatScroll").css("height",h+"rem");
			floatScroll.refresh();
			floatScroll.scrollToElement("#" + activeSearch + "_list li:nth-child(1)",0);
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

				$("#" + activeSearch + "_list li[class*=on]").removeClass("on");
				$("#" + activeSearch + "_list li:nth-child(" +(idx+1)+ ")").addClass("on");
				if(idx>3){
					floatScroll.scrollToElement("#" + activeSearch + "_list li:nth-child(" +(idx-2)+ ")",1000);
				}else{
					floatScroll.scrollToElement("#" + activeSearch + "_list li:nth-child(1)",1000);
				}
				return false;
			}		
			
			//字母数字和空格、退格
			if(e.keyCode==32 || e.keyCode==8
			    || (e.keyCode>64 && e.keyCode<91)
			    || (e.keyCode>47 && e.keyCode<58)
			    || (e.keyCode>95 && e.keyCode<106)){
			
				var txt = this.value;
				txt = $.trim(txt.replace(/[\\'\{\}\[\]]/g,"")); //过滤特殊字符 xiedx
				
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
			$.navsearch.closeSearch();
			return true;
		},
		onMenuItemTap: function(el, menuId, modeCode, modeName,title,idx){
			if(!el || !el.nodeType) return;
			currentListIdx[activeSearch] = idx;
			$("li[class*=on]:first", el.parentNode).removeClass("on");
			$(el).addClass("on");
			$.navsearch.closeSearch();
			//打开菜单或添加到快捷菜单
			if($ && $.fastmenu && $.fastmenu.isEditMode()){
				$.fastmenu.actions.addMenu(menuId, title, modeCode, modeName, [e.pageX, e.pageY]);
			}else if($ && $.menuopener){
				$.menuopener.openMenu(menuId, title, modeName);
			}
			
			//更新点击次数
			if($.hotmenu){
				$.hotmenu.actions.updateMenu(menuId);
			}
		},
		onProductItemTap: function(el,offerId,offerType,idx){
			//点击搜索结果中商品的操作
			if(!el || !el.nodeType) return;
			currentListIdx[activeSearch] = idx;
			var k = $("div .title", el).text();
			
			$("li[class*=on]:first", el.parentNode).removeClass("on");
			$(el).addClass("on");
			$.navsearch.closeSearch();
			$.navsearch.openOfferPage(offerId, offerType,k);
		},
		openOfferPage:function(offerId, offerType,offerName){
			
			if($.menuopener){
				var url,title;
				if(offerType == '10' || offerType == '11' || offerType == '37'){
					url = $.redirect.buildUrl("/ordercentre/ordercentre", "oc.enterprise.cs.OperGroupUser", "initial", "");
					title = "商品业务受理";
				}else if(offerType == '12' || offerType == '13'){
					url = $.redirect.buildUrl("/ordercentre/ordercentre", "oc.enterprise.cs.OperGroupMember", null, "");
					title = "成员业务受理";
				}else if(offerType == '14'){
					url = $.redirect.buildUrl("/ordercentre/ordercentre", "oc.enterprise.cs.EcCampnOper", "initPage", '&TYPE=0');
					title = '集团营销受理';
				}else if(offerType == '20'){
					url = $.redirect.buildUrl("/ordercentre/ordercentre", "oc.enterprise.cs.OpenGroupMember", "initPage", "");
					title = "集团成员开户";
				}
				if(url) {
					$.menuopener.openMenu("", title, url,offerId);
					return ;
				}else{
					title = "商品详情";
					if(offerName) title = title+"-"+offerName
					$.ajax.get("ngboss.frame.pc.common.WelcomeNew", "fetchProductPage", "&offerId="+offerId+"&offerType="+offerType, null,function(data){
						var p = data.get("DETAIL_PAGE");
						if(p && p!=""){
							url = $.redirect.buildUrl("/ordercentre/ordercentre", p, "init", "&OFFER_ID="+offerId+"&PARENT_OFFER_ID="+offerId);
							$.menuopener.openMenu("", title, url,offerId);
						}else{
							alert("商品页面不存在");
						}
					},function(code, info){
						alert('获取商品信息时发生错误：\n' + code + ':' + info);
					});
				}
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
		$.navsearch.init();
	});	
})();
