/* $Id  */


//集团资料查询成功后调用的方法
function selectGroupAfterAction(data) {
   //填充集团客户显示信息
   insertGroupCustInfo(data);
   //重置产品显示信息
   restartProductState();
   //加载集团产品树
   loadGroupProductTree(data);
   //生成合同信息
   renderGrpContractInfos($('#CUST_ID').val(),'',$('#CUST_NAME').val(),$('#GROUP_ID').val());

}
//集团资料查询失败后调用的方法
function selectGroupErrorAfterAction() {
	//清空填充的集团客户信息内容
    clearGroupCustInfo();
    //重置产品显示信息
    restartProductState();
    //清空集团产品树信息
    cleanGroupProductTree();
    //清空合同信息
    cleanGrpContractPart();

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
	//REQ201806080001 关于更新跨省互联网专线业务支撑实施方案的通知--界面集成及计费规则调整  add by hzl
	if(selectProduct=='9912'){
		alert('跨省互联网专线产品业务请到两级界面去办理产!');
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
    //校验是否在选择正确的合同信息
    if(!checkHasProductContract(selectProduct)){
    	return false;
    };
    
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
	cleanGrpUserList();
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

//点击合同下的产品列表事件，自动选中右侧的产品树
function seleContractProductClickAction(contractProductObj){
	productTreeNodeClickAction(contractProductObj.attr('productTypeCode'),contractProductObj.attr('productId'));
 }

//判断EC是否已经同步到平台
function isSynchro(){
	var product_id = $('#GRP_PRODUCT_ID').val();
	var custId = $('#CUST_ID').val();
	if (product_id != null){
		//查询是否已同步
		Wade.httphandler.submit('','com.asiainfo.veris.crm.order.web.frame.csview.group.creategroupuser.BaseInfoHttpHandler', 'checkEcSyn','&CUST_ID='+custId+'&PRODUCT_ID='+product_id,
				function(data){
					var success = data.get("success");/* 判断返回 */
					if(success == 'true'){
						isSelectedProducts();
					}else if(success == 'false'){
						alert("集团客户资料管理"+data.get("msg")+".请先在集团客户资料管理界面同步.");
					}else{
						alert("未定义返回。");
					}
				    $.endPageLoading();
				 },
				function(error_code,error_info,derror){
			        $.endPageLoading();
					showDetailErrorInfo(error_code,error_info,derror);
			    });
	}else{
		alert('请先输入集团客户编码，查询并选择需要办理的集团产品后，再进行此操作！');
	}
}

