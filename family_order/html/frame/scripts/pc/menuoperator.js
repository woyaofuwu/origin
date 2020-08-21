/*!
 * 统一外框菜单层级加载js
 * auth:xiedx@asiainfo.com
 * date:2015/11/24
 */
(function(){
	var menuClazz = "com.asiainfo.veris.framework.web.frame.handler.MenuHandler";
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
			//$.beginPageLoading('加载中...');

			$.httphandler.get(menuClazz, "loadL1Menu", "&DOMAIN_ID=" + domainId, function(data){
				//$.endPageLoading();
				data = parseErrorData(data);
				if(data){
					if(data.indexOf("error:") == 0){
						alert("加载失败:"+data.replace(/error:/ig, ""))
					}else{
						$("#levelTag_1_"+domainId).after(data); //插入在tag后
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
		loadL2Menu: function(menuId,domainId){
			if(!menuId){
				alert("menuId为空");
				return;
			}
			//$.beginPageLoading('加载中...');
			$.httphandler.get(menuClazz, "loadL2Menu", "&PARENT_MENU_ID=" + menuId +"&DOMAIN_ID="+domainId, function(data){
				//$.endPageLoading();
				data = parseErrorData(data);
				if(data.indexOf("error:") == 0){
					alert("加载失败:"+data.replace(/error:/ig, ""))
				}else{
					$("#levelTag_2_"+domainId).after(data); //插入在loading后
				}
				if(window.fullmenuScroll){
					fullmenuScroll.refresh();
				}
			},null,{
				simple : true,
				dataType : "text"
			});
		},
		loadL3Menu: function(menuId,domainId){
			if(!menuId){
				alert("menuId为空");
				return;
			}
			//$.beginPageLoading('加载中...');
			
			$.httphandler.get(menuClazz,"loadL3Menu", "&PARENT_MENU_ID=" + menuId, function(data){
				//$.endPageLoading();
				data = parseErrorData(data);
				if(data.indexOf("error:") == 0){
					alert("加载失败:"+data.replace(/error:/ig, ""))
				}else{
					$("#levelTag_3_"+domainId).after(data); //插入在loading后
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
		showL2Menu: function(menuId,domainId){
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
					$.navmenu.loadL2Menu(menuId,domainId);
				}
				
				activeL2MenuId = menuId;
				$("#navMenu_L1_" + activeL1MenuId).find("li[menuId=" + activeL2MenuId + "]").attr("className", "link on");
			}
		},
		showL3Menu: function(menuId,domainId){
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
					$.navmenu.loadL3Menu(menuId,domainId);
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
			var domainId = $.attr(el, "domainId");
			$.navmenu.showL2Menu(menuId,domainId);
		},
		onL2MenuTap: function(el){
			var menuId = $.attr(el, "menuId");
			var domainId = $.attr(el,"domainId");
			$.navmenu.showL3Menu(menuId,domainId);
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
			$("#button_search").bind("tap",$.navsearch.toSearchPage);
			
			$(document.body).bind("ontouchstart" in window ? "touchstart" : "mousedown", $.navsearch.onDocClick)
			//$(document.body).bind("mousewheel", $.navsearch.mouseWheel);
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
			openNav('搜索结果', 'ngboss.frame.pc.common.SearchFrame','searchAll','&keyWords='+searchStr);
			return false;
		},
		hotSearch:function(searchStr){
			if(searchStr.length < 2){
				$.MessageBox.alert("搜索关键字至少两个字符");
				return ;
			}
			openNav('搜索结果', 'ngboss.frame.pc.common.SearchFrame','searchAll','&keyWords='+searchStr);
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
		floatResize:function(){
			var ct = $("#"+activeSearch+"_ct");
			var inputWidth = $("#menu_search").css("width");
			var btnWidth = $("#button_search").css("width");
			var tW = parseFloat(inputWidth)+parseFloat(btnWidth);

			if(window.ActiveXObject!== undefined ){
				tW = tW+34.15;
			}
			//ct.css("width",$.format.px2rem(tW)+"rem");
		},
		onClick:function (e){
			if(!e.target) return;
			activeSearch = $.attr(e.target, "id");
			//ct.css("left", ($.format.px2rem(offset.left)+0.125) + "rem");
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
		onProductItemTap: function(el, offerId, offerType, idx){
			//点击搜索结果中商品的操作
			if(!el || !el.nodeType) return;
			currentListIdx[activeSearch] = idx;
			var k = $("div .title", el).text();
			
			$("li[class*=on]:first", el.parentNode).removeClass("on");
			$(el).addClass("on");
			$.navsearch.hideList();
			$.navsearch.openOfferPage(offerId, offerType,k);
		},
		openOfferPage:function(offerId, offerType,offerName){
			
			if(parent.$ && parent.$.menuopener){
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
					parent.$.menuopener.openMenu("", title, url,offerId);
					return ;
				}else{
					title = "商品详情";
					if(offerName) title = title+"-"+offerName
					$.ajax.submit(null, "fetchProductPage", "&offerId="+offerId+"&offerType="+offerType, null,function(data){
						var p = data.get("DETAIL_PAGE");
						if(p && p!=""){
							url = $.redirect.buildUrl("/ordercentre/ordercentre", p, "init", "&OFFER_ID="+offerId+"&PARENT_OFFER_ID="+offerId);
							parent.$.menuopener.openMenu("", title, url,offerId);
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
	function welAfterSwitch(e, idx){
		if(idx==6){
			menus.tabScroller.refresh();
		}
	}
	function welSwitch(e, idx){
		var offerType,partId;
		var start,end;
		if(idx==1){
			//办活动
			$.ajax.submit(null, "fetchActions", null, "activities",function(data){
				//
			},function(code, info){
				alert('获取活动信息时发生错误：\n' + code + ':' + info);
			});
			return ;
			/*offerType = "1";
			partId = "activities";
			start = 0;
			end = 12;*/
		}else if(idx==2){
			//选套餐
			offerType = "2";
			partId = "combos";
			start = 0;
			end = 12;
		}else if(idx==3){
			//买手机
			offerType = "3";
			partId = "phones";
			start = 0;
			end = 20;
		}else if(idx==4){
			//开宽带
			offerType = "4";
			partId = "broadbands";
			start = 0;
			end = 12;
			
			$.ajax.submit(null, "fetchOffers", "&OFFER_TYPE=5&START="+start+"&END="+end, "broadbandSales",function(data){
				//
			},function(code, info){
				alert('获取商品信息时发生错误：\n' + code + ':' + info);
			});
		}else if(idx==5){
			//查资料
			parent.$.login.openCustCentre();
			return false;
		}
		
		if(offerType){
			$.ajax.submit(null, "fetchOffers", "&OFFER_TYPE="+offerType+"&START="+start+"&END="+end, partId,function(data){
				//
			},function(code, info){
				alert('获取商品信息时发生错误：\n' + code + ':' + info);
			});
		}
	}
	
	function menuSwitch(e, idx){
		var domainId = $("#content_"+idx).attr("domainId");
		$.navmenu.showL1Menu(domainId);
		if(e)e.stop();
		return true;
	}
	
	$(function(){
		$.navsearch.init();
		$("#welTab").afterSwitchAction(welAfterSwitch);
		$("#welTab").switchAction(welSwitch);
		$("#menus").switchAction(menuSwitch);
		menuSwitch(null,0);
		saleStatChange("D");
	});
	
})();

function openMenu(e,f,g){
	parent.$.menuopener.openMenu(e, f, g);
}

function saleStatChange(type){
	
	$.ajax.submit(null,"fetchSaleStatistic", "&type="+type, null, function(data){
		var total = data.get("CONTACT_CNT");
		var succ = data.get("SUCCEED_CNT");
		var fail = data.get("FAIL_CNT");
		if(total == '') total = '0';
		if(succ == '') succ = '0';
		if(fail == '') fail = '0';
		if(total==0){
			$("#saleEmote").attr("class","c_msg");
		}else{
			var rate = succ/total;
			if(rate>=0.9){
				$("#saleEmote").attr("class","c_msg c_msg-success");
			}else if(rate >= 0.7){
			 	$("#saleEmote").attr("class","c_msg c_msg-however");
			}else{
				$("#saleEmote").attr("class","c_msg c_msg-angry");
			}
		}
	
		$("#saleTotal").text(total);
		$("#saleSucc").text(succ);
		$("#saleFail").text(fail);
	}, function(error_code,error_info,detail){
		alert(error_info);
	});
}