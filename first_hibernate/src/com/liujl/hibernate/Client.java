package com.liujl.hibernate;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;


public class Client {
	private static final Logger logger = (Logger) LogManager.getLogger(Client.class.getName());
	//在main方法中完成所有代码
	public static void main(String[] args) {
		//读取hibernate.cfg.xml文件
		Configuration config=new Configuration().configure();
		
		/**
		 * 创建SessionFactory,
		 * 一个数据库对应一个SessionFactory
		 * SessionFactory是线程安全的
		 */
		@SuppressWarnings("unchecked")
		SessionFactory factory=config.buildSessionFactory();
		
		/**
		 * 创建Session
		 * 这里的Session并不是web中的Session
		 * session只有在使用时，才建立connection ,session还管理缓存
		 * session用完后,必须关闭
		 * session是非线程安全的，一般是一个请求对应一个session
		 */
		Session session=null;
		try{
			session=factory.openSession();
			//手动开启事务，(可以在hibernate.cfg.xml中配置自动开启事务)
			session.beginTransaction();
			
			User u=new User();
			u.setName("智道");
			u.setAge(20);
			u.setCreateTime(new Date());
			u.setExpireTime(new Date());
			
			/**
			 * 保存数据，此处的数据就是保存对象，这就是hibernate操作对象的好处
			 * 我们不需要写那么多jdbc代码，只需要利用session保存对象，至于hibernate怎么存储对象这里我们不需要关心它
			 * 这些都由hibernate完成，我们只需要创建对象后交给hibernate就行了
			 */
			logger.entry();//这句话是什么意思
			logger.info("将要保存user");
			session.save(u);
			//提交事务
			session.getTransaction().commit();
			logger.info("保存成功");
		}catch (Exception e) {
			//保存数据发生异常，回滚
			session.getTransaction().rollback();
			logger.error("保存失败，事务回滚");
			e.printStackTrace();
		}finally{
			//关闭连接
			if(session!=null){
				session.close();
				logger.info("session连接关闭");
			}
		}
	}
}
