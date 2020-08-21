/* $Id  */

function validate() {
    var selectProduct=getElement("grpProductTreeSelected").value;
	if (selectProduct=='[]') {
	   alert('请先选择需要办理的集团产品！');
	   return false;
	}
	
    var custName=getElement("CUST_NAME").value;	   
	if (custName=="") {
	   alert('请先查询集团客户资料!');
	   return false;
	}
	
    return true;    
}

function queryCompProduct(node) {
    var obj=getElement("CompProductInfoPart");
    obj.style.display = "block";
    
    var d = node.parentNode.parentNode.children;   
	var namesrt = d[1].children[0].children[0].id;
    
    ajaxSubmit(this, 'queryProductInfo','&PRODUCT_ID='+node.id+'&NAMESTR='+namesrt,'CompProductInfoPart');
}

function setAcctPayitem()
{   
	var s = document.getElementsByName("itemcodes");
	var planname=getElement("PLAN_NAME").value;	
	var plandesc=getElement("PLAN_DESC").value;	
	var str = getElement("defaultPayPlans").value;
	var s2 = "";
	   for( var i = 0; i < s.length; i++ )
	   {
	    if ( s[i].checked ){
	    s2 += s[i].value+'|';
	    }
	   }
	if(s2.length<1){
		alert("请选择综合帐目编码");
		return false;
	}
	if(planname==null||planname.length<1){
		alert("请输入付费计划名称");
		return false;
	}

	var defaultPayPlans = new Wade.DatasetList(str);
	
	defaultPayPlans.each(function(item){
	    item.removeKey("POP_PAY_ITEMS_DESC");
	});
	defaultPayPlans.each(function(item){
	    item.removeKey("PAY_ITEMS");
	});
	defaultPayPlans.each(function(item){
	    item.removeKey("PAY_ITEMS_DESC");
	});

	var defaultPayPlansstr = defaultPayPlans.toString();

	parent.ajaxDirect('group.creategroupuser.AccountInfo', 'setItemcodes', '&ITEMCODES='+s2+'&DEF_PAYPLAN='+defaultPayPlansstr+'&PLAN_NAME='+planname+'&PLAN_DESC='+plandesc, 'grpAccttype');
    cancel(true);
}

function afterSelectProductAction()
{
	var compixProduct = this.ajaxDataset.get(0, "COMPIX_PRODUCT");
	getElement('COMPIX_PRODUCT').value = compixProduct;
	Wade.page.endFlowOverlay();
}