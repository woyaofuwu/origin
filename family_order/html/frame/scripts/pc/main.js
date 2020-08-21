
var activeFnTag = "cust";
var activeFnIdx = 0;
var fnParts = {cust: "LoginPart", group: "m_group", favo: "m_favo", notice: "m_notice", ambu: null, wiki:null,like: null, menu: "m_menu"};
$.sidebar = {
	init : function(){
		$("#m_fn_ul li").each(function(idx){
			$.attr(this, "idx", idx);
		});
		$("#m_fn_ul").tap($.sidebar.events.onFnTapClick);
		$("#m_home").tap(function(){
			/*var partId = fnParts[activeFnTag];
			if(partId){
				$("#m_fn_ul li[idx=" + activeFnIdx + "]").removeClass("on");
				$("#" + partId).css("display", "none");
				document.documentElement.className = "s_noside";
				activeFnIdx = -1;
			}*/
			$.navtabset.switchToDefNav();
		});
		var partId = fnParts[activeFnTag]
		$("#m_fn_ul li[idx=" + activeFnIdx + "]").addClass("on");
		$("#" + partId).css("display", "");
		$(document.documentElement).removeClass("s_noside");
		$(document.body).bind("mousedown",function(e){
			e = e.target;
			while(e){
				if(e.nodeName=="BODY"){
					break;
				}
				var eid = $.attr(e, "id");
				if(eid=="menuContextMenu"){
					return true;
				}
				e = e.parentNode;
			}
			if($("#menuContextMenu").css("display")!="none"){
				$("#menuContextMenu").css("display","none");
				$("#mainCover").css("display","none");
			}
			return true;
		});
		
	},
	checkStaffPwd:function(){
		setTimeout(function(){
			$.ajax.submit(null,"checkStaffPwd", null, null, function(data){
				if(data && data.get("REMINDER_TAG")=="1"){
					var info = data.get("RESULT_MSG");
					$.MessageBox.confirm("提示","距密码过期还有"+info+"天，请及时修改密码！",function(re){
						if(re=="ok"){
							$.menuopener.openMenu("", "员工密码自助重置", "/commonbusinesscentre/commonbusinesscentre?service=page/secframe.bs.securitymgt.SafetyQuestionMgt&listener=init");
						}
					});
				}
			}, function(error_code,error_info,detail){
				$.MessageBox.error(error_info);
			});
		},2000);
	},
	navAction:function(){
		$('#helpNavPerson').css('display','none');
		$('#helpNavGroup').css('display','none');
		$.ajax.submit(null,"tipAction", "&operType=0", null, null, null);
	},
	msgAction:function(){
		$('#UI-release').css('display','none');
		if($('#msgnoshow').attr('checked')){
			$.ajax.submit(null,"tipAction", "&operType=1", null, null, null);
		}
	},
	tipAction:function(content){
		$('#searchTipContent').text(content);
		$('#searchTip').css('display','');
		setTimeout(function(){
			$('#searchTip').css('display','none');
		},5000);
	},
	fetchFee:function(t){
		
		setTimeout(function(){
			$.ajax.submit(null,"fetchFee", null, null, function(data){
				$("#monFee").text(data.get("MONTH_TOTAL"));
				$("#dayFee").text(data.get("DAY_TOTAL"));
			}, function(error_code,error_info,detail){
				//$.MessageBox.error("获取营业员收费信息", error_info,null,null,detail);
			});
		
		},t);
	},
	//删除收藏菜单
	deleteCollect:function(e,favId){
		
		ajaxSubmit(null,'deleteCollect','&favId='+favId,null,
		function(){
			$(e).parent().remove();
		},
		function(error_code,error_info,detail){
			MessageBox.error('错误信息',error_info);
		});
	},
	freshFavMenu:function(){
		ajaxSubmit(null,'getCollectMenu',null,'favo_pages',function(){
			var idx = $("#favo_tabs li[class=on]").attr("idx");
			$($("#favo_pages").children()[0]).css("display","none");
			$($("#favo_pages").children()[idx]).css("display","");
		},function(error_code,error_info,detail){
			$.MessageBox.error("获取数据失败", error_info,null,null,detail);
		});
    },
	events : {
		onFnTapClick : function(e){
			var el = e.target;
			
			if(!el || !el.nodeType || !$.nodeName(el, "li"))
				return;
			
			var tag = $.attr(el, "tag");
			var idx = $.attr(el, "idx");
			var partId = fnParts[activeFnTag];
			if(idx == activeFnIdx) {
				$("#m_fn_ul li[idx=" + activeFnIdx + "]").removeClass("on");
				$("#" + partId).css("display", "none");
				$(document.documentElement).addClass("s_noside");
				activeFnIdx = -1;
				return;
			}
			
			if(tag == "like"){
				openNav("给设计师点赞", "bs.config.likeAPerson", "init", "", "/resourcecentre/resourcecentre");
			}
			if(tag == "wiki"){
				window.open("http://10.173.226.172:8081/csp/c_kbs/oAoutLink.action?staffId=ZKF11353","newwindow","height=600,width=800,top=0,left=0");
			}
			
			if(tag == "notice"){
				setTimeout(function(){
					if(window.noticeUnreadScroller){
						window.noticeUnreadScroller.refresh();
					}
					if(window.noticeReadedScroller){
						window.noticeReadedScroller.refresh();
					}
				}, 100);
			}
			
			partId = fnParts[tag];
			if(partId){
				$("#" + fnParts[activeFnTag]).css("display", "none");
				$("#" + partId).css("display", "");
				$(document.documentElement).removeClass("s_noside");
				if(partId=="m_menu") m_menu.refresh();
				$("#m_fn_ul li[idx=" + activeFnIdx + "]").removeClass("on");
				$("#m_fn_ul li[idx=" + idx + "]").addClass("on");
				
				activeFnTag = tag;
				activeFnIdx = idx;
			}else{
				//document.documentElement.className = "s_noside";
			}
		},
		showFn:function(tag){
			var curE = $("#m_fn_ul li[idx=" + activeFnIdx + "]");
			if(curE && curE.attr("tag")==tag){
				return ;
			}
			var partId = fnParts[tag];
			if(curE && partId){
				curE.removeClass("on");
				$("#" + fnParts[activeFnTag]).css("display", "none");			
			}
			if(partId){
				$("#m_fn_ul li[tag=" + tag + "]").addClass("on");
				$("#" + partId).css("display", "");
				$(document.documentElement).removeClass("s_noside");
				
				activeFnTag = tag;
				activeFnIdx = $("#m_fn_ul li[tag=" + tag + "]").attr("idx");
			}
		},
		hideFn:function(){
			var curE = $("#m_fn_ul li[idx=" + activeFnIdx + "]");
			if(curE){
				curE.removeClass("on");
				$("#" + fnParts[curE.attr("tag")]).css("display", "none");			
			}
			$(document.documentElement).addClass("s_noside");
			activeFnIdx=-1;
		}
	}
};

