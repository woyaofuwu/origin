window.onload = function () {
    $("#cond_SERIAL_NUMBER").focus();
}

function queryUserPlatsvcs() {
    //查询条件校验
    if (!$.validate.verifyAll("UserPlatsvcCond")) {
        return false;
    }

    $.beginPageLoading("正在查询数据...");

    ajaxSubmit('UserPlatsvcCond', 'qryUserPlatSvc', null, 'selectList,PlatsvcLastList',
        function (data) {
            $.endPageLoading();
        }, function (code, info, detail) {
            $.endPageLoading();
            MessageBox.error("错误提示", info);
        }, function () {
            $.endPageLoading();
            MessageBox.alert("告警提示", "查询超时!", function (btn) {
            });
        });
}

function changeServiceIdType(){	
	$.beginPageLoading("载入中..");
     $.ajax.submit('UserPlatsvcCond', 'getQueryUserPlatSvcLastList', null, 'PlatsvcLastList',function(data){
		$.endPageLoading();
	},
	
	function(error_code,error_info,detail){
		$.endPageLoading();
		MessageBox.error("错误提示", error_info, null, null, null, detail);
    });
}



/**
 * 数指重跑
 */
function checkSubmitBefore() {
    var dataInfo = getSelectedData();
    if (dataInfo.length === 0) {
        MessageBox.alert("请选择需要重跑订单后提交！");
        return false;
    }
    if (dataInfo.get("PLAT_SYN_ID").toString().length === 0) {
        MessageBox.alert("数指数据查询不到，无法重跑");
        return false;
    }
    
  
    
	$.beginPageLoading("正在查询数据....");
    $.ajax.submit('UserPlatsvcCond'
    		,'anewSendPlatsDataForDataCom', "&PLAT_SYN_ID=" + dataInfo.get("PLAT_SYN_ID").toString()+"&BUSI_SIGN=" + dataInfo.get("BUSI_SIGN").toString()+"&SUBSCRIBE_ID=" + dataInfo.get("SUBSCRIBE_ID").toString()
    		, "PlatsvcLastList",function(data){
		$.endPageLoading();
		 var x_resultcode = data.get("IS_SUCCESS","0");
	        if(x_resultcode == '1'){ 
	        	MessageBox.alert("","重跑数据成功");
	        	return;
	        }else{
	        	MessageBox.alert("","重跑数据失败");
	        	return;
	        }
		
	},
	
	function(error_code,error_info,detail){
		$.endPageLoading();
		MessageBox.error("错误提示", error_info, null, null, null, detail);
   });
    
}

function getSelectedData(obj) {
    if (obj && typeof obj !== "undefined") {
        return UserPlatsvcTable.getRowData(obj.attr("rowIndex") - 1);
    } else {
        if ($("#UserPlatsvcTable tbody .on").length === 1) {
            return UserPlatsvcTable.getSelectedRowData();
        } else {
            return new $.DataMap();
        }
    }
}