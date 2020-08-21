function tableRowDBClick() {
	var table = $.table.get("QueryListTable");
	var json = table.getRowData();
	var elementId = json.get('ELEMENT_ID','');
	var elementName = json.get('ELEMENT_NAME','');
	var elementType = json.get('ELEMENT_TYPE','');
	var returnValues = elementId + "|" ;
	returnValues += elementName ;
	returnValues += "|" ;
	returnValues += elementType ;
	setPopupReturnValue(returnValues,elementName);
}

function queryElements(){
	var queryType = $("#cond_QUERY_TYPE").val();
	if(queryType == ''){
		alert('查询类型不能为空!');
		return false;
	}
	$.beginPageLoading("数据查询中..");
	$.ajax.submit('QueryCondPart', 'queryElements', null, 'QueryListPart,TipInfoPart', function(data){
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}