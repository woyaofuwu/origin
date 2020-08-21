--IS_CACHE=Y
SELECT S.DISCNT_CODE,
       S.DISCNT_NAME,
       S.DISCNT_EXPLAIN,
       S.ITEM_TYPE,
       S.ITEM_CODE,
       S.HIGH_FEE,
       decode(S.HALF_NET, 0, 'ÕûÔÂ', 1, '°ëÔÂ', S.HALF_NET) HALF_NET,
       S.ADJUST_TAG,
       S.DISCNT_CODE_A,
       S.DISCNT_STATE,
       S.ENABLE_TAG
  FROM  TD_B_DISCNT_SHARE S,td_s_commpara a
 WHERE a.param_attr = '4455'
   and a.param_code = 'SHARE'
   and s.discnt_code = a.para_code1
   AND Sysdate Between S.Start_Date And S.End_Date