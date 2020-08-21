/* $Id  */


//集团资料查询成功后调用的方法
function selectGroupAfterAction(data) {
   //填充集团客户显示信息
   insertGroupCustInfo(data);
   //重置产品显示信息
   restartProductState();
   //加载集团产品树
   loadGroupProductTree(data);

}
//集团资料查询失败后调用的方法
function selectGroupErrorAfterAction() {
	//清空填充的集团客户信息内容
    clearGroupCustInfo();
    //重置产品显示信息
    restartProductState();
    //清空集团产品树信息
    cleanGroupProductTree();

}

//点击页面流的下一步 验证方法
function isSelectedProducts() {
	
    var custName=$("#CUST_NAME").val();
	var grpUserEparchyCode=$("#GRP_USER_EPARCHYCODE").val();
    var selectProduct=$("#GRP_PRODUCT_ID").val();
	if (selectProduct=='') {
	   alert('请先输入集团客户编码，查询并选择需要办理的集团产品后，再进行此操作！');
	   return false;
	}

	if (custName=="") {
	   alert('请先查询集团客户资料!');
	   return false;
	}

   var result =  $.validate.verifyAll('baseInfoPart');
   if(!result){
   	   return false;
    }
   
    
    $.beginPageLoading('业务验证中....');
	var result = pageFlowRuleCheck('com.asiainfo.veris.crm.order.web.frame.csview.group.rule.CreateGroupUserRule','checkBaseInfoRule','&CUST_ID='+$("#CUST_ID").val()+'&PRODUCT_ID='+selectProduct+'&EPARCHY_CODE='+grpUserEparchyCode);
	
	return false;
}

//选择产品树上的某个产品后触发的方法
function queryProduct(node)
{
    var nodeid = node.id;
    if(nodeid=='USER_NODE_TREE'){
    	 return true;
    }
    restartProductState();
    var productId = node.value;
    var custId = $('#CUST_ID').val();
    return chooseProductAfterAction(productId,custId);
}

function chooseProductAfterAction(productId,custId){
	
    var ifcheck =true;
   	$.beginPageLoading();
   	
   	Wade.httphandler.submit('','com.asiainfo.veris.crm.order.web.frame.csview.group.creategroupuser.BaseInfoHttpHandler', 'queryProductInfo','&PRODUCT_ID='+productId+'&CUST_ID='+custId,
		function(data){
			if(data == null)
				return;
			
			var x_resultcode = data.get("x_resultcode","0");
			if(x_resultcode == '-1'){
				$.endPageLoading();
				showErrorInfo(error_code,error_info);
				return;
		    }
		    
		    var productDescInfo = data.get("PRODUCT_DESC_INFO");
			var grpUserList = data.get("GRP_USER_LIST");
			if(productDescInfo != undefined && productDescInfo != null){
					insertProductExplainInfo(productDescInfo);
			}
			if(grpUserList != undefined && grpUserList != null){
					insertGrpUserList(grpUserList);
			}
			$('#GRP_PRODUCT_ID').val(productId);
			seeCompProductRefreshPart();
		    $.endPageLoading();
	        
		 },
		function(error_code,error_info,derror){
			ifcheck= false;
	        $.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
	    });
   	
   	return ifcheck;
}


//重置产品信息状态
function restartProductState(){
	hiddenCompProductRefreshPart();
	$("#grpuserlist_tbody").html('');
    $('#GRP_PRODUCT_ID').val('');
 }
 
 //隐藏产品右侧区域
function hiddenCompProductRefreshPart(){
	var obj=$('#CompProductInfoPart'); 
  	obj.css("display","none"); 
}

//显示产品右侧区域
function seeCompProductRefreshPart(){
	var comp=$('#CompProductInfoPart');
	comp.css("display","");
}



//生成集团用户列表信息
function insertGrpUserList(grpUserList){
	$("#grpuserlist_tbody").html('');
	if(grpUserList instanceof $.DatasetList){
		grpUserList.each(function(item,idx){
			
			$("#grpuserlist_tbody").prepend(makeGrpUserHtml(item));
				
		});
	}else if(grpUserList instanceof $.DataMap){
		$("#grpuserlist_tbody").prepend(makeGrpUserHtml(grpUserList));
	}
}
//生成集团用户列表信息
function makeGrpUserHtml(item){
	var userId = item.get('USER_ID');
	var sn = item.get('SERIAL_NUMBER');
	var openDate = item.get('OPEN_DATE');
	var clickAction = $('#GRPUSERLIST_CLICK_ACTION').val();
	var ifHasCheck = $('#GRPUSERLIST_IF_HAS_CHECK').val();
	var html="";
	var delAction = "$.popupPageExternal('csserv.group.simpleBusi.destroygroupuser.SimpleDestroyGroupUser','initial','&cond_GROUP_SERIAL_NUMBER="+sn+"','集团退订',880,550,'POP_cond_GROUP_ID')";
	var updateAction = "$.popupPageExternal('csserv.group.simpleBusi.changegroupuser.SimpleChangeGroupUser','initial','&cond_GROUP_SERIAL_NUMBER="+sn+"','集团变更',880,550,'POP_cond_GROUP_ID')";
	
	html += '<tr>';
	
	html += '<td >' + userId+ '</td>';
	html += '<td >' + sn+ '</td>';
	html += '<td >' + openDate+ '</td>';
	html += '<td > <a href="javascript:void(0);" sn="'+sn+'" onclick="'+updateAction+'" ><span>修改</span></a></td>';
	html += '<td > <a href="javascript:void(0);" sn="'+sn+'" onclick="'+delAction+'" ><span>删除</span></a> </td>';
	
	html += '</tr>';
	
	return html;
}


