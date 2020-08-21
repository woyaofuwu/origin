var CHOOSE_OFFERS_LIST = new $.DatasetList();

var chooseOperType = "";

if(typeof(EnterpriseCatalog) == "undefined")
{
	window["EnterpriseCatalog"] = function(id){
		this.id = id;
	};
};

(function(){
	$.extend(EnterpriseCatalog.prototype, {
		setOfferListDivSize : function(){
			var offerList = document.getElementById("OfferListDiv");
			var offerHeader = document.getElementById("OfferHead");
			var offerFilter = document.getElementById("OfferTypePart");
//			offerList.style.top = $.format.px2em(offerHeader.offsetHeight + offerFilter.offsetHeight) + "em";
		},
		setOfferTypeStyle : function(id){
			$("li[name=offerType][class='on']").attr("class", "off");
			
			$("#"+id).attr("class","on");
		},
		chooseOfferCategory : function(c){
			
			var CATALOG_ID = c.attr("CATALOG_ID");
			
			//判断是哪种操作类型
			var ifec = $("#IF_EC").val();
			if(ifec == "true")
			{
				chooseOperType = "CrtUs";
				if(CATALOG_ID == "10012")
					chooseOperType = "ChgUs";
			}
			else
			{
				chooseOperType = "CrtMb";
				if(CATALOG_ID == "10014")
					chooseOperType = "ChgMb";
			}
			
			if("10013" == CATALOG_ID || "10015" == CATALOG_ID || "10016" == CATALOG_ID)
			{
				if(!$.enterpriseLogin || !$.enterpriseLogin.isLogin())
				{
					MessageBox.alert("提示信息","请在左边外框先认证政企客户信息！");
					return false;
				}
			}
			
			var custId = $("#cond_CUST_ID").val();
			if($.enterpriseLogin && $.enterpriseLogin.isLogin())
			{
				var custInfo = $.enterpriseLogin.getInfo().get("CUST_INFO");
				custId = custInfo.get("CUST_ID");
				$("#cond_CUST_ID").val(custId);
			}
			var param = "CATALOG_ID="+CATALOG_ID+"&CUST_ID="+custId;
			
			var subscriberInsId = $("#cond_USER_ID").val();
			if(typeof(subscriberInsId) != "undefined")
			{
				param = param + "&USER_ID="+subscriberInsId;
			}
			
			//配置显示隐藏标示，标志是否特殊展示
			var showTag = $("#SHOW_TAG").val();
			if (showTag) {
				param += "&SHOW_TAG="+showTag;
			}
			//渠道调VPMN成员受理标记
			var productTree = $("#cond_PRODUCTTREE_LIMIT_PRODUCTS").val();
			if (productTree) {
				param += "&PRODUCTTREE_LIMIT_PRODUCTS="+productTree;
			}
			
			//根据登陆集团选择的商品来加载已订购商品列表
			if($.enterpriseLogin && $.enterpriseLogin.isLogin())
			{
				var selProd = $.enterpriseLogin.getInfo().get("SEL_PROD");
				if(selProd)
				{
					var selSubInsId = selProd.get("USER_ID");
					var selOfferId = selProd.get("OFFER_ID");
					if(selSubInsId){
						param += "&SEL_USER_ID=" + selSubInsId;
					}
					if(selOfferId){
						param += "&SEL_OFFER_ID=" + selOfferId;
					}
					
				}
			}

			if("10016" == CATALOG_ID)
			{
				param += "&BATCH_OPER_TYPE=" + parent.$("#BATCH_OPER_TYPE").val();
			}
			param += "&KF_FLAG="+$("#kfFlag").val();
			$.beginPageLoading("数据加载中......");
			$.ajax.submit("", "", param, "OfferListPart", function(data){
				//给搜索准备数据
				CHOOSE_OFFERS_LIST = data;
				
				//判断集团是否受理过VPMN产品
			   var userId = $("#VPMN_USER_ID").val();
			   if(productTree){
				   if(userId == undefined || userId == null || userId ==""){
					   if("10013" == CATALOG_ID){
						   MessageBox.alert("提示信息","该集团未订购或已订购集团VPMN成员，请在集团侧订购或查询已订购商品！");
					   }
					   if("10014" == CATALOG_ID){
						   MessageBox.alert("提示信息","该号码未订购集团VPMN成员，请先订购集团VPMN成员！");
					   }
				   }
			   }
			   
				$.endPageLoading();
			},
			function(error_code,error_info,derror){
				$.endPageLoading();
				showDetailErrorInfo(error_code,error_info,derror);
		    });
			
			this.setOfferTypeStyle(c.attr("id"));
		},
		showBbossSubOffers : function(offerInsId, operType){
			var cataDataStr = $("#CATADATA_"+offerInsId).text();
			var cataData = new Wade.DataMap(cataDataStr);
			
			var unfoldId = "unfold_" + offerInsId;
			var foldId = "fold_" + offerInsId;
			
			$("#"+unfoldId).css("display", "none");
			$("#"+foldId).css("display", "");
			
			if($("#sub_"+offerInsId).length != 0)
			{
				$("#sub_"+offerInsId).css("display", "");
			}
			else
			{
				if(operType == "CrtMb")
				{
					this.loadBbossCrtSubOffers(cataData, offerInsId);
				}
				else
				{
					var mebsubInstId = $("#cond_USER_ID").val();
					var mebEparchyCode = $("#cond_USER_EPARCHY_CODE").val();
					
					this.loadBbossChgDelSubOffers(cataData, mebEparchyCode,mebsubInstId, operType);
				}
			}
		},
		loadBbossCrtSubOffers : function(cataData){
			var self = this;
			var ecSubscriberInsId = cataData.get( "USER_ID");//集团商品用户id
			 $("#cond_EC_MERCH_USER_ID").val(ecSubscriberInsId);
			var memUserId = $("#cond_USER_ID").val();
			$.beginPageLoading("数据加载中......");
			$.ajax.submit("", "initBBossOpenedSubOffers", "USER_ID="+ecSubscriberInsId+"&OFFER_ID="+cataData.get("OFFER_ID")+"&MEB_USER_ID="+memUserId, "", function(dataset){
				$.endPageLoading();
				if(dataset != null && dataset.length > 0 )
				{
					var subOfferHtml = "<div class='sub' id='sub_"+ecSubscriberInsId+"'><div class='main'>"
						+ "<div class='c_list c_list-gray c_list-border c_list-line c_list-col-1'>"
						+ "<ul>";
					for(var i = 0; i < dataset.length; i++)
					{
						var data = dataset.get(i);
						var subOfferId = data.get("OFFER_ID");
						var subOfferName = data.get("OFFER_NAME");
						var subSubInsId = data.get("USER_ID");
						var subAccessNum = data.get("SERIAL_NUMBER");
						var offerCode = data.get("OFFER_CODE");
						var subCataData = self.buildCatalogData(cataData, subOfferId, subOfferName, subSubInsId, subAccessNum, offerCode);

						if($("#cond_IS_BATCH").val())
						{//批量任务
							subCataData = new Wade.DataMap(subCataData);
							subCataData.put("BATCH_OPER_TYPE", parent.$("#BATCH_OPER_TYPE").val());
							subCataData.put("IS_BATCH", "true");
							subCataData = subCataData.toString();
						}
						subOfferHtml += "<li>"
							+ "<div class='group'>"
							+ "<div class='main'>" 
							+ subOfferName 
							+ "<div class='content'>" + "服务号码：" + subAccessNum + "</div>"
							+ "</div>"
							+ "</div>"
							+ "<div class='side'>"
							+ "<button class='e_button-s e_button-r e_button-green' ontap='chooseOfferInfo(&#39;" + subSubInsId + "&#39;, &#39;CrtMb&#39;)'>"
							+ "<span class='e_ico-cart'></span>"
							+ "<span>订购</span>"
							+ "</button>"
							+ "<div id='CATADATA_"+ subSubInsId +"' name='CATADATA_"+ subSubInsId +"' style='display:none'>" 
							+ subCataData 
							+ "</div>"
							+ "</div></li>";
					}
					subOfferHtml += "</ul></div></div></div>";
					$("#cata_"+ecSubscriberInsId).append(subOfferHtml);
				}
				
			},
			function(error_code,error_info,derror){
				$.endPageLoading();
				showDetailErrorInfo(error_code,error_info,derror);
		    });
		},
		loadBbossChgDelSubOffers : function(cataData, mebEparchyCode,mebsubInstId, operType){
			var self = this;
			var ecSubscriberInsId = cataData.get("USER_ID");
			var offerid = cataData.get("OFFER_ID");
			var param = "EC_USER_ID="+ecSubscriberInsId+"&MEM_USER_ID="+mebsubInstId+"&MEM_EPARCHY_CODE="+mebEparchyCode+"&OFFER_ID="+offerid;
			var isBatch = $("#cond_IS_BATCH").val();
			if(isBatch == "true")
			{
				param += "&IS_BATCH=true";
			}
			$.beginPageLoading("数据加载中......");
			$.ajax.submit("", "initBBossMebOpenedSubOffers", param, "", function(dataset){
				$.endPageLoading();
				if(dataset != null && dataset.length > 0 )
				{
					var subOfferHtml = "<div class='sub' id='sub_"+ecSubscriberInsId+"'><div class='main'>"
						+ "<div class='c_list c_list-gray c_list-border c_list-line c_list-col-1'>"
						+ "<ul>";
					for(var i = 0; i < dataset.length; i++)
					{
						var data = dataset.get(i);
						var subOfferId = data.get("OFFER_ID");
						var subOfferName = data.get("OFFER_NAME");
						var subSubInsId = data.get("USER_ID");
						var subAccessNum = data.get("SERIAL_NUMBER");
						var offerCode = data.get("OFFER_CODE");
						var subCataData = self.buildCatalogData(cataData, subOfferId, subOfferName, subSubInsId, subAccessNum, offerCode);
						
						if(isBatch == "true")
						{//批量任务
							subCataData = new Wade.DataMap(subCataData);
							subCataData.put("BATCH_OPER_TYPE", parent.$("#BATCH_OPER_TYPE").val());
							subCataData.put("IS_BATCH", "true");
							subCataData = subCataData.toString();
						}
						
						subOfferHtml += "<li>"
							+ "<div class='group'>"
							+ "<div class='main'>" 
							+ subOfferName 
							+ "<div class='content'>" + "服务号码：" + subAccessNum + "</div>"
							+ "</div>"
							+ "</div>"
							+ "<div class='side' style='width:200px'>";
						var chgBtnHtml = "<button class='e_button-s e_button-r e_button-blue' ontap='chooseOfferInfo(&#39;" + subSubInsId + "&#39;, &#39;ChgMb&#39;)'>"
							+ "<span class='e_ico-change'></span>"
							+ "<span>变更</span>"
							+ "</button>";
						var delBtnHtml = "<button class='e_button-s e_button-r e_button-red' ontap='chooseOfferInfo(&#39;" + subSubInsId + "&#39;, &#39;DstMb&#39;)'>" 
							+ "<span class='e_ico-delete'></span>"
							+ "<span>注销</span>"
							+ "</button>";

						if(isBatch == "true")
						{
							if(operType == "memChg")
							{
								subOfferHtml = subOfferHtml + chgBtnHtml;
							}
							else
							{
								subOfferHtml = subOfferHtml + delBtnHtml;
							}
						}
						else
						{
							if($("#kfFlag").val() == "true")
							{//新疆客服只有成员删除
								subOfferHtml = subOfferHtml + delBtnHtml;
							}
							else
							{
								subOfferHtml = subOfferHtml + chgBtnHtml + "&nbsp;" + delBtnHtml;
							}
						}
						
						subOfferHtml += "<div id='CATADATA_"+ subSubInsId +"' name='CATADATA_"+ subSubInsId +"' style='display:none'>" 
							+ subCataData 
							+ "</div>" 
							+ "</div></li>";
						
					}
					subOfferHtml += "</ul></div></div></div>";
					$("#cata_"+ecSubscriberInsId).append(subOfferHtml);
				}
				
			},
			function(error_code,error_info,derror){
				$.endPageLoading();
				showDetailErrorInfo(error_code,error_info,derror);
			});
		},
		buildCatalogData : function(cataData, subOfferId, subOfferName, subSubInsId, subAccessNum, offerCode){
			var subCataData = new Wade.DataMap();
			subCataData.put("USER_ID", subSubInsId);
			subCataData.put("SERIAL_NUMBER", subAccessNum);
			subCataData.put("SUB_OFFER_ID", subOfferId);
			subCataData.put("OFFER_NAME", subOfferName);
			//subCataData.put("OFFER_ID", cataData.get("OFFER_ID"));
			subCataData.put("OFFER_ID", subOfferId);
			subCataData.put("BRAND_CODE", cataData.get("BRAND_CODE"));
			subCataData.put("GROUP_ID", cataData.get("GROUP_ID"));
			subCataData.put("GROUP_NAME", cataData.get("GROUP_NAME"));
			subCataData.put("CUST_ID", cataData.get("CUST_ID"));
			subCataData.put("OFFER_CODE", offerCode);
			return subCataData.toString();
		},
		hideBbossSubOffers : function(offerInsId){

			var foldId = "fold_" + offerInsId;
			var unfoldId = "unfold_" + offerInsId;
			
			$("#"+foldId).css("display", "none");
			$("#"+unfoldId).css("display", "");
			
			if($("#sub_"+offerInsId).length != 0)
			{
				$("#sub_"+offerInsId).css("display", "none");
			}
		},
		closeEcCataPopupItem : function(){
			var ecCataPopupItem = document.getElementById(this.id);
			hidePopup(ecCataPopupItem);
		},
		showBbossSubOffers2 : function(el, operType){
			var offerId = $(el).attr("OFFER_ID");                    //商品编码(集团主商品)
			var offerCode = $(el).attr("OFFER_CODE"); 
			var offerInsId = $(el).attr("USER_ID");                    //集团用户标识
			var ecAccessNum = $(el).attr("SERIAL_NUMBER");              //集团服务号码
			var ecBrand = $(el).attr("BRAND_CODE");                       //集团商品品牌
			var custId = $(el).attr("CUST_ID");                      //选择商品的集团标识
			var groupId = $(el).attr("GROUP_ID");                    //选择商品的集团编码
			var groupName = $(el).attr("GROUP_NAME");                //选择商品的集团名称
			
			var cataData = new Wade.DataMap();
			cataData.put("OFFER_ID",offerId);
			cataData.put("OFFER_CODE",offerCode);
			cataData.put("USER_ID",offerInsId);
			cataData.put("SERIAL_NUMBER",ecAccessNum);
			cataData.put("BRAND_CODE",ecBrand);
			cataData.put("CUST_ID",custId);
			cataData.put("GROUP_ID",groupId);
			cataData.put("GROUP_NAME",groupName);
			
			var unfoldId = "unfold_" + offerInsId;
			var foldId = "fold_" + offerInsId;
			
			$("#"+unfoldId).css("display", "none");
			$("#"+foldId).css("display", "");
			
			if($("#sub_"+offerInsId).length != 0)
			{
				$("#sub_"+offerInsId).css("display", "");
			}
			else
			{
				if(operType == "CrtMb")
				{
					this.loadBbossCrtSubOffers(cataData, offerInsId);
				}
				else
				{
					var mebsubInstId = $("#cond_USER_ID").val();
					var mebEparchyCode = $("#cond_USER_EPARCHY_CODE").val();
					
					this.loadBbossChgDelSubOffers(cataData, mebEparchyCode,mebsubInstId, operType);
				}
			}
		},
		search:function(){
			var text = $("#PRODUCT_SEARCH_TEXT").val();
			
				var rst = new $.DatasetList();
				for(var i=0;i<CHOOSE_OFFERS_LIST.length;i++){
					var product = CHOOSE_OFFERS_LIST.get(i);
					var productId = product.get("OFFER_CODE");
					var productName = product.get("OFFER_NAME");
//					if(productId.indexOf(text)>-1){
//						rst.add(product);
//					}else 
						if(productName.indexOf(text)>-1){
						rst.add(product);
					}
				}
				enterpriseCatalog.drawProduct(rst);
			
			
		},
		drawProduct : function(products){
			var drawArea = $("#chooseOffers");
			drawArea.empty();
			var html=[];
			for(var i=0;i<products.length;i++){
				var className=null;
				var checked = false;
				var disabled = false;
				var product = products.get(i);
				var offerCode = product.get("OFFER_CODE");
				var offerName = product.get("OFFER_NAME");
				var offerId = product.get("OFFER_ID");
				
                
                if(chooseOperType == "CrtUs"){
                	
                	var crtType = "CrtUs";
                	
                	//集团新增
                	html.push('<li>');
    				html.push('<div class="content">');
    				html.push('<div class="main">');
    				html.push('<div class="title" >'+offerName+'</div>');
    				html.push('</div>');
    				
    				html.push('<div class="side" style="width:100px;">');
    				html.push('<button jwcid="@Any" class="e_button-s e_button-r e_button-green" OFFER_CODE="'+offerCode+'"OFFER_ID="'+offerId+'"OFFER_NAME="'+offerName+'" onclick="chooseOfferInfo(this,\''+crtType+'\');">');
    				html.push('<span class="e_ico-change"></span>');
    				html.push('<span>订购</span>');
    				html.push('</button>');
		
    				html.push('</div>');
    				html.push('</div>');
    				
                }
                
                else if(chooseOperType == "ChgUs"){
                
                var chgType = "ChgUs";
                var dstType = "DstUs";
                
                var userId = product.get("USER_ID");
                var serialNumber = product.get("SERIAL_NUMBER");
                var groupName = product.get("GROUP_NAME");
                
                //集团变更,退订
				html.push('<li>');
				html.push('<div class="content">');
				html.push('<div class="main">');
				html.push('<div class="title" >'+offerName+'</div>');
				html.push('<div class="content" >'+"服务号码："+serialNumber+'</div>');
				html.push('<div class="content" >'+"集团名称:"+groupName+'</div>');
				html.push('</div>');
				
				html.push('<div class="side" style="width:200px;">');
				html.push('<button jwcid="@Any" class="e_button-s e_button-r e_button-blue" OFFER_CODE="'+offerCode+'"OFFER_ID="'+offerId+'"OFFER_NAME="'+offerName+'"USER_ID="'+userId+'" onclick="chooseOfferInfo(this,\''+chgType+'\');">');
				html.push('<span class="e_ico-change"></span>');
				html.push('<span>变更</span>');
				html.push('</button>');

				html.push('<button jwcid="@Any" class="e_button-s e_button-r e_button-red" OFFER_CODE="'+offerCode+'"OFFER_ID="'+offerId+'"OFFER_NAME="'+offerName+'"USER_ID="'+userId+'" ontap="chooseOfferInfo(this,\''+dstType+'\');">');
				html.push('<span class="e_ico-delete"></span>');
				html.push('<span>注销</span>');
				html.push('</button>');			
				html.push('</div>');
				html.push('</div>');
				
                }
				
                else if(chooseOperType == "CrtMb"){
                	
                    var crtType = "CrtMb";
                	
                    var userId = product.get("USER_ID");
                    var serialNumber = product.get("SERIAL_NUMBER");
                    var groupName = product.get("GROUP_NAME");
                    var groupId = product.get("GROUP_ID");
                    var custId = product.get("CUST_ID");
                    var brandCode = product.get("BRAND_CODE");
                    
                    var bosgInsId = "cata_"+userId;
                    
                    if(brandCode == "BOSG")
                    {
                    	html.push('<li id="'+ bosgInsId +'">');
        				html.push('<div class="content">');
        				html.push('<div class="main">');
        				html.push('<div class="title" >'+offerName+'</div>');
        				html.push('<div class="content" >'+"服务号码："+serialNumber+'</div>');
        				html.push('<div class="content" >'+"集团名称:"+groupName+'</div>');
        				html.push('</div>');
        				
        				var unfold = "unfold_"+userId;	
        				var fold   = "fold_"+userId;	
        				
        				html.push('<div class="fn" jwcid="@Any" id="'+unfold+'" BRAND_CODE="'+brandCode+'"CUST_ID="'+custId+'"GROUP_ID="'+groupId+'"OFFER_CODE="'+offerCode+'"OFFER_ID="'+offerId+'"OFFER_NAME="'+offerName+'"USER_ID="'+userId+'" ontap="enterpriseCatalog.showBbossSubOffers2(this, \''+crtType+'\');" style="display:">');
        				html.push('<span class="e_ico-unfold">');
        				html.push('</span>');
        				html.push('</div>');
        				html.push('<div class="fn" jwcid="@Any" id="'+fold+'" BRAND_CODE="'+brandCode+'"CUST_ID="'+custId+'"GROUP_ID="'+groupId+'"OFFER_CODE="'+offerCode+'"OFFER_ID="'+offerId+'"OFFER_NAME="'+offerName+'"USER_ID="'+userId+'" ontap="enterpriseCatalog.hideBbossSubOffers(\''+userId+'\');" style="display:none">');
        				html.push('<span class="e_ico-fold">');
        				html.push('</span>');
        				html.push('</div>');
        				  		
        				html.push('</div>');
                    	
                    }	
                    else
                    {
                	//成员新增
                    html.push('<li>');
    				html.push('<div class="content">');
    				html.push('<div class="main">');
    				html.push('<div class="title" >'+offerName+'</div>');
    				html.push('<div class="content" >'+"服务号码："+serialNumber+'</div>');
    				html.push('<div class="content" >'+"集团名称:"+groupName+'</div>');
    				html.push('</div>');
    				
    				html.push('<div class="side" style="width:100px;">');
    				html.push('<button jwcid="@Any" class="e_button-s e_button-r e_button-green" BRAND_CODE="'+brandCode+'"CUST_ID="'+custId+'"GROUP_ID="'+groupId+'"OFFER_CODE="'+offerCode+'"OFFER_ID="'+offerId+'"OFFER_NAME="'+offerName+'"USER_ID="'+userId+'" onclick="chooseOfferInfo2(this,\''+crtType+'\');">');
    				html.push('<span class="e_ico-change"></span>');
    				html.push('<span>订购</span>');
    				html.push('</button>');
		
    				html.push('</div>');
    				html.push('</div>');
    				
                    }
    				
                }
				
                else if(chooseOperType == "ChgMb"){
                	
                	//成员变更
                	var chgType = "ChgMb";
                    var dstType = "DstMb";
                	
                    var userId = product.get("USER_ID");
                    var serialNumber = product.get("SERIAL_NUMBER");
                    var groupName = product.get("GROUP_NAME");
                    var groupId = product.get("GROUP_ID");
                    var custId = product.get("CUST_ID");
                    var brandCode = product.get("BRAND_CODE");
                    
                    var bosgInsId = "cata_"+userId;
                    
                    if(brandCode == "BOSG")
                    {
                    	html.push('<li id="'+ bosgInsId +'">');
        				html.push('<div class="content">');
        				html.push('<div class="main">');
        				html.push('<div class="title" >'+offerName+'</div>');
        				html.push('<div class="content" >'+"服务号码："+serialNumber+'</div>');
        				html.push('<div class="content" >'+"集团名称:"+groupName+'</div>');
        				html.push('</div>');
        				
        				var unfold = "unfold_"+userId;	
        				var fold   = "fold_"+userId;	
        				
        				html.push('<div class="fn" jwcid="@Any" id="'+unfold+'" GROUP_NAME="'+groupName+'"BRAND_CODE="'+brandCode+'"CUST_ID="'+custId+'"GROUP_ID="'+groupId+'"OFFER_CODE="'+offerCode+'"OFFER_ID="'+offerId+'"OFFER_NAME="'+offerName+'"USER_ID="'+userId+'" ontap="enterpriseCatalog.showBbossSubOffers2(this, \''+chgType+'\');" style="display:">');
        				html.push('<span class="e_ico-unfold">');
        				html.push('</span>');
        				html.push('</div>');
        				html.push('<div class="fn" jwcid="@Any" id="'+fold+'" GROUP_NAME="'+groupName+'"BRAND_CODE="'+brandCode+'"CUST_ID="'+custId+'"GROUP_ID="'+groupId+'"OFFER_CODE="'+offerCode+'"OFFER_ID="'+offerId+'"OFFER_NAME="'+offerName+'"USER_ID="'+userId+'" ontap="enterpriseCatalog.hideBbossSubOffers(\''+userId+'\');" style="display:none">');
        				html.push('<span class="e_ico-fold">');
        				html.push('</span>');
        				html.push('</div>');
        				  		
        				html.push('</div>');
                    	
                    }	
                    else
                    {
                	
                    html.push('<li>');
    				html.push('<div class="content">');
    				html.push('<div class="main">');
    				html.push('<div class="title" >'+offerName+'</div>');
    				html.push('<div class="content" >'+"服务号码："+serialNumber+'</div>');
    				html.push('<div class="content" >'+"集团名称:"+groupName+'</div>');
    				html.push('</div>');
    				
    				html.push('<div class="side" style="width:200px;">');
    				html.push('<button jwcid="@Any" class="e_button-s e_button-r e_button-blue" GROUP_NAME="'+groupName+'"BRAND_CODE="'+brandCode+'"CUST_ID="'+custId+'"GROUP_ID="'+groupId+'"OFFER_CODE="'+offerCode+'"OFFER_ID="'+offerId+'"OFFER_NAME="'+offerName+'"USER_ID="'+userId+'" onclick="chooseOfferInfo2(this,\''+chgType+'\');">');
    				html.push('<span class="e_ico-change"></span>');
    				html.push('<span>变更</span>');
    				html.push('</button>');

    				html.push('<button jwcid="@Any" class="e_button-s e_button-r e_button-red" GROUP_NAME="'+groupName+'"BRAND_CODE="'+brandCode+'"CUST_ID="'+custId+'"GROUP_ID="'+groupId+'"OFFER_CODE="'+offerCode+'"OFFER_ID="'+offerId+'"OFFER_NAME="'+offerName+'"USER_ID="'+userId+'" onclick="chooseOfferInfo2(this,\''+dstType+'\');">');
    				html.push('<span class="e_ico-delete"></span>');
    				html.push('<span>注销</span>');
    				html.push('</button>');			
    				html.push('</div>');
    				html.push('</div>');
                    }
    				
                }
			}

			$.insertHtml('beforeend',drawArea,html.join(""));
		},
	});
})();

