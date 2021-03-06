package br.usp.marketvc.beans;

import java.util.*;
import java.text.*;
import org.hibernate.*;
import javax.persistence.*;
import br.usp.marketvc.util.*;
import br.usp.marketvc.config.*;
import javax.xml.bind.*;
import java.net.*;
import java.io.*;

public class Market implements Default {
	static {
		List<Stock> stocks = new ArrayList<Stock>();
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		stocks = (ArrayList<Stock>)session.createQuery("select s from Stock s").list();
		Iterator itr = stocks.iterator();
		if (!itr.hasNext()) {
			try {
				JAXBContext jc = JAXBContext.newInstance(Stock.class);
				Unmarshaller unmarshaller = jc.createUnmarshaller();
				for (int i=0;i<companies.length;i++) {
					URL url = new URL("http://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.quotes%20where%20symbol%20in%20(%22"+companies[i]+"%22)&env=store://datatables.org/alltableswithkeys");
					InputStream xmlStream = url.openStream();
					Stock stock = (Stock) unmarshaller.unmarshal(xmlStream);
					stock.update();
					stocks.add(stock);
					session.save(stock);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		session.getTransaction().commit();
		newDay();
		newHour();
		/*TimerTask clock = new Clock();
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(clock,new Date(),(long)interval);*/
	}

	public static void generateQuote(Stock s) {
		Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String yesterday;
		Quote quote = null;
		try {
			calendar.add(Calendar.DATE, -1);
			yesterday = dateFormat.format(calendar.getTime());
			JAXBContext jc = JAXBContext.newInstance(Quote.class);
			Unmarshaller unmarshaller = jc.createUnmarshaller();
			URL url = new URL("http://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.historicaldata%20where%20symbol%20in%20(%22"+s.getSymbol()+"%22)%20and%20startDate%3D%22"+yesterday+"%22%20and%20endDate%3D%22"+yesterday+"%22%0A%09%09&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys");
			InputStream xmlStream = url.openStream();
			quote = (Quote) unmarshaller.unmarshal(xmlStream);
			quote.update();
			s.getQuotes().add(quote);
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.save(quote);
	    } catch (Exception e) {
			e.printStackTrace();
        }
	}
	public static void generateTick(Stock s) {
		Tick tick = null;
		try {
			JAXBContext jc = JAXBContext.newInstance(Tick.class);
			Unmarshaller unmarshaller = jc.createUnmarshaller();
			URL url = new URL("http://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.quotes%20where%20symbol%20in%20(%22"+s.getSymbol()+"%22)&env=store://datatables.org/alltableswithkeys");
			InputStream xmlStream = url.openStream();
			tick = (Tick) unmarshaller.unmarshal(xmlStream);
			tick.update();
			s.getTicks().add(tick);
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.save(tick);
		} catch (Exception e) {
			e.printStackTrace();
        }
	}
	public static void newDay() {
		List<Stock> stocks;
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		stocks = (ArrayList<Stock>)session.createQuery("select s from Stock s").list();
		if (stocks != null) {
			Iterator itr = stocks.iterator();
			while (itr.hasNext()) {
				Stock s = (Stock)itr.next();
				generateQuote(s);
			}
		}
		List<User> users = new ArrayList<User>();
		users = (ArrayList<User>)session.createQuery("select u from User u left join fetch u.loans").list();
		if (users != null) {
			Iterator itr = users.iterator();
			while (itr.hasNext()) {
				User u = (User)itr.next();
				u.newDay();
			}
		}
		session.getTransaction().commit();
	}
	public static void newHour() {
		List<Stock> stocks;
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		stocks = (ArrayList<Stock>)session.createQuery("select s from Stock s").list();
		if (stocks != null) {
			Iterator itr = stocks.iterator();
			while (itr.hasNext()) {
				Stock s = (Stock)itr.next();
				generateTick(s);
			}
		}
		session.getTransaction().commit();
	}
	public static List<Stock> getStocks() {
		List<Stock> stocks = new ArrayList();
		List<Stock> stocks2;
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		for (int i=0;i<companies.length;i++) {
			Stock s1 = (Stock)session.createQuery("select s from Stock s left join fetch s.quotes where s.symbol = :ssymbol").setParameter("ssymbol",companies[i]).uniqueResult();
			Stock s2 = (Stock)session.createQuery("select s from Stock s left join fetch s.ticks where s.symbol = :ssymbol").setParameter("ssymbol",companies[i]).uniqueResult();
			s1.setTicks(s2.getTicks());
			stocks.add(s1);
		}
		return stocks;
	}
	public static List<Investment> getAvailableInvestments() {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		List<Investment> investments = (ArrayList<Investment>)session.createQuery("select i from Investment i where i.selling = :true").setParameter("true",true).list();
		return investments;
	}
}
