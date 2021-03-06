--IS_CACHE=Y
SELECT PARA_ATTR,PARA_CODE1,PARA_CODE2,PARA_NAME,PARA_VALUE1,REMARK,DECODE(VALID_TAG,'0','有效','1','无效') VALID_TAG,VALID_TAG VALIDATE_FLAG,
       TO_CHAR(UPDATE_TIME,'YYYY-MM-DD') UPDATE_TIME,f_res_getcodename('STAFF_ID',UPDATE_STAFF_ID,'','') UPDATE_STAFF_ID,
       f_res_getcodename('depart_id',UPDATE_DEPART_ID,'','') UPDATE_DEPART_ID FROM TD_M_RES_PARA WHERE 
       EPARCHY_CODE='ZZZZ' AND PARA_ATTR='4001'
       AND (:ALIDATE_FLAG IS NULL OR VALID_TAG = :ALIDATE_FLAG)
       AND (:PARAMETER_TYPE IS NULL OR PARA_CODE1 = :PARAMETER_TYPE)
       AND (:PARA_CODE2 IS NULL OR PARA_CODE2 = :PARA_CODE2)