var roleCheck = {
    memCountCfgData: null,
    memCountExistData: null,
    setMemberCount: function (memNumCfgData, memNumExistData) {
        this.memCountCfgData = memNumCfgData;
        this.memCountExistData = memNumExistData;
    },
    // 校验成员个数
    checkRoleCountLimit: function (roleCode) {
        var roleNum = this._getRoleObjectCount(roleCode).get("COUNT");
        var existRoleNum = this._getExistMemCount(roleCode);
        var maxNum = this.memCountCfgData.get("MAX_MEMBER_" + roleCode);
        if (maxNum == null || maxNum == undefined || maxNum == "") {
            MessageBox.alert("提示信息", "您选购的商品不允许添加该成员！");
            return false;
        }
        var minNum = this.memCountCfgData.get("MIN_MEMBER_" + roleCode);
        if (minNum == null || minNum == undefined || minNum == "") {
            MessageBox.alert("提示信息", "您选购的商品不允许添加该成员！");
            return false;
        }
        if (maxNum > -1) {
            if (maxNum == 0) {
                MessageBox.alert("提示信息", "您选购的商品不允许添加该成员！");
                return false;
            }
            if ((roleNum + existRoleNum) >= maxNum) {
                MessageBox.alert("提示信息", "已经达到上限，最多允许添加" + maxNum + "个，当前已经" + (roleNum + existRoleNum) + "个！");
                return false;
            }
        }
        return true;
    },
    // 获取角色数量
    _getRoleObjectCount: function (roleCode) {
        var phoneRoleList = familyAccept.getPhoneRoleList();
        var countMap = $.DataMap();
        countMap.put("COUNT", 0);
        countMap.put(roleCode, ",");
        for (var i = 0; i < phoneRoleList.length; i++) {
            var phoneRole = phoneRoleList.get(i);
            if (phoneRole.roleCode == roleCode) {
                countMap.put("COUNT", countMap.get("COUNT") + 1);
                if (phoneRole.sn) {
                    countMap.put(roleCode, countMap.get(roleCode) + phoneRole.sn + ",");
                }
            } else {
                this._getSubRoleCount(phoneRole, roleCode, countMap);
            }
        }
        return countMap;
    },
    _getSubRoleCount: function (subRole, roleCode, countMap) {
        if (!subRole.isEmpty) {
            var subRoles = subRole.getSubRoles();
            if (subRoles != null && subRoles.length > 0) {
                for (var j = 0; j < subRoles.length; j++) {
                    var tmpRole = subRoles[j];
                    if (!tmpRole.isEmpty) {
                        if (tmpRole.roleCode == roleCode) {
                            countMap.put("COUNT", countMap.get("COUNT") + 1);
                            countMap.put(roleCode, countMap.get(roleCode) + "," + tmpRole.sn);
                        } else {
                            this._getSubRoleCount(tmpRole, roleCode, countMap);
                        }
                    }
                }
            }
        }
    },
    // 获取存量角色数量
    _getExistMemCount: function (roleCode) {
        if (null == this.memCountExistData) {
            return 0;
        }
        return parseInt(this.memCountExistData.get(roleCode, '0'));
    },
    checkMemCountLimit: function () {
        var errorInfo = "";
        var _self = this;
        this.memCountCfgData.eachKey(function (key) {
            var v = _self.memCountCfgData.get(key);
            var arr = key.split("_MEMBER_");
            var tt = arr[0];
            var roleCode = arr[1];
            if (v > -1) {
                var roleCount = _self._getRoleObjectCount(roleCode).get("COUNT");
                if (tt == "MIN") {
                    if (roleCount < v) {
                        errorInfo += "【" + constdata.busiTitle[roleCode] + "】角色，最少添加" + v + "个！<br>";
                    }
                } else {
                    if (roleCount > v) {
                        errorInfo += "【" + constdata.busiTitle[roleCode] + "】角色，最多添加" + v + "个！<br>";
                    }
                }
            }
        });
        if (errorInfo.length > 0) {
            MessageBox.alert("提示信息", errorInfo);
            return false;
        }
        return true;
    },
    // 校验角色号码是否已经在成员列表中
    checkSnExistInRoleList: function (roleCode, sn) {
        var roleSnStr = this._getRoleObjectCount(roleCode).get(roleCode);
        if (roleSnStr.indexOf("," + sn + ",") >= 0) {
            MessageBox.alert("查询提示", constdata.busiTitle[roleCode] + "角色，已经添加过该号码【" + sn + "】！");
            return true;
        }
        return false;
    },
    checkSubRolesCount: function (role, subRoleCode) {
        var count = 0;
        var subRoles = role.getSubRoles();
        if (subRoles != null && subRoles.length > 0) {
            for (var j = 0; j < subRoles.length; j++) {
                var tmpRole = subRoles[j];
                if (!tmpRole.isEmpty) {
                    if (tmpRole.roleCode == subRoleCode) {
                        count += 1;
                    }
                }
            }
        }
        if (count > 0) {
            MessageBox.alert("查询提示", "一个" + constdata.busiTitle[role.roleCode] + "角色下，只能添加一个" + constdata.busiTitle[subRoleCode] + "！");
            return false;
        }
        return true;
    }
}