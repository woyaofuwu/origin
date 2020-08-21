(function($){
  $.multioffer = {
	clazz: "com.ailk.personview.multioffer.MultiOfferHandler",
	init:function(){
	    debugger;
	    $.multioffer.rowStateInit(); //初始化行状态
	    var prodAttrTable = $.tableManager.get("prodAttrTable");
	    for(var i = 0; i < prodAttrTable.count(); i++){
	    	var contextMenuStr = prodAttrTable.getValue(i,"CONTEXT_MENU");
	    	if(!contextMenuStr || contextMenuStr == ""){
	    		continue;
	    	}
	    	var needContextMenu = false;
	    	var tmpRow = prodAttrTable.getRowObj(i);
	    	var trEleId = $(tmpRow).attr("id");
	    	var contextMenuList = new $.DatasetList(contextMenuStr);
	    	for(var j = 0; j < contextMenuList.length; j++){
	    		var contextMenu = contextMenuList.get(j);
	    		var menuGroup = contextMenu.get("MENU_GROUP");
	    		var menuName = contextMenu.get("MENU_NAME");
	    		var menuFn = contextMenu.get("MENU_FN");
	    		var menuParam = contextMenu.get("MENU_PARAM");
	    		//$.addContextMenu(menuGroup,menuName,eval(menuFn),trEleId);
	    		$.addContextMenu(menuGroup,menuName,(function(menuFn,menuParam){
	    			return function(e){debugger;eval(menuFn+"("+menuParam+");"); };
	    		})(menuFn,menuParam),trEleId);
	    		needContextMenu = true;
	    		//$.addContextMenu(menuName,"function(e){"+menuFn+"(e);}");
	    	}
	    	if(needContextMenu){
	    		$("#"+trEleId).contextmenu(function(e){
		    		debugger;
		    		prodAttrTable.addContextMenu(trEleId,e);
		    		$.showContextMenu(e);
		    		return false;
		    	});
	    	}
	    }
	    prodAttrTable.setAllSelectCheckBoxSts(false); //禁用全选按钮
    },
    rowStateInit:function(){
    	var prodAttrTable = $.tableManager.get("prodAttrTable");
    	for(var i = 0; i < prodAttrTable.count(); i++){
    		var rowStatusStr= prodAttrTable.getValue(i,"ROW_SPE_STATUS");
    		if(!rowStatusStr){continue;}
    		debugger;
    		var statusItems= rowStatusStr.split(';');
    		for(var j=0;j<statusItems.length;j++){
    			var statusItem= statusItems[j].split('=');
    			if(statusItem[0]=='checked'){
    				if(statusItem[1]=='true'){
    					prodAttrTable.rowSelected(i,true);
    				}
    			}
    			if(statusItem[0]=='enabled'){
    				if(statusItem[1]=='false'){
    					prodAttrTable.disabledSelect(i,true);
    				}
    			}
    			if(statusItem[0]=='displayed'){
    				if(statusItem[1]=='false'){
    					prodAttrTable.visibleSelect(i,false);
    				}
    			}
    			if(statusItem[0]=='isMainRole'){
    				if(statusItem[1]=='false'){
    					
    				}
    			}
    			if(statusItem[0]=='isMgrRole'){
    				if(statusItem[1]=='false'){
    					
    				}
    			}
    			if(statusItem[0]=='visible'){
    				if(statusItem[1]=='false'){
    					prodAttrTable.setRowHidden(i,true);
    				}
    			}
    		}
    	}
    },
    checkMaxLimit:function(rowIndex){
    	var maxCount = $.multioffer.getMaxMemCount(rowIndex);
    	if(maxCount != -1){
    		var childCount = $.multioffer.getValidChildCount(rowIndex);
    		if(childCount >= maxCount){
    			alert("成员已经达到最大限制，不能再添加成员!");
    			return false;
    		}
    	}
    	return true;
    },
    getValidChildCount:function(rowIndex){
    	var childCount = 0;
    	var prodAttrTable = $.tableManager.get("prodAttrTable");
    	var childList = prodAttrTable.getChildList(rowIndex);
    	for(var i = 0; i < childList.length; i++){
    		var isSelected = prodAttrTable.isSelected(childList[i]);
    		var sts = prodAttrTable.getRowSts(childList[i]);
    		if(isSelected && sts != "D"){  //排除已经删除的
    			childCount++;
    		}
        }
    	return childCount;
    },
    getMaxMemCount:function(rowIndex){
    	var maxCount = -1;
    	var prodAttrTable = $.tableManager.get("prodAttrTable");
    	var ordCount = prodAttrTable.getValue(rowIndex,"ORD_COUNT");
    	if(ordCount && $.isString(ordCount)){
    		ordCount = new $.DataMap(ordCount);
    		maxCount = ordCount.get("MAX_MEN_COUNT");	
    		if(maxCount){
    			maxCount = parseInt(maxCount);
    		}
    	}
    	return maxCount;
    },
    getMinMemCount:function(rowIndex){
    	var minCount = -1;
    	var prodAttrTable = $.tableManager.get("prodAttrTable");
    	var ordCount = prodAttrTable.getValue(rowIndex,"ORD_COUNT");
    	if(ordCount && $.isString(ordCount)){
    		ordCount = new $.DataMap(ordCount);
    		minCount = ordCount.get("MIN_MEN_COUNT");	
    		if(minCount){
    			minCount = parseInt(minCount);
    		}
    	}
    	return minCount;
    },
    addBusiPage:function(pageId,pageTitle,menuParam){
    	$.beginPageLoading('Loading.....');
    	$.httphandler.post($.multioffer.clazz, "getPageInfo", "&PAGE_ID="+pageId, 
			function(data){
				$.endPageLoading();
				$.multioffer.addPanelFrame(data,pageId,pageTitle,menuParam);
			},function(code, info, detail){
				$.endPageLoading();
				MessageBox.error("错误提示","获取页面信息出错！",null, null, info, detail);
			},function(){
				$.endPageLoading();
				MessageBox.alert("告警提示", "获取页面信息超时");
		});
    },
    addPanelFrame:function(page,pageId,pageTitle,menuParam){
        var pageName = page.get("PAGE_NAME");  
        var listener = page.get("LISTENER");
        var subSys = page.get("SUB_SYS");
        var dataset = page.get("DATASET");
        var params  = $.multioffer.buildParams(menuParam);
        var panel = $.panel.get("mypanel");
    	panel.addPanelFrame(pageTitle,0,pageName,listener,params,subSys,dataset);
    },
    needSelProduct:function(menuParam){
    	var needSelProd = false;
    	var param = $.multioffer.buildParams(menuParam);
    	$.beginPageLoading('Loading.....');
    	$.httphandler.post($.multioffer.clazz, "needSelProduct", param, 
			function(data){
				$.endPageLoading();
				needSelProd = data.get("NEED_SEL_PROD");
			},function(code, info, detail){
				$.endPageLoading();
				MessageBox.error("错误提示","判断是否需要选择产品出错！",null, null, info, detail);
			},{async:false});
    	return needSelProd;
    },
    addTableRow:function(id,parentId,type,name,busiName,effDate,expDate,titleArray){
    	var prodAttrTable = $.tableManager.get("prodAttrTable");
    	var parentIndex = prodAttrTable.getCurRowIndex();
    	var newRowIndex = $.multioffer.getNewRowIndex(prodAttrTable,parentIndex);
    	var newIndex = prodAttrTable.insertRow(newRowIndex,parentIndex);
    	prodAttrTable.rowSelected(newIndex,true);//默认勾选上
    	prodAttrTable.setValue(newIndex,"ITEM_ID",id);
    	prodAttrTable.setValue(newIndex,"PARENT_ITEM_ID",parentId);
    	prodAttrTable.setValue(newIndex,"ITEM_TYPE",type);
    	prodAttrTable.setValue(newIndex,"ITEM_NAME",name);
    	prodAttrTable.setValue(newIndex,"BUSINESS_NAME",busiName);
    	prodAttrTable.setValue(newIndex,"EFFECTIVE_DATE",effDate);
    	prodAttrTable.setValue(newIndex,"EXPIRE_DATE",expDate);
		var rowObj = prodAttrTable.getRowObj(newIndex);
		$(rowObj).data("title",titleArray);//当前行上存储页面标题
		return newIndex;
    },
    getNewRowIndex:function(prodAttrTable,parentIndex){
    	var lastChildIndex = prodAttrTable.getLastChildIndexOfRow(parentIndex);
    	var newRowIndex = 0;
    	if(lastChildIndex == -1){
    		return parentIndex + 1;
    	}else{
    		return lastChildIndex + 1;
    	}
    },
    buildParams:function(menuParam){
    	var params = "";
    	var paramsArray=[];
    	for(var p in menuParam){
    		if(menuParam[p]&&$.isString(menuParam[p])){
				paramsArray.push("&");
				paramsArray.push(p);
				paramsArray.push("=");
				paramsArray.push(menuParam[p]);
			}
    	}
    	if(paramsArray.length>0){
    		params=paramsArray.join("");
		}
    	return params;
    },
    mobileuser:{
    	addMobileMember:function(menuParam){
	    	var params  = $.multioffer.buildParams(menuParam);
	        var returnData = $.popupDialog("multioffer.SelMember","",params,menuParam,800,400,"personserv");
	        if(returnData){
	        	var titleArray = [];
	        	var serialNumber = returnData["SERIAL_NUMBER"];
	        	var pageId = "20000005";
	        	var pageTitle = "手机成员【"+serialNumber+"】加入-订购附属";
	        	titleArray.push(pageTitle);
	        	$.multioffer.addBusiPage(pageId,pageTitle,menuParam); //加载成员添加页面
	        	var newProductId = returnData["NEW_PRODUCT_ID"];
	        	if(newProductId){
	        		pageId = "20000006";
	            	pageTitle = "手机成员【"+serialNumber+"】更换基本产品";
	            	titleArray.push(pageTitle);
	            	$.multioffer.addBusiPage(pageId,pageTitle,menuParam); //加载成员添加页面
	        	}
	        }
        },
        createMobileUser:function(menuParam){
        	
        }
    },
    wideuser:{
    	addWideMember:function(menuParam){
	    	var params  = $.multioffer.buildParams(menuParam);
	        var returnData = $.popupDialog("multioffer.SelMember","",params,menuParam,800,400,"personserv");
	        if(returnData){
	        	var titleArray = [];
	        	var serialNumber = returnData["SERIAL_NUMBER"];
	        	var pageId = "20000005";
	        	var pageTitle = "宽带成员【"+serialNumber+"】加入-订购附属";
	        	titleArray.push(pageTitle);
	        	$.multioffer.addBusiPage(pageId,pageTitle,menuParam); //加载成员添加页面
	        	var newProductId = returnData["NEW_PRODUCT_ID"];
	        	if(newProductId){
	        		pageId = "20000007";
	            	pageTitle = "宽带成员【"+serialNumber+"】更换基本产品";
	            	titleArray.push(pageTitle);
	            	$.multioffer.addBusiPage(pageId,pageTitle,menuParam); //加载成员添加页面
	        	}
	        }
        },
        createWideUser:function(menuParam){
	    	var titleArray = [];
	    	var needSelProd = $.multioffer.needSelProduct(menuParam);
	    	if(needSelProd){ //是否需要弹出产品选择框
	    		var params  = $.multioffer.buildParams(menuParam);
	            var returnData = $.popupDialog("multioffer.SelProduct","",params,menuParam,800,400,"personserv");
	    	}
	    	var pageId = "20000005";
	    	var pageTitle = "宽带成员【"+serialNumber+"】加入-订购附属";
	    	titleArray.push(pageTitle);
	    	$.multioffer.addBusiPage(pageId,pageTitle,menuParam); //加载新装宽带成员订购附属页面
	    }
    },
    virtualuser:{
    	createVirtualUser:function(menuParam){
    	    var prodAttrTable = $.tableManager.get("prodAttrTable");
    	    var parentIndex = prodAttrTable.getCurRowIndex();
    	    if($.multioffer.checkMaxLimit(parentIndex)){
    	    	var pageId = "20000004";
    	    	var pageTitle = "虚拟用户-创建虚拟网";
    	    	$.multioffer.addBusiPage(pageId,pageTitle,menuParam); //加载页面
    	    	var titleArray = [pageTitle];
    	    	var rowIndex = $.multioffer.addTableRow("","","成员","虚拟用户","创建虚拟网","","",titleArray);
    	    	var prodAttrTable = $.tableManager.get("prodAttrTable");
    	    	prodAttrTable.disabledSelect(rowIndex,true); //禁用
    	    }
        }
    }
  };	
  
})(Wade);