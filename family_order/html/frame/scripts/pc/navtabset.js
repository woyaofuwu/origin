(function($, window, doc){
	if(!$.navtabset){
		var max_tab_num = 8; //最大打开标签页数目，欢迎页占的页不算
		
		var defNavFrame = "navframe_def";
		var activeNavTabIdx, contextMenuNavTabIdx;
		var special_cls = ['home', 'home on'];
		var special_idx = [];
		
		$.navtabset = {
			idx: [],
			items: {},
			extend_data: {},
			findItemByIdx: function(idx){
				if(idx && /^\d+$/.test(idx)){
					for(var title in $.navtabset.items){
						if($.navtabset.items[title].idx == idx){
							return $.navtabset.items[title];
						}
					}
				}
			},
			getCurrentItem: function(){
				if(activeNavTabIdx){
					return $.navtabset.findItemByIdx(activeNavTabIdx);
				}
			},
			add: function(title, url, cls, data){
				//判断限制
				if($.navtabset.idx.length >= max_tab_num){
					alert("打开界面数目已经达到上限" + max_tab_num + "个");
					return;
				}
				if((!title || !$.isString(title))){
					alert("标题不能为空！");
					return;
				}
				if(!$.isString(url)){
					alert("URL必须是String类型！");
					return;
				}
				
				//判断是否已经有已存在的界面
				var item = $.navtabset.items[title];
				if(item){
					var e_idx = item.idx;
					var e_url = item.url;
					if(e_idx){
						if($.navtabset.switchTo(e_idx, true) == false){
							return;
						}
						//$.navtabset.frameMgr.switchTo(e_idx);
						if(url != e_url){
							$.navtabset.reload(e_idx, url);
							item.url = url;
						}
					}
					return;
				}
	
				var idx = ++$.guid;
				var sNav = false;
				if(data && data["menu_id"]){
					var m = data["menu_id"];
					if(m=='crm8108'||m=='crm8109'||m=='crm8093'||m=='crm8097'||m=='crm9130'||m=='BIL1101'||m=='crm9311'){
						sNav = true;;
					}
				}
				$("#tab_ct_ul").append(
				    '<li id="navtab_' + idx + '" idx="' + idx + '"' + ((cls && $.isString(cls)) ? ' class="' + cls + '" ' : '') +
				    	' title="' + title + '" >' +
						'<div class="text">' + title + '</div>' +
						'<div class="fn">' +
							(sNav ? ('<div id="navtab_showNav_' + idx + '" class="guide" title="导航"></div>'):'') +
							'<div id="navtab_addFavo_' + idx + '" class="addFavo" title="收藏"></div>' +
							'<div id="navtab_close_' + idx + '" class="close" title="关闭"></div>' +
						'</div>' +
					'</li>');
				$("#navtab_" + idx).bind("contextmenu",idx,$.navtabset.onContextMenu);
				$("#navtab_" + idx).bind("click", idx, $.navtabset.onSwitchClick);
				$("#navtab_" + idx).bind("dblclick", idx, $.navtabset.onCloseClick);
				$("#navtab_close_" + idx).bind("click", idx, $.navtabset.onCloseClick);
				$("#navtab_addFavo_" + idx).bind("click", idx, $.navtabset.onAddFavoClick);
				if(sNav){
					$("#navtab_showNav_" + idx).bind("click", idx, $.navtabset.callMenuNav);
				}
				
				/*
				if($.browser.msie && $.browser.version <= 6){
					$("#navtab_" + idx).bind("mouseenter", idx, $.navtabset.onMouseEnter_IE6);
					$("#navtab_" + idx).bind("mouseleave", idx, $.navtabset.onMouseLeave_IE6);
				}
				*/
				
				$.navtabset.idx.push(idx);
				$.navtabset.items[title] = {"idx": idx, "title": title, "url": url};
				if(data){ //扩展数据
					$.navtabset.extend_data[idx] = data;
				}
				
				var canSwitch = $.navtabset.switchTo(idx, false);
				if(canSwitch && "none" != $("#" + defNavFrame).css("display")){
					$("#" + defNavFrame).css("display", "none");
				}
				$.navtabset.frameMgr.add(idx, url, canSwitch);				
				
				//添加样式
				if($.navtabset.idx.length == 5){
					$("#m_task").addClass("task-full");
				}
				
				if(cls && special_cls.contains(cls)){
					special_idx.push(idx);
				}	
			},
			redirect: function(title, url, cls, data){
				if((!title || !$.isString(title))){
					alert("标题不能为空！");
					return;
				}
				if(!$.isString(url)){
					alert("URL必须是String类型！");
					return;
				}
				if(!activeNavTabIdx){
					return;
				}
				var item = $.navtabset.items[title]; //已经存在同样标题的界面
				if(item){
					var e_idx = item.idx;
					var e_url = item.url;
					if(e_idx){
						if($.navtabset.switchTo(e_idx, true) == false){
							return;
						}
						if(url!=e_url){
							$.navtabset.reload(e_idx, url);
							item.url = url;
						}
					}
					return;
				}
				
				
				//跳转当前页面
				item = $.navtabset.getCurrentItem();
				if(item){
				    //更新标题
				    $("#navtab_" + activeNavTabIdx).attr("title",title);
					$("#navtab_" + activeNavTabIdx + " div[class=text]").text(title);
	
					//重新加载
					$.navtabset.reload(activeNavTabIdx, url);
					
					var p_title = item.title; item = null;
					delete $.navtabset.items[p_title];
					$.navtabset.items[title] = {"idx": activeNavTabIdx, "title": title, "url": url};
				}
			},
			reload: function(idx, url){
				$.navtabset.frameMgr.reload(idx, url);
			},
			reloadCurrent: function(url){
				if(activeNavTabIdx){
					$.navtabset.reload(activeNavTabIdx, url);
				}
			},
			switchTo: function(idx, switchFrame){
				if(activeNavTabIdx){
					//先进行onUnActive事件处理
					var ct = $.navtabset.getCurrentContentWindow();
					if($.isSameDomain(ct)){ //如果在同一个域
						var fn = ct[$.nav.events.unactive];
						if(fn && $.isFunction(fn)
							&& false === fn()){ //如果有onUnActive方法，且执行结果为false，则不切换
							return false;
						}
					}
					$("#navtab_" + activeNavTabIdx).removeClass("on");
					$("#m_home").removeClass("on");
				}
				$("#navtab_" + idx).addClass("on");
				if(switchFrame){
					$.navtabset.frameMgr.switchTo(idx);
				}
				activeNavTabIdx = idx;
				
				if(switchFrame){
					if("none" != $("#" + defNavFrame).css("display")){
						$("#" + defNavFrame).css("display", "none");
					}
					//进行onActive事件处理
					var ct = $.navtabset.getCurrentContentWindow();
					if($.isSameDomain(ct)){ //如果在同一个域
						var fn = ct[$.nav.events.active];
						if(fn && $.isFunction(fn)){
							fn();
						}
					}
				}
				return true;
			},
			switchToByTitle: function(title){
				if($.navtabset.items[title]){
					var idx = $.navtabset.items[title].idx;
					return $.navtabset.switchTo(idx, true);
				}	
			},
			switchToDefNav: function(){
				if(activeNavTabIdx){
					//先进行onUnActive事件处理
					var ct = $.navtabset.getCurrentContentWindow();
					if($.isSameDomain(ct)){ //如果在同一个域
						var fn = ct[$.nav.events.unactive];
						if(fn && $.isFunction(fn)
							&& false === fn()){ //如果有onUnActive方法，且执行结果为false，则不切换
							return false;
						}
					}
					$("#navtab_" + activeNavTabIdx).removeClass("on");
					activeNavTabIdx = null;
				}
				
				//隐藏前一个活动界面frame
				$.navtabset.frameMgr.hideActive();
				
				$("#m_home").addClass("on");
				$("#" + defNavFrame).css("display", "");
				$("#show_search").css("display", "none");
				
			},
			close: function(idx){
				//先进行onClose事件处理
				var ct = $.navtabset.getCurrentContentWindow();
				if($.isSameDomain(ct)){ //如果在同一个域
					var fn = ct[$.nav.events.close];
					if(fn && $.isFunction(fn)
						&& false === fn()){ //如果有onClose方法，且执行结果为false，则不关闭
						return;
					}
				}
				
				/*
				if($.browser.msie && $.browser.version<=6){
					$("#navtab_" + idx).unbind("mouseenter",$.navtabset.onMouseEnter_IE6);
					$("#navtab_" + idx).unbind("mouseleave",$.navtabset.onMouseLeave_IE6);
				}
				*/
				if(!idx && contextMenuNavTabIdx){
					idx = contextMenuNavTabIdx;
				}
				$("#navtab_close_" + idx).unbind("click", $.navtabset.onCloseClick);
				$("#navtab_" + idx).unbind("dblclick", $.navtabset.onCloseClick);
				$("#navtab_" + idx).unbind("click", $.navtabset.onSwitchClick);
				
				$("#navtab_" + idx).remove();
				$.navtabset.frameMgr.remove(idx);
				//删除数组数据
				for(var i = $.navtabset.idx.length - 1; i >= 0; i--){
					if($.navtabset.idx[i] == idx){
						$.navtabset.idx.splice(i,1);
					}
				}
				//删除标题数据
				for(var title in $.navtabset.items){
					if($.navtabset.items[title].idx == idx){
						delete $.navtabset.items[title];
					}
				}
				delete $.navtabset.extend_data[idx];
				
				//切换到最后一个标签页
				if($.navtabset.idx.length>0){
					var l_idx = $.navtabset.idx[$.navtabset.idx.length-1];	
				
					//alert($.navtabset.idx);
								
					$.navtabset.switchTo(l_idx,true);
					//$.navtabset.frameMgr.switchTo(l_idx);
				}else{
					activeNavTabIdx = null;
					$.navtabset.switchToDefNav();
				}
	
				//移除样式
				if($.navtabset.idx.length < 5){
					$("#m_task").removeClass("task-full");
				}
				$("#menuContextMenu").css("display","none");
			},
			closeByTitle: function(title){
				if($.navtabset.items[title]){
					var idx=$.navtabset.items[title].idx;
					$.navtabset.close(idx);
				}
			},
			
			closeCurrent: function(){
				if(activeNavTabIdx){
					$.navtabset.close(activeNavTabIdx);
				}
			},
			closeAll: function(){
				//全部删除
				for(var i = $.navtabset.idx.length - 1; i >= 0; i--){
					if(special_idx.contains($.navtabset.idx[i]))
						break;
					$.navtabset.close($.navtabset.idx[i]);
				}
				$("#menuContextMenu").css("display","none");	
			},
			closeOther: function(uncloseidx){
				uncloseidx = contextMenuNavTabIdx;
				for(var i = $.navtabset.idx.length - 1; i >= 0; i--){
					if(uncloseidx == $.navtabset.idx[i] 
						|| special_idx.contains($.navtabset.idx[i]))
						continue;
					$.navtabset.close($.navtabset.idx[i]);
				}
				$("#menuContextMenu").css("display","none");	
			},
			callMenuNav:function(e){
				$.navtabset.openMenuNavAction(e.data);
				e.stop();
			},
			onAddFavoClick:function(e){
				$.navtabset.addFavoAction(e.data);
				e.stop();
			},
			addFavo:function(){
				$.navtabset.addFavoAction(contextMenuNavTabIdx);
			},
			addFavoAction:function(index){
				var menuObj = $.navtabset.extend_data[index];
				$("#menuContextMenu").css("display","none");
				if(menuObj){
					var menuId = menuObj["menu_id"];
					var offerId = menuObj["offer_id"];
					var type = "0";
					if(offerId && offerId.length > 0){
						type = "1";
						menuId = offerId;
					}
					$.ajax.submit(null,"addFavoMenu", "&menuId="+menuId+"&type="+type, null, function(data){
						$.navtabset.showTip(1,"添加收藏成功！");
						$.sidebar.freshFavMenu();
					}, function(error_code,error_info,detail){
						$.navtabset.showTip(0,"添加收藏失败："+error_info);
					});
				}else{
					$.navtabset.showTip(0,"非功能菜单，不能收藏！");
				}
			},
			pageReload:function(){
				var item = $.navtabset.findItemByIdx(contextMenuNavTabIdx);
				if(item) $.navtabset.reload(contextMenuNavTabIdx, item.url);
			},
			showTip:function(tag,msg){
				$("#opTip").text(msg);
				if(tag){
					$("#opTip").attr("class","c_tip c_tip-float c_tip-green c_tip-float-show");
				}else{
					$("#opTip").attr("class","c_tip c_tip-float c_tip-red c_tip-float-show");
				}
				setTimeout(function(){
					$("#opTip").removeClass("c_tip-float-show");
				},2000);
			},
			openDeskNav:function(){
				if($("#isManager").val()=="1"){
					$.sidebar.events.showFn("group");
					$('#helpNavGroup').css('display','');
					$('#GR-step1').css('display','');
				}else{
					$.sidebar.events.showFn("cust");
					$('#helpNavPerson').css('display','');
					$('#UI-step1').css('display','');
				}
				$.navtabset.switchToDefNav();
			},
			openMenuNav:function(){
				$.navtabset.openMenuNavAction(contextMenuNavTabIdx);
			},
			openMenuNavAction:function(mIdx){
				if(mIdx!=activeNavTabIdx)return ;
				
				var menuObj = $.navtabset.extend_data[mIdx];
				var isnav = false;
				if(menuObj && menuObj["menu_id"]){
					var m = menuObj["menu_id"];
					if(m=='crm8108'||m=='crm8109'||m=='crm8093'||m=='crm8097'||m=='crm9130'||m=='BIL1101'||m=='crm9311'){
						isnav = true;;
					}
				}
				
				if(isnav){
					if(getNavContent().showNavHelp){
						getNavContent().showNavHelp();
					}
				}
			},
			getCurrentData: function(){
				if(activeNavTabIdx){
					return $.navtabset.extend_data[activeNavTabIdx];
				}
			},
			getDataByTitle: function(title){
				if($.navtabset.items[title]){
					var idx = $.navtabset.items[title].idx;
					return $.navtabset.extend_data[idx];
				}
			},
			getCurrentContentWindow: function(){
				if(activeNavTabIdx){
					return $.navtabset.getContentWindow(activeNavTabIdx);
				}
			},
			getContentWindow: function(idx){
				var fm = $("#navframe_" +idx);
				if(fm.length){
					return fm[0].contentWindow;
				}
			},
			getContentWindowByTitle: function(title){
				if($.navtabset.items[title]){
					var idx = $.navtabset.items[title].idx;
					return $.navtabset.getContentWindow(idx);
				}
			},
			onSwitchClick: function(e){
				if(e.data){
					$.navtabset.switchTo(e.data, true);
					//$.navtabset.frameMgr.switchTo(e.data);
					return false;
				}
			},
			hasOpendTab: function(){
				for(var i = $.navtabset.idx.length - 1; i >= 0; i--){
					if(!special_idx.contains($.navtabset.idx[i]))
						return true;
				}
				return false;
			},
			onCloseClick: function(e){
				if(e.data){
					$.navtabset.close(e.data);
					return false;
				}
			},
			onContextMenu: function(e){
				var idx = e.data;
				if(!special_idx.contains(idx)){
					contextMenuNavTabIdx = idx;
					var f = e.pageX,n=e.pageY;
					
					var menuObj = $.navtabset.extend_data[idx];
					var isnav = false;
					if(menuObj && menuObj["menu_id"]){
						var m = menuObj["menu_id"];
						if(m=='crm8108'||m=='crm8109'||m=='crm8093'||m=='crm8097'||m=='crm9130'||m=='BIL1101'||m=='crm9311'){
							isnav = true;;
						}
					}
					if(isnav){
						$("#contextOpenIndex").css("display","");
					}else{
						$("#contextOpenIndex").css("display","none");
					}
					var c = $("#menuContextMenu");
					c.css("left",f+"px");
					c.css("top",n+"px");
					c.css("display","");
					$("#mainCover").css("display","");
					//$.showContextMenu(e);
				}
				return false;
			}
			/*,
			onMouseEnter_IE6:function(e){
				$("#navtab_" + e.data).addClass("hover");
			},
			onMouseLeave_IE6:function(e){
				$("#navtab_" + e.data).removeClass("hover");
			}*/
		}
		
		var activeNavFrameIdx;
		$.navtabset.frameMgr = {
			add: function(idx, url, doSwitch){	
				$.insertHtml("beforeend", $("#main_ct")[0],
					'<iframe id="navframe_' + idx + '" name="navframeName_' + idx + '"  idx="' + idx + '" class="m_mainFrame" frameborder="0" src="" ' +
					' ></iframe>');
					
				$.insertHtml("beforeend", $("#main_ct")[0],
					'<div id="navframe_loading_' + idx + '" idx="' + idx + '" class="loading" style="display:none"><div class="wrapper">' +
				    '<div class="title">努力加载中</div><div class="subtitle">loading...</div></div></div>');
				
				/*
				if($.browser.msie && $.browser.version<=6){
					$.insertHtml("beforeend",$("#main_ct")[0],
						'<iframe id="navframe_cover_'+idx+'" name="navframe_coverName_'+idx+'" idx="'+idx+'" class="m_mainCover" style="display:none" src=""></iframe>');
				}			
				*/
					
				//bind event
				if($.browser.msie && window.attachEvent){
					$("#navframe_" + idx).bind("readystatechange", idx, $.navtabset.frameMgr.onIframeReadyStateChange_IE);
				}else{
					$("#navframe_" + idx).bind("load", idx, $.navtabset.frameMgr.onIframeLoad);
				}
				
				if(doSwitch){
					$.navtabset.frameMgr.switchTo(idx);
					$.navtabset.frameMgr.showLoading(idx);
				}
				$("#m_home").removeClass("on");
				$("#navframe_" + idx).attr("src", url);
				$("#show_search").css("display", "");
			},
			switchTo: function(idx){		
				//隐藏前一个活动界面frame
				$.navtabset.frameMgr.hideActive();
				
				//显示当前页面界面
				$("#navframe_" + idx).css("display","");
				$("#m_home").removeClass("on");
				$("#show_search").css("display", "");
				//如果当前界面没有加载完,显示当前界面的loading
				if(true !== $("#navframe_" + idx).data("loaded")){
					$("#navframe_loading_" + idx).css("display", "");
					/*
					if($.browser.msie && $.browser.version<=6){
						$("#navframe_cover_"+idx).css("display","");
					}
					*/
				}
				activeNavFrameIdx = idx;
			},
			remove: function(idx){
				//unbind event
				if($.browser.msie && window.attachEvent){
					$("#navframe_" + idx).unbind("readystatechange", $.navtabset.frameMgr.onIframeReadyStateChange_IE);
				}else{
					$("#navframe_" + idx).unbind("load", $.navtabset.frameMgr.onIframeLoad);
				}
				//remove el
				$("#navframe_" + idx).remove();
				$("#navframe_loading_" + idx).remove();
				
				/*
				if($.browser.msie && $.browser.version<=6){
					$("#navframe_cover_"+idx).remove();
				}
				*/
				
				//如果是IE，由于iframe remove后内存不释放，进行强制垃圾回收。
				if($.browser.msie){
					CollectGarbage();
				}
			},
			hideActive: function(){
				if(activeNavFrameIdx){
					//隐藏前一个页面界面
					$("#navframe_" + activeNavFrameIdx).css("display","none");
					//如果前一个页面没有加载完,隐藏前一个界面的loading
					if(true !== $("#navframe_" + activeNavFrameIdx).data("loaded")){
						$("#navframe_loading_" + activeNavFrameIdx).css("display", "none");
						/*
						if($.browser.msie && $.browser.version<=6){
							$("#navframe_cover_"+activeNavFrameIdx).css("display","none");
						}
						*/
					}
				}
			},
			reload: function(idx, url){
				$.navtabset.frameMgr.showLoading(idx);
				$("#navframe_" + idx).attr("src", url);
				$("#navframe_" + idx).data("loaded", false);
			},
			showLoading: function(idx){
				$("#navframe_loading_" + idx).css("display","");
				/*
				if($.browser.msie && $.browser.version<=6){
					$("#navframe_cover_"+idx).css("display","");
				}
				*/
			},
			showContent: function(idx){
				$("#navframe_loading_" + idx).css("display","none");
				/*
				if($.browser.msie && $.browser.version<=6){
					$("#navframe_cover_"+idx).css("display","none");
				}
				*/
			},
			onIframeLoad: function(e){
				var idx=e.data;
				if(idx){
					$.navtabset.frameMgr.showContent(idx);
					$("#navframe_" + idx).data("loaded", true);
				}
			},
			onIframeReadyStateChange_IE: function(e){
				if(this.readyState == "complete"){
					var idx = e.data;
					if(idx){
						$.navtabset.frameMgr.showContent(idx);
						$("#navframe_" + idx).data("loaded", true);
					}
				}
			}
		}
	}
	
	$(function(){	
		$("#m_task").bind("selectstart", function(e){
			e.stop();
			return false;
		});
		//$.navtabset.add('欢迎','?service=page/Welcome','home on');
	});

})(Wade, window, document);

