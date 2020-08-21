/*$(document).ready(function(){

	//查当天收到的订单
	 $.ajax.submit('null', 'onInit', null, 'DestroyTablePart', function (data) {
         $.endPageLoading();
         //$.cssubmit.disabledSubmitBtn(false);
     },
     function (error_code, error_info) {
         $.endPageLoading();
         alert(error_info);
         // closeNav("remotedestroyuser.QueryReceiveDestroyHis"); 不关闭页面了
     });
});*/
function queryDestroyOrderHis() {

    var startDate = $("#START_TIME").val();
    var endDate = $("#END_TIME").val();
    var dealTag = $("#DEAL_TAG").val();

    if ("" == startDate && "" == endDate && "" == dealTag) {
        alert("请输入至少一个查询条件！");
        return false;
    }

    if ("" == startDate && "" != endDate) {
        alert("请输入开始时间！");
        return false;
    }
    if ("" == endDate && "" != startDate) {
        alert("请输入结束时间！");
        return false;
    }

    if (startDate > endDate) {
        alert("开始时间不能大于结束时间！请重新输入！");
        return false;
    }

    $.beginPageLoading("数据处理中...");
    $.ajax.submit('QueryCondPart', 'queryDestroyOrder', null, 'DestroyTablePart', function (data) {
            $.endPageLoading();
            $.cssubmit.disabledSubmitBtn(false);
        },
        function (error_code, error_info) {
            $.endPageLoading();
            alert(error_info);
            // closeNav("remotedestroyuser.QueryReceiveDestroyHis"); 不关闭页面了
        });
}

function onTradeSubmit() {
}

function detailInfo(self){
	
	var rowIndex = self.parentNode.parentNode.parentNode.rowIndex;
	var num =rowIndex-1;
	var rowData = DestroyTable.getRowData(num);
	var orderId =rowData.get('col_ORDER_ID');//订单号
	var serial_number =rowData.get('SERIAL_NUMBER');//手机号
	var deal_tag =rowData.get('DEAL_TAG');//订单状态
	if("未处理"==deal_tag){
		deal_tag='0';
	}else if("审核完成"==deal_tag){
		deal_tag='1';
	}else if("审核退回"==deal_tag){
		deal_tag='3';
	}else if("已销户"==deal_tag){
		deal_tag='9';
	}
	var allStatusTag='HIS';
	/*if("3"==deal_tag||"9"==deal_tag){
		return;
	}*/
	var param = '&ORDER_ID='+orderId;
	param += '&SERIAL_NUMBER='+serial_number;
	param += '&DEAL_TAG='+deal_tag;
	param += '&ALL_STATUS_TAG='+allStatusTag;
	popupDialog('跨区销户审核', 'remotedestroyuser.AuditDestroyOrder', 'qryCustInfo', param, null, '60', '35', true, null, null);

}