//退出按钮事件
/*
var doUnloadLogout=true;
function logout(){
	 //最后使用的subsys菜单
	 $.ajax.get("Home", "logout", null, null, function(){	
		if(frame_isdebug){	 //开发模式 跳转
		 	$.redirect.toPage("Home","","");
		}else{    //否则关闭
			//alert("感谢您的使用！");
		 	try{
		 		window.doUnloadLogout = false;
				parent.window.opener = null;
				parent.window.open("", "_self");
				parent.window.close();
			}catch(ex){
				window.doUnloadLogout = true;
			}	
		}
	 },function(){
	 		try{
	 			window.doUnloadLogout = false;
				parent.window.opener = null;
				parent.window.open("", "_self");
				parent.window.close();
			}catch(ex){
				window.doUnloadLogout = true;
			}
	 },{async:false});

}
*/

$.forceLogout = {
	doit: function(content){
		MessageBox.show({
			error : true,
			msg : "您已经被强制下线",
			content : (content ? content : "您已经被强制下线,请与系统管理人员联系"),
			buttons : {"cancel" : "关闭"},
			fn : function(btn){
				
			},
			beforeHide : function(btn){
				$.ajax.get("Home", "logout", null, null, function(){
					$.redirect.toPage("Home");
				},function(code, msg){
					$.redirect.toPage("Home");
				},{async:false});
			}
		});
	}
};

