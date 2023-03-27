package ru.zahaand.servlet;

import com.google.gson.Gson;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.zahaand.dao.UserDao;
import ru.zahaand.model.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UserServletTest {
    @Mock
    private UserDao userDao;
    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    private UserServlet userServlet;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        userServlet = new UserServlet();
        userServlet.setUserDao(userDao);
    }

    @Test
    public void testDoGet() throws Exception {
        int id = 1;
        User expectedUser = new User();

        when(request.getParameter("id")).thenReturn(String.valueOf(id));
        when(userDao.read(id)).thenReturn(expectedUser);

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);

        when(response.getWriter()).thenReturn(writer);

        userServlet.doGet(request, response);

        ArgumentCaptor<User> argument = ArgumentCaptor.forClass(User.class);
        verify(userDao).read(eq(id));
        verify(response).setStatus(eq(HttpServletResponse.SC_OK));
        verify(response).setContentType(eq("application/json"));
        writer.flush();

        String result = stringWriter.toString().trim();
        User actualUser = new Gson().fromJson(result, User.class);

        assertEquals(expectedUser, actualUser);
    }

    @Test
    public void testDoPost() throws Exception {
        User expectedUser = new User();

        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(new Gson().toJson(expectedUser))));
        when(userDao.create(any(User.class))).thenReturn(expectedUser);

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);

        when(response.getWriter()).thenReturn(writer);

        userServlet.doPost(request, response);

        ArgumentCaptor<User> argument = ArgumentCaptor.forClass(User.class);
        verify(userDao).create(argument.capture());
        verify(response).setStatus(eq(HttpServletResponse.SC_CREATED));
        verify(response).setContentType(eq("application/json"));
        writer.flush();

        User actualUser = argument.getValue();
        String result = stringWriter.toString().trim();
        assertEquals(expectedUser, actualUser);
    }

    @Test
    public void testDoPut() throws Exception {
        int id = 1;
        User expectedUser = new User();

        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(new Gson().toJson(expectedUser))));
        when(userDao.update(any(User.class))).thenReturn(expectedUser);

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);

        when(response.getWriter()).thenReturn(writer);

        userServlet.doPut(request, response);

        ArgumentCaptor<User> argument = ArgumentCaptor.forClass(User.class);
        verify(userDao).update(argument.capture());
        verify(response).setStatus(eq(HttpServletResponse.SC_OK));
        verify(response).setContentType(eq("application/json"));
        writer.flush();

        User actualUser = argument.getValue();
        String result = stringWriter.toString().trim();
        assertEquals(expectedUser, actualUser);
    }

    @Test
    public void testDoDelete() throws Exception {
        int id = 1;

        when(request.getParameter("id")).thenReturn(String.valueOf(id));

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);

        when(response.getWriter()).thenReturn(writer);

        userServlet.doDelete(request, response);

        verify(userDao).delete(eq(id));
        verify(response).setStatus(eq(HttpServletResponse.SC_NO_CONTENT));
    }

    @Test
    public void testHandleException() throws Exception {
        SQLException expectedException = new SQLException("Test exception");

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);

        when(response.getWriter()).thenReturn(writer);

        userServlet.handleException(response, expectedException);

        verify(response).setStatus(eq(HttpServletResponse.SC_INTERNAL_SERVER_ERROR));
        String result = stringWriter.toString().trim();
        assertEquals("An error occurred while processing the request.", result);
    }
}
