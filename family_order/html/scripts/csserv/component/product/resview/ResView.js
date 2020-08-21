function insertRes(obj){
	var dynaTableResView = $.table.get("resview");
	
	$("#DYNATABLE_RES_TYPE").val(getResTypeCodeDesc(obj.get("RES_TYPE_CODE")));
	$("#DYNATABLE_RES_TYPE_CODE").val(obj.get("RES_TYPE_CODE"));
	$("#DYNATABLE_RES_CODE").val(obj.get("RES_CODE"));
	$("#DYNATABLE_CHECKED").val(obj.get("CHECKED")=="true"?"\u6210\u529F":"");
	$("#DYNATABLE_DISABLED").val(obj.get("DISABLED")=="true"?"\u4E0D\u53EF\u4FEE\u6539":"");
	var custEdit = $.ajax.buildJsonData("ResPart");
	dynaTableResView.addRow(custEdit);
	modiResList(obj, "ADD");
}

function deleteRes(obj){

	var dynaTableResView = $.table.get("resview");
	var tmp = dynaTableResView.getTableData(null,true);
	
	tmp.each(function(item,index,totalcount){
	    
		if (item.get("DYNATABLE_RES_TYPE_CODE") == obj.get("RES_TYPE_CODE") && item.get("DYNATABLE_RES_CODE") == obj.get("RES_CODE")){
			//dynaTableResView.rowIndex = index+1;
			
			dynaTableResView.deleteRow(String(index+1),true);
			return false;
		}
	});	
	modiResList(obj, "REMOVE");
}

function initResList(obj){
	var dynaTableResView = $.table.get("resview");
	
    obj.each(function(item,index,totalcount){
    	
		$("#DYNATABLE_RES_TYPE").val(getResTypeCodeDesc(item.get("RES_TYPE_CODE")));
		$("#DYNATABLE_RES_TYPE_CODE").val(item.get("RES_TYPE_CODE"));
		$("#DYNATABLE_RES_CODE").val(item.get("RES_CODE"));
		$("#DYNATABLE_CHECKED").val(item.get("CHECKED")=="true"?"\u6210\u529F":"");
		$("#DYNATABLE_DISABLED").val(item.get("DISABLED")=="true"?"\u4E0D\u53EF\u4FEE\u6539":"");
		var custEdit = $.ajax.buildJsonData("ResPart");
		dynaTableResView.addRow(custEdit);
		modiResList(item, "EXIST");
	});
	
	//$("#DYNATABLE_RES_LIST").val($("#DYNATABLE_RES_RECORD").val().toString());
}

function modiResList(obj,state){
	var resRecord = $("#DYNATABLE_RES_RECORD");
	var selected = $.DatasetList(resRecord.val());
	
	var old = $("#DYNATABLE_RES_LIST");
	var oldList = $.DatasetList(old.val());
	
	if (state == "ADD"){
		obj.put("MODIFY_TAG", "0");
		selected.add(obj);
	}
	
	if (state == "EXIST"){
		obj.put("MODIFY_TAG", "EXIST");
		oldList.add(obj);
		old.val(oldList.toString());
	}
	
	if (state == "REMOVE"){
		selected.each(function(item,index,totalcount){
			if (item.get("RES_TYPE_CODE") == obj.get("RES_TYPE_CODE") && item.get("RES_CODE") == obj.get("RES_CODE")){
				selected.removeAt(index);
			}
		});
		oldList.each(function(item,index,totalcount){
			if (item.get("RES_TYPE_CODE") == obj.get("RES_TYPE_CODE") && item.get("RES_CODE") == obj.get("RES_CODE")){
				item.put("MODIFY_TAG", "1");
				selected.add(item);
				return ;
			}
		});
	}
	
	resRecord.val(selected.toString());
	selected = null;
}

function getResTypeCodeDesc(resTypeCode){
	
	var result ="未知类型";
	Wade.httphandler.submit('','com.asiainfo.veris.crm.order.web.frame.csview.common.component.product.resview.ResViewHttpHandler','getResTypeName','&RES_TYPE_CODE='+resTypeCode,
	function(data){
		result = data.get('RES_TYPE');
	},'',{async:false});
	
	return result;
}

//将RES的信息清空
function cleanResPart(){
	$("#DYNATABLE_RES_RECORD").val('');
	$("#DYNATABLE_RES_LIST").val('');
	$("#RESVIEW_TBODY").html('');
}

