   
   function queryNewCardIdUser()
   { 
	   
	   var cardidnum = $.trim($("#cond_CARD_ID_NUM").val());	   
	   if(cardidnum==''){
		   alert('身份证号码不能为空！');
		   return false;
	   }
	   if(checkidcardnum(cardidnum)){
	   }else{
  		alert('身份证号码不合法！'); 
	    return false;
	   }
		$.beginPageLoading("系统正在查询。。。");
        ajaxSubmit('QueryRecordPart','queryInfos','','StateList',function(data){
    	   $.endPageLoading();
    	   if(data.get("ERROR_DESC")!=undefined && data.get("ERROR_DESC")!="")
    		   alert(data.get("ERROR_DESC"));
        
		},function(e,i){
			$.endPageLoading();
			alert(e+":"+i);
		});

   }
   
   function checkidcardnum(psptId)
   { 
	    var area = {11:"\u5317\u4eac", 12:"\u5929\u6d25", 13:"\u6cb3\u5317", 14:"\u5c71\u897f", 15:"\u5185\u8499\u53e4", 21:"\u8fbd\u5b81", 22:"\u5409\u6797", 23:"\u9ed1\u9f99\u6c5f", 31:"\u4e0a\u6d77", 32:"\u6c5f\u82cf", 33:"\u6d59\u6c5f", 34:"\u5b89\u5fbd", 35:"\u798f\u5efa", 36:"\u6c5f\u897f", 37:"\u5c71\u4e1c", 41:"\u6cb3\u5357", 42:"\u6e56\u5317", 43:"\u6e56\u5357", 44:"\u5e7f\u4e1c", 45:"\u5e7f\u897f", 46:"\u6d77\u5357", 50:"\u91cd\u5e86", 51:"\u56db\u5ddd", 52:"\u8d35\u5dde", 53:"\u4e91\u5357", 54:"\u897f\u85cf", 61:"\u9655\u897f", 62:"\u7518\u8083", 63:"\u9752\u6d77", 64:"\u5b81\u590f", 65:"\u65b0\u7586", 71:"\u53f0\u6e7e", 81:"\u9999\u6e2f", 82:"\u6fb3\u95e8", 91:"\u56fd\u5916"};
		
		psptId = psptId.toUpperCase();
		
		if (area[parseInt(psptId.substr(0, 2))] == null) {
			//alert("\u8eab\u4efd\u8bc1\u53f7\u7801\u4e0d\u6b63\u786e\u0028\u5730\u533a\u975e\u6cd5\u0029\uff01");
			return false;
		}
		
		if (!(/(^\d{15}$)|(^\d{17}([0-9]|X)$)/.test(psptId))) {
			//alert("\u8f93\u5165\u7684\u8eab\u4efd\u8bc1\u53f7\u957f\u5ea6\u4e0d\u5bf9\uff0c\u6216\u8005\u53f7\u7801\u4e0d\u7b26\u5408\u89c4\u5b9a\uff01\n15\u4f4d\u53f7\u7801\u5e94\u5168\u4e3a\u6570\u5b57\uff0c18\u4f4d\u53f7\u7801\u672b\u4f4d\u53ef\u4ee5\u4e3a\u6570\u5b57\u6216X\u3002 ");
			return false;
		}
		
	    // 下面分别分析出生日期和校验位
		var len, re;
		len = psptId.length;
		var arrSplit = "";
		var dtmBirth = "";
		
		if (len == 15) {
			re = new RegExp(/^(\d{6})(\d{2})(\d{2})(\d{2})(\d{3})$/);
			arrSplit = psptId.match(re);  // 检查生日日期是否正确
			dtmBirth = new Date("19" + arrSplit[2] + "/" + arrSplit[3] + "/" + arrSplit[4]);
		}
		
		if (len == 18) {
			re = new RegExp(/^(\d{6})(\d{4})(\d{2})(\d{2})(\d{3})([0-9]|X)$/);
			arrSplit = psptId.match(re);  // 检查生日日期是否正确
			dtmBirth = new Date(arrSplit[2] + "/" + arrSplit[3] + "/" + arrSplit[4]);
		}
		
		var bGoodDay = (dtmBirth.getFullYear() == Number(arrSplit[2])) && ((dtmBirth.getMonth() + 1) == Number(arrSplit[3])) && (dtmBirth.getDate() == Number(arrSplit[4]));
		if (!bGoodDay) {
			//alert("\u8f93\u5165\u7684\u8eab\u4efd\u8bc1\u53f7\u91cc\u51fa\u751f\u65e5\u671f\u4e0d\u5bf9\uff01");
			return false;
		}
		
		/*每位加权因子*/
		var powers= new Array("7","9","10","5","8","4","2","1","6","3","7","9","10","5","8","4","2");
		/*第18位校检码*/
		var parityBit=new Array("1","0","X","9","8","7","6","5","4","3","2");
		var psptBit = psptId.charAt(17).toUpperCase();
		var id17 = psptId.substring(0,17);    
	   /*加权 */
	   var power = 0;
	   for(var i=0;i<17;i++){
	     power += parseInt(id17.charAt(i),10) * parseInt(powers[i]);
	   }              
	   /*取模*/ 
	   var mod = power % 11;
	   var checkBit = parityBit[mod];
	   if(psptBit!=checkBit){
	   		//alert('身份证号码不合法'); 
			return false;
	   }
	   
	   return true;
   }
   
