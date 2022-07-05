package efs.task.todoapp.web;

import com.sun.net.httpserver.HttpServer;
import efs.task.todoapp.repository.TaskRepository;
import efs.task.todoapp.repository.UserRepository;
import efs.task.todoapp.service.ToDoService;

import java.net.InetSocketAddress;
import java.io.IOException;


public class WebServerFactory {
    public static HttpServer createServer() {

        try {
            final HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 8080), 0);

            ToDoService service = new ToDoService(new UserRepository(), new TaskRepository());
            server.createContext("/todo/task", new TaskHandler(service));
            server.createContext("/todo/user", new UserHandler(service));

            return server;
        }
        catch (IOException e) {
            throw new RuntimeException("Unable to start server", e);
        }
    }
}
