/*$Id: psptcard.js,v 1.3 2013/04/11 02:36:20 pengsy3 Exp $*/

/*************************************身份识别相关函数 开始************************************/
function CreateControl(DivID) {
    $("#" + DivID).html("<OBJECT id='PsptCard' classid='CLSID:53C4C69B-0096-4451-BA34-F8064CA2561D' codeBase='HNAN/cabs/PsptControl.cab#Version=1,0,0,0' style='display:none;' width='10' height='10'></OBJECT>");
}

function checkDriver() {
    try {
        var obj1 = $("#PsptCard");
        obj1.OCXVersion;
    } catch (e) {
        alert("系统检测到您没有安装写卡客户端，请到系统首页进行下载安装！");
        return false;
    }
}

// 读取身份证提示
function readCardTime(TT) {
    TT = TT - 1;
    if (TT >= 0) {
        if (TT <= 0) {
            readCardFromOCX();
        } else {
            document.getElementById("ReadCardButton").value = "读取身份证...";
            setTimeout("readCardTime(" + TT + ")", 100);
        }
    }
}

// 读取身份证
function readCardFromOCX() {
    if (document.getElementById("custinfo_ReadCardFlag").value == 0) {
        // 临时情况客户信息
        CreateControl("PsptControlOCX");
        if (checkDriver() == false) {
            return;
        }
        var obj = document.getElementById("PsptCard");
        var ReadTxt;

        if (obj == null) {
            alert("加载身份证阅读控件失败!");
        }

        if (obj != null) {
            document.getElementById("ReadCardButton").value = "加载控件...";

            // 读取证件信息
            ReadTxt = obj.ReadPsptCard();

            // 解析证件信息
            var idCardArr = ReadTxt.split('|');
            if (idCardArr.length <= 0) {
                alert("读取身份证信息失败! 内容: " + ReadTxt);
                return;
            }

            var arrNum = idCardArr[0];
            if (arrNum < 8) {
                alert("读取身份证信息格式不正确! 字段数量:" + arrNum + ", 内容: " + ReadTxt);
                return;
            }

            document.getElementById("ReadCardButton").value = "解析数据...";

            var strCardVersion = idCardArr[1].substr(4, idCardArr[1].length - 4);
            var strPsptId = idCardArr[2].substr(4, idCardArr[2].length - 4);
            var strBirthday = idCardArr[3].substr(4, idCardArr[3].length - 4);
            var strName = idCardArr[4].substr(4, idCardArr[4].length - 4);
            var strSex = idCardArr[5].substr(4, idCardArr[5].length - 4);
            var strMz = idCardArr[6].substr(4, idCardArr[6].length - 4); // 民族
            var strAddress = idCardArr[7].substr(4, idCardArr[7].length - 4);

            // 设置身份证信息到页面
            setIdentityCardInfoToHtml(strCardVersion, strPsptId, strBirthday, strName, strSex, strMz, strAddress);
        }
    } else {
        // 清除页面上身份证信息
        clearHtmlIdentityCardInfo();
    }
}

// 读取身份证提示
function readCardOpenUser(TT) {
    TT = TT - 1;
    if (TT >= 0) {
        if (TT <= 0) {
            readCardByOpenUser();
        } else {
            document.getElementById("ReadCardButton").value = "读取身份证...";
            setTimeout("readCardOpenUser(" + TT + ")", 100);
        }
    }
}

// 读取身份证
function readCardByOpenUser() {
    if (document.getElementById("custinfo_ReadCardFlag").value == 0) {
        // 临时情况客户信息
        CreateControl("PsptControlOCX");
        var obj = document.getElementById("PsptCard");
        var ReadTxt;

        if (obj == null) {
            alert("加载身份证阅读控件失败!");
        }

        if (obj != null) {
            document.getElementById("ReadCardButton").value = "加载控件...";

            // 读取证件信息
            ReadTxt = obj.ReadPsptCard();

            // 解析证件信息
            var idCardArr = ReadTxt.split('|');
            if (idCardArr.length <= 0) {
                alert("读取身份证信息失败! 内容: " + ReadTxt);
                return;
            }

            var arrNum = idCardArr[0];
            if (arrNum < 8) {
                alert("读取身份证信息格式不正确! 字段数量:" + arrNum + ", 内容: " + ReadTxt);
                return;
            }

            document.getElementById("ReadCardButton").value = "解析数据...";

            var strPsptId = idCardArr[2].substr(4, idCardArr[2].length - 4);
            var strName = idCardArr[4].substr(4, idCardArr[4].length - 4);
            var strAddress = idCardArr[7].substr(4, idCardArr[7].length - 4);

            document.getElementById("CUST_NAME").value = strName;
            document.getElementById("POST_ADDRESS").value = strAddress;
            document.getElementById("PSPT_ID").value = strPsptId;
            document.getElementById("PSPT_ADDR").value = strAddress;
            document.getElementById("PSPT_TYPE_CODE").value = "0";
            document.getElementById("custinfo_ReadCardFlag").value = 0;
        }
    } else {
        // 修改二代证读取信息
        document.getElementById("CUST_NAME").disabled = false;
        document.getElementById("POST_ADDRESS").disabled = false;
        document.getElementById("PSPT_ID").disabled = false;
        document.getElementById("PSPT_END_DATE").disabled = false;
        document.getElementById("PSPT_ADDR").disabled = false;
        document.getElementById("custinfo_ReadCardFlag").value = 0;
    }
}

