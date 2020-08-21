function validateParamPage(methodName) {
	if(methodName=='CrtUs')
	{
		try
		{
			initGoodsParamTable();
			if(!onProductClick()){return false;};
			saveUserParamDataset();
		}
		catch(error){}
	}
	if(methodName=='ChgUs')
	{
		try
		{
			initGoodsParamTable();
			onProductClick();
			saveUserParamDataset();
			
			var productInfo = new Wade.DataMap(getElementValue("productInfo"));
			productInfo.put("PRODUCT_OPER_TYPE",getElementValue("productOperType"));
			getElement("productInfo").value = productInfo;
		}
		catch(error){}
		if(getElement('operType').value && getElement('productOperType').value)
		{
			return window.confirm("\u662F\u5426"+getElement('operType').options[getElement('operType').selectedIndex].text+"?");
		}
		else
		{
			alert("\u8BF7\u9009\u62E9\u5546\u54C1\u548C\u4EA7\u54C1\u7684\u64CD\u4F5C\u7C7B\u578B\uFF01");
			return false;
		}
	}
	if(methodName=='CrtMb')
	{
		try
		{
			//onMemberProductInfoClick();
			memberParamNext();
			/*  设置成员类型  */  
			if(getElementValue("SELECTED_PRODUCT_INFO"))
			{
				var productInfo = new Wade.DataMap(getElementValue("SELECTED_PRODUCT_INFO"));
				productInfo.put("USER_TYPE",getElementValue("userType"));
				getElement("SELECTED_PRODUCT_INFO").value = productInfo;
			}
		}
		catch(error)
		{
			alert("\u8BF7\u586B\u5199\u4EA7\u54C1\u53C2\u6570\u4FE1\u606F\uFF01");
			return false;
		}
		if(getElementValue('SELECTED_PRODUCT_INFO'))
		{
			var productInfo = new Wade.DataMap(getElementValue("SELECTED_PRODUCT_INFO"));
			if(!productInfo)
			{
				alert("\u8BF7\u9009\u62E9\u4EA7\u54C1");
				return false;
			}
		}
		else
		{
			alert("\u8BF7\u9009\u62E9\u4EA7\u54C1");
			return false;
		}
	}
	if(methodName=='ChgMb')
	{
		try
		{
			var inputs = getChildsByRecursion('input_param_div', "input", "type", "text");
			for(var i=0;i<inputs.length;i++)
			{
				inputs[i].nullable="";
			}
			var selects = getChildsByRecursion('input_param_div', "select", "", "");
			for(var i=0;i<selects.length;i++)
			{
				selects[i].nullable="";
			}
		}catch(error){}
	}
	return true;
}

function compareDataset(strNewValue,strOldValue,strKey){
    if (strOldValue == "")
        strOldValue="[]";
    var oldValueSet = new Wade.DatasetList(strOldValue);
    var newValueSet = new Wade.DatasetList(strNewValue);
    var resultSet = new Wade.DatasetList();
    var isfound = "false";
    
    var ss = strKey.split(",");
    
    for (var i=0;i<newValueSet.length;i++) {
        isfound = "false";
        var newValueColumn = newValueSet.get(i);
        
        for(var k=0;k<ss.length;k++)
        {
	        for (var j=0;j<oldValueSet.length;j++) {
	            var oldValueColumn = oldValueSet.get(j);
	            if (oldValueColumn.get(ss[k]) == newValueColumn.get(ss[k])) {
	                newValueColumn.put("STATE","EXIST");
	                isfound = "true";
	                break;
	            }
	        }
	    }
        if (isfound == "false")
            newValueColumn.put("STATE","ADD");
        resultSet.add(newValueColumn);
    }

    for (var i=0;i<oldValueSet.length;i++) {
        isfound = "false";
        var oldValueColumn = oldValueSet.get(i);
        for(var k=0;k<ss.length;k++)
        {
	        for (var j=0;j<newValueSet.length;j++) {
	            var newValueColumn = newValueSet.get(j);
	            if (oldValueColumn.get(ss[k]) == newValueColumn.get(ss[k])) {
	                isfound = "true";
	                break;
	            }
	        }
	    }
        if (isfound == "false") {
            oldValueColumn.put("STATE","DEL");
            resultSet.add(oldValueColumn);
        }
    }
    
    return resultSet.toString();
}