function logout(){
	$.beginLoading("正在注销...");
	$.ajax.get("Home", "logout", null, null, function(){
		$.endLoading();
		$.redirect.toPage("Home");
	},function(code, msg, detail){
		$.endLoading();
		$.redirect.toPage("Home");
	});
}

function changeCallBack(data){
	$("#city_btn").text(data.get("eparchyName"));
	$("#staff_city").text(data.get("eparchyName"));
}

function changeError(error_code,error_info,detail){
	$.MessageBox.error("切换登录地州失败", error_info,null,null,detail);
}

$(function(){
	
	/*
	if(!frame_isdebug){
		$(window).bind("beforeunload",function(e){
			//return; //开发时return
			if(doUnloadLogout){
				return "将注销当前用户并退出系统";
			}
			return;
		});
		
		$(window).unload(function(e){
			if(doUnloadLogout){
				//使用同步的ajax来注销登录
				logout(); //开发时注销该行
			}
		});

	}
	*/
	
	$.sidebar.init();
	$("#staff_min").bind("click",function(){
		$("#staff_max").css("display", "");
		$("#city_btn").removeClass("city-unfold");
		$("#city_choose").css("display", "none");
		$.sidebar.fetchFee(0);
	});
	
	$("#staff_max").bind("mouseleave",function(){
		$("#staff_max").css("display", "none");
	});
	
	$("#more_btn").bind("click",function(){
		$("#staff_max").css("display", "none");
		$('#menu_btn').trigger('tap');
	});
	$("#favo_tabs li").each(function(idx){
		$.attr(this, "idx", idx);
	});
	$("#favo_tabs li").bind("tap",function(e){
		if($(e.target).hasClass("on")){
			return ;
		}
		var oldIdx = $("#favo_tabs li[class=on]").attr("idx");
		$("#favo_tabs li[class=on]").removeClass("on");
		$(e.target).addClass("on");
		var idx = $(e.target).attr("idx");
		$($("#favo_pages").children()[oldIdx]).css("display","none");
		$($("#favo_pages").children()[idx]).css("display","");
	});
	
	if($("#isProv").val()=='1'){//全省工号
		$("#city_btn").bind("click",function(){
			var show = $("#city_choose").css("display");
			if(show=="none"){
				$("#city_btn").addClass("city-unfold");
				$("#city_choose").css("display", "");
			}else{
				$("#city_btn").removeClass("city-unfold");
				$("#city_choose").css("display", "none");
			}
		});
		
		$("#city_choose li").bind("click",function(e){
			
			$("#city_btn").removeClass("city-unfold");
			$("#city_choose").css("display", "none");
			var eparchyCode = $(e.target).attr("id");
			var eparchyName = $(e.target).text();
			if(eparchyCode && eparchyName){
				$.ajax.submit(null,"changeLoginEparchy", "eparchyCode="+eparchyCode+"&eparchyName="+eparchyName, null, changeCallBack, changeError);
			}
		});
	}
    //导入导出日志查询
     $("#my_impexp").bind("tap",function(e){
    	 openNav("我的导入导出", "secframe.op.impexp.ImpExp", "init", null,"/commonbusinesscentre/commonbusinesscentre");	 
     });
    //分布式缓存刷新
     $("#memCacheFlush").bind("tap",function(e){
    	 openNav("静态参数缓存", "secframe.op.flushmemcache.FlushMemCache", null,null,"/commonbusinesscentre/commonbusinesscentre");	 
     });
     
    //权限验证
     $("#privCheck").bind("tap",function(e){
    	 openNav("权限验证", "secframe.op.privcheck.PrivCheck", "init",null,"/commonbusinesscentre/commonbusinesscentre");	 	 
     });
     
     //权限刷新
     $("#privReload").bind("tap",function(e){
    	 openNav("权限刷新", "secframe.op.privcheck.PrivLoad", "", null,"/commonbusinesscentre/commonbusinesscentre");	 	 
     });
     
   //异常编码维护
     $("#errCode").bind("tap",function(e){
    	 openNav("异常编码维护", "secframe.op.errorcode.ErrorCode", null,null,"/commonbusinesscentre/commonbusinesscentre");	 	 
     });
   //图标选择
     $("#iconChoose").bind("tap",function(e){
    	 openNav("图标选择", "secframe.op.iconchoose.IconChoose", 'init','&centrecode=CommonBusinessCentre',"/commonbusinesscentre/commonbusinesscentre");	 	 
    });
   //会话管理
     $("#monitorses").bind("tap",function(e){
    	 openNav("会话管理", "secframe.op.monitorses.MonitorSes", null,'&centrecode=CommonBusinessCentre',"/commonbusinesscentre/commonbusinesscentre");	 	 
    });
   //会话监控
     $("#monitormap").bind("tap",function(e){
    	 openNav("会话监控", "secframe.op.monitorses.MonitorMap", 'init','&centrecode=CommonBusinessCentre',"/commonbusinesscentre/commonbusinesscentre");	 	 
    });
   //截屏
    $("#screenShots").bind("tap",function(){
   	 	try{
			var ocx=new ActiveXObject("ScreenCapture.CNGSC");
			var path = ocx.GetScreenImage();
			if (path != null && path != '') {
				alert('截图文件位置:'+path);
			}
		} catch(e){
			alert('未找到截图控件，请下载完成后安装，截图控件仅支持IE11(32bit)');
			window.open('/static/printScreen.exe','_new_download_win','height=40,width=100,top=0,left=0,toolbar=no,menubar=no,scrollbars=no, resizable=no,location=no, status=no');
		}
    });
     //收藏菜单显示
    $("#favoFn").bind("click",$.sidebar.freshFavMenu);
     
	$("#m_menu_log").bind("tap",function(){
		window.open('log.jsp','_new_log_win','height=600,width=800,top=0,left=0,toolbar=no,menubar=no,scrollbars=no, resizable=no,location=no, status=no');
	});
	
	if($("#isManager").val()=="1"){
		$("#groupFn").trigger("tap");
	}
	$.sidebar.checkStaffPwd();
	//$.sidebar.fetchFee(5000);
	
	if($("#navShow").val()!="1"){
		if($("#isManager").val()=="1"){
			$('#helpNavGroup').css('display','');
		}else{
			$('#helpNavPerson').css('display','');
		}
	}
	
	if($("#msgShow").val()!="1"){
		$('#UI-release').css('display','');
	}
});

function hideDetail()
{
	var detailId = "";
	var targetId = "";
	if($("#groupDetail").hasClass("more-show"))
	{
		detailId = "groupDetail";
		targetId = "groupCust";
	}
	else
	{
		return;
	}
	var layer = document.getElementById(detailId);
	var button = document.getElementById(targetId);
	var isIn = (function () {
		var tempDom = event.target || event.srcElement;
		while(tempDom != document.body && tempDom != layer && tempDom != button) {
			tempDom = tempDom.parentNode;
		}
		if(tempDom != document.body) {
			return true;
		} else {
			return false;
		}
	})();
	// alert(isIn);
	if(!isIn)
	{
		$("#"+detailId).removeClass('more-show');
		$("#detailCover").css("display", "none");
	}
}