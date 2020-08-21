function getGroupInfo()
{
    var group = $("#cond_GROUP_ID").val;
    
    if(!$.validate.verifyField($("#cond_GROUP_ID"))) 
    {
       return false;
    }  
	$.beginPageLoading("\u6570\u636e\u67e5\u8be2\u4e2d......");//���ݲ�ѯ��......
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

// �ύǰУ��
function onSubmitBaseTradeCheck(){
	
	var userId = $("#USER_ID").val();
	if(userId == null || userId == ""){
		//��ѡ���Ų�Ʒ!
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
			//���ϴ�ƾ֤����!
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
			//��ѡ�����Ա!
			alert("\u8bf7\u9009\u62e9\u7a3d\u6838\u5458!");
			return false;
		}
	}
    					
	return true;
}

//��������ѡ�����
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

//�����޸�ɾ��ѡ�������
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
	
	$.beginPageLoading("\u6570\u636e\u67e5\u8be2\u4e2d......");//���ݲ�ѯ��......
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
	if(obj == "2"){//�޸�
		var userId = $("#EUSER_ID").val();
		var instId = $("#INST_ID").val();
		
		if(userId == null || userId == "" || instId == null || instId == ""){
			//��ѡ�񷵻�������Ϣ�޸ĺ����ύ!
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
		
   } else if(obj == "1"){//ɾ��
   		var userId = $("#EUSER_ID").val();
		var instId = $("#INST_ID").val();
		
		if(userId == null || userId == "" || instId == null || instId == ""){
			//��ѡ��Ҫɾ���ķ������������ύ!
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
			//���ϴ�ƾ֤����!
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
			//��ѡ�����Ա!
			alert("\u8bf7\u9009\u62e9\u7a3d\u6838\u5458!");
			return false;
		}
		param += '&AUDIT_STAFF_ID=' + staffValue;
	}
		
  	$.beginPageLoading("\u63d0\u4ea4\u4e2d......");//�ύ��......
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
		//MessageBox.alert("������������ɹ�","ҵ�񶩵���:"+result.get("ORDER_ID"),function(btn){
		MessageBox.alert("\u8fd4\u8fd8\u6bd4\u4f8b\u5904\u7406\u6210\u529f","\u4e1a\u52a1\u8ba2\u5355\u53f7:"+result.get("ORDER_ID"),function(btn){
		redirectToInitTrade();
	});
}

function redirectToInitTrade()
{
	//���ڼ��˻���ƾ֤��Ϣ����,���������û�а취ˢ����,�������µ��򵽳�ʼ������
	$.redirect.toPage('group.returnratio.ReturnRatio','onInitTrade',null);
}

