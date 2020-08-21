var BaseInfoLib = {
	clazz:"com.asiainfo.veris.crm.order.web.frame.csview.common.component.person.BaseInfoFieldHandler",
	cacheWidget:{},
	setEvent:function(obj, eventName, func){
		obj.bind(eventName, func);
	},
	
	//是否展示隐藏信息编辑块
	toggleInfoPart:function(curEl, infoPart, label){
		var infoPart = $("#"+infoPart);
		if (infoPart.css("display") != "none"){
			infoPart.css("display", "none");
			curEl.find("i").removeClass().addClass("e_ico-unfold");
			curEl.find("span").html("填写"+label);
		}else {
			infoPart.css("display", "");
			curEl.find("i").removeClass().addClass("e_ico-fold");
			curEl.find("span").html("隐藏"+label);
		}				
	},
	
	//是否展示隐藏必填项
    toggleNeedInput: function (o, infoPart) {
        var $infoPart = $("#" + infoPart);
        if (!$infoPart.hasClass("e_hide-x")) {
            $infoPart.addClass("e_hide-x");
            $(o).find("span:eq(0)").removeClass("e_ico-hide").addClass("e_ico-show");
            $(o).find("span:eq(1)").html("显示非必填项");
        } else {
            $infoPart.removeClass("e_hide-x");
            $(o).find("span:eq(0)").removeClass("e_ico-show").addClass("e_ico-hide");
            $(o).find("span:eq(1)").html("隐藏非必填项");
        }
    },
	showWidget:function(pos){
		var len = $("#"+this.infoPart+" li").length;
		if(pos==0){
			$("#"+this.infoPart+" li:eq(0)").css("display", "");
		}else if(pos>0){
			pos = (pos>len)?len:pos;
			$("#"+this.infoPart+" li:eq("+(pos-1)+")").css("display", "");
		}else{
			pos = (len<Math.abs(pos))?-len:pos;
			$("#"+this.infoPart+" li:eq("+(len+pos)+")").css("display", "");
		}
	},
	hideWidget:function(pos){
		var len = $("#"+this.infoPart+" li").length;
		if(pos==0){
			$("#"+this.infoPart+" li:eq(0)").css("display", "none");
		}else if(pos>0){
			pos = (pos>len)?len:pos;
			$("#"+this.infoPart+" li:eq("+(pos-1)+")").css("display", "none");
		}else{
			pos = (len<Math.abs(pos))?-len:pos;
			$("#"+this.infoPart+" li:eq("+(len+pos)+")").css("display", "none");
		}
	},
	//增加表单控件
	addWidget:function(pos, partId){
		if(!this.cacheWidget[partId]){
			var dom = $("#"+partId);
			if(!dom || !dom.length) return;
			this.cacheWidget[partId] = dom.html();
			dom.remove();
		} 
		var htmlStr = this.cacheWidget[partId];
		var len = $("#"+this.infoPart+" li").length;
		if(pos==0 || pos==1){
			$("#"+this.infoPart+" li:eq(0)").before(htmlStr);
		}else if(pos>1){
			pos = (pos>len)?len+1:pos;
			$("#"+this.infoPart+" li:eq("+(pos-2)+")").after(htmlStr);
		}else{
			pos = (len<Math.abs(pos))? 0 : len+1+pos;
			if(pos == 0){
				$("#"+this.infoPart+" li:eq(0)").before(htmlStr);
			}else{
				$("#"+this.infoPart+" li:eq("+(pos-1)+")").after(htmlStr);
			}
		}
		htmlStr = null;
	},
	//删除表单控件
	delWidget:function(pos){
		var len = $("#"+this.infoPart+" li").length;;
		if(pos==0){
			$("#"+this.infoPart+" li:eq(0)").remove();
		}else if(pos>0){
			pos = (pos>=len)?len:pos;
			$("#"+this.infoPart+" li:eq("+(pos-1)+")").remove();
		}else{
			pos = (len<Math.abs(pos))? 0 : len+pos;
			$("#"+this.infoPart+" li:eq("+pos+")").remove();
		}
	},
	//加入一个表单控件，该方法不是马上生效，需要页面初始化时候执行
	pushWidget:function(pos, partId){
		this.widgets.push({
			"partId":partId, 
			"pos": pos,
			"state": 1
		});
	},
	//删除一个表单控件，该方法不是马上生效，需要页面初始化时候执行
	pullWidget:function(pos){
		this.widgets.push({
			"pos": pos,
			"state": -1
		});
	},
	//初始化组件内部表单控件
	initWidget:function(){
		var oThis = this;
		if(!this.widgets || this.widgets.length<1) return;
		$.each(this.widgets, function(idx, widget){
			if(widget["state"] == 1){
				oThis.addWidget(widget["pos"], widget["partId"]);
			}else{
				oThis.delWidget(widget["pos"]);
			}
		});
		this.widgets = null;
	},

	//初始化必填字段
	initRequiredField:function(requiredField){
		if(!requiredField || $.trim(requiredField) == "") return;
		var fieldS = requiredField.split(",");
		for(var i=0; i<fieldS.length; i++){
			this.setRequiredField($.trim(fieldS[i]), true);
		}
	},

	//设置必填字段
	setRequiredField:function(fieldId, tag){
		var nullable= tag? "no" : "yes";

		$("#"+this.infoPart+",#"+this.popupPart).find("#"+fieldId).attr("nullable", nullable);
		if(tag){
			$("#"+this.infoPart+",#" + this.popupPart).find("#span_"+fieldId).addClass("required");
		}else{
			$("#"+this.infoPart+",#" + this.popupPart).find("#span_"+fieldId).removeClass("required");
		}

	}
};