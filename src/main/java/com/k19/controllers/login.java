package com.k19.controllers;

import java.io.IOException;
import java.sql.SQLException;

import javax.persistence.NoResultException;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.k19.models.Users;
import com.k19.DAO.UserDAO;
import com.k19.models.member;
import com.k19.models.memberJPA;
import com.google.gson.Gson;
import com.k19.DAO.memberDAO;
import com.k19.DAO.memberJPADAO;

// servlet mapping
@WebServlet(name = "loginConfirmServlet", urlPatterns = { "/member/sign-in/confirm" })
// if first time we will doPost
// in doPost user must login
public class login extends HttpServlet {

    // [GET] /member/sign-in/confirm
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp)
            throws ServletException, IOException {
        this.doPost(req, resp);
    }

    // [POST] /member/sign-in/confirm
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // action
        String action = req.getParameter("action");
        // remember
        String rem = req.getParameter("remember"); // tick - nontick
        // get info
        String email = req.getParameter("email");
        String passwd = req.getParameter("password");

        String mess = null;
        if (action == null) {
            action = "cancel";
        }
        if (rem == null) {
            rem = "nontick";
        }
        if (action.equals("cancel")) {
            resp.sendRedirect(req.getContextPath() + "/member/sign-in");
        } else if (action.equals("submit")) {
            // final member member = memberDAO.checkMember(uname, passwd);
            final Users member = UserDAO.checkUser(email, passwd);

            // check member
            if (member != null) {
                HttpSession session = req.getSession();
                // valid
                // set session
                session.setAttribute("email", email);
                //
                session.setAttribute("member", member.getFirstName()+member.getLastName());
                // create cookies
                Cookie cookiea = new Cookie("usernameCookie", email);
                cookiea.setMaxAge(60 * 60 * 24);
                cookiea.setPath("/");
                resp.addCookie(cookiea);

                // get user id
                Cookie cookiec = new Cookie("userIdCookie", Integer.toString(member.getId()));
                cookiec.setMaxAge(60 * 60 * 24);
                cookiec.setPath("/");
                resp.addCookie(cookiec);
                // Add data base and let sample fetch it
                // memberavtSample sample;

                mess = "Success Login";
                // Get json
                // String jsonMember = new Gson().toJson(member);

                // session.setAttribute("memberJSON", jsonMember);
                //
                // change it to Home
                String url = "/";
                session.setAttribute("message", mess);

                //  getServletContext().getRequestDispatcher(url).forward((ServletRequest) req,
                //  (ServletResponse) resp);
                resp.sendRedirect(url);
            } else {
                // invalid password
                // try to get latest data
                HttpSession session = req.getSession();
                try {
                    if (memberJPADAO.selectMember(email) != null) {
                        mess = "Wrong password, You are " + email + " ?";
                    } else {
                        mess = "We can not define you on my server, You are new ?";
                    }
                    session.setAttribute("message", mess);
                    // resp.sendRedirect(req.getContextPath() + "/member/sign-in");
                } catch (NoResultException e) {
                    e.printStackTrace();
                    session.setAttribute("message", "Server error, try again");
                }
                resp.sendRedirect(req.getContextPath() + "/member/sign-in");
            }

        }
        //

    }
}
