/*$Id: psptcard.js,v 1.3 2013/04/11 02:36:20 pengsy3 Exp $*/



/*************************************身份识别相关函数 开始************************************/

function CreateControl(DivID)	{ 
                       	
   $('#'+DivID).html("<OBJECT id='PsptCard'  classid='CLSID:53C4C69B-0096-4451-BA34-F8064CA2561D'   codeBase='HNAN/cabs/PsptControl.cab#Version=1,0,0,0' style='display:none;'  width='10' height='10' ></OBJECT>");  
   //"<OBJECT id='JunAct' classid='CLSID:BAB91EC4-6964-452C-8500-8BA8F1050903'  style='display:none;' codeBase='HNAN/cabs/chase_ABC.cab#Version=1,0,0,3' width='10' height='10' ></OBJECT>";  

}

function checkDriver() {
	try {
		
		var obj1= $('#PsptCard');
		
		obj1.OCXVersion;
	} catch(e) {
		alert('系统检测到您没有安装写卡客户端，请到系统首页进行下载安装！');
		return false;
	}
}

/*读取身份证提示*/
function readcardTime(TT)
{ 
	TT=TT-1;
	if(TT>=0)
	{
		if(TT<=0)
		{	
			readCardFromOCX();
		}else{
			document.getElementById("ReadCardButton").value ="读取身份证...";
			setTimeout("readcardTime("+TT+")",100);
		}
	}
}
/*读取身份证*/
function readCardFromOCX()
{ 
	
	if(document.getElementById("custinfo_ReadCardFlag").value==0)
	{
		//临时情况客户信息
	 	CreateControl( "PsptControlOCX" ) ;
	 	
	 	if( checkDriver() == false )
	 	{
	 			return ;
	 	}
		var obj=document.getElementById("PsptCard");
		var ReadTxt;
		
		if( obj == null )
		{
			alert("加载身份证阅读控件失败!");
		}
		
		if( obj != null )
		{
			//alert( "加载身份证阅读控件..." );
			document.getElementById("ReadCardButton").value ="加载控件...";
			
			//ReadTxt = obj.UploadPhoto();
			//alert( ReadTxt );
			
			// 读取证件信息
			ReadTxt = obj.ReadPsptCard();

			// 解析证件信息
			var idcard_array = new Array();
			idcard_array = ReadTxt.split('|');
			//alert( ReadTxt );	
			if( idcard_array.length <= 0 )
			{
				alert( "读取身份证信息失败! 内容: " + ReadTxt );
				return;
			}
			
			var arrNum = idcard_array[0];
			
			if( arrNum < 8 )
			{
				alert( "读取身份证信息格式不正确! 字段数量:" + arrNum + ", 内容: " + ReadTxt );
				return;
			}
						
			document.getElementById("ReadCardButton").value ="解析数据...";
			// 民族
			var strCardVersion = idcard_array[1].substr(4, idcard_array[1].length - 4 );
			var strPsptId = idcard_array[2].substr(4, idcard_array[2].length - 4 );
			var strBirthday = idcard_array[3].substr(4, idcard_array[3].length - 4 );
			var strName = idcard_array[4].substr(4, idcard_array[4].length - 4 );
			var strSex = idcard_array[5].substr(4, idcard_array[5].length - 4 );
			var strMz = idcard_array[6].substr(4, idcard_array[6].length - 4 );
			var strAddress = idcard_array[7].substr(4, idcard_array[7].length - 4 );
			//var strPhonePath = idcard_array[8].substr(4, idcard_array[8].length - 4 );
			
			setIdentityCardInfoToHtml(strCardVersion,strPsptId,strBirthday,strName,strSex,strMz,strAddress);//设置身份证信息到页面
			
		}
		
	}else{
			clearHtmlIdentityCardInfo();//清除页面上身份证信息
			
	}
	
	
}


