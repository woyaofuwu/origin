function queryStaffs()
{
	if(!$.validate.verifyAll("QueryCondPart")) {//先校验已配置的校验属性
		return false;
	}
	
	$.beginPageLoading("查询员工信息。。。");
	$.ajax.submit('QueryCondPart', 'queryStaffs', '', 'QueryListPart', 
		function(ajaxData){
			$.endPageLoading();
		},
		function(code, info){
			$.endPageLoading();
			alert(info);
		});
}

function selectStaff(obj) {
	var staffId = obj.getAttribute("staffId");
	var staffName = obj.getAttribute("staffName");
	setPopupReturnValue(staffId,staffName);
}