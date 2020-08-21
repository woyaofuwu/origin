function getGroupInfo()
{
    var group = $("#cond_GROUP_ID").val;
    
    if(!$.validate.verifyField($("#cond_GROUP_ID"))) 
    {
       return false;
    }  
	$.beginPageLoading("\u6570\u636e\u67e5\u8be2\u4e2d......");//数据查询中......
	$.ajax.submit('queryFrom','getGroupBaseInfo','&cond_GROUP_ID='+group,'TradeListPart,BackPart',
	function(data){
			$.endPageLoading();
		},
		function(error_code,error_info, derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
    	}
	      );
}

// 提交前校验
function onSubmitBaseTradeCheck(){
	
	var userId = $("#USER_ID").val();
	if(userId == null || userId == ""){
		//请选择集团产品!
		alert("\u8bf7\u9009\u62e9\u96c6\u56e2\u4ea7\u54c1!");
		return false;
	}
	 
	if(!$.validate.verifyAll("queryFrom")) return false;
	
	if(!$.validate.verifyAll("TradeListPart")) return false;
	
	if(!$.validate.verifyAll("BackPart")) return false;
		
	var fileObj = $("#VOUCHER_FILE_LIST");
	if(fileObj && typeof(fileObj) !='undefined' )
	{
		var fileValue = fileObj.val();
		if(fileValue == '')
		{
			//请上传凭证附件!
			alert("\u8bf7\u4e0a\u4f20\u51ed\u8bc1\u9644\u4ef6!");
			return false;
		}
	}
    var staffObj = $("#AUDIT_STAFF_ID");
	if(staffObj && typeof(staffObj) !='undefined')
	{
		var staffValue = staffObj.val();
		if(staffValue == '')
		{
			//请选择稽核员!
			alert("\u8bf7\u9009\u62e9\u7a3d\u6838\u5458!");
			return false;
		}
	}
    					
	return true;
}

//设置新增选项卡资料
function refresAdd(){
	$("#ratioAddId").attr("className", "on");
	$("#ratioChDelId").attr("className", "");
	
	$("#queryFrom").css("display","");
	$("#submitPartForm1").css("display","");
	$("#addForm").css("display","none");
	$("#editForm").css("display","none");
	$("#submitPartForm2").css("display","none");
	
	//clearAuditInfo();
}

//设置修改删除选项卡的资料
function refresEditDel(){
	$("#ratioAddId").attr("className", "");
	$("#ratioChDelId").attr("className", "on");
	
	$("#queryFrom").css("display","none");
	$("#submitPartForm1").css("display","none");
	$("#addForm").css("display","");
	$("#editForm").css("display","");
	$("#submitPartForm2").css("display","");
	
	//clearAuditInfo();
}

function clearAuditInfo()
{
	var fileObj = $("#VOUCHER_FILE_LIST");
	if(fileObj && typeof(fileObj) !='undefined' )
	{	
		fileObj.val("");
	}
    var staffObj = $("#AUDIT_STAFF_ID");
	if(staffObj && typeof(staffObj) !='undefined')
	{
		 staffObj.val("");
	}
}
function queryRatioList(){
	
	$.beginPageLoading("\u6570\u636e\u67e5\u8be2\u4e2d......");//数据查询中......
	$.ajax.submit("addForm,hiddenPart", "queryRatioOtherInfo", null, "AddDelPart,ratioPart,EditPart,hiddenPart", 
		function(data){
			$.endPageLoading();
			var flag = data.get("ENABLE_FLAG");
			if(flag == "true" || flag == true) {
				$("#cond_CITY_CODE").attr("disabled",false);
			}else {
				$("#cond_CITY_CODE").attr("disabled",true);
			}
		},
		function(error_code,error_info, derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
    	}
    );
}


function init()
{
	var flag = $("#cond_ENABLE_FLAG").val();
	if(flag == "true"){
		//alert(1);
		$("#cond_CITY_CODE").attr("disabled",false);
	} else {
		//alert(2);
		$("#cond_CITY_CODE").attr("disabled",true);
	}
	
}