/*读取身份证提示*/
function readcard_OpenUser(TT)
{ 
	TT=TT-1;
	if(TT>=0)
	{
		if(TT<=0)
		{	
			readCardByOpenUser();
		}else{
			document.getElementById("ReadCardButton").value ="读取身份证...";
			setTimeout("readcard_OpenUser("+TT+")",100);
		}
	}
}

/*读取身份证*/
function readCardByOpenUser()
{ 
	if(document.getElementById("custinfo_ReadCardFlag").value==0)
	{
		//临时情况客户信息
	 	CreateControl( "PsptControlOCX" ) ;
		var obj=document.getElementById("PsptCard");
		var ReadTxt;
		
		if( obj == null )
		{
			alert("加载身份证阅读控件失败!");
		}
		
		if( obj != null )
		{
			//alert( "加载身份证阅读控件..." );
			document.getElementById("ReadCardButton").value ="加载控件...";
			
			//ReadTxt = obj.UploadPhoto();
			//alert( ReadTxt );
			
			// 读取证件信息
			ReadTxt = obj.ReadPsptCard();
			
		//	alert( ReadTxt );
			// 解析证件信息
			var idcard_array = new Array();
			idcard_array = ReadTxt.split('|');
		//	alert( idcard_array );	
			if( idcard_array.length <= 0 )
			{
				alert( "读取身份证信息失败! 内容: " + ReadTxt );
				return;
			}
			
			var arrNum = idcard_array[0];
			
			if( arrNum < 8 )
			{
				alert( "读取身份证信息格式不正确! 字段数量:" + arrNum + ", 内容: " + ReadTxt );
				return;
			}
						
			document.getElementById("ReadCardButton").value ="解析数据...";
		//	alert( idcard_array[1] );	
			// 民族
			var strCardVersion = idcard_array[1].substr(4, idcard_array[1].length - 4 );
			var strPsptId = idcard_array[2].substr(4, idcard_array[2].length - 4 );
			var strBirthday = idcard_array[3].substr(4, idcard_array[3].length - 4 );
			var strName = idcard_array[4].substr(4, idcard_array[4].length - 4 );
			var strSex = idcard_array[5].substr(4, idcard_array[5].length - 4 );
			var strMz = idcard_array[6].substr(4, idcard_array[6].length - 4 );
			var strAddress = idcard_array[7].substr(4, idcard_array[7].length - 4 );
			//var strPhonePath = idcard_array[8].substr(4, idcard_array[8].length - 4 );
			
		 
			document.getElementById("CUST_NAME").value=strName;
			//document.getElementById("userinfo_ROAM_CUST_NAME_TEMP").value=strName;
			document.getElementById("POST_ADDRESS").value=strAddress;
			document.getElementById("PSPT_ID").value=strPsptId;
			//document.getElementById("custinfo_PSPT_END_DATE").value=false;
			document.getElementById("PSPT_ADDR").value=strAddress;
			document.getElementById("PSPT_TYPE_CODE").value="0";
			document.getElementById("custinfo_ReadCardFlag").value=0;
			
			//document.getElementById("readcard_photo").innerHTML ="<img src='"+strPhonePath+"'>";
			
			

			
		}
		
	}else{
			//修改二代证读取信息
			document.getElementById("CUST_NAME").disabled=false;
			//document.getElementById("userinfo_ROAM_CUST_NAME_TEMP").disabled=false;
			document.getElementById("POST_ADDRESS").disabled=false;
			document.getElementById("PSPT_ID").disabled=false;
			document.getElementById("PSPT_END_DATE").disabled=false;
			document.getElementById("PSPT_ADDR").disabled=false;
			//document.getElementById("custInfo_PSPT_TYPE_CODE").disabled=false;
			document.getElementById("custinfo_ReadCardFlag").value=0;
	}
	
	
}




