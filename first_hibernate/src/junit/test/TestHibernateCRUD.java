package junit.test;

import java.util.Date;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.junit.BeforeClass;
import org.junit.Test;
import com.liujl.hibernate.User;
public class TestHibernateCRUD {
private static SessionFactory sessionFactory=null;
private static final Logger logger=(Logger) LogManager.getLogger(org.hibernate.Session.class.getName());
	
@BeforeClass public static void init(){
		//读取配置hibernate.cfg.xml
		Configuration config=new Configuration().configure();
		sessionFactory=config.buildSessionFactory();
	}
	@Test
	public void testSave1() {
		Session session=null;
		Transaction tx = null;
		User user=null;
		try{
			session=sessionFactory.openSession();
			tx=	session.beginTransaction();
		    user=new User();
			user.setAge(26);
			user.setName("刘江龙");
			user.setCreateTime(new Date());
			user.setExpireTime(new Date());
			
			logger.debug("user即将进入persistent状态");
			/*persistent状态
			  persistent状态的对象，当属性发生变化时，hibernate会自动跟数据库保持同步
			*/
			session.save(user);
			
			user.setAge(25);
			
			/**
			 * 实际上user.setAge(25);此时已经发出了一条update指令了
			 * 也可以显示的调用update指定
			 * session.update(user);
			 */
			logger.debug("session提交事务--【前】还未发送语句");
			tx.commit();
			logger.debug("session提交事务--【后】,发出insert update语句?");
		}catch (HibernateException e) {
			e.printStackTrace();
			tx.rollback();
		}finally{
			if(session!=null){
				session.close();
			}
		}
		
		/**
		 * session已经关闭，这时候user对象的状态就变为detached状态
		 * 所有的user对象已经不被session管理，但数据库中确实存在与之对应的记录(age=25)
		 */
		//detached状态(分离独立状态)
		user.setAge(18);
		
		try{
			session=sessionFactory.openSession();
			tx=session.beginTransaction();
			
			/**
			 * 此时，session有对user对象进行管理
			 * session发出update指令后,进行更新数据为(age=18)
			 */
			session.update(user);
			
			//update后user对象的状态又变为persistent状态(坚固状态)
			logger.debug("session提交事务--前");
			tx.commit();
			//session提交事务，发出update语句
			logger.debug("session提交事务--后，发出update语句？");
		}catch (HibernateException e) {
			e.printStackTrace();
			tx.rollback();
		}finally{
			if(session!=null){
				session.close();
			}
		}
	}

	/**
	 * 查询方法get
	 */
	@Test 
	public void testGet(){
		Session session=null;
		Transaction tx=null;
		User user=null;
		try{
			session=sessionFactory.openSession();
			tx=session.beginTransaction();
			/*
			* Object org.hibernate.Session.get(Class arg0, Serializable arg1) throws
			HibernateException
			* arg0:需要加载对象的类，例如：User.class
			* arg1:查询条件(实现了序列化接口的对象)：例"4028940447f7bf260147f7bf27aa0000"字
			符串已经实现了序列化接口。
			* 此方法返回类型为Object，也就是对象，然后我们再强行转换为需要加载的对象就可以了。
			如果数据不存在，则返回null
			* 执行此方法时立即发出查询SQL语句。加载User对象。
			*/
			
			user =(User)session.get(User.class, "4028940447f7bf260147f7bf27aa0000");
			//数据加载完后的状态为persistent状态。数据将与数据库同步。
			logger.debug("user.name= "+user.getName());
			user.setName("龙哥");
			tx.commit();
		}catch (HibernateException e) {
			e.printStackTrace();
			tx.rollback();
		}finally{
			if(session!=null){
				session.close();
			}
		}
	}
	
	/**
	 * 查询方法load
	 */
	@Test 
	public void testLoad(){
		Session session=null;
		Transaction tx=null;
		User user=null;
		try{
			session=sessionFactory.openSession();
			tx=session.beginTransaction();
			/** arg0:需要加载对象的类，例如：User.class
			* arg1:查询条件(实现了序列化接口的对象)：例"4028940447f7bf260147f7bf27aa0000"字符串已经实现
			了序列化接口。
			* 此方法返回类型为Object，但返回的是代理对象。
			* 执行此方法时不会立即发出查询SQL语句。只有在使用对象时，它才发出查询SQL语句，加载对象。
			* 因为load方法实现了lazy(称为延迟加载、赖加载)
			* 延迟加载：只有真正使用这个对象的时候，才加载(才发出SQL语句)
			* hibernate延迟加载实现原理是代理方式。
			* 采用load() 方法加载数据， 如果数据库中没有相应的记录， 则会抛出异常对象不找到
			(org.hibernate.ObjectNotFoundException)
			*/
			user =(User)session.load(User.class, "4028940447f7bf260147f7bf27aa0000");
			//数据加载完后的状态为persistent状态。数据将与数据库同步。
//			logger.debug("user.name= "+user.getName());
			user.setName("发哥");
			logger.debug("使用了user对象 setter，但是没有提交，是否发送了select语句？");
			tx.commit();
			logger.debug("session提交了事务，是否发出update语句?");
		}catch (HibernateException e) {
			e.printStackTrace();
			tx.rollback();
		}finally{
			if(session!=null){
				session.close();
			}
		}
	}
	
	/**
	 * 删除方法
	 */
	@Test
	public void testDelete1(){
		Session session=null;
		Transaction tx=null;
		User user=null;
		try{
			session=sessionFactory.openSession();
			tx=session.beginTransaction();
			user=(User)session.load(User.class, "4028940447f7bf260147f7bf27aa0000");//根据主键加载对象
			logger.debug("load延迟加载user对象，由于暂时没有用到user对象的方法，所以不会发送select语句");
			session.delete(user);
			//user对象删除后，回复到transient 临时状态,随时可以被jvm回收
			logger.debug("session未提交事务，由于使用了user对象，load延迟加载的代理对象发送了了select语句？");
			tx.commit();
		}catch (HibernateException e) {
			e.printStackTrace();
			tx.rollback();
		}finally{
			if(session!=null){
				session.close();
			}
		}
	}

}


	