SELECT HOME_ADDR,rsrv_str1,rsrv_tag3 REG_STATUS,count(1) PRE_REG_NUMBER
  FROM TF_F_WIDENET_BOOK A
 WHERE   rsrv_tag3 = :REG_STATUS
   group by HOME_ADDR,rsrv_str1,rsrv_tag3