//根据副号码查询主号码信息
function queryMainCardInfo(){
	$.beginPageLoading("正在查询数据...");
	//员工优惠查询
	$.ajax.submit('FuCardInfo', 'qryMainCardInfoByFuCard', null, 'MainCardInfo', function(data){
		$.endPageLoading();
		alert("查询主号信息成功！");
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}
//根据副号码查询副号码信息
function queryFuCardInfo(){
	$.beginPageLoading("正在查询数据...");
	//员工优惠查询
	$.ajax.submit('FuCardInfo', 'qryFuCardInfoByFuCard', null, 'MainCardInfo', function(data){
		$.endPageLoading();
		alert("查询副号信息成功！");
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}

