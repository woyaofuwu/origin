function setFrameHeight(frameName,height) {
	var frame = document.getElementById(frameName);
	if(frame){
		if(!height){
			height = frame.contentDocument.body.scrollHeight;	
		}
		frame.height= height;
    }
}

function getBusiId(){
	return getElementValue("busiId");
}

function setBusiId(busiId){
	getElement("busiId").value=busiId;
}

function setTradeBusiFee(feeMap){
   var frame = window.frames["FRAME_14100007"];
   if(frame){
      frame.TradeBusiFee.DealFee.insertFeeBySelf(feeMap);
   }
}

function deleteBusiFeeBySerialNumber(feeMap) {
	var frame = window.frames["FRAME_14100007"];
	if (frame) {
		frame.TradeBusiFee.DealFee.deleteFeeBySerialNumber(feeMap);
	}
}

function delTradeBusiFeeByElementId(feeMap){
   var frame = window.frames["FRAME_14100007"];
   if(frame){
      frame.deleteBusiFeeByElementId(feeMap);
   }
}

function deleteBusiFeeByAllFeeInfo(feeMap){
   var frame = window.frames["FRAME_14100007"];
   if(frame){
      frame.deleteBusiFeeByAllFeeInfo(feeMap);
   }
}

function deleteBusiFeeByFeeTypeCode(feeMap){
   var frame = window.frames["FRAME_14100007"];
   if(frame){
      frame.deleteBusiFeeByFeeTypeCode(feeMap);
   }
}

function deleteBusiFeeByFeeMode(feeMap){
   var frame = window.frames["FRAME_14100007"];
   if(frame){
      frame.deleteBusiFeeByFeeMode(feeMap);
   }
}

function gSoFrame_save(){
	debugger;
	if (window.confirm("系统将提交受理数据信息，你确认要继续吗？")) {
		func = function() {
           	resu=gSoFrame_submitAllPageData();
           	if (resu==false){
           		//BCEFrame_hideWaitingBanner();
           	}                   
        }
		window.setTimeout("func()", 10);
	}
}

function gSoFrame_submitAllPageData(){
	var objSubmitData =  gSoFrame_getAllSubmitPageData();
	if(objSubmitData){
		var msg = gSoFrame_PostDataToServer(objSubmitData.toXmlString());
		return msg;
	}	
}

function gSoFrame_getAllSubmitPageData() {
	var objSubmitData = new SubmitData();
	for (var k = 0; k < gPageIdList.length; k++) {
		var curFrameObj = document.getElementById("FRAME_" + gPageIdList[k]);
		if (!gBmFrame_checkPageValid(curFrameObj)) {
            return false; 
        }
		var objNodeInfo = objSubmitData.addNewNodeInfo(gPageIdList[k],curFrameObj.id, "");
		var objPageSets = g_SoFrame_objPageSetsArray[gPageIdList[k]];
        if (objPageSets) {
        	for (var i = 0; i < objPageSets.sets.length; i++) {
                var dataSetType = objPageSets.sets[i].dataSetType;
                var dataSetKey = objPageSets.sets[i].dataSetKey;
                var dataSetMethod = objPageSets.sets[i].dataSetMethod;
                if (dataSetType == "1"){
                	eval("var jsonStr=" + curFrameObj.id + "." + dataSetMethod);
                	objNodeInfo.addUserData(dataSetKey, jsonStr);
                }else if(dataSetType == "2"){
                	eval("var jsonStr=" + curFrameObj.id + "." + dataSetMethod);
                	objNodeInfo.addJSONObjectStr(dataSetKey, jsonStr);
                }else if(dataSetType == "3"){
                	eval("var jsonStr=" + curFrameObj.id + "." + dataSetMethod);
                	objNodeInfo.addJSONArrayStr(dataSetKey, jsonStr);
                }else if(dataSetType == "4"){
                	eval("var childSubmitData=" + curFrameObj.id + "." + dataSetMethod);
                	objNodeInfo.addSubmitData(childSubmitData);
                }
        	}
        }
	}
	objSubmitData.addUserData("BUSINESS_ID", getBusiId());
	objSubmitData.addUserData("IN_MODE_CODE", "0");
	return objSubmitData;
}

function gSoFrame_PostDataToServer(objSubmitData){
	var postParam = "POST_PARAM="+objSubmitData;
	Wade.ajax.ajaxPost(this, "saveContent", "&BUSINESS_ID="+getBusiId(), "", false, postParam, true,
			function t(){gSoFrame_AfterPostDataToServer();});
}

function gSoFrame_AfterPostDataToServer(){
	var param = "&ORDER_ID="+this.ajaxDataset.get(0,"ORDER_ID");
	param += "&BUSINESS_ID="+this.ajaxDataset.get(0,"BUSINESS_ID");
	var tradeId = "";
	for(var i = 0; i < this.ajaxDataset.length; i++){
		if(tradeId.length > 0){
			tradeId += ",";
		}
		tradeId += this.ajaxDataset.get(i,"TRADE_ID");
	}
	param += "&TRADE_ID="+tradeId;
	popupModalDialog('TTEasyMessageBox', 'initEasyPrintData', param, '成功信息', '650', '355');
	redirectTo('bmframe.bmframepage.BmFramePage', '', '&BUSINESS_ID='+getElementValue('busiId'));
	//closeNav();
}