package ru.netology.servlet;

import ru.netology.controller.PostController;
import ru.netology.repository.PostRepository;
import ru.netology.service.PostService;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class MainServlet extends HttpServlet {
  private PostController controller;

  @Override
  public void init() {
    final var repository = new PostRepository();
    final var service = new PostService(repository);
    controller = new PostController(service);
  }

  @Override
  protected void service(HttpServletRequest req, HttpServletResponse resp) {
    // если деплоились в root context, то достаточно этого
    try {
      final var path = req.getRequestURI();
      final var method = req.getMethod();
      // primitive routing
      if (method.equals("GET")) {
        handleGet(path, resp);
      } else if (method.equals("POST") && path.equals(API_POSTS)) {
        controller.save(req.getReader(), resp);
      } else if (method.equals("DELETE") && path.matches(API_POSTS_ID_PATTERN)) {
        final var id = getFromPath(path);
        controller.removeById(id, resp);
      } else {
        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
      }
    } catch (ru.netology.exception.NotFoundException e) {
      resp.setStatus(HttpServletRespose.SC_NOT_FOUND);
    } catch (Exception e) {
      e.printStackTrace();
      resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
    }


  private void handleGet(String path, HttpServletResponse resp) throws Exception {
if (path.equals(API_POSTS)) {
  controller.all(resp);
} else if (path.matches(API_ID_PATTERN)) {
  final var id = getIdFromPath(path);
  controller.getById(id, resp);
} else {
  resp.getStatus(HttpServletResponse.SC_NOT_FOUND);
}
  }
  private long getIdFromPath(String path) {
    return Long.parseLong(path.substring(path.lastIndexOf("/") + 1));
  }
}

