package org.example.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.dto.Movie;

import java.io.IOException;

@WebServlet("/movies")
public class MovieServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        var movie = Movie.builder()
                        .title("The Batman")
                        .year((short) 2022)
                        .build();
        var out = resp.getWriter();
        out.println("<h1>Movie Servlet (GET)</h1>");
        out.println(movie);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        var movie = Movie.builder()
                .title("Barbie")
                .year((short) 2023)
                .build();
        var out = resp.getWriter();
        out.println("<h1>Movie Servlet (POST)</h1>");
        out.println(movie);
    }
}
