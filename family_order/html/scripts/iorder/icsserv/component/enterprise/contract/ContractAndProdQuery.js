var CHOOSE_CONTRACT_LIST = new $.DatasetList();

//查询合同产品信息
function queryContractProductInfo1(obj)
{
	var contractId = obj.getAttribute("CONTRACT_ID");
	var custInfo = window.parent.m_group.getInfo().get("CUST_INFO");
	var custId= custInfo.get("CUST_ID");
	$.ajax.submit("", "", "&flag=back&contractId="+contractId+"&custId="+custId, "",function(){});
}
var  popupContractId='popup';  	
//查询合同信息
function queryContractInfo()
{
	var custInfo = $.enterpriseLogin.getInfo().get("CUST_INFO");
	var custId = custInfo.get("CUST_ID");
	var groupId = $.enterpriseLogin.getInfo().get("GROUP_ID");
	$.beginPageLoading("数据查询中......");
	$.ajax.submit("", "", "&flag=back&custId="+custId+"&groupId="+groupId, "contractInfo", function(data){
		$.endPageLoading();
		var hasContract = data.get("HAS_CONTRACT");
		if(hasContract == "false")
		{
			MessageBox.alert("提示信息", "没有查询到合同信息，如需新增合同，请点击新增合同按钮！", function(btn){
				if("ext1" == btn){
					var custName = custInfo.get("CUST_NAME");
					openNav('集团合同新增', 'customer.cs.groupcontract.addcontract.AddContract','init', '&refresh=true&typeCode=group&CUST_ID='+custId+'&GROUP_ID='+groupId+'&CUST_NAME='+ encodeURIComponent(custName),'/customer/customer');
				}
			}, {"ext1" : "集团合同新增"});
//			MessageBox.alert("提示信息", "没有查询到合同信息，如需新增合同，请到集团合同管理菜单新增合同！");
			
		}
		else
		{
            //给搜索准备数据
            CHOOSE_CONTRACT_LIST = data.get("CHOOSE_CONTRACT_LIST","");
			showPopup(popupContractId, 'contractInfoList', true);
			$("#contractInfomationUl li").find("button").bind("click",function(){
				hidePopup(this);
				chooseContract(this);
			});
		}
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
}
function updateShowToPop(popupIdNew)
{
	popupContractId=popupIdNew;
}
function search(){
    var text = $("#CONTRACT_SEARCH_TEXT").val();

    var rst = new $.DatasetList();
    for(var i=0;i<CHOOSE_CONTRACT_LIST.length;i++){
        var contract = CHOOSE_CONTRACT_LIST.get(i);
        var contractName = contract.get("CONTRACT_NAME");
        if(contractName.indexOf(text)>-1){
            rst.add(contract);
        }
    }
    drawContract(rst);
}

function drawContract(contracts){
    var drawArea = $("#contractInfomationUl");
    drawArea.empty();
    var html=[];
    for(var i=0;i<contracts.length;i++){
        var contract = contracts.get(i);
        var contractName = contract.get("CONTRACT_NAME");
        var contractId = contract.get("CONTRACT_ID");
        var contractConman = contract.get("CONTRACT_CONMAN");
        var contractManager = contract.get("CONTRACT_MANAGER");
        var contractWriter = contract.get("CONTRACT_WRITER");
        var contractWriteDate = contract.get("CONTRACT_WRITE_DATE");
        var contractEndDate = contract.get("CONTRACT_END_DATE");

        if(contractWriteDate.length>10)
		{
            contractWriteDate = contractWriteDate.substr(0,10);
		}

        if(contractEndDate.length>10)
        {
            contractEndDate = contractEndDate.substr(0,10);
        }

        var contractTypeCode = contract.get("CONTRACT_TYPE_CODE");
        var contractContent = contract.get("CONTRACT_CONTENT");
        var offerIDS = contract.get("SEARCH_OFFER_IDS");

        var queryProductInfo = new $.DatasetList();
        queryProductInfo = contract.get("QUERY_PRODUCT_INFO");


        html.push('<li>');
        html.push('<div class="content">');
			html.push('<div class="main">');
				html.push('<div class="title e_blue">[<span>'+contractId+'</span>] <span>'+contractName+'</span></div>');
				html.push('<div class="content">');
					html.push('<div class="c_param c_param-label-auto c_param-col-2">');
						html.push('<ul>');
							html.push('<li>');
							html.push('<span class="label">决策人：</span>');
							html.push('<span class="value"><span>'+contractConman+'</span></span>');
							html.push('</li>');
							html.push('<li>');
							html.push('<span class="label">项目经理：</span>');
							html.push('<span class="value"><span>'+contractManager+'</span></span>');
							html.push('</li>');
							html.push('<li>');
							html.push('<span class="label">签订人：</span>');
							html.push('<span class="value"><span>'+contractWriter+'</span></span>');
							html.push('</li>');
							html.push('<li>');
							html.push('<span class="label">签订日期：</span>');
							html.push('<span class="value"><span>'+contractWriteDate+'</span></span>');
							html.push('</li>');
							html.push('<li>');
							html.push('<span class="label">有效期：：</span>');
							html.push('<span class="value"><span>'+contractEndDate+'</span></span>');
							html.push('</li>');
						html.push('</ul>');
					html.push('</div>');
				html.push('</div>');
			html.push('</div>');


			html.push('<div class="fn" onclick="updateContract(this)" contractId="'+contractId+'"><span class="e_ico-edit"></span></div>');
			html.push('<div class="side">');
			html.push('<button type="button" class="e_button-blue e_button-r" contractId="'+contractId+ '"contractName="'+contractName+ '"contractType="'+contractTypeCode+ '"contractManager="'+contractManager+ '"contractConman="'+contractConman+ '"contractWriter="'+contractWriter+ '"contractWriteDate="'+contractWriteDate+ '"contractEndDate="'+contractEndDate+ '"contractContent="'+contractContent+ '"offerIds="'+offerIDS+'">');
			html.push('<span class="e_ico-ok"></span><span>选择</span>');
			html.push('</button>');
			html.push('</div>');
        html.push('</div>');




        for(var y=0;y<queryProductInfo.length;y++){
            var productInfoData = queryProductInfo.get(y);
            var productId = productInfoData.get("PRODUCT_ID");
            var productName = productInfoData.get("PRODUCT_NAME");

            html.push('<div class="sub sub-noline">');
            	html.push('<div class="main">');
					html.push('<div class="c_list c_list-gray c_list-line c_list-border">');
						html.push('<ul>');
            				html.push('<li>');
            					html.push('<div class="main">');
								html.push('<div class="title">[<span id="PRODUCT_ID" name="PRODUCT_ID">'+productId+'</span>] <span id="PRODUCT_NAME" name="PRODUCT_NAME">'+productName+'</span></div>');
								html.push('<div class="content"></div>');
            					html.push('</div>');
            				html.push('</li>');
						html.push('</ul>');
					html.push('</div>');
				html.push('</div>');
			html.push('</div>');
        }


    }

    $.insertHtml('beforeend',drawArea,html.join(""));
    $("#contractInfomationUl li").find("button").bind("click",function(){
        hidePopup(this);
        chooseContract(this);
    });
}

function addContract(){
	var custInfo = $.enterpriseLogin.getInfo().get("CUST_INFO");
	var groupId = $.enterpriseLogin.getInfo().get("GROUP_ID");
	var custId = custInfo.get("CUST_ID");
	var custName = custInfo.get("CUST_NAME");
	openNav('集团合同新增', 'customer.cs.groupcontract.addcontract.AddContract','init', '&refresh=true&typeCode=group&CUST_ID='+custId+'&GROUP_ID='+groupId+'&CUST_NAME='+ encodeURIComponent(custName),'/customer/customer');
}


function updateContract(e){
	
	var contractId = $(e).attr("contractId");
	var custInfo = $.enterpriseLogin.getInfo().get("CUST_INFO");
	var custId = custInfo.get("CUST_ID");
	openNav('集团合同新增', 'customer.cs.groupcontract.modifycontract.EditContract','', '&refresh=true&typeCode=group&CUST_ID=' + custId + '&CONTRACT_ID=' + contractId,'/customer/customer');

}


function refreshCont(){
	queryContractInfo();
}
//选取合同信息
function chooseContract(e) {
	var dataDetail = $.DataMap();
	var data = $.DataMap();
	var contractType = $(e).attr("contractType");
	var contractId = $(e).attr("contractId");
	var contractName = $(e).attr("contractName");
	var contractManager = $(e).attr("contractManager");
	var contractConman = $(e).attr("contractConman");
	var contractWriter = $(e).attr("contractWriter");
	var contractWriteDate = $(e).attr("contractWriteDate");
	var contractEndDate = $(e).attr("contractEndDate");
	var contractContent = $(e).attr("contractContent");
	var offerIds = $(e).attr("offerIds");
	dataDetail.put("CONTRACT_ID",contractId);
	dataDetail.put("CONTRACT_TYPE_CODE",contractType);
	dataDetail.put("CONTRACT_NAME",contractName);
	dataDetail.put("CONTRACT_CONMAN",contractConman);
	dataDetail.put("CONTRACT_MANAGER",contractManager);
	dataDetail.put("CONTRACT_WRITER",contractWriter);
	dataDetail.put("CONTRACT_WRITE_DATE",contractWriteDate);
	dataDetail.put("CONTRACT_END_DATE",contractEndDate);
	dataDetail.put("CONTRACT_CONTENT",contractContent);
	dataDetail.put("OFFER_IDS",offerIds);
	data.put("CONTRACT_INFO",dataDetail);
	contractPopupItemCallback(data);
}
//展开产品信息
function toggle(thisT) {
	if($($(thisT).children()).attr("class")=="e_ico-unfold"){
		$($(thisT).parent().next()).css("display","");
		$($(thisT).children()).removeClass("e_ico-unfold");
		$($(thisT).children()).addClass("e_ico-fold");
	}
	else{
		$($(thisT).parent().next()).css("display","none");
		$($(thisT).children()).removeClass("e_ico-fold");
		$($(thisT).children()).addClass("e_ico-unfold");
	}
}

