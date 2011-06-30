package br.usp.marketvc.servlets;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
// Para Hibernate 
import org.hibernate.*;
import br.usp.marketvc.util.*;
import br.usp.marketvc.beans.*;
import br.usp.marketvc.bundles.*;
import br.usp.marketvc.config.*;
import java.util.*;
import org.apache.commons.fileupload.*;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.io.output.*;

public class UserServlet extends HttpServlet implements Default {

  public void doGet(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
	PrintWriter out = response.getWriter();
	User user,user2;
	HttpSession usession;
    int op = LOGOUT;

	boolean isMultipart = ServletFileUpload.isMultipartContent(request);
	Date birth = new Date();
	user = new User();    
	if (isMultipart) {
		FileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);
		List items = null;
		try {
			items = upload.parseRequest(request);
		} catch (FileUploadException e) {
			e.printStackTrace();
		}
		Iterator itr = items.iterator();
		while (itr.hasNext()) {
			FileItem item = (FileItem) itr.next();
			
			if (item.isFormField()) { // form field
				if (item.getFieldName().equals("email")) user.setEmail(item.getString());
				else if (item.getFieldName().equals("pass")) user.setPasswd(item.getString()); 
					 else if (item.getFieldName().equals("name")) user.setName(item.getString());
						  else if (item.getFieldName().equals("byear")) birth.setYear(Integer.parseInt(item.getString())-1900);
							   else if (item.getFieldName().equals("bmonth")) birth.setMonth(Integer.parseInt(item.getString())-1);
									else if (item.getFieldName().equals("bday")) birth.setDate(Integer.parseInt(item.getString()));
										 else if (item.getFieldName().equals("phone")) user.setPhone(item.getString());
											  else if (item.getFieldName().equals("op")) op = Integer.parseInt(item.getString());
			} else { // is a file
				user.setPhoto(item.get());
			}
		}
	}
	else op = Integer.parseInt(request.getParameter("op"));
	switch (op) {
		case INSERT:
				try {
					user.setBirth(birth);
					Session session = HibernateUtil.getSessionFactory().getCurrentSession();
					session.beginTransaction();
					session.save(user);
					session.getTransaction().commit();
					response.sendRedirect("login.jsp");
				} catch (Exception e) { e.printStackTrace(); 
					response.sendRedirect("signup.jsp?msg=1");			
				}
			break;
		case LOGIN:
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			user2 = new User();
			user2.setEmail(request.getParameter("email"));
			user2.setPasswd(request.getParameter("pass"));
			user = (User) session.createQuery("select u from User u where u.email = :uemail and u.passwd = :upasswd").setParameter("uemail", user2.getEmail()).setParameter("upasswd", user2.getPasswd()).uniqueResult();				
			session.getTransaction().commit();
			if (user == null) { response.sendRedirect("login.jsp"); }
			else {
				usession = request.getSession();
				usession.setAttribute("user", user);
				response.sendRedirect("index.jsp");
			}			
			break;
		case LOGOUT:
			usession = request.getSession();
			usession.invalidate();
			response.sendRedirect("login.jsp");
			break;
		case VIEW_PROFILE:
			usession = request.getSession();
			user = (User) usession.getAttribute("user");
			if (user == null){
				response.sendRedirect("login.jsp");
			}
			else
				response.sendRedirect("profile.jsp");
			break;
		case VIEW_FUNDS:
			usession = request.getSession();
			user = (User) usession.getAttribute("user");
			if (user == null){
				response.sendRedirect("login.jsp");
			}
			else
				response.sendRedirect("funds.jsp");
			break;
		case VIEW_INVESTMENTS:
			usession = request.getSession();
			user = (User) usession.getAttribute("user");
			if (user == null){
				response.sendRedirect("login.jsp");
			}
			else
				response.sendRedirect("investments.jsp");
			break;
	}

  }

  public void doPost(HttpServletRequest request,HttpServletResponse response)throws ServletException, IOException 
  {
      	doGet(request, response);
  }

}


