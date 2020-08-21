function refreshContractPartCustId(){
	cleanGrpContractPart();
	var custId = $('#SELE_CONTRACTPRODUCT_CUST_ID').val();
	if (custId == ""){
		$.showWarnMessage('请输入正确的集团编码！','集团编码信息不能为空');
		return false;
	}

	Wade.httphandler.submit("","com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.selecontractproduct.SeleContractProductHttpHandler","qryContractByCustIdForGrp","&CUST_ID="+custId,
    	function(data){
    		if(data != null){
				insertContractList(data);
			}

	    },
	    function(error_code,error_info,derror){
    		showDetailErrorInfo(error_code,error_info,derror);
		},{async:false});
}


function refreshContractPartCustIdProductId() {
    cleanGrpContractPart();
    var custId = $('#SELE_CONTRACTPRODUCT_CUST_ID').val();
    var productId = $('#SELE_CONTRACTPRODUCT_PRODUCT_ID').val();
    var eos = $('#SELE_CONTRACTPRODUCT_EOS').val();
    if (custId == ""){
        $.showWarnMessage('请输入正确的集团编码！','集团编码信息不能为空');
        return false;
    }

    Wade.httphandler.submit("","com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.selecontractproduct.SeleContractProductHttpHandler","qryContractByCustIdProductIdForGrp","&CUST_ID="+custId+"&PRODUCT_ID="+productId+"&EOS="+eos,
        function(data){
            if(data != null){
                insertContractList(data);
            }

        },
        function(error_code,error_info,derror){
            showDetailErrorInfo(error_code,error_info,derror);
        },{async:false});
}

function refreshContractPartUserId(){
	cleanGrpContractPart();
	var userId = $('#SELE_CONTRACTPRODUCT_USER_ID').val();
	if (userId == ""){
		$.showWarnMessage('获取集团用户ID出错！','集团用户ID不能为空');
		return false;
	}
	Wade.httphandler.submit("","com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.selecontractproduct.SeleContractProductHttpHandler","qryContractByUserIdForGrp","&USER_ID="+userId,
    	function(data){
    		if(data != null){
    			var contractList = data.get("GRP_CONTRACT_LIST")
    			var contractInfo = data.get("GRP_CONTRACT_INFO");
				var contractProductInfo = data.get("GRP_CONTRACT_PRODUCT_INFOS");
				if(contractList != undefined && contractList != null){
					insertContractList(contractList);
				}
				if(contractInfo != undefined && contractInfo != null){
					insertContractInfo(contractInfo);
				}
				if(contractProductInfo != undefined && contractProductInfo != null){
					insertContractProductList(contractProductInfo);
				}

			}

	    },
	    function(error_code,error_info,derror){
    		showDetailErrorInfo(error_code,error_info,derror);
	    },{async:false});
}


function renderGrpContractInfos(custId,userId,custName,groupId,productId,eos){
	if(userId == undefined || userId == ''){
		$('#SELE_CONTRACTPRODUCT_CUST_ID').val(custId);
		$('#SELE_CONTRACTPRODUCT_CUST_NAME').val(custName);
		$('#SELE_CONTRACTPRODUCT_GROUP_ID').val(groupId);
        if(productId == '7010' || productId =='7011' || productId=='7012'){
            $('#SELE_CONTRACTPRODUCT_PRODUCT_ID').val(productId);
            $('#SELE_CONTRACTPRODUCT_EOS').val(eos);
            refreshContractPartCustIdProductId();
        } else{
            refreshContractPartCustId();
        }
	}else{
		$('#SELE_CONTRACTPRODUCT_CUST_ID').val(custId);
		$('#SELE_CONTRACTPRODUCT_USER_ID').val(userId);
		$('#SELE_CONTRACTPRODUCT_CUST_NAME').val(custName);
		$('#SELE_CONTRACTPRODUCT_GROUP_ID').val(groupId);
        if(productId == '7010' || productId =='7011' || productId=='7012') {
            $('#SELE_CONTRACTPRODUCT_PRODUCT_ID').val(productId);
            $('#SELE_CONTRACTPRODUCT_EOS').val(eos);
            refreshContractPartCustIdProductId();
        } else {
            refreshContractPartUserId();
        }
	}
}

//初始合同组件状态
function cleanGrpContractPart(){
	$("#contractProductBody").html('');
	cleanContractInfo();
	var roleObj = $('#SELE_CONTRACTPRODUCT_CONTRACT_ID');
	roleObj.html('');
	var roleSelInnerHTML = [];
	roleSelInnerHTML.push('<option value="" title="--请选择--">--请选择--</option>');
	$.insertHtml('afterbegin',roleObj,roleSelInnerHTML.join(""));
}

