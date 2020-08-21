//销号号码服务信息查询
function queryUserRoamingInfo() {
	// 查询条件校验
	if (!$.validate.verifyAll("SEARCH_PART")) 
	{
		return false;
	}
	$.beginPageLoading("正在查询数据..."); // 播记录查询
	$.ajax.submit('SEARCH_PART', 'queryUserRoamingInfo', null, 'RoamingInfoPart', function(data) {
		$.endPageLoading();
	}, function(error_code, error_info) {
		$.endPageLoading();
		$.showErrorMessage("查询消息错误","错误编码：" + error_code + "<br/>" + error_info);
	});
}
