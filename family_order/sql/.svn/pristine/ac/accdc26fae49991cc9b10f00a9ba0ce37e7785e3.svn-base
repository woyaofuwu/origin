UPDATE TF_F_USER_PLATSVC_BOOK A
           SET A.BOOK_STATE = '2',
               A.REMARK     = 'MUSC预约生效--开机发正常',
               UPDATE_TIME  = SYSDATE
          WHERE USER_ID = :USER_ID
            AND BOOK_STATE = 'T'
            AND BIZ_TYPE_CODE='25'