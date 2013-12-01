package fr.twiced.ucoinj;

import java.io.IOException;
import java.util.Properties;

import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;

import fr.twiced.ucoinj.bean.Merkle;
import fr.twiced.ucoinj.bean.Node;
import fr.twiced.ucoinj.bean.PublicKey;
import fr.twiced.ucoinj.bean.Signature;

@Configuration
@ComponentScan("fr.twiced.ucoinj")
public class SpringConfiguration {

	@Bean
	public DriverManagerDataSource dataSource(){
		GlobalConfiguration config = GlobalConfiguration.getInstance();
		DriverManagerDataSource driver = new DriverManagerDataSource();
		driver.setUrl(config.getDBURL());
		driver.setUsername(config.getDBUsername());
		driver.setPassword(config.getDBPassword());
		driver.setDriverClassName("com.mysql.jdbc.Driver");
		return driver;
	}

	@Bean
	public SessionFactory sessionFactory() throws IOException{
		Properties props = new Properties();
		props.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
		props.put("hibernate.show_sql", "true");
		props.put("hibernate.hbm2ddl.auto", "update");
		LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
		sessionFactory.setDataSource(dataSource());
		sessionFactory.setHibernateProperties(props);
		Class<?>[] annotatedClasses = new Class<?>[]{
			Signature.class, PublicKey.class, Node.class, Merkle.class
		};
		sessionFactory.setAnnotatedClasses(annotatedClasses);
		sessionFactory.afterPropertiesSet();
		return sessionFactory.getObject();
	}
	
	@Bean
	@DependsOn("sessionFactory")
	public HibernateTransactionManager transactionManager() throws IOException{
		HibernateTransactionManager txManager = new HibernateTransactionManager();
		txManager.setSessionFactory(sessionFactory());
		return txManager;
	}
}