function onProductClick(e) {
	var ds = new Wade.DatasetList();
	var tmp;
	var chks = getChildsByRecursion('productTable', 'input', 'type', 'checkbox');
	if (chks.length>0) {
	    for(var i=0; i<chks.length; i++) {
		    var chk = chks[i];
		    var chkdata = productTableEdit.getRowData(i+1,"X_TAG,PRODUCT_DISCNT_CODE,PRODUCT_DISC_DESC");
		    if (chk.checked) {
			    ds.add(chkdata);
		    }
	    }
	    tmp = "" + ds;
	}
	
	if(ds.length<1)
	{
		alert("\u8BF7\u9009\u62E9\u4EA7\u54C1\u8D44\u8D39\uFF01");
		return false;
	}
	
    document.getElementById("SELECTED_PRODUCT_DISCNT").value=compareDataset(tmp,getElementValue("OLD_PRODUCT_DISCNT"),"PRODUCT_DISCNT_CODE");
    return true;
}

function onProductInfoClick(e)
{
	var ds = new Wade.DatasetList();
	var tmp;
	var chks = getChildsByRecursion('productInfoTable', 'input', 'type', 'checkbox');
	if (chks.length>0) {
	    for(var i=0; i<chks.length; i++) {
		    var chk = chks[i];
		    var chkdata = productInfoTableEdit.getRowData(i+1,"X_TAG,PRODUCTSPECNAME,PRODUCT_SPEC_CODE");
		    if (chk.checked) {
		    	ds.add(chkdata);
		    }
	    }
	    tmp = "" + ds;
	}
	
    document.getElementById("SELECTED_PRODUCT_INFO").value=compareDataset(tmp,getElementValue("OLD_PRODUCT_INFO"),"PRODUCT_SPEC_CODE");
}
 
 function onMemberProductInfoClick(e)
 {
 	var ds = new Wade.DatasetList();
 	var tmp;
 	var chks = getChildsByRecursion('productInfoTable', 'input', 'type', 'checkbox');
 	if (chks.length>0) {
 	    for(var i=0; i<chks.length; i++) {
 		    var chk = chks[i];
 		    var chkdata = productInfoTableEdit.getRowData(i+1,"X_TAG,PRODUCTSPECNAME,PRODUCT_SPEC_CODE,PRODUCT_OFFER_ID");
 		    if (chk.checked) {
 		    	ds.add(chkdata);
 		    }
 	    }
 	    tmp = "" + ds;
 	}
 	
     document.getElementById("SELECTED_PRODUCT_INFO").value=compareDataset(tmp,getElementValue("OLD_PRODUCT_INFO"),"PRODUCT_SPEC_CODE");
     //alert(document.getElementById("SELECTED_PRODUCT_INFO").value);
 }

function onMemberParamClick(e)
{
	var ds = new Wade.DatasetList();
	var tmp;
	var chks = getChildsByRecursion('memberParamsTable', 'input', 'type', 'checkbox');
	if (chks.length>0) {
	    for(var i=0; i<chks.length; i++) {
		    var chk = chks[i];
		    var chkdata = productInfoTableEdit.getRowData(i+1,"X_TAG,PARAM_ATTR,PARAM_CODE,PARAM_NAME");
		    if (chk.checked) {
		    	ds.add(chkdata);
		    }
	    }
	    tmp = "" + ds;
	}
	
    document.getElementById("memberParams").value=compareDataset(tmp,getElementValue("canSelectParams"),"PARAM_ATTR,PARAM_CODE");
}


