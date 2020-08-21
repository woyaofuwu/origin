/* $Id  */

//集团资料查询成功后调用的方法
function selectGroupAfterAction(data) {
   //填充集团客户显示信息
   insertGroupCustInfo(data);  
   //设置CUST_ID
   
   $.feeMgr.clearFeeList();
   
   $("#PACK_CUST_ID").val(data.get("CUST_ID"));
   
   //查询已订购流量包
   queryOrderedPackage(data);
  
   //$("#CSSUBMIT_BUTTON").attr("disabled", "");
   
}
//集团资料查询失败后调用的方法
function selectGroupErrorAfterAction() {
	//清空填充的集团客户信息内容
    clearGroupCustInfo();
}
//查询已订购流量包
function queryOrderedPackage(data){
	beginPageLoading("正在查询..");
	var groupId = data.get("GROUP_ID");
	var custId = data.get("CUST_ID");
	var productId = $("#PRODUCT_ID").val();
	var paramStr = '&CUST_ID='+ custId+'&GROUP_ID='+groupId;
	 $("#CUST_ID").val(custId);
	$.ajax.submit('', 'queryOrderedPackage',paramStr , 'PackageOrdered', function(data){	
		$("#DATAPCK_COUNT").val("");
		$.endPageLoading();
		$("#CSSUBMIT_BUTTON").attr("disabled", false);
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		clearGroupCustInfo();
		$("#CSSUBMIT_BUTTON").attr("disabled", true);
		showDetailErrorInfo(error_code,error_info,derror);
    });
}
//查询可订购流量包
function queryOrderingPackage(data){
	beginPageLoading("正在查询..");
	$.ajax.submit('', 'queryOrderingPackage', null, 'PackageOrdering', function(data){		
		$.endPageLoading();	
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
}

/**
 * 提交前业务校验
 * */
function onSubmitBaseTradeCheck() {
	var custid = $("#PACK_CUST_ID").val();
	if("" == custid || null == custid || undefined == custid ){
		alert("请输入集团编码！");
		return false;
	}
	var checkedInstIds = getCheckedValues("InstIdCheckBox");
	if("" == checkedInstIds || null == checkedInstIds || undefined == checkedInstIds ){
		alert("请选择需要延期处理的流量包！");
		return false;
	}
	var enddate = $("#END_DATE").val();
	if("" == enddate || null == enddate || undefined == enddate ){
		alert("新失效时间不能为空！");
		return false;
	}
	if(!$.validate.verifyAll("orderingPart")) {
		return false;
	}
	$("#INST_ID_STRS").val(checkedInstIds);
	return true;
}

function refreshFee(totalfee)
{
	$.feeMgr.clearFeeList();
	
	var feeData = $.DataMap();
	feeData.put("TRADE_TYPE_CODE", "1111");
	feeData.put("MODE", "0");
	feeData.put("CODE", "293");
	feeData.put("FEE",  totalfee);
	
	$.feeMgr.insertFee(feeData);
}


function calOrderFee()
{
	var feeVal = $("#DATAPCK_FEE").val();      //单价
	var pckcnt = $("#DATAPCK_COUNT").val();    //数量
	var discnt=$("#DISCOUNT").val();        //折扣
	if("" == feeVal || null == feeVal || undefined == feeVal || "" == pckcnt || null == pckcnt || undefined == pckcnt) {
		return false;
	}
	if(discnt !=null && discnt!=""){
		if(isNaN(discnt)){
			alert("请输入两位或者三位折扣数字，如：70，表示打7折（70%）。");
			$("#DISCOUNT").focus();
			return false;
		}else{
			if(parseInt(discnt)<=0){
				alert("请输入两位或者三位折扣数字，如：70，表示打7折（70%）。必须大于0。");
				$("#DISCOUNT").val("100");
				$("#DISCOUNT").focus();
				return false;
			}else if(parseInt(discnt)>100){
				alert("请输入两位或者三位折扣数字，如：70，表示打7折（70%）。不允许超过100%。");
				$("#DISCOUNT").val("100");
				$("#DISCOUNT").focus();
				return false;
			}
		}
		if(discnt.length!=2 && discnt.length!=3){
			alert("请输入两位或者三位折扣数字，如：70，表示打7折（70%）!");
			$("#DISCOUNT").focus();
			return  false;
		}
	}else{
		discnt=100;
	}
	
	var totalfee = parseInt(feeVal)*parseInt(pckcnt)*100*discnt/100;
	
	//刷新费用组件费用
	refreshFee(totalfee);
	
    return true;	
}

/**
 * 选择套餐，并填入各选项
 * */
function selectFlowPack(){
	var pack=$("#FLOW_PACK").val();
	var param = '&FLOW_PACKAGE='+ pack;
	$.ajax.submit('', 'queryFlowPackage',param , null, function(data){		
		var dataVal=data.get("PARA_CODE2");
		var dataFee=data.get("PARA_CODE1");
		$("#DATAPCK_VALUE").val(dataVal);
		$("#DATAPCK_FEE").val(dataFee);
		calOrderFee();
		$.endPageLoading(); 
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
}
/**
 * 根据打折自动计算金额
 * */
function calculateFee(){
	var discnt=$("#DISCOUNT").val();
	if(discnt !=null && discnt!=""){
		//isNaN(str)=false 为数字
		if(isNaN(discnt)){
			alert("请输入两位或者三位折扣数字，如：70，表示打7折（70%）。");
			$("#DISCOUNT").focus();
			return false;
		}else{
			if(parseInt(discnt)<=0){
				alert("请输入两位或者三位折扣数字，如：70，表示打7折（70%）。必须大于0。");
				$("#DISCOUNT").val("100");
				$("#DISCOUNT").focus();
				return false;
			}else if(parseInt(discnt)>100){
				alert("请输入两位或者三位折扣数字，如：70，表示打7折（70%）。不允许超过100%。");
				$("#DISCOUNT").val("100");
				$("#DISCOUNT").focus();
				return false;
			}
		}
		if(discnt.length!=2 && discnt.length!=3){
			alert("请输入两位或者三位折扣数字，如：70，表示打7折（70%）!");
			$("#DISCOUNT").focus();
			return  false;
		}
	}else{
		alert("折扣数字不允许为空，默认100%。");
		$("#DISCOUNT").val("100");
		$("#DISCOUNT").focus();
		return false;
	}
	calOrderFee();
}
