 var error = "0"; 
 
 
$(document).ready(function() {
		if(typeof($("#ESOP_PRODUCT_ID").val()) !='undefined'){
		   var strProductset = $('#ESOP_PRODUCTMEBSET').val();
		   if(strProductset != ''){
			   var productSet = $.DatasetList(strProductset);
			   var productSetLen = productSet.length;
				if(productSetLen == 1){
			   		var productData = productSet.get(0);
			   		$("#PRODUCT_ID").val(productData.get('PRODUCT_ID'));
			   		initTreeByProductInfo(productData.get('PRODUCT_NAME'),productData.get('PRODUCT_ID'));
			   		//mytree.init();
			   }else if(productSetLen > 1){
			   		initTreeByProductInfoSet(productSet);
			   }
		   }
	   }
		
});

// 刷新产品树结构 
function refreshProductTree(data) {
	error = "0";
	mytree.empty(true);
	
	var grpId = $("#GROUP_ID").val();
	$("#POP_cond_GROUP_ID").val(grpId);
	
	var resultcode = data.get('X_RESULTCODE','0');
	if(resultcode=='0'){		
		mytree.setParam('CUST_ID',document.getElementById('CUST_ID').value);
		mytree.init();		
	}else{
		showErrorInfo(data.get('X_RESULTTITLE',''),data.get('X_RESULTINFO',''));	
	}
}

function isSelectedGroupProducts(){
	var grpPackageList = $("#GroupTreePart input:checked");
	var selectedFlag = 0; //是否选中集团产品标记 
	grpPackageList.each(function(){		
		var elementID = $(this).val();		
		selectedFlag = 1;
	});
	if(selectedFlag == 0){
		alert("请在集团产品树上选择需要办理的集团产品后，再进行此操作！");
		return false;
	}
	return true;
}

// 选中IMS语音产品显示
function displayIms(productId){	
	
    var obj = $("#ImsInfoPart"); 
	if(productId == "801110"){
	    obj.css("display","")	 
	}else{
		obj.css("display","none")
	}	
}

function validate() {
	
    var custName=$("#CUST_NAME").val();
    if (custName=="") {
 	   alert('集团客户名称为空，请先查询集团客户资料！');
 	   return false;
 	}
    
    var productId=$("#PRODUCT_ID").val();  
	if (productId=='' || productId==undefined) {
	   alert('当前尚未选择开户的产品，请选择需要办理的开户产品！');
	   return false;
	}    
	
	if(error=="1"){	
		alert("产品功能暂未提供！");
		return false;
	}
	
	/**
	 * REQ201801150022_新增IMS号码开户人像比对功能
	 * <br/>
	 * 人像比对权限判断
	 * @author zhuoyingzhi
	 * @date 20180321
	 */
	if(productId == '801110'){
		//IMS语音产品
		var cmpTag = "1";
		$.ajax.submit(null,'isCmpPic','','',
				function(data){ 
			var flag=data.get("CMPTAG");
			if(flag=="0"){ 
				cmpTag = "0";
			}
			$.endPageLoading();
		},function(error_code,error_info){
			$.MessageBox.error(error_code,error_info);
			$.endPageLoading();
		},{
			"async" : false
		});
		
		if(cmpTag == "0"){
			//客户人像比对信息
			var picid = $("#MEM_CUST_INFO_PIC_ID").val();
			
			if(null != picid && picid == "ERROR"){
				MessageBox.error("告警提示","客户"+$("#MEM_CUST_INFO_PIC_STREAM").val(),null, null, null, null);
				return false;
			}
			//客户证件类型
			var psptTypeCode=$("#MEM_CUST_INFO_PSPT_TYPE_CODE").val();
			
			//经办人信息(人像比对信息)
			var agentpicid = $("#MEM_CUST_INFO_AGENT_PIC_ID").val();
			//经办人证件类型
			var agentTypeCode = $("#MEM_CUST_INFO_AGENT_PSPT_TYPE_CODE").val();
			
			if((psptTypeCode == "0" || psptTypeCode == "1" ) && picid == ""){
				
				//经办人名称
			    var  custName = $("#MEM_CUST_INFO_AGENT_CUST_NAME").val();
			    //经办人证件号码
				var  psptId = $("#MEM_CUST_INFO_AGENT_PSPT_ID").val();
				//经办人证件地址
				var  agentPsptAddr= $("#MEM_CUST_INFO_AGENT_PSPT_ADDR").val();
				
				if(agentTypeCode == ''&& custName == '' && psptId == '' && agentPsptAddr== ''){
					MessageBox.error("告警提示","请进行客户或经办人摄像!",null, null, null, null);
					return false;
				}
				
				if((agentTypeCode == "0" || agentTypeCode == "1" ) && agentpicid == ""){
					MessageBox.error("告警提示","请进行客户或经办人摄像!",null, null, null, null);
					return false;
				}
			}
			
			if(null != agentpicid && agentpicid == "ERROR"){
				MessageBox.error("告警提示","经办人"+$("#MEM_CUST_INFO_AGENT_PIC_STREAM").val(),null, null, null, null);
				return false;
			}
			
			if((agentTypeCode == "0" || agentTypeCode == "1" ) && agentpicid == ""){
				MessageBox.error("告警提示","请进行经办人摄像!",null, null, null, null);
				return false;
			}
		}
	}	
	/*************************REQ201801150022_新增IMS号码开户人像比对功能_end************************/
	
	return true;
}

//选择产品ID的校验
function checkProduct(productId){
  
	//勾选
 	$("#PRODUCT_ID").val(productId);
 	var custId = $("#CUST_ID").val();
 	
    var ifcheck=true;
    
   	$.beginPageLoading();   	
    $.ajax.submit("", "queryProductInfo", "&PRODUCT_ID=" + productId + "&CUST_ID=" + custId, null, 
	    function(data){
	    	$.endPageLoading();
	    	displayIms(productId);
	    	error = "0";
		},
		function(error_code,error_info,derror){
			$.endPageLoading();
			error = "1"; //产品功能未提供
			ifcheck=false;
			showDetailErrorInfo(error_code,error_info,derror);
	    }
	 );
	    
	return ifcheck;
}

function psptChange(){
  var type = $("#MEM_CUST_INFO_PSPT_TYPE_CODE").val();
  
  if( type == "0"){
     $("#MEM_CUST_INFO_PSPT_ID").attr("datatype","pspt");
  }else{
  	 $("#MEM_CUST_INFO_PSPT_ID").attr("datatype","");
  }
}

/**
 * 校验证件类型和证件号码
 */
function validPspt()
{
	if($("#MEM_CUST_INFO_PSPT_ID").val()=='')
		return true;
	var busi_licence_type_obj = $("#MEM_CUST_INFO_PSPT_TYPE_CODE");
    var lic_type = busi_licence_type_obj.val();
    if(lic_type==null || lic_type=='')
    {
    	MessageBox.alert("证件类型不能为空");
         return false;  
    }
    else if(lic_type == 'E')
    {    
    	var lic_no =  $("#MEM_CUST_INFO_PSPT_ID").val();
        if(lic_no.length!=15&&lic_no.length!=18 &&lic_no.length!=13&&lic_no.length!=20&&lic_no.length!=22&&lic_no.length!=24){
        	MessageBox.alert("营业执照类型校验：营业执照证件号码长度需满足13位、15位、18位、20位、22位或24位！当前："+lic_no.length+"位。");
        	var obj =  $("#MEM_CUST_INFO_PSPT_ID");
        	obj.val("");
        	obj.focus();
       	 return false;
        }
    }
    return true;
}