function onGoodsClick(e) {
	var ds = new Wade.DatasetList();
	var tmp;
	var chks = getChildsByRecursion('goodsTable', 'input', 'type', 'checkbox');
	if (chks.length>0) {
	    for(var i=0; i<chks.length; i++) {
		    var chk = chks[i];
		    var chkdata = goodsTableEdit.getRowData(i+1,"X_TAG,MERCH_DISCNT_CODE,GOODS_DISC_DESC");
		    if (chk.checked) {
		    	ds.add(chkdata);
		    }
	    }
	    tmp = "" + ds;
	}
	document.getElementById("SELECTED_GOODS_DISCNT").value=compareDataset(tmp,getElementValue("OLD_GOODS_DISCNT"),"MERCH_DISCNT_CODE");
	//alert(ds);
	//alert(document.getElementById("SELECTED_GOODS_DISCNT").value);
}

function createUserParamTabset(){
	if(typeof(window["leftgoodstabsetobj"])!="object"){
		window["leftgoodstabsetobj"] = new TabSet("leftgoodstabset", "top");
		window["leftgoodstabsetobj"].addTab("\u5546\u54C1\u8D44\u8D39\u4FE1\u606F", getElement("goodsInfos"));
		window["leftgoodstabsetobj"].addTab("\u4EA7\u54C1\u8D44\u8D39\u4FE1\u606F", getElement("goodsProducts"));
		window["leftgoodstabsetobj"].addTab("\u8BA2\u8D2D\u4FE1\u606F", getElement("goodsParamPart"));	
		window["leftgoodstabsetobj"].draw();
	}
}


function initCreateMemberParamTable()
{
	productInfoTableEdit = new TableEdit("productInfoTable",false,null);
	document.getElementById("SELECTED_PRODUCT_INFO").value="";
}

function initCreateMemberParam()
{
	if(typeof(window["leftgoodstabsetobj"])!="object"){
		window["leftgoodstabsetobj"] = new TabSet("leftgoodstabset", "top");
		window["leftgoodstabsetobj"].addTab("\u57FA\u672C\u4FE1\u606F", getElement("goodsProducts"));
		window["leftgoodstabsetobj"].addTab("\u7528\u6237\u6269\u5C55\u5C5E\u6027", getElement("memberParams"));
		window["leftgoodstabsetobj"].draw();
	}
}

function initGoodsParamTable() {
   // window["goodsParamTableEdit"] = new TableEdit("goodsParamTable",false,clickColumn);
   window["goodsParamTableEdit"] = new TableEdit("goodsParamTable",false,clickUserParamsRow);
   window["productTableEdit"] = new TableEdit("productTable",false,null);
   window["goodsTableEdit"] = new TableEdit("goodsTable",false,null);
}

function clickColumn(e){
	document.getElementById("bupdate").style.display="";
	document.getElementById("bcancel").style.display="";
}

function saveRow(){
	goodsParamTableEdit.updateRow();
	document.getElementById("bupdate").style.display="none";
	document.getElementById("bcancel").style.display="none";
	
	var tmp;
	tmp = goodsParamTableEdit.getTableData("X_TAG,ATTRIBUTE_DESC,ATTRIBUTE_VALUE,ATTRIBUTE_MEMO");
    tmp = "" + tmp;
    document.getElementById("SELECTED_GOODS_PARAMS").value=tmp; 
}

// shixb 
function saveUserParamRow(){
	getElement("PARAM_VALUE").value=getElement("input_"+_id).value;
	getElement("CGROUP").value=getElement("cgroup_"+_id).value;
	goodsParamTableEdit.updateRow();
	document.getElementById("bupdate").style.display="none";
	document.getElementById("bcancel").style.display="none";
	
	var tmp;
	tmp = goodsParamTableEdit.getTableData("X_TAG,PARAM_CODE,PARAM_NAME,PARAM_VALUE,PARAM_ATTR,CGROUP");
	tmp = "" + tmp;
    
    document.getElementById("SELECTED_GOODS_PARAMS").value=compareDataset(tmp,getElementValue("OLD_SELECTED_GOODS_PARAMS"),"PARAM_CODE,PARAM_VALUE");
}

