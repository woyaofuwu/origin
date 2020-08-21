﻿var EnterpriseLogin = function(id) {
	this.id = id;
	this.pageSize = 7;
	this.offerPageSize = 20;
	this.loginData;
	
	Wade.extend({
		enterpriseLogin:this
	});
};
(function(c){
	var o = c.prototype;
	
    o.queryGroup = function (page, pageSize) {
        var code = this.getQueryCode(),
        codetype = this.getQueryCodeType(),
        value = this.getQueryValue();
        value = value.replace(/\s+/g, "");
        
        if (!this._validate()) {
			return;
		}
        
        this._qryGroup(code, codetype, value, page, pageSize);
    };
    
    o.queryOffers = function (page, pageSize) {
    	
    	var custId = "";
    	var custInfo = this.getInfo().get("CUST_INFO");
        if (custInfo) {
			custId = custInfo.get("CUST_ID");
		}
        
        if(!custId)
        {
        	return ;
        }
        
        this._qryOffer(custId, page, pageSize);
    };
    
    o.selectGroup = function(el) {
        var groupId = $(el).find(".main .content").text();
        groupId = $.trim(groupId);
        var self = this;
        
        this.refreshGroupInfo(groupId, null, this.refreshActiveNav);
        
    };
    
    o.refreshGroupInfo = function(groupId, subscriberId, sucfn, errfn) {
    	var self = this;
    	var param = "";
    	if (!groupId || groupId=="null") {
    		return;
    	}
    	param += "&GROUP_ID="+groupId;
    	param += "&ajaxListener=queryDetailByGroupId";
        param += "&grpListPage_current=1";//+page;
        param += "&grpListPage_pagesize="+this.offerPageSize;
        param += "&pagin=grpListPage";
        
//        $.beginPageLoading("查询中...");
        $("#groupListLoading").css("display", "");
        ajaxPost(null, null, param, "GroupLoginPart", function (json) {
            // success
//        	$.endPageLoading();
        	
        	$("#groupListLoading").css("display", "none");
        	
        	$("#loginBtn").css("disabled", false);
            $("#loginBtn").text("查询");
        	
            var dat = new Wade.DataMap(json + "");

            self._refreshGroupCust(dat.get("GROUP_INFO"));
            self._refreshSaleInfo(dat.get("SALE_INFOS"));
            self._refreshGroupDetailParam(dat.get("PARAM"));
            self._refreshGroupDetailManager(dat.get("MANAGER_INFO"));
            self._refreshGroupDetailProdList(dat.get("PROD_LIST"), 1, self.offerPageSize);
           
            
            self.setInfo(dat.toString());
            //$("#"+self.id).data("GROUP_DETAIL", dat);
            
            $("#groupList").removeClass('list-show');
            
            $("#groupLogin").css("display", "none");
            $("#groupLogoutFn").css("display", "none");
            
            $("#groupLoginFn").css("display", "");
            $("#groupCust").css("display", "");
            $("#groupSpInfo").css("display", "");

            if (subscriberId) {
            	self.selectProd(subscriberId);
			}
            
            $.enterpriseLogin.showHealthMore();
            // 刷新滚动
            window.groupDetailScroll.refresh();
			window.contractDetailScroll.refresh();
            window.healthDetailScroll.refresh();
            if (sucfn) {
            	sucfn.call(self, dat);
            }
        }, function (errCode, errDesc, errStack) {
            // faild
//        	$.endPageLoading();
        	
        	$("#groupListLoading").css("display", "none");
        	
        	$.MessageBox.error("错误提示", "查询集团信息失败！", "", null, errStack);	
        	if (errfn) {
        		errfn.call(self);
        	}
        });
    };
    
    o.isLogin = function() {
    	return this._getInfoText() ? true : false;
    };
    
    o.logout = function() {
    	$("#groupLogoutFn").css("display", "");
    	$("#groupLogin").css("display", "");
        
        $("#groupLoginFn").css("display", "none");
        $("#groupCust").css("display", "none");
        $("#saleRec").css("display", "none");
    	$("#groupBase").css("display", "none");
    	$("#groupSpInfo").css("display", "none");
    	this._setInfoText("");
    };
    
    o.prev = function(el) {
    	var countdiv = $("#groupList .page .count");
        
        if (countdiv.text()) {
        	var txt = countdiv.text().split("/");
			var current = parseInt(txt[0]);
			if (current - 1 > 0) {
				this.queryGroup(current - 1);
			}
		}
    };
    
    o.next = function(el) {
    	var countdiv = $("#groupList .page .count");
        var count = $("#groupList .page").data("count");
        var totalPage = Math.ceil(count/this.pageSize);
        
        if (countdiv.text()) {
        	var txt = countdiv.text().split("/");
			var current = parseInt(txt[0]);
			if (current + 1 <= totalPage) {
				this.queryGroup(current + 1);
			}
		}
    };
    
    o.prevOffer = function(el) {
    	var countdiv = $("#groupOrderedProduct .page .count");
        
        if (countdiv.text()) {
        	var txt = countdiv.text().split("/");
			var current = parseInt(txt[0]);
			if (current - 1 > 0) {
				this.queryOffers(current - 1);
			}
		}
    };
    
    o.nextOffer = function(el) {
    	var countdiv = $("#groupOrderedProduct .page .count");
        var count = $("#groupOrderedProduct .page").data("count");
        var totalPage = Math.ceil(count/this.offerPageSize);
        
        if (countdiv.text()) {
        	var txt = countdiv.text().split("/");
			var current = parseInt(txt[0]);
			if (current + 1 <= totalPage) {
				this.queryOffers(current + 1);
			}
		}
    };
    
    o.getInfo = function() {
    	var str = this._getInfoText();
    	var data = new Wade.DataMap(str ? str : "");
    	return data;
    };
    
    o.setInfo = function(data) {
    	if (data instanceof String) {
    		this._setInfoText(data);
		} else {
			this._setInfoText(data.toString());
		}
    };
    
    o.getQueryCode = function() {
    	return $("#groupQueryTypeButton .value").attr("code");
    };
    
    o.getQueryCodeType = function() {
    	return $("#groupQueryTypeButton .value").attr("codetype");
    };
    
    o.getQueryValue = function() {
    	return $("#groupQueryTypeValue input").val();
    };
    
    o._getInfoText = function() {
    	return $("#"+this.id+"Info").text();
    };
    
    o._setInfoText = function(text) {
    	$("#"+this.id+"Info").text(text);
    };
    
    o._refreshGroupCust = function(data) {
    	$("#groupCust .user").empty();
    	
        $("#groupCust .name").text(data.get("CUST_NAME"));
        $("#groupCust .num").text("[" + data.get("EPARCHY_CODE") + " / " + data.get("GROUP_ID") + "]");
        $("#groupCust .level").attr("class", "level level-"+data.get("LEVEL"));
        $("#groupCust .honor").text(data.get("CLASS_NAME"));
    };
    
    o._refreshSaleInfo = function(data) {
        var ul = $("#saleRec .content ul");
        ul.empty();
        
        if (data.length > 0) {
        	$("#saleRec").css("display", "block");
		}
        var html = '';
        for (var i = 0; i < data.length; i++) {
            var info = data.get(i);
            html += '<li>'+info.get("DESC")+'</li>';
        }
        ul.append(html);
    };
    
    o._refreshGroupDetailParam = function(data) {
        $("#contact .value").text(data.get("CONTACT"));
        $("#contactPhone .value").text(data.get("CONTACT_PHONE"));
        $("#custId .value").text(data.get("CUST_ID"));
        $("#groupType .value").text(data.get("GROUP_TYPE_NAME"));
        $("#callingType .value").text(data.get("CALLING_TYPE_NAME"));
        $("#address .value").text(data.get("ADDRESS"));
        $("#orgCode .value").text(data.get("ORG_CODE"));
        $("#enterpriseTypeCode .value").text(data.get("ENTERPRISE_TYPE_CODE"));
        $("#orgTypeA .value").text(data.get("ORG_TYPE_A"));
        $("#groupBase").css("display", "");
    };
    
    o._refreshGroupDetailManager = function(data) {
        $("#mgrName").text(data.get("NAME"));
        $("#mgrPhone").text(data.get("PHONE"));
        $("#mgrEmail").text(data.get("EMAIL"));
    };
    
    // 已订购产品列表
    o._refreshGroupDetailProdList = function(json,page,pageSize) {
        var ul = $("#groupOrderedProduct ul");
        ul.empty();
        
        var data = new Wade.DataMap(json + "");
    	if (!data || data.length <= 0) {
			return;
		}
    	// 数据
        var datas = data.get("DATAS");
        
        var _cutStr = function(str) {
        	if (str.length > 10) {
        		str = str.substring(0, 9) + "...";
        	}
        	return str;
        };
        
        var html = '';
        for (var i = 0; i < datas.length; i++) {
        	var info = datas.get(i);
        	html += '<li id="'+this.id+'_'+info.get("USER_ID")+'" title="'+info.get("SERIAL_NUMBER") + " / " +info.get("NAME")+'">';
        	html += '   <div class="main" ontap="'+this.id+'.selectProd('+info.get("USER_ID")+');">';
        	html += '       <span>'+info.get("SERIAL_NUMBER")+'</span>';
        	html += '       <span class="e_strong e_blue"> / </span>';
        	html += '       <span>'+_cutStr(info.get("NAME"))+'</span>';
        	html += '   </div>';
//        	html += '   <div class="fn" ontap="'+this.id+'.navTo360('+info.get("USER_ID")+');">详情</div>';
        	html += '</li>';
        }
        ul.append(html);
        
        // 分页信息
        var count = parseInt(data.get("COUNT"));
        var totalPage = Math.ceil(count/pageSize);
        $("#groupOrderedProduct .page .count").text(page+"/"+totalPage);
        $("#groupOrderedProduct .page").data("count", count);
        $("#OrderedOffer").text("已订购商品 ("+count+")");
        
        //集团有效在线用户信息 
        $("#healthButton .side span").text(count);

        
        
        groupDetailScroll.refresh();
    };
    
    o.isSelectedProd = function() {
    	if (this.getInfo().get("SEL_PROD")) {
			return true;
		}
    	return false;
    };
    
    o.selectProd = function(userid) {
    	var info = this.getInfo();
    	var list = info.get("PROD_LIST");
    	var selProd;
    	if(!list)
    	{
    		return;
    	}
    	if (!userid || userid=="null") {
			return;
		}
    	
    	list = list.get("DATAS");
    	
    	for (var i = 0,len = list.length; i < len; i++) {
			var prod = list.get(i);
			if (userid == prod.get("USER_ID")) {
				selProd = prod;
				prod.put("SUBSCRIBER_INS_ID", userid);
				prod.put("ACCESS_NUM", prod.get("SERIAL_NUMBER"));
				info.put("SEL_PROD", prod);
				this.setInfo(info);
				break;
			}
		}
    	if (selProd) {
    		// 刷新客户信息主界面
    		var html = '';
    		html += '<span class="label">商品：</span>';
    		html += '<span class="value">'+selProd.get("NAME")+'</span>';
    		html += '<div style="height: 0.05rem;"></div>';
    		html += '<span class="label">服务号码：</span>';
    		html += '<span class="value">'+selProd.get("SERIAL_NUMBER")+'</span>';
        	$("#groupCust .user").html(html);
        	
        	// 选择后的样式
        	$("#groupOrderedProduct li").removeClass("on");
        	$("#"+this.id+"_"+userid).addClass("on");
        	
        	this.refreshActiveNav4EcOfferOper();
		}
    };
    
    o.disSelectProd = function() {
    	var selProd = this.getInfo().get("SEL_PROD");
    	if (selProd && selProd.length > 0) {
    		this.getInfo().removeKey("SEL_PROD");
		}
    	$("#groupCust .user").empty();
    	$("#groupOrderedProduct li").removeClass("on");
    };
    
    /**
     * 打开tab页
     */
    o.openEcNavByUrl = function(title, url) {
//    	var preUrl = UrlUtil.getUrlPrefix(url);
//    	var urlParam = UrlUtil.getUrlParam(url);
//    	urlParam["ENTERPRISE_INFO"] = this.getEnterpriseUrlParam();
//    	var tailUrl = UrlUtil.urlEncode(urlParam);
//    	url = assembleUrl(preUrl, tailUrl);
    	
		url = UrlUtil.setUrlParamValue(url, "ENTERPRISE_INFO", this.getEnterpriseUrlParam());
		openNavByUrl(title, url);
	};
	
    /**
     * 刷新当前tab页
     */
    o.refreshActiveNav = function() {
		var activeTitle = getNavTitle();
		if (activeTitle) {
			var win = getNavContent();
			var url = win.location.href;
			this.openEcNavByUrl(activeTitle, url);
		} else {
			//$.navtabset.closeAll();
		}
	};
	
    /**
     * 刷新当前tab页(仅限集团商品受理)
     */
    o.refreshActiveNav4EcOfferOper = function() {
		var activeTitle = getNavTitle();
		if (activeTitle) {
			var win = getNavContent();
			var url = win.location.href;
			if(url.indexOf("oc.enterprise.cs.OperGroupUser") > -1 || url.indexOf("oc.enterprise.cs.OperGroupMember") > -1)
			{
				this.openEcNavByUrl(activeTitle, url);
			}
		} else {
			//$.navtabset.closeAll();
		}
	};
	
	/**
	 * 获取集团url参数
	 */
	o.getEnterpriseUrlParam = function() {
		var enterpriseInfo = $.DataMap();
		var groupId = this.getInfo().get("GROUP_ID");
		var custInfo = this.getInfo().get("CUST_INFO");
		
		if (groupId) {
			enterpriseInfo.put("GROUP_ID", groupId);
		}
		if (custInfo) {
			var data = $.DataMap();
			data.put("CUST_ID", custInfo.get("CUST_ID"));
			
			enterpriseInfo.put("CUST_INFO", data);
		}
		if (this.isSelectedProd()) {
			var selProd = this.getInfo().get("SEL_PROD");
			var data = $.DataMap();
			data.put("OFFER_ID", selProd.get("OFFER_ID"));
			data.put("SUBSCRIBER_INS_ID", selProd.get("SUBSCRIBER_INS_ID"));
			data.put("ACCESS_NUM", selProd.get("ACCESS_NUM"));
			
			enterpriseInfo.put("SEL_PROD", data);
		}
		var urlParam = enterpriseInfo.toString();
		return urlParam;
	};
    
    o.navTo360 = function(userid) {
    	// 选择
    	this.selectProd(userid);
    	// 跳转
    	openNav("集团产品360视图", "oc.enterprise.enterprisePro360view.EnterprisePro360View", 
    			"initPage", "&SUBSCRIBER_INS_ID="+ userid, "/order");
    };
    
    o.navToCust360 = function(el) {
    	var groupId = this.getInfo().get("GROUP_ID");    	
    	if(groupId)
    	{
    		openNav("集团客户360视图", "customer.vc.entqry.enterprise360view.Enterprise360ViewContent", 
    			"init", "&groupId="+ groupId, "/customercentre");
    	}else{
    	    openNav("集团客户360视图", "customer.vc.entqry.enterprise360view.Enterprise360ViewContent", 
    			"", "", "/customercentre");
    	}
    };
    
    o.navToMemberMgr = function(el) {
    	var groupId = this.getInfo().get("GROUP_ID");
    	if(groupId)
    	{
    		openNav("集团成员管理", "customer.cs.entmembermgr.membermgr.EnterpriseMemberManage", 
    			"init", "&groupId="+ groupId, "/customercentre");
    	}else{
    		openNav("集团成员管理", "customer.cs.entmembermgr.membermgr.EnterpriseMemberManage", 
    			"", "", "/customercentre");
    	}

    };
    
    o.navToNewGrp = function(el) {
    	openNav("集团客户新增", "customer.cs.entmgr.addenterprise.AddEnterprise", 
    			"init", "", "/customercentre");
    };
    
    o.navToGrpOfferOper = function(el) {
    	openNav("集团产品受理", "csserv.group.creategroupuser.GrpUseOpenFlowMain", "", "", "/order");
    };
    
    c._getGroupClass = function(classId) {
    	classId = classId ? classId : "";
    	var clazz = "";
    	if (classId.indexOf("A") >= 0) {
			clazz = " me_red";
		} else if (classId.indexOf("B") >= 0) {
			clazz = " me_orange";
		} else if (classId.indexOf("C") >= 0) {
			clazz = " me_blue";
		} else if (classId.indexOf("D") >= 0) {
			clazz = " me_green";
		}
    	return clazz;
    };
    
    o._qryGroup = function(code, codetype, value, page, pageSize) {
    	var self = this;
        var param = "";
        
        page = page ? page : 1;
        pageSize = pageSize ? pageSize : this.pageSize;
        
        param += "&ajaxListener=ajaxQuery";
        param += "&CODE="+code;
        param += "&CODE_TYPE="+codetype;
        param += "&VALUE="+value;
        param += "&grpListPage_current="+page;
        param += "&grpListPage_pagesize="+pageSize;
        param += "&pagin=grpListPage";
        
        // 先清空
        $("#groupList ul").empty();
        
//        $.beginPageLoading("查询中...");
        
        $("#loginBtn").attr("disabled", true);
        $("#loginBtn").text("认证中...");
        
        ajaxPost(null, null, param, "GroupLoginPart", function (json) {
//        	$.endPageLoading();
        	
        	$("#loginBtn").attr("disabled", false);
            $("#loginBtn").text("查询");
        	
        	var data = new Wade.DataMap(json + "");
        	if (!data || data.length <= 0) {
				return;
			}
        	// 数据
            var datas = data.get("DATAS");
            var html = "";
            for (var i = 0; i < datas.length; i++) {
                var dat = datas.get(i);
                var classId = dat.get("CLASS_ID");
                var className = dat.get("CLASS_NAME");

                html += '<li ontap="'+self.id+'.selectGroup(this);">';
                html += '	<div class="main" tip="'+dat.get("CUST_NAME")+'">';
                html += '		<div class="title">'+dat.get("CUST_NAME")+'</div>';
                html += '		<div class="content">'+dat.get("GROUP_ID")+'</div>';
                html += '		</div>';
                html += '	<div class="side'+c._getGroupClass(classId)+'">'+className+'类</div>';
                html += '</li>';
            }
            
            $("#groupList ul").empty();
            $("#groupList ul").append(html);
            
            $("#groupList").addClass('list-show');
            
            // 分页信息
            var count = parseInt(data.get("COUNT"));
            var totalPage = Math.ceil(count/pageSize);
            $("#groupList .page .count").text(page+"/"+totalPage);
            $("#groupList .page").data("count", count);
            $("#groupList .back").text("选择集团 ("+count+")");
        }, function (errCode, errDesc, errStack) {
            // faild
//        	$.endPageLoading();
        	$("#loginBtn").attr("disabled", false);
            $("#loginBtn").text("查询");
        	$.MessageBox.error("错误提示", "获取集团信息失败！", "", null, errStack);	
        });
    };
    
    o._qryOffer = function(custId, page, pageSize) {
    	var self = this;
        var param = "";
        
        page = page ? page : 1;
        pageSize = pageSize ? pageSize : this.offerPageSize;
        
        param += "&ajaxListener=queryEcOrderedOffers";
        param += "&CUST_ID="+custId;
        param += "&grpListPage_current="+page;
        param += "&grpListPage_pagesize="+pageSize;
        param += "&pagin=grpListPage";
        
        $.beginPageLoading("查询中...");
        ajaxPost(null, null, param, "GroupLoginPart", function (json) {
        	$.endPageLoading();
        	
        	var data = new Wade.DataMap(json + "");
        	
        	//更新info中的已订购商品列表
        	var prodList = data.get("PROD_LIST");
        	var info = self.getInfo();
        	info.put("PROD_LIST", prodList);
			self.setInfo(info);
        	self._refreshGroupDetailProdList(data.get("PROD_LIST"),page,pageSize);
        	
        }, function (errCode, errDesc, errStack) {
            // faild
        	$.endPageLoading();
        	$.MessageBox.error("错误提示", "获取集团订购商品列表失败！", "", null, errStack);	
        });
    };
    
    c.toggle = function(els) {
        for (var i = 0; i < arguments.length; i++) {
            var o;
            var el = arguments[i];
            typeof el == "object" ? o = el : o = document.getElementById(el);
            o.style.display == "none" ? o.style.display = "" : o.style.display = "none";
        };
    };
    
    c.ctrlFloatLayer = function(buttonId,layerId,preventLayer) {
        var layer = document.getElementById(layerId);
        $("#"+buttonId).tap(function(){
            layer.style.display == "" ? layer.style.display = "none" : layer.style.display = "";
        });
        if(layer){
	        $("body").tap(function(event){
	        	var isIn = (function () {
	                var tempDom = event.target;
	                if(tempDom){
	                    if(!preventLayer) {
	                        while(tempDom && tempDom != document.body && tempDom != document.getElementById(buttonId)) {
	                            tempDom = tempDom.parentNode;
	                        }
	                        
	                    } else {
	                        while(tempDom && tempDom != document.body && tempDom != document.getElementById(buttonId) && tempDom != layer) {
	                            tempDom = tempDom.parentNode;
	                        }
	                    }
	                    return tempDom == document.body;
	                }else{
	                	return false;
	                }
	            })();
	            if(isIn) { layer.style.display = 'none';}
	        });
        }
     }
    
    o._validate = function() {
    	var code = this.getQueryCode(),
        codetype = this.getQueryCodeType(),
        value = this.getQueryValue();
    	var qtli = $("#groupQueryTypeValue");
    	var input = qtli.find("input");
    	var text = qtli.find(".label").text();
    	
    	value = value.replace(/\s+/g, "");
    	
    	input.removeAttr("desc");
    	
        if (!value || "" == value) {
        	input.attr("desc", "查询内容");
		}
//        else if ("GROUP_ID" == code) {
//        	input.attr("desc", text);
//        	input.attr("datatype", "pinteger");
//        }
        else if ("MEB_ACCESS_NUM" == code) {
        	input.attr("desc", text);
        	input.attr("datatype", "pinteger");
        }
		
		return $.validate.verifyAll(this.id);
    };
    
    $(function(){
    	c.ctrlFloatLayer('groupQueryTypeButton','groupQueryType');
    	
    	$("#groupQueryType li").tap(function (){
            // 按钮高亮
            $("#groupQueryType li").removeClass("on");
            $(this).addClass("on");
            // 获取选取项
            var code = $(this).attr("code");
            var codetype = $(this).attr("codetype");
            var name = $(this).text();
            
            // 更新下拉框
            var btn = $("#groupQueryTypeButton .value");
            btn.attr("code", code);
            if (codetype) {
            	btn.attr("codetype", codetype);
    		}
            
            btn.text(name.length > 7 ? name.substring(0,7) + "..." : name);
            
            // 更新查询参数
            var qtli = $("#groupQueryTypeValue");
            if ("GROUP_ID" == code) {
            	qtli.find(".label").text("编码");
            }
            else if ("GROUP_NAME" == code) {
            	qtli.find(".label").text("名称");
            }
            else if ("ACCESS_NUM" == code) {
            	qtli.find(".label").text("服务号码");
            } 
            else if ("MEB_ACCESS_NUM" == code) {
            	qtli.find(".label").text("服务号码");
            }
            else if ("KEYMAN_SN" == code) {
            	qtli.find(".label").text("服务号码");
            }
            //qtli.find("input").val("");
        });
        $("#groupQueryTypeValue input").bind("keydown",function(e){
        	var key_enter = [13, 108];
        	if ($.inArray(e.keyCode, key_enter) >= 0) {
        		$.enterpriseLogin.queryGroup(1);
        		this.blur();
    		}
    	});
        
    });
    /**
     * 健康度
     */
    o.showHealthMore = function ()
    {
    	var custId = "";
    	var custInfo = this.getInfo().get("CUST_INFO");
        if (custInfo) {
			custId = custInfo.get("CUST_ID");
		}
        
    	var param="";
         param += "&CUST_ID="+custId;
         param += "&ajaxListener=queryGroupHealth";
    	ajaxPost(null, null,param, "GroupLoginPart",function(datas){
    		 var html = "";
             for (var i = 0; i < datas.length; i++) {
                 var dat = datas.get(i);
                 var name = dat.get("PRODUCT_NAME");
                 var deposit_balance = dat.get("DEPOSIT_BALANCE");
                 var isArrearage=false;
                 if(deposit_balance<0){
                	 isArrearage=true;
                 }

                 html += '<li>';
                 html += '	<div class="main">';
                 html += '		<div class="title e_size-s">'+name+'</div>';
                 html += '		<div class="content">余额：'+deposit_balance;
                 if(isArrearage){
                	 html +='<span class="e_red">（已欠费）</span>'    
                 }
                 html += '	 </div></div>';
                 if(isArrearage){
                	 html +='  <div class=side"><button type="button" class="e_button-sprite-blue e_button-m e_button-r"><span class="e_ico-play"></span><span>特殊开机</span></button></div>'
                 }
                 html += '</li>';
             }
             
             $("#healthList ul").empty();
             $("#healthList ul").append(html);
             window.healthDetailScroll.refresh();
    		
    	}, function (errCode, errDesc, errStack) {
        	$.MessageBox.error("错误提示", "查询集团信息失败！", "", null, errStack);
        });

    }    
    
    
    
    
})(EnterpriseLogin);