/*读取身份证提示*/
function readcard_RealName(TT)
{ 
	TT=TT-1;
	if(TT>=0)
	{
		if(TT<=0)
		{	
			readCardByRealName();
		}else{
			document.getElementById("ReadCardButton").value ="读取身份证...";
			setTimeout("readcard_RealName("+TT+")",100);
		}
	}
}

/*读取身份证*/
function readCardByRealName()
{ 
	if(document.getElementById("custinfo_ReadCardFlag").value==0)
	{
		//临时情况客户信息
	 	CreateControl( "PsptControlOCX" ) ;
		var obj=document.getElementById("PsptCard");
		var ReadTxt;
		
		if( obj == null )
		{
			alert("加载身份证阅读控件失败!");
		}
		
		if( obj != null )
		{
			//alert( "加载身份证阅读控件..." );
			document.getElementById("ReadCardButton").value ="加载控件...";
			
			//ReadTxt = obj.UploadPhoto();
			//alert( ReadTxt );
			
			// 读取证件信息
			ReadTxt = obj.ReadPsptCard();
			
			// 解析证件信息
			var idcard_array = new Array();
			idcard_array = ReadTxt.split('|');
			//alert( ReadTxt );	
			if( idcard_array.length <= 0 )
			{
				alert( "读取身份证信息失败! 内容: " + ReadTxt );
				return;
			}
			
			var arrNum = idcard_array[0];
			
			if( arrNum < 8 )
			{
				alert( "读取身份证信息格式不正确! 字段数量:" + arrNum + ", 内容: " + ReadTxt );
				return;
			}
						
			document.getElementById("ReadCardButton").value ="解析数据...";
			// 民族
			var strCardVersion = idcard_array[1].substr(4, idcard_array[1].length - 4 );
			var strPsptId = idcard_array[2].substr(4, idcard_array[2].length - 4 );
			var strBirthday = idcard_array[3].substr(4, idcard_array[3].length - 4 );
			var strName = idcard_array[4].substr(4, idcard_array[4].length - 4 );
			var strSex = idcard_array[5].substr(4, idcard_array[5].length - 4 );
			var strMz = idcard_array[6].substr(4, idcard_array[6].length - 4 );
			var strAddress = idcard_array[7].substr(4, idcard_array[7].length - 4 );
			//var strPhonePath = idcard_array[8].substr(4, idcard_array[8].length - 4 );
			
		 
			document.getElementById("custinfo_CUST_NAME").value=strName;
			//document.getElementById("userinfo_ROAM_CUST_NAME_TEMP").value=strName;
			//document.getElementById("custinfo_POST_ADDRESS").value=strAddress;
			document.getElementById("custinfo_PSPT_ID").value=strPsptId;
			//document.getElementById("custinfo_PSPT_END_DATE").value=false;
			document.getElementById("custinfo_PSPT_ADDR").value=strAddress;
			document.getElementById("custinfo_PSPT_TYPE_CODE").value="0";
			document.getElementById("custinfo_ReadCardFlag").value=0;
			
			//document.getElementById("readcard_photo").innerHTML ="<img src='"+strPhonePath+"'>";
			
			
		}
		
	}else{
			//修改二代证读取信息
			document.getElementById("custinfo_CUST_NAME").disabled=false;
			//document.getElementById("userinfo_ROAM_CUST_NAME_TEMP").value=strName;
			//document.getElementById("custinfo_POST_ADDRESS").value=strAddress;
			document.getElementById("custinfo_PSPT_ID").disabled=false;
			//document.getElementById("custinfo_PSPT_END_DATE").value=false;
			document.getElementById("custinfo_PSPT_ADDR").disabled=false;
			//document.getElementById("custinfo_PSPT_TYPE_CODE").value="0";
			 
			document.getElementById("custinfo_ReadCardFlag").value=0;

	}
	
	
}



/*读取身份证提示*/
function readcard_Modi(TT)
{ 
	TT=TT-1;
	if(TT>=0)
	{
		if(TT<=0)
		{	
			readCardByRealName();
		}else{
			document.getElementById("ReadCardButton").value ="读取身份证...";
			setTimeout("readcard_Modi("+TT+")",100);
		}
	}
}

