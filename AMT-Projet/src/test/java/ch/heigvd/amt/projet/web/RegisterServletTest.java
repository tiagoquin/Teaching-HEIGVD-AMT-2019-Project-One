package ch.heigvd.amt.projet.web;

import ch.heigvd.amt.projet.dao.UsersManagerLocal;
import ch.heigvd.amt.projet.dao.exception.DuplicateKeyException;
import ch.heigvd.amt.projet.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterServletTest {

    @Mock
    HttpServletResponse response;

    @Mock
    HttpServletRequest request;

    @Mock
    UsersManagerLocal usersManager;

    @Mock
    RequestDispatcher requestDispatcher;

    @Mock
    HashMap<String,String> errors;

    RegisterServlet servlet;

    @BeforeEach
    public void setup() throws IOException{
        servlet = new RegisterServlet();
        servlet.userManager = usersManager;
    }

    @Test
    void doGetShouldDoRegisterView() throws ServletException, IOException {
        when(request.getRequestDispatcher("/WEB-INF/pages/signin.jsp")).thenReturn(requestDispatcher);
        servlet.doGet(request, response);
        verify(requestDispatcher, atLeastOnce()).forward(request, response);
    }

    @Test
    void doPostShouldRegisterSuccess() throws ServletException, IOException, DuplicateKeyException {
        when(request.getParameter("username")).thenReturn("galiaker");
        when(request.getParameter("fullname")).thenReturn("François Burgener");
        when(request.getParameter("email")).thenReturn("francois.burgener@hotmail.fr");
        when(request.getParameter("password")).thenReturn("toto");
        when(request.getParameter("confirm-password")).thenReturn("toto");
        when(usersManager.createUser(any(User.class))).thenReturn(true);

        servlet.doPost(request,response);
        verify(response, atLeastOnce()).setStatus(HttpServletResponse.SC_OK);
        verify(response,atLeastOnce()).sendRedirect(request.getContextPath() + "/signin");
    }

    @Test
    void doPostShouldNotRegisterIfEmptyFields() throws ServletException, IOException {
        when(request.getParameter("username")).thenReturn("");
        when(request.getParameter("fullname")).thenReturn("");
        when(request.getParameter("email")).thenReturn("");
        when(request.getParameter("password")).thenReturn("");
        when(request.getParameter("confirm-password")).thenReturn("");
        when(request.getRequestDispatcher("/WEB-INF/pages/signin.jsp")).thenReturn(requestDispatcher);

        servlet.doPost(request,response);

        verify(request,atLeastOnce()).setAttribute(eq("tabSelect"),eq(false));
        verify(response, atLeastOnce()).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(requestDispatcher).forward(request,response);
    }

    //TODO remove or to make it work
    @Test
    void doPostShouldNotRegisterInCaseOfSQLError() throws ServletException, IOException {
        when(request.getParameter("username")).thenReturn("galiaker");
        when(request.getParameter("fullname")).thenReturn("François Burgener");
        when(request.getParameter("email")).thenReturn("francois.burgener@hotmail.fr");
        when(request.getParameter("password")).thenReturn("toto");
        when(request.getParameter("confirm-password")).thenReturn("toto");
        when(request.getRequestDispatcher("/WEB-INF/pages/signin.jsp")).thenReturn(requestDispatcher);
        when(usersManager.createUser(any(User.class))).thenReturn(false);

        servlet.doPost(request,response);

        verify(request,atLeastOnce()).setAttribute(eq("sqlError"),eq("SQL ERROR : This username is already used"));
        verify(request,atLeastOnce()).setAttribute(eq("tabSelect"),eq(false));
        verify(response, atLeastOnce()).setStatus(HttpServletResponse.SC_CONFLICT);
        verify(requestDispatcher,atLeastOnce()).forward(request,response);
    }



}