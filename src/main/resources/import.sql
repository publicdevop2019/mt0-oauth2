---- *** manually insert below record if you running it in development mode for fist time***
INSERT INTO user_ (ID,Created_at,deleted,Email,PASSWORD,modified_at,locked,granted_authorities,created_by,subscription,version) VALUES(838330249904129,CURRENT_TIMESTAMP,0,'haolinwei2015@gmail.com','$2a$10$AWv./gLENRDp7ghJbawG1uSYa0Qlf4xwaaMW8LHwcEOBdVOB6UPZW',CURRENT_TIMESTAMP,false,'ROLE_ROOT,ROLE_ADMIN,ROLE_USER','script',true,0);
INSERT INTO user_ (ID,Created_at,deleted,Email,PASSWORD,modified_at,locked,granted_authorities,created_by,subscription,version) VALUES(838330249904130,CURRENT_TIMESTAMP,0,'haolinwei2017@gmail.com','$2a$10$AWv./gLENRDp7ghJbawG1uSYa0Qlf4xwaaMW8LHwcEOBdVOB6UPZW',CURRENT_TIMESTAMP,false,'ROLE_ADMIN,ROLE_USER','script',false,0);
INSERT INTO user_ (ID,Created_at,deleted,Email,PASSWORD,modified_at,locked,granted_authorities,created_by,subscription,version) VALUES(838330249904131,CURRENT_TIMESTAMP,0,'haolinwei2018@gmail.com','$2a$10$AWv./gLENRDp7ghJbawG1uSYa0Qlf4xwaaMW8LHwcEOBdVOB6UPZW',CURRENT_TIMESTAMP,false,'ROLE_USER','script',false,0);

