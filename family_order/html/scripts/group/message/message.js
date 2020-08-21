//为了与CSSubmit提交组件弹出框样式相同而增加的js

//提示信息
function showMessage(result, title, content, isPrint) {  
	if (!result) {
		result = "error";
	}
	if(!isPrint){
		isPrint = false;
	}
	var judgeTrade = ("error" == result); 
	var msgPanel = $("#SUBMIT_MSG_PANEL");
	if (!msgPanel.length) {
		var msgArr = [];
		msgArr.push('<div id="SUBMIT_MSG_PANEL" class="c_popup" style="z-index:88;">	');
		msgArr.push('<div class="c_popupWrapper">	');
		msgArr.push('<div class="c_popupHeight"></div>	');
		msgArr.push('<div class="c_popupBox">	');
		msgArr.push('<div class="c_popupTitle">	');
		msgArr.push('<div class="text">'	+ (judgeTrade ? "错误提示" : "成功提示")	+ 	'</div>	');
		msgArr.push('</div>	');
		msgArr.push('<div class="c_popupContent"><div class="c_popupContentWrapper">	');
		msgArr.push('<div class="c_msg c_msg-popup'+ (judgeTrade? "": " c_msg-success")+ '">	');
		msgArr.push('	<div id="SUBMIT_MSG_TITLE" class="title"></div>	');
		msgArr.push('	<div id="SUBMIT_MSG_CONTENT" class="content"></div>	');
		msgArr.push('</div>	');
		msgArr.push('<div id="SUBMIT_MSG_BTN" class="c_submit">	');
		msgArr.push('<button class="'+	(judgeTrade ? "e_button-page-cancel" : "e_button-page-ok")	+'" onclick="javascript:closeMessage('+(judgeTrade ? "" : "true")+');">	');
		msgArr.push('<i></i><span>'	+(judgeTrade? "关闭" : "确定")+	'</span></button>	 ');

		msgArr.push('</div>	');
		msgArr.push('</div></div>	');
		msgArr.push('<div class="c_popupBottom"><div></div></div>	');
		msgArr.push('<div class="c_popupShadow"></div>	');
		msgArr.push('</div>	');
		msgArr.push('</div>	');
		msgArr.push('<iframe class="c_popupFrame"></iframe>	');
		msgArr.push('<div class="c_popupCover"></div>	');
		msgArr.push('</div>	');
		
		$(document.body).append(msgArr.join(""));
		msgPanel = $("#SUBMIT_MSG_PANEL");
		msgArr=null;
	}
	if (msgPanel.length) {
		var t = $("#SUBMIT_MSG_TITLE");
		if (t.length) {
			t.html((title ? title : ""));
		}
		var e = $("#SUBMIT_MSG_CONTENT");
		if (e.length) {
			e.html((content ? ("" + content).replace(/\n/ig, "<br />") : ""));
		}
		msgPanel.css("display", "");
	}
	msgPanel = null;
}

//业务弹出框按钮事件
function closeMessage(succFlag) {
	if(!succFlag){
		return;
	}	
	
	var affirmAction = $("#CSSUBMIT_BUTTON").attr("affirmAction");
	//如果有确认动作，则执行确认事件，否则刷新
	if(affirmAction && affirmAction != ""){
		(new Function("return " + affirmAction + ";"))();
	}else{
		var href = window.location.href;
		if(href){
			if(href.lastIndexOf("#nogo") == href.length-5){
				href = href.substring(0, href.length-5);
			}
			window.location.href = href;
		}					
	}
}

function showSuccess(data){    
    var title = "业务受理成功";
	var content = "点【确定】继续业务受理。";
	var tradeData;
	if(data instanceof $.DatasetList){
		tradeData = data.get(0);
	}else if(data instanceof $.DataMap){
		tradeData = data;
	}	
	if(tradeData && tradeData.containsKey("ORDER_ID")){
		content = "客户订单标识：" + tradeData.get("ORDER_ID") + "\n点【确定】继续业务受理。";
	} 
    showMessage("success",title, content, false);   
}