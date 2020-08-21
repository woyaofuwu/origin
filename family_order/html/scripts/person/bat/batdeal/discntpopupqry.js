function tableRowDBClick() {
	var table = $.table.get("QueryListTable");
	var json = table.getRowData();
	var discntId = json.get('DISCNT_CODE','');
	var discntName = json.get('DISCNT_NAME','');
	setPopupReturnValue(discntId,discntName);
}


function queryDiscntInfo(){
	$.beginPageLoading("数据查询中..");
	$.ajax.submit('QueryCondPart', 'queryDiscntInfo', null, 'QueryListPart,TipInfoPart', function(data){
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}