INSERT INTO client (id,created_at,created_by,deleted,deleted_at,deleted_by,modified_at,modified_by,restored_at,restored_by,accessible_,authorities,auto_approve,redirect_urls,authorization_code_gt_access_token_validity_seconds,authorization_code_gt_enabled,client_credentials_gt_access_token_validity_seconds,client_credentials_gt_enabled,client_id,description,name,pwd_gt_refresh_token_gt_enabled,pwd_gt_refresh_token_gt_refresh_token_validity_seconds,password_gt_access_token_validity_seconds,password_gt_enabled,scopes,secret,version)VALUES(843498099048450,'2020-12-24 21:22:27','0',0,NULL,NULL,'2020-12-24 21:22:27','0',NULL,NULL,1,'ROLE_TRUST,ROLE_FIRST_PARTY,ROLE_BACKEND,ROLE_ROOT',0,'',120,0,120,1,'0C8AZTODP4HT',NULL,'identityAccess',0,0,120,0,'READ,TRUST,WRITE','$2a$12$3ke5rpx81DFfYlLXpQW6TOvTw2W8OkG.p5oFOmFsnmh7LVp11khna',0),(843509306228737,'2020-12-25 03:18:43','0',0,NULL,NULL,'2020-12-25 03:18:43','0',NULL,NULL,1,'ROLE_TRUST,ROLE_FIRST_PARTY,ROLE_BACKEND,ROLE_ROOT',0,'',120,0,120,1,'0C8AZYTQ5W5C',NULL,'edgeProxy',0,0,120,0,'READ,TRUST,WRITE','$2a$12$.HPmCVWM86g.OmWmWgsttuXB95onmQXhsNqwA.ugS9qdn/rxnLk/6',0),(843509757116417,'2020-12-25 03:33:03','0',0,NULL,NULL,'2020-12-25 03:33:03','0',NULL,NULL,0,'ROLE_TRUST,ROLE_FRONTEND,ROLE_ROOT',0,'',120,0,120,0,'0C8AZZ16LZB4',NULL,'adminUILogin',1,0,120,1,'READ,TRUST,WRITE','$2a$12$lBpm19kTPZserQCa72/67OAPAjPZWc0yJ9Kh6xPYPYOSVuFWS94IC',0),(843511877861378,'2020-12-25 04:40:28','0',0,NULL,NULL,'2020-12-25 04:40:28','0',NULL,NULL,0,'ROLE_FIRST_PARTY,ROLE_FRONTEND,ROLE_ROOT',0,'',120,0,120,1,'0C8B00098WLD',NULL,'adminUIRegister',0,0,120,0,'READ,TRUST,WRITE','$2a$12$lBpm19kTPZserQCa72/67OAPAjPZWc0yJ9Kh6xPYPYOSVuFWS94IC',0),(843512635457539,'2020-12-25 05:04:33','0',0,NULL,NULL,'2020-12-25 05:04:33','0',NULL,NULL,0,'ROLE_TRUST,ROLE_FRONTEND,ROLE_ROOT',0,'',120,0,120,0,'0C8B00CSATJ6',NULL,'testPasswordOnly',0,0,120,1,'READ,TRUST,WRITE','$2a$12$k73Ru5FaxyoX5NxQ775SxefRDY04HVvUNekQ.X/tBZJcsUuBUqiMS',0),(843594578001923,'2020-12-27 00:29:26','0',0,NULL,NULL,'2020-12-27 00:29:26','0',NULL,NULL,0,'ROLE_FIRST_PARTY,ROLE_FRONTEND',1,'http://localhost:4200/account,http://localhost:4200/zh-Hans/account',120,1,120,0,'0C8B11ZYRXFM',NULL,'objectMarket',0,0,120,0,'READ,TRUST,WRITE','$2a$12$5PlVPPijEbr69oYIdC9e2uvDzW7NPqxnoxU9aKFiemB2vG7UmaC1S',0),(843498099048451,'2020-12-24 21:22:27','0',0,NULL,NULL,'2020-12-24 21:22:27','0',NULL,NULL,1,'ROLE_TRUST,ROLE_FIRST_PARTY,ROLE_BACKEND,ROLE_ROOT',0,'',120,0,120,1,'0C8AZTODP4H0',NULL,'eventStore',0,0,120,0,'READ,TRUST,WRITE','$2a$12$3ke5rpx81DFfYlLXpQW6TOvTw2W8OkG.p5oFOmFsnmh7LVp11khna',0),(843498099048452,'2020-12-24 21:22:27','0',0,NULL,NULL,'2020-12-24 21:22:27','0',NULL,NULL,1,'ROLE_TRUST,ROLE_FIRST_PARTY,ROLE_BACKEND',0,'',120,0,120,1,'0C8AZTODP4H1',NULL,'sagaOrchestrator',0,0,120,0,'READ,TRUST,WRITE','$2a$12$3ke5rpx81DFfYlLXpQW6TOvTw2W8OkG.p5oFOmFsnmh7LVp11khna',0);
INSERT INTO client (id,created_at,created_by,deleted,deleted_at,deleted_by,modified_at,modified_by,restored_at,restored_by,accessible_,authorities,auto_approve,redirect_urls,authorization_code_gt_access_token_validity_seconds,authorization_code_gt_enabled,client_credentials_gt_access_token_validity_seconds,client_credentials_gt_enabled,client_id,description,name,pwd_gt_refresh_token_gt_enabled,pwd_gt_refresh_token_gt_refresh_token_validity_seconds,password_gt_access_token_validity_seconds,password_gt_enabled,scopes,secret,version)VALUES(843498099048453,'2020-12-24 21:22:27','0',0,NULL,NULL,'2020-12-24 21:22:27','0',NULL,NULL,1,'ROLE_TRUST,ROLE_FIRST_PARTY,ROLE_BACKEND',0,'',120,0,120,1,'0C8AZTODP4H2',NULL,'bbsBackend',0,0,120,0,'TRUST','$2a$12$3ke5rpx81DFfYlLXpQW6TOvTw2W8OkG.p5oFOmFsnmh7LVp11khna',0),(843498099048454,'2020-12-24 21:22:27','0',0,NULL,NULL,'2020-12-24 21:22:27','0',NULL,NULL,1,'ROLE_TRUST,ROLE_FIRST_PARTY,ROLE_BACKEND',0,'',120,0,120,1,'0C8AZTODP4H3',NULL,'payment',0,0,120,0,'TRUST','$2a$12$3ke5rpx81DFfYlLXpQW6TOvTw2W8OkG.p5oFOmFsnmh7LVp11khna',0),(843498099048455,'2020-12-24 21:22:27','0',0,NULL,NULL,'2020-12-24 21:22:27','0',NULL,NULL,1,'ROLE_TRUST,ROLE_FIRST_PARTY,ROLE_BACKEND',0,'',120,0,120,1,'0C8AZTODP4H4',NULL,'fileUpload',0,0,120,0,'READ,TRUST,WRITE','$2a$12$3ke5rpx81DFfYlLXpQW6TOvTw2W8OkG.p5oFOmFsnmh7LVp11khna',0),(843498099048456,'2020-12-24 21:22:27','0',0,NULL,NULL,'2020-12-24 21:22:27','0',NULL,NULL,1,'ROLE_TRUST,ROLE_FIRST_PARTY,ROLE_BACKEND',0,'',120,0,120,1,'0C8AZTODP4H5',NULL,'messenger',0,0,120,0,'READ,TRUST,WRITE','$2a$12$3ke5rpx81DFfYlLXpQW6TOvTw2W8OkG.p5oFOmFsnmh7LVp11khna',0),(843498099048457,'2020-12-24 21:22:27','0',0,NULL,NULL,'2020-12-24 21:22:27','0',NULL,NULL,1,'ROLE_TRUST,ROLE_FIRST_PARTY,ROLE_BACKEND',0,'',120,0,120,1,'0C8AZTODP4H6',NULL,'product',0,0,120,0,'READ,TRUST,WRITE','$2a$12$3ke5rpx81DFfYlLXpQW6TOvTw2W8OkG.p5oFOmFsnmh7LVp11khna',0),(843498099048458,'2020-12-24 21:22:27','0',0,NULL,NULL,'2020-12-24 21:22:27','0',NULL,NULL,1,'ROLE_TRUST,ROLE_FIRST_PARTY,ROLE_BACKEND',0,'',120,0,120,1,'0C8AZTODP4H7',NULL,'userProfile',0,0,120,0,'READ,TRUST,WRITE','$2a$12$3ke5rpx81DFfYlLXpQW6TOvTw2W8OkG.p5oFOmFsnmh7LVp11khna',0),(843498099048459,'2020-12-24 21:22:27','0',0,NULL,NULL,'2020-12-24 21:22:27','0',NULL,NULL,1,'ROLE_FRONTEND',0,'',120,0,120,1,'0C8AZTODP4H8',NULL,'rightRoleNotSufficientResourceId',0,0,120,0,'READ,TRUST,WRITE','$2a$12$5PlVPPijEbr69oYIdC9e2uvDzW7NPqxnoxU9aKFiemB2vG7UmaC1S',0),(843594578001960,'2020-12-27 00:29:26','0',0,NULL,NULL,'2020-12-27 00:29:26','0',NULL,NULL,0,'ROLE_FIRST_PARTY,ROLE_FRONTEND',1,'http://localhost:3000/account,http://localhost:3000/zh-Hans/account',120,1,120,0,'0C8AZTODP4H9',NULL,'bbs',0,0,120,0,'READ,TRUST,WRITE','$2a$12$5PlVPPijEbr69oYIdC9e2uvDzW7NPqxnoxU9aKFiemB2vG7UmaC1S',0),(843512635457561,'2020-12-25 05:04:33','0',0,NULL,NULL,'2020-12-25 05:04:33','0',NULL,NULL,1,'ROLE_FIRST_PARTY,ROLE_BACKEND',0,'',120,0,120,0,'0C8AZTODP4I0',NULL,'resourceId',0,0,120,1,'READ,TRUST,WRITE','$2a$12$k73Ru5FaxyoX5NxQ775SxefRDY04HVvUNekQ.X/tBZJcsUuBUqiMS',0);
INSERT INTO resources_map (id,client_id) VALUES(843509757116417,'0C8AZTODP4H4'),(843509757116417,'0C8AZTODP4H0'),(843509757116417,'0C8AZTODP4H6'),(843509757116417,'0C8AZTODP4HT'),(843509757116417,'0C8AZTODP4H7'),(843509757116417,'0C8AZYTQ5W5C'),(843509757116417,'0C8AZTODP4H1'),(843509757116417,'0C8AZTODP4H2'),(843511877861378,'0C8AZTODP4HT'),(843594578001923,'0C8AZTODP4H7'),(843594578001923,'0C8AZTODP4H6'),(843512635457539,'0C8AZTODP4H4'),(843512635457539,'0C8AZTODP4H6'),(843512635457539,'0C8AZTODP4HT'),(843512635457539,'0C8AZTODP4H7'),(843512635457539,'0C8AZYTQ5W5C'),(843498099048452,'0C8AZTODP4H3'),(843498099048452,'0C8AZTODP4H5'),(843498099048452,'0C8AZTODP4H6'),(843498099048452,'0C8AZTODP4H1'),(843498099048456,'0C8AZTODP4HT'),(843498099048458,'0C8AZTODP4H3'),(843498099048458,'0C8AZTODP4H5'),(843498099048458,'0C8AZTODP4H6'),(843498099048458,'0C8AZTODP4H1'),(843509306228737,'0C8AZTODP4HT'),(843498099048459,'0C8AZTODP4HT'),(843498099048450,'0C8AZTODP4H5'),(843498099048450,'0C8AZTODP4HT'),(843498099048450,'0C8AZYTQ5W5C'),(843594578001960,'0C8AZTODP4H4'),(843594578001960,'0C8AZTODP4H2'),(843512635457561,'0C8AZTODP4HT');