// 读取身份证提示
function readCardRealName(TT) {
    TT = TT - 1;
    if (TT >= 0) {
        if (TT <= 0) {
            readCardByRealName();
        } else {
            document.getElementById("ReadCardButton").value = "读取身份证...";
            setTimeout("readCardRealName(" + TT + ")", 100);
        }
    }
}

// 读取身份证
function readCardByRealName() {
    if (document.getElementById("custinfo_ReadCardFlag").value == 0) {
        // 临时情况客户信息
        CreateControl("PsptControlOCX");
        var obj = document.getElementById("PsptCard");
        var ReadTxt;

        if (obj == null) {
            alert("加载身份证阅读控件失败!");
        }

        if (obj != null) {
            document.getElementById("ReadCardButton").value = "加载控件...";

            // 读取证件信息
            ReadTxt = obj.ReadPsptCard();

            // 解析证件信息
            var idCardArr = ReadTxt.split('|');
            if (idCardArr.length <= 0) {
                alert("读取身份证信息失败! 内容: " + ReadTxt);
                return;
            }

            var arrNum = idCardArr[0];

            if (arrNum < 8) {
                alert("读取身份证信息格式不正确! 字段数量:" + arrNum + ", 内容: " + ReadTxt);
                return;
            }

            document.getElementById("ReadCardButton").value = "解析数据...";

            var strPsptId = idCardArr[2].substr(4, idCardArr[2].length - 4);
            var strName = idCardArr[4].substr(4, idCardArr[4].length - 4);
            var strAddress = idCardArr[7].substr(4, idCardArr[7].length - 4);

            document.getElementById("custinfo_CUST_NAME").value = strName;
            document.getElementById("custinfo_PSPT_ID").value = strPsptId;
            document.getElementById("custinfo_PSPT_ADDR").value = strAddress;
            document.getElementById("custinfo_PSPT_TYPE_CODE").value = "0";
            document.getElementById("custinfo_ReadCardFlag").value = 0;
        }
    } else {
        // 修改二代证读取信息
        document.getElementById("custinfo_CUST_NAME").disabled = false;
        document.getElementById("custinfo_PSPT_ID").disabled = false;
        document.getElementById("custinfo_PSPT_ADDR").disabled = false;
        document.getElementById("custinfo_ReadCardFlag").value = 0;
    }
}

// 读取身份证提示
function readCardModi(TT) {
    TT = TT - 1;
    if (TT >= 0) {
        if (TT <= 0) {
            readCardByRealName();
        } else {
            document.getElementById("ReadCardButton").value = "读取身份证...";
            setTimeout("readCardModi(" + TT + ")", 100);
        }
    }
}

// 读取身份证
function readCardByModi() {
    if (document.getElementById("custinfo_ReadCardFlag").value == 0) {
        // 临时情况客户信息
        CreateControl("PsptControlOCX");

        if (checkDriver() == false) {
            return;
        }

        var obj = document.getElementById("PsptCard");
        var ReadTxt;

        if (obj == null) {
            alert("加载身份证阅读控件失败!");
        }

        if (obj != null) {
            document.getElementById("ReadCardButton").value = "加载控件...";

            // 读取证件信息
            ReadTxt = obj.ReadPsptCard();

            // 解析证件信息
            var idCardArr = ReadTxt.split('|');
            if (idCardArr.length <= 0) {
                alert("读取身份证信息失败! 内容: " + ReadTxt);
                return;
            }

            var arrNum = idCardArr[0];

            if (arrNum < 8) {
                alert("读取身份证信息格式不正确! 字段数量:" + arrNum + ", 内容: " + ReadTxt);
                return;
            }

            document.getElementById("ReadCardButton").value = "解析数据...";

            var strPsptId = idCardArr[2].substr(4, idCardArr[2].length - 4);
            var strName = idCardArr[4].substr(4, idCardArr[4].length - 4);
            var strAddress = idCardArr[7].substr(4, idCardArr[7].length - 4);

            document.getElementById("custInfo_CUST_NAME").value = strName;
            document.getElementById("custInfo_PSPT_ID").value = strPsptId;
            document.getElementById("cond_PSPT_ADDR_NEW").value = strAddress;
            document.getElementById("custInfo_CONTACT").value = strAddress;
            document.getElementById("custInfo_PSPT_TYPE_CODE").value = "0";
            document.getElementById("custinfo_ReadCardFlag").value = 0;
        }
    } else {
        // 修改二代证读取信息
        document.getElementById("custInfo_CUST_NAME").disabled = false;
        document.getElementById("custInfo_PSPT_ID").disabled = false;
        document.getElementById("cond_PSPT_ADDR_NEW").value = strAddress;
        document.getElementById("custInfo_CONTACT").value = strAddress;
        document.getElementById("custinfo_ReadCardFlag").value = 0;
    }
}
/*************************************身份识别相关函数 结束************************************/