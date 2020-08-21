/* $Id */
function validate() {
    var selectProduct=$("#grpProductTreeSelected").val();
	if (selectProduct=='[]') {
	   alert('当前尚未选择集团产品，请选择需要办理的集团产品！');
	   return false;
	}
	
    var custName=$("#CUST_NAME").val();	   
	if (custName=="") {
	   alert('请先查询集团客户资料!');
	   return false;
	}
	
    return true;    
}

function queryCompProduct(node) {
    var obj=$("#CompProductInfoPart");
    obj.css('display','block');
    ajaxSubmit(this, 'queryProductInfo','&PRODUCT_ID='+node.id,'CompProductInfoPart');
}

//---------

//集团资料查询成功后调用的方法
function selectGroupAfterAction(data) {
 //填充集团客户显示信息
 insertGroupCustInfo(data);
 //重置产品显示信息
 initRightElement();
 //加载集团产品树
 loadGroupProductTreeOrdered($('#CUST_ID').val(),$('#GRP_USER_ID').val());

}
//集团资料查询失败后调用的方法
function selectGroupErrorAfterAction() {
	//清空填充的集团客户信息内容
  clearGroupCustInfo();
  //重置右侧产品显示信息
  initRightElement();
  //清空集团产品树信息
  cleanGroupProductTree();

}
//通过集团号码查询集团资料成功后调用的方法
function selectGroupBySnAfterAction(data)
{	
	//重置右侧产品显示信息
  initRightElement(); 
  //清空集团产品树信息
  cleanGroupProductTree();
  
  var productExplainInfo =  data.get("PRODUCT_DESC_INFO");
  var userInfo =  data.get("GRP_USER_INFO");
  var groupInfo =  data.get("GRP_CUST_INFO");
  
  //生成三户数据
  if(userInfo != undefined && userInfo != null){
		insertGrpUserList(userInfo);
		insertSelGroupUserInfo(userInfo);
	}
	if(groupInfo != undefined && groupInfo != null){
		insertGroupCustInfo(groupInfo);
	}
	if(productExplainInfo != undefined && productExplainInfo != null){
		insertProductExplainInfo(productExplainInfo);
	}
	
  //初始树
	initTreeByProductInfo($('#GRP_PRODUCT_NAME').val(),$('#GRP_PRODUCT_ID').val());
//	renderGrpContractInfos($('#CUST_ID').val(),$('#GRP_USER_ID').val());
	//显示右侧区域
	seeCompProductRefreshPart();
		
	
			
}

//SN集团资料查询失败后调用的方法
function selectGroupBySnErrorAfterAction() {
	//清空填充的集团客户信息内容
  clearGroupCustInfo();
  //重置右侧产品显示信息
  initRightElement();
  //清空集团产品树信息
  cleanGroupProductTree();

}
//下一步验证方法
function isSelectedProducts() {
	
	 var custName=$('#CUST_NAME').val();	   
	 if (custName=="") {
	   alert('请先查询集团客户资料!');
	   return false;
	 }
	 var selectProduct=$('#GRP_PRODUCT_ID').val();
	 if (selectProduct=='') {
	   alert('请先选择需要办理的集团产品！');
	   return false;
	 }
 	var userId=$('#GRP_USER_ID').val();
	 if (userId=='') {
	   alert('请选择需要办理业务的用户！');
	   return false;
	 }
	
	if(!$.validate.verifyAll('baseInfoPart')) return false;
	
	$.beginPageLoading('业务验证中....');
	var checkParam = '&CUST_ID='+$('#CUST_ID').val() +'&USER_ID='+userId +'&PRODUCT_ID='+selectProduct +'&EPARCHY_CODE='+$('#GRP_USER_EPARCHYCODE').val();
	pageFlowRuleCheck('com.asiainfo.veris.crm.order.web.frame.csview.group.rule.ChangeUserElementRule','checkBaseInfoRule',checkParam);
	
	return false;
   
}

function queryProduct(node)
{
	//通过号码查询的节点不需要再重新刷新用户信息
  var nodeid = node.id;
  if(nodeid=='USER_NODE_TREE'){
  	 return true;
  }
  initRightElement();
  $.beginPageLoading(); 
  var ifcheck =true;
  Wade.httphandler.submit('','com.asiainfo.veris.crm.order.web.frame.csview.group.changeuserelement.BaseInfoHttpHandler', 'queryProductInfo','&PRODUCT_ID='+node.value+'&CUST_ID='+$('#CUST_ID').val(),
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
			var seleUserInfo = data.get("SEL_USER_INFO");
			if(productDescInfo != undefined && productDescInfo != null){
					insertProductExplainInfo(productDescInfo);
			}
			if(grpUserList != undefined && grpUserList != null){
					insertGrpUserList(grpUserList);
			}
			if(seleUserInfo != undefined && seleUserInfo != null){
					insertSelGroupUserInfo(seleUserInfo);
			}
			
			if($('#GRP_USER_ID').val() != null && $('#GRP_USER_ID').val() != ''){
//		    	renderGrpContractInfos($('#CUST_ID').val(),$('#GRP_USER_ID').val());
		    }
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

function chooseUserProducts(obj){
	if(!chooseUserProductsBase(obj)) return ;
//	renderGrpContractInfos($('#CUST_ID').val(),$('#GRP_USER_ID').val());
}
 
function initRightElement(){
	hiddenCompProductRefreshPart();
	cleanGrpUserList();
//	cleanGrpContractPart(); 	
}

//隐藏产品右侧区域
function hiddenCompProductRefreshPart(){
	var obj=$('#CompProductRefreshPart'); 
  	obj.css("display","none"); 
}

//显示产品右侧区域
function seeCompProductRefreshPart(){
	var comp=$('#CompProductRefreshPart');
	comp.css("display","");
}
