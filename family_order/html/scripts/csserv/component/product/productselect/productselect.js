if(typeof(ProductSelect)=="undefined"){
	window["ProductSelect"]=(function(){
		return{
			monitor:null,
			returnValue:"",
			productName:"",
			brandCode:"",
			brandName:"",
			products:null,
			/**
			 * 点击产品目录弹出框事件
			 * 
			 * @param productTypeCode
			 * @param eparchyCode
			 * @param userProductId
			 * @param assignProductIds
			 * @returns
			 */
			popupProductSelect : function(productTypeCode,eparchyCode,userProductId,assignProductIds){
				var param = "";
				param+="&USER_PRODUCT_ID="+userProductId;
				param+="&PRODUCT_TYPE_CODE="+productTypeCode;
				param+="&EPARCHY_CODE="+eparchyCode;
				if(assignProductIds){
					param+="&ASSIGN_PRODUCTIDS="+assignProductIds;
				}
				if(typeof(getOtherParam)=="function"){
					param+=getOtherParam();
				}
				if($("#monitor").val()){
					if($("#"+$("#monitor").val()).val()){
						if(this.monitor!=$("#"+$("#monitor").val()).val()){
							this.monitor = $("#"+$("#monitor").val()).val();
							this.returnValue="";
							this.productName="";
							this.brandCode="";
							this.brandName="";
							this.products=null;
						}
						else{
							$("#productTreePanel").css("display","");
							return;
						}
					}
				}
				var obj = this;
				$.ajax.submit(null,null,param,$("#PRODUCTSELECT_COMPONENT_ID").val(), function(data){obj.afterRender(data,obj);});
			},
			
			/**
			 * 点击产品目录弹出框事件, 传入产品名称
			 * 
			 * @param productTypeCode
			 * @param eparchyCode
			 * @param userProductId
			 * @param assignProductIds
			 * @returns
			 */
			popupProductSelectYW : function(productTypeCode, productTypeName,eparchyCode,userProductId,assignProductIds){		
				var param = "";
				param+="&PRODUCT_TYPE_NAME="+productTypeName;
				param+="&USER_PRODUCT_ID="+userProductId;
				param+="&PRODUCT_TYPE_CODE="+productTypeCode;
				param+="&EPARCHY_CODE="+eparchyCode;
				param+="&IS_TREE=true";
				if(assignProductIds){
					param+="&ASSIGN_PRODUCTIDS="+assignProductIds;
				}
				if(typeof(getOtherParam)=="function"){
					param+=getOtherParam();
				}
				
				if($("#monitor").val()){
					if($("#"+$("#monitor").val()).val()){
						if(this.monitor!=$("#"+$("#monitor").val()).val()){
							this.monitor = $("#"+$("#monitor").val()).val();
							this.returnValue="";
							this.productName="";
							this.brandCode="";
							this.brandName="";
							this.products=null;
						}
						else{
							$("#productTreePanel").css("display","");
							return;
						}
					}
				}
				var obj = this;
				$.ajax.submit(null,null,param,$("#PRODUCTSELECT_COMPONENT_ID").val(), function(data){obj.afterRender(data,obj);});
			},
			
			/**
			 * 点击产品目录弹出框事件(对于新的产品树，不传入productId)
			 * 
			 * @param productTypeCode
			 * @param eparchyCode
			 * @param userProductId
			 * @param assignProductIds
			 * @returns
			 */
			popupProductSelectNew : function(productTypeCode,eparchyCode,userProductId,assignProductIds){
				var param = "";
				param+="&USER_PRODUCT_ID="+userProductId;
				param+="&PRODUCT_TYPE_CODE="+productTypeCode;
				param+="&EPARCHY_CODE="+eparchyCode;
				param+="&IS_TREE=true";
				if(assignProductIds){
					param+="&ASSIGN_PRODUCTIDS="+assignProductIds;
				}
				if(typeof(getOtherParam)=="function"){
					param+=getOtherParam();
				}
				
				if($("#monitor").val()){
					if($("#"+$("#monitor").val()).val()){
						if(this.monitor!=$("#"+$("#monitor").val()).val()){
							this.monitor = $("#"+$("#monitor").val()).val();
							this.returnValue="";
							this.productName="";
							this.brandCode="";
							this.brandName="";
							this.products=null;
						}
						else{
							$("#productTreePanel").css("display","");
							return;
						}
					}
				}
				var obj = this;
				$.ajax.submit(null,null,param,$("#PRODUCTSELECT_COMPONENT_ID").val(), function(data){obj.afterRender(data,obj);});
			},
			
			
			afterRender : function(data, obj){
				var productTags = data.get("TAGS");
				var offers = data.get("OFFERS");
				obj.products = offers;
				obj.drawProductTag(productTags);
				obj.drawProduct(offers);
				$("#productTreePanel").css("display","");
			},
			
			drawProduct : function(products){
				var drawArea = $("#productArea");
				drawArea.empty();
				var html=[];
				for(var i=0;i<products.length;i++){
					var className=null;
					var checked = false;
					var disabled = false;
					var product = products.get(i);
					var productId = product.get("OFFER_CODE");
					var productName = product.get("OFFER_NAME");
					var brandCode = product.get("BRAND_CODE");
					var brandName = product.get("BRAND_NAME");
					html.push('<li>');
					html.push('<label class="text"><span>');
					html.push('<input name="productRadio" type="radio" value="'+productId+'" productName="'+productName+'" brandCode="'+brandCode+'" brandName="'+brandName+'" onclick="ProductSelect.selectedProduct($(this));" class="e_radio"/>');
					html.push('['+productId+']'+productName);
					html.push('</span></label></li>');
				}
				//html.push("</div></div>");
				$.insertHtml('beforeend',drawArea,html.join(""));
			},
			
			drawProductTag : function(productTags){
				var drawArea = $("#productTagArea");
				drawArea.empty();
				var html=[];
				html.push("<li>");
				html.push('<div class="label">品牌：</div>');
				html.push('<div class="option"><ul>');
				html.push('<li tagName="不限" tagValue="-1" class="on" onclick="ProductSelect.filter($(this));">不限</li>');
				for(var i=0;i<productTags.length;i++){
					var productTag = productTags.get(i);
					var tagName = productTag.get("CATALOG_NAME");
					var tagValue = productTag.get("CATALOG_ID");
					html.push("<li tagName='"+tagName+"' tagValue='"+tagValue+"' onclick='ProductSelect.filter($(this));'>");
					html.push(tagName);
					html.push("</li>");
				}
				html.push("</ul></div>");
				html.push("</li>");
				$.insertHtml('beforeend',drawArea,html.join(""));
			},
			
			filter : function(obj){
				this.switchTagClass(obj);
				var tagValue = obj.attr("tagValue");
				if(tagValue == -1){
					this.drawProduct(this.products);
					return;
				}
				
				var rst = new $.DatasetList();
				for(var i=0;i<this.products.length;i++){
					var product = this.products.get(i);
					var tempTagValue = product.get("CATALOG_ID");
					if(tempTagValue == tagValue){
						rst.add(product);
					}
				}
				this.drawProduct(rst);
			},
			
			switchTagClass : function(obj){
				var parent = obj.parent();
				var children = parent.children();
				var tagValue = obj.attr("tagValue");
				for(var i=0;i<children.length;i++){
					var child = $(children[i]);
					var tempTagValue = child.attr("tagValue");
					if(tempTagValue == tagValue){
						child.attr("class","on");
					}
					else{
						child.attr("class","");
					}
				}
			},
			/**
			 * 勾选主产品树节点触发动作
			 */
			selectedProduct: function(obj){
				this.returnValue=obj.val();
				this.productName=obj.attr("productName");
				this.brandCode=obj.attr("brandCode");
				this.brandName=obj.attr("brandName");
			},
			
			confirmAction: function(){
				if(this.returnValue==""){
					alert("请选择产品");
					return false;
				}
				var productId = this.returnValue;
				var productName = this.productName;
				var brandCode=this.brandCode;
				var brandName=this.brandName;
				if($("#AFTER_ACTION").val()!=""&&$("#AFTER_ACTION").val()!="undefined"){
					eval($("#AFTER_ACTION").val());
				}
				$("#productTreePanel").css("display","none");
			}
		}
	})();
}