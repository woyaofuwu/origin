function initGrpFeeList()
{
	var list = $.DatasetList($("#GRP_FEE_LIST").val());
	
	if(list.length > 0)
	{
		if($.feeMgr)
		{
			$.feeMgr.clearFee();
		}
		
		for(var i = 0; i < list.length; i++)
		{
			var feeMap = $.DataMap();
			feeMap.put("TRADE_TYPE_CODE", list.get(i).get("TRADE_TYPE_CODE"));
			feeMap.put("ELEMENT_ID", list.get(i).get("ELEMENT_ID"));
			feeMap.put("MODE", list.get(i).get("FEE_MODE"));
			feeMap.put("CODE", list.get(i).get("FEE_TYPE_CODE"));
			feeMap.put("FEE", list.get(i).get("FEE"));
			
			$.feeMgr.insertFee(feeMap);
		}
		
		// 设置POS参数
		var tradeTypeCode = $('#TRADE_TYPE_CODE').val();
		var serialNumber = $('#SERIAL_NUMBER').val();
		var eparchyCode = $('#EPARCHY_CODE').val();
		var userId = $('#USER_ID').val();
		
		$.feeMgr.setPosParam(tradeTypeCode, serialNumber, eparchyCode, userId);
	}
}