function tableRatioClick()
{
	var rowData = $.table.get("ratioTable").getRowData();
	var returnRation = rowData.get("RSRV_STR2");
	var startDate = rowData.get("RSRV_STR3");
	var endDate = rowData.get("RSRV_STR4"); 
	var activeCode = rowData.get("RSRV_STR5");
	var instId = rowData.get("INST_ID");
	var userId = rowData.get("EUSER_ID");
	
	$("#ERETURN_RATION").val(returnRation);
	$("#ESTART_ACTIVE_DATE").val(startDate);
	$("#EEND_ACTIVE_DATE").val(endDate); 
	$("#EACTIVE_APPROVE_CODE").val(activeCode); 
	$("#INST_ID").val(instId); 
	$("#EUSER_ID").val(userId); 
}

function addReturnRatio(obj){

	var param = ""; 
	if(obj == "2"){//修改
		var userId = $("#EUSER_ID").val();
		var instId = $("#INST_ID").val();
		
		if(userId == null || userId == "" || instId == null || instId == ""){
			//请选择返还比例信息修改后再提交!
			alert("\u8bf7\u9009\u62e9\u8fd4\u8fd8\u6bd4\u4f8b\u4fe1\u606f\u4fee\u6539\u540e\u518d\u63d0\u4ea4!");
			return false;
		}
		if(!$.validate.verifyAll("editForm")){
			return false;
		}
		 
		var enableFlag = $("#cond_ENABLE_FLAG").val();
		if(enableFlag){
			param += '&ENABLE_FLAG=' + enableFlag;
		}
				
		param += '&OPERATION=2';
		
   } else if(obj == "1"){//删除
   		var userId = $("#EUSER_ID").val();
		var instId = $("#INST_ID").val();
		
		if(userId == null || userId == "" || instId == null || instId == ""){
			//请选择要删除的返还比例后再提交!
			alert("\u8bf7\u9009\u62e9\u8981\u5220\u9664\u7684\u8fd4\u8fd8\u6bd4\u4f8b\u540e\u518d\u63d0\u4ea4!");
			return false;
		}
		if(!$.validate.verifyAll("editForm")){
			return false;
		}
		 
		var enableFlag = $("#cond_ENABLE_FLAG").val();
		if(enableFlag){
			param += '&ENABLE_FLAG=' + enableFlag;
		}
		
		param += '&OPERATION=1';
   }
	
	var fileObj = $("#VOUCHER_FILE_LIST");
	if(fileObj && typeof(fileObj) !='undefined' )
	{
		var fileValue = fileObj.val();
		if(fileValue == '')
		{
			//请上传凭证附件!
			alert("\u8bf7\u4e0a\u4f20\u51ed\u8bc1\u9644\u4ef6!");
			return false;
		}
		param += '&VOUCHER_FILE_LIST=' + fileValue;
	}
    var staffObj = $("#AUDIT_STAFF_ID");
	if(staffObj && typeof(staffObj) !='undefined')
	{
		var staffValue = staffObj.val();
		if(staffValue == '')
		{
			//请选择稽核员!
			alert("\u8bf7\u9009\u62e9\u7a3d\u6838\u5458!");
			return false;
		}
		param += '&AUDIT_STAFF_ID=' + staffValue;
	}
		
  	$.beginPageLoading("\u63d0\u4ea4\u4e2d......");//提交中......
  	$.ajax.submit('EditPart','onSubmitBaseTrade', param,'EditPart,ratioPart,AuditInfoPart', function(data){
  		successMessage(data);
		$.endPageLoading(); 
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
  	
}

function successMessage(data){ 
		var result = data.get(0);
		//MessageBox.alert("返还比例处理成功","业务订单号:"+result.get("ORDER_ID"),function(btn){
		MessageBox.alert("\u8fd4\u8fd8\u6bd4\u4f8b\u5904\u7406\u6210\u529f","\u4e1a\u52a1\u8ba2\u5355\u53f7:"+result.get("ORDER_ID"),function(btn){
		redirectToInitTrade();
	});
}

function redirectToInitTrade()
{
	//由于加了稽核凭证信息输入,条件满足的没有办法刷出来,所以重新导向到初始化界面
	$.redirect.toPage('group.returnratio.ReturnRatio','onInitTrade',null);
}

