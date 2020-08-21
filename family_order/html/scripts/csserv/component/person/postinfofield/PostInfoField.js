(function($){
	$.extend({postInfo:{
		widgets:[],
		infoPart: "PostInfoPart",
		init:function(){
			this.initWidget();
			this.bindEvents();
			//初始化是否邮寄标识事件
			this.events.onChangePostTag();
			
			var requiredField = $("#PostInfoCmp").attr("requiredField");
			this.initRequiredField(requiredField);

		},
		
		//绑定事件
		bindEvents:function(){
			//邮递标志
			$("#POST_TAG").bind("change", $.postInfo.events.onChangePostTag);
			//邮寄方式
			$("input[name=POST_TYPESET]").bind("click", $.postInfo.events.onClickPostTypeset);
			$("input[name=POST_TYPESET][value=0]").attr("disabled", true);
			//区块显示/隐藏
			$("#PostInfoVisiblPart").bind("click", function(e){
				$.postInfo.toggleInfoPart($(this), $.postInfo.infoPart, "邮寄信息");
			});
			var isHidden = $("#PostInfoCmp").attr("isHidden")=="true"? true : false;
			if(isHidden && $("#"+this.infoPart).css("display")!=="none"){
				$("#PostInfoVisiblPart").trigger("click");
			}
		},
		events:{
			onChangePostTag:function(){
				var postTag = $("#POST_TAG").val();
				//选择不邮寄
				if(postTag=="0" || postTag=="") {
					//隐藏邮寄信息区
					$("#PostTagHidePart").css("display", "none");
					$("#PostTypeContentPart,#EmailTypeContentPart,#MmsTypeContentPart").css("display", "none");
					
					//去掉必填样式
					$("#span_POST_TYPESET,#span_POSTTYPE_CONTENT,#span_POST_ADDRESS,#span_POST_NAME,#span_POST_FAX_NBR,#span_POST_EMAIL").removeClass("e_required");

					//去掉必填限制
					$("#POST_TYPESET,#POSTTYPE_CONTENT,#POST_ADDRESS,#POST_NAME,#POST_FAX_NBR,#POST_EMAIL").attr("nullable", "yes");
					
					//清空邮寄信息
					//$("#POST_TYPESET,#POSTTYPE_CONTENT,#POST_ADDRESS,#POST_NAME,#POST_FAX_NBR,#POST_EMAIL,#ZIP_CODE").val();
				}
				//选择邮寄
				else {
					//显示邮寄信息区
					$("#PostTagHidePart").css("display", "");
					
					//初始化邮寄信息,从页面客户编辑区域获取
					/**
					$("#POST_ADDRESS").val(($("#ADDRESS")&& $("#ADDRESS").length)?$("#ADDRESS").val():"");
					$("#ZIP_CODE").val(($("#POST_CODE")&& $("#POST_CODE").length)?$("#POST_CODE").val():"");
					$("#POST_NAME").val(($("#CUST_NAME")&& $("#CUST_NAME").length)?$("#CUST_NAME").val():"");
					$("#POST_FAX_NBR").val(($("#FAX_NBR")&& $("#FAX_NBR").length)?$("#FAX_NBR").val():"");
					$("#POST_EMAIL").val(($("#EMAIL")&& $("#EMAIL").length)?$("#EMAIL").val():"");
					*/
					//必填样式
					$("#span_POST_TYPESET").addClass("e_required");
					//必填限制
					$("input[name=POST_TYPESET]").attr("nullable", "no");
					$("input[name=POST_TYPESET]").each(function(){
						if($(this).attr("checked")){
							if($(this).val() == "0"){
								$("#PostTypeContentPart").css("display", "");
							}else if($(this).val() == "2"){
								$("#EmailTypeContentPart").css("display", "");
							}else if($(this).val() == "3"){
								$("#MmsTypeContentPart").css("display", "");
							}
						}
					});
				}
			},
			onClickPostTypeset:function(){
			    //选中邮寄方式
				if($(this).attr("checked")) {
					if($(this).val()==0) {	//邮政投递
						$("#PostTypeContentPart").css("display", "");
						//必填样式
					    $("#span_POSTTYPE_CONTENT,#span_POST_ADDRESS,#span_POST_NAME").addClass("e_required");
					     
					     //必填限制
						$("#POST_ADDRESS,#POST_NAME").attr("nullable", "no");
			    		 
			    		 //默认邮政投递内容账单
						$("input[name=POSTTYPE_CONTENT][value=0]").attr("checked", true).attr("disabled", true);
						$("input[name=POSTTYPE_CONTENT][value=2]").attr("checked", false).attr("disabled", false);

			            /********取消EMAIL********/
			            $("#span_POST_EMAIL").removeClass("e_required");
					    $("#POST_EMAIL").attr("nullable","yes");

					}else if($(this).val()==2) {
						$("#EmailTypeContentPart").css("display", "");
						//必填样式
						$("#span_EMAILTYPE_CONTENT,#span_EMAIL").addClass("e_required");		     
						$("#POST_EMAIL").attr("nullable", "no");
						
						$("input[name=EMAILTYPE_CONTENT]").attr("checked", false).attr("disabled", false);
						$("input[name=EMAILTYPE_CONTENT][value=0]").attr("checked", true).attr("disabled", true);
					}else if($(this).val()==3) {
						$("#MmsTypeContentPart").css("display", "");
						$("#span_MMSTYPE_CONTENT").addClass("e_required");
						
						$("input[name=MMSTYPE_CONTENT]").attr("checked", false).attr("disabled", false);
						$("input[name=MMSTYPE_CONTENT][value=0]").attr("checked", true).attr("disabled", true);
					}	
				}
				//取消邮寄方式
				else {
					//邮政投递
					if($(this).val()==0) {
						$("#PostTypeContentPart").css("display", "none");
						//去掉必填样式
					    $("#span_POSTTYPE_CONTENT,#span_POST_ADDRESS,#span_POST_NAME").removeClass("e_required");
					     
					     //去掉必填限制
						$("#POST_ADDRESS,#POST_NAME").attr("nullable", "yes");
			    		 
			    		 //去掉邮政投递内容
						$("input[name=POSTTYPE_CONTENT]").attr("checked", false).attr("disabled", false);
					}else if($(this).val()==2) {
						$("#EmailTypeContentPart").css("display", "none");
						//必填样式
						$("#span_EMAILTYPE_CONTENT,#span_EMAIL").removeClass("e_required");		     
						$("#POST_EMAIL").attr("nullable", "yes");
						
						$("input[name=EMAILTYPE_CONTENT]").attr("checked", false).attr("disabled", false);
					}else if($(this).val()==3) {
						$("#MmsTypeContentPart").css("display", "none");
						$("#span_MMSTYPE_CONTENT").removeClass("e_required");
						
						$("input[name=MMSTYPE_CONTENT]").attr("checked", false).attr("disabled", false);
					}		
				}
				
			}
		}
	}});
	$.extend($.postInfo, BaseInfoLib);
})(Wade);
