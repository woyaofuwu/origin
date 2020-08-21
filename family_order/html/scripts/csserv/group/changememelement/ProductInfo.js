
function initProductInfo() {
	
}

//下一步验证事件
function productInfoNextCheck()
{
	
	if(typeof(validateParamPage)=="function"){
		mytab.switchTo("产品参数信息");
		var result = $.validate.verifyAll("prama")
		if(!result){
			return false;
		}
		if (!validateParamPage('ChgMb')) return false
	}
	
	mytab.switchTo("产品信息");
	if(!selectedElements.checkForcePackage())
		return false;
	
	var tempElements = selectedElements.selectedEls;
    if(!tempElements)
   		return false;
    
    var flag=1;
    if(flag==1){
        flag=0;
    var num=0;
    var length= selectedElements.getSubmitData().length;
    for(var i=0;i<length;i++){
        temp=selectedElements.getSubmitData().get(i);
        //MODIFY_TAG:0表示新增，1表示删除;新增的才需要判断
        if(temp.get("MODIFY_TAG")=="0"){
        	if(temp.get("isNeedChange")==1){
                var endDate = temp.get("END_DATE").substring(0,10);
                if(endDate == '2050-12-31'){
                 	num++;
                }
                if(temp.get("ELEMENT_ID")==84017249){
                	alert("如选择办理达量限速功能，使用超过套餐内的流量，则将进行限速。");
                }
                var now = new Date();
        		var startDate = temp.get("START_DATE");
                if(startDate.substring(0,10) < now.format('yyyy-MM-dd')){
                	alert("开始时间不能小于当前时间");
                	return false;
                }
            }
        }
        
    }
    if(num!=0) {alert("结束日期为2050年12月31日，请确认是否修改");}
    }
    
    for (var i=0;i<tempElements.length ;i++ )
    {
     if(tempElements.get(i,"MODIFY_TAG")=='0'&&tempElements.get(i,"ATTR_PARAM_TYPE")=='9'&&tempElements.get(i,"ATTR_PARAM").get(0,"PARAM_VERIFY_SUCC")=='false')
	 {
		alert(tempElements.get(i,"ELEMENT_NAME")+",缺少服务参数，请补全相关服务参数信息！"); 
		$('#'+tempElements.get(i,'ITEM_INDEX')+'_ATTRPARAM').click(); 
		return false ; 
      }
		    
    }
    if(!$.validate.verifyAll('productInfoPart')) return false; 
    
    if($("#MEB_VOUCHER_FILE_LIST")&&$("#MEB_VOUCHER_FILE_LIST").val() == ""){
		alert("请上传凭证信息");
		mytab.switchTo("凭证信息");
		return false;
	}
	if($("#AUDIT_STAFF_ID") && $("#AUDIT_STAFF_ID").val() == ""){
		alert("请选择稽核人员！");
		mytab.switchTo("凭证信息");
		return false;
	}
    
    $.beginPageLoading('业务验证中....');
    var checkParam = '&SERIAL_NUMBER='+$('#MEB_SERIAL_NUMBER').val()+'&PRODUCT_ID='+$('#PRODUCT_ID').val()+'&USER_ID_B='+$('#MEB_USER_ID').val()+'&USER_ID='+$('#GRP_USER_ID').val() +'&EPARCHY_CODE_B='+$("#MEB_EPARCHY_CODE").val()+'&ALL_SELECTED_ELEMENTS='+ tempElements ;
	pageFlowRuleCheck('com.asiainfo.veris.crm.order.web.frame.csview.group.rule.ChangeMemElementRule','checkProductInfoRule',checkParam);
	
	return false;
} 

//下一步验证成功后执行的方法
function pageFlowCheckAfterAction(){
	var submitDatas = selectedElements.getSubmitData();
	if(!submitDatas)
		return false
	$('#SELECTED_ELEMENTS').val(submitDatas);
	return true;
}

//点包列表后展示包下元素时增加查询参数
function pkgListAfterSelectAction(package){
	var selfParam = "&GRP_USER_ID=" + $('#GRP_USER_ID').val() + "&PRODUCT_ID=" + $('#PRODUCT_ID').val();
	var eparchyCode = $('#MEB_EPARCHY_CODE').val();
	pkgElementList.renderComponent(package,eparchyCode,selfParam);
}
/**
 * 设置ADCMAS弹出的服务参数页面URL值
 * 
 */
(function(){$.extend(SelectedElements.prototype,{
	buildPopupAttrParam: function(data){
	        var eparchyCode=$("#MEB_EPARCHY_CODE").val();
	        var param="&MEB_EPARCHY_CODE="+eparchyCode;
	        return param;
	       }
	});
})();

function productTabSwitchAction(ptitle,title){
	
	if ($('#elementPanel').length != 0  && $('#elementPanel').css('display') =='block'){
		$('#elementPanel').css('display','none');
	}
	
	return true;
}  

function getOtherParam()
{
    var grpUserId=$("#GRP_USER_ID").val();
    var grpProductId=$("#PRODUCT_ID").val();
    var mebProductId=$("#MEB_PRODUCT_ID").val();
    
    var param="&GRP_USER_ID="+grpUserId+"&GRP_PRODUCT_ID="+grpProductId+"&PRODUCT_ID="+mebProductId;
    return param;
} 