/*读取身份证*/
function readCardByModi()
{ 
	if(document.getElementById("custinfo_ReadCardFlag").value==0)
	{
		//临时情况客户信息
	 	CreateControl( "PsptControlOCX" ) ;
	 	
	 	if( checkDriver() == false )
	 	{
	 			return ;
	 	}
	 	
		var obj=document.getElementById("PsptCard");
		var ReadTxt;
		
		if( obj == null )
		{
			alert("加载身份证阅读控件失败!");
		}
		
		if( obj != null )
		{
			//alert( "加载身份证阅读控件..." );
			document.getElementById("ReadCardButton").value ="加载控件...";
			
			//ReadTxt = obj.UploadPhoto();
			//alert( ReadTxt );
			
			// 读取证件信息
			ReadTxt = obj.ReadPsptCard();
			
			// 解析证件信息
			var idcard_array = new Array();
			idcard_array = ReadTxt.split('|');
			//alert( ReadTxt );	
			if( idcard_array.length <= 0 )
			{
				alert( "读取身份证信息失败! 内容: " + ReadTxt );
				return;
			}
			
			var arrNum = idcard_array[0];
			
			if( arrNum < 8 )
			{
				alert( "读取身份证信息格式不正确! 字段数量:" + arrNum + ", 内容: " + ReadTxt );
				return;
			}
						
			document.getElementById("ReadCardButton").value ="解析数据...";
			// 民族
			var strCardVersion = idcard_array[1].substr(4, idcard_array[1].length - 4 );
			var strPsptId = idcard_array[2].substr(4, idcard_array[2].length - 4 );
			var strBirthday = idcard_array[3].substr(4, idcard_array[3].length - 4 );
			var strName = idcard_array[4].substr(4, idcard_array[4].length - 4 );
			var strSex = idcard_array[5].substr(4, idcard_array[5].length - 4 );
			var strMz = idcard_array[6].substr(4, idcard_array[6].length - 4 );
			var strAddress = idcard_array[7].substr(4, idcard_array[7].length - 4 );
			//var strPhonePath = idcard_array[8].substr(4, idcard_array[8].length - 4 );
			
		 
			document.getElementById("custInfo_CUST_NAME").value=strName;
			//document.getElementById("userinfo_ROAM_CUST_NAME_TEMP").value=strName;
			//document.getElementById("custinfo_POST_ADDRESS").value=strAddress;
			document.getElementById("custInfo_PSPT_ID").value=strPsptId;
			//document.getElementById("custinfo_PSPT_END_DATE").value=false;
			document.getElementById("cond_PSPT_ADDR_NEW").value=strAddress;
			document.getElementById("custInfo_CONTACT").value=strAddress;
			document.getElementById("custInfo_PSPT_TYPE_CODE").value="0";
			document.getElementById("custinfo_ReadCardFlag").value=0;
			
			//document.getElementById("readcard_photo").innerHTML ="<img src='"+strPhonePath+"'>";
			
			
		}
		
	}else{
			//修改二代证读取信息
			document.getElementById("custInfo_CUST_NAME").disabled=false;
			//document.getElementById("userinfo_ROAM_CUST_NAME_TEMP").value=strName;
			//document.getElementById("custinfo_POST_ADDRESS").value=strAddress;
			document.getElementById("custInfo_PSPT_ID").disabled=false;
			//document.getElementById("custinfo_PSPT_END_DATE").value=false;
			document.getElementById("cond_PSPT_ADDR_NEW").value=strAddress;
			document.getElementById("custInfo_CONTACT").value=strAddress;
			//document.getElementById("custinfo_PSPT_TYPE_CODE").value="0";
			 
			document.getElementById("custinfo_ReadCardFlag").value=0;
	}
	
	
}

/*************************************身份识别相关函数 结束************************************/