function seleGrpContractAfterAction(){
	var contractId = $('#SELE_CONTRACTPRODUCT_CONTRACT_ID').val();
	$.beginPageLoading();
	$("#contractProductBody").html('');
	Wade.httphandler.submit("","com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.selecontractproduct.SeleContractProductHttpHandler","qryContractProductByContractIdForGrp","&CONTRACT_ID="+contractId,
    	function(data){
    		if(data != null){
				var contractInfo = data.get("GRP_CONTRACT_INFO");
				var contractProductInfo = data.get("GRP_CONTRACT_PRODUCT_INFOS");
				if(contractInfo != undefined && contractInfo != null){
					insertContractInfo(contractInfo);
				}
				if(contractProductInfo != undefined && contractProductInfo != null){
					insertContractProductList(contractProductInfo);
				}
			}
			$.endPageLoading();

	    },
	    function(error_code,error_info,derror){
    		showDetailErrorInfo(error_code,error_info,derror);
			$.endPageLoading();

	    });

}
function insertContractList(data){
	var leng = data.length;
	var roleObj = $('#SELE_CONTRACTPRODUCT_CONTRACT_ID');
	roleObj.html('');
	var roleSelInnerHTML = [];
	roleSelInnerHTML.push('<option value="" title="--请选择--">--请选择--</option>');
	for(var i =0;i<leng;i++){
		var info = data.get(i);
		var contractId = info.get("CONTRACT_ID");
		var contractName = info.get("CONTRACT_NAME");
		roleSelInnerHTML.push( '<option value="'+contractId+'">'+contractId+'|'+contractName+'</option>');
	}
	$.insertHtml('afterbegin',roleObj,roleSelInnerHTML.join(""));
}
function insertContractInfo(data){
	var contractName  = data.get("CONTRACT_NAME");
	$('#SELE_CONTRACTPRODUCT_CONTRACT_NAME').val(contractName);

	var contractType  = data.get("CONTRACT_TYPE");
	$('#SELE_CONTRACTPRODUCT_CONTRACT_TYPE').val(contractType);

	var contractConman  = data.get("CONTRACT_CONMAN");
	$('#SELE_CONTRACTPRODUCT_CONTRACT_CONMAN').val(contractConman);

	var contractConman  = data.get("CONTRACT_CONMAN");
	$('#SELE_CONTRACTPRODUCT_CONTRACT_CONMAN').val(contractConman);

	var contractManger  = data.get("CONTRACT_MANAGER");
	$('#SELE_CONTRACTPRODUCT_CONTRACT_MANAGER').val(contractManger);

	var contractWriteDate  = data.get("CONTRACT_WRITE_DATE");
	$('#SELE_CONTRACTPRODUCT_CONTRACT_WRITE_DATE').val(contractWriteDate);

	var contractEndDate  = data.get("CONTRACT_END_DATE");
	$('#SELE_CONTRACTPRODUCT_CONTRACT_END_DATE').val(contractEndDate);

	var contractWriter  = data.get("CONTRACT_WRITER");
	$('#SELE_CONTRACTPRODUCT_CONTRACT_WRITER').val(contractWriter);

	var contractContent  = data.get("CONTRACT_CONTENT");
	$('#SELE_CONTRACTPRODUCT_CONTRACT_CONTENT').val(contractContent);

	var contractState  = data.get("CONTRACT_STATE");
	$('#SELE_CONTRACTPRODUCT_CONTRACT_STATE').val(contractState);

	var contractWriter  = data.get("CONTRACT_WRITER");
	$('#SELE_CONTRACTPRODUCT_CONTRACT_WRITER').val(contractWriter);

	var contractWriter  = data.get("CONTRACT_WRITER");
	$('#SELE_CONTRACTPRODUCT_CONTRACT_WRITER').val(contractWriter);

	var contractWriter  = data.get("CONTRACT_WRITER");
	$('#SELE_CONTRACTPRODUCT_CONTRACT_WRITER').val(contractWriter);

	var contractId  = data.get("CONTRACT_ID");
	$('#SELE_CONTRACTPRODUCT_CONTRACT_ID').val(contractId);

}

function cleanContractInfo(){
	$('#SELE_CONTRACTPRODUCT_CONTRACT_NAME').val('');

	$('#SELE_CONTRACTPRODUCT_CONTRACT_TYPE').val('');

	$('#SELE_CONTRACTPRODUCT_CONTRACT_CONMAN').val('');

	$('#SELE_CONTRACTPRODUCT_CONTRACT_CONMAN').val('');

	$('#SELE_CONTRACTPRODUCT_CONTRACT_MANAGER').val('');

	$('#SELE_CONTRACTPRODUCT_CONTRACT_WRITE_DATE').val('');

	$('#SELE_CONTRACTPRODUCT_CONTRACT_END_DATE').val('');

	$('#SELE_CONTRACTPRODUCT_CONTRACT_WRITER').val('');

	$('#SELE_CONTRACTPRODUCT_CONTRACT_CONTENT').val('');

	$('#SELE_CONTRACTPRODUCT_CONTRACT_STATE').val('');

	$('#SELE_CONTRACTPRODUCT_CONTRACT_WRITER').val('');

	$('#SELE_CONTRACTPRODUCT_CONTRACT_WRITER').val('');

	$('#SELE_CONTRACTPRODUCT_CONTRACT_WRITER').val('');
}

