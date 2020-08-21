function refreshPartAtferAuth(data)
{
	$.beginPageLoading("数据查询中..");
	$.ajax.submit('AuthPart', 'loadChildInfo',  "&USER_INFO="+(data.get("USER_INFO")).toString(), 'QueryListPart', function(data1){
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}


function submitBeforeAction(){
	var newIMEI =$("#NEW_RES_CODE").val();
	newIMEI=$.trim(newIMEI);
	var oldIMEI = $("#OLD_RES_CODE").val();
	oldIMEI=$.trim(oldIMEI);
	var remark = $("#REMARK").val();
	remark=$.trim(remark);
	var tradeId= $("#CHG_RELATION_TRADE_ID").val();
	tradeId=$.trim(tradeId);
	if(newIMEI == ""){
		alert("新IMEI号不能为空！");
		return false;
	}
	if(newIMEI.length > 15){
		alert("【新IMEI号】最大长度不能超过15位！");
		return false;
	}
	if(newIMEI == oldIMEI){
		alert("新旧IMEI号不能一样！");
		return false;
	}
	/*REQ201712040014 销户业务限制查询 add by zhanglin3 20180206 start*/
    var checkTradeId= $("#CHECK_TRADE_ID").val();
    checkTradeId=$.trim(checkTradeId);
	if (!checkTradeId) {
        alert("审批工单号不能为空！");
        return false;
    }
    if (checkTradeId == $("#BAK_CHECK_TRADE_ID").val()) {
        alert("新旧审批工单号不能一样！");
        return false;
    }
    if (checkTradeId.length > 100) {
        alert("【审批工单号】内容长度不能超过100！");
        return false;
    }
    /*end*/
	if(remark.length > 100){
		alert("【备注】内容长度不能超过100！");
		return false;
	}
	var param = "&RELATION_TRADE_ID="+tradeId;
	$.cssubmit.addParam(param);
       return true;
}

function changeGoodsInfo(){
	var rowData = $.table.get("userGoodsInfoTable").getRowData();
	var oldIMEI=rowData.get("RES_CODE");
	var oldRemark=rowData.get("REMARK");
	var tradeId=rowData.get("RELATION_TRADE_ID");
	var oldResCode=$("#OLD_RES_CODE");
	var newResCode=$("#NEW_RES_CODE");
	var newRemark=$("#REMARK");
	var chgId=$("#CHG_RELATION_TRADE_ID");
	chgId.val(tradeId);
	oldResCode.val(oldIMEI);
	newResCode.val(oldIMEI);
	newRemark.val(oldRemark);
    var checkTradeId = rowData.get("CHECK_TRADE_ID");
	$("#CHECK_TRADE_ID").val(checkTradeId);
    $("#BAK_CHECK_TRADE_ID").val(checkTradeId);
}

