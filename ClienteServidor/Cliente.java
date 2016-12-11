import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Cliente{

    public static void main(String[] args) throws FileNotFoundException{
        byte[] datosEnviar;

        int bytesLeidos = 0;

        String host = "192.168.1.18";
        int port = 5001, aux_port=8988;

        Scanner in = new Scanner(System.in);

        System.out.println("Introduzca dirección archivo/sistema a crear copia: (ej. /home, /, /dir_absoluta/archivo. )");
        String dir = in.nextLine();



        System.out.println("Introduzca dirección de salida: (no introducir puntos en el nombre, ej: arch.ss) ");
        String salida = in.nextLine();

        Socket socketServicio = null;

        copyCreator procesador = new copyCreator(dir,salida);
        procesador.createSecurityCopy();

        File file = new File(procesador.getOutputDir());

        InputStream fileInput = new FileInputStream(file);

        String borrado = "rm -rf " + file ;

        System.out.println(borrado);

        try {
            System.out.println("Hello");
            socketServicio = new Socket(host, port);

            // Primeros envíamos el nombre del archivo.
            OutputStream out = socketServicio.getOutputStream();
            byte [] env = procesador.getOutputDir().substring(procesador.getOutputDir().lastIndexOf('/') + 1).getBytes();
            System.out.println("Enviando nombre del fichero");
            out.write(env,0,env.length);

	        System.out.println("Obteniendo outputStream");
            OutputStream outputStream = socketServicio.getOutputStream();

	        System.out.println("OutputStream obtenido");
            datosEnviar = new byte[1024*1024];

            System.out.println("Enviando fichero");
            while((bytesLeidos = fileInput.read(datosEnviar)) > 0){
		        System.out.println("Enviando...");
                outputStream.write(datosEnviar, 0, bytesLeidos);
		        System.out.println("Paquete enviado");
            }

            outputStream.flush();

            socketServicio.close();

        } catch (UnknownHostException e) {
            System.err.println("Error: Nombre de host no encontrado.");
        } catch (IOException e) {
            System.err.println("Error de entrada/salida al abrir el socket.");
        }

        // Destruimos el archivo una vez ya trasmitido.
        try{
            Process ps = Runtime.getRuntime().exec(borrado);
        }catch (Exception e){
            System.err.println("Imposible realizar borrado");
        }

    }
}

