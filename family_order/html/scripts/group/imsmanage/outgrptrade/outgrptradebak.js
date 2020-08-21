 
function queryGrpCustInfo(){
 
  if (Wade.validate.confirmAll(document.forms[0])){
		Wade.page.beginPageLoading();
		 
		ajaxSubmit(this,"getGrpCustInfos","","grpcustRefreshPart",null,false,getGrpuserinfo);
	}else{
		return false;
	}
  
}
function getGrpuserinfo(){
   
 if (Wade.validate.confirmAll(document.forms[0])){
		Wade.page.beginPageLoading();
		 
		ajaxSubmit(this,"getGrpUserVpnInfos","","vpmnRefreshPart",null,false,null);
	}else{
		return false;
	}
}
function onHC(){
   if(event.keyCode==13){
    queryGrpCustInfo();
   }
}
 
function addTabset() 
{
var tabset;

 //var parms = '&USER_ID='+user_id;
     
	tabset = new TabSet("tabset");
                  
    tabset.addTab("集团用户基本信息", getElement("grpbaseinfo"),clickTabPane('first','group.outgrptrade.outgrpBase','',''));	  
	 
    tabset.addTab("网外号码信息",    getElement("outphoneInfo"),clickTabPane('outinfo','group.outgrptrade.outgrpOutInfo','',''));

	tabset.addTab("成员信息", getElement("groupmemInfo"));
	tabset.draw();
	tabset.onTabSelect(tabEventHandler);
	 
 
}
function tabEventHandler(tabset) 
{
  var user_id=document.getElementById("vpmnuserid").value; 
 alert("取得的user_id2:"+user_id); 
 var parms = '&USER_ID='+getElementValue("vpmnuserid");
	var tab = tabset.getActiveTab();
	
	if (tab.caption == "集团基本信息") {
 
	clickTabPane('grpbaseinfo','custview.CustFirstInfo','');
    
	if (tab.caption == "基本信息")     clickTabPane('baseInfo', 'custview.BaseInfo', 'getCustomerInfos',parms);
	if (tab.caption == "网外号码信息")     clickTabPane('outinfo','group.outgrptrade.outgrpOutInfo','getGrpCustInfos',parms);
	if (tab.caption == "个性扩展信息") clickTabPane('personExtInfo','custview.PersonExtInfo','getPersonExtInfos',parms);
    
	}
	 
 
}
function changeTab() {
alert("change tab");
 //tabset.switchTo("网外号码信息");
}
function memFresh(){
getElement("mem_part").style.display = "block";
}
function addOgrpFresh(){
getElement("add_outgrppart").style.display = "block";
}
 