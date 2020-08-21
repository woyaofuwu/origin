
function SubmitData(name)
{
   this.nodeInfoArray = new Array();
   this.nodeInfoHash = new Array();
   this.userData = new UserData();
   
   SubmitData.prototype.addUserData = SubmitData_addUserData;
   SubmitData.prototype.addNodeInfo = SubmitData_addNodeInfo;
   SubmitData.prototype.toXmlString= SubmitData_toXmlString;
   SubmitData.prototype.getNodeInfo = SubmitData_getNodeInfo;	
   SubmitData.prototype.addNewNodeInfo = SubmitData_addNewNodeInfo;
}

function SubmitData_addUserData(paramName,paramValue)
{
    this.userData.addUserData(paramName,paramValue);
}

function SubmitData_addNodeInfo(pNodeInfo)
{
    if(pNodeInfo!=null)
    {
	    if(pNodeInfo.getName() && pNodeInfo.getName()!="")
	    {
		   if(this.nodeInfoHash[pNodeInfo.getName()])
		   {
		       return;
		   }
	       this.nodeInfoHash[pNodeInfo.getName()] = pNodeInfo;
	       this.nodeInfoArray[this.nodeInfoArray.length] = pNodeInfo;  
	   } 
    } 
}

function SubmitData_toXmlString()
{
  var tmpArray = new Array();
  var headTmpArray = new Array();
  if(this.nodeInfoArray.length>0)
  {
	   tmpArray[tmpArray.length] = this.userData.toXmlString();
	  
	   for(var i=0;i<this.nodeInfoArray.length;i++)
	   {      
	      tmpArray[tmpArray.length] = this.nodeInfoArray[i].toXmlString();  
	   }   
	   if(tmpArray.join("").length>0)
	   {
	     headTmpArray[headTmpArray.length] = "<submitdata ";
	     headTmpArray[headTmpArray.length] = "name='";
	     headTmpArray[headTmpArray.length] = this.name;
	     headTmpArray[headTmpArray.length] = "' type='";
	     headTmpArray[headTmpArray.length] = this.type;
	     headTmpArray[headTmpArray.length] = "'>"; 
	     headTmpArray[headTmpArray.length]=tmpArray.join("");
	     headTmpArray[headTmpArray.length] = "</submitdata>";
	   }
  }
  return headTmpArray.join("");
}

function SubmitData_getNodeInfo(pName)
{
  if(pName && this.nodeInfoHash[pName])
  {
     return this.nodeInfoHash[pName]
  }
  else
  {
     return null;
  }
}

function SubmitData_addNewNodeInfo(pId,pName,pType)
{
  var nodeInfo = new NodeInfo(pId,pName,pType);
  this.addNodeInfo(nodeInfo);
  return nodeInfo;
}

function NodeInfo(pId,pName,pInfoType)
{
  this.id = "";
  this.name = "";
  this.infotype="";
  if(pId!=null){this.id=pId};
  if(pName!=null) this.name=pName;
  if(pInfoType!=null) this.infotype = pInfoType;
  this.userData = new UserData();
  this.submitDataArray = new Array();
  this.jsonStrArray = new Array();
  
  NodeInfo.prototype.wrapJsonStr = NodeInfo_wrapJsonStr;
  NodeInfo.prototype.getName = NodeInfo_getName;
  NodeInfo.prototype.addUserData = NodeInfo_addUserData;
  NodeInfo.prototype.addSubmitData = NodeInfo_addSubmitData ;
  NodeInfo.prototype.addJSONObjectStr = NodeInfo_addJSONObjectStr;
  NodeInfo.prototype.addJSONArrayStr = NodeInfo_addJSONArrayStr;
  NodeInfo.prototype.toXmlString = NodeInfo_toXmlString;
}
function NodeInfo_wrapJsonStr(pName,pType,pJsonStr)
{
  var tmpArray = new Array();
  tmpArray[tmpArray.length] = "<json name='";
  tmpArray[tmpArray.length] = pName;
  tmpArray[tmpArray.length] = "' type='";
  tmpArray[tmpArray.length] = pType;
  tmpArray[tmpArray.length] ="'>";
  tmpArray[tmpArray.length] = pJsonStr;
  tmpArray[tmpArray.length] = "</json>";
  return tmpArray.join("");
}

function NodeInfo_getName()
{
    return this.name;
}

function NodeInfo_addUserData(paramName,paramValue)
{
    this.userData.addUserData(paramName,paramValue);
}

function NodeInfo_addSubmitData(pChildSubmitData)
{
    if(pChildSubmitData){
    	this.submitDataArray[this.submitDataArray.length] = pChildSubmitData;  
    } 
}

function NodeInfo_addJSONObjectStr(pJsonName,pJsonStr)
{
   this.jsonStrArray[this.jsonStrArray.length] = this.wrapJsonStr(pJsonName,"[Object]",pJsonStr);
}

function NodeInfo_addJSONArrayStr(pJsonName,pJsonStr)
{
   this.jsonStrArray[this.jsonStrArray.length] = this.wrapJsonStr(pJsonName,"[Array]",pJsonStr);
}

function NodeInfo_toXmlString()
{
  var tmpArray = new Array();
  
  tmpArray[tmpArray.length] = this.userData.toXmlString();
 
  for(var i=0;i<this.jsonStrArray.length;i++)
  {
     tmpArray[tmpArray.length] = this.jsonStrArray[i];  
  }
  
  for(var i=0;i<this.submitDataArray.length;i++)
  {
    tmpArray[tmpArray.length] = this.submitDataArray[i].toXmlString();
  }
  
  var reVal = tmpArray.join("");
  
  if(reVal!="")
  {
      return  "<nodeinfo id='"+this.id+"' name='"+this.name+"' infotype='"+this.infotype+"'>"+reVal+"</nodeinfo>";
  }
  else
  {
      return "";
  }
}

function UserData()
{
  this.valueMap = new Wade.DataMap();
   
  UserData.prototype.addUserData = UserData_addUserData;
  UserData.prototype.toXmlString = UserData_toXmlString;
}

function UserData_addUserData(pName,pVal){
	this.valueMap.put(pName,pVal);
}

function UserData_toXmlString(){
	return "<ud>" + this.valueMap.toString() + "</ud>";
}

//PageDataSet
function PageDataSet()
{
	this.dataSetId = "";
    this.dataSetType = "";
    this.dataSetKey = "";
    this.dataSetMethod = "";
}

//PageDataSets
function PageDataSets()
{
    this.sets = new Array();
    PageDataSets.prototype.addPageDataSet = PageDataSets_addPageDataSet;
}

function PageDataSets_addPageDataSet(set)
{
    this.sets[this.sets.length] = set;
}