package uy.edu.um.doors.entities;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {
    private final String logname;

    public Logger(){
        LocalDate hoy=LocalDate.now();
        this.logname="DOORS_PROCESS_LOG_"+ hoy+".txt";// le pone nombre al archivo
    }

    private void write(String linea) {
        try {
            // la primera vez que se corra esto, se crea el archivo.Si ya existe, edita el existente
            FileWriter writer = new FileWriter(logname, true);//abre el archivo, true hace que no borres al escribir
            writer.write(linea + "\n");// escribe linea y pasa a la siguiente
            writer.close();
        } catch (IOException e) {
            System.out.println("Error escribiendo en el log: " + e.getMessage());
        }
    }

    private String getTimestamp() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return now.format(formatter);
    }

    public void logpPrepare(Proceso proceso){
        String linea = "[" + getTimestamp() + "]: NEW PENDING PROCESS: "
                + "PID=" + proceso.getPid()
                + " | " + proceso.getNombre()
                + " | USER:" + proceso.getUsuario().getAlias()
                + " UID:" + proceso.getUsuario().getUid()
                + " | P=" + proceso.getPrioridad();

        write(linea);
    }

    public void logExecute(Proceso proceso){
        String linea = "[" + getTimestamp() + "]: EXECUTING PROCESS: "
                + "PID=" + proceso.getPid()
                + " | USER:" + proceso.getUsuario().getAlias()
                + " UID:" + proceso.getUsuario().getUid();

        write(linea);

        for(int i=0;i<proceso.getEventos().size();i++){
            Evento evento=proceso.getEventos().get(i);

            linea = "EVENT: "
                    + evento.getTipo() + " |"
                    + "Instructions " + evento.getInstrucciones();

            write(linea);
        }
    }

    public void logFinishOK (Proceso proceso){
        String linea = "[" + getTimestamp() + "]: ENDING PROCESS: "
                + "PID=" + proceso.getPid()
                + " | STATE: OK";
        write(linea);
    }

    public void logFinishError (Proceso proceso){
        String linea = "[" + getTimestamp() + "]: ENDING PROCESS: "
                + "PID=" + proceso.getPid()
                + " | STATE: ERROR";
        write(linea);
    }

    public void logFinishTerm(Proceso proceso, int uid){
        String linea = "[" + getTimestamp() + "]: ENDING PROCESS: "
                + "PID=" + proceso.getPid()
                + " | STATE: TERMINATED by USER: "
                + proceso.getUsuario().getAlias()
                + "UID: " + uid;
        write(linea);
    }

    public void logOverflow(){
        String linea = "[" + getTimestamp() + "]: FINISHED PROCESS OVERFLOW ";
        write(linea);
    }

    public void logProcesoOverflow(Proceso proceso){
        String linea = "PID=" + proceso.getPid()
                + " " + proceso.getNombre()
                + " | STATE:" + proceso.getFinalizacion()
                + " | USER:" + proceso.getUsuario().getAlias()
                + " UID" + proceso.getUsuario().getUid();
        write(linea);
    }




}
