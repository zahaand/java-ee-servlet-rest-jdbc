package ru.zahaand.servlet;

import com.google.gson.Gson;
import ru.zahaand.connection.ConnectionUtil;
import ru.zahaand.dao.UserDao;
import ru.zahaand.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

@WebServlet("/users")
public class UserServlet extends HttpServlet {
    private static final int HTTP_STATUS_OK = 200;
    private static final int HTTP_STATUS_CREATED = 201;
    private static final int HTTP_STATUS_NO_CONTENT = 204;
    private static final int HTTP_STATUS_INTERNAL_SERVER_ERROR = 500;

    private UserDao userDao;

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public void init() throws ServletException {
        try {
            userDao = new UserDao(ConnectionUtil.getConnection());
        } catch (SQLException e) {
            throw new ServletException("Error initializing UserDao", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        try {
            User user = userDao.read(id);
            writeResponse(response, user);
        } catch (SQLException e) {
            handleException(response, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = new Gson().fromJson(req.getReader(), User.class);
        try {
            user = userDao.create(user);
            writeResponse(resp, user);
            resp.setStatus(HTTP_STATUS_CREATED);
        } catch (SQLException e) {
            handleException(resp, e);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = new Gson().fromJson(req.getReader(), User.class);
        try {
            user = userDao.update(user);
            writeResponse(resp, user);
        } catch (SQLException e) {
            handleException(resp, e);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int id = Integer.parseInt(req.getParameter("id"));
        try {
            userDao.delete(id);
            resp.setStatus(HTTP_STATUS_NO_CONTENT);
        } catch (SQLException e) {
            handleException(resp, e);
        }
    }

    private void writeResponse(HttpServletResponse resp, User user) throws IOException {
        resp.setContentType("application/json");
        try (PrintWriter writer = resp.getWriter()) {
            writer.write(new Gson().toJson(user));
        }
        resp.setStatus(HTTP_STATUS_OK);
    }

    void handleException(HttpServletResponse resp, SQLException e) throws IOException {
        e.printStackTrace();
        resp.sendError(HTTP_STATUS_INTERNAL_SERVER_ERROR, "An error occurred while processing the request.");
    }
}
