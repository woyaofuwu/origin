//js加载树
function initTreeByProductInfo(productname,productid){

	var limitType =  mytree.params.LIMIT_TYPE;
	var limitProducts = mytree.params.LIMIT_PRODUCTS;
	if(limitType == 0 &&   limitProducts.indexOf(productid) >=0 ){
		mytree.init();
		return;
	  
	}
	var myobj={"USER_NODE_TREE":{"text":productname,"showcheck":"true","id":"USER_NODE_TREE","haschild":"false","order":"0","complete":"false","value":productid,"expand":"false","groupid":null,"dataid":"USER_NODE_TREE","href":null,"checked":"true","disabled":"true"}};
	
	window["treeData_mytree"] = myobj;
	mytree.init();
	
}

function initTreeByProductInfoSet(productSet){
	var myobj={};
	productSet.each(function(item,index,totalcount){
		var productName = item.get('PRODUCT_NAME');
		var productId = item.get('PRODUCT_ID');
		var treeData = {"text":productName,"showcheck":"true","id":productName+'-'+productId,"haschild":"false","order":"0","complete":"false","value":productId,"expand":"false","groupid":null,"dataid":productName+'-'+productId,"href":null,"checked":"false","disabled":"false"};
		myobj[productName+'-'+productId]=treeData;
	});
	window["treeData_mytree"] = myobj;
	mytree.init();
	
}

//js生成集团产品开户树
function loadGroupProductTree(data) {
	mytree.empty(true);
	mytree.setParam('CUST_ID',$('#CUST_ID').val());
	mytree.init();
	
}

//js生成集团产品已订购的产品树
function loadGroupProductTreeOrdered(custid,userid) {
	mytree.empty(true);
	mytree.setParam('CUST_ID',custid);
	mytree.setParam('GRP_USER_ID',userid);  
	mytree.init();
	
}

//清空集团产品树
function cleanGroupProductTree() {
	mytree.empty(true);
}

//选择树上父节点node0Name下的子节点node1Name
function productTreeNodeClickAction(node0Name,node1Name) {
	var path = node0Name; // + '-' + node1Name;
	mytree.expandByPath(path, function(data){
		$('#treePar input[value='+node1Name+']').trigger("click");
	});
}


//popup树点击确定后触发的事件
function popupGrpTreeConfirmAction(){
	var nodeId = $('#GROUPTREEPOPUP_NODE_ID').val();
	if(nodeId==""){
		alert("请选择产品");
		return false;
	}
	
	$('#productTreePanel').css('display','none');
	var nodeName = $('#GROUPTREEPOPUP_NODE_NAME').val();
	if($("#GROUPTREEPOPUP_ACTION").val()!=""&&$("#GROUPTREEPOPUP_ACTION").val()!="undefined"){
	
		$.beginPageLoading();
		setTimeout(function(){
			eval($("#GROUPTREEPOPUP_ACTION").val());
			$.endPageLoading();
		}, 0);
	}
}


function popupGrpTreeCheckTreeAction(nodedata){
	var nodeId=nodedata.id;
	var nodeName=nodedata.text;
	$('#GROUPTREEPOPUP_NODE_ID').val(nodeId);
	$('#GROUPTREEPOPUP_NODE_NAME').val(nodeName);
	return true;
			
}
				