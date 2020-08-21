(function($){
	$.extend({BmFrame:{
		gPageIdList:new $.DatasetList(),  //页面集
	    gPageDataSetsMap:new $.DataMap(), //页面数据集
	    gBmFrame_init:function(){
		    $("#CSSUBMIT_BUTTON").bind("click", $.BmFrame.gBmFrame_save);
		    $("#CSRESET_BUTTON").bind("click", $.BmFrame.gBmFrame_save);
	    },
		gBmFrame_save:function(){
		    if(window.confirm("系统将提交受理数据信息，你确认要继续吗？")){
		    	$.BmFrame.gBmFrame_submitAllPageData();
		    }
	    }, 
	    gBmFrame_submitAllPageData:function(){
	    	var objSubmitData =  $.BmFrame.gBmFrame_getAllSubmitPageData();
	    	if(objSubmitData){
	    		$.BmFrame.gBmFrame_PostDataToServer(objSubmitData.toXmlString());
	    	}
	    },
	    gBmFrame_getAllSubmitPageData:function(){
	    	debugger;
	    	var objSubmitData = new $.BmFrame.SubmitData();
	    	for (var k = 0; k < this.gPageIdList.length; k++) {
	    		var pageInstId = this.gPageIdList.get(k,"PAGESET_PAGE_ID");
	    		var pageId = this.gPageIdList.get(k,"PAGE_ID");
	    		var curFrameObj = $("#"+pageInstId);
	    		var curFrameWindow = "curFrameObj[0].contentWindow";
	    		if (!$.BmFrame.gBmFrame_checkPageValid(curFrameObj)) {
	    			return false; 
	            }
	    		var objNodeInfo = objSubmitData.addNewNodeInfo(pageInstId,pageId);
	    		var objPageSets = this.gPageDataSetsMap.get(pageInstId); 
	            if (objPageSets) {
	            	for (var i = 0; i < objPageSets.length; i++) {
	                    var dataSetType = objPageSets.get(i,"DATASET_TYPE");
	                    var dataSetKey = objPageSets.get(i,"DATASET_KEY");
	                    var dataSetMethod = objPageSets.get(i,"DATASET_METHOD");
	                    if (dataSetType == "1"){
	                    	eval("var jsonStr=" + curFrameWindow + "." + dataSetMethod);
	                    	objNodeInfo.addAttrData(dataSetKey, jsonStr);
	                    }else if(dataSetType == "2"){
	                    	eval("var jsonStr=" + curFrameWindow + "." + dataSetMethod);
	                    	objNodeInfo.addJSONObjectStr(dataSetKey, jsonStr);
	                    }else if(dataSetType == "3"){
	                    	eval("var jsonStr=" + curFrameWindow + "." + dataSetMethod);
	                    	objNodeInfo.addJSONArrayStr(dataSetKey, jsonStr);
	                    }else if(dataSetType == "4"){
	                    	eval("var childSubmitData=" + curFrameWindow + "." + dataSetMethod);
	                    	objNodeInfo.addSubmitData(childSubmitData);
	                    }
	            	}
	            }
	    	}
	    	objSubmitData.addAttrData("FRAME_ID", $("#frameId").val());
	    	objSubmitData.addAttrData("IN_MODE_CODE", "0");
	    	return objSubmitData;
	    },
	    gBmFrame_PostDataToServer:function(objSubmitData){
	    	$.beginPageLoading("业务受理中。。。");
	    	var postParam = "&POST_PARAM="+objSubmitData;
	    	ajaxSubmit('', "saveContent", postParam, "", $.BmFrame.gBmFrame_SuccessFunc,
	    			$.BmFrame.gBmFrame_ErrorFunc,$.BmFrame.gBmFrame_TimeoutFunc);
	    },
	    gBmFrame_SuccessFunc:function(data){
	    	$.endPageLoading();
	    	MessageBox.success("成功提示","业务受理成功!");
	    },
        gBmFrame_ErrorFunc:function(code, info, detail){
	    	$.endPageLoading();
	    	MessageBox.error("错误提示","业务受理失败！", null, null, info, detail);	
	    },
        gBmFrame_TimeoutFunc:function(){
	    	$.endPageLoading();
	    	MessageBox.alert("告警提示", "业务提交超时！");
	    },
	    gBmFrame_checkPageValid:function(curFrameObj){
	    	var curFrameWindow = "curFrameObj[0].contentWindow";
	    	eval("var pluginFunc = " + curFrameWindow + ".pageValidatePlugIn;");
      	    if (pluginFunc) {
	            if (pluginFunc() == false){
	            	return false;
	            }        
	        }
      	    eval("var validFunc = " + curFrameWindow + ".pageValidate;");
	        if (validFunc) {
	            if (validFunc() == false){
	            	return false;
	            }         
	        }
	        return true;
	    },
	    popupModalDialog:function(){
	    	
	    }
	}});
	$.BmFrame.SubmitData=function(){
		this.nodeInfoArray = new Array();
		this.nodeInfoHash = new Array();
		this.attrData = new $.BmFrame.AttrData();
	};
	$.extend($.BmFrame.SubmitData.prototype,{
		addNewNodeInfo:function(pInstId,pId){
		    var nodeInfo = new $.BmFrame.NodeInfo(pInstId,pId);
		    this.addNodeInfo(nodeInfo);
		    return nodeInfo;
	    },
		addNodeInfo:function(pNodeInfo){
	    	var index = this.nodeInfoArray.length;
	    	this.nodeInfoArray[index] = pNodeInfo; 
	    },
	    addAttrData:function(pName,pValue){
	    	this.attrData.addAttrData(pName,pValue);
	    },
	    setAttrData:function(pAttrData){
	    	this.attrData = pAttrData;
	    },
	    toXmlString:function(){
	    	var tmpArray = new Array();
	    	tmpArray[tmpArray.length] = this.attrData.toXmlString();
	    	if(this.nodeInfoArray.length>0){
	    		for(var i=0;i<this.nodeInfoArray.length;i++){      
	    		   tmpArray[tmpArray.length] = this.nodeInfoArray[i].toXmlString();  
	    		}   
	    	}
	    	var headTmpArray = new Array();
	    	headTmpArray[headTmpArray.length] = "<submitdata>";
    		headTmpArray[headTmpArray.length]=tmpArray.join("");
    		headTmpArray[headTmpArray.length] = "</submitdata>";
	    	return headTmpArray.join("");
	    }
	});
	$.BmFrame.NodeInfo=function(pInstId,pId){
		this.instId = pInstId;
		this.id = pId;
		this.attrData = new $.BmFrame.AttrData();
		this.submitDataArray = new Array();
		this.jsonObjectStrArray = new Array();
		this.jsonArrayStrArray = new Array();
	};
	$.extend($.BmFrame.NodeInfo.prototype,{
		addJSONObjectStr:function(pJsonName,pJsonStr){
		    var index = this.jsonObjectStrArray.length;
		    this.jsonObjectStrArray[index] = this.wrapJsonObjectStr(pJsonName,pJsonStr);
	    },
	    addJSONArrayStr:function(pJsonName,pJsonStr){
	    	var index = this.jsonArrayStrArray.length;
		    this.jsonArrayStrArray[index] = this.wrapJsonArrayStr(pJsonName,pJsonStr);
	    },
	    addAttrData:function(pName,pValue){
	    	this.attrData.addAttrData(pName,pValue);
	    },
	    addSubmitData:function(pSubmitData){
	    	var index = this.submitDataArray.length;
	    	this.submitDataArray[index] = pSubmitData;  
	    },
	    wrapJsonObjectStr:function(pName,pJsonStr){
	    	var tmpArray = new Array();
	    	tmpArray[tmpArray.length] = "<jsonobject name='";
	    	tmpArray[tmpArray.length] = pName;
	    	tmpArray[tmpArray.length] ="'>";
	    	tmpArray[tmpArray.length] = pJsonStr;
	    	tmpArray[tmpArray.length] = "</jsonobject>";
	    	return tmpArray.join("");
	    },
	    wrapJsonArrayStr:function(pName,pJsonStr){
	    	var tmpArray = new Array();
	    	tmpArray[tmpArray.length] = "<jsonarray name='";
	    	tmpArray[tmpArray.length] = pName;
	    	tmpArray[tmpArray.length] ="'>";
	    	tmpArray[tmpArray.length] = pJsonStr;
	    	tmpArray[tmpArray.length] = "</jsonarray>";
	    	return tmpArray.join("");
	    },
	    toXmlString:function(){
	    	var tmpArray = new Array();
	    	tmpArray[tmpArray.length] = this.attrData.toXmlString();
	    	for(var i=0;i<this.jsonObjectStrArray.length;i++){
	    	    tmpArray[tmpArray.length] = this.jsonObjectStrArray[i];  
	    	}
	    	for(var i=0;i<this.jsonArrayStrArray.length;i++){
	    	    tmpArray[tmpArray.length] = this.jsonArrayStrArray[i];  
	    	}
	    	for(var i=0;i<this.submitDataArray.length;i++){
	    	    tmpArray[tmpArray.length] = this.submitDataArray[i].toXmlString();
	    	}
	    	var reVal = tmpArray.join("");
	    	return  "<nodeinfo instid='"+this.instId+"' id='"+this.id+"'>"+reVal+"</nodeinfo>";
	    }
	});
    $.BmFrame.AttrData=function(data){
    	if(typeof(data)!="undefined" && data instanceof Wade.DataMap){ 
    		this.valueMap = data;
    	}else{
    		this.valueMap = new $.DataMap();
    	}
	};
	$.extend($.BmFrame.AttrData.prototype,{
		addAttrData:function(pName,pVal){
		    this.valueMap.put(pName,pVal);
	    },
	    toXmlString:function(){
	    	return "<ud>" + this.valueMap.toString() + "</ud>";
	    }
	});
	$($.BmFrame.gBmFrame_init);  //页面加载完成之后才执行
})(Wade);