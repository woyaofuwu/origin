UPDATE TF_F_USER_PLATSVC_BOOK A
   SET A.BOOK_STATE = '1', A.REMARK = '全退订取消预约'
 WHERE A.USER_ID = :USER_ID
   AND A.ORG_DOMAIN = :ORG_DOMAIN
   AND A.BOOK_STATE = '0'
   AND A.BIZ_TYPE_CODE = (SELECT BIZ_TYPE_CODE
                            FROM TD_B_PLATSVC_PARAM
                           WHERE ORG_DOMAIN = A.ORG_DOMAIN
                             AND SERV_TYPE = '2')