// shixb
function saveUserParamDataset()
{
	try
	{
		var tmp;
		tmp = goodsParamTableEdit.getTableData("X_TAG,PARAM_CODE,PARAM_NAME,PARAM_VALUE,PARAM_ATTR");
		tmp = "" + tmp;
		document.getElementById("SELECTED_GOODS_PARAMS").value=compareDataset(tmp,getElementValue("OLD_SELECTED_GOODS_PARAMS"),"PARAM_CODE,PARAM_VALUE");
	}
	catch(error){}
}

function cancelit(){
	if(document.getElementById("ATTRIBUTE_DESC"))
	{
		document.getElementById("ATTRIBUTE_DESC").value = "";
	}
	if(document.getElementById("ATTRIBUTE_VALUE"))
	{
		document.getElementById("ATTRIBUTE_VALUE").value = "";
	}
	
	document.getElementById("bupdate").style.display="none";
	document.getElementById("bcancel").style.display="none";
}

function cancleUserParamEdit(){
	document.getElementById("ATTRIBUTE_DESC").value = "";	
	document.getElementById("PARAM_VALUE").value = "";	
	
	document.getElementById("bupdate").style.display="none";
	document.getElementById("bcancel").style.display="none";
}

function initMemberParamTable()
{
	window["memberParamsTableEdit"] = new TableEdit("memberParamsTable",false,clickMemberParamsRow);
}

var _id;
function clickMemberParamsRow(e)
{
	document.getElementById("bupdate").style.display="";
	document.getElementById("bcancel").style.display="";
	
	var td=e.target;  
	var row=td.parentNode;  
	var paramAttr=this.getCell(row,"PARAM_ATTR");
	var paramCode=this.getCell(row,"PARAM_CODE");
	var paramAttrValue=paramAttr.firstChild.nodeValue;  
	var paramCodeValue=paramCode.firstChild.nodeValue; 
	if(_id&&_id != paramAttrValue+paramCodeValue)
	{
		hidden(getElement("div_"+_id),true);
	}
	_id = paramAttrValue+paramCodeValue;
	hidden(getElement("div_"+paramAttrValue+paramCodeValue),false);
}

function clickUserParamsRow(e)
{
	document.getElementById("bupdate").style.display="";
	document.getElementById("bcancel").style.display="";
	
	var td=e.target;  
	var row=td.parentNode;  
	var paramCode=this.getCell(row,"PARAM_CODE");
	var paramCodeValue=paramCode.firstChild.nodeValue; 
	
	var paramName=this.getCell(row,"PARAM_NAME");
	var paramNameValue=paramName.firstChild.nodeValue; 
	
	getElement('ATTRIBUTE_DESC').value=paramNameValue;
	
	if(_id&&_id != paramCodeValue)
	{
		hidden(getElement("div_"+_id),true);
	}
	_id = paramCodeValue;
	hidden(getElement("div_"+paramCodeValue),false);
}

function saveTableToDataset(obj) {
	getElement("PARAM_VALUE").value=getElement("input_"+_id).value;
	getElement("PARAM_VALUE_DESC").value=getElement("input_"+_id).value;
	if((getElement("input_"+_id).tagName) == "SELECT")
	{
		getElement("PARAM_VALUE_DESC").value = getElement("input_"+_id).options[getElement("input_"+_id).selectedIndex].getAttribute('text');
	}

	memberParamsTableEdit.updateRow();
	document.getElementById("bupdate").style.display="none";
	document.getElementById("bcancel").style.display="none";
	getElement("memberParamsValues").value = memberParamsTableEdit.getTableData("X_TAG,PARAM_ATTR,PARAM_CODE,PARAM_NAME,PARAM_VALUE");
}

function saveUserParamTableToDataset(obj) {
	getElement("PARAM_VALUE").value=getElement("input_"+_id).value;
	memberParamsTableEdit.updateRow();
	document.getElementById("bupdate").style.display="none";
	document.getElementById("bcancel").style.display="none";
	getElement("memberParamsValues").value = memberParamsTableEdit.getTableData("X_TAG,PARAM_ATTR,PARAM_CODE,PARAM_NAME,PARAM_VALUE");
}

