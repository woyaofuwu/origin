function checkTerminal(){
	
	var  custInfo = $.enterpriseLogin.getInfo().get("CUST_INFO");
    var  custId =  custInfo.get("CUST_ID"); 
    $.beginPageLoading("数据加载中......");
    ajaxSubmit("","initPage", '&CUST_ID=' + custId, "OfferListPart", function(data){
					$.endPageLoading();				
					showPopup('qryPopup','qryPopupItem');
				}, function(error_code, error_info) {
					MessageBox.error(error_code, error_info);
					$.endPageLoading();
				});
} 


function chooseOfferInfo(obj){
	var accessNum = $(obj).attr("SERIAL_NUMBER");
	var stateFlag = $("#STATE_FLAG").val();
	 $("#OFFER_NAME_a").val(accessNum);
	 $.beginPageLoading();
	 ajaxSubmit("","queryCustInfos", '&SERIAL_NUMBER=' + accessNum + '&OPER_TYPE=' + stateFlag, "itemPart,itemPart1,itemPart2,itemPart3,itemPart4", function(data){
			$.endPageLoading();	
			backPopup(obj);
		}, function(error_code, error_info) {
			MessageBox.error(error_code, error_info);
			$.endPageLoading();
		});
	 
}

function submit(){
	debugger;
	var productId = $("#PRODUCT_ID").val();
	var userId = $("#USER_ID").val();
	var groupId = $("#GROUP_ID").val();
	var eparchyCode = $("#USER_EPARCHY_CODE").val();
	var stateFlag = $("#STATE_FLAG").val();
	var mebCount = $("#MEB_COUNT").val();
	if( $("#OFFER_NAME_a").val()==""|| $("#OFFER_NAME_a").val()==undefined|| $("#OFFER_NAME_a").val()==null){
		$.validate.alerter.one($("#OFFER_NAME_a")[0],"您没有选择需要受理的产品，请选择！");
		return false;
	}
	
	
	if("0" != mebCount){
		
		if(!confirm("还有[" + mebCount + "]位成员使用了集团产品,是否确认一起状态变更!"))
		{
			return false;
		}
	}
	$.beginPageLoading();
	 ajaxSubmit("","submitChange", '&PRODUCT_ID=' + productId+'&USER_ID=' + userId+'&GROUP_ID=' + groupId+'&USER_EPARCHY_CODE=' + eparchyCode+'&STATE_FLAG=' + stateFlag, "", function(data){
		 $.endPageLoading();
			var obj = JSON.parse(data);
	    	MessageBox.success("业务受理成功!", "订单号: "+obj[0].ORDER_ID,function(btn){
	    		if("ok" == btn){
					window.location.reload();
				}
	    	});
		}, function(error_code, error_info) {
			MessageBox.error(error_code, error_info);
			$.endPageLoading();
		});
}