var UrlUtil = function(){};
(function(c){
	c.getUrlPrefix = function(url) {
    	var preUrl = url;
    	if (url && url.indexOf("?") >= 0) {
    		preUrl = url.substring(0, url.indexOf("?"));
    	}
    	return preUrl;
    };
    
    c.getUrlTail = function(url) {
    	var tailUrl = "";
    	if (url && url.indexOf("?") >= 0) {
    		tailUrl = url.substring(url.indexOf("?") + 1);
    	}
    	return tailUrl;
    };
    
    c.assembleUrl = function(prefix, tail) {
    	var url = prefix;
    	if (tail) {
    		url = prefix + "?" +tail;
    	}
    	return url;
    };
    
    c.getUrlParam = function(url) {
    	var tailUtl = c.getUrlTail(url);
    	var paraArr = tailUtl.split("&");
    	var param = {};
    	for (var i = 0; i < paraArr.length; i++) {
			var para = paraArr[i];
			var p = para.split("=");
			param[p[0]] = p[1];
		}
    	return param;
    };
    
    c.urlEncode = function(param, key, encode) {
        if (param == null) return '';
        var paramStr = '';
        var t = typeof (param);
        if (t == 'string' || t == 'number' || t == 'boolean') {
            paramStr += '&' + key + '=' + ((encode == null || encode) 
            		? encodeURIComponent(param) : param);
        } else {
            for (var i in param) {
                var k = key == null ? i : 
                	key + (param instanceof Array ? '[' + i + ']' : '.' + i);
                paramStr += urlEncode(param[i], k, encode);
            }
        }
        return paramStr;
    };
    
    c.setUrlParamValue = function(url, name, value) {
    	var preUrl = c.getUrlPrefix(url);
    	var tailUrl = c.getUrlTail(url);
    	
        var reg = eval('/'+ name +'=([^&]*)/gi');
        if (tailUrl.match(reg)) {
        	tailUrl = tailUrl.replace(reg, name+'='+value);
        	url = c.assembleUrl(preUrl, tailUrl);
        } else {
        	if (url.indexOf("?") < 0) {
        		url += "?";
            }
        	url += "&"+name+"="+value;
        }
        return url;
    };
    
    c.getUrlParamValue = function(url, name) {
    	var tailUrl = c.getUrlTail(url);
        var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
        var r = tailUrl.match(reg);
        if (r != null) return unescape(r[2]);
        return null;
    };
})(UrlUtil);

//显示集团详细信息
function showGrpDetail(layId)
{
	
	hideDeal();
	$("#"+layId).addClass('more more-show');
	$("#"+layId).css("display", "");
	$("#mainCover").css("display", "");
	
	
	
}
/**
 * 合同到期
 */
function showcontractMore()
{
	var layer = document.getElementById("contractDetail");
	var isIn = (function () {
		var tempDom = event.srcElement || event.target;
		while(tempDom != document.body && tempDom != layer) {
			tempDom = tempDom.parentNode;
		}
		if(tempDom != document.body) {
			return true;
		} else {
			return false;
		}
	})();
	if(isIn)
	{
		$("#contractDetail").removeClass('more-show');
		$("#mainCover").css("display", "none");
	}
	else
	{
		$("#contractDetail").addClass('more-show');
		
		$("#mainCover").css("display", "");
	}
}

/**
 * 隐藏明细信息
 */
function hideDeal()
{
	$("#groupDetail").removeClass('more more-show');
	$("#healthDetail").removeClass('more more-show');
	$("#contractDetail").removeClass('more more-show');
	$("#mainCover").css("display", "none");
	$("#groupDetail").css("display", "none");
	$("#healthDetail").css("display", "none");
	$("#contractDetail").css("display", "none");
}