function memberParamNext()
{
	//
	try
	{
		initMemberParamTable();
	}
	catch(error) {}
	if(getElement("memberParamsValues"))
	{
		getElement("memberParamsValues").value = memberParamsTableEdit.getTableData("X_TAG,PARAM_ATTR,PARAM_CODE,PARAM_NAME,PARAM_VALUE");
		//getElement("memberParamsValues").value = compareDataset(getElement("memberParamsValues").value,getElementValue('oldMemberParamsValues'),'PARAM_CODE,PARAM_VALUE');
		
		getElement("memberParamsValues").value = compareDataset(getElement("memberParamsValues").value,"",'PARAM_CODE,PARAM_VALUE');
	}
}

function changeUserElementOperType()
{
//	var operTypeObj = getElement("operType");
//	if(operTypeObj)
//	{
//		if(operTypeObj.value == "1")
//		{
//			//add
//			
//			ajaxSubmit('group.param.bboss.changeuserelement.ChangeUserElement', 'queryBBossPurchases', null, 'MerchRefreshPart',null,false,initGoodsParamTable);
//		}
//		else
//		{
//			//not add
//			//getElement("PO_NUMBER").disabled="true";
//			//getElement("PRODUCT_NUMBER").disabled="true";
//			
//			//ajaxSubmit('group.param.bboss.changeuserelement.ChangeUserElement', 'initChgUs', null, 'MerchRefreshPart,DevRefreshPart,PoPart,PurPart,ParamPart,ProPart',null,false,initGoodsParamTable);
//			
//			
//			ajaxSubmit('group.param.bboss.changeuserelement.ChangeUserElement', 'initChgUs', null, 'MerchRefreshPart,DevRefreshPart,PoPart,PurPart,ParamPart,ProPart',null,false,initGoodsParamTable);
//		}
//	}

	
ajaxSubmit('group.param.bboss.changeuserelement.ChangeUserElement', 'queryPoList', null, 'MerchRefreshPart',null,false,initGoodsParamTable);
}

function changeUserMerch()
{
	initChangeUserElementTables();
	
	var ds = new Wade.DatasetList(getElementValue('purchaseInfos'));
	var poNumber = getElement("PO_NUMBER").value;
	for(var i=0;i<ds.length;i++)
	{
		if(poNumber == ds.get(i,"MERCH_SPEC_CODE"))
		{
			getElement("purchaseInfo").value = ds.get(i);
			break;
		}
	}
}

function changeUserProductOperType()
{
	ajaxSubmit('group.param.bboss.changeuserelement.ChangeUserElement', 'queryProductList', null, 'DevRefreshPart',null,false,initChangeUserElementTables);
}

/*product change*/
function changeUserProduct()
{
	initChangeUserElementTables();
	var ds = new Wade.DatasetList(getElementValue('productInfos'));
	var poNumber = getElement("PRODUCT_NUMBER").value;
	for(var i=0;i<ds.length;i++)
	{
		if(poNumber == ds.get(i,"PRODUCT_SPEC_CODE"))
		{
			getElement("productInfo").value = ds.get(i);
			break;
		}
	}
}

function changeMemberElementOperType()
{
	var operTypeObj = getElement("operType");
	if(operTypeObj.value=='2'||operTypeObj.value=='1'){getElement('memberParams').style.display=''}else{getElement('memberParams').style.display='none'}
	getElement("PO_NUMBER").value="";
	if(operTypeObj)
	{
		if(operTypeObj.value == "1")
		{
			//add
			ajaxSubmit('group.param.bboss.changememelement.ChangeMemberElement', 'queryUserMerch', '', 'merch_part,DevRefreshPart,param_part',null,false,initMemberParamTable);
		}
		else
		{
			//not add
			ajaxSubmit('group.param.bboss.changememelement.ChangeMemberElement', 'initChgMb', null, 'merch_part,DevRefreshPart,param_part',null,false,initMemberParamTable);
		}
	}
}

function initChangeUserElementTables()
{
	try {
        initGoodsParamTable();
        createUserParamTabset();
        onGoodsClick();
    } catch (error) {
    }
}