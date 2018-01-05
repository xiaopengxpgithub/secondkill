-- 数据库初始化脚本
-- 创建
CREATE DATABASE sencondkill;

-- 使用数据库
USE sencondkill;

-- 创建表
CREATE TABLE seckill(
  seckill_id BIGINT  NOT NULL AUTO_INCREMENT COMMENT '商品库存id',
  product_name VARCHAR(100) NOT NULL  COMMENT '秒杀商品名称',
  number INT NOT NULL COMMENT '库存数量',
  start_time TIMESTAMP NOT NULL  COMMENT '秒杀开始时间',
  end_time TIMESTAMP NOT NULL  COMMENT '秒杀结束时间',
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (seckill_id),
  KEY idx_start_time(start_time),
  KEY idx_end_time(end_time),
  KEY idx_create_time(create_time)
)ENGINE =InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET='utf8' COMMENT = '秒杀库存表';

-- 初始化数据
INSERT INTO
  seckill(product_name, number, start_time, end_time)
VALUES
  ('1000元秒杀iPhone6','100','2015-11-01 00:00:00','2015-11-02 00:00:00'),
  ('500元秒杀iPad2','200','2015-11-01 00:00:00','2015-11-02 00:00:00'),
  ('300元秒杀小米4','300','2015-11-01 00:00:00','2015-11-02 00:00:00'),
  ('200元秒杀红米note2','400','2015-11-01 00:00:00','2015-11-02 00:00:00');

-- 秒杀成功明细表
-- 用户登录认证相关信息
CREATE TABLE success_kill(
seckill_id BIGINT NOT NULL COMMENT '秒杀商品id',
user_phone BIGINT NOT NULL COMMENT '用户手机',
state TINYINT NOT NULL DEFAULT -1 COMMENT '状态标识:-1,无效; 0,秒杀成功; 1,已付款; 2,已发货',
create_time TIMESTAMP NOT NULL COMMENT '创建时间',
PRIMARY KEY(seckill_id,user_phone), /*联合主键,防止重复秒杀*/
KEY idx_create_time(create_time)
)ENGINE=InnoDB DEFAULT CHARSET ='utf8' COMMENT '秒杀成功明细表';

-- 执行秒杀的存储过程
-- row_count()//返回上一条修改sql的影响行数
USE  sencondkill;
DELIMITER $$  -- 存储过程定义开始
CREATE PROCEDURE execute_seckill_proceduer(IN v_seckill_id BIGINT, IN v_user_phone BIGINT,
    IN v_kill_time TIMESTAMP,OUT r_result INT)
BEGIN
  -- 定义变量
  DECLARE INSERT_COUNT INT DEFAULT 0;
  -- 开启事务
  START TRANSACTION;

  -- 插入购买明细
  INSERT IGNORE INTO success_kill(seckill_id, user_phone, state, create_time)
  VALUES(v_seckill_id,v_user_phone,0,v_kill_time);

  -- 获得上一条修改的sql的影响行数
  SELECT row_count() INTO INSERT_COUNT;

  -- 如果row_count=0,表示没有插入成功;row_count<0,表示sql错误/未执行
  IF (INSERT_COUNT = 0) THEN
    ROLLBACK;
    SET r_result = -1; -- 重复秒杀
  ELSEIF(INSERT_COUNT<0) THEN
    ROLLBACK;
    SET r_result = -2; -- 内部错误

  ELSE
    -- 更新库存
    UPDATE seckill
    SET seckill.number = seckill.number - 1
    WHERE seckill.seckill_id = v_seckill_id
    AND v_kill_time >= seckill.start_time
    AND v_kill_time <= seckill.end_time
    AND seckill.number > 0;

    -- 获得上一条修改的sql的影响行数
    SELECT row_count() INTO INSERT_COUNT;

    -- 如果row_count=0,表示没有修改;row_count<0,表示sql错误/未执行
    IF (INSERT_COUNT = 0) THEN
      ROLLBACK;
      SET r_result = 0; -- 秒杀结束
    ELSEIF(INSERT_COUNT<0) THEN
      ROLLBACK;
      SET r_result = -2; -- 内部错误
    ELSE
      COMMIT; -- 更新成功
      SET r_result = 1;
    END IF ;
  END  if;
end;
$$; -- 存储过程定义结束


-- 显示存储过程的详细信息
SHOW CREATE PROCEDURE execute_seckill_proceduer;

DELIMITER ;
-- 定义变量
SET @r_result=-3;
-- 调用存储过程
CALL sencondkill.execute_seckill_proceduer(1003,12345678912,now(),@r_result);
SELECT @r_result;

-- 存储过程
-- 1.存储过程优化: 事务行级锁持有的时间
-- 2.不要过度依赖存储过程
-- 3.简单的业务逻辑可以使用业务逻辑
