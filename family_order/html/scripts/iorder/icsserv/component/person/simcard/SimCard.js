var SimCardOcx = {
    VERSION:null,		//控件版本号
    /**
     * 区分是否为预制卡，1为预制卡，0为非预制卡（判断标准可以调用资源接口获取或者直接通过新的接口取到空卡号，长度为16是非预制卡，长度为20为预制卡）
     */
    PRE_SIM:null,
    USIM:null,				//非预制卡（老卡）区分是否为USIM卡：0为普通SIM卡，1为USIM卡
    EMPTY_CARD_ID:null,		//卡片空卡序列号
    /**
     * 初始化操作，确定写卡卡片各参数
     * 函数执行成功：0&卡序列号
     * 函数执行失败：1&错误代码
     */
    initParam:function(){
        var cardSn=null;
        try{
            //该函数支持本方案实施前后的所有现场写卡系统空卡
            cardSn=ngrpsocx.OPS_GetCardSN();
        }catch(e){
            MessageBox.alert("告警提示", "请确认是否已经安装写卡控件，或浏览器是否支持写卡控件！ ");
            return false;
        }
        if(cardSn.substr(0,1)!="0"){
            MessageBox.alert("获取卡片空卡序列号错误！");
            return false;
        }
        this.EMPTY_CARD_ID=cardSn.substr(2);
        this.PRE_SIM=this.EMPTY_CARD_ID.length==20? 1 : 0;
        if(this.PRE_SIM==0){
            //如果非预制卡（老卡），则继续判断是否为USIM卡
            var result= null;
            try{
                ngrpsocx.Initialize();
                result=ngrpsocx.CheckCardSim();
            }catch(e){
                MessageBox.alert("确认是否为USIM卡错误：\n"+ this.getErrMsg());
                return false;
            }
            this.USIM=(result==1)?1:0
        }
        this.VERSION=this.getOcxVersion();
        return true;
    },
    //创建控件
    createOcx:function(id,classid){
        if($("#ngrpsocx") && $("#ngrpsocx").length){
            return;
        }
        $(document.body).append('<object style="display:none;" id="ngrpsocx" classid="clsid:4F8A4B0A-3543-4BC6-8D41-09ECA0CD4AF7"></object>');
    },
    //获取版本号
    getOcxVersion:function(){
        var strOcxVersion= "";
        if(this.PRE_SIM){
            var strOcxVersion=ngrpsocx.OPS_GetVersion();
            var code=strOcxVersion.substr(0, 1);
            if(code !=0){
                MessageBox.alert(this.getErrMsg(strOcxVersion.substr(2)));
                return false;
            }
            strOcxVersion=strOcxVersion.substr(2);
        }else{
            strOcxVersion=ngrpsocx.GetOcxVersion();
        }
        return strOcxVersion;
    },
    /**
     * 非预制卡（老卡）接口
     * 初始化控件，返回0=成功，1=失败
     * required表示是否已经换了其他SIM卡，，如果是，则需要重新初始化该SIM卡的参数【是否预制卡，是否为USIM卡】，为后面相关读取控件方法提供支持
     */
    initalize:function(required){
        if(required){
            if(!this.initParam()) {
                return false;
            }
        }
        //预制卡不需要进行初始化，直接返回
        if(this.PRE_SIM){
            return true;
        }
        var result=null;
        try{
            result=ngrpsocx.Initialize();
        }catch(e){
            MessageBox.alert("告警提示", "请确认是否已经安装写卡控件，或浏览器是否支持写卡控件！ ");
            return false;
        }
        if(result!=0){
            MessageBox.alert("写卡控件初始化失败：\n"+this.getErrMsg());
            return false;
        }
        return true;
    },
    /**
     * 非预制卡（老卡）接口
     * 释放写卡控件资源
     */
    finalize:function(){
        //预制卡不需要进行释放，直接返回
        if(this.PRE_SIM){
            return true;
        }
        var result=ngrpsocx.Finalize();
        if(result!=0){
            MessageBox.alert("写卡控件释放失败：\n"+this.getErrMsg());
            return false;
        }
        return true;
    },
    //获取当前操作错误信息
    getErrMsg:function(code){
        var errStr=null;
        if(this.PRE_SIM){
            errStr=ngrpsocx.OPS_GetErrorMsg(code);
        }else{
            errStr=ngrpsocx.GetLastError();
            var errs=errStr.split("&");
            if(!errs || !errs.length){
                return errStr;
            }
            for(var i=0,length=errs.length; i<length; i++){
                var map=errs[i].split("=");
                if(map && map.length==2 && map[0].toUpperCase() == "DESC"){
                    errStr=map[1];
                    break;
                }
            }errs=null;
        }
        return errStr;
    },
    //获取ICCID号码，如果为多卡号，则每个ICCID用&连接返回
    getICCID:function(){
        var iccid=null, strICCID=null;
        if(this.PRE_SIM){
            //暂时不区分一卡双号
            var strICCID=ngrpsocx.OPS_GetCardInfo();
            var code = strICCID.substring(0,1);
            if(code!=0){
                MessageBox.alert(this.getErrMsg(strICCID.substr(2)));
                return false;
            }
            //0E0A为白卡号
            iccid=this.deCodeICCID(strICCID.substring(6,26));
        }else{
            var strICCID=this.USIM? ngrpsocx.GetICCIDUsim() : ngrpsocx.GetICCID();
            if(strICCID == ""){
                MessageBox.alert("获取ICCID错误："+ this.getErrMsg());
                return false;
            }
            //获取卡号数
            var phoneNum=this.getPhoneNums();
            if(!phoneNum){
                return false;
            }
            if(phoneNum == 1 || phoneNum == 10){
                iccid = strICCID.substring(7, strICCID.length);
            }else if(phoneNum == 2 || phoneNum == 20){
                iccid = strICCID.substring(7,27);
                iccid += "&"+strICCID.substring(35, strICCID.length);
            }
        }
        return iccid;
    },
    //主要是非预制卡获取卡号数，预制卡暂时不支持后面在扩展
    getPhoneNums:function(){
        var phoneNum=null;
        //对于预制卡，暂时不支持一卡多号，所以默认都返回1，后续有调整，在完善
        if(this.PRE_SIM){
            return 1;
        }
        phoneNum=(this.USIM)?ngrpsocx.isPhoneNumsUsim() : ngrpsocx.isPhoneNums();
        if(phoneNum==0){
            MessageBox.alert("获单号/多号卡错误："+ this.getErrMsg());
            return false;

        }
        return phoneNum;
    },
    /**
     * 获取IMSI，如果为多卡号，则每个IMSI用&连接返回,预制卡IMSI从资源获取
     * 预制卡没有提供接口获取，需要call服务从资源获取
     */
    getIMSI:function(){
        var imsi=null;
        if(this.PRE_SIM){
            return "";
        }
        var strIMSI=this.USIM? ngrpsocx.GetIMSIUsim() : ngrpsocx.GetIMSI();
        if(strIMSI == ""){
            MessageBox.alert("获取IMSI错误："+ this.getErrMsg());
            return false;
        }
        //获取卡号数
        var phoneNum=this.getPhoneNums();
        if(!phoneNum){
            return false;
        }
        if(phoneNum == 1 || phoneNum == 10){
            imsi = strIMSI.substring(6, strIMSI.length);
        }else if(phoneNum == 2 || phoneNum == 20){
            imsi = strIMSI.substring(6,21);
            imsi += "&"+strIMSI.substring(28, strIMSI.length);
        }
        return imsi;
    },

    getATR:function(){
        var strATR= ngrpsocx.GetCardATR();
        if(strATR==""){
            MessageBox.error("获取Sim卡ATR错误", this.getErrMsg());
            return;
        }
        return strATR;
    },
    checkDll:function(dllName){
        var result=ngrpsocx.CheckDll(dllName);
        if(result!= 0){
            return false;
        } else{
            return true;
        }
    },
    //写卡，如果成功返回写卡结果，否则返回false
    writeCard:function(str){
        var result=null;
        if(this.PRE_SIM){
            //如果成功返回0&写卡结果,否则返回1&错误代码
            result=ngrpsocx.OPS_WriteCard(str);
            if(result.substr(0,1)!="0"){
                MessageBox.alert("写卡错误："+ this.getErrMsg(result.substr(2)));
                return false;
            }
            result=result.substr(2);
        }else{
            //如果成功返回0,否则返回1
            if(this.USIM){
                result=ngrpsocx.WriteCardUsim(str);
            }else{
                result=ngrpsocx.WriteCard(str);
            }
            if(result!="0"){
                MessageBox.alert("写卡错误："+ this.getErrMsg());
                return false;
            }
        }
        return result;
    },

    getCardInfo:function(){
        var result= ngrpsocx.OPS_GetCardInfo();
        var code = result.substring(0,1);
        if(code!=0){
            MessageBox.alert("读取卡片信息错误："+ this.getErrMsg(result.substr(2)));
            return false;
        }
        return result.substring(2);
    },

    checkCard:function(){
        var result= ngrpsocx.CheckCard();
        if(result!=0){
            MessageBox.alert(this.getErrMsg());
            return false;
        }
        return true;
    },

    formatCard:function(flagStr){
        var result= ngrpsocx.DoFormatCard(flagStr);
        if(result!=0){
            ngrpsocx.Finalize();
            MessageBox.alert(this.getErrMsg());
            return false;
        }
        return true;
    },

    deCodeICCID:function(str){
        var i =0;
        for(i=0;i<str.length;i=i+2){
            if(i%2==0){
                var a = str.substring(i,i+2);
                var b = a.substring(0,1);
                var c = a.substring(1,2);
                a = c+b;
                str = str.substring(0,i)+a+str.substring(i+2);
            }
        }
        return str;
    }

};