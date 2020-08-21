(function($){
	$.extend({assureInfo:{
		infoPart: "AssureInfoPart",
		widgets:[],
		init:function(){
			this.initWidget();
			this.bindEvents();
			var requiredField = $("#AssureInfoCmp").attr("requiredField");
			this.initRequiredField(requiredField);
		},
		
		//绑定事件
		bindEvents:function(){
			var oThis = this;
			//显示/隐藏客户编辑区域
			$("#AssureInfoVisiblPart").bind("click", function(e){
				oThis.toggleInfoPart($(this), oThis.infoPart, "担保信息");
			});
			var isHidden = $("#AssureInfoCmp").attr("isHidden")=="true"? true : false;
			if(isHidden && $("#"+this.infoPart).css("display")!=="none"){
				$("#AssureInfoVisiblPart").trigger("click");
			}
		}
	}});
	$.extend($.assureInfo, BaseInfoLib);
})(Wade);
