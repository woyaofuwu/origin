if(typeof(OfferSimpleCha) == "undefined")
{
	window["OfferSimpleCha"] = function(id){
		this.id = id;
	};
};

(function(){
	$.extend(OfferSimpleCha.prototype, {
		initOfferSimpleCha : function(el){
			var self = this;
			var offerId = $(el).attr("OFFER_ID");
			var userId = $(el).attr("USER_ID");
			var offerInsId = $(el).attr("OFFER_INS_ID");
			var offerType = $(el).attr("OFFER_TYPE");
			var param = "&OFFER_ID=" + offerId+"&USER_ID=" + userId+"&OFFER_INS_ID=" + offerInsId+"&OFFER_TYPE=" + offerType;
			var pOfferIndex = $(el).closest("li").attr("offerIndex");
			$.beginPageLoading("数据加载中......");
			$.ajax.submit("", "", param, "OfferSimpleChaListPart", function(data){
				
				$("#pricecha_OFFER_ID").val(offerId);
				
				self.initOfferSimpleChaVal(offerId, pOfferIndex);
				
				$.endPageLoading();
				showPopup("popup", self.id, true);
			},
			function(error_code,error_info,derror){
				$.endPageLoading();
				showDetailErrorInfo(error_code,error_info,derror);
		    });
		},
		initOfferSimpleChaVal : function(offerId, pOfferIndex){
			var offerSimpleChaStr = $("#pcha_"+offerId+"_"+pOfferIndex).val();
			if(offerSimpleChaStr == "")
			{
				return ;
			}
			var offerSimpleChaList = new Wade.DatasetList(offerSimpleChaStr);
			for(var i = 0, size = offerSimpleChaList.length; i < size; i++)
			{
				var offerSimpleCha = offerSimpleChaList.get(i);
				var chaSpecCode = offerSimpleCha.get("CHA_SPEC_CODE");
				var chaValue = offerSimpleCha.get("CHA_VALUE");
				var el = $("div[id=OfferSimpleChaListPart] input[name="+chaSpecCode+"]");
				if("checkbox" == el.attr("type"))
				{
					if(chaValue == "1")
					{
						el.attr("checked", true);
					}
					else
					{
						el.attr("checked", false);
					}
				}
				else
				{
					el.val(chaValue);
				}
			}
		},
		submitOfferSimpleCha : function(){
			if(!$.validate.verifyAll("OfferSimpleChaListPart"))
			{
				return ;
			}
			
			this.calculateParam(); //自动计算总价

			var offerId = $("#pricecha_OFFER_ID").val();
			var pOfferIndex = $("#pricecha_OFFER_INDEX").val();
			
			var offerChaIsChg = false; // 资费类商品特征是否被修改(针对实例数据)
			
			var offerSimpleChaList = new Wade.DatasetList(); //资费类商品特征
			$("div[id=OfferSimpleChaListPart] input").each(function(){
				var chaSpecCode = $(this).attr("id");
				var chaValue = $(this).attr("value");
				if($(this).attr("type") == "checkbox")
				{
					chaValue = $(this).attr("checked") ? "1" : "0";
				}
				var chaSpecId = $(this).attr("CHA_SPEC_ID");
				if(!chaSpecId)
				{
					return true; //相当于 continue
				}
				
				var offerSimpleChaData = new Wade.DataMap();
				offerSimpleChaData.put("CHA_SPEC_ID", chaSpecId);
				offerSimpleChaData.put("CHA_VALUE", chaValue);
				offerSimpleChaData.put("CHA_SPEC_CODE", chaSpecCode);
				
				//判断资费类商品特征实例是否变更
				var offerChaInsId = $(this).attr("OFFER_CHA_INS_ID");
				if(offerChaInsId)
				{
					offerSimpleChaData.put("OLD_VALUE", $(this).attr("oldValue"));
					if(chaValue != $(this).attr("oldValue"))
					{
						offerChaIsChg = true;
					}
				}
				offerSimpleChaList.add(offerSimpleChaData);
			});
			
			var self = this;

			//将保存的值设置到页面隐藏域中
			$("#pcha_"+offerId+"_"+pOfferIndex).val(offerSimpleChaList);
			
			if(offerSimpleChaList.length > 0)
			{//将数据保存到offerData中
				$("#Attr_"+offerId).val(offerSimpleChaList.toString());
			}
			if(offerChaIsChg==true){
				var offerData = new Wade.DataMap($("#OFFER_DATA_"+offerId).text());
				offerData.put("OPER_CODE","2");
				$("#OFFER_DATA_"+offerId).text(offerData.toString());
			}

			//隐藏资费类商品特征"待设置"标签
			self.hideOfferSimpleChaTag(offerId, pOfferIndex);
			
			//关闭资费类商品特征popup
			backPopup(document.getElementById(self.id));
			
		},
		calculateParam : function(){
			var self = this;
			$("div[id=OfferSimpleChaListPart] input[calculate_formula]").each(function(){
				var formula = $(this).attr("calculate_formula");
				var regExp = /([0-9]+)(.{1})([A-Za-z0-9]+)(.{1})([A-Za-z0-9]+)/;
				var arr = formula.match(regExp);
				if(arr && arr.length == 6)
				{//arr格式：100*PRICE*COUNT,100,*,PRICE,*,COUNT
					var value1 = arr[1];
					var oper1 = arr[2];
					var value2 = self.getParamValue(arr[3]);
					var oper2 = arr[4];
					var value3 = self.getParamValue(arr[5]);
					var result0 = eval(value1 + oper1 + value2);
					var result = eval(result0 + oper2 + value3);
					
					$(this).attr("value", result);
				}
			});
		},
		getParamValue : function(fieldName){
			var paramValue = $("div[id=OfferSimpleChaListPart] input[name="+fieldName+"]").val();
			if(!paramValue)
			{
				var desc = $("div[id=OfferSimpleChaListPart] input[name="+fieldName+"]").attr("desc");
				MessageBox.alert("提示信息", "["+desc+"]不能为空！");
			}
			return paramValue;
		},
		hideOfferSimpleChaTag : function(offerId, pOfferIndex){
			//隐藏资费类商品特征"待设置"标签 显示"已设置"标签
			var liName = "li_" + offerId;
			$("li[id="+liName+"]").find(".side").attr("tag", "1");
			$("li[id="+liName+"]").find(".side").html("<span class='e_tag e_tag-green'>已设置</span>");
		}
	});
})();



