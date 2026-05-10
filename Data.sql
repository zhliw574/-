-- 插入测试用户
INSERT INTO user(username,password,email,month_budget)
VALUES
('zhangsan','123456','zhangsan@qq.com',3000.00),
('lisi','654321','lisi@qq.com',5000.00);

-- 插入测试管理员
INSERT INTO admin(admin_name,password,email)
VALUES
('admin','admin123','admin@qq.com');

-- 插入用户1（zhangsan）的本月流水
INSERT INTO transaction(user_id,amount,trans_date,trans_type)
VALUES
(1,800.00,'2026-05-01','餐饮'),
(1,200.00,'2026-05-05','水电'),
(1,150.00,'2026-05-08','交通'),
(1,300.00,'2026-05-10','生活用品'),
(1,4500.00,'2026-05-15','工资'),
(1,100.00,'2026-05-20','其他');

-- 插入用户2（lisi）的本月流水
INSERT INTO transaction(user_id,amount,trans_date,trans_type)
VALUES
(2,600.00,'2026-05-02','餐饮'),
(2,250.00,'2026-05-06','水电'),
(2,180.00,'2026-05-09','交通');