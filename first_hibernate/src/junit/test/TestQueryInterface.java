package junit.test;

import java.util.Iterator;
import java.util.List;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.junit.BeforeClass;
import org.junit.Test;

import com.liujl.hibernate.User;

public class TestQueryInterface {
	private static final  Logger logger=(Logger) LogManager.getLogger(TestQueryInterface.class.getName());
	private static SessionFactory sessionFactory;
	
	
	@BeforeClass
	public static void init(){
		Configuration config=new Configuration().configure();
		sessionFactory=config.buildSessionFactory();
	}
	
	@Test
	public void testQuery1(){
		Session session=null;
		Transaction tx=null;
		User user=null;
		
		try{
			session=sessionFactory.openSession();
			tx=session.beginTransaction();
			
			/*Query session.createQuery(String hql)方法;
			* hibernate的session.createQuery()方法是使用HQL(hibernate的查询语句)语句查询对象的。
			* hql：是查询对象的，例如："from User"，其中from不区分大小写，而User是区分大小写，因为它是对象。
			是User类
			* 返回Query对象。
			* 执行这条语句后，Hibernate会根据配置文件中所配置的数据库适配器自动生成相应数据库的SQL语句。*/
			Query query= session.createQuery("from User");
			//分页查询
			query.setFirstResult(0);
			query.setMaxResults(3);
			
			List<User> userList=query.list();
			for(Iterator iter=userList.iterator();iter.hasNext();){
				User u=(User) iter.next();
				System.out.println(u.getId()+" "+u.getName()+" "+u.getAge()+" "+u.getCreateTime()+" "+u.getExpireTime());
			}
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
