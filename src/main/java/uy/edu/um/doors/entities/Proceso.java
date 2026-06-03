package uy.edu.um.doors.entities;

import lombok.Getter;
import lombok.Setter;
import uy.edu.um.tad.list.MyList;

@Setter
@Getter
public class Proceso implements Comparable<Proceso> {

    private String pid;
    private String nombre;
    private Usuario usuario;
    private int prioridad;
    private String estado; //Puede ser NEW, PENDING, RUNNING o FINISHED. Como eso solo se edita desde el codigo (no el usuario) no hay problemas con mayusculas y minisculas
    private MyList<Evento> eventos;
    private String finalizacion;


    public Proceso(String pid, String nombre, int prioridad, Usuario usuario, String estado, MyList<Evento> eventos) {
        this.pid = pid;
        this.nombre = nombre;
        this.prioridad = prioridad;
        this.usuario = usuario;
        this.estado = estado;
        this.eventos = eventos;
        this.finalizacion=null;
    }


    @Override
    public int compareTo(Proceso otro){
        return Integer.compare(this.prioridad, otro.prioridad); //devuelve negativo si this<otro, 0 si song iguales, positivo si this>otro
    }

    public int calcularPrioridad(){

        double cantidadEventos = eventos.size();
        double cantidadDISK = 0;
        double cantidadCPU = 0;
        double cantidadRAM = 0;

        for(int i=0; i<eventos.size(); i++){

            switch (eventos.get(i).getTipo()) {
                case "DISK":
                    cantidadDISK++;
                    break;

                case "CPU":
                    cantidadCPU++;
                    break;

                case "RAM":
                    cantidadRAM++;
                    break;

            }
        }

        int pesoUsuario=0;

        if(this.getUsuario().getTipo().equals("ADMIN")){
            pesoUsuario=32;
        }

        else{
            pesoUsuario=8;
        }

        double prioridadDouble = (((8*cantidadCPU + 2*cantidadRAM + 2*cantidadDISK)/cantidadEventos)+pesoUsuario*cantidadEventos);


        return (int) prioridadDouble;
    }

    public void printEventos(){

        for(int i=0;i<this.getEventos().size();i++){
            Evento evento=this.getEventos().get(i);

            String linea = "EVENT: " + evento.getTipo() + " |" + "Instructions [";

            for(int j=0; j<evento.getInstrucciones().size();j++)   {
                linea = linea + evento.getInstrucciones().get(j) + ", ";
            }

            linea = linea.substring(0, linea.length()-2);
            linea = linea + "]";

            System.out.println(linea);

        }

    }

}




