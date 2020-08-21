function chkInput(makeData){
	var sn = $("#SERIAL_NUMBER").val();
	//var tradeTypeCode = $("#TRADE_TYPE_CODE").val();
 	var start = $("#START_DATE").val();
 	var end = $("#END_DATE").val();
 	if (!$.validate.verifyAll("SearchCondPart")){
		return false; 
	}
 	var day=makeData?1:31;
	//校验起始日期范围
	if(dateDiff(start, end)>day){
		alert("开始时间和结束时间跨度不能超过"+day+"天");
		return false;
	}
	return true;
}

//点击查询确认事件
function chkSearchForm(){
	//校验起始日期范围
	if(!chkInput(true)){
		return false;
	}
	
	$.beginPageLoading("查询电子工单业务数据。。。");
	$.ajax.submit("SearchCondPart", "queryElectronicworkorder", null, "SearchResultPart",function(data){
		$.endPageLoading();
		if(!$("#printTable tbody tr") || $("#printTable tbody tr").length==0){
			$("#tipInfo").html("获取电子工单业务信息无数据!");
			$("#tipInfo").css("display", "");
		}else{
			$("#tipInfo").css("display", "none");
		}
		
	},function(code, info, detail){
		$.endPageLoading();
		MessageBox.error("错误提示","查询电子工单业务数据！", null, null, info, detail);
	});
}

function dateDiff(start,end){
	var day = 0;
	var startDt = new Date(Date.parse(start.replace(/-/g,   "/")));
	var endDt = new Date(Date.parse(end.replace(/-/g,   "/")));
	day = (endDt.getTime()-startDt.getTime()) / (1000*60*60*24) ;
	return day;
}
/**
 *	模拟组装BOSS前台传输过来的接口XML数据
 */
function assambleBossXml(data) {
	var TRADE_ID = data.get("TRADE_ID","");
	var BRAND_CODE = data.get("BRAND_CODE","");
	var BRAND_NAME = data.get("BRAND_NAME","");
	var TRADE_STAFF_ID = data.get("TRADE_STAFF_ID","");
	var STAFF_NAME = data.get("STAFF_NAME","");
	var ORG_INFO = data.get("ORG_INFO","");
	var DEPART_NAME = data.get("DEPART_NAME","");
	var SERIAL_NUMBER = data.get("SERIAL_NUMBER","");
	var ACCEPT_DATE = data.get("ACCEPT_DATE","");
	var TRADE_TYPE_CODE = data.get("TRADE_TYPE_CODE","");
	var RECEIPT_INFO = data.get("RECEIPT_INFO1","")+data.get("RECEIPT_INFO2","")+data.get("RECEIPT_INFO3","");
	var VIP_CLASS = data.get("VIP_CLASS","");
	var CUST_NAME = data.get("CUST_NAME","");
    var str = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><req>";
    str = str + "<command_id>save_paperbill</command_id>";
    str = str + "<billid>"+TRADE_ID+"</billid>";
    str = str + "<brand_name>"+BRAND_NAME+"</brand_name>";
    str = str + "<brand_id>"+BRAND_CODE+"</brand_id>";
    str = str + "<work_name>"+STAFF_NAME+"</work_name>";
    str = str + "<work_no>"+TRADE_STAFF_ID+"</work_no>";
    str = str + "<org_info>"+ORG_INFO+"</org_info>";
    str = str + "<org_name>"+DEPART_NAME+"</org_name>";
    str = str + "<phone>"+SERIAL_NUMBER+"</phone>";
    str = str + "<serv_id></serv_id>";
    str = str + "<op_time>"+ACCEPT_DATE+"</op_time>";
    str = str + "<sys_accept></sys_accept>";
    str = str + "<op_code>"+TRADE_TYPE_CODE+"</op_code>";
    str = str + "<busi_detail>"+RECEIPT_INFO+"</busi_detail>";
    str = str + "<verify_mode>"+VIP_CLASS+"</verify_mode>";
    str = str + "<id_card></id_card>";
    str = str + "<cust_name>"+CUST_NAME+"</cust_name>";
    str = str + "<is_luckynumber></is_luckynumber>";
    str = str + "</req>";
    return str;
}

/**
 *	新增人像采集接口
 */
function ImageAcquisition() {
    var bossOriginalXml;
    var prtTradeObj = $("input[name=TRADE_ID]:checked");
	if(!prtTradeObj || prtTradeObj.length!=1){
		alert("请选择需要补录电子工单的业务！");
		return;
	}
	var tradeId=prtTradeObj.val();
	$.ajax.submit("", "electronicworkorderbuluToDzh", "&TRADE_ID="+tradeId, null,function(data){
    	//调用拍照方法
        var resultInfo = MakeActiveX.ImageAcquisition(assambleBossXml(data));
        //获取保存结果
        var result = MakeActiveX.ImageAcquisitionInfo.result;
        //获取保存照片ID
        var picID = MakeActiveX.ImageAcquisitionInfo.picid;
        if(result=="0"){
        	alert("上传数据成功！");
        }else{
        	alert("上传数据失败！");
        }
	});
}