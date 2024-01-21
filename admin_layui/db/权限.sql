INSERT INTO `f_security`( `fdescription`, `fname`, `fpriority`, `fparentid`, `furl`)  (select '开放api', '开放api',3, fid, '/userApi/list' from f_security where fname='用户管理');
INSERT INTO `f_role_security`(`fsecurityid`, `froleid`) (select fid,'1' from f_security where furl='/userApi/list');
INSERT INTO `f_security`( `fdescription`, `fname`, `fpriority`, `fparentid`, `furl`)  (select '启用', '启用',4, fid, '/userApi/enable' from f_security where fname='开放api');
INSERT INTO `f_role_security`(`fsecurityid`, `froleid`) (select fid,'1' from f_security where furl='/userApi/enable');
INSERT INTO `f_security`( `fdescription`, `fname`, `fpriority`, `fparentid`, `furl`)  (select '禁用', '禁用',4, fid, '/userApi/disable' from f_security where fname='开放api');
INSERT INTO `f_role_security`(`fsecurityid`, `froleid`) (select fid,'1' from f_security where furl='/userApi/disable');

