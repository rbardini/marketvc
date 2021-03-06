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

public class Bank implements Default {
	private static Double interestrate;
	static {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		Long nloans = (Long)session.createQuery("select count(*) from Loan").uniqueResult();
		interestrate = 0.3 + 0.01 * nloans;
	}
	public static Double getInterestRate() {
		return interestrate;
	}
	public static Double newLoan() {
		interestrate += 0.01;
		return (interestrate - 0.01);
	}
	public static void paidLoan() {
		if (interestrate >= 0.11) interestrate -= 0.01;
	}
}
