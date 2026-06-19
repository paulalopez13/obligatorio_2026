package uy.edu.um.doors.entities;
import uy.edu.um.tad.list.MyList;


public class Evento {
    private String tipo; //Puede ser CPU, RAM o DISK. misma consideracion q estado de proceso
    private String[] instrucciones;


    public Evento(String tipo, String[] instrucciones) {
        this.tipo = tipo;
        this.instrucciones = instrucciones;
    }


    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String[] getInstrucciones() {
        return instrucciones;
    }

    public void setInstrucciones(String[] instrucciones) {
        this.instrucciones = instrucciones;
    }
}
