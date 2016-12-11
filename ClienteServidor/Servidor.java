import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Servidor {

    public static void main(String args[]) throws IOException {
        int port = 5001;
        byte [] datosRecibidos = new byte[1024*1024];
        SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy-hh:mm:ss");
        String date = sdf.format(new Date());

        byte [] nombreRecibido = new byte[256];
        int long_nombre = 0;

        InputStream inputStream = null;
        String file_name = "";

        int bytesRecibidos = 0;

        try {
            ServerSocket socketServidor = new ServerSocket(port);

            System.out.println("Esperando a petición");
            Socket socketServicio = socketServidor.accept();

            System.out.println("Petición recibida, recibiendo nombre del fichero");

            InputStream in = socketServicio.getInputStream();

            long_nombre = in.read(nombreRecibido);
            String aux = new String(nombreRecibido,0,long_nombre);
            // Usado para almacenar dentro de una raspberry pi
            file_name ="/home/pi/"+aux.substring(0,aux.indexOf('.'))+".tar.gz";
            System.out.println("nombre archivo entrante: " + file_name);

            inputStream = socketServicio.getInputStream();

            File file = new File(file_name);
            System.out.println("Fichero abierto:" + file_name);
            OutputStream output = new FileOutputStream(file);

            System.out.println("Lectura del fichero");
            while((bytesRecibidos = inputStream.read(datosRecibidos) ) > 0){
                System.out.println("Leyendo...");
                output.write(datosRecibidos,0,bytesRecibidos);
                System.out.println("Paquete Leído");
            }

            output.flush();
            output.close();


        } catch (IOException e) {
            System.err.println("Error al escuchar en el puerto " + port);
        }
    }
}
