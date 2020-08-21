/*!
 * 消息盒子
 * auth:xiedx@asiainfo.com
 * date:2016/4/22
 */
 (function($, window, doc){
 
 	var MSG_TYPE = {
		"001": {
			type: "消息",
			cls: "e_ico-speak",
			action: function(data){
				openNav('查看消息-' + data.TOPIC ,'secframe.bs.infomgt.MessageView', 'queryMessage', 'INFO_ID=' + data.INFO_ID, '/commonbusinesscentre/commonbusinesscentre');
			}
		},
		"002": {
			type: "公告",
			cls: "e_ico-notice",
			action: function(data){
				openNav('查看公告-' + data.TOPIC ,'secframe.bs.infomgt.BulletinView', 'queryBulletin', 'INFO_ID=' + data.INFO_ID, '/commonbusinesscentre/commonbusinesscentre');
			}
		},
		"003": {
			type: "提醒",
			cls: "e_ico-alarm",
			action: function(data){
			}
		},
		"004": {
			type: "导入",
			cls: "e_ico-import",
			action: function(data){
				
			}
		},
		"005": {
			type: "导出",
			cls: "e_ico-export",
			action: function(data){
			}
		},
		"006": {
			type: "下载",
			cls: "e_ico-download",
			action: function(data){
			}
		}
	};
	
	var MSG_IDX = 0;
	var MSG_NUM = 0;
	var MSG_COLLECTION = {};
	
	$.FrameMsgBox = {
		handle: function(data){
			if(!data || !$.isObject(data) || !data.TYPE) 
				return;
				
			//051 强制退出
			if("051" == data.TYPE){
				if($.forceLogout){
					$.forceLogout.doit( ( data.CONTENT ? data.CONTENT : null ) );
				}
			}else{
				this.add(data);
			}
		},
		add: function(data){
			//console.log($.stringifyJSON(data));
			var type = MSG_TYPE[data.TYPE];
			var idx = MSG_IDX ++;

			$("#noticeUlUnread").prepend("<li idx=\"" + idx + "\" class=\"link\">" +
											"<div class=\"ico\"><span class=\"" + type.cls + "\"></span></div>" +
											"<div class=\"main\">" +
												"<div class=\"title title-auto\">" + data.TOPIC + "</div>" +
												"<span class=\"content\">" + (data.DATE ? data.DATE : (new Date()).format("yyyy-MM-dd hh:mm:ss")) + "</span>" +
											"</div>" +
										 "</li>");
								
			MSG_COLLECTION[idx] = data;
			MSG_NUM ++;
			
			var mini = $("#msg_mini");
			if("none" == mini.css("display")){
				mini.css("display", "");
			}
			mini.text(MSG_NUM);
			mini = null;
			
		},
		setReaded: function(idx){
			var li = $("#noticeUlUnread li[idx=" + idx + "]");
			if(li.length){
				$("#noticeUlReaded").prepend("<li idx=\"" + idx + "\" class=\"link\">" +
												li.html() + 
											 "</li>");
			}
			
			li.remove();
			li = null;
		}
	};
	
	
	function handleClick(el){
		if(!el || !el.nodeType)
			return;
			
		var n = 0, found = false;
		while(n < 3 && el.nodeType){
			if( $.nodeName(el, "li") ){
				found = true;
				break;
			}
			el = el.parentNode;
			n ++;
		}
		
		if(found){
			var idx = $.attr(el, "idx");
			var data = MSG_COLLECTION[idx];

			if(data.ACTION && $.isString(data.ACTION)){
				(new Function(data.ACTION))();
			}else{
				MSG_TYPE[data.TYPE].action.call(window, data);
			}
			
			if(!data.visited){
				data.visited = true;
				
				if(MSG_NUM >0) MSG_NUM --;	
				
				var mini = $("#msg_mini");
				if(MSG_NUM <= 0 && "none" != mini.css("display")){
					mini.css("display", "none");
				}
				mini.text(MSG_NUM);
				mini = null;

				setTimeout("$.FrameMsgBox.setReaded(" + idx + ")", 10);
			}
			
		}
	}
	
	$(function(){
	
		$("#noticeTabset").afterSwitchAction(function(e, idx){
			if("0" == idx){
				//刷新滚动条
				if(window.noticeUnreadScroller){
					window.noticeUnreadScroller.refresh();
				}
			}else if("1" == idx){
				//刷新滚动条
				if(window.noticeReadedScroller){
					window.noticeReadedScroller.refresh();
				}
			}
		});
		
		
		//由于和scroll组件冲突，必须用tap事件
		$("#noticeUlUnread").tap(function(e){
			if(e.originalEvent)
				e = $.event.fix(e.originalEvent);
			handleClick(e.target);
		});
		
		$("#noticeUlReaded").tap(function(e){
			if(e.originalEvent)
				e = $.event.fix(e.originalEvent);
				
			handleClick(e.target);
			
		});
		
		/*
		setTimeout(function(){
			$.httphandler.get("com.asiainfo.veris.framework.web.frame.handler.MessageHandler", "loadMessage", null, function(json){
				if(json){
					var ctx = json.context;
					if(ctx && "0" == ctx.x_resultcode){
						var data = json.data;
						if(data && $.isArray(data)){
							for(var i = 0, size = data.length; i < size; i++){
								$.FrameMsgBox.add( data[i] );
							}
						}
					}
				}
			}, function(code, info){
			},{
				dataType: "json",
				simple: true
			});
		}, 100);
		*/
	});
 
 })(Wade, window, document);