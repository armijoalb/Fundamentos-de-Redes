
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;


public class copyCreator
{
    private SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy-hh:mm:ss");
    private String date = sdf.format(new Date());
    private String defaultOutputDir = "";
    private String sudoCommand = "";
    private String dir = "";

    public copyCreator(String direction, String dir_salida){
        dir = direction;
        defaultOutputDir = dir_salida;
        sudoCommand = resolveCommand(dir, defaultOutputDir);
        defaultOutputDir +="-"+date+".tar.gz";
    }

    public String resolveCommand(String dir, String dir_salida){
        String salida  = "";
        if(dir.indexOf("/") == 0 && dir.length() == 1){ // Es una copia de todo el sistema.
            salida = "sudo tar -cpzf "+ dir_salida+"-"+date+".tar.gz " + dir +
            "--exclude=mnt --exclude=dev --exclude=media --exclude=run --exclude=var/tmp" +
            "--exclude=proc --exclude=var/run --exclude=sys --exclude=lost+found --exclude=tmp" +
            "--exclude=var/spool --exclude=var/cache/dnf --exclude=lib/modules/*/volatile/.mounted" +
            "--exclude=etc/fstab --exclude=home/*/.local/share/Trash --exclude=home/*/.mozilla" +
            "--exclude=home/*/.cache --exclude=home/*/.thumbnails --exclude="+
            dir_salida.substring(1,dir_salida.lastIndexOf('/'));
        }
        else if(dir.equals("/home") ){ // Es una copia del directorio home.
            salida = "sudo tar -cpzf "+ dir_salida+"-"+date+".tar.gz " + dir +
                    "--exclude=home/*/.cache --exclude=home/*/.thumbnails" +
                    "--exclude=home/*/.local/share/Trash --exclude=home/*/.mozilla";

        }
        else{
            salida = "sudo tar -cpzf "+ dir_salida+"-"+date+".tar.gz " + dir;
        }

        return salida;
    }

    public String getOutputDir(){
        return defaultOutputDir;
    }

    public String createSecurityCopy(){
        System.out.println("Creando copia de seguridad del archivo\n");
        String respuesta = "";
        ProcessBuilder createCopyProcessBuilder = null;

        String os = System.getProperty("os.name");

        if( os.indexOf("nux") >= 0 || os.indexOf("mac") >=0){
            createCopyProcessBuilder = new ProcessBuilder("/bin/bash", "-c", sudoCommand);
        }
        else{
            System.err.println("Este programa sólo puede utilizarse en Linux y Mac OS");
        }

        createCopyProcessBuilder.redirectErrorStream(true);

        try{
            Process createCopy = createCopyProcessBuilder.start();

            InputStream createCopyStream = createCopy.getInputStream();

            respuesta = getStringFromStream(createCopyStream);



        }catch (Exception e){
            System.err.println("Error ejecutando comando" + e);
        }

        return respuesta;
    }

    private static String getStringFromStream(InputStream stream) throws IOException {
        if (stream != null) {
            Writer writer = new StringWriter();

            char[] buffer = new char[2048];
            try {
                Reader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
                int count;
                while ((count = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, count);
                }
            } finally {
                stream.close();
            }
            return writer.toString();
        } else {
            return "";
        }
    }
}
