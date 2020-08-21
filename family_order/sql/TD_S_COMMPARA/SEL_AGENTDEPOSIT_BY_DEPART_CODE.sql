SELECT COUNT(A.DEVICE_ID) PARA_CODE1,       
       NVL(SUM(TO_NUMBER(C.SALE_PRICE/100)), 0) PARA_CODE2,       
       F_CHNL_GETAGENTDEPOSIT(:PARA_CODE3) PARA_CODE3,       
       F_CHNL_GETAGENTDEPOSIT(:PARA_CODE3) -       
       NVL(SUM(TO_NUMBER(C.SALE_PRICE/100)), 0) PARA_CODE4,       
       '' PARA_CODE5,       
       '' PARA_CODE6,       
       '' PARA_CODE7,       
       '' PARA_CODE8,       
       '' PARA_CODE9,       
       '' PARA_CODE10,       
       '' PARA_CODE11,       
       '' PARA_CODE12,       
       '' PARA_CODE13,       
       '' PARA_CODE14,       
       '' PARA_CODE15,       
       '' PARA_CODE16,       
       '' PARA_CODE17,       
       '' PARA_CODE18,       
       '' PARA_CODE19,       
       '' PARA_CODE20,       
       '' PARA_CODE21,       
       '' PARA_CODE22,       
       '' PARA_CODE23,       
       '' PARA_CODE24,       
       '' PARA_CODE25,       
       '' PARA_CODE26,       
       '' PARA_CODE27,       
       '' PARA_CODE28,       
       '' PARA_CODE29,       
       '' PARA_CODE30,       
       '' START_DATE,       
       '' END_DATE,       
       '' EPARCHY_CODE,       
       '' REMARK,       
       '' UPDATE_STAFF_ID,       
       '' UPDATE_DEPART_ID,       
       '' UPDATE_TIME,       
       '' SUBSYS_CODE,       
       0 PARAM_ATTR,       
       '' PARAM_CODE,       
       '' PARAM_NAME
  FROM TF_R_MOBILEDEVICE A,       
       TF_R_MOBILEDEVICE_APPEND B,       
       TD_S_PURCHASE_PRICE C
 WHERE A.EPARCHY_CODE = :PARA_CODE1      
   AND A.DEVICE_ID = B.DEVICE_ID      
   AND A.DEVICE_MODEL_CODE = C.DEVICE_MODEL_CODE      
   AND A.FACTORY_CODE = C.FACTORY_CODE      
   AND A.COLOR_CODE = C.COLOR_CODE      
   AND A.FITTING_CODE = C.FITTING_CODE      
   AND B.SUPPLY_TYPE = C.SUPPLY_TYPE      
   AND B.NAMEBRAND_CODE = C.NAMEBRAND_CODE      
   AND B.IS_DATA_LINE = C.IS_DATA_LINE      
   AND B.IS_GIFT_PACKAGE = C.IS_GIFT_PACKAGE      
   AND B.IS_EARPHONE = C.IS_EARPHONE      
   AND B.IS_BLUE_TOOTH = C.IS_BLUE_TOOTH      
   AND A.STOCK_ID =:PARA_CODE2
   AND A.STOCK_LEVEL = 'S'      
   AND A.SALE_TAG = '0'      
   AND A.DEVICE_STATE = '1'      
   AND (:PARA_CODE4 = '' OR :PARA_CODE4 IS NULL)      
   AND (:PARA_CODE5 = '' OR :PARA_CODE5 IS NULL)      
   AND (:PARA_CODE6 = '' OR :PARA_CODE6 IS NULL)      
   AND (:PARA_CODE7 = '' OR :PARA_CODE7 IS NULL)      
   AND (:PARA_CODE8 = '' OR :PARA_CODE8 IS NULL)      
   AND (:PARA_CODE9 = '' OR :PARA_CODE9 IS NULL)      
   AND (:PARA_CODE10 = '' OR :PARA_CODE10 IS NULL)