package org.example;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {

    // Método que arranca el servidor en el puerto 8080
    public void init() throws IOException {
        ServerSocket server = new ServerSocket(8080);
        var isAlive = true;
        while (isAlive) {
            System.out.println("Esperando un cliente...");
            Socket socket = server.accept(); // Espera una conexión
            System.out.println("Conectado.");
            dispatchWorker(socket); // Lanza un hilo para atender al cliente
        }
    }

    //Los 3 pasos son:
    //Recibir
    //Procesar
    //Entregar

    // Crea un nuevo hilo para cada cliente (para permitir múltiples clientes al mismo tiempo)
    public void dispatchWorker(Socket socket) {
        new Thread(
            ()->{
                try{
                    handleRequest(socket);
                }catch(IOException e){
                    throw new RuntimeException(e);
                }
            }
            ).start();
    }

    // Método que maneja la solicitud HTTP del cliente
    public void handleRequest(Socket socket) throws IOException {
        var reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            if(line.startsWith("GET")){
                // Obtenemos el recurso solicitado
                var resource = line.split(" ")[1].replace("/", "");
                if (resource.isEmpty()) resource = "login.html"; // Página por defecto
                System.out.println("El cliente está pidiendo: " + resource);
                sendResponse(socket, resource);
            }
        }
    }

    // Método para enviar una respuesta al cliente
    private void sendResponse(Socket socket, String resource) throws IOException {
        File res = new File("resources/" + resource);

        OutputStream outputStream = socket.getOutputStream();
        BufferedOutputStream dataOut = new BufferedOutputStream(outputStream);
        BufferedWriter headerOut = new BufferedWriter(new OutputStreamWriter(outputStream));

        if (res.exists()) {
            System.out.println("Se ha encontrado el recurso ");
            // Detectamos el tipo de contenido según la extensión
            String contentType = getContentType(resource);

            // Enviamos cabecera HTTP con estado 200
            headerOut.write("HTTP/1.0 200 OK\r\n");
            headerOut.write("Content-Type: " + contentType + "\r\n");
            headerOut.write("Content-Length: " + res.length() + "\r\n");
            headerOut.write("Connection: close\r\n");
            headerOut.write("\r\n");
            headerOut.flush(); // Importante: enviar la cabecera antes del contenido

            // Enviamos el contenido del archivo
            FileInputStream fis = new FileInputStream(res);
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                dataOut.write(buffer, 0, bytesRead);
            }

            fis.close();
            dataOut.flush();
        } else {
            // Imprimir en consola que no se encontró el recurso
            System.out.println("ERROR 404 - Recurso no encontrado: " + res.getAbsolutePath());

            // Recurso no encontrado: error 404
            String errorMessage = "<html><body><h1>404 - Recurso no encontrado</h1></body></html>";
            headerOut.write("HTTP/1.0 404 Not Found\r\n");
            headerOut.write("Content-Type: text/html\r\n");
            headerOut.write("Content-Length: " + errorMessage.length() + "\r\n");
            headerOut.write("Connection: close\r\n");
            headerOut.write("\r\n");
            headerOut.write(errorMessage);
            headerOut.flush();
        }

        // Cerramos la conexión
        socket.close();
    }

    // Método auxiliar para determinar el tipo MIME según la extensión del archivo
    private String getContentType(String fileName) {
        if (fileName.endsWith(".html") || fileName.endsWith(".htm")) return "text/html";
        if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) return "image/jpeg";
        if (fileName.endsWith(".gif")) return "image/gif";
        return "application/octet-stream"; // Tipo por defecto
    }

    // Método main que arranca el servidor
    public static void main(String[] args) throws IOException {
        Main main = new Main();
        main.init();
    }
}