function insertContractProductList(conPoductList){
	conPoductList.each(function(item,idx){

		$("#contractProductBody").prepend(makeContractProductHtml(item));

	});
}
function makeContractProductHtml(item){
	var contractId = item.get('CONTRACT_ID');
	var productTypeCode = item.get('PRODUCT_TYPE_CODE');
	var productId = item.get('PRODUCT_ID');
	var productName = item.get('PRODUCT_NAME');
	var startDate = item.get('START_DATE');
	var endDate = item.get('END_DATE');
	var clickAction = $('#SELE_CONTRACTPRODUCT_CLICKPRODUCT_ACTION').val();
	var html="";

	if(clickAction != ''){
		html += "<tr contractId ='"+contractId+"' productTypeCode ='"+productTypeCode+"' productId ='"+productId+"' productName ='"+productName+"' startDate ='"+startDate+"' onclick='"+clickAction+"'>";
	}else{
		html += '<tr>';
	}
	html += '<td style="display:none">3</td>';
	html += '<td >' + contractId+ '</td>';
	html += '<td style="display:none">' + productTypeCode+ '</td>';
	html += '<td style="display:none" productId="'+productId+'">' + productId+ '</td>';
	html += '<td >' + productName+ '</td>';
	html += '<td >' + startDate+ '</td>';
	html += '<td >' + endDate+ '</td>';
	html += '</tr>';

	return html;
}

function createContract(){
	var custId = $("#SELE_CONTRACTPRODUCT_CUST_ID").val();
	var groupId = $("#SELE_CONTRACTPRODUCT_GROUP_ID").val();
	var custName = $("#SELE_CONTRACTPRODUCT_CUST_NAME").val()
    var productId = $("#SELE_CONTRACTPRODUCT_PRODUCT_ID").val();

    if(productId =='7010' || productId == '7011' || productId =='7012'){
        alert('数据专线的合同只能通过审批流程新增！');
        return ;
    } else {
        $.popupPageExternal('customer.cs.groupcontract.addcontract.AddContract', 'init', '&refresh=true&typeCode=group&CUST_ID='+custId+'&GROUP_ID='+groupId, '合同新增', '820', '650', null, null, subsys_cfg.custmanm, false);
    }
}

function updateContract(){
	var custId = $("#SELE_CONTRACTPRODUCT_CUST_ID").val();
    var contractId = $("#SELE_CONTRACTPRODUCT_CONTRACT_ID").val();
    if(custId == ''){
    	alert('请选择合同后再进行维护合同的操作！');
    	return ;
    }
    var productId = $("#SELE_CONTRACTPRODUCT_PRODUCT_ID").val();

    if(productId =='7010' || productId == '7011' || productId =='7012'){
        alert('数据专线的合同只能通过审批流程修改！');
        return ;
    } else {
        $.popupPageExternal('customer.cs.groupcontract.modifycontract.EditContract', '', '&refresh=true&typeCode=group&CUST_ID=' + custId + '&CONTRACT_ID=' + contractId, '维护合同', '820', '650', null, null, subsys_cfg.custmanm, false);
    }
}

function refreshCont(){
	$('#SELE_CONTRACTPRODUCT_USER_ID').val('');

    var productId = $('#SELE_CONTRACTPRODUCT_PRODUCT_ID').val();

    if(productId =='7010' || productId == '7011' || productId =='7012') {
        refreshContractPartCustIdProductId();
    } else {
        refreshContractPartCustId();
    }


}

//验证合同中是否有产品conProductId
function checkHasProductContract(conProductId){
	var contractId =$('#SELE_CONTRACTPRODUCT_CONTRACT_ID').val();
	if(contractId == ''){
		alert('您没有选择相应合同，请确认是否存在合同信息？');
		return false;
	}

	var checkproductlist = $('#contractProductBody td[productId='+conProductId+']');
	if(checkproductlist == null || checkproductlist.length==0){
		alert('所选择的产品在合同中没有找到，请确认是否选择正确的合同！');
		return false;
	}

	
	var contractDay = $('#SELE_CONTRACTPRODUCT_CONTRACT_END_DATE').val();
	var now = new Date();
	var year  = now.getFullYear();
	var month = now.getMonth() + 1;
	var day   = now.getDate();
	var hour  = now.getHours();
	var min   = now.getMinutes();
	var sec   = now.getSeconds();
	month = (parseInt(month) < 10) ? ("0" + month) : (month);
	day   = (parseInt(day)   < 10) ? ("0" + day )  : (day);
	hour  = (parseInt(hour)  < 10) ? ("0" + hour)  : (hour);
	min   = (parseInt(min)   < 10) ? ("0" + min)   : (min);
	sec   = (parseInt(sec)   < 10) ? ("0" + sec)   : (sec);
	var toDay = year + "-" + month + "-" + day + " " + hour + ":" + min + ":" + sec;
  	if(toDay > contractDay) 
  	{
       	alert("该集团产品合同已过期，请续约后再操作！","提示信息！");
       	return false;
  	}

  	
	return true;
}

function hiddenPopupSeleContract(){
	$('#popupSeleContract').css('display','none');
}

function displayPopupSeleContract(){
	$('#popupSeleContract').css('display','');
}

function delPopupSeleContract(){
	$('#popupSeleContract').html('');

}
