
//证件类型改变时调用
function psptChange(){
  var type = $("#custinfo_PSPT_TYPE_CODE").val();
  if(type == "0"){
      $("#custinfo_PSPT_TYPE_CODE").attr("datatype","pspt2");
  }else{
  	  $("#custinfo_PSPT_TYPE_CODE").attr("datatype","");
  }
}


/*==================================================================================================*/

 /*产品信息处理*/
 function confirmAllForProductInfo(obj){
	if(!verifyField($("#MAIN_PRODUCT_ID")))return false;
	return confirmAll(obj);
 }

/**产品页面的初始化*/ 
function init() {
	addTopTabset();
}
function addTopTabset() {
	var lefttabset = new TabSet("lefttabset","left");
	lefttabset.addTab("产品信息", $("#productInfo"));
	lefttabset.addTab("资源信息", $("#resourceInfo"));
	lefttabset.draw();
}

/**基本信息页面下一步时执行的*/
function isSelectedProducts() {
    var custName= $("#CUST_NAME").val();
    var selectProduct= $("#grpProductTreeSelected").val();
	if (selectProduct=='[]') {
	   alert('请先选择需要办理的集团产品！');
	   return false;
	}
	if (custName=="") {
	   alert('请先查询集团客户资料!');
	   return false;
	}
    return true;
 }    


function checkGrpProduct(){
   var grpId =$("#cond_GROUP_ID").val();
   var productId="";
   var inputs = document.getElementsByTagName("input");   
 
	//遍历input标签，获取同级radio   
	for(var i = 0; i < inputs.length; i++){   
		var ele = inputs[i];
		if (ele.type == "radio"){
		  if (ele.checked){
		    productId = ele.value;
		    break;
		  }
		}  
	}
  if (productId == ""){
    alert("请选择成员产品");
    return;
  }
  //ajaxGet(this,"checkGroupProduct","&GROUP_ID="+grpId+"&PRODUCT_ID="+productId,null,null,true,checkGrpByajax);
    $("#custinfo_dealFalg").val("true");
    parent.frames.flowsubmit.bnext.click();
    return true;
}

function checkGrpByajax(){
  var serverParamdataset=this.ajaxDataset;
  var serialNumber = serverParamdataset.get(0,"SERIAL_NUMBER");
  if (serialNumber == ""){
    alert("该集团还未开户，请先进行集团专线用户开户！");
    return false;
  }else{
    $("#custinfo_dealFalg").val("true");
    parent.frames.flowsubmit.bnext.click();
    return true;
  }
}

function displayTextIms(node) {
	var obj=$("#ImsInfoPart");
	if(node.id == "801110"){
	    obj.css("display","block")
	}else{
		obj.css("display","